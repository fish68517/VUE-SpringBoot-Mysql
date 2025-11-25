你的代码中 .register-container 已经使用了 min-height: 100vh，这通常是让页面全屏的关键属性。

但是，为了确保它能够彻底铺满且没有白色边距或滚动条（通常由浏览器默认的 body margin 引起），我建议对 CSS 进行微调，并提供两种方案。

方案一：标准全屏写法（推荐）

这是最稳健的写法。注意：如果页面四周有白边，请务必在你的全局样式文件（如 App.vue 或 main.css）中设置 body { margin: 0; }。

code
Html
play_circle
download
content_copy
expand_less
<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>小鲨鱼运动健身管理系统</h2>
          <p>用户注册</p>
        </div>
      </template>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
        @submit.prevent="handleRegister"
      >
        <!-- 表单内容保持不变 -->
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（3-20个字符）"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6个字符）"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item label="用户角色" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio label="user">普通用户</el-radio>
            <el-radio label="coach">教练</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="login-link">
            已有账号？
            <router-link to="/login">立即登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
// 注意：如果你没有实际用到这些导入的校验函数，可以删除下面这一行
import { showSuccess, showError } from '@/utils/feedback'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  role: 'user'
})

// 校验规则保持不变
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度应为3-50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度应为6-100个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  role: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate().catch(() => false)
    if (!valid) {
      // 如果使用了 Element Plus，不需要这里手动报错，表单会自动标红
      // showError('请完善注册信息') 
      return
    }

    loading.value = true

    const { confirmPassword, ...registerData } = registerForm
    await register(registerData)

    showSuccess('注册成功！正在跳转登录页...')
    
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error) {
    console.error('Register error:', error)
    // 根据你的 axios 封装，如果这里没有全局处理错误，可以解开下面的注释
    // if (error.message) showError(error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 核心修改区域 */
.register-container {
  position: fixed; /* 强制固定在窗口，无视父级元素 */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 100; /* 确保在最上层 */
  overflow-y: auto; /* 允许内容过多时滚动 */
}

.register-card {
  width: 500px;
  max-width: 90%;
  border-radius: 10px; /* 圆角美化 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); /* 阴影增强立体感 */
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-link {
  width: 100%;
  text-align: center;
  color: #606266;
  font-size: 14px;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
