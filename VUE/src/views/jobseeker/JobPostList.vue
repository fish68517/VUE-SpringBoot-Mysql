<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <div style="font-weight:700">兼职浏览</div>
      <div>
        <el-button @click="reload">刷新</el-button>
      </div>
    </div>

    <el-form :inline="true" :model="filters" style="margin-bottom: 10px">
      <el-form-item label="关键词">
        <el-input v-model="filters.keyword" placeholder="标题/分类" clearable />
      </el-form-item>
      <el-form-item label="城市">
        <el-input v-model="filters.city" placeholder="Los Angeles" clearable />
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="filters.category" placeholder="餐饮/物流..." clearable />
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="filters.onlyPublished">只看发布中</el-checkbox>
      </el-form-item>
    </el-form>

    <el-table :data="filteredJobs" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="workCity" label="城市" width="140" />
      <el-table-column label="薪资" width="140">
        <template #default="{ row }">
          {{ row.salaryAmount }} / {{ row.salaryUnit }}
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始" width="170">
        <template #default="{ row }">{{ fmt(row.startTime) }}</template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束" width="170">
        <template #default="{ row }">{{ fmt(row.endTime) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="110" />

      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>

          <el-button
            size="small"
            type="primary"
            :disabled="appliedSet.has(row.id) || role !== 'JOBSEEKER'"
            :loading="applyLoadingId === row.id"
            @click="apply(row)"
          >
            {{ appliedSet.has(row.id) ? "已报名" : "报名" }}
          </el-button>

          <el-button
            size="small"
            type="danger"
            plain
            :disabled="role !== 'JOBSEEKER'"
            @click="openReport(row)"
          >
            举报
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 岗位详情 -->
  <el-drawer v-model="drawer" title="岗位详情" size="40%">
    <div v-if="currentJob">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ currentJob.title }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentJob.category }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ currentJob.workCity }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ currentJob.workAddress }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ fmt(currentJob.startTime) }} ～ {{ fmt(currentJob.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="薪资">{{ currentJob.salaryAmount }} / {{ currentJob.salaryUnit }}</el-descriptions-item>
        <el-descriptions-item label="结算">{{ currentJob.settlementType }}</el-descriptions-item>
        <el-descriptions-item label="招募人数">{{ currentJob.headcount }}</el-descriptions-item>
        <el-descriptions-item label="已报名">{{ currentJob.appliedCount }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentJob.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="封面图">{{ currentJob.coverImg }}</el-descriptions-item>
        <el-descriptions-item label="附件">{{ currentJob.attachmentFile }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ currentJob.description }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 12px; display:flex; gap:8px; flex-wrap: wrap;">
        <el-button
          type="primary"
          :disabled="appliedSet.has(currentJob.id) || role !== 'JOBSEEKER'"
          :loading="applyLoadingId === currentJob.id"
          @click="apply(currentJob)"
        >
          {{ appliedSet.has(currentJob.id) ? "已报名" : "立即报名" }}
        </el-button>

        <el-button
          type="danger"
          plain
          :disabled="role !== 'JOBSEEKER'"
          @click="openReport(currentJob)"
        >
          举报岗位
        </el-button>

        <el-button @click="drawer=false">关闭</el-button>
      </div>
    </div>
  </el-drawer>

  <!-- 举报弹窗 -->
  <el-dialog v-model="reportDlg" title="举报岗位" width="520px">
    <el-form :model="reportForm" label-width="110px">
      <el-form-item label="岗位">
        <el-input :model-value="reportTargetTitle" disabled />
      </el-form-item>

      <el-form-item label="举报类型">
        <el-select v-model="reportForm.type" style="width:100%">
          <el-option label="虚假信息" value="虚假信息" />
          <el-option label="联系方式异常" value="联系方式异常" />
          <el-option label="薪资争议" value="薪资争议" />
          <el-option label="安全风险" value="安全风险" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>

      <el-form-item label="举报内容">
        <el-input v-model="reportForm.content" type="textarea" :rows="4" placeholder="请描述问题详情..." />
      </el-form-item>

      <el-form-item label="证据图片">
        <el-input v-model="reportForm.evidenceImg" placeholder="image/complaint_xxx.png（可选）" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="reportDlg=false">取消</el-button>
      <el-button type="primary" :loading="reportLoading" @click="submitReport">提交举报</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { JobPostApi, ApplicationApi, ComplaintApi } from "@/api/Api";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const role = computed(() => auth.role);
const user = computed(() => auth.user);

const jobs = ref([]);
const myApps = ref([]);
const appliedSet = computed(() => new Set(myApps.value.map(a => a.jobPost?.id)));

const filters = reactive({
  keyword: "",
  city: "",
  category: "",
  onlyPublished: true,
});

const drawer = ref(false);
const currentJob = ref(null);

const applyLoadingId = ref(null);

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
  jobs.value = await JobPostApi.list();

  if (user.value?.id) {
    const allApps = await ApplicationApi.list();
    myApps.value = allApps.filter(a => a.jobseeker?.id === user.value.id);
  } else {
    myApps.value = [];
  }
}

const filteredJobs = computed(() => {
  const kw = filters.keyword.trim().toLowerCase();
  const city = filters.city.trim().toLowerCase();
  const cat = filters.category.trim().toLowerCase();

  return jobs.value
    .filter(j => !filters.onlyPublished || j.status === "PUBLISHED")
    .filter(j => !kw || (j.title || "").toLowerCase().includes(kw) || (j.category || "").toLowerCase().includes(kw))
    .filter(j => !city || (j.workCity || "").toLowerCase().includes(city))
    .filter(j => !cat || (j.category || "").toLowerCase().includes(cat));
});

function openDetail(row) {
  currentJob.value = row;
  drawer.value = true;
}

async function apply(job) {
  if (role.value !== "JOBSEEKER") return;
  if (!user.value?.id) return;

  applyLoadingId.value = job.id;
  try {
    await ApplicationApi.create({
      jobPost: { id: job.id },
      jobseeker: { id: user.value.id },
      status: "APPLIED",
      applyTime: nowLocalIso(),
      merchantNote: "",
      cancelReason: "",
    });
    ElMessage.success("报名成功");
    await reload();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "报名失败（可能重复报名）");
  } finally {
    applyLoadingId.value = null;
  }
}

/* =======================
   举报岗位（业务入口）
   ======================= */
const reportDlg = ref(false);
const reportLoading = ref(false);
const reportTargetJob = ref(null);

const reportForm = reactive({
  type: "虚假信息",
  content: "",
  evidenceImg: "",
});

const reportTargetTitle = computed(() => {
  const j = reportTargetJob.value;
  if (!j) return "";
  return `#${j.id} ${j.title || ""}`;
});

function openReport(job) {
  if (role.value !== "JOBSEEKER") {
    ElMessage.warning("仅求职者可举报岗位");
    return;
  }
  reportTargetJob.value = job;
  reportForm.type = "虚假信息";
  reportForm.content = "";
  reportForm.evidenceImg = "";
  reportDlg.value = true;
}

async function submitReport() {
  if (!user.value?.id) return;
  const job = reportTargetJob.value;
  if (!job?.id) return;

  if (!reportForm.content.trim()) {
    ElMessage.warning("请填写举报内容");
    return;
  }

  reportLoading.value = true;
  try {
    await ComplaintApi.create({
      fromUser: { id: user.value.id },
      targetType: "JOB",
      targetId: job.id,
      type: reportForm.type,
      content: reportForm.content,
      evidenceImg: reportForm.evidenceImg || null,
      status: "PENDING",
      result: null,
      handlerAdmin: null,
      handleTime: null,
    });

    ElMessage.success("举报已提交");
    reportDlg.value = false;
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "提交失败");
  } finally {
    reportLoading.value = false;
  }
}

onMounted(reload);
</script>
