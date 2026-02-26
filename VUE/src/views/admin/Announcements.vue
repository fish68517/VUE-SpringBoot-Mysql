<template>
  <div class="admin-announcements-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="handleCreate">新建公告</el-button>
        </div>
      </template>

      <el-table :data="announcements" stripe v-loading="loading">
        <el-table-column prop="id" label="公告ID" width="80" />
        <el-table-column prop="title" label="公告标题" min-width="200" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: center"
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </el-card>

    <!-- 创建/编辑公告对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑公告' : '新建公告'"
      width="70%"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="公告标题">
          <el-input 
            v-model="form.title" 
            placeholder="请输入公告标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="公告内容">
          <el-input 
            v-model="form.content" 
            type="textarea"
            placeholder="请输入公告内容"
            :rows="10"
            maxlength="50000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { announcementApi } from '@/api/announcement'

const announcements = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const form = ref({
  id: null,
  title: '',
  content: ''
})

// 获取公告列表
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const page = pagination.value.currentPage - 1
    const size = pagination.value.pageSize
    const response = await announcementApi.getAnnouncements({ page, size })
    
    if (response.code === '0') {
      announcements.value = response.data.announcements
      pagination.value.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取公告列表失败')
    }
  } catch (error) {
    ElMessage.error('获取公告列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 新建公告
const handleCreate = () => {
  isEdit.value = false
  form.value = {
    id: null,
    title: '',
    content: ''
  }
  dialogVisible.value = true
}

// 编辑公告
const handleEdit = (row) => {
  isEdit.value = true
  form.value = {
    id: row.id,
    title: row.title,
    content: row.content
  }
  dialogVisible.value = true
}

// 保存公告
const handleSave = async () => {
  if (!form.value.title.trim()) {
    ElMessage.error('公告标题不能为空')
    return
  }
  
  if (!form.value.content.trim()) {
    ElMessage.error('公告内容不能为空')
    return
  }

  loading.value = true
  try {
    let response
    if (isEdit.value) {
      response = await announcementApi.updateAnnouncement(form.value.id, {
        title: form.value.title,
        content: form.value.content
      })
    } else {
      response = await announcementApi.createAnnouncement({
        title: form.value.title,
        content: form.value.content,
        createdBy: 1 // 这里应该从登录用户信息获取
      })
    }

    if (response.code === '0') {
      ElMessage.success(isEdit.value ? '公告更新成功' : '公告创建成功')
      dialogVisible.value = false
      fetchAnnouncements()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 删除公告
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除这条公告吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      const response = await announcementApi.deleteAnnouncement(row.id)
      if (response.code === '0') {
        ElMessage.success('公告删除成功')
        fetchAnnouncements()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败: ' + error.message)
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 分页变化
const handlePageChange = () => {
  fetchAnnouncements()
}

const handlePageSizeChange = () => {
  pagination.value.currentPage = 1
  fetchAnnouncements()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 初始化
onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.admin-announcements-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
