<template>
  <div class="admin-comments-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>留言管理</span>
        </div>
      </template>
      
      <el-table :data="comments" stripe v-loading="loading">
        <el-table-column prop="id" label="留言ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="targetType" label="目标类型" width="100">
          <template #default="{ row }">
            {{ row.targetType === 'attraction' ? '景点' : '酒店' }}
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="目标ID" width="80" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="rating" label="评分" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isPinned" label="置顶" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isPinned ? 'success' : 'info'">
              {{ row.isPinned ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'pending'"
              type="success" 
              size="small"
              @click="handleApprove(row)"
              :loading="row.actionLoading"
            >
              批准
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="danger" 
              size="small"
              @click="handleReject(row)"
              :loading="row.actionLoading"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="row.status === 'approved'"
              type="warning" 
              size="small"
              @click="handlePin(row)"
              :loading="row.actionLoading"
            >
              {{ row.isPinned ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
              :loading="row.actionLoading"
            >
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
        @current-change="loadComments"
        @size-change="loadComments"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const API_BASE_URL = 'http://localhost:8080/api'

const comments = ref([])
const loading = ref(false)
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusLabel = (status) => {
  const statusMap = {
    'pending': '待审核',
    'approved': '已批准',
    'rejected': '已拒绝'
  }
  return statusMap[status] || status
}

const loadComments = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `${API_BASE_URL}/comments/admin/pending?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    )
    const data = await response.json()
    
    if (data.code === '0') {
      comments.value = data.data.comments.map(comment => ({
        ...comment,
        actionLoading: false
      }))
      pagination.value.total = data.data.total
    } else {
      ElMessage.error(data.message || '加载留言失败')
    }
  } catch (error) {
    ElMessage.error('加载留言失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row) => {
  row.actionLoading = true
  try {
    const response = await fetch(
      `${API_BASE_URL}/comments/admin/${row.id}/approve`,
      { method: 'PUT' }
    )
    const data = await response.json()
    
    if (data.code === '0') {
      ElMessage.success('留言已批准')
      row.status = 'approved'
    } else {
      ElMessage.error(data.message || '批准失败')
    }
  } catch (error) {
    ElMessage.error('批准失败: ' + error.message)
  } finally {
    row.actionLoading = false
  }
}

const handleReject = async (row) => {
  row.actionLoading = true
  try {
    const response = await fetch(
      `${API_BASE_URL}/comments/admin/${row.id}/reject`,
      { method: 'PUT' }
    )
    const data = await response.json()
    
    if (data.code === '0') {
      ElMessage.success('留言已拒绝')
      row.status = 'rejected'
    } else {
      ElMessage.error(data.message || '拒绝失败')
    }
  } catch (error) {
    ElMessage.error('拒绝失败: ' + error.message)
  } finally {
    row.actionLoading = false
  }
}

const handlePin = async (row) => {
  row.actionLoading = true
  try {
    const endpoint = row.isPinned ? 'unpin' : 'pin'
    const response = await fetch(
      `${API_BASE_URL}/comments/admin/${row.id}/${endpoint}`,
      { method: 'PUT' }
    )
    const data = await response.json()
    
    if (data.code === '0') {
      ElMessage.success(row.isPinned ? '已取消置顶' : '已置顶')
      row.isPinned = !row.isPinned
    } else {
      ElMessage.error(data.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + error.message)
  } finally {
    row.actionLoading = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条留言吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    row.actionLoading = true
    const response = await fetch(
      `${API_BASE_URL}/comments/admin/${row.id}`,
      { method: 'DELETE' }
    )
    const data = await response.json()
    
    if (data.code === '0') {
      ElMessage.success('留言已删除')
      await loadComments()
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message)
    }
  } finally {
    row.actionLoading = false
  }
}

onMounted(() => {
  loadComments()
})
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


<style scoped>
.admin-comments-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
