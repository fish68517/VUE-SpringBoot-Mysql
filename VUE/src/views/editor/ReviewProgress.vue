<template>
  <div class="review-progress-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>审稿进度跟踪</span>
        </div>
      </template>

      <!-- Manuscripts for Review Progress -->
      <el-table :data="manuscriptsUnderReview" stripe style="width: 100%; margin-bottom: 20px">
        <el-table-column prop="id" label="稿件ID" width="80" />
        <el-table-column prop="title" label="稿件标题" min-width="200" />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="submissionDate" label="投稿日期" width="150" :formatter="formatDate" />
        <el-table-column prop="reviewerCount" label="分配审稿人数" width="120" />
        <el-table-column prop="submittedCount" label="已提交意见数" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewProgress(row)">查看进度</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Review Progress Detail Dialog -->
      <el-dialog v-model="progressDialogVisible" title="审稿进度详情" width="800px">
        <div v-if="selectedManuscript" class="progress-detail">
          <el-descriptions :column="2" border size="small" style="margin-bottom: 20px">
            <el-descriptions-item label="稿件ID">{{ selectedManuscript.id }}</el-descriptions-item>
            <el-descriptions-item label="稿件标题">{{ selectedManuscript.title }}</el-descriptions-item>
            <el-descriptions-item label="作者">{{ selectedManuscript.authorName }}</el-descriptions-item>
            <el-descriptions-item label="投稿日期">{{ formatDate(selectedManuscript.submissionDate) }}</el-descriptions-item>
          </el-descriptions>

          <h4 style="margin-bottom: 10px">审稿人信息与进度</h4>
          <el-table :data="reviewProgress" stripe style="width: 100%; margin-bottom: 20px">
            <el-table-column prop="reviewerName" label="审稿人" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="分配时间" width="150" :formatter="formatDate" />
            <el-table-column prop="submittedDate" label="提交时间" width="150" :formatter="formatDate" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button 
                  v-if="row.status === 'SUBMITTED'" 
                  link 
                  type="primary" 
                  @click="handleViewOpinion(row)"
                >
                  查看意见
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-dialog>

      <!-- Review Opinion Detail Dialog -->
      <el-dialog v-model="opinionDialogVisible" title="审稿意见详情" width="700px">
        <div v-if="selectedReview" class="opinion-detail">
          <el-descriptions :column="1" border size="small" style="margin-bottom: 20px">
            <el-descriptions-item label="审稿人">{{ selectedReview.reviewerName }}</el-descriptions-item>
            <el-descriptions-item label="评分">{{ selectedReview.score }}</el-descriptions-item>
            <el-descriptions-item label="建议">
              <el-tag :type="getRecommendationType(selectedReview.recommendation)">
                {{ getRecommendationLabel(selectedReview.recommendation) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatDate(selectedReview.submittedDate) }}</el-descriptions-item>
          </el-descriptions>

          <h4 style="margin-bottom: 10px">审稿意见</h4>
          <el-input
            v-model="selectedReview.opinion"
            type="textarea"
            :rows="8"
            disabled
            style="margin-bottom: 20px"
          />
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { editorService } from '../../services/editorService'
import { manuscriptService } from '../../services/manuscriptService'

const manuscriptsUnderReview = ref([])
const progressDialogVisible = ref(false)
const opinionDialogVisible = ref(false)
const selectedManuscript = ref(null)
const selectedReview = ref(null)
const reviewProgress = ref([])

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

const getStatusType = (status) => {
  switch (status) {
    case 'PENDING':
      return 'info'
    case 'ACCEPTED':
      return 'warning'
    case 'REJECTED':
      return 'danger'
    case 'SUBMITTED':
      return 'success'
    default:
      return ''
  }
}

const getStatusLabel = (status) => {
  switch (status) {
    case 'PENDING':
      return '待审稿'
    case 'ACCEPTED':
      return '已接受'
    case 'REJECTED':
      return '已拒绝'
    case 'SUBMITTED':
      return '已提交'
    default:
      return status
  }
}

const getRecommendationType = (recommendation) => {
  switch (recommendation) {
    case 'ACCEPT':
      return 'success'
    case 'ACCEPT_WITH_REVISION':
      return 'warning'
    case 'REJECT':
      return 'danger'
    default:
      return ''
  }
}

const getRecommendationLabel = (recommendation) => {
  switch (recommendation) {
    case 'ACCEPT':
      return '同意录用'
    case 'ACCEPT_WITH_REVISION':
      return '修改后录用'
    case 'REJECT':
      return '拒绝录用'
    default:
      return recommendation
  }
}

const loadManuscriptsUnderReview = async () => {
  try {
    const response = await editorService.getPendingManuscripts()
    if (response.data) {
      // Fetch full manuscript details for each manuscript
      const manuscriptsWithDetails = await Promise.all(
        response.data.map(async (item) => {
          try {
            const detail = await manuscriptService.getManuscriptDetail(item.manuscriptId)
            const manuscript = detail.data.manuscript
            
            // Get review progress to count reviewers and submitted reviews
            const progressResponse = await editorService.getReviewProgress(manuscript.id)
            const reviews = progressResponse.data || []
            const submittedCount = reviews.filter(r => r.status === 'SUBMITTED').length
            
            return {
              id: manuscript.id,
              title: manuscript.title,
              authorName: manuscript.authorName || 'Unknown',
              submissionDate: manuscript.submissionDate,
              reviewerCount: reviews.length,
              submittedCount: submittedCount
            }
          } catch (error) {
            return {
              id: item.manuscriptId,
              title: 'Unknown',
              authorName: 'Unknown',
              submissionDate: null,
              reviewerCount: 0,
              submittedCount: 0
            }
          }
        })
      )
      manuscriptsUnderReview.value = manuscriptsWithDetails
    }
  } catch (error) {
    ElMessage.error('加载稿件列表失败')
  }
}

const handleViewProgress = async (manuscript) => {
  try {
    selectedManuscript.value = manuscript
    const response = await editorService.getReviewProgress(manuscript.id)
    reviewProgress.value = response.data || []
    progressDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载审稿进度失败')
  }
}

const handleViewOpinion = (review) => {
  selectedReview.value = review
  opinionDialogVisible.value = true
}

onMounted(() => {
  loadManuscriptsUnderReview()
})
</script>

<style scoped>
.review-progress-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-detail {
  padding: 10px 0;
}

.opinion-detail {
  padding: 10px 0;
}

h4 {
  color: #333;
  font-weight: 600;
}
</style>
