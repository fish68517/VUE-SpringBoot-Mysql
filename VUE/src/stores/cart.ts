import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/api/request' // 引入全局请求实例
import { ElMessage } from 'element-plus'

export interface CartItem {
  id: number // 现在是数据库中的 cart_items 表的主键
  productId: number
  productName: string
  price: number
  quantity: number
  image: string
  subtotal: number
  specs?: Record<string, string>
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const totalItems = ref(0)
  const totalPrice = ref(0)

  // 1. 从后端数据库加载购物车数据
  const loadCart = async () => {
    try {
      // 调用后端的 GET /api/cart
      const res: any = await request.get('/cart')
      if (res && res.code === 200 && res.data) {
        items.value = res.data.items || []
        totalItems.value = res.data.totalQuantity || 0
        totalPrice.value = res.data.totalPrice || 0
      }
    } catch (error) {
      console.error('获取购物车数据失败', error)
    }
  }

  // 2. 调用后端接口，将商品真正存入数据库
  const addItem = async (productId: number, quantity: number) => {
    try {
      // 调用后端的 POST /api/cart/add
      console.log('addItem', productId, quantity)
      const res: any = await request.post('/cart/add', {
        productId: productId,
        quantity: quantity
      })
      
      if (res && res.code === 200) {
        // 后端添加成功后，会返回最新的购物车计算结果，直接覆盖前端数据
        items.value = res.data.items || []
        totalItems.value = res.data.totalQuantity || 0
        totalPrice.value = res.data.totalPrice || 0
        return true
      }
      return false
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '添加购物车失败')
      return false
    }
  }

  // 3. 调用后端接口删除购物车项
  const removeItem = async (cartItemId: number | string) => {
    try {
      // 调用后端的 DELETE /api/cart/{id}
      const res: any = await request.delete(`/cart/${cartItemId}`)
      if (res && res.code === 200) {
        items.value = res.data.items || []
        totalItems.value = res.data.totalQuantity || 0
        totalPrice.value = res.data.totalPrice || 0
        return true
      }
      return false
    } catch (error) {
      console.error('删除商品失败', error)
      return false
    }
  }

 // 4. 真正调用后端接口更新数量
  const updateQuantity = async (cartItemId: number | string, quantity: number) => {
    // 防御性处理：如果前端传0或负数，直接调用删除
    if (quantity <= 0) {
      return await removeItem(cartItemId)
    }

    try {
      // 调用后端的 PUT /api/cart/{id}
      const res: any = await request.put(`/cart/${cartItemId}`, {
        quantity: quantity
      })
      
      if (res && res.code === 200) {
        // 更新成功，把后端重新计算的总价和数组直接覆盖前端
        items.value = res.data.items || []
        totalItems.value = res.data.totalQuantity || 0
        totalPrice.value = res.data.totalPrice || 0
        return true
      }
      return false
    } catch (error: any) {
      // 核心体验优化：如果后端报错（比如库存不足），把真实的购物车数据重新拉回来，让前端页面乱改的数字恢复原状
      ElMessage.error(error.response?.data?.message || '更新数量失败')
      await loadCart() 
      return false
    }
  }

  const clearCart = () => {
    items.value = []
    totalItems.value = 0
    totalPrice.value = 0
  }

  // 为了兼容你之前代码里写在 onMounted 里的 loadFromStorage() 调用，我们做一个别名
  const loadFromStorage = () => {
    loadCart()
  }

  return {
    items,
    totalItems,
    totalPrice,
    addItem,
    updateQuantity,
    removeItem,
    clearCart,
    loadFromStorage,
    loadCart
  }
})