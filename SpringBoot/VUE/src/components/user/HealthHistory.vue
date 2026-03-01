<template>
  <div class="health-history-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('healthHistory.healthHistory') || '健康历史' }}</h2>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="8" animated />

    <!-- 筛选条件卡片 -->
    <div v-else class="filter-card">
      <div class="filter-section">
        <div class="filter-group">
          <label>{{ $t('healthHistory.recordDate') || '记录日期' }}:</label>
          <el-date-picker
            v-model="filterData.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </div>

        <div class="filter-group">
          <label>{{ $t('healthHistory.recordType') || '记录类型' }}:</label>
          <el-select
            v-model="filterData.recordType"
            :placeholder="$t('common.search') || '选择类型'"
            clearable
            @change="handleFilterChange"
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
          <el-button type="primary" @click="handleSearch" :loading="isLoading">
            {{ $t('common.search') || '查询' }}
          </el-button>
          <el-button @click="handleReset">
            {{ $t('common.reset') || '重置' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 历史记录表格 -->
    <div v-if="!isLoading && historyRecords.length > 0" class="records-card">
      <h3>{{ $t('healthHistory.recordDetails') || '记录详情' }}</h3>
      <el-table
        :data="paginatedRecords"
        stripe
        style="width: 100%"
        @expand-change="handleExpandChange"
      >
        <!-- 展开行 -->
        <el-table-column type="expand" width="50">
          <template #default="{ row }">
            <div class="expand-content">
              <div class="detail-section">
                <h4>详细信息</h4>
                <div class="detail-grid">
                  <div v-for="(value, key) in getDetailInfo(row)" :key="key" class="detail-item">
                    <span class="detail-label">{{ formatDetailLabel(key) }}:</span>
                    <span class="detail-value">{{ formatDetailValue(value) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 记录日期 -->
        <el-table-column
          prop="recordedAt"
          :label="$t('healthHistory.recordDate') || '记录日期'"
          width="180"
          sortable
        />

        <!-- 记录类型 -->
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

        <!-- 摘要 -->
        <el-table-column
          prop="summary"
          label="摘要"
          min-width="200"
          show-overflow-tooltip
        />

        <!-- 操作 -->
        <el-table-column :label="$t('common.edit') || '操作'" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 无数据提示 -->
    <div v-if="!isLoading && historyRecords.length === 0" class="no-data">
      <el-empty :description="$t('healthHistory.noRecords') || '暂无记录'" />
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="$t('healthHistory.recordDetails') || '记录详情'"
      width="600px"
    >
      <div v-if="selectedRecord" class="detail-dialog-content">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">记录日期:</span>
              <span class="detail-value">{{ selectedRecord.recordedAt }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">记录类型:</span>
              <span class="detail-value">{{ formatRecordType(selectedRecord.recordType) }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>详细数据</h4>
          <div class="detail-grid">
            <div v-for="(value, key) in getDetailInfo(selectedRecord)" :key="key" class="detail-item">
              <span class="detail-label">{{ formatDetailLabel(key) }}:</span>
              <span class="detail-value">{{ formatDetailValue(value) }}</span>
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
import { ElMessage } from 'element-plus'
import { healthDataAPI, consultationAPI, healthAdviceAPI } from '../../services/api'
import { authService } from '../../services/auth'
import { formatDateTime } from '../../utils/formatters'

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

    // 默认日期范围（最近90天）
    const getDefaultDateRange = () => {
      const endDate = new Date()
      const startDate = new Date(endDate.getTime() - 90 * 24 * 60 * 60 * 1000)
      return [
        startDate.toISOString().split('T')[0],
        endDate.toISOString().split('T')[0]
      ]
    }

    const defaultRange = getDefaultDateRange()

    const filterData = reactive({
      dateRange: defaultRange,
      recordType: ''
    })

    // 分页后的记录
    const paginatedRecords = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return historyRecords.value.slice(start, end)
    })

    // 获取记录类型标签颜色
    const getRecordTypeTag = (type) => {
      const tagMap = {
        'HEALTH_DATA': 'success',
        'HEALTH_CHECK': 'info',
        'GENDER_HEALTH': 'warning',
        'CONSULTATION': 'primary',
        'HEALTH_ADVICE': ''
      }
      return tagMap[type] || 'info'
    }

    // 格式化记录类型
    const formatRecordType = (type) => {
      const typeMap = {
        'HEALTH_DATA': '健康数据',
        'HEALTH_CHECK': '常规检查',
        'GENDER_HEALTH': '性别健康',
        'CONSULTATION': '咨询记录',
        'HEALTH_ADVICE': '健康建议'
      }
      return typeMap[type] || type
    }

    // 格式化详情标签
    const formatDetailLabel = (key) => {
      const labelMap = {
        'height': '身高',
        'weight': '体重',
        'bloodPressure': '血压',
        'heartRate': '心率',
        'dietRecord': '饮食记录',
        'exerciseRecord': '运动记录',
        'checkItems': '检查项目',
        'checkResults': '检查结果',
        'menstrualCycle': '月经周期',
        'menstrualDays': '月经天数',
        'lastMenstrualDate': '最后月经日期',
        'pregnancyStatus': '妊娠状态',
        'menstrualSymptoms': '月经症状',
        'prostateHealth': '前列腺健康',
        'sexualFunction': '性功能状态',
        'question': '问题',
        'answer': '回答',
        'status': '状态',
        'adviceContent': '建议内容',
        'recommendation': '推荐',
        'doctorName': '医师名称'
      }
      return labelMap[key] || key
    }

    // 格式化详情值
    const formatDetailValue = (value) => {
      if (value === null || value === undefined) {
        return '-'
      }
      if (typeof value === 'boolean') {
        return value ? '是' : '否'
      }
      if (typeof value === 'object') {
        return JSON.stringify(value)
      }
      return String(value)
    }

    // 获取详情信息
    const getDetailInfo = (record) => {
      const info = {}
      const excludeKeys = ['id', 'userId', 'recordedAt', 'recordType', 'summary', 'createdAt', 'updatedAt', 'dataType']

      for (const key in record) {
        if (!excludeKeys.includes(key) && record[key] !== null && record[key] !== undefined) {
          info[key] = record[key]
        }
      }

      return info
    }

    // 获取健康数据记录
    const fetchHealthDataRecords = async (startDate, endDate) => {
      try {
        const response = await healthDataAPI.getDataByRange(startDate, endDate)
        if (response && response.data) {
          return response.data.map(item => ({
            id: item.id,
            recordedAt: item.recordedAt,
            recordType: 'HEALTH_DATA',
            summary: `身高: ${item.height}cm, 体重: ${item.weight}kg`,
            height: item.height,
            weight: item.weight,
            bloodPressure: item.bloodPressure,
            heartRate: item.heartRate,
            dietRecord: item.dietRecord,
            exerciseRecord: item.exerciseRecord
          }))
        }
      } catch (error) {
        console.error('获取健康数据记录失败:', error)
      }
      return []
    }

    // 获取咨询记录
    const fetchConsultationRecords = async () => {
      try {
        const response = await consultationAPI.getConsultations()
        if (response && response.data) {
          return response.data.map(item => ({
            id: item.id,
            recordedAt: item.createdAt,
            recordType: 'CONSULTATION',
            summary: item.question.substring(0, 50) + (item.question.length > 50 ? '...' : ''),
            question: item.question,
            answer: item.answer,
            status: item.status,
            doctorName: item.doctorName
          }))
        }
      } catch (error) {
        console.error('获取咨询记录失败:', error)
      }
      return []
    }

    // 获取健康建议记录
    const fetchHealthAdviceRecords = async () => {
      try {
        const response = await healthAdviceAPI.getMyAdvice()
        if (response && response.data) {
          return response.data.map(item => ({
            id: item.id,
            recordedAt: item.createdAt,
            recordType: 'HEALTH_ADVICE',
            summary: item.adviceContent.substring(0, 50) + (item.adviceContent.length > 50 ? '...' : ''),
            adviceContent: item.adviceContent,
            recommendation: item.recommendation,
            doctorName: item.doctorName
          }))
        }
      } catch (error) {
        console.error('获取健康建议记录失败:', error)
      }
      return []
    }

    // 获取所有历史记录
    const fetchAllRecords = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''

        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取用户信息')
        }

        const allRecords = []

        // 获取健康数据记录
        if (!filterData.recordType || filterData.recordType === 'HEALTH_DATA') {
          const healthRecords = await fetchHealthDataRecords(
            filterData.dateRange[0],
            filterData.dateRange[1]
          )
          allRecords.push(...healthRecords)
        }

        // 获取咨询记录
        if (!filterData.recordType || filterData.recordType === 'CONSULTATION') {
          const consultationRecords = await fetchConsultationRecords()
          // 按日期范围筛选
          const filtered = consultationRecords.filter(record => {
            const recordDate = record.recordedAt.split(' ')[0]
            return recordDate >= filterData.dateRange[0] && recordDate <= filterData.dateRange[1]
          })
          allRecords.push(...filtered)
        }

        // 获取健康建议记录
        if (!filterData.recordType || filterData.recordType === 'HEALTH_ADVICE') {
          const adviceRecords = await fetchHealthAdviceRecords()
          // 按日期范围筛选
          const filtered = adviceRecords.filter(record => {
            const recordDate = record.recordedAt.split(' ')[0]
            return recordDate >= filterData.dateRange[0] && recordDate <= filterData.dateRange[1]
          })
          allRecords.push(...filtered)
        }

        // 按日期排序（最新的在前）
        historyRecords.value = allRecords.sort((a, b) =>
          new Date(b.recordedAt) - new Date(a.recordedAt)
        )

        // 重置分页
        currentPage.value = 1
      } catch (error) {
        console.error('获取历史记录失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '获取历史记录失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '获取历史记录失败'
        }
        ElMessage.error(errorMessage.value)
      } finally {
        isLoading.value = false
      }
    }

    // 日期变化处理
    const handleDateChange = () => {
      if (filterData.dateRange && filterData.dateRange.length === 2) {
        if (new Date(filterData.dateRange[0]) > new Date(filterData.dateRange[1])) {
          ElMessage.warning('开始日期不能晚于结束日期')
          filterData.dateRange = defaultRange
        }
      }
    }

    // 筛选条件变化处理
    const handleFilterChange = () => {
      // 可以在这里添加自动搜索逻辑
    }

    // 搜索按钮处理
    const handleSearch = async () => {
      await fetchAllRecords()
    }

    // 重置按钮处理
    const handleReset = () => {
      filterData.dateRange = defaultRange
      filterData.recordType = ''
      currentPage.value = 1
      historyRecords.value = []
    }

    // 查看详情
    const handleViewDetail = (record) => {
      selectedRecord.value = record
      detailDialogVisible.value = true
    }

    // 展开行处理
    const handleExpandChange = () => {
      // 可以在这里添加展开行的逻辑
    }

    // 分页处理
    const handlePageChange = () => {
      // 分页自动处理
    }

    const handlePageSizeChange = () => {
      currentPage.value = 1
    }

    // 页面加载时获取数据
    onMounted(async () => {
      await fetchAllRecords()
    })

    return {
      isLoading,
      errorMessage,
      historyRecords,
      paginatedRecords,
      currentPage,
      pageSize,
      filterData,
      detailDialogVisible,
      selectedRecord,
      getRecordTypeTag,
      formatRecordType,
      formatDetailLabel,
      formatDetailValue,
      getDetailInfo,
      handleDateChange,
      handleFilterChange,
      handleSearch,
      handleReset,
      handleViewDetail,
      handleExpandChange,
      handlePageChange,
      handlePageSizeChange
    }
  }
}
</script>

<style scoped>
.health-history-container {
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

.filter-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
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
  min-width: 200px;
}

.filter-group label {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  flex: 1;
  min-width: 200px;
  justify-content: flex-start;
}

.records-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
}

.records-card h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.expand-content {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
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

.detail-label {
  color: #606266;
  font-size: 12px;
  font-weight: 500;
}

.detail-value {
  color: #333;
  font-size: 14px;
  word-break: break-word;
}

.detail-dialog-content {
  padding: 20px 0;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.no-data {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 60px 20px;
  text-align: center;
}

.error-alert {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .health-history-container {
    padding: 10px;
  }

  .filter-section {
    flex-direction: column;
    gap: 15px;
  }

  .filter-group {
    min-width: 100%;
  }

  .filter-actions {
    min-width: 100%;
    justify-content: space-between;
  }

  .records-card {
    padding: 20px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  :deep(.el-date-picker) {
    width: 100%;
  }

  :deep(.el-select) {
    width: 100%;
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
