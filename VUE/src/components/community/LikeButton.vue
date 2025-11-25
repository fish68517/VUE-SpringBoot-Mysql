<template>
  <div class="like-button" @click="handleLike">
    <el-icon :size="20" :class="{ 'liked': isLiked }">
      <component :is="isLiked ? 'Star-filled' : 'Star'" />
    </el-icon>
    <span class="like-count">{{ displayCount }}</span>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { likeDynamic, unlikeDynamic } from '@/api/community'
import { showError } from '@/utils/feedback'

const props = defineProps({
  dynamicId: {
    type: Number,
    required: true
  },
  likeCount: {
    type: Number,
    default: 0
  },
  liked: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:liked', 'update:likeCount'])

// Local state for optimistic UI update
const isLiked = ref(props.liked)
const localCount = ref(props.likeCount)

const displayCount = computed(() => localCount.value)

const handleLike = async () => {
  // Optimistic UI update
  const previousLiked = isLiked.value
  const previousCount = localCount.value
  
  isLiked.value = !isLiked.value
  localCount.value = isLiked.value ? localCount.value + 1 : localCount.value - 1
  
  try {
    if (isLiked.value) {
      await likeDynamic(props.dynamicId)
    } else {
      await unlikeDynamic(props.dynamicId)
    }
    
    // Emit updates to parent
    emit('update:liked', isLiked.value)
    emit('update:likeCount', localCount.value)
  } catch (error) {
    // Revert on error
    isLiked.value = previousLiked
    localCount.value = previousCount
    showError('Failed to update like status')
  }
}
</script>

<style scoped>
.like-button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  user-select: none;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.like-button:hover {
  background-color: #f5f5f5;
}

.el-icon {
  color: #909399;
  transition: color 0.2s;
}

.el-icon.liked {
  color: var(--color-danger, #f56c6c);
  animation: StarBeat 0.6s ease;
}

.like-count {
  font-size: 14px;
  color: var(--color-text-regular, #606266);
  transition: color var(--transition-fast, 0.2s);
}

.like-button:hover .like-count {
  color: var(--color-text-primary, #303133);
}

@keyframes StarBeat {
  0% {
    transform: scale(1);
  }
  14% {
    transform: scale(1.3);
  }
  28% {
    transform: scale(1);
  }
  42% {
    transform: scale(1.3);
  }
  70% {
    transform: scale(1);
  }
}
</style>
