<template>
  <div class="admin-page">
    <div class="page-head">
      <div>
        <h2>用户管理</h2>
        <p>管理员可以新增、修改、删除用户，并切换角色。</p>
      </div>
      <el-button type="primary" @click="openCreateDialog">新增用户</el-button>
    </div>

    <el-table :data="users" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" min-width="140" />
      <el-table-column prop="email" label="邮箱" min-width="220" />
      <el-table-column prop="role" label="角色" width="120" />
      <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="removeUser(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEditing ? '编辑用户' : '新增用户'" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item :label="isEditing ? '重置密码（留空则不修改）' : '密码'">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="头像地址">
          <el-input v-model="form.avatarUrl" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.bio" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminService } from '../services/adminService'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEditing = ref(false)
const currentId = ref(null)
const users = ref([])
const form = reactive({
  username: '',
  email: '',
  password: '',
  role: 'USER',
  avatarUrl: '',
  bio: ''
})

const resetForm = () => {
  form.username = ''
  form.email = ''
  form.password = ''
  form.role = 'USER'
  form.avatarUrl = ''
  form.bio = ''
  currentId.value = null
}

const loadUsers = async () => {
  loading.value = true
  try {
    const response = await adminService.getUsers()
    users.value = response.data.content || []
  } catch (error) {
    ElMessage.error(error.message || '加载用户失败')
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  resetForm()
  isEditing.value = false
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEditing.value = true
  currentId.value = row.id
  form.username = row.username
  form.email = row.email
  form.password = ''
  form.role = row.role
  form.avatarUrl = row.avatarUrl || ''
  form.bio = row.bio || ''
  dialogVisible.value = true
}

const submitForm = async () => {
  saving.value = true
  try {
    if (isEditing.value) {
      const payload = {
        username: form.username,
        email: form.email,
        role: form.role,
        avatarUrl: form.avatarUrl,
        bio: form.bio
      }
      if (form.password) {
        payload.password = form.password
      }
      await adminService.updateUser(currentId.value, payload)
      ElMessage.success('用户更新成功')
    } else {
      await adminService.createUser({ ...form })
      ElMessage.success('用户创建成功')
    }
    dialogVisible.value = false
    await loadUsers()
  } catch (error) {
    ElMessage.error(error.message || '保存用户失败')
  } finally {
    saving.value = false
  }
}

const removeUser = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.username}」吗？`, '删除确认', {
      type: 'warning'
    })
    await adminService.deleteUser(row.id)
    ElMessage.success('用户删除成功')
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除用户失败')
    }
  }
}

onMounted(loadUsers)
</script>

<style scoped>
.admin-page {
  max-width: 1320px;
  margin: 0 auto;
}

.page-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.page-head h2 {
  margin: 0 0 8px;
  color: #183b56;
}

.page-head p {
  margin: 0;
  color: #6b7a88;
}
</style>
