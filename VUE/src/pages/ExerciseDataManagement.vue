<template>
  <div class="exercise-data-management-layout">
    <Sidebar />
    
    <div class="main-content">
      <Header />
      
      <div class="content-area">
        <div class="exercise-data-management-container">
          <div class="page-header">
            <h2>运动数据管理</h2>
            <p>管理用户的运动记录</p>
          </div>

          <!-- Refresh Button -->
          <div class="action-section">
            <el-button @click="handleRefresh">
              <el-icon><Refresh /></el-icon>
              <span>刷新</span>
            </el-button>
          </div>

          <!-- Exercise Data Table -->
          <div class="table-section">
            <el-table
              :data="exerciseDataList"
              stripe
              style="width: 100%"
              :loading="loading"
              v-loading="loading"
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="userId" label="用户ID" width="100" />
              <el-table-column prop="exerciseType" label="运动方式" min-width="120" />
              <el-table-column prop="location" label="运动地点" min-width="150">
                <template #default="{ row }">
                  <div class="location-preview">
                    {{ truncateContent(row.location, 40) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="duration" label="运动时长(分钟)" width="130" />
              <el-table-column prop="createdAt" label="记录时间" min-width="180">
                <template #default="{ row }">
                  {{ formatDate(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="250" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    size="small"
                    @click="handleViewDetails(row)"
                  >
                    查看详情
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    @click="handleDelete(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- Pagination -->
          <div class="pagination-section">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handlePageSizeChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Exercise Data Details Dialog -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="运动数据详情"
      width="600px"
      @close="resetDetailsDialog"
    >
      <div v-if="selectedExerciseData" class="exercise-data-details">
        <div class="data-section">
          <div class="data-header">
            <span class="label">用户ID:</span>
            <span class="value">{{ selectedExerciseData.userId }}</span>
          </div>
          <div class="data-header">
            <span class="label">运动方式:</span>
            <span class="value">{{ selectedExerciseData.exerciseType }}</span>
          </div>
          <div class="data-header">
            <span class="label">运动地点:</span>
            <span class="value">{{ selectedExerciseData.location }}</span>
          </div>
          <div class="data-header">
            <span class="label">运动时长:</span>
            <span class="value">{{ selectedExerciseData.duration }} 分钟</span>
          </div>
          <div class="data-header">
            <span class="label">记录时间:</span>
            <span class="value">{{ formatDate(selectedExerciseData.createdAt) }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import Sidebar from '../components/Sidebar.vue'
import Header from '../components/Header.vue'
import {
  getExerciseDataList,
  getExerciseDataById,
  deleteExerciseData
} from '../api/exerciseData.js'
import { showSuccess, showError } from '../utils/notification.js'

const exerciseDataList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const detailsDialogVisible = ref(false)
const selectedExerciseData = ref(null)

/**
 * Format date to readable string
 */
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

/**
 * Truncate content to specified length
 */
const truncateContent = (content, length) => {
  if (!content) return '-'
  return content.length > length ? content.substring(0, length) + '...' : content
}

/**
 * Load exercise data list from API
 */
const loadExerciseDataList = async () => {
  loading.value = true
  try {
    const response = await getExerciseDataList(currentPage.value, pageSize.value)
    
    if (response.code === 200) {
      exerciseDataList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      showError(response.message || '获取运动数据列表失败', '加载失败')
    }
  } catch (error) {
    console.error('Error loading exercise data list:', error)
    showError('获取运动数据列表失败，请检查网络连接', '加载失败')
  } finally {
    loading.value = false
  }
}

/**
 * Handle refresh
 */
const handleRefresh = () => {
  currentPage.value = 1
  loadExerciseDataList()
}

/**
 * Handle page change
 */
const handlePageChange = () => {
  loadExerciseDataList()
}

/**
 * Handle page size change
 */
const handlePageSizeChange = () => {
  currentPage.value = 1
  loadExerciseDataList()
}

/**
 * Handle view exercise data details
 */
const handleViewDetails = async (row) => {
  try {
    const response = await getExerciseDataById(row.id)
    
    if (response.code === 200) {
      selectedExerciseData.value = response.data
      detailsDialogVisible.value = true
    } else {
      showError(response.message || '获取运动数据详情失败', '加载失败')
    }
  } catch (error) {
    console.error('Error loading exercise data details:', error)
    showError('获取运动数据详情失败，请检查网络连接', '加载失败')
  }
}

/**
 * Handle delete exercise data
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除该运动记录吗？此操作不可撤销。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        const response = await deleteExerciseData(row.id)

        if (response.code === 200) {
          showSuccess('运动数据删除成功', '删除成功')
          loadExerciseDataList()
        } else {
          showError(response.message || '删除运动数据失败', '删除失败')
        }
      } catch (error) {
        console.error('Error deleting exercise data:', error)
        showError('删除运动数据失败，请检查网络连接', '删除失败')
      }
    })
    .catch(() => {
      // User cancelled the deletion
    })
}

/**
 * Reset details dialog
 */
const resetDetailsDialog = () => {
  selectedExerciseData.value = null
}

/**
 * Load exercise data list on component mount
 */
onMounted(() => {
  loadExerciseDataList()
})
</script>

<style scoped>
.exercise-data-management-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.exercise-data-management-container {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 5px 0;
  letter-spacing: -0.5px;
}

.page-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.action-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.1s both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.table-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.2s both;
}

.table-section :deep(.el-table) {
  font-size: 14px;
  border-radius: 8px;
}

.table-section :deep(.el-table__header th) {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #333;
}

.table-section :deep(.el-table__body tr:hover > td) {
  background-color: #f9f9f9;
}

.location-preview {
  color: #666;
  word-break: break-word;
}

.pagination-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  display: flex;
  justify-content: flex-end;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.3s both;
}

.pagination-section :deep(.el-pagination) {
  display: flex;
  gap: 10px;
}

/* Exercise Data Details Dialog Styles */
.exercise-data-details {
  padding: 10px 0;
}

.data-section {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
}

.data-header {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}

.data-header .label {
  font-weight: 600;
  color: #333;
  min-width: 100px;
}

.data-header .value {
  color: #666;
  flex: 1;
  word-break: break-word;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #e0e0e0;
  padding: 16px 20px;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #333;
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}
</style>
