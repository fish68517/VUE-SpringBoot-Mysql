<template>
  <div class="favorites-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="i in 6" :key="i">
          <el-card class="favorite-card">
            <div class="favorite-image">
              <el-image 
                src="https://via.placeholder.com/300x200" 
                fit="cover"
                style="width: 100%; height: 200px"
              />
            </div>
            <h3>收藏项目 {{ i }}</h3>
            <p class="type">类型: 景点</p>
            <el-button type="primary" size="small" style="width: 48%; margin-right: 4%">
              查看
            </el-button>
            <el-button type="danger" size="small" style="width: 48%">
              取消收藏
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="favorites.length === 0" description="暂无收藏" />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const favorites = ref([])
</script>

<style scoped>
.favorites-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}

.favorite-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.favorite-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.favorite-image {
  margin-bottom: 10px;
  border-radius: 4px;
  overflow: hidden;
}

.favorite-card h3 {
  margin: 10px 0;
  color: #333;
  font-size: 16px;
}

.type {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
}
</style>
