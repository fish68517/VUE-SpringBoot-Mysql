<template>
  <div class="admin-routes-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>路线管理</span>
          <el-button type="primary" size="small" style="float: right">
            新增路线
          </el-button>
        </div>
      </template>
      
      <el-table :data="routes" stripe>
        <el-table-column prop="id" label="路线ID" width="80" />
        <el-table-column prop="name" label="路线名称" />
        <el-table-column prop="durationDays" label="天数" width="80" />
        <el-table-column prop="totalPrice" label="总价" width="100" />
        <el-table-column prop="attractionCount" label="景点数" width="80" />
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

const routes = ref([
  { id: 1, name: '广州经典3日游', durationDays: 3, totalPrice: 800, attractionCount: 5 },
  { id: 2, name: '广州美食5日游', durationDays: 5, totalPrice: 1200, attractionCount: 8 }
])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 2
})
</script>

<style scoped>
.admin-routes-container {
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
