<template>
  <div class="favorite-button-container">
    <el-button
      :type="isFavorited ? 'danger' : 'default'"
      :icon="isFavorited ? HeartFilled : Heart"
      @click="toggleFavorite"
      :loading="loading"
      :disabled="!userId"
    >
      {{ isFavorited ? '已收藏' : '收藏' }}
    </el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Heart, HeartFilled } from '@element-plus/icons-vue'

const props = defineProps({
  targetType: {
    type: String,
    required: true,
    validator: (value) => ['attraction', 'hotel', 'product'].includes(value)
  },
  targetId: {
    type: Number,
    required: true
  },
  userId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['favorite-changed'])

const isFavorited = ref(false)
const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080'

/**
 * 检查是否已收藏
 */
const checkFavorite = async () => {
  if (!props.userId) return

  try {
    const response = await fetch(
      `${API_BASE_URL}/favorites/check/${props.userId}/${props.targetType}/${props.targetId}`
    )
    const data = await response.json()

    if (data.code === 0) {
      isFavorited.value = data.data.isFavorited
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

/**
 * 切换收藏状态
 */
const toggleFavorite = async () => {
  if (!props.userId) {
    ElMessage.warning('请先登录')
    return
  }

  loading.value = true
  try {
    if (isFavorited.value) {
      // 取消收藏
      const response = await fetch(
        `${API_BASE_URL}/favorites/${props.userId}/${props.targetType}/${props.targetId}`,
        {
          method: 'DELETE'
        }
      )
      const data = await response.json()

      if (data.code === 0) {
        isFavorited.value = false
        ElMessage.success('取消收藏成功')
        emit('favorite-changed', false)
      } else {
        ElMessage.error(data.message || '取消收藏失败')
      }
    } else {
      // 添加收藏
      const response = await fetch(`${API_BASE_URL}/favorites`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          userId: props.userId,
          targetType: props.targetType,
          targetId: props.targetId
        })
      })
      const data = await response.json()

      if (data.code === 0) {
        isFavorited.value = true
        ElMessage.success('收藏成功')
        emit('favorite-changed', true)
      } else {
        ElMessage.error(data.message || '收藏失败')
      }
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  checkFavorite()
})
</script>

<style scoped>
.favorite-button-container {
  display: inline-block;
}
</style>
