<template>
  <div class="admin-interactions-page">
    <div class="page-header">
      <h1>互动管理</h1>
    </div>

    <!-- 标签页导航 -->
    <div class="tabs-container">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        :class="['tab-button', { active: activeTab === tab.id }]"
        @click="activeTab = tab.id"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 评论管理标签页 -->
    <div v-if="activeTab === 'comments'" class="tab-content">
      <div class="section-header">
        <h2>评论管理</h2>
      </div>

      <!-- 评论列表 -->
      <div class="table-container">
        <table class="interactions-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户</th>
              <th>内容</th>
              <th>目标类型</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="comments.length === 0">
              <td colspan="6" class="empty-message">暂无评论数据</td>
            </tr>
            <tr v-for="comment in comments" :key="comment.id" class="interaction-row">
              <td>{{ comment.id }}</td>
              <td>{{ comment.userName || '匿名用户' }}</td>
              <td class="content-cell">{{ truncateText(comment.content, 50) }}</td>
              <td>{{ comment.targetType || '-' }}</td>
              <td>{{ formatDate(comment.createdAt) }}</td>
              <td class="action-cell">
                <button @click="viewCommentDetail(comment)" class="btn-action btn-view" title="查看">
                  👁️
                </button>
                <button @click="showReplyModal(comment)" class="btn-action btn-reply" title="回复">
                  💬
                </button>
                <button @click="deleteCommentConfirm(comment)" class="btn-action btn-delete" title="删除">
                  🗑️
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <Pagination
          :current-page="commentCurrentPage"
          :total-pages="commentTotalPages"
          :total-items="commentTotalItems"
          @update:current-page="handleCommentPageChange"
        />
      </div>
    </div>

    <!-- 话题管理标签页 -->
    <div v-if="activeTab === 'topics'" class="tab-content">
      <div class="section-header">
        <h2>话题管理</h2>
      </div>

      <!-- 话题列表 -->
      <div class="table-container">
        <table class="interactions-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>话题标题</th>
              <th>创建者</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="topics.length === 0">
              <td colspan="6" class="empty-message">暂无话题数据</td>
            </tr>
            <tr v-for="topic in topics" :key="topic.id" class="interaction-row">
              <td>{{ topic.id }}</td>
              <td>{{ topic.title }}</td>
              <td>{{ topic.creatorName || '管理员' }}</td>
              <td>
                <span class="status-badge" :class="{ 'status-pinned': topic.isPinned }">
                  {{ topic.isPinned ? '已置顶' : '正常' }}
                </span>
              </td>
              <td>{{ formatDate(topic.createdAt) }}</td>
              <td class="action-cell">
                <button @click="viewTopicDetail(topic)" class="btn-action btn-view" title="查看">
                  👁️
                </button>
                <button
                  v-if="!topic.isPinned"
                  @click="pinTopic(topic)"
                  class="btn-action btn-pin"
                  title="置顶"
                >
                  📌
                </button>
                <button
                  v-if="topic.isPinned"
                  @click="unpinTopic(topic)"
                  class="btn-action btn-unpin"
                  title="取消置顶"
                >
                  📍
                </button>
                <button @click="deleteTopicConfirm(topic)" class="btn-action btn-delete" title="删除">
                  🗑️
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <Pagination
          :current-page="topicCurrentPage"
          :total-pages="topicTotalPages"
          :total-items="topicTotalItems"
          @update:current-page="handleTopicPageChange"
        />
      </div>
    </div>

    <!-- 投票统计标签页 -->
    <div v-if="activeTab === 'votes'" class="tab-content">
      <div class="section-header">
        <h2>投票统计</h2>
      </div>

      <!-- 投票列表 -->
      <div class="table-container">
        <table class="interactions-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>投票标题</th>
              <th>状态</th>
              <th>参与人数</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="votes.length === 0">
              <td colspan="6" class="empty-message">暂无投票数据</td>
            </tr>
            <tr v-for="vote in votes" :key="vote.id" class="interaction-row">
              <td>{{ vote.id }}</td>
              <td>{{ vote.title }}</td>
              <td>
                <span class="status-badge" :class="`status-${vote.status}`">
                  {{ vote.status === 'active' ? '进行中' : '已结束' }}
                </span>
              </td>
              <td>{{ vote.participantCount || 0 }}</td>
              <td>{{ formatDate(vote.createdAt) }}</td>
              <td class="action-cell">
                <button @click="viewVoteStats(vote)" class="btn-action btn-view" title="查看统计">
                  📊
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <Pagination
          :current-page="voteCurrentPage"
          :total-pages="voteTotalPages"
          :total-items="voteTotalItems"
          @update:current-page="handleVotePageChange"
        />
      </div>
    </div>

    <!-- 反馈处理标签页 -->
    <div v-if="activeTab === 'feedback'" class="tab-content">
      <div class="section-header">
        <h2>反馈处理</h2>
      </div>

      <!-- 反馈列表 -->
      <div class="table-container">
        <table class="interactions-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户邮箱</th>
              <th>内容</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="feedbackList.length === 0">
              <td colspan="6" class="empty-message">暂无反馈数据</td>
            </tr>
            <tr v-for="feedback in feedbackList" :key="feedback.id" class="interaction-row">
              <td>{{ feedback.id }}</td>
              <td>{{ feedback.email }}</td>
              <td class="content-cell">{{ truncateText(feedback.content, 50) }}</td>
              <td>
                <span class="status-badge" :class="`status-${feedback.status}`">
                  {{ feedback.status === 'pending' ? '待处理' : '已处理' }}
                </span>
              </td>
              <td>{{ formatDate(feedback.createdAt) }}</td>
              <td class="action-cell">
                <button @click="viewFeedbackDetail(feedback)" class="btn-action btn-view" title="查看">
                  👁️
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <Pagination
          :current-page="feedbackCurrentPage"
          :total-pages="feedbackTotalPages"
          :total-items="feedbackTotalItems"
          @update:current-page="handleFeedbackPageChange"
        />
      </div>
    </div>

    <!-- 评论详情模态框 -->
    <div class="modal-overlay" v-if="showCommentDetailModal" @click.self="showCommentDetailModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>评论详情</h2>
          <button @click="showCommentDetailModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-item">
            <label>评论ID:</label>
            <span>{{ selectedComment.id }}</span>
          </div>
          <div class="detail-item">
            <label>用户:</label>
            <span>{{ selectedComment.userName || '匿名用户' }}</span>
          </div>
          <div class="detail-item">
            <label>内容:</label>
            <span class="content-text">{{ selectedComment.content }}</span>
          </div>
          <div class="detail-item">
            <label>目标类型:</label>
            <span>{{ selectedComment.targetType || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>创建时间:</label>
            <span>{{ formatDate(selectedComment.createdAt) }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showCommentDetailModal = false" class="btn btn-secondary">关闭</button>
        </div>
      </div>
    </div>

    <!-- 评论回复模态框 -->
    <div class="modal-overlay" v-if="showCommentReplyModal" @click.self="showCommentReplyModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>回复评论</h2>
          <button @click="showCommentReplyModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>原评论内容:</label>
            <div class="original-comment">{{ selectedComment.content }}</div>
          </div>
          <div class="form-group">
            <label>回复内容 *</label>
            <textarea
              v-model="replyData.content"
              placeholder="输入回复内容"
              class="form-textarea"
              rows="4"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showCommentReplyModal = false" class="btn btn-secondary">取消</button>
          <button @click="submitReply" class="btn btn-primary" :disabled="isReplying">
            {{ isReplying ? '发送中...' : '发送回复' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 话题详情模态框 -->
    <div class="modal-overlay" v-if="showTopicDetailModal" @click.self="showTopicDetailModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>话题详情</h2>
          <button @click="showTopicDetailModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-item">
            <label>话题ID:</label>
            <span>{{ selectedTopic.id }}</span>
          </div>
          <div class="detail-item">
            <label>标题:</label>
            <span>{{ selectedTopic.title }}</span>
          </div>
          <div class="detail-item">
            <label>描述:</label>
            <span class="content-text">{{ selectedTopic.description }}</span>
          </div>
          <div class="detail-item">
            <label>创建者:</label>
            <span>{{ selectedTopic.creatorName || '管理员' }}</span>
          </div>
          <div class="detail-item">
            <label>状态:</label>
            <span class="status-badge" :class="{ 'status-pinned': selectedTopic.isPinned }">
              {{ selectedTopic.isPinned ? '已置顶' : '正常' }}
            </span>
          </div>
          <div class="detail-item">
            <label>创建时间:</label>
            <span>{{ formatDate(selectedTopic.createdAt) }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showTopicDetailModal = false" class="btn btn-secondary">关闭</button>
        </div>
      </div>
    </div>

    <!-- 投票统计模态框 -->
    <div class="modal-overlay" v-if="showVoteStatsModal" @click.self="showVoteStatsModal = false">
      <div class="modal modal-large">
        <div class="modal-header">
          <h2>投票统计</h2>
          <button @click="showVoteStatsModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-item">
            <label>投票标题:</label>
            <span>{{ selectedVote.title }}</span>
          </div>
          <div class="detail-item">
            <label>投票描述:</label>
            <span class="content-text">{{ selectedVote.description }}</span>
          </div>
          <div class="detail-item">
            <label>状态:</label>
            <span class="status-badge" :class="`status-${selectedVote.status}`">
              {{ selectedVote.status === 'active' ? '进行中' : '已结束' }}
            </span>
          </div>
          <div class="detail-item">
            <label>参与人数:</label>
            <span>{{ selectedVote.participantCount || 0 }}</span>
          </div>

          <!-- 投票选项统计 -->
          <div class="vote-stats-section" v-if="voteStats && voteStats.options">
            <h3>投票选项统计</h3>
            <div v-for="option in voteStats.options" :key="option.id" class="vote-option-stat">
              <div class="option-header">
                <span class="option-name">{{ option.name }}</span>
                <span class="option-count">{{ option.count }} 票</span>
              </div>
              <div class="option-bar">
                <div
                  class="option-progress"
                  :style="{ width: calculatePercentage(option.count, selectedVote.participantCount) + '%' }"
                ></div>
              </div>
              <span class="option-percentage">
                {{ calculatePercentage(option.count, selectedVote.participantCount) }}%
              </span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showVoteStatsModal = false" class="btn btn-secondary">关闭</button>
        </div>
      </div>
    </div>

    <!-- 反馈详情模态框 -->
    <div class="modal-overlay" v-if="showFeedbackDetailModal" @click.self="showFeedbackDetailModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>反馈详情</h2>
          <button @click="showFeedbackDetailModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-item">
            <label>反馈ID:</label>
            <span>{{ selectedFeedback.id }}</span>
          </div>
          <div class="detail-item">
            <label>用户邮箱:</label>
            <span>{{ selectedFeedback.email }}</span>
          </div>
          <div class="detail-item">
            <label>内容:</label>
            <span class="content-text">{{ selectedFeedback.content }}</span>
          </div>
          <div class="detail-item">
            <label>状态:</label>
            <span class="status-badge" :class="`status-${selectedFeedback.status}`">
              {{ selectedFeedback.status === 'pending' ? '待处理' : '已处理' }}
            </span>
          </div>
          <div class="detail-item">
            <label>创建时间:</label>
            <span>{{ formatDate(selectedFeedback.createdAt) }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showFeedbackDetailModal = false" class="btn btn-secondary">关闭</button>
        </div>
      </div>
    </div>

    <!-- 删除确认模态框 -->
    <div class="modal-overlay" v-if="showDeleteConfirm" @click.self="showDeleteConfirm = false">
      <div class="modal modal-small">
        <div class="modal-header">
          <h2>确认删除</h2>
          <button @click="showDeleteConfirm = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <p>确定要删除这条{{ deleteType === 'comment' ? '评论' : '话题' }}吗？此操作不可撤销。</p>
        </div>
        <div class="modal-footer">
          <button @click="showDeleteConfirm = false" class="btn btn-secondary">取消</button>
          <button @click="confirmDelete" class="btn btn-danger" :disabled="isDeleting">
            {{ isDeleting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Toast 通知 -->
    <Toast ref="toast" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { AdminInteractionService } from '../../services'
import Pagination from '../../components/Pagination.vue'
import Toast from '../../components/Toast.vue'

// 标签页
const tabs = [
  { id: 'comments', label: '评论管理' },
  { id: 'topics', label: '话题管理' },
  { id: 'votes', label: '投票统计' },
  { id: 'feedback', label: '反馈处理' }
]

const activeTab = ref('comments')
const toast = ref(null)

// ===== 评论管理状态 =====
const comments = ref([])
const commentCurrentPage = ref(1)
const commentPageSize = ref(10)
const commentTotalItems = ref(0)
const selectedComment = ref(null)
const showCommentDetailModal = ref(false)
const showCommentReplyModal = ref(false)
const isReplying = ref(false)

const replyData = ref({
  content: ''
})

// ===== 话题管理状态 =====
const topics = ref([])
const topicCurrentPage = ref(1)
const topicPageSize = ref(10)
const topicTotalItems = ref(0)
const selectedTopic = ref(null)
const showTopicDetailModal = ref(false)

// ===== 投票统计状态 =====
const votes = ref([])
const voteCurrentPage = ref(1)
const votePageSize = ref(10)
const voteTotalItems = ref(0)
const selectedVote = ref(null)
const voteStats = ref(null)
const showVoteStatsModal = ref(false)

// ===== 反馈处理状态 =====
const feedbackList = ref([])
const feedbackCurrentPage = ref(1)
const feedbackPageSize = ref(10)
const feedbackTotalItems = ref(0)
const selectedFeedback = ref(null)
const showFeedbackDetailModal = ref(false)

// ===== 通用状态 =====
const showDeleteConfirm = ref(false)
const itemToDelete = ref(null)
const deleteType = ref(null)
const isDeleting = ref(false)

// 计算属性
const commentTotalPages = computed(() => Math.ceil(commentTotalItems.value / commentPageSize.value))
const topicTotalPages = computed(() => Math.ceil(topicTotalItems.value / topicPageSize.value))
const voteTotalPages = computed(() => Math.ceil(voteTotalItems.value / votePageSize.value))
const feedbackTotalPages = computed(() => Math.ceil(feedbackTotalItems.value / feedbackPageSize.value))

// 工具方法
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const truncateText = (text, length) => {
  if (!text) return '-'
  return text.length > length ? text.substring(0, length) + '...' : text
}

const calculatePercentage = (count, total) => {
  if (!total || total === 0) return 0
  return Math.round((count / total) * 100)
}

// ===== 评论管理方法 =====
const loadComments = async () => {
  try {
    const params = {
      pageNum: commentCurrentPage.value,
      pageSize: commentPageSize.value
    }
    const response = await AdminInteractionService.getComments(params)
    if (response && response.comments) {
      comments.value = response.comments || []
      commentTotalItems.value = response.total || 0
    }
  } catch (error) {
    console.error('加载评论列表错误:', error)
    toast.value.error('加载评论列表失败')
  }
}

const handleCommentPageChange = (page) => {
  commentCurrentPage.value = page
  loadComments()
}

const viewCommentDetail = (comment) => {
  selectedComment.value = comment
  showCommentDetailModal.value = true
}

const showReplyModal = (comment) => {
  selectedComment.value = comment
  replyData.value = { content: '' }
  showCommentReplyModal.value = true
}

const submitReply = async () => {
  if (!replyData.value.content) {
    toast.value.warning('请输入回复内容')
    return
  }

  isReplying.value = true
  try {
    const response = await AdminInteractionService.replyComment(selectedComment.value.id, replyData.value)
    if (response) {
      toast.value.success('回复成功')
      showCommentReplyModal.value = false
      loadComments()
    } else {
      toast.value.error('回复失败')
    }
  } catch (error) {
    console.error('回复评论错误:', error)
    toast.value.error('回复评论失败')
  } finally {
    isReplying.value = false
  }
}

const deleteCommentConfirm = (comment) => {
  itemToDelete.value = comment
  deleteType.value = 'comment'
  showDeleteConfirm.value = true
}

// ===== 话题管理方法 =====
const loadTopics = async () => {
  try {
    const params = {
      pageNum: topicCurrentPage.value,
      pageSize: topicPageSize.value
    }
    const response = await AdminInteractionService.getTopics(params)
    if (response && response.topics) {
      topics.value = response.topics || []
      topicTotalItems.value = response.total || 0
    }
  } catch (error) {
    console.error('加载话题列表错误:', error)
    toast.value.error('加载话题列表失败')
  }
}

const handleTopicPageChange = (page) => {
  topicCurrentPage.value = page
  loadTopics()
}

const viewTopicDetail = (topic) => {
  selectedTopic.value = topic
  showTopicDetailModal.value = true
}

const pinTopic = async (topic) => {
  try {
    const response = await AdminInteractionService.pinTopic(topic.id)
    if (response) {
      toast.value.success('话题已置顶')
      loadTopics()
    } else {
      toast.value.error('置顶失败')
    }
  } catch (error) {
    console.error('置顶话题错误:', error)
    toast.value.error('置顶话题失败')
  }
}

const unpinTopic = async (topic) => {
  try {
    const response = await AdminInteractionService.pinTopic(topic.id)
    if (response) {
      toast.value.success('已取消置顶')
      loadTopics()
    } else {
      toast.value.error('取消置顶失败')
    }
  } catch (error) {
    console.error('取消置顶话题错误:', error)
    toast.value.error('取消置顶话题失败')
  }
}

const deleteTopicConfirm = (topic) => {
  itemToDelete.value = topic
  deleteType.value = 'topic'
  showDeleteConfirm.value = true
}

// ===== 投票统计方法 =====
const loadVotes = async () => {
  try {
    const params = {
      pageNum: voteCurrentPage.value,
      pageSize: votePageSize.value
    }
    const response = await AdminInteractionService.getVotes?.(params)
    if (response && response.votes) {
      votes.value = response.votes || []
      voteTotalItems.value = response.total || 0
    }
  } catch (error) {
    console.error('加载投票列表错误:', error)
    toast.value.error('加载投票列表失败')
  }
}

const handleVotePageChange = (page) => {
  voteCurrentPage.value = page
  loadVotes()
}

const viewVoteStats = async (vote) => {
  selectedVote.value = vote
  try {
    const response = await AdminInteractionService.getVoteStats(vote.id)
    if (response) {
      voteStats.value = response
      showVoteStatsModal.value = true
    } else {
      toast.value.error('加载投票统计失败')
    }
  } catch (error) {
    console.error('加载投票统计错误:', error)
    toast.value.error('加载投票统计失败')
  }
}

// ===== 反馈处理方法 =====
const loadFeedback = async () => {
  try {
    const params = {
      pageNum: feedbackCurrentPage.value,
      pageSize: feedbackPageSize.value
    }
    const response = await AdminInteractionService.getFeedback(params)
    if (response && response.feedback) {
      feedbackList.value = response.feedback || []
      feedbackTotalItems.value = response.total || 0
    }
  } catch (error) {
    console.error('加载反馈列表错误:', error)
    toast.value.error('加载反馈列表失败')
  }
}

const handleFeedbackPageChange = (page) => {
  feedbackCurrentPage.value = page
  loadFeedback()
}

const viewFeedbackDetail = (feedback) => {
  selectedFeedback.value = feedback
  showFeedbackDetailModal.value = true
}

// ===== 通用删除方法 =====
const confirmDelete = async () => {
  isDeleting.value = true
  try {
    let response
    if (deleteType.value === 'comment') {
      response = await AdminInteractionService.deleteComment(itemToDelete.value.id)
    } else if (deleteType.value === 'topic') {
      response = await AdminInteractionService.deleteTopic(itemToDelete.value.id)
    }

    if (response) {
      toast.value.success('删除成功')
      showDeleteConfirm.value = false
      if (deleteType.value === 'comment') loadComments()
      else if (deleteType.value === 'topic') loadTopics()
    } else {
      toast.value.error('删除失败')
    }
  } catch (error) {
    console.error('删除错误:', error)
    toast.value.error('删除失败')
  } finally {
    isDeleting.value = false
  }
}

// 生命周期
onMounted(() => {
  loadComments()
  loadTopics()
  loadVotes()
  loadFeedback()
})
</script>
<style s
coped>
.admin-interactions-page {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  min-height: 100vh;
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  color: var(--primary-color);
  margin: 0;
  font-size: var(--font-size-2xl);
}

/* 标签页 */
.tabs-container {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  border-bottom: 2px solid var(--border-color);
}

.tab-button {
  padding: var(--spacing-md) var(--spacing-lg);
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-button:hover {
  color: var(--primary-color);
}

.tab-button.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.section-header h2 {
  color: var(--primary-color);
  margin: 0;
  font-size: var(--font-size-xl);
}

/* 表格 */
.table-container {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  margin-bottom: var(--spacing-lg);
}

.interactions-table {
  width: 100%;
  border-collapse: collapse;
}

.interactions-table thead {
  background-color: var(--primary-color);
  color: white;
}

.interactions-table th {
  padding: var(--spacing-md);
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid var(--border-color);
}

.interactions-table td {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

.interaction-row:hover {
  background-color: var(--bg-secondary);
}

.empty-message {
  text-align: center;
  color: var(--text-secondary);
  padding: var(--spacing-lg) !important;
}

.content-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  font-weight: 600;
}

.status-active {
  background-color: #27ae60;
  color: white;
}

.status-closed {
  background-color: #95a5a6;
  color: white;
}

.status-pending {
  background-color: #f39c12;
  color: white;
}

.status-processed {
  background-color: #27ae60;
  color: white;
}

.status-pinned {
  background-color: #e74c3c;
  color: white;
}

.action-cell {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.btn-action {
  background: none;
  border: none;
  font-size: var(--font-size-lg);
  cursor: pointer;
  padding: var(--spacing-xs);
  transition: transform 0.2s ease;
}

.btn-action:hover {
  transform: scale(1.2);
}

.btn-view {
  color: #0066cc;
}

.btn-reply {
  color: #27ae60;
}

.btn-pin {
  color: #e74c3c;
}

.btn-unpin {
  color: #f39c12;
}

.btn-delete {
  color: #e74c3c;
}

/* 按钮样式 */
.btn {
  padding: var(--spacing-md) var(--spacing-lg);
  border: none;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.btn-primary {
  background-color: var(--primary-color);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover:not(:disabled) {
  background-color: var(--border-color);
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
}

.modal {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-large {
  max-width: 700px;
}

.modal-small {
  max-width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  margin: 0;
  color: var(--primary-color);
}

.modal-close {
  background: none;
  border: none;
  font-size: var(--font-size-2xl);
  cursor: pointer;
  color: var(--text-secondary);
  transition: color 0.3s ease;
}

.modal-close:hover {
  color: var(--text-primary);
}

.modal-body {
  padding: var(--spacing-lg);
}

.modal-footer {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.detail-item label {
  font-weight: 600;
  color: var(--text-primary);
}

.detail-item span {
  color: var(--text-secondary);
  padding: var(--spacing-md);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  word-break: break-word;
}

.content-text {
  white-space: pre-wrap;
  word-wrap: break-word;
}

.original-comment {
  padding: var(--spacing-md);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  border-left: 3px solid var(--primary-color);
  color: var(--text-secondary);
  white-space: pre-wrap;
  word-wrap: break-word;
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.form-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  font-family: inherit;
  resize: vertical;
}

.form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
}

/* 投票统计 */
.vote-stats-section {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.vote-stats-section h3 {
  color: var(--primary-color);
  margin-bottom: var(--spacing-md);
}

.vote-option-stat {
  margin-bottom: var(--spacing-lg);
}

.option-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.option-name {
  font-weight: 600;
  color: var(--text-primary);
}

.option-count {
  color: var(--primary-color);
  font-weight: 600;
}

.option-bar {
  width: 100%;
  height: 24px;
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  overflow: hidden;
  margin-bottom: var(--spacing-sm);
}

.option-progress {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), #0052a3);
  transition: width 0.3s ease;
}

.option-percentage {
  display: block;
  text-align: right;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

@media (max-width: 768px) {
  .admin-interactions-page {
    padding: var(--spacing-md);
  }

  .tabs-container {
    flex-wrap: wrap;
  }

  .tab-button {
    padding: var(--spacing-md);
    font-size: var(--font-size-sm);
  }

  .interactions-table {
    font-size: var(--font-size-sm);
  }

  .interactions-table th,
  .interactions-table td {
    padding: var(--spacing-sm);
  }

  .action-cell {
    flex-direction: column;
  }

  .modal {
    max-width: 95%;
  }

  .modal-large {
    max-width: 95%;
  }
}
</style>