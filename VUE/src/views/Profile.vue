<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 基本信息标签 -->
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
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
        </el-tab-pane>

        <!-- 头像标签 -->
        <el-tab-pane label="头像" name="avatar">
          <div class="avatar-section">
            <div class="avatar-preview">
              <img v-if="form.avatar" :src="form.avatar" alt="头像" class="avatar-img" />
              <div v-else class="avatar-placeholder">
                <span>暂无头像</span>
              </div>
            </div>

            <div class="avatar-upload">
              <el-upload
                action="http://localhost:8080/api/user/avatar"
                :headers="uploadHeaders"
                :on-success="handleAvatarSuccess"
                :on-error="handleAvatarError"
                :show-file-list="false"
              >
                <el-button type="primary">上传头像</el-button>
              </el-upload>
              <p class="upload-tip">支持 JPG、PNG 格式，大小不超过 10MB</p>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { getUserProfile, updateUserProfile } from "@/api/user";
import { useUserStore } from "@/store/userStore";

const userStore = useUserStore();
const formRef = ref(null);
const loading = ref(false);
const activeTab = ref("info");

const form = ref({
  username: "",
  nickname: "",
  email: "",
  phone: "",
  address: "",
  avatar: ""
});

const originalForm = ref({});

const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}));

const validateEmail = (rule, value, callback) => {
  if (value === "") {
    callback();
  } else {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(value)) {
      callback(new Error("邮箱格式不正确"));
    } else {
      callback();
    }
  }
};

const validatePhone = (rule, value, callback) => {
  if (value === "") {
    callback();
  } else {
    const phoneRegex = /^1[3-9]\d{9}$/;
    if (!phoneRegex.test(value)) {
      callback(new Error("手机号格式不正确"));
    } else {
      callback();
    }
  }
};

const rules = {
  nickname: [{ max: 50, message: "昵称长度不能超过50个字符", trigger: "blur" }],
  email: [{ validator: validateEmail, trigger: "blur" }],
  phone: [{ validator: validatePhone, trigger: "blur" }],
  address: [{ max: 255, message: "地址长度不能超过255个字符", trigger: "blur" }]
};

onMounted(async () => {
  await loadUserProfile();
});

async function loadUserProfile() {
  try {
    const res = await getUserProfile();
    form.value = {
      username: res.username || "",
      nickname: res.nickname || "",
      email: res.email || "",
      phone: res.phone || "",
      address: res.address || "",
      avatar: res.avatar || ""
    };
    originalForm.value = JSON.parse(JSON.stringify(form.value));
  } catch (error) {
    ElMessage.error("加载用户信息失败");
  }
}

function handleUpdate() {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const { username, ...data } = form.value;
        await updateUserProfile(data);
        userStore.setUserInfo(form.value);
        originalForm.value = JSON.parse(JSON.stringify(form.value));
        ElMessage.success("个人信息更新成功");
      } catch (error) {
        ElMessage.error(error.response?.data?.message || "更新失败");
      } finally {
        loading.value = false;
      }
    }
  });
}

function handleReset() {
  form.value = JSON.parse(JSON.stringify(originalForm.value));
}

function handleAvatarSuccess(response) {
  form.value.avatar = response.data;
  userStore.setUserInfo(form.value);
  ElMessage.success("头像上传成功");
}

function handleAvatarError(error) {
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

.avatar-section {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.avatar-preview {
  flex-shrink: 0;
}

.avatar-img {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  object-fit: cover;
}

.avatar-placeholder {
  width: 150px;
  height: 150px;
  background-color: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.avatar-upload {
  flex: 1;
}

.upload-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
