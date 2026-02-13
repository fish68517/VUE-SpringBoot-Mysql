<template>
  <div class="admin-logs">
    <Header />
    <div class="admin-container">
      <AdminMenu />
      <div class="logs-content">
        <h1>日志管理</h1>

        <!-- 日志列表 -->
        <div class="logs-list-section">
          <h2>系统日志</h2>

          <!-- 筛选和搜索 -->
          <div class="filter-bar">
            <select v-model="filterLogLevel">
              <option value="">全部日志级别</option>
              <option value="INFO">信息</option>
              <option value="WARN">警告</option>
              <option value="ERROR">错误</option>
            </select>
            <input
              v-model="filterStartDate"
              type="date"
              placeholder="开始日期"
            />
            <input
              v-model="filterEndDate"
              type="date"
              placeholder="结束日期"
            />
            <button @click="handleFilter" class="btn btn-secondary">筛选</button>
            <button @click="handleReset" class="btn btn-secondary">重置</button>
            <button @click="handleExport" class="btn btn-secondary">导出</button>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-indicator">
            <div class="spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- 日志表格 -->
          <div v-else-if="logs.length > 0" class="logs-table">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>日志级别</th>
                  <th>消息</th>
                  <th>时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in logs" :key="log.id">
                  <td>{{ log.id }}</td>
                  <td>
                    <span :class="['log-level-badge', 'level-' + log.logLevel.toLowerCase()]">
                      {{ log.logLevel }}
                    </span>
                  </td>
                  <td class="message-cell">{{ log.message }}</td>
                  <td>{{ formatDate(log.createdAt) }}</td>
                  <td class="actions">
                    <button
                      @click="handleViewDetails(log)"
                      class="btn btn-small btn-info"
                    >
                      详情
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <p>暂无日志数据</p>
          </div>

          <!-- 分页 -->
          <div v-if="totalPages > 1" class="pagination">
            <button
              @click="currentPage--"
              :disabled="currentPage === 0"
              class="btn btn-secondary"
            >
              上一页
            </button>
            <span class="page-info">
              第 {{ currentPage + 1 }} / {{ totalPages }} 页
            </span>
            <button
              @click="currentPage++"
              :disabled="currentPage >= totalPages - 1"
              class="btn btn-secondary"
            >
              下一页
            </button>
          </div>
        </div>

        <!-- 日志详情模态框 -->
        <div v-if="showDetailsModal" class="modal-overlay" @click="closeDetailsModal">
          <div class="modal" @click.stop>
            <div class="modal-header">
              <h3>日志详情</h3>
              <button @click="closeDetailsModal" class="close-btn">×</button>
            </div>
            <div class="modal-body">
              <div class="log-details-section">
                <div class="detail-item">
                  <label>日志ID</label>
                  <p>{{ selectedLog.id }}</p>
                </div>
                <div class="detail-item">
                  <label>日志级别</label>
                  <p>
                    <span :class="['log-level-badge', 'level-' + selectedLog.logLevel.toLowerCase()]">
                      {{ selectedLog.logLevel }}
                    </span>
                  </p>
                </div>
                <div class="detail-item">
                  <label>时间</label>
                  <p>{{ formatDate(selectedLog.createdAt) }}</p>
                </div>
                <div class="detail-item full-width">
                  <label>消息</label>
                  <p class="message-text">{{ selectedLog.message }}</p>
                </div>
                <div v-if="selectedLog.details" class="detail-item full-width">
                  <label>详细信息</label>
                  <p class="details-text">{{ selectedLog.details }}</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button @click="closeDetailsModal" class="btn btn-secondary">
                关闭
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { adminAPI } from '../services/api'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import AdminMenu from '../components/AdminMenu.vue'
import { ElMessage } from 'element-plus'

// State
const logs = ref([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(1)
const filterLogLevel = ref('')
const filterStartDate = ref('')
const filterEndDate = ref('')
const showDetailsModal = ref(false)
const selectedLog = ref(null)

// Load logs
const loadLogs = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    if (filterLogLevel.value) {
      params.logLevel = filterLogLevel.value
    }
    
    const response = await adminAPI.getLogs(params)
    if (response.code === 200) {
      logs.value = response.data.content || []
      totalPages.value = response.data.totalPages || 1
    } else {
      ElMessage.error(response.message || '获取日志列表失败')
    }
  } catch (error) {
    ElMessage.error('获取日志列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// Filter logs
const handleFilter = () => {
  currentPage.value = 0
  loadLogs()
}

// Reset filters
const handleReset = () => {
  filterLogLevel.value = ''
  filterStartDate.value = ''
  filterEndDate.value = ''
  currentPage.value = 0
  loadLogs()
}

// Export logs
const handleExport = () => {
  try {
    if (logs.value.length === 0) {
      ElMessage.warning('没有日志数据可导出')
      return
    }

    // Prepare CSV data
    const headers = ['ID', '日志级别', '消息', '时间', '详细信息']
    const rows = logs.value.map(log => [
      log.id,
      log.logLevel,
      log.message,
      formatDate(log.createdAt),
      log.details || ''
    ])

    // Create CSV content
    let csvContent = headers.join(',') + '\n'
    rows.forEach(row => {
      csvContent += row.map(cell => `"${cell}"`).join(',') + '\n'
    })

    // Create blob and download
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `logs_${new Date().getTime()}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    ElMessage.success('日志已导出')
  } catch (error) {
    ElMessage.error('导出失败: ' + error.message)
  }
}

// View log details
const handleViewDetails = (log) => {
  selectedLog.value = log
  showDetailsModal.value = true
}

// Close details modal
const closeDetailsModal = () => {
  showDetailsModal.value = false
  selectedLog.value = null
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN')
}

// Watch page changes
watch(() => currentPage.value, () => {
  loadLogs()
})

// Lifecycle
onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.admin-logs {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-container {
  display: flex;
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  gap: 2rem;
  padding: 2rem;
}

.logs-content {
  flex: 1;
}

.logs-content h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.logs-content h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #333;
}

/* Logs List Section */
.logs-list-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.filter-bar input,
.filter-bar select {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.filter-bar input {
  flex: 1;
  min-width: 150px;
}

.filter-bar select {
  min-width: 150px;
}

/* Loading */
.loading-indicator {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  gap: 1rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Table */
.logs-table {
  overflow-x: auto;
  margin-bottom: 1.5rem;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #f5f5f5;
}

th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #ddd;
}

td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

tbody tr:hover {
  background-color: #f9f9f9;
}

.message-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

/* Log Level Badge */
.log-level-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
}

.level-info {
  background-color: #d1ecf1;
  color: #0c5460;
}

.level-warn {
  background-color: #fff3cd;
  color: #856404;
}

.level-error {
  background-color: #f8d7da;
  color: #721c24;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
}

.page-info {
  color: #666;
  font-size: 0.875rem;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  max-width: 700px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 1.5rem;
}

.log-details-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-item label {
  font-weight: 600;
  color: #666;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
}

.detail-item p {
  color: #333;
  margin: 0;
  word-break: break-word;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.message-text {
  background-color: #f9f9f9;
  padding: 0.75rem;
  border-radius: 4px;
  border-left: 3px solid #667eea;
}

.details-text {
  background-color: #f9f9f9;
  padding: 0.75rem;
  border-radius: 4px;
  border-left: 3px solid #ffc107;
  max-height: 300px;
  overflow-y: auto;
  font-family: monospace;
  font-size: 0.875rem;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  padding: 1.5rem;
  border-top: 1px solid #eee;
}

/* Buttons */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #5568d3;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.btn-info {
  background-color: #17a2b8;
  color: white;
}

.btn-info:hover:not(:disabled) {
  background-color: #138496;
}

.btn-small {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

/* Responsive */
@media (max-width: 768px) {
  .admin-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .filter-bar {
    flex-direction: column;
  }

  .filter-bar input,
  .filter-bar select {
    width: 100%;
  }

  table {
    font-size: 0.875rem;
  }

  th,
  td {
    padding: 0.75rem 0.5rem;
  }

  .message-cell {
    max-width: 150px;
  }

  .actions {
    flex-direction: column;
  }

  .modal {
    width: 95%;
  }

  .logs-content h1 {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
  }
}

@media (max-width: 480px) {
  .filter-bar {
    flex-direction: column;
  }

  .filter-bar input,
  .filter-bar select {
    width: 100%;
  }

  .admin-container {
    padding: 0.5rem;
  }

  .logs-list-section {
    padding: 1rem;
  }

  table {
    font-size: 0.75rem;
  }

  th,
  td {
    padding: 0.5rem;
  }

  .message-cell {
    max-width: 100px;
  }
}
</style>
