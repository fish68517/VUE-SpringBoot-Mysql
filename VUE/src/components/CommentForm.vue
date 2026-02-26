<template>
  <div class="comment-form-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>发表评价</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="submitComment"
      >
        <!-- 评分 -->
        <el-form-item label="评分" prop="rating">
          <el-rate
            v-model="form.rating"
            :max="5"
            allow-half
            show-text
            texts="很差,差,一般,好,很好"
          />
        </el-form-item>

        <!-- 留言内容 -->
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价内容（最多5000字）"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitComment" :loading="loading">
            提交评价
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  targetType: {
    type: String,
    required: true,
    validator: (value) => ['attraction', 'hotel'].includes(value)
  },
  targetId: {
    type: Number,
    required: true
  },
  userId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['comment-submitted'])

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  rating: 5,
  content: ''
})

const rules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 5, message: '评价内容至少5个字符', trigger: 'blur' },
    { max: 5000, message: '评价内容不能超过5000字符', trigger: 'blur' }
  ]
}

const API_BASE_URL = 'http://localhost:8080/api'

/**
 * 提交评价
 */
const submitComment = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    ElMessage.error('请填写完整的评价信息')
    return
  }

  loading.value = true
  try {
    const response = await fetch(`${API_BASE_URL}/comments`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: props.userId,
        targetType: props.targetType,
        targetId: props.targetId,
        content: form.content.trim(),
        rating: form.rating
      })
    })

    const data = await response.json()

    if (data.code === '0') {
      ElMessage.success('评价提交成功，等待审核')
      resetForm()
      emit('comment-submitted', data.data)
    } else {
      ElMessage.error(data.message || '提交评价失败')
    }
  } catch (error) {
    ElMessage.error('提交评价失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.rating = 5
  form.content = ''
}
</script>

<style scoped>
.comment-form-container {
  margin: 20px 0;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  color: #333;
}
</style>
