<template>
  <div class="pay-container">
    <div class="pay-header">
      <h3>订单支付</h3>
      <div class="order-info">
        <p>订单号: {{ orderNo }}</p>
        <p>订单标题: {{ subject }}</p>
        <p class="amount">支付金额: <span>¥{{ totalAmount }}</span></p>
      </div>
    </div>

    <div class="payment-methods">
      <div class="method active">
        <img src="https://ts1.tc.mm.bing.net/th/id/R-C.d92375244b2aa72cb0a908ced5f3b82f?rik=Vp1gcK%2fgp9RwKw&riu=http%3a%2f%2fwww.kuaipng.com%2fUploads%2fpic%2fw%2f2020%2f03-29%2f76002%2fwater_76002_698_698_.png&ehk=PC1tAFEArlnKJy20uawSNpn175%2bYYM8ywnhX8ej%2bpJ0%3d&risl=&pid=ImgRaw&r=0" alt="支付宝">
        <span>支付宝支付</span>
      </div>

      <button class="pay-btn" @click="directAlipay">立即支付</button>
    </div>

    <!-- 支付表单容器 -->

    <div class="status-info" v-if="paymentStatus">
      <p v-if="paymentStatus === 'SUCCESS'" class="success">支付成功! 正在跳转...</p>
      <p v-if="paymentStatus === 'PROCESSING'" class="processing">支付处理中...</p>
      <p v-if="paymentStatus === 'FAILED'" class="failed">支付失败</p>
      <p v-if="paymentStatus === 'TIMEOUT'" class="failed">支付超时，请重新支付</p>
    </div>
  </div>
</template>

<script>
import ordersApi from '@/api/orders'

export default {
  data() {
    return {
      orderNo: '',
      subject: '',
      totalAmount: 0,
      alipayForm: '',
      paymentStatus: '',
      timer: null,
      pollingCount: 0 // 轮询计数器
    }
  },

  mounted() {
    // 从路由参数获取订单信息
    this.orderNo = this.$route.query.orderNo || ''
    this.subject = this.$route.query.subject || '在线教育课程'
    this.totalAmount = parseFloat(this.$route.query.totalAmount) || 0
  },

  beforeDestroy() {
    // 清除定时器
    if (this.timer) {
      clearInterval(this.timer)
    }
  },

  methods: {
    // 初始化支付宝支付
    directAlipay() {
      const payUrl = ` https://4e67cc999ce7.ngrok-free.app/edeorder/order/pay?subject=${encodeURIComponent(this.subject)}&traceNo=${this.orderNo}&totalAmount=${this.totalAmount}`
      window.location.href = payUrl
    },
  }
}
</script>

<style scoped>
.pay-container {
  max-width: 600px;
  margin: 30px auto;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.pay-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.pay-header h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.order-info {
  margin-top: 15px;
  font-size: 14px;
  background: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
}

.order-info p {
  margin: 8px 0;
  line-height: 1.6;
}

.amount {
  font-weight: bold;
  font-size: 18px;
  color: #333;
  margin-top: 10px;
}

.amount span {
  color: #f56c6c;
  font-size: 20px;
}

.payment-methods {
  margin-top: 25px;
}

.method {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.method.active {
  border-color: #1677ff;
  background-color: #f0f7ff;
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

.method img {
  width: 40px;
  height: 40px;
  margin-right: 15px;
  object-fit: contain;
}

.method span {
  font-size: 16px;
  font-weight: 500;
}

.pay-btn {
  display: block;
  width: 100%;
  padding: 14px;
  background-color: #1677ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
  box-shadow: 0 2px 6px rgba(22, 119, 255, 0.3);
}

.pay-btn:hover {
  background-color: #0056b3;
}

.pay-btn:active {
  transform: translateY(1px);
}

.status-info {
  margin-top: 25px;
  text-align: center;
  padding: 15px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
}

.success {
  color: #52c41a;
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
}

.processing {
  color: #faad14;
  background-color: #fffbe6;
  border: 1px solid #ffe58f;
}

.failed,
.timeout {
  color: #ff4d4f;
  background-color: #fff2f0;
  border: 1px solid #ffccc7;
}

.alipay-form-container {
  display: none;
}
</style>
