<template>
  <div class="favorites-container">
    <el-card>
      <template #header>
        <span>我的收藏座位</span>
      </template>

      <div v-if="favorites.length === 0" class="empty-tip">
        暂无收藏的座位
      </div>

      <div v-else class="favorites-grid">
        <div v-for="item in favorites" :key="item.id" class="favorite-item">
          <div class="seat-info">
            <div class="seat-no">{{ item.area }}-{{ item.seatNo }}</div>
            <div class="seat-tags">
              <el-tag :type="item.status === 1 ? 'success' : 'danger'" size="small">
                {{ item.status === 1 ? '可用' : '不可用' }}
              </el-tag>
            </div>
          </div>
          <div class="actions">
            <el-button type="primary" size="small" @click="goReserve(item)">
              立即预约
            </el-button>
            <el-button type="danger" size="small" @click="handleRemove(item)">
              取消收藏
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyFavorites, removeFavorite } from '../api/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const favorites = ref([])

const loadFavorites = async () => {
  const res = await getMyFavorites()
  favorites.value = res.data
}

const handleRemove = async (item) => {
  await ElMessageBox.confirm('确定取消收藏该座位吗？', '提示', { type: 'warning' })
  await removeFavorite(item.seatId)
  ElMessage.success('已取消收藏')
  loadFavorites()
}

const goReserve = (item) => {
  router.push('/reserve')
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-container {
  max-width: 1000px;
  margin: 0 auto;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.favorite-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.seat-no {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.seat-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 15px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>
