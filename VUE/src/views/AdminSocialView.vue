<template>
  <div class="admin-page">
    <div class="page-head">
      <div>
        <h2>社交数据管理</h2>
        <p>统一管理评论与点赞数据。</p>
      </div>
      <el-button @click="loadSocialData">刷新</el-button>
    </div>

    <div class="section">
      <h3>评论管理</h3>
      <el-table :data="comments" v-loading="commentsLoading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="travelRecordId" label="记录ID" width="100" />
        <el-table-column prop="user.username" label="评论用户" min-width="140" />
        <el-table-column prop="content" label="评论内容" min-width="320" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="removeComment(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section">
      <h3>点赞管理</h3>
      <el-table :data="likes" v-loading="likesLoading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="travelRecordId" label="记录ID" width="120" />
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="createdAt" label="点赞时间" min-width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="removeLike(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminService } from '../services/adminService'

const comments = ref([])
const likes = ref([])
const commentsLoading = ref(false)
const likesLoading = ref(false)

const loadComments = async () => {
  commentsLoading.value = true
  try {
    const response = await adminService.getComments()
    comments.value = response.data.content || []
  } catch (error) {
    ElMessage.error(error.message || '加载评论失败')
  } finally {
    commentsLoading.value = false
  }
}

const loadLikes = async () => {
  likesLoading.value = true
  try {
    const response = await adminService.getLikes()
    likes.value = response.data.content || []
  } catch (error) {
    ElMessage.error(error.message || '加载点赞失败')
  } finally {
    likesLoading.value = false
  }
}

const loadSocialData = async () => {
  await Promise.all([loadComments(), loadLikes()])
}

const removeComment = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除评论 #${row.id} 吗？`, '删除确认', { type: 'warning' })
    await adminService.deleteComment(row.id)
    ElMessage.success('评论删除成功')
    await loadComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除评论失败')
    }
  }
}

const removeLike = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除点赞记录 #${row.id} 吗？`, '删除确认', { type: 'warning' })
    await adminService.deleteLike(row.id)
    ElMessage.success('点赞记录删除成功')
    await loadLikes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除点赞记录失败')
    }
  }
}

onMounted(loadSocialData)
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

.section + .section {
  margin-top: 28px;
}

.section h3 {
  margin: 0 0 12px;
  color: #183b56;
}
</style>
