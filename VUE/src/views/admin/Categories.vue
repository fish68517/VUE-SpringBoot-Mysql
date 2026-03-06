<template>
  <div class="categories">
    <h1>栏目管理</h1>
    
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增栏目</el-button>
      <el-button @click="loadCategories" :loading="loading">刷新</el-button>
    </div>

    <el-table :data="categories" stripe style="margin-top: 20px">
      <el-table-column prop="id" label="栏目ID" width="100" />
      <el-table-column prop="name" label="栏目名称" width="150" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="fileFormat" label="文件格式" width="120" />
      <el-table-column prop="maxFileSize" label="最大文件大小(字节)" width="150" />
      <el-table-column prop="wordCountMin" label="最小字数" width="120" />
      <el-table-column prop="wordCountMax" label="最大字数" width="120" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="info" size="small" @click="handleViewRequirements(row)">查看要求</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Add/Edit Category Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑栏目' : '新增栏目'" width="600px">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="栏目名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入栏目名称" />
        </el-form-item>
        <el-form-item label="栏目描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入栏目描述" />
        </el-form-item>
        <el-form-item label="投稿要求" prop="requirements">
          <el-input v-model="form.requirements" type="textarea" rows="3" placeholder="请输入投稿要求" />
        </el-form-item>
        <el-form-item label="文件格式" prop="fileFormat">
          <el-input v-model="form.fileFormat" placeholder="例如: pdf, doc, docx" />
        </el-form-item>
        <el-form-item label="最大文件大小(字节)" prop="maxFileSize">
          <el-input-number v-model="form.maxFileSize" :min="0" />
        </el-form-item>
        <el-form-item label="最小字数" prop="wordCountMin">
          <el-input-number v-model="form.wordCountMin" :min="0" />
        </el-form-item>
        <el-form-item label="最大字数" prop="wordCountMax">
          <el-input-number v-model="form.wordCountMax" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saveLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- View Requirements Dialog -->
    <el-dialog v-model="requirementsDialogVisible" title="栏目投稿要求" width="600px">
      <div v-if="selectedCategory" class="requirements-content">
        <p><strong>栏目名称:</strong> {{ selectedCategory.name }}</p>
        <p><strong>栏目描述:</strong></p>
        <p>{{ selectedCategory.description }}</p>
        <p><strong>投稿要求:</strong></p>
        <p>{{ selectedCategory.requirements }}</p>
        <p><strong>文件格式:</strong> {{ selectedCategory.fileFormat }}</p>
        <p><strong>最大文件大小:</strong> {{ selectedCategory.maxFileSize }} 字节</p>
        <p><strong>字数要求:</strong> {{ selectedCategory.wordCountMin }} - {{ selectedCategory.wordCountMax }} 字</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryService } from '../../services/categoryService'

const loading = ref(false)
const saveLoading = ref(false)
const dialogVisible = ref(false)
const requirementsDialogVisible = ref(false)
const isEdit = ref(false)

const categories = ref([])
const selectedCategory = ref(null)

const form = ref({
  id: null,
  name: '',
  description: '',
  requirements: '',
  fileFormat: '',
  maxFileSize: null,
  wordCountMin: null,
  wordCountMax: null
})

const formRef = ref(null)

const rules = {
  name: [
    { required: true, message: '栏目名称不能为空', trigger: 'blur' }
  ]
}

// Load categories on component mount
onMounted(async () => {
  await loadCategories()
})

// Load all categories
const loadCategories = async () => {
  loading.value = true
  try {
    const response = await categoryService.getAllCategories()
    if (response.data.code === 200) {
      categories.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '加载栏目列表失败')
    }
  } catch (error) {
    ElMessage.error('加载栏目列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// Add new category
const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    description: '',
    requirements: '',
    fileFormat: '',
    maxFileSize: null,
    wordCountMin: null,
    wordCountMax: null
  }
  dialogVisible.value = true
}

// Edit category
const handleEdit = (category) => {
  isEdit.value = true
  form.value = {
    id: category.id,
    name: category.name,
    description: category.description || '',
    requirements: category.requirements || '',
    fileFormat: category.fileFormat || '',
    maxFileSize: category.maxFileSize,
    wordCountMin: category.wordCountMin,
    wordCountMax: category.wordCountMax
  }
  dialogVisible.value = true
}

// Save category
const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saveLoading.value = true
    try {
      let response
      if (isEdit.value) {
        response = await categoryService.updateCategory(form.value.id, form.value)
      } else {
        response = await categoryService.createCategory(form.value)
      }

      if (response.data.code === 200 || response.data.code === 201) {
        ElMessage.success(isEdit.value ? '栏目已更新' : '栏目已创建')
        dialogVisible.value = false
        await loadCategories()
      } else {
        ElMessage.error(response.data.message || '保存失败')
      }
    } catch (error) {
      ElMessage.error('保存失败')
      console.error(error)
    } finally {
      saveLoading.value = false
    }
  })
}

// View requirements
const handleViewRequirements = (category) => {
  selectedCategory.value = category
  requirementsDialogVisible.value = true
}

// Delete category
const handleDelete = (categoryId) => {
  ElMessageBox.confirm('确定要删除该栏目吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await categoryService.deleteCategory(categoryId)
      if (response.data.code === 200) {
        ElMessage.success('栏目已删除')
        await loadCategories()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {
    // User cancelled the action
  })
}
</script>

<style scoped>
.categories {
  padding: 20px;
}

.categories h1 {
  margin-bottom: 20px;
  color: #333;
}

.toolbar {
  margin-bottom: 20px;
}

.requirements-content {
  line-height: 1.8;
}

.requirements-content p {
  margin: 10px 0;
}

.requirements-content strong {
  color: #333;
}
</style>
