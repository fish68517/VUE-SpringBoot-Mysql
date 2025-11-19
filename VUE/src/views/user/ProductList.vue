<template>
  <div class="product-list">
    <div class="container">
      <!-- 筛选和搜索区域 -->
      <div class="filter-section">
        <div class="search-box">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索商品..."
            @keyup.enter="handleSearch"
          />
          <button @click="handleSearch">搜索</button>
        </div>

        <div class="filter-controls">
          <div class="filter-group">
            <label>分类：</label>
            <select v-model="selectedCategory" @change="handleCategoryChange">
              <option value="">全部分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>

          <div class="filter-group">
            <label>排序：</label>
            <select v-model="sortBy" @change="handleSortChange">
              <option value="">默认排序</option>
              <option value="price_asc">价格低到高</option>
              <option value="price_desc">价格高到低</option>
              <option value="sales">销量最高</option>
              <option value="newest">最新上架</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="products-section">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="products.length > 0" class="product-grid">
          <ProductCard
            v-for="product in products"
            :key="product.id"
            :product="product"
          />
        </div>
        <div v-else class="empty">暂无商品</div>
      </div>

      <!-- 分页 -->
      <Pagination
        v-if="products.length > 0"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @page-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import ProductCard from "@/components/ProductCard.vue";
import Pagination from "@/components/Pagination.vue";
import {
  getProductList,
  searchProducts,
  getProductsByCategory,
  getCategories
} from "@/api/product";

const route = useRoute();

const loading = ref(false);
const products = ref([]);
const categories = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);
const searchKeyword = ref("");
const selectedCategory = ref("");
const sortBy = ref("");

const loadCategories = async () => {
  try {
    const data = await getCategories();
    categories.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("加载分类失败:", error);
  }
};

const loadProducts = async () => {
  loading.value = true;
  try {
    let data;
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    };

    // 根据排序条件添加参数
    if (sortBy.value === "price_asc") {
      params.sortBy = "price";
      params.sortOrder = "asc";
    } else if (sortBy.value === "price_desc") {
      params.sortBy = "price";
      params.sortOrder = "desc";
    } else if (sortBy.value === "sales") {
      params.sortBy = "sales";
      params.sortOrder = "desc";
    } else if (sortBy.value === "newest") {
      params.sortBy = "createTime";
      params.sortOrder = "desc";
    }

    // 根据不同条件加载商品
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
      data = await searchProducts(params);
    } else if (selectedCategory.value) {
      data = await getProductsByCategory(selectedCategory.value, params);
    } else {
      data = await getProductList(params);
    }

    // 处理响应数据
    if (data && typeof data === "object") {
      if (Array.isArray(data)) {
        products.value = data;
        total.value = data.length;
      } else if (data.content) {
        products.value = data.content;
        total.value = data.totalElements || 0;
      } else if (data.list) {
        products.value = data.list;
        total.value = data.total || 0;
      }
    } else {
      products.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error("加载商品失败:", error);
    products.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadProducts();
};

const handleCategoryChange = () => {
  currentPage.value = 1;
  searchKeyword.value = "";
  loadProducts();
};

const handleSortChange = () => {
  currentPage.value = 1;
  loadProducts();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  loadProducts();
};

onMounted(() => {
  loadCategories();
  loadProducts();
});
</script>

<style scoped>
.product-list {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.search-box input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-box button {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.search-box button:hover {
  background: #5568d3;
}

.filter-controls {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-group label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.filter-group select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: white;
  cursor: pointer;
}

.products-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.loading,
.empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

@media (max-width: 768px) {
  .filter-controls {
    flex-direction: column;
    gap: 10px;
  }

  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
}
</style>
