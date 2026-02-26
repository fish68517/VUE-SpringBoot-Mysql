<template>
  <div class="browsing-history-container">
    <div class="history-header">
      <el-button type="danger" @click="handleClearAll" v-if="historyList.length > 0">
        清空浏览历史
      </el-button>
    </div>

    <el-empty v-if="historyList.length === 0" description="暂无浏览历史" />

    <div v-else class="history-list">
      <div v-for="item in historyList" :key="item.id" class="history-item">
        <div class="item-content">
          <div class="item-title">
            <span class="item-type" :class="'type-' + item.targetType">
              {{ getTargetTypeName(item.targetType) }}
            </span>
            <span class="item-name">{{ getItemName(item) }}</span>
          </div>
          <div class="item-time">
            浏览时间：{{ formatTime(item.createdAt) }}
          </div>
        </div>
        <div class="item-actions">
          <el-button type="primary" size="small" @click="handleViewDetail(item)">
            查看详情
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(item)">
            删除
          </el-button>
        </div>
      </div>
    </div>

    <el-pagination
      v-if="total > pageSize"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      style="margin-top: 20px; text-align: center"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { browsingHistoryApi } from '../api/browsingHistory'
import { attractionApi } from '../api/attraction'
import { hotelApi } from '../api/hotel'
import { productApi } from '../api/product'

const router = useRouter()

const historyList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const itemCache = ref({})

const userId = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user).id : null
})

onMounted(() => {
  if (userId.value) {
    loadBrowsingHistory()
  }
})

const loadBrowsingHistory = async () => {
  try {
    const response = await browsingHistoryApi.getBrowsingHistory(
      userId.value,
      currentPage.value - 1,
      pageSize.value
    )
    
    if (response.code === '0') {
      historyList.value = response.data.content || []
      total.value = response.data.totalElements || 0
      
      // 预加载项目信息
      for (const item of historyList.value) {
        await loadItemInfo(item)
      }
    } else {
      ElMessage.error(response.message || '加载浏览历史失败')
    }
  } catch (error) {
    ElMessage.error('加载浏览历史失败')
  }
}

const loadItemInfo = async (item) => {
  const key = `${item.targetType}_${item.targetId}`
  if (itemCache.value[key]) {
    return
  }
  
  try {
    let response
    if (item.targetType === 'attraction') {
      response = await attractionApi.getDetail(item.targetId)
    } else if (item.targetType === 'hotel') {
      response = await hotelApi.getDetail(item.targetId)
    } else if (item.targetType === 'product') {
      response = await productApi.getDetail(item.targetId)
    }
    
    if (response && response.code === '0') {
      itemCache.value[key] = response.data
    }
  } catch (error) {
    console.error('加载项目信息失败:', error)
  }
}

const getTargetTypeName = (type) => {
  const typeMap = {
    'attraction': '景点',
    'hotel': '酒店',
    'product': '商品'
  }
  return typeMap[type] || type
}

const getItemName = (item) => {
  const key = `${item.targetType}_${item.targetId}`
  const cachedItem = itemCache.value[key]
  if (cachedItem) {
    return cachedItem.name || '未知'
  }
  return '加载中...'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const handleViewDetail = (item) => {
  const routeMap = {
    'attraction': `/attractions/${item.targetId}`,
    'hotel': `/hotels/${item.targetId}`,
    'product': `/products/${item.targetId}`
  }
  
  const route = routeMap[item.targetType]
  if (route) {
    router.push(route)
  }
}

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条浏览历史吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 从列表中删除
    const index = historyList.value.findIndex(h => h.id === item.id)
    if (index > -1) {
      historyList.value.splice(index, 1)
      total.value--
      ElMessage.success('删除成功')
    }
  } catch (error) {
    // 用户取消删除
  }
}

const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有浏览历史吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await browsingHistoryApi.deleteAll(userId.value)
    if (response.code === '0') {
      historyList.value = []
      total.value = 0
      ElMessage.success('浏览历史已清空')
    } else {
      ElMessage.error(response.message || '清空失败')
    }
  } catch (error) {
    // 用户取消清空
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadBrowsingHistory()
}
</script>

<style scoped>
.browsing-history-container {
  padding: 20px;
}

.history-header {
  margin-bottom: 20px;
  text-align: right;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  transition: all 0.3s ease;
}

.history-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.item-content {
  flex: 1;
}

.item-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 500;
}

.item-type {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.type-attraction {
  background-color: #409eff;
}

.type-hotel {
  background-color: #67c23a;
}

.type-product {
  background-color: #e6a23c;
}

.item-name {
  color: #333;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.item-actions {
  display: flex;
  gap: 10px;
  margin-left: 20px;
}

@media (max-width: 768px) {
  .history-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .item-actions {
    margin-left: 0;
    margin-top: 10px;
    width: 100%;
  }

  .item-actions button {
    flex: 1;
  }
}
</style>
