<template>
  <div class="feedback-container">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>提交反馈</span>
          </template>

          <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
            <el-form-item label="类型" prop="type">
              <el-select v-model="form.type" placeholder="选择反馈类型" style="width: 100%">
                <el-option label="座位问题" :value="1" />
                <el-option label="环境问题" :value="2" />
                <el-option label="建议" :value="3" />
                <el-option label="其他" :value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="相关座位">
              <el-select v-model="form.seatId" placeholder="选择座位（可选）" clearable style="width: 100%">
                <el-option
                  v-for="seat in seats"
                  :key="seat.id"
                  :label="seat.area + '-' + seat.seatNo"
                  :value="seat.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="内容" prop="content">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="5"
                placeholder="请详细描述您的问题或建议"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit" :loading="submitting">
                提交反馈
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card>
          <template #header>
            <span>我的反馈记录</span>
          </template>

          <el-table :data="feedbacks" v-loading="loading" :header-cell-style="{ textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
            <el-table-column label="类型" width="100" align="center">
              <template #default="{ row }">
                {{ row.typeName }}
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip align="center" />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">
                  {{ row.statusName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="回复" width="200" align="center">
              <template #default="{ row }">
                <span v-if="row.reply">{{ row.reply }}</span>
                <span v-else class="no-reply">暂无回复</span>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { createFeedback, getMyFeedbacks } from '../api/feedback'
import { getSeatList } from '../api/seat'
import { ElMessage } from 'element-plus'

const formRef = ref()
const loading = ref(false)
const submitting = ref(false)
const feedbacks = ref([])
const seats = ref([])

const form = reactive({
  type: null,
  seatId: null,
  content: ''
})

const rules = {
  type: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
}

const statusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: '' }
  return types[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const loadFeedbacks = async () => {
  loading.value = true
  try {
    const res = await getMyFeedbacks()
    feedbacks.value = res.data
  } finally {
    loading.value = false
  }
}

const loadSeats = async () => {
  const res = await getSeatList()
  seats.value = res.data
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await createFeedback(form)
    ElMessage.success('反馈提交成功')
    form.type = null
    form.seatId = null
    form.content = ''
    loadFeedbacks()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadFeedbacks()
  loadSeats()
})
</script>

<style scoped>
.feedback-container {
  max-width: 1200px;
  margin: 0 auto;
}

.no-reply {
  color: #909399;
  font-size: 13px;
}
</style>
