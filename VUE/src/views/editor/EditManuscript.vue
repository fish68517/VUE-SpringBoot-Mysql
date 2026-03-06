<template>
  <div class="edit-manuscript-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>稿件编辑</span>
        </div>
      </template>

      <!-- Accepted Manuscripts List -->
      <el-table :data="acceptedManuscripts" stripe style="width: 100%; margin-bottom: 20px">
        <el-table-column prop="id" label="稿件ID" width="80" />
        <el-table-column prop="title" label="稿件标题" min-width="200" />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="submissionDate" label="投稿日期" width="150" :formatter="formatDate" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleGenerateNotification(row)">生成录用通知</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Edit Form Dialog -->
      <el-dialog v-model="editDialogVisible" title="编辑稿件" width="800px">
        <el-form v-if="editForm" :model="editForm" label-width="100px">
          <el-form-item label="稿件标题" required>
            <el-input v-model="editForm.title" placeholder="请输入稿件标题" />
          </el-form-item>
          <el-form-item label="摘要" required>
            <el-input
              v-model="editForm.abstractText"
              type="textarea"
              rows="4"
              placeholder="请输入摘要"
            />
          </el-form-item>
          <el-form-item label="内容" required>
            <el-input
              v-model="editForm.content"
              type="textarea"
              rows="8"
              placeholder="请输入稿件内容"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitting">保存</el-button>
        </template>
      </el-dialog>

      <!-- Notification Dialog -->
      <el-dialog v-model="notificationDialogVisible" title="录用通知" width="700px">
        <div v-if="notificationContent" class="notification-content">
          <el-input
            v-model="notificationContent"
            type="textarea"
            rows="12"
            readonly
          />
        </div>
        <template #footer>
          <el-button @click="notificationDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="copyNotification">复制</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { editorService } from '../../services/editorService'
import { manuscriptService } from '../../services/manuscriptService'

const acceptedManuscripts = ref([])
const editDialogVisible = ref(false)
const notificationDialogVisible = ref(false)
const submitting = ref(false)
const selectedManuscript = ref(null)
const notificationContent = ref('')

const editForm = ref({
  id: null,
  title: '',
  abstractText: '',
  content: ''
})

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

const loadAcceptedManuscripts = async () => {
  try {
    const response = await editorService.getAcceptedManuscripts()
    if (response.data) {
      // Fetch full manuscript details for each accepted manuscript
      const manuscriptsWithDetails = await Promise.all(
        response.data.map(async (item) => {
          try {
            const detail = await manuscriptService.getManuscriptDetail(item.id)
            const manuscript = detail.data.manuscript
            return {
              id: manuscript.id,
              title: manuscript.title,
              authorName: manuscript.authorName || 'Unknown',
              abstractText: manuscript.abstractText,
              content: manuscript.content,
              submissionDate: manuscript.submissionDate,
              status: manuscript.status
            }
          } catch (error) {
            return {
              id: item.id,
              title: item.title || 'Unknown',
              authorName: 'Unknown',
              abstractText: item.abstractText || '',
              content: item.content || '',
              submissionDate: item.submissionDate,
              status: item.status
            }
          }
        })
      )
      acceptedManuscripts.value = manuscriptsWithDetails
    }
  } catch (error) {
    ElMessage.error('加载已接受稿件失败')
  }
}

const handleEdit = (manuscript) => {
  selectedManuscript.value = manuscript
  editForm.value = {
    id: manuscript.id,
    title: manuscript.title,
    abstractText: manuscript.abstractText,
    content: manuscript.content
  }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  if (!editForm.value.title) {
    ElMessage.error('请输入稿件标题')
    return
  }
  if (!editForm.value.abstractText) {
    ElMessage.error('请输入摘要')
    return
  }
  if (!editForm.value.content) {
    ElMessage.error('请输入稿件内容')
    return
  }

  submitting.value = true
  try {
    await editorService.editManuscriptContent(
      editForm.value.id,
      editForm.value.title,
      editForm.value.abstractText,
      editForm.value.content
    )
    ElMessage.success('稿件编辑成功')
    editDialogVisible.value = false
    await loadAcceptedManuscripts()
  } catch (error) {
    ElMessage.error('编辑稿件失败')
  } finally {
    submitting.value = false
  }
}

const handleGenerateNotification = async (manuscript) => {
  try {
    const response = await editorService.generateAcceptanceNotification(manuscript.id)
    if (response.data) {
      notificationContent.value = response.data
      notificationDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('生成录用通知失败')
  }
}

const copyNotification = () => {
  if (notificationContent.value) {
    navigator.clipboard.writeText(notificationContent.value).then(() => {
      ElMessage.success('已复制到剪贴板')
    }).catch(() => {
      ElMessage.error('复制失败')
    })
  }
}

onMounted(() => {
  loadAcceptedManuscripts()
})
</script>

<style scoped>
.edit-manuscript-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-content {
  padding: 20px 0;
}
</style>
