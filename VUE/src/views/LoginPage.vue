<template>
  <div class="login-container">
    <div class="page-header">
      <h1 class="page-title">智能仓储管理系统</h1>
    </div>
    <div class="login-box">
      <h2 class="title">欢迎回来</h2>

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="0px">
        <el-form-item prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%;" @click="handleSubmit">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import api from '../api/NetWorkApi'; // 确保路径正确

const router = useRouter();
const formRef = ref(null);

// 表单数据
const formData = reactive({
  username: '',
  password: '',
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
};

// 提交按钮点击事件
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        console.log('正在登录:', formData);
        // 使用更新后的 usersApi
        const response = await api.usersApi.login(formData);
        
        // 此处可以处理后端返回的用户信息，例如存储token和用户信息
        // const userInfo = response.data;
        // localStorage.setItem('token', userInfo.token);
        // localStorage.setItem('user', JSON.stringify(userInfo));

        ElMessage.success('登录成功！');
        
        // 假设后端返回的用户信息中有角色信息，根据角色跳转
        // if (userInfo.role === '系统管理员') {
        //   router.push('/admin');
        // } else {
        //   router.push('/');
        // }
        // 暂时简单跳转到主页
        router.push('/admin');

      } catch (error) {
        console.error('登录失败:', error);
        ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码');
      }
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
  /* 更换为科技感、物流感的背景图 */
  background-image: url('../assets/login.png'); /* 您可以替换成更合适的背景图 */
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
</style>