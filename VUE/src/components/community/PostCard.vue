<template>
  <el-card class="post-card" shadow="hover" @click="handleCardClick">
    <div class="post-header">
      <div class="user-info">
        <el-avatar :size="40" :src="post.user?.avatar || '/default-avatar.png'">
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

    <div class="post-content">
      <p :class="{ 'content-collapsed': !expanded && isLongContent }">
        {{ post.content }}
      </p>
      <span 
        v-if="isLongContent && !expanded" 
        class="read-more"
        @click.stop="expanded = true"
      >
        Read more
      </span>
    </div>

    <div v-if="post.imageUrls && imageList.length > 0" class="post-images">
      <div :class="imageGridClass">
        <el-image
          v-for="(image, index) in displayImages"
          :key="index"
          :src="image"
          :preview-src-list="imageList"
          :initial-index="index"
          fit="cover"
          class="post-image"
          @click.stop
        />
        <div 
          v-if="imageList.length > maxDisplayImages" 
          class="more-images-overlay"
          @click.stop="handleCardClick"
        >
          +{{ imageList.length - maxDisplayImages }}
        </div>
      </div>
    </div>

    <div class="post-footer">
      <like-button
        :dynamic-id="post.id"
        :like-count="post.likeCount"
        :liked="post.liked"
        @click.stop
        @update:liked="handleLikeUpdate"
        @update:like-count="handleLikeCountUpdate"
      />
      <div class="comment-info" @click.stop="handleCardClick">
        <el-icon :size="20"><chat-line-round /></el-icon>
        <span>{{ post.commentCount || 0 }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { MoreFilled, Edit, Delete, ChatLineRound } from '@element-plus/icons-vue'
import { deleteDynamic } from '@/api/community'
import { showSuccess, showError, confirmDelete } from '@/utils/feedback'
import LikeButton from './LikeButton.vue'

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['refresh', 'edit'])

const router = useRouter()
const authStore = useAuthStore()

const expanded = ref(false)
const maxDisplayImages = 4
const maxContentLength = 200

const isOwnPost = computed(() => {
  return authStore.currentUser?.id === props.post.user?.id
})

const isLongContent = computed(() => {
  return props.post.content && props.post.content.length > maxContentLength
})

const imageList = computed(() => {
  if (!props.post.imageUrls) return []
  return props.post.imageUrls.split(',').filter(url => url.trim())
})

const displayImages = computed(() => {
  return imageList.value.slice(0, maxDisplayImages)
})

const imageGridClass = computed(() => {
  const count = Math.min(imageList.value.length, maxDisplayImages)
  if (count === 1) return 'image-grid-single'
  if (count === 2) return 'image-grid-two'
  if (count === 3) return 'image-grid-three'
  return 'image-grid-four'
})

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

const handleCardClick = () => {
  router.push(`/community/${props.post.id}`)
}

const handleCommand = async (command) => {
  if (command === 'edit') {
    emit('edit', props.post)
  } else if (command === 'delete') {
    try {
      await confirmDelete('this post')
      
      await deleteDynamic(props.post.id)
      showSuccess('Post deleted successfully')
      emit('refresh')
    } catch (error) {
      if (error !== 'cancel' && error !== 'close') {
        showError('Failed to delete post')
      }
    }
  }
}

const handleLikeUpdate = (liked) => {
  props.post.liked = liked
}

const handleLikeCountUpdate = (count) => {
  props.post.likeCount = count
}
</script>

<style scoped>
.post-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s;
}

.post-card:hover {
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
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
  font-size: 14px;
  color: #303133;
}

.timestamp {
  font-size: 12px;
  color: #909399;
}

.post-actions {
  cursor: pointer;
}

.post-content {
  margin-bottom: 12px;
}

.post-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.content-collapsed {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more {
  color: #409eff;
  font-size: 14px;
  cursor: pointer;
  user-select: none;
}

.read-more:hover {
  text-decoration: underline;
}

.post-images {
  margin-bottom: 12px;
}

.image-grid-single,
.image-grid-two,
.image-grid-three,
.image-grid-four {
  display: grid;
  gap: 4px;
  position: relative;
}

.image-grid-single {
  grid-template-columns: 1fr;
  max-width: 400px;
}

.image-grid-two {
  grid-template-columns: repeat(2, 1fr);
}

.image-grid-three {
  grid-template-columns: repeat(2, 1fr);
}

.image-grid-three .post-image:first-child {
  grid-column: 1 / -1;
}

.image-grid-four {
  grid-template-columns: repeat(2, 1fr);
}

.post-image {
  width: 100%;
  height: 200px;
  border-radius: 4px;
  overflow: hidden;
}

.image-grid-single .post-image {
  height: 300px;
}

.more-images-overlay {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: calc(50% - 2px);
  height: 200px;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: 600;
  border-radius: 4px;
  cursor: pointer;
}

.post-footer {
  display: flex;
  align-items: center;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.comment-info {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.comment-info:hover {
  background-color: #f5f5f5;
}

.comment-info .el-icon {
  color: #909399;
}

.comment-info span {
  font-size: 14px;
  color: #606266;
}
</style>
