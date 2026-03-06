// src/store/cartStore.js
import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { getCartList, addToCart, updateCartQuantity, deleteCartItem, clearCart } from "@/api/cart";

export const useCartStore = defineStore("cart", () => {
  const cartItems = ref([]);
  const loading = ref(false);

  // 计算购物车商品总数
  const totalCount = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.quantity, 0);
  });

  // 计算购物车总金额
  const totalAmount = computed(() => {
    return cartItems.value.reduce((sum, item) => {
      return sum + (item.subtotal || 0);
    }, 0);
  });

  // 获取购物车列表
  async function fetchCart(userId) {
    loading.value = true;
    try {
      const data = await getCartList(userId);
      cartItems.value = data || [];
      return cartItems.value;
    } catch (error) {
      console.error("获取购物车失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  }

  // 添加商品到购物车
  async function addItem(userId, productId, quantity) {
    try {
      const result = await addToCart({
        userId,
        productId,
        quantity
      });
      
      // 检查是否已存在该商品
      const existingItem = cartItems.value.find(item => item.productId === productId);
      if (existingItem) {
        existingItem.quantity += quantity;
        existingItem.subtotal = existingItem.productPrice * existingItem.quantity;
      } else {
        cartItems.value.push(result);
      }
      
      return result;
    } catch (error) {
      console.error("添加购物车失败:", error);
      throw error;
    }
  }

  // 更新购物车商品数量
  async function updateQuantity(cartId, quantity) {
    try {
      const result = await updateCartQuantity(cartId, quantity);
      
      const item = cartItems.value.find(item => item.id === cartId);
      if (item) {
        item.quantity = quantity;
        item.subtotal = item.productPrice * quantity;
      }
      
      return result;
    } catch (error) {
      console.error("更新购物车失败:", error);
      throw error;
    }
  }

  // 删除购物车商品
  async function removeItem(cartId) {
    try {
      await deleteCartItem(cartId);
      cartItems.value = cartItems.value.filter(item => item.id !== cartId);
    } catch (error) {
      console.error("删除购物车商品失败:", error);
      throw error;
    }
  }

  // 清空购物车
  async function clear(userId) {
    try {
      await clearCart(userId);
      cartItems.value = [];
    } catch (error) {
      console.error("清空购物车失败:", error);
      throw error;
    }
  }

  // 获取选中的商品
  function getSelectedItems(selectedIds) {
    return cartItems.value.filter(item => selectedIds.includes(item.id));
  }

  // 计算选中商品的总金额
  function calculateSelectedTotal(selectedIds) {
    return cartItems.value
      .filter(item => selectedIds.includes(item.id))
      .reduce((sum, item) => sum + (item.subtotal || 0), 0);
  }

  return {
    cartItems,
    loading,
    totalCount,
    totalAmount,
    fetchCart,
    addItem,
    updateQuantity,
    removeItem,
    clear,
    getSelectedItems,
    calculateSelectedTotal
  };
});
