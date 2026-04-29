<template>
  <div class="not-found">
    <h1>404</h1>
    <p>页面未找到</p>
    <router-link to="/login">返回首页</router-link>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  text-align: center;
}

.not-found h1 {
  font-size: 72px;
  margin: 0;
  color: #409eff;
}

.not-found p {
  font-size: 24px;
  color: #666;
  margin: 20px 0;
}

.not-found a {
  color: #409eff;
  text-decoration: none;
  font-size: 16px;
  padding: 10px 20px;
  border: 1px solid #409eff;
  border-radius: 4px;
  transition: all 0.3s;
}

.not-found a:hover {
  background-color: #409eff;
  color: white;
}
</style>
