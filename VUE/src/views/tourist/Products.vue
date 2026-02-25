<template>
  <div class="products-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>旅游商品</span>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索商品"
            @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="i in 6" :key="i">
          <el-card class="product-card">
            <div class="product-image">
              <el-image 
                src="https://via.placeholder.com/300x200" 
                fit="cover"
                style="width: 100%; height: 200px"
              />
            </div>
            <h3>商品名称 {{ i }}</h3>
            <p class="description">商品描述信息</p>
            <div class="stock">库存: {{ 10 + i }}</div>
            <div class="price">¥{{ 50 + i * 10 }}</div>
            <el-button type="primary" size="small" style="width: 100%">
              查看详情
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: center"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const searchForm = ref({
  keyword: ''
})

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 60
})

const handleSearch = () => {
  pagination.value.currentPage = 1
}
</script>

<style scoped>
.products-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}

.product-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.product-image {
  margin-bottom: 10px;
  border-radius: 4px;
  overflow: hidden;
}

.product-card h3 {
  margin: 10px 0;
  color: #333;
  font-size: 16px;
}

.description {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.stock {
  color: #999;
  font-size: 12px;
  margin: 5px 0;
}

.price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0;
}
</style>
