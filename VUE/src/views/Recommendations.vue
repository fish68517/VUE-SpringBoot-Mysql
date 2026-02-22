<template>
  <div class="recommendations-container">
    <Card title="农资推荐">
      <el-row :gutter="20" class="filter-row">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="filters.warningId"
            placeholder="请输入预警ID"
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
            v-model="filters.status"
            placeholder="选择状态"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="待处理" value="pending" />
            <el-option label="已接受" value="accepted" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col
          v-for="rec in recommendations"
          :key="rec.id"
          :xs="24"
          :sm="12"
          :md="8"
        >
          <div class="recommendation-card">
            <div class="card-header">
              <h4>{{ rec.productName }}</h4>
              <el-tag :type="getStatusType(rec.status)">{{ rec.status }}</el-tag>
            </div>
            <p class="reason">推荐原因: {{ rec.reason }}</p>
            <p class="priority">优先级: {{ rec.priority }}</p>
            <p class="price">价格: ¥{{ rec.price }}</p>
            <div class="actions" v-if="rec.status === 'pending'">
              <el-button
                type="success"
                size="small"
                @click="handleAccept(rec)"
              >
                接受
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleReject(rec)"
              >
                拒绝
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </Card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { recommendationAPI } from '@/api/recommendation'
import Card from '@/components/common/Card.vue'

const filters = ref({
  warningId: '',
  status: '',
})

const recommendations = ref([])

const getStatusType = (status) => {
  const statusMap = {
    pending: 'info',
    accepted: 'success',
    rejected: 'danger',
  }
  return statusMap[status] || 'info'
}

const handleSearch = async () => {
  try {
    if (!filters.value.warningId) {
      ElMessage.warning('请输入预警ID')
      return
    }
    const response = await recommendationAPI.getRecommendations(
      filters.value.warningId,
      { status: filters.value.status }
    )
    recommendations.value = response.data || []
  } catch (error) {
    ElMessage.error('获取推荐列表失败')
  }
}

const handleAccept = async (rec) => {
  try {
    await recommendationAPI.acceptRecommendation(rec.id)
    ElMessage.success('已接受推荐')
    handleSearch()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (rec) => {
  try {
    await recommendationAPI.rejectRecommendation(rec.id)
    ElMessage.success('已拒绝推荐')
    handleSearch()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  // Initialize with empty state
})
</script>

<style scoped>
.recommendations-container {
  padding: 20px;
}

.filter-row {
  margin-bottom: 20px;
}

.recommendation-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  background-color: #f5f7fa;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-header h4 {
  margin: 0;
  color: #303133;
}

.recommendation-card p {
  margin: 5px 0;
  font-size: 13px;
  color: #606266;
}

.reason {
  color: #909399;
  font-style: italic;
}

.priority {
  color: #f56c6c;
  font-weight: bold;
}

.price {
  color: #409eff;
  font-weight: bold;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid #dcdfe6;
}
</style>
