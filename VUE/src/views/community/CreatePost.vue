没问题！我已经将 “发布动态” (Create Post) 页面进行了完整的汉化，包括表单标签、占位符、上传提示以及操作按钮的反馈信息。

请复制以下代码覆盖你原来的文件：

code
Html
play_circle
download
content_copy
expand_less
<template>
  <div class="create-post">
    <div class="page-header">
      <h2>发布动态</h2>
      <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
    </div>

    <el-card class="create-form">
      <el-form :model="form" label-position="top">
        <el-form-item label="动态内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="分享您的健身心得、技巧或成就..."
            maxlength="1000"
            show-word-limit
          />
          <div class="character-count">
            {{ form.content.length }} / 1000 字
          </div>
        </el-form-item>

        <el-form-item label="配图 (可选)">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadAction"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :on-error="handleUploadError"
            list-type="picture-card"
            :limit="9"
            accept="image/jpeg,image/png,image/gif"
            multiple
          >
            <el-icon><plus /></el-icon>
            <template #tip>
              <div class="upload-tip">
                最多支持 9 张图片（JPG/PNG/GIF 格式，每张不超过 5MB）
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button @click="handleCancel">取消</el-button>
            <el-button
              type="primary"
              :loading="submitting"
              :disabled="!form.content.trim()"
              @click="handleSubmit"
            >
              发布
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { createDynamic } from '@/api/community'
// 注意：如果你的项目中没有 uploadImage 这个 API，可以忽略这个导入，或者确保路径正确
import { uploadImage } from '@/api/upload' 
import { showSuccess, showError, showWarning, confirmDiscard } from '@/utils/feedback'

const router = useRouter()

const form = ref({
  content: '',
  imageUrls: []
})

const fileList = ref([])
const submitting = ref(false)

const uploadAction = computed(() => {
  // 这里的 URL 仅用于显示，实际上传逻辑可能由 before-upload 或自定义 request 处理
  // 请确保这里指向你后端的真实上传接口
  return import.meta.env.VITE_API_BASE_URL + '/api/upload/image'
})

const beforeUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    showError('仅支持 JPG, PNG 和 GIF 格式的图片')
    return false
  }
  if (!isLt5M) {
    showError('图片大小不能超过 5MB')
    return false
  }

  return true
}

const handleUploadSuccess = (response, file) => {
  // 根据后端返回的数据结构获取 URL
  if (response && response.url) {
    form.value.imageUrls.push(response.url)
    file.url = response.url
  } else if (typeof response === 'string') {
    // 如果直接返回字符串 URL
    form.value.imageUrls.push(response)
    file.url = response
  }
}

const handleRemove = (file) => {
  if (file.url) {
    const index = form.value.imageUrls.indexOf(file.url)
    if (index > -1) {
      form.value.imageUrls.splice(index, 1)
    }
  }
}

const handleUploadError = (error) => {
  showError('图片上传失败')
  console.error('上传错误:', error)
}

const handleSubmit = async () => {
  if (!form.value.content.trim()) {
    showWarning('请输入动态内容')
    return
  }

  submitting.value = true
  try {
    await createDynamic({
      content: form.value.content,
      imageUrls: form.value.imageUrls.join(',')
    })
    
    showSuccess('动态发布成功！')
    router.push('/community')
  } catch (error) {
    // 错误通常已被拦截器处理
    console.error('发布动态失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = async () => {
  // 如果有内容或已上传图片，提示用户是否放弃
  if (form.value.content.trim() || form.value.imageUrls.length > 0) {
    try {
      await confirmDiscard() // 这里假设 confirmDiscard 内部提示语也是中文，或者它是通用的确认框
      goBack()
    } catch (error) {
      // 用户取消了放弃操作，什么都不做
    }
  } else {
    goBack()
  }
}

const goBack = () => {
  router.push('/community')
}
</script>

<style scoped>
.create-post {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.create-form {
  padding: 24px;
}

.character-count {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

/* 样式穿透，适配 Element Plus 上传组件 */
:deep(.el-upload-list--picture-card) {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.el-upload--picture-card) {
  width: 148px;
  height: 148px;
  border-radius: 8px;
}

:deep(.el-upload-list__item) {
  width: 148px;
  height: 148px;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .create-post {
    padding: 12px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .create-form {
    padding: 16px;
  }

  :deep(.el-upload--picture-card),
  :deep(.el-upload-list__item) {
    width: 100px;
    height: 100px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>
