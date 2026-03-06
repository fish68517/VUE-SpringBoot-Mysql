<template>
  <div class="history">
    <h1>审稿历史记录</h1>
    
    <el-table :data="historyList" stripe v-loading="loading">
      <el-table-column prop="id" label="审稿ID" width="100" />
      <el-table-column prop="manuscriptId" label="稿件ID" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag type="success">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="recommendation" label="建议" width="120" />
      <el-table-column prop="score" label="评分" width="80" />
      <el-table-column prop="submittedDate" label="提交时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.submittedDate) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="viewDetails(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Review Details Dialog -->
    <el-dialog v-model="detailsDialogVisible" title="审稿意见详情" width="70%">
      <div v-if="currentReview" class="review-details">
        <div class="detail-item">
          <strong>审稿ID：</strong> {{ currentReview.id }}
        </div>
        <div class="detail-item">
          <strong>稿件ID：</strong> {{ currentReview.manuscriptId }}
        </div>
        <div class="detail-item">
          <strong>状态：</strong> {{ currentReview.status }}
        </div>
        <div class="detail-item">
          <strong>评分：</strong> {{ currentReview.score }}
        </div>
        <div class="detail-item">
          <strong>建议：</strong> {{ currentReview.recommendation }}
        </div>
        <div class="detail-item">
          <strong>提交时间：</strong> {{ formatDate(currentReview.submittedDate) }}
        </div>
        <div class="detail-item">
          <strong>审稿意见：</strong>
          <div class="opinion-box">{{ currentReview.opinion }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const loading = ref(false)
const historyList = ref([])
const detailsDialogVisible = ref(false)
const currentReview = ref(null)

// Load review history on component mount
onMounted(() => {
  loadReviewHistory()
})

// Load review history
const loadReviewHistory = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/reviewers/history')
    if (response.data.code === 200) {
      historyList.value = response.data.data || []
    }
  } catch (error) {
    ElMessage.error('加载审稿历史失败')
  } finally {
    loading.value = false
  }
}

// View review details
const viewDetails = async (review) => {
  try {
    const response = await axios.get(`/api/reviewers/reviews/${review.id}`)
    if (response.data.code === 200) {
      currentReview.value = response.data.data
      detailsDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载审稿意见失败')
  }
}

// Format date
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style scoped>
.history {
  padding: 20px;
}

.history h1 {
  margin-bottom: 20px;
  color: #333;
}

.review-details {
  padding: 20px;
}

.detail-item {
  margin: 15px 0;
  line-height: 1.6;
}

.detail-item strong {
  display: inline-block;
  width: 100px;
  color: #333;
}

.opinion-box {
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 4px;
  background-color: #f9f9f9;
  margin-top: 10px;
  max-height: 300px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>
