<template>
  <div class="attraction-detail-container">
    <el-card v-if="attraction">
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" type="info" text>← 返回</el-button>
          <span>{{ attraction.name }}</span>
        </div>
      </template>

      <el-row :gutter="20">
        <!-- 左侧图片 -->
        <el-col :xs="24" :md="12">
          <div class="image-container">
            <el-image
              :src="attraction.imageUrl || 'https://via.placeholder.com/500x400'"
              fit="cover"
              style="width: 100%; height: 400px; border-radius: 4px"
            />
          </div>
        </el-col>

        <!-- 右侧信息 -->
        <el-col :xs="24" :md="12">
          <div class="info-container">
            <h2>{{ attraction.name }}</h2>

            <!-- 标签 -->
            <div class="tags" v-if="attraction.tags && attraction.tags.length > 0">
              <el-tag
                v-for="tag in attraction.tags"
                :key="tag"
                style="margin-right: 8px; margin-bottom: 8px"
              >
                {{ tag }}
              </el-tag>
              <el-tag
                v-if="attraction.isGuangzhouSpecial"
                type="success"
                style="margin-right: 8px; margin-bottom: 8px"
              >
                广州特色
              </el-tag>
            </div>

            <!-- 基本信息 -->
            <div class="basic-info">
              <div class="info-item">
                <span class="label">位置：</span>
                <span class="value">{{ attraction.location || '未提供' }}</span>
              </div>
              <div class="info-item">
                <span class="label">门票价格：</span>
                <span class="value price">¥{{ attraction.ticketPrice }}</span>
              </div>
              <div class="info-item">
                <span class="label">营业时间：</span>
                <span class="value">{{ attraction.openingHours || '未提供' }}</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="actions">
              <el-button type="primary" size="large" style="width: 100%; margin-bottom: 10px" @click="goToBooking">
                预订门票
              </el-button>
              <el-button
                :type="isFavorited ? 'danger' : 'default'"
                size="large"
                style="width: 100%"
                @click="toggleFavorite"
              >
                {{ isFavorited ? '取消收藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 描述 -->
      <el-divider />
      <div class="description-section">
        <h3>景点介绍</h3>
        <p class="description">{{ attraction.description || '暂无介绍' }}</p>
      </div>

      <!-- 评价 -->
      <el-divider />
      <div class="comments-section">
        <h3>游客评价</h3>
        
        <!-- 评价表单 -->
        <CommentForm
          v-if="currentUser"
          :target-type="'attraction'"
          :target-id="attraction.id"
          :user-id="currentUser.id"
          @comment-submitted="onCommentSubmitted"
        />
        
        <!-- 评价列表 -->
        <CommentList
          :target-type="'attraction'"
          :target-id="attraction.id"
          :key="commentListKey"
        />
      </div>
    </el-card>

    <el-card v-else>
      <el-empty description="景点不存在" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import CommentForm from '@/components/CommentForm.vue'
import CommentList from '@/components/CommentList.vue'

const router = useRouter()
const route = useRoute()

const attraction = ref(null)
const isFavorited = ref(false)
const loading = ref(false)
const currentUser = ref(null)
const commentListKey = ref(0)

const API_BASE_URL = 'http://localhost:8080'

/**
 * 加载景点详情
 */
const loadAttractionDetail = async () => {
  loading.value = true
  try {
    const attractionId = route.params.id
    const response = await fetch(`${API_BASE_URL}/attractions/${attractionId}`)
    const data = await response.json()

    if (data.code === 0) {
      attraction.value = data.data
    } else {
      ElMessage.error(data.message || '加载景点详情失败')
      setTimeout(() => {
        router.push('/attractions')
      }, 1500)
    }
  } catch (error) {
    ElMessage.error('加载景点详情失败: ' + error.message)
    setTimeout(() => {
      router.push('/attractions')
    }, 1500)
  } finally {
    loading.value = false
  }
}

/**
 * 返回上一页
 */
const goBack = () => {
  router.back()
}

/**
 * 跳转到预订页面
 */
const goToBooking = () => {
  const user = localStorage.getItem('user')
  if (!user) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  router.push(`/attractions/${route.params.id}/booking`)
}

/**
 * 切换收藏状态
 */
const toggleFavorite = () => {
  isFavorited.value = !isFavorited.value
  const message = isFavorited.value ? '已收藏' : '已取消收藏'
  ElMessage.success(message)
}

/**
 * 处理评价提交
 */
const onCommentSubmitted = () => {
  // 刷新评价列表
  commentListKey.value++
}

// 页面加载时获取景点详情
onMounted(() => {
  loadAttractionDetail()
  
  // 获取当前登录用户
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      currentUser.value = JSON.parse(userStr)
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }
})
</script>

<style scoped>
.attraction-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: bold;
  color: #333;
}

.image-container {
  margin-bottom: 20px;
}

.info-container {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.info-container h2 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 24px;
}

.tags {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
}

.basic-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}

.info-item .label {
  font-weight: bold;
  color: #666;
  min-width: 80px;
}

.info-item .value {
  color: #333;
  flex: 1;
}

.info-item .price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: bold;
}

.actions {
  margin-top: 20px;
}

.description-section {
  margin: 20px 0;
}

.description-section h3 {
  margin-bottom: 10px;
  color: #333;
  font-size: 16px;
}

.description {
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.comments-section {
  margin: 20px 0;
}

.comments-section h3 {
  margin-bottom: 10px;
  color: #333;
  font-size: 16px;
}
</style>
