<template>
  <div class="register-container">
    <div class="register-wrapper">
      <UserRegister />
    </div>
  </div>
</template>

<script setup>
import UserRegister from '../components/UserRegister.vue'
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-wrapper {
  width: 100%;
  max-width: 500px;
  padding: 20px;
}
</style>
