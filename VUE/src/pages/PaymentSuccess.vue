<template>
  <div class="payment-success-page">
    <div class="success-container">
      <!-- Success Icon -->
      <div class="success-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
      </div>

      <!-- Success Message -->
      <h1 class="success-title">支付成功！</h1>
      <p class="success-message">感谢您的支持，您的众筹支持已成功记录</p>

      <!-- Support Details -->
      <div class="support-details" v-if="supportData">
        <div class="detail-item">
          <span class="label">支持金额：</span>
          <span class="value">¥{{ supportData.amount }}</span>
        </div>
        <div class="detail-item">
          <span class="label">支持时间：</span>
          <span class="value">{{ formatDateTime(supportData.createdAt) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">支付状态：</span>
          <span class="value status-completed">已完成</span>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="action-buttons">
        <button @click="goToActivityDetail" class="btn-primary">
          返回活动详情
        </button>
        <button @click="goToHome" class="btn-secondary">
          返回首页
        </button>
      </div>

      <!-- Redirect Timer -->
      <p class="redirect-timer">
        将在 {{ redirectCountdown }} 秒后自动返回活动详情
      </p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import http from '../utils/http';

export default {
  name: 'PaymentSuccess',
  setup() {
    const route = useRoute();
    const router = useRouter();

    const supportData = ref(null);
    const redirectCountdown = ref(5);
    const supportId = computed(() => route.params.supportId);

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '';
      const date = new Date(dateTime);
      return date.toLocaleString('zh-CN');
    };

    const loadSupportData = async () => {
      try {
        // In a real scenario, you would fetch the support data from the API
        // For now, we'll use the data passed from the payment page
        if (route.query.amount) {
          supportData.value = {
            amount: route.query.amount,
            createdAt: new Date().toISOString()
          };
        }
      } catch (error) {
        console.error('Failed to load support data:', error);
      }
    };

    const goToActivityDetail = () => {
      if (route.query.activityId) {
        router.push({
          name: 'ActivityDetail',
          params: { id: route.query.activityId }
        });
      } else {
        router.push({ name: 'Home' });
      }
    };

    const goToHome = () => {
      router.push({ name: 'Home' });
    };

    onMounted(() => {
      loadSupportData();

      // Auto redirect after 5 seconds
      const interval = setInterval(() => {
        redirectCountdown.value--;
        if (redirectCountdown.value <= 0) {
          clearInterval(interval);
          goToActivityDetail();
        }
      }, 1000);
    });

    return {
      supportData,
      redirectCountdown,
      formatDateTime,
      goToActivityDetail,
      goToHome
    };
  }
};
</script>

<style scoped>
.payment-success-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.success-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
  padding: 40px;
  text-align: center;
}

.success-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  animation: scaleIn 0.5s ease-out;
}

.success-icon svg {
  width: 50px;
  height: 50px;
  color: white;
  stroke-linecap: round;
  stroke-linejoin: round;
}

@keyframes scaleIn {
  from {
    transform: scale(0);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.success-title {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
}

.success-message {
  font-size: 16px;
  color: #666;
  margin: 0 0 30px 0;
}

.support-details {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  text-align: left;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item .label {
  color: #666;
}

.detail-item .value {
  color: #333;
  font-weight: 600;
}

.status-completed {
  color: #4caf50;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.btn-primary,
.btn-secondary {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #f0f0f0;
  color: #333;
}

.btn-secondary:hover {
  background: #e0e0e0;
}

.redirect-timer {
  font-size: 14px;
  color: #999;
  margin: 0;
}

@media (max-width: 600px) {
  .success-container {
    padding: 20px;
  }

  .success-title {
    font-size: 24px;
  }

  .success-message {
    font-size: 14px;
  }
}
</style>
