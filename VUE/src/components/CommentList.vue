<template>
  <div class="comment-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>游客评价 ({{ totalComments }})</span>
        </div>
      </template>

      <!-- 加载状态 -->
      <el-skeleton v-if="loading" :rows="3" animated />

      <!-- 评价列表 -->
      <div v-else-if="comments.length > 0" class="comments">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <!-- 置顶标签 -->
          <div v-if="comment.isPinned" class="pinned-badge">
            <el-icon><Top /></el-icon>
            置顶
          </div>

          <!-- 评分和用户信息 -->
          <div class="comment-header">
            <div class="user-info">
              <span class="user-id">用户 #{{ comment.userId }}</span>
              <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <el-rate
              v-model="comment.rating"
              :max="5"
              disabled
              show-score
              score-template="{value} 分"
            />
          </div>

          <!-- 评价内容 -->
          <div class="comment-content">
            {{ comment.content }}
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无评价" />

      <!-- 分页 -->
      <div v-if="comments.length > 0" class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20]"
          :total="totalComments"
          layout="total, sizes, prev, pager, next, jumper"
          @change="loadComments"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Top } from '@element-plus/icons-vue'

const props = defineProps({
  targetType: {
    type: String,
    required: true,
    validator: (value) => ['attraction', 'hotel'].includes(value)
  },
  targetId: {
    type: Number,
    required: true
  }
})

const comments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalComments = ref(0)

const API_BASE_URL = 'http://localhost:8080'

/**
 * 加载评价列表
 */
const loadComments = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `${API_BASE_URL}/comments/target/${props.targetType}/${props.targetId}?page=${currentPage.value - 1}&size=${pageSize.value}`
    )
    const data = await response.json()

    if (data.code === 0) {
      comments.value = data.data.comments || []
      totalComments.value = data.data.total || 0
    } else {
      ElMessage.error(data.message || '加载评价失表')
    }
  } catch (error) {
    ElMessage.error('加载评价失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 格式化日期
 */
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 页面加载时获取评价列表
onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-list-container {
  margin: 20px 0;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  color: #333;
}

.comments {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  position: relative;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.pinned-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background-color: #ffd666;
  color: #333;
  border-radius: 3px;
  font-size: 12px;
  font-weight: bold;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #666;
}

.user-id {
  font-weight: bold;
  color: #333;
}

.comment-date {
  color: #999;
}

.comment-content {
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
