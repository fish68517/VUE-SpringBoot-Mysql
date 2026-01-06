<template>
  <CrudPage
    title="我的消息"
    :api="MessageApi"
    :columns="columns"
    :formFields="fields"
    :transformIn="transformIn"
    :transformOut="transformOut"
  />
</template>

<script setup>
import CrudPage from "@/components/CrudPage.vue";
import { MessageApi } from "@/api/Api";

const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "toUserId", label: "接收者ID", width: 100 },
  { prop: "title", label: "标题" },
  { prop: "readFlag", label: "已读", width: 80 },
];

const fields = [
  { prop: "toUserId", label: "接收者用户ID", type: "number" },
  { prop: "title", label: "标题", type: "text" },
  { prop: "content", label: "内容", type: "textarea" },
  { prop: "readFlag", label: "已读标记", type: "select", options: [
    { label: "未读", value: 0 }, { label: "已读", value: 1 }
  ]},
];

function transformIn(row) {
  return { ...row, toUserId: row.toUser?.id };
}
function transformOut(form) {
  const payload = { ...form };
  payload.toUser = { id: Number(form.toUserId) };
  delete payload.toUserId;
  return payload;
}
</script>
