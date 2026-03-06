<template>
  <div class="user-approval">
    <h1>用户审核</h1>
    
    <el-card>
      <template #header>
        <div class="card-header">
          <span>待审核用户列表</span>
          <el-button type="primary" @click="loadPendingUsers" :loading="loading">刷新</el-button>
        </div>
      </template>

      <el-table :data="pendingUsers" stripe>
        <el-table-column prop="id" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="role" label="申请角色" width="100" />
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleApprove(row.id)" :loading="approveLoading === row.id">批准</el-button>
            <el-button type="danger" size="small" @click="handleReject(row.id)" :loading="rejectLoading === row.id">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="pendingUsers.length === 0" class="empty-state">
        <p>暂无待审核用户</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminService } from '../../services/adminService'

const loading = ref(false)
const approveLoading = ref(null)
const rejectLoading = ref(null)
const pendingUsers = ref([])

// Load pending users on component mount
onMounted(async () => {
  await loadPendingUsers()
})

// Load pending users
const loadPendingUsers = async () => {
  loading.value = true
  try {
    const response = await adminService.getPendingUsers()
    if (response.data.code === 200) {
      pendingUsers.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '加载待审核用户失败')
    }
  } catch (error) {
    ElMessage.error('加载待审核用户失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// Approve user
const handleApprove = async (userId) => {
  approveLoading.value = userId
  try {
    const response = await adminService.approveUser(userId)
    if (response.data.code === 200) {
      ElMessage.success('用户已批准')
      await loadPendingUsers()
    } else {
      ElMessage.error(response.data.message || '批准失败')
    }
  } catch (error) {
    ElMessage.error('批准失败')
    console.error(error)
  } finally {
    approveLoading.value = null
  }
}

// Reject user
const handleReject = async (userId) => {
  rejectLoading.value = userId
  try {
    const response = await adminService.rejectUser(userId)
    if (response.data.code === 200) {
      ElMessage.success('用户已驳回')
      await loadPendingUsers()
    } else {
      ElMessage.error(response.data.message || '驳回失败')
    }
  } catch (error) {
    ElMessage.error('驳回失败')
    console.error(error)
  } finally {
    rejectLoading.value = null
  }
}
</script>

<style scoped>
.user-approval {
  padding: 20px;
}

.user-approval h1 {
  margin-bottom: 20px;
  color: #333;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
