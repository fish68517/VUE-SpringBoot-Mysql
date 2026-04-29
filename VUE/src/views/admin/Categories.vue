<template>
  <div class="categories">
    <el-card>
      <template #header>
        <div class="header">
          <span>{{ pageTitle }}</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>

      <el-table :data="categories" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="180" />
        <el-table-column prop="description" label="分类描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序值" width="100" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      :title="dialogType === 'add' ? '新增分类' : '编辑分类'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" maxlength="30" show-word-limit />
        </el-form-item>
        <el-form-item label="排序值" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="255" show-word-limit />
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
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryApi } from '@/api/networkApi'

export default {
  setup() {
    const loading = ref(false)
    const categories = ref([])
    const dialogVisible = ref(false)
    const dialogType = ref('add')
    const formRef = ref(null)
    const form = ref({
      id: null,
      name: '',
      description: '',
      sortOrder: 0
    })

    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const pageTitle = computed(() => userInfo.role === 'merchant' ? '商家分类管理' : '分类管理')

    const rules = {
      name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
      sortOrder: [{ required: true, message: '请输入排序值', trigger: 'change' }]
    }

    const getCategories = async () => {
      loading.value = true
      try {
        const data = await categoryApi.getCategories()
        categories.value = Array.isArray(data) ? data : []
      } catch (error) {
        ElMessage.error('获取分类失败')
      } finally {
        loading.value = false
      }
    }

    const resetForm = () => {
      form.value = {
        id: null,
        name: '',
        description: '',
        sortOrder: 0
      }
    }

    const handleAdd = () => {
      dialogType.value = 'add'
      resetForm()
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      dialogType.value = 'edit'
      form.value = {
        id: row.id,
        name: row.name || '',
        description: row.description || '',
        sortOrder: row.sortOrder ?? 0
      }
      dialogVisible.value = true
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
        await categoryApi.deleteCategory(row.id)
        ElMessage.success('删除成功')
        getCategories()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const handleSubmit = async () => {
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (!valid) return
        const payload = {
          name: form.value.name,
          description: form.value.description,
          sortOrder: form.value.sortOrder
        }
        try {
          if (dialogType.value === 'add') {
            await categoryApi.createCategory(payload)
            ElMessage.success('新增成功')
          } else {
            await categoryApi.updateCategory(form.value.id, payload)
            ElMessage.success('更新成功')
          }
          dialogVisible.value = false
          getCategories()
        } catch (error) {
          ElMessage.error(dialogType.value === 'add' ? '新增失败' : '更新失败')
        }
      })
    }

    onMounted(getCategories)

    return {
      loading,
      categories,
      pageTitle,
      dialogVisible,
      dialogType,
      formRef,
      form,
      rules,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.categories {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
