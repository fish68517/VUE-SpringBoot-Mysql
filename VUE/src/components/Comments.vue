<template>
  <div class="comments-container">
    <!-- Comments Header -->
    <div class="comments-header">
      <h2>Discussion ({{ totalComments }})</h2>
      <button
        v-if="isAuthenticated"
        class="btn-add-comment"
        @click="toggleCommentForm"
      >
        {{ showCommentForm ? '✕ Cancel' : '+ Add Comment' }}
      </button>
      <div v-else class="login-prompt">
        <router-link to="/login" class="btn-login">Login to comment</router-link>
      </div>
    </div>

    <!-- Comment Form -->
    <div v-if="showCommentForm && isAuthenticated" class="comment-form-wrapper">
      <CommentForm
        :schoolId="schoolId"
        placeholder="Share your thoughts about this school..."
        submitButtonText="Post Comment"
        @submit-success="handleCommentSubmitSuccess"
        @cancel="toggleCommentForm"
      />
    </div>

    <!-- Comments List -->
    <div v-if="comments.length > 0" class="comments-list">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
        :class="{ 'is-reply': comment.parentId }"
      >
        <!-- Comment Header -->
        <div class="comment-header">
          <div class="comment-author">{{ comment.authorUsername || 'Anonymous' }}</div>
          <div class="comment-date">{{ formatDate(comment.createdAt) }}</div>
        </div>

        <!-- Comment Content -->
        <div class="comment-content">{{ comment.content }}</div>

        <!-- Comment Actions -->
        <div class="comment-actions">
          <button
            v-if="isAuthenticated"
            class="btn-reply"
            @click="toggleReplyForm(comment.id)"
          >
            {{ replyingToId === comment.id ? '✕ Cancel' : '↳ Reply' }}
          </button>
          <button
            v-if="canDeleteComment(comment)"
            class="btn-delete"
            @click="deleteComment(comment.id)"
            :disabled="deletingCommentId === comment.id"
          >
            {{ deletingCommentId === comment.id ? 'Deleting...' : '🗑 Delete' }}
          </button>
        </div>

        <!-- Reply Form -->
        <div v-if="replyingToId === comment.id && isAuthenticated" class="reply-form-wrapper">
          <CommentForm
            :parentCommentId="comment.id"
            placeholder="Write your reply..."
            submitButtonText="Post Reply"
            @submit-success="handleReplySubmitSuccess"
            @cancel="toggleReplyForm(null)"
          />
        </div>

        <!-- Nested Replies -->
        <div v-if="getReplies(comment.id).length > 0" class="replies-section">
          <div
            v-for="reply in getReplies(comment.id)"
            :key="reply.id"
            class="reply-item"
          >
            <div class="reply-header">
              <div class="reply-author">{{ reply.authorUsername || 'Anonymous' }}</div>
              <div class="reply-date">{{ formatDate(reply.createdAt) }}</div>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
            <div class="reply-actions">
              <button
                v-if="canDeleteComment(reply)"
                class="btn-delete-small"
                @click="deleteComment(reply.id)"
                :disabled="deletingCommentId === reply.id"
              >
                {{ deletingCommentId === reply.id ? 'Deleting...' : 'Delete' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- No Comments State -->
    <div v-else class="no-comments">
      <p>No comments yet. Be the first to comment!</p>
    </div>

    <!-- Pagination -->
    <div v-if="totalCommentPages > 1" class="comments-pagination">
      <button
        class="btn-pagination"
        :disabled="currentPage === 0"
        @click="previousPage"
      >
        ← Previous
      </button>
      <span class="page-info">
        Page {{ currentPage + 1 }} of {{ totalCommentPages }}
      </span>
      <button
        class="btn-pagination"
        :disabled="currentPage >= totalCommentPages - 1"
        @click="nextPage"
      >
        Next →
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-overlay">
      <p>Loading comments...</p>
    </div>
  </div>
</template>

<script>
import schoolService from '../services/schoolService'
import CommentForm from './CommentForm.vue'

export default {
  name: 'Comments',
  components: {
    CommentForm
  },
  props: {
    schoolId: {
      type: Number,
      required: true
    },
    pageSize: {
      type: Number,
      default: 20
    }
  },
  data() {
    return {
      comments: [],
      currentPage: 0,
      totalCommentPages: 0,
      totalComments: 0,
      loading: false,
      showCommentForm: false,
      replyingToId: null,
      deletingCommentId: null,
      isAuthenticated: false,
      currentUserId: null
    }
  },
  mounted() {
    this.checkAuthentication()
    this.loadComments()
  },
  methods: {
    checkAuthentication() {
      const token = localStorage.getItem('token')
      this.isAuthenticated = !!token
      if (this.isAuthenticated) {
        const userStr = localStorage.getItem('user')
        if (userStr) {
          try {
            const user = JSON.parse(userStr)
            this.currentUserId = user.id
          } catch (e) {
            console.error('Error parsing user data:', e)
          }
        }
      }
    },

    async loadComments() {
      this.loading = true
      try {
        const response = await schoolService.getSchoolComments(
          this.schoolId,
          this.currentPage,
          this.pageSize
        )
        const data = response.data.data

        this.comments = data.content || []
        this.totalCommentPages = data.totalPages || 0
        this.totalComments = data.totalElements || 0
      } catch (error) {
        console.error('Error loading comments:', error)
        this.comments = []
      } finally {
        this.loading = false
      }
    },

    async nextPage() {
      if (this.currentPage < this.totalCommentPages - 1) {
        this.currentPage++
        await this.loadComments()
        this.scrollToComments()
      }
    },

    async previousPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        await this.loadComments()
        this.scrollToComments()
      }
    },

    scrollToComments() {
      const commentsContainer = this.$el.querySelector('.comments-container')
      if (commentsContainer) {
        commentsContainer.scrollIntoView({ behavior: 'smooth' })
      }
    },

    toggleCommentForm() {
      this.showCommentForm = !this.showCommentForm
      if (!this.showCommentForm) {
        this.replyingToId = null
      }
    },

    toggleReplyForm(commentId) {
      if (this.replyingToId === commentId) {
        this.replyingToId = null
      } else {
        this.replyingToId = commentId
      }
    },

    async handleCommentSubmitSuccess() {
      this.showCommentForm = false
      this.currentPage = 0
      await this.loadComments()
    },

    async handleReplySubmitSuccess() {
      this.replyingToId = null
      await this.loadComments()
    },

    async deleteComment(commentId) {
      if (!confirm('Are you sure you want to delete this comment?')) {
        return
      }

      this.deletingCommentId = commentId

      try {
        await schoolService.deleteComment(commentId)
        await this.loadComments()
      } catch (error) {
        console.error('Error deleting comment:', error)
        alert(error.response?.data?.message || 'Failed to delete comment. Please try again.')
      } finally {
        this.deletingCommentId = null
      }
    },

    getReplies(parentCommentId) {
      return this.comments.filter(comment => comment.parentId === parentCommentId)
    },

    canDeleteComment(comment) {
      if (!this.isAuthenticated) {
        return false
      }
      // User can delete their own comments or if they're an admin
      return comment.userId === this.currentUserId
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.comments-container {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #667eea;
}

.comments-header h2 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.btn-add-comment {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-add-comment:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.login-prompt {
  display: flex;
  gap: 10px;
}

.btn-login {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: inline-block;
}

.btn-login:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.comment-form-wrapper {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  border-left: 4px solid #667eea;
}

.reply-form-wrapper {
  background: #f0f2ff;
  padding: 15px;
  border-radius: 4px;
  margin-top: 12px;
  border-left: 3px solid #764ba2;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 20px;
}

.comment-item {
  padding: 15px;
  background: #f8f9fa;
  border-left: 4px solid #667eea;
  border-radius: 4px;
  transition: all 0.3s;
}

.comment-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-item.is-reply {
  margin-left: 30px;
  border-left-color: #764ba2;
  background: #f0f2ff;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;
}

.comment-author {
  font-weight: 600;
  color: #333;
}

.comment-date {
  color: #999;
}

.comment-content {
  color: #666;
  line-height: 1.5;
  font-size: 14px;
  white-space: pre-wrap;
  word-wrap: break-word;
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  gap: 10px;
  font-size: 13px;
}

.btn-reply,
.btn-delete,
.btn-delete-small {
  padding: 6px 12px;
  background: transparent;
  color: #667eea;
  border: 1px solid #667eea;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-reply:hover:not(:disabled),
.btn-delete:hover:not(:disabled),
.btn-delete-small:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.btn-delete,
.btn-delete-small {
  color: #c0392b;
  border-color: #c0392b;
}

.btn-delete:hover:not(:disabled),
.btn-delete-small:hover:not(:disabled) {
  background: #c0392b;
  color: white;
}

.btn-reply:disabled,
.btn-delete:disabled,
.btn-delete-small:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-delete-small {
  padding: 4px 8px;
  font-size: 11px;
}

.replies-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e0e0e0;
}

.reply-item {
  padding: 12px;
  background: #f0f2ff;
  border-left: 3px solid #764ba2;
  border-radius: 4px;
  margin-bottom: 10px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
}

.reply-author {
  font-weight: 600;
  color: #333;
}

.reply-date {
  color: #999;
}

.reply-content {
  color: #666;
  line-height: 1.5;
  font-size: 13px;
  white-space: pre-wrap;
  word-wrap: break-word;
  margin-bottom: 8px;
}

.reply-actions {
  display: flex;
  gap: 8px;
  font-size: 12px;
}

.no-comments {
  padding: 30px;
  text-align: center;
  color: #999;
  background: #f8f9fa;
  border-radius: 4px;
  font-size: 14px;
}

.comments-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.btn-pagination {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-pagination:hover:not(:disabled) {
  background: #764ba2;
  transform: translateY(-2px);
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 13px;
  font-weight: 600;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 16px;
  z-index: 1000;
}

@media (max-width: 768px) {
  .comments-container {
    padding: 15px;
  }

  .comments-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .btn-add-comment {
    width: 100%;
  }

  .comment-item.is-reply {
    margin-left: 15px;
  }

  .comment-actions {
    flex-wrap: wrap;
  }

  .comments-pagination {
    flex-direction: column;
    gap: 10px;
  }

  .btn-pagination {
    width: 100%;
  }
}
</style>
