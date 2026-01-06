<template>
  <CrudPage
    ref="crudRef"
    title="我的报名"
    :api="ApplicationApi"
    :columns="columns"
    :formFields="[]"
    :showCreate="false"
    :showEdit="false"
    :showDelete="false"
    :defaultActions="false"
    :actionWidth="220"
    :loadFn="loadMyApps"
    :rowActions="rowActions"
  >
    <!-- 用 slot 自定义显示：岗位标题 -->
    <template #jobTitle="{ row }">
      {{ row.jobPost?.title || `岗位#${row.jobPost?.id}` }}
    </template>

    <template #statusTag="{ row }">
      <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
    </template>
  </CrudPage>
</template>

<script setup>
import { computed, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import CrudPage from "@/components/CrudPage.vue";
import { ApplicationApi } from "@/api/Api";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const me = computed(() => auth.user);
const crudRef = ref(null);

const columns = [
  { prop: "id", label: "报名ID", width: 90 },
  { prop: "jobTitle", label: "岗位", slot: "jobTitle", width: 260 },
  { prop: "status", label: "状态", slot: "statusTag", width: 120 },
  { prop: "applyTime", label: "报名时间", width: 180 },
  { prop: "merchantNote", label: "商家备注", minWidth: 180 },
];

function statusType(s) {
  if (s === "APPLIED") return "info";
  if (s === "ACCEPTED") return "success";
  if (s === "REJECTED") return "danger";
  if (s === "CHECKED_IN") return "warning";
  if (s === "FINISHED") return "success";
  if (s === "SETTLED") return "success";
  return "";
}

async function loadMyApps() {
  if (!me.value?.id) return [];
  const all = await ApplicationApi.list();
  // 只显示当前登录用户的报名
  return all.filter(a => a.jobseeker?.id === me.value.id);
}

async function cancelApply(row) {
  try {
    await ElMessageBox.confirm("确认取消报名？", "提示", { type: "warning" });

    const payload = {
      jobPost: { id: row.jobPost?.id },
      jobseeker: { id: row.jobseeker?.id },
      status: "CANCELLED",
      applyTime: row.applyTime,
      merchantNote: row.merchantNote || "",
      cancelReason: "用户主动取消",
      checkinTime: row.checkinTime,
      finishTime: row.finishTime,
      settleTime: row.settleTime,
    };

    await ApplicationApi.update(row.id, payload);
    ElMessage.success("已取消报名");
    await crudRef.value.load();
  } catch {}
}

const rowActions = [
  {
    label: "取消报名",
    type: "danger",
    disabled: (row) => row.status !== "APPLIED",
    onClick: cancelApply,
  },
];
</script>
