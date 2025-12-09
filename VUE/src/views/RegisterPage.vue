<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="title">智能仓储管理系统后台登录</h2>
     

      <el-form 
        ref="formRef" 
        :model="formData" 
        :rules="rules" 
        label-width="80px"
        label-position="top"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="Lock" 
            show-password 
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="formData.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            prefix-icon="Lock" 
            show-password 
          />
        </el-form-item>

        <el-form-item label="全名" prop="fullName">
          <el-input v-model="formData.fullName" placeholder="请输入您的真实姓名" prefix-icon="Postcard" />
        </el-form-item>

        <el-form-item label="所属部门" prop="departmentId" v-if="false">
          <el-select v-model="formData.departmentId" placeholder="请选择部门" style="width: 100%;">
            <el-option 
              v-for="dept in departments" 
              :key="dept.id" 
              :label="dept.name" 
              :value="dept.id" 
            />
          </el-select>
        </el-form-item>

        <!-- 角色选择通常不由用户自己决定，或者默认为操作员，此处演示可选 -->
        <!-- 
        <el-form-item label="申请角色" prop="roleId">
           <el-select v-model="formData.roleId" ...> ... </el-select>
        </el-form-item> 
        -->

        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%;" @click="handleRegister">
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-link">
        <span>已有账号？</span>
        <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { User, Lock, Postcard } from '@element-plus/icons-vue';
import api from '../api/NetWorkApi';

const router = useRouter();
const formRef = ref(null);
const loading = ref(false);
const departments = ref([]);

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  fullName: '',
  departmentId: null,
  roleId: 3, // 默认注册为 "操作员" (根据 db.sql 假设 ID 3 是操作员)
  isActive: true // 默认激活，或者 false 等待管理员审核
});

// 验证两次密码是否一致
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== formData.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

// 验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入全名', trigger: 'blur' }
  ]
//   departmentId: [
//     { required: true, message: '请选择所属部门', trigger: 'change' }
//   ]
};

// 获取部门列表供选择
onMounted(async () => {
  try {
    const res = await api.departmentsApi.list();
    departments.value = res.data || [];
  } catch (error) {
    console.error("加载部门失败", error);
    // 可以在这里提供一些默认选项或错误提示
  }
});

const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // 构造发送给后端的数据，移除 confirmPassword
        const { confirmPassword, ...registerData } = formData;
        
        // 调用注册接口
        await api.usersApi.register({
            username: registerData.username,
            passwordHash: registerData.password, // 注意：后端实体类字段可能是 passwordHash
            fullName: registerData.fullName,
            departmentId: registerData.departmentId,
            roleId: 1,
            isActive: registerData.isActive
        });

        ElMessage.success('注册成功！请登录');
        router.push('/login');
      } catch (error) {
        console.error('注册失败:', error);
        ElMessage.error(error.response?.data?.message || '注册失败，请稍后重试');
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url('../assets/login.png'); /* 复用登录背景 */
  background-size: cover;
  background-position: center;
  position: relative;
}

.register-container::before {
  content: "";
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.4); /* 背景遮罩 */
}

.register-box {
  width: 420px;
  padding: 40px;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.title {
  text-align: center;
  margin: 0 0 10px 0;
  color: #303133;
}

.subtitle {
  text-align: center;
  margin: 0 0 30px 0;
  color: #909399;
  font-size: 14px;
}

.login-link {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.login-link .el-link {
  margin-left: 5px;
  vertical-align: baseline;
}
</style>