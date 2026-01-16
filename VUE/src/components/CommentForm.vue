<template>
  <div class="comment-form-wrapper">
    <div class="comment-form-section">
      <textarea
        v-model="content"
        class="comment-textarea"
        :placeholder="placeholder"
        rows="4"
        @keydown.ctrl.enter="submitForm"
        @keydown.meta.enter="submitForm"
      ></textarea>
      <div class="form-actions">
        <button
          class="btn-submit"
          @click="submitForm"
          :disabled="!content.trim() || submitting"
        >
          {{ submitting ? 'Posting...' : submitButtonText }}
        </button>
        <button
          class="btn-cancel"
          @click="cancelForm"
          :disabled="submitting"
        >
          Cancel
        </button>
      </div>
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    </div>
  </div>
</template>

<script>
import schoolService from '../services/schoolService'

export default {
  name: 'CommentForm',
  props: {
    schoolId: {
      type: Number,
      required: false,
      default: null
    },
    parentCommentId: {
      type: Number,
      required: false,
      default: null
    },
    placeholder: {
      type: String,
      default: 'Share your thoughts about this school...'
    },
    submitButtonText: {
      type: String,
      default: 'Post Comment'
    }
  },
  data() {
    return {
      content: '',
      submitting: false,
      errorMessage: '',
      successMessage: ''
    }
  },
  methods: {
    async submitForm() {
      if (!this.content.trim()) {
        this.errorMessage = 'Comment cannot be empty'
        return
      }

      this.submitting = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        if (this.parentCommentId) {
          // Submit as a reply
          await schoolService.createReply(this.parentCommentId, this.content)
          this.successMessage = 'Reply posted successfully!'
        } else if (this.schoolId) {
          // Submit as a new comment
          await schoolService.createComment(this.schoolId, this.content)
          this.successMessage = 'Comment posted successfully!'
        } else {
          throw new Error('Either schoolId or parentCommentId must be provided')
        }

        // Clear form and emit success event
        this.content = ''
        this.$emit('submit-success')

        // Clear success message after 2 seconds
        setTimeout(() => {
          this.successMessage = ''
        }, 2000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Failed to post. Please try again.'
        console.error('Error submitting comment:', error)
      } finally {
        this.submitting = false
      }
    },

    cancelForm() {
      this.content = ''
      this.errorMessage = ''
      this.successMessage = ''
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.comment-form-wrapper {
  width: 100%;
}

.comment-form-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.comment-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.comment-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

.btn-submit {
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

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-cancel {
  padding: 10px 20px;
  background: #e0e0e0;
  color: #333;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover:not(:disabled) {
  background: #d0d0d0;
}

.btn-cancel:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #c0392b;
  font-size: 13px;
  margin-top: 10px;
  padding: 10px;
  background: #fadbd8;
  border-radius: 4px;
}

.success-message {
  color: #155724;
  font-size: 13px;
  margin-top: 10px;
  padding: 10px;
  background: #d4edda;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .comment-form-section {
    padding: 15px;
  }

  .form-actions {
    flex-wrap: wrap;
  }

  .btn-submit,
  .btn-cancel {
    flex: 1;
    min-width: 100px;
  }
}
</style>
