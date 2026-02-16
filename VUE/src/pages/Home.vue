<template>
  <div class="home-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <div class="home-container">
      <h1>欢迎来到校园活动众筹与报名平台</h1>
      <p>发现、参与、支持校园活动</p>
      <button @click="goToActivityList" class="btn-explore">浏览活动</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import TopNavigation from '../components/TopNavigation.vue'

const router = useRouter()

const goToActivityList = () => {
  router.push('/activities')
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  display: flex;
  flex-direction: column;
}

.home-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.home-container {
  text-align: center;
  color: white;
}

h1 {
  font-size: 48px;
  margin-bottom: 20px;
  font-weight: 700;
}

p {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.btn-explore {
  padding: 12px 40px;
  background-color: white;
  color: #1890ff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-explore:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  h1 {
    font-size: 32px;
  }

  p {
    font-size: 16px;
  }
}
</style>
