<template>
  <div class="login-container">
    <div class="page-header">
      <h1 class="page-title">大学生时间管理与自律引导系统</h1>
    </div>

    <div class="login-box">
      <h2 class="title">{{ isLogin ? '欢迎回来' : '加入我们' }}</h2>

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="0px">
        <el-form-item prop="campusEmailAddr">
          <el-input
            v-model="formData.campusEmailAddr"
            placeholder="请输入校园邮箱"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item v-if="!isLogin" prop="campusUserType">
          <el-select
            v-model="formData.campusUserType"
            placeholder="请选择用户类型"
            style="width: 100%;"
          >
            <el-option v-if="false" label="学生" value="student" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%;" @click="handleSubmit">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="switch-link">
        <el-link type="info" @click="isLogin = !isLogin">
          {{ isLogin ? '还没有账号？去注册' : '已有账号？去登录' }}
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import api from '../api/NetWorkApi';
import { setStoredUser } from '../utils/auth';

const router = useRouter();
const formRef = ref(null);
const isLogin = ref(true);

const formData = reactive({
  campusEmailAddr: '',
  password: '',
  campusUserType: 'student',
});

const rules = {
  campusEmailAddr: [
    { required: true, message: '请输入校园邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  campusUserType: [
    { required: true, message: '请选择用户类型', trigger: 'change' },
  ],
};

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }

    try {
      if (isLogin.value) {
        const response = await api.campusUserApi.login({
          campusEmailAddr: formData.campusEmailAddr,
          password: formData.password,
        });

        const loginUser = response.data?.data || response.data || {};
        setStoredUser(loginUser);
        ElMessage.success('登录成功');

        if (loginUser.campusUserType === 'admin') {
          router.replace('/admin');
        } else {
          router.replace('/dashboard');
        }
        return;
      }

      await api.campusUserApi.register(formData);
      ElMessage.success('注册成功，请登录');
      isLogin.value = true;
    } catch (error) {
      console.error('操作失败:', error);
      ElMessage.error(error.response?.data?.message || '操作失败，请检查网络或联系管理员');
    }
  });
};
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('../assets/login.png');
  background-size: cover;
  background-position: center;
  position: relative;
}

.page-header {
  position: absolute;
  top: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.page-title {
  color: white;
  font-size: 32px;
  font-weight: bold;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  margin: 0;
}

.login-box {
  width: 400px;
  padding: 40px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 30px;
  color: #333;
}

.switch-link {
  margin-top: 10px;
  text-align: right;
}
</style>
