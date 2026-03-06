<template>
  <div class="initial-review-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>稿件初审</span>
        </div>
      </template>

      <!-- Pending Manuscripts List -->
      <el-table :data="pendingManuscripts" stripe style="width: 100%; margin-bottom: 20px">
        <el-table-column prop="id" label="稿件ID" width="80" />
        <el-table-column prop="title" label="稿件标题" min-width="200" />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="submissionDate" label="投稿日期" width="150" :formatter="formatDate" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleReview(row)">初审</el-button>
            <el-button link type="info" @click="handleViewDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Review Form Dialog -->
      <el-dialog v-model="reviewDialogVisible" title="提交初审意见" width="600px">
        <el-form :model="reviewForm" label-width="100px">
          <el-form-item label="稿件标题">
            <el-input v-model="reviewForm.title" disabled />
          </el-form-item>
          <el-form-item label="初审结果" required>
            <el-select v-model="reviewForm.status" placeholder="请选择初审结果">
              <el-option label="通过" value="PASS" />
              <el-option label="驳回" value="REJECT" />
              <el-option label="退修" value="REVISION_REQUIRED" />
            </el-select>
          </el-form-item>
          <el-form-item label="初审意见" required>
            <el-input
              v-model="reviewForm.opinion"
              type="textarea"
              rows="6"
              placeholder="请输入初审意见"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview" :loading="submitting">提交</el-button>
        </template>
      </el-dialog>

      <!-- Detail Dialog -->
      <el-dialog v-model="detailDialogVisible" title="稿件详情" width="700px">
        <div v-if="selectedManuscript" class="manuscript-detail">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="稿件ID">{{ selectedManuscript.id }}</el-descriptions-item>
            <el-descriptions-item label="标题">{{ selectedManuscript.title }}</el-descriptions-item>
            <el-descriptions-item label="作者">{{ selectedManuscript.authorName }}</el-descriptions-item>
            <el-descriptions-item label="摘要">{{ selectedManuscript.abstractText }}</el-descriptions-item>
            <el-descriptions-item label="投稿日期">{{ formatDate(selectedManuscript.submissionDate) }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(selectedManuscript.status)">{{ selectedManuscript.status }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
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

const pendingManuscripts = ref([])
const reviewDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const submitting = ref(false)
const selectedManuscript = ref(null)

const reviewForm = ref({
  manuscriptId: null,
  title: '',
  status: '',
  opinion: ''
})

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'info',
    'PASS': 'success',
    'REJECT': 'danger',
    'REVISION_REQUIRED': 'warning'
  }
  return statusMap[status] || 'info'
}

const loadPendingManuscripts = async () => {
  try {
    const response = await editorService.getPendingManuscripts()
    if (response.data) {
      // Fetch full manuscript details for each pending manuscript
      const manuscriptsWithDetails = await Promise.all(
        response.data.map(async (item) => {
          try {
            const detail = await manuscriptService.getManuscriptDetail(item.manuscriptId)
            const manuscript = detail.data.manuscript
            return {
              id: manuscript.id,
              title: manuscript.title,
              authorName: manuscript.authorName || 'Unknown',
              abstractText: manuscript.abstractText,
              submissionDate: manuscript.submissionDate,
              status: item.status || 'PENDING'
            }
          } catch (error) {
            return {
              id: item.manuscriptId,
              title: 'Unknown',
              authorName: 'Unknown',
              abstractText: '',
              submissionDate: null,
              status: item.status || 'PENDING'
            }
          }
        })
      )
      pendingManuscripts.value = manuscriptsWithDetails
    }
  } catch (error) {
    ElMessage.error('加载待初审稿件失败')
  }
}

const handleReview = (manuscript) => {
  selectedManuscript.value = manuscript
  reviewForm.value = {
    manuscriptId: manuscript.id,
    title: manuscript.title,
    status: '',
    opinion: ''
  }
  reviewDialogVisible.value = true
}

const handleViewDetail = (manuscript) => {
  selectedManuscript.value = manuscript
  detailDialogVisible.value = true
}

const submitReview = async () => {
  if (!reviewForm.value.status) {
    ElMessage.error('请选择初审结果')
    return
  }
  if (!reviewForm.value.opinion) {
    ElMessage.error('请输入初审意见')
    return
  }

  submitting.value = true
  try {
    await editorService.submitInitialReview(
      reviewForm.value.manuscriptId,
      reviewForm.value.status,
      reviewForm.value.opinion
    )
    ElMessage.success('初审意见提交成功')
    reviewDialogVisible.value = false
    await loadPendingManuscripts()
  } catch (error) {
    ElMessage.error('提交初审意见失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPendingManuscripts()
})
</script>

<style scoped>
.initial-review-container {
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

.manuscript-detail {
  padding: 20px 0;
}
</style>
