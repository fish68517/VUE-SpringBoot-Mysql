<template>
  <div class="image-upload-container">
    <div class="upload-area" @click="triggerFileInput" :class="{ 'has-preview': previewUrl }">
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        @change="handleFileSelect"
        style="display: none"
      />

      <div v-if="!previewUrl" class="upload-placeholder">
        <el-icon class="upload-icon"><Picture /></el-icon>
        <p class="upload-text">点击选择图片或拖拽上传</p>
        <p class="upload-hint">支持 JPG、JPEG、PNG、GIF 格式</p>
      </div>

      <div v-else class="preview-container">
        <img :src="previewUrl" :alt="selectedFile?.name" class="preview-image" />
        <div class="preview-overlay">
          <el-button type="primary" size="small" @click.stop="triggerFileInput">
            更换图片
          </el-button>
          <el-button size="small" @click.stop="clearPreview">
            清除
          </el-button>
        </div>
      </div>
    </div>

    <div v-if="selectedFile" class="file-info">
      <p><strong>文件名:</strong> {{ selectedFile.name }}</p>
      <p><strong>文件大小:</strong> {{ formatFileSize(selectedFile.size) }}</p>
    </div>

    <div class="upload-actions">
      <el-button
        type="primary"
        @click="handleUpload"
        :loading="uploading"
        :disabled="!selectedFile || uploading"
      >
        {{ uploading ? '上传中...' : '上传图片' }}
      </el-button>
      <el-button @click="clearPreview" :disabled="!selectedFile">
        取消
      </el-button>
    </div>

    <div v-if="uploadedImagePath" class="upload-success">
      <el-alert
        title="上传成功"
        type="success"
        :closable="false"
        show-icon
      >
        <p>图片已成功上传</p>
        <p class="image-path">路径: {{ uploadedImagePath }}</p>
        <img :src="uploadedImagePath" :alt="uploadedImagePath" class="uploaded-preview" />
      </el-alert>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { uploadImage } from '../api/upload.js'

const fileInput = ref(null)
const selectedFile = ref(null)
const previewUrl = ref('')
const uploading = ref(false)
const uploadedImagePath = ref('')

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  // Validate file type
  if (!isValidImageType(file.name)) {
    ElMessage.error('只支持 JPG、JPEG、PNG、GIF 格式的图片')
    return
  }

  // Validate file size (max 5MB)
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 5MB')
    return
  }

  selectedFile.value = file
  previewUrl.value = URL.createObjectURL(file)
  uploadedImagePath.value = ''
}

const isValidImageType = (filename) => {
  const lowerFilename = filename.toLowerCase()
  return (
    lowerFilename.endsWith('.jpg') ||
    lowerFilename.endsWith('.jpeg') ||
    lowerFilename.endsWith('.png') ||
    lowerFilename.endsWith('.gif')
  )
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

const handleUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择图片')
    return
  }

  uploading.value = true
  try {
    const response = await uploadImage(selectedFile.value)
    if (response.code === 200) {
      uploadedImagePath.value = response.data.path
      ElMessage.success('图片上传成功')
      // Reset after successful upload
      setTimeout(() => {
        clearPreview()
      }, 2000)
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    console.error('Upload error:', error)
    ElMessage.error('上传失败: ' + (error.message || '网络错误'))
  } finally {
    uploading.value = false
  }
}

const clearPreview = () => {
  selectedFile.value = null
  previewUrl.value = ''
  uploadedImagePath.value = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}
</script>

<style scoped>
.image-upload-container {
  width: 100%;
  max-width: 500px;
}

.upload-area {
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
  margin-bottom: 20px;
}

.upload-area:hover {
  border-color: #409eff;
  background: #f5f7fa;
}

.upload-area.has-preview {
  border-color: #67c23a;
  background: #f0f9ff;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.upload-icon {
  font-size: 48px;
  color: #909399;
}

.upload-text {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

.upload-hint {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.preview-container {
  position: relative;
  display: inline-block;
}

.preview-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-radius: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.preview-container:hover .preview-overlay {
  opacity: 1;
}

.file-info {
  background: #f5f7fa;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 20px;
  font-size: 12px;
}

.file-info p {
  margin: 5px 0;
  color: #606266;
}

.file-info strong {
  color: #333;
}

.upload-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.upload-actions :deep(.el-button) {
  flex: 1;
}

.upload-success {
  margin-top: 20px;
}

.image-path {
  font-size: 12px;
  color: #606266;
  word-break: break-all;
  margin: 8px 0;
}

.uploaded-preview {
  max-width: 100%;
  max-height: 200px;
  margin-top: 10px;
  border-radius: 4px;
}
</style>
