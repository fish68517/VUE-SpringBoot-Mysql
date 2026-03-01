<template>
  <div class="audit-log-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('admin.auditLogs') || '审计日志' }}</h2>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="8" animated />

    <!-- 筛选条件卡片 -->
    <div v-else class="filter-card">
      <div class="filter-section">
        <div class="filter-group">
          <label>{{ $t('admin.userManagement') || '用户ID' }}:</label>
          <el-input
            v-model="filterData.userId"
            type="number"
            :placeholder="$t('admin.userManagement') || '输入用户ID'"
            clearable
            @change="handleFilterChange"
          />
        </div>

        <div class="filter-group">
          <label>{{ $t('admin.action') || '操作类型' }}:</label>
          <el-input
            v-model="filterData.action"
            :placeholder="$t('admin.action') || '输入操作类型'"
            clearable
            @change="handleFilterChange"
          />
        </div>

        <div class="filter-group">
          <label>{{ $t('healthData.startDate') || '开始日期' }}:</label>
          <el-date-picker
            v-model="filterData.startDate"
            type="datetime"
            :placeholder="$t('healthData.startDate') || '开始日期'"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </div>

        <div class="filter-group">
          <label>{{ $t('healthData.endDate') || '结束日期' }}:</label>
          <el-date-picker
            v-model="filterData.endDate"
            type="datetime"
            :placeholder="$t('healthData.endDate') || '结束日期'"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </div>

        <div class="filter-actions">
          <el-button type="primary" @click="handleSearch" :loading="isLoading">
            {{ $t('common.search') || '搜索' }}
          </el-button>
          <el-button @click="handleReset">
            {{ $t('common.reset') || '重置' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 审计日志表格 -->
    <div v-if="!isLoading" class="table-card">
      <el-table
        :data="auditLogs"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'timestamp', order: 'descending' }"
        @expand-change="handleExpandChange"
      >
        <!-- 展开行 -->
        <el-table-column type="expand" width="50">
          <template #default="{ row }">
            <div class="expand-content">
              <div class="detail-item">
                <span class="detail-label">{{ $t('admin.details') || '详情' }}:</span>
                <span class="detail-value">{{ row.details || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- ID列 -->
        <el-table-column
          prop="id"
          :label="$t('common.id') || 'ID'"
          width="80"
          sortable
        />

        <!-- 用户ID列 -->
        <el-table-column
          prop="userId"
          :label="$t('admin.userManagement') || '用户ID'"
          width="100"
          sortable
        />

        <!-- 操作类型列 -->
        <el-table-column
          prop="action"
          :label="$t('admin.action') || '操作类型'"
          width="150"
          sortable
        >
          <template #default="{ row }">
            <el-tag :type="getActionTagType(row.action)">
              {{ row.action }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 资源列 -->
        <el-table-column
          prop="resource"
          :label="$t('admin.resource') || '资源'"
          width="120"
          sortable
        />

        <!-- 时间戳列 -->
        <el-table-column
          prop="timestamp"
          :label="$t('admin.timestamp') || '时间戳'"
          width="180"
          sortable
          :formatter="formatTimestamp"
        />

        <!-- 操作列 -->
        <el-table-column
          :label="$t('common.action') || '操作'"
          width="120"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleViewDetails(row)"
            >
              {{ $t('common.view') || '查看' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalCount"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailsDialogVisible"
      :title="$t('admin.auditLogDetails') || '审计日志详情'"
      width="600px"
    >
      <div v-if="selectedLog" class="details-dialog">
        <div class="detail-row">
          <span class="detail-label">ID:</span>
          <span class="detail-value">{{ selectedLog.id }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ $t('admin.userManagement') || '用户ID' }}:</span>
          <span class="detail-value">{{ selectedLog.userId || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ $t('admin.action') || '操作类型' }}:</span>
          <span class="detail-value">
            <el-tag :type="getActionTagType(selectedLog.action)">
              {{ selectedLog.action }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ $t('admin.resource') || '资源' }}:</span>
          <span class="detail-value">{{ selectedLog.resource || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ $t('admin.timestamp') || '时间戳' }}:</span>
          <span class="detail-value">{{ formatTimestamp(selectedLog) }}</span>
        </div>
        <div class="detail-row full-width">
          <span class="detail-label">{{ $t('admin.details') || '详情' }}:</span>
          <div class="detail-value details-text">{{ selectedLog.details || '-' }}</div>
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
import { adminAPI } from '../../services/api'

export default {
  name: 'AuditLog',
  setup() {
    const isLoading = ref(true)
    const errorMessage = ref('')
    const successMessage = ref('')
    const auditLogs = ref([])
    const selectedLog = ref(null)
    const detailsDialogVisible = ref(false)

    // 分页数据
    const currentPage = ref(1)
    const pageSize = ref(20)
    const totalCount = ref(0)

    // 筛选数据
    const filterData = reactive({
      userId: '',
      action: '',
      startDate: '',
      endDate: ''
    })

    // 获取默认日期范围（最近30天）
    const getDefaultDateRange = () => {
      const endDate = new Date()
      const startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000)
      return {
        start: startDate.toISOString().replace('T', ' ').substring(0, 19),
        end: endDate.toISOString().replace('T', ' ').substring(0, 19)
      }
    }

    // 初始化默认日期范围
    const defaultRange = getDefaultDateRange()
    filterData.startDate = defaultRange.start
    filterData.endDate = defaultRange.end

    // 分页后的审计日志
    const paginatedLogs = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return auditLogs.value.slice(start, end)
    })

    // 获取操作类型标签颜色
    const getActionTagType = (action) => {
      const actionTypes = {
        'CREATE': 'success',
        'UPDATE': 'warning',
        'DELETE': 'danger',
        'LOGIN': 'info',
        'LOGOUT': 'info',
        'QUERY': 'info'
      }
      return actionTypes[action] || 'info'
    }

    // 格式化时间戳
    const formatTimestamp = (row) => {
      if (!row.timestamp) return '-'
      // 如果时间戳已经是格式化的字符串，直接返回
      if (typeof row.timestamp === 'string') {
        return row.timestamp
      }
      // 如果是数组格式（[year, month, day, hour, minute, second]），转换为字符串
      if (Array.isArray(row.timestamp)) {
        const [year, month, day, hour, minute, second] = row.timestamp
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`
      }
      return row.timestamp
    }

    // 获取审计日志
    const fetchAuditLogs = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''
        successMessage.value = ''

        // 构建查询参数
        const params = {}
        if (filterData.userId) {
          params.userId = filterData.userId
        }
        if (filterData.action) {
          params.action = filterData.action
        }
        if (filterData.startDate && filterData.endDate) {
          params.startTime = filterData.startDate
          params.endTime = filterData.endDate
        }

        const response = await adminAPI.getAuditLogs(params)

        if (response && response.data) {
          auditLogs.value = response.data
          totalCount.value = response.data.length
          currentPage.value = 1
          successMessage.value = '审计日志加载成功'
        }
      } catch (error) {
        console.error('获取审计日志失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '获取审计日志失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '获取审计日志失败'
        }
        ElMessage.error(errorMessage.value)
      } finally {
        isLoading.value = false
      }
    }

    // 日期变化处理
    const handleDateChange = () => {
      if (filterData.startDate && filterData.endDate) {
        if (new Date(filterData.startDate) > new Date(filterData.endDate)) {
          ElMessage.warning('开始日期不能晚于结束日期')
          return
        }
      }
    }

    // 筛选条件变化处理
    const handleFilterChange = () => {
      // 可以在这里添加实时搜索逻辑
    }

    // 搜索按钮处理
    const handleSearch = async () => {
      await fetchAuditLogs()
    }

    // 重置按钮处理
    const handleReset = () => {
      filterData.userId = ''
      filterData.action = ''
      const defaultRange = getDefaultDateRange()
      filterData.startDate = defaultRange.start
      filterData.endDate = defaultRange.end
    }

    // 查看详情
    const handleViewDetails = (row) => {
      selectedLog.value = row
      detailsDialogVisible.value = true
    }

    // 展开行处理
    const handleExpandChange = (row, expandedRows) => {
      // 可以在这里添加展开行的逻辑
    }

    // 分页大小变化处理
    const handlePageSizeChange = (newSize) => {
      pageSize.value = newSize
      currentPage.value = 1
    }

    // 页码变化处理
    const handlePageChange = (newPage) => {
      currentPage.value = newPage
    }

    // 页面加载时获取数据
    onMounted(async () => {
      await fetchAuditLogs()
    })

    return {
      isLoading,
      errorMessage,
      successMessage,
      auditLogs: paginatedLogs,
      selectedLog,
      detailsDialogVisible,
      filterData,
      currentPage,
      pageSize,
      totalCount,
      getActionTagType,
      formatTimestamp,
      handleDateChange,
      handleFilterChange,
      handleSearch,
      handleReset,
      handleViewDetails,
      handleExpandChange,
      handlePageSizeChange,
      handlePageChange
    }
  }
}
</script>

<style scoped>
.audit-log-container {
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
  min-width: 180px;
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
  min-width: 180px;
  justify-content: flex-start;
}

.table-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 30px;
}

.expand-content {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.detail-item {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.detail-label {
  font-weight: 500;
  color: #333;
  min-width: 100px;
}

.detail-value {
  color: #606266;
  flex: 1;
  word-break: break-all;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7eb;
}

.details-dialog {
  padding: 20px;
}

.detail-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  align-items: flex-start;
}

.detail-row.full-width {
  flex-direction: column;
}

.detail-label {
  font-weight: 500;
  color: #333;
  min-width: 100px;
  flex-shrink: 0;
}

.detail-value {
  color: #606266;
  flex: 1;
  word-break: break-all;
}

.details-text {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
}

.error-alert {
  margin-top: 20px;
}

.success-alert {
  margin-top: 20px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table__header th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #f5f7fa;
}

:deep(.el-date-picker) {
  width: 100%;
}

:deep(.el-input) {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .audit-log-container {
    padding: 10px;
  }

  .page-header h2 {
    font-size: 20px;
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

  .table-card {
    padding: 10px;
    overflow-x: auto;
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

  .detail-row {
    flex-direction: column;
    gap: 5px;
  }

  .detail-label {
    min-width: auto;
  }
}
</style>
