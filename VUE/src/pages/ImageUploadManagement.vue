<template>
  <div class="image-upload-management-layout">
    <Sidebar />

    <div class="main-content">
      <Header />

      <div class="content-area">
        <div class="management-container">
          <div class="page-header">
            <h2>图片上传管理</h2>
            <p>上传和管理系统中使用的图片资源</p>
          </div>

          <div class="upload-section">
            <el-card class="box-card">
              <template #header>
                <div class="card-header">
                  <span>上传新图片</span>
                </div>
              </template>

              <ImageUpload />
            </el-card>
          </div>

          <div class="uploaded-images-section">
            <el-card class="box-card">
              <template #header>
                <div class="card-header">
                  <span>已上传的图片</span>
                  <el-button type="primary" size="small" @click="loadUploadedImages">
                    刷新
                  </el-button>
                </div>
              </template>

              <div v-if="loadingImages" class="loading-state">
                <el-icon class="is-loading"><Loading /></el-icon>
                <p>加载中...</p>
              </div>

              <div v-else-if="uploadedImages.length === 0" class="empty-state">
                <el-icon><Picture /></el-icon>
                <p>暂无已上传的图片</p>
              </div>

              <div v-else class="images-grid">
                <div v-for="image in uploadedImages" :key="image.id" class="image-card">
                  <div class="image-wrapper">
                    <img :src="image.path" :alt="image.filename" class="image-thumbnail" />
                    <div class="image-overlay">
                      <el-button
                        type="primary"
                        size="small"
                        @click="copyImagePath(image.path)"
                      >
                        复制路径
                      </el-button>
                      <el-button
                        type="danger"
                        size="small"
                        @click="deleteImage(image.id)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                  <div class="image-info">
                    <p class="filename">{{ image.filename }}</p>
                    <p class="upload-time">{{ formatDate(image.uploadTime) }}</p>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Loading, Picture } from '@element-plus/icons-vue'
import Sidebar from '../components/Sidebar.vue'
import Header from '../components/Header.vue'
import ImageUpload from '../components/ImageUpload.vue'
import { showSuccess, showError } from '../utils/notification.js'

const uploadedImages = ref([])
const loadingImages = ref(false)

const loadUploadedImages = async () => {
  loadingImages.value = true
  try {
    // In a real application, this would fetch from an API endpoint
    // For now, we'll use localStorage to store uploaded images
    const stored = localStorage.getItem('uploadedImages')
    uploadedImages.value = stored ? JSON.parse(stored) : []
  } catch (error) {
    console.error('Failed to load images:', error)
    showError('加载图片失败，请重试', '加载失败')
  } finally {
    loadingImages.value = false
  }
}

const copyImagePath = (path) => {
  navigator.clipboard.writeText(path).then(() => {
    showSuccess('路径已复制到剪贴板', '复制成功')
  }).catch(() => {
    showError('复制失败，请重试', '复制失败')
  })
}

const deleteImage = (imageId) => {
  ElMessageBox.confirm(
    '确定要删除这张图片吗？此操作不可撤销。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    uploadedImages.value = uploadedImages.value.filter(img => img.id !== imageId)
    localStorage.setItem('uploadedImages', JSON.stringify(uploadedImages.value))
    showSuccess('图片已删除', '删除成功')
  }).catch(() => {
    // User cancelled
  })
}

const formatDate = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadUploadedImages()
})
</script>

<style scoped>
.image-upload-management-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.management-container {
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 10px 0;
  letter-spacing: -0.5px;
}

.page-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.upload-section {
  margin-bottom: 30px;
  animation: slideUp 0.5s ease 0.1s both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.box-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  border: none;
  animation: slideUp 0.5s ease 0.2s both;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #e0e0e0;
  padding: 16px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  font-weight: 600;
  color: #333;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.image-card {
  border-radius: 12px;
  overflow: hidden;
  background: white;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.image-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.image-wrapper {
  position: relative;
  width: 100%;
  padding-bottom: 100%;
  overflow: hidden;
  background: #f5f7fa;
}

.image-thumbnail {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-card:hover .image-thumbnail {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-card:hover .image-overlay {
  opacity: 1;
}

.image-overlay :deep(.el-button) {
  border-radius: 4px;
  font-size: 12px;
  padding: 6px 12px;
}

.image-info {
  padding: 12px;
}

.filename {
  font-size: 12px;
  color: #333;
  margin: 0 0 5px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.upload-time {
  font-size: 11px;
  color: #999;
  margin: 0;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
}

.loading-state :deep(.el-icon) {
  font-size: 48px;
  margin-bottom: 10px;
  animation: rotating 2s linear infinite;
  color: #409eff;
}

.empty-state :deep(.el-icon) {
  font-size: 48px;
  margin-bottom: 10px;
  color: #c0c4cc;
}

.loading-state p,
.empty-state p {
  font-size: 14px;
  margin: 0;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
