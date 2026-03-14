<template>
  <div class="community-management-layout">
    <Sidebar />
    
    <div class="main-content">
      <Header />
      
      <div class="content-area">
        <div class="community-management-container">
          <div class="page-header">
            <h2>社区管理</h2>
            <p>管理社区内容和评论</p>
          </div>

          <!-- Refresh Button -->
          <div class="action-section">
            <el-button @click="handleRefresh">
              <el-icon><Refresh /></el-icon>
              <span>刷新</span>
            </el-button>
          </div>

          <!-- Post Table -->
          <div class="table-section">
            <el-table
              :data="postList"
              stripe
              style="width: 100%"
              :loading="loading"
              v-loading="loading"
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="userId" label="用户ID" width="100" />
              <el-table-column prop="content" label="内容" min-width="200">
                <template #default="{ row }">
                  <div class="content-preview">
                    {{ truncateContent(row.content, 50) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="likesCount" label="点赞数" width="100" />
              <el-table-column prop="createdAt" label="发布时间" min-width="180">
                <template #default="{ row }">
                  {{ formatDate(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="250" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    size="small"
                    @click="handleViewDetails(row)"
                  >
                    查看详情
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    @click="handleDeletePost(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- Pagination -->
          <div class="pagination-section">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handlePageSizeChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Post Details Dialog -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="帖子详情"
      width="700px"
      @close="resetDetailsDialog"
    >
      <div v-if="selectedPost" class="post-details">
        <!-- Post Content -->
        <div class="post-section">
          <div class="post-header">
            <span class="label">用户ID:</span>
            <span class="value">{{ selectedPost.userId }}</span>
          </div>
          <div class="post-header">
            <span class="label">发布时间:</span>
            <span class="value">{{ formatDate(selectedPost.createdAt) }}</span>
          </div>
          <div class="post-content">
            <span class="label">内容:</span>
            <div class="content-text">{{ selectedPost.content }}</div>
          </div>
          <div v-if="selectedPost.imagePath" class="post-image">
            <span class="label">图片:</span>
            <img :src="getImageUrl(selectedPost.imagePath)" alt="Post image" />
          </div>
          <div class="post-stats">
            <span class="label">点赞数:</span>
            <span class="value">{{ selectedPost.likesCount }}</span>
          </div>
        </div>

        <!-- Comments Section -->
        <div class="comments-section">
          <h4>评论 ({{ selectedPost.comments ? selectedPost.comments.length : 0 }})</h4>
          <div v-if="selectedPost.comments && selectedPost.comments.length > 0" class="comments-list">
            <div v-for="comment in selectedPost.comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <span class="user-id">用户 #{{ comment.userId }}</span>
                <span class="time">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-actions">
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteComment(comment)"
                >
                  删除评论
                </el-button>
              </div>
            </div>
          </div>
          <div v-else class="no-comments">
            暂无评论
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import Sidebar from '../components/Sidebar.vue'
import Header from '../components/Header.vue'
import { getPostList, getPostById, deletePost, deleteComment } from '../api/community.js'
import { showSuccess, showError } from '../utils/notification.js'

const postList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const detailsDialogVisible = ref(false)
const selectedPost = ref(null)

/**
 * Format date to readable string
 */
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

/**
 * Truncate content to specified length
 */
const truncateContent = (content, length) => {
  if (!content) return '-'
  return content.length > length ? content.substring(0, length) + '...' : content
}

/**
 * Get image URL
 */
const getImageUrl = (imagePath) => {
  if (!imagePath) return ''
  if (imagePath.startsWith('http')) {
    return imagePath
  }
  return `http://localhost:8080/${imagePath}`
}

/**
 * Load post list from API
 */
const loadPostList = async () => {
  loading.value = true
  try {
    const response = await getPostList(currentPage.value, pageSize.value)
    
    if (response.code === 200) {
      postList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      showError(response.message || '获取帖子列表失败', '加载失败')
    }
  } catch (error) {
    console.error('Error loading post list:', error)
    showError('获取帖子列表失败，请检查网络连接', '加载失败')
  } finally {
    loading.value = false
  }
}

/**
 * Handle refresh
 */
const handleRefresh = () => {
  currentPage.value = 1
  loadPostList()
}

/**
 * Handle page change
 */
const handlePageChange = () => {
  loadPostList()
}

/**
 * Handle page size change
 */
const handlePageSizeChange = () => {
  currentPage.value = 1
  loadPostList()
}

/**
 * Handle view post details
 */
const handleViewDetails = async (row) => {
  try {
    const response = await getPostById(row.id)
    
    if (response.code === 200) {
      selectedPost.value = response.data
      detailsDialogVisible.value = true
    } else {
      showError(response.message || '获取帖子详情失败', '加载失败')
    }
  } catch (error) {
    console.error('Error loading post details:', error)
    showError('获取帖子详情失败，请检查网络连接', '加载失败')
  }
}

/**
 * Handle delete post
 */
const handleDeletePost = (row) => {
  ElMessageBox.confirm(
    `确定要删除该帖子吗？此操作不可撤销。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        const response = await deletePost(row.id)

        if (response.code === 200) {
          showSuccess('帖子删除成功', '删除成功')
          loadPostList()
        } else {
          showError(response.message || '删除帖子失败', '删除失败')
        }
      } catch (error) {
        console.error('Error deleting post:', error)
        showError('删除帖子失败，请检查网络连接', '删除失败')
      }
    })
    .catch(() => {
      // User cancelled the deletion
    })
}

/**
 * Handle delete comment
 */
const handleDeleteComment = (comment) => {
  ElMessageBox.confirm(
    `确定要删除该评论吗？此操作不可撤销。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        const response = await deleteComment(comment.id)

        if (response.code === 200) {
          showSuccess('评论删除成功', '删除成功')
          // Reload post details to refresh comments
          const postResponse = await getPostById(selectedPost.value.id)
          if (postResponse.code === 200) {
            selectedPost.value = postResponse.data
          }
        } else {
          showError(response.message || '删除评论失败', '删除失败')
        }
      } catch (error) {
        console.error('Error deleting comment:', error)
        showError('删除评论失败，请检查网络连接', '删除失败')
      }
    })
    .catch(() => {
      // User cancelled the deletion
    })
}

/**
 * Reset details dialog
 */
const resetDetailsDialog = () => {
  selectedPost.value = null
}

/**
 * Load post list on component mount
 */
onMounted(() => {
  loadPostList()
})
</script>

<style scoped>
.community-management-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.community-management-container {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 5px 0;
  letter-spacing: -0.5px;
}

.page-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.action-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.1s both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.table-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.2s both;
}

.table-section :deep(.el-table) {
  font-size: 14px;
  border-radius: 8px;
}

.table-section :deep(.el-table__header th) {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #333;
}

.table-section :deep(.el-table__body tr:hover > td) {
  background-color: #f9f9f9;
}

.content-preview {
  color: #666;
  word-break: break-word;
}

.pagination-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  display: flex;
  justify-content: flex-end;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.3s both;
}

.pagination-section :deep(.el-pagination) {
  display: flex;
  gap: 10px;
}

/* Post Details Dialog Styles */
.post-details {
  padding: 10px 0;
}

.post-section {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.post-header {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
}

.post-header .label {
  font-weight: 600;
  color: #333;
  min-width: 80px;
}

.post-header .value {
  color: #666;
}

.post-content {
  margin-bottom: 15px;
}

.post-content .label {
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
}

.content-text {
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 10px;
  color: #666;
  line-height: 1.6;
  word-break: break-word;
  max-height: 200px;
  overflow-y: auto;
}

.post-image {
  margin-bottom: 15px;
}

.post-image .label {
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
}

.post-image img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.post-stats {
  display: flex;
  font-size: 14px;
}

.post-stats .label {
  font-weight: 600;
  color: #333;
  min-width: 80px;
}

.post-stats .value {
  color: #666;
}

.comments-section {
  margin-top: 20px;
}

.comments-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
}

.comments-list {
  max-height: 400px;
  overflow-y: auto;
}

.comment-item {
  background: #f9f9f9;
  border-left: 3px solid #409eff;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
  transition: all 0.3s ease;
}

.comment-item:hover {
  background: #f5f5f5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
}

.comment-header .user-id {
  font-weight: 600;
  color: #333;
}

.comment-header .time {
  color: #999;
}

.comment-content {
  color: #666;
  line-height: 1.5;
  margin-bottom: 10px;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.no-comments {
  text-align: center;
  color: #999;
  padding: 20px;
  font-size: 14px;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #e0e0e0;
  padding: 16px 20px;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #333;
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}
</style>
