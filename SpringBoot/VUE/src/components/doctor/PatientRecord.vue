<template>
  <div class="patient-record-container">
    <!-- 页面标题和返回按钮 -->
    <div class="page-header">
      <div class="header-top">
        <el-button type="primary" text @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('common.back') || '返回' }}
        </el-button>
        <h2>{{ $t('menu.patientRecord') || '患者档案' }}</h2>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="10" animated />

    <!-- 患者档案内容 -->
    <div v-else class="record-content">
      <!-- 个人信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h3>{{ $t('user.personalInfo') || '个人信息' }}</h3>
        </div>
        <div class="card-body">
          <div v-if="patientRecord.personalInfo" class="info-grid">
            <div class="info-item">
              <span class="label">{{ $t('auth.username') || '用户名' }}:</span>
              <span class="value">{{ patientRecord.personalInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ $t('user.name') || '姓名' }}:</span>
              <span class="value">{{ patientRecord.personalInfo.name || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ $t('user.age') || '年龄' }}:</span>
              <span class="value">{{ patientRecord.personalInfo.age ? patientRecord.personalInfo.age + '岁' : '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ $t('user.gender') || '性别' }}:</span>
              <span class="value">{{ formatGender(patientRecord.personalInfo.gender) }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ $t('user.phone') || '电话' }}:</span>
              <span class="value">{{ patientRecord.personalInfo.phone || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ $t('auth.email') || '邮箱' }}:</span>
              <span class="value">{{ patientRecord.personalInfo.email || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ $t('common.createdAt') || '注册时间' }}:</span>
              <span class="value">{{ formatDateTime(patientRecord.personalInfo.createdAt) }}</span>
            </div>
          </div>
          <div v-else class="no-data">
            <el-empty :description="$t('common.noData') || '暂无数据'" />
          </div>
        </div>
      </div>

      <!-- 健康数据卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h3>{{ $t('healthData.healthData') || '健康数据' }}</h3>
          <span class="count-badge">{{ patientRecord.healthData ? patientRecord.healthData.length : 0 }}</span>
        </div>
        <div class="card-body">
          <div v-if="patientRecord.healthData && patientRecord.healthData.length > 0" class="data-table">
            <el-table
              :data="paginatedHealthData"
              stripe
              style="width: 100%"
              max-height="400"
            >
              <el-table-column
                prop="recordedAt"
                label="记录时间"
                width="180"
                sortable
              />
              <el-table-column
                prop="height"
                label="身高(cm)"
                width="100"
              />
              <el-table-column
                prop="weight"
                label="体重(kg)"
                width="100"
              />
              <el-table-column
                prop="bloodPressure"
                label="血压"
                width="120"
              />
              <el-table-column
                prop="heartRate"
                label="心率(bpm)"
                width="100"
              />
              <el-table-column
                label="操作"
                width="100"
                fixed="right"
              >
                <template #default="{ row }">
                  <el-button link type="primary" @click="showHealthDataDetail(row)">
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 健康数据分页 -->
            <div v-if="patientRecord.healthData.length > pageSize" class="pagination-container">
              <el-pagination
                v-model:current-page="healthDataPage"
                v-model:page-size="pageSize"
                :page-sizes="[5, 10, 20]"
                :total="patientRecord.healthData.length"
                layout="total, sizes, prev, pager, next"
              />
            </div>
          </div>
          <div v-else class="no-data">
            <el-empty :description="$t('common.noData') || '暂无健康数据'" />
          </div>
        </div>
      </div>

      <!-- 咨询记录卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h3>{{ $t('consultation.consultations') || '咨询记录' }}</h3>
          <span class="count-badge">{{ patientRecord.consultations ? patientRecord.consultations.length : 0 }}</span>
        </div>
        <div class="card-body">
          <div v-if="patientRecord.consultations && patientRecord.consultations.length > 0" class="consultation-list">
            <div
              v-for="(consultation, index) in paginatedConsultations"
              :key="consultation.id"
              class="consultation-item"
            >
              <div class="consultation-header">
                <span class="index">{{ (consultationPage - 1) * pageSize + index + 1 }}</span>
                <el-tag :type="getConsultationStatusTag(consultation.status)">
                  {{ formatConsultationStatus(consultation.status) }}
                </el-tag>
                <span class="date">{{ formatDateTime(consultation.createdAt) }}</span>
              </div>
              <div class="consultation-content">
                <div class="question">
                  <strong>问题:</strong>
                  <p>{{ consultation.question }}</p>
                </div>
                <div v-if="consultation.answer" class="answer">
                  <strong>回答:</strong>
                  <p>{{ consultation.answer }}</p>
                </div>
                <div v-else class="no-answer">
                  <em>暂无回答</em>
                </div>
              </div>
            </div>

            <!-- 咨询记录分页 -->
            <div v-if="patientRecord.consultations.length > pageSize" class="pagination-container">
              <el-pagination
                v-model:current-page="consultationPage"
                v-model:page-size="pageSize"
                :page-sizes="[5, 10, 20]"
                :total="patientRecord.consultations.length"
                layout="total, sizes, prev, pager, next"
              />
            </div>
          </div>
          <div v-else class="no-data">
            <el-empty :description="$t('common.noData') || '暂无咨询记录'" />
          </div>
        </div>
      </div>

      <!-- 健康建议卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h3>{{ $t('healthAdvice.healthAdvice') || '健康建议' }}</h3>
          <span class="count-badge">{{ patientRecord.healthAdvice ? patientRecord.healthAdvice.length : 0 }}</span>
        </div>
        <div class="card-body">
          <div v-if="patientRecord.healthAdvice && patientRecord.healthAdvice.length > 0" class="advice-list">
            <div
              v-for="(advice, index) in paginatedHealthAdvice"
              :key="advice.id"
              class="advice-item"
            >
              <div class="advice-header">
                <span class="index">{{ (advicePage - 1) * pageSize + index + 1 }}</span>
                <span class="date">{{ formatDateTime(advice.createdAt) }}</span>
              </div>
              <div class="advice-content">
                <div class="advice-text">
                  <strong>建议:</strong>
                  <p>{{ advice.adviceContent }}</p>
                </div>
                <div v-if="advice.recommendation" class="recommendation">
                  <strong>推荐:</strong>
                  <p>{{ advice.recommendation }}</p>
                </div>
              </div>
            </div>

            <!-- 健康建议分页 -->
            <div v-if="patientRecord.healthAdvice.length > pageSize" class="pagination-container">
              <el-pagination
                v-model:current-page="advicePage"
                v-model:page-size="pageSize"
                :page-sizes="[5, 10, 20]"
                :total="patientRecord.healthAdvice.length"
                layout="total, sizes, prev, pager, next"
              />
            </div>
          </div>
          <div v-else class="no-data">
            <el-empty :description="$t('common.noData') || '暂无健康建议'" />
          </div>
        </div>
      </div>
    </div>

    <!-- 健康数据详情对话框 -->
    <el-dialog
      v-model="healthDataDetailVisible"
      title="健康数据详情"
      width="600px"
    >
      <div v-if="selectedHealthData" class="detail-dialog-content">
        <div class="detail-section">
          <h4>基本数据</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">记录时间:</span>
              <span class="detail-value">{{ formatDateTime(selectedHealthData.recordedAt) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">身高:</span>
              <span class="detail-value">{{ selectedHealthData.height }} cm</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">体重:</span>
              <span class="detail-value">{{ selectedHealthData.weight }} kg</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">血压:</span>
              <span class="detail-value">{{ selectedHealthData.bloodPressure }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">心率:</span>
              <span class="detail-value">{{ selectedHealthData.heartRate }} bpm</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">数据类型:</span>
              <span class="detail-value">{{ selectedHealthData.dataType || '-' }}</span>
            </div>
          </div>
        </div>

        <div v-if="selectedHealthData.dietRecord || selectedHealthData.exerciseRecord" class="detail-section">
          <h4>详细记录</h4>
          <div class="detail-grid">
            <div v-if="selectedHealthData.dietRecord" class="detail-item full-width">
              <span class="detail-label">饮食记录:</span>
              <span class="detail-value">{{ selectedHealthData.dietRecord }}</span>
            </div>
            <div v-if="selectedHealthData.exerciseRecord" class="detail-item full-width">
              <span class="detail-label">运动记录:</span>
              <span class="detail-value">{{ selectedHealthData.exerciseRecord }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

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
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { doctorAPI } from '../../services/api'
import { authService } from '../../services/auth'
import { ArrowLeft } from '@element-plus/icons-vue'

export default {
  name: 'PatientRecord',
  components: {
    ArrowLeft
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const isLoading = ref(true)
    const errorMessage = ref('')
    const patientId = route.params.id

    // 分页相关
    const pageSize = ref(10)
    const healthDataPage = ref(1)
    const consultationPage = ref(1)
    const advicePage = ref(1)

    // 对话框相关
    const healthDataDetailVisible = ref(false)
    const selectedHealthData = ref(null)

    // 患者档案数据
    const patientRecord = reactive({
      personalInfo: null,
      healthData: [],
      consultations: [],
      healthAdvice: []
    })

    // 计算分页后的健康数据
    const paginatedHealthData = computed(() => {
      const start = (healthDataPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return patientRecord.healthData.slice(start, end)
    })

    // 计算分页后的咨询记录
    const paginatedConsultations = computed(() => {
      const start = (consultationPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return patientRecord.consultations.slice(start, end)
    })

    // 计算分页后的健康建议
    const paginatedHealthAdvice = computed(() => {
      const start = (advicePage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return patientRecord.healthAdvice.slice(start, end)
    })

    // 格式化性别
    const formatGender = (gender) => {
      const genderMap = {
        'MALE': '男',
        'FEMALE': '女',
        'OTHER': '其他'
      }
      return genderMap[gender] || '未知'
    }

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

    // 获取咨询状态标签颜色
    const getConsultationStatusTag = (status) => {
      const statusMap = {
        'PENDING': 'warning',
        'ANSWERED': 'success'
      }
      return statusMap[status] || 'info'
    }

    // 格式化咨询状态
    const formatConsultationStatus = (status) => {
      const statusMap = {
        'PENDING': '待回答',
        'ANSWERED': '已回答'
      }
      return statusMap[status] || status
    }

    // 显示健康数据详情
    const showHealthDataDetail = (data) => {
      selectedHealthData.value = data
      healthDataDetailVisible.value = true
    }

    // 返回患者列表
    const goBack = () => {
      router.push('/doctor/patients')
    }

    // 获取患者档案
    const fetchPatientRecord = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''

        // 获取当前医师信息
        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取医师信息')
        }

        // 检查权限：患者ID必须是有效的数字
        if (!patientId || isNaN(patientId)) {
          throw new Error('无效的患者ID')
        }

        // 调用API获取患者档案
        const response = await doctorAPI.getPatientRecord(patientId, currentUser.id)

        if (response && response.data) {
          const data = response.data
          patientRecord.personalInfo = data.personalInfo || null
          patientRecord.healthData = data.healthData || []
          patientRecord.consultations = data.consultations || []
          patientRecord.healthAdvice = data.healthAdvice || []
        } else {
          throw new Error('获取患者档案失败')
        }
      } catch (error) {
        console.error('获取患者档案失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '获取患者档案失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '获取患者档案失败'
        }
        ElMessage.error(errorMessage.value)
      } finally {
        isLoading.value = false
      }
    }

    // 页面加载时获取患者档案
    onMounted(() => {
      fetchPatientRecord()
    })

    return {
      isLoading,
      errorMessage,
      patientRecord,
      pageSize,
      healthDataPage,
      consultationPage,
      advicePage,
      paginatedHealthData,
      paginatedConsultations,
      paginatedHealthAdvice,
      healthDataDetailVisible,
      selectedHealthData,
      formatGender,
      formatDateTime,
      getConsultationStatusTag,
      formatConsultationStatus,
      showHealthDataDetail,
      goBack
    }
  }
}
</script>

<style scoped>
.patient-record-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.header-top {
  display: flex;
  align-items: center;
  gap: 15px;
}

.back-btn {
  padding: 0;
  font-size: 14px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.record-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 15px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7eb;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  flex: 1;
}

.count-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.3);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.card-body {
  padding: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  color: #606266;
  font-size: 12px;
  font-weight: 500;
}

.info-item .value {
  color: #333;
  font-size: 14px;
  word-break: break-word;
}

.no-data {
  padding: 40px 20px;
  text-align: center;
}

.data-table {
  display: flex;
  flex-direction: column;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.consultation-list,
.advice-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.consultation-item,
.advice-item {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 15px;
  background: #fafafa;
  transition: all 0.3s ease;
}

.consultation-item:hover,
.advice-item:hover {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.consultation-header,
.advice-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.index {
  display: inline-block;
  background: #409eff;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  text-align: center;
  line-height: 24px;
  font-size: 12px;
  font-weight: bold;
}

.date {
  color: #909399;
  font-size: 12px;
  margin-left: auto;
}

.consultation-content,
.advice-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.question,
.answer,
.advice-text,
.recommendation {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.question strong,
.answer strong,
.advice-text strong,
.recommendation strong {
  color: #333;
  font-size: 13px;
}

.question p,
.answer p,
.advice-text p,
.recommendation p {
  margin: 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.no-answer {
  color: #909399;
  font-size: 13px;
  font-style: italic;
}

.detail-dialog-content {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 14px;
  font-weight: bold;
  border-left: 3px solid #409eff;
  padding-left: 10px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-label {
  color: #606266;
  font-size: 12px;
  font-weight: 500;
}

.detail-value {
  color: #333;
  font-size: 14px;
  word-break: break-word;
  line-height: 1.5;
}

.error-alert {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .patient-record-container {
    padding: 10px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .header-top {
    flex-direction: column;
    align-items: flex-start;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .count-badge {
    align-self: flex-start;
  }

  .consultation-header,
  .advice-header {
    flex-wrap: wrap;
  }

  .date {
    margin-left: 0;
    margin-top: 5px;
    width: 100%;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table__header th) {
    padding: 8px 0;
  }

  :deep(.el-table__body td) {
    padding: 8px 0;
  }

  :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
