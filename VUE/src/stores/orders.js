import { defineStore } from 'pinia'
import { ref } from 'vue'
import { orderAPI } from '@/api/order'

export const useOrdersStore = defineStore('orders', () => {
  const orders = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)
  const error = ref(null)

  async function fetchOrders(filters = {}) {
    loading.value = true
    error.value = null
    try {
      const response = await orderAPI.getOrders(filters)
      orders.value = Array.isArray(response) ? response : []
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function fetchOrderById(id) {
    loading.value = true
    error.value = null
    try {
      const response = await orderAPI.getOrderById(id)
      currentOrder.value = response
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function createOrder(orderData) {
    loading.value = true
    error.value = null
    try {
      const response = await orderAPI.createOrder(orderData)
      orders.value.push(response)
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateOrderStatus(id, status) {
    loading.value = true
    error.value = null
    try {
      const response = await orderAPI.updateOrderStatus(id, status)
      const index = orders.value.findIndex((o) => o.id === id)
      if (index !== -1) {
        orders.value[index] = response
      }
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function payOrder(id) {
    loading.value = true
    error.value = null
    try {
      const response = await orderAPI.payOrder(id)
      const index = orders.value.findIndex((o) => o.id === id)
      if (index !== -1) {
        orders.value[index] = response
      }
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    orders,
    currentOrder,
    loading,
    error,
    fetchOrders,
    fetchOrderById,
    createOrder,
    updateOrderStatus,
    payOrder,
  }
})
