<template>
  <CrudPage
    title="我的资料（简历/认证）"
    :api="JobseekerProfileApi"
    :columns="columns"
    :formFields="fields"
    :transformIn="transformIn"
    :transformOut="transformOut"
  />
</template>

<script setup>
import CrudPage from "@/components/CrudPage.vue";
import { JobseekerProfileApi } from "@/api/Api";

const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "userId", label: "用户ID", width: 90 },
  { prop: "realName", label: "姓名", width: 120 },
  { prop: "city", label: "城市", width: 140 },
  { prop: "verifyStatus", label: "认证状态", width: 120 },
];

const fields = [
  { prop: "userId", label: "用户ID", type: "number" },
  { prop: "realName", label: "真实姓名", type: "text" },
  { prop: "gender", label: "性别", type: "select", options: [
    { label: "男", value: "M" }, { label: "女", value: "F" }, { label: "未知", value: "U" }
  ]},
  { prop: "age", label: "年龄", type: "number" },
  { prop: "city", label: "城市", type: "text" },
  { prop: "skills", label: "技能", type: "text" },
  { prop: "availableTimeDesc", label: "可工作时间", type: "text" },
  { prop: "resumeFile", label: "简历文件(file/...)", type: "text" },
  { prop: "idCardFront", label: "证件正面(image/...)", type: "text" },
  { prop: "idCardBack", label: "证件反面(image/...)", type: "text" },
  { prop: "verifyStatus", label: "认证状态", type: "select", options: [
    { label: "待审核", value: "PENDING" }, { label: "通过", value: "APPROVED" }, { label: "驳回", value: "REJECTED" }
  ]},
];

function transformIn(row) {
  return { ...row, userId: row.user?.id };
}
function transformOut(form) {
  const payload = { ...form };
  payload.user = { id: Number(form.userId) };
  delete payload.userId;
  return payload;
}
</script>
