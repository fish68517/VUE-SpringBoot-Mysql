<template>
  <div class="video-upload">
    <el-upload
      class="upload-container"
      :action="uploadAction"
      :auto-upload="false"
      :show-file-list="false"
      :on-change="handleChange"
      :before-upload="beforeUpload"
      :http-request="handleUpload"
      drag
      accept="video/mp4,video/x-msvideo"
    >
      <div v-if="!videoUrl && !uploading" class="upload-placeholder">
        <el-icon class="upload-icon"><VideoCamera /></el-icon>
        <div class="upload-text">Drop video here or click to upload</div>
        <div class="upload-hint">MP4 or AVI (max 100MB)</div>
      </div>
      <div v-else-if="videoUrl && !uploading" class="video-preview">
        <video :src="videoUrl" controls class="video-player"></video>
        <div class="video-actions">
          <el-button type="danger" size="small" @click.stop="handleRemove">
            <el-icon><Delete /></el-icon>
            Remove Video
          </el-button>
        </div>
      </div>
      <div v-else class="uploading-state">
        <el-icon class="uploading-icon"><Loading /></el-icon>
        <div class="uploading-text">Uploading video...</div>
      </div>
    </el-upload>

    <!-- Progress bar -->
    <div v-if="uploading" class="progress-section">
      <el-progress
        :percentage="uploadProgress"
        :status="uploadStatus"
        :stroke-width="20"
      >
        <template #default="{ percentage }">
          <span class="progress-text">{{ percentage }}%</span>
        </template>
      </el-progress>
      <el-button
        type="warning"
        size="small"
        class="cancel-button"
        @click="handleCancel"
      >
        <el-icon><Close /></el-icon>
        Cancel Upload
      </el-button>
    </div>

    <!-- File info -->
    <div v-if="fileInfo && !uploading" class="file-info">
      <el-icon><Document /></el-icon>
      <span class="file-name">{{ fileInfo.name }}</span>
      <span class="file-size">{{ formatFileSize(fileInfo.size) }}</span>
    </div>

    <!-- Error message -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      @close="errorMessage = ''"
      class="error-alert"
      show-icon
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { VideoCamera, Delete, Loading, Close, Document } from '@element-plus/icons-vue';
import { uploadVideo } from '../../api/upload';
import { showSuccess, showError, showInfo } from '@/utils/feedback';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:modelValue', 'upload-success', 'upload-error']);

const videoUrl = ref(props.modelValue);
const uploading = ref(false);
const uploadProgress = ref(0);
const uploadStatus = ref('');
const errorMessage = ref('');
const fileInfo = ref(null);
const uploadAction = ref(''); // Not used with custom http-request
const cancelUpload = ref(false);

// File type validation
const ALLOWED_TYPES = ['video/mp4', 'video/x-msvideo'];
const ALLOWED_EXTENSIONS = ['.mp4', '.avi'];
const MAX_SIZE = 100 * 1024 * 1024; // 100MB

const beforeUpload = (file) => {
  errorMessage.value = '';
  
  // Validate file type
  const isValidType = ALLOWED_TYPES.includes(file.type) || 
                      ALLOWED_EXTENSIONS.some(ext => file.name.toLowerCase().endsWith(ext));
  
  if (!isValidType) {
    errorMessage.value = 'Only MP4 and AVI video formats are supported';
    showError(errorMessage.value);
    return false;
  }
  
  // Validate file size
  if (file.size > MAX_SIZE) {
    errorMessage.value = 'Video size must not exceed 100MB';
    showError(errorMessage.value);
    return false;
  }
  
  return true;
};

const handleChange = (file) => {
  if (file.raw) {
    fileInfo.value = {
      name: file.name,
      size: file.size
    };
    
    // Start upload
    handleUpload({ file: file.raw });
  }
};

const handleUpload = async ({ file }) => {
  if (!beforeUpload(file)) {
    return;
  }
  
  uploading.value = true;
  uploadProgress.value = 0;
  uploadStatus.value = '';
  errorMessage.value = '';
  cancelUpload.value = false;
  
  try {
    // Simulate progress for large video files
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 85 && !cancelUpload.value) {
        // Slower progress for videos
        uploadProgress.value += 5;
      }
    }, 500);
    
    const response = await uploadVideo(file);
    
    if (cancelUpload.value) {
      clearInterval(progressInterval);
      uploading.value = false;
      uploadProgress.value = 0;
      fileInfo.value = null;
      return;
    }
    
    clearInterval(progressInterval);
    uploadProgress.value = 100;
    uploadStatus.value = 'success';
    
    // Update video URL with server response
    videoUrl.value = response;
    emit('update:modelValue', response);
    emit('upload-success', response);
    
    showSuccess('Video uploaded successfully');
    
    // Reset progress after a delay
    setTimeout(() => {
      uploading.value = false;
      uploadProgress.value = 0;
    }, 1500);
  } catch (error) {
    if (cancelUpload.value) {
      // User cancelled, don't show error
      uploading.value = false;
      uploadProgress.value = 0;
      fileInfo.value = null;
      return;
    }
    
    uploadStatus.value = 'exception';
    errorMessage.value = error.message || 'Upload failed';
    emit('upload-error', error);
    showError('Video upload failed: ' + errorMessage.value);
    
    // Reset after error
    setTimeout(() => {
      uploading.value = false;
      uploadProgress.value = 0;
      videoUrl.value = props.modelValue; // Restore original
      fileInfo.value = null;
    }, 2000);
  }
};

const handleCancel = () => {
  cancelUpload.value = true;
  uploading.value = false;
  uploadProgress.value = 0;
  uploadStatus.value = '';
  fileInfo.value = null;
  showInfo('Upload cancelled');
};

const handleRemove = () => {
  videoUrl.value = '';
  errorMessage.value = '';
  uploadProgress.value = 0;
  fileInfo.value = null;
  emit('update:modelValue', '');
};

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
};

// Watch for external changes to modelValue
import { watch } from 'vue';
watch(() => props.modelValue, (newVal) => {
  videoUrl.value = newVal;
  if (newVal) {
    // If there's a URL, we don't need file info anymore
    fileInfo.value = null;
  }
});
</script>

<style scoped>
.video-upload {
  width: 100%;
}

.upload-container {
  width: 100%;
}

.upload-container :deep(.el-upload) {
  width: 100%;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.upload-container :deep(.el-upload:hover) {
  border-color: #409eff;
}

.upload-container :deep(.el-upload-dragger) {
  width: 100%;
  min-height: 250px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  border: none;
  padding: 20px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.upload-icon {
  font-size: 64px;
  color: #8c939d;
  margin-bottom: 20px;
}

.upload-text {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
  font-weight: 500;
}

.upload-hint {
  font-size: 13px;
  color: #909399;
}

.uploading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.uploading-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 16px;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.uploading-text {
  font-size: 14px;
  color: #606266;
}

.video-preview {
  width: 100%;
  padding: 20px;
}

.video-player {
  width: 100%;
  max-height: 400px;
  border-radius: 4px;
  background-color: #000;
}

.video-actions {
  margin-top: 15px;
  display: flex;
  justify-content: center;
}

.progress-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.progress-section .el-progress {
  margin-bottom: 15px;
}

.progress-text {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
}

.cancel-button {
  width: 100%;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 15px;
  padding: 12px;
  background-color: #f0f9ff;
  border: 1px solid #d1e7fd;
  border-radius: 4px;
  font-size: 14px;
}

.file-info .el-icon {
  font-size: 18px;
  color: #409eff;
}

.file-name {
  flex: 1;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  color: #909399;
  font-size: 13px;
}

.error-alert {
  margin-top: 15px;
}
</style>
