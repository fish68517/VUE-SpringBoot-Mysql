<template>
  <div class="station-detail-page">
    <el-skeleton v-if="loading" :rows="10" animated />
    <template v-else-if="station">
      <div class="detail-breadcrumb">
        <router-link to="/stations">充电站地图</router-link><span>/</span><span>{{ station.stationName }}</span>
      </div>

      <section class="station-detail-hero">
        <div>
          <div class="detail-labels">
            <el-tag :type="station.status === 1 ? 'success' : 'warning'">{{ stationStatusText(station.status) }}</el-tag>
            <span>{{ station.regionName }}</span>
            <span>{{ stationTypeText(station.stationType) }}</span>
          </div>
          <h1>{{ station.stationName }}</h1>
          <p>{{ station.address }}</p>
          <div class="detail-actions">
            <el-button type="primary" :loading="favoriteLoading" @click="toggleFavorite">
              {{ favoriteState.favorited ? '取消收藏' : '收藏站点' }}
            </el-button>
            <router-link :to="`/stations/${station.id}/guide`"><el-button>查看站内导览</el-button></router-link>
          </div>
        </div>
        <div class="availability-panel">
          <strong>{{ station.freeCount }}</strong>
          <span>当前空闲充电桩</span>
          <small>共 {{ station.pileCount }} 个充电桩</small>
        </div>
      </section>

      <section class="detail-metric-grid">
        <article v-for="metric in statusMetrics" :key="metric.key" :class="`detail-metric ${metric.tone}`">
          <span>{{ metric.label }}</span>
          <strong>{{ station[metric.key] || 0 }}</strong>
        </article>
      </section>

      <section class="detail-content-grid">
        <article class="detail-card station-information-card">
          <div class="card-section-title"><div><p class="eyebrow">STATION INFO</p><h2>站点信息</h2></div></div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="站点编号">{{ station.stationCode }}</el-descriptions-item>
            <el-descriptions-item label="运营商">{{ station.operatorName || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="营业时间">{{ station.openTime || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="经纬度">{{ station.longitude }}, {{ station.latitude }}</el-descriptions-item>
            <el-descriptions-item label="停车说明" :span="2">{{ station.parkingDesc || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="收费说明" :span="2">{{ station.feeDesc || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="配套设施" :span="2">{{ station.serviceFacilities || '暂无' }}</el-descriptions-item>
          </el-descriptions>
        </article>

        <article class="detail-card quick-guide-card">
          <p class="eyebrow">VISUAL GUIDE</p>
          <h2>站内导览</h2>
          <p>查看入口、停车区域、快充区、慢充区和服务设施的二维位置示意。</p>
          <div class="mini-guide-map">
            <span class="mini-road"></span>
            <i style="left: 12%; top: 74%">入口</i>
            <i style="left: 48%; top: 42%">充电区</i>
            <i style="left: 78%; top: 22%">服务区</i>
          </div>
          <router-link :to="`/stations/${station.id}/guide`" class="guide-link">进入可视化导览 →</router-link>
        </article>
      </section>

      <section class="detail-card pile-table-card">
        <div class="card-section-title">
          <div><p class="eyebrow">PILE STATUS</p><h2>充电桩实时状态</h2></div>
          <span>最后状态来自模拟数据</span>
        </div>
        <el-table :data="piles" stripe empty-text="暂无充电桩数据">
          <el-table-column prop="pileCode" label="充电桩编号" min-width="180" />
          <el-table-column prop="pileName" label="名称" min-width="120" />
          <el-table-column label="接口类型" width="110">
            <template #default="scope">{{ scope.row.connectorType === 0 ? '直流快充' : '交流慢充' }}</template>
          </el-table-column>
          <el-table-column label="额定功率" width="110">
            <template #default="scope">{{ scope.row.ratedPower }} kW</template>
          </el-table-column>
          <el-table-column label="当前状态" width="110">
            <template #default="scope">
              <el-tag :type="pileStatusType(scope.row.workStatus)" effect="light">{{ pileStatusText(scope.row.workStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="当前功率" width="110">
            <template #default="scope">{{ scope.row.currentPower ?? 0 }} kW</template>
          </el-table-column>
          <el-table-column label="更新时间" min-width="165">
            <template #default="scope">{{ formatTime(scope.row.statusTime) }}</template>
          </el-table-column>
        </el-table>
      </section>
    </template>

    <el-result v-else icon="error" title="站点信息加载失败" sub-title="请返回站点地图重新选择">
      <template #extra><router-link to="/stations"><el-button type="primary">返回站点地图</el-button></router-link></template>
    </el-result>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getStationDetail, getStationPiles } from '../../api/station'
import { addFavorite, checkFavorite, removeFavorite } from '../../api/user'
import { useUserStore } from '../../stores/user'

const props = defineProps({ id: { type: String, required: true } })
const router = useRouter()
const userStore = useUserStore()
const loading = ref(true)
const favoriteLoading = ref(false)
const station = ref(null)
const piles = ref([])
const favoriteState = reactive({ favorited: false, favoriteId: null })

const statusMetrics = computed(() => [
  { key: 'freeCount', label: '空闲', tone: 'free' },
  { key: 'usingCount', label: '使用中', tone: 'using' },
  { key: 'reservedCount', label: '预约', tone: 'reserved' },
  { key: 'offlineCount', label: '离线', tone: 'offline' },
  { key: 'faultCount', label: '故障', tone: 'fault' }
])

const loadData = async () => {
  loading.value = true
  try {
    const [stationData, pileData] = await Promise.all([
      getStationDetail(props.id),
      getStationPiles(props.id)
    ])
    station.value = stationData
    piles.value = pileData || []
    if (userStore.user?.id) {
      Object.assign(favoriteState, await checkFavorite({ userId: userStore.user.id, stationId: props.id }))
    }
  } catch (error) {
    ElMessage.error(error.message || '站点详情加载失败')
  } finally {
    loading.value = false
  }
}

const toggleFavorite = async () => {
  if (!userStore.user?.id) {
    ElMessage.info('请先登录后收藏站点')
    router.push('/login')
    return
  }
  favoriteLoading.value = true
  try {
    if (favoriteState.favorited) {
      await removeFavorite(favoriteState.favoriteId)
      favoriteState.favorited = false
      favoriteState.favoriteId = null
      ElMessage.success('已取消收藏')
    } else {
      Object.assign(favoriteState, await addFavorite({ userId: userStore.user.id, stationId: Number(props.id) }))
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error(error.message || '收藏操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const stationStatusText = value => ({ 0: '已停用', 1: '运营中', 2: '维护中' }[value] || '未知')
const stationTypeText = value => ({ 0: '公共充电站', 1: '专用充电站', 2: '高速充电站' }[value] || '其他站点')
const pileStatusText = value => ({ 0: '空闲', 1: '使用中', 2: '预约', 3: '离线', 4: '故障' }[value] || '未知')
const pileStatusType = value => ({ 0: 'success', 1: 'warning', 2: 'primary', 3: 'info', 4: 'danger' }[value] || 'info')
const formatTime = value => value ? String(value).replace('T', ' ') : '暂无'

onMounted(loadData)
</script>
