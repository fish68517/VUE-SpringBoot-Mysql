<template>
  <div class="assign-reviewer-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>分配审稿人</span>
        </div>
      </template>

      <!-- Manuscripts for Review Assignment -->
      <el-table :data="manuscriptsForReview" stripe style="width: 100%; margin-bottom: 20px">
        <el-table-column prop="id" label="稿件ID" width="80" />
        <el-table-column prop="title" label="稿件标题" min-width="200" />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="submissionDate" label="投稿日期" width="150" :formatter="formatDate" />
        <el-table-column prop="reviewerCount" label="已分配审稿人数" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleAssign(row)">分配审稿人</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Assign Reviewer Dialog -->
      <el-dialog v-model="assignDialogVisible" title="分配审稿人" width="600px">
        <el-form :model="assignForm" label-width="100px">
          <el-form-item label="稿件标题">
            <el-input v-model="assignForm.title" disabled />
          </el-form-item>
          <el-form-item label="选择审稿人" required>
            <el-select
              v-model="assignForm.reviewerId"
              placeholder="请选择审稿人"
              filterable
              @change="onReviewerChange"
            >
              <el-option
                v-for="reviewer in availableReviewers"
                :key="reviewer.id"
                :label="`${reviewer.username} (${reviewer.expertiseAreas || ''})`"
                :value="reviewer.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="审稿人信息" v-if="selectedReviewerInfo">
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="用户名">{{ selectedReviewerInfo.username }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ selectedReviewerInfo.email }}</el-descriptions-item>
              <el-descriptions-item label="专业领域">{{ selectedReviewerInfo.expertiseAreas || 'N/A' }}</el-descriptions-item>
              <el-descriptions-item label="研究方向">{{ selectedReviewerInfo.researchDirections || 'N/A' }}</el-descriptions-item>
            </el-descriptions>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssignment" :loading="assigning">分配</el-button>
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

const manuscriptsForReview = ref([])
const assignDialogVisible = ref(false)
const assigning = ref(false)
const availableReviewers = ref([])
const selectedReviewerInfo = ref(null)

const assignForm = ref({
  manuscriptId: null,
  title: '',
  reviewerId: null
})

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

const loadManuscriptsForReview = async () => {
  try {
    const response = await editorService.getPendingManuscripts()
    if (response.data) {
      // Fetch full manuscript details for each manuscript
      const manuscriptsWithDetails = await Promise.all(
        response.data.map(async (item) => {
          try {
            const detail = await manuscriptService.getManuscriptDetail(item.manuscriptId)
            const manuscript = detail.data.manuscript
            return {
              id: manuscript.id,
              title: manuscript.title,
              authorName: manuscript.authorName || 'Unknown',
              submissionDate: manuscript.submissionDate,
              reviewerCount: 0 // Will be updated based on actual review assignments
            }
          } catch (error) {
            return {
              id: item.manuscriptId,
              title: 'Unknown',
              authorName: 'Unknown',
              submissionDate: null,
              reviewerCount: 0
            }
          }
        })
      )
      manuscriptsForReview.value = manuscriptsWithDetails
    }
  } catch (error) {
    ElMessage.error('加载稿件列表失败')
  }
}

const loadReviewers = async () => {
  try {
    const response = await editorService.getAllReviewers()
    if (response.data) {
      availableReviewers.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载审稿人列表失败')
  }
}

const handleAssign = (manuscript) => {
  assignForm.value = {
    manuscriptId: manuscript.id,
    title: manuscript.title,
    reviewerId: null
  }
  selectedReviewerInfo.value = null
  assignDialogVisible.value = true
}

const onReviewerChange = () => {
  if (assignForm.value.reviewerId) {
    const reviewer = availableReviewers.value.find(r => r.id === assignForm.value.reviewerId)
    selectedReviewerInfo.value = reviewer || null
  } else {
    selectedReviewerInfo.value = null
  }
}

const submitAssignment = async () => {
  if (!assignForm.value.reviewerId) {
    ElMessage.error('请选择审稿人')
    return
  }

  assigning.value = true
  try {
    await editorService.assignReviewer(assignForm.value.manuscriptId, assignForm.value.reviewerId)
    ElMessage.success('审稿人分配成功')
    assignDialogVisible.value = false
    await loadManuscriptsForReview()
  } catch (error) {
    ElMessage.error('分配审稿人失败')
  } finally {
    assigning.value = false
  }
}

onMounted(() => {
  loadManuscriptsForReview()
  loadReviewers()
})
</script>

<style scoped>
.assign-reviewer-container {
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
</style>
