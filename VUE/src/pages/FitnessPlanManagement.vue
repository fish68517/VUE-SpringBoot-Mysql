<template>
  <div class="fitness-plan-management-layout">
    <Sidebar />
    
    <div class="main-content">
      <Header />
      
      <div class="content-area">
        <div class="fitness-plan-management-container">
          <div class="page-header">
            <h2>健身计划管理</h2>
            <p>管理用户的健身计划</p>
          </div>

          <!-- Refresh Button -->
          <div class="action-section">
            <el-button @click="handleRefresh">
              <el-icon><Refresh /></el-icon>
              <span>刷新</span>
            </el-button>
          </div>

          <!-- Fitness Plan Table -->
          <div class="table-section">
            <el-table
              :data="fitnessPlanList"
              stripe
              style="width: 100%"
              :loading="loading"
              v-loading="loading"
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="userId" label="用户ID" width="100" />
              <el-table-column prop="goal" label="目标" min-width="150">
                <template #default="{ row }">
                  <div class="goal-preview">
                    {{ truncateContent(row.goal, 40) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="planContent" label="计划内容" min-width="200">
                <template #default="{ row }">
                  <div class="content-preview">
                    {{ truncateContent(row.planContent, 50) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" min-width="180">
                <template #default="{ row }">
                  {{ formatDate(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="280" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    size="small"
                    @click="handleViewDetails(row)"
                  >
                    查看详情
                  </el-button>
                  <el-button
                    type="warning"
                    size="small"
                    @click="handleEdit(row)"
                  >
                    编辑
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

    <!-- Plan Details Dialog -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="健身计划详情"
      width="700px"
      @close="resetDetailsDialog"
    >
      <div v-if="selectedPlan" class="plan-details">
        <div class="plan-section">
          <div class="plan-header">
            <span class="label">用户ID:</span>
            <span class="value">{{ selectedPlan.userId }}</span>
          </div>
          <div class="plan-header">
            <span class="label">创建时间:</span>
            <span class="value">{{ formatDate(selectedPlan.createdAt) }}</span>
          </div>
          <div class="plan-goal">
            <span class="label">目标:</span>
            <div class="goal-text">{{ selectedPlan.goal }}</div>
          </div>
          <div class="plan-content">
            <span class="label">计划内容:</span>
            <div class="content-text">{{ selectedPlan.planContent }}</div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- Edit Plan Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑健身计划"
      width="700px"
      @close="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editFormRules"
        label-width="100px"
      >
        <el-form-item label="目标" prop="goal">
          <el-input
            v-model="editForm.goal"
            type="textarea"
            :rows="3"
            placeholder="输入健身目标"
          />
        </el-form-item>
        <el-form-item label="计划内容" prop="planContent">
          <el-input
            v-model="editForm.planContent"
            type="textarea"
            :rows="5"
            placeholder="输入详细的计划内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="editLoading">
          保存
        </el-button>
      </template>
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
  getFitnessPlanList,
  getFitnessPlanById,
  updateFitnessPlan,
  deleteFitnessPlan
} from '../api/fitnessPlan.js'
import { showSuccess, showError } from '../utils/notification.js'

const fitnessPlanList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const detailsDialogVisible = ref(false)
const selectedPlan = ref(null)

const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  id: null,
  goal: '',
  planContent: ''
})

const editFormRules = {
  goal: [
    { required: true, message: '目标不能为空', trigger: 'blur' }
  ],
  planContent: [
    { required: true, message: '计划内容不能为空', trigger: 'blur' }
  ]
}

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
 * Load fitness plan list from API
 */
const loadFitnessPlanList = async () => {
  loading.value = true
  try {
    const response = await getFitnessPlanList(currentPage.value, pageSize.value)
    
    if (response.code === 200) {
      fitnessPlanList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      showError(response.message || '获取健身计划列表失败', '加载失败')
    }
  } catch (error) {
    console.error('Error loading fitness plan list:', error)
    showError('获取健身计划列表失败，请检查网络连接', '加载失败')
  } finally {
    loading.value = false
  }
}

/**
 * Handle refresh
 */
const handleRefresh = () => {
  currentPage.value = 1
  loadFitnessPlanList()
}

/**
 * Handle page change
 */
const handlePageChange = () => {
  loadFitnessPlanList()
}

/**
 * Handle page size change
 */
const handlePageSizeChange = () => {
  currentPage.value = 1
  loadFitnessPlanList()
}

/**
 * Handle view plan details
 */
const handleViewDetails = async (row) => {
  try {
    const response = await getFitnessPlanById(row.id)
    
    if (response.code === 200) {
      selectedPlan.value = response.data
      detailsDialogVisible.value = true
    } else {
      showError(response.message || '获取计划详情失败', '加载失败')
    }
  } catch (error) {
    console.error('Error loading plan details:', error)
    showError('获取计划详情失败，请检查网络连接', '加载失败')
  }
}

/**
 * Handle edit plan
 */
const handleEdit = (row) => {
  editForm.value = {
    id: row.id,
    goal: row.goal,
    planContent: row.planContent
  }
  editDialogVisible.value = true
}

/**
 * Handle save edit
 */
const handleSaveEdit = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
  } catch (error) {
    return
  }

  editLoading.value = true
  try {
    const response = await updateFitnessPlan(editForm.value.id, {
      goal: editForm.value.goal,
      planContent: editForm.value.planContent
    })

    if (response.code === 200) {
      showSuccess('健身计划更新成功', '更新成功')
      editDialogVisible.value = false
      loadFitnessPlanList()
    } else {
      showError(response.message || '更新健身计划失败', '更新失败')
    }
  } catch (error) {
    console.error('Error updating fitness plan:', error)
    showError('更新健身计划失败，请检查网络连接', '更新失败')
  } finally {
    editLoading.value = false
  }
}

/**
 * Handle delete plan
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除该健身计划吗？此操作不可撤销。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        const response = await deleteFitnessPlan(row.id)

        if (response.code === 200) {
          showSuccess('健身计划删除成功', '删除成功')
          loadFitnessPlanList()
        } else {
          showError(response.message || '删除健身计划失败', '删除失败')
        }
      } catch (error) {
        console.error('Error deleting fitness plan:', error)
        showError('删除健身计划失败，请检查网络连接', '删除失败')
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
  selectedPlan.value = null
}

/**
 * Reset edit form
 */
const resetEditForm = () => {
  editForm.value = {
    id: null,
    goal: '',
    planContent: ''
  }
  if (editFormRef.value) {
    editFormRef.value.clearValidate()
  }
}

/**
 * Load fitness plan list on component mount
 */
onMounted(() => {
  loadFitnessPlanList()
})
</script>

<style scoped>
.fitness-plan-management-layout {
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

.fitness-plan-management-container {
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

.goal-preview {
  color: #666;
  word-break: break-word;
}

.content-preview {
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

/* Plan Details Dialog Styles */
.plan-details {
  padding: 10px 0;
}

.plan-section {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
}

.plan-header {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
}

.plan-header .label {
  font-weight: 600;
  color: #333;
  min-width: 100px;
}

.plan-header .value {
  color: #666;
}

.plan-goal {
  margin-bottom: 15px;
}

.plan-goal .label {
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
}

.goal-text {
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 10px;
  color: #666;
  line-height: 1.6;
  word-break: break-word;
  max-height: 150px;
  overflow-y: auto;
}

.plan-content {
  margin-bottom: 0;
}

.plan-content .label {
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
}

.content-text {
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 10px;
  color: #666;
  line-height: 1.6;
  word-break: break-word;
  max-height: 300px;
  overflow-y: auto;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #e0e0e0;
  padding: 16px 20px;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #333;
}

:deep(.el-form-item__label) {
  font-weight: 500;
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
