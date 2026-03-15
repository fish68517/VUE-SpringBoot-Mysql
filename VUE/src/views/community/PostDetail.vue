<template>
  <div class="post-detail">
    <div class="back-button">
      <el-button :icon="ArrowLeft" @click="goBack">返回动态列表</el-button>
    </div>

    <div v-loading="loading" class="detail-content">
      <el-card v-if="!loading && post" class="post-card">
        <div class="post-header">
          <div class="user-info">
            <div class="user-details">
              <span class="username">发布者：{{ postAuthorName }}</span>
              <span class="timestamp">{{ formatTime(post.createdAt) }}</span>
            </div>
          </div>

          <div v-if="isOwnPost" class="post-actions">
            <el-dropdown @command="handleCommand">
              <el-icon :size="20"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-dropdown-item>
                  <el-dropdown-item command="delete">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <div class="post-content">
          <p>{{ post.content }}</p>
        </div>

        <div v-if="imageList.length > 0" class="post-images">
          <el-image
            v-for="(image, index) in imageList"
            :key="index"
            :src="image"
            :preview-src-list="imageList"
            :initial-index="index"
            fit="cover"
            class="post-image"
          >
            <template #error>
              <div class="image-fallback">
                <img :src="postDefaultImage" alt="post default" class="fallback-image" />
              </div>
            </template>
          </el-image>
        </div>

        <div class="post-footer">
          <LikeButton
            v-if="false"
            :dynamic-id="post.id"
            :like-count="post.likeCount"
            :liked="post.liked"
            @update:liked="handleLikeUpdate"
            @update:like-count="handleLikeCountUpdate"
          />
          <div class="comment-count">
            <el-icon :size="20"><ChatLineRound /></el-icon>
            <span>{{ comments.length }}</span>
          </div>
        </div>
      </el-card>

      <el-card v-if="!loading && post" class="comments-card">
        <CommentList :comments="comments" @refresh="loadComments" />

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

    <el-dialog v-model="editDialogVisible" title="编辑动态" width="600px" @close="handleDialogClose">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="内容">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="分享你的健身心得..."
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="updating" @click="handleUpdatePost">更新</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { ArrowLeft, ChatLineRound, Delete, Edit, MoreFilled } from '@element-plus/icons-vue'
import { deleteDynamic, getDynamicById, updateDynamic } from '@/api/community'
import { createComment, getComments } from '@/api/comment'
import LikeButton from '@/components/community/LikeButton.vue'
import CommentList from '@/components/community/CommentList.vue'
import { confirmDelete, showError, showSuccess, showWarning } from '@/utils/feedback'
import postDefaultImage from '@/assets/post_default.png'

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

const getEntityUserId = (entity) => {
  if (!entity) return null
  return entity.id ?? entity.userId ?? entity.user?.id ?? entity.user?.userId ?? null
}

const currentUserId = computed(() => getEntityUserId(currentUser.value))
const postOwnerId = computed(() => getEntityUserId(post.value?.user || post.value))

const postAuthorName = computed(() => {
  return post.value?.user?.username || post.value?.username || '未知用户'
})

const isOwnPost = computed(() => {
  if (currentUserId.value == null || postOwnerId.value == null) return false
  return String(currentUserId.value) === String(postOwnerId.value)
})

const imageList = computed(() => {
  if (!post.value?.imageUrls) return []
  return post.value.imageUrls
    .split(',')
    .map((url) => url.trim())
    .filter(Boolean)
})

onMounted(() => {
  loadPost()
  loadComments()
})

const loadPost = async () => {
  loading.value = true
  try {
    post.value = await getDynamicById(route.params.id)
  } catch (error) {
    showError('加载动态详情失败')
    router.push('/community')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    comments.value = await getComments(route.params.id)
  } catch (error) {
    showError('加载评论失败')
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

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  return date.toLocaleDateString('zh-CN')
}

const goBack = () => {
  router.push('/community')
}

const guardOwnerPermission = () => {
  if (isOwnPost.value) return true
  showWarning('只有当前动态作者才可以编辑或删除')
  return false
}

const handleCommand = async (command) => {
  if (!guardOwnerPermission()) return

  if (command === 'edit') {
    editForm.value = {
      content: post.value?.content || '',
      imageUrls: post.value?.imageUrls || ''
    }
    editDialogVisible.value = true
    return
  }

  if (command === 'delete') {
    try {
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
  if (!guardOwnerPermission()) {
    editDialogVisible.value = false
    return
  }

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
    await loadPost()
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
    await loadComments()
    post.value.commentCount = comments.value.length
  } catch (error) {
    showError('发表评论失败')
  } finally {
    submitting.value = false
  }
}

const handleLikeUpdate = (liked) => {
  if (post.value) post.value.liked = liked
}

const handleLikeCountUpdate = (count) => {
  if (post.value) post.value.likeCount = count
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

.image-fallback {
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fallback-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
