<template>
  <div class="artwork-detail-page">
    <router-link to="/artworks" class="back-link">&larr; {{ copy.backToList }}</router-link>

    <div v-if="isLoading" class="loading-container">
      <div class="spinner"></div>
      <p>{{ copy.loading }}</p>
    </div>

    <div v-else-if="artwork" class="detail-container">
      <section class="artwork-main">
        <div class="artwork-image-section">
          <div class="artwork-image-shell">
            <img :src="artwork.imageUrl" :alt="artwork.title" class="artwork-image" />
          </div>
        </div>

        <div class="artwork-info-section">
          <div class="artwork-heading">
            <span class="section-kicker">{{ copy.detailKicker }}</span>
            <h1 class="artwork-title">{{ artwork.title }}</h1>
          </div>

          <div class="artwork-description-card">
            <div class="description-header">
              <span class="description-badge">{{ copy.descriptionTitle }}</span>
            </div>
            <p class="description-text">{{ artwork.description || copy.emptyDescription }}</p>
          </div>

          <div class="artwork-secondary-panel">
            <div class="artwork-meta">
              <div class="meta-item">
                <span class="meta-label">{{ copy.categoryLabel }}</span>
                <span class="meta-value">{{ artwork.category || copy.emptyCategory }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">{{ copy.creatorLabel }}</span>
                <span class="meta-value">{{ artwork.creator || copy.emptyCreator }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">{{ copy.techniqueLabel }}</span>
                <span class="meta-value">{{ artwork.technique || copy.emptyTechnique }}</span>
              </div>
            </div>

            <div class="artwork-actions-row">
              <div class="artwork-stats">
                <div class="stat-item">
                  <span class="stat-label">{{ copy.viewLabel }}</span>
                  <span class="stat-value">{{ artwork.viewCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">{{ copy.collectLabel }}</span>
                  <span class="stat-value">{{ artwork.collectCount || 0 }}</span>
                </div>
              </div>

              <router-link
                v-if="!isLoggedIn"
                :to="loginTarget"
                class="collect-btn collect-btn--link"
                :title="collectTitle"
              >
                登录后收藏
              </router-link>
              <button
                v-else
                class="collect-btn"
                :class="{ collected: isCollected }"
                @click="toggleCollect"
                :title="collectTitle"
              >
                {{ isCollected ? copy.collectedButton : copy.collectButton }}
              </button>
            </div>
          </div>
        </div>
      </section>

      <section class="comments-section">
        <div class="comments-header">
          <div>
            <span class="section-kicker">{{ copy.commentsKicker }}</span>
            <h2>{{ copy.commentsTitle }}</h2>
            <p class="comments-subtitle">{{ copy.commentsSubtitle }}</p>
          </div>
          <div class="comments-count">{{ commentTotal }} {{ copy.commentsCountSuffix }}</div>
        </div>

        <div v-if="isLoggedIn" class="comment-form-card">
          <div class="comment-avatar comment-avatar--composer">
            {{ getUserInitial(authStore.user?.username || authStore.user?.id) }}
          </div>

          <div class="comment-form-main">
            <div class="comment-form-head">
              <strong>{{ copy.publishComment }}</strong>
              <span>{{ copy.commentRule }}</span>
            </div>

            <textarea
              v-model="newComment"
              :placeholder="copy.commentPlaceholder"
              class="comment-input"
              rows="5"
              :maxlength="COMMENT_LIMIT"
            ></textarea>

            <div class="comment-form-footer">
              <span class="comment-hint">{{ newComment.trim().length }}/{{ COMMENT_LIMIT }}</span>
              <button
                @click="submitComment"
                class="submit-btn"
                :disabled="!newComment.trim() || isSubmittingComment"
              >
                {{ isSubmittingComment ? copy.submitting : copy.submitComment }}
              </button>
            </div>
          </div>
        </div>

        <div v-else class="login-prompt">
          <p>
            {{ copy.loginPromptPrefix }}
            {{ copy.loginAction }}
            {{ copy.loginPromptSuffix }}
          </p>
          <router-link :to="loginTarget" class="login-prompt__action">
            去登录后评论
          </router-link>
        </div>

        <div class="comments-list">
          <article
            v-for="comment in comments"
            :key="comment.id"
            class="comment-card"
          >
            <div class="comment-avatar">
              {{ getUserInitial(getCommentAuthor(comment)) }}
            </div>

            <div class="comment-body">
              <div class="comment-meta-row">
                <div class="comment-meta-main">
                  <span class="comment-author">{{ getCommentAuthor(comment) }}</span>
                  <span class="comment-meta-dot"></span>
                  <span class="comment-meta-text">{{ copy.commentMetaText }}</span>
                </div>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
              </div>

              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </article>

          <div v-if="comments.length === 0" class="no-comments">
            <div class="no-comments-icon">{{ copy.emptyCommentsIcon }}</div>
            <h3>{{ copy.emptyCommentsTitle }}</h3>
            <p>{{ copy.emptyCommentsText }}</p>
          </div>
        </div>
      </section>
    </div>

    <div v-else class="error-state">
      <div class="error-icon">!</div>
      <p>{{ copy.errorMessage }}</p>
      <router-link to="/artworks" class="back-btn">{{ copy.backToList }}</router-link>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ArtworkService, CommentService } from '../services'
import { useAuthStore } from '../stores/authStore'
import { useToast } from '../utils/useToast'

const COMMENT_LIMIT = 300

const copy = {
  backToList: '\u8fd4\u56de\u4f5c\u54c1\u5217\u8868',
  loading: '\u6b63\u5728\u52a0\u8f7d\u4f5c\u54c1\u8be6\u60c5...',
  detailKicker: '\u4f5c\u54c1\u8be6\u60c5',
  descriptionTitle: '\u4f5c\u54c1\u63cf\u8ff0',
  emptyDescription: '\u6682\u65e0\u4f5c\u54c1\u63cf\u8ff0\u3002',
  categoryLabel: '\u5206\u7c7b',
  creatorLabel: '\u521b\u4f5c\u8005',
  techniqueLabel: '\u6280\u6cd5',
  emptyCategory: '\u672a\u5206\u7c7b',
  emptyCreator: '\u672a\u77e5',
  emptyTechnique: '\u6682\u65e0\u4fe1\u606f',
  viewLabel: '\u6d4f\u89c8',
  collectLabel: '\u6536\u85cf',
  collectButton: '\u6536\u85cf\u4f5c\u54c1',
  collectedButton: '\u5df2\u6536\u85cf',
  collectTitle: '\u6536\u85cf\u4f5c\u54c1',
  uncollectTitle: '\u53d6\u6d88\u6536\u85cf',
  loginFirst: '\u8bf7\u5148\u767b\u5f55',
  commentsKicker: '\u4ea4\u6d41\u4e92\u52a8',
  commentsTitle: '\u8bc4\u8bba\u533a',
  commentsSubtitle:
    '\u5206\u4eab\u4f60\u5bf9\u8fd9\u4ef6\u4f5c\u54c1\u7684\u770b\u6cd5\uff0c\u7559\u4e0b\u4f60\u7684\u7406\u89e3\u3001\u611f\u53d7\u6216\u5efa\u8bae\u3002',
  commentsCountSuffix: '\u6761\u8bc4\u8bba',
  publishComment: '\u53d1\u8868\u8bc4\u8bba',
  commentRule: '\u7406\u6027\u4ea4\u6d41\uff0c\u6587\u660e\u53d1\u8a00',
  commentPlaceholder:
    '\u8bf4\u8bf4\u4f60\u5bf9\u8fd9\u4ef6\u4f5c\u54c1\u7684\u7406\u89e3\u3001\u559c\u6b22\u7684\u7ec6\u8282\uff0c\u6216\u4f60\u60f3\u8865\u5145\u7684\u80cc\u666f\u4fe1\u606f...',
  submitComment: '\u53d1\u5e03\u8bc4\u8bba',
  submitting: '\u53d1\u5e03\u4e2d...',
  loginPromptPrefix: '\u8bf7',
  loginAction: '\u767b\u5f55',
  loginPromptSuffix: '\u540e\u53c2\u4e0e\u8bc4\u8bba\u3002',
  commentMetaText: '\u7528\u6237\u8bc4\u8bba',
  emptyCommentsIcon: '\u8bc4',
  emptyCommentsTitle: '\u8fd8\u6ca1\u6709\u8bc4\u8bba',
  emptyCommentsText: '\u6210\u4e3a\u7b2c\u4e00\u4e2a\u5206\u4eab\u60f3\u6cd5\u7684\u4eba\u3002',
  errorMessage: '\u4f5c\u54c1\u4e0d\u5b58\u5728\u6216\u5df2\u88ab\u5220\u9664',
  loadError: '\u52a0\u8f7d\u4f5c\u54c1\u8be6\u60c5\u5931\u8d25',
  emptyCommentError: '\u8bc4\u8bba\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a',
  lengthErrorPrefix: '\u8bc4\u8bba\u5185\u5bb9\u4e0d\u80fd\u8d85\u8fc7',
  lengthErrorSuffix: '\u5b57',
  submitSuccess: '\u8bc4\u8bba\u53d1\u5e03\u6210\u529f',
  submitError: '\u8bc4\u8bba\u53d1\u5e03\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5',
  uncollectSuccess: '\u5df2\u53d6\u6d88\u6536\u85cf',
  collectSuccess: '\u6536\u85cf\u6210\u529f',
  actionError: '\u64cd\u4f5c\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5',
  guestInitial: '\u6e38',
  userPrefix: '\u7528\u6237',
  minutesAgo: '\u5206\u949f\u524d',
  hoursAgo: '\u5c0f\u65f6\u524d',
  daysAgo: '\u5929\u524d',
}

const route = useRoute()
const authStore = useAuthStore()

const artwork = ref(null)
const comments = ref([])
const commentTotal = ref(0)
const newComment = ref('')
const isLoading = ref(false)
const isSubmittingComment = ref(false)
const isCollected = ref(false)

const { success, error } = useToast()
const isLoggedIn = computed(() => authStore.isLoggedIn)
const loginTarget = computed(() => ({
  path: '/login',
  query: {
    redirect: route.fullPath || `/artworks/${route.params.id}`,
  },
}))
const collectTitle = computed(() => {
  if (!isLoggedIn.value) {
    return copy.loginFirst
  }

  return isCollected.value ? copy.uncollectTitle : copy.collectTitle
})

const getCurrentArtworkId = () => route.params.id

const fetchArtworkDetail = async (artworkId) => {
  const response = await ArtworkService.getArtworkDetail(artworkId)
  artwork.value = response
}

const loadCollectStatus = async (artworkId, userId) => {
  if (!userId) {
    isCollected.value = false
    return
  }

  isCollected.value = await ArtworkService.getCollectStatus(artworkId, userId)
}

const loadComments = async (artworkId) => {
  try {
    const response = await CommentService.getComments({
      artworkId,
      pageNum: 1,
      pageSize: 20,
    })

    comments.value = response.items || []
    commentTotal.value = response.total || comments.value.length
  } catch (err) {
    console.error('Failed to load comments:', err)
    comments.value = []
    commentTotal.value = 0
  }
}

const loadArtworkDetail = async ({ recordView = true } = {}) => {
  isLoading.value = true

  try {
    const artworkId = getCurrentArtworkId()
    const userId = authStore.user?.id

    if (recordView) {
      await ArtworkService.recordView(artworkId, userId)
    }

    await Promise.all([
      fetchArtworkDetail(artworkId),
      loadComments(artworkId),
      loadCollectStatus(artworkId, userId),
    ])
  } catch (err) {
    console.error('Failed to load artwork detail:', err)
    error(copy.loadError)
  } finally {
    isLoading.value = false
  }
}

const submitComment = async () => {
  const content = newComment.value.trim()

  if (!content) {
    error(copy.emptyCommentError)
    return
  }

  if (content.length > COMMENT_LIMIT) {
    error(`${copy.lengthErrorPrefix} ${COMMENT_LIMIT} ${copy.lengthErrorSuffix}`)
    return
  }

  isSubmittingComment.value = true

  try {
    await CommentService.postComment({
      content,
      userId: authStore.user.id,
      artworkId: artwork.value.id,
    })

    success(copy.submitSuccess)
    newComment.value = ''
    await loadComments(artwork.value.id)
  } catch (err) {
    console.error('Failed to submit comment:', err)
    error(copy.submitError)
  } finally {
    isSubmittingComment.value = false
  }
}

const toggleCollect = async () => {
  if (!isLoggedIn.value) {
    error(copy.loginFirst)
    return
  }

  try {
    if (isCollected.value) {
      await ArtworkService.uncollectArtwork(artwork.value.id, authStore.user.id)
      success(copy.uncollectSuccess)
    } else {
      await ArtworkService.collectArtwork(artwork.value.id, authStore.user.id)
      success(copy.collectSuccess)
    }

    await loadArtworkDetail({ recordView: false })
  } catch (err) {
    console.error('Failed to toggle collect:', err)
    error(copy.actionError)
  }
}

const getCommentAuthor = (comment) => {
  return comment.username || `${copy.userPrefix} ${comment.userId || ''}`.trim()
}

const getUserInitial = (value) => {
  if (!value) {
    return copy.guestInitial
  }

  return String(value).trim().slice(0, 1).toUpperCase()
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < hour) {
    const minutes = Math.max(1, Math.floor(diff / minute))
    return `${minutes} ${copy.minutesAgo}`
  }

  if (diff < day) {
    const hours = Math.floor(diff / hour)
    return `${hours} ${copy.hoursAgo}`
  }

  if (diff < day * 7) {
    const days = Math.floor(diff / day)
    return `${days} ${copy.daysAgo}`
  }

  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadArtworkDetail()
})
</script>

<style scoped>
.artwork-detail-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  width: fit-content;
  color: var(--primary-color);
  font-weight: 600;
}

.back-link:hover {
  gap: var(--spacing-md);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  padding: calc(var(--spacing-xl) * 2) 0;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.detail-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.artwork-main {
  display: grid;
  grid-template-columns: minmax(0, 1.02fr) minmax(0, 0.98fr);
  gap: var(--spacing-xl);
  padding: var(--spacing-xl);
  background:
    radial-gradient(circle at top right, rgba(0, 102, 204, 0.08), transparent 28%),
    var(--bg-primary);
  border-radius: 20px;
  box-shadow: var(--shadow-md);
}

.artwork-image-section {
  display: flex;
}

.artwork-image-shell {
  width: 100%;
  padding: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f4f7fb 100%);
  border: 1px solid rgba(0, 102, 204, 0.08);
  border-radius: 20px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.06);
}

.artwork-image {
  width: 100%;
  height: 100%;
  min-height: 420px;
  border-radius: 14px;
  object-fit: cover;
  display: block;
}

.artwork-info-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  min-width: 0;
}

.section-kicker {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-size: var(--font-size-sm);
  font-weight: 700;
  letter-spacing: 0.04em;
}

.artwork-heading {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.artwork-title {
  margin: 0;
  color: var(--text-primary);
  font-size: 34px;
  line-height: 1.25;
  font-weight: 800;
}

.artwork-description-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(0, 102, 204, 0.1), rgba(51, 133, 221, 0.04));
  border: 1px solid rgba(0, 102, 204, 0.14);
  box-shadow: 0 12px 30px rgba(0, 102, 204, 0.08);
}

.description-header {
  display: flex;
  align-items: center;
}

.description-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  color: var(--primary-dark);
  font-size: var(--font-size-sm);
  font-weight: 700;
}

.description-text {
  margin: 0;
  color: #24405c;
  font-size: 15px;
  line-height: 1.9;
}

.artwork-secondary-panel {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  padding: 20px 22px;
  border-radius: 18px;
  background: #fafafa;
  border: 1px solid #ececec;
}

.artwork-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--spacing-md);
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.meta-label {
  color: var(--text-light);
  font-size: var(--font-size-sm);
}

.meta-value {
  color: var(--text-primary);
  font-weight: 600;
  word-break: break-word;
}

.artwork-actions-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.artwork-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 999px;
  background: #ffffff;
  border: 1px solid #e8e8e8;
  color: var(--text-secondary);
}

.stat-label {
  font-size: var(--font-size-sm);
}

.stat-value {
  color: var(--text-primary);
  font-weight: 700;
}

.collect-btn {
  padding: 10px 18px;
  background: #ffffff;
  border: 1px solid #d8d8d8;
  color: var(--text-secondary);
  font-weight: 700;
}

.collect-btn--link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
}

.collect-btn:hover:not(:disabled) {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(0, 102, 204, 0.04);
}

.collect-btn.collected {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: #ffffff;
}

.collect-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.comments-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  background: var(--bg-primary);
  border-radius: 20px;
  box-shadow: var(--shadow-md);
}

.comments-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-lg);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid #efefef;
}

.comments-header h2 {
  margin: 10px 0 8px;
  font-size: 30px;
  line-height: 1.2;
  color: var(--text-primary);
}

.comments-subtitle {
  margin: 0;
  color: var(--text-secondary);
}

.comments-count {
  flex-shrink: 0;
  padding: 10px 16px;
  border-radius: 999px;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-weight: 700;
}

.comment-form-card,
.comment-card {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr);
  gap: var(--spacing-md);
}

.comment-form-card {
  padding: 22px;
  background: linear-gradient(180deg, #fbfdff 0%, #f5f9ff 100%);
  border: 1px solid rgba(0, 102, 204, 0.1);
  border-radius: 18px;
}

.comment-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #ffffff;
  font-size: 20px;
  font-weight: 700;
  box-shadow: 0 10px 22px rgba(0, 102, 204, 0.2);
}

.comment-avatar--composer {
  margin-top: 4px;
}

.comment-form-main {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.comment-form-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.comment-form-head strong {
  font-size: var(--font-size-lg);
  color: var(--text-primary);
}

.comment-form-head span {
  color: var(--text-light);
  font-size: var(--font-size-sm);
}

.comment-input {
  min-height: 130px;
  padding: 16px 18px;
  resize: vertical;
  line-height: 1.8;
  background: #ffffff;
  border: 1px solid #dfe7f1;
}

.comment-form-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
}

.comment-hint {
  color: var(--text-light);
  font-size: var(--font-size-sm);
}

.submit-btn {
  padding: 12px 22px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #ffffff;
  font-weight: 700;
  box-shadow: 0 12px 24px rgba(0, 102, 204, 0.18);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
}

.submit-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
  box-shadow: none;
}

.login-prompt {
  padding: 18px 22px;
  border-radius: 16px;
  background: #fafafa;
  border: 1px dashed #dddddd;
  color: var(--text-secondary);
  text-align: center;
}

.login-prompt p {
  margin: 0;
}

.login-prompt__action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  margin-top: 14px;
  padding: 12px 22px;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #ffffff;
  font-weight: 700;
  text-decoration: none;
  box-shadow: 0 12px 24px rgba(0, 102, 204, 0.18);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.comment-card {
  padding: 22px;
  background: #ffffff;
  border: 1px solid #efefef;
  border-radius: 18px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
}

.comment-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.comment-meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.comment-meta-main {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.comment-author {
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 700;
}

.comment-meta-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #c8c8c8;
}

.comment-meta-text,
.comment-date {
  color: var(--text-light);
  font-size: var(--font-size-sm);
}

.comment-content {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.9;
  white-space: pre-wrap;
  word-break: break-word;
}

.no-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 44px 20px;
  border-radius: 18px;
  background: linear-gradient(180deg, #fcfcfc 0%, #f7f7f7 100%);
  border: 1px dashed #dddddd;
  text-align: center;
}

.no-comments-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-size: 22px;
  font-weight: 700;
}

.no-comments h3,
.no-comments p,
.error-state p {
  margin: 0;
}

.no-comments p {
  color: var(--text-secondary);
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  padding: calc(var(--spacing-xl) * 2);
  background: var(--bg-primary);
  border-radius: 20px;
  box-shadow: var(--shadow-sm);
}

.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 68px;
  height: 68px;
  border-radius: 50%;
  background: rgba(255, 51, 51, 0.08);
  color: var(--error-color);
  font-size: 28px;
  font-weight: 700;
}

.back-btn {
  padding: 12px 20px;
  background: var(--primary-color);
  color: #ffffff;
  font-weight: 700;
}

@media (max-width: 960px) {
  .artwork-main {
    grid-template-columns: 1fr;
  }

  .artwork-image {
    min-height: 340px;
  }

  .artwork-meta {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .artwork-detail-page {
    padding: var(--spacing-md);
    gap: var(--spacing-lg);
  }

  .artwork-main,
  .comments-section {
    padding: var(--spacing-lg);
    border-radius: 16px;
  }

  .artwork-title {
    font-size: 28px;
  }

  .comments-header {
    flex-direction: column;
    align-items: stretch;
  }

  .comments-count {
    width: fit-content;
  }

  .comment-form-card,
  .comment-card {
    grid-template-columns: 1fr;
  }

  .comment-avatar {
    width: 44px;
    height: 44px;
    font-size: 18px;
  }

  .comment-form-footer,
  .artwork-actions-row {
    flex-direction: column;
    align-items: stretch;
  }

  .artwork-stats {
    width: 100%;
  }

  .stat-item,
  .collect-btn,
  .submit-btn {
    justify-content: center;
  }
}
</style>
