<template>
  <div class="admin-attractions-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>景点管理</span>
          <el-button type="primary" size="small" style="float: right">
            新增景点
          </el-button>
        </div>
      </template>
      
      <el-table :data="attractions" stripe>
        <el-table-column prop="id" label="景点ID" width="80" />
        <el-table-column prop="name" label="景点名称" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="ticketPrice" label="门票价格" />
        <el-table-column prop="isGuangzhouSpecial" label="广州特色">
          <template #default="{ row }">
            <el-tag :type="row.isGuangzhouSpecial ? 'success' : 'info'">
              {{ row.isGuangzhouSpecial ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
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

const attractions = ref([
  { id: 1, name: '陈家祠', location: '广州市越秀区', ticketPrice: 50, isGuangzhouSpecial: true },
  { id: 2, name: '珠江夜游', location: '广州市珠江', ticketPrice: 80, isGuangzhouSpecial: true }
])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 2
})
</script>

<style scoped>
.admin-attractions-container {
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
