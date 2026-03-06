<template>
  <div class="product-card" @click="goToDetail">
    <div class="product-image">
      <img :src="getImageUrl(product.image)" :alt="product.name" />
      <div v-if="product.stock === 0" class="out-of-stock">缺货</div>
    </div>
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ product.description }}</p>
      <div class="product-footer">
        <span class="product-price">¥{{ product.price }}</span>
        <span class="product-sales">销量: {{ product.sales }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useRouter, useRoute } from "vue-router"; // 1. 引入 useRoute

// 2. 必须赋值给 props 变量，否则下面的 goToDetail 无法获取 props.product
const props = defineProps({
  product: {
    type: Object,
    required: true
  }
});

const router = useRouter();
const route = useRoute(); // 3. 获取当前路由对象

const goToDetail = () => {
  // 如果当前在 /user 下（登录后），跳转到 /user/product/:id
  if (route.path && route.path.startsWith('/user')) {
    router.push(`/user/product/${props.product.id}`);
  } else {
    // 否则（未登录或在首页），统一尝试跳转到 /user/product/:id
    // 因为我们在路由配置里只定义了带有侧边栏的详情页
    // 路由守卫会自动拦截未登录用户去登录
    router.push(`/user/product/${props.product.id}`);
  }
};

// 处理图片 src:"products/p1.jpg" 加载 springboot目录下的 uploads/products/p1.jpg
const getImageUrl = (src) => {
  console.log("图片路径：" + src);
  return `http://localhost:8080/uploads/${src}`;
};

</script>

<style scoped>
.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.product-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-image {
  position: relative;
  width: 100%;
  padding-bottom: 100%;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.out-of-stock {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
}

.product-info {
  padding: 12px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-description {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b6b;
}

.product-sales {
  font-size: 12px;
  color: #999;
}
</style>