<template>
  <section class="page-card" style="padding: 24px;">
    <div class="toolbar">
      <div class="toolbar-title">用户管理</div>
      <div class="toolbar-actions">
        <el-button type="primary" @click="openCreate">新增用户</el-button>
        <el-button @click="loadUsers">刷新</el-button>
      </div>
    </div>

    <el-table :data="users" border>
      <el-table-column type="index" width="60" label="#" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除该用户？" @confirm="removeUser(row.id)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="巡检员" value="INSPECTOR" />
            <el-option label="维护员" value="MAINTAINER" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUser">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const users = ref([])
const dialogVisible = ref(false)
const form = reactive(defaultForm())

function defaultForm() {
  return {
    id: null,
    username: '',
    password: '123456',
    realName: '',
    role: 'INSPECTOR',
    phone: '',
    email: '',
    status: 'ENABLED'
  }
}

function resetForm() {
  Object.assign(form, defaultForm())
}

async function loadUsers() {
  users.value = await http.get('/api/users')
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, { ...row, password: '123456' })
  dialogVisible.value = true
}

async function submitUser() {
  if (form.id) {
    await http.put(`/api/users/${form.id}`, form)
  } else {
    await http.post('/api/users', form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadUsers()
}

async function removeUser(id) {
  await http.delete(`/api/users/${id}`)
  ElMessage.success('删除成功')
  loadUsers()
}

onMounted(loadUsers)
</script>
