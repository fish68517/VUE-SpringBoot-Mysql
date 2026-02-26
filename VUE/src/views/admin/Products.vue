<template>
  <div class="admin-products-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" size="small" @click="openCreateDialog">
            新增商品
          </el-button>
        </div>
      </template>
      
      <el-table :data="products" stripe v-loading="loading">
        <el-table-column prop="id" label="商品ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.category || '未分类' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span style="color: #ff6b6b; font-weight: bold;">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image 
              v-if="row.imageUrl"
              :src="getFullImageUrl(row.imageUrl)" 
              style="width: 50px; height: 50px; border-radius: 4px;"
              fit="cover"
              :preview-src-list="[getFullImageUrl(row.imageUrl)]"
              preview-teleported
            />
            <span v-else style="color: #999; font-size: 12px;">无图</span>
          </template>
        </el-table-column>

        <el-table-column prop="isGuangzhouSpecial" label="广州特色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isGuangzhouSpecial ? 'success' : 'info'">
              {{ row.isGuangzhouSpecial ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editProduct(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteProduct(row.id)">删除</el-button>
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
        @current-change="loadProducts"
        @size-change="loadProducts"
      />
    </el-card>

    <el-dialog 
      v-model="showCreateDialog" 
      :title="editingProduct ? '编辑商品' : '新增商品'" 
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="美食" value="美食" />
            <el-option label="手工艺品" value="手工艺品" />
            <el-option label="纪念品" value="纪念品" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品价格" prop="price">
              <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品库存" prop="stock">
              <el-input-number v-model="formData.stock" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            rows="4"
            placeholder="请输入商品描述"
          />
        </el-form-item>

        <el-form-item label="商品图片" prop="imageUrl">
          <el-upload
            class="avatar-uploader"
            :action="`${API_BASE_URL}/upload/image`"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :headers="uploadHeaders"
          >
            <img v-if="formData.imageUrl" :src="getFullImageUrl(formData.imageUrl)" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="font-size: 12px; color: #999; margin-top: 5px; line-height: 1.2;">
            只能上传 jpg/png/webp 文件，且不超过 5MB
          </div>
        </el-form-item>

        <el-form-item label="广州特色" prop="isGuangzhouSpecial">
          <el-switch v-model="formData.isGuangzhouSpecial" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const products = ref([])
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const showCreateDialog = ref(false)
const loading = ref(false)
const formRef = ref(null)
const editingProduct = ref(null)

const formData = ref({
  name: '',
  category: '',
  price: 0,
  stock: 0,
  description: '',
  imageUrl: '',
  isGuangzhouSpecial: false
})

const formRules = {
  name: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '价格不能为空', trigger: 'blur' }],
  stock: [{ required: true, message: '库存不能为空', trigger: 'blur' }]
}

const API_BASE_URL = 'http://localhost:8080/api'

// 拼接完整的图片 URL 供前端显示
const getFullImageUrl = (url) => {
  if (!url) return ''
  // 如果已经是完整的网络图片地址（比如外链），直接返回
  if (url.startsWith('http')) return url 
  // 如果是相对路径（我们自己上传的），拼接上 Spring Boot 后端的地址
  return `http://localhost:8080/api${url}`
}

const uploadHeaders = computed(() => {
  const user = localStorage.getItem('user')
  if (user) {
    const userInfo = JSON.parse(user)
    return {
      'X-User-Id': userInfo.id,
      'X-User-Role': userInfo.role
    }
  }
  return {}
})

const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) ElMessage.error('上传图片只能是 JPG/PNG/WEBP 格式!')
  if (!isLt5M) ElMessage.error('上传图片大小不能超过 5MB!')
  return isImage && isLt5M
}

const handleUploadSuccess = (response) => {
  if (response.code === 0 || response.code === '0') {
    formData.value.imageUrl = response.data
    ElMessage.success('图片上传成功！')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}
// ==== 图片上传相关逻辑结束 ====

const openCreateDialog = () => {
  editingProduct.value = null
  resetForm()
  showCreateDialog.value = true
}

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `${API_BASE_URL}/products/list?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    )
    const data = await response.json()

    if (data.code === 0 || data.code === '0') {
      products.value = data.data.products || data.data.content
      pagination.value.total = data.data.total || data.data.totalElements
    } else {
      ElMessage.error(data.message || '加载商品列表失败')
    }
  } catch (error) {
    ElMessage.error('加载商品列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const editProduct = (product) => {
  editingProduct.value = product
  formData.value = { ...product }
  showCreateDialog.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const url = editingProduct.value
        ? `${API_BASE_URL}/products/admin/${editingProduct.value.id}`
        : `${API_BASE_URL}/products/admin/create`
      
      const method = editingProduct.value ? 'PUT' : 'POST'
      
      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData.value)
      })
      const data = await response.json()

      if (data.code === 0 || data.code === '0') {
        ElMessage.success(editingProduct.value ? '商品编辑成功' : '商品创建成功')
        showCreateDialog.value = false
        loadProducts()
      } else {
        ElMessage.error(data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败: ' + error.message)
    }
  })
}

const resetForm = () => {
  formData.value = {
    name: '',
    category: '',
    price: 0,
    stock: 0,
    description: '',
    imageUrl: '',
    isGuangzhouSpecial: false
  }
}

const deleteProduct = (productId) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/products/admin/${productId}`, { method: 'DELETE' })
      const data = await response.json()

      if (data.code === 0 || data.code === '0') {
        ElMessage.success('商品删除成功')
        loadProducts()
      } else {
        ElMessage.error(data.message || '删除商品失败')
      }
    } catch (error) {
      ElMessage.error('删除商品失败: ' + error.message)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.admin-products-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 上传组件的样式 */
:deep(.avatar-uploader .el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

:deep(.avatar-uploader .el-upload:hover) {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}
</style>