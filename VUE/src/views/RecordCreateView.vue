<template>
  <div class="record-create-container">
    <div class="record-create-card">
      <h2>撰写旅行记忆</h2>
      <p class="tip">记录下这次旅行的点点滴滴，留住最珍贵的回忆～</p>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        @submit.prevent="handleCreateRecord"
      >
        <!-- 记录标题 -->
        <el-form-item label="旅行标题" prop="title">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.title"
              placeholder="例如：2024东京樱花季8天7夜 | 一个人在樱花雨里走了800公里"
              clearable
              maxlength="200"
              show-word-limit
            />
            <div v-if="hasFieldError('title')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('title') }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 目的地 -->
        <el-form-item label="目的地" prop="destination">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.destination"
              placeholder="例如：日本·东京、泰国·曼谷、四川·稻城亚丁"
              clearable
              maxlength="200"
              show-word-limit
            />
            <div v-if="hasFieldError('destination')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('destination') }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 开始日期 -->
        <el-form-item label="开始日期" prop="startDate">
          <div class="form-field-wrapper">
            <el-date-picker
              v-model="form.startDate"
              type="date"
              placeholder="选择出发日期"
              style="width: 100%"
              :disabled-date="time => time.getTime() > Date.now()"
            />
            <div v-if="hasFieldError('startDate')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('startDate') }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 结束日期 -->
        <el-form-item label="结束日期" prop="endDate">
          <div class="form-field-wrapper">
            <el-date-picker
              v-model="form.endDate"
              type="date"
              placeholder="选择返程日期"
              style="width: 100%"
              :disabled-date="time => form.startDate && time.getTime() < new Date(form.startDate).getTime()"
            />
            <div v-if="hasFieldError('endDate')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('endDate') }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 简短描述 -->
        <el-form-item label="简短描述" prop="description">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="一句话概括这次旅行，比如：一个人在樱花雨里走了800公里～（可选）"
              maxlength="500"
              show-word-limit
            />
          </div>
        </el-form-item>

        <!-- 富文本日记内容 -->
        <el-form-item label="详细日记" prop="diaryContent" v-if="false">
          <div class="diary-editor">
            <div id="editor" ref="editorRef"></div>
            <div v-if="!form.diaryContent && submitted" class="editor-hint">
              请填写详细的旅行日记内容哦～
            </div>
          </div>
        </el-form-item>

        <!-- 公开/私密 -->
        <el-form-item label="可见性" prop="isPublic" v-if="false">
          <el-radio-group v-model="form.isPublic" size="large">
            <el-radio :label="false">
              <el-icon><Lock /></el-icon> 仅自己可见（私密）
            </el-radio>
            <el-radio :label="true">
              <el-icon><View /></el-icon> 公开分享（所有人可见）
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleCreateRecord"
            :loading="loading"
            style="width: 100%; height: 52px; font-size: 16px"
          >
            {{ loading ? '正在发布...' : '发布旅行记忆' }}
          </el-button>

          <el-button
            size="large"
            @click="handleCancel"
            style="width: 100%; margin-top: 14px; margin-left: 0"
          >
            取消返回
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 错误提示 -->
      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        closable
        @close="errorMessage = ''"
        style="margin-top: 24px"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCloseFilled, Lock, View } from '@element-plus/icons-vue'
import { travelService } from '../services/travelService'
import { useUserStore } from '../stores/userStore'
import { useFormValidation } from '../composables/useFormValidation'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const editorRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const submitted = ref(false)
let quillEditor = null

const form = reactive({
  title: '',
  destination: '',
  startDate: null,
  endDate: null,
  description: '',
  diaryContent: '',
  isPublic: true
})

// 全中文校验规则
const rules = {
  title: [
    { required: true, message: '请填写旅行标题', trigger: 'blur' },
    { min: 2, max: 200, message: '标题长度为2-200个字符', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: '请填写目的地', trigger: 'blur' },
    { min: 2, max: 200, message: '目的地长度为2-200个字符', trigger: 'blur' }
  ],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (form.startDate && value && value < form.startDate) {
          callback(new Error('结束日期不能早于开始日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  diaryContent: [
    {
      validator: (rule, value, callback) => {
        if (!quillEditor?.root.innerHTML || quillEditor.root.innerHTML === '<p><br></p>') {
          callback(new Error('请填写详细的旅行日记内容'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 表单校验
const { validateField, getFieldError, hasFieldError } = useFormValidation(form, {
  title: { required: true, label: '旅行标题', minLength: 2, maxLength: 200 },
  destination: { required: true, label: '目的地', minLength: 2, maxLength: 200 },
  startDate: { required: true, label: '开始日期' },
  endDate: { required: true, label: '结束日期' },
})

// 初始化 Quill 富文本编辑器
onMounted(async () => {
  const Quill = (await import('quill')).default
  await nextTick()

  quillEditor = new Quill(editorRef.value, {
    theme: 'snow',
    placeholder: '开始记录你的旅行吧～\n这一路的风景、美食、人情、感动，都可以写在这里……',
    modules: {
      toolbar: [
        [{ header: [1, 2, 3, false] }],
        ['bold', 'italic', 'underline'],
        [{ list: 'ordered' }, { list: 'bullet' }],
        ['link', 'image'],
        ['clean']
      ]
    }
  })

  quillEditor.on('text-change', () => {
    form.diaryContent = quillEditor.root.innerHTML
  })
})

const handleCreateRecord = async () => {
  if (!formRef.value) return
  submitted.value = true

  try {
    await formRef.value.validate()
    if (!quillEditor || !form.diaryContent || form.diaryContent === '<p><br></p>') {
      ElMessage.warning('请填写详细的旅行日记内容哦～')
      return
    }

    loading.value = true
    errorMessage.value = ''

    const recordData = {
      title: form.title.trim(),
      destination: form.destination.trim(),
      startDate: form.startDate,
      endDate: form.endDate,
      description: form.description.trim(),
      diaryContent: form.diaryContent,
      isPublic: form.isPublic
    }

    const response = await travelService.createTravelRecord(recordData)

    ElMessage.success('旅行记忆发布成功！')
    router.push(`/records/${response.data.id}`)
  } catch (err) {
    loading.value = false
    errorMessage.value = err.message || '发布失败，请稍后重试'
  }
}

const handleCancel = () => router.back()
</script>

<style scoped>
.record-create-container {
  max-width: 920px;
  margin: 20px auto;
  padding: 20px;
}

.record-create-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 44px;
  box-shadow: 0 12px 40px rgba(0,0,0,0.1);
}

.record-create-card h2 {
  text-align: center;
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
  font-weight: 600;
}

.tip {
  text-align: center;
  color: #909399;
  margin-bottom: 40px;
  font-size: 15px;
}

.diary-editor {
  border: 1px solid #dcdfe6;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.editor-hint {
  padding: 12px 16px;
  color: #f56c6c;
  font-size: 13px;
  background: #fef0f0;
}

:deep(.ql-container) {
  min-height: 380px;
  font-size: 15px;
  background: #fafafa;
}

:deep(.ql-toolbar) {
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
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
}
</style>