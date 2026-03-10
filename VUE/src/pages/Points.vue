<template>
  <div class="points-container">
    <div class="points-header">
      <div class="header-content">
        <el-button type="primary" text icon="ArrowLeft" @click="handleBackToHome">返回首页</el-button>
        <h1>打卡与积分商城</h1>
        <div class="user-points">
          <el-icon><Coin /></el-icon>
          当前积分：<span class="points-num">{{ userPoints }}</span>
        </div>
      </div>
    </div>

    <div class="points-content">
      <div class="tabs-section">
        <el-tabs v-model="activeTab">
          
          <el-tab-pane label="打卡任务" name="tasks">
            <div class="task-grid">
              <div v-for="task in tasks" :key="task.id" class="task-card">
                <div class="task-info">
                  <h3>{{ task.name }}</h3>
                  <p>{{ task.description }}</p>
                  <div class="task-reward">
                    <el-tag type="warning" effect="dark">+{{ task.points }} 积分</el-tag>
                  </div>
                </div>
                <div class="task-action">
                  <el-button 
                    :type="task.status === 'completed' ? 'info' : 'primary'" 
                    :disabled="task.status === 'completed'"
                    @click="handleCheckin(task)"
                  >
                    {{ task.status === 'completed' ? '已打卡' : '去打卡' }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="积分商城" name="mall">
            <div class="mall-grid">
              <div v-for="item in mallItems" :key="item.id" class="mall-card">
                <div class="mall-image">
                  <img :src="item.image" :alt="item.name" onerror="this.src='https://via.placeholder.com/200x200?text=Gift'" />
                </div>
                <div class="mall-info">
                  <h3>{{ item.name }}</h3>
                  <p class="desc">{{ item.description }}</p>
                  <div class="mall-meta">
                    <span class="cost">{{ item.pointsRequired }} 积分</span>
                    <span class="stock">仅剩 {{ item.stock }} 件</span>
                  </div>
                  <el-button 
                    type="primary" 
                    class="exchange-btn" 
                    :disabled="item.stock <= 0 || userPoints < item.pointsRequired"
                    @click="handleExchange(item)"
                  >
                    {{ userPoints < item.pointsRequired ? '积分不足' : '立即兑换' }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="积分明细" name="history">
            <el-timeline class="history-timeline">
              <el-timeline-item 
                v-for="record in history" 
                :key="record.id" 
                :type="record.changeAmount > 0 ? 'success' : 'warning'"
                :timestamp="formatDate(record.createdAt)"
                placement="top"
              >
                <el-card>
                  <h4>{{ record.description }}</h4>
                  <p :class="record.changeAmount > 0 ? 'plus' : 'minus'">
                    {{ record.changeAmount > 0 ? '+' : '' }}{{ record.changeAmount }}
                  </p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </el-tab-pane>

        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Coin, ArrowLeft } from '@element-plus/icons-vue'
import { pointsApi } from '@/api/points'

const router = useRouter()
const activeTab = ref('tasks')

// 修改点 1：初始积分设为 0，等待从后端获取真实数据
const userPoints = ref(0) 

// 数据定义
const tasks = ref<any[]>([])
const mallItems = ref<any[]>([])
const history = ref<any[]>([])

const handleBackToHome = () => {
  router.push('/')
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

// 加载数据 
const loadData = async () => {
  // 1. 获取用户真实积分余额
  try {
    const res: any = await pointsApi.getUserPoints()
    if (res.code === 200) userPoints.value = res.data || 0
  } catch (e) {
    console.error("获取积分余额失败", e)
  }

  // 2. 获取打卡任务
  try {
    const res: any = await pointsApi.getTasks()
    if (res.code === 200) tasks.value = res.data
    else throw new Error()
  } catch (e) {
    // 后端没数据时的兜底 Mock
    tasks.value = [
      { id: 1, name: '音乐节正门签到', description: '到达沈阳音乐节正门入口处进行地理位置打卡签到', points: 50, status: 'ongoing' },
    ]
  }

  // 3. 获取商城商品
  try {
    const res: any = await pointsApi.getMallItems()
    if (res.code === 200) mallItems.value = res.data
    else throw new Error()
  } catch (e) {
    mallItems.value = [
      { id: 1, name: '沈阳音乐节定制帆布袋', description: '2024沈阳音乐节限量版周边', pointsRequired: 500, stock: 120, image: 'https://via.placeholder.com/300x300?text=Bag' },
    ]
  }

  // 4. 获取积分明细
  try {
    const res: any = await pointsApi.getHistory()
    if (res.code === 200) history.value = res.data
    else throw new Error()
  } catch (e) {
    history.value = []
  }
}

// 修改点 2：调用真实的后端打卡接口
const handleCheckin = async (task: any) => {
  try {
    const res: any = await pointsApi.checkin(task.id)
    if (res.code === 200) {
      ElMessage.success(`🎉 打卡成功！获得了 ${task.points} 积分！`)
      // 打卡成功后，重新调用 loadData 刷新全局数据（任务状态变灰、积分增加、多出一条流水记录）
      await loadData() 
    } else {
      ElMessage.error(res.message || '打卡失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '打卡过程中出现错误')
  }
}

// 修改点 3：调用真实的后端兑换接口
const handleExchange = (item: any) => {
  ElMessageBox.prompt('请输入收货地址及联系方式（虚拟物品填无）', '积分兑换', {
    confirmButtonText: '确认兑换',
    cancelButtonText: '取消',
  }).then(async ({ value }) => {
    try {
      const res: any = await pointsApi.exchangeItem(item.id, value || '虚拟物品无需地址')
      if (res.code === 200) {
        ElMessage.success('🎁 兑换成功！系统已扣除相应积分，请留意物流状态。')
        // 兑换成功后，重新调用 loadData 刷新全局数据（扣减积分、扣减库存、多出一条流水记录）
        await loadData()
      } else {
        ElMessage.error(res.message || '兑换失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '兑换过程中出现错误')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 样式部分保持完全不变 */
.points-container { min-height: 100vh; background: #f5f5f5; }
.points-header { background: white; padding: 20px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); position: sticky; top: 0; z-index: 100; }
.header-content { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.points-header h1 { color: #333; margin: 0; font-size: 24px; font-weight: bold; }
.user-points { display: flex; align-items: center; gap: 8px; font-size: 16px; color: #666; }
.points-num { font-size: 24px; font-weight: bold; color: #e74c3c; }

.points-content { max-width: 1200px; margin: 0 auto; padding: 30px 20px; }
.tabs-section { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }

/* 打卡任务样式 */
.task-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
.task-card { background: #f9f9f9; border-radius: 8px; padding: 20px; display: flex; flex-direction: column; justify-content: space-between; border-left: 4px solid #409eff; transition: transform 0.3s; }
.task-card:hover { transform: translateY(-3px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.task-info h3 { margin: 0 0 10px 0; color: #333; font-size: 16px; font-weight: bold; }
.task-info p { color: #666; font-size: 13px; line-height: 1.5; margin-bottom: 15px; }
.task-reward { margin-bottom: 15px; }
.task-action { text-align: right; }

/* 积分商城样式 */
.mall-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 20px; }
.mall-card { background: white; border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; transition: box-shadow 0.3s; }
.mall-card:hover { box-shadow: 0 8px 16px rgba(0,0,0,0.1); }
.mall-image { width: 100%; height: 200px; background: #f5f5f5; }
.mall-image img { width: 100%; height: 100%; object-fit: cover; }
.mall-info { padding: 15px; }
.mall-info h3 { margin: 0 0 8px 0; font-size: 16px; color: #333; font-weight: bold; }
.mall-info .desc { color: #999; font-size: 12px; margin-bottom: 15px; height: 36px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.mall-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.mall-meta .cost { font-size: 18px; font-weight: bold; color: #e74c3c; }
.mall-meta .stock { font-size: 12px; color: #999; }
.exchange-btn { width: 100%; }

/* 积分明细样式 */
.history-timeline { padding: 20px; max-width: 600px; margin: 0 auto; }
.history-timeline h4 { margin: 0 0 5px 0; font-size: 14px; color: #333; }
.history-timeline .plus { color: #67c23a; font-weight: bold; font-size: 16px; margin: 0; }
.history-timeline .minus { color: #f56c6c; font-weight: bold; font-size: 16px; margin: 0; }

@media (max-width: 768px) {
  .points-header h1 { font-size: 20px; }
  .user-points { font-size: 14px; }
  .points-num { font-size: 20px; }
  .task-grid, .mall-grid { grid-template-columns: 1fr; }
}
</style>