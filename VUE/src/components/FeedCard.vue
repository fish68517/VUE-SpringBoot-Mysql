<template>
  <div class="feed-card">
    <!-- Card Header with User Info -->
    <div class="feed-card-header">
      <div class="user-info">
        <img
          v-if="record.user?.avatarUrl"
          :src="record.user.avatarUrl"
          :alt="record.user.username"
          class="user-avatar"
        />
        <div v-else class="user-avatar-placeholder">
          {{ record.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
        </div>
        <div class="user-details">
          <p class="username">{{ record.user?.username || 'Unknown User' }}</p>
          <p class="created-date">{{ formatDate(record.createdAt) }}</p>
        </div>
      </div>
    </div>

    <!-- Card Body -->
    <div class="feed-card-body" @click="handleViewRecord">
      <h3 class="record-title">{{ record.title }}</h3>
      <p class="destination">
        <el-icon><Location /></el-icon>
        {{ record.destination }}
      </p>
      <p class="dates">
        <el-icon><Calendar /></el-icon>
        {{ formatDateRange(record.startDate, record.endDate) }}
      </p>
      <p class="description">{{ truncateText(record.description, 150) }}</p>
    </div>

    <!-- Card Footer with Interactions -->
    <div class="feed-card-footer">
      <div class="interaction-stats">
        <el-button
          type="primary"
          text
          size="small"
          :class="{ liked: isLiked }"
          @click="handleToggleLike"
          :loading="liking"
        >
          <el-icon><Share /></el-icon>
          {{ record.likeCount || 0 }} Likes
        </el-button>
        <span class="stat">
          <el-icon><ChatDotRound /></el-icon>
          {{ record.commentCount || 0 }} Comments
        </span>
      </div>
      <el-button
        type="primary"
        text
        size="small"
        @click="handleViewRecord"
      >
        View Details
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Calendar, Share, ChatDotRound } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/userStore'
import likeService from '../services/likeService'

const router = useRouter()
const userStore = useUserStore()

const props = defineProps({
  record: {
    type: Object,
    required: true
  }
})

const liking = ref(false)
const isLiked = ref(false)

const handleViewRecord = () => {
  router.push(`/records/${props.record.id}`)
}

const handleToggleLike = async () => {
  if (!userStore.user) {
    ElMessage.warning('Please log in to like records')
    router.push('/login')
    return
  }

  try {
    liking.value = true
    if (isLiked.value) {
      // Unlike
      await likeService.deleteLikeByTravelRecord(props.record.id)
      props.record.likeCount = Math.max(0, (props.record.likeCount || 0) - 1)
      isLiked.value = false
      ElMessage.success('Like removed')
    } else {
      // Like
      await likeService.createLike(props.record.id)
      props.record.likeCount = (props.record.likeCount || 0) + 1
      isLiked.value = true
      ElMessage.success('Liked!')
    }
  } catch (error) {
    ElMessage.error(error.message || 'Failed to update like')
  } finally {
    liking.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const formatDateRange = (startDate, endDate) => {
  if (!startDate || !endDate) return ''
  const start = new Date(startDate).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric'
  })
  const end = new Date(endDate).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
  return `${start} - ${end}`
}

const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}
</script>

<style scoped>
.feed-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.feed-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.feed-card-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

.user-details {
  flex: 1;
}

.username {
  margin: 0;
  color: #333;
  font-weight: 600;
  font-size: 14px;
}

.created-date {
  margin: 4px 0 0 0;
  color: #999;
  font-size: 12px;
}

.feed-card-body {
  padding: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.feed-card-body:hover {
  background-color: #fafafa;
}

.record-title {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.destination {
  margin: 8px 0;
  color: #409eff;
  font-weight: 500;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dates {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.description {
  margin: 12px 0 0 0;
  color: #999;
  font-size: 14px;
  line-height: 1.5;
  max-height: 80px;
  overflow: hidden;
}

.feed-card-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fafafa;
}

.interaction-stats {
  display: flex;
  gap: 20px;
  align-items: center;
}

.stat {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 13px;
}

:deep(.interaction-stats .el-button) {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 13px;
  padding: 0;
}

:deep(.interaction-stats .el-button.liked) {
  color: #f56c6c;
}

:deep(.interaction-stats .el-button:hover) {
  color: #409eff;
}

@media (max-width: 768px) {
  .feed-card-body {
    padding: 12px;
  }

  .record-title {
    font-size: 16px;
  }

  .feed-card-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
