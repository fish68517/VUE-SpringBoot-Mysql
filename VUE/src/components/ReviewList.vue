<template>
  <div class="review-list">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="reviews.length > 0" class="reviews">
      <div v-for="review in reviews" :key="review.id" class="review-item">
        <div class="review-header">
          <div class="reviewer-info">
            <img v-if="review.userAvatar" :src="review.userAvatar" :alt="review.userNickname" class="avatar" />
            <div v-else class="avatar-placeholder">{{ review.userNickname?.charAt(0) || 'U' }}</div>
            <span class="reviewer-name">{{ review.userNickname || '匿名用户' }}</span>
          </div>
          <div class="review-rating">
            <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= review.rating }">★</span>
            <span class="rating-text">({{ review.rating }}分)</span>
          </div>
        </div>
        <p class="review-content">{{ review.content }}</p>
        <div v-if="review.images" class="review-images">
          <img
            v-for="(img, index) in parseImages(review.images)"
            :key="index"
            :src="img"
            :alt="`评价图片${index + 1}`"
            class="review-image"
          />
        </div>
        <p class="review-time">{{ formatDate(review.createTime) }}</p>
      </div>
    </div>
    <div v-else class="empty">暂无评价</div>
  </div>
</template>

<script setup>
import { defineProps, ref, onMounted } from "vue";
import { getProductReviews } from "@/api/review";

defineProps({
  productId: {
    type: Number,
    required: true
  }
});

const loading = ref(false);
const reviews = ref([]);

const loadReviews = async () => {
  loading.value = true;
  try {
    const data = await getProductReviews(props.productId);
    reviews.value = data || [];
  } catch (error) {
    console.error("加载评价失败:", error);
    reviews.value = [];
  } finally {
    loading.value = false;
  }
};

const parseImages = (imagesJson) => {
  try {
    if (!imagesJson) return [];
    const parsed = JSON.parse(imagesJson);
    return Array.isArray(parsed) ? parsed : [];
  } catch (e) {
    return [];
  }
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN");
};

onMounted(() => {
  loadReviews();
});
</script>

<style scoped>
.review-list {
  width: 100%;
}

.loading {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}

.empty {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}

.reviews {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
  background: #fafafa;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
}

.reviewer-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: 5px;
}

.star {
  color: #ddd;
  font-size: 14px;
}

.star.filled {
  color: #ffc107;
}

.rating-text {
  font-size: 12px;
  color: #999;
}

.review-content {
  margin: 10px 0;
  color: #333;
  font-size: 14px;
  line-height: 1.6;
}

.review-images {
  display: flex;
  gap: 10px;
  margin: 10px 0;
  flex-wrap: wrap;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
  cursor: pointer;
  transition: all 0.3s ease;
}

.review-image:hover {
  transform: scale(1.05);
}

.review-time {
  margin: 0;
  color: #999;
  font-size: 12px;
}
</style>
