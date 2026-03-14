<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>

        <el-form-item label="积分">
          <el-input :value="form.point" disabled />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" type="textarea" placeholder="请输入地址" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="loading">保存修改</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getUserProfile, updateUserProfile } from "@/api/user";
import { useUserStore } from "@/store/userStore";

const userStore = useUserStore();
const formRef = ref(null);
const loading = ref(false);

const form = ref({
  username: "",
  nickname: "",
  email: "",
  phone: "",
  address: "",
  avatar: "",
  point: 0
});

const originalForm = ref({});

const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}));

const validateEmail = (rule, value, callback) => {
  if (!value) return callback();
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(value)) callback(new Error("邮箱格式不正确"));
  else callback();
};

const validatePhone = (rule, value, callback) => {
  if (!value) return callback();
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!phoneRegex.test(value)) callback(new Error("手机号格式不正确"));
  else callback();
};

const rules = {
  nickname: [{ max: 50, message: "昵称长度不能超过50个字符", trigger: "blur" }],
  email: [{ validator: validateEmail, trigger: "blur" }],
  phone: [{ validator: validatePhone, trigger: "blur" }],
  address: [{ max: 255, message: "地址长度不能超过255个字符", trigger: "blur" }]
};

const fillForm = (userInfo = {}) => {
  form.value = {
    username: userInfo.username || "",
    nickname: userInfo.nickname || "",
    email: userInfo.email || "",
    phone: userInfo.phone || "",
    address: userInfo.address || "",
    avatar: userInfo.avatar || "",
    point: Number(userInfo.point || 0)
  };
  originalForm.value = JSON.parse(JSON.stringify(form.value));
};

const refreshProfile = async () => {
  const userId = userStore.userInfo?.id;
  if (!userId) return;
  const latest = await getUserProfile(userId);
  userStore.setUserInfo({
    ...userStore.userInfo,
    ...latest
  });
  fillForm({
    ...userStore.userInfo,
    ...latest
  });
};

onMounted(async () => {
  fillForm(userStore.userInfo || {});
  try {
    await refreshProfile();
  } catch (error) {
    console.error("加载用户信息失败:", error);
    ElMessage.error("获取最新个人信息失败");
  }
});

function handleUpdate() {
  formRef.value.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      const userId = userStore.userInfo?.id;
      if (!userId) {
        ElMessage.error("用户信息异常，请重新登录");
        return;
      }
      const { username, point, ...data } = form.value;
      await updateUserProfile(userId, data);
      await refreshProfile();
      ElMessage.success("个人信息更新成功");
    } catch (error) {
      ElMessage.error(error.response?.data?.message || "更新失败");
    } finally {
      loading.value = false;
    }
  });
}

function handleReset() {
  form.value = JSON.parse(JSON.stringify(originalForm.value));
}

function handleAvatarSuccess(response) {
  const newAvatarUrl = response.data;
  form.value.avatar = newAvatarUrl;
  userStore.setUserInfo({ ...userStore.userInfo, avatar: newAvatarUrl });
  ElMessage.success("头像上传成功");
}

function handleAvatarError() {
  ElMessage.error("头像上传失败");
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
