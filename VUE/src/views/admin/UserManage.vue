<template>
  <CrudPage
    title="用户管理"
    :api="UserApi"
    :columns="columns"
    :formFields="fields"
  />
</template>

<script setup>
import CrudPage from "@/components/CrudPage.vue";
import { UserApi } from "@/api/Api";

const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "username", label: "账号" },
  { prop: "role", label: "角色", width: 120 },
  { prop: "status", label: "状态", width: 120 },
];

const fields = [
  { prop: "username", label: "账号", type: "text" },
  { prop: "passwordHash", label: "密码(演示用)", type: "text" },
  { prop: "phone", label: "手机号", type: "text" },
  { prop: "email", label: "邮箱", type: "text" },
  { prop: "role", label: "角色", type: "select", options: [
    { label: "求职者", value: "JOBSEEKER" },
    { label: "商家", value: "MERCHANT" },
    { label: "管理员", value: "ADMIN" },
  ]},
  { prop: "status", label: "状态", type: "select", options: [
    { label: "正常", value: "NORMAL" },
    { label: "封禁", value: "BANNED" },
  ]},
];
</script>
