<template>
  <div class="favorites-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
          <el-tag v-if="totalCount > 0" type="info">{{ totalCount }}</el-tag>
        </div>
      </template>

      <!-- 筛选选项 -->
      <div class="filter-section">
        <el-select
          v-model="selectedType"
          placeholder="选择类型"
          @change="handleTypeChange"
          clearable
        >
          <el-option label="全部" value="" />
          <el-option label="景点" value="attraction" />
          <el-option label="酒店" value="hotel" />
          <el-option label="商品" value="product" />
        </el-select>
      </div>

      <!-- 收藏列表 -->
      <el-empty v-if="favorites.length === 0" description="暂无收藏" />

      <el-table
        v-else
        :data="favorites"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="targetType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.targetType)">
              {{ getTypeLabel(row.targetType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="项目ID" width="100" />
        <el-table-column prop="createdAt" label="收藏时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              @click="removeFavorite(row.id, row.targetType, row.targetId)"
              :loading="removingId === row.id"
            >
              取消收藏
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-if="totalCount > pageSize"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="totalCount"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const favorites = ref([])
const selectedType = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const loading = ref(false)
const removingId = ref(null)

const API_BASE_URL = 'http://localhost:8080/api'

/**
 * 获取类型标签类型
 */
const getTypeTagType = (type) => {
  const typeMap = {
    attraction: 'success',
    hotel: 'warning',
    product: 'info'
  }
  return typeMap[type] || 'info'
}

/**
 * 获取类型标签文本
 */
const getTypeLabel = (type) => {
  const typeMap = {
    attraction: '景点',
    hotel: '酒店',
    product: '商品'
  }
  return typeMap[type] || type
}

/**
 * 格式化日期
 */
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

/**
 * 加载收藏列表
 */
const loadFavorites = async () => {
  if (!props.userId) return

  loading.value = true
  try {
    let url = `${API_BASE_URL}/favorites/user/${props.userId}?page=${currentPage.value - 1}&size=${pageSize.value}`

    if (selectedType.value) {
      url = `${API_BASE_URL}/favorites/user/${props.userId}/type/${selectedType.value}`
    }

    const response = await fetch(url)
    const data = await response.json()

    if (data.code === '0') {
      if (selectedType.value) {
        // 如果选择了类型，手动处理分页
        const allFavorites = data.data.favorites
        totalCount.value = allFavorites.length
        const start = (currentPage.value - 1) * pageSize.value
        const end = start + pageSize.value
        favorites.value = allFavorites.slice(start, end)
      } else {
        favorites.value = data.data.favorites
        totalCount.value = data.data.total
      }
    } else {
      ElMessage.error(data.message || '加载收藏列表失败')
    }
  } catch (error) {
    ElMessage.error('加载收藏列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 取消收藏
 */
const removeFavorite = async (id, targetType, targetId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    removingId.value = id
    const response = await fetch(
      `${API_BASE_URL}/favorites/${props.userId}/${targetType}/${targetId}`,
      {
        method: 'DELETE'
      }
    )
    const data = await response.json()

    if (data.code === '0') {
      ElMessage.success('取消收藏成功')
      loadFavorites()
    } else {
      ElMessage.error(data.message || '取消收藏失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消收藏失败: ' + error.message)
    }
  } finally {
    removingId.value = null
  }
}

/**
 * 处理类型变更
 */
const handleTypeChange = () => {
  currentPage.value = 1
  loadFavorites()
}

/**
 * 处理页码变更
 */
const handlePageChange = () => {
  loadFavorites()
}

/**
 * 处理每页数量变更
 */
const handlePageSizeChange = () => {
  currentPage.value = 1
  loadFavorites()
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-list-container {
  margin: 20px 0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: bold;
  color: #333;
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.filter-section :deep(.el-select) {
  width: 150px;
}
</style>
