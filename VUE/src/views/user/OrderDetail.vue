<template>
  <div class="order-detail-page">
    <div class="container">
      <div class="header">
        <h1 class="page-title">订单详情</h1>
        <button @click="goBack" class="btn-back">返回</button>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="!order" class="empty-state">
        <p>订单不存在</p>
      </div>
      <div v-else class="detail-content">
        <div class="section status-section">
          <div class="status-card" :class="getStatusClass(order.status)">
            <div class="status-icon">
              <span v-if="order.status === 0">⌛</span>
              <span v-else-if="order.status === 1">📦</span>
              <span v-else-if="order.status === 2">🚚</span>
              <span v-else-if="order.status === 3">✓</span>
              <span v-else-if="order.status === 4">✕</span>
            </div>
            <div class="status-info">
              <p class="status-title">{{ getStatusText(order.status) }}</p>
              <p class="status-time">{{ formatDate(order.createTime) }}</p>
            </div>
          </div>
        </div>

        <div class="section">
          <h2 class="section-title">订单信息</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">订单号：</span>
              <span class="value">{{ order.orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="label">下单时间：</span>
              <span class="value">{{ formatDate(order.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">支付时间：</span>
              <span class="value">{{ order.payTime ? formatDate(order.payTime) : "未支付" }}</span>
            </div>
            <div class="info-item">
              <span class="label">发货时间：</span>
              <span class="value">{{ order.shipTime ? formatDate(order.shipTime) : "未发货" }}</span>
            </div>
          </div>
        </div>

        <div class="section">
          <div class="section-header">
            <h2 class="section-title">收货信息</h2>
            <button
              v-if="canEditReceiverInfo && !editingReceiver"
              @click="startEditReceiver"
              class="btn-edit-address"
            >
              修改收货信息
            </button>
          </div>

          <div v-if="editingReceiver" class="address-form">
            <div class="form-group">
              <label>收货人</label>
              <input
                v-model.trim="receiverForm.receiverName"
                type="text"
                placeholder="请输入收货人姓名"
              />
            </div>
            <div class="form-group">
              <label>电话号码</label>
              <input
                v-model.trim="receiverForm.receiverPhone"
                type="text"
                placeholder="请输入电话号码"
              />
            </div>
            <div class="form-group">
              <label>收货地址</label>
              <textarea
                v-model.trim="receiverForm.receiverAddress"
                rows="3"
                placeholder="请输入收货地址"
              ></textarea>
            </div>
            <div class="address-form-actions">
              <button
                @click="saveReceiverInfo"
                :disabled="savingReceiver || !isReceiverFormValid"
                class="btn-address-action btn-address-save"
              >
                {{ savingReceiver ? "保存中..." : "保存" }}
              </button>
              <button
                @click="cancelEditReceiver"
                :disabled="savingReceiver"
                class="btn-address-action btn-address-cancel"
              >
                取消
              </button>
            </div>
          </div>

          <div v-else class="address-info">
            <p class="address-name">{{ order.receiverName || "未填写" }}</p>
            <p class="address-phone">{{ order.receiverPhone || "未填写" }}</p>
            <p class="address-detail">{{ order.receiverAddress || "未填写" }}</p>
            <p v-if="order.remark" class="address-remark">备注：{{ order.remark }}</p>
          </div>
        </div>

        <div class="section">
          <h2 class="section-title">订单商品</h2>
          <div class="products-table">
            <div class="table-header">
              <div class="col-product">商品</div>
              <div class="col-price">单价</div>
              <div class="col-quantity">数量</div>
              <div class="col-subtotal">小计</div>
            </div>
            <div class="table-body">
              <div v-for="item in orderItems" :key="item.id" class="table-row">
                <div class="col-product">
                  <div class="product-info">
                    <img :src="getImageUrl(item.productImage)" :alt="item.productName" class="product-image" />
                    <p class="product-name">{{ item.productName }}</p>
                  </div>
                </div>
                <div class="col-price">￥{{ item.price }}</div>
                <div class="col-quantity">{{ item.quantity }}</div>
                <div class="col-subtotal">￥{{ item.subtotal }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="section amount-section">
          <div class="amount-summary">
            <div class="amount-row">
              <span>商品总额：</span>
              <span>￥{{ order.totalAmount }}</span>
            </div>
            <div class="amount-row total">
              <span>订单总额：</span>
              <span class="total-amount">￥{{ order.totalAmount }}</span>
            </div>
          </div>
        </div>

        <div class="section action-section">
          <div class="actions">
            <button
              v-if="order.status === 0"
              @click="payOrder"
              :disabled="paying"
              class="btn-action btn-pay"
            >
              {{ paying ? "支付中..." : "支付订单" }}
            </button>
            <button
              v-if="order.status === 0"
              @click="cancelOrderAction"
              :disabled="cancelling"
              class="btn-action btn-cancel"
            >
              {{ cancelling ? "取消中..." : "取消订单" }}
            </button>
            <button
              v-if="order.status === 2"
              @click="completeOrderAction"
              :disabled="completing"
              class="btn-action btn-complete"
            >
              {{ completing ? "确认中..." : "确认收货" }}
            </button>
            <button @click="goBack" class="btn-action btn-back-action">返回列表</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { useOrderStore } from "@/store/orderStore";
import { useUserStore } from "@/store/userStore";
import { getOrderItems } from "@/api/orderItems";

const getImageUrl = (src) => {
  if (!src) return "";
  if (src.startsWith("http")) {
    return src;
  }
  return `http://localhost:8080/uploads/${src}`;
};

const router = useRouter();
const route = useRoute();
const orderStore = useOrderStore();
const userStore = useUserStore();

const loading = ref(false);
const paying = ref(false);
const cancelling = ref(false);
const completing = ref(false);
const savingReceiver = ref(false);
const editingReceiver = ref(false);
const order = ref(null);
const orderItems = ref([]);
const receiverForm = ref({
  receiverName: "",
  receiverPhone: "",
  receiverAddress: ""
});

const canEditReceiverInfo = computed(() => order.value?.status === 0);
const isReceiverFormValid = computed(() => {
  return (
    receiverForm.value.receiverName &&
    receiverForm.value.receiverPhone &&
    receiverForm.value.receiverAddress
  );
});

const syncReceiverForm = () => {
  receiverForm.value = {
    receiverName: order.value?.receiverName || "",
    receiverPhone: order.value?.receiverPhone || "",
    receiverAddress: order.value?.receiverAddress || ""
  };
};

const loadOrderDetail = async () => {
  loading.value = true;
  try {
    const orderId = route.params.id;
    order.value = await orderStore.fetchOrderDetail(orderId);
    syncReceiverForm();

    try {
      orderItems.value = await getOrderItems(orderId);
    } catch (error) {
      console.error("获取订单商品失败:", error);
      orderItems.value = [];
    }
  } catch (error) {
    console.error("加载订单详情失败:", error);
    ElMessage.error("加载订单详情失败");
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return `${date.toLocaleDateString("zh-CN")} ${date.toLocaleTimeString("zh-CN")}`;
};

const getStatusText = (status) => orderStore.getStatusText(status);

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

const startEditReceiver = () => {
  syncReceiverForm();
  editingReceiver.value = true;
};

const cancelEditReceiver = () => {
  syncReceiverForm();
  editingReceiver.value = false;
};

// ★ 核心修复位置
const saveReceiverInfo = async () => {
  if (!isReceiverFormValid.value) {
    ElMessage.warning("请填写完整的收货信息");
    return;
  }

  try {
    savingReceiver.value = true;
    
    // 1. 调用更新接口
    await orderStore.updateReceiver(order.value.id, receiverForm.value);
    
    // 2. 关闭编辑状态
    editingReceiver.value = false;
    
    // 3. 提示成功
    ElMessage.success("收货信息已更新");
    
    // 4. ★直接重新加载数据刷新页面，不要再去读取没定义的变量了
    await loadOrderDetail();
    
  } catch (error) {
    console.error("修改收货信息失败:", error);
    ElMessage.error(error.message || "修改收货信息失败");
  } finally {
    savingReceiver.value = false;
  }
};

const payOrder = async () => {
  try {
    await ElMessageBox.confirm("确定要支付这个订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    paying.value = true;
    const payResult = await orderStore.pay(order.value.id);
    const awardedPoints = Number(payResult?.awardedPoints ?? Math.floor(Number(order.value?.totalAmount || 0) / 100));
    const currentPoints = Number(payResult?.currentPoints ?? Number(userStore.userInfo?.point || 0) + awardedPoints);
    if (userStore.userInfo) {
      userStore.setUserInfo({
        ...userStore.userInfo,
        point: currentPoints
      });
    }
    ElMessage.closeAll();
    ElMessage.success(`支付成功，获得${awardedPoints}积分，当前总积分${currentPoints}`);
    await loadOrderDetail();
  } catch (error) {
    if (error !== "cancel") {
      console.error("支付失败:", error);
      ElMessage.error("支付失败");
    }
  } finally {
    paying.value = false;
  }
};

const cancelOrderAction = async () => {
  try {
    await ElMessageBox.confirm("确定要取消这个订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    cancelling.value = true;
    await orderStore.cancel(order.value.id);
    ElMessage.success("订单已取消");
    await loadOrderDetail();
  } catch (error) {
    if (error !== "cancel") {
      console.error("取消订单失败:", error);
      ElMessage.error("取消订单失败");
    }
  } finally {
    cancelling.value = false;
  }
};

const completeOrderAction = async () => {
  try {
    await ElMessageBox.confirm("确认已经收到商品？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    completing.value = true;
    await orderStore.complete(order.value.id);
    ElMessage.success("订单已完成");
    await loadOrderDetail();
  } catch (error) {
    if (error !== "cancel") {
      console.error("确认收货失败:", error);
      ElMessage.error("确认收货失败");
    }
  } finally {
    completing.value = false;
  }
};

const goBack = () => {
  router.push("/user/orders");
};

onMounted(() => {
  loadOrderDetail();
});
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  margin: 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
}

.btn-back {
  padding: 8px 16px;
  background: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  border-color: #667eea;
  color: #667eea;
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

.detail-content {
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

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 15px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.btn-edit-address {
  padding: 8px 14px;
  border: 1px solid #667eea;
  background: white;
  color: #667eea;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s ease;
}

.btn-edit-address:hover {
  background: #667eea;
  color: white;
}

.status-section {
  background: #f9f9f9;
}

.status-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid #ddd;
}

.status-card.status-pending {
  background: #fff3cd;
  border-left-color: #ffc107;
}

.status-card.status-processing {
  background: #cfe2ff;
  border-left-color: #0d6efd;
}

.status-card.status-shipped {
  background: #d1ecf1;
  border-left-color: #17a2b8;
}

.status-card.status-completed {
  background: #d4edda;
  border-left-color: #28a745;
}

.status-card.status-cancelled {
  background: #f8d7da;
  border-left-color: #dc3545;
}

.status-icon {
  font-size: 32px;
}

.status-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.status-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.status-time {
  margin: 0;
  font-size: 12px;
  color: #666;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 4px;
}

.address-name {
  margin: 0;
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.address-phone {
  margin: 0;
  font-size: 13px;
  color: #666;
}

.address-detail {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.address-remark {
  margin: 0;
  font-size: 12px;
  color: #999;
  font-style: italic;
}

.address-form {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 4px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 14px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  font-size: 13px;
  color: #666;
  font-weight: 600;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  color: #333;
  background: white;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.12);
}

.form-group textarea {
  resize: vertical;
  min-height: 88px;
}

.address-form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 16px;
}

.btn-address-action {
  padding: 10px 18px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-address-save {
  background: #667eea;
  border: 1px solid #667eea;
  color: white;
}

.btn-address-cancel {
  background: white;
  border: 1px solid #ddd;
  color: #666;
}

.products-table {
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 1fr 100px 80px 100px;
  gap: 15px;
  padding: 12px 15px;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
  font-weight: 600;
  font-size: 13px;
  color: #333;
}

.table-body {
  display: flex;
  flex-direction: column;
}

.table-row {
  display: grid;
  grid-template-columns: 1fr 100px 80px 100px;
  gap: 15px;
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  align-items: center;
  font-size: 13px;
}

.table-row:last-child {
  border-bottom: none;
}

.col-product {
  display: flex;
  align-items: center;
}

.product-info {
  display: flex;
  gap: 10px;
  align-items: center;
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  object-fit: cover;
  background: #f5f5f5;
}

.product-name {
  margin: 0;
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.col-price,
.col-quantity,
.col-subtotal {
  text-align: right;
  color: #666;
}

.col-subtotal {
  color: #ff6b6b;
  font-weight: 600;
}

.amount-section {
  background: #f9f9f9;
}

.amount-summary {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 300px;
  margin-left: auto;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}

.amount-row.total {
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

.action-section {
  background: white;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.btn-action {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-pay {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-pay:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-cancel {
  background: white;
  color: #ff6b6b;
  border: 1px solid #ff6b6b;
}

.btn-cancel:hover:not(:disabled) {
  background: #ff6b6b;
  color: white;
}

.btn-complete {
  background: white;
  color: #28a745;
  border: 1px solid #28a745;
}

.btn-complete:hover:not(:disabled) {
  background: #28a745;
  color: white;
}

.btn-back-action {
  background: white;
  color: #666;
  border: 1px solid #ddd;
}

.btn-back-action:hover {
  border-color: #667eea;
  color: #667eea;
}

.btn-action:disabled,
.btn-address-action:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .table-header,
  .table-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .col-price,
  .col-quantity,
  .col-subtotal {
    text-align: left;
  }

  .actions,
  .address-form-actions {
    justify-content: flex-start;
  }

  .btn-action {
    flex: 1;
    min-width: 100px;
  }
}
</style>
