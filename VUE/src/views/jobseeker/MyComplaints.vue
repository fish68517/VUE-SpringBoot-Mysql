<template>
  <CrudPage
    ref="crudRef"
    title="我的举报/反馈"
    :api="ComplaintApi"
    :columns="columns"
    :formFields="[]"
    :showCreate="false"
    :showEdit="false"
    :showDelete="false"
    :defaultActions="false"
    :actionWidth="320"
    :loadFn="loadMyComplaints"
    :rowActions="rowActions"
  >
    <template #statusTag="{ row }">
      <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
    </template>

    <template #target="{ row }">
      {{ row.targetType }} #{{ row.targetId }}
    </template>

    <template #handler="{ row }">
      {{ row.handlerAdmin?.username || (row.handlerAdmin?.id ? `管理员#${row.handlerAdmin.id}` : "-") }}
    </template>

    <template #time="{ row }">
      {{ fmt(row.createTime) }}
    </template>
  </CrudPage>

  <!-- 补充证据弹窗 -->
  <el-dialog v-model="dlg" title="补充证据" width="520px">
    <el-form :model="editForm" label-width="120px">
      <el-form-item label="证据图片路径">
        <el-input v-model="editForm.evidenceImg" placeholder="image/complaint_xxx.png" />
      </el-form-item>
      <el-form-item label="补充说明">
        <el-input v-model="editForm.content" type="textarea" :rows="4" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dlg=false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="saveEvidence">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import CrudPage from "@/components/CrudPage.vue";
import { ComplaintApi } from "@/api/Api";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const me = computed(() => auth.user);
const crudRef = ref(null);

function fmt(v) {
  if (!v) return "";
  return String(v).replace("T", " ");
}

function statusText(s) {
  if (s === "PENDING") return "待处理";
  if (s === "PROCESSING") return "处理中";
  if (s === "DONE") return "已完成";
  return s || "";
}
function statusType(s) {
  if (s === "PENDING") return "info";
  if (s === "PROCESSING") return "warning";
  if (s === "DONE") return "success";
  return "";
}

const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "target", label: "目标", slot: "target", width: 140 },
  { prop: "type", label: "类型", width: 140 },
  { prop: "content", label: "内容", minWidth: 220 },
  { prop: "evidenceImg", label: "证据图", width: 180 },
  { prop: "status", label: "状态", slot: "statusTag", width: 110 },
  { prop: "result", label: "处理结果", minWidth: 160 },
  { prop: "handler", label: "处理人", slot: "handler", width: 140 },
  { prop: "createTime", label: "提交时间", slot: "time", width: 180 },
];

async function loadMyComplaints() {
  if (!me.value?.id) return [];
  const all = await ComplaintApi.list();
  return all
    .filter(c => c.fromUser?.id === me.value.id)
    .sort((a, b) => (b.id || 0) - (a.id || 0));
}

// ========== 业务按钮：撤销 / 补充证据 ==========
async function cancelComplaint(row) {
  try {
    await ElMessageBox.confirm("确认撤销该举报？（仅待处理可撤销）", "提示", { type: "warning" });

    // 注意：后端 ComplaintStatus 枚举没有 CANCELLED，我们采用“删除记录”模拟撤销
    // 也可以改为：status DONE + result="用户撤销"，但更像撤销的是 delete
    await ComplaintApi.remove(row.id);

    ElMessage.success("已撤销（删除记录）");
    await crudRef.value.load();
  } catch {}
}

const dlg = ref(false);
const saving = ref(false);
const editingRow = ref(null);
const editForm = reactive({
  evidenceImg: "",
  content: "",
});

function openEvidence(row) {
  editingRow.value = row;
  editForm.evidenceImg = row.evidenceImg || "";
  editForm.content = row.content || "";
  dlg.value = true;
}

async function saveEvidence() {
  const row = editingRow.value;
  if (!row) return;

  saving.value = true;
  try {
    await ComplaintApi.update(row.id, {
      fromUser: { id: row.fromUser?.id },
      targetType: row.targetType,
      targetId: row.targetId,
      type: row.type,
      content: editForm.content,
      evidenceImg: editForm.evidenceImg,
      status: row.status,
      result: row.result,
      handlerAdmin: row.handlerAdmin?.id ? { id: row.handlerAdmin.id } : null,
      handleTime: row.handleTime,
    });

    ElMessage.success("补充证据已保存");
    dlg.value = false;
    await crudRef.value.load();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "保存失败");
  } finally {
    saving.value = false;
  }
}

const rowActions = [
  {
    label: "补充证据",
    type: "primary",
    disabled: (row) => !(row.status === "PENDING" || row.status === "PROCESSING"),
    onClick: openEvidence,
  },
  {
    label: "撤销",
    type: "danger",
    disabled: (row) => row.status !== "PENDING",
    onClick: cancelComplaint,
  },
];
</script>
