<template>
  <CrudPage
    title="举报反馈"
    :api="ComplaintApi"
    :columns="columns"
    :formFields="fields"
    :transformIn="transformIn"
    :transformOut="transformOut"
  />
</template>

<script setup>
import CrudPage from "@/components/CrudPage.vue";
import { ComplaintApi } from "@/api/Api";

const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "fromUserId", label: "提交者ID", width: 100 },
  { prop: "targetType", label: "目标类型", width: 110 },
  { prop: "targetId", label: "目标ID", width: 90 },
  { prop: "status", label: "状态", width: 120 },
];

const fields = [
  { prop: "fromUserId", label: "提交者用户ID", type: "number" },
  { prop: "targetType", label: "目标类型", type: "select", options: [
    { label: "岗位", value: "JOB" }, { label: "企业", value: "COMPANY" }, { label: "用户", value: "USER" }
  ]},
  { prop: "targetId", label: "目标ID", type: "number" },
  { prop: "type", label: "举报类型", type: "text" },
  { prop: "content", label: "内容", type: "textarea" },
  { prop: "evidenceImg", label: "证据图(image/...)", type: "text" },
  { prop: "status", label: "状态", type: "select", options: [
    { label: "待处理", value: "PENDING" }, { label: "处理中", value: "PROCESSING" }, { label: "已完成", value: "DONE" }
  ]},
  { prop: "result", label: "处理结果", type: "textarea" },
  { prop: "handlerAdminId", label: "处理管理员ID", type: "number" },
  { prop: "handleTime", label: "处理时间(ISO)", type: "text" },
];

function transformIn(row) {
  return {
    ...row,
    fromUserId: row.fromUser?.id,
    handlerAdminId: row.handlerAdmin?.id,
  };
}

function transformOut(form) {
  const payload = { ...form };
  payload.fromUser = { id: Number(form.fromUserId) };
  delete payload.fromUserId;

  if (form.handlerAdminId) payload.handlerAdmin = { id: Number(form.handlerAdminId) };
  else payload.handlerAdmin = null;
  delete payload.handlerAdminId;

  return payload;
}
</script>
