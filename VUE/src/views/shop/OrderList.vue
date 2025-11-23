<template>
  <div class="shop-orders">
    <div class="page-header">
      <h1>订单管理</h1>
    </div>

    <div class="filters">
      <select v-model="statusFilter" class="status-select">
        <option value="">全部状态</option>
        <option value="0">待支付</option>
        <option value="1">待发货</option>
        <option value="2">已发货</option>
        <option value="3">已完成</option>
        <option value="4">已取消</option>
      </select>
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索订单号..."
        class="search-input"
      />
      <button @click="handleSearch" class="btn-search">搜索</button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="orders.length > 0" class="orders-table">
      <table>
        <thead>
          <tr>
            <th>订单号</th>
            <th>买家</th>
            <th>金额</th>
            <th>商品数</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td class="order-no">{{ order.orderNo }}</td>
            <td>{{ order.user?.nickname || "未知用户" }}</td>
            <td>¥{{ order.totalAmount }}</td>
            <td>{{ order.orderItems?.length || 0 }}</td>
            <td>
              <span class="status-badge" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </td>
            <td>{{ formatDate(order.createTime) }}</td>
            <td class="actions">
              <router-link :to="`/shop/orders/${order.id}`" class="btn-view">查看</router-link>
              <button
                v-if="order.status === 1"
                @click="handleShip(order)"
                class="btn-ship"
              >
                发货
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else class="empty">暂无订单</div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <button
        @click="currentPage--"
        :disabled="currentPage === 1"
        class="page-btn"
      >
        上一页
      </button>
      <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
      <button
        @click="currentPage++"
        :disabled="currentPage === totalPages"
        class="page-btn"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getShopOrderList, shipOrder } from "@/api/shop";
import { useUserStore } from "@/store/userStore";

const loading = ref(false);
const orders = ref([]);
const searchQuery = ref("");
const statusFilter = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const userStore = useUserStore();

const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

const getStatusText = (status) => {
  const statusMap = {
    0: "待支付",
    1: "待发货",
    2: "已发货",
    3: "已完成",
    4: "已取消"
  };
  return statusMap[status] || "未知";
};

const getStatusClass = (status) => {
  const classMap = {
    0: "status-pending",
    1: "status-unpaid",
    2: "status-shipped",
    3: "status-completed",
    4: "status-cancelled"
  };
  return classMap[status] || "";
};

const formatDate = (date) => {
  if (!date) return "-";
  return new Date(date).toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  });
};

const loadOrders = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value -1,
      size: pageSize.value
    };
    if (searchQuery.value) {
      params.search = searchQuery.value;
    }
    if (statusFilter.value !== "") {
      params.status = statusFilter.value;
    }

    // 添加shopId
    params.shopId = userStore.userInfo?.shopId;
    const data = await getShopOrderList(params);
    orders.value = data || [];
    total.value = data?.total || 0;
  } catch (error) {
    ElMessage.error("加载订单列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadOrders();
};

const handleShip = async (order) => {
  try {
    await ElMessageBox.confirm("确定要发货吗？", "确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    await shipOrder(order.id, {});
    ElMessage.success("发货成功");
    loadOrders();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("发货失败");
    }
  }
};

watch(currentPage, () => {
  loadOrders();
});

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.shop-orders {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
}

.filters {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-select,
.search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

.btn-search {
  padding: 8px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-search:hover {
  background: #5568d3;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.orders-table {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #f9f9f9;
  border-bottom: 2px solid #eee;
}

th {
  padding: 15px;
  text-align: left;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

td {
  padding: 15px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
  color: #333;
}

tbody tr:hover {
  background: #f9f9f9;
}

.order-no {
  font-weight: 500;
  color: #667eea;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.status-pending {
  background: #f39c12;
}

.status-unpaid {
  background: #e74c3c;
}

.status-shipped {
  background: #3498db;
}

.status-completed {
  background: #27ae60;
}

.status-cancelled {
  background: #95a5a6;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-view,
.btn-ship {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-view {
  background: #3498db;
  color: white;
  text-decoration: none;
  display: inline-block;
}

.btn-view:hover {
  background: #2980b9;
}

.btn-ship {
  background: #27ae60;
  color: white;
}

.btn-ship:hover {
  background: #229954;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
  padding: 20px;
}

.page-btn {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #5568d3;
}

.page-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
  }

  .search-input {
    min-width: auto;
  }

  table {
    font-size: 12px;
  }

  th,
  td {
    padding: 10px;
  }

  .actions {
    flex-direction: column;
  }

  .btn-view,
  .btn-ship {
    width: 100%;
  }
}
</style>
