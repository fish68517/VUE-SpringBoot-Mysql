<template>
  <div class="register-container">
    <div class="register-box">
      <h1>Register</h1>
      <form @submit.prevent="handleRegister">
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
          <div class="password-strength">
            <div class="strength-bar">
              <div
                class="strength-fill"
                :class="passwordStrength.class"
                :style="{ width: passwordStrength.percentage + '%' }"
              ></div>
            </div>
            <span class="strength-text">{{ passwordStrength.text }}</span>
          </div>
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirm Password</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            placeholder="Confirm your password"
            required
            @blur="validateConfirmPassword"
          >
          <span v-if="errors.confirmPassword" class="error">{{ errors.confirmPassword }}</span>
        </div>

        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? 'Registering...' : 'Register' }}
        </button>

        <div v-if="message.error" class="message error-message">
          {{ message.error }}
        </div>
        <div v-if="message.success" class="message success-message">
          {{ message.success }}
        </div>
      </form>

      <div class="login-link">
        <p>Already have an account? <router-link to="/login">Login here</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
import authService from '../services/authService'

export default {
  name: 'Register',
  data() {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: ''
      },
      errors: {
        username: '',
        password: '',
        confirmPassword: ''
      },
      message: {
        error: '',
        success: ''
      },
      loading: false
    }
  },
  computed: {
    passwordStrength() {
      const password = this.form.password
      let strength = 0
      let text = 'Weak'
      let className = 'weak'

      if (password.length >= 8) strength += 25
      if (password.length >= 12) strength += 25
      if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength += 25
      if (/\d/.test(password)) strength += 12.5
      if (/[!@#$%^&*]/.test(password)) strength += 12.5

      if (strength >= 75) {
        text = 'Strong'
        className = 'strong'
      } else if (strength >= 50) {
        text = 'Medium'
        className = 'medium'
      }

      return {
        percentage: Math.min(strength, 100),
        text,
        class: className
      }
    }
  },
  methods: {
    validateUsername() {
      if (!this.form.username) {
        this.errors.username = 'Username is required'
      } else if (this.form.username.length < 3) {
        this.errors.username = 'Username must be at least 3 characters'
      } else if (this.form.username.length > 20) {
        this.errors.username = 'Username must not exceed 20 characters'
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
    validateConfirmPassword() {
      if (!this.form.confirmPassword) {
        this.errors.confirmPassword = 'Please confirm your password'
      } else if (this.form.password !== this.form.confirmPassword) {
        this.errors.confirmPassword = 'Passwords do not match'
      } else {
        this.errors.confirmPassword = ''
      }
    },
    async handleRegister() {
      this.validateUsername()
      this.validatePassword()
      this.validateConfirmPassword()

      if (this.errors.username || this.errors.password || this.errors.confirmPassword) {
        return
      }

      this.loading = true
      this.message.error = ''
      this.message.success = ''

      try {
        const response = await authService.register(this.form.username, this.form.password)
        const { token, id, username, role } = response.data

        authService.setToken(token)
        authService.setUser({ id, username, role })

        this.message.success = 'Registration successful! Redirecting...'
        setTimeout(() => {
          this.$router.push('/app/dashboard')
        }, 1000)
      } catch (error) {
        this.message.error = error.response?.data?.message || 'Registration failed. Please try again.'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
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

.password-strength {
  margin-top: 8px;
}

.strength-bar {
  height: 4px;
  background-color: #eee;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 5px;
}

.strength-fill {
  height: 100%;
  transition: width 0.3s, background-color 0.3s;
}

.strength-fill.weak {
  background-color: #e74c3c;
}

.strength-fill.medium {
  background-color: #f39c12;
}

.strength-fill.strong {
  background-color: #27ae60;
}

.strength-text {
  font-size: 12px;
  color: #666;
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

.login-link {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.login-link p {
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.login-link a:hover {
  color: #764ba2;
}
</style>
