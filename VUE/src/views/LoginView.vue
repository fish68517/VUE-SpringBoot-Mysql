<template>
  <div class="login-container">
    <div class="login-card">
      <h1>旅行记忆</h1>
      <h2>用户登录</h2>
      <p class="login-tip">普通用户登录后进入个人空间，管理员登录后进入管理后台。</p>
      <UserLogin />
    </div>
  </div>
</template>

<script setup>
import UserLogin from '../components/UserLogin.vue'
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: white;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 520px;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

h2 {
  text-align: center;
  color: #666;
  margin-bottom: 10px;
  font-size: 20px;
  font-weight: normal;
}

.login-tip {
  margin: 0 0 10px;
  text-align: center;
  color: #909399;
  font-size: 14px;
}
</style>
