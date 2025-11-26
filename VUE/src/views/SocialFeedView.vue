<template>
  <div class="social-feed-container">
    <div class="feed-header">
      <h1>Social Feed</h1>
      <p class="feed-subtitle">Discover travel stories from the community</p>
    </div>

    <!-- Loading state with skeleton -->
    <SkeletonLoader v-if="loading" :count="5" type="card" />

    <!-- Empty state -->
    <el-empty
      v-else-if="records.length === 0"
      description="No public travel records yet"
      style="margin-top: 40px"
    />

    <!-- Feed cards -->
    <div v-else class="feed-list">
      <FeedCard
        v-for="record in records"
        :key="record.id"
        :record="record"
      />
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
import { ref, onMounted } from 'vue'
import FeedCard from '../components/FeedCard.vue'
import SkeletonLoader from '../components/SkeletonLoader.vue'
import { travelService } from '../services/travelService'
import { showError } from '../utils/notificationUtils'

const records = ref([])
const loading = ref(false)
const errorMessage = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// Fetch records on component mount
onMounted(() => {
  fetchPublicRecords()
})

const fetchPublicRecords = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    const response = await travelService.getPublicTravelRecords(
      currentPage.value - 1,
      pageSize.value
    )

    // Handle the ApiResponse structure
    if (response && response.data) {
      const pageData = response.data
      records.value = pageData.content || []
      total.value = pageData.totalElements || 0
    }
  } catch (error) {
    const errorMsg = error.message || 'Failed to load public travel records'
    errorMessage.value = errorMsg
    showError(errorMsg)
    console.error('Error fetching public records:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => {
  fetchPublicRecords()
}

const handlePageSizeChange = () => {
  currentPage.value = 1
  fetchPublicRecords()
}
</script>

<style scoped>
.social-feed-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.feed-header {
  margin-bottom: 30px;
  text-align: center;
}

.feed-header h1 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 32px;
  font-weight: 600;
}

.feed-subtitle {
  margin: 0;
  color: #999;
  font-size: 16px;
}

.feed-list {
  margin-bottom: 30px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .social-feed-container {
    padding: 12px;
  }

  .feed-header h1 {
    font-size: 24px;
  }

  .feed-subtitle {
    font-size: 14px;
  }
}
</style>
