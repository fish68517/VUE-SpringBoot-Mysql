<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <div class="avatar-section">
        <div class="avatar-preview">
          <img v-if="avatarUrl" :src="avatarUrl" alt="头像" class="avatar-img" />
          <div v-else class="avatar-placeholder">暂无头像</div>
        </div>
        <div class="avatar-actions">
          <el-upload
            :show-file-list="false"
            :http-request="handleAvatarUpload"
            accept="image/*"
          >
            <el-button type="primary" :loading="uploadingAvatar">上传头像</el-button>
          </el-upload>
          <p class="avatar-tip">图片将上传到 SpringBoot/uploads/user 目录</p>
        </div>
      </div>

      <el-divider />

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
import { getUserProfile, updateUserProfile, uploadUserAvatar } from "@/api/user";
import { useUserStore } from "@/store/userStore";

const userStore = useUserStore();
const formRef = ref(null);
const loading = ref(false);
const uploadingAvatar = ref(false);

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

const avatarUrl = computed(() => {
  const avatar = form.value.avatar;
  if (!avatar) return "";
  if (avatar.startsWith("http")) return avatar;
  return `http://localhost:8080/uploads/${avatar}`;
});

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
  userStore.setUserInfo({ ...userStore.userInfo, ...latest });
  fillForm({ ...userStore.userInfo, ...latest });
};

const handleAvatarUpload = async (options) => {
  try {
    const userId = userStore.userInfo?.id;
    if (!userId) {
      ElMessage.error("用户信息异常，请重新登录");
      return;
    }

    const file = options.file;
    if (!file?.type?.startsWith("image/")) {
      ElMessage.warning("请选择图片文件");
      return;
    }

    uploadingAvatar.value = true;
    const avatarPath = await uploadUserAvatar(userId, file);
    form.value.avatar = avatarPath;
    userStore.setUserInfo({ ...userStore.userInfo, avatar: avatarPath });
    await refreshProfile();

    options.onSuccess?.(avatarPath);
    ElMessage.success("头像上传成功");
  } catch (error) {
    console.error(error);
    options.onError?.(error);
    ElMessage.error(error.message || "头像上传失败");
  } finally {
    uploadingAvatar.value = false;
  }
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
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 860px;
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

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-preview {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  overflow: hidden;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 12px;
  color: #9ca3af;
}

.avatar-tip {
  margin: 8px 0 0;
  font-size: 12px;
  color: #6b7280;
}
</style>
