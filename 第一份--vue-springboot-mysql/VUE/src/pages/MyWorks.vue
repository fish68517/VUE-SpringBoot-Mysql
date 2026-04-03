<template>
  <div class="my-works-page">
    <div class="page-shell">
      <div class="page-header">
        <button class="back-btn" @click="router.push('/user-center')">返回个人中心</button>
        <div>
          <h1>我的作品</h1>
          <p>查看自己上传的作品，点击作品可查看详情和评论。</p>
        </div>
      </div>

      <div v-if="worksLoading" class="loading-state">
        <el-skeleton :rows="4" animated />
      </div>

      <div v-else-if="works.length === 0" class="empty-state">
        <h3>还没有上传过作品</h3>
        <p>可以先去原创作品页面上传你的第一件作品。</p>
        <router-link to="/works" class="empty-link">前往原创作品页</router-link>
      </div>

      <div v-else class="works-grid">
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
            <div class="work-meta">
              <h3 class="work-title">{{ work.title }}</h3>
              <span :class="['status-badge', getStatusClass(work.status)]">
                {{ getStatusLabel(work.status) }}
              </span>
            </div>
            <p class="work-time">上传时间：{{ formatDate(work.createdAt) }}</p>
            <p class="work-description">
              {{ work.description || '暂无作品描述' }}
            </p>
            <div class="work-stats">
              <span class="stat">点赞 {{ work.likeCount || 0 }}</span>
              <span class="stat">评论 {{ work.commentCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="totalPages > 1" class="pagination">
        <button
          class="page-btn"
          :disabled="currentPage === 0"
          @click="previousPage"
        >
          上一页
        </button>
        <span class="page-info">第 {{ currentPage + 1 }} / {{ totalPages }} 页</span>
        <button
          class="page-btn"
          :disabled="currentPage >= totalPages - 1"
          @click="nextPage"
        >
          下一页
        </button>
      </div>
    </div>

    <div v-if="selectedWork" class="modal-overlay" @click="closeWorkDetail">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeWorkDetail">×</button>

        <div class="detail-container">
          <div class="detail-image">
            <img :src="selectedWork.imageUrl" :alt="selectedWork.title" />
          </div>

          <div class="detail-info">
            <div class="detail-title-row">
              <h2>{{ selectedWork.title }}</h2>
              <span :class="['status-badge', getStatusClass(selectedWork.status)]">
                {{ getStatusLabel(selectedWork.status) }}
              </span>
            </div>

            <div class="creator-info">
              <div v-if="selectedWork.user?.avatarUrl" class="avatar">
                <img :src="selectedWork.user.avatarUrl" :alt="selectedWork.user.username" />
              </div>
              <div v-else class="avatar-placeholder">
                {{ selectedWork.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
              </div>
              <div class="creator-details">
                <p class="creator-name">
                  {{ selectedWork.user?.nickname || selectedWork.user?.username || '匿名用户' }}
                </p>
                <p class="create-time">{{ formatTime(selectedWork.createdAt) }}</p>
              </div>
            </div>

            <div v-if="selectedWork.description" class="description">
              <h4>作品描述</h4>
              <p>{{ selectedWork.description }}</p>
            </div>

            <div class="work-stats-detail">
              <span class="stat">点赞 {{ selectedWork.likeCount || 0 }}</span>
              <span class="stat">评论 {{ workComments.length }}</span>
            </div>
          </div>
        </div>

        <div class="comments-section">
          <h3>评论 ({{ workComments.length }})</h3>

          <div v-if="isLoggedIn" class="comment-input">
            <textarea
              v-model="newWorkComment"
              placeholder="发表你的评论..."
              rows="3"
              :disabled="commentSubmitting"
            ></textarea>
            <button
              class="submit-btn"
              :disabled="!newWorkComment.trim() || commentSubmitting"
              @click="submitWorkComment"
            >
              {{ commentSubmitting ? '发布中...' : '发布评论' }}
            </button>
          </div>

          <div v-else class="login-prompt">
            <p>登录后可以发表评论</p>
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>

          <div v-if="workComments.length > 0" class="comments-list">
            <div v-for="comment in workComments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <div class="user-info">
                  <div v-if="comment.user?.avatarUrl" class="avatar small-avatar">
                    <img :src="comment.user.avatarUrl" :alt="comment.user.username" />
                  </div>
                  <div v-else class="avatar-placeholder small-avatar">
                    {{ comment.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
                  </div>
                  <div class="user-details">
                    <p class="username">
                      {{ comment.user?.nickname || comment.user?.username || '匿名用户' }}
                    </p>
                    <p class="timestamp">{{ formatTime(comment.createdAt) }}</p>
                  </div>
                </div>

                <button
                  v-if="canDeleteComment(comment)"
                  class="delete-btn"
                  :disabled="commentDeleting"
                  @click="deleteWorkComment(comment.id)"
                >
                  删除
                </button>
              </div>

              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </div>

          <div v-else class="empty-comments">
            <p>暂无评论，来发表第一条评论吧</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { commentAPI, workAPI } from '../services/api'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()

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

const isLoggedIn = computed(() => userStore.isLoggedIn)

const fetchMyWorks = async () => {
  const userId = userStore.user?.id
  if (!userId) {
    ElMessage.warning('此功能需要登录才能访问')
    router.push('/home')
    return
  }

  worksLoading.value = true
  try {
    const response = await workAPI.getUserWorks(userId, {
      page: currentPage.value,
      size: pageSize.value
    })

    if (response?.code === 200 && response.data) {
      works.value = response.data.content || []
      totalPages.value = response.data.totalPages || 0
      return
    }

    works.value = []
    totalPages.value = 0
  } catch (error) {
    console.error('Failed to fetch my works:', error)
    ElMessage.error('加载我的作品失败')
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
      workId,
      page: 0,
      size: 100
    })

    if (response?.data) {
      workComments.value = response.data.content || []
      return
    }

    workComments.value = []
  } catch (error) {
    console.error('Failed to fetch work comments:', error)
    workComments.value = []
  }
}

const submitWorkComment = async () => {
  if (!newWorkComment.value.trim() || !selectedWork.value) {
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
  return userStore.user && (userStore.user.id === comment.userId || userStore.user.roleId === 1 || userStore.user.roleId === 2)
}

const getStatusClass = (status) => {
  const statusMap = {
    APPROVED: 'approved',
    PENDING: 'pending',
    REJECTED: 'rejected'
  }

  return statusMap[status] || 'pending'
}

const getStatusLabel = (status) => {
  const statusMap = {
    APPROVED: '已通过',
    PENDING: '待审核',
    REJECTED: '未通过'
  }

  return statusMap[status] || '待审核'
}

const formatDate = (dateString) => {
  if (!dateString) return '-'

  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
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

const nextPage = async () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value += 1
    await fetchMyWorks()
  }
}

const previousPage = async () => {
  if (currentPage.value > 0) {
    currentPage.value -= 1
    await fetchMyWorks()
  }
}

onMounted(() => {
  fetchMyWorks()
})
</script>

<style scoped>
.my-works-page {
  min-height: calc(100vh - 80px);
  background: #f5f7fa;
  padding: 2rem 1rem;
}

.page-shell {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.page-header h1 {
  margin: 0 0 0.5rem;
  color: #1f2937;
  font-size: 2rem;
}

.page-header p {
  margin: 0;
  color: #6b7280;
}

.back-btn {
  border: none;
  border-radius: 999px;
  padding: 0.75rem 1rem;
  background: #e5e7eb;
  color: #1f2937;
  cursor: pointer;
  transition: background 0.2s ease;
}

.back-btn:hover {
  background: #d1d5db;
}

.loading-state,
.empty-state {
  background: #fff;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
}

.empty-state {
  text-align: center;
}

.empty-state h3 {
  margin: 0 0 0.75rem;
  color: #1f2937;
}

.empty-state p {
  margin: 0 0 1rem;
  color: #6b7280;
}

.empty-link {
  display: inline-block;
  padding: 0.75rem 1.25rem;
  border-radius: 10px;
  background: #4f46e5;
  color: #fff;
  text-decoration: none;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1.5rem;
}

.work-card {
  overflow: hidden;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 36px rgba(15, 23, 42, 0.14);
}

.work-image {
  position: relative;
  height: 220px;
  background: #e5e7eb;
  overflow: hidden;
}

.work-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.25s ease;
}

.work-card:hover .work-image img {
  transform: scale(1.04);
}

.work-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(17, 24, 39, 0.45);
  opacity: 0;
  transition: opacity 0.25s ease;
}

.work-card:hover .work-overlay {
  opacity: 1;
}

.view-btn,
.submit-btn,
.page-btn,
.delete-btn {
  border: none;
  cursor: pointer;
}

.view-btn {
  border-radius: 999px;
  padding: 0.7rem 1.2rem;
  background: #fff;
  color: #111827;
}

.work-info {
  padding: 1rem 1rem 1.2rem;
}

.work-meta {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}

.work-title {
  margin: 0;
  color: #111827;
  font-size: 1.05rem;
}

.work-time,
.work-description {
  margin: 0 0 0.75rem;
  color: #6b7280;
  font-size: 0.92rem;
  line-height: 1.6;
}

.work-description {
  min-height: 44px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.work-stats,
.work-stats-detail {
  display: flex;
  gap: 1rem;
  color: #4b5563;
  font-size: 0.92rem;
}

.status-badge {
  flex-shrink: 0;
  border-radius: 999px;
  padding: 0.3rem 0.7rem;
  font-size: 0.82rem;
  font-weight: 600;
}

.status-badge.approved {
  background: #dcfce7;
  color: #166534;
}

.status-badge.pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.rejected {
  background: #fee2e2;
  color: #991b1b;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1.75rem;
}

.page-btn {
  border-radius: 10px;
  padding: 0.7rem 1rem;
  background: #fff;
  color: #111827;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);
}

.page-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.page-info {
  color: #4b5563;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.68);
}

.modal-content {
  position: relative;
  width: 90%;
  max-width: 960px;
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 16px;
  background: #fff;
}

.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  z-index: 2;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(17, 24, 39, 0.65);
  color: #fff;
  font-size: 1.25rem;
}

.detail-container {
  display: flex;
  gap: 2rem;
  padding: 2rem;
}

.detail-image {
  flex: 0 0 42%;
  height: 400px;
  overflow: hidden;
  border-radius: 14px;
  background: #e5e7eb;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-info {
  flex: 1;
}

.detail-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.detail-title-row h2 {
  margin: 0;
  color: #111827;
}

.creator-info {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  margin-bottom: 1.25rem;
  padding-bottom: 1.25rem;
  border-bottom: 1px solid #e5e7eb;
}

.avatar,
.avatar-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #4f46e5;
  color: #fff;
  font-weight: 700;
}

.small-avatar {
  width: 36px;
  height: 36px;
  font-size: 0.95rem;
}

.creator-name,
.username {
  margin: 0 0 0.25rem;
  color: #111827;
  font-weight: 600;
}

.create-time,
.timestamp {
  margin: 0;
  color: #6b7280;
  font-size: 0.85rem;
}

.description {
  margin-bottom: 1.25rem;
}

.description h4 {
  margin: 0 0 0.5rem;
  color: #111827;
}

.description p,
.comment-content {
  margin: 0;
  color: #4b5563;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.comments-section {
  padding: 2rem;
  border-top: 1px solid #e5e7eb;
}

.comments-section h3 {
  margin: 0 0 1rem;
  color: #111827;
}

.comment-input {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.comment-input textarea {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #d1d5db;
  border-radius: 12px;
  padding: 0.85rem 1rem;
  font: inherit;
  resize: vertical;
}

.comment-input textarea:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.14);
}

.submit-btn {
  align-self: flex-end;
  border-radius: 10px;
  padding: 0.7rem 1.2rem;
  background: #4f46e5;
  color: #fff;
}

.submit-btn:disabled,
.delete-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.login-prompt,
.empty-comments {
  border-radius: 12px;
  background: #f9fafb;
  padding: 1rem;
  text-align: center;
  color: #6b7280;
}

.login-link {
  display: inline-block;
  margin-top: 0.5rem;
  color: #4f46e5;
  text-decoration: none;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  border-radius: 12px;
  background: #f9fafb;
  padding: 1rem;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.user-info {
  display: flex;
  gap: 0.75rem;
  flex: 1;
}

.delete-btn {
  align-self: flex-start;
  border-radius: 8px;
  padding: 0.45rem 0.8rem;
  background: #ef4444;
  color: #fff;
}

@media (max-width: 767px) {
  .my-works-page {
    padding: 1rem 0.75rem;
  }

  .page-header {
    flex-direction: column;
  }

  .works-grid {
    grid-template-columns: 1fr;
  }

  .detail-container {
    flex-direction: column;
    padding: 1rem;
  }

  .detail-image {
    flex-basis: auto;
    height: 280px;
  }

  .comments-section {
    padding: 1rem;
  }

  .comment-header {
    flex-direction: column;
  }
}
</style>
