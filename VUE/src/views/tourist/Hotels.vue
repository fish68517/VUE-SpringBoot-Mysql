<template>
  <div class="hotels-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>酒店列表</span>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索酒店"
            @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="hotel in hotels" :key="hotel.id">
          <el-card class="hotel-card" @click="goToDetail(hotel.id)">
            <div class="hotel-image">
              <el-image 
                :src="getFullImageUrl(hotel.imageUrl)" 
                fit="cover"
                style="width: 100%; height: 200px"
              />
            </div>
            <h3>{{ hotel.name }}</h3>
            <div class="rating">
              <el-rate v-model="hotel.rating" disabled size="small" />
            </div>
            <p class="location">📍 {{ hotel.location }}</p>
            <div class="price" v-if="hotel.rooms && hotel.rooms.length > 0">
              ¥{{ Math.min(...hotel.rooms.map(r => r.pricePerNight)) }}/晚起
            </div>
            <el-button type="primary" size="small" style="width: 100%">
              查看详情
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: center"
        @change="loadHotels"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const searchForm = ref({
  keyword: ''
})

const hotels = ref([])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const loadHotels = async () => {
  try {
    let url = 'http://localhost:8080/api/hotels/list?page=' + (pagination.value.currentPage - 1) + '&size=' + pagination.value.pageSize
    
    if (searchForm.value.keyword) {
      url = 'http://localhost:8080/api/hotels/search?keyword=' + searchForm.value.keyword + '&page=' + (pagination.value.currentPage - 1) + '&size=' + pagination.value.pageSize
    }
    
    const response = await fetch(url)
    const data = await response.json()
    
    if (data.code === '0') {
      hotels.value = data.data.hotels
      pagination.value.total = data.data.total
    } else {
      ElMessage.error(data.message || '加载酒店列表失败')
    }
  } catch (error) {
    ElMessage.error('加载酒店列表失败: ' + error.message)
  }
}


// 拼接完整的图片 URL 供前端显示
const getFullImageUrl = (url) => {
  if (!url) return ''
  // 如果已经是完整的网络图片地址（比如外链），直接返回
  if (url.startsWith('http')) return url 
  // 如果是相对路径（我们自己上传的），拼接上 Spring Boot 后端的地址
  return `http://localhost:8080/api${url}`
}

const handleSearch = () => {
  pagination.value.currentPage = 1
  loadHotels()
}

const goToDetail = (hotelId) => {
  router.push({ name: 'HotelDetail', params: { id: hotelId } })
}

onMounted(() => {
  loadHotels()
})
</script>

<style scoped>
.hotels-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}

.hotel-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.hotel-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.hotel-image {
  margin-bottom: 10px;
  border-radius: 4px;
  overflow: hidden;
}

.hotel-card h3 {
  margin: 10px 0;
  color: #333;
  font-size: 16px;
}

.rating {
  margin: 5px 0;
}

.location {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
}

.price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0;
}

/* Tablet screens */
@media (max-width: 1024px) {
  .hotels-container {
    padding: 15px;
  }

  .hotel-card h3 {
    font-size: 15px;
  }

  .location {
    font-size: 13px;
  }

  .price {
    font-size: 16px;
  }
}

/* Tablet and smaller */
@media (max-width: 768px) {
  .hotels-container {
    padding: 12px;
  }

  .card-header {
    font-size: 14px;
  }

  .hotel-card h3 {
    font-size: 14px;
    margin: 8px 0;
  }

  .location {
    font-size: 12px;
    margin: 4px 0;
  }

  .price {
    font-size: 15px;
    margin: 8px 0;
  }
}

/* Mobile devices */
@media (max-width: 480px) {
  .hotels-container {
    padding: 10px;
  }

  .card-header {
    font-size: 13px;
  }

  .hotel-card h3 {
    font-size: 13px;
    margin: 6px 0;
  }

  .location {
    font-size: 11px;
    margin: 3px 0;
  }

  .price {
    font-size: 14px;
    margin: 6px 0;
  }
}

/* Extra small devices */
@media (max-width: 360px) {
  .hotels-container {
    padding: 8px;
  }

  .hotel-card h3 {
    font-size: 12px;
  }

  .location {
    font-size: 10px;
  }

  .price {
    font-size: 13px;
  }
}
</style>
