好的，这是汉化后的 “动态详情” (Post Detail) 页面代码。

主要改动：

界面文本：全部按钮、菜单项、占位符均已改为中文。

时间格式：formatTime 函数已调整为中文显示逻辑（刚刚、分钟前、小时前等）。

反馈提示：所有成功/失败/确认提示都已翻译。

请复制以下代码覆盖：

code
Html
play_circle
download
content_copy
expand_less
<template>
  <div class="post-detail">
    <!-- 返回按钮 -->
    <div class="back-button">
      <el-button :icon="ArrowLeft" @click="goBack">返回动态列表</el-button>
    </div>

    <div v-loading="loading" class="detail-content">
      <el-card v-if="!loading && post" class="post-card">
        <!-- 动态头部 (Post Header) -->
        <div class="post-header">
          <div class="user-info">
            <el-avatar :size="48" :src="post.user?.avatar || '/default-avatar.png'">
              {{ post.user?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="user-details">
              <span class="username">{{ post.user?.username }}</span>
              <!-- 时间显示已本地化 -->
              <span class="timestamp">{{ formatTime(post.createdAt) }}</span>
            </div>
          </div>
          <div v-if="isOwnPost" class="post-actions">
            <el-dropdown @command="handleCommand">
              <el-icon :size="20"><more-filled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">
                    <el-icon><edit /></el-icon>
                    编辑
                  </el-dropdown-item>
                  <el-dropdown-item command="delete">
                    <el-icon><delete /></el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <!-- 动态内容 (Post Content) -->
        <div class="post-content">
          <p>{{ post.content }}</p>
        </div>

        <!-- 动态图片 (Post Images) -->
        <div v-if="post.imageUrls && imageList.length > 0" class="post-images">
          <el-image
            v-for="(image, index) in imageList"
            :key="index"
            :src="image"
            :preview-src-list="imageList"
            :initial-index="index"
            fit="cover"
            class="post-image"
          />
        </div>

        <!-- 底部操作栏 (Post Footer) -->
        <div class="post-footer">
          <like-button
            :dynamic-id="post.id"
            :like-count="post.likeCount"
            :liked="post.liked"
            @update:liked="handleLikeUpdate"
            @update:like-count="handleLikeCountUpdate"
          />
          <div class="comment-count">
            <el-icon :size="20"><chat-line-round /></el-icon>
            <span>{{ comments.length }}</span>
          </div>
        </div>
      </el-card>

      <!-- 评论区域 (Comments Section) -->
      <el-card v-if="!loading && post" class="comments-card">
        <comment-list :comments="comments" @refresh="loadComments" />

        <!-- 评论输入框 -->
        <div class="comment-input-section">
          <el-avatar :size="36" :src="currentUser?.avatar || '/default-avatar.png'">
            {{ currentUser?.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
            maxlength="500"
            show-word-limit
            class="comment-input"
          />
        </div>
        <div class="comment-actions">
          <el-button
            type="primary"
            :loading="submitting"
            :disabled="!commentContent.trim()"
            @click="handleSubmitComment"
          >
            发表评论
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 编辑动态弹窗 (Edit Post Dialog) -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑动态"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="editForm" label-position="top">
        <el-form-item label="内容">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="分享您的健身心得..."
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="updating" @click="handleUpdatePost">
          更新
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import {
  ArrowLeft,
  MoreFilled,
  Edit,
  Delete,
  ChatLineRound
} from '@element-plus/icons-vue'
import { getDynamicById, updateDynamic, deleteDynamic } from '@/api/community'
import { getComments, createComment } from '@/api/comment'
import LikeButton from '@/components/community/LikeButton.vue'
import CommentList from '@/components/community/CommentList.vue'
import { showSuccess, showError, showWarning, confirmDelete } from '@/utils/feedback'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const submitting = ref(false)
const updating = ref(false)
const post = ref(null)
const comments = ref([])
const commentContent = ref('')

const editDialogVisible = ref(false)
const editForm = ref({
  content: '',
  imageUrls: ''
})

const currentUser = computed(() => authStore.currentUser)

const isOwnPost = computed(() => {
  return currentUser.value?.id === post.value?.user?.id
})

const imageList = computed(() => {
  if (!post.value?.imageUrls) return []
  return post.value.imageUrls.split(',').filter(url => url.trim())
})

onMounted(() => {
  loadPost()
  loadComments()
})

const loadPost = async () => {
  loading.value = true
  try {
    const postId = route.params.id
    post.value = await getDynamicById(postId)
  } catch (error) {
    showError('加载动态详情失败')
    router.push('/community')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const postId = route.params.id
    comments.value = await getComments(postId)
  } catch (error) {
    showError('加载评论失败')
  }
}

// 汉化后的时间格式函数
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString()
}

const goBack = () => {
  router.push('/community')
}

const handleCommand = async (command) => {
  if (command === 'edit') {
    editForm.value = {
      content: post.value.content,
      imageUrls: post.value.imageUrls || ''
    }
    editDialogVisible.value = true
  } else if (command === 'delete') {
    try {
      // 这里的 confirmDelete 假设是通用的确认框，文案在调用处可定制，
      // 如果 utils/feedback 中的 confirmDelete 固定了英文，需要去改那个文件
      await confirmDelete('这条动态') 
      
      await deleteDynamic(post.value.id)
      showSuccess('动态删除成功')
      router.push('/community')
    } catch (error) {
      if (error !== 'cancel' && error !== 'close') {
        showError('删除动态失败')
      }
    }
  }
}

const handleUpdatePost = async () => {
  if (!editForm.value.content.trim()) {
    showWarning('请输入动态内容')
    return
  }

  updating.value = true
  try {
    await updateDynamic(post.value.id, {
      content: editForm.value.content,
      imageUrls: editForm.value.imageUrls
    })
    
    showSuccess('动态更新成功')
    editDialogVisible.value = false
    loadPost()
  } catch (error) {
    showError('更新动态失败')
  } finally {
    updating.value = false
  }
}

const handleDialogClose = () => {
  editForm.value = {
    content: '',
    imageUrls: ''
  }
}

const handleSubmitComment = async () => {
  if (!commentContent.value.trim()) {
    showWarning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await createComment({
      content: commentContent.value,
      dynamicId: post.value.id
    })
    
    showSuccess('评论发表成功')
    commentContent.value = ''
    
    // Reload comments and update comment count
    await loadComments()
    post.value.commentCount = comments.value.length
  } catch (error) {
    showError('发表评论失败')
  } finally {
    submitting.value = false
  }
}

const handleLikeUpdate = (liked) => {
  post.value.liked = liked
}

const handleLikeCountUpdate = (count) => {
  post.value.likeCount = count
}
</script>

<style scoped>
/* 样式保持不变 */
.post-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.back-button {
  margin-bottom: 20px;
}

.detail-content {
  min-height: 400px;
}

.post-card {
  margin-bottom: 20px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.timestamp {
  font-size: 13px;
  color: #909399;
}

.post-actions {
  cursor: pointer;
}

.post-content {
  margin-bottom: 16px;
}

.post-content p {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 8px;
  margin-bottom: 16px;
}

.post-image {
  width: 100%;
  height: 250px;
  border-radius: 8px;
  overflow: hidden;
}

.post-footer {
  display: flex;
  align-items: center;
  gap: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.comment-count {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
}

.comment-count .el-icon {
  color: #909399;
}

.comment-count span {
  font-size: 14px;
  color: #606266;
}

.comments-card {
  margin-top: 20px;
}

.comment-input-section {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.comment-input {
  flex: 1;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

@media (max-width: 768px) {
  .post-detail {
    padding: 12px;
  }

  .post-images {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .post-image {
    height: 150px;
  }

  .comment-input-section {
    flex-direction: column;
  }
}
</style>