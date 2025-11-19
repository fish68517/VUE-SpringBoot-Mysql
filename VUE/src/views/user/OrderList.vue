<template>
  <div class="order-list-page">
    <div class="container">
      <h1 class="page-title">我的订单</h1>

      <!-- 状态筛选 -->
      <div class="filter-section">
        <button
          v-for="status in statusFilters"
          :key="status.value"
          class="filter-btn"
          :class="{ active: selectedStatus === status.value }"
          @click="selectedStatus = status.value"
        >
          {{ status.label }}
        </button>
      </div>

      <!-- 订单列表 -->
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="filteredOrders.length === 0" class="empty-state">
        <p>暂无订单</p>
        <router-link to="/products" class="btn-continue-shopping">继续购物</router-link>
      </div>
      <div v-else class="orders-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <p class="order-no">订单号：{{ order.orderNo }}</p>
              <p class="order-time">{{ formatDate(order.createTime) }}</p>
            </div>
            <div class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </div>
          </div>

          <!-- 订单商品 -->
          <div class="order-items">
            <div v-for="item in getOrderItems(order.id)" :key="item.id" class="order-item">
              <img :src="item.productImage" :alt="item.productName" class="item-image" />
              <div class="item-info">
                <p class="item-name">{{ item.productName }}</p>
                <p class="item-price">¥{{ item.price }}</p>
              </div>
              <div class="item-quantity">x{{ item.quantity }}</div>
              <div class="item-subtotal">¥{{ item.subtotal }}</div>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="order-total">
              <span>合计：</span>
              <span class="total-amount">¥{{ order.totalAmount }}</span>
            </div>
            <div class="order-actions">
              <button
                v-if="order.status === 0"
                @click="goToDetail(order.id)"
                class="btn-action btn-pay"
              >
                支付
              </button>
              <button
                v-if="order.status === 0"
                @click="cancelOrderAction(order.id)"
                class="btn-action btn-cancel"
              >
                取消
              </button>
              <button
                v-if="order.status === 2"
                @click="completeOrderAction(order.id)"
                class="btn-action btn-complete"
              >
                确认收货
              </button>
              <button @click="goToDetail(order.id)" class="btn-action btn-detail">
                详情
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { useOrderStore } from "@/store/orderStore";

const router = useRouter();
const userStore = useUserStore();
const orderStore = useOrderStore();

const loading = ref(false);
const selectedStatus = ref(-1);

const statusFilters = [
  { value: -1, label: "全部订单" },
  { value: 0, label: "待支付" },
  { value: 1, label: "待发货" },
  { value: 2, label: "已发货" },
  { value: 3, label: "已完成" },
  { value: 4, label: "已取消" }
];

const filteredOrders = computed(() => {
  if (selectedStatus.value === -1) {
    return orderStore.orders;
  }
  return orderStore.orders.filter(order => order.status === selectedStatus.value);
});

const loadOrders = async () => {
  if (!userStore.isLogin) {
    router.push("/login");
    return;
  }

  loading.value = true;
  try {
    await orderStore.fetchOrders(userStore.userInfo.id);
  } catch (error) {
    console.error("加载订单失败:", error);
    ElMessage.error("加载订单失败");
  } finally {
    loading.value = false;
  }
};

const getOrderItems = (orderId) => {
  // 这里需要从后端获取订单明细
  // 暂时返回空数组，实际应该在订单详情中获取
  return [];
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN") + " " + date.toLocaleTimeString("zh-CN");
};

const getStatusText = (status) => {
  return orderStore.getStatusText(status);
};

const getStatusClass = (status) => {
  const classMap = {
    0: "status-pending",
    1: "status-processing",
    2: "status-shipped",
    3: "status-completed",
    4: "status-cancelled"
  };
  return classMap[status] || "";
};

const goToDetail = (orderId) => {
  router.push(`/order/${orderId}`);
};

const cancelOrderAction = async (orderId) => {
  try {
    await ElMessageBox.confirm("确定要取消这个订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    await orderStore.cancel(orderId);
    ElMessage.success("订单已取消");
  } catch (error) {
    if (error !== "cancel") {
      console.error("取消订单失败:", error);
      ElMessage.error("取消订单失败");
    }
  }
};

const completeOrderAction = async (orderId) => {
  try {
    await ElMessageBox.confirm("确认已收到商品？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    await orderStore.complete(orderId);
    ElMessage.success("订单已完成");
  } catch (error) {
    if (error !== "cancel") {
      console.error("确认收货失败:", error);
      ElMessage.error("确认收货失败");
    }
  }
};

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.order-list-page {
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

.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 8px 16px;
  background: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.filter-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
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

.btn-continue-shopping {
  display: inline-block;
  padding: 10px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.btn-continue-shopping:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no {
  margin: 0;
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.order-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.order-status {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-processing {
  background: #cfe2ff;
  color: #084298;
}

.status-shipped {
  background: #d1ecf1;
  color: #0c5460;
}

.status-completed {
  background: #d4edda;
  color: #155724;
}

.status-cancelled {
  background: #f8d7da;
  color: #721c24;
}

.order-items {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.order-item {
  display: grid;
  grid-template-columns: 60px 1fr 60px 80px;
  gap: 12px;
  align-items: center;
  padding: 10px 0;
}

.order-item:not(:last-child) {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 10px;
  margin-bottom: 10px;
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
  background: #f5f5f5;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-name {
  margin: 0;
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price {
  margin: 0;
  font-size: 12px;
  color: #ff6b6b;
  font-weight: 600;
}

.item-quantity {
  text-align: center;
  font-size: 12px;
  color: #666;
}

.item-subtotal {
  text-align: right;
  font-size: 12px;
  color: #ff6b6b;
  font-weight: 600;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f9f9f9;
}

.order-total {
  font-size: 14px;
  color: #666;
}

.total-amount {
  font-size: 16px;
  color: #ff6b6b;
  font-weight: 600;
  margin-left: 8px;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.btn-action {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-pay {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-pay:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.btn-cancel {
  background: white;
  color: #ff6b6b;
  border: 1px solid #ff6b6b;
}

.btn-cancel:hover {
  background: #ff6b6b;
  color: white;
}

.btn-complete {
  background: white;
  color: #28a745;
  border: 1px solid #28a745;
}

.btn-complete:hover {
  background: #28a745;
  color: white;
}

.btn-detail {
  background: white;
  color: #666;
  border: 1px solid #ddd;
}

.btn-detail:hover {
  border-color: #667eea;
  color: #667eea;
}

@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .order-item {
    grid-template-columns: 50px 1fr;
    gap: 10px;
  }

  .item-quantity,
  .item-subtotal {
    grid-column: 1 / -1;
    display: flex;
    justify-content: space-between;
  }

  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .order-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .btn-action {
    flex: 1;
    min-width: 80px;
  }
}
</style>
