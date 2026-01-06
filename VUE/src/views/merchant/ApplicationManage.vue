<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <div style="font-weight:700">报名管理（商家）</div>
      <div style="display:flex; gap:8px;">
        <el-button @click="reload">刷新</el-button>
      </div>
    </div>

    <el-alert
      type="info"
      show-icon
      :closable="false"
      style="margin-bottom: 12px"
      title="说明：本页面通过前端过滤展示“属于本商家企业的岗位报名”，并用更新 status 实现录用/拒绝/到岗/完结/结算。"
    />

    <el-form :inline="true" :model="filters" style="margin-bottom: 10px">
      <el-form-item label="状态">
        <el-select v-model="filters.status" clearable placeholder="全部" style="width: 160px">
          <el-option label="已报名" value="APPLIED" />
          <el-option label="已录用" value="ACCEPTED" />
          <el-option label="已拒绝" value="REJECTED" />
          <el-option label="已到岗" value="CHECKED_IN" />
          <el-option label="已完结" value="FINISHED" />
          <el-option label="已结算" value="SETTLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键字">
        <el-input v-model="filters.keyword" placeholder="岗位标题/求职者账号" clearable />
      </el-form-item>
    </el-form>

    <el-table :data="filteredRows" border>
      <el-table-column prop="id" label="报名ID" width="90" />
      <el-table-column prop="jobTitle" label="岗位" min-width="220" />
      <el-table-column prop="jobseekerName" label="求职者" width="160" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="applyTime" label="报名时间" width="170">
        <template #default="{ row }">{{ fmt(row.applyTime) }}</template>
      </el-table-column>

      <el-table-column label="操作" width="420">
        <template #default="{ row }">
          <el-button size="small" type="success" :disabled="row.status !== 'APPLIED'" @click="setStatus(row, 'ACCEPTED')">
            录用
          </el-button>
          <el-button size="small" type="danger" :disabled="row.status !== 'APPLIED'" @click="setStatus(row, 'REJECTED')">
            拒绝
          </el-button>

          <el-divider direction="vertical" />

          <el-button size="small" :disabled="row.status !== 'ACCEPTED'" @click="setStatus(row, 'CHECKED_IN', true)">
            标记到岗
          </el-button>
          <el-button size="small" :disabled="row.status !== 'CHECKED_IN'" @click="setStatus(row, 'FINISHED', true)">
            标记完结
          </el-button>
          <el-button size="small" type="primary" :disabled="row.status !== 'FINISHED'" @click="setStatus(row, 'SETTLED', true)">
            结算
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useAuthStore } from "@/store/auth";
import { CompanyApi, JobPostApi, ApplicationApi, UserApi } from "@/api/Api";

const auth = useAuthStore();

const filters = reactive({
  status: "",
  keyword: "",
});

const companies = ref([]);
const jobs = ref([]);
const apps = ref([]);
const users = ref([]);

function fmt(v) {
  if (!v) return "";
  return String(v).replace("T", " ");
}
function nowLocalIso() {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
}

async function reload() {
  const me = auth.user;
  if (!me?.id) return;

  // 1) 本商家的企业
  const allCompanies = await CompanyApi.list();
  companies.value = allCompanies.filter(c => c.merchant?.id === me.id);

  const companyIdSet = new Set(companies.value.map(c => c.id));

  // 2) 本商家的岗位
  const allJobs = await JobPostApi.list();
  jobs.value = allJobs.filter(j => companyIdSet.has(j.company?.id));

  const jobIdSet = new Set(jobs.value.map(j => j.id));
  const jobMap = new Map(jobs.value.map(j => [j.id, j]));

  // 3) 报名：只保留报名到本商家岗位的
  const allApps = await ApplicationApi.list();
  const relatedApps = allApps.filter(a => jobIdSet.has(a.jobPost?.id));

  // 4) 为了显示求职者账号，拉全量用户做映射（演示联调用，毕设可接受）
  users.value = await UserApi.list();
  const userMap = new Map(users.value.map(u => [u.id, u]));

  // 5) 组合展示字段
  apps.value = relatedApps.map(a => {
    const job = jobMap.get(a.jobPost?.id);
    const js = userMap.get(a.jobseeker?.id);
    return {
      ...a,
      jobTitle: job?.title || `岗位#${a.jobPost?.id}`,
      jobseekerName: js?.username || `用户#${a.jobseeker?.id}`,
    };
  });
}

const filteredRows = computed(() => {
  const kw = filters.keyword.trim().toLowerCase();
  return apps.value
    .filter(a => !filters.status || a.status === filters.status)
    .filter(a => !kw || (a.jobTitle || "").toLowerCase().includes(kw) || (a.jobseekerName || "").toLowerCase().includes(kw));
});

async function setStatus(row, status, setTimeFields = false) {
  try {
    let note = row.merchantNote || "";
    if (status === "REJECTED") {
      const { value } = await ElMessageBox.prompt("请输入拒绝原因（可选）", "拒绝报名", { confirmButtonText: "确定", cancelButtonText: "取消" });
      note = value || note;
    }

    const payload = {
      jobPost: { id: row.jobPost?.id },
      jobseeker: { id: row.jobseeker?.id },
      status,
      applyTime: row.applyTime,
      merchantNote: note,
      cancelReason: row.cancelReason || "",
      checkinTime: row.checkinTime,
      finishTime: row.finishTime,
      settleTime: row.settleTime,
    };

    if (setTimeFields) {
      if (status === "CHECKED_IN") payload.checkinTime = nowLocalIso();
      if (status === "FINISHED") payload.finishTime = nowLocalIso();
      if (status === "SETTLED") payload.settleTime = nowLocalIso();
    }

    await ApplicationApi.update(row.id, payload);
    ElMessage.success("操作成功");
    await reload();
  } catch (e) {
    // 用户取消 prompt 也会进 catch，忽略
    if (e?.message === "cancel") return;
    ElMessage.error(e?.response?.data || e?.message || "操作失败");
  }
}

onMounted(reload);
</script>
