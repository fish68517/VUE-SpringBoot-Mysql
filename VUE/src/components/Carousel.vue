<template>
  <div class="carousel-container">
    <el-carousel :interval="5000" type="card" height="400px">
      <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
        <img :src="item.image" :alt="item.title" class="carousel-image" />
        <div class="carousel-overlay">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup lang="ts">
// 1. 删除这一行！defineProps 不需要导入
// import { defineProps } from 'vue' 

interface CarouselItem {
  image: string
  title: string
  description: string
}

// 2. defineProps 只能调用一次，请直接赋值给一个变量（通常命名为 props）
const props = defineProps<{
  items: CarouselItem[]
}>()


</script>

<style scoped>
.carousel-container {
  width: 100%;
  margin-bottom: 30px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  color: white;
  padding: 20px;
  text-align: left;
}

.carousel-overlay h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  font-weight: bold;
}

.carousel-overlay p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

:deep(.el-carousel__item) {
  position: relative;
}
</style>
