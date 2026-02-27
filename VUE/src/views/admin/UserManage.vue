<template>
  <div class="admin-page-container">
    <el-card shadow="never" class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="filters" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="filters.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select v-model="filters.userType" placeholder="选择角色" clearable style="width: 150px">
            <el-option label="普通农户" value="FARMER" />
            
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" icon="Search">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="users" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="userType" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.userType)">{{ formatRole(row.userType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="region" label="所在地区" min-width="120" />
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              @click="handleEdit(row)" 
              :disabled="row.userType === 'ADMIN'">
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)" 
              :disabled="row.userType === 'ADMIN'">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑用户信息" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled placeholder="用户名不可修改" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入新邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入新手机号" />
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="editForm.region" placeholder="请输入新地区" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitLoading">确定保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import apiClient from '@/api/client' // 直接调用底层 axios client

// --- 状态定义 ---
const loading = ref(false)
const submitLoading = ref(false)
const users = ref([])
const filters = ref({ username: '', userType: '' })
const pagination = ref({ current: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const currentUserId = ref(null)
const editForm = ref({ username: '', email: '', phone: '', region: '' })

// --- 辅助方法 ---
const formatRole = (role) => {
  const map = { 'FARMER': '普通农户', 'MERCHANT': '农资商家', 'ADMIN': '系统管理员' }
  return map[role] || role
}
const getRoleTagType = (role) => {
  const map = { 'FARMER': 'success', 'MERCHANT': 'warning', 'ADMIN': 'danger' }
  return map[role] || 'info'
}

// --- 数据获取 (纯前端过滤与分页，因为后端没有提供条件查询接口) ---
const handleSearch = async () => {
  loading.value = true
  try {
    // 假设后端有一个获取所有用户的通用接口 (如果没有，这里需要你在 Controller 加一个)
    const res = await apiClient.get('/users')
    let allUsers = res.data || res || []

    // 前端条件筛选
    if (filters.value.username) {
      allUsers = allUsers.filter(u => u.username.includes(filters.value.username))
    }
    if (filters.value.userType) {
      allUsers = allUsers.filter(u => u.userType === filters.value.userType)
    }

    // 前端分页计算
    pagination.value.total = allUsers.length
    const startIdx = (pagination.value.current - 1) * pagination.value.size
    const endIdx = startIdx + pagination.value.size
    users.value = allUsers.slice(startIdx, endIdx)

  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 分页事件
const handlePageChange = (page) => { pagination.value.current = page; handleSearch() }
const handlePageSizeChange = (size) => { pagination.value.size = size; pagination.value.current = 1; handleSearch() }

// --- 增删改操作 ---
const handleEdit = (row) => {
  currentUserId.value = row.id
  editForm.value = {
    username: row.username,
    email: row.email || '',
    phone: row.phone || '',
    region: row.region || ''
  }
  dialogVisible.value = true
}

const submitEdit = async () => {
  submitLoading.value = true
  try {
    // 调用之前你已经改好的 userUpdateRequest 接口
    await apiClient.put(`/users/${currentUserId.value}`, {
      email: editForm.value.email,
      phone: editForm.value.phone,
      region: editForm.value.region,
      username: editForm.value.username // 占位传参
    })
    ElMessage.success('用户信息更新成功')
    dialogVisible.value = false
    handleSearch() // 刷新列表
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？此操作不可恢复！`, '危险操作', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'error',
  }).then(async () => {
    try {
      await apiClient.delete(`/users/${row.id}`)
      ElMessage.success('用户删除成功')
      handleSearch()
    } catch (error) {
      ElMessage.error('删除用户失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.admin-page-container {
  padding: 0;
}
.card-header {
  font-size: 16px;
  font-weight: bold;
}
.search-form {
  margin-bottom: 20px;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>