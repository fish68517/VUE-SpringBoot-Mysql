<template>
  <div class="admin-feedbacks">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>反馈管理</span>
          <el-select v-model="filterStatus" placeholder="筛选状态" clearable style="width: 150px" @change="loadFeedbacks">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已解决" :value="2" />
            <el-option label="已关闭" :value="3" />
          </el-select>
        </div>
      </template>

      <el-table :data="feedbacks" v-loading="loading" :header-cell-style="{ textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="用户" width="120" align="center">
          <template #default="{ row }">
            {{ row.nickname || row.username }}
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="类型" width="100" align="center" />
        <el-table-column label="相关座位" width="100" align="center">
          <template #default="{ row }">
            {{ row.seatArea ? row.seatArea + '-' + row.seatNo : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showReplyDialog(row)">
              {{ row.reply ? '查看' : '回复' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="反馈详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户">{{ currentFeedback?.nickname || currentFeedback?.username }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentFeedback?.typeName }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentFeedback?.statusName }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ formatTime(currentFeedback?.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ currentFeedback?.content }}</el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <el-form label-width="80px">
        <el-form-item label="回复内容">
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
            :disabled="!!currentFeedback?.reply"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="!currentFeedback?.reply" type="primary" @click="handleReply" :loading="replying">
          提交回复
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllFeedbacks, replyFeedback } from '../../api/feedback'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const feedbacks = ref([])
const filterStatus = ref(null)
const dialogVisible = ref(false)
const currentFeedback = ref(null)
const replyContent = ref('')
const replying = ref(false)

const statusType = (status) => {
  const types = { 0: 'danger', 1: 'warning', 2: 'success', 3: 'info' }
  return types[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const loadFeedbacks = async () => {
  loading.value = true
  try {
    const res = await getAllFeedbacks(filterStatus.value)
    feedbacks.value = res.data
  } finally {
    loading.value = false
  }
}

const showReplyDialog = (row) => {
  currentFeedback.value = row
  replyContent.value = row.reply || ''
  dialogVisible.value = true
}

const handleReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replying.value = true
  try {
    await replyFeedback(currentFeedback.value.id, replyContent.value)
    ElMessage.success('回复成功')
    dialogVisible.value = false
    loadFeedbacks()
  } finally {
    replying.value = false
  }
}

onMounted(() => {
  loadFeedbacks()
})
</script>

<style scoped>
.admin-feedbacks {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
