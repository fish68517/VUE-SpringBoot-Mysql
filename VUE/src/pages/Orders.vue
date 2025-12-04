<template>
  <div class="orders-container">
    <!-- Header -->
    <div class="orders-header">
      <div class="header-content">
        <el-button type="primary" text icon="ArrowLeft" @click="handleBackToHome">返回首页</el-button>
        <h1>我的订单</h1>
        <div style="width: 100px"></div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="orders-content">
      <!-- Tabs for order types -->
      <div class="tabs-section">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部订单" name="all">
            <template #label>
              <span>全部订单 <span class="tab-count">({{ allOrders.length }})</span></span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="商品订单" name="product">
            <template #label>
              <span>商品订单 <span class="tab-count">({{ productOrders.length }})</span></span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="待支付" name="pending">
            <template #label>
              <span>待支付 <span class="tab-count">({{ pendingOrders.length }})</span></span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="已发货" name="shipped">
            <template #label>
              <span>已发货 <span class="tab-count">({{ shippedOrders.length }})</span></span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="已完成" name="completed">
            <template #label>
              <span>已完成 <span class="tab-count">({{ completedOrders.length }})</span></span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- Empty State -->
      <div v-if="displayedOrders.length === 0" class="empty-state">
        <el-empty description="暂无订单" />
      </div>

      <!-- Orders List -->
      <div v-else class="orders-list">
        <div v-for="order in displayedOrders" :key="order.id" class="order-card">
          <!-- Order Header -->
          <div class="order-header">
            <div class="order-info">
              <div class="order-id">订单号：{{ order.id }}</div>
              <div class="order-type">{{ getOrderTypeLabel(order.orderType) }}</div>
              <div class="order-date">{{ formatDate(order.createdAt) }}</div>
            </div>
            <div class="order-status">
              <el-tag :type="getStatusType(order.status)">{{ getStatusLabel(order.status) }}</el-tag>
            </div>
          </div>

          <!-- Order Items -->
          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-item">
              <div class="item-info">
                <div class="item-name">{{ item.productName }}</div>
                <div class="item-details">
                  <span class="item-quantity">数量：{{ item.quantity }}</span>
                  <span class="item-price">¥{{ item.unitPrice.toFixed(2) }}</span>
                </div>
              </div>
              <div class="item-subtotal">¥{{ item.subtotal.toFixed(2) }}</div>
            </div>
          </div>

          <!-- Order Footer -->
          <div class="order-footer">
            <div class="order-total">
              <span class="label">订单总额：</span>
              <span class="total-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>

            <!-- Shipping Address -->
            <div v-if="order.shippingAddress" class="shipping-info">
              <span class="label">收货地址：</span>
              <span class="address">{{ order.shippingAddress }}</span>
            </div>

            <!-- Tracking Number -->
            <div v-if="order.trackingNumber && order.status === 'shipped'" class="tracking-info">
              <span class="label">物流单号：</span>
              <span class="tracking-number">{{ order.trackingNumber }}</span>
              <el-button type="primary" text size="small" @click="handleCopyTracking(order.trackingNumber)">
                复制
              </el-button>
            </div>

            <!-- Action Buttons -->
            <div class="order-actions">
              <el-button type="primary" text @click="handleViewDetails(order)">
                查看详情
              </el-button>
              <el-button v-if="order.status === 'shipped'" type="primary" text @click="handleTrackOrder(order)">
                查看物流
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Order Details Dialog -->
    <el-dialog v-model="showDetailsDialog" title="订单详情" width="600px" @close="handleCloseDetails">
      <div v-if="selectedOrder" class="order-details">
        <!-- Basic Info -->
        <div class="details-section">
          <h3>基本信息</h3>
          <div class="details-row">
            <span class="label">订单号：</span>
            <span class="value">{{ selectedOrder.id }}</span>
          </div>
          <div class="details-row">
            <span class="label">订单类型：</span>
            <span class="value">{{ getOrderTypeLabel(selectedOrder.orderType) }}</span>
          </div>
          <div class="details-row">
            <span class="label">订单状态：</span>
            <span class="value">
              <el-tag :type="getStatusType(selectedOrder.status)">
                {{ getStatusLabel(selectedOrder.status) }}
              </el-tag>
            </span>
          </div>
          <div class="details-row">
            <span class="label">下单时间：</span>
            <span class="value">{{ formatDateTime(selectedOrder.createdAt) }}</span>
          </div>
        </div>

        <!-- Items -->
        <div class="details-section">
          <h3>订单商品</h3>
          <div v-for="item in selectedOrder.items" :key="item.id" class="item-detail">
            <div class="item-name">{{ item.productName }}</div>
            <div class="item-row">
              <span>数量：{{ item.quantity }}</span>
              <span>单价：¥{{ item.unitPrice.toFixed(2) }}</span>
              <span>小计：¥{{ item.subtotal.toFixed(2) }}</span>
            </div>
          </div>
        </div>

        <!-- Shipping Info -->
        <div v-if="selectedOrder.shippingAddress" class="details-section">
          <h3>收货信息</h3>
          <div class="details-row">
            <span class="label">收货地址：</span>
            <span class="value">{{ selectedOrder.shippingAddress }}</span>
          </div>
        </div>

        <!-- Tracking Info -->
        <div v-if="selectedOrder.trackingNumber" class="details-section">
          <h3>物流信息</h3>
          <div class="details-row">
            <span class="label">物流单号：</span>
            <span class="value">{{ selectedOrder.trackingNumber }}</span>
          </div>
          <div class="tracking-timeline">
            <div class="timeline-item">
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <div class="timeline-title">订单已发货</div>
                <div class="timeline-time">{{ formatDate(selectedOrder.updatedAt) }}</div>
              </div>
            </div>
            <div class="timeline-item">
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <div class="timeline-title">物流单号：{{ selectedOrder.trackingNumber }}</div>
                <div class="timeline-time">请联系快递公司查询详细物流信息</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Total -->
        <div class="details-section total-section">
          <div class="details-row total">
            <span class="label">订单总额：</span>
            <span class="value">¥{{ selectedOrder.totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseDetails">关闭</el-button>
          <el-button v-if="selectedOrder?.status === 'shipped'" type="primary" @click="handleTrackOrder(selectedOrder)">
            查看物流
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Tracking Dialog -->
    <el-dialog v-model="showTrackingDialog" title="物流追踪" width="600px" @close="handleCloseTracking">
      <div v-if="selectedOrder" class="tracking-details">
        <div class="tracking-header">
          <div class="tracking-number">物流单号：{{ selectedOrder.trackingNumber }}</div>
          <div class="tracking-company">快递公司：请联系快递公司查询</div>
        </div>

        <div class="tracking-timeline">
          <div class="timeline-item">
            <div class="timeline-dot active"></div>
            <div class="timeline-content">
              <div class="timeline-title">订单已发货</div>
              <div class="timeline-time">{{ formatDate(selectedOrder.updatedAt) }}</div>
              <div class="timeline-desc">您的订单已由卖家发出</div>
            </div>
          </div>

          <div class="timeline-item">
            <div class="timeline-dot"></div>
            <div class="timeline-content">
              <div class="timeline-title">运输中</div>
              <div class="timeline-time">预计 3-5 个工作日送达</div>
              <div class="timeline-desc">快递正在运输中，请耐心等待</div>
            </div>
          </div>

          <div class="timeline-item">
            <div class="timeline-dot"></div>
            <div class="timeline-content">
              <div class="timeline-title">待签收</div>
              <div class="timeline-time">-</div>
              <div class="timeline-desc">快递即将送达，请保持手机畅通</div>
            </div>
          </div>

          <div class="timeline-item">
            <div class="timeline-dot"></div>
            <div class="timeline-content">
              <div class="timeline-title">已签收</div>
              <div class="timeline-time">-</div>
              <div class="timeline-desc">感谢您的购买，欢迎再次光临</div>
            </div>
          </div>
        </div>

        <div class="tracking-tip">
          <el-alert
            title="提示"
            type="info"
            description="物流信息由快递公司提供，如需查询详细物流信息，请联系快递公司或使用快递公司官方渠道查询。"
            :closable="false"
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseTracking">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { orderApi, OrderDTO } from '@/api/order'

const router = useRouter()

// State
const orders = ref<OrderDTO[]>([])
const activeTab = ref('all')
const showDetailsDialog = ref(false)
const showTrackingDialog = ref(false)
const selectedOrder = ref<OrderDTO | null>(null)
const loading = ref(false)

// Computed properties
const allOrders = computed(() => orders.value)

const productOrders = computed(() => orders.value.filter(o => o.orderType === 'product'))

const pendingOrders = computed(() => orders.value.filter(o => o.status === 'paid'))

const shippedOrders = computed(() => orders.value.filter(o => o.status === 'shipped'))

const completedOrders = computed(() => orders.value.filter(o => o.status === 'completed'))

const displayedOrders = computed(() => {
  switch (activeTab.value) {
    case 'product':
      return productOrders.value
    case 'pending':
      return pendingOrders.value
    case 'shipped':
      return shippedOrders.value
    case 'completed':
      return completedOrders.value
    default:
      return allOrders.value
  }
})

// Methods
const loadOrders = async () => {
  loading.value = true
  try {
    const response = await orderApi.getProductOrders()
    if (response.data.code === 200) {
      orders.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '加载订单失败')
    }
  } catch (error) {
    ElMessage.error('加载订单失败，请稍后重试')
    console.error('Error loading orders:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  // Tab change is handled by computed property
}

const handleBackToHome = () => {
  router.push('/')
}

const handleViewDetails = (order: OrderDTO) => {
  selectedOrder.value = order
  showDetailsDialog.value = true
}

const handleCloseDetails = () => {
  showDetailsDialog.value = false
  selectedOrder.value = null
}

const handleTrackOrder = (order: OrderDTO) => {
  selectedOrder.value = order
  showDetailsDialog.value = false
  showTrackingDialog.value = true
}

const handleCloseTracking = () => {
  showTrackingDialog.value = false
  selectedOrder.value = null
}

const handleCopyTracking = (trackingNumber: string) => {
  navigator.clipboard.writeText(trackingNumber)
  ElMessage.success('物流单号已复制')
}

const getOrderTypeLabel = (type: string): string => {
  const typeMap: Record<string, string> = {
    product: '商品订单',
    ticket: '票务订单',
    points: '积分兑换',
  }
  return typeMap[type] || type
}

const getStatusLabel = (status: string): string => {
  const statusMap: Record<string, string> = {
    paid: '待发货',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消',
  }
  return statusMap[status] || status
}

const getStatusType = (status: string): string => {
  const typeMap: Record<string, string> = {
    paid: 'warning',
    shipped: 'info',
    completed: 'success',
    cancelled: 'danger',
  }
  return typeMap[status] || 'info'
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

// Lifecycle
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.orders-header {
  background: white;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.orders-header h1 {
  color: #333;
  margin: 0;
  font-size: 28px;
  font-weight: bold;
}

.orders-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.tabs-section {
  background: white;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tabs-section :deep(.el-tabs) {
  padding: 0;
}

.tabs-section :deep(.el-tabs__header) {
  margin: 0;
  border-bottom: 1px solid #e0e0e0;
}

.tab-count {
  color: #999;
  font-size: 12px;
  margin-left: 4px;
}

.empty-state {
  background: white;
  border-radius: 8px;
  padding: 60px 20px;
  text-align: center;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
  background: #fafafa;
}

.order-info {
  display: flex;
  gap: 30px;
  align-items: center;
}

.order-id {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.order-type {
  color: #666;
  font-size: 14px;
}

.order-date {
  color: #999;
  font-size: 12px;
}

.order-status {
  display: flex;
  align-items: center;
}

.order-items {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.order-item:not(:last-child) {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
  margin-bottom: 15px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  font-size: 14px;
}

.item-details {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 12px;
}

.item-quantity,
.item-price {
  display: flex;
  align-items: center;
}

.item-subtotal {
  font-weight: bold;
  color: #e74c3c;
  font-size: 14px;
  min-width: 100px;
  text-align: right;
}

.order-footer {
  padding: 20px;
  background: #fafafa;
}

.order-total {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.order-total .label {
  color: #666;
  margin-right: 10px;
}

.total-amount {
  font-weight: bold;
  color: #e74c3c;
  font-size: 16px;
}

.shipping-info,
.tracking-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  font-size: 12px;
}

.shipping-info .label,
.tracking-info .label {
  color: #666;
  font-weight: bold;
}

.address,
.tracking-number {
  color: #333;
  flex: 1;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
}

/* Order Details Dialog */
.order-details {
  max-height: 500px;
  overflow-y: auto;
}

.details-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.details-section h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 14px;
  font-weight: bold;
}

.details-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 13px;
}

.details-row .label {
  color: #666;
  font-weight: bold;
}

.details-row .value {
  color: #333;
}

.details-row.total {
  padding-top: 15px;
  border-top: 1px solid #e0e0e0;
  font-size: 14px;
  font-weight: bold;
}

.details-row.total .value {
  color: #e74c3c;
  font-size: 16px;
}

.item-detail {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.item-detail:last-child {
  border-bottom: none;
}

.item-detail .item-name {
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  font-size: 13px;
}

.item-detail .item-row {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 12px;
}

.total-section {
  border-bottom: none;
  padding-bottom: 0;
}

/* Tracking Timeline */
.tracking-timeline {
  margin-top: 20px;
}

.timeline-item {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  position: relative;
}

.timeline-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 7px;
  top: 30px;
  width: 2px;
  height: 40px;
  background: #e0e0e0;
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #e0e0e0;
  border: 2px solid white;
  flex-shrink: 0;
  margin-top: 2px;
}

.timeline-dot.active {
  background: #409eff;
  border-color: white;
}

.timeline-content {
  flex: 1;
  padding-top: 2px;
}

.timeline-title {
  font-weight: bold;
  color: #333;
  font-size: 13px;
  margin-bottom: 4px;
}

.timeline-time {
  color: #999;
  font-size: 12px;
  margin-bottom: 4px;
}

.timeline-desc {
  color: #666;
  font-size: 12px;
}

/* Tracking Details */
.tracking-details {
  padding: 20px 0;
}

.tracking-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.tracking-number,
.tracking-company {
  font-size: 13px;
  color: #333;
  margin-bottom: 8px;
}

.tracking-number {
  font-weight: bold;
}

.tracking-tip {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .orders-header h1 {
    font-size: 20px;
  }

  .orders-content {
    padding: 20px 15px;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .order-info {
    flex-direction: column;
    gap: 8px;
    width: 100%;
  }

  .order-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .item-subtotal {
    width: 100%;
    text-align: left;
  }

  .order-actions {
    justify-content: flex-start;
  }

  .details-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>
