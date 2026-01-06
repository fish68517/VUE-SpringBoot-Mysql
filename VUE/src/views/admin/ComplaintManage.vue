<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <div style="font-weight:700">举报工单（管理员）</div>
      <el-button @click="reload">刷新</el-button>
    </div>

    <el-form :inline="true" :model="filters" style="margin-bottom: 10px">
      <el-form-item label="状态">
        <el-select v-model="filters.status" clearable placeholder="全部" style="width: 160px">
          <el-option label="待处理" value="PENDING" />
          <el-option label="处理中" value="PROCESSING" />
          <el-option label="已完成" value="DONE" />
        </el-select>
      </el-form-item>
      <el-form-item label="目标类型">
        <el-select v-model="filters.targetType" clearable placeholder="全部" style="width: 160px">
          <el-option label="岗位" value="JOB" />
          <el-option label="企业" value="COMPANY" />
          <el-option label="用户" value="USER" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="filters.keyword" placeholder="类型/内容" clearable />
      </el-form-item>
    </el-form>

    <el-table :data="filteredRows" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="targetType" label="目标类型" width="110" />
      <el-table-column prop="targetId" label="目标ID" width="90" />
      <el-table-column prop="type" label="类型" width="140" />
      <el-table-column prop="content" label="内容" min-width="260" />
      <el-table-column prop="evidenceImg" label="证据图" min-width="180" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="result" label="处理结果" min-width="200" />

      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" :disabled="row.status !== 'PENDING'" @click="start(row)">开始处理</el-button>
          <el-button size="small" type="primary" :disabled="row.status === 'DONE'" @click="finish(row)">完成处理</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useAuthStore } from "@/store/auth";
import { ComplaintApi } from "@/api/Api";

const auth = useAuthStore();
const rows = ref([]);

const filters = reactive({
  status: "",
  targetType: "",
  keyword: "",
});

function nowLocalIso() {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
}

async function reload() {
  rows.value = await ComplaintApi.list();
}

const filteredRows = computed(() => {
  const kw = filters.keyword.trim().toLowerCase();
  return rows.value
    .filter(r => !filters.status || r.status === filters.status)
    .filter(r => !filters.targetType || r.targetType === filters.targetType)
    .filter(r => !kw || (r.type || "").toLowerCase().includes(kw) || (r.content || "").toLowerCase().includes(kw));
});

async function start(row) {
  try {
    await ComplaintApi.update(row.id, {
      fromUser: { id: row.fromUser?.id },
      targetType: row.targetType,
      targetId: row.targetId,
      type: row.type,
      content: row.content,
      evidenceImg: row.evidenceImg,
      status: "PROCESSING",
      result: row.result,
      handlerAdmin: auth.user?.id ? { id: auth.user.id } : null,
      handleTime: nowLocalIso(),
    });
    ElMessage.success("已开始处理");
    await reload();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

async function finish(row) {
  try {
    const { value } = await ElMessageBox.prompt("请输入处理结果/说明", "完成工单", { confirmButtonText: "确定", cancelButtonText: "取消" });
    await ComplaintApi.update(row.id, {
      fromUser: { id: row.fromUser?.id },
      targetType: row.targetType,
      targetId: row.targetId,
      type: row.type,
      content: row.content,
      evidenceImg: row.evidenceImg,
      status: "DONE",
      result: value || "已处理",
      handlerAdmin: auth.user?.id ? { id: auth.user.id } : null,
      handleTime: nowLocalIso(),
    });
    ElMessage.success("工单已完成");
    await reload();
  } catch (e) {
    if (e?.message === "cancel") return;
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

onMounted(reload);
</script>
