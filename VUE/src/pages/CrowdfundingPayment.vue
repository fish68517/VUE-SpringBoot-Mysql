<template>
  <div class="crowdfunding-payment-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <div class="payment-container">
      <!-- Back Button -->
      <button @click="goBack" class="btn-back">← 返回</button>

      <!-- Payment Header -->
      <div class="payment-header">
        <h1>众筹支持</h1>
        <p class="activity-title">{{ activity?.title }}</p>
      </div>

      <!-- Payment Form -->
      <div class="payment-form">
        <!-- Tier Selection -->
        <div class="form-section">
          <h2>选择支持档位</h2>
          <div class="tier-options">
            <div
              v-for="tier in tiers"
              :key="tier.id"
              class="tier-card"
              :class="{ active: selectedTierId === tier.id }"
              @click="selectTier(tier)"
            >
              <div class="tier-name">{{ tier.name }}</div>
              <div class="tier-amount">¥{{ tier.amount }}</div>
              <div class="tier-description">{{ tier.description }}</div>
            </div>
          </div>
        </div>

        <!-- Custom Amount -->
        <div class="form-section">
          <h2>自定义金额</h2>
          <div class="custom-amount-input">
            <span class="currency-symbol">¥</span>
            <input
              v-model.number="customAmount"
              type="number"
              placeholder="输入自定义金额"
              min="0.01"
              step="0.01"
              @input="handleCustomAmountChange"
            />
          </div>
        </div>

        <!-- Payment Method Selection -->
        <div class="form-section">
          <h2>选择支付方式</h2>
          <div class="payment-methods">
            <label class="payment-method">
              <input
                v-model="selectedPaymentMethod"
                type="radio"
                value="wechat"
              />
              <span class="method-icon">💳</span>
              <span class="method-name">微信支付</span>
            </label>
            <label class="payment-method">
              <input
                v-model="selectedPaymentMethod"
                type="radio"
                value="alipay"
              />
              <span class="method-icon">💳</span>
              <span class="method-name">支付宝</span>
            </label>
            <label class="payment-method">
              <input
                v-model="selectedPaymentMethod"
                type="radio"
                value="card"
              />
              <span class="method-icon">💳</span>
              <span class="method-name">银行卡</span>
            </label>
          </div>
        </div>

        <!-- Payment Summary -->
        <div class="payment-summary">
          <div class="summary-item">
            <span class="label">支持金额：</span>
            <span class="value">¥{{ finalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-item">
            <span class="label">支付方式：</span>
            <span class="value">{{ getPaymentMethodLabel(selectedPaymentMethod) }}</span>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="form-actions">
          <button @click="goBack" class="btn-cancel">取消</button>
          <button
            @click="handlePayment"
            class="btn-pay"
            :disabled="!isFormValid || isProcessing"
          >
            {{ isProcessing ? '处理中...' : '立即支付' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import TopNavigation from '../components/TopNavigation.vue';
import http from '../utils/http';
import { showLoading, hideLoading } from '../utils/loading';
import { showError, showSuccess } from '../utils/errorHandler';

export default {
  name: 'CrowdfundingPayment',
  components: {
    TopNavigation
  },
  setup() {
    const route = useRoute();
    const router = useRouter();

    const activity = ref(null);
    const tiers = ref([]);
    const selectedTierId = ref(null);
    const customAmount = ref(null);
    const selectedPaymentMethod = ref('wechat');
    const isProcessing = ref(false);

    const activityId = computed(() => route.params.activityId);

    const finalAmount = computed(() => {
      if (customAmount.value && customAmount.value > 0) {
        return customAmount.value;
      }
      const selectedTier = tiers.value.find(t => t.id === selectedTierId.value);
      return selectedTier ? parseFloat(selectedTier.amount) : 0;
    });

    const isFormValid = computed(() => {
      return finalAmount.value > 0 && selectedPaymentMethod.value;
    });

    const loadActivityData = async () => {
      try {
        showLoading();
        const response = await http.get(`/api/activities/${activityId.value}`);
        activity.value = response.data.data;
      } catch (error) {
        showError('加载活动信息失败');
        console.error(error);
      } finally {
        hideLoading();
      }
    };

    const loadTiers = async () => {
      try {
        const response = await http.get(`/api/crowdfunding/tiers/${activityId.value}`);
        tiers.value = response.data.data;
        if (tiers.value.length > 0) {
          selectedTierId.value = tiers.value[0].id;
        }
      } catch (error) {
        showError('加载众筹档位失败');
        console.error(error);
      }
    };

    const selectTier = (tier) => {
      selectedTierId.value = tier.id;
      customAmount.value = null;
    };

    const handleCustomAmountChange = () => {
      if (customAmount.value && customAmount.value > 0) {
        selectedTierId.value = null;
      }
    };

    const getPaymentMethodLabel = (method) => {
      const labels = {
        wechat: '微信支付',
        alipay: '支付宝',
        card: '银行卡'
      };
      return labels[method] || '未知';
    };

    const handlePayment = async () => {
      if (!isFormValid.value) {
        showError('请填写完整的支付信息');
        return;
      }

      try {
        isProcessing.value = true;
        showLoading();

        const supportData = {
          activityId: activityId.value,
          amount: finalAmount.value,
          tierId: selectedTierId.value,
          paymentMethod: selectedPaymentMethod.value
        };

        const response = await http.post('/api/crowdfunding/support', supportData);

        if (response.data.code === 200) {
          showSuccess('支付成功！感谢您的支持');
          
          // Redirect to success page after 2 seconds
          setTimeout(() => {
            router.push({
              name: 'PaymentSuccess',
              params: { supportId: response.data.data.id }
            });
          }, 2000);
        }
      } catch (error) {
        showError(error.response?.data?.message || '支付失败，请重试');
        console.error(error);
      } finally {
        isProcessing.value = false;
        hideLoading();
      }
    };

    const goBack = () => {
      router.back();
    };

    onMounted(() => {
      loadActivityData();
      loadTiers();
    });

    return {
      activity,
      tiers,
      selectedTierId,
      customAmount,
      selectedPaymentMethod,
      isProcessing,
      finalAmount,
      isFormValid,
      selectTier,
      handleCustomAmountChange,
      getPaymentMethodLabel,
      handlePayment,
      goBack
    };
  }
};
</script>

<style scoped>
.crowdfunding-payment-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.payment-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  max-width: 600px;
  width: 100%;
  padding: 40px;
}

.btn-back {
  background: none;
  border: none;
  color: #667eea;
  font-size: 16px;
  cursor: pointer;
  margin-bottom: 20px;
  padding: 0;
  transition: color 0.3s;
}

.btn-back:hover {
  color: #764ba2;
}

.payment-header {
  margin-bottom: 30px;
  text-align: center;
}

.payment-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
}

.activity-title {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.form-section {
  margin-bottom: 30px;
}

.form-section h2 {
  font-size: 16px;
  color: #333;
  margin: 0 0 15px 0;
  font-weight: 600;
}

.tier-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.tier-card {
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.tier-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.tier-card.active {
  border-color: #667eea;
  background: #f0f4ff;
}

.tier-name {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.tier-amount {
  font-size: 24px;
  color: #667eea;
  font-weight: bold;
  margin-bottom: 8px;
}

.tier-description {
  font-size: 12px;
  color: #999;
}

.custom-amount-input {
  display: flex;
  align-items: center;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 12px;
  transition: border-color 0.3s;
}

.custom-amount-input:focus-within {
  border-color: #667eea;
}

.currency-symbol {
  font-size: 18px;
  color: #667eea;
  margin-right: 8px;
  font-weight: bold;
}

.custom-amount-input input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  color: #333;
}

.custom-amount-input input::placeholder {
  color: #ccc;
}

.payment-methods {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.payment-method {
  display: flex;
  align-items: center;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-method:hover {
  border-color: #667eea;
  background: #f9f9f9;
}

.payment-method input[type="radio"] {
  margin-right: 10px;
  cursor: pointer;
}

.payment-method input[type="radio"]:checked + .method-icon + .method-name {
  color: #667eea;
  font-weight: 600;
}

.method-icon {
  font-size: 20px;
  margin-right: 8px;
}

.method-name {
  font-size: 14px;
  color: #333;
  transition: color 0.3s;
}

.payment-summary {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-item .label {
  color: #666;
}

.summary-item .value {
  color: #333;
  font-weight: 600;
}

.form-actions {
  display: flex;
  gap: 15px;
}

.btn-cancel,
.btn-pay {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: #f0f0f0;
  color: #333;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-pay {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-pay:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-pay:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .payment-container {
    padding: 20px;
  }

  .tier-options,
  .payment-methods {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
