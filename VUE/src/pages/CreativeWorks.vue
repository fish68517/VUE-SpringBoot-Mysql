<template>
  <div class="creative-works-container">
    <div class="creative-works-wrapper">
      <!-- Upload Section -->
      <div class="upload-section">
        <h2>上传原创作品</h2>
        <form @submit.prevent="handleUploadWork" class="upload-form">
          <!-- Image Upload -->
          <div class="form-group">
            <label for="image-input">作品图片 *</label>
            <div class="image-upload-area" @click="triggerImageInput" :class="{ 'has-image': imagePreview }">
              <input
                id="image-input"
                ref="imageInput"
                type="file"
                accept="image/*"
                @change="handleImageSelect"
                style="display: none"
              />
              <div v-if="!imagePreview" class="upload-placeholder">
                <div class="upload-icon">📸</div>
                <p>点击选择图片或拖拽上传</p>
                <p class="hint">支持 JPG、PNG、GIF 格式，最大 5MB</p>
              </div>
              <div v-else class="image-preview">
                <img :src="imagePreview" :alt="uploadForm.title || '作品预览'" />
                <button type="button" class="remove-image-btn" @click.stop="removeImage">✕</button>
              </div>
            </div>
            <span v-if="uploadErrors.image" class="error-message">{{ uploadErrors.image }}</span>
          </div>

          <!-- Title -->
          <div class="form-group">
            <label for="title">作品标题 *</label>
            <input
              id="title"
              v-model="uploadForm.title"
              type="text"
              placeholder="请输入作品标题"
              maxlength="200"
              @blur="validateTitle"
            />
            <span v-if="uploadErrors.title" class="error-message">{{ uploadErrors.title }}</span>
            <span class="char-count">{{ uploadForm.title.length }}/200</span>
          </div>

          <!-- Description -->
          <div class="form-group">
            <label for="description">作品描述</label>
            <textarea
              id="description"
              v-model="uploadForm.description"
              placeholder="请输入作品描述（可选）"
              rows="5"
              maxlength="1000"
              @blur="validateDescription"
            ></textarea>
            <span v-if="uploadErrors.description" class="error-message">{{ uploadErrors.description }}</span>
            <span class="char-count">{{ uploadForm.description.length }}/1000</span>
          </div>

          <!-- Submit Button -->
          <button type="submit" class="submit-btn" :disabled="uploadLoading">
            <span v-if="!uploadLoading">上传作品</span>
            <span v-else>上传中...</span>
          </button>
        </form>
      </div>

      <!-- Success Message -->
      <div v-if="uploadSuccess" class="success-message">
        <div class="success-content">
          <div class="success-icon">✓</div>
          <h3>上传成功</h3>
          <p>您的作品已提交，等待管理员审核</p>
          <button @click="resetForm" class="reset-btn">继续上传</button>
        </div>
      </div>

      <!-- Works Display Section -->
      <div class="works-section">
        <h2>已审核作品展示</h2>
        
        <!-- Loading State -->
        <div v-if="worksLoading" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>

        <!-- Works Grid -->
        <div v-else-if="works.length > 0" class="works-grid">
          <div
            v-for="work in works"
            :key="work.id"
            class="work-card"
            @click="selectWork(work)"
          >
            <div class="work-image">
              <img :src="work.imageUrl" :alt="work.title" />
              <div class="work-overlay">
                <button class="view-btn">查看详情</button>
              </div>
            </div>
            <div class="work-info">
              <h3 class="work-title">{{ work.title }}</h3>
              <p class="work-creator">创作者: {{ work.user?.nickname || work.user?.username || '匿名' }}</p>
              <div class="work-stats">
                <span class="stat">❤️ {{ work.likeCount || 0 }}</span>
                <span class="stat">💬 {{ work.commentCount || 0 }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="empty-state">
          <p>暂无已审核的作品</p>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button
            @click="previousPage"
            :disabled="currentPage === 0"
            class="page-btn"
          >
            上一页
          </button>
          <span class="page-info">
            第 {{ currentPage + 1 }} / {{ totalPages }} 页
          </span>
          <button
            @click="nextPage"
            :disabled="currentPage >= totalPages - 1"
            class="page-btn"
          >
            下一页
          </button>
        </div>
      </div>

      <!-- Work Detail Modal -->
      <div v-if="selectedWork" class="modal-overlay" @click="closeWorkDetail">
        <div class="modal-content" @click.stop>
          <button class="close-btn" @click="closeWorkDetail">✕</button>
          
          <div class="detail-container">
            <!-- Work Image -->
            <div class="detail-image">
              <img :src="selectedWork.imageUrl" :alt="selectedWork.title" />
            </div>

            <!-- Work Info -->
            <div class="detail-info">
              <h2>{{ selectedWork.title }}</h2>
              <div class="creator-info">
                <div v-if="selectedWork.user?.avatarUrl" class="avatar">
                  <img :src="selectedWork.user.avatarUrl" :alt="selectedWork.user.username" />
                </div>
                <div v-else class="avatar-placeholder">
                  {{ selectedWork.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
                </div>
                <div class="creator-details">
                  <p class="creator-name">{{ selectedWork.user?.nickname || selectedWork.user?.username || '匿名用户' }}</p>
                  <p class="create-time">{{ formatTime(selectedWork.createdAt) }}</p>
                </div>
              </div>

              <div v-if="selectedWork.description" class="description">
                <h4>作品描述</h4>
                <p>{{ selectedWork.description }}</p>
              </div>

              <div class="work-stats-detail">
                <span class="stat">❤️ {{ selectedWork.likeCount || 0 }} 赞</span>
                <span class="stat">💬 {{ selectedWork.commentCount || 0 }} 评论</span>
              </div>
            </div>
          </div>

          <!-- Comments Section -->
          <div class="comments-section">
            <h3>评论 ({{ workComments.length }})</h3>

            <!-- Comment Input -->
            <div v-if="isLoggedIn" class="comment-input">
              <textarea
                v-model="newWorkComment"
                placeholder="发表你的评论..."
                rows="3"
                :disabled="commentSubmitting"
              ></textarea>
              <button
                @click="submitWorkComment"
                :disabled="!newWorkComment.trim() || commentSubmitting"
                class="submit-btn"
              >
                {{ commentSubmitting ? '发布中...' : '发布评论' }}
              </button>
            </div>

            <!-- Login Prompt -->
            <div v-else class="login-prompt">
              <p>登录后可以发表评论</p>
              <router-link to="/login" class="login-link">立即登录</router-link>
            </div>

            <!-- Comments List -->
            <div v-if="workComments.length > 0" class="comments-list">
              <div v-for="comment in workComments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <div class="user-info">
                    <div v-if="comment.user?.avatarUrl" class="avatar">
                      <img :src="comment.user.avatarUrl" :alt="comment.user.username" />
                    </div>
                    <div v-else class="avatar-placeholder">
                      {{ comment.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
                    </div>
                    <div class="user-details">
                      <p class="username">{{ comment.user?.nickname || comment.user?.username || '匿名用户' }}</p>
                      <p class="timestamp">{{ formatTime(comment.createdAt) }}</p>
                    </div>
                  </div>
                  <button
                    v-if="canDeleteComment(comment)"
                    @click="deleteWorkComment(comment.id)"
                    class="delete-btn"
                    :disabled="commentDeleting"
                  >
                    删除
                  </button>
                </div>
                <p class="comment-content">{{ comment.content }}</p>
              </div>
            </div>

            <!-- Empty Comments -->
            <div v-else class="empty-comments">
              <p>暂无评论，来发表第一条评论吧</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { workAPI, commentAPI } from '../services/api'
import { useUserStore } from '../store'

import axios from 'axios' // 1. 必须引入 axios

const userStore = useUserStore()
const imageInput = ref(null)

// State
const uploadForm = reactive({
  title: '',
  description: '',
  imageUrl: ''
})

const uploadErrors = reactive({
  title: '',
  description: '',
  image: ''
})

const imagePreview = ref('')
const uploadLoading = ref(false)
const uploadSuccess = ref(false)
const selectedFile = ref(null)

// Works display state
const works = ref([])
const worksLoading = ref(false)
const currentPage = ref(0)
const pageSize = ref(12)
const totalPages = ref(0)
const selectedWork = ref(null)
const workComments = ref([])
const newWorkComment = ref('')
const commentSubmitting = ref(false)
const commentDeleting = ref(false)

// Computed
const isLoggedIn = computed(() => userStore.isLoggedIn)

// Methods - Upload
const triggerImageInput = () => {
  imageInput.value?.click()
}

// src/pages/CreativeWorks.vue

const handleImageSelect = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 校验文件大小和类型 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    uploadErrors.value.image = '图片大小不能超过 5MB';
    return;
  }

  const formData = new FormData();
  formData.append('file', file);

  try {
    // 假设后端上传接口为 /api/common/upload
    const res = await axios.post('/api/common/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    console.log('上传成功：', res.data);
    
    // 3. 修复点：reactive 对象没有 .value！直接赋值
    if (res != null ) {
      uploadForm.imageUrl = res.data.url // 去掉 .value
      imagePreview.value = URL.createObjectURL(file)
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(res.data.message || '上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error);
    ElMessage.error('图片上传失败');
  }
};

const removeImage = () => {
  imagePreview.value = ''
  selectedFile.value = null
  uploadForm.imageUrl = ''
  uploadErrors.image = ''
  if (imageInput.value) {
    imageInput.value.value = ''
  }
}

const validateTitle = () => {
  uploadErrors.title = ''
  if (!uploadForm.title.trim()) {
    uploadErrors.title = '作品标题不能为空'
  } else if (uploadForm.title.length > 200) {
    uploadErrors.title = '作品标题最多 200 个字符'
  }
}

const validateDescription = () => {
  uploadErrors.description = ''
  if (uploadForm.description.length > 1000) {
    uploadErrors.description = '作品描述最多 1000 个字符'
  }
}

const validateForm = () => {
  validateTitle()
  validateDescription()

  if (!imagePreview.value) {
    uploadErrors.image = '请选择作品图片'
  }

  return !uploadErrors.title && !uploadErrors.description && !uploadErrors.image
}

const handleUploadWork = async () => {
  if (!validateForm()) {
    ElMessage.error('请修正表单错误')
    return
  }

  uploadLoading.value = true
  try {
    const userId = userStore.user?.id
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }

    // Convert image to base64 for API submission
    const base64Image = imagePreview.value

    const response = await workAPI.createWork({
      userId: userId,
      title: uploadForm.title.trim(),
      description: uploadForm.description.trim(),
      imageUrl: uploadForm.imageUrl,
    })

    if (response.code === 200) {
      uploadSuccess.value = true
      ElMessage.success('作品上传成功，等待审核')
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    console.error('Failed to upload work:', error)
    const errorMessage = error.response?.data?.message || '上传作品失败'
    ElMessage.error(errorMessage)
  } finally {
    uploadLoading.value = false
  }
}

const resetForm = () => {
  uploadForm.title = ''
  uploadForm.description = ''
  uploadForm.imageUrl = ''
  uploadErrors.title = ''
  uploadErrors.description = ''
  uploadErrors.image = ''
  imagePreview.value = ''
  selectedFile.value = null
  uploadSuccess.value = false
  if (imageInput.value) {
    imageInput.value.value = ''
  }
}

// Methods - Works Display
const fetchWorks = async () => {
  worksLoading.value = true
  try {
    const response = await workAPI.getWorks({
      page: currentPage.value,
      size: pageSize.value,
      status: 'approved'
    })

    if (response && response.data) {
      works.value = response.data.content || []
      totalPages.value = response.data.totalPages || 0
    }
  } catch (error) {
    console.error('Failed to fetch works:', error)
    ElMessage.error('加载作品失败')
  } finally {
    worksLoading.value = false
  }
}

const selectWork = async (work) => {
  selectedWork.value = work
  await fetchWorkComments(work.id)
}

const closeWorkDetail = () => {
  selectedWork.value = null
  workComments.value = []
  newWorkComment.value = ''
}

const fetchWorkComments = async (workId) => {
  try {
    const response = await commentAPI.getComments({
      workId: workId,
      page: 0,
      size: 100
    })

    if (response && response.data) {
      workComments.value = response.data.content || []
    }
  } catch (error) {
    console.error('Failed to fetch work comments:', error)
    workComments.value = []
  }
}

const submitWorkComment = async () => {
  if (!newWorkComment.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  commentSubmitting.value = true
  try {
    await commentAPI.createComment({
      userId: userStore.user?.id,
      workId: selectedWork.value.id,
      content: newWorkComment.value.trim()
    })

    ElMessage.success('评论发布成功')
    newWorkComment.value = ''
    await fetchWorkComments(selectedWork.value.id)
  } catch (error) {
    console.error('Failed to submit comment:', error)
    ElMessage.error('发布评论失败，请重试')
  } finally {
    commentSubmitting.value = false
  }
}

const deleteWorkComment = async (commentId) => {
  if (!confirm('确定要删除这条评论吗？')) {
    return
  }

  commentDeleting.value = true
  try {
    await commentAPI.deleteComment(commentId)
    ElMessage.success('评论已删除')
    await fetchWorkComments(selectedWork.value.id)
  } catch (error) {
    console.error('Failed to delete comment:', error)
    ElMessage.error('删除评论失败，请重试')
  } finally {
    commentDeleting.value = false
  }
}

const canDeleteComment = (comment) => {
  return userStore.user && (userStore.user.id === comment.userId || userStore.user.roleId === 1)
}

const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date

  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  return date.toLocaleDateString('zh-CN')
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchWorks()
  }
}

const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchWorks()
  }
}

// Lifecycle
onMounted(() => {
  fetchWorks()
})
</script>

<style scoped>
.creative-works-container {
  min-height: calc(100vh - 80px);
  background: #f5f7fa;
  padding: 2rem 1rem;
}

.creative-works-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

/* Upload Section */
.upload-section {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.upload-section h2 {
  margin: 0 0 2rem 0;
  color: #333;
  font-size: 1.5rem;
  border-bottom: 2px solid #f0f2ff;
  padding-bottom: 1rem;
}

/* Form Group */
.form-group {
  margin-bottom: 2rem;
  position: relative;
}

.form-group label {
  display: block;
  margin-bottom: 0.75rem;
  color: #333;
  font-weight: 600;
  font-size: 0.95rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.95rem;
  font-family: inherit;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.char-count {
  display: block;
  margin-top: 0.3rem;
  color: #999;
  font-size: 0.85rem;
  text-align: right;
}

/* Image Upload Area */
.image-upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f9fafb;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-upload-area:hover {
  border-color: #667eea;
  background: #f0f2ff;
}

.image-upload-area.has-image {
  border-color: #667eea;
  background: white;
  padding: 0;
  min-height: auto;
}

.upload-placeholder {
  width: 100%;
}

.upload-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.upload-placeholder p {
  margin: 0.5rem 0;
  color: #333;
}

.upload-placeholder p.hint {
  color: #999;
  font-size: 0.85rem;
}

/* Image Preview */
.image-preview {
  position: relative;
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
}

.remove-image-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
}

.remove-image-btn:hover {
  background: rgba(0, 0, 0, 0.8);
}

/* Error Message */
.error-message {
  display: block;
  color: #ff6b6b;
  font-size: 0.85rem;
  margin-top: 0.3rem;
}

/* Submit Button */
.submit-btn {
  width: 100%;
  padding: 0.875rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Success Message */
.success-message {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.success-content {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  max-width: 400px;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.success-icon {
  font-size: 3rem;
  color: #52c41a;
  margin-bottom: 1rem;
}

.success-content h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.3rem;
}

.success-content p {
  margin: 0 0 1.5rem 0;
  color: #999;
  font-size: 0.95rem;
}

.reset-btn {
  padding: 0.75rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.reset-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.reset-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* Works Section */
.works-section {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-top: 2rem;
}

.works-section h2 {
  margin: 0 0 2rem 0;
  color: #333;
  font-size: 1.5rem;
  border-bottom: 2px solid #f0f2ff;
  padding-bottom: 1rem;
}

/* Works Grid */
.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.work-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.work-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
}

.work-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.work-card:hover .work-image img {
  transform: scale(1.05);
}

.work-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.work-card:hover .work-overlay {
  opacity: 1;
}

.view-btn {
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background 0.3s;
}

.view-btn:hover {
  background: #5568d3;
}

.work-info {
  padding: 1rem;
}

.work-title {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1rem;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.work-creator {
  margin: 0 0 0.75rem 0;
  color: #999;
  font-size: 0.85rem;
}

.work-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.stat {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.work-stats-detail {
  display: flex;
  gap: 1.5rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
  font-size: 0.95rem;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: #999;
}

.empty-comments {
  text-align: center;
  padding: 2rem 1rem;
  color: #999;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.page-btn {
  padding: 0.5rem 1rem;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
  border-color: #999;
}

.page-btn:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 0.95rem;
}

/* Modal Overlay */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 900px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
  z-index: 10;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.8);
}

.detail-container {
  display: flex;
  gap: 2rem;
  padding: 2rem;
}

.detail-image {
  flex: 0 0 40%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f0f0;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-info {
  flex: 1;
}

.detail-info h2 {
  margin: 0 0 1.5rem 0;
  color: #333;
  font-size: 1.5rem;
}

.creator-info {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
  flex-shrink: 0;
}

.creator-details {
  flex: 1;
}

.creator-name {
  margin: 0 0 0.3rem 0;
  color: #333;
  font-weight: 600;
}

.create-time {
  margin: 0;
  color: #999;
  font-size: 0.85rem;
}

.description {
  margin-bottom: 1.5rem;
}

.description h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 0.95rem;
}

.description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

/* Comments Section */
.comments-section {
  padding: 2rem;
  border-top: 1px solid #eee;
}

.comments-section h3 {
  margin: 0 0 1.5rem 0;
  color: #333;
  font-size: 1.2rem;
}

.comment-input {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #eee;
}

.comment-input textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.95rem;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.3s;
}

.comment-input textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.comment-input .submit-btn {
  align-self: flex-end;
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background 0.3s;
}

.comment-input .submit-btn:hover:not(:disabled) {
  background: #5568d3;
}

.comment-input .submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.login-prompt {
  text-align: center;
  padding: 1.5rem;
  background: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 1.5rem;
}

.login-prompt p {
  margin: 0 0 0.75rem 0;
  color: #666;
}

.login-link {
  display: inline-block;
  background: #667eea;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  transition: background 0.3s;
}

.login-link:hover {
  background: #5568d3;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 4px;
  border-left: 3px solid #667eea;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.75rem;
}

.user-info {
  display: flex;
  gap: 0.75rem;
  align-items: flex-start;
  flex: 1;
}

.user-info .avatar {
  width: 36px;
  height: 36px;
}

.user-info .avatar-placeholder {
  width: 36px;
  height: 36px;
  font-size: 1rem;
}

.user-details {
  flex: 1;
}

.username {
  margin: 0 0 0.2rem 0;
  color: #333;
  font-weight: 600;
  font-size: 0.9rem;
}

.timestamp {
  margin: 0;
  color: #999;
  font-size: 0.8rem;
}

.delete-btn {
  padding: 0.4rem 0.8rem;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.8rem;
  cursor: pointer;
  transition: background 0.3s;
  white-space: nowrap;
}

.delete-btn:hover:not(:disabled) {
  background: #ff5252;
}

.delete-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.comment-content {
  margin: 0;
  color: #555;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 0.95rem;
}

/* Responsive */
@media (max-width: 768px) {
  .creative-works-container {
    padding: 1rem 0.5rem;
  }

  .upload-section {
    padding: 1.5rem;
  }

  .upload-section h2 {
    font-size: 1.2rem;
  }

  .image-preview {
    height: 250px;
  }

  .success-content {
    margin: 0 1rem;
  }

  .works-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .creative-works-container {
    padding: 1rem 0;
  }

  .upload-section {
    padding: 1rem;
    border-radius: 0;
  }

  .upload-section h2 {
    font-size: 1.1rem;
    margin-bottom: 1.5rem;
  }

  .form-group {
    margin-bottom: 1.5rem;
  }

  .image-upload-area {
    padding: 1.5rem;
    min-height: 150px;
  }

  .upload-icon {
    font-size: 2rem;
    margin-bottom: 0.5rem;
  }

  .image-preview {
    height: 200px;
  }

  .success-content {
    padding: 1.5rem;
    margin: 0 0.5rem;
  }

  .success-icon {
    font-size: 2rem;
  }

  .works-grid {
    grid-template-columns: 1fr;
  }

  .modal-content {
    width: 95%;
    max-height: 95vh;
  }

  .detail-container {
    flex-direction: column;
  }

  .detail-image {
    width: 100%;
    height: 300px;
  }

  .detail-info {
    width: 100%;
  }
}
</style>
