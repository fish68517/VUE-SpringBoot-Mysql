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
        
        <div class="order-items">
          <div v-for="detail in order.orderDetails" :key="detail.id" class="order-item">
            <el-image
                :src="getImageUrl(detail.recipe?.image)"
                fit="cover"
                class="item-image"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><PictureIcon /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="item-info">
              <h4>{{ detail.recipe.name }}</h4>
              <p>数量：{{ detail.quantity }}</p>
              <p>单价：¥{{ detail.price }}</p>
            </div>
          </div>
        </div>
        
        <div class="order-footer">
          <span class="total">总计：¥{{ order.totalAmount }}</span>
          <div class="actions">
            <el-button 
              v-if="order.status === '待付款'"
              type="primary"
              @click="payOrder(order)"
            >
              立即支付
            </el-button>
            <el-button 
              v-if="order.status === '待付款'"
              @click="cancelOrder(order)"
            >
              取消订单
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

export default {
  setup() {
    const orders = ref([])

    const getOrders = async () => {
      try {
        const data = await orderApi.getOrderList()
        orders.value = data
      } catch (error) {
        ElMessage.error('获取订单失败')
      }
    }

    const getStatusType = (status) => {
      const types = {
        '待付款': 'warning',
        '已付款': 'primary',
        '已完成': 'success',
        '已取消': 'info'
      }
      return types[status] || 'info'
    }

    const payOrder = async (order) => {
      try {
        await ElMessageBox.confirm('确认支付该订单？')
        await orderApi.updateOrderStatus(order.id, '已付款')
        order.status = '已付款'
        ElMessage.success('支付成功')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('支付失败')
        }
      }
    }

    const cancelOrder = async (order) => {
      try {
        await ElMessageBox.confirm('确认取消该订单？')
        await orderApi.updateOrderStatus(order.id, '已取消')
        order.status = '已取消'
        ElMessage.success('订单已取消')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('取消失败')
        }
      }
    }

    onMounted(getOrders)

    return {
      orders,
      getStatusType,
      payOrder,
      cancelOrder,
      getImageUrl
    }
  }
}
</script>

<style scoped>
.orders {
  padding: 20px;
}
.order-card {
  margin-bottom: 20px;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.order-no {
  color: #666;
}
.order-items {
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}
.order-item {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}
.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
}
.item-info {
  flex: 1;
}
.item-info h4 {
  margin: 0 0 5px;
}
.item-info p {
  margin: 5px 0;
  color: #666;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}
.total {
  font-size: 16px;
  font-weight: bold;
}
.actions {
  display: flex;
  gap: 10px;
}
</style> 