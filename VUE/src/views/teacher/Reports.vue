<template>
  <div class="reports-container">
    <h2>周记批阅</h2>

    <!-- 周记列表 -->
    <el-card class="reports-card">
      <template #header>
        <div class="card-header">
          <span>待批阅周记</span>
          <el-select v-model="filterStatus" placeholder="筛选状态" style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="待批阅" value="SUBMITTED" />
            <el-option label="已批阅" value="REVIEWED" />
            <el-option label="已打回" value="REJECTED" />
          </el-select>
        </div>
      </template>

      <el-empty v-if="filteredReports.length === 0" description="暂无周记" />

      <el-table v-else :data="filteredReports" stripe>
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="weekNumber" label="周次" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'SUBMITTED'" type="warning">待批阅</el-tag>
            <el-tag v-else-if="row.status === 'REVIEWED'" type="success">已批阅</el-tag>
            <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已打回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80">
          <template #default="{ row }">
            {{ row.teacherScore || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showReviewForm(row)">
              批阅
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 周记批阅对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="批阅周记" width="700px" @close="resetReviewForm">
      <div v-if="selectedReport" class="review-form">
        <!-- 周记内容 -->
        <el-descriptions :column="1" border class="report-info">
          <el-descriptions-item label="学生姓名">
            {{ selectedReport.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="周次">
            {{ selectedReport.weekNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="周记内容">
            <div class="content-display">{{ selectedReport.content }}</div>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedReport.teacherComment" label="之前的评语">
            <div class="content-display">{{ selectedReport.teacherComment }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 批阅表单 -->
        <el-form :model="reviewForm" label-width="100px" style="margin-top: 20px">
          <el-form-item label="评分" required>
            <el-rate v-model="reviewForm.score" :max="5" />
          </el-form-item>
          <el-form-item label="评语" required>
            <el-input
              v-model="reviewForm.comment"
              type="textarea"
              rows="4"
              placeholder="请输入评语"
            />
          </el-form-item>
          <el-form-item label="操作">
            <el-radio-group v-model="reviewForm.action">
              <el-radio label="REVIEWED">批阅通过</el-radio>
              <el-radio label="REJECTED">打回重写</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="reviewLoading">
          提交批阅
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getWeeklyReportsForTeacher,
  reviewWeeklyReport,
} from '@/api/teacher'

// 数据
const reports = ref([])
const selectedReport = ref(null)
const reviewDialogVisible = ref(false)
const reviewLoading = ref(false)
const filterStatus = ref('')

const reviewForm = ref({
  score: 5,
  comment: '',
  action: 'REVIEWED',
})

// 计算过滤后的周记列表
const filteredReports = computed(() => {
  if (!filterStatus.value) {
    return reports.value
  }
  return reports.value.filter(report => report.status === filterStatus.value)
})

// 获取待批阅的周记列表
const fetchReports = async () => {
  try {
    const response = await getWeeklyReportsForTeacher()
    reports.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取周记列表失败')
  }
}

// 显示批阅表单
const showReviewForm = (report) => {
  selectedReport.value = report
  reviewForm.value.score = report.teacherScore || 5
  reviewForm.value.comment = report.teacherComment || ''
  reviewForm.value.action = 'REVIEWED'
  reviewDialogVisible.value = true
}

// 重置批阅表单
const resetReviewForm = () => {
  reviewForm.value.score = 5
  reviewForm.value.comment = ''
  reviewForm.value.action = 'REVIEWED'
}

// 提交批阅
const submitReview = async () => {
  if (!reviewForm.value.comment.trim()) {
    ElMessage.warning('请输入评语')
    return
  }

  reviewLoading.value = true
  try {
    await reviewWeeklyReport(selectedReport.value.internshipId, selectedReport.value.id, {
      score: reviewForm.value.score,
      comment: reviewForm.value.comment,
      status: reviewForm.value.action,
    })
    ElMessage.success('批阅完成')
    reviewDialogVisible.value = false
    resetReviewForm()
    await fetchReports()
  } catch (error) {
    ElMessage.error(error.message || '批阅失败')
  } finally {
    reviewLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.reports-container {
  padding: 20px;
}

.reports-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.review-form {
  padding: 20px 0;
}

.report-info {
  margin-bottom: 20px;
}

.content-display {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}
</style>
