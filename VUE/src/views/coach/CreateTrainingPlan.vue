<template>
  <Layout>
    <div class="create-training-plan">
      <div class="page-header">
        <h2>{{ isEditMode ? 'Edit Training Plan' : 'Create Training Plan' }}</h2>
        <el-button @click="goBack">Back</el-button>
      </div>

      <el-card>
        <el-form
          ref="formRef"
          :model="planForm"
          :rules="rules"
          label-width="120px"
          label-position="top"
        >
          <!-- Student Selector -->
          <el-form-item label="Student" prop="studentId">
            <el-select
              v-model="planForm.studentId"
              placeholder="Select a student"
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

          <!-- Plan Name -->
          <el-form-item label="Plan Name" prop="name">
            <el-input
              v-model="planForm.name"
              placeholder="Enter plan name"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <!-- Description -->
          <el-form-item label="Description" prop="description">
            <el-input
              v-model="planForm.description"
              type="textarea"
              :rows="4"
              placeholder="Enter plan description"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <!-- Date Range -->
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="Start Date" prop="startDate">
                <el-date-picker
                  v-model="planForm.startDate"
                  type="date"
                  placeholder="Select start date"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="End Date" prop="endDate">
                <el-date-picker
                  v-model="planForm.endDate"
                  type="date"
                  placeholder="Select end date"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- Status -->
          <el-form-item label="Status" prop="status">
            <el-radio-group v-model="planForm.status">
              <el-radio label="active">Active</el-radio>
              <el-radio label="completed">Completed</el-radio>
              <el-radio label="cancelled">Cancelled</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- Exercises Section -->
          <el-form-item label="Exercises">
            <div class="exercises-section">
              <div
                v-for="(exercise, index) in planForm.exercises"
                :key="index"
                class="exercise-item"
              >
                <div class="exercise-header">
                  <span class="exercise-number">Exercise {{ index + 1 }}</span>
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
                      placeholder="Exercise name"
                      class="exercise-input"
                    />
                  </el-col>
                </el-row>

                <el-row :gutter="15" class="exercise-details">
                  <el-col :span="8">
                    <el-input
                      v-model.number="exercise.sets"
                      type="number"
                      placeholder="Sets"
                      min="1"
                    >
                      <template #prepend>Sets</template>
                    </el-input>
                  </el-col>
                  <el-col :span="8">
                    <el-input
                      v-model.number="exercise.reps"
                      type="number"
                      placeholder="Reps"
                      min="1"
                    >
                      <template #prepend>Reps</template>
                    </el-input>
                  </el-col>
                  <el-col :span="8">
                    <el-input
                      v-model="exercise.duration"
                      placeholder="e.g., 30 min"
                    >
                      <template #prepend>Duration</template>
                    </el-input>
                  </el-col>
                </el-row>

                <el-row :gutter="15">
                  <el-col :span="24">
                    <el-input
                      v-model="exercise.notes"
                      type="textarea"
                      :rows="2"
                      placeholder="Additional notes (optional)"
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
                Add Exercise
              </el-button>
            </div>
          </el-form-item>

          <!-- Form Actions -->
          <el-form-item>
            <el-button type="primary" @click="submitForm" :loading="submitting">
              {{ isEditMode ? 'Update Plan' : 'Create Plan' }}
            </el-button>
            <el-button @click="goBack">Cancel</el-button>
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
    { required: true, message: 'Please select a student', trigger: 'change' }
  ],
  name: [
    { required: true, message: 'Please enter plan name', trigger: 'blur' },
    { min: 3, max: 200, message: 'Length should be 3 to 200 characters', trigger: 'blur' }
  ],
  description: [
    { max: 1000, message: 'Length should not exceed 1000 characters', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: 'Please select start date', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: 'Please select end date', trigger: 'change' }
  ],
  status: [
    { required: true, message: 'Please select status', trigger: 'change' }
  ]
}

const fetchStudents = async () => {
  try {
    const response = await getMyStudents()
    students.value = response
  } catch (error) {
    showError('Failed to load students')
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
      showWarning('Please fill in all required fields')
      return
    }

    if (planForm.exercises.length === 0) {
      showWarning('Please add at least one exercise')
      return
    }

    // Validate exercises
    const hasInvalidExercise = planForm.exercises.some(ex => !ex.name.trim())
    if (hasInvalidExercise) {
      showWarning('Please provide names for all exercises')
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
        showSuccess('Training plan updated successfully')
      } else {
        await createTrainingPlan(data)
        showSuccess('Training plan created successfully')
      }

      router.push('/coach/dashboard')
    } catch (error) {
      showError(error.message || 'Failed to save training plan')
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
    showError('Failed to load plan data')
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
