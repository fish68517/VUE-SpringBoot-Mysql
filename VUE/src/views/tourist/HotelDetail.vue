<template>
  <div class="detail-container">
    <el-card>
      <el-row :gutter="20">
        <el-col :xs="24" :md="12">
          <el-image 
            src="https://via.placeholder.com/400x300" 
            fit="cover"
            style="width: 100%; height: 300px"
          />
        </el-col>
        <el-col :xs="24" :md="12">
          <h2>酒店名称</h2>
          <div class="info-item">
            <span class="label">位置:</span>
            <span>广州市</span>
          </div>
          <div class="info-item">
            <span class="label">评分:</span>
            <el-rate v-model="rating" disabled />
          </div>
          <div class="info-item">
            <span class="label">描述:</span>
            <p>酒店详细描述信息</p>
          </div>
          <el-button type="primary" size="large" style="width: 100%; margin-top: 20px">
            预订酒店
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>房间类型</span>
        </div>
      </template>
      <el-table :data="rooms">
        <el-table-column prop="type" label="房间类型" />
        <el-table-column prop="price" label="价格/晚" />
        <el-table-column prop="quantity" label="可用房间数" />
        <el-table-column label="操作">
          <template #default>
            <el-button type="primary" size="small">预订</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const rating = ref(4)

const rooms = ref([
  { type: '单人间', price: 200, quantity: 5 },
  { type: '双人间', price: 300, quantity: 8 },
  { type: '套房', price: 500, quantity: 3 }
])
</script>

<style scoped>
.detail-container {
  padding: 20px;
}

.info-item {
  margin: 15px 0;
  display: flex;
  align-items: flex-start;
}

.label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 80px;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
