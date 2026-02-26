<template>
  <div class="admin-attractions-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>景点管理</span>
          <el-button type="primary" size="small" @click="openCreateDialog">
            新增景点
          </el-button>
        </div>
      </template>
      
      <el-table :data="attractions" stripe>
        <el-table-column prop="id" label="景点ID" width="80" />
        <el-table-column prop="name" label="景点名称" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="ticketPrice" label="门票价格" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image 
              v-if="row.imageUrl"
              :src="getFullImageUrl(row.imageUrl)" 
              style="width: 50px; height: 50px; border-radius: 4px;"
              fit="cover"
              :preview-src-list="[getFullImageUrl(row.imageUrl)]"
              preview-teleported
            />
            <span v-else>无图</span>
          </template>
        </el-table-column>
        <el-table-column label="标签" width="200">
          <template #default="{ row }">
            <div class="tags-cell">
              <el-tag
                v-for="tag in row.tags"
                :key="tag"
                closable
                @close="removeTag(row.id, tag)"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ tag }}
              </el-tag>
              <el-button type="primary" size="small" text @click="showTagDialog(row)">
                + 添加标签
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="isGuangzhouSpecial" label="广州特色">
          <template #default="{ row }">
            <el-tag :type="row.isGuangzhouSpecial ? 'success' : 'info'">
              {{ row.isGuangzhouSpecial ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editAttraction(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteAttraction(row.id)">删除</el-button>
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
        @current-change="loadAttractions"
        @size-change="loadAttractions"
      />
    </el-card>

    <el-dialog v-model="tagDialogVisible" title="添加标签" width="400px">
      </el-dialog>

    <el-dialog 
      v-model="showCreateDialog" 
      :title="editingAttraction ? '编辑景点' : '新增景点'" 
      width="600px"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入景点名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入景点位置" />
        </el-form-item>
        <el-form-item label="门票价格" prop="ticketPrice">
          <el-input-number v-model="formData.ticketPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="营业时间" prop="openingHours">
          <el-input v-model="formData.openingHours" placeholder="例如: 09:00-17:00" />
        </el-form-item>
        <el-form-item label="景点描述" prop="description">
          <el-input v-model="formData.description" type="textarea" rows="4" placeholder="请输入景点描述"/>
        </el-form-item>
        
        <el-form-item label="景点图片" prop="imageUrl">
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
            只能上传 jpg/png 文件，且不超过 5MB
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
import { Plus } from '@element-plus/icons-vue' // 引入加号图标

// 修复了之前缺失 /api 的问题
const API_BASE_URL = 'http://localhost:8080/api'

const attractions = ref([])
const pagination = ref({ currentPage: 1, pageSize: 10, total: 0 })

const tagDialogVisible = ref(false)
const showCreateDialog = ref(false)
const selectedAttraction = ref(null)
const newTag = ref('')
const loading = ref(false)
const formRef = ref(null)
const editingAttraction = ref(null)

const formData = ref({
  name: '', location: '', ticketPrice: 0, openingHours: '',
  description: '', imageUrl: '', isGuangzhouSpecial: false
})

const formRules = {
  name: [{ required: true, message: '景点名称不能为空', trigger: 'blur' }],
  location: [{ required: true, message: '位置不能为空', trigger: 'blur' }],
  ticketPrice: [{ required: true, message: '门票价格不能为空', trigger: 'blur' }],
  openingHours: [{ required: true, message: '营业时间不能为空', trigger: 'blur' }]
}

// ==== 图片上传相关逻辑开始 ====

// 拼接完整的图片 URL 供前端显示
const getFullImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url // 如果已经是网络绝对路径，直接返回
  return `${API_BASE_URL}${url}` // 拼接后端地址
}

// 动态获取请求头（如果后端有拦截器校验用户的话）
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

// 图片上传前的校验
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('上传图片只能是 JPG/PNG/WEBP 格式!')
  }
  if (!isLt5M) {
    ElMessage.error('上传图片大小不能超过 5MB!')
  }
  return isImage && isLt5M
}

// 图片上传成功的回调
const handleUploadSuccess = (response, uploadFile) => {
  if (response.code === 0 || response.code === '0') {
    // 将后端返回的相对路径赋值给 formData
    formData.value.imageUrl = response.data
    ElMessage.success('图片上传成功！')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}
// ==== 图片上传相关逻辑结束 ====

const openCreateDialog = () => {
  editingAttraction.value = null
  resetForm()
  showCreateDialog.value = true
}

const loadAttractions = async () => {
  loading.value = true
  try {
    const response = await fetch(`${API_BASE_URL}/attractions/list?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`)
    const data = await response.json()
    if (data.code === 0 || data.code === '0') {
      attractions.value = data.data.attractions
      pagination.value.total = data.data.total
    }
  } catch (error) {
    ElMessage.error('加载列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const showTagDialog = (attraction) => {
  selectedAttraction.value = attraction
  newTag.value = ''
  tagDialogVisible.value = true
}

const addTag = async () => { /* 保持你原来的逻辑 */ }
const removeTag = async (attractionId, tagName) => { /* 保持你原来的逻辑 */ }

const editAttraction = (attraction) => {
  editingAttraction.value = attraction
  formData.value = { ...attraction }
  showCreateDialog.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const url = editingAttraction.value
        ? `${API_BASE_URL}/attractions/admin/${editingAttraction.value.id}`
        : `${API_BASE_URL}/attractions/admin/create`
      const method = editingAttraction.value ? 'PUT' : 'POST'
      
      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData.value)
      })
      const data = await response.json()

      if (data.code === 0 || data.code === '0') {
        ElMessage.success(editingAttraction.value ? '景点编辑成功' : '景点创建成功')
        showCreateDialog.value = false
        loadAttractions()
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
    name: '', location: '', ticketPrice: 0, openingHours: '',
    description: '', imageUrl: '', isGuangzhouSpecial: false
  }
}

const deleteAttraction = (attractionId) => {
  ElMessageBox.confirm('确定要删除该景点吗？', '警告', { type: 'warning' })
    .then(async () => {
      const response = await fetch(`${API_BASE_URL}/attractions/admin/${attractionId}`, { method: 'DELETE' })
      const data = await response.json()
      if (data.code === 0 || data.code === '0') {
        ElMessage.success('景点删除成功')
        loadAttractions()
      }
    }).catch(() => {})
}

onMounted(() => {
  loadAttractions()
})
</script>

<style scoped>
.admin-attractions-container { padding: 20px; }
.card-header {
  font-weight: bold; color: #333; display: flex;
  justify-content: space-between; align-items: center;
}
.tags-cell {
  display: flex; flex-wrap: wrap; gap: 4px; align-items: center;
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