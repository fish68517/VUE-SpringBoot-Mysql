<template>
  <div class="edit-manuscript">
    <h1>编辑稿件</h1>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="form-container"
      v-loading="loading"
    >
      <el-form-item label="栏目" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="请选择栏目">
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="标题" prop="title">
        <el-input
          v-model="form.title"
          placeholder="请输入稿件标题"
          maxlength="255"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="摘要" prop="abstractText">
        <el-input
          v-model="form.abstractText"
          type="textarea"
          placeholder="请输入稿件摘要"
          :rows="4"
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          placeholder="请输入稿件内容（可选）"
          :rows="6"
          maxlength="5000"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="上传文件" prop="file">
        <div class="file-section">
          <div v-if="form.currentFilePath" class="current-file">
            <span>当前文件: </span>
            <el-link type="primary" :href="`/image/${form.currentFilePath}`" target="_blank">
              下载
            </el-link>
            <el-button link type="danger" size="small" @click="removeCurrentFile">
              删除
            </el-button>
          </div>
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".pdf,.doc,.docx,.jpg,.png,.gif"
            drag
          >
            <template #default>
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                拖拽文件到此或 <em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 pdf, doc, docx, jpg, png, gif 格式，单个文件不超过 10MB
                </div>
              </template>
            </template>
          </el-upload>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm(formRef)" :loading="submitting">
          保存修改
        </el-button>
        <el-button @click="resetForm(formRef)">重置</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { manuscriptService } from '@/services/manuscriptService'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const uploadRef = ref()
const loading = ref(false)
const submitting = ref(false)
const categories = ref([])

const form = ref({
  categoryId: '',
  title: '',
  abstractText: '',
  content: '',
  currentFilePath: null,
  file: null
})

const rules = {
  categoryId: [
    { required: true, message: '请选择栏目', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 3, max: 255, message: '标题长度在 3 到 255 个字符', trigger: 'blur' }
  ],
  abstractText: [
    { required: true, message: '请输入摘要', trigger: 'blur' },
    { min: 10, max: 1000, message: '摘要长度在 10 到 1000 个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadCategories()
  loadManuscript()
})

const loadCategories = () => {
  // Load categories - for now using mock data
  // In real implementation, this would call a category service
  categories.value = [
    { id: 1, name: '学术论文' },
    { id: 2, name: '科普文章' },
    { id: 3, name: '技术报告' }
  ]
}

const loadManuscript = async () => {
  loading.value = true
  try {
    const manuscriptId = route.params.id
    const response = await manuscriptService.getManuscript(manuscriptId)
    
    if (response.data) {
      const manuscript = response.data
      form.value.categoryId = manuscript.categoryId
      form.value.title = manuscript.title
      form.value.abstractText = manuscript.abstractText
      form.value.content = manuscript.content
      form.value.currentFilePath = manuscript.filePath
    }
  } catch (error) {
    ElMessage.error(error.message || '加载稿件失败')
    setTimeout(() => {
      router.push('/author/manuscripts')
    }, 1500)
  } finally {
    loading.value = false
  }
}

const removeCurrentFile = () => {
  form.value.currentFilePath = null
}

const submitForm = async (formInstance) => {
  if (!formInstance) return

  await formInstance.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const formData = new FormData()
        formData.append('categoryId', form.value.categoryId)
        formData.append('title', form.value.title)
        formData.append('abstractText', form.value.abstractText)
        formData.append('content', form.value.content)

        // Add file if selected
        if (uploadRef.value && uploadRef.value.uploadFiles.length > 0) {
          formData.append('file', uploadRef.value.uploadFiles[0].raw)
        }

        const manuscriptId = route.params.id
        await manuscriptService.updateManuscript(manuscriptId, formData)
        ElMessage.success('稿件修改成功')
        setTimeout(() => {
          router.push('/author/manuscripts')
        }, 1000)
      } catch (error) {
        ElMessage.error(error.message || '修改失败，请重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

const resetForm = (formInstance) => {
  if (!formInstance) return
  loadManuscript()
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const goBack = () => {
  router.push('/author/manuscripts')
}
</script>

<style scoped>
.edit-manuscript {
  padding: 20px;
}

.edit-manuscript h1 {
  margin-bottom: 30px;
  color: #333;
}

.form-container {
  max-width: 600px;
}

.file-section {
  width: 100%;
}

.current-file {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 10px;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style>
