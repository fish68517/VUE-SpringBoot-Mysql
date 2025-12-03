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
          <!-- 
            修改说明：
            1. 移除了 action 属性，改用 http-request 自定义上传
            2. 使用 list-type="picture-card" 展示照片墙
          -->
          <el-upload
            v-model:file-list="fileList"
            action="#"
            :http-request="handleCustomUpload"
            :before-upload="beforeUpload"
            :on-remove="handleRemove"
            list-type="picture-card"
            :limit="9"
            accept="image/jpeg,image/png,image/gif"
            multiple
          >
            <el-icon><Plus /></el-icon>
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
              :disabled="!form.content.trim() && form.imageUrls.length === 0"
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { createDynamic } from '@/api/community.js'
import { uploadImage } from '@/api/upload.js' // 确保导入路径正确
import { showSuccess, showError, showWarning, confirmDiscard } from '@/utils/feedback.js'

const router = useRouter()

const form = ref({
  content: '',
  imageUrls: [] // 存储上传成功的图片路径/名称
})

const fileList = ref([])
const submitting = ref(false)

// 图片上传前的校验
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

// 自定义上传逻辑，直接调用 API
const handleCustomUpload = async (options) => {
  const { file, onSuccess, onError } = options
  try {
    // 调用封装好的 uploadImage API，它会发送 POST 请求到后端的 /api/upload/image
    const response = await uploadImage(file)
    // 打印返回结果以便调试
    console.log('上传成功，返回数据：', JSON.stringify(response))
    // 上传成功，返回数据： {"filename":"7bc4a906a96b3aeed5bd252a035ab377.jpeg","url":"/images/89637cd0-a9da-4d68-9ada-63ed69c9ecff.jpeg"}
    
    // 假设后端返回的是图片的相对路径或完整URL字符串
    // 将返回的图片路径存入 form.imageUrls
    form.value.imageUrls.push(response.url)
    
    // 通知 Element Plus 上传成功（这样图片会有绿色的勾选标记）
    // 我们把 response 传给 onSuccess，这样 handleRemove 时可以通过 file.response 获取到
    onSuccess(response)
  } catch (error) {
    console.error('上传失败:', error)
    onError(error)
    showError('图片上传失败')
  }
}

// 移除图片时的逻辑
const handleRemove = (uploadFile) => {
  // uploadFile.response 是我们在 onSuccess 中传入的值
  // 如果是回显的图片可能没有 response，取 url
  const urlToRemove = uploadFile.response || uploadFile.url
  
  if (urlToRemove) {
    const index = form.value.imageUrls.indexOf(urlToRemove)
    if (index > -1) {
      form.value.imageUrls.splice(index, 1)
    }
  }
}

// 提交发布
const handleSubmit = async () => {
  if (!form.value.content.trim() && form.value.imageUrls.length === 0) {
    showWarning('请输入内容或上传图片')
    return
  }

  submitting.value = true
  try {
    // 构造提交给后端的数据
    // imageUrls 数组转换为逗号分隔的字符串
    const postData = {
      content: form.value.content,
      imageUrls: form.value.imageUrls.join(',')
    }

    await createDynamic(postData)
    
    showSuccess('动态发布成功！')
    router.push('/community')
  } catch (error) {
    console.error('发布动态失败:', error)
    // 错误通常已被拦截器处理，这里不一定需要再次弹窗
  } finally {
    submitting.value = false
  }
}

const handleCancel = async () => {
  if (form.value.content.trim() || form.value.imageUrls.length > 0) {
    try {
      await confirmDiscard()
      goBack()
    } catch (error) {
      // 用户取消
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