<template>
  <div class="login-container">
    <div class="login-box">
      <h1>Login</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Username</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            placeholder="Enter your username"
            required
            @blur="validateUsername"
          >
          <span v-if="errors.username" class="error">{{ errors.username }}</span>
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="Enter your password"
            required
            @blur="validatePassword"
          >
          <span v-if="errors.password" class="error">{{ errors.password }}</span>
        </div>

        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>

        <div v-if="message.error" class="message error-message">
          {{ message.error }}
        </div>
        <div v-if="message.success" class="message success-message">
          {{ message.success }}
        </div>
      </form>

      <div class="register-link">
        <p>Don't have an account? <router-link to="/register">Register here</router-link></p>
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
        this.errors.username = 'Username is required'
      } else if (this.form.username.length < 3) {
        this.errors.username = 'Username must be at least 3 characters'
      } else {
        this.errors.username = ''
      }
    },
    validatePassword() {
      if (!this.form.password) {
        this.errors.password = 'Password is required'
      } else if (this.form.password.length < 6) {
        this.errors.password = 'Password must be at least 6 characters'
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
        const { token, id, username, role } = response.data

        authService.setToken(token)
        authService.setUser({ id, username, role })

        this.message.success = 'Login successful! Redirecting...'
        setTimeout(() => {
          this.$router.push('/app/dashboard')
        }, 1000)
      } catch (error) {
        this.message.error = error.response?.data?.message || 'Login failed. Please try again.'
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
