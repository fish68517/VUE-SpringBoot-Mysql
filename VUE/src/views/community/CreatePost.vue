<template>
  <div class="create-post">
    <div class="page-header">
      <h2>Create Post</h2>
      <el-button :icon="ArrowLeft" @click="goBack">Back</el-button>
    </div>

    <el-card class="create-form">
      <el-form :model="form" label-position="top">
        <el-form-item label="Content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="Share your fitness journey, tips, or achievements..."
            maxlength="1000"
            show-word-limit
          />
          <div class="character-count">
            {{ form.content.length }} / 1000 characters
          </div>
        </el-form-item>

        <el-form-item label="Images (Optional)">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadAction"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :on-error="handleUploadError"
            list-type="picture-card"
            :limit="9"
            accept="image/jpeg,image/png,image/gif"
            multiple
          >
            <el-icon><plus /></el-icon>
            <template #tip>
              <div class="upload-tip">
                Upload up to 9 images (JPG, PNG, GIF, max 5MB each)
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button @click="handleCancel">Cancel</el-button>
            <el-button
              type="primary"
              :loading="submitting"
              :disabled="!form.content.trim()"
              @click="handleSubmit"
            >
              Publish Post
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { createDynamic } from '@/api/community'
import { uploadImage } from '@/api/upload'
import { showSuccess, showError, showWarning, confirmDiscard } from '@/utils/feedback'

const router = useRouter()

const form = ref({
  content: '',
  imageUrls: []
})

const fileList = ref([])
const submitting = ref(false)

const uploadAction = computed(() => {
  // This is just for display, actual upload is handled by before-upload
  return import.meta.env.VITE_API_BASE_URL + '/api/upload/image'
})

const beforeUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    showError('Only JPG, PNG, and GIF images are allowed')
    return false
  }
  if (!isLt5M) {
    showError('Image size must be less than 5MB')
    return false
  }

  return true
}

const handleUploadSuccess = (response, file) => {
  // The response should contain the file URL
  if (response && response.url) {
    form.value.imageUrls.push(response.url)
    file.url = response.url
  } else if (typeof response === 'string') {
    // If response is directly the URL string
    form.value.imageUrls.push(response)
    file.url = response
  }
}

const handleRemove = (file) => {
  if (file.url) {
    const index = form.value.imageUrls.indexOf(file.url)
    if (index > -1) {
      form.value.imageUrls.splice(index, 1)
    }
  }
}

const handleUploadError = (error) => {
  showError('Failed to upload image')
  console.error('Upload error:', error)
}

const handleSubmit = async () => {
  if (!form.value.content.trim()) {
    showWarning('Please enter post content')
    return
  }

  submitting.value = true
  try {
    await createDynamic({
      content: form.value.content,
      imageUrls: form.value.imageUrls.join(',')
    })
    
    showSuccess('Post published successfully!')
    router.push('/community')
  } catch (error) {
    // Error already handled by request interceptor
    console.error('Failed to publish post:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = async () => {
  if (form.value.content.trim() || form.value.imageUrls.length > 0) {
    try {
      await confirmDiscard()
      goBack()
    } catch (error) {
      // User cancelled, do nothing
    }
  } else {
    goBack()
  }
}

const goBack = () => {
  router.push('/community')
}
</script>

<style scoped>
.create-post {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.create-form {
  padding: 24px;
}

.character-count {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

:deep(.el-upload-list--picture-card) {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.el-upload--picture-card) {
  width: 148px;
  height: 148px;
  border-radius: 8px;
}

:deep(.el-upload-list__item) {
  width: 148px;
  height: 148px;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .create-post {
    padding: 12px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .create-form {
    padding: 16px;
  }

  :deep(.el-upload--picture-card),
  :deep(.el-upload-list__item) {
    width: 100px;
    height: 100px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>
