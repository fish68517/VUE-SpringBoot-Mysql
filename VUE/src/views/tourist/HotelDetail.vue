<template>
  <div class="hotel-detail-container">
    <el-card v-if="hotel">
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" type="info">返回</el-button>
          <span>{{ hotel.name }}</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :xs="24" :md="12">
          <div class="hotel-image">
            <el-image 
              :src="getFullImageUrl(hotel.imageUrl)" 
              fit="cover"
              style="width: 100%; height: 400px; border-radius: 4px"
            />
          </div>
        </el-col>
        
        <el-col :xs="24" :md="12">
          <div class="hotel-info">
            <h2>{{ hotel.name }}</h2>
            
            <div class="rating-section">
              <span>评分：</span>
              <el-rate v-model="hotel.rating" disabled size="large" />
              <span class="rating-value">{{ hotel.rating }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">📍 位置：</span>
              <span>{{ hotel.location }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">📝 描述：</span>
              <p>{{ hotel.description }}</p>
            </div>
            
            <div class="rooms-section">
              <h3>房间类型</h3>
              <el-table :data="hotel.rooms" stripe style="width: 100%">
                <el-table-column prop="roomType" label="房间类型" width="120" />
                <el-table-column prop="pricePerNight" label="价格/晚" width="100">
                  <template #default="{ row }">
                    ¥{{ row.pricePerNight }}
                  </template>
                </el-table-column>
                <el-table-column prop="quantity" label="可用房间" width="100" />
                <el-table-column label="操作" width="100">
                  <template #default>
                    <el-button type="primary" size="small" @click="bookRoom">
                      预订
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 评价 -->
      <el-divider />
      <div class="comments-section">
        <h3>游客评价</h3>
        
        <!-- 评价表单 -->
        <CommentForm
          v-if="currentUser"
          :target-type="'hotel'"
          :target-id="hotel.id"
          :user-id="currentUser.id"
          @comment-submitted="onCommentSubmitted"
        />
        
        <!-- 评价列表 -->
        <CommentList
          :target-type="'hotel'"
          :target-id="hotel.id"
          :key="commentListKey"
        />
      </div>
    </el-card>
    
    <el-empty v-else description="酒店信息加载中..." />
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


// 拼接完整的图片 URL 供前端显示
const getFullImageUrl = (url) => {
  if (!url) return ''
  // 如果已经是完整的网络图片地址（比如外链），直接返回
  if (url.startsWith('http')) return url 
  // 如果是相对路径（我们自己上传的），拼接上 Spring Boot 后端的地址
  return `http://localhost:8080/api${url}`
}

const hotel = ref(null)
const currentUser = ref(null)
const commentListKey = ref(0)

const loadHotelDetail = async () => {
  try {
    const hotelId = route.params.id
    const response = await fetch(`http://localhost:8080/api/hotels/${hotelId}`)
    const data = await response.json()
    
    if (data.code === '0') {
      hotel.value = data.data
    } else {
      ElMessage.error(data.message || '加载酒店详情失败')
    }
  } catch (error) {
    ElMessage.error('加载酒店详情失败: ' + error.message)
  }
}

const goBack = () => {
  router.back()
}

const bookRoom = () => {
  router.push(`/hotels/${route.params.id}/booking`)
}

/**
 * 处理评价提交
 */
const onCommentSubmitted = () => {
  // 刷新评价列表
  commentListKey.value++
}

onMounted(() => {
  loadHotelDetail()
  
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
.hotel-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: bold;
  color: #333;
}

.hotel-image {
  margin-bottom: 20px;
}

.hotel-info {
  padding: 20px;
}

.hotel-info h2 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 28px;
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.rating-value {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
}

.info-item {
  margin-bottom: 15px;
  line-height: 1.6;
}

.label {
  font-weight: bold;
  color: #333;
  margin-right: 10px;
}

.rooms-section {
  margin-top: 30px;
}

.rooms-section h3 {
  margin-bottom: 15px;
  color: #333;
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
