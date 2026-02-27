<template>
  <div class="cart-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的购物车</span>
          <el-button v-if="cartStore.items.length > 0" type="danger" text @click="clearAll">清空购物车</el-button>
        </div>
      </template>

      <div v-if="cartStore.items.length === 0" class="empty-cart">
        <el-empty description="购物车空空如也，快去挑选农资吧~">
          <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
        </el-empty>
      </div>

      <div v-else>
        <el-table :data="cartStore.items" style="width: 100%">
          <el-table-column label="商品信息" min-width="200">
            <template #default="{ row }">
              <div class="product-info">
                <strong>{{ row.product.productName }}</strong>
                <span class="category-tag">{{ row.product.category }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.product.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="200">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" :max="row.product.stock" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="subtotal">¥{{ (row.product.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作（删除）" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="danger" icon="Delete" circle @click="removeItem(row.product.id)" />
            </template>
          </el-table-column>
        </el-table>

        <div class="address-section">
          <h3>收货信息</h3>
          <el-input 
            v-model="deliveryAddress" 
            placeholder="请输入您的详细收货地址 (例如: 河南省郑州市中原区建设路1号)" 
            clearable 
            size="large"
          >
            <template #prepend>送货地址</template>
          </el-input>
        </div>

        <div class="cart-footer">
          <div class="total-section">
            共 <span class="highlight">{{ cartStore.totalItems }}</span> 件商品，
            总计：<span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" :loading="isPaying" @click="handleCheckout">
            {{ isPaying ? '正在生成订单并支付...' : '立即支付' }}
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { orderAPI } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

const isPaying = ref(false)
const deliveryAddress = ref('') // 收货地址

const removeItem = (id) => {
  cartStore.removeFromCart(id)
}

const clearAll = () => {
  ElMessageBox.confirm('确定要清空购物车吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    cartStore.clearCart()
  }).catch(() => {})
}

// 真实的下单与支付逻辑
const handleCheckout = async () => {
  if (!deliveryAddress.value.trim()) {
    ElMessage.warning('请先填写收货地址！')
    return
  }

  // 从 localStorage 获取当前登录的用户ID
  const userStr = localStorage.getItem('user')
  const userId = userStr ? JSON.parse(userStr).userId : null
  
  if (!userId) {
    ElMessage.error('无法获取用户信息，请重新登录')
    router.push('/login')
    return
  }

  isPaying.value = true

  try {
    // 遍历购物车中的每一个商品，为其创建订单
    for (const item of cartStore.items) {
      
      // 1. 调用后端：创建订单
      const createRes = await orderAPI.createOrder({
        userId: userId,
        productId: item.product.id,
        quantity: item.quantity,
        deliveryAddress: deliveryAddress.value
      })
      
      // 获取后端返回的刚创建好的订单ID
      // 注意：这里需要根据你 axios 拦截器的实际返回结构调整，如果是直接返回 data 对象则不需要 .data
      const orderId = createRes.data ? createRes.data.id : createRes.id

      if (orderId) {
        // 2. 调用后端：模拟支付，将状态更新为 PAID
        await orderAPI.payOrder(orderId, {
          paymentMethod: '在线支付(模拟)'
        })
      }
    }

    ElMessage.success('支付成功！订单已生成，等待商家发货。')
    
    // 清空购物车
    cartStore.clearCart()
    
    // 跳转到订单页面查看
    router.push('/orders')

  } catch (error) {
    console.error('结账失败:', error)
    ElMessage.error('下单或支付过程中出现错误，请检查库存或稍后重试。')
  } finally {
    isPaying.value = false
  }
}
</script>

<style scoped>
.cart-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.empty-cart { padding: 40px 0; }
.product-info { display: flex; flex-direction: column; }
.category-tag { font-size: 12px; color: #909399; margin-top: 4px; }
.price, .subtotal { color: #f56c6c; font-weight: bold; }

/* 收货地址区域样式 */
.address-section {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}
.address-section h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
}

.cart-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
.total-section { font-size: 16px; }
.highlight { color: #409eff; font-weight: bold; }
.total-price { color: #f56c6c; font-size: 24px; font-weight: bold; }
</style>