<template>
  <div class="admin-comments-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>留言管理</span>
        </div>
      </template>
      
      <el-table :data="comments" stripe>
        <el-table-column prop="id" label="留言ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="rating" label="评分" width="80" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'pending'"
              type="success" 
              size="small"
              @click="handleApprove(row)"
            >
              批准
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="danger" 
              size="small"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="row.status === 'approved'"
              type="warning" 
              size="small"
              @click="handlePin(row)"
            >
              {{ row.isPinned ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
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

const comments = ref([
  { id: 1, userId: 1, content: '很好的景点', rating: 5, status: 'pending', isPinned: false },
  { id: 2, userId: 2, content: '不错的体验', rating: 4, status: 'approved', isPinned: false }
])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 2
})

const getStatusType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return statusMap[status] || 'info'
}

const handleApprove = (row) => {
  row.status = 'approved'
}

const handleReject = (row) => {
  row.status = 'rejected'
}

const handlePin = (row) => {
  row.isPinned = !row.isPinned
}

const handleDelete = (row) => {
  const index = comments.value.indexOf(row)
  if (index > -1) {
    comments.value.splice(index, 1)
  }
}
</script>

<style scoped>
.admin-comments-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
