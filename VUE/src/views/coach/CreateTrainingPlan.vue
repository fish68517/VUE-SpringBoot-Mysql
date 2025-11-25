没问题！这是 “创建/编辑训练计划” (Create/Edit Training Plan) 页面的完整汉化版本。

主要修改包括：

界面文本：Create Training Plan -> 创建训练计划, Edit Training Plan -> 编辑训练计划, Student -> 学员 等。

表单标签：Plan Name -> 计划名称, Description -> 计划描述, Date Range -> 日期范围, Status -> 状态 等。

状态选项：Active -> 进行中, Completed -> 已完成, Cancelled -> 已取消。

训练动作：Exercises -> 训练动作, Sets -> 组数, Reps -> 次数, Duration -> 时长 等。

校验规则：必填项提示、长度限制提示等。

反馈提示：创建成功、更新成功、加载失败等。

请复制以下代码覆盖：

code
Html
play_circle
download
content_copy
expand_less
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
          <!-- 学员选择 (Student Selector) -->
          <el-form-item label="学员" prop="studentId">
            <el-select
              v-model="planForm.studentId"
              placeholder="请选择一位学员"
              style="width: 100%"
              :disabled="isEditMode"
            >
              <el-option
                v-for="student in students"
                :key="student.id"
                :label="student.username"
                :value="student.id"
              >
                <div class="student-option">
                  <el-avatar :src="student.avatar" :size="30">
                    {{ student.username.charAt(0).toUpperCase() }}
                  </el-avatar>
                  <span>{{ student.username }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 计划名称 (Plan Name) -->
          <el-form-item label="计划名称" prop="name">
            <el-input
              v-model="planForm.name"
              placeholder="请输入计划名称"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <!-- 计划描述 (Description) -->
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

          <!-- 日期范围 (Date Range) -->
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

          <!-- 状态 (Status) -->
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="planForm.status">
              <el-radio label="active">进行中</el-radio>
              <el-radio label="completed">已完成</el-radio>
              <el-radio label="cancelled">已取消</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 训练动作部分 (Exercises Section) -->
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
                      placeholder="动作名称 (如: 深蹲)"
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
                      placeholder="如: 30分钟"
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
                      placeholder="备注信息 (可选)"
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

          <!-- 表单操作 (Form Actions) -->
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Plus, ArrowUp, ArrowDown, Delete } from '@element-plus/icons-vue'
import Layout from '@/components/common/Layout.vue'
import { getMyStudents } from '@/api/coach'
import { createTrainingPlan, updateTrainingPlan, getTrainingPlanById } from '@/api/training'
import { showSuccess, showError, showWarning } from '@/utils/feedback'

const router = useRouter()
const route = useRoute()

const formRef = ref(null)
const students = ref([])
const submitting = ref(false)
const isEditMode = ref(false)
const planId = ref(null)

const planForm = reactive({
  studentId: null,
  name: '',
  description: '',
  startDate: '',
  endDate: '',
  status: 'active',
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
    students.value = response
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

    // Validate exercises
    const hasInvalidExercise = planForm.exercises.some(ex => !ex.name.trim())
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
    planForm.exercises = typeof plan.exercises === 'string' 
      ? JSON.parse(plan.exercises) 
      : plan.exercises || []
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

  // Check if editing existing plan
  if (route.params.id) {
    isEditMode.value = true
    planId.value = route.params.id
    await loadPlanData(planId.value)
  } else {
    // Add one default exercise for new plans
    addExercise()
  }
})
</script>

<style scoped>
/* 样式保持不变 */
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

.exercises-section {
  width: 100%;
}

.exercise-item {
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  background-color: #F5F7FA;
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

  .exercise-actions .el-button {
    padding: 5px;
  }
}
</style>