<template>
  <!-- 管理员：统计大盘 -->
  <div v-if="role === 'ADMIN'">
    <!-- 顶部卡片 -->
    <el-row :gutter="12" style="margin-bottom: 12px;">
      <el-col :span="6">
        <el-card>
          <div style="font-size: 13px; color:#666;">用户总数</div>
          <div style="font-size: 28px; font-weight: 700; margin-top: 6px;">{{ kpi.userTotal }}</div>
          <div style="margin-top:8px; color:#888;">
            求职者 {{ kpi.jobseekerTotal }} ｜商家 {{ kpi.merchantTotal }} ｜管理员 {{ kpi.adminTotal }}
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <div style="font-size: 13px; color:#666;">岗位总数</div>
          <div style="font-size: 28px; font-weight: 700; margin-top: 6px;">{{ kpi.jobTotal }}</div>
          <div style="margin-top:8px; color:#888;">
            发布中 {{ kpi.jobPublished }} ｜草稿 {{ kpi.jobDraft }} ｜下架 {{ kpi.jobOffline }}
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <div style="font-size: 13px; color:#666;">企业总数</div>
          <div style="font-size: 28px; font-weight: 700; margin-top: 6px;">{{ kpi.companyTotal }}</div>
          <div style="margin-top:8px; color:#888;">
            待审 {{ kpi.companyPending }} ｜通过 {{ kpi.companyApproved }} ｜驳回 {{ kpi.companyRejected }}
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <div style="font-size: 13px; color:#666;">举报工单总数</div>
          <div style="font-size: 28px; font-weight: 700; margin-top: 6px;">{{ kpi.complaintTotal }}</div>
          <div style="margin-top:8px; color:#888;">
            待处理 {{ kpi.complaintPending }} ｜处理中 {{ kpi.complaintProcessing }} ｜已完成 {{ kpi.complaintDone }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="12">
      <el-col :span="16">
        <el-card>
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div style="font-weight:700;">近 14 天趋势（新增用户 / 新增岗位 / 新增工单）</div>
            <el-button size="small" @click="reload" :loading="loading">刷新</el-button>
          </div>
          <div ref="trendRef" style="height: 360px; margin-top: 10px;"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card style="margin-bottom: 12px;">
          <div style="font-weight:700;">工单状态分布</div>
          <div ref="complaintPieRef" style="height: 180px; margin-top: 10px;"></div>
        </el-card>

        <el-card>
          <div style="font-weight:700;">企业审核分布</div>
          <div ref="companyBarRef" style="height: 180px; margin-top: 10px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 非管理员：首页 -->
  <el-card v-else>
    <div style="font-weight:700; margin-bottom:8px;">欢迎进入系统</div>
    <div>左侧选择功能菜单进行操作。</div>
  </el-card>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref } from "vue";
import * as echarts from "echarts";
import { useAuthStore } from "@/store/auth";
import { ElMessage } from "element-plus";
import { UserApi, CompanyApi, ComplaintApi, JobPostApi } from "@/api/Api";

const auth = useAuthStore();
const role = computed(() => auth.role);

const loading = ref(false);

// KPI
const kpi = reactive({
  userTotal: 0,
  jobseekerTotal: 0,
  merchantTotal: 0,
  adminTotal: 0,

  jobTotal: 0,
  jobPublished: 0,
  jobDraft: 0,
  jobOffline: 0,

  companyTotal: 0,
  companyPending: 0,
  companyApproved: 0,
  companyRejected: 0,

  complaintTotal: 0,
  complaintPending: 0,
  complaintProcessing: 0,
  complaintDone: 0,
});

// 图表 DOM
const trendRef = ref(null);
const complaintPieRef = ref(null);
const companyBarRef = ref(null);

// 图表实例
let trendChart = null;
let complaintPieChart = null;
let companyBarChart = null;

function dayKeyFromIso(iso) {
  // 支持：2026-01-06T12:00:00 或 Date
  if (!iso) return null;
  const d = new Date(iso);
  if (isNaN(d.getTime())) return null;
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${dd}`;
}

function lastNDaysLabels(n = 14) {
  const labels = [];
  const now = new Date();
  for (let i = n - 1; i >= 0; i--) {
    const d = new Date(now);
    d.setDate(now.getDate() - i);
    labels.push(dayKeyFromIso(d));
  }
  return labels;
}

function countByDay(list, getIsoFn, labels) {
  const map = new Map();
  for (const it of list) {
    const key = dayKeyFromIso(getIsoFn(it));
    if (!key) continue;
    map.set(key, (map.get(key) || 0) + 1);
  }
  return labels.map((d) => map.get(d) || 0);
}

function safeDestroy(chart) {
  try {
    if (chart) chart.dispose();
  } catch {}
}

function renderCharts({ labels, userSeries, jobSeries, complaintSeries, complaintStatus, companyStatus }) {
  // 1) 折线：趋势
  if (!trendChart) trendChart = echarts.init(trendRef.value);
  trendChart.setOption({
    tooltip: { trigger: "axis" },
    legend: { data: ["新增用户", "新增岗位", "新增工单"] },
    grid: { left: 40, right: 20, top: 40, bottom: 30 },
    xAxis: { type: "category", data: labels },
    yAxis: { type: "value" },
    series: [
      { name: "新增用户", type: "line", smooth: true, data: userSeries },
      { name: "新增岗位", type: "line", smooth: true, data: jobSeries },
      { name: "新增工单", type: "line", smooth: true, data: complaintSeries },
    ],
  });

  // 2) 饼图：工单状态
  if (!complaintPieChart) complaintPieChart = echarts.init(complaintPieRef.value);
  complaintPieChart.setOption({
    tooltip: { trigger: "item" },
    series: [
      {
        type: "pie",
        radius: ["40%", "70%"],
        data: [
          { name: "待处理", value: complaintStatus.PENDING || 0 },
          { name: "处理中", value: complaintStatus.PROCESSING || 0 },
          { name: "已完成", value: complaintStatus.DONE || 0 },
        ],
        label: { formatter: "{b}: {c}" },
      },
    ],
  });

  // 3) 柱状：企业审核
  if (!companyBarChart) companyBarChart = echarts.init(companyBarRef.value);
  companyBarChart.setOption({
    tooltip: { trigger: "axis" },
    grid: { left: 40, right: 20, top: 20, bottom: 30 },
    xAxis: { type: "category", data: ["待审核", "通过", "驳回"] },
    yAxis: { type: "value" },
    series: [
      {
        type: "bar",
        data: [
          companyStatus.PENDING || 0,
          companyStatus.APPROVED || 0,
          companyStatus.REJECTED || 0,
        ],
      },
    ],
  });
}

async function reload() {
  if (role.value !== "ADMIN") return;

  loading.value = true;
  try {
    // 并发拉全量数据（毕设足够，后期可改后端筛选接口）
    const [users, companies, complaints, jobs] = await Promise.all([
      UserApi.list(),
      CompanyApi.list(),
      ComplaintApi.list(),
      JobPostApi.list(),
    ]);

    // ===== KPI 统计 =====
    kpi.userTotal = users.length;
    kpi.jobseekerTotal = users.filter(u => u.role === "JOBSEEKER").length;
    kpi.merchantTotal = users.filter(u => u.role === "MERCHANT").length;
    kpi.adminTotal = users.filter(u => u.role === "ADMIN").length;

    kpi.jobTotal = jobs.length;
    kpi.jobPublished = jobs.filter(j => j.status === "PUBLISHED").length;
    kpi.jobDraft = jobs.filter(j => j.status === "DRAFT").length;
    kpi.jobOffline = jobs.filter(j => j.status === "OFFLINE").length;

    kpi.companyTotal = companies.length;
    kpi.companyPending = companies.filter(c => c.verifyStatus === "PENDING").length;
    kpi.companyApproved = companies.filter(c => c.verifyStatus === "APPROVED").length;
    kpi.companyRejected = companies.filter(c => c.verifyStatus === "REJECTED").length;

    kpi.complaintTotal = complaints.length;
    kpi.complaintPending = complaints.filter(c => c.status === "PENDING").length;
    kpi.complaintProcessing = complaints.filter(c => c.status === "PROCESSING").length;
    kpi.complaintDone = complaints.filter(c => c.status === "DONE").length;

    // ===== 趋势统计（近14天，按 createTime）=====
    const labels = lastNDaysLabels(14);

    const userSeries = countByDay(users, (u) => u.createTime, labels);
    const jobSeries = countByDay(jobs, (j) => j.createTime, labels);
    const complaintSeries = countByDay(complaints, (c) => c.createTime, labels);

    const complaintStatus = {
      PENDING: kpi.complaintPending,
      PROCESSING: kpi.complaintProcessing,
      DONE: kpi.complaintDone,
    };
    const companyStatus = {
      PENDING: kpi.companyPending,
      APPROVED: kpi.companyApproved,
      REJECTED: kpi.companyRejected,
    };

    await nextTick();
    renderCharts({ labels, userSeries, jobSeries, complaintSeries, complaintStatus, companyStatus });
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "首页统计加载失败");
  } finally {
    loading.value = false;
  }
}

function onResize() {
  if (trendChart) trendChart.resize();
  if (complaintPieChart) complaintPieChart.resize();
  if (companyBarChart) companyBarChart.resize();
}

onMounted(async () => {
  if (role.value === "ADMIN") {
    await reload();
    window.addEventListener("resize", onResize);
  }
});

onUnmounted(() => {
  window.removeEventListener("resize", onResize);
  safeDestroy(trendChart); trendChart = null;
  safeDestroy(complaintPieChart); complaintPieChart = null;
  safeDestroy(companyBarChart); companyBarChart = null;
});
</script>
