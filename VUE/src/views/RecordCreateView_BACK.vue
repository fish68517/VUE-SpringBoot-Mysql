<template>
  <div class="record-create-container">
    <div class="record-create-card">
      <div class="card-header">
        <h2>撰写旅行记忆</h2>
        <p class="tip">记录下这次旅行的点点滴滴，留住最珍贵的回忆～</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="travel-form"
        @submit.prevent="handleCreateRecord"
      >
        <el-form-item label="旅行标题" prop="title">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.title"
              placeholder="例如：2024东京樱花季8天7夜 | 一个人在樱花雨里走了800公里"
              clearable
              maxlength="200"
              show-word-limit
              size="large"
            />
          </div>
        </el-form-item>

        <div class="form-row">
          <el-form-item label="目的地" prop="destination" class="row-item">
            <div class="form-field-wrapper">
              <el-input
                v-model="form.destination"
                placeholder="例如：日本·东京、泰国·曼谷"
                clearable
                maxlength="200"
                size="large"
              />
            </div>
          </el-form-item>

          <el-form-item label="时间跨度" required class="row-item date-range-item">
            <div class="date-range-group">
              <el-form-item prop="startDate" class="inner-date-item">
                 <el-date-picker
                  v-model="form.startDate"
                  type="date"
                  placeholder="出发日期"
                  size="large"
                  style="width: 100%"
                  :disabled-date="time => time.getTime() > Date.now()"
                />
              </el-form-item>
              <span class="date-separator">至</span>
              <el-form-item prop="endDate" class="inner-date-item">
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  placeholder="返程日期"
                  size="large"
                  style="width: 100%"
                  :disabled-date="time => form.startDate && time.getTime() < new Date(form.startDate).getTime()"
                />
              </el-form-item>
            </div>
             <div v-if="hasFieldError('startDate') || hasFieldError('endDate')" class="field-error">
                <el-icon><CircleCloseFilled /></el-icon>
                <span>请完善起止日期</span>
             </div>
          </el-form-item>
        </div>

        <el-form-item label="简短描述" prop="description">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="一句话概括这次旅行，比如：一个人在樱花雨里走了800公里～（可选）"
              maxlength="500"
              show-word-limit
              resize="none"
            />
          </div>
        </el-form-item>

        <el-form-item label="详细日记" prop="diaryContent" class="editor-item">
          <div class="diary-editor-wrapper">
            <div id="editor" ref="editorRef"></div>
            <div v-if="!form.diaryContent && submitted" class="editor-hint">
              请填写详细的旅行日记内容哦～
            </div>
          </div>
        </el-form-item>

        <div class="form-actions">
          <el-button
            type="primary"
            size="large"
            @click="handleCreateRecord"
            :loading="loading"
            class="submit-btn"
          >
            {{ loading ? '正在发布...' : '发布旅行记忆' }}
          </el-button>

          <el-button
            size="large"
            @click="handleCancel"
            class="cancel-btn"
          >
            取消返回
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCloseFilled } from '@element-plus/icons-vue'
// 假设这些服务和你的项目路径一致，保持不变
import { travelService } from '../services/travelService'
import { useUserStore } from '../stores/userStore'
import { useFormValidation } from '../composables/useFormValidation'

// *** 关键修复 1：引入 Quill 的样式文件 ***
import 'quill/dist/quill.snow.css'

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

// 校验规则保持不变
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
        // 简单的空内容检查
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

const { getFieldError, hasFieldError } = useFormValidation(form, {
  title: { required: true },
  destination: { required: true },
  startDate: { required: true },
  endDate: { required: true },
})

onMounted(async () => {
  const Quill = (await import('quill')).default
  await nextTick()

  quillEditor = new Quill(editorRef.value, {
    theme: 'snow',
    placeholder: '在此处开始书写你的旅行故事...\n支持插入图片、格式化文本',
    modules: {
      toolbar: [
        [{ header: [1, 2, 3, false] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ color: [] }, { background: [] }],
        [{ list: 'ordered' }, { list: 'bullet' }],
        ['link', 'image', 'blockquote'],
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
    const recordData = {
      ...form,
      title: form.title.trim(),
      description: form.description.trim(),
    }

    // 模拟API调用 (或者使用你真实的API)
    // const response = await travelService.createTravelRecord(recordData)
    // router.push(`/records/${response.data.id}`)
    
    // 这里为了演示不报错，暂时注释掉真实请求
    console.log('提交数据:', recordData)
    setTimeout(() => {
        loading.value = false;
        ElMessage.success('发布成功 (演示)');
    }, 1000)

  } catch (err) {
    loading.value = false
    console.error(err)
  }
}

const handleCancel = () => router.back()
</script>

<style scoped>
.record-create-container {
  max-width: 1000px;
  margin: 40px auto;
  padding: 0 20px;
}

.record-create-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px 50px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
}

.card-header {
  text-align: center;
  margin-bottom: 40px;
}

.card-header h2 {
  font-size: 28px;
  color: #1a1a1a;
  margin-bottom: 10px;
  font-weight: 700;
}

.tip {
  color: #909399;
  font-size: 14px;
}

/* *** 关键修复 2：优化表单布局和输入框宽度 *** */

/* 让 Form 整体更紧凑 */
.travel-form {
  max-width: 100%;
}

/* 强制所有输入框占满容器宽度 */
.form-field-wrapper {
  width: 100%;
}

:deep(.el-input),
:deep(.el-textarea),
:deep(.el-select),
:deep(.el-date-editor) {
  width: 100% !important; /* 强制填满 */
}

/* 布局调整：第二行分两列 */
.form-row {
  display: flex;
  gap: 24px;
}

.row-item {
  flex: 1; /* 平分宽度 */
}

/* 日期范围特殊处理 */
.date-range-group {
  display: flex;
  align-items: flex-start; /* 对齐顶部，防止校验错误信息撑开高度 */
  gap: 10px;
  width: 100%;
}

.inner-date-item {
  margin-bottom: 0 !important;
  flex: 1;
}

.date-separator {
  line-height: 40px; /* 与大号输入框高度对齐 */
  color: #606266;
}

/* Label 样式优化 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  padding-bottom: 8px; /* label和input之间的间距 */
}

.el-form-item {
  margin-bottom: 28px; /* 增加表单项之间的垂直间距 */
}

/* *** 关键修复 3：Quill 编辑器美化 *** */
.diary-editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.diary-editor-wrapper:hover {
  border-color: #c0c4cc;
}

/* 使用 :deep 穿透修改 Quill 内部样式 */
:deep(.ql-toolbar) {
  border: none !important;
  border-bottom: 1px solid #ebeef5 !important;
  background-color: #f9fafe;
  padding: 12px;
}

:deep(.ql-container) {
  border: none !important;
  background-color: #fff;
  font-size: 16px;
  min-height: 400px; /* 增加编辑器高度 */
}

:deep(.ql-editor) {
  padding: 20px;
  min-height: 400px;
}

/* 按钮区域 */
.form-actions {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.submit-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  letter-spacing: 1px;
}

.cancel-btn {
  width: 100%;
  border: none;
  background: transparent;
  color: #909399;
}

.cancel-btn:hover {
  background: #f5f7fa;
  color: #606266;
}

.field-error {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 12px;
  color: #f56c6c;
}
</style>