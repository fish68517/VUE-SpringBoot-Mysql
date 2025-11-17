<template>
  <div class="post-detail">
    <div class="back-button">
      <el-button :icon="ArrowLeft" @click="goBack">Back to Feed</el-button>
    </div>

    <div v-loading="loading" class="detail-content">
      <el-card v-if="!loading && post" class="post-card">
        <!-- Post Header -->
        <div class="post-header">
          <div class="user-info">
            <el-avatar :size="48" :src="post.user?.avatar || '/default-avatar.png'">
              {{ post.user?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="user-details">
              <span class="username">{{ post.user?.username }}</span>
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
                    Edit
                  </el-dropdown-item>
                  <el-dropdown-item command="delete">
                    <el-icon><delete /></el-icon>
                    Delete
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <!-- Post Content -->
        <div class="post-content">
          <p>{{ post.content }}</p>
        </div>

        <!-- Post Images -->
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

        <!-- Post Footer -->
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

      <!-- Comments Section -->
      <el-card v-if="!loading && post" class="comments-card">
        <comment-list :comments="comments" @refresh="loadComments" />

        <!-- Comment Input -->
        <div class="comment-input-section">
          <el-avatar :size="36" :src="currentUser?.avatar || '/default-avatar.png'">
            {{ currentUser?.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="Write a comment..."
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
            Submit Comment
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- Edit Post Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="Edit Post"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="editForm" label-position="top">
        <el-form-item label="Content">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="Share your fitness journey..."
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="updating" @click="handleUpdatePost">
          Update
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
    showError('Failed to load post')
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
    showError('Failed to load comments')
  }
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes}m ago`
  if (hours < 24) return `${hours}h ago`
  if (days < 7) return `${days}d ago`
  
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
      await confirmDelete('this post')
      
      await deleteDynamic(post.value.id)
      showSuccess('Post deleted successfully')
      router.push('/community')
    } catch (error) {
      if (error !== 'cancel' && error !== 'close') {
        showError('Failed to delete post')
      }
    }
  }
}

const handleUpdatePost = async () => {
  if (!editForm.value.content.trim()) {
    showWarning('Please enter post content')
    return
  }

  updating.value = true
  try {
    await updateDynamic(post.value.id, {
      content: editForm.value.content,
      imageUrls: editForm.value.imageUrls
    })
    
    showSuccess('Post updated successfully')
    editDialogVisible.value = false
    loadPost()
  } catch (error) {
    showError('Failed to update post')
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
    showWarning('Please enter comment content')
    return
  }

  submitting.value = true
  try {
    await createComment({
      content: commentContent.value,
      dynamicId: post.value.id
    })
    
    showSuccess('Comment added successfully')
    commentContent.value = ''
    
    // Reload comments and update comment count
    await loadComments()
    post.value.commentCount = comments.value.length
  } catch (error) {
    showError('Failed to add comment')
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
