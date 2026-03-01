<template>
  <div class="health-advice-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('menu.healthAdvice') || '健康建议' }}</h2>
    </div>

    <!-- 创建健康建议表单 -->
    <div class="create-advice-section">
      <div class="section-title">
        <h3>{{ $t('doctor.healthAdvice') || '创建健康建议' }}</h3>
      </div>

      <div class="form-container">
        <!-- 患者选择 -->
        <div class="form-group">
          <label>{{ $t('doctor.selectPatient') || '选择患者' }}:</label>
          <el-select
            v-model="formData.patientId"
            placeholder="请选择患者"
            clearable
            filterable
            class="full-width"
          >
            <el-option
              v-for="patient in patientsList"
              :key="patient.id"
              :label="patient.name || patient.username"
              :value="patient.id"
            />
          </el-select>
        </div>

        <!-- 建议内容 -->
        <div class="form-group">
          <label>{{ $t('doctor.adviceContent') || '建议内容' }}:</label>
          <el-input
            v-model="formData.adviceContent"
            type="textarea"
            :rows="4"
            placeholder="请输入详细的健康建议内容..."
            maxlength="1000"
            show-word-limit
            class="full-width"
          />
        </div>

        <!-- 推荐 -->
        <div class="form-group">
          <label>{{ $t('doctor.recommendation') || '推荐' }}:</label>
          <el-input
            v-model="formData.recommendation"
            type="textarea"
            :rows="3"
            placeholder="请输入具体的推荐措施..."
            maxlength="500"
            show-word-limit
            class="full-width"
          />
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button
            type="primary"
            @click="submitAdvice"
            :loading="isSubmitting"
            class="submit-btn"
          >
            {{ $t('common.submit') || '提交建议' }}
          </el-button>
          <el-button @click="resetForm" class="reset-btn">
            {{ $t('common.reset') || '重置' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 已发送的健康建议列表 -->
    <div class="advice-list-section">
      <div class="section-title">
        <h3>{{ $t('doctor.healthAdvice') || '已发送的健康建议' }}</h3>
      </div>

      <!-- 筛选选项 -->
      <div class="filter-section">
        <div class="filter-group">
          <label>{{ $t('doctor.selectPatient') || '筛选患者' }}:</label>
          <el-select
            v-model="filterPatientId"
            placeholder="请选择患者"
            clearable
            filterable
            @change="loadAdviceList"
            class="filter-select"
          >
            <el-option label="全部患者" value="" />
            <el-option
              v-for="patient in patientsList"
              :key="patient.id"
              :label="patient.name || patient.username"
              :value="patient.id"
            />
          </el-select>
        </div>

        <el-button type="primary" @click="loadAdviceList" class="search-btn">
          {{ $t('common.search') || '搜索' }}
        </el-button>
      </div>

      <!-- 加载状态 -->
      <el-skeleton v-if="isLoadingAdvice" :rows="5" animated />

      <!-- 建议列表 -->
      <div v-else class="advice-list">
        <!-- 空状态 -->
        <el-empty
          v-if="adviceList.length === 0"
          :description="$t('common.noData') || '暂无健康建议'"
          class="empty-state"
        />

        <!-- 建议卡片列表 -->
        <div v-else class="advice-cards">
          <div
            v-for="(advice, index) in paginatedAdviceList"
            :key="advice.id"
            class="advice-card"
          >
            <!-- 建议头部 -->
            <div class="advice-header">
              <div class="advice-info">
                <span class="index">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
                <span class="patient-name">
                  {{ advice.patientName || advice.patientUsername || '患者' }}
                </span>
              </div>
              <div class="advice-date">
                {{ formatDateTime(advice.createdAt) }}
              </div>
            </div>

            <!-- 建议内容 -->
            <div class="advice-content">
              <div class="content-section">
                <strong>{{ $t('doctor.adviceContent') || '建议内容' }}:</strong>
                <p class="content-text">{{ advice.adviceContent }}</p>
              </div>

              <div v-if="advice.recommendation" class="content-section">
                <strong>{{ $t('doctor.recommendation') || '推荐' }}:</strong>
                <p class="content-text">{{ advice.recommendation }}</p>
              </div>
            </div>

            <!-- 建议元数据 -->
            <div class="advice-meta">
              <span class="meta-item">
                <strong>患者ID:</strong> {{ advice.userId }}
              </span>
              <span class="meta-item">
                <strong>医师ID:</strong> {{ advice.doctorId }}
              </span>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="adviceList.length > pageSize" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20]"
            :total="adviceList.length"
            layout="total, sizes, prev, pager, next"
          />
        </div>
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
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { doctorAPI, healthAdviceAPI } from '../../services/api'
import { authService } from '../../services/auth'

export default {
  name: 'HealthAdvice',
  setup() {
    const isSubmitting = ref(false)
    const isLoadingAdvice = ref(true)
    const errorMessage = ref('')
    const successMessage = ref('')
    const currentUser = authService.getUser()

    // 表单数据
    const formData = reactive({
      patientId: null,
      adviceContent: '',
      recommendation: ''
    })

    // 患者列表
    const patientsList = ref([])

    // 建议列表
    const adviceList = ref([])
    const filterPatientId = ref('')

    // 分页
    const pageSize = ref(10)
    const currentPage = ref(1)

    // 计算分页后的建议列表
    const paginatedAdviceList = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return adviceList.value.slice(start, end)
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

    // 加载建议列表
    const loadAdviceList = async () => {
      try {
        isLoadingAdvice.value = true
        errorMessage.value = ''

        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取医师信息')
        }

        // 获取医师发送的所有建议
        // 由于API没有直接的"获取医师发送的建议"端点，我们需要获取所有患者的建议
        let allAdvice = []

        // 如果选择了特定患者，只获取该患者的建议
        if (filterPatientId.value) {
          const response = await healthAdviceAPI.getPatientAdvice(filterPatientId.value)
          if (response && response.data) {
            allAdvice = response.data
          }
        } else {
          // 获取所有患者的建议
          for (const patient of patientsList.value) {
            try {
              const response = await healthAdviceAPI.getPatientAdvice(patient.id)
              if (response && response.data) {
                allAdvice = allAdvice.concat(response.data)
              }
            } catch (error) {
              console.error(`获取患者 ${patient.id} 的建议失败:`, error)
            }
          }
        }

        // 过滤出当前医师发送的建议
        adviceList.value = allAdvice.filter(advice => advice.doctorId === currentUser.id)

        // 按创建时间排序（最新的在前）
        adviceList.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

        currentPage.value = 1
      } catch (error) {
        console.error('加载建议列表失败:', error)
        errorMessage.value = '加载建议列表失败'
        adviceList.value = []
      } finally {
        isLoadingAdvice.value = false
      }
    }

    // 提交建议
    const submitAdvice = async () => {
      try {
        // 验证表单
        if (!formData.patientId) {
          ElMessage.warning('请选择患者')
          return
        }

        if (!formData.adviceContent || formData.adviceContent.trim() === '') {
          ElMessage.warning('请输入建议内容')
          return
        }

        isSubmitting.value = true
        errorMessage.value = ''
        successMessage.value = ''

        // 调用API创建建议
        const response = await healthAdviceAPI.createAdvice(
          {
            patientId: formData.patientId,
            adviceContent: formData.adviceContent,
            recommendation: formData.recommendation
          },
          currentUser.id
        )

        if (response && response.code === 200) {
          successMessage.value = '健康建议已成功发送'
          ElMessage.success('健康建议已成功发送')

          // 重置表单
          resetForm()

          // 重新加载建议列表
          await loadAdviceList()
        } else {
          throw new Error(response?.message || '提交建议失败')
        }
      } catch (error) {
        console.error('提交建议失败:', error)
        const errorMsg = error.response?.data?.message || error.message || '提交建议失败'
        errorMessage.value = errorMsg
        ElMessage.error(errorMsg)
      } finally {
        isSubmitting.value = false
      }
    }

    // 重置表单
    const resetForm = () => {
      formData.patientId = null
      formData.adviceContent = ''
      formData.recommendation = ''
    }

    // 页面加载时初始化
    onMounted(async () => {
      try {
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取医师信息')
        }

        // 加载患者列表
        await loadPatientsList()

        // 加载建议列表
        await loadAdviceList()
      } catch (error) {
        console.error('初始化失败:', error)
        errorMessage.value = error.message || '初始化失败'
      }
    })

    return {
      isSubmitting,
      isLoadingAdvice,
      errorMessage,
      successMessage,
      formData,
      patientsList,
      adviceList,
      filterPatientId,
      pageSize,
      currentPage,
      paginatedAdviceList,
      formatDateTime,
      loadAdviceList,
      submitAdvice,
      resetForm
    }
  }
}
</script>

<style scoped>
.health-advice-container {
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

.section-title {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7eb;
}

.section-title h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
}

/* 创建建议表单 */
.create-advice-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.full-width {
  width: 100%;
}

.form-group :deep(.el-select),
.form-group :deep(.el-input) {
  width: 100%;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-start;
  margin-top: 10px;
}

.submit-btn {
  min-width: 120px;
}

.reset-btn {
  min-width: 100px;
}

/* 建议列表 */
.advice-list-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.filter-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  align-items: flex-end;
  flex-wrap: wrap;
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

.filter-select {
  width: 100%;
}

.search-btn {
  white-space: nowrap;
}

.advice-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
}

.advice-cards {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.advice-card {
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e4e7eb;
  overflow: hidden;
  transition: all 0.3s ease;
}

.advice-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.advice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7eb;
}

.advice-info {
  display: flex;
  align-items: center;
  gap: 15px;
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

.patient-name {
  font-size: 14px;
  font-weight: 500;
}

.advice-date {
  font-size: 12px;
  opacity: 0.9;
}

.advice-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.content-section strong {
  color: #333;
  font-size: 13px;
}

.content-text {
  margin: 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
  padding: 10px;
  background: white;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.advice-meta {
  display: flex;
  gap: 20px;
  padding: 10px 20px;
  background: white;
  border-top: 1px solid #e4e7eb;
  font-size: 12px;
  color: #909399;
}

.meta-item {
  display: flex;
  gap: 5px;
}

.meta-item strong {
  color: #606266;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  margin-top: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.error-alert,
.success-alert {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .health-advice-container {
    padding: 10px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .section-title h3 {
    font-size: 16px;
  }

  .create-advice-section,
  .advice-list-section {
    padding: 15px;
  }

  .form-actions {
    flex-direction: column;
  }

  .submit-btn,
  .reset-btn {
    width: 100%;
  }

  .filter-section {
    flex-direction: column;
    gap: 10px;
  }

  .filter-group {
    min-width: 100%;
  }

  .search-btn {
    width: 100%;
  }

  .advice-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .advice-date {
    align-self: flex-start;
  }

  .advice-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
