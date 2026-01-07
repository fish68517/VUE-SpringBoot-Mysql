<template>
  <div class="product-detail">
    <div class="container">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="product" class="detail-content">
        <!-- 商品图片和基本信息 -->
        <div class="detail-main">
          <!-- 图片轮播 -->
          <div class="image-section">
            <div class="main-image">
              <img :src="currentImage" :alt="product.name" />
            </div>
            <div v-if="images.length > 1" class="thumbnail-images">
              <img
                v-if="false"
                v-for="(img, index) in images"
                :key="index"
                :src="img"
                :alt="`图片${index + 1}`"
                :class="{ active: currentImage === img }"
                @click="currentImage = img"
              />
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="info-section">
            <h1 class="product-name">{{ product.name }}</h1>
            <p class="product-description">{{ product.description }}</p>

            <div class="product-stats">
              <span class="stat">
                <strong>价格：</strong>
                <span class="price">¥{{ product.price }}</span>
              </span>
              <span class="stat">
                <strong>库存：</strong>
                <span :class="{ 'out-of-stock': product.stock === 0 }">
                  {{ product.stock > 0 ? `${product.stock}件` : "缺货" }}
                </span>
              </span>
              <span class="stat">
                <strong>销量：</strong>
                <span>{{ product.sales }}</span>
              </span>
            </div>

            <!-- 购买选项 -->
            <div class="purchase-section">
              <div class="quantity-selector">
                <label>数量：</label>
                <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
                <input v-model.number="quantity" type="number" min="1" />
                <button @click="increaseQuantity" :disabled="quantity >= product.stock">+</button>
              </div>

              <button
                class="btn-add-cart"
                :disabled="product.stock === 0"
                @click="addToCart"
              >
                {{ product.stock === 0 ? "缺货" : "加入购物车" }}
              </button>
            </div>

            <!-- 店铺信息 -->
            <div class="shop-info">
              <h3>店铺信息</h3>
              <p>{{ product.shopName || "未知店铺" }}</p>
            </div>
          </div>
        </div>

        <!-- 商品详情和评价 -->
        <div class="detail-tabs">
          <div class="tabs-header">
            <button
              :class="{ active: activeTab === 'detail' }"
              @click="activeTab = 'detail'"
            >
              商品详情
            </button>
            <button
              :class="{ active: activeTab === 'reviews' }"
              @click="activeTab = 'reviews'"
            >
              评价 ({{ reviewCount }})
            </button>
          </div>

          <!-- 商品详情 -->
          <div v-if="activeTab === 'detail'" class="tab-content">
            <div class="detail-text">
              <h3>商品规格</h3>
              <p>{{ product.description }}</p>
            </div>
          </div>

          <!-- 评价列表 -->
          <div v-if="activeTab === 'reviews'" class="tab-content">
            <div class="reviews-header">
              <button class="btn-write-review" @click="goToReviewForm">写评价</button>
            </div>
            <ReviewList :productId="parseInt(route.params.id)" />
          </div>
        </div>
      </div>
      <div v-else class="empty">商品不存在</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getProductDetail } from "@/api/product";
import { getProductReviews } from "@/api/review";
import { useUserStore } from "@/store/userStore";
import { useCartStore } from "@/store/cartStore";
import ReviewList from "@/components/ReviewList.vue";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const cartStore = useCartStore();

const loading = ref(false);
const product = ref(null);
const quantity = ref(1);
const currentImage = ref("");
const activeTab = ref("detail");
const reviews = ref([]);

const images = computed(() => {
  if (!product.value) return [];
  const imgs = [product.value.image];
  if (product.value.images) {
    try {
      const parsed = JSON.parse(product.value.images);
      if (Array.isArray(parsed)) {
        imgs.push(...parsed);
      }
    } catch (e) {
      // 如果不是有效的JSON，忽略
    }
  }
  return imgs;
});

const reviewCount = computed(() => {
  return reviews.value.length;
});

// 处理图片 src:"products/p1.jpg" 加载 springboot目录下的 uploads/products/p1.jpg
const getImageUrl = (src) => {
  console.log("图片路径：" + src);  // 图片路径：products/p1.jpg
  return `http://localhost:8080/uploads/${src}`;
};

const loadProduct = async () => {
  loading.value = true;
  try {
    const productId = route.params.id;
    const data = await getProductDetail(productId);
    product.value = data;
    currentImage.value = getImageUrl(data.image);

    // 加载评价数据
    try {
      const reviewsData = await getProductReviews(productId);
      reviews.value = reviewsData || [];
    } catch (error) {
      console.error("加载评价失败:", error);
      reviews.value = [];
    }
  } catch (error) {
    console.error("加载商品详情失败:", error);
    ElMessage.error("加载商品详情失败");
  } finally {
    loading.value = false;
  }
};

const increaseQuantity = () => {
  if (quantity.value < product.value.stock) {
    quantity.value++;
  }
};

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const addToCart = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  try {
    await cartStore.addItem(userStore.userInfo.id, product.value.id, quantity.value);
    ElMessage.success(`已添加 ${quantity.value} 件商品到购物车`);
    quantity.value = 1;
  } catch (error) {
    console.error("添加购物车失败:", error);
    ElMessage.error("添加购物车失败");
  }
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN");
};

const goToReviewForm = () => {
  if (!userStore.isLogin) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }
  router.push(`/review/form/${product.value.id}`);
};

onMounted(() => {
  loadProduct();
});
</script>

<style scoped>
.product-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.loading,
.empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.detail-content {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.detail-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 30px;
}

.image-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-images {
  display: flex;
  gap: 10px;
  overflow-x: auto;
}

.thumbnail-images img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  object-fit: cover;
}

.thumbnail-images img:hover,
.thumbnail-images img.active {
  border-color: #667eea;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-name {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.product-description {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.product-stats {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.stat strong {
  color: #666;
}

.price {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: bold;
}

.out-of-stock {
  color: #999;
}

.purchase-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-selector label {
  font-size: 14px;
  color: #666;
}

.quantity-selector button {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.quantity-selector button:hover:not(:disabled) {
  background: #f5f5f5;
}

.quantity-selector button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-selector input {
  width: 60px;
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
  font-size: 14px;
}

.btn-add-cart {
  padding: 12px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-add-cart:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-add-cart:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.shop-info {
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.shop-info h3 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
}

.shop-info p {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.detail-tabs {
  border-top: 1px solid #eee;
}

.tabs-header {
  display: flex;
  border-bottom: 1px solid #eee;
}

.tabs-header button {
  flex: 1;
  padding: 15px;
  border: none;
  background: white;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
}

.tabs-header button.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.tab-content {
  padding: 30px;
}

.detail-text h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #333;
}

.detail-text p {
  margin: 0;
  color: #666;
  line-height: 1.8;
}

.reviews-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.btn-write-review {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-write-review:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .detail-main {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }

  .product-name {
    font-size: 20px;
  }

  .tab-content {
    padding: 20px;
  }
}
</style>
