<template>
  <div class="record-create-container">
    <div class="record-create-card">
      <div class="card-header">
        <h2>撰写旅行记忆</h2>
        <p class="tip">记录这次旅程的风景、情绪和故事，发布为一篇完整的旅行日志。</p>
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
          <el-input
            v-model="form.title"
            placeholder="例如：2026 春天在苏州园林慢行三日"
            clearable
            maxlength="200"
            show-word-limit
            size="large"
          />
        </el-form-item>

        <div class="form-row">
          <el-form-item label="目的地" prop="destination" class="row-item">
            <el-input
              v-model="form.destination"
              placeholder="例如：江苏·苏州"
              clearable
              maxlength="200"
              size="large"
            />
          </el-form-item>

          <el-form-item label="时间跨度" required class="row-item">
            <div class="date-range-group">
              <el-form-item prop="startDate" class="inner-date-item">
                <el-date-picker
                  v-model="form.startDate"
                  type="date"
                  placeholder="出发日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
              <span class="date-separator">至</span>
              <el-form-item prop="endDate" class="inner-date-item">
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  placeholder="返程日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </div>
          </el-form-item>
        </div>

        <el-form-item label="精彩瞬间（图片/视频）">
          <div class="upload-wrapper">
            <el-upload
              v-model:file-list="fileList"
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :on-preview="handlePictureCardPreview"
              accept="image/*,video/*"
              multiple
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">支持上传 JPG、PNG 图片或 MP4 视频，创建成功后会自动关联到本篇记录。</div>
          </div>

          <el-dialog v-model="dialogVisible" width="720px">
            <img :src="dialogImageUrl" alt="图片预览" style="width: 100%" />
          </el-dialog>
        </el-form-item>

        <el-form-item label="简短描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="用两三句话概括这次旅行的核心感受"
            maxlength="500"
            show-word-limit
            resize="none"
          />
        </el-form-item>

        <section class="ai-assistant">
          <div class="ai-assistant__head">
            <div>
              <p class="eyebrow">AI 撰写日志</p>
              <h3>根据当前旅行信息，自动生成日志草稿</h3>
            </div>
            <el-button type="success" :loading="aiLoading" @click="handleGenerateDiary">
              {{ aiLoading ? '正在生成...' : '生成日志草稿' }}
            </el-button>
          </div>

          <div class="ai-grid">
            <el-form-item label="写作风格">
              <el-select v-model="aiForm.style" style="width: 100%">
                <el-option label="温暖叙事" value="温暖叙事" />
                <el-option label="轻松随笔" value="轻松随笔" />
                <el-option label="文艺细腻" value="文艺细腻" />
                <el-option label="攻略记录" value="攻略记录" />
              </el-select>
            </el-form-item>

            <el-form-item label="旅行天数">
              <el-input :model-value="travelDaysText" readonly />
            </el-form-item>
          </div>

          <el-form-item label="关键经历">
            <el-input
              v-model="aiForm.highlights"
              type="textarea"
              :rows="3"
              placeholder="例如：清晨在平江路散步、雨天坐船穿过古镇、夜里吃到最好的一碗面"
            />
          </el-form-item>
        </section>

        <el-form-item label="详细日志" prop="diaryContent" class="editor-item">
          <div class="diary-editor-wrapper">
            <div ref="editorRef"></div>
            <div v-if="!form.diaryContent && submitted" class="editor-hint">
              请输入旅行日志正文，或先使用上方 AI 功能生成草稿。
            </div>
          </div>
        </el-form-item>

        <div class="form-actions">
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            @click="handleCreateRecord"
          >
            {{ loading ? uploadStatus : '发布旅行记忆' }}
          </el-button>

          <el-button size="large" class="cancel-btn" :disabled="loading" @click="handleCancel">
            取消返回
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import 'quill/dist/quill.snow.css'
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { aiService } from '../services/aiService'
import { fileService } from '../services/fileService'
import { travelService } from '../services/travelService'

const router = useRouter()
const formRef = ref(null)
const editorRef = ref(null)
const loading = ref(false)
const aiLoading = ref(false)
const uploadStatus = ref('正在发布...')
const submitted = ref(false)
const fileList = ref([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
let quillEditor = null

const form = reactive({
  title: '',
  destination: '',
  startDate: '',
  endDate: '',
  description: '',
  diaryContent: '',
  isPublic: true
})

const aiForm = reactive({
  style: '温暖叙事',
  highlights: ''
})

const rules = {
  title: [{ required: true, message: '请填写旅行标题', trigger: 'blur' }],
  destination: [{ required: true, message: '请填写目的地', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择出发日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择返程日期', trigger: 'change' }],
  diaryContent: [{
    validator: (_rule, _value, callback) => {
      if (!quillEditor || !quillEditor.getText().trim()) {
        callback(new Error('请填写详细日志'))
        return
      }
      callback()
    },
    trigger: 'blur'
  }]
}

const travelDays = computed(() => {
  if (!form.startDate || !form.endDate) return 0
  const start = new Date(form.startDate)
  const end = new Date(form.endDate)
  const diff = end.getTime() - start.getTime()
  if (Number.isNaN(diff) || diff < 0) return 0
  return Math.floor(diff / (24 * 60 * 60 * 1000)) + 1
})

const travelDaysText = computed(() => (travelDays.value > 0 ? `${travelDays.value} 天` : '请先选择日期'))

onMounted(async () => {
  const Quill = (await import('quill')).default
  await nextTick()
  quillEditor = new Quill(editorRef.value, {
    theme: 'snow',
    placeholder: '在这里开始记录你的旅程...',
    modules: {
      toolbar: [
        [{ header: [1, 2, false] }],
        ['bold', 'italic', 'underline'],
        [{ list: 'ordered' }, { list: 'bullet' }],
        ['clean']
      ]
    }
  })
  quillEditor.on('text-change', () => {
    form.diaryContent = quillEditor.root.innerHTML
  })
})

const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url
  dialogVisible.value = true
}

const setEditorContent = (content) => {
  if (!quillEditor) return
  quillEditor.root.innerHTML = content
  form.diaryContent = content
}

const handleGenerateDiary = async () => {
  if (!form.title.trim() || !form.destination.trim() || travelDays.value <= 0) {
    ElMessage.warning('请先填写标题、目的地和日期，再使用 AI 撰写日志')
    return
  }

  aiLoading.value = true
  try {
    const response = await aiService.generateDiary({
      title: form.title.trim(),
      destination: form.destination.trim(),
      travelDays: travelDays.value,
      style: aiForm.style,
      highlights: aiForm.highlights,
      description: form.description
    })
    setEditorContent(response.data.content)
    ElMessage.success('AI 日志草稿已生成，可继续手动修改')
  } catch (error) {
    ElMessage.error(error.message || 'AI 日志生成失败')
  } finally {
    aiLoading.value = false
  }
}

const handleCreateRecord = async () => {
  if (!formRef.value) return
  submitted.value = true

  try {
    await formRef.value.validate()

    if (!form.diaryContent || form.diaryContent === '<p><br></p>') {
      ElMessage.warning('请填写旅行日志内容')
      return
    }

    loading.value = true
    uploadStatus.value = '正在创建记录...'

    const response = await travelService.createTravelRecord({
      title: form.title.trim(),
      destination: form.destination.trim(),
      startDate: form.startDate,
      endDate: form.endDate,
      description: form.description.trim(),
      diaryContent: form.diaryContent,
      isPublic: form.isPublic
    })

    const newRecordId = response.data?.id || response.id
    if (!newRecordId) {
      throw new Error('未获取到旅行记录 ID')
    }

    if (fileList.value.length > 0) {
      let successCount = 0
      for (const fileItem of fileList.value) {
        await fileService.uploadFile(newRecordId, fileItem.raw)
        successCount += 1
        uploadStatus.value = `正在上传文件 (${successCount}/${fileList.value.length})...`
      }
    }

    ElMessage.success('旅行记忆发布成功')
    router.push(`/records/${newRecordId}`)
  } catch (error) {
    ElMessage.error(error.message || '发布失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
.record-create-container {
  max-width: 1080px;
  margin: 40px auto;
  padding: 0 20px;
}

.record-create-card {
  background: #fff;
  border-radius: 18px;
  padding: 40px 50px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.06);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.card-header h2 {
  font-size: 30px;
  color: #1a1a1a;
  margin: 0 0 10px;
}

.tip {
  margin: 0;
  color: #909399;
}

.form-row {
  display: flex;
  gap: 24px;
}

.row-item {
  flex: 1;
}

.date-range-group {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  width: 100%;
}

.inner-date-item {
  margin-bottom: 0 !important;
  flex: 1;
}

.date-separator {
  line-height: 40px;
  color: #606266;
}

.upload-wrapper {
  width: 100%;
}

.upload-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}

.ai-assistant {
  margin-bottom: 28px;
  padding: 24px;
  border-radius: 16px;
  background:
    linear-gradient(135deg, rgba(69, 104, 220, 0.08), rgba(176, 106, 179, 0.1)),
    #f9fbff;
  border: 1px solid rgba(69, 104, 220, 0.12);
}

.ai-assistant__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.ai-assistant__head h3 {
  margin: 4px 0 0;
  color: #1f2d3d;
}

.eyebrow {
  margin: 0;
  color: #4568dc;
  font-weight: 700;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.ai-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.diary-editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.ql-toolbar) {
  border: none !important;
  border-bottom: 1px solid #ebeef5 !important;
  background-color: #f9fafe;
}

:deep(.ql-container) {
  border: none !important;
  min-height: 320px;
}

:deep(.ql-editor) {
  min-height: 320px;
  padding: 20px;
  font-size: 16px;
}

.editor-hint {
  padding: 12px 20px 16px;
  color: #f56c6c;
  font-size: 13px;
}

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
}

.cancel-btn {
  width: 100%;
}

@media (max-width: 768px) {
  .record-create-card {
    padding: 28px 20px;
  }

  .form-row,
  .ai-grid,
  .ai-assistant__head {
    display: flex;
    flex-direction: column;
  }
}
</style>
