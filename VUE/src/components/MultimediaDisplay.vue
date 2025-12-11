<template>
  <div class="multimedia-display">
    <el-card v-if="files.length > 0" class="display-card">
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><Picture /></el-icon>
            多媒体内容. ({{ files.length }})
          </span>
        </div>
      </template>

      <!-- Gallery Grid -->
      <div class="gallery-grid">
        <div
          v-for="file in files"
          :key="file.id"
          class="gallery-item"
          @click="openPreview(file)"
        >
          <!-- Image Thumbnail -->
          <div v-if="isImage(file)" class="thumbnail-container">
            <img
              v-lazy="getFileUrl(file)"
              :alt="file.fileName"
              class="thumbnail"
              loading="lazy"
              @error="handleImageError"
            />
            <div class="overlay">
              <el-icon class="preview-icon"><ZoomIn /></el-icon>
            </div>
          </div>

          <!-- Video Thumbnail -->
          <div v-else-if="isVideo(file)" class="thumbnail-container video-thumbnail">
            <video :src="getFileUrl(file)" class="thumbnail" @error="handleVideoError" />
            <div class="overlay">
              <el-icon class="preview-icon"><VideoPlay /></el-icon>
            </div>
          </div>

          <!-- File Info -->
          <div class="file-info">
            <p class="file-name" :title="file.fileName">{{ file.fileName }}</p>
            <p class="file-size">{{ formatFileSize(file.fileSize) }}</p>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Image Preview Dialog -->
    <el-dialog
      v-model="previewVisible"
      :title="currentFile?.fileName"
      width="90%"
      :close-on-click-modal="true"
      class="preview-dialog"
    >
      <div class="preview-content">
        <!-- Image Preview -->
        <img
          v-if="currentFile && isImage(currentFile)"
          :src="getFileUrl(currentFile)"
          :alt="currentFile.fileName"
          class="preview-image"
        />

        <!-- Video Preview -->
        <video
          v-else-if="currentFile && isVideo(currentFile)"
          :src="getFileUrl(currentFile)"
          controls
          class="preview-video"
        />
      </div>

      <!-- File Details -->
      <div class="preview-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名">
            {{ currentFile?.fileName }}
          </el-descriptions-item>
          <el-descriptions-item label="文件大小">
            {{ formatFileSize(currentFile?.fileSize) }}
          </el-descriptions-item>
          <el-descriptions-item label="文件类型">
            {{ currentFile?.fileType }}
          </el-descriptions-item>
          <el-descriptions-item label="上传时间">
            {{ formatDate(currentFile?.uploadDate) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Picture, ZoomIn, VideoPlay } from '@element-plus/icons-vue'

const props = defineProps({
  files: {
    type: Array,
    default: () => []
  },
  baseUrl: {
    type: String,
    default: 'http://localhost:8080'
  }
})

const previewVisible = ref(false)
const currentFile = ref(null)

// Image file types
const imageTypes = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp']
// Video file types
const videoTypes = ['mp4', 'avi', 'mov', 'mkv', 'webm', 'flv']

/**
 * Check if file is an image
 */
const isImage = (file) => {
  // 打印 file
  console.log('%c[Image Type Debug]file .....' + JSON.stringify(file));
  // "fileType":"image/jpeg"  截取文件类型
  // 从 "image/jpeg" 或 "image/png" 中提取 jpeg/png/webp 等
  const fileTypeSub = (file.fileType || '').split('/').pop().toLowerCase();

  console.log('%c[Image Type Debug] 提取的后缀:', 'color: #409eff', fileTypeSub);

  
  const ext = fileTypeSub?.toLowerCase() || ''
  // 打印return 返回值
  const isImage = imageTypes.includes(ext)
  console.log('[Image Type Debug]' + JSON.stringify(isImage));
  return isImage
}

/**
 * Check if file is a video
 */
const isVideo = (file) => {
   console.log('%c[isVideo Type Debug]file .....' + JSON.stringify(file));
  // "fileType":"image/jpeg"  截取文件类型
  // 从 "image/jpeg" 或 "image/png" 中提取 jpeg/png/webp 等
  const fileTypeSub = (file.fileType || '').split('/').pop().toLowerCase();

  console.log('%c[isVideo Type Debug] 提取的后缀:', 'color: #409eff', fileTypeSub);
  const ext = fileTypeSub?.toLowerCase() || ''
  return videoTypes.includes(ext)
}

/**
 * Get full file URL
 */
const getFileUrl = (file) => {
  // 1. 安全检查：如果没有文件或路径，返回空
  console.log('%c[File URL Debug]', 'file is null or path is null');
  if (!file || !file.filePath) return ''

  // 2. 如果已经是完整 http/https 链接，直接返回
  if (file.filePath.startsWith('http')) {
    return file.filePath
  }

  // 3. 规范化 BaseUrl：去掉末尾可能存在的斜杠
  // 假设 props.baseUrl 是 "http://localhost:8080"
  let cleanBase = props.baseUrl || '';
  if (cleanBase.endsWith('/')) {
    cleanBase = cleanBase.slice(0, -1);
  }

  // 4. 规范化 FilePath：确保开头有斜杠
  // 假设 file.filePath 是 "uploads/records/..."
  let cleanPath = file.filePath;
  if (!cleanPath.startsWith('/')) {
    cleanPath = '/' + cleanPath;
  }

  // 5. 关键修改：拼接 /api
  // 逻辑：BaseUrl + ContextPath + FilePath
  const contextPath = '/api';
  
  // 防御性编程：如果 props.baseUrl 本身已经包含了 /api，就不要重复添加
  let fullUrl = '';
  if (cleanBase.endsWith(contextPath)) {
      fullUrl = `${cleanBase}${cleanPath}`;
  } else {
      fullUrl = `${cleanBase}${contextPath}${cleanPath}`;
  }

  // 6. 增加调试日志
  console.log('%c[Image URL Debug]', 'color: blue; font-weight: bold;');
  console.log('1. BaseUrl:', props.baseUrl);
  console.log('2. FilePath:', file.filePath);
  console.log('3. Result  :', fullUrl);

  return fullUrl
}

/**
 * Open preview dialog
 */
const openPreview = (file) => {
  currentFile.value = file
  previewVisible.value = true
}

/**
 * Format file size
 */
const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
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
 * Handle image load error
 */
const handleImageError = (event) => {
  event.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="100" height="100"%3E%3Crect fill="%23f0f0f0" width="100" height="100"/%3E%3Ctext x="50" y="50" text-anchor="middle" dy=".3em" fill="%23999" font-size="12"%3EFailed to load%3C/text%3E%3C/svg%3E'
  event.target.classList.add('load-error')
}

/**
 * Handle video load error
 */
const handleVideoError = (event) => {
  event.target.classList.add('load-error')
}
</script>

<style scoped>
.multimedia-display {
  width: 100%;
}

.display-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  padding: 10px 0;
}

.gallery-item {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.gallery-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.thumbnail-container {
  position: relative;
  width: 100%;
  padding-bottom: 100%;
  overflow: hidden;
  background: #f0f0f0;
}

.thumbnail-container.video-thumbnail {
  background: #000;
}

.thumbnail {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail.lazy-loading {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.thumbnail.lazy-error {
  opacity: 0.5;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.gallery-item:hover .overlay {
  opacity: 1;
}

.preview-icon {
  font-size: 32px;
  color: #fff;
}

.file-info {
  padding: 10px;
  background: #fff;
}

.file-name {
  margin: 0;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.file-size {
  margin: 5px 0 0 0;
  font-size: 11px;
  color: #999;
}

.preview-dialog {
  --el-dialog-width: 90%;
}

.preview-content {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  max-height: 600px;
  overflow: auto;
}

.preview-image {
  max-width: 100%;
  max-height: 500px;
  border-radius: 4px;
}

.preview-video {
  max-width: 100%;
  max-height: 500px;
  border-radius: 4px;
}

.preview-details {
  margin-top: 20px;
}

/* Lazy loading placeholder */
:deep(.el-image__placeholder) {
  background: #f0f0f0;
}

/* Responsive grid */
@media (max-width: 768px) {
  .gallery-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 10px;
  }

  .preview-content {
    max-height: 400px;
  }

  .preview-image,
  .preview-video {
    max-height: 350px;
  }
}

@media (max-width: 480px) {
  .gallery-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 8px;
  }

  .preview-content {
    max-height: 300px;
    padding: 10px;
  }

  .preview-image,
  .preview-video {
    max-height: 250px;
  }
}
</style>
