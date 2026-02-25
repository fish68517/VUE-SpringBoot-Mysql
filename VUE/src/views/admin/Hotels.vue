<template>
  <div class="admin-hotels-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>酒店管理</span>
          <el-button type="primary" size="small" style="float: right">
            新增酒店
          </el-button>
        </div>
      </template>
      
      <el-table :data="hotels" stripe>
        <el-table-column prop="id" label="酒店ID" width="80" />
        <el-table-column prop="name" label="酒店名称" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="rating" label="评分" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small">编辑</el-button>
            <el-button type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

const hotels = ref([
  { id: 1, name: '五星酒店', location: '广州市天河区', rating: 5 },
  { id: 2, name: '四星酒店', location: '广州市越秀区', rating: 4 }
])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 2
})
</script>

<style scoped>
.admin-hotels-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
