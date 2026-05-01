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
         <el-table-column label="投稿日期" width="150">
          <template #default="{ row }">
            {{ formatDate(row.submissionDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="reviewerCount" label="分配审稿人数" width="120" />
        <el-table-column prop="submittedCount" label="已提交意见数" width="120" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewProgress(row)">查看进度</el-button>
            <el-button
              link
              type="success"
              :disabled="row.submittedCount === 0"
              @click="handleFinalReview(row)"
            >
              终审
            </el-button>
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
             <el-table-column label="投稿日期" width="150">
          <template #default="{ row }">
            {{ formatDate(row.submissionDate) }}
          </template>
        </el-table-column>
          </el-descriptions>

          <h4 style="margin-bottom: 10px">审稿人信息与进度</h4>
          <el-table :data="reviewProgress" stripe style="width: 100%; margin-bottom: 20px">
            <el-table-column prop="reviewerName" label="审稿人" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
           

               <el-table-column label="分配时间" width="150">
                <template #default="{ row }">
                  {{ formatDate(row.createdAt) }}
                </template>
              </el-table-column>
            

              <el-table-column label="提交时间" width="150">
                <template #default="{ row }">
                  {{ formatDate(row.submittedDate) }}
                </template>
              </el-table-column>

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

      <el-dialog v-model="finalReviewDialogVisible" title="提交终审结果" width="600px">
        <el-form :model="finalReviewForm" label-width="100px">
          <el-form-item label="稿件标题">
            <el-input :model-value="selectedManuscript?.title" disabled />
          </el-form-item>
          <el-form-item label="终审结果" required>
            <el-select v-model="finalReviewForm.finalStatus" placeholder="请选择终审结果" style="width: 100%">
              <el-option label="录用" value="ACCEPTED" />
              <el-option label="退修" value="REVISION_REQUIRED" />
              <el-option label="拒稿" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item label="终审意见">
            <el-input
              v-model="finalReviewForm.opinion"
              type="textarea"
              :rows="5"
              placeholder="请输入给作者的终审意见"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="finalReviewDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submittingFinalReview" @click="submitFinalReview">提交终审</el-button>
        </template>
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
const finalReviewDialogVisible = ref(false)
const selectedManuscript = ref(null)
const selectedReview = ref(null)
const reviewProgress = ref([])
const submittingFinalReview = ref(false)
const finalReviewForm = ref({
  finalStatus: '',
  opinion: ''
})

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
    const response = await manuscriptService.getAllManuscripts()
    if (response.data) {
      const underReviewManuscripts = response.data.filter(item => item.status === 'UNDER_REVIEW')
      const manuscriptsWithDetails = await Promise.all(
        underReviewManuscripts.map(async (item) => {
          try {
            const detail = await manuscriptService.getManuscriptDetail(item.id)
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
              id: item.id,
              title: item.title || '未知稿件',
              authorName: item.authorName || '未知作者',
              submissionDate: item.submissionDate,
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

const handleFinalReview = (manuscript) => {
  selectedManuscript.value = manuscript
  finalReviewForm.value = {
    finalStatus: '',
    opinion: ''
  }
  finalReviewDialogVisible.value = true
}

const submitFinalReview = async () => {
  if (!finalReviewForm.value.finalStatus) {
    ElMessage.warning('请选择终审结果')
    return
  }

  submittingFinalReview.value = true
  try {
    await editorService.submitFinalReview(
      selectedManuscript.value.id,
      finalReviewForm.value.finalStatus,
      finalReviewForm.value.opinion
    )
    ElMessage.success('终审结果已提交')
    finalReviewDialogVisible.value = false
    progressDialogVisible.value = false
    await loadManuscriptsUnderReview()
  } catch (error) {
    ElMessage.error(error.message || '提交终审失败')
  } finally {
    submittingFinalReview.value = false
  }
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
