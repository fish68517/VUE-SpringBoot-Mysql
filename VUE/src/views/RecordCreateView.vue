<template>
  <div class="record-create-container">
    <div class="record-create-card">
      <h2>Create Travel Record</h2>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        @submit.prevent="handleCreateRecord"
      >
        <el-form-item label="Title" prop="title">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.title"
              placeholder="Enter record title"
              clearable
              maxlength="200"
              show-word-limit
              @blur="validateField('title')"
            />
            <div v-if="hasFieldError('title')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('title') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Destination" prop="destination">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.destination"
              placeholder="Enter destination"
              clearable
              maxlength="200"
              show-word-limit
              @blur="validateField('destination')"
            />
            <div v-if="hasFieldError('destination')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('destination') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Start Date" prop="startDate">
          <div class="form-field-wrapper">
            <el-date-picker
              v-model="form.startDate"
              type="date"
              placeholder="Select start date"
              style="width: 100%"
              @blur="validateField('startDate')"
            />
            <div v-if="hasFieldError('startDate')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('startDate') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="End Date" prop="endDate">
          <div class="form-field-wrapper">
            <el-date-picker
              v-model="form.endDate"
              type="date"
              placeholder="Select end date"
              style="width: 100%"
              @blur="validateField('endDate')"
            />
            <div v-if="hasFieldError('endDate')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('endDate') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Description" prop="description">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.description"
              type="textarea"
              placeholder="Enter a brief description"
              rows="3"
              maxlength="500"
              show-word-limit
              @blur="validateField('description')"
            />
            <div v-if="hasFieldError('description')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('description') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Diary Content" prop="diaryContent">
          <div class="diary-editor">
            <div id="editor" ref="editorRef"></div>
          </div>
        </el-form-item>

        <el-form-item label="Privacy" prop="isPublic">
          <el-radio-group v-model="form.isPublic">
            <el-radio :label="false">Private</el-radio>
            <el-radio :label="true">Public</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="handleCreateRecord"
            :loading="loading"
            style="width: 100%"
          >
            Create Record
          </el-button>
          <el-button
            @click="handleCancel"
            style="width: 100%; margin-top: 10px"
          >
            Cancel
          </el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        closable
        @close="errorMessage = ''"
        style="margin-top: 20px"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { travelService } from '../services/travelService'
import { useUserStore } from '../stores/userStore'
import { useFormValidation } from '../composables/useFormValidation'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const editorRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')
let quillEditor = null

const form = reactive({
  title: '',
  destination: '',
  startDate: null,
  endDate: null,
  description: '',
  diaryContent: '',
  isPublic: false
})

// Validation rules
const rules = {
  title: [
    { required: true, message: 'Title is required', trigger: 'blur' },
    { min: 1, max: 200, message: 'Title must be between 1 and 200 characters', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: 'Destination is required', trigger: 'blur' },
    { min: 1, max: 200, message: 'Destination must be between 1 and 200 characters', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: 'Start date is required', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: 'End date is required', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (form.startDate && value && value < form.startDate) {
          callback(new Error('End date must be after or equal to start date'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  description: [
    { max: 500, message: 'Description must not exceed 500 characters', trigger: 'blur' }
  ]
}

// Use form validation composable
const {
  validateField,
  getFieldError,
  hasFieldError
} = useFormValidation(form, {
  title: { required: true, minLength: 1, maxLength: 200 },
  destination: { required: true, minLength: 1, maxLength: 200 },
  startDate: { required: true },
  endDate: {
    required: true,
    validator: (value) => {
      if (form.startDate && value && value < form.startDate) {
        return { isValid: false, message: 'End date must be after or equal to start date' }
      }
      return { isValid: true, message: '' }
    }
  },
  description: { maxLength: 500 }
})

// Initialize Quill editor
onMounted(async () => {
  // Dynamically import Quill
  const Quill = (await import('quill')).default
  
  quillEditor = new Quill(editorRef.value, {
    theme: 'snow',
    placeholder: 'Enter your diary content here...',
    modules: {
      toolbar: [
        ['bold', 'italic', 'underline', 'strike'],
        ['blockquote', 'code-block'],
        [{ 'header': 1 }, { 'header': 2 }],
        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
        ['link', 'image'],
        ['clean']
      ]
    }
  })

  // Update form when editor content changes
  quillEditor.on('text-change', () => {
    form.diaryContent = quillEditor.root.innerHTML
  })
})

const handleCreateRecord = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    
    // Check if user is authenticated
    if (!userStore.isAuthenticated) {
      errorMessage.value = 'You must be logged in to create a record'
      return
    }

    loading.value = true
    errorMessage.value = ''

    // Prepare data for API call
    const recordData = {
      title: form.title,
      destination: form.destination,
      startDate: form.startDate,
      endDate: form.endDate,
      description: form.description,
      diaryContent: form.diaryContent,
      isPublic: form.isPublic
    }

    // Call API to create record
    const response = await travelService.createTravelRecord(recordData)

    ElMessage.success('Travel record created successfully!')
    
    // Redirect to record detail page
    router.push(`/records/${response.data.id}`)
  } catch (error) {
    loading.value = false
    
    if (error.message) {
      errorMessage.value = error.message
    } else if (error.errors && error.errors.length > 0) {
      errorMessage.value = error.errors[0].message
    } else {
      errorMessage.value = 'Failed to create travel record. Please try again.'
    }
  }
}

const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
.record-create-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.record-create-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.record-create-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.diary-editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

:deep(.ql-container) {
  font-size: 14px;
  min-height: 300px;
}

:deep(.ql-editor) {
  min-height: 300px;
}

:deep(.ql-toolbar) {
  border-bottom: 1px solid #dcdfe6;
}

.form-field-wrapper {
  position: relative;
}

.field-error {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 12px;
  color: #f56c6c;
  line-height: 1;
}

.field-error :deep(.el-icon) {
  font-size: 14px;
  flex-shrink: 0;
}
</style>
