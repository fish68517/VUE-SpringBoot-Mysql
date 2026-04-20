<template>
  <div class="admin-page">
    <div class="page-head">
      <div>
        <h2>旅行记忆管理</h2>
        <p>统一管理所有用户发布的旅行记录。</p>
      </div>
      <el-button @click="loadTravels">刷新</el-button>
    </div>

    <el-table :data="travels" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="destination" label="目的地" min-width="140" />
      <el-table-column prop="user.username" label="所属用户" min-width="120" />
      <el-table-column prop="isPublic" label="公开" width="90">
        <template #default="{ row }">
          {{ row.isPublic ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column prop="likeCount" label="点赞" width="90" />
      <el-table-column prop="commentCount" label="评论" width="90" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="removeTravel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="编辑旅行记录" width="720px">
      <el-form :model="form" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="目的地">
          <el-input v-model="form.destination" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="详细日志">
          <el-input v-model="form.diaryContent" type="textarea" :rows="8" />
        </el-form-item>
        <el-form-item label="是否公开">
          <el-switch v-model="form.isPublic" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminService } from '../services/adminService'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const currentId = ref(null)
const travels = ref([])
const form = reactive({
  title: '',
  destination: '',
  startDate: '',
  endDate: '',
  description: '',
  diaryContent: '',
  isPublic: false
})

const loadTravels = async () => {
  loading.value = true
  try {
    const response = await adminService.getTravels()
    travels.value = response.data.content || []
  } catch (error) {
    ElMessage.error(error.message || '加载旅行记录失败')
  } finally {
    loading.value = false
  }
}

const openEditDialog = (row) => {
  currentId.value = row.id
  form.title = row.title
  form.destination = row.destination
  form.startDate = row.startDate
  form.endDate = row.endDate
  form.description = row.description || ''
  form.diaryContent = row.diaryContent || ''
  form.isPublic = row.isPublic
  dialogVisible.value = true
}

const submitForm = async () => {
  saving.value = true
  try {
    await adminService.updateTravel(currentId.value, { ...form })
    ElMessage.success('旅行记录更新成功')
    dialogVisible.value = false
    await loadTravels()
  } catch (error) {
    ElMessage.error(error.message || '更新旅行记录失败')
  } finally {
    saving.value = false
  }
}

const removeTravel = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除旅行记录「${row.title}」吗？`, '删除确认', { type: 'warning' })
    await adminService.deleteTravel(row.id)
    ElMessage.success('旅行记录删除成功')
    await loadTravels()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除旅行记录失败')
    }
  }
}

onMounted(loadTravels)
</script>

<style scoped>
.admin-page {
  max-width: 1320px;
  margin: 0 auto;
}

.page-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.page-head h2 {
  margin: 0 0 8px;
  color: #183b56;
}

.page-head p {
  margin: 0;
  color: #6b7a88;
}
</style>
