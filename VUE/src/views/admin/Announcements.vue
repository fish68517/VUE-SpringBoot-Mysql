<template>
  <div class="admin-announcements-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" size="small" style="float: right">
            新增公告
          </el-button>
        </div>
      </template>
      
      <el-table :data="announcements" stripe>
        <el-table-column prop="id" label="公告ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" />
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

const announcements = ref([
  { id: 1, title: '系统维护通知', content: '系统将于今晚进行维护', createdAt: '2024-01-01' },
  { id: 2, title: '新景点上线', content: '新增景点已上线', createdAt: '2024-01-02' }
])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 2
})
</script>

<style scoped>
.admin-announcements-container {
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
