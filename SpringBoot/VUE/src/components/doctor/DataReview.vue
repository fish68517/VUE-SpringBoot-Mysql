<template>
  <div class="data-review-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('menu.dataReview') || '数据审核与反馈' }}</h2>
    </div>

    <!-- 患者选择和筛选 -->
    <div class="filter-section">
      <div class="filter-group">
        <label>{{ $t('doctor.selectPatient') || '选择患者' }}:</label>
        <el-select
          v-model="selectedPatientId"
          placeholder="请选择患者"
          @change="onPatientChange"
          clearable
          filterable
        >
          <el-option
            v-for="patient in patientsList"
            :key="patient.id"
            :label="patient.name || patient.username"
            :value="patient.id"
          />
        </el-select>
      </div>

      <div class="filter-group">
        <label>{{ $t('healthData.dataType') || '数据类型' }}:</label>
        <el-select
          v-model="selectedDataType"
          placeholder="请选择数据类型"
          @change="loadHealthData"
          clearable
        >
          <el-option label="全部" value="" />
          <el-option label="常规数据" value="ROUTINE" />
          <el-option label="性别相关" value="GENDER_SPECIFIC" />
        </el-select>
      </div>

      <div class="filter-group">
        <label>{{ $t('common.dateRange') || '日期范围' }}:</label>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="loadHealthData"
        />
      </div>

      <el-button type="primary" @click="loadHealthData">
        {{ $t('common.search') || '搜索' }}
      </el-button>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="10" animated />

    <!-- 健康数据审核列表 -->
    <div v-else class="review-content">
      <div v-if="healthDataList.length > 0" class="data-list">
        <div
          v-for="(data, index) in paginatedHealthData"
          :key="data.id"
          class="data-item"
        >
          <div class="data-header">
            <span class="index">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
            <span class="record-time">{{ formatDateTime(data.recordedAt) }}</span>
            <el-tag
              :type="getReviewStatusTag(data.reviewStatus)"
              class="status-tag"
            >
              {{ formatReviewStatus(data.reviewStatus) }}
            </el-tag>
          </div>

          <div class="data-content">
            <div class="data-grid">
              <div class="data-field">
                <span class="field-label">身高:</span>
                <span class="field-value">{{ data.height }} cm</span>
              </div>
              <div class="data-field">
                <span class="field-label">体重:</span>
                <span class="field-value">{{ data.weight }} kg</span>
              </div>
              <div class="data-field">
                <span class="field-label">血压:</span>
                <span class="field-value">{{ data.bloodPressure }}</span>
              </div>
              <div class="data-field">
                <span class="field-label">心率:</span>
                <span class="field-value">{{ data.heartRate }} bpm</span>
              </div>
            </div>

            <div v-if="data.dietRecord || data.exerciseRecord" class="data-details">
              <div v-if="data.dietRecord" class="detail-item">
                <strong>饮食记录:</strong>
                <p>{{ data.dietRecord }}</p>
              </div>
              <div v-if="data.exerciseRecord" class="detail-item">
                <strong>运动记录:</strong>
                <p>{{ data.exerciseRecord }}</p>
              </div>
            </div>
          </div>

          <!-- 审核反馈表单 -->
          <div class="review-form">
            <div class="form-group">
              <label>{{ $t('doctor.reviewOpinion') || '审核意见' }}:</label>
              <el-input
                v-model="reviewFeedback[data.id]"
                type="textarea"
                :rows="3"
                placeholder="请输入审核意见和反馈..."
                maxlength="500"
                show-word-limit
              />
            </div>

            <div class="form-actions">
              <el-button
                type="success"
                @click="submitReview(data.id)"
                :loading="submittingIds.includes(data.id)"
              >
                {{ $t('common.submit') || '提交反馈' }}
              </el-button>
              <el-button
                @click="clearReview(data.id)"
              >
                {{ $t('common.clear') || '清空' }}
              </el-button>
            </div>
          </div>

          <!-- 已有反馈显示 -->
          <div v-if="data.reviewFeedback" class="existing-feedback">
            <div class="feedback-header">
              <strong>{{ $t('doctor.existingFeedback') || '已有反馈' }}:</strong>
              <span class="feedback-date">{{ formatDateTime(data.feedbackDate) }}</span>
            </div>
            <div class="feedback-content">
              {{ data.reviewFeedback }}
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="healthDataList.length > pageSize" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20]"
            :total="healthDataList.length"
            layout="total, sizes, prev, pager, next"
          />
        </div>
      </div>

      <div v-else class="no-data">
        <el-empty
          :description="selectedPatientId ? '暂无健康数据' : '请先选择患者'"
        />
      </div>
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      @close="errorMessage = ''"
      class="error-alert"
    />
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { doctorAPI, healthDataAPI, healthAdviceAPI } from '../../services/api'
import { authService } from '../../services/auth'

export default {
  name: 'DataReview',
  setup() {
    const isLoading = ref(true)
    const errorMessage = ref('')
    const currentUser = authService.getUser()

    // 患者和数据选择
    const selectedPatientId = ref(null)
    const selectedDataType = ref('')
    const dateRange = ref([])
    const patientsList = ref([])
    const healthDataList = ref([])

    // 分页
    const pageSize = ref(10)
    const currentPage = ref(1)

    // 审核反馈
    const reviewFeedback = reactive({})
    const submittingIds = ref([])

    // 计算分页后的健康数据
    const paginatedHealthData = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return healthDataList.value.slice(start, end)
    })

    // 格式化日期时间
    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      try {
        const date = new Date(dateTime)
        return date.toLocaleString('zh-CN')
      } catch (e) {
        return dateTime
      }
    }

    // 获取审核状态标签颜色
    const getReviewStatusTag = (status) => {
      const statusMap = {
        'PENDING': 'warning',
        'REVIEWED': 'success'
      }
      return statusMap[status] || 'info'
    }

    // 格式化审核状态
    const formatReviewStatus = (status) => {
      const statusMap = {
        'PENDING': '待审核',
        'REVIEWED': '已审核'
      }
      return statusMap[status] || status
    }

    // 加载患者列表
    const loadPatientsList = async () => {
      try {
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取医师信息')
        }

        const response = await doctorAPI.getPatients(currentUser.id)
        if (response && response.data) {
          patientsList.value = response.data
        }
      } catch (error) {
        console.error('加载患者列表失败:', error)
        errorMessage.value = '加载患者列表失败'
      }
    }

    // 加载健康数据
    const loadHealthData = async () => {
      try {
        if (!selectedPatientId.value) {
          healthDataList.value = []
          return
        }

        isLoading.value = true
        errorMessage.value = ''

        // 获取患者的健康数据
        const response = await healthDataAPI.getUserData()
        if (response && response.data) {
          let data = response.data

          // 按患者ID筛选
          data = data.filter(item => item.userId === selectedPatientId.value)

          // 按数据类型筛选
          if (selectedDataType.value) {
            data = data.filter(item => item.dataType === selectedDataType.value)
          }

          // 按日期范围筛选
          if (dateRange.value && dateRange.value.length === 2) {
            const startDate = new Date(dateRange.value[0])
            const endDate = new Date(dateRange.value[1])
            endDate.setHours(23, 59, 59, 999)

            data = data.filter(item => {
              const itemDate = new Date(item.recordedAt)
              return itemDate >= startDate && itemDate <= endDate
            })
          }

          // 按记录时间排序（最新的在前）
          data.sort((a, b) => new Date(b.recordedAt) - new Date(a.recordedAt))

          healthDataList.value = data
          currentPage.value = 1
        }
      } catch (error) {
        console.error('加载健康数据失败:', error)
        errorMessage.value = '加载健康数据失败'
      } finally {
        isLoading.value = false
      }
    }

    // 患者变更时加载数据
    const onPatientChange = () => {
      currentPage.value = 1
      loadHealthData()
    }

    // 提交审核反馈
    const submitReview = async (dataId) => {
      try {
        const feedback = reviewFeedback[dataId]
        if (!feedback || feedback.trim() === '') {
          ElMessage.warning('请输入审核意见')
          return
        }

        submittingIds.value.push(dataId)

        // 创建健康建议作为反馈
        const response = await healthAdviceAPI.createAdvice({
          patientId: selectedPatientId.value,
          adviceContent: feedback,
          recommendation: '根据数据审核提供的建议'
        }, currentUser.id)

        if (response && response.code === 200) {
          ElMessage.success('反馈提交成功')
          reviewFeedback[dataId] = ''

          // 更新数据项的审核状态
          const dataItem = healthDataList.value.find(item => item.id === dataId)
          if (dataItem) {
            dataItem.reviewStatus = 'REVIEWED'
            dataItem.reviewFeedback = feedback
            dataItem.feedbackDate = new Date().toISOString()
          }
        } else {
          throw new Error(response?.message || '提交反馈失败')
        }
      } catch (error) {
        console.error('提交反馈失败:', error)
        const errorMsg = error.response?.data?.message || error.message || '提交反馈失败'
        ElMessage.error(errorMsg)
      } finally {
        submittingIds.value = submittingIds.value.filter(id => id !== dataId)
      }
    }

    // 清空反馈
    const clearReview = (dataId) => {
      reviewFeedback[dataId] = ''
    }

    // 页面加载时获取患者列表
    onMounted(async () => {
      try {
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取医师信息')
        }
        await loadPatientsList()
      } catch (error) {
        console.error('初始化失败:', error)
        errorMessage.value = error.message || '初始化失败'
      } finally {
        isLoading.value = false
      }
    })

    return {
      isLoading,
      errorMessage,
      selectedPatientId,
      selectedDataType,
      dateRange,
      patientsList,
      healthDataList,
      pageSize,
      currentPage,
      paginatedHealthData,
      reviewFeedback,
      submittingIds,
      formatDateTime,
      getReviewStatusTag,
      formatReviewStatus,
      loadHealthData,
      onPatientChange,
      submitReview,
      clearReview
    }
  }
}
</script>

<style scoped>
.data-review-container {
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

.filter-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.filter-group :deep(.el-select),
.filter-group :deep(.el-date-picker) {
  width: 100%;
}

.review-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.data-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.data-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.data-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.data-header {
  display: flex;
  align-items: center;
  gap: 15px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7eb;
}

.index {
  display: inline-block;
  background: rgba(255, 255, 255, 0.3);
  width: 28px;
  height: 28px;
  border-radius: 50%;
  text-align: center;
  line-height: 28px;
  font-size: 12px;
  font-weight: bold;
}

.record-time {
  font-size: 14px;
  flex: 1;
}

.status-tag {
  margin-left: auto;
}

.data-content {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 15px;
}

.data-field {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.field-label {
  color: #606266;
  font-size: 12px;
  font-weight: 500;
}

.field-value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.data-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-item strong {
  color: #333;
  font-size: 13px;
}

.detail-item p {
  margin: 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.review-form {
  padding: 20px;
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.form-group :deep(.el-textarea) {
  width: 100%;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.form-actions :deep(.el-button) {
  flex: 1;
}

.existing-feedback {
  padding: 20px;
  background: #f0f9ff;
  border-top: 1px solid #ebeef5;
}

.feedback-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #b3d8ff;
}

.feedback-header strong {
  color: #333;
  font-size: 13px;
}

.feedback-date {
  color: #909399;
  font-size: 12px;
  margin-left: auto;
}

.feedback-content {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.no-data {
  background: white;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.error-alert {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .data-review-container {
    padding: 10px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    min-width: 100%;
  }

  .data-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .data-header {
    flex-wrap: wrap;
  }

  .status-tag {
    margin-left: 0;
    margin-top: 5px;
    width: 100%;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
