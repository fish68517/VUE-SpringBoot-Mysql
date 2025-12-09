<template>
  <div class="login-container">
    <div class="page-header">
      <h1 class="page-title">大学生时间管理与自律引导系统</h1>
    </div>
    <div class="login-box">
      <h2 class="title">{{ isLogin ? '欢迎回来' : '加入我们' }}</h2>

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="0px">
        <el-form-item prop="campusEmailAddr">
          <el-input v-model="formData.campusEmailAddr" placeholder="请输入校园邮箱" prefix-icon="User" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>

        <el-form-item v-if="!isLogin" prop="campusUserType">
          <el-select v-model="formData.campusUserType" placeholder="请选择用户类型" style="width: 100%;">
            <el-option label="学生" value="student" v-if="false"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%;" @click="handleSubmit">{{ isLogin ? '登 录' : '注 册' }}</el-button>
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
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import api from '../api/NetWorkApi'; // 确保路径正确

const router = useRouter(); // 获取路由实例，用于跳转
const formRef = ref(null); // 表单引用
const isLogin = ref(true); // 控制显示登录还是注册表单

// 表单数据
const formData = reactive({
  campusEmailAddr: '',
  password: '',
  campusUserType: 'student', // 默认为学生
});

// 表单验证规则
const rules = {
  campusEmailAddr: [
    { required: true, message: '请输入校园邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  campusUserType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ]
};

// 提交按钮点击事件
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isLogin.value) {
          // --- 执行登录逻辑 ---
          console.log('正在登录:', formData);
          const response = await api.campusUserApi.login({ 
            campusEmailAddr: formData.campusEmailAddr, 
            password: formData.password 
          });
          ElMessage.success('登录成功！');
          // 登录成功后，跳转到主页
          // campusUserType: formData.campusUserType
          // 打印 response 数据，确认包含用户类型
          console.log('登录响应:', response);
          console.log('用户类型:', response.data.campusUserType);
          if(response.data.data.campusUserType === 'admin'){
            router.push('/admin'); 
          } else {
            router.push('/'); 
          }
        
        } else {
          // --- 执行注册逻辑 ---
          console.log('正在注册:', formData);
          const response = await api.campusUserApi.register(formData);
          ElMessage.success('注册成功，请登录！');
          // 注册成功后，自动切换到登录界面
          isLogin.value = true;
        }
      } catch (error) {
        console.error('操作失败:', error);
        ElMessage.error(error.response?.data?.message || '操作失败，请检查网络或联系管理员');
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
  /* 在这里设置背景图片 */
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
  background-color: rgba(255, 255, 255, 0.9); /* 半透明背景 */
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
