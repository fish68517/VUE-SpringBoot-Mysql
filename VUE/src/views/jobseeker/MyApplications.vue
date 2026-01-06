<template>
  <CrudPage
    title="我的报名"
    :api="ApplicationApi"
    :columns="columns"
    :formFields="fields"
    :transformIn="transformIn"
    :transformOut="transformOut"
  />
</template>

<script setup>
import CrudPage from "@/components/CrudPage.vue";
import { ApplicationApi } from "@/api/Api";

const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "jobPostId", label: "岗位ID", width: 90 },
  { prop: "jobseekerId", label: "求职者ID", width: 100 },
  { prop: "status", label: "状态", width: 120 },
  { prop: "applyTime", label: "报名时间" },
];

const fields = [
  { prop: "jobPostId", label: "岗位ID", type: "number" },
  { prop: "jobseekerId", label: "求职者用户ID", type: "number" },
  { prop: "status", label: "状态", type: "select", options: [
    { label: "已报名", value: "APPLIED" },
    { label: "已录用", value: "ACCEPTED" },
    { label: "已拒绝", value: "REJECTED" },
    { label: "已取消", value: "CANCELLED" },
    { label: "已到岗", value: "CHECKED_IN" },
    { label: "已完结", value: "FINISHED" },
    { label: "已结算", value: "SETTLED" },
  ]},
  { prop: "applyTime", label: "报名时间(ISO)", type: "text", placeholder: "2026-01-04T12:00:00" },
  { prop: "merchantNote", label: "商家备注", type: "text" },
  { prop: "cancelReason", label: "取消原因", type: "text" },
  { prop: "checkinTime", label: "到岗时间(ISO)", type: "text" },
  { prop: "finishTime", label: "完结时间(ISO)", type: "text" },
  { prop: "settleTime", label: "结算时间(ISO)", type: "text" },
];

function transformIn(row) {
  return {
    ...row,
    jobPostId: row.jobPost?.id,
    jobseekerId: row.jobseeker?.id,
  };
}

function transformOut(form) {
  const payload = { ...form };
  payload.jobPost = { id: Number(form.jobPostId) };
  payload.jobseeker = { id: Number(form.jobseekerId) };
  delete payload.jobPostId;
  delete payload.jobseekerId;
  return payload;
}
</script>
