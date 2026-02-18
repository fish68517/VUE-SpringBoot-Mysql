<template>
  <div class="application-review-container">
    <h2>申请审批</h2>
    
    <!-- 申请列表 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>待审批申请列表</span>
          <el-button type="primary" @click="loadApplications">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="applications" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentName" label="学生" width="120" />
        <el-table-column prop="postTitle" label="岗位" width="150" />
        <el-table-column prop="hospitalName" label="医院" width="150" />
        <el-table-column prop="schoolStatus" label="学校审批状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.schoolStatus)">{{ row.schoolStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hospitalStatus" label="医院审批状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.hospitalStatus)">{{ row.hospitalStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleReview(row)"
              :disabled="row.schoolStatus !== 'PENDING'">
              审批
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="handleViewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 审批对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="申请审批" width="500px">
      <div v-if="selectedApplication" class="review-form">
        <div class="form-group">
          <label>学生：</label>
          <span>{{ selectedApplication.studentName }}</span>
        </div>
        <div class="form-group">
          <label>岗位：</label>
          <span>{{ selectedApplication.postTitle }}</span>
        </div>
        <div class="form-group">
          <label>申请理由：</label>
          <p class="reason-text">{{ selectedApplication.reason }}</p>
        </div>
        <div class="form-group">
          <label>审批意见：</label>
          <el-input 
            v-model="reviewOpinion" 
            type="textarea" 
            rows="4"
            placeholder="请输入审批意见" />
        </div>
        <div class="form-group">
          <label>审批结果：</label>
          <el-radio-group v-model="reviewStatus">
            <el-radio label="APPROVED">通过</el-radio>
            <el-radio label="REJECTED">驳回</el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交审批</el-button>
      </template>
    </el-dialog>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="申请详情" width="600px">
      <div v-if="selectedApplication" class="detail-view">
        <div class="detail-group">
          <label>学生：</label>
          <span>{{ selectedApplication.studentName }}</span>
        </div>
        <div class="detail-group">
          <label>岗位：</label>
          <span>{{ selectedApplication.postTitle }}</span>
        </div>
        <div class="detail-group">
          <label>医院：</label>
          <span>{{ selectedApplication.hospitalName }}</span>
        </div>
        <div class="detail-group">
          <label>申请理由：</label>
          <p class="reason-text">{{ selectedApplication.reason }}</p>
        </div>
        <div class="detail-group">
          <label>学校审批状态：</label>
          <el-tag :type="getStatusType(selectedApplication.schoolStatus)">
            {{ selectedApplication.schoolStatus }}
          </el-tag>
        </div>
        <div class="detail-group">
          <label>学校审批意见：</label>
          <p>{{ selectedApplication.schoolOpinion || '暂无' }}</p>
        </div>
        <div class="detail-group">
          <label>医院审批状态：</label>
          <el-tag :type="getStatusType(selectedApplication.hospitalStatus)">
            {{ selectedApplication.hospitalStatus }}
          </el-tag>
        </div>
        <div class="detail-group">
          <label>医院审批意见：</label>
          <p>{{ selectedApplication.hospitalOpinion || '暂无' }}</p>
        </div>
        <div class="detail-group">
          <label>申请时间：</label>
          <span>{{ selectedApplication.createdAt }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSchoolApplications, schoolReviewApplication } from '@/api/schoolAdmin'

const applications = ref([])
const loading = ref(false)
const reviewDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedApplication = ref(null)
const reviewOpinion = ref('')
const reviewStatus = ref('APPROVED')

const loadApplications = async () => {
  loading.value = true
  try {
    const response = await getSchoolApplications()
    applications.value = response.data || []
  } catch (error) {
    ElMessage.error('加载申请列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return statusMap[status] || 'info'
}

const handleReview = (row) => {
  selectedApplication.value = row
  reviewOpinion.value = ''
  reviewStatus.value = 'APPROVED'
  reviewDialogVisible.value = true
}

const handleViewDetail = (row) => {
  selectedApplication.value = row
  detailDialogVisible.value = true
}

const submitReview = async () => {
  try {
    await schoolReviewApplication(selectedApplication.value.id, {
      status: reviewStatus.value,
      opinion: reviewOpinion.value
    })
    ElMessage.success('审批完成')
    reviewDialogVisible.value = false
    loadApplications()
  } catch (error) {
    ElMessage.error('审批失败: ' + (error.response?.data?.message || error.message))
  }
}

onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.application-review-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.box-card {
  margin-bottom: 20px;
}

.review-form,
.detail-view {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group,
.detail-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label,
.detail-group label {
  font-weight: bold;
  color: #333;
}

.reason-text {
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}
</style>
