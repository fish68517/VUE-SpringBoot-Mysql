<template>
  <div class="tasks">
    <h1>审稿任务</h1>
    <el-tabs>
      <el-tab-pane label="待审稿">
        <el-table :data="pendingTasks" stripe v-loading="loading">
          <el-table-column prop="id" label="任务ID" width="100" />
          <el-table-column prop="manuscriptId" label="稿件ID" width="100" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="viewManuscript(row)">查看稿件</el-button>
              <el-button link type="success" size="small" @click="acceptTask(row)">接受</el-button>
              <el-button link type="danger" size="small" @click="rejectTask(row)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="进行中">
        <el-table :data="acceptedTasks" stripe v-loading="loading">
          <el-table-column prop="id" label="任务ID" width="100" />
          <el-table-column prop="manuscriptId" label="稿件ID" width="100" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="viewManuscript(row)">查看稿件</el-button>
              <el-button link type="success" size="small" @click="submitReview(row)">提交审稿意见</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已完成">
        <el-table :data="completedTasks" stripe v-loading="loading">
          <el-table-column prop="id" label="任务ID" width="100" />
          <el-table-column prop="manuscriptId" label="稿件ID" width="100" />
          <el-table-column prop="recommendation" label="建议" width="120" />
          <el-table-column prop="score" label="评分" width="80" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="viewReview(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Manuscript View Dialog -->
    <el-dialog v-model="manuscriptDialogVisible" title="查看稿件" width="80%">
      <div v-if="currentManuscript" class="manuscript-view">
        <div class="manuscript-info">
          <p><strong>标题：</strong> {{ currentManuscript.title }}</p>
          <p><strong>作者：</strong> {{ currentManuscript.authorName }}</p>
          <p><strong>摘要：</strong> {{ currentManuscript.abstractText }}</p>
        </div>
        <div class="manuscript-content">
          <strong>内容：</strong>
          <div class="content-box">{{ currentManuscript.content }}</div>
        </div>
      </div>
    </el-dialog>

    <!-- Review Opinion Dialog -->
    <el-dialog v-model="reviewDialogVisible" title="提交审稿意见" width="60%">
      <el-form :model="reviewForm" label-width="100px">
        <el-form-item label="审稿意见">
          <el-input v-model="reviewForm.opinion" type="textarea" rows="6" placeholder="请输入审稿意见" />
        </el-form-item>
        <el-form-item label="评分">
          <el-input-number v-model="reviewForm.score" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="建议">
          <el-select v-model="reviewForm.recommendation" placeholder="请选择建议">
            <el-option label="同意录用" value="ACCEPT" />
            <el-option label="修改后录用" value="ACCEPT_WITH_REVISION" />
            <el-option label="拒绝录用" value="REJECT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReviewOpinion">提交</el-button>
      </template>
    </el-dialog>

    <!-- Review Details Dialog -->
    <el-dialog v-model="reviewDetailsDialogVisible" title="审稿意见详情" width="60%">
      <div v-if="currentReview" class="review-details">
        <p><strong>审稿意见：</strong> {{ currentReview.opinion }}</p>
        <p><strong>评分：</strong> {{ currentReview.score }}</p>
        <p><strong>建议：</strong> {{ currentReview.recommendation }}</p>
        <p><strong>提交时间：</strong> {{ formatDate(currentReview.submittedDate) }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const loading = ref(false)
const pendingTasks = ref([])
const acceptedTasks = ref([])
const completedTasks = ref([])

const manuscriptDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const reviewDetailsDialogVisible = ref(false)

const currentManuscript = ref(null)
const currentReview = ref(null)
const currentTask = ref(null)

const reviewForm = ref({
  opinion: '',
  score: 0,
  recommendation: ''
})

// Load review tasks on component mount
onMounted(() => {
  loadReviewTasks()
  loadReviewHistory()
})

// Load pending and accepted review tasks
const loadReviewTasks = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/api/reviewers/tasks')
    if (response.data.code === 200) {
      const tasks = response.data.data || []
      pendingTasks.value = tasks.filter(t => t.status === 'PENDING')
      acceptedTasks.value = tasks.filter(t => t.status === 'ACCEPTED')
    }
  } catch (error) {
    ElMessage.error('加载审稿任务失败')
  } finally {
    loading.value = false
  }
}

// Load review history
const loadReviewHistory = async () => {
  try {
    const response = await axios.get('/api/api/reviewers/history')
    if (response.data.code === 200) {
      completedTasks.value = response.data.data || []
    }
  } catch (error) {
    ElMessage.error('加载审稿历史失败')
  }
}

// View manuscript content
const viewManuscript = async (task) => {
  try {
    const response = await axios.get(`/api/api/reviewers/manuscripts/${task.manuscriptId}`)
    if (response.data.code === 200) {
      currentManuscript.value = response.data.data
      manuscriptDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载稿件失败')
  }
}

// Accept review task
const acceptTask = async (task) => {
  try {
    const response = await axios.post(`/api/api/reviewers/tasks/${task.id}/accept`)
    if (response.data.code === 200) {
      ElMessage.success('已接受审稿任务')
      loadReviewTasks()
    }
  } catch (error) {
    ElMessage.error('接受任务失败')
  }
}

// Reject review task
const rejectTask = async (task) => {
  try {
    const response = await axios.post(`/api/api/reviewers/tasks/${task.id}/reject`)
    if (response.data.code === 200) {
      ElMessage.success('已拒绝审稿任务')
      loadReviewTasks()
    }
  } catch (error) {
    ElMessage.error('拒绝任务失败')
  }
}

// Open review opinion submission dialog
const submitReview = (task) => {
  currentTask.value = task
  reviewForm.value = {
    opinion: '',
    score: 0,
    recommendation: ''
  }
  reviewDialogVisible.value = true
}

// Submit review opinion
const submitReviewOpinion = async () => {
  if (!reviewForm.value.opinion || !reviewForm.value.recommendation) {
    ElMessage.warning('请填写审稿意见和建议')
    return
  }

  try {
    const response = await axios.post(`/api/api/reviewers/reviews/${currentTask.value.id}/submit`, {
      opinion: reviewForm.value.opinion,
      score: reviewForm.value.score,
      recommendation: reviewForm.value.recommendation
    })
    if (response.data.code === 200) {
      ElMessage.success('审稿意见已提交')
      reviewDialogVisible.value = false
      loadReviewTasks()
      loadReviewHistory()
    }
  } catch (error) {
    ElMessage.error('提交审稿意见失败')
  }
}

// View review details
const viewReview = async (task) => {
  try {
    const response = await axios.get(`/api/api/reviewers/reviews/${task.id}`)
    if (response.data.code === 200) {
      currentReview.value = response.data.data
      reviewDetailsDialogVisible.value = true
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
.tasks {
  padding: 20px;
}

.tasks h1 {
  margin-bottom: 20px;
  color: #333;
}

.manuscript-view {
  padding: 20px;
}

.manuscript-info {
  margin-bottom: 20px;
}

.manuscript-info p {
  margin: 10px 0;
  line-height: 1.6;
}

.manuscript-content {
  margin-top: 20px;
}

.content-box {
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 4px;
  background-color: #f9f9f9;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.review-details {
  padding: 20px;
}

.review-details p {
  margin: 15px 0;
  line-height: 1.6;
}
</style>
