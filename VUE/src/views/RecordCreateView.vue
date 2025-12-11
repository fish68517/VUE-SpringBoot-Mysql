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
                placeholder="例如：日本·东京"
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
          </el-form-item>
        </div>

        <el-form-item label="精彩瞬间 (图片/视频)" class="upload-item">
          <div class="upload-wrapper">
            <el-upload
              v-model:file-list="fileList"
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :on-preview="handlePictureCardPreview"
              :on-remove="handleRemove"
              accept="image/*,video/*"
              multiple
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">
              支持上传 JPG, PNG 图片或 MP4 视频，记录你的旅途风光
            </div>
          </div>
          
          <el-dialog v-model="dialogVisible">
            <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%" />
          </el-dialog>
        </el-form-item>

        <el-form-item label="简短描述" prop="description">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="2"
              placeholder="一句话概括这次旅行..."
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
            {{ loading ? uploadStatus : '发布旅行记忆' }}
          </el-button>

          <el-button
            size="large"
            @click="handleCancel"
            class="cancel-btn"
            :disabled="loading"
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
import { CircleCloseFilled, Plus } from '@element-plus/icons-vue'
import 'quill/dist/quill.snow.css'

// 导入服务
import { travelService } from '../services/travelService'
import { fileService } from '../services/fileService' // 确保你创建了这个文件
import { useFormValidation } from '../composables/useFormValidation'

const router = useRouter()
const formRef = ref(null)
const editorRef = ref(null)
const loading = ref(false)
const uploadStatus = ref('正在发布...')
const submitted = ref(false)
let quillEditor = null

// 上传相关状态
const fileList = ref([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const form = reactive({
  title: '',
  destination: '',
  startDate: null,
  endDate: null,
  description: '',
  diaryContent: '',
  isPublic: true
})

// 校验规则
const rules = {
  title: [{ required: true, message: '请填写旅行标题', trigger: 'blur' }],
  destination: [{ required: true, message: '请填写目的地', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  diaryContent: [{ 
    validator: (rule, value, cb) => {
      if (!quillEditor?.root.innerHTML || quillEditor.root.innerHTML === '<p><br></p>') {
        cb(new Error('请填写内容'))
      } else {
        cb()
      }
    }, 
    trigger: 'blur' 
  }]
}

const { hasFieldError } = useFormValidation(form, rules)

// 初始化编辑器
onMounted(async () => {
  const Quill = (await import('quill')).default
  await nextTick()
  quillEditor = new Quill(editorRef.value, {
    theme: 'snow',
    placeholder: '在此处开始书写你的旅行故事...',
    modules: {
      toolbar: [
        [{ header: [1, 2, false] }],
        ['bold', 'italic', 'underline'],
        [{ list: 'ordered' }, { list: 'bullet' }],
        ['image'],
        ['clean']
      ]
    }
  })
  quillEditor.on('text-change', () => {
    form.diaryContent = quillEditor.root.innerHTML
  })
})

// 处理文件预览
const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url
  dialogVisible.value = true
}

const handleRemove = (uploadFile) => {
  console.log('Remove', uploadFile)
}

// *** 核心逻辑：创建记录 + 上传文件 ***
const handleCreateRecord = async () => {
  if (!formRef.value) return
  submitted.value = true

  try {
    await formRef.value.validate()
    
    // 二次检查内容
    if (!quillEditor || !form.diaryContent || form.diaryContent === '<p><br></p>') {
      ElMessage.warning('请填写日记内容')
      return
    }

    loading.value = true
    uploadStatus.value = '正在创建记录...'

    // 1. 先创建旅行记录数据
    const recordData = {
      title: form.title.trim(),
      destination: form.destination.trim(),
      startDate: form.startDate,
      endDate: form.endDate,
      description: form.description.trim(),
      diaryContent: form.diaryContent,
      isPublic: form.isPublic
    }

    // 调用创建接口，获取 travelRecordId
    const response = await travelService.createTravelRecord(recordData)
    // 假设后端返回结构是 { code: 200, data: { id: 123, ... } }
    // 如果直接返回对象，请调整为 const newRecordId = response.id
    const newRecordId = response.data ? response.data.id : response.id 

    if (!newRecordId) {
      throw new Error('未获取到旅行记录ID')
    }

    // 2. 如果有文件，开始上传
    if (fileList.value.length > 0) {
      uploadStatus.value = `正在上传文件 (0/${fileList.value.length})...`
      
      let successCount = 0
      // 遍历文件列表上传
      for (const fileItem of fileList.value) {
        try {
          // fileItem.raw 是 ElementPlus 中原始的 File 对象
          await fileService.uploadFile(newRecordId, fileItem.raw)
          successCount++
          uploadStatus.value = `正在上传文件 (${successCount}/${fileList.value.length})...`
        } catch (uploadErr) {
          console.error(`文件 ${fileItem.name} 上传失败`, uploadErr)
          ElMessage.error(`文件 ${fileItem.name} 上传失败`)
          // 可以选择是否中断，这里选择继续传下一个
        }
      }
    }

    ElMessage.success('发布成功！')
    
    // 3. 跳转到详情页
    router.push(`/travel/${newRecordId}`)

  } catch (err) {
    console.error(err)
    ElMessage.error(err.message || '发布失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => router.back()
</script>

<style scoped>
/* 保持之前的样式，新增 Upload 相关样式 */
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

.travel-form {
  max-width: 100%;
}

.form-field-wrapper {
  width: 100%;
}

:deep(.el-input),
:deep(.el-textarea),
:deep(.el-select),
:deep(.el-date-editor) {
  width: 100% !important;
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

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  padding-bottom: 8px;
}

.el-form-item {
  margin-bottom: 28px;
}

/* 编辑器样式 */
.diary-editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.diary-editor-wrapper:hover {
  border-color: #c0c4cc;
}

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
  min-height: 300px;
}

:deep(.ql-editor) {
  padding: 20px;
  min-height: 300px;
}

/* 新增上传样式 */
.upload-wrapper {
  width: 100%;
}
.upload-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
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
</style>