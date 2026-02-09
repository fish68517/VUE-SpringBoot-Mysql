<template>
  <div class="login-container">
    <div class="login-box">
      <h1>登录</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            required
            @blur="validateUsername"
          >
          <span v-if="errors.username" class="error">{{ errors.username }}</span>
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            required
            @blur="validatePassword"
          >
          <span v-if="errors.password" class="error">{{ errors.password }}</span>
        </div>

        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? '正在登录...' : '登录' }}
        </button>

        <div v-if="message.error" class="message error-message">
          {{ message.error }}
        </div>
        <div v-if="message.success" class="message success-message">
          {{ message.success }}
        </div>
      </form>

      <div class="register-link">
        <p>还没有账号？<router-link to="/register">点这里注册</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
import authService from '../services/authService'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      errors: {
        username: '',
        password: ''
      },
      message: {
        error: '',
        success: ''
      },
      loading: false
    }
  },
  methods: {
    validateUsername() {
      if (!this.form.username) {
        this.errors.username = '请输入用户名'
      } else if (this.form.username.length < 3) {
        this.errors.username = '用户名长度至少为 3 个字符'
      } else {
        this.errors.username = ''
      }
    },
    validatePassword() {
      if (!this.form.password) {
        this.errors.password = '请输入密码'
      } else if (this.form.password.length < 6) {
        this.errors.password = '密码长度至少为 6 个字符'
      } else {
        this.errors.password = ''
      }
    },
    async handleLogin() {
      this.validateUsername()
      this.validatePassword()

      if (this.errors.username || this.errors.password) {
        return
      }

      this.loading = true
      this.message.error = ''
      this.message.success = ''

      try {
        const response = await authService.login(this.form.username, this.form.password)

        // --- 修改开始 ---
        // 这里的 response.data 包含了 code, message, data
        const responseBody = response.data

        // 真正的用户数据在 response.data.data 里面
        const { token, user } = responseBody.data

        // 检查 user 对象是否存在，以防万一
        if (token && user) {
          authService.setToken(token)
          // 注意：后端返回的 user 对象里包含了 id, username, role
          authService.setUser(user)

          this.message.success = '登录成功！正在跳转...'
          setTimeout(() => {
            // 根据角色跳转到不同的布局
            if (user.role === 'ADMIN') {
              this.$router.push('/admin/users') // 管理员跳到新的后台
            } else {
              this.$router.push('/app/dashboard') // 普通用户跳到前台
            }
          }, 1000)
        } else {
          throw new Error('响应格式不正确')
        }
        // --- 修改结束 ---

      } catch (error) {
        console.error(error) // 方便调试
        this.message.error = error.response?.data?.message || '登录失败，请稍后重试。'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>




<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.error {
  display: block;
  color: #e74c3c;
  font-size: 12px;
  margin-top: 5px;
}

.btn-submit {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 10px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.message {
  margin-top: 15px;
  padding: 12px;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
}

.error-message {
  background-color: #fadbd8;
  color: #c0392b;
  border: 1px solid #e74c3c;
}

.success-message {
  background-color: #d5f4e6;
  color: #27ae60;
  border: 1px solid #2ecc71;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.register-link p {
  color: #666;
  font-size: 14px;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.register-link a:hover {
  color: #764ba2;
}
</style>
