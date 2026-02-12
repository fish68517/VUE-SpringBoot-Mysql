<template>
  <div class="student-auth-container">
    <h2>学生认证管理</h2>
    
    <!-- 待认证学生列表 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>待认证学生列表</span>
          <el-button type="primary" @click="loadStudents">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="students" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleApprove(row)">
              通过
            </el-button>
            <el-button type="danger" size="small" @click="handleReject(row)">
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPendingStudents, approveStudent, rejectStudent } from '@/api/schoolAdmin'

const students = ref([])
const loading = ref(false)

const loadStudents = async () => {
  loading.value = true
  try {
    const response = await getPendingStudents()
    students.value = response.data.data || []
  } catch (error) {
    ElMessage.error('加载学生列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return statusMap[status] || 'info'
}

const handleApprove = (row) => {
  ElMessageBox.confirm(
    `确定要通过 ${row.username} 的认证吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await approveStudent(row.id, { status: 'APPROVED' })
      ElMessage.success('认证通过')
      loadStudents()
    } catch (error) {
      ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

const handleReject = (row) => {
  ElMessageBox.prompt(
    '请输入驳回原因',
    '驳回认证',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async ({ value }) => {
    try {
      await rejectStudent(row.id, { status: 'REJECTED', reason: value })
      ElMessage.success('已驳回')
      loadStudents()
    } catch (error) {
      ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

onMounted(() => {
  loadStudents()
})
</script>

<style scoped>
.student-auth-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.box-card {
  margin-bottom: 20px;
}
</style>
