<template>
  <div class="record-list-container">
    <div class="record-list-header">
      <h1>My Travel Records</h1>
      <el-button type="primary" @click="handleCreateRecord">
        <el-icon><Plus /></el-icon>
        Create New Record
      </el-button>
    </div>

    <!-- Loading state with skeleton -->
    <SkeletonLoader v-if="loading" :count="5" type="card" />

    <!-- Empty state -->
    <el-empty
      v-else-if="records.length === 0"
      description="No travel records yet"
      style="margin-top: 40px"
    >
      <el-button type="primary" @click="handleCreateRecord">Create First Record</el-button>
    </el-empty>

    <!-- Records list -->
    <div v-else class="records-grid">
      <div
        v-for="record in records"
        :key="record.id"
        class="record-card"
        @click="handleViewRecord(record.id)"
      >
        <div class="record-card-header">
          <h3>{{ record.title }}</h3>
          <el-tag
            :type="record.isPublic ? 'success' : 'info'"
            size="small"
          >
            {{ record.isPublic ? 'Public' : 'Private' }}
          </el-tag>
        </div>

        <div class="record-card-body">
          <p class="destination">
            <el-icon><Location /></el-icon>
            {{ record.destination }}
          </p>
          <p class="dates">
            <el-icon><Calendar /></el-icon>
            {{ formatDateRange(record.startDate, record.endDate) }}
          </p>
          <p class="description">{{ truncateText(record.description, 100) }}</p>
        </div>

        <div class="record-card-footer">
          <span class="created-date">{{ formatDate(record.createdAt) }}</span>
          <el-button
            type="primary"
            text
            size="small"
            @click.stop="handleViewRecord(record.id)"
          >
            View Details
          </el-button>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="records.length > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </div>

    <!-- Error message -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      closable
      @close="errorMessage = ''"
      style="margin-top: 20px"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Location, Calendar } from '@element-plus/icons-vue'
import { travelService } from '../services/travelService'
import { useTravelStore } from '../stores/travelStore'
import { showError } from '../utils/notificationUtils'
import SkeletonLoader from '../components/SkeletonLoader.vue'

const router = useRouter()
const travelStore = useTravelStore()

const records = ref([])
const loading = ref(false)
const errorMessage = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// Fetch records on component mount
onMounted(() => {
  fetchRecords()
})

const fetchRecords = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    const response = await travelService.getUserTravelRecords(
      currentPage.value - 1,
      pageSize.value
    )

    // Handle the ApiResponse structure
    if (response && response.data) {
      const pageData = response.data
      records.value = pageData.content || []
      total.value = pageData.totalElements || 0
      travelStore.setRecords(pageData.content || [])
    }
  } catch (error) {
    const errorMsg = error.message || 'Failed to load travel records'
    errorMessage.value = errorMsg
    showError(errorMsg)
    console.error('Error fetching records:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => {
  fetchRecords()
}

const handlePageSizeChange = () => {
  currentPage.value = 1
  fetchRecords()
}

const handleViewRecord = (recordId) => {
  router.push(`/records/${recordId}`)
}

const handleCreateRecord = () => {
  router.push('/records/create')
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const formatDateRange = (startDate, endDate) => {
  if (!startDate || !endDate) return ''
  const start = new Date(startDate).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric'
  })
  const end = new Date(endDate).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
  return `${start} - ${end}`
}

const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}
</script>

<style scoped>
.record-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.record-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.record-list-header h1 {
  margin: 0;
  color: #333;
  font-size: 28px;
}

.records-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.record-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.record-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.record-card-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
}

.record-card-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
  flex: 1;
  word-break: break-word;
}

.record-card-body {
  padding: 16px;
  flex: 1;
}

.record-card-body p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.destination {
  color: #409eff;
  font-weight: 500;
}

.dates {
  color: #666;
}

.description {
  color: #999;
  line-height: 1.5;
  max-height: 60px;
  overflow: hidden;
}

.record-card-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.created-date {
  font-size: 12px;
  color: #999;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .record-list-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .records-grid {
    grid-template-columns: 1fr;
  }
}
</style>
