<template>
  <div class="orders-container">
    <div class="orders-header">
      <div class="header-content">
        <el-button type="primary" text icon="ArrowLeft" @click="handleBackToHome">返回首页</el-button>
        <h1>我的订单</h1>
        <div style="width: 100px"></div>
      </div>
    </div>

    <div class="orders-content">
      <div class="tabs-section">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部订单" name="all">
            <template #label>
              <span>全部订单 <span class="tab-count">({{ allOrders.length }})</span></span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="票务订单" name="ticket">
            <template #label>
              <span>票务订单 <span class="tab-count">({{ ticketOrders.length }})</span></span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="商品订单" name="product">
            <template #label>
              <span>商品订单 <span class="tab-count">({{ productOrders.length }})</span></span>
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

      <div v-if="displayedOrders.length === 0" class="empty-state">
        <el-empty description="暂无订单" />
      </div>

      <div v-else class="orders-list">
        <div v-for="order in displayedOrders" :key="order.id" class="order-card">
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

          <div class="order-items">
            <template v-if="order.items && order.items.length > 0">
              <div v-for="item in order.items" :key="item.id" class="order-item">
                <div class="item-info">
                  <div class="item-name">{{ item.productName }}</div>
                  <div class="item-details">
                    <span class="item-quantity">数量：{{ item.quantity }}</span>
                    <span class="item-price">¥{{ item.unitPrice?.toFixed(2) }}</span>
                  </div>
                </div>
                <div class="item-subtotal">¥{{ item.subtotal?.toFixed(2) }}</div>
              </div>
            </template>
            <template v-else-if="order.orderType === 'ticket'">
              <div class="order-item">
                <div class="item-info">
                  <div class="item-name" style="color: #409eff; font-size: 16px;">🎟️ 沈阳音乐节电子门票</div>
                  <div class="item-details">
                    <span class="item-quantity">点击下方“查看详情”查看入场二维码和观演人信息</span>
                  </div>
                </div>
                <div class="item-subtotal">¥{{ order.totalAmount?.toFixed(2) }}</div>
              </div>
            </template>
          </div>

          <div class="order-footer">
            <div class="order-total">
              <span class="label">订单总额：</span>
              <span class="total-amount">¥{{ order.totalAmount?.toFixed(2) }}</span>
            </div>

            <div v-if="order.shippingAddress" class="shipping-info">
              <span class="label">收货地址：</span>
              <span class="address">{{ order.shippingAddress }}</span>
            </div>

            <div v-if="order.trackingNumber && order.status === 'shipped'" class="tracking-info">
              <span class="label">物流单号：</span>
              <span class="tracking-number">{{ order.trackingNumber }}</span>
              <el-button type="primary" text size="small" @click="handleCopyTracking(order.trackingNumber)">
                复制
              </el-button>
            </div>

            <div class="order-actions" v-if="false">
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

    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { orderApi, OrderDTO } from '@/api/order'

const router = useRouter()
const orders = ref<OrderDTO[]>([])
const activeTab = ref('all')
const showDetailsDialog = ref(false)
const showTrackingDialog = ref(false)
const selectedOrder = ref<OrderDTO | null>(null)
const loading = ref(false)

const allOrders = computed(() => orders.value)
const productOrders = computed(() => orders.value.filter(o => o.orderType === 'product'))
const ticketOrders = computed(() => orders.value.filter(o => o.orderType === 'ticket')) // 新增：票务分类
const pendingOrders = computed(() => orders.value.filter(o => o.status === 'paid'))
const shippedOrders = computed(() => orders.value.filter(o => o.status === 'shipped'))
const completedOrders = computed(() => orders.value.filter(o => o.status === 'completed'))

const displayedOrders = computed(() => {
  switch (activeTab.value) {
    case 'ticket': return ticketOrders.value // 新增过滤映射
    case 'product': return productOrders.value
    case 'pending': return pendingOrders.value
    case 'shipped': return shippedOrders.value
    case 'completed': return completedOrders.value
    default: return allOrders.value
  }
})

const loadOrders = async () => {
  loading.value = true
  try {
    // 核心修改：调用刚写好的 getAllOrders 接口！
    const response: any = await orderApi.getAllOrders()
    if (response.code === 200) {
      orders.value = response.data || []
    } else {
      ElMessage.error(response.message || '加载订单失败')
    }
  } catch (error) {
    ElMessage.error('加载订单失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {}
const handleBackToHome = () => { router.push('/') }
const handleViewDetails = (order: OrderDTO) => { selectedOrder.value = order; showDetailsDialog.value = true }
const handleCloseDetails = () => { showDetailsDialog.value = false; selectedOrder.value = null }
const handleTrackOrder = (order: OrderDTO) => { selectedOrder.value = order; showDetailsDialog.value = false; showTrackingDialog.value = true }
const handleCloseTracking = () => { showTrackingDialog.value = false; selectedOrder.value = null }
const handleCopyTracking = (trackingNumber: string) => { navigator.clipboard.writeText(trackingNumber); ElMessage.success('物流单号已复制') }

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
    paid: '已支付/待发货', // 票务一般是已支付，商品是待发货
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
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const formatDateTime = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
/* 样式保留你原本的所有 CSS 即可 */
.orders-container { min-height: 100vh; background: #f5f5f5; }
.orders-header { background: white; padding: 20px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); position: sticky; top: 0; z-index: 100; }
.header-content { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.orders-header h1 { color: #333; margin: 0; font-size: 28px; font-weight: bold; }
.orders-content { max-width: 1200px; margin: 0 auto; padding: 30px 20px; }
.tabs-section { background: white; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
.tabs-section :deep(.el-tabs) { padding: 0; }
.tabs-section :deep(.el-tabs__header) { margin: 0; border-bottom: 1px solid #e0e0e0; }
.tab-count { color: #999; font-size: 12px; margin-left: 4px; }
.empty-state { background: white; border-radius: 8px; padding: 60px 20px; text-align: center; }
.orders-list { display: flex; flex-direction: column; gap: 20px; }
.order-card { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); overflow: hidden; transition: box-shadow 0.3s ease; }
.order-card:hover { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); }
.order-header { display: flex; justify-content: space-between; align-items: center; padding: 20px; border-bottom: 1px solid #e0e0e0; background: #fafafa; }
.order-info { display: flex; gap: 30px; align-items: center; }
.order-id { font-weight: bold; color: #333; font-size: 14px; }
.order-type { color: #666; font-size: 14px; }
.order-date { color: #999; font-size: 12px; }
.order-status { display: flex; align-items: center; }
.order-items { padding: 20px; border-bottom: 1px solid #e0e0e0; }
.order-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; }
.order-item:not(:last-child) { border-bottom: 1px solid #f0f0f0; padding-bottom: 15px; margin-bottom: 15px; }
.item-info { flex: 1; }
.item-name { font-weight: bold; color: #333; margin-bottom: 8px; font-size: 14px; }
.item-details { display: flex; gap: 20px; color: #666; font-size: 12px; }
.item-quantity, .item-price { display: flex; align-items: center; }
.item-subtotal { font-weight: bold; color: #e74c3c; font-size: 14px; min-width: 100px; text-align: right; }
.order-footer { padding: 20px; background: #fafafa; }
.order-total { display: flex; justify-content: flex-end; align-items: center; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #e0e0e0; }
.order-total .label { color: #666; margin-right: 10px; }
.total-amount { font-weight: bold; color: #e74c3c; font-size: 16px; }
.order-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 15px; }
</style>