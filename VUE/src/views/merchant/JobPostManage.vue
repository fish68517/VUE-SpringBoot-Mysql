<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <div style="font-weight:700">岗位管理（商家）.</div>
      <div style="display:flex; gap:8px;">
        <el-button @click="reloadAll" :loading="loading">刷新</el-button>
      </div>
    </div>

    <el-alert
      v-if="role !== 'MERCHANT'"
      type="warning"
      show-icon
      :closable="false"
      title="该页面仅商家使用。你当前不是商家角色。"
      style="margin-bottom: 12px"
    />

    <el-alert
      v-else-if="!companyId && !loading"
      type="info"
      show-icon
      :closable="false"
      title="你还没有创建企业资料（或企业未绑定到该商家）。请先去【企业管理】创建企业。"
      style="margin-bottom: 12px"
    />

    <CrudPage
      v-else
      ref="crudRef"
      title="我发布的岗位"
      :api="JobPostApi"
      :columns="columns"
      :formFields="fields"
      :loadFn="loadMyJobs"
      :transformIn="transformIn"
      :transformOut="transformOut"
      :rowActions="rowActions"
      :actionWidth="360"
      :showRefresh="true"
    >
      <template #statusTag="{ row }">
        <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
      </template>

      <template #salary="{ row }">
        {{ row.salaryAmount }} / {{ row.salaryUnit }}
      </template>

      <template #timeRange="{ row }">
        {{ fmt(row.startTime) }} ～ {{ fmt(row.endTime) }}
      </template>
    </CrudPage>
  </el-card>
</template>

<script setup>
import { computed, ref } from "vue";
import { ElMessage } from "element-plus";
import CrudPage from "@/components/CrudPage.vue";
import { useAuthStore } from "@/store/auth";
import { CompanyApi, JobPostApi } from "@/api/Api";

const auth = useAuthStore();
const role = computed(() => auth.role);
const me = computed(() => auth.user);

const crudRef = ref(null);
const loading = ref(false);

const companyId = ref(null); // 当前商家企业ID（默认取第一条企业）

function fmt(v) {
  if (!v) return "";
  return String(v).replace("T", " ");
}

function statusText(s) {
  if (s === "DRAFT") return "草稿";
  if (s === "PUBLISHED") return "发布中";
  if (s === "OFFLINE") return "已下架";
  if (s === "CLOSED") return "已关闭";
  return s || "";
}
function statusType(s) {
  if (s === "PUBLISHED") return "success";
  if (s === "DRAFT") return "info";
  if (s === "OFFLINE") return "warning";
  if (s === "CLOSED") return "danger";
  return "";
}

/** columns：只展示商家关心的字段 */
const columns = [
  { prop: "id", label: "ID", width: 70 },
  { prop: "title", label: "标题", width: 220 },
  { prop: "category", label: "分类", width: 120 },
  { prop: "workCity", label: "城市", width: 140 },
  { prop: "salary", label: "薪资", slot: "salary", width: 140 },
  { prop: "timeRange", label: "时间", slot: "timeRange", width: 260 },
  { prop: "headcount", label: "人数", width: 80 },
  { prop: "appliedCount", label: "已报", width: 80 },
  { prop: "status", label: "状态", slot: "statusTag", width: 110 },
];

/** formFields：商家可编辑字段（不让商家自己填 companyId，自动绑定到当前企业） */
const fields = [
  { prop: "title", label: "标题", type: "text" },
  { prop: "category", label: "分类", type: "text" },
  { prop: "salaryAmount", label: "薪资", type: "number" },
  {
    prop: "salaryUnit",
    label: "薪资单位",
    type: "select",
    options: [
      { label: "按小时", value: "HOUR" },
      { label: "按天", value: "DAY" },
      { label: "按单", value: "TASK" },
    ],
  },
  {
    prop: "settlementType",
    label: "结算方式",
    type: "select",
    options: [
      { label: "日结", value: "DAILY" },
      { label: "周结", value: "WEEKLY" },
      { label: "月结", value: "MONTHLY" },
    ],
  },
  { prop: "workCity", label: "城市", type: "text" },
  { prop: "workAddress", label: "地址", type: "text" },
  { prop: "startTime", label: "开始时间(ISO)", type: "text", placeholder: "2026-01-08T18:00:00" },
  { prop: "endTime", label: "结束时间(ISO)", type: "text", placeholder: "2026-01-08T23:00:00" },
  { prop: "headcount", label: "招募人数", type: "number" },
  { prop: "contactPhone", label: "联系电话", type: "text" },
  { prop: "coverImg", label: "封面图(image/...)", type: "text" },
  { prop: "attachmentFile", label: "附件(file/...)", type: "text" },
  { prop: "description", label: "岗位描述", type: "textarea" },
  {
    prop: "status",
    label: "岗位状态",
    type: "select",
    options: [
      { label: "草稿", value: "DRAFT" },
      { label: "发布", value: "PUBLISHED" },
      { label: "下架", value: "OFFLINE" },
      { label: "关闭", value: "CLOSED" },
    ],
  },
];

/** 只加载当前商家企业的岗位 */
async function loadMyJobs() {
  console.log("loadMyJobs......");
  if (role.value !== "MERCHANT") return [];
  if (!me.value?.id) return [];

  loading.value = true;
  console.log("loadMyJobs......", me.value);
 
  try {
    // 1) 找当前商家的企业（取第一条企业；你业务上可以限制只能一条）
    const allCompanies = await CompanyApi.list();
    const myCompanies = allCompanies.filter((c) => c.merchant?.id === me.value.id);

    companyId.value = myCompanies[0]?.id || null;
    if (!companyId.value) return [];

    // 2) 加载所有岗位 -> 过滤出 companyId = 我的企业
    const allJobs = await JobPostApi.list();
    return allJobs
      .filter((j) => j.company?.id === companyId.value)
      .sort((a, b) => (b.id || 0) - (a.id || 0));
  } finally {
    loading.value = false;
  }
}

/** row -> form（编辑时把 company 展开不需要，保留原字段即可） */
function transformIn(row) {
  return {
    ...row,
    // company 不让商家编辑，不需要 companyId
  };
}

/** form -> payload（保存时强制绑定到当前商家企业） */
function transformOut(form) {
  const payload = { ...form };
  payload.company = { id: companyId.value }; // 关键：商家岗位永远属于自己的企业
  return payload;
}

/** 快捷业务按钮：发布/下架/关闭 */
async function quickSetStatus(row, status) {
  try {
    await JobPostApi.update(row.id, {
      ...row,
      company: { id: row.company?.id || companyId.value },
      status,
    });
    ElMessage.success(`已更新状态为：${statusText(status)}`);
    await crudRef.value.load();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

const rowActions = [
  {
    label: "发布",
    type: "success",
    disabled: (row) => row.status === "PUBLISHED",
    onClick: (row) => quickSetStatus(row, "PUBLISHED"),
  },
  {
    label: "下架",
    type: "warning",
    disabled: (row) => row.status === "OFFLINE" || row.status === "CLOSED",
    onClick: (row) => quickSetStatus(row, "OFFLINE"),
  },
  {
    label: "关闭",
    type: "danger",
    disabled: (row) => row.status === "CLOSED",
    onClick: (row) => quickSetStatus(row, "CLOSED"),
  },
];

async function reloadAll() {
  if (crudRef.value?.load) await crudRef.value.load();
}
</script>
