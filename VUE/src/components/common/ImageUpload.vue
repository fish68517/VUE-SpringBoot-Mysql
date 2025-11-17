<template>
  <div class="image-upload">
    <el-upload
      class="upload-container"
      :action="uploadAction"
      :auto-upload="false"
      :show-file-list="false"
      :on-change="handleChange"
      :before-upload="beforeUpload"
      :http-request="handleUpload"
      drag
      accept="image/jpeg,image/png,image/gif"
    >
      <div v-if="!imageUrl" class="upload-placeholder">
        <el-icon class="upload-icon"><Plus /></el-icon>
        <div class="upload-text">Drop image here or click to upload</div>
        <div class="upload-hint">JPG, PNG or GIF (max 5MB)</div>
      </div>
      <div v-else class="image-preview">
        <img :src="imageUrl" alt="Preview" />
        <div class="image-overlay">
          <el-icon class="preview-icon" @click.stop="handlePreview"><ZoomIn /></el-icon>
          <el-icon class="remove-icon" @click.stop="handleRemove"><Delete /></el-icon>
        </div>
      </div>
    </el-upload>

    <!-- Progress indicator -->
    <el-progress
      v-if="uploading"
      :percentage="uploadProgress"
      :status="uploadStatus"
      class="upload-progress"
    />

    <!-- Error message -->
    <div v-if="errorMessage" class="error-message">
      <el-icon><WarningFilled /></el-icon>
      <span>{{ errorMessage }}</span>
    </div>

    <!-- Image preview dialog -->
    <el-dialog v-model="previewVisible" title="Image Preview" width="60%">
      <img :src="imageUrl" alt="Preview" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, ZoomIn, Delete, WarningFilled } from '@element-plus/icons-vue';
import { uploadImage } from '../../api/upload';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:modelValue', 'upload-success', 'upload-error']);

const imageUrl = ref(props.modelValue);
const uploading = ref(false);
const uploadProgress = ref(0);
const uploadStatus = ref('');
const errorMessage = ref('');
const previewVisible = ref(false);
const uploadAction = ref(''); // Not used with custom http-request

// File type validation
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif'];
const MAX_SIZE = 5 * 1024 * 1024; // 5MB

const beforeUpload = (file) => {
  errorMessage.value = '';
  
  // Validate file type
  if (!ALLOWED_TYPES.includes(file.type)) {
    errorMessage.value = 'Only JPG, PNG and GIF formats are supported';
    ElMessage.error(errorMessage.value);
    return false;
  }
  
  // Validate file size
  if (file.size > MAX_SIZE) {
    errorMessage.value = 'Image size must not exceed 5MB';
    ElMessage.error(errorMessage.value);
    return false;
  }
  
  return true;
};

const handleChange = (file) => {
  if (file.raw) {
    // Create preview URL
    const reader = new FileReader();
    reader.onload = (e) => {
      imageUrl.value = e.target.result;
    };
    reader.readAsDataURL(file.raw);
    
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
  
  try {
    // Simulate progress (since we don't have real progress tracking)
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10;
      }
    }, 200);
    
    const response = await uploadImage(file);
    
    clearInterval(progressInterval);
    uploadProgress.value = 100;
    uploadStatus.value = 'success';
    
    // Update image URL with server response
    imageUrl.value = response;
    emit('update:modelValue', response);
    emit('upload-success', response);
    
    ElMessage.success('Image uploaded successfully');
    
    // Reset progress after a delay
    setTimeout(() => {
      uploading.value = false;
      uploadProgress.value = 0;
    }, 1000);
  } catch (error) {
    uploadStatus.value = 'exception';
    errorMessage.value = error.message || 'Upload failed';
    emit('upload-error', error);
    ElMessage.error('Image upload failed: ' + errorMessage.value);
    
    // Reset after error
    setTimeout(() => {
      uploading.value = false;
      uploadProgress.value = 0;
      imageUrl.value = props.modelValue; // Restore original
    }, 2000);
  }
};

const handleRemove = () => {
  imageUrl.value = '';
  errorMessage.value = '';
  uploadProgress.value = 0;
  emit('update:modelValue', '');
};

const handlePreview = () => {
  previewVisible.value = true;
};

// Watch for external changes to modelValue
import { watch } from 'vue';
watch(() => props.modelValue, (newVal) => {
  imageUrl.value = newVal;
});
</script>

<style scoped>
.image-upload {
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
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  border: none;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.upload-icon {
  font-size: 48px;
  color: #8c939d;
  margin-bottom: 16px;
}

.upload-text {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 12px;
  color: #909399;
}

.image-preview {
  position: relative;
  width: 100%;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.image-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-preview:hover .image-overlay {
  opacity: 1;
}

.preview-icon,
.remove-icon {
  font-size: 24px;
  color: white;
  cursor: pointer;
  transition: transform 0.3s;
}

.preview-icon:hover,
.remove-icon:hover {
  transform: scale(1.2);
}

.upload-progress {
  margin-top: 10px;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 8px 12px;
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
  border-radius: 4px;
  color: #f56c6c;
  font-size: 14px;
}

.error-message .el-icon {
  font-size: 16px;
}
</style>
