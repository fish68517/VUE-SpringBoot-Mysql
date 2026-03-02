<template>
  <div class="consultation-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('consultation.consultation') || '在线咨询' }}</h2>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="8" animated />

    <!-- 主要内容区域 -->
    <div v-else class="consultation-content">
      <!-- 提交咨询表单 -->
      <div class="submit-consultation-card">
        <h3>{{ $t('consultation.submitQuestion') || '提交问题' }}</h3>
        
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
          @submit.prevent="handleSubmit"
        >
          <!-- 咨询问题 -->
          <el-form-item :label="$t('consultation.question') || '问题'" prop="question">
            <el-input
              v-model="formData.question"
              type="textarea"
              :rows="5"
              :placeholder="$t('consultation.question') || '请输入您的健康问题...'"
              clearable
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <!-- 操作按钮 -->
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="isSubmitting">
              {{ $t('common.submit') || '提交' }}
            </el-button>
            <el-button @click="resetForm">
              {{ $t('common.reset') || '重置' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 错误提示 -->
        <el-alert
          v-if="errorMessage"
          :title="errorMessage"
          type="error"
          :closable="true"
          @close="errorMessage = ''"
          class="error-alert"
        />

        <!-- 成功提示 -->
        <el-alert
          v-if="successMessage"
          :title="successMessage"
          type="success"
          :closable="true"
          @close="successMessage = ''"
          class="success-alert"
        />
      </div>

      <!-- 咨询列表 -->
      <div class="consultation-list-card">
        <h3>{{ $t('consultation.consultationHistory') || '咨询历史' }}</h3>

        <!-- 筛选选项 -->
        <div class="filter-section">
          <el-select
            v-model="filterStatus"
            :placeholder="$t('consultation.status') || '状态'"
            clearable
            @change="handleFilterChange"
            style="width: 200px"
          >
            <el-option
              label="全部"
              value=""
            />
            <el-option
              :label="$t('consultation.pending') || '待回答'"
              value="PENDING"
            />
            <el-option
              :label="$t('consultation.answered') || '已回答'"
              value="ANSWERED"
            />
          </el-select>
        </div>

        <!-- 咨询列表 - 卡片视图 -->
        <div v-if="filteredConsultations.length > 0" class="consultation-cards">
          <div
            v-for="consultation in filteredConsultations"
            :key="consultation.id"
            class="consultation-card"
            @click="selectConsultation(consultation)"
            :class="{ active: selectedConsultation && selectedConsultation.id === consultation.id }"
          >
            <div class="card-header">
              <div class="card-title">
                <span class="question-text">{{ consultation.question }}</span>
              </div>
              <el-tag
                :type="consultation.status === 'ANSWERED' ? 'success' : 'info'"
                class="status-tag"
              >
                {{ consultation.status === 'ANSWERED' ? $t('consultation.answered') || '已回答' : $t('consultation.pending') || '待回答' }}
              </el-tag>
            </div>
            <div class="card-meta">
              <span class="meta-item">
                <i class="el-icon-date"></i>
                {{ formatDate(consultation.createdAt) }}
              </span>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty
          v-else
          :description="$t('healthHistory.noRecords') || '暂无记录'"
          style="margin-top: 40px"
        />
      </div>

      <!-- 咨询详情面板 -->
      <div v-if="selectedConsultation" class="consultation-detail-card">
        <div class="detail-header">
          <h3>{{ $t('consultation.consultationHistory') || '咨询详情' }}</h3>
          <el-button
            type="primary"
            text
            @click="selectedConsultation = null"
          >
            {{ $t('common.back') || '返回' }}
          </el-button>
        </div>

        <!-- 问题部分 -->
        <div class="detail-section">
          <h4>{{ $t('consultation.question') || '问题' }}</h4>
          <div class="detail-content">
            <p>{{ selectedConsultation.question }}</p>
            <div class="detail-meta">
              <span>{{ formatDate(selectedConsultation.createdAt) }}</span>
            </div>
          </div>
        </div>

        <!-- 回答部分 -->
        <div v-if="selectedConsultation.status === 'ANSWERED'" class="detail-section">
          <h4>{{ $t('consultation.answer') || '回答' }}</h4>
          <div class="detail-content answer-content">
            <p>{{ selectedConsultation.answer }}</p>
            <div class="detail-meta">
              <span>{{ formatDate(selectedConsultation.answeredAt) }}</span>
            </div>
          </div>
        </div>

        <!-- 待回答提示 -->
        <div v-else class="detail-section">
          <el-alert
            :title="$t('consultation.pending') || '待回答'"
            type="info"
            :closable="false"
            description="医师正在处理您的问题，请耐心等待..."
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { consultationAPI } from '../../services/api'
import { authService } from '../../services/auth'

export default {
  name: 'Consultation',
  setup() {
    const formRef = ref(null)
    const isLoading = ref(true)
    const isSubmitting = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')
    const consultations = ref([])
    const selectedConsultation = ref(null)
    const filterStatus = ref('')

    const formData = reactive({
      question: ''
    })

    // 表单验证规则
    const formRules = {
      question: [
        { required: true, message: '问题不能为空', trigger: 'blur' },
        { min: 10, message: '问题长度至少为10个字符', trigger: 'blur' },
        { max: 1000, message: '问题长度不能超过1000个字符', trigger: 'blur' }
      ]
    }

    // 过滤后的咨询列表
    const filteredConsultations = computed(() => {
      if (!filterStatus.value) {
        return consultations.value
      }
      return consultations.value.filter(c => c.status === filterStatus.value)
    })

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 获取咨询列表
    const fetchConsultations = async () => {
      try {
        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取用户信息')
        }

        const response = await consultationAPI.getConsultations()
        if (response && response.data) {
          // 按创建时间倒序排列
          consultations.value = response.data.sort((a, b) => {
            return new Date(b.createdAt) - new Date(a.createdAt)
          })
        }
      } catch (error) {
        console.error('获取咨询列表失败:', error)
        ElMessage.error('获取咨询列表失败')
      }
    }

    // 提交咨询
    const handleSubmit = async () => {
      if (!formRef.value) return

      try {
        await formRef.value.validate()

        isSubmitting.value = true
        errorMessage.value = ''
        successMessage.value = ''

        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取用户信息')
        }

        const submitRequest = {
          userId: currentUser.id,
          question: formData.question
        }

        const response = await consultationAPI.submitConsultation(submitRequest)

        if (response && response.data) {
          successMessage.value = response.message || '问题已提交'
          ElMessage.success('问题已提交')
          
          // 重置表单
          resetForm()
          
          // 刷新咨询列表
          await fetchConsultations()
        }
      } catch (error) {
        console.error('提交咨询失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '提交咨询失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '提交咨询失败'
        }
      } finally {
        isSubmitting.value = false
      }
    }

    // 重置表单
    const resetForm = () => {
      formData.question = ''
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }

    // 选择咨询
    const selectConsultation = (consultation) => {
      selectedConsultation.value = consultation
    }

    // 处理筛选变化
    const handleFilterChange = () => {
      selectedConsultation.value = null
    }

    // 页面加载时获取咨询列表
    onMounted(async () => {
      try {
        await fetchConsultations()
      } finally {
        isLoading.value = false
      }
    })

    return {
      formRef,
      isLoading,
      isSubmitting,
      errorMessage,
      successMessage,
      formData,
      formRules,
      consultations,
      selectedConsultation,
      filterStatus,
      filteredConsultations,
      formatDate,
      handleSubmit,
      resetForm,
      selectConsultation,
      handleFilterChange
    }
  }
}
</script>

<style scoped>
.consultation-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.consultation-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.submit-consultation-card,
.consultation-list-card,
.consultation-detail-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.submit-consultation-card h3,
.consultation-list-card h3,
.consultation-detail-card h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.error-alert,
.success-alert {
  margin-top: 20px;
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.consultation-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 600px;
  overflow-y: auto;
}

.consultation-card {
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f9f9f9;
}

.consultation-card:hover {
  border-color: #409eff;
  background: #f0f9ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.consultation-card.active {
  border-color: #409eff;
  background: #e6f7ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 10px;
}

.card-title {
  flex: 1;
}

.question-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: #333;
  font-size: 14px;
  line-height: 1.5;
}

.status-tag {
  flex-shrink: 0;
}

.card-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.consultation-detail-card {
  grid-column: 1 / -1;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.detail-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  border: none;
  padding: 0;
}

.detail-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 6px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
  font-weight: bold;
}

.detail-content {
  color: #606266;
  line-height: 1.6;
  word-break: break-word;
}

.answer-content {
  background: #e6f7ff;
  border-left: 4px solid #409eff;
}

.detail-meta {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .consultation-content {
    grid-template-columns: 1fr;
  }

  .consultation-cards {
    max-height: 400px;
  }
}

@media (max-width: 768px) {
  .consultation-container {
    padding: 10px;
  }

  .submit-consultation-card,
  .consultation-list-card,
  .consultation-detail-card {
    padding: 20px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .filter-section {
    flex-direction: column;
  }

  :deep(.el-select) {
    width: 100%;
  }

  .consultation-cards {
    max-height: 300px;
  }

  .card-header {
    flex-direction: column;
  }

  .status-tag {
    align-self: flex-start;
  }
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  padding: 8px 12px;
}

:deep(.el-textarea__inner) {
  font-family: inherit;
}

:deep(.el-empty) {
  padding: 40px 0;
}
</style>
