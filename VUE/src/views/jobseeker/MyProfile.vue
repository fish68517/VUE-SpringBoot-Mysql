<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <div style="font-weight:700">我的资料 / 简历</div>
      <div style="display:flex; gap:8px;">
        <el-button @click="reload" :loading="loading">刷新</el-button>
        <el-button type="primary" @click="save" :loading="saving" :disabled="!profileLoaded">
          保存修改
        </el-button>
      </div>
    </div>

    <el-alert
      v-if="role !== 'JOBSEEKER'"
      type="warning"
      show-icon
      :closable="false"
      title="该页面仅求职者使用。你当前不是求职者角色。"
      style="margin-bottom: 12px"
    />

    <div v-if="role === 'JOBSEEKER'">
      <el-empty v-if="!profileLoaded && !loading" description="你还没有创建资料">
        <el-button type="primary" @click="createMine" :loading="saving">创建我的资料</el-button>
      </el-empty>

      <div v-else>
        <el-form :model="form" label-width="120px">
          <el-form-item label="用户ID">
            <el-input :model-value="me?.id" disabled />
          </el-form-item>

          <el-form-item label="真实姓名">
            <el-input v-model="form.realName" />
          </el-form-item>

          <el-form-item label="性别">
            <el-select v-model="form.gender" style="width: 100%">
              <el-option label="男" value="M" />
              <el-option label="女" value="F" />
              <el-option label="未知" value="U" />
            </el-select>
          </el-form-item>

          <el-form-item label="年龄">
            <el-input v-model.number="form.age" type="number" />
          </el-form-item>

          <el-form-item label="城市">
            <el-input v-model="form.city" placeholder="例如：Los Angeles" />
          </el-form-item>

          <el-form-item label="技能">
            <el-input v-model="form.skills" placeholder="例如：餐饮/收银/搬运..." />
          </el-form-item>

          <el-form-item label="可工作时间">
            <el-input v-model="form.availableTimeDesc" placeholder="例如：周末/晚上 18:00-22:00" />
          </el-form-item>

          <el-divider />

          <el-form-item label="简历文件路径">
            <el-input v-model="form.resumeFile" placeholder="file/resume_xxx.pdf" />
            <div style="margin-top:6px;">
              <el-link v-if="resumeUrl" :href="resumeUrl" target="_blank">预览/下载简历</el-link>
              <el-text v-else type="info">填写后可点击预览</el-text>
            </div>
          </el-form-item>

          <el-form-item label="证件正面路径">
            <el-input v-model="form.idCardFront" placeholder="image/id_front_xxx.jpg" />
            <div style="margin-top:6px;">
              <el-link v-if="idFrontUrl" :href="idFrontUrl" target="_blank">查看证件正面</el-link>
              <el-text v-else type="info">填写后可点击查看</el-text>
            </div>
          </el-form-item>

          <el-form-item label="证件反面路径">
            <el-input v-model="form.idCardBack" placeholder="image/id_back_xxx.jpg" />
            <div style="margin-top:6px;">
              <el-link v-if="idBackUrl" :href="idBackUrl" target="_blank">查看证件反面</el-link>
              <el-text v-else type="info">填写后可点击查看</el-text>
            </div>
          </el-form-item>

          <el-form-item label="认证状态">
            <el-tag :type="verifyTagType(form.verifyStatus)">
              {{ verifyText(form.verifyStatus) }}
            </el-tag>
            <el-text type="info" style="margin-left:10px;">
              （认证由管理员审核，本页不允许手动改）
            </el-text>
          </el-form-item>
        </el-form>

        <el-divider />

        <el-descriptions title="预览信息" :column="2" border>
          <el-descriptions-item label="姓名">{{ form.realName }}</el-descriptions-item>
          <el-descriptions-item label="城市">{{ form.city }}</el-descriptions-item>
          <el-descriptions-item label="技能">{{ form.skills }}</el-descriptions-item>
          <el-descriptions-item label="可工作时间">{{ form.availableTimeDesc }}</el-descriptions-item>
          <el-descriptions-item label="简历">
            <el-link v-if="resumeUrl" :href="resumeUrl" target="_blank">{{ form.resumeFile }}</el-link>
            <el-text v-else type="info">未填写</el-text>
          </el-descriptions-item>
          <el-descriptions-item label="证件">
            <span v-if="idFrontUrl || idBackUrl">
              <el-link v-if="idFrontUrl" :href="idFrontUrl" target="_blank">正面</el-link>
              <span v-if="idFrontUrl && idBackUrl"> / </span>
              <el-link v-if="idBackUrl" :href="idBackUrl" target="_blank">反面</el-link>
            </span>
            <el-text v-else type="info">未填写</el-text>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { JobseekerProfileApi } from "@/api/Api";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const me = computed(() => auth.user);
const role = computed(() => auth.role);

// 读 Api.js 的 baseURL（与你 axios baseURL 一致）
// 这里直接从环境变量推导（如果你 Api.js 里 baseURL 是 VITE_API_BASE_URL）
const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const loading = ref(false);
const saving = ref(false);

const profileId = ref(null);
const profileLoaded = computed(() => !!profileId.value);

// 单条资料表单
const form = reactive({
  realName: "",
  gender: "U",
  age: null,
  city: "",
  skills: "",
  availableTimeDesc: "",
  resumeFile: "",
  idCardFront: "",
  idCardBack: "",
  verifyStatus: "PENDING",
});

function verifyText(s) {
  if (s === "PENDING") return "待审核";
  if (s === "APPROVED") return "已通过";
  if (s === "REJECTED") return "已驳回";
  return s || "";
}
function verifyTagType(s) {
  if (s === "PENDING") return "info";
  if (s === "APPROVED") return "success";
  if (s === "REJECTED") return "danger";
  return "";
}

// 生成可点击预览的 URL：BASE_URL + "/" + "file/xxx.pdf"
function makeUrl(path) {
  if (!path) return "";
  // 如果用户已经填了 http 开头，就直接用
  if (path.startsWith("http://") || path.startsWith("https://")) return path;
  const p = path.startsWith("/") ? path : `/${path}`;
  return `${BASE_URL}${p}`;
}

const resumeUrl = computed(() => makeUrl(form.resumeFile));
const idFrontUrl = computed(() => makeUrl(form.idCardFront));
const idBackUrl = computed(() => makeUrl(form.idCardBack));

function fillFromRow(row) {
  profileId.value = row.id;
  form.realName = row.realName || "";
  form.gender = row.gender || "U";
  form.age = row.age ?? null;
  form.city = row.city || "";
  form.skills = row.skills || "";
  form.availableTimeDesc = row.availableTimeDesc || "";
  form.resumeFile = row.resumeFile || "";
  form.idCardFront = row.idCardFront || "";
  form.idCardBack = row.idCardBack || "";
  form.verifyStatus = row.verifyStatus || "PENDING";
}

async function reload() {
  if (role.value !== "JOBSEEKER") return;
  if (!me.value?.id) return;

  loading.value = true;
  try {
    const all = await JobseekerProfileApi.list();
    const mine = all.find(p => p.user?.id === me.value.id);
    if (!mine) {
      profileId.value = null;
      // 清空表单
      fillFromRow({
        id: null,
        realName: "",
        gender: "U",
        age: null,
        city: "",
        skills: "",
        availableTimeDesc: "",
        resumeFile: "",
        idCardFront: "",
        idCardBack: "",
        verifyStatus: "PENDING",
      });
      return;
    }
    fillFromRow(mine);
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "加载失败");
  } finally {
    loading.value = false;
  }
}

async function createMine() {
  if (!me.value?.id) return;
  saving.value = true;
  try {
    const created = await JobseekerProfileApi.create({
      user: { id: me.value.id },
      realName: form.realName || "未填写",
      gender: form.gender || "U",
      age: form.age,
      city: form.city || "",
      skills: form.skills || "",
      availableTimeDesc: form.availableTimeDesc || "",
      resumeFile: form.resumeFile || "",
      idCardFront: form.idCardFront || "",
      idCardBack: form.idCardBack || "",
      verifyStatus: "PENDING",
    });
    ElMessage.success("资料已创建");
    fillFromRow(created);
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "创建失败");
  } finally {
    saving.value = false;
  }
}

async function save() {
  if (!me.value?.id) return;
  if (!profileId.value) {
    // 还没创建就走创建
    await createMine();
    return;
  }

  saving.value = true;
  try {
    await JobseekerProfileApi.update(profileId.value, {
      user: { id: me.value.id },
      realName: form.realName,
      gender: form.gender,
      age: form.age,
      city: form.city,
      skills: form.skills,
      availableTimeDesc: form.availableTimeDesc,
      resumeFile: form.resumeFile,
      idCardFront: form.idCardFront,
      idCardBack: form.idCardBack,
      // 认证状态不允许求职者修改：提交原值
      verifyStatus: form.verifyStatus,
    });
    ElMessage.success("保存成功");
    await reload();
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "保存失败");
  } finally {
    saving.value = false;
  }
}

onMounted(reload);
</script>
