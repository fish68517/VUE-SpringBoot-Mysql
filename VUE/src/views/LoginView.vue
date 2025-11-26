<template>
  <div class="login-container">
    <div class="login-card">
      <h1>Travel Memory System</h1>
      <h2>Login</h2>
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
  max-width: 500px;
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
  margin-bottom: 30px;
  font-size: 20px;
  font-weight: normal;
}
</style>
