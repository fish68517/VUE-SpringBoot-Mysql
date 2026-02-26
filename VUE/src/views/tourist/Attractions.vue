<template>
  <div class="attractions-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>景点列表</span>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索景点"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select 
            v-model="searchForm.tag" 
            placeholder="选择标签筛选"
            clearable
            @change="handleSearch"
          >
            <el-option-group label="广州特色">
              <el-option label="广州特色景点" value="广州特色" />
            </el-option-group>
            <el-option-group label="景点类型">
              <el-option label="美食" value="美食" />
              <el-option label="文化" value="文化" />
              <el-option label="历史" value="历史" />
              <el-option label="现代" value="现代" />
              <el-option label="建筑" value="建筑" />
              <el-option label="公园" value="公园" />
              <el-option label="休闲" value="休闲" />
              <el-option label="自然" value="自然" />
              <el-option label="生态" value="生态" />
              <el-option label="动物" value="动物" />
              <el-option label="家庭" value="家庭" />
              <el-option label="夜景" value="夜景" />
            </el-option-group>
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="clearSearch">清空</el-button>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="attraction in attractions" :key="attraction.id">
          <el-card class="attraction-card" @click="goToDetail(attraction.id)">
            <div class="attraction-image">
              <el-image 
                :src="attraction.imageUrl || 'https://via.placeholder.com/300x200'" 
                fit="cover"
                style="width: 100%; height: 200px"
              />
              <div v-if="attraction.isGuangzhouSpecial" class="guangzhou-badge">
                广州特色
              </div>
            </div>
            <h3>{{ attraction.name }}</h3>
            <div class="tags" v-if="attraction.tags && attraction.tags.length > 0">
              <el-tag
                v-for="tag in attraction.tags.slice(0, 2)"
                :key="tag"
                size="small"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ tag }}
              </el-tag>
              <el-tag
                v-if="attraction.tags.length > 2"
                size="small"
                style="margin-bottom: 4px"
              >
                +{{ attraction.tags.length - 2 }}
              </el-tag>
            </div>
            <p class="description">{{ attraction.description }}</p>
            <div class="price">¥{{ attraction.ticketPrice }}</div>
            <el-button type="primary" size="small" style="width: 100%">
              查看详情
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="attractions.length === 0" description="暂无景点数据" />

      <el-pagination
        v-if="pagination.total > 0"
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: center"
        @current-change="loadAttractions"
        @size-change="loadAttractions"
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
  keyword: '',
  tag: ''
})

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const attractions = ref([])
const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080/api'

/**
 * 加载景点列表
 */
const loadAttractions = async () => {
  loading.value = true
  try {
    let url = `${API_BASE_URL}/attractions/list?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    
    // 根据搜索条件调整URL
    if (searchForm.value.keyword) {
      url = `${API_BASE_URL}/attractions/search?keyword=${searchForm.value.keyword}&page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    } else if (searchForm.value.tag === '广州特色') {
      url = `${API_BASE_URL}/attractions/guangzhou-special?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    } else if (searchForm.value.tag) {
      url = `${API_BASE_URL}/attractions/by-tag?tagName=${searchForm.value.tag}&page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    }
    
    const response = await fetch(url)
    const data = await response.json()
    
    if (data.code === '0') {
      attractions.value = data.data.attractions
      pagination.value.total = data.data.total
    } else {
      ElMessage.error(data.message || '加载景点列表失败')
    }
  } catch (error) {
    ElMessage.error('加载景点列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  pagination.value.currentPage = 1
  loadAttractions()
}

/**
 * 清空搜索条件
 */
const clearSearch = () => {
  searchForm.value.keyword = ''
  searchForm.value.tag = ''
  pagination.value.currentPage = 1
  loadAttractions()
}

/**
 * 跳转到景点详情页面
 */
const goToDetail = (attractionId) => {
  router.push(`/attractions/${attractionId}`)
}

// 页面加载时获取景点列表
onMounted(() => {
  loadAttractions()
})
</script>

<style scoped>
.attractions-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}

.attraction-card {
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.attraction-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.attraction-image {
  margin-bottom: 10px;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.guangzhou-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: #67c23a;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.attraction-card h3 {
  margin: 10px 0;
  color: #333;
  font-size: 16px;
}

.tags {
  margin: 8px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.description {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0;
}

/* Tablet screens */
@media (max-width: 1024px) {
  .attractions-container {
    padding: 15px;
  }

  .attraction-card h3 {
    font-size: 15px;
  }

  .description {
    font-size: 13px;
  }

  .price {
    font-size: 16px;
  }
}

/* Tablet and smaller */
@media (max-width: 768px) {
  .attractions-container {
    padding: 12px;
  }

  .card-header {
    font-size: 14px;
  }

  .attraction-card h3 {
    font-size: 14px;
    margin: 8px 0;
  }

  .tags {
    margin: 6px 0;
    gap: 3px;
  }

  .tags :deep(.el-tag) {
    font-size: 11px;
  }

  .description {
    font-size: 12px;
    margin: 4px 0;
  }

  .price {
    font-size: 15px;
    margin: 8px 0;
  }

  .guangzhou-badge {
    font-size: 11px;
    padding: 3px 6px;
    top: 6px;
    right: 6px;
  }
}

/* Mobile devices */
@media (max-width: 480px) {
  .attractions-container {
    padding: 10px;
  }

  .card-header {
    font-size: 13px;
  }

  .attraction-card h3 {
    font-size: 13px;
    margin: 6px 0;
  }

  .tags {
    margin: 5px 0;
    gap: 2px;
  }

  .tags :deep(.el-tag) {
    font-size: 10px;
  }

  .description {
    font-size: 11px;
    margin: 3px 0;
  }

  .price {
    font-size: 14px;
    margin: 6px 0;
  }

  .guangzhou-badge {
    font-size: 10px;
    padding: 2px 5px;
    top: 4px;
    right: 4px;
  }
}

/* Extra small devices */
@media (max-width: 360px) {
  .attractions-container {
    padding: 8px;
  }

  .attraction-card h3 {
    font-size: 12px;
  }

  .description {
    font-size: 10px;
  }

  .price {
    font-size: 13px;
  }
}
</style>
