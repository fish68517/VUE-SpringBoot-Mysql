import { defineStore } from 'pinia'
import { ref } from 'vue'
import { productAPI } from '@/api/product'

export const useProductsStore = defineStore('products', () => {
  const products = ref([])
  const currentProduct = ref(null)
  const loading = ref(false)
  const error = ref(null)

  async function fetchProducts(filters = {}) {
    loading.value = true
    error.value = null
    try {
      const response = await productAPI.getProducts(filters)
      products.value = Array.isArray(response) ? response : []
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function fetchProductById(id) {
    loading.value = true
    error.value = null
    try {
      const response = await productAPI.getProductById(id)
      currentProduct.value = response
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function createProduct(productData) {
    loading.value = true
    error.value = null
    try {
      const response = await productAPI.createProduct(productData)
      products.value.push(response)
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateProduct(id, productData) {
    loading.value = true
    error.value = null
    try {
      const response = await productAPI.updateProduct(id, productData)
      const index = products.value.findIndex((p) => p.id === id)
      if (index !== -1) {
        products.value[index] = response
      }
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteProduct(id) {
    loading.value = true
    error.value = null
    try {
      await productAPI.deleteProduct(id)
      products.value = products.value.filter((p) => p.id !== id)
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    products,
    currentProduct,
    loading,
    error,
    fetchProducts,
    fetchProductById,
    createProduct,
    updateProduct,
    deleteProduct,
  }
})
