<template>
  <div class="activity-form-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <div class="form-container">
      <h1>{{ isEdit ? '编辑活动' : '创建活动' }}</h1>
      
      <form @submit.prevent="handleSubmit">
        <!-- Basic Information Section -->
        <div class="form-section">
          <h2>基本信息</h2>
          
          <div class="form-group">
            <label for="title">活动标题 <span class="required">*</span></label>
            <input
              id="title"
              v-model="form.title"
              type="text"
              placeholder="请输入活动标题"
              required
            />
            <span v-if="errors.title" class="error-message">{{ errors.title }}</span>
          </div>

          <div class="form-group">
            <label for="type">活动类型</label>
            <select id="type" v-model="form.type">
              <option value="">请选择活动类型</option>
              <option value="lecture">讲座</option>
              <option value="competition">比赛</option>
              <option value="charity">公益</option>
              <option value="other">其他</option>
            </select>
          </div>

          <div class="form-group">
            <label for="description">活动描述</label>
            <textarea
              id="description"
              v-model="form.description"
              placeholder="请输入活动描述"
              rows="4"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="location">活动地点</label>
            <input
              id="location"
              v-model="form.location"
              type="text"
              placeholder="请输入活动地点"
            />
          </div>

          <div class="form-group">
            <label for="coverUrl">封面图片URL</label>
            <input
              id="coverUrl"
              v-model="form.coverUrl"
              type="url"
              placeholder="请输入封面图片URL"
            />
          </div>
        </div>

        <!-- Time Information Section -->
        <div class="form-section">
          <h2>时间信息</h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="startTime">活动开始时间 <span class="required">*</span></label>
              <input
                id="startTime"
                v-model="form.startTime"
                type="datetime-local"
                required
              />
              <span v-if="errors.startTime" class="error-message">{{ errors.startTime }}</span>
            </div>

            <div class="form-group">
              <label for="endTime">活动结束时间 <span class="required">*</span></label>
              <input
                id="endTime"
                v-model="form.endTime"
                type="datetime-local"
                required
              />
              <span v-if="errors.endTime" class="error-message">{{ errors.endTime }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="registrationDeadline">报名截止时间</label>
            <input
              id="registrationDeadline"
              v-model="form.registrationDeadline"
              type="datetime-local"
            />
          </div>
        </div>

        <!-- Participation Information Section -->
        <div class="form-section">
          <h2>参与信息</h2>
          
          <div class="form-group">
            <label for="maxParticipants">最大参与人数</label>
            <input
              id="maxParticipants"
              v-model.number="form.maxParticipants"
              type="number"
              placeholder="请输入最大参与人数"
              min="1"
            />
          </div>
        </div>

        <!-- Crowdfunding Configuration Section -->
        <div class="form-section">
          <h2>众筹配置</h2>
          
          <div class="form-group checkbox">
            <input
              id="enableCrowdfunding"
              v-model="form.enableCrowdfunding"
              type="checkbox"
            />
            <label for="enableCrowdfunding">启用众筹</label>
          </div>

          <div v-if="form.enableCrowdfunding" class="form-group">
            <label for="crowdfundingTarget">众筹目标金额 (元)</label>
            <input
              id="crowdfundingTarget"
              v-model.number="form.crowdfundingTarget"
              type="number"
              placeholder="请输入众筹目标金额"
              min="0"
              step="0.01"
            />
          </div>
        </div>

        <!-- Form Actions -->
        <div class="form-actions">
          <button type="submit" class="btn-primary">{{ isEdit ? '更新活动' : '创建活动' }}</button>
          <button type="button" class="btn-secondary" @click="handleCancel">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import TopNavigation from '../components/TopNavigation.vue'
import http from '../utils/http'

const router = useRouter()
const route = useRoute()

const isEdit = ref(false)
const activityId = ref(null)

const form = reactive({
  title: '',
  type: '',
  description: '',
  location: '',
  coverUrl: '',
  startTime: '',
  endTime: '',
  registrationDeadline: '',
  maxParticipants: null,
  enableCrowdfunding: false,
  crowdfundingTarget: null
})

const errors = reactive({
  title: '',
  startTime: '',
  endTime: ''
})

/**
 * Initialize form for editing
 */
const initializeForm = async () => {
  if (route.params.id) {
    isEdit.value = true
    activityId.value = route.params.id

    try {
      const response = await http.get(`/activities/${activityId.value}`)
      const activity = response.data

      // Convert ISO datetime to datetime-local format
      form.title = activity.title
      form.type = activity.type || ''
      form.description = activity.description || ''
      form.location = activity.location || ''
      form.coverUrl = activity.coverUrl || ''
      form.startTime = convertToDatetimeLocal(activity.startTime)
      form.endTime = convertToDatetimeLocal(activity.endTime)
      form.registrationDeadline = activity.registrationDeadline ? convertToDatetimeLocal(activity.registrationDeadline) : ''
      form.maxParticipants = activity.maxParticipants || null
      form.enableCrowdfunding = activity.enableCrowdfunding || false
      form.crowdfundingTarget = activity.crowdfundingTarget || null
    } catch (error) {
      ElMessage.error('Failed to load activity')
      router.push('/')
    }
  }
}

/**
 * Convert ISO datetime string to datetime-local format
 */
const convertToDatetimeLocal = (isoString) => {
  if (!isoString) return ''
  const date = new Date(isoString)
  return date.toISOString().slice(0, 16)
}

/**
 * Validate form data
 */
const validateForm = () => {
  errors.title = ''
  errors.startTime = ''
  errors.endTime = ''

  if (!form.title || form.title.trim() === '') {
    errors.title = '活动标题不能为空'
    return false
  }

  if (!form.startTime) {
    errors.startTime = '活动开始时间不能为空'
    return false
  }

  if (!form.endTime) {
    errors.endTime = '活动结束时间不能为空'
    return false
  }

  const startTime = new Date(form.startTime)
  const endTime = new Date(form.endTime)

  if (startTime >= endTime) {
    errors.endTime = '活动结束时间必须晚于开始时间'
    return false
  }

  return true
}

/**
 * Handle form submission
 */
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  try {
    const payload = {
      title: form.title,
      type: form.type || null,
      description: form.description || null,
      location: form.location || null,
      coverUrl: form.coverUrl || null,
      startTime: form.startTime,
      endTime: form.endTime,
      registrationDeadline: form.registrationDeadline || null,
      maxParticipants: form.maxParticipants || null,
      enableCrowdfunding: form.enableCrowdfunding,
      crowdfundingTarget: form.enableCrowdfunding ? form.crowdfundingTarget : null
    }

    if (isEdit.value) {
      await http.put(`/activities/${activityId.value}`, payload)
      ElMessage.success('活动更新成功')
    } else {
      await http.post('/activities', payload)
      ElMessage.success('活动创建成功，等待审核')
    }

    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

/**
 * Handle cancel button
 */
const handleCancel = () => {
  router.push('/')
}

onMounted(() => {
  initializeForm()
})
</script>

<style scoped>
.activity-form-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.form-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

h1 {
  color: var(--primary-color);
  margin-bottom: 30px;
  font-size: 28px;
  text-align: center;
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.form-section:last-of-type {
  border-bottom: none;
}

.form-section h2 {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
}

.form-group {
  margin-bottom: 15px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.required {
  color: #f56c6c;
}

input[type="text"],
input[type="url"],
input[type="number"],
input[type="datetime-local"],
select,
textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s ease;
}

input[type="text"]:focus,
input[type="url"]:focus,
input[type="number"]:focus,
input[type="datetime-local"]:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

textarea {
  resize: vertical;
}

.form-group.checkbox {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.form-group.checkbox input[type="checkbox"] {
  width: auto;
  margin-right: 8px;
  cursor: pointer;
}

.form-group.checkbox label {
  margin-bottom: 0;
  cursor: pointer;
}

.error-message {
  display: block;
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 30px;
}

.btn-primary,
.btn-secondary {
  padding: 10px 30px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: var(--primary-color);
  color: white;
}

.btn-primary:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.btn-secondary {
  background-color: #f0f0f0;
  color: var(--text-primary);
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

@media (max-width: 768px) {
  .form-container {
    padding: 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  h1 {
    font-size: 24px;
  }
}
</style>
