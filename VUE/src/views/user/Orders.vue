<template>
  <div class="orders">
    <h2>我的订单</h2>

    <div v-if="orders.length === 0" class="empty-orders">
      <el-empty description="暂无订单" />
    </div>

    <template v-else>
      <el-card v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <el-tag :type="getStatusType(order.status)">{{ order.status }}</el-tag>
        </div>

        <div class="pickup-code" v-if="shouldShowPickupCode(order)">
          取餐码：<b>{{ order.pickupCode || '-' }}</b>
        </div>
        <div class="pickup-code" v-if="order.remark">订单备注：{{ order.remark }}</div>

        <div class="order-items">
          <div v-for="detail in order.orderDetails" :key="detail.id" class="order-item">
            <el-image :src="getImageUrl(detail.recipe?.image)" fit="cover" class="item-image" />
            <div class="item-info">
              <h4>{{ detail.recipe?.name }}</h4>
              <p>数量：{{ detail.quantity }}</p>
            </div>
          </div>
        </div>

        <div class="order-footer">
          <span class="total">总计：￥{{ order.totalAmount }}</span>
          <div class="actions">
            <el-button v-if="order.status === STATUS_PENDING" type="primary" @click="payOrder(order)">立即支付</el-button>
            <el-button v-if="order.status === STATUS_PENDING" @click="cancelOrder(order)">取消订单</el-button>

            <el-button :type="[STATUS_FINISHED, STATUS_TAKEN].includes(order.status) ? 'success' : 'default'" disabled>
              已完成
            </el-button>
            <el-button
              v-if="order.status === STATUS_FINISHED"
              type="success"
              @click="takeOrder(order)"
            >
              已取走
            </el-button>
            <el-button
              v-else
              :type="order.status === STATUS_TAKEN ? 'success' : 'default'"
              disabled
            >
              已取走
            </el-button>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/networkApi'
import { getImageUrl } from '@/utils/image'

const STATUS_PENDING = '待付款'
const STATUS_PAID = '已付款'
const STATUS_FINISHED = '已完成'
const STATUS_TAKEN = '已取走'
const STATUS_CANCELED = '已取消'

export default {
  setup() {
    const orders = ref([])

    const getOrders = async () => {
      try {
        const data = await orderApi.getOrderList()
        orders.value = Array.isArray(data) ? data : (data?.data || [])
      } catch (error) {
        ElMessage.error('获取订单失败')
      }
    }

    const getStatusType = (status) => {
      const types = {
        [STATUS_PENDING]: 'warning',
        [STATUS_PAID]: 'primary',
        [STATUS_FINISHED]: 'success',
        [STATUS_TAKEN]: 'success',
        [STATUS_CANCELED]: 'info'
      }
      return types[status] || 'info'
    }

    const shouldShowPickupCode = (order) => {
      return [STATUS_PAID, STATUS_FINISHED].includes(order?.status)
    }

    const payOrder = async (order) => {
      try {
        await ElMessageBox.confirm('确认支付该订单？', '提示', { type: 'warning' })
        await orderApi.updateOrderStatus(order.id, STATUS_PAID)
        order.status = STATUS_PAID
        ElMessage.success('支付成功')
      } catch (error) {
        if (error !== 'cancel') ElMessage.error('支付失败')
      }
    }

    const cancelOrder = async (order) => {
      try {
        await ElMessageBox.confirm('确认取消该订单？', '提示', { type: 'warning' })
        await orderApi.updateOrderStatus(order.id, STATUS_CANCELED)
        order.status = STATUS_CANCELED
        ElMessage.success('取消成功')
      } catch (error) {
        if (error !== 'cancel') ElMessage.error('取消失败')
      }
    }

    const takeOrder = async (order) => {
      try {
        await ElMessageBox.confirm('确认已取走该订单吗？', '提示', { type: 'warning' })
        await orderApi.updateOrderStatus(order.id, STATUS_TAKEN)
        order.status = STATUS_TAKEN
        ElMessage.success('取餐状态已更新')
      } catch (error) {
        if (error !== 'cancel') ElMessage.error('更新失败')
      }
    }

    onMounted(getOrders)

    return {
      orders,
      getStatusType,
      shouldShowPickupCode,
      payOrder,
      cancelOrder,
      takeOrder,
      getImageUrl,
      STATUS_PENDING,
      STATUS_FINISHED,
      STATUS_TAKEN
    }
  }
}
</script>

<style scoped>
.orders { padding: 20px; }
.order-card { margin-bottom: 20px; }
.order-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.pickup-code { margin-bottom: 10px; color: #e67e22; }
.order-item { display: flex; gap: 12px; margin-bottom: 10px; }
.item-image { width: 72px; height: 72px; }
.order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.actions { display: flex; gap: 10px; flex-wrap: wrap; justify-content: flex-end; }
</style>
