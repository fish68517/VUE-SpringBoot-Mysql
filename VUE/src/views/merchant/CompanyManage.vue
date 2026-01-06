<template>
  <el-card>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
      <div style="font-weight:700">企业资料 / 预览</div>
      <div style="display:flex; gap:8px;">
        <el-button @click="reload" :loading="loading">刷新</el-button>
        <el-button type="primary" @click="save" :loading="saving" :disabled="!companyLoaded">
          保存修改
        </el-button>
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

    <div v-if="role === 'MERCHANT'">
      <el-empty v-if="!companyLoaded && !loading" description="你还没有创建企业资料">
        <el-button type="primary" @click="createMine" :loading="saving">创建企业资料</el-button>
      </el-empty>

      <div v-else>
        <el-form :model="form" label-width="120px">
          <el-form-item label="商家用户ID">
            <el-input :model-value="me?.id" disabled />
          </el-form-item>

          <el-form-item label="企业名称">
            <el-input v-model="form.name" placeholder="请输入企业名称" />
          </el-form-item>

          <el-form-item label="营业执照号">
            <el-input v-model="form.licenseNo" placeholder="请输入执照号" />
          </el-form-item>

          <el-form-item label="执照图片路径">
            <el-input v-model="form.licenseImg" placeholder="image/license_xxx.jpg" />
            <div style="margin-top:6px;">
              <el-link v-if="licenseUrl" :href="licenseUrl" target="_blank">预览执照图片</el-link>
              <el-text v-else type="info">填写后可点击预览</el-text>
            </div>
          </el-form-item>

          <el-form-item label="企业地址">
            <el-input v-model="form.address" placeholder="请输入企业地址" />
          </el-form-item>

          <el-form-item label="联系人">
            <el-input v-model="form.contactName" placeholder="联系人姓名" />
          </el-form-item>

          <el-form-item label="联系电话">
            <el-input v-model="form.contactPhone" placeholder="联系人电话" />
          </el-form-item>

          <el-form-item label="审核状态">
            <el-tag :type="verifyTagType(form.verifyStatus)">
              {{ verifyText(form.verifyStatus) }}
            </el-tag>
            <el-text type="info" style="margin-left:10px;">
              （审核由管理员处理，本页不允许修改）
            </el-text>
          </el-form-item>

          <el-form-item v-if="form.verifyStatus === 'REJECTED'" label="驳回原因">
            <el-input v-model="form.rejectReason" type="textarea" :rows="2" disabled />
          </el-form-item>
        </el-form>

        <el-divider />

        <el-descriptions title="企业资料预览" :column="2" border>
          <el-descriptions-item label="企业名称">{{ form.name }}</el-descriptions-item>
          <el-descriptions-item label="执照号">{{ form.licenseNo }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ form.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ form.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ form.address }}</el-descriptions-item>
          <el-descriptions-item label="执照图片" :span="2">
            <el-link v-if="licenseUrl" :href="licenseUrl" target="_blank">{{ form.licenseImg }}</el-link>
            <el-text v-else type="info">未填写</el-text>
          </el-descriptions-item>
          <el-descriptions-item label="审核状态" :span="2">
            <el-tag :type="verifyTagType(form.verifyStatus)">{{ verifyText(form.verifyStatus) }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { CompanyApi } from "@/api/Api";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const me = computed(() => auth.user);
const role = computed(() => auth.role);

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const loading = ref(false);
const saving = ref(false);

const companyId = ref(null);
const companyLoaded = computed(() => !!companyId.value);

const form = reactive({
  name: "",
  licenseNo: "",
  licenseImg: "",
  address: "",
  contactName: "",
  contactPhone: "",
  verifyStatus: "PENDING",
  rejectReason: "",
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

function makeUrl(path) {
  if (!path) return "";
  if (path.startsWith("http://") || path.startsWith("https://")) return path;
  const p = path.startsWith("/") ? path : `/${path}`;
  return `${BASE_URL}${p}`;
}
const licenseUrl = computed(() => makeUrl(form.licenseImg));

function fillFromRow(row) {
  companyId.value = row.id;
  form.name = row.name || "";
  form.licenseNo = row.licenseNo || "";
  form.licenseImg = row.licenseImg || "";
  form.address = row.address || "";
  form.contactName = row.contactName || "";
  form.contactPhone = row.contactPhone || "";
  form.verifyStatus = row.verifyStatus || "PENDING";
  form.rejectReason = row.rejectReason || "";
}

async function reload() {
  if (role.value !== "MERCHANT") return;
  if (!me.value?.id) return;

  loading.value = true;
  try {
    const all = await CompanyApi.list();
    // 只取当前商家的企业（取第一条；正常业务应限制只能一条）
    const mine = all.find(c => c.merchant?.id === me.value.id);

    if (!mine) {
      companyId.value = null;
      fillFromRow({
        id: null,
        name: "",
        licenseNo: "",
        licenseImg: "",
        address: "",
        contactName: "",
        contactPhone: "",
        verifyStatus: "PENDING",
        rejectReason: "",
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

  // 简单校验
  if (!form.name.trim()) return ElMessage.warning("请填写企业名称");
  if (!form.licenseNo.trim()) return ElMessage.warning("请填写营业执照号");

  saving.value = true;
  try {
    const created = await CompanyApi.create({
      merchant: { id: me.value.id },
      name: form.name,
      licenseNo: form.licenseNo,
      licenseImg: form.licenseImg || "",
      address: form.address || "",
      contactName: form.contactName || "",
      contactPhone: form.contactPhone || "",
      verifyStatus: "PENDING",
      rejectReason: null,
    });

    ElMessage.success("企业资料已创建，等待管理员审核");
    fillFromRow(created);
  } catch (e) {
    ElMessage.error(e?.response?.data || e?.message || "创建失败");
  } finally {
    saving.value = false;
  }
}

async function save() {
  if (!me.value?.id) return;

  if (!companyId.value) {
    await createMine();
    return;
  }

  // 校验
  if (!form.name.trim()) return ElMessage.warning("请填写企业名称");
  if (!form.licenseNo.trim()) return ElMessage.warning("请填写营业执照号");

  saving.value = true;
  try {
    await CompanyApi.update(companyId.value, {
      merchant: { id: me.value.id },
      name: form.name,
      licenseNo: form.licenseNo,
      licenseImg: form.licenseImg || "",
      address: form.address || "",
      contactName: form.contactName || "",
      contactPhone: form.contactPhone || "",
      // 审核状态不允许商家修改：提交原值
      verifyStatus: form.verifyStatus,
      rejectReason: form.rejectReason || null,
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
