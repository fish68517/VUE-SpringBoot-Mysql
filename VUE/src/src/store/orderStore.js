// src/store/orderStore.js
import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { getOrderList, getOrderDetail, createOrder, payOrder, cancelOrder, completeOrder } from "@/api/order";

export const useOrderStore = defineStore("order", () => {
  const orders = ref([]);
  const currentOrder = ref(null);
  const loading = ref(false);

  // 获取订单列表
  async function fetchOrders(userId) {
    loading.value = true;
    try {
      const data = await getOrderList(userId);
      orders.value = data || [];
      return orders.value;
    } catch (error) {
      console.error("获取订单列表失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  }

  // 获取订单详情
  async function fetchOrderDetail(orderId) {
    loading.value = true;
    try {
      const data = await getOrderDetail(orderId);
      currentOrder.value = data;
      return data;
    } catch (error) {
      console.error("获取订单详情失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  }

  // 创建订单
  async function submitOrder(orderData) {
    loading.value = true;
    try {
      const result = await createOrder(orderData);
      orders.value.unshift(result);
      return result;
    } catch (error) {
      console.error("创建订单失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  }

  // 支付订单
  async function pay(orderId) {
    loading.value = true;
    try {
      const result = await payOrder(orderId);
      
      // 更新订单列表中的订单状态
      const order = orders.value.find(o => o.id === orderId);
      if (order) {
        order.status = result.status;
        order.payTime = result.payTime;
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = result;
      }
      
      return result;
    } catch (error) {
      console.error("支付订单失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  }

  // 取消订单
  async function cancel(orderId) {
    loading.value = true;
    try {
      const result = await cancelOrder(orderId);
      
      // 更新订单列表中的订单状态
      const order = orders.value.find(o => o.id === orderId);
      if (order) {
        order.status = result.status;
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = result;
      }
      
      return result;
    } catch (error) {
      console.error("取消订单失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  }

  // 确认收货
  async function complete(orderId) {
    loading.value = true;
    try {
      const result = await completeOrder(orderId);
      
      // 更新订单列表中的订单状态
      const order = orders.value.find(o => o.id === orderId);
      if (order) {
        order.status = result.status;
      }
      
      // 更新当前订单
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = result;
      }
      
      return result;
    } catch (error) {
      console.error("确认收货失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  }

  // 获取订单状态文本
  function getStatusText(status) {
    const statusMap = {
      0: "待支付",
      1: "待发货",
      2: "已发货",
      3: "已完成",
      4: "已取消"
    };
    return statusMap[status] || "未知";
  }

  return {
    orders,
    currentOrder,
    loading,
    fetchOrders,
    fetchOrderDetail,
    submitOrder,
    pay,
    cancel,
    complete,
    getStatusText
  };
});
