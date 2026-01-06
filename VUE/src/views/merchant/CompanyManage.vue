<template>
  <CrudPage
    title="企业管理"
    :api="CompanyApi"
    :columns="columns"
    :formFields="fields"
    :transformIn="transformIn"
    :transformOut="transformOut"
  />
</template>

<script setup>
import CrudPage from "@/components/CrudPage.vue";
import { CompanyApi } from "@/api/Api";

const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "merchantId", label: "商家用户ID", width: 120 },
  { prop: "name", label: "企业名称" },
  { prop: "verifyStatus", label: "审核状态", width: 120 },
];

const fields = [
  { prop: "merchantId", label: "商家用户ID", type: "number" },
  { prop: "name", label: "企业名称", type: "text" },
  { prop: "licenseNo", label: "营业执照号", type: "text" },
  { prop: "licenseImg", label: "执照图片(image/...)", type: "text" },
  { prop: "address", label: "地址", type: "text" },
  { prop: "contactName", label: "联系人", type: "text" },
  { prop: "contactPhone", label: "联系电话", type: "text" },
  { prop: "verifyStatus", label: "审核状态", type: "select", options: [
    { label: "待审核", value: "PENDING" }, { label: "通过", value: "APPROVED" }, { label: "驳回", value: "REJECTED" }
  ]},
  { prop: "rejectReason", label: "驳回原因", type: "text" },
];

function transformIn(row) {
  return { ...row, merchantId: row.merchant?.id };
}
function transformOut(form) {
  const payload = { ...form };
  payload.merchant = { id: Number(form.merchantId) };
  delete payload.merchantId;
  return payload;
}
</script>
