import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface CartItem {
  id: string
  productId: number
  name: string
  price: number
  quantity: number
  image: string
  specs: Record<string, string>
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])

  const totalItems = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))

  const totalPrice = computed(() => items.value.reduce((sum, item) => sum + item.price * item.quantity, 0))

  const addItem = (product: any, specs: Record<string, string> = {}) => {
    const itemId = `${product.id}-${JSON.stringify(specs)}`
    const existingItem = items.value.find((item) => item.id === itemId)

    if (existingItem) {
      existingItem.quantity++
    } else {
      items.value.push({
        id: itemId,
        productId: product.id,
        name: product.name,
        price: product.currentPrice,
        quantity: 1,
        image: product.images?.[0] || '',
        specs,
      })
    }
    saveToStorage()
  }

  const updateQuantity = (itemId: string, quantity: number) => {
    const item = items.value.find((i) => i.id === itemId)
    if (item) {
      if (quantity <= 0) {
        removeItem(itemId)
      } else {
        item.quantity = quantity
        saveToStorage()
      }
    }
  }

  const removeItem = (itemId: string) => {
    items.value = items.value.filter((item) => item.id !== itemId)
    saveToStorage()
  }

  const clearCart = () => {
    items.value = []
    localStorage.removeItem('cart')
  }

  const saveToStorage = () => {
    localStorage.setItem('cart', JSON.stringify(items.value))
  }

  const loadFromStorage = () => {
    const stored = localStorage.getItem('cart')
    if (stored) {
      items.value = JSON.parse(stored)
    }
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
  }
})
