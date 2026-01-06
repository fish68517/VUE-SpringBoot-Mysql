<template>
  <CrudPage
    ref="crudRef"
    title="我的消息"
    :api="MessageApi"
    :columns="columns"
    :formFields="[]"
    :showCreate="false"
    :showEdit="false"
    :showDelete="false"
    :defaultActions="false"
    :actionWidth="240"
    :loadFn="loadMyMessages"
    :rowActions="rowActions"
  >
    <template #readTag="{ row }">
      <el-tag :type="row.readFlag === 1 ? 'success' : 'info'">
        {{ row.readFlag === 1 ? "已读" : "未读" }}
      </el-tag>
    </template>

    <template #created="{ row }">
      {{ fmt(row.createTime) }}
    </template>
  </CrudPage>
</template>

<script setup>
import { computed, ref } from "vue";
import { ElMessage } from "element-plus";
import CrudPage from "@/components/CrudPage.vue";
import { MessageApi } from "@/api/Api";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const me = computed(() => auth.user);
const crudRef = ref(null);

function fmt(v) {
  if (!v) return "";
  return String(v).replace("T", " ");
}

const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "title", label: "标题", width: 220 },
  { prop: "content", label: "内容", minWidth: 280 },
  { prop: "readFlag", label: "状态", slot: "readTag", width: 100 },
  { prop: "createTime", label: "时间", slot: "created", width: 180 },
];

async function loadMyMessages() {
  if (!me.value?.id) return [];
  const all = await MessageApi.list();
  // 只显示当前用户消息
  return all
    .filter(m => m.toUser?.id === me.value.id)
    .sort((a, b) => (b.id || 0) - (a.id || 0)); // 新的在前
}

async function markRead(row) {
  try {
    if (row.readFlag === 1) return;

    await MessageApi.update(row.id, {
      toUser: { id: row.toUser?.id },
      title: row.title,
      content: row.content,
      readFlag: 1,
    });

    ElMessage.success("已标记为已读");
    await crudRef.value.load();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

async function markAllRead() {
  try {
    const rows = await loadMyMessages();
    const unread = rows.filter(r => r.readFlag !== 1);
    for (const r of unread) {
      await MessageApi.update(r.id, {
        toUser: { id: r.toUser?.id },
        title: r.title,
        content: r.content,
        readFlag: 1,
      });
    }
    ElMessage.success("全部标记已读完成");
    await crudRef.value.load();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

const rowActions = [
  {
    label: "标记已读",
    type: "primary",
    disabled: (row) => row.readFlag === 1,
    onClick: markRead,
  },
  {
    label: "全部已读",
    type: "success",
    disabled: () => false,
    onClick: markAllRead,
  },
];
</script>
