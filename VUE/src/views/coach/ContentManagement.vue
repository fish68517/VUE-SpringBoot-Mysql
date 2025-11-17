<template>
  <Layout>
    <div class="content-management">
      <div class="page-header">
        <h2>My Content</h2>
        <el-button type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon>
          Create Content
        </el-button>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- Empty State -->
      <div v-else-if="contentList.length === 0" class="empty-state">
        <el-empty description="No content yet">
          <el-button type="primary" @click="openCreateDialog">Create Your First Content</el-button>
        </el-empty>
      </div>

      <!-- Content Table -->
      <el-card v-else>
        <el-table :data="contentList" style="width: 100%">
          <el-table-column prop="title" label="Title" min-width="200" />
          <el-table-column label="Type" width="120">
            <template #default="{ row }">
              <el-tag :type="getContentTypeColor(row.contentType)">
                {{ row.contentType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="Views" width="100" />
          <el-table-column label="Created" width="150">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)">Edit</el-button>
              <el-button size="small" type="danger" @click="confirmDelete(row)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- Pagination -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @current-change="fetchContent"
            @size-change="fetchContent"
          />
        </div>
      </el-card>

      <!-- Create/Edit Content Dialog -->
      <el-dialog
        v-model="showContentDialog"
        :title="isEditMode ? 'Edit Content' : 'Create Content'"
        width="700px"
        :close-on-click-modal="false"
      >
        <el-form
          ref="contentFormRef"
          :model="contentForm"
          :rules="contentRules"
          label-width="120px"
        >
          <el-form-item label="Title" prop="title">
            <el-input
              v-model="contentForm.title"
              placeholder="Enter content title"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="Description" prop="description">
            <el-input
              v-model="contentForm.description"
              type="textarea"
              :rows="3"
              placeholder="Enter content description"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="Content Type" prop="contentType">
            <el-radio-group v-model="contentForm.contentType" @change="handleContentTypeChange">
              <el-radio label="video">Video</el-radio>
              <el-radio label="article">Article</el-radio>
              <el-radio label="document">Document</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- File Upload for Video/Document -->
          <el-form-item
            v-if="contentForm.contentType === 'video' || contentForm.contentType === 'document'"
            label="Upload File"
            prop="fileUrl"
          >
            <el-upload
              class="upload-component"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :limit="1"
              :file-list="fileList"
              :on-remove="handleFileRemove"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                Select File
              </el-button>
              <template #tip>
                <div class="el-upload__tip">
                  {{ contentForm.contentType === 'video' ? 'MP4, AVI (max 100MB)' : 'PDF, DOC, DOCX (max 10MB)' }}
                </div>
              </template>
            </el-upload>
          </el-form-item>

          <!-- Rich Text Editor for Article -->
          <el-form-item
            v-if="contentForm.contentType === 'article'"
            label="Content"
            prop="content"
          >
            <el-input
              v-model="contentForm.content"
              type="textarea"
              :rows="10"
              placeholder="Write your article content here..."
            />
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="showContentDialog = false">Cancel</el-button>
          <el-button type="primary" @click="submitContent" :loading="submitting">
            {{ isEditMode ? 'Update' : 'Create' }}
          </el-button>
        </template>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Loading, Upload } from '@element-plus/icons-vue'
import Layout from '@/components/common/Layout.vue'
import { getResources, createResource, updateResource, deleteResource } from '@/api/resource'
import { getToken } from '@/utils/auth'
import { showSuccess, showError, showWarning, confirmDelete } from '@/utils/feedback'

const contentList = ref([])
const loading = ref(false)
const showContentDialog = ref(false)
const isEditMode = ref(false)
const currentEditId = ref(null)
const submitting = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const contentFormRef = ref(null)
const fileList = ref([])

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return contentForm.contentType === 'video' 
    ? `${baseUrl}/api/upload/video`
    : `${baseUrl}/api/upload/image`
})

const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${getToken()}`
}))

const contentForm = reactive({
  title: '',
  description: '',
  contentType: 'article',
  fileUrl: '',
  content: ''
})

const contentRules = {
  title: [
    { required: true, message: 'Please enter title', trigger: 'blur' },
    { min: 3, max: 200, message: 'Length should be 3 to 200 characters', trigger: 'blur' }
  ],
  description: [
    { max: 1000, message: 'Length should not exceed 1000 characters', trigger: 'blur' }
  ],
  contentType: [
    { required: true, message: 'Please select content type', trigger: 'change' }
  ],
  fileUrl: [
    { 
      validator: (rule, value, callback) => {
        if ((contentForm.contentType === 'video' || contentForm.contentType === 'document') && !value) {
          callback(new Error('Please upload a file'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  content: [
    {
      validator: (rule, value, callback) => {
        if (contentForm.contentType === 'article' && !value) {
          callback(new Error('Please enter article content'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const fetchContent = async () => {
  loading.value = true
  try {
    const response = await getResources({
      page: currentPage.value - 1,
      size: pageSize.value,
      creatorId: 'current' // Filter by current coach
    })
    
    if (response.content) {
      contentList.value = response.content
      total.value = response.totalElements || response.content.length
    } else {
      contentList.value = response
      total.value = response.length
    }
  } catch (error) {
    showError('Failed to load content')
    console.error('Fetch content error:', error)
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  isEditMode.value = false
  currentEditId.value = null
  resetForm()
  showContentDialog.value = true
}

const openEditDialog = (content) => {
  isEditMode.value = true
  currentEditId.value = content.id
  
  contentForm.title = content.title
  contentForm.description = content.description
  contentForm.contentType = content.contentType
  contentForm.fileUrl = content.fileUrl || ''
  contentForm.content = content.content || ''
  
  if (content.fileUrl) {
    fileList.value = [{
      name: content.title,
      url: content.fileUrl
    }]
  }
  
  showContentDialog.value = true
}

const resetForm = () => {
  contentForm.title = ''
  contentForm.description = ''
  contentForm.contentType = 'article'
  contentForm.fileUrl = ''
  contentForm.content = ''
  fileList.value = []
  contentFormRef.value?.clearValidate()
}

const handleContentTypeChange = () => {
  contentForm.fileUrl = ''
  contentForm.content = ''
  fileList.value = []
}

const beforeUpload = (file) => {
  const isVideo = contentForm.contentType === 'video'
  const maxSize = isVideo ? 100 * 1024 * 1024 : 10 * 1024 * 1024 // 100MB for video, 10MB for document
  
  if (file.size > maxSize) {
    showError(`File size should not exceed ${isVideo ? '100MB' : '10MB'}`)
    return false
  }
  
  return true
}

const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    contentForm.fileUrl = response.data
    showSuccess('File uploaded successfully')
  } else {
    showError(response.msg || 'Upload failed')
  }
}

const handleUploadError = () => {
  showError('File upload failed')
}

const handleFileRemove = () => {
  contentForm.fileUrl = ''
  fileList.value = []
}

const submitContent = async () => {
  if (!contentFormRef.value) return

  await contentFormRef.value.validate(async (valid) => {
    if (!valid) {
      showWarning('Please fill in all required fields')
      return
    }

    submitting.value = true
    try {
      const data = {
        title: contentForm.title,
        description: contentForm.description,
        contentType: contentForm.contentType,
        fileUrl: contentForm.fileUrl,
        content: contentForm.content
      }

      if (isEditMode.value) {
        await updateResource(currentEditId.value, data)
        showSuccess('Content updated successfully')
      } else {
        await createResource(data)
        showSuccess('Content created successfully')
      }

      showContentDialog.value = false
      fetchContent()
    } catch (error) {
      showError(error.message || 'Failed to save content')
    } finally {
      submitting.value = false
    }
  })
}

const confirmDelete = async (content) => {
  try {
    await confirmDelete(`"${content.title}"`)
    await deleteResource(content.id)
    showSuccess('Content deleted successfully')
    fetchContent()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      showError('Failed to delete content')
    }
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

const getContentTypeColor = (type) => {
  const colorMap = {
    video: 'success',
    article: 'primary',
    document: 'warning'
  }
  return colorMap[type?.toLowerCase()] || 'info'
}

onMounted(() => {
  fetchContent()
})
</script>

<style scoped>
.content-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px;
  color: #409EFF;
}

.empty-state {
  padding: 60px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.upload-component {
  width: 100%;
}

@media (max-width: 768px) {
  .content-management {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .page-header .el-button {
    width: 100%;
  }
}
</style>
