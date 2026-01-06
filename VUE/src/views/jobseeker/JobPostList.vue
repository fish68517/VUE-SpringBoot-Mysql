<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <div style="font-weight:700;">兼职浏览</div>
      <el-button @click="reload" :loading="loading">刷新</el-button>
    </div>

    <!-- 筛选 -->
    <el-form :inline="true" :model="filters" style="margin-bottom: 12px;">
      <el-form-item label="城市">
        <el-input v-model="filters.workCity" placeholder="Los Angeles" clearable />
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="filters.category" placeholder="餐饮/零售/物流" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.status" clearable placeholder="全部">
          <el-option label="发布" value="PUBLISHED" />
          <el-option label="下架" value="OFFLINE" />
          <el-option label="关闭" value="CLOSED" />
          <el-option label="草稿" value="DRAFT" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="applyFilters">筛选</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table :data="displayJobs" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="companyName" label="企业" width="160" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="workCity" label="城市" width="140" />
      <el-table-column label="薪资" width="140">
        <template #default="{ row }">
          {{ row.salaryAmount }} / {{ row.salaryUnit }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
          <el-button
            size="small"
            type="primary"
            :disabled="!canApply(row)"
            @click="applyJob(row)"
          >
            报名
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 详情弹窗 -->
  <el-dialog v-model="detailDlg" title="岗位详情" width="700px">
    <div v-if="currentJob">
      <div style="font-weight:700; font-size:16px; margin-bottom:8px;">
        {{ currentJob.title }}
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="企业">{{ currentJob.companyName }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentJob.category }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ currentJob.workCity }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ currentJob.workAddress }}</el-descriptions-item>
        <el-descriptions-item label="时间">
          {{ currentJob.startTime }} ~ {{ currentJob.endTime }}
        </el-descriptions-item>
        <el-descriptions-item label="招募人数">{{ currentJob.headcount }}</el-descriptions-item>
        <el-descriptions-item label="已报名">{{ currentJob.appliedCount }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentJob.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="封面图">
          <span>{{ currentJob.coverImg }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="附件">
          <span>{{ currentJob.attachmentFile }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-divider />
      <div style="white-space: pre-wrap;">{{ currentJob.description }}</div>
    </div>

    <template #footer>
      <el-button @click="detailDlg=false">关闭</el-button>
      <el-button type="primary" :disabled="!currentJob || !canApply(currentJob)" @click="applyJob(currentJob)">
        立即报名
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { CompanyApi, JobPostApi, ApplicationApi } from "@/api/Api";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const loading = ref(false);

const companies = ref([]);
const jobsRaw = ref([]);
const displayJobs = ref([]);

const detailDlg = ref(false);
const currentJob = ref(null);

const filters = reactive({
  workCity: "",
  category: "",
  status: "PUBLISHED",
});

function nowLocalIso() {
  // Spring 的 LocalDateTime 更稳：YYYY-MM-DDTHH:mm:ss（不要 Z、不要毫秒）
  return new Date().toISOString().slice(0, 19);
}

const companyMap = computed(() => {
  const m = new Map();
  companies.value.forEach((c) => m.set(c.id, c));
  return m;
});

function enrichJobs(list) {
  return list.map((j) => {
    const c = j.company?.id ? companyMap.value.get(j.company.id) : null;
    return {
      ...j,
      companyName: c?.name || `Company#${j.company?.id ?? "-"}`,
      salaryAmount: j.salaryAmount,
      salaryUnit: j.salaryUnit,
    };
  });
}

async function reload() {
  loading.value = true;
  try {
    companies.value = await CompanyApi.list();
    const jobs = await JobPostApi.list();
    jobsRaw.value = enrichJobs(jobs);
    applyFilters();
  } finally {
    loading.value = false;
  }
}

function applyFilters() {
  const wc = (filters.workCity || "").trim().toLowerCase();
  const cat = (filters.category || "").trim().toLowerCase();
  const st = filters.status;

  displayJobs.value = jobsRaw.value.filter((j) => {
    if (st && j.status !== st) return false;
    if (wc && !(j.workCity || "").toLowerCase().includes(wc)) return false;
    if (cat && !(j.category || "").toLowerCase().includes(cat)) return false;
    return true;
  });
}

function resetFilters() {
  filters.workCity = "";
  filters.category = "";
  filters.status = "PUBLISHED";
  applyFilters();
}

function openDetail(row) {
  currentJob.value = row;
  detailDlg.value = true;
}

function canApply(job) {
  if (!auth.user) return false;
  if (auth.user.role !== "JOBSEEKER") return false;
  if (job.status !== "PUBLISHED") return false;
  // 你也可以加：人数已满不可报名（这里简单处理）
  if (job.appliedCount != null && job.headcount != null && job.appliedCount >= job.headcount) return false;
  return true;
}

async function applyJob(job) {
  try {
    if (!canApply(job)) return;

    const payload = {
      jobPost: { id: job.id },
      jobseeker: { id: auth.user.id },
      status: "APPLIED",
      applyTime: nowLocalIso(),
      merchantNote: "",
      cancelReason: "",
      checkinTime: null,
      finishTime: null,
      settleTime: null,
    };

    await ApplicationApi.create(payload);

    ElMessage.success("报名成功");
    detailDlg.value = false;

    // 前端把 applied_count +1（演示用，避免你还没做后端联动统计）
    job.appliedCount = (job.appliedCount || 0) + 1;
  } catch (e) {
    const msg = e?.response?.data || e?.message || "报名失败";
    // 后端 ApplicationController 对重复报名返回 409 duplicate application
    ElMessage.error(msg);
  }
}

onMounted(reload);
</script>
