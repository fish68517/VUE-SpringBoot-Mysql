<template>
  <div class="feedback-submit-container">
    <div class="feedback-box">
      <h1>提交反馈</h1>
      <p class="subtitle">欢迎提出意见与建议，帮助我们持续改进系统</p>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="type">反馈类型 *</label>
          <select
            id="type"
            v-model="form.type"
            required
            @blur="validateType"
          >
            <option value="">-- 请选择类型 --</option>
            <option value="BUG">问题反馈</option>
            <option value="SUGGESTION">功能建议</option>
            <option value="DATA_ERROR">数据错误</option>
          </select>
          <span v-if="errors.type" class="error">{{ errors.type }}</span>
        </div>

        <div class="form-group">
          <label for="content">反馈内容 *</label>
          <textarea
            id="content"
            v-model="form.content"
            placeholder="请详细描述你的问题/建议（越具体越好）..."
            rows="8"
            required
            @blur="validateContent"
          ></textarea>
          <span v-if="errors.content" class="error">{{ errors.content }}</span>
          <div class="char-count">
            {{ form.content.length }} / 2000 字符
          </div>
        </div>

        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? '正在提交...' : '提交反馈' }}
        </button>

        <div v-if="message.error" class="message error-message">
          {{ message.error }}
        </div>
        <div v-if="message.success" class="message success-message">
          {{ message.success }}
        </div>
      </form>

      <div class="feedback-info">
        <h3>反馈类型说明</h3>
        <ul>
          <li><strong>问题反馈：</strong>提交你在使用过程中遇到的功能问题或报错</li>
          <li><strong>功能建议：</strong>提出对系统功能、交互或体验的改进建议</li>
          <li><strong>数据错误：</strong>反馈院校信息存在错误、缺失或已过时的情况</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import feedbackService from '../services/feedbackService'

export default {
  name: 'FeedbackSubmit',
  data() {
    return {
      form: {
        type: '',
        content: ''
      },
      errors: {
        type: '',
        content: ''
      },
      message: {
        error: '',
        success: ''
      },
      loading: false
    }
  },
  methods: {
    validateType() {
      if (!this.form.type) {
        this.errors.type = '请选择反馈类型'
      } else {
        this.errors.type = ''
      }
    },
    validateContent() {
      if (!this.form.content) {
        this.errors.content = '请填写反馈内容'
      } else if (this.form.content.trim().length < 10) {
        this.errors.content = '反馈内容至少需要 10 个字符'
      } else if (this.form.content.length > 2000) {
        this.errors.content = '反馈内容不能超过 2000 个字符'
      } else {
        this.errors.content = ''
      }
    },
    async handleSubmit() {
      this.validateType()
      this.validateContent()

      if (this.errors.type || this.errors.content) {
        return
      }

      this.loading = true
      this.message.error = ''
      this.message.success = ''

      try {
        await feedbackService.submitFeedback(this.form.type, this.form.content)

        this.message.success = '反馈提交成功！感谢你的反馈与支持。'
        this.form.type = ''
        this.form.content = ''

        setTimeout(() => {
          this.$router.push('/feedback-history')
        }, 2000)
      } catch (error) {
        this.message.error = error.response?.data?.message || '提交反馈失败，请稍后重试。'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>




<style scoped>
.feedback-submit-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.feedback-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 600px;
}

h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

.subtitle {
  color: #666;
  margin-bottom: 30px;
  font-size: 14px;
}

.form-group {
  margin-bottom: 25px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

select,
textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s;
}

select:focus,
textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

textarea {
  resize: vertical;
  min-height: 150px;
}

.char-count {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  text-align: right;
}

.error {
  display: block;
  color: #e74c3c;
  font-size: 12px;
  margin-top: 5px;
}

.btn-submit {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 10px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.message {
  margin-top: 15px;
  padding: 12px;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
}

.error-message {
  background-color: #fadbd8;
  color: #c0392b;
  border: 1px solid #e74c3c;
}

.success-message {
  background-color: #d5f4e6;
  color: #27ae60;
  border: 1px solid #2ecc71;
}

.feedback-info {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.feedback-info h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 16px;
}

.feedback-info ul {
  list-style: none;
  padding: 0;
}

.feedback-info li {
  color: #666;
  font-size: 13px;
  margin-bottom: 10px;
  padding-left: 20px;
  position: relative;
}

.feedback-info li:before {
  content: '•';
  position: absolute;
  left: 0;
  color: #667eea;
  font-weight: bold;
}

.feedback-info strong {
  color: #333;
}
</style>
