<template>
  <div class="shop-products">
    <div class="page-header">
      <h1>商品管理</h1>
      <router-link to="/shop/products/create" class="btn-add">➕ 添加商品</router-link>
    </div>

    <div class="filters">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索商品名称..."
        class="search-input"
      />
      <select v-model="statusFilter" class="status-select">
        <option value="">全部状态</option>
        <option value="0">下架</option>
        <option value="1">上架</option>
        <option value="2">待审核</option>
      </select>
      <button @click="handleSearch" class="btn-search">搜索</button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="products.length > 0" class="products-table">
      <table>
        <thead>
          <tr>
            <th>商品ID</th>
            <th>商品名称</th>
            <th>价格</th>
            <th>库存</th>
            <th>销量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>{{ product.id }}</td>
            <td class="product-name">
              <img v-if="product.image" :src="product.image" :alt="product.name" class="product-thumb" />
              <span>{{ product.name }}</span>
            </td>
            <td>¥{{ product.price }}</td>
            <td>{{ product.stock }}</td>
            <td>{{ product.sales || 0 }}</td>
            <td>
              <span class="status-badge" :class="getStatusClass(product.status)">
                {{ getStatusText(product.status) }}
              </span>
            </td>
            <td class="actions">
              <router-link :to="`/shop/products/edit/${product.id}`" class="btn-edit">编辑</router-link>
              <button @click="handleToggleStatus(product)" class="btn-toggle">
                {{ product.status === 1 ? "下架" : "上架" }}
              </button>
              <button @click="handleDelete(product.id)" class="btn-delete">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else class="empty">暂无商品</div>

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
import { getShopProductList, updateProductStatus, deleteProduct } from "@/api/shop";

const loading = ref(false);
const products = ref([]);
const searchQuery = ref("");
const statusFilter = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

const getStatusText = (status) => {
  const statusMap = {
    0: "下架",
    1: "上架",
    2: "待审核"
  };
  return statusMap[status] || "未知";
};

const getStatusClass = (status) => {
  const classMap = {
    0: "status-offline",
    1: "status-online",
    2: "status-pending"
  };
  return classMap[status] || "";
};

const loadProducts = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    };
    if (searchQuery.value) {
      params.search = searchQuery.value;
    }
    if (statusFilter.value !== "") {
      params.status = statusFilter.value;
    }

    const data = await getShopProductList(params);
    products.value = data?.content || [];
    total.value = data?.total || 0;
  } catch (error) {
    ElMessage.error("加载商品列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadProducts();
};

const handleToggleStatus = async (product) => {
  try {
    const newStatus = product.status === 1 ? 0 : 1;
    await updateProductStatus(product.id, { status: newStatus });
    ElMessage.success(newStatus === 1 ? "商品已上架" : "商品已下架");
    loadProducts();
  } catch (error) {
    ElMessage.error("更新状态失败");
  }
};

const handleDelete = async (productId) => {
  try {
    await ElMessageBox.confirm("确定要删除该商品吗？", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    await deleteProduct(productId);
    ElMessage.success("商品已删除");
    loadProducts();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

watch(currentPage, () => {
  loadProducts();
});

onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
.shop-products {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
}

.btn-add {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-add:hover {
  background: #5568d3;
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

.search-input,
.status-select {
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

.products-table {
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

.product-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-thumb {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.status-online {
  background: #27ae60;
}

.status-offline {
  background: #95a5a6;
}

.status-pending {
  background: #f39c12;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit,
.btn-toggle,
.btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-edit {
  background: #3498db;
  color: white;
  text-decoration: none;
  display: inline-block;
}

.btn-edit:hover {
  background: #2980b9;
}

.btn-toggle {
  background: #f39c12;
  color: white;
}

.btn-toggle:hover {
  background: #e67e22;
}

.btn-delete {
  background: #e74c3c;
  color: white;
}

.btn-delete:hover {
  background: #c0392b;
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
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

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

  .btn-edit,
  .btn-toggle,
  .btn-delete {
    width: 100%;
  }
}
</style>
