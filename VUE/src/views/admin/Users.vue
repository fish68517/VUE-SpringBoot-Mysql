<template>
  <div class="admin-users-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索用户"
            @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
      </el-row>

      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'active'"
              type="warning" 
              size="small"
              @click="handleDisable(row)"
            >
              禁用
            </el-button>
            <el-button 
              v-else
              type="success" 
              size="small"
              @click="handleEnable(row)"
            >
              启用
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
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const API_BASE_URL = 'http://localhost:8080/api'

const searchForm = ref({
  keyword: ''
})

const users = ref([])
const loading = ref(false)

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const page = pagination.value.currentPage - 1
    const size = pagination.value.pageSize
    const response = await fetch(`${API_BASE_URL}/users/admin/list?page=${page}&size=${size}`)
    const data = await response.json()
    
    if (data.code === '0') {
      users.value = data.data.users
      pagination.value.total = data.data.total
    } else {
      ElMessage.error(data.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索用户
const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchUsers()
}

// 禁用用户
const handleDisable = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用用户 "${row.username}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await fetch(`${API_BASE_URL}/users/admin/${row.id}/disable`, {
      method: 'PUT'
    })
    const data = await response.json()
    
    if (data.code === '0') {
      ElMessage.success('用户已禁用')
      fetchUsers()
    } else {
      ElMessage.error(data.message || '禁用用户失败')
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      ElMessage.error('禁用用户失败: ' + error.message)
    }
  }
}

// 启用用户
const handleEnable = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要启用用户 "${row.username}" 吗？`,
      '确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    const response = await fetch(`${API_BASE_URL}/users/admin/${row.id}/enable`, {
      method: 'PUT'
    })
    const data = await response.json()
    
    if (data.code === '0') {
      ElMessage.success('用户已启用')
      fetchUsers()
    } else {
      ElMessage.error(data.message || '启用用户失败')
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      ElMessage.error('启用用户失败: ' + error.message)
    }
  }
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？此操作不可撤销。`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    const response = await fetch(`${API_BASE_URL}/users/admin/${row.id}`, {
      method: 'DELETE'
    })
    const data = await response.json()
    
    if (data.code === '0') {
      ElMessage.success('用户已删除')
      fetchUsers()
    } else {
      ElMessage.error(data.message || '删除用户失败')
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      ElMessage.error('删除用户失败: ' + error.message)
    }
  }
}

// 页码改变
const handlePageChange = () => {
  fetchUsers()
}

// 每页数量改变
const handlePageSizeChange = () => {
  pagination.value.currentPage = 1
  fetchUsers()
}

// 组件挂载时获取用户列表
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-users-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
