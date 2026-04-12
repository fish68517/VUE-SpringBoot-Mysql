<template>
  <div class="comment-section">
    <!-- 评论表单 -->
    <div class="comment-form">
      <h3 class="section-title">评论区</h3>
      <div v-if="isAuthenticated" class="comment-input-area">
        <div class="user-avatar">
          {{ currentUserInitial }}
        </div>
        <div class="input-wrapper">
          <el-input
            v-model="newCommentContent"
            type="textarea"
            :rows="3"
            placeholder="写下你的看法吧..."
            maxlength="500"
            show-word-limit
            @keydown.ctrl.enter="submitComment"
            @keydown.meta.enter="submitComment"
          />
          <div class="button-group">
            <el-button @click="resetComment">清空</el-button>
            <el-button
              type="primary"
              :loading="isSubmitting"
              @click="submitComment"
            >
              发布评论
            </el-button>
          </div>
        </div>
      </div>
      <div v-else class="login-prompt">
        <p>请先 <router-link to="/login">登录</router-link> 后再发表评论</p>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comments-list">
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>
      <div v-else-if="comments.length === 0" class="empty-state">
        <p>还没有评论，来抢个沙发吧！</p>
      </div>
      <div v-else>
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="comment-item"
        >
          <div class="comment-header">
            <div class="user-info">
              <div class="avatar">
                {{ comment.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
              </div>
              <div class="user-details">
                <p class="username">{{ comment.user?.username || '未知用户' }}</p>
                <p class="timestamp">{{ formatDate(comment.createdAt) }}</p>
              </div>
            </div>
            <el-button
              v-if="isCommentOwner(comment)"
              type="danger"
              text
              size="small"
              :loading="deletingCommentId === comment.id"
              @click="deleteComment(comment.id)"
            >
              删除
            </el-button>
          </div>
          <div class="comment-content">
            {{ comment.content }}
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="totalComments > pageSize" class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="totalComments"
            layout="prev, pager, next"
            @current-change="loadComments"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { commentService } from '../services/commentService'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const props = defineProps({
  travelRecordId: {
    type: Number,
    required: true
  }
})

const comments = ref([])
const newCommentContent = ref('')
const loading = ref(false)
const isSubmitting = ref(false)
const deletingCommentId = ref(null)
const currentPage = ref(1)
const pageSize = 10
const totalComments = ref(0)

const isAuthenticated = computed(() => userStore.isAuthenticated)
const currentUser = computed(() => userStore.user)
const currentUserInitial = computed(() => {
  return currentUser.value?.username?.charAt(0)?.toUpperCase() || 'U'
})

const isCommentOwner = (comment) => {
  return isAuthenticated.value && currentUser.value?.id === comment.userId
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return '刚刚'
  if (diffMins < 60) return `${diffMins} 分钟前`
  if (diffHours < 24) return `${diffHours} 小时前`
  if (diffDays < 7) return `${diffDays} 天前`

  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const loadComments = async () => {
  loading.value = true
  try {
    const response = await commentService.getCommentsByTravelRecord(
      props.travelRecordId,
      currentPage.value - 1,
      pageSize
    )
    comments.value = response.data.content || []
    totalComments.value = response.data.totalElements || 0
  } catch (error) {
    console.error('Failed to load comments:', error)
    ElMessage.error('加载评论失败')
  } finally {
    loading.value = false
  }
}

const submitComment = async () => {
  if (!newCommentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  isSubmitting.value = true
  try {
    await commentService.createComment({
      travelRecordId: props.travelRecordId,
      content: newCommentContent.value.trim()
    })
    ElMessage.success('评论发布成功')
    newCommentContent.value = ''
    currentPage.value = 1
    await loadComments()
  } catch (error) {
    console.error('Failed to post comment:', error)
    if (error.response?.status === 401) {
      ElMessage.error('请先登录后再发表评论')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '发表评论失败')
    }
  } finally {
    isSubmitting.value = false
  }
}

const resetComment = () => {
  newCommentContent.value = ''
}

const deleteComment = async (commentId) => {
  try {
    deletingCommentId.value = commentId
    await commentService.deleteComment(commentId)
    ElMessage.success('评论已删除')
    await loadComments()
  } catch (error) {
    console.error('Failed to delete comment:', error)
    ElMessage.error(error.response?.data?.message || '删除评论失败')
  } finally {
    deletingCommentId.value = null
  }
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-top: 20px;
}

.section-title {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.comment-form {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-input-area {
  display: flex;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}

.input-wrapper {
  flex: 1;
}

.button-group {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  justify-content: flex-end;
}

.login-prompt {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  color: #666;
}

.login-prompt a {
  color: #409eff;
  text-decoration: none;
}

.login-prompt a:hover {
  text-decoration: underline;
}

.comments-list {
  margin-top: 20px;
}

.loading-state {
  padding: 20px 0;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  background: #fafafa;
  border-radius: 4px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.username {
  margin: 0;
  color: #333;
  font-weight: 600;
  font-size: 14px;
}

.timestamp {
  margin: 4px 0 0 0;
  color: #999;
  font-size: 12px;
}

.comment-content {
  margin-left: 44px;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .comment-section {
    padding: 16px;
  }

  .comment-input-area {
    flex-direction: column;
  }

  .user-avatar {
    width: 36px;
    height: 36px;
  }

  .button-group {
    justify-content: flex-start;
  }

  .comment-content {
    margin-left: 0;
  }
}
</style>
