<template>
  <div class="cart">
    <el-card>
      <template #header>
        <div class="header">
          <span>我的购物车</span>
          <el-button 
            type="danger" 
            plain 
            size="small" 
            @click="clearCart"
            :disabled="!cartItems.length"
          >
            清空购物车
          </el-button>
        </div>
      </template>

      <!-- 购物车为空时显示 -->
      <el-empty 
        v-if="!cartItems.length" 
        description="购物车还是空的"
      >
        <el-button type="primary" @click="$router.push('/user/home')">
          去选购
        </el-button>
      </el-empty>

      <!-- 购物车列表 -->
      <div v-else class="cart-list">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <el-image 
            :src="getImageUrl(item.recipe?.image)"
            class="recipe-image"
            fit="cover"
          >
            <template #error>
              <div class="image-error">
                <el-icon><PictureIcon /></el-icon>
              </div>
            </template>
          </el-image>

          <div class="recipe-info">
            <h3>{{ item.recipe?.name }}</h3>
            <p class="description">{{ item.recipe?.description }}</p>
          </div>

          <div class="quantity-control">
            <el-input-number 
              v-model="item.quantity" 
              :min="1" 
              :max="99"
              size="small"
              @change="(val) => updateQuantity(item, val)"
            />
          </div>

          <div class="actions">
            <el-button 
              type="danger" 
              link
              @click="removeFromCart(item.id)"
            >
              删除
            </el-button>
          </div>
        </div>

        <!-- 底部操作区 -->
        <div class="cart-footer">
          <div class="total">
            <span>共 {{ totalQuantity }} 件商品</span>
          </div>
          <el-button 
            type="primary" 
            size="large"
            :disabled="!cartItems.length"
            @click="createOrder"
          >
            结算
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture as PictureIcon } from '@element-plus/icons-vue'
import { cartApi, orderApi } from '@/api/networkApi'
import { getImageUrl } from '@/utils/image'

export default {
  components: {
    PictureIcon
  },

  setup() {
    const router = useRouter()
    const cartItems = ref([])

    // 计算总数量
    const totalQuantity = computed(() => {
      return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
    })

    // 获取购物车列表
    const getCartList = async () => {
      try {
        const data = await cartApi.getCartList()
        cartItems.value = data
      } catch (error) {
        ElMessage.error('获取购物车失败')
      }
    }

    // 更新商品数量
    const updateQuantity = async (item, quantity) => {
      try {
        await cartApi.updateCartItem(item.id, { quantity })
        ElMessage.success('数量已更新')
      } catch (error) {
        ElMessage.error('更新失败')
        // 恢复原数量
        getCartList()
      }
    }

    // 删除商品
    const removeFromCart = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
          type: 'warning'
        })
        await cartApi.removeFromCart(id)
        ElMessage.success('已删除')
        getCartList()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    // 清空购物车
    const clearCart = async () => {
      try {
        await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
          type: 'warning'
        })
        await cartApi.clearCart()
        ElMessage.success('购物车已清空')
        cartItems.value = []
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('清空失败')
        }
      }
    }

    // 创建订单
    const createOrder = async () => {
      try {
        const orderData = {
          items: cartItems.value.map(item => ({
            recipeId: item.recipeId,
            quantity: item.quantity
          }))
        }
        await orderApi.createOrder(orderData)
        ElMessage.success('订单已创建')
        // 清空购物车并跳转到订单页
        await cartApi.clearCart()
        router.push('/user/orders')
      } catch (error) {
        ElMessage.error('创建订单失败')
      }
    }

    onMounted(() => {
      getCartList()
    })

    return {
      cartItems,
      totalQuantity,
      updateQuantity,
      removeFromCart,
      clearCart,
      createOrder,
      getImageUrl
    }
  }
}
</script>

<style scoped>
.cart {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.cart-list {
  min-height: 200px;
}
.cart-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}
.cart-item:last-child {
  border-bottom: none;
}
.recipe-image {
  width: 120px;
  height: 120px;
  border-radius: 4px;
  margin-right: 20px;
}
.recipe-info {
  flex: 1;
  margin-right: 20px;
}
.recipe-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
}
.description {
  color: #909399;
  font-size: 14px;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.quantity-control {
  margin: 0 40px;
}
.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
.total {
  margin-right: 20px;
  font-size: 14px;
  color: #606266;
}
.image-error {
  width: 120px;
  height: 120px;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 24px;
  border-radius: 4px;
}
</style> 