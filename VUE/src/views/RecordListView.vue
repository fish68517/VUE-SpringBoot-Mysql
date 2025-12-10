<template>
  <div class="record-list-container">
    <div class="record-list-header">
      <h1>我的旅行记录</h1>
      <el-button type="primary" size="large" @click="handleCreateRecord">
        <el-icon><Plus /></el-icon>
        新建旅行记录
      </el-button>
    </div>

    <!-- 加载中 -->
    <SkeletonLoader v-if="loading" :count="6" type="card" />

    <!-- 空状态 -->
    <el-empty
      v-else-if="records.length === 0"
      description="你还没有创建任何旅行记录哦～"
      style="padding: 80px 0"
    >
      <el-button type="primary" size="large" @click="handleCreateRecord">
        去创建第一篇旅行记忆
      </el-button>
    </el-empty>

    <!-- 记录列表 -->
    <div v-else class="records-grid">
      <div
        v-for="record in records"
        :key="record.id"
        class="record-card"
        @click="handleViewRecord(record.id)"
      >
        <div class="record-card-header">
          <h3 class="title">{{ record.title }}</h3>
          <el-tag
            :type="record.isPublic ? 'success' : 'info'"
            size="large"
            effect="light"
          >
            {{ record.isPublic ? '公开' : '私密' }}
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
          <p class="description" v-if="record.description">
            {{ truncateText(record.description, 90) }}
          </p>
          <p class="no-desc" v-else>暂无描述，点击查看详情～</p>
        </div>

        <div class="record-card-footer">
          <span class="created-date">
            创建于 {{ formatDate(record.createdAt) }}
          </span>
          <el-button type="primary" text size="small" @click.stop="handleViewRecord(record.id)">
            查看详情
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[6, 12, 24]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      closable
      @close="errorMessage = ''"
      style="margin-top: 24px"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Location, Calendar } from '@element-plus/icons-vue'
import { travelService } from '../services/travelService'
import { useTravelStore } from '../stores/travelStore'
import { showError } from '../utils/notificationUtils'
import SkeletonLoader from '../components/SkeletonLoader.vue'

const router = useRouter()
const travelStore = useTravelStore()

const records = ref([])
const loading = ref(true)
const errorMessage = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

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

    if (response?.data) {
      records.value = response.data.content || []
      total.value = response.data.totalElements || 0
      travelStore.setRecords(records.value)
    }
  } catch (err) {
    errorMessage.value = '加载旅行记录失败，请刷新重试'
    showError(errorMessage.value)
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => fetchRecords()
const handlePageSizeChange = () => {
  currentPage.value = 1
  fetchRecords()
}

const handleViewRecord = (id) => router.push(`/records/${id}`)
const handleCreateRecord = () => router.push('/records/create')

// 中文友好日期格式
const formatDate = (dateStr) => {
  if (!dateStr) return '未知时间'
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric'
  })
}

const formatDateRange = (start, end) => {
  if (!start || !end) return '日期未知'
  const s = new Date(start).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
  const e = new Date(end).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', year: 'numeric' })
  return `${s} - ${e}`
}

const truncateText = (text, len) => {
  return text && text.length > len ? text.substring(0, len) + '...' : text || ''
}
</script>

<style scoped>
.record-list-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 30px 20px;
}

.record-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 36px;
  flex-wrap: wrap;
  gap: 16px;
}

.record-list-header h1 {
  margin: 0;
  font-size: 28px;
  color: #303133;
  font-weight: 600;
}

.records-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.record-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.record-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.15);
}

.record-card-header {
  padding: 20px 20px 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  background: linear-gradient(120deg, #f8f9ff 0%, #f0f4ff 100%);
}

.title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  flex: 1;
}

.record-card-body {
  padding: 0 20px 16px;
  flex: 1;
}

.record-card-body p {
  margin: 12px 0;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.destination {
  color: #409eff;
  font-weight: 500;
  font-size: 15px !important;
}

.dates {
  color: #67c23a;
}

.description {
  color: #909399;
  line-height: 1.7;
  margin-top: 16px;
}

.no-desc {
  color: #c0c4cc;
  font-style: italic;
  margin-top: 16px;
}

.record-card-footer {
  padding: 16px 20px;
  border-top: 1px dashed #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
}

.created-date {
  font-size: 13px;
  color: #999;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .record-list-header {
    flex-direction: column;
    align-items: stretch;
  }
  .records-grid {
    grid-template-columns: 1fr;
  }
}
</style>