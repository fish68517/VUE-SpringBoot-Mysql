<template>
  <div class="admin-review">
    
    <div class="admin-container">
      
      <div class="review-content">
        <h1>内容审核</h1>

        <!-- Tab Navigation -->
        <div class="tab-navigation">
          <button
            :class="['tab-btn', { active: activeTab === 'works' }]"
            @click="activeTab = 'works'"
          >
            原创作品审核
          </button>
          <button
            :class="['tab-btn', { active: activeTab === 'comments' }]"
            @click="activeTab = 'comments'"
          >
            评论审核
          </button>
        </div>

        <!-- Works Review Section -->
        <div v-if="activeTab === 'works'" class="review-section">
          <h2>待审核原创作品</h2>

          <!-- Filter Bar -->
          <div class="filter-bar">
            <select v-model="workStatusFilter">
              <option value="">全部状态</option>
              <option value="pending">待审核</option>
              <option value="approved">已通过</option>
              <option value="rejected">已拒绝</option>
            </select>
            <button @click="loadPendingWorks" class="btn btn-secondary">刷新</button>
          </div>

          <!-- Loading State -->
          <div v-if="loadingWorks" class="loading-indicator">
            <div class="spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- Works List -->
          <div v-else-if="pendingWorks.length > 0" class="works-list">
            <div v-for="work in pendingWorks" :key="work.id" class="work-card">
              <div class="work-header">
                <div class="work-info">
                  <h3>{{ work.title }}</h3>
                  <p class="work-creator">创作者: {{ work.user?.username || '未知用户' }}</p>
                  <p class="work-date">提交时间: {{ formatDate(work.createdAt) }}</p>
                </div>
                <span :class="['status-badge', `status-${work.status}`]">
                  {{ getStatusLabel(work.status) }}
                </span>
              </div>

              <div class="work-preview">
                <img :src="work.imageUrl" :alt="work.title" class="work-image" />
                <div class="work-details">
                  <p v-if="work.description" class="work-description">
                    <strong>描述:</strong> {{ work.description }}
                  </p>
                  <p class="work-stats">
                    <span>点赞: {{ work.likeCount }}</span>
                  </p>
                </div>
              </div>

              <!-- Action Buttons -->
              <div v-if="work.status === 'pending'" class="work-actions">
                <button
                  @click="handleApproveWork(work.id)"
                  class="btn btn-success"
                  :disabled="reviewingWorkId === work.id"
                >
                  {{ reviewingWorkId === work.id ? '处理中...' : '通过' }}
                </button>
                <button
                  @click="handleRejectWork(work.id)"
                  class="btn btn-danger"
                  :disabled="reviewingWorkId === work.id"
                >
                  {{ reviewingWorkId === work.id ? '处理中...' : '拒绝' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="empty-state">
            <p>暂无待审核作品</p>
          </div>

          <!-- Pagination -->
          <div v-if="worksTotalPages > 1" class="pagination">
            <button
              @click="worksCurrentPage--"
              :disabled="worksCurrentPage === 0"
              class="btn btn-secondary"
            >
              上一页
            </button>
            <span class="page-info">
              第 {{ worksCurrentPage + 1 }} / {{ worksTotalPages }} 页
            </span>
            <button
              @click="worksCurrentPage++"
              :disabled="worksCurrentPage >= worksTotalPages - 1"
              class="btn btn-secondary"
            >
              下一页
            </button>
          </div>
        </div>

        <!-- Comments Review Section -->
        <div v-if="activeTab === 'comments'" class="review-section">
          <h2>评论审核</h2>

          <!-- Filter Bar -->
          <div class="filter-bar">
            <input
              v-model="commentSearchKeyword"
              type="text"
              placeholder="搜索评论内容..."
              @keyup.enter="loadComments"
            />
            <button @click="loadComments" class="btn btn-secondary">搜索</button>
            <button @click="resetCommentSearch" class="btn btn-secondary">重置</button>
          </div>

          <!-- Loading State -->
          <div v-if="loadingComments" class="loading-indicator">
            <div class="spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- Comments List -->
          <div v-else-if="comments.length > 0" class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-card">
              <div class="comment-header">
                <div class="comment-info">
                  <p class="comment-author">
                    <strong>评论者:</strong> {{ comment.user?.username || '未知用户' }}
                  </p>
                  <p class="comment-date">
                    <strong>时间:</strong> {{ formatDate(comment.createdAt) }}
                  </p>
                  <p class="comment-pattern">
                    <strong>纹样:</strong> {{ comment.pattern?.name || '未知纹样' }}
                  </p>
                </div>
              </div>

              <div class="comment-content">
                <p>{{ comment.content }}</p>
              </div>

              <!-- Delete Button -->
              <div class="comment-actions">
                <button
                  @click="handleDeleteComment(comment.id)"
                  class="btn btn-danger"
                  :disabled="deletingCommentId === comment.id"
                >
                  {{ deletingCommentId === comment.id ? '删除中...' : '删除' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="empty-state">
            <p>暂无评论</p>
          </div>

          <!-- Pagination -->
          <div v-if="commentsTotalPages > 1" class="pagination">
            <button
              @click="commentsCurrentPage--"
              :disabled="commentsCurrentPage === 0"
              class="btn btn-secondary"
            >
              上一页
            </button>
            <span class="page-info">
              第 {{ commentsCurrentPage + 1 }} / {{ commentsTotalPages }} 页
            </span>
            <button
              @click="commentsCurrentPage++"
              :disabled="commentsCurrentPage >= commentsTotalPages - 1"
              class="btn btn-secondary"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { workAPI, commentAPI } from '../services/api'
import api from '../services/api'

import { ElMessage, ElMessageBox } from 'element-plus'

// State
const activeTab = ref('works')

// Works Review State
const pendingWorks = ref([])
const loadingWorks = ref(false)
const worksCurrentPage = ref(0)
const worksPageSize = ref(10)
const worksTotalPages = ref(1)
const workStatusFilter = ref('')
const reviewingWorkId = ref(null)

// Comments Review State
const comments = ref([])
const loadingComments = ref(false)
const commentsCurrentPage = ref(0)
const commentsPageSize = ref(10)
const commentsTotalPages = ref(1)
const commentSearchKeyword = ref('')
const deletingCommentId = ref(null)

// Load pending works
const loadPendingWorks = async () => {
  loadingWorks.value = true
  try {
    const params = {
      page: worksCurrentPage.value,
      size: worksPageSize.value
    }
    const response = await workAPI.getPendingWorks(params)
    if (response.code === 200) {
      let workList = response.data.content || []
      
      // Apply status filter if selected
      if (workStatusFilter.value) {
        workList = workList.filter(work => work.status === workStatusFilter.value)
      }
      
      pendingWorks.value = workList
      worksTotalPages.value = response.data.totalPages || 1
    } else {
      ElMessage.error(response.message || '获取待审核作品失败')
    }
  } catch (error) {
    ElMessage.error('获取待审核作品失败: ' + error.message)
  } finally {
    loadingWorks.value = false
  }
}

// Load comments
const loadComments = async () => {
  loadingComments.value = true
  try {
    const params = {
      page: commentsCurrentPage.value,
      size: commentsPageSize.value
    }
    const response = await commentAPI.getComments(params)
    if (response.code === 200) {
      let commentList = response.data.content || []
      
      // Apply search filter if keyword provided
      if (commentSearchKeyword.value.trim()) {
        const keyword = commentSearchKeyword.value.toLowerCase()
        commentList = commentList.filter(comment =>
          comment.content.toLowerCase().includes(keyword)
        )
      }
      
      comments.value = commentList
      commentsTotalPages.value = response.data.totalPages || 1
    } else {
      ElMessage.error(response.message || '获取评论列表失败')
    }
  } catch (error) {
    ElMessage.error('获取评论列表失败: ' + error.message)
  } finally {
    loadingComments.value = false
  }
}

// Reset comment search
const resetCommentSearch = () => {
  commentSearchKeyword.value = ''
  commentsCurrentPage.value = 0
  loadComments()
}

// Approve work
const handleApproveWork = async (workId) => {
  try {
    await ElMessageBox.confirm('确定要通过这个作品吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    reviewingWorkId.value = workId
    const response = await api.put(`/works/${workId}/review`, {}, { params: { status: 'approved' } })
    if (response.code === 200) {
      ElMessage.success('作品已通过')
      loadPendingWorks()
    } else {
      ElMessage.error(response.message || '审核失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审核失败: ' + error.message)
    }
  } finally {
    reviewingWorkId.value = null
  }
}

// Reject work
const handleRejectWork = async (workId) => {
  try {
    await ElMessageBox.confirm('确定要拒绝这个作品吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    reviewingWorkId.value = workId
    const response = await api.put(`/works/${workId}/review`, {}, { params: { status: 'rejected' } })
    if (response.code === 200) {
      ElMessage.success('作品已拒绝')
      loadPendingWorks()
    } else {
      ElMessage.error(response.message || '审核失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审核失败: ' + error.message)
    }
  } finally {
    reviewingWorkId.value = null
  }
}

// Delete comment
const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    deletingCommentId.value = commentId
    const response = await commentAPI.deleteComment(commentId)
    if (response.code === 200) {
      ElMessage.success('评论已删除')
      loadComments()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message)
    }
  } finally {
    deletingCommentId.value = null
  }
}

// Get status label
const getStatusLabel = (status) => {
  const labels = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return labels[status] || status
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN')
}

// Watch page changes
watch(() => worksCurrentPage.value, () => {
  loadPendingWorks()
})

watch(() => commentsCurrentPage.value, () => {
  loadComments()
})

// Lifecycle
onMounted(() => {
  loadPendingWorks()
  loadComments()
})
</script>

<style scoped>
.admin-review {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-container {
  display: flex;
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  gap: 2rem;
  padding: 2rem;
}

.review-content {
  flex: 1;
}

.review-content h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.review-content h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #333;
}

/* Tab Navigation */
.tab-navigation {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  border-bottom: 2px solid #eee;
}

.tab-btn {
  padding: 0.75rem 1.5rem;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  font-size: 1rem;
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.tab-btn:hover {
  color: #667eea;
}

/* Review Section */
.review-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.filter-bar input,
.filter-bar select {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.filter-bar input {
  flex: 1;
  min-width: 200px;
}

.filter-bar select {
  min-width: 150px;
}

/* Loading */
.loading-indicator {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  gap: 1rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Works List */
.works-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.work-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1.5rem;
  background-color: #f9f9f9;
  transition: all 0.3s;
}

.work-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.work-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  gap: 1rem;
}

.work-info h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.2rem;
}

.work-creator,
.work-date {
  margin: 0.25rem 0;
  color: #666;
  font-size: 0.9rem;
}

.status-badge {
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
  white-space: nowrap;
}

.status-pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-approved {
  background-color: #d4edda;
  color: #155724;
}

.status-rejected {
  background-color: #f8d7da;
  color: #721c24;
}

.work-preview {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.work-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.work-details {
  flex: 1;
}

.work-description {
  margin: 0 0 0.5rem 0;
  color: #555;
  line-height: 1.5;
}

.work-stats {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.work-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

/* Comments List */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.comment-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1.5rem;
  background-color: #f9f9f9;
  transition: all 0.3s;
}

.comment-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.comment-header {
  margin-bottom: 1rem;
}

.comment-info p {
  margin: 0.25rem 0;
  color: #666;
  font-size: 0.9rem;
}

.comment-content {
  margin-bottom: 1rem;
  padding: 1rem;
  background-color: white;
  border-radius: 4px;
  border-left: 3px solid #667eea;
}

.comment-content p {
  margin: 0;
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
}

.page-info {
  color: #666;
  font-size: 0.875rem;
}

/* Buttons */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #5568d3;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background-color: #218838;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

.btn-small {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

/* Responsive */
@media (max-width: 768px) {
  .admin-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .review-section {
    padding: 1rem;
  }

  .filter-bar {
    flex-direction: column;
  }

  .filter-bar input,
  .filter-bar select {
    width: 100%;
  }

  .work-header {
    flex-direction: column;
  }

  .work-preview {
    flex-direction: column;
  }

  .work-image {
    width: 100%;
    height: auto;
  }

  .work-actions {
    flex-direction: column;
  }

  .work-actions .btn {
    width: 100%;
  }

  .comment-actions {
    flex-direction: column;
  }

  .comment-actions .btn {
    width: 100%;
  }

  .tab-navigation {
    flex-wrap: wrap;
  }

  .tab-btn {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .admin-container {
    padding: 0.5rem;
  }

  .review-content h1 {
    font-size: 1.5rem;
    margin-bottom: 1rem;
  }

  .review-content h2 {
    font-size: 1.2rem;
    margin-bottom: 1rem;
  }

  .review-section {
    padding: 0.75rem;
  }

  .work-card,
  .comment-card {
    padding: 1rem;
  }

  .work-info h3 {
    font-size: 1rem;
  }

  .btn {
    padding: 0.6rem 1rem;
    font-size: 0.9rem;
  }
}
</style>
