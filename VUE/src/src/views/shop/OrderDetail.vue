<template>
  <div class="shop-order-detail">
    <div class="page-header">
      <h1>订单详情</h1>
      <router-link to="/shop/orders" class="btn-back">← 返回列表</router-link>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="order" class="detail-container">
      <!-- 订单基本信息 -->
      <div class="info-section">
        <h2>订单信息</h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">订单号：</span>
            <span class="value">{{ order.orderNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">订单状态：</span>
            <span class="value" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="value">{{ formatDate(order.createTime) }}</span>
          </div>
          <div class="info-item">
            <span class="label">支付时间：</span>
            <span class="value">{{ formatDate(order.payTime) || "-" }}</span>
          </div>
          <div class="info-item">
            <span class="label">发货时间：</span>
            <span class="value">{{ formatDate(order.shipTime) || "-" }}</span>
          </div>
          <div class="info-item">
            <span class="label">订单金额：</span>
            <span class="value amount">¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </div>

      <!-- 收货信息 -->
      <div class="info-section">
        <h2>收货信息</h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">收货人：</span>
            <span class="value">{{ order.receiverName }}</span>
          </div>
          <div class="info-item">
            <span class="label">收货电话：</span>
            <span class="value">{{ order.receiverPhone }}</span>
          </div>
          <div class="info-item full-width">
            <span class="label">收货地址：</span>
            <span class="value">{{ order.receiverAddress }}</span>
          </div>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="info-section">
        <h2>商品信息</h2>
        <div class="items-table">
          <table>
            <thead>
              <tr>
                <th>商品</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in order.orderItems" :key="item.id">
                <td class="product-cell">
                  <img v-if="item.productImage" :src="item.productImage" :alt="item.productName" class="product-thumb" />
                  <span>{{ item.productName }}</span>
                </td>
                <td>¥{{ item.price }}</td>
                <td>{{ item.quantity }}</td>
                <td>¥{{ item.subtotal }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="actions-section">
        <button
          v-if="order.status === 1"
          @click="handleShip"
          class="btn-primary"
          :disabled="shipping"
        >
          {{ shipping ? "发货中..." : "确认发货" }}
        </button>
        <button @click="handlePrint" class="btn-secondary">打印订单</button>
      </div>
    </div>
    <div v-else class="empty">订单不存在</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { getShopOrderDetail, shipOrder } from "@/api/shop";

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const shipping = ref(false);
const order = ref(null);

const getStatusText = (status) => {
  const statusMap = {
    0: "待支付",
    1: "待发货",
    2: "已发货",
    3: "已完成",
    4: "已取消"
  };
  return statusMap[status] || "未知";
};

const getStatusClass = (status) => {
  const classMap = {
    0: "status-pending",
    1: "status-unpaid",
    2: "status-shipped",
    3: "status-completed",
    4: "status-cancelled"
  };
  return classMap[status] || "";
};

const formatDate = (date) => {
  if (!date) return "";
  return new Date(date).toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  });
};

const loadOrderDetail = async () => {
  loading.value = true;
  try {
    const data = await getShopOrderDetail(route.params.id);
    order.value = data;
  } catch (error) {
    ElMessage.error("加载订单详情失败");
  } finally {
    loading.value = false;
  }
};

const handleShip = async () => {
  try {
    await ElMessageBox.confirm("确定要发货吗？", "确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    shipping.value = true;
    await shipOrder(route.params.id, {});
    ElMessage.success("发货成功");
    await loadOrderDetail();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("发货失败");
    }
  } finally {
    shipping.value = false;
  }
};

const handlePrint = () => {
  window.print();
};

onMounted(() => {
  loadOrderDetail();
});
</script>

<style scoped>
.shop-order-detail {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
}

.btn-back {
  padding: 10px 20px;
  background: #f0f0f0;
  color: #333;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #e0e0e0;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.info-section h2 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #333;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item .label {
  color: #999;
  font-size: 12px;
  font-weight: 500;
}

.info-item .value {
  color: #333;
  font-size: 14px;
}

.info-item .value.amount {
  font-size: 18px;
  font-weight: 600;
  color: #e74c3c;
}

.status-pending {
  color: #f39c12;
}

.status-unpaid {
  color: #e74c3c;
}

.status-shipped {
  color: #3498db;
}

.status-completed {
  color: #27ae60;
}

.status-cancelled {
  color: #95a5a6;
}

.items-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #f9f9f9;
  border-bottom: 2px solid #eee;
}

th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
  color: #333;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-thumb {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.actions-section {
  display: flex;
  gap: 10px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #27ae60;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #229954;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f0f0f0;
  color: #333;
}

.btn-secondary:hover {
  background: #e0e0e0;
}

@media print {
  .page-header,
  .actions-section {
    display: none;
  }
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .actions-section {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
  }
}
</style>
