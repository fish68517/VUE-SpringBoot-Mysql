<template>
  <div class="community-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>互动交流</h1>
      <p>参与话题讨论、投票和反馈</p>
    </div>

    <!-- 标签页切换 -->
    <div class="tabs-container">
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'topics' }"
        @click="activeTab = 'topics'"
      >
        💬 话题讨论
      </button>
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'votes' }"
        @click="activeTab = 'votes'"
      >
        🗳️ 投票活动
      </button>
    </div>

    <!-- 话题讨论标签页 -->
    <div v-if="activeTab === 'topics'" class="tab-content">
      <!-- 加载状态 -->
      <div v-if="isLoadingTopics" class="loading-container">
        <div class="spinner"></div>
        <p>加载话题中...</p>
      </div>

      <!-- 话题列表 -->
      <div v-else-if="topics.length > 0" class="topics-container">
        <div
          v-for="topic in topics"
          :key="topic.id"
          class="topic-card"
          @click="selectTopic(topic)"
        >
          <div class="topic-header">
            <h3 class="topic-title">{{ topic.title }}</h3>
            <span v-if="topic.isPinned" class="pinned-badge">📌 置顶</span>
          </div>
          <p class="topic-description">{{ topic.description }}</p>
          <div class="topic-meta">
            <span class="meta-item">💬 {{ topic.commentCount || 0 }} 条评论</span>
            <span class="meta-item">👤 {{ topic.createdBy }}</span>
          </div>
        </div>

        <!-- 分页 -->
        <Pagination
          :current-page="topicCurrentPage"
          :total-pages="topicTotalPages"
          :total-items="topicTotalItems"
          @update:current-page="handleTopicPageChange"
        />
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">💬</div>
        <p>暂无话题</p>
      </div>

      <!-- 话题详情模态框 -->
      <div v-if="selectedTopic" class="modal-overlay" @click="selectedTopic = null">
        <div class="modal-content" @click.stop>
          <button class="close-btn" @click="selectedTopic = null">✕</button>

          <div class="topic-detail">
            <h2>{{ selectedTopic.title }}</h2>
            <p class="topic-detail-desc">{{ selectedTopic.description }}</p>

            <!-- 评论区 -->
            <div class="comments-section">
              <h3>讨论区</h3>

              <!-- 发布评论表单 -->
              <div v-if="isLoggedIn" class="comment-form">
                <textarea
                  v-model="newTopicComment"
                  placeholder="分享你的想法..."
                  class="comment-input"
                  rows="3"
                ></textarea>
                <button
                  @click="submitTopicComment"
                  class="submit-btn"
                  :disabled="!newTopicComment.trim() || isSubmittingComment"
                >
                  {{ isSubmittingComment ? '发布中...' : '发布评论' }}
                </button>
              </div>

              <!-- 未登录提示 -->
              <div v-else class="login-prompt">
                <p>请 <router-link to="/login">登录</router-link> 后发表评论</p>
              </div>

              <!-- 评论列表 -->
              <div class="comments-list">
                <div v-if="topicComments.length > 0">
                  <div
                    v-for="comment in topicComments"
                    :key="comment.id"
                    class="comment-item"
                  >
                    <div class="comment-header">
                      <span class="comment-author">{{ comment.userId }}</span>
                      <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                    </div>
                    <p class="comment-content">{{ comment.content }}</p>
                  </div>
                </div>
                <div v-else class="no-comments">
                  <p>暂无评论，来发表第一条评论吧！</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 投票活动标签页 -->
    <div v-if="activeTab === 'votes'" class="tab-content">
      <!-- 加载状态 -->
      <div v-if="isLoadingVotes" class="loading-container">
        <div class="spinner"></div>
        <p>加载投票中...</p>
      </div>

      <!-- 投票列表 -->
      <div v-else-if="votes.length > 0" class="votes-container">
        <div
          v-for="vote in votes"
          :key="vote.id"
          class="vote-card"
          @click="selectVote(vote)"
        >
          <h3 class="vote-title">{{ vote.title }}</h3>
          <p class="vote-description">{{ vote.description }}</p>
          <div class="vote-status">
            <span class="status-badge" :class="vote.status">
              {{ vote.status === 'active' ? '进行中' : '已结束' }}
            </span>
          </div>
        </div>

        <!-- 分页 -->
        <Pagination
          :current-page="voteCurrentPage"
          :total-pages="voteTotalPages"
          :total-items="voteTotalItems"
          @update:current-page="handleVotePageChange"
        />
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">🗳️</div>
        <p>暂无投票活动</p>
      </div>

      <!-- 投票详情模态框 -->
      <div v-if="selectedVote" class="modal-overlay" @click="selectedVote = null">
        <div class="modal-content vote-modal" @click.stop>
          <button class="close-btn" @click="selectedVote = null">✕</button>

          <div class="vote-detail">
            <h2>{{ selectedVote.title }}</h2>
            <p class="vote-detail-desc">{{ selectedVote.description }}</p>

            <!-- 投票选项 -->
            <div class="vote-options">
              <div
                v-for="(option, index) in selectedVote.options"
                :key="index"
                class="vote-option"
              >
                <div class="option-header">
                  <label class="option-label">
                    <input
                      type="radio"
                      :value="option"
                      v-model="selectedVoteOption"
                      :disabled="selectedVote.status === 'closed' || hasVoted"
                      class="option-radio"
                    />
                    <span>{{ option }}</span>
                  </label>
                </div>

                <!-- 投票结果显示 -->
                <div v-if="voteStats[option]" class="option-result">
                  <div class="result-bar">
                    <div
                      class="result-fill"
                      :style="{ width: voteStats[option].percentage + '%' }"
                    ></div>
                  </div>
                  <span class="result-text">
                    {{ voteStats[option].count }} 票 ({{ voteStats[option].percentage }}%)
                  </span>
                </div>
              </div>
            </div>

            <!-- 投票按钮 -->
            <div v-if="selectedVote.status === 'active'" class="vote-actions">
              <button
                @click="submitVote"
                class="submit-btn"
                :disabled="!selectedVoteOption || isSubmittingVote || hasVoted"
              >
                {{ hasVoted ? '已投票' : isSubmittingVote ? '投票中...' : '提交投票' }}
              </button>
            </div>

            <!-- 投票已结束提示 -->
            <div v-else class="vote-closed-msg">
              <p>投票已结束</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Pagination } from '../components'
import { TopicService, VoteService, CommentService } from '../services'
import { useAuthStore } from '../stores/authStore'
import { useToast } from '../utils/useToast'

const authStore = useAuthStore()
const { success, error } = useToast()

// 标签页状态
const activeTab = ref('topics')

// 话题相关状态
const topics = ref([])
const selectedTopic = ref(null)
const topicComments = ref([])
const newTopicComment = ref('')
const isLoadingTopics = ref(false)
const isSubmittingComment = ref(false)
const topicCurrentPage = ref(1)
const topicTotalPages = ref(1)
const topicTotalItems = ref(0)

// 投票相关状态
const votes = ref([])
const selectedVote = ref(null)
const selectedVoteOption = ref('')
const voteStats = ref({})
const isLoadingVotes = ref(false)
const isSubmittingVote = ref(false)
const hasVoted = ref(false)
const voteCurrentPage = ref(1)
const voteTotalPages = ref(1)
const voteTotalItems = ref(0)

const isLoggedIn = authStore.isLoggedIn

// 加载话题列表
const loadTopics = async () => {
  isLoadingTopics.value = true
  try {
    const response = await TopicService.getTopics({
      pageNum: topicCurrentPage.value,
      pageSize: 10,
    })
    const data = response.data
    topics.value = data.content || []
    topicTotalPages.value = data.totalPages || 1
    topicTotalItems.value = data.total || 0
  } catch (err) {
    console.error('Failed to load topics:', err)
    error('加载话题失败，请重试')
  } finally {
    isLoadingTopics.value = false
  }
}

// 加载投票列表
const loadVotes = async () => {
  isLoadingVotes.value = true
  try {
    const response = await VoteService.getVotes({
      pageNum: voteCurrentPage.value,
      pageSize: 10,
    })
    const data = response.data
    votes.value = data.content || []
    voteTotalPages.value = data.totalPages || 1
    voteTotalItems.value = data.total || 0
  } catch (err) {
    console.error('Failed to load votes:', err)
    error('加载投票失败，请重试')
  } finally {
    isLoadingVotes.value = false
  }
}

// 选择话题
const selectTopic = async (topic) => {
  selectedTopic.value = topic
  newTopicComment.value = ''
  await loadTopicComments(topic.id)
}

// 加载话题评论
const loadTopicComments = async (topicId) => {
  try {
    const response = await CommentService.getComments({
      topicId: topicId,
      pageNum: 1,
      pageSize: 20,
    })
    topicComments.value = response.data.content || []
  } catch (err) {
    console.error('Failed to load topic comments:', err)
  }
}

// 提交话题评论
const submitTopicComment = async () => {
  if (!newTopicComment.value.trim()) {
    error('评论内容不能为空')
    return
  }

  isSubmittingComment.value = true
  try {
    const commentData = {
      content: newTopicComment.value,
      userId: authStore.user.id,
      topicId: selectedTopic.value.id,
    }

    await CommentService.postComment(commentData)
    success('评论发布成功')
    newTopicComment.value = ''

    // 重新加载评论列表
    await loadTopicComments(selectedTopic.value.id)
  } catch (err) {
    console.error('Failed to submit comment:', err)
    error('评论发布失败，请重试')
  } finally {
    isSubmittingComment.value = false
  }
}

// 选择投票
const selectVote = async (vote) => {
  selectedVote.value = vote
  selectedVoteOption.value = ''
  hasVoted.value = false
  await loadVoteStats(vote.id)
}

// 加载投票统计
const loadVoteStats = async (voteId) => {
  try {
    const response = await VoteService.getVoteDetail(voteId)
    const vote = response.data

    // 计算投票统计
    const stats = {}
    const totalVotes = vote.totalVotes || 0

    if (vote.options && Array.isArray(vote.options)) {
      vote.options.forEach((option) => {
        const count = vote.voteResults?.[option] || 0
        const percentage = totalVotes > 0 ? Math.round((count / totalVotes) * 100) : 0
        stats[option] = { count, percentage }
      })
    }

    voteStats.value = stats

    // 检查当前用户是否已投票
    if (isLoggedIn && vote.userVoted) {
      hasVoted.value = true
      selectedVoteOption.value = vote.userVoted
    }
  } catch (err) {
    console.error('Failed to load vote stats:', err)
  }
}

// 提交投票
const submitVote = async () => {
  if (!selectedVoteOption.value) {
    error('请选择投票选项')
    return
  }

  isSubmittingVote.value = true
  try {
    const voteData = {
      selectedOption: selectedVoteOption.value,
      userId: authStore.user.id,
    }

    await VoteService.submitVote(selectedVote.value.id, voteData)
    success('投票成功')
    hasVoted.value = true

    // 重新加载投票统计
    await loadVoteStats(selectedVote.value.id)
  } catch (err) {
    console.error('Failed to submit vote:', err)
    error('投票失败，请重试')
  } finally {
    isSubmittingVote.value = false
  }
}

// 处理话题分页变化
const handleTopicPageChange = (newPage) => {
  topicCurrentPage.value = newPage
  loadTopics()
}

// 处理投票分页变化
const handleVotePageChange = (newPage) => {
  voteCurrentPage.value = newPage
  loadVotes()
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now - date)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    return '今天'
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
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
  padding: var(--spacing-lg);
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  font-size: var(--font-size-2xl);
  color: var(--primary-color);
  margin-bottom: var(--spacing-sm);
  font-weight: 700;
}

.page-header p {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
}

/* 标签页 */
.tabs-container {
  display: flex;
  gap: var(--spacing-md);
  border-bottom: 2px solid var(--border-color);
  margin-bottom: var(--spacing-lg);
}

.tab-btn {
  padding: var(--spacing-md) var(--spacing-lg);
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: var(--font-size-lg);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
}

.tab-btn:hover {
  color: var(--primary-color);
}

.tab-btn.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
  gap: var(--spacing-lg);
}

.spinner {
  width: 50px;
  height: 50px;
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

.loading-container p {
  color: var(--text-secondary);
  font-size: var(--font-size-lg);
}

/* 话题容器 */
.topics-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.topic-card {
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid var(--primary-color);
}

.topic-card:hover {
  transform: translateX(8px);
  box-shadow: var(--shadow-lg);
}

.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.topic-title {
  margin: 0;
  font-size: var(--font-size-lg);
  color: var(--text-primary);
  font-weight: 600;
  flex: 1;
}

.pinned-badge {
  background-color: var(--primary-color);
  color: white;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  font-weight: 600;
  white-space: nowrap;
}

.topic-description {
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.topic-meta {
  display: flex;
  gap: var(--spacing-lg);
  font-size: var(--font-size-sm);
  color: var(--text-light);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

/* 投票容器 */
.votes-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.vote-card {
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: all 0.3s ease;
}

.vote-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
}

.vote-title {
  margin: 0 0 var(--spacing-md) 0;
  font-size: var(--font-size-lg);
  color: var(--text-primary);
  font-weight: 600;
}

.vote-description {
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.vote-status {
  display: flex;
  justify-content: flex-end;
}

.status-badge {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  font-weight: 600;
}

.status-badge.active {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-badge.closed {
  background-color: #ffebee;
  color: #c62828;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
  gap: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.empty-icon {
  font-size: 60px;
}

.empty-state p {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
  margin: 0;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-lg);
}

.modal-content {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  padding: var(--spacing-xl);
}

.modal-content.vote-modal {
  max-width: 700px;
}

.close-btn {
  position: absolute;
  top: var(--spacing-lg);
  right: var(--spacing-lg);
  background: none;
  border: none;
  font-size: var(--font-size-xl);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  color: var(--text-primary);
  transform: scale(1.2);
}

/* 话题详情 */
.topic-detail h2 {
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-primary);
  font-size: var(--font-size-xl);
  font-weight: 700;
}

.topic-detail-desc {
  margin: 0 0 var(--spacing-lg) 0;
  color: var(--text-secondary);
  line-height: 1.8;
}

/* 投票详情 */
.vote-detail h2 {
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-primary);
  font-size: var(--font-size-xl);
  font-weight: 700;
}

.vote-detail-desc {
  margin: 0 0 var(--spacing-lg) 0;
  color: var(--text-secondary);
  line-height: 1.8;
}

/* 投票选项 */
.vote-options {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.vote-option {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  border: 2px solid var(--border-color);
  transition: all 0.3s ease;
}

.vote-option:hover {
  border-color: var(--primary-color);
}

.option-header {
  margin-bottom: var(--spacing-md);
}

.option-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  cursor: pointer;
  font-weight: 500;
  color: var(--text-primary);
}

.option-radio {
  cursor: pointer;
  width: 18px;
  height: 18px;
}

.option-result {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.result-bar {
  width: 100%;
  height: 24px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

.result-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-light) 0%, var(--primary-color) 100%);
  transition: width 0.3s ease;
}

.result-text {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

/* 评论区 */
.comments-section {
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-xl);
  border-top: 1px solid var(--border-color);
}

.comments-section h3 {
  margin: 0 0 var(--spacing-lg) 0;
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: 600;
}

/* 评论表单 */
.comment-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
}

.comment-input {
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-family: inherit;
  font-size: var(--font-size-base);
  color: var(--text-primary);
  background-color: var(--bg-primary);
  resize: vertical;
}

.comment-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
}

.submit-btn {
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover:not(:disabled) {
  background-color: var(--primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 登录提示 */
.login-prompt {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  text-align: center;
  margin-bottom: var(--spacing-lg);
}

.login-prompt p {
  margin: 0;
  color: var(--text-secondary);
}

.login-prompt a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
}

.login-prompt a:hover {
  text-decoration: underline;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.comment-item {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  border-left: 4px solid var(--primary-color);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.comment-author {
  font-weight: 600;
  color: var(--text-primary);
}

.comment-date {
  font-size: var(--font-size-sm);
  color: var(--text-light);
}

.comment-content {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.6;
}

.no-comments {
  padding: var(--spacing-lg);
  text-align: center;
  color: var(--text-light);
}

/* 投票操作 */
.vote-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

.vote-closed-msg {
  padding: var(--spacing-lg);
  background-color: #ffebee;
  border-radius: var(--border-radius-md);
  text-align: center;
  color: #c62828;
  font-weight: 600;
}

.vote-closed-msg p {
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .community-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .page-header h1 {
    font-size: var(--font-size-xl);
  }

  .tabs-container {
    gap: var(--spacing-sm);
  }

  .tab-btn {
    padding: var(--spacing-md) var(--spacing-md);
    font-size: var(--font-size-base);
  }

  .votes-container {
    grid-template-columns: 1fr;
  }

  .modal-content {
    max-width: 90vw;
    max-height: 90vh;
    padding: var(--spacing-lg);
  }

  .vote-options {
    gap: var(--spacing-md);
  }

  .vote-option {
    padding: var(--spacing-md);
  }
}
</style>
