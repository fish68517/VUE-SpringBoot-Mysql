<template>
  <div class="community-page">
    <section class="hero-section">
      <div class="hero-content">
        <span class="hero-kicker">{{ copy.heroKicker }}</span>
        <h1>{{ copy.title }}</h1>
        <p>{{ copy.subtitle }}</p>
      </div>

      <div class="hero-stats">
        <div class="hero-stat-card">
          <span>{{ copy.totalTopics }}</span>
          <strong>{{ topicTotalItems || topics.length }}</strong>
        </div>
        <div class="hero-stat-card">
          <span>{{ copy.totalVotes }}</span>
          <strong>{{ voteTotalItems || votes.length }}</strong>
        </div>
        <div class="hero-stat-card">
          <span>{{ copy.activeVotes }}</span>
          <strong>{{ activeVoteCount }}</strong>
        </div>
      </div>
    </section>

    <section class="tabs-shell">
      <div class="tabs-header">
        <div>
          <h2>{{ copy.tabTitle }}</h2>
          <p>{{ copy.tabSubtitle }}</p>
        </div>

        <div class="tabs-container">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'topics' }"
            @click="activeTab = 'topics'"
          >
            {{ copy.topicTab }}
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'votes' }"
            @click="activeTab = 'votes'"
          >
            {{ copy.voteTab }}
          </button>
        </div>
      </div>

      <div v-if="activeTab === 'topics'" class="tab-content">
        <div v-if="isLoadingTopics" class="loading-container">
          <div class="spinner"></div>
          <p>{{ copy.loadingTopics }}</p>
        </div>

        <div v-else-if="topics.length > 0" class="topic-list">
          <article
            v-for="topic in topics"
            :key="topic.id"
            class="topic-card"
            @click="selectTopic(topic)"
          >
            <div class="topic-card-top">
              <div class="topic-badges">
                <span class="topic-badge">{{ copy.topicBadge }}</span>
                <span v-if="topic.isPinned" class="topic-badge topic-badge--pinned">
                  {{ copy.pinnedBadge }}
                </span>
              </div>
              <span class="topic-time">{{ formatDate(topic.createdAt) }}</span>
            </div>

            <h3>{{ topic.title }}</h3>
            <p class="topic-description">{{ topic.description || copy.emptyTopicDescription }}</p>

            <div class="topic-card-footer">
              <div class="topic-meta">
                <span>{{ copy.authorPrefix }} {{ getTopicAuthor(topic) }}</span>
                <span class="meta-divider"></span>
                <span>{{ topic.commentCount || 0 }} {{ copy.commentsSuffix }}</span>
              </div>
              <span class="topic-link">{{ copy.joinTopic }}</span>
            </div>
          </article>

          <Pagination
            :current-page="topicCurrentPage"
            :total-pages="topicTotalPages"
            :total-items="topicTotalItems"
            @update:current-page="handleTopicPageChange"
          />
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">{{ copy.emptyTopicIcon }}</div>
          <h3>{{ copy.emptyTopicTitle }}</h3>
          <p>{{ copy.emptyTopicText }}</p>
        </div>
      </div>

      <div v-else class="tab-content">
        <div v-if="isLoadingVotes" class="loading-container">
          <div class="spinner"></div>
          <p>{{ copy.loadingVotes }}</p>
        </div>

        <div v-else-if="votes.length > 0" class="vote-grid">
          <article
            v-for="vote in votes"
            :key="vote.id"
            class="vote-card"
            @click="selectVote(vote)"
          >
            <div class="vote-card-top">
              <span class="vote-type">{{ copy.voteBadge }}</span>
              <span class="vote-status" :class="getVoteStatusClass(vote.status)">
                {{ getVoteStatusLabel(vote.status) }}
              </span>
            </div>

            <h3>{{ vote.title }}</h3>
            <p>{{ vote.description || copy.emptyVoteDescription }}</p>

            <div class="vote-card-footer">
              <span>{{ formatDate(vote.createdAt) }}</span>
              <span class="vote-link">{{ copy.viewVote }}</span>
            </div>
          </article>

          <Pagination
            :current-page="voteCurrentPage"
            :total-pages="voteTotalPages"
            :total-items="voteTotalItems"
            @update:current-page="handleVotePageChange"
          />
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">{{ copy.emptyVoteIcon }}</div>
          <h3>{{ copy.emptyVoteTitle }}</h3>
          <p>{{ copy.emptyVoteText }}</p>
        </div>
      </div>
    </section>

    <div v-if="selectedTopic" class="modal-overlay" @click.self="selectedTopic = null">
      <div class="modal-card">
        <button class="close-btn" @click="selectedTopic = null">×</button>

        <div class="modal-head">
          <div>
            <span class="section-kicker">{{ copy.topicDetailKicker }}</span>
            <h2>{{ selectedTopic.title }}</h2>
          </div>

          <div class="modal-meta">
            <span>{{ copy.authorPrefix }} {{ getTopicAuthor(selectedTopic) }}</span>
            <span class="meta-divider"></span>
            <span>{{ formatDate(selectedTopic.createdAt) }}</span>
          </div>
        </div>

        <p class="modal-description">
          {{ selectedTopic.description || copy.emptyTopicDescription }}
        </p>

        <section class="comments-section">
          <div class="comments-header">
            <div>
              <h3>{{ copy.commentsTitle }}</h3>
              <p>{{ copy.commentsSubtitle }}</p>
            </div>
            <div class="comments-count">{{ topicComments.length }} {{ copy.commentsSuffix }}</div>
          </div>

          <div v-if="isLoggedIn" class="comment-form-card">
            <div class="comment-avatar">
              {{ getUserInitial(authStore.user?.username || authStore.user?.id) }}
            </div>

            <div class="comment-form-main">
              <div class="comment-form-head">
                <strong>{{ copy.commentAction }}</strong>
                <span>{{ copy.commentRule }}</span>
              </div>

              <textarea
                v-model="newTopicComment"
                :placeholder="copy.commentPlaceholder"
                class="comment-input"
                rows="4"
              ></textarea>

              <div class="comment-form-footer">
                <span class="comment-hint">{{ copy.commentHint }}</span>
                <button
                  class="submit-btn"
                  :disabled="!newTopicComment.trim() || isSubmittingComment"
                  @click="submitTopicComment"
                >
                  {{ isSubmittingComment ? copy.submittingComment : copy.submitComment }}
                </button>
              </div>
            </div>
          </div>

          <div v-else class="login-prompt">
            <p>
              {{ copy.loginPromptPrefix }}
              <router-link to="/login">{{ copy.loginAction }}</router-link>
              {{ copy.loginPromptSuffix }}
            </p>
          </div>

          <div class="comments-list">
            <article
              v-for="comment in topicComments"
              :key="comment.id"
              class="comment-card"
            >
              <div class="comment-avatar comment-avatar--small">
                {{ getUserInitial(getCommentAuthor(comment)) }}
              </div>

              <div class="comment-body">
                <div class="comment-meta-row">
                  <div class="comment-meta-main">
                    <span class="comment-author">{{ getCommentAuthor(comment) }}</span>
                    <span class="meta-divider"></span>
                    <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                  </div>
                </div>
                <p class="comment-content">{{ comment.content }}</p>
              </div>
            </article>

            <div v-if="topicComments.length === 0" class="empty-comments">
              <div class="empty-icon">{{ copy.emptyCommentIcon }}</div>
              <h4>{{ copy.emptyCommentTitle }}</h4>
              <p>{{ copy.emptyCommentText }}</p>
            </div>
          </div>
        </section>
      </div>
    </div>

    <div v-if="selectedVote" class="modal-overlay" @click.self="selectedVote = null">
      <div class="modal-card modal-card--vote">
        <button class="close-btn" @click="selectedVote = null">×</button>

        <div class="modal-head">
          <div>
            <span class="section-kicker">{{ copy.voteDetailKicker }}</span>
            <h2>{{ selectedVote.title }}</h2>
          </div>

          <div class="modal-meta">
            <span class="vote-status" :class="getVoteStatusClass(selectedVote.status)">
              {{ getVoteStatusLabel(selectedVote.status) }}
            </span>
            <span class="meta-divider"></span>
            <span>{{ copy.voteTotalLabel }} {{ selectedVote.totalVotes || 0 }}</span>
          </div>
        </div>

        <p class="modal-description">
          {{ selectedVote.description || copy.emptyVoteDescription }}
        </p>

        <div class="vote-options">
          <div
            v-for="option in selectedVote.options || []"
            :key="option"
            class="vote-option"
            :class="{ selected: selectedVoteOption === option }"
          >
            <label class="vote-option-label">
              <input
                v-model="selectedVoteOption"
                :value="option"
                type="radio"
                :disabled="selectedVote.status !== 'active' || hasVoted"
              />
              <span>{{ option }}</span>
            </label>

            <div class="vote-option-result">
              <div class="result-bar">
                <div
                  class="result-fill"
                  :style="{ width: `${voteStats[option]?.percentage || 0}%` }"
                ></div>
              </div>
              <span class="result-text">
                {{ voteStats[option]?.count || 0 }} {{ copy.voteUnit }}
                ({{ voteStats[option]?.percentage || 0 }}%)
              </span>
            </div>
          </div>
        </div>

        <div class="vote-action-row">
          <p v-if="selectedVote.status !== 'active'" class="vote-tip vote-tip--closed">
            {{ copy.voteClosedText }}
          </p>
          <p v-else-if="hasVoted" class="vote-tip">{{ copy.votedText }}</p>
          <p v-else-if="!isLoggedIn" class="vote-tip">{{ copy.voteLoginTip }}</p>
          <p v-else class="vote-tip">{{ copy.voteActionTip }}</p>

          <button
            class="submit-btn"
            :disabled="!canSubmitVote"
            @click="submitVote"
          >
            {{ isSubmittingVote ? copy.submittingVote : copy.submitVote }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Pagination } from '../components'
import { CommentService, TopicService, VoteService } from '../services'
import { useAuthStore } from '../stores/authStore'
import { useToast } from '../utils/useToast'

const copy = {
  title: '互动社区',
  subtitle: '在这里参与话题讨论、投票互动和观点交流，快速了解大家关注的内容与偏好。',
  heroKicker: '社区互动',
  totalTopics: '讨论话题',
  totalVotes: '投票活动',
  activeVotes: '进行中投票',
  tabTitle: '社区内容',
  tabSubtitle: '查看正在讨论的话题，或参与平台投票互动。',
  topicTab: '话题讨论',
  voteTab: '投票活动',
  loadingTopics: '正在加载话题内容...',
  loadingVotes: '正在加载投票活动...',
  topicBadge: '讨论',
  pinnedBadge: '置顶',
  voteBadge: '投票',
  commentsSuffix: '条评论',
  authorPrefix: '发起人',
  joinTopic: '进入讨论',
  viewVote: '查看投票',
  emptyTopicDescription: '这个话题暂时还没有详细说明。',
  emptyVoteDescription: '这个投票暂时还没有详细说明。',
  emptyTopicIcon: '话',
  emptyTopicTitle: '暂无讨论话题',
  emptyTopicText: '可以稍后再来查看最新互动内容。',
  emptyVoteIcon: '投',
  emptyVoteTitle: '暂无投票活动',
  emptyVoteText: '平台有新投票时会第一时间展示在这里。',
  topicDetailKicker: '话题详情',
  voteDetailKicker: '投票详情',
  commentsTitle: '评论区',
  commentsSubtitle: '留下你的看法，和其他用户一起交流。',
  commentAction: '发布评论',
  commentRule: '保持礼貌，清晰表达观点',
  commentPlaceholder: '写下你的想法、补充信息或个人感受...',
  commentHint: '支持多行输入',
  submitComment: '发布评论',
  submittingComment: '发布中...',
  loginPromptPrefix: '请先',
  loginAction: '登录',
  loginPromptSuffix: '后参与评论。',
  emptyCommentIcon: '评',
  emptyCommentTitle: '还没有评论',
  emptyCommentText: '成为第一个参与讨论的人。',
  voteTotalLabel: '总票数',
  voteUnit: '票',
  voteClosedText: '本次投票已结束。',
  votedText: '你已经参与过这次投票。',
  voteLoginTip: '登录后即可参与投票。',
  voteActionTip: '选择一个选项并提交你的投票。',
  submitVote: '提交投票',
  submittingVote: '提交中...',
  activeStatus: '进行中',
  closedStatus: '已结束',
  loginFirst: '请先登录',
  commentError: '评论内容不能为空',
  commentSuccess: '评论发布成功',
  commentFail: '评论发布失败，请重试',
  loadTopicFail: '加载话题失败，请稍后重试',
  loadVoteFail: '加载投票失败，请稍后重试',
  voteSelectError: '请选择投票选项',
  voteSuccess: '投票成功',
  voteFail: '投票失败，请稍后重试',
  guestInitial: '游',
  userPrefix: '用户',
  minutesAgo: '分钟前',
  hoursAgo: '小时前',
  daysAgo: '天前',
}

const authStore = useAuthStore()
const { success, error } = useToast()

const activeTab = ref('topics')

const topics = ref([])
const votes = ref([])

const selectedTopic = ref(null)
const selectedVote = ref(null)

const topicComments = ref([])
const newTopicComment = ref('')
const isSubmittingComment = ref(false)
const isLoadingTopics = ref(false)
const topicCurrentPage = ref(1)
const topicTotalPages = ref(1)
const topicTotalItems = ref(0)

const selectedVoteOption = ref('')
const voteStats = ref({})
const isLoadingVotes = ref(false)
const isSubmittingVote = ref(false)
const hasVoted = ref(false)
const voteCurrentPage = ref(1)
const voteTotalPages = ref(1)
const voteTotalItems = ref(0)

const isLoggedIn = computed(() => authStore.isLoggedIn)
const activeVoteCount = computed(() => votes.value.filter((vote) => vote.status === 'active').length)
const canSubmitVote = computed(() => {
  return Boolean(
    isLoggedIn.value &&
      selectedVote.value &&
      selectedVote.value.status === 'active' &&
      selectedVoteOption.value &&
      !hasVoted.value &&
      !isSubmittingVote.value,
  )
})

const loadTopics = async () => {
  isLoadingTopics.value = true

  try {
    const response = await TopicService.getTopics({
      pageNum: topicCurrentPage.value,
      pageSize: 8,
    })

    topics.value = response.items || []
    topicTotalPages.value = response.totalPages || 1
    topicTotalItems.value = response.total || 0
  } catch (err) {
    console.error('Failed to load topics:', err)
    error(copy.loadTopicFail)
  } finally {
    isLoadingTopics.value = false
  }
}

const loadVotes = async () => {
  isLoadingVotes.value = true

  try {
    const response = await VoteService.getVotes({
      pageNum: voteCurrentPage.value,
      pageSize: 6,
    })

    votes.value = response.items || []
    voteTotalPages.value = response.totalPages || 1
    voteTotalItems.value = response.total || 0
  } catch (err) {
    console.error('Failed to load votes:', err)
    error(copy.loadVoteFail)
  } finally {
    isLoadingVotes.value = false
  }
}

const selectTopic = async (topic) => {
  selectedTopic.value = topic
  newTopicComment.value = ''
  await loadTopicComments(topic.id)
}

const loadTopicComments = async (topicId) => {
  try {
    const response = await CommentService.getComments({
      topicId,
      pageNum: 1,
      pageSize: 30,
    })

    topicComments.value = response.items || []
  } catch (err) {
    console.error('Failed to load topic comments:', err)
    topicComments.value = []
  }
}

const submitTopicComment = async () => {
  const content = newTopicComment.value.trim()

  if (!content) {
    error(copy.commentError)
    return
  }

  isSubmittingComment.value = true

  try {
    await CommentService.postComment({
      content,
      userId: authStore.user.id,
      topicId: selectedTopic.value.id,
    })

    success(copy.commentSuccess)
    newTopicComment.value = ''
    await loadTopicComments(selectedTopic.value.id)
  } catch (err) {
    console.error('Failed to submit topic comment:', err)
    error(copy.commentFail)
  } finally {
    isSubmittingComment.value = false
  }
}

const selectVote = async (vote) => {
  selectedVote.value = vote
  selectedVoteOption.value = ''
  hasVoted.value = false
  await loadVoteStats(vote.id)
}

const loadVoteStats = async (voteId) => {
  try {
    const response = await VoteService.getVoteStatistics(voteId)
    const currentVote = {
      ...(selectedVote.value || {}),
      ...response,
      options: response.options || selectedVote.value?.options || [],
    }

    const totalVotes = currentVote.totalVotes || 0
    const stats = {}

    currentVote.options.forEach((option) => {
      const count = currentVote.statistics?.[option] || 0
      const percentage = totalVotes > 0 ? Math.round((count / totalVotes) * 100) : 0
      stats[option] = { count, percentage }
    })

    selectedVote.value = currentVote
    voteStats.value = stats

    if (isLoggedIn.value && currentVote.userVoted) {
      hasVoted.value = true
      selectedVoteOption.value = currentVote.userVoted
    }
  } catch (err) {
    console.error('Failed to load vote statistics:', err)
  }
}

const submitVote = async () => {
  if (!selectedVoteOption.value) {
    error(copy.voteSelectError)
    return
  }

  if (!isLoggedIn.value) {
    error(copy.loginFirst)
    return
  }

  isSubmittingVote.value = true

  try {
    await VoteService.submitVote(selectedVote.value.id, {
      selectedOption: selectedVoteOption.value,
      userId: authStore.user.id,
    })

    success(copy.voteSuccess)
    hasVoted.value = true
    await loadVoteStats(selectedVote.value.id)
    await loadVotes()
  } catch (err) {
    console.error('Failed to submit vote:', err)
    error(copy.voteFail)
  } finally {
    isSubmittingVote.value = false
  }
}

const handleTopicPageChange = (newPage) => {
  topicCurrentPage.value = newPage
  loadTopics()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleVotePageChange = (newPage) => {
  voteCurrentPage.value = newPage
  loadVotes()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const getTopicAuthor = (topic) => {
  return topic.creatorUsername || topic.createdBy || copy.userPrefix
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

const getVoteStatusLabel = (status) => {
  return status === 'active' ? copy.activeStatus : copy.closedStatus
}

const getVoteStatusClass = (status) => {
  return status === 'active' ? 'is-active' : 'is-closed'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < hour) {
    return `${Math.max(1, Math.floor(diff / minute))}${copy.minutesAgo}`
  }

  if (diff < day) {
    return `${Math.max(1, Math.floor(diff / hour))}${copy.hoursAgo}`
  }

  if (diff < day * 7) {
    return `${Math.max(1, Math.floor(diff / day))}${copy.daysAgo}`
  }

  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadTopics()
  loadVotes()
})
</script>

<style scoped>
.community-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 1160px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(300px, 0.85fr);
  gap: var(--spacing-lg);
  padding: 32px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.16), transparent 32%),
    linear-gradient(135deg, #0f5d5f 0%, #137f74 46%, #67b96d 100%);
  box-shadow: 0 24px 46px rgba(15, 93, 95, 0.22);
}

.hero-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
  color: #ffffff;
}

.hero-kicker,
.section-kicker {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.hero-kicker {
  background: rgba(255, 255, 255, 0.16);
}

.hero-content h1 {
  margin: 0;
  font-size: 40px;
  line-height: 1.15;
  font-weight: 800;
}

.hero-content p {
  margin: 0;
  max-width: 620px;
  color: rgba(255, 255, 255, 0.88);
  line-height: 1.85;
  font-size: 16px;
}

.hero-stats {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.hero-stat-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.16);
  color: #ffffff;
  backdrop-filter: blur(10px);
}

.hero-stat-card span {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.74);
}

.hero-stat-card strong {
  font-size: 28px;
  font-weight: 800;
}

.tabs-shell {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  padding: 24px;
  border-radius: 22px;
  background: var(--bg-primary);
  box-shadow: var(--shadow-md);
}

.tabs-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.tabs-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: var(--text-primary);
}

.tabs-header p {
  margin: 0;
  color: var(--text-secondary);
}

.tabs-container {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 6px;
  border-radius: 999px;
  background: #f4f8f8;
  border: 1px solid #e0ecec;
}

.tab-btn {
  padding: 11px 18px;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: #406364;
  font-size: 14px;
  font-weight: 700;
  transition: background 0.2s ease, box-shadow 0.2s ease, color 0.2s ease;
}

.tab-btn.active {
  background: linear-gradient(135deg, #137f74, #67b96d);
  color: #ffffff;
  box-shadow: 0 10px 18px rgba(19, 127, 116, 0.18);
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  padding: calc(var(--spacing-xl) * 1.8);
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

.topic-list,
.vote-grid {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.vote-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
}

.topic-card,
.vote-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease;
}

.topic-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 24px;
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff, #f8fcfb);
  border: 1px solid #e3eeeb;
  box-shadow: 0 14px 30px rgba(17, 78, 75, 0.06);
}

.vote-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 220px;
  padding: 22px;
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff, #f8fbfd);
  border: 1px solid #e3ebf0;
  box-shadow: 0 14px 30px rgba(15, 59, 105, 0.06);
}

.topic-card:hover,
.vote-card:hover {
  transform: translateY(-4px);
}

.topic-card:hover {
  border-color: rgba(19, 127, 116, 0.24);
  box-shadow: 0 20px 40px rgba(17, 78, 75, 0.12);
}

.vote-card:hover {
  border-color: rgba(0, 102, 204, 0.18);
  box-shadow: 0 20px 40px rgba(15, 59, 105, 0.12);
}

.topic-card-top,
.vote-card-top,
.topic-card-footer,
.vote-card-footer,
.comment-form-footer,
.vote-action-row,
.comment-meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.topic-badges,
.topic-meta,
.modal-meta,
.comment-meta-main {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.topic-badge,
.vote-type,
.vote-status {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.topic-badge {
  background: rgba(19, 127, 116, 0.1);
  color: #137f74;
}

.topic-badge--pinned {
  background: rgba(255, 180, 82, 0.18);
  color: #ac5b05;
}

.vote-type {
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
}

.vote-status.is-active {
  background: rgba(28, 154, 85, 0.12);
  color: #15713f;
}

.vote-status.is-closed {
  background: rgba(180, 55, 55, 0.1);
  color: #a53131;
}

.topic-card h3,
.vote-card h3,
.modal-head h2 {
  margin: 0;
  color: var(--text-primary);
}

.topic-card h3,
.vote-card h3 {
  font-size: 24px;
  line-height: 1.35;
}

.topic-description,
.vote-card p,
.modal-description,
.comment-content {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.85;
}

.topic-link,
.vote-link {
  font-weight: 700;
}

.topic-link {
  color: #137f74;
}

.vote-link {
  color: var(--primary-color);
}

.topic-time,
.vote-card-footer,
.comment-date {
  color: var(--text-light);
  font-size: 13px;
}

.meta-divider {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #ccd6db;
}

.empty-state,
.empty-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 48px 24px;
  border-radius: 20px;
  background: linear-gradient(180deg, #fbfbfb, #f7f8fa);
  border: 1px dashed #dde4ea;
  text-align: center;
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58px;
  height: 58px;
  border-radius: 50%;
  background: rgba(19, 127, 116, 0.08);
  color: #137f74;
  font-size: 22px;
  font-weight: 800;
}

.empty-state h3,
.empty-state p,
.empty-comments h4,
.empty-comments p {
  margin: 0;
}

.empty-state p,
.empty-comments p {
  color: var(--text-secondary);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(16, 27, 36, 0.56);
  z-index: 1000;
}

.modal-card {
  position: relative;
  width: min(860px, 100%);
  max-height: 88vh;
  overflow-y: auto;
  padding: 28px;
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 28px 54px rgba(0, 0, 0, 0.22);
}

.modal-card--vote {
  width: min(760px, 100%);
}

.close-btn {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: #f3f6f8;
  color: var(--text-secondary);
  font-size: 24px;
  line-height: 1;
}

.close-btn:hover {
  color: var(--text-primary);
}

.section-kicker {
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
}

.modal-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  padding-right: 44px;
}

.modal-head h2 {
  margin-top: 10px;
  font-size: 30px;
  line-height: 1.25;
}

.modal-description {
  margin-top: 18px;
}

.comments-section {
  display: flex;
  flex-direction: column;
  gap: 18px;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #edf0f3;
}

.comments-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.comments-header h3 {
  margin: 0 0 8px;
  font-size: 24px;
  color: var(--text-primary);
}

.comments-header p {
  margin: 0;
  color: var(--text-secondary);
}

.comments-count {
  padding: 10px 16px;
  border-radius: 999px;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-weight: 700;
}

.comment-form-card,
.comment-card {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr);
  gap: 14px;
}

.comment-form-card {
  padding: 20px;
  border-radius: 18px;
  background: linear-gradient(180deg, #fbfdff, #f5f9ff);
  border: 1px solid #dfe9f5;
}

.comment-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #137f74, #67b96d);
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
}

.comment-avatar--small {
  width: 44px;
  height: 44px;
  font-size: 16px;
}

.comment-form-main,
.comment-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-form-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.comment-form-head strong,
.comment-author {
  color: var(--text-primary);
  font-weight: 700;
}

.comment-form-head span,
.comment-hint {
  color: var(--text-light);
  font-size: 13px;
}

.comment-input {
  min-height: 120px;
  padding: 15px 16px;
  border: 1px solid #dbe5ef;
  border-radius: 16px;
  background: #ffffff;
  line-height: 1.8;
  resize: vertical;
}

.comment-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(0, 102, 204, 0.08);
}

.submit-btn {
  padding: 12px 22px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #ffffff;
  font-weight: 700;
  box-shadow: 0 12px 22px rgba(0, 102, 204, 0.18);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.login-prompt {
  padding: 18px 20px;
  border-radius: 16px;
  background: #f8fafb;
  border: 1px dashed #d9e2e8;
  text-align: center;
  color: var(--text-secondary);
}

.login-prompt p {
  margin: 0;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.comment-card {
  padding: 20px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid #edf1f4;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.04);
}

.vote-options {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 22px;
}

.vote-option {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 18px;
  border-radius: 18px;
  background: #f8fbfd;
  border: 1px solid #e1e8ee;
}

.vote-option.selected {
  border-color: rgba(0, 102, 204, 0.28);
  box-shadow: inset 0 0 0 1px rgba(0, 102, 204, 0.1);
}

.vote-option-label {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-primary);
  font-weight: 600;
}

.vote-option-result {
  display: flex;
  align-items: center;
  gap: 12px;
}

.result-bar {
  flex: 1;
  height: 10px;
  border-radius: 999px;
  background: #e8eef4;
  overflow: hidden;
}

.result-fill {
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
}

.result-text,
.vote-tip {
  color: var(--text-secondary);
  font-size: 14px;
}

.vote-tip {
  margin: 0;
}

.vote-tip--closed {
  color: #a53131;
}

@media (max-width: 960px) {
  .hero-section {
    grid-template-columns: 1fr;
  }

  .vote-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .community-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .hero-section,
  .tabs-shell,
  .modal-card {
    padding: 20px;
    border-radius: 18px;
  }

  .hero-content h1 {
    font-size: 34px;
  }

  .topic-card h3,
  .vote-card h3 {
    font-size: 22px;
  }

  .modal-head,
  .comments-header,
  .vote-action-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .comment-form-card,
  .comment-card {
    grid-template-columns: 1fr;
  }

  .modal-overlay {
    padding: 16px;
  }
}
</style>
