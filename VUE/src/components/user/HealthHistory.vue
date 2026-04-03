<template>
  <div class="health-history-container">
    <div class="page-header">
      <h2>{{ $t('healthHistory.healthHistory') || '健康历史' }}</h2>
    </div>

    <el-skeleton v-if="isLoading" :rows="8" animated />

    <div v-else class="filter-card">
      <div class="filter-section">
        <div class="filter-group">
          <label>{{ $t('healthHistory.recordDate') || '记录日期' }}</label>
          <el-date-picker
            v-model="filterData.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </div>

        <div class="filter-group">
          <label>{{ $t('healthHistory.recordType') || '记录类型' }}</label>
          <el-select
            v-model="filterData.recordType"
            :placeholder="$t('common.search') || '选择类型'"
            clearable
          >
            <el-option label="全部" value="" />
            <el-option label="健康数据" value="HEALTH_DATA" />
            <el-option label="常规检查" value="HEALTH_CHECK" />
            <el-option label="性别健康" value="GENDER_HEALTH" />
            <el-option label="咨询记录" value="CONSULTATION" />
            <el-option label="健康建议" value="HEALTH_ADVICE" />
          </el-select>
        </div>

        <div class="filter-actions">
          <el-button type="primary" :loading="isLoading" @click="handleSearch">
            {{ $t('common.search') || '查询' }}
          </el-button>
          <el-button @click="handleReset">
            {{ $t('common.reset') || '重置' }}
          </el-button>
        </div>
      </div>
    </div>

    <div v-if="!isLoading && historyRecords.length > 0" class="records-card">
      <h3>{{ $t('healthHistory.recordDetails') || '记录详情' }}</h3>
      <el-table :data="paginatedRecords" stripe style="width: 100%">
        <el-table-column
          prop="recordedAt"
          :label="$t('healthHistory.recordDate') || '记录日期'"
          width="180"
          sortable
        >
          <template #default="{ row }">
            {{ formatDateTimeDisplay(row.recordedAt) }}
          </template>
        </el-table-column>

        <el-table-column
          prop="recordType"
          :label="$t('healthHistory.recordType') || '记录类型'"
          width="120"
        >
          <template #default="{ row }">
            <el-tag :type="getRecordTypeTag(row.recordType)">
              {{ formatRecordType(row.recordType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="summary" label="摘要" min-width="260" show-overflow-tooltip />

        <el-table-column label="补充信息" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            {{ buildSecondarySummary(row) }}
          </template>
        </el-table-column>

        <el-table-column :label="$t('common.edit') || '操作'" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="historyRecords.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <div v-if="!isLoading && historyRecords.length === 0" class="no-data">
      <el-empty :description="$t('healthHistory.noRecords') || '暂无记录'" />
    </div>

    <el-dialog
      v-model="detailDialogVisible"
      :title="$t('healthHistory.recordDetails') || '记录详情'"
      width="760px"
    >
      <div v-if="selectedRecord" class="detail-dialog-content">
        <div class="detail-section basic-section">
          <h4>基础信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">记录时间</span>
              <span class="detail-value">{{ formatDateTimeDisplay(selectedRecord.recordedAt) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">记录类型</span>
              <span class="detail-value">{{ formatRecordType(selectedRecord.recordType) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">摘要</span>
              <span class="detail-value">{{ selectedRecord.summary || '-' }}</span>
            </div>
            <div v-if="selectedRecord.createdAt" class="detail-item">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ formatDateTimeDisplay(selectedRecord.createdAt) }}</span>
            </div>
          </div>
        </div>

        <div
          v-for="group in detailGroups"
          :key="group.title"
          class="detail-section"
        >
          <h4>{{ group.title }}</h4>
          <div class="detail-grid">
            <div
              v-for="item in group.items"
              :key="`${group.title}-${item.label}`"
              class="detail-item"
              :class="{ 'detail-item-wide': item.wide }"
            >
              <span class="detail-label">{{ item.label }}</span>
              <span class="detail-value">{{ item.value }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { consultationAPI, healthAdviceAPI, healthDataAPI } from '../../services/api'
import { authService } from '../../services/auth'

const STATUS_TEXT = {
  PENDING: '待处理',
  ANSWERED: '已回复',
  REVIEWED: '已审核',
  ACTIVE: '正常'
}

const REVIEW_STATUS_TEXT = {
  PENDING: '待审核',
  REVIEWED: '已审核'
}

const GENDER_VALUE_TEXT = {
  MALE: '男',
  FEMALE: '女',
  OTHER: '其他'
}

const GENDER_HEALTH_LABELS = {
  menstrualCycle: '月经周期',
  menstrualDays: '月经天数',
  lastMenstrualDate: '最后月经日期',
  pregnancyStatus: '妊娠状态',
  menstrualSymptoms: '经期症状',
  prostateHealth: '前列腺状态',
  sexualFunction: '性功能状态'
}

const safeText = (value, fallback = '-') => {
  if (value === null || value === undefined || value === '') {
    return fallback
  }
  return String(value)
}

const formatDateTimeDisplay = (value) => {
  if (!value) {
    return '-'
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return safeText(value)
  }
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const formatNumber = (value, decimals = 1) => {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  const numericValue = Number(value)
  if (!Number.isFinite(numericValue)) {
    return safeText(value)
  }
  return numericValue.toFixed(decimals)
}

const calculateBMI = (height, weight) => {
  const heightValue = Number(height)
  const weightValue = Number(weight)
  if (!heightValue || !weightValue) {
    return null
  }
  const bmi = weightValue / ((heightValue / 100) ** 2)
  return Number.isFinite(bmi) ? bmi : null
}

const buildBloodPressureInsight = (bloodPressure) => {
  if (!bloodPressure || !String(bloodPressure).includes('/')) {
    return null
  }
  const [systolicRaw, diastolicRaw] = String(bloodPressure).split('/')
  const systolic = Number(systolicRaw)
  const diastolic = Number(diastolicRaw)
  if (!Number.isFinite(systolic) || !Number.isFinite(diastolic)) {
    return null
  }
  return {
    pulsePressure: systolic - diastolic,
    pressureLevel: systolic >= 140 || diastolic >= 90 ? '偏高' : systolic < 90 || diastolic < 60 ? '偏低' : '正常范围'
  }
}

const getDefaultDateRange = () => {
  const endDate = new Date()
  const startDate = new Date(endDate.getTime() - 90 * 24 * 60 * 60 * 1000)
  return [
    startDate.toISOString().split('T')[0],
    endDate.toISOString().split('T')[0]
  ]
}

export default {
  name: 'HealthHistory',
  setup() {
    const isLoading = ref(true)
    const errorMessage = ref('')
    const historyRecords = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const detailDialogVisible = ref(false)
    const selectedRecord = ref(null)

    const filterData = reactive({
      dateRange: getDefaultDateRange(),
      recordType: ''
    })

    const paginatedRecords = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return historyRecords.value.slice(start, end)
    })

    const detailGroups = computed(() => {
      if (!selectedRecord.value) {
        return []
      }
      return buildDetailGroups(selectedRecord.value)
    })

    const getRecordTypeTag = (type) => {
      const tagMap = {
        HEALTH_DATA: 'success',
        HEALTH_CHECK: 'info',
        GENDER_HEALTH: 'warning',
        CONSULTATION: 'primary',
        HEALTH_ADVICE: ''
      }
      return tagMap[type] || 'info'
    }

    const formatRecordType = (type) => {
      const typeMap = {
        HEALTH_DATA: '健康数据',
        HEALTH_CHECK: '常规检查',
        GENDER_HEALTH: '性别健康',
        CONSULTATION: '咨询记录',
        HEALTH_ADVICE: '健康建议'
      }
      return typeMap[type] || type
    }

    const normalizeStatus = (status) => STATUS_TEXT[status] || safeText(status)

    const normalizeReviewStatus = (status) => REVIEW_STATUS_TEXT[status] || safeText(status)

    const buildSecondarySummary = (record) => {
      if (record.recordType === 'HEALTH_DATA' || record.recordType === 'HEALTH_CHECK') {
        const parts = []
        if (record.bloodPressure) parts.push(`血压 ${record.bloodPressure}`)
        if (record.heartRate !== null && record.heartRate !== undefined) parts.push(`心率 ${record.heartRate} 次/分`)
        if (record.bodyTemperature !== null && record.bodyTemperature !== undefined) parts.push(`体温 ${formatNumber(record.bodyTemperature)} °C`)
        if (record.bloodOxygen !== null && record.bloodOxygen !== undefined) parts.push(`血氧 ${record.bloodOxygen}%`)
        if (record.bloodSugar !== null && record.bloodSugar !== undefined) parts.push(`血糖 ${formatNumber(record.bloodSugar)} mmol/L`)
        if (record.sleepDuration !== null && record.sleepDuration !== undefined) parts.push(`睡眠 ${formatNumber(record.sleepDuration)} 小时`)
        return parts.join('，') || '-'
      }

      if (record.recordType === 'CONSULTATION') {
        return record.answer ? `医生已回复：${safeText(record.answer).slice(0, 20)}${safeText(record.answer).length > 20 ? '...' : ''}` : '等待医生回复'
      }

      if (record.recordType === 'HEALTH_ADVICE') {
        return record.recommendation || '暂无补充建议'
      }

      if (record.recordType === 'GENDER_HEALTH') {
        const keys = Object.keys(GENDER_HEALTH_LABELS).filter((key) => record[key])
        return keys.map((key) => GENDER_HEALTH_LABELS[key]).join('，') || '已记录专属健康信息'
      }

      return '-'
    }

    const buildHealthSummary = (item, recordType) => {
      if (recordType === 'HEALTH_CHECK') {
        return item.checkResults
          ? `检查结果：${safeText(item.checkResults).slice(0, 24)}${safeText(item.checkResults).length > 24 ? '...' : ''}`
          : `体检记录：血压 ${safeText(item.bloodPressure)}，心率 ${safeText(item.heartRate)}`
      }
      if (recordType === 'GENDER_HEALTH') {
        return '性别健康专项记录'
      }
      return `身高 ${safeText(item.height)} cm，体重 ${safeText(item.weight)} kg`
    }

    const fetchHealthDataRecords = async (startDate, endDate) => {
      try {
        const response = await healthDataAPI.getDataByRange(startDate, endDate)
        if (!response?.data) {
          return []
        }

        return response.data.map((item) => {
          const recordType = item.dataType === 'HEALTH_CHECK'
            ? 'HEALTH_CHECK'
            : item.dataType === 'GENDER_SPECIFIC'
              ? 'GENDER_HEALTH'
              : 'HEALTH_DATA'

          const bmi = calculateBMI(item.height, item.weight)
          const pressureInsight = buildBloodPressureInsight(item.bloodPressure)

          return {
            id: item.id,
            userId: item.userId,
            recordedAt: item.recordedAt,
            createdAt: item.createdAt,
            recordType,
            summary: buildHealthSummary(item, recordType),
            height: item.height,
            weight: item.weight,
            bmi: bmi ? formatNumber(bmi, 1) : null,
            bloodPressure: item.bloodPressure,
            heartRate: item.heartRate,
            bodyTemperature: item.bodyTemperature,
            bloodOxygen: item.bloodOxygen,
            bloodSugar: item.bloodSugar,
            sleepDuration: item.sleepDuration,
            dietRecord: item.dietRecord,
            exerciseRecord: item.exerciseRecord,
            checkResults: item.checkResults,
            reviewStatus: item.reviewStatus,
            reviewFeedback: item.reviewFeedback,
            feedbackDate: item.feedbackDate,
            pulsePressure: pressureInsight?.pulsePressure ?? null,
            pressureLevel: pressureInsight?.pressureLevel ?? null,
            menstrualCycle: item.menstrualCycle,
            menstrualDays: item.menstrualDays,
            lastMenstrualDate: item.lastMenstrualDate,
            pregnancyStatus: item.pregnancyStatus,
            menstrualSymptoms: item.menstrualSymptoms,
            prostateHealth: item.prostateHealth,
            sexualFunction: item.sexualFunction
          }
        })
      } catch (error) {
        console.error('Failed to fetch health history records:', error)
        return []
      }
    }

    const isWithinDateRange = (value, startDate, endDate) => {
      if (!value || !startDate || !endDate) {
        return true
      }
      const current = new Date(value).getTime()
      const start = new Date(`${startDate} 00:00:00`).getTime()
      const end = new Date(`${endDate} 23:59:59`).getTime()
      return current >= start && current <= end
    }

    const fetchConsultationRecords = async (startDate, endDate) => {
      try {
        const response = await consultationAPI.getConsultations()
        if (!response?.data) {
          return []
        }

        return response.data
          .filter((item) => isWithinDateRange(item.createdAt, startDate, endDate))
          .map((item) => ({
          id: item.id,
          userId: item.userId,
          doctorId: item.doctorId,
          recordedAt: item.createdAt,
          createdAt: item.createdAt,
          answeredAt: item.answeredAt,
          recordType: 'CONSULTATION',
          summary: safeText(item.question, '').slice(0, 50) + (safeText(item.question, '').length > 50 ? '...' : ''),
          question: item.question,
          answer: item.answer,
          status: item.status,
          doctorName: item.doctorName
        }))
      } catch (error) {
        console.error('Failed to fetch consultation records:', error)
        return []
      }
    }

    const fetchHealthAdviceRecords = async (startDate, endDate) => {
      try {
        const response = await healthAdviceAPI.getMyAdvice()
        if (!response?.data) {
          return []
        }

        return response.data
          .filter((item) => isWithinDateRange(item.createdAt, startDate, endDate))
          .map((item) => ({
          id: item.id,
          userId: item.userId,
          doctorId: item.doctorId,
          recordedAt: item.createdAt,
          createdAt: item.createdAt,
          updatedAt: item.updatedAt,
          recordType: 'HEALTH_ADVICE',
          summary: safeText(item.adviceContent, '').slice(0, 50) + (safeText(item.adviceContent, '').length > 50 ? '...' : ''),
          adviceContent: item.adviceContent,
          recommendation: item.recommendation,
          doctorName: item.doctorName
        }))
      } catch (error) {
        console.error('Failed to fetch health advice records:', error)
        return []
      }
    }

    const fetchAllRecords = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''

        const currentUser = authService.getUser()
        if (!currentUser?.id) {
          throw new Error('无法获取用户信息')
        }

        if (!filterData.dateRange || filterData.dateRange.length !== 2) {
          throw new Error('请选择完整的日期范围')
        }

        const allRecords = []
        const [startDate, endDate] = filterData.dateRange

        if (!filterData.recordType || ['HEALTH_DATA', 'HEALTH_CHECK', 'GENDER_HEALTH'].includes(filterData.recordType)) {
          const healthRecords = await fetchHealthDataRecords(startDate, endDate)
          const filteredHealthRecords = filterData.recordType
            ? healthRecords.filter((item) => item.recordType === filterData.recordType)
            : healthRecords
          allRecords.push(...filteredHealthRecords)
        }

        if (!filterData.recordType || filterData.recordType === 'CONSULTATION') {
          const consultationRecords = await fetchConsultationRecords(startDate, endDate)
          allRecords.push(...consultationRecords)
        }

        if (!filterData.recordType || filterData.recordType === 'HEALTH_ADVICE') {
          const adviceRecords = await fetchHealthAdviceRecords(startDate, endDate)
          allRecords.push(...adviceRecords)
        }

        historyRecords.value = allRecords.sort((a, b) => new Date(b.recordedAt) - new Date(a.recordedAt))
        currentPage.value = 1
      } catch (error) {
        console.error('Failed to fetch history records:', error)
        errorMessage.value = error.message || '获取历史记录失败'
        ElMessage.error(errorMessage.value)
      } finally {
        isLoading.value = false
      }
    }

    const buildDetailGroups = (record) => {
      const groups = []

      if (record.recordType === 'HEALTH_DATA' || record.recordType === 'HEALTH_CHECK') {
        const metricItems = [
          { label: '身高', value: record.height ? `${formatNumber(record.height, 1)} cm` : '-', wide: false },
          { label: '体重', value: record.weight ? `${formatNumber(record.weight, 1)} kg` : '-', wide: false },
          { label: 'BMI', value: record.bmi ? `${record.bmi}` : '-', wide: false },
          { label: '血压', value: safeText(record.bloodPressure), wide: false },
          { label: '脉压差', value: record.pulsePressure !== null && record.pulsePressure !== undefined ? `${record.pulsePressure} mmHg` : '-', wide: false },
          { label: '血压判断', value: safeText(record.pressureLevel), wide: false },
          { label: '心率', value: record.heartRate !== null && record.heartRate !== undefined ? `${record.heartRate} 次/分` : '-', wide: false },
          { label: '体温', value: record.bodyTemperature !== null && record.bodyTemperature !== undefined ? `${formatNumber(record.bodyTemperature, 1)} °C` : '-', wide: false },
          { label: '血氧', value: record.bloodOxygen !== null && record.bloodOxygen !== undefined ? `${record.bloodOxygen}%` : '-', wide: false },
          { label: '血糖', value: record.bloodSugar !== null && record.bloodSugar !== undefined ? `${formatNumber(record.bloodSugar, 1)} mmol/L` : '-', wide: false },
          { label: '睡眠时长', value: record.sleepDuration !== null && record.sleepDuration !== undefined ? `${formatNumber(record.sleepDuration, 1)} 小时` : '-', wide: false }
        ]
        groups.push({ title: '健康指标', items: metricItems })

        const noteItems = []
        if (record.checkResults) noteItems.push({ label: '检查结果', value: safeText(record.checkResults), wide: true })
        if (record.dietRecord) noteItems.push({ label: '饮食记录', value: safeText(record.dietRecord), wide: true })
        if (record.exerciseRecord) noteItems.push({ label: '运动记录', value: safeText(record.exerciseRecord), wide: true })
        if (noteItems.length > 0) {
          groups.push({ title: '补充说明', items: noteItems })
        }

        const reviewItems = [
          { label: '审核状态', value: normalizeReviewStatus(record.reviewStatus), wide: false }
        ]
        if (record.feedbackDate) reviewItems.push({ label: '审核时间', value: formatDateTimeDisplay(record.feedbackDate), wide: false })
        if (record.reviewFeedback) reviewItems.push({ label: '审核反馈', value: safeText(record.reviewFeedback), wide: true })
        groups.push({ title: '审核信息', items: reviewItems })
      }

      if (record.recordType === 'GENDER_HEALTH') {
        const genderHealthItems = [
          { label: '月经周期', value: record.menstrualCycle ? `${record.menstrualCycle} 天` : '-', wide: false },
          { label: '月经天数', value: record.menstrualDays ? `${record.menstrualDays} 天` : '-', wide: false },
          { label: '最后月经日期', value: safeText(record.lastMenstrualDate), wide: false },
          { label: '妊娠状态', value: safeText(record.pregnancyStatus), wide: false },
          { label: '经期症状', value: safeText(record.menstrualSymptoms), wide: true },
          { label: '前列腺状态', value: safeText(record.prostateHealth), wide: true },
          { label: '性功能状态', value: safeText(record.sexualFunction), wide: false }
        ]
        groups.push({ title: '专项健康信息', items: genderHealthItems })
      }

      if (record.recordType === 'CONSULTATION') {
        const consultationItems = [
          { label: '当前状态', value: normalizeStatus(record.status), wide: false },
          { label: '医生姓名', value: safeText(record.doctorName), wide: false },
          { label: '医生ID', value: safeText(record.doctorId), wide: false },
          { label: '提问内容', value: safeText(record.question), wide: true },
          { label: '医生回复', value: safeText(record.answer), wide: true }
        ]
        if (record.answeredAt) {
          consultationItems.push({ label: '回复时间', value: formatDateTimeDisplay(record.answeredAt), wide: false })
        }
        groups.push({ title: '咨询详情', items: consultationItems })
      }

      if (record.recordType === 'HEALTH_ADVICE') {
        const adviceItems = [
          { label: '医生姓名', value: safeText(record.doctorName), wide: false },
          { label: '医生ID', value: safeText(record.doctorId), wide: false },
          { label: '建议内容', value: safeText(record.adviceContent), wide: true },
          { label: '推荐方案', value: safeText(record.recommendation), wide: true }
        ]
        if (record.updatedAt) {
          adviceItems.push({ label: '更新时间', value: formatDateTimeDisplay(record.updatedAt), wide: false })
        }
        groups.push({ title: '建议详情', items: adviceItems })
      }

      return groups.filter((group) => group.items.some((item) => item.value !== '-'))
    }

    const handleSearch = () => {
      fetchAllRecords()
    }

    const handleReset = () => {
      filterData.dateRange = getDefaultDateRange()
      filterData.recordType = ''
      fetchAllRecords()
    }

    const handleViewDetail = (record) => {
      selectedRecord.value = record
      detailDialogVisible.value = true
    }

    const handlePageChange = (page) => {
      currentPage.value = page
    }

    const handlePageSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
    }

    onMounted(() => {
      fetchAllRecords()
    })

    return {
      isLoading,
      errorMessage,
      historyRecords,
      paginatedRecords,
      filterData,
      currentPage,
      pageSize,
      detailDialogVisible,
      selectedRecord,
      detailGroups,
      formatDateTimeDisplay,
      getRecordTypeTag,
      formatRecordType,
      buildSecondarySummary,
      handleSearch,
      handleReset,
      handleViewDetail,
      handlePageChange,
      handlePageSizeChange
    }
  }
}
</script>

<style scoped>
.health-history-container {
  width: 100%;
  padding: 8px 0 24px;
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
  font-weight: 700;
}

.filter-card,
.records-card,
.no-data {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e8eef5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.filter-card {
  padding: 24px 28px;
  margin-bottom: 30px;
}

.filter-section {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 220px;
}

.filter-group label {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.filter-actions {
  display: flex;
  gap: 10px;
  min-width: 200px;
}

.records-card {
  padding: 32px 36px;
  margin-bottom: 30px;
}

.records-card h3 {
  margin: 0 0 20px;
  color: #333;
  font-size: 18px;
  font-weight: 700;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.no-data {
  padding: 60px 20px;
  text-align: center;
}

.detail-dialog-content {
  padding: 8px 0;
}

.detail-section {
  margin-bottom: 22px;
}

.detail-section h4 {
  margin: 0 0 14px;
  color: #1f2d3d;
  font-size: 15px;
  font-weight: 700;
  border-left: 3px solid #409eff;
  padding-left: 10px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  background: #f8fbff;
  border: 1px solid #e8eef5;
  border-radius: 12px;
  padding: 14px 16px;
}

.detail-item-wide {
  grid-column: span 2;
}

.detail-label {
  color: #606266;
  font-size: 13px;
}

.detail-value {
  color: #1f2d3d;
  font-size: 14px;
  line-height: 1.7;
  word-break: break-word;
}

.basic-section .detail-item {
  background: #f5f7fa;
}

.error-alert {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group,
  .filter-actions {
    min-width: 100%;
  }

  .records-card {
    padding: 20px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-item-wide {
    grid-column: span 1;
  }

  :deep(.el-date-editor),
  :deep(.el-select) {
    width: 100%;
  }
}
</style>
