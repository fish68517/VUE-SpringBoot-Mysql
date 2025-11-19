<template>
  <div class="home">
    <div class="home-header">
      <h1>星落宠物用品商城</h1>
      <p>为您的宠物找到最好的用品</p>
    </div>

    <!-- 推荐商品部分 -->
    <section class="section">
      <h2>为您推荐</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="recommendedProducts.length > 0" class="product-grid">
        <ProductCard
          v-for="product in recommendedProducts"
          :key="product.id"
          :product="product"
        />
      </div>
      <div v-else class="empty">暂无推荐商品</div>
    </section>

    <!-- 热门商品部分 -->
    <section class="section">
      <h2>热门商品</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="hotProducts.length > 0" class="product-grid">
        <ProductCard
          v-for="product in hotProducts"
          :key="product.id"
          :product="product"
        />
      </div>
      <div v-else class="empty">暂无热门商品</div>
    </section>

    <!-- 查看更多 -->
    <div class="view-more">
      <router-link to="/products" class="btn-primary">查看所有商品</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import ProductCard from "@/components/ProductCard.vue";
import { getRecommendedProducts, getProductList } from "@/api/product";

const loading = ref(false);
const recommendedProducts = ref([]);
const hotProducts = ref([]);

const loadData = async () => {
  loading.value = true;
  try {
    // 获取推荐商品
    const recommendedData = await getRecommendedProducts({ limit: 8 });
    recommendedProducts.value = Array.isArray(recommendedData) ? recommendedData : [];

    // 获取热门商品（按销量排序）
    const hotData = await getProductList({
      page: 1,
      pageSize: 8,
      sortBy: "sales",
      sortOrder: "desc"
    });
    hotProducts.value = Array.isArray(hotData) ? hotData : [];
  } catch (error) {
    console.error("加载商品失败:", error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f5f5;
}

.home-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 20px;
  text-align: center;
}

.home-header h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
}

.home-header p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.section {
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.section h2 {
  margin: 0 0 20px 0;
  font-size: 24px;
  color: #333;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.loading,
.empty {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 16px;
}

.view-more {
  text-align: center;
  padding: 40px 20px;
}

.btn-primary {
  display: inline-block;
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 4px;
  text-decoration: none;
  font-size: 16px;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .home-header {
    padding: 40px 20px;
  }

  .home-header h1 {
    font-size: 24px;
  }

  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
}
</style>
