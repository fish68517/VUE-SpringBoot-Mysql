<template>
  <Layout>
    <div class="create-training-plan">
      <div class="page-header">
        <h2>{{ isEditMode ? '编辑训练计划' : '创建训练计划' }}</h2>
        <el-button @click="goBack">返回</el-button>
      </div>

      <el-card>
        <el-form
          ref="formRef"
          :model="planForm"
          :rules="rules"
          label-width="120px"
          label-position="top"
        >
          <el-form-item label="学员" prop="studentId">
            <el-select
              v-model="planForm.studentId"
              placeholder="请选择一位学员"
              style="width: 100%"
              :disabled="isEditMode"
            >
              <el-option
                v-for="studentItem in students"
                :key="studentItem.id || studentItem.studentId"
                :label="studentItem.student?.username || `Student ${studentItem.studentId}`"
                :value="studentItem.studentId"
              >
                <div class="student-option">
                  <el-avatar :src="studentItem.student?.avatar || studentItem.avatar" :size="30">
                    {{ (studentItem.student?.username || '').charAt(0).toUpperCase() }}
                  </el-avatar>
                  <span>{{ studentItem.student?.username || `Student ${studentItem.studentId}` }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="计划名称" prop="name">
            <el-input
              v-model="planForm.name"
              placeholder="请输入计划名称"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="计划描述" prop="description">
            <el-input
              v-model="planForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入计划描述"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="训练视频">
            <div class="plan-video-section">
              <el-upload
                :show-file-list="false"
                :http-request="handlePlanVideoUpload"
                :before-upload="beforePlanVideoUpload"
                accept="video/*"
              >
                <el-button
                  type="primary"
                  plain
                  :loading="uploadingVideo"
                  :disabled="uploadingVideo || !!planForm.videoUrl"
                >
                  上传视频
                </el-button>
              </el-upload>

              <div v-if="planForm.videoUrl" class="video-preview-wrapper">
                <div class="video-preview-header">
                  <span class="video-name">{{ planForm.videoName || '已上传视频' }}</span>
                  <el-button type="danger" link @click="removePlanVideo">
                    删除视频重新上传
                  </el-button>
                </div>
                <video :src="planForm.videoUrl" controls class="plan-video-preview" />
              </div>
            </div>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="开始日期" prop="startDate">
                <el-date-picker
                  v-model="planForm.startDate"
                  type="date"
                  placeholder="选择开始日期"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="结束日期" prop="endDate">
                <el-date-picker
                  v-model="planForm.endDate"
                  type="date"
                  placeholder="选择结束日期"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="planForm.status">
              <el-radio label="active">进行中</el-radio>
              <el-radio label="completed">已完成</el-radio>
              <el-radio label="cancelled">已取消</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="训练动作">
            <div class="exercises-section">
              <div
                v-for="(exercise, index) in planForm.exercises"
                :key="index"
                class="exercise-item"
              >
                <div class="exercise-header">
                  <span class="exercise-number">动作 {{ index + 1 }}</span>
                  <div class="exercise-actions">
                    <el-button
                      size="small"
                      :disabled="index === 0"
                      @click="moveExercise(index, 'up')"
                    >
                      <el-icon><ArrowUp /></el-icon>
                    </el-button>
                    <el-button
                      size="small"
                      :disabled="index === planForm.exercises.length - 1"
                      @click="moveExercise(index, 'down')"
                    >
                      <el-icon><ArrowDown /></el-icon>
                    </el-button>
                    <el-button
                      size="small"
                      type="danger"
                      @click="removeExercise(index)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>

                <el-row :gutter="15">
                  <el-col :span="24">
                    <el-input
                      v-model="exercise.name"
                      placeholder="动作名称"
                      class="exercise-input"
                    />
                  </el-col>
                </el-row>

                <el-row :gutter="15" class="exercise-details">
                  <el-col :span="8">
                    <el-input
                      v-model.number="exercise.sets"
                      type="number"
                      placeholder="组数"
                      min="1"
                    >
                      <template #prepend>组数</template>
                    </el-input>
                  </el-col>
                  <el-col :span="8">
                    <el-input
                      v-model.number="exercise.reps"
                      type="number"
                      placeholder="次数"
                      min="1"
                    >
                      <template #prepend>次数</template>
                    </el-input>
                  </el-col>
                  <el-col :span="8">
                    <el-input
                      v-model="exercise.duration"
                      placeholder="如：30分钟"
                    >
                      <template #prepend>时长</template>
                    </el-input>
                  </el-col>
                </el-row>

                <el-row :gutter="15">
                  <el-col :span="24">
                    <el-input
                      v-model="exercise.notes"
                      type="textarea"
                      :rows="2"
                      placeholder="备注信息（可选）"
                    />
                  </el-col>
                </el-row>
              </div>

              <el-button
                type="primary"
                plain
                @click="addExercise"
                class="add-exercise-btn"
              >
                <el-icon><Plus /></el-icon>
                添加动作
              </el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm" :loading="submitting">
              {{ isEditMode ? '更新计划' : '创建计划' }}
            </el-button>
            <el-button @click="goBack">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, ArrowUp, Delete, Plus } from '@element-plus/icons-vue'
import Layout from '@/components/common/Layout.vue'
import { getMyStudents } from '@/api/coach'
import { createTrainingPlan, getTrainingPlanById, updateTrainingPlan } from '@/api/training'
import { uploadVideo } from '@/api/upload'
import { showError, showSuccess, showWarning } from '@/utils/feedback'

const router = useRouter()
const route = useRoute()

const formRef = ref(null)
const students = ref([])
const submitting = ref(false)
const isEditMode = ref(false)
const planId = ref(null)
const uploadingVideo = ref(false)

const planForm = reactive({
  studentId: null,
  name: '',
  description: '',
  startDate: '',
  endDate: '',
  status: 'active',
  videoUrl: '',
  videoName: '',
  exercises: []
})

const rules = {
  studentId: [
    { required: true, message: '请选择一位学员', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入计划名称', trigger: 'blur' },
    { min: 3, max: 200, message: '名称长度应在 3 到 200 个字符之间', trigger: 'blur' }
  ],
  description: [
    { max: 1000, message: '描述长度不能超过 1000 个字符', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const fetchStudents = async () => {
  try {
    const response = await getMyStudents()
    students.value = (response || [])
      .map((item) => ({
        ...item,
        studentId: item?.student?.id ?? item?.studentId
      }))
      .filter((item) => item.studentId != null && item?.status !== 0 && item?.status !== '0')
  } catch (error) {
    showError('加载学员列表失败')
    console.error('Fetch students error:', error)
  }
}

const addExercise = () => {
  planForm.exercises.push({
    name: '',
    sets: null,
    reps: null,
    duration: '',
    notes: ''
  })
}

const removeExercise = (index) => {
  planForm.exercises.splice(index, 1)
}

const moveExercise = (index, direction) => {
  const newIndex = direction === 'up' ? index - 1 : index + 1
  const temp = planForm.exercises[index]
  planForm.exercises[index] = planForm.exercises[newIndex]
  planForm.exercises[newIndex] = temp
}

const beforePlanVideoUpload = (file) => {
  const isLt100M = file.size / 1024 / 1024 < 100
  if (!isLt100M) {
    showWarning('视频大小不能超过 100MB')
  }
  return isLt100M
}

const handlePlanVideoUpload = async ({ file, onSuccess, onError }) => {
  uploadingVideo.value = true
  try {
    const response = await uploadVideo(file)
    planForm.videoUrl = response?.url || ''
    planForm.videoName = response?.filename || file.name
    showSuccess('训练视频上传成功')
    onSuccess?.(response)
  } catch (error) {
    showError(error.message || '训练视频上传失败')
    onError?.(error)
  } finally {
    uploadingVideo.value = false
  }
}

const removePlanVideo = () => {
  planForm.videoUrl = ''
  planForm.videoName = ''
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) {
      showWarning('请完善表单信息')
      return
    }

    if (planForm.exercises.length === 0) {
      showWarning('请至少添加一个训练动作')
      return
    }

    const hasInvalidExercise = planForm.exercises.some((exercise) => !exercise.name.trim())
    if (hasInvalidExercise) {
      showWarning('请为所有动作填写名称')
      return
    }

    submitting.value = true
    try {
      const data = {
        ...planForm,
        exercises: JSON.stringify(planForm.exercises)
      }

      if (isEditMode.value) {
        await updateTrainingPlan(planId.value, data)
        showSuccess('训练计划更新成功')
      } else {
        await createTrainingPlan(data)
        showSuccess('训练计划创建成功')
      }

      router.push('/coach/dashboard')
    } catch (error) {
      showError(error.message || '保存训练计划失败')
    } finally {
      submitting.value = false
    }
  })
}

const loadPlanData = async (id) => {
  try {
    const plan = await getTrainingPlanById(id)
    planForm.studentId = plan.studentId
    planForm.name = plan.name
    planForm.description = plan.description
    planForm.startDate = plan.startDate
    planForm.endDate = plan.endDate
    planForm.status = plan.status
    planForm.videoUrl = plan.videoUrl || ''
    planForm.videoName = plan.videoName || ''
    planForm.exercises = typeof plan.exercises === 'string'
      ? JSON.parse(plan.exercises)
      : (plan.exercises || [])
  } catch (error) {
    showError('加载计划数据失败')
    console.error('Load plan error:', error)
  }
}

const goBack = () => {
  router.back()
}

onMounted(async () => {
  await fetchStudents()

  if (route.params.id) {
    isEditMode.value = true
    planId.value = route.params.id
    await loadPlanData(planId.value)
  } else {
    addExercise()
  }
})
</script>

<style scoped>
.create-training-plan {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.student-option {
  display: flex;
  align-items: center;
  gap: 10px;
}

.plan-video-section {
  width: 100%;
}

.video-preview-wrapper {
  margin-top: 12px;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  background-color: #f8fafc;
}

.video-preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.video-name {
  color: #303133;
  font-weight: 500;
  word-break: break-all;
}

.plan-video-preview {
  width: 100%;
  max-height: 360px;
  border-radius: 8px;
  background-color: #000;
}

.exercises-section {
  width: 100%;
}

.exercise-item {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  background-color: #f5f7fa;
}

.exercise-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.exercise-number {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.exercise-actions {
  display: flex;
  gap: 5px;
}

.exercise-input {
  margin-bottom: 10px;
}

.exercise-details {
  margin-top: 10px;
  margin-bottom: 10px;
}

.add-exercise-btn {
  width: 100%;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .create-training-plan {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .video-preview-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .exercise-actions .el-button {
    padding: 5px;
  }
}
</style>
