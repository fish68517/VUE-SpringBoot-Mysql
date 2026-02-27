<template>
  <div class="ranking-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>本月学习排行榜</span>
              <el-tag type="warning">TOP 20</el-tag>
            </div>
          </template>

          <div v-if="myRanking" class="my-rank">
            <span>我的排名：第 {{ myRanking.monthlyRank }} 名</span>
            <span>本月学习：{{ formatTime(myRanking.monthlyStudyTime) }}</span>
          </div>

          <el-table :data="monthlyRanking" :header-cell-style="{ textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
            <el-table-column prop="rank" label="排名" width="80" align="center">
              <template #default="{ row }">
                <span :class="['rank-num', 'rank-' + row.rank]">{{ row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="nickname" label="用户" align="center" />
            <el-table-column label="学习时长" width="150" align="center">
              <template #default="{ row }">
                {{ formatTime(row.studyTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>累计学习排行榜</span>
              <el-tag type="success">TOP 20</el-tag>
            </div>
          </template>

          <div v-if="myRanking" class="my-rank">
            <span>我的排名：第 {{ myRanking.totalRank }} 名</span>
            <span>累计学习：{{ formatTime(myRanking.totalStudyTime) }}</span>
          </div>

          <el-table :data="totalRanking" :header-cell-style="{ textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
            <el-table-column prop="rank" label="排名" width="80" align="center">
              <template #default="{ row }">
                <span :class="['rank-num', 'rank-' + row.rank]">{{ row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="nickname" label="用户" align="center" />
            <el-table-column label="学习时长" width="150" align="center">
              <template #default="{ row }">
                {{ formatTime(row.studyTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMonthlyRanking, getTotalRanking, getMyRanking } from '../api/ranking'

const monthlyRanking = ref([])
const totalRanking = ref([])
const myRanking = ref(null)

const formatTime = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins}分钟`
  }
  return `${mins}分钟`
}

const loadData = async () => {
  const [monthlyRes, totalRes, myRes] = await Promise.all([
    getMonthlyRanking(20),
    getTotalRanking(20),
    getMyRanking()
  ])
  monthlyRanking.value = monthlyRes.data
  totalRanking.value = totalRes.data
  myRanking.value = myRes.data
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.ranking-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.my-rank {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  font-weight: 500;
}

.rank-num {
  font-weight: bold;
  font-size: 16px;
}

.rank-1 {
  color: #f5af19;
  font-size: 20px;
}

.rank-2 {
  color: #bdc3c7;
  font-size: 18px;
}

.rank-3 {
  color: #c9a227;
  font-size: 17px;
}
</style>
