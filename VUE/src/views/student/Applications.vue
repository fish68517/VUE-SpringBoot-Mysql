<template>
  <div class="applications-container">
    <h2>我的申请</h2>

    <!-- 申请列表 -->
    <el-card>
      <el-empty v-if="applications.length === 0" description="暂无申请" />
      
      <el-table v-else :data="applications" stripe>
        <el-table-column prop="id" label="申请ID" width="80" />
        <el-table-column prop="postTitle" label="岗位名称" />
        <el-table-column prop="department" label="科室" />
        <el-table-column label="学校审批状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.schoolStatus === 'PENDING'" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.schoolStatus === 'APPROVED'" type="success">已通过</el-tag>
            <el-tag v-else-if="row.schoolStatus === 'REJECTED'" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="医院审批状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.hospitalStatus === 'PENDING'" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.hospitalStatus === 'APPROVED'" type="success">已通过</el-tag>
            <el-tag v-else-if="row.hospitalStatus === 'REJECTED'" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showApplicationDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 申请详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="申请详情" width="600px">
      <div v-if="selectedApplication" class="application-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="岗位名称">
            {{ selectedApplication.postTitle }}
          </el-descriptions-item>
          <el-descriptions-item label="科室">
            {{ selectedApplication.department }}
          </el-descriptions-item>
          <el-descriptions-item label="申请理由">
            {{ selectedApplication.reason }}
          </el-descriptions-item>
          <el-descriptions-item label="学校审批状态">
            <el-tag v-if="selectedApplication.schoolStatus === 'PENDING'" type="warning">
              待审核
            </el-tag>
            <el-tag v-else-if="selectedApplication.schoolStatus === 'APPROVED'" type="success">
              已通过
            </el-tag>
            <el-tag v-else-if="selectedApplication.schoolStatus === 'REJECTED'" type="danger">
              已驳回
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.schoolOpinion" label="学校审批意见">
            {{ selectedApplication.schoolOpinion }}
          </el-descriptions-item>
          <el-descriptions-item label="医院审批状态">
            <el-tag v-if="selectedApplication.hospitalStatus === 'PENDING'" type="warning">
              待审核
            </el-tag>
            <el-tag v-else-if="selectedApplication.hospitalStatus === 'APPROVED'" type="success">
              已通过
            </el-tag>
            <el-tag v-else-if="selectedApplication.hospitalStatus === 'REJECTED'" type="danger">
              已驳回
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.hospitalOpinion" label="医院审批意见">
            {{ selectedApplication.hospitalOpinion }}
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ formatDate(selectedApplication.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getApplications, getApplicationDetail } from '@/api/application'

// 数据
const applications = ref([])
const selectedApplication = ref(null)
const detailDialogVisible = ref(false)

// 获取申请列表
const fetchApplications = async () => {
  try {
    const response = await getApplications()
    applications.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取申请列表失败')
  }
}

// 显示申请详情
const showApplicationDetail = async (application) => {
  try {
    const response = await getApplicationDetail(application.id)
    selectedApplication.value = response.data
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error(error.message || '获取申请详情失败')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// 初始化
onMounted(() => {
  fetchApplications()
})
</script>

<style scoped>
.applications-container {
  padding: 20px;
}

.application-detail {
  padding: 20px 0;
}
</style>
