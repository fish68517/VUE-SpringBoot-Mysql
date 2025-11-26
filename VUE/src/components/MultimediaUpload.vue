<template>
  <div class="multimedia-upload">
    <el-card class="upload-card">
      <template #header>
        <div class="card-header">
          <span>上传多媒体文件</span>
        </div>
      </template>

      <!-- Upload Area -->
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :on-change="handleFileSelect"
        :on-remove="handleFileRemove"
        :multiple="true"
        :accept="acceptedFormats"
        drag
        class="upload-area"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此或<em>点击选择</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持的格式：{{ acceptedFormats }} | 单个文件最大 500MB
          </div>
        </template>
      </el-upload>

      <!-- Selected Files List -->
      <div v-if="selectedFiles.length > 0" class="selected-files">
        <el-divider />
        <h4>待上传文件 ({{ selectedFiles.length }})</h4>
        <el-table :data="selectedFiles" style="width: 100%">
          <el-table-column prop="name" label="文件名" width="200" show-overflow-tooltip />
          <el-table-column prop="size" label="大小" width="100">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ $index }">
              <el-button
                link
                type="danger"
                size="small"
                @click="removeFile($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Upload Progress -->
      <div v-if="uploadingFiles.length > 0" class="upload-progress">
        <el-divider />
        <h4>上传进度</h4>
        <div v-for="(uploadFile, index) in uploadingFiles" :key="index" class="progress-item">
          <div class="progress-header">
            <span>{{ uploadFile.name }}</span>
            <span class="progress-text">{{ uploadFile.progress }}%</span>
          </div>
          <el-progress :percentage="uploadFile.progress" />
        </div>
      </div>

      <!-- Uploaded Files List -->
      <div v-if="uploadedFiles.length > 0" class="uploaded-files">
        <el-divider />
        <h4>已上传文件 ({{ uploadedFiles.length }})</h4>
        <el-table :data="uploadedFiles" style="width: 100%">
          <el-table-column prop="fileName" label="文件名" width="200" show-overflow-tooltip />
          <el-table-column prop="fileSize" label="大小" width="100">
            <template #default="{ row }">
              {{ formatFileSize(row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="fileType" label="类型" width="80" />
          <el-table-column prop="uploadDate" label="上传时间" width="150">
            <template #default="{ row }">
              {{ formatDate(row.uploadDate) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button
                link
                type="danger"
                size="small"
                @click="deleteFile(row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Action Buttons -->
      <div class="action-buttons">
        <el-button
          v-if="selectedFiles.length > 0"
          type="primary"
          @click="startUpload"
          :loading="isUploading"
        >
          开始上传
        </el-button>
        <el-button @click="clearAll">清空</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import fileService from '@/services/fileService'

const props = defineProps({
  travelRecordId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['upload-complete', 'file-deleted'])

const uploadRef = ref(null)
const selectedFiles = ref([])
const uploadingFiles = ref([])
const uploadedFiles = ref([])
const isUploading = ref(false)

const CHUNK_SIZE = 5 * 1024 * 1024 // 5MB chunks
const acceptedFormats = '.jpg,.jpeg,.png,.gif,.mp4,.avi,.mov,.mkv'

/**
 * Handle file selection
 */
const handleFileSelect = (file) => {
  if (file.raw) {
    selectedFiles.value.push(file.raw)
  }
}

/**
 * Handle file removal from upload list
 */
const handleFileRemove = (file) => {
  const index = selectedFiles.value.findIndex(f => f.name === file.name)
  if (index > -1) {
    selectedFiles.value.splice(index, 1)
  }
}

/**
 * Remove file from selected files
 */
const removeFile = (index) => {
  selectedFiles.value.splice(index, 1)
}

/**
 * Start uploading selected files
 */
const startUpload = async () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  isUploading.value = true

  for (const file of selectedFiles.value) {
    await uploadFile(file)
  }

  isUploading.value = false
  selectedFiles.value = []
  ElMessage.success('所有文件上传完成')
  emit('upload-complete')
}

/**
 * Upload a single file with chunking
 */
const uploadFile = async (file) => {
  const totalChunks = Math.ceil(file.size / CHUNK_SIZE)
  const uploadingFile = {
    name: file.name,
    progress: 0
  }
  uploadingFiles.value.push(uploadingFile)

  try {
    for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
      const start = chunkIndex * CHUNK_SIZE
      const end = Math.min(start + CHUNK_SIZE, file.size)
      const chunk = file.slice(start, end)

      const response = await fileService.uploadFileChunk(
        props.travelRecordId,
        chunk,
        chunkIndex,
        totalChunks,
        file.name,
        file.size
      )

      // Update progress
      uploadingFile.progress = Math.round(((chunkIndex + 1) / totalChunks) * 100)

      // If all chunks uploaded, add to uploaded files
      if (response && response.data) {
        uploadedFiles.value.push(response.data)
      }
    }
  } catch (error) {
    ElMessage.error(`文件 ${file.name} 上传失败: ${error.message}`)
  } finally {
    // Remove from uploading list
    const index = uploadingFiles.value.findIndex(f => f.name === file.name)
    if (index > -1) {
      uploadingFiles.value.splice(index, 1)
    }
  }
}

/**
 * Delete a file
 */
const deleteFile = async (fileId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除此文件吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await fileService.deleteFile(fileId)
    const index = uploadedFiles.value.findIndex(f => f.id === fileId)
    if (index > -1) {
      uploadedFiles.value.splice(index, 1)
    }
    ElMessage.success('文件已删除')
    emit('file-deleted')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败: ${error.message}`)
    }
  }
}

/**
 * Clear all files
 */
const clearAll = () => {
  selectedFiles.value = []
  uploadingFiles.value = []
}

/**
 * Format file size
 */
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

/**
 * Format date
 */
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

/**
 * Load uploaded files on mount
 */
const loadUploadedFiles = async () => {
  try {
    const response = await fileService.getFilesByTravelRecord(props.travelRecordId)
    uploadedFiles.value = response.data || []
  } catch (error) {
    console.error('Failed to load uploaded files:', error)
  }
}

onMounted(() => {
  loadUploadedFiles()
})
</script>

<style scoped>
.multimedia-upload {
  width: 100%;
}

.upload-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-area {
  margin-bottom: 20px;
}

.selected-files,
.upload-progress,
.uploaded-files {
  margin-top: 20px;
}

.selected-files h4,
.upload-progress h4,
.uploaded-files h4 {
  margin: 10px 0;
  font-size: 14px;
  font-weight: 600;
}

.progress-item {
  margin-bottom: 15px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 12px;
}

.progress-text {
  color: #909399;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
</style>
