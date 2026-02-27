import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'

export const useCartStore = defineStore('cart', () => {
  // 从 localStorage 初始化购物车数据，防止刷新页面后丢失
  const savedCart = localStorage.getItem('cartItems')
  const items = ref(savedCart ? JSON.parse(savedCart) : [])

  // 监听 items 变化，自动保存到 localStorage
  watch(items, (newItems) => {
    localStorage.setItem('cartItems', JSON.stringify(newItems))
  }, { deep: true })

  // 计算属性：购物车商品总数量
  const totalItems = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // 计算属性：购物车总价格
  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + (item.product.price * item.quantity), 0)
  })

  // 动作：添加到购物车
  const addToCart = (product) => {
    const existingItem = items.value.find(item => item.product.id === product.id)
    if (existingItem) {
      // 如果购物车已存在且数量未超过库存，则数量 +1
      if (existingItem.quantity < product.stock) {
        existingItem.quantity++
      }
    } else {
      // 新商品加入购物车，默认数量为 1
      items.value.push({
        product: product,
        quantity: 1
      })
    }
  }

  // 动作：从购物车移除
  const removeFromCart = (productId) => {
    items.value = items.value.filter(item => item.product.id !== productId)
  }

  // 动作：清空购物车（支付成功后调用）
  const clearCart = () => {
    items.value = []
  }

  return { 
    items, 
    totalItems, 
    totalPrice, 
    addToCart, 
    removeFromCart, 
    clearCart 
  }
})