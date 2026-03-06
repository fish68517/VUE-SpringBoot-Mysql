<template>
  <div class="order-confirm-page">
    <div class="container">
      <h1 class="page-title">订单确认</h1>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="selectedItems.length === 0" class="empty-state">
        <p>没有选中的商品</p>
        <router-link to="/cart" class="btn-back">返回购物车</router-link>
      </div>
      <div v-else class="confirm-content">
        <!-- 商品信息 -->
        <div class="section">
          <h2 class="section-title">订单商品</h2>
          <div class="products-list">
            <div v-for="item in selectedItems" :key="item.id" class="product-item">
              <img :src="item.productImage" :alt="item.productName" class="product-image" />
              <div class="product-info">
                <p class="product-name">{{ item.productName }}</p>
                <p class="product-price">¥{{ item.productPrice }}</p>
              </div>
              <div class="product-quantity">
                <span>x{{ item.quantity }}</span>
              </div>
              <div class="product-subtotal">
                <span class="subtotal">¥{{ item.subtotal }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="section">
          <h2 class="section-title">收货地址</h2>
          <div class="address-form">
            <div class="form-group">
              <label>收货人</label>
              <input v-model="form.receiverName" type="text" placeholder="请输入收货人姓名" />
            </div>
            <div class="form-group">
              <label>电话号码</label>
              <input v-model="form.receiverPhone" type="tel" placeholder="请输入电话号码" />
            </div>
            <div class="form-group">
              <label>收货地址</label>
              <input v-model="form.receiverAddress" type="text" placeholder="请输入收货地址" />
            </div>
            <div class="form-group">
              <label>备注（可选）</label>
              <textarea v-model="form.remark" placeholder="请输入订单备注" rows="3"></textarea>
            </div>
          </div>
        </div>

        <!-- 优惠券选择 -->
        <div class="section">
          <h2 class="section-title">优惠券</h2>
          <div class="coupon-section">
            <div v-if="availableCoupons.length === 0" class="no-coupon">
              <p>暂无可用优惠券</p>
            </div>
            <div v-else class="coupon-list">
              <div
                v-for="coupon in availableCoupons"
                :key="coupon.id"
                class="coupon-item"
                :class="{ selected: selectedCoupon?.id === coupon.id }"
                @click="selectCoupon(coupon)"
              >
                <div class="coupon-info">
                  <p class="coupon-name">{{ coupon.name }}</p>
                  <p class="coupon-condition">满{{ coupon.minAmount }}元可用</p>
                </div>
                <div class="coupon-discount">
                  <span class="discount-amount">-¥{{ coupon.discountAmount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单摘要 -->
        <div class="section summary-section">
          <h2 class="section-title">订单摘要</h2>
          <div class="summary-details">
            <div class="summary-row">
              <span>商品总额：</span>
              <span>¥{{ productTotal }}</span>
            </div>
            <div v-if="selectedCoupon" class="summary-row discount">
              <span>优惠券折扣：</span>
              <span>-¥{{ selectedCoupon.discountAmount }}</span>
            </div>
            <div class="summary-row total">
              <span>订单总额：</span>
              <span class="total-amount">¥{{ finalTotal }}</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button @click="goBack" class="btn-cancel">返回购物车</button>
          <button @click="submitOrder" :disabled="!isFormValid || submitting" class="btn-submit">
            {{ submitting ? "提交中..." : "提交订单" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { useCartStore } from "@/store/cartStore";
import { useOrderStore } from "@/store/orderStore";
import { getAvailableCoupons } from "@/api/coupon";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const cartStore = useCartStore();
const orderStore = useOrderStore();

const loading = ref(false);
const submitting = ref(false);
const selectedItems = ref([]);
const availableCoupons = ref([]);
const selectedCoupon = ref(null);

const form = ref({
  receiverName: "",
  receiverPhone: "",
  receiverAddress: "",
  remark: ""
});

const productTotal = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + (item.subtotal || 0), 0).toFixed(2);
});

const finalTotal = computed(() => {
  let total = parseFloat(productTotal.value);
  if (selectedCoupon.value) {
    total -= parseFloat(selectedCoupon.value.discountAmount);
  }
  return Math.max(0, total).toFixed(2);
});

const isFormValid = computed(() => {
  return form.value.receiverName && form.value.receiverPhone && form.value.receiverAddress;
});

// src/views/user/OrderConfirm.vue

const loadData = async () => {
  // 打印日志
  console.log("OrderConfirm.vue:", route.query);
  if (!userStore.isLogin) {
    router.push("/login");
    return;
  }

  loading.value = true;
  try {
    // 1. 获取 URL 参数中的 cartIds
    const cartIds = route.query.cartIds ? route.query.cartIds.split(",").map(Number) : [];
    if (cartIds.length === 0) {
      ElMessage.warning("没有选中的商品");
      router.push("/cart");
      return;
    }

    // 2. ★★★ 关键修改：如果 store 中没数据，先重新拉取一次购物车列表 ★★★
    if (cartStore.cartItems.length === 0) {
      await cartStore.fetchCart(userStore.userInfo.id);
    }

    // 3. 现在再从 store 中筛选选中的商品
    selectedItems.value = cartStore.getSelectedItems(cartIds);

    if (selectedItems.value.length === 0) {
      ElMessage.warning("购物车中找不到对应的商品，可能已被删除");
      router.push("/cart");
      return;
    }

    // 4. 获取店铺ID（假设所有商品来自同一店铺）
    // 注意：需确保 selectedItems 非空后再取 shopId
    const shopId = selectedItems.value[0].shopId;

    // 5. 获取可用优惠券
    const coupons = await getAvailableCoupons(shopId);
    availableCoupons.value = coupons || [];
    
  } catch (error) {
    console.error("加载数据失败:", error);
    ElMessage.error("加载数据失败");
  } finally {
    loading.value = false;
  }
};

const selectCoupon = (coupon) => {
  if (selectedCoupon.value?.id === coupon.id) {
    selectedCoupon.value = null;
  } else {
    selectedCoupon.value = coupon;
  }
};

const submitOrder = async () => {
  if (!isFormValid.value) {
    ElMessage.warning("请填写完整的收货信息");
    return;
  }

  submitting.value = true;
  try {
    const cartIds = selectedItems.value.map(item => item.id);
    
    const orderData = {
      userId: userStore.userInfo.id,
      cartIds,
      receiverName: form.value.receiverName,
      receiverPhone: form.value.receiverPhone,
      receiverAddress: form.value.receiverAddress,
      remark: form.value.remark
    };

    const order = await orderStore.submitOrder(orderData);
    
    ElMessage.success("订单创建成功");
    
    // 跳转到订单详情页
    router.push(`/order/${order.id}`);
  } catch (error) {
    console.error("创建订单失败:", error);
    ElMessage.error(error.message || "创建订单失败");
  } finally {
    submitting.value = false;
  }
};

const goBack = () => {
  router.push("/cart");
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.order-confirm-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  margin: 0 0 30px 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
}

.loading,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  color: #999;
  font-size: 16px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.btn-back {
  display: inline-block;
  padding: 10px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.confirm-content {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.section {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.section:last-of-type {
  border-bottom: none;
}

.section-title {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: grid;
  grid-template-columns: 80px 1fr 80px 100px;
  gap: 15px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 4px;
  align-items: center;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
  background: white;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-name {
  margin: 0;
  font-size: 14px;
  color: #333;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  margin: 0;
  font-size: 13px;
  color: #ff6b6b;
  font-weight: 600;
}

.product-quantity {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.product-subtotal {
  text-align: right;
}

.subtotal {
  font-size: 14px;
  color: #ff6b6b;
  font-weight: 600;
}

.address-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.coupon-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.no-coupon {
  text-align: center;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 4px;
  color: #999;
  font-size: 14px;
}

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.coupon-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f9f9f9;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.coupon-item:hover {
  background: #f0f0f0;
}

.coupon-item.selected {
  background: #f0f7ff;
  border-color: #667eea;
}

.coupon-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.coupon-name {
  margin: 0;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.coupon-condition {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.coupon-discount {
  text-align: right;
}

.discount-amount {
  font-size: 14px;
  color: #ff6b6b;
  font-weight: 600;
}

.summary-section {
  background: #f9f9f9;
}

.summary-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}

.summary-row.discount {
  color: #ff6b6b;
}

.summary-row.total {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  padding-top: 10px;
  border-top: 1px solid #ddd;
}

.total-amount {
  color: #ff6b6b;
  font-size: 18px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  padding: 20px;
  background: white;
  border-top: 1px solid #eee;
  justify-content: flex-end;
}

.btn-cancel,
.btn-submit {
  padding: 12px 30px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: white;
  color: #666;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background: #f5f5f5;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .product-item {
    grid-template-columns: 60px 1fr;
    gap: 10px;
  }

  .product-quantity,
  .product-subtotal {
    grid-column: 1 / -1;
    display: flex;
    justify-content: space-between;
  }

  .action-buttons {
    flex-direction: column;
  }

  .btn-cancel,
  .btn-submit {
    width: 100%;
  }
}
</style>
