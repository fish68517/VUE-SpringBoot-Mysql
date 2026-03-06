<template>
  <div class="manuscript-detail">
    <el-button @click="goBack" style="margin-bottom: 20px">返回</el-button>

    <div v-loading="loading" class="detail-container">
      <!-- Manuscript Information -->
      <el-card class="manuscript-card" v-if="manuscript">
        <template #header>
          <div class="card-header">
            <span class="title">稿件信息</span>
            <el-tag :type="getStatusType(manuscript.status)">
              {{ getStatusLabel(manuscript.status) }}
            </el-tag>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="稿件ID">
            {{ manuscript.id }}
          </el-descriptions-item>
          <el-descriptions-item label="标题">
            {{ manuscript.title }}
          </el-descriptions-item>
          <el-descriptions-item label="投稿日期">
            {{ formatDate(manuscript.submissionDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(manuscript.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="摘要" :span="2">
            {{ manuscript.abstractText }}
          </el-descriptions-item>
          <el-descriptions-item label="内容" :span="2" v-if="manuscript.content">
            <div class="content-preview">
              {{ manuscript.content.substring(0, 200) }}...
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="文件" :span="2" v-if="manuscript.filePath">
            <el-link type="primary" :href="`/image/${manuscript.filePath}`" target="_blank">
              下载文件
            </el-link>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- Initial Review -->
      <el-card class="review-card" v-if="initialReview">
        <template #header>
          <div class="card-header">
            <span class="title">初审意见</span>
            <el-tag :type="getReviewStatusType(initialReview.status)">
              {{ getReviewStatusLabel(initialReview.status) }}
            </el-tag>
          </div>
        </template>

        <el-descriptions :column="1" border>
          <el-descriptions-item label="编辑">
            {{ initialReview.editorName }}
          </el-descriptions-item>
          <el-descriptions-item label="审核时间">
            {{ formatDate(initialReview.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="意见">
            {{ initialReview.opinion || '暂无意见' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- Reviews -->
      <el-card class="review-card" v-if="reviews && reviews.length > 0">
        <template #header>
          <div class="card-header">
            <span class="title">专家审稿意见 ({{ reviews.length }})</span>
          </div>
        </template>

        <div v-for="(review, index) in reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <span class="reviewer-name">审稿人: {{ review.reviewerName }}</span>
            <el-tag :type="getReviewStatusType(review.status)">
              {{ getReviewStatusLabel(review.status) }}
            </el-tag>
          </div>

          <el-descriptions :column="2" border size="small" class="review-details">
            <el-descriptions-item label="评分">
              <el-rate v-model="review.score" disabled allow-half />
              ({{ review.score }}/5)
            </el-descriptions-item>
            <el-descriptions-item label="建议">
              <el-tag :type="getRecommendationType(review.recommendation)">
                {{ getRecommendationLabel(review.recommendation) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">
              {{ formatDate(review.submittedDate) }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ formatDate(review.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="审稿意见" :span="2">
              {{ review.opinion || '暂无意见' }}
            </el-descriptions-item>
          </el-descriptions>

          <el-divider v-if="index < reviews.length - 1" />
        </div>
      </el-card>

      <el-empty v-if="!initialReview && (!reviews || reviews.length === 0)" description="暂无审稿意见" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { manuscriptService } from '@/services/manuscriptService'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const manuscript = ref(null)
const initialReview = ref(null)
const reviews = ref([])

const statusMap = {
  'DRAFT': '草稿',
  'SUBMITTED': '已投稿',
  'UNDER_REVIEW': '审核中',
  'REVISION_REQUIRED': '需要修改',
  'ACCEPTED': '已录用',
  'REJECTED': '已驳回'
}

const statusTypeMap = {
  'DRAFT': 'info',
  'SUBMITTED': 'warning',
  'UNDER_REVIEW': 'warning',
  'REVISION_REQUIRED': 'warning',
  'ACCEPTED': 'success',
  'REJECTED': 'danger'
}

const reviewStatusMap = {
  'PENDING': '待审',
  'ACCEPTED': '已接受',
  'REJECTED': '已拒绝',
  'SUBMITTED': '已提交',
  'PASS': '通过',
  'REJECT': '驳回',
  'REVISION_REQUIRED': '需要修改'
}

const reviewStatusTypeMap = {
  'PENDING': 'warning',
  'ACCEPTED': 'info',
  'REJECTED': 'danger',
  'SUBMITTED': 'success',
  'PASS': 'success',
  'REJECT': 'danger',
  'REVISION_REQUIRED': 'warning'
}

const recommendationMap = {
  'ACCEPT': '同意录用',
  'ACCEPT_WITH_REVISION': '修改后录用',
  'REJECT': '拒绝录用'
}

const recommendationTypeMap = {
  'ACCEPT': 'success',
  'ACCEPT_WITH_REVISION': 'warning',
  'REJECT': 'danger'
}

onMounted(() => {
  loadManuscriptDetail()
})

const loadManuscriptDetail = async () => {
  loading.value = true
  try {
    const manuscriptId = route.params.id
    const response = await manuscriptService.getManuscriptDetail(manuscriptId)
    
    if (response.data) {
      manuscript.value = response.data.manuscript
      initialReview.value = response.data.initialReview
      reviews.value = response.data.reviews || []
    }
  } catch (error) {
    ElMessage.error(error.message || '加载稿件详情失败')
  } finally {
    loading.value = false
  }
}

const getStatusLabel = (status) => {
  return statusMap[status] || status
}

const getStatusType = (status) => {
  return statusTypeMap[status] || 'info'
}

const getReviewStatusLabel = (status) => {
  return reviewStatusMap[status] || status
}

const getReviewStatusType = (status) => {
  return reviewStatusTypeMap[status] || 'info'
}

const getRecommendationLabel = (recommendation) => {
  return recommendationMap[recommendation] || recommendation
}

const getRecommendationType = (recommendation) => {
  return recommendationTypeMap[recommendation] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const goBack = () => {
  router.push('/author/manuscripts')
}
</script>

<style scoped>
.manuscript-detail {
  padding: 20px;
}

.detail-container {
  max-width: 1000px;
}

.manuscript-card,
.review-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-weight: bold;
  font-size: 16px;
}

.content-preview {
  color: #666;
  line-height: 1.5;
  max-height: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.review-item {
  margin-bottom: 20px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.reviewer-name {
  font-weight: bold;
  color: #333;
}

.review-details {
  margin-top: 10px;
}

:deep(.el-descriptions__body) {
  background-color: #fafafa;
}

:deep(.el-rate) {
  display: inline-block;
}
</style>
