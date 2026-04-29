<template>
  <div class="users">
    <el-card>
      <template #header>
        <div class="header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">添加用户</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchQuery" placeholder="搜索用户名/昵称" clearable @keyup.enter="handleSearch">
          <template #append>
            <el-button icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="100">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              {{ row.nickname?.charAt(0) || row.username?.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : (row.role === 'merchant' ? 'warning' : 'info')">
              {{ row.role === 'admin' ? '管理员' : (row.role === 'merchant' ? '商家' : '普通用户') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)" :disabled="row.role === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog :title="dialogType === 'add' ? '添加用户' : '编辑用户'" v-model="dialogVisible" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role">
            <el-option label="普通用户" value="user" />
            <el-option label="商家" value="merchant" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/networkApi'

export default {
  setup() {
    const loading = ref(false)
    const users = ref([])
    const searchQuery = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const dialogVisible = ref(false)
    const dialogType = ref('add')
    const formRef = ref(null)

    const form = ref({
      username: '',
      nickname: '',
      password: '',
      role: 'user'
    })

    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      role: [{ required: true, message: '请选择角色', trigger: 'change' }]
    }

    const getUsers = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          pageSize: pageSize.value,
          query: searchQuery.value || ''
        }
        const { data, total: totalCount } = await userApi.getUsers(params)
        users.value = data || []
        total.value = totalCount || 0
      } catch (error) {
        ElMessage.error('获取用户列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      getUsers()
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      getUsers()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      getUsers()
    }

    const handleAdd = () => {
      dialogType.value = 'add'
      form.value = {
        username: '',
        nickname: '',
        password: '',
        role: 'user'
      }
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      dialogType.value = 'edit'
      form.value = {
        id: row.id,
        username: row.username,
        nickname: row.nickname || '',
        role: row.role || 'user'
      }
      dialogVisible.value = true
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
        await userApi.deleteUser(row.id)
        ElMessage.success('删除成功')
        getUsers()
      } catch (error) {
        if (error !== 'cancel') ElMessage.error('删除失败')
      }
    }

    const handleSubmit = async () => {
      if (!formRef.value) return

      await formRef.value.validate(async (valid) => {
        if (!valid) return

        try {
          if (dialogType.value === 'add') {
            await userApi.createUser(form.value)
            ElMessage.success('添加成功')
          } else {
            await userApi.updateUser(form.value.id, {
              username: form.value.username,
              nickname: form.value.nickname,
              role: form.value.role
            })
            ElMessage.success('更新成功')
          }

          dialogVisible.value = false
          getUsers()
        } catch (error) {
          ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
        }
      })
    }

    const formatDate = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleString()
    }

    onMounted(() => {
      getUsers()
    })

    return {
      loading,
      users,
      searchQuery,
      currentPage,
      pageSize,
      total,
      dialogVisible,
      dialogType,
      formRef,
      form,
      rules,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit,
      formatDate
    }
  }
}
</script>

<style scoped>
.users { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.search-bar { margin-bottom: 20px; width: 320px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
