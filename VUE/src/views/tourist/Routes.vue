<template>
  <div class="routes-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>推荐路线</span>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input-number 
            v-model="filterForm.days" 
            :min="1" 
            :max="30"
            placeholder="旅游天数"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleSearch">搜索推荐</el-button>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="12" v-for="i in 4" :key="i">
          <el-card class="route-card">
            <h3>推荐路线 {{ i }}</h3>
            <div class="route-info">
              <div class="info-item">
                <span class="label">天数:</span>
                <span>{{ 2 + i }} 天</span>
              </div>
              <div class="info-item">
                <span class="label">价格:</span>
                <span class="price">¥{{ 500 + i * 100 }}</span>
              </div>
              <div class="info-item">
                <span class="label">景点数:</span>
                <span>{{ 3 + i }} 个</span>
              </div>
            </div>
            <el-button type="primary" size="small" style="width: 100%; margin-top: 10px">
              查看详情
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const filterForm = ref({
  days: 3
})

const handleSearch = () => {
  // Search for recommended routes
}
</script>

<style scoped>
.routes-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}

.route-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.route-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.route-card h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
}

.route-info {
  margin-bottom: 15px;
}

.info-item {
  margin: 8px 0;
  display: flex;
  justify-content: space-between;
}

.label {
  font-weight: bold;
  color: #666;
}

.price {
  color: #ff6b6b;
  font-weight: bold;
  font-size: 16px;
}
</style>
