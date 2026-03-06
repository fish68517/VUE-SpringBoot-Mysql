<template>
  <div class="manuscripts">
    <h1>我的稿件</h1>
    <el-button type="primary" style="margin-bottom: 20px" @click="goToSubmit">
      提交新稿件
    </el-button>

    <el-table :data="manuscripts" stripe v-loading="loading">
      <el-table-column prop="id" label="稿件ID" width="100" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submissionDate" label="投稿日期" width="150">
        <template #default="{ row }">
          {{ formatDate(row.submissionDate) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="viewManuscript(row.id)">
            查看
          </el-button>
          <el-button
            link
            type="primary"
            size="small"
            @click="editManuscript(row.id)"
            :disabled="row.status !== 'DRAFT' && row.status !== 'SUBMITTED'"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            size="small"
            @click="withdrawManuscript(row.id)"
            :disabled="row.status !== 'DRAFT' && row.status !== 'SUBMITTED'"
          >
            撤回
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="manuscripts.length === 0 && !loading" description="暂无稿件" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { manuscriptService } from '@/services/manuscriptService'

const router = useRouter()
const manuscripts = ref([])
const loading = ref(false)

const statusMap = {
  'DRAFT': '草稿',
  'SUBMITTED': '已投稿',
  'UNDER_REVIEW': '审核中',
  'REVISION_REQUIRED': '需要修改',
  'ACCEPTED': '已录用',
  'REJECTED': '已驳回'
}

const statusTypeMap = {
  'DRAFT': 'info',
  'SUBMITTED': 'warning',
  'UNDER_REVIEW': 'warning',
  'REVISION_REQUIRED': 'warning',
  'ACCEPTED': 'success',
  'REJECTED': 'danger'
}

onMounted(() => {
  loadManuscripts()
})

const loadManuscripts = async () => {
  loading.value = true
  try {
    const response = await manuscriptService.getAuthorManuscripts()
    manuscripts.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '加载稿件列表失败')
  } finally {
    loading.value = false
  }
}

const getStatusLabel = (status) => {
  return statusMap[status] || status
}

const getStatusType = (status) => {
  return statusTypeMap[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const goToSubmit = () => {
  router.push('/author/submit-manuscript')
}

const viewManuscript = (id) => {
  router.push(`/author/manuscript-detail/${id}`)
}

const editManuscript = (id) => {
  router.push(`/author/edit-manuscript/${id}`)
}

const deleteManuscript = (id) => {
  ElMessageBox.confirm(
    '确定要撤回这份稿件吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        await manuscriptService.deleteManuscript(id)
        ElMessage.success('稿件撤回成功')
        loadManuscripts()
      } catch (error) {
        ElMessage.error(error.message || '撤回失败')
      }
    })
    .catch(() => {
      ElMessage.info('已取消撤回')
    })
}

const withdrawManuscript = (id) => {
  deleteManuscript(id)
}
</script>

<style scoped>
.manuscripts {
  padding: 20px;
}

.manuscripts h1 {
  margin-bottom: 20px;
  color: #333;
}
</style>
