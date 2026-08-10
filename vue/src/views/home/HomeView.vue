<template>
  <div class="home-page">
    <section class="hero-panel">
      <div>
        <p class="eyebrow">EV CHARGING GUIDE</p>
        <h1>快速了解城市充电设施</h1>
        <p>集中查看区域、充电站、充电桩和当前使用状态，为后续地图导览与统计分析提供统一入口。</p>
        <div class="hero-actions">
          <router-link to="/stations"><el-button type="primary" size="large">查看站点地图</el-button></router-link>
          <router-link to="/statistics"><el-button size="large">浏览区域统计</el-button></router-link>
        </div>
      </div>
      <div class="hero-visual" aria-label="充电状态示意">
        <div class="charge-ring"><span>{{ overview.freeCount || 0 }}</span><small>当前空闲</small></div>
      </div>
    </section>

    <section class="section-block">
      <div class="section-heading">
        <div>
          <p class="eyebrow">PLATFORM OVERVIEW</p>
          <h2>平台数据概览</h2>
        </div>
        <el-button :loading="loading" @click="loadData">刷新数据</el-button>
      </div>

      <div v-loading="loading" class="metric-grid">
        <article v-for="item in metrics" :key="item.key" class="metric-card">
          <span :class="['metric-dot', item.tone]"></span>
          <div>
            <strong>{{ overview[item.key] ?? 0 }}</strong>
            <span>{{ item.label }}</span>
          </div>
        </article>
      </div>
    </section>

    <section class="section-block">
      <div class="section-heading">
        <div>
          <p class="eyebrow">RECOMMENDED STATIONS</p>
          <h2>空闲充足的推荐站点</h2>
        </div>
        <router-link to="/stations" class="section-link">查看全部站点 →</router-link>
      </div>
      <div v-loading="loading" class="recommendation-grid">
        <router-link
          v-for="station in recommendations"
          :key="station.id"
          :to="`/stations/${station.id}`"
          class="recommendation-card"
        >
          <div class="station-card-head">
            <span>{{ station.regionName }}</span>
            <strong>{{ station.freeCount }}<small> 空闲</small></strong>
          </div>
          <h3>{{ station.stationName }}</h3>
          <p>{{ station.address }}</p>
          <div class="station-card-meta">
            <span>共 {{ station.pileCount }} 桩</span>
            <span>{{ station.openTime || '营业时间待确认' }}</span>
          </div>
        </router-link>
      </div>
    </section>

    <section class="content-grid">
      <article class="info-card">
        <div class="card-title-row">
          <div>
            <p class="eyebrow">RECENT NOTICES</p>
            <h2>最新公告</h2>
          </div>
          <span>{{ notices.length }} 条</span>
        </div>
        <el-skeleton v-if="loading" :rows="4" animated />
        <el-empty v-else-if="notices.length === 0" description="暂无公告" />
        <ul v-else class="notice-list">
          <li v-for="notice in notices" :key="notice.id">
            <span>{{ noticeTypeText(notice.noticeType) }}</span>
            <div>
              <router-link :to="`/notices/${notice.id}`"><strong>{{ notice.title }}</strong></router-link>
              <small>{{ notice.publishTime }}</small>
            </div>
          </li>
        </ul>
      </article>

      <article class="info-card stage-card">
          <p class="eyebrow">PHASE TWO</p>
        <h2>第二阶段功能入口</h2>
        <ol class="stage-list">
          <li><span>01</span>高德地图站点定位与筛选</li>
          <li><span>02</span>区域使用率和趋势图表</li>
          <li><span>03</span>站点详情与站内导览</li>
          <li><span>04</span>收藏、反馈与个人中心</li>
        </ol>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getHomeOverview, getPublishedNotices, getRecommendedStations } from '../../api/home'

const loading = ref(false)
const overview = reactive({})
const notices = ref([])
const recommendations = ref([])

const metrics = computed(() => [
  { key: 'regionCount', label: '区域数量', tone: 'blue' },
  { key: 'stationCount', label: '充电站数量', tone: 'green' },
  { key: 'pileCount', label: '充电桩总数', tone: 'purple' },
  { key: 'freeCount', label: '空闲充电桩', tone: 'green' },
  { key: 'usingCount', label: '使用中', tone: 'orange' },
  { key: 'faultCount', label: '故障数量', tone: 'red' }
])

const loadData = async () => {
  loading.value = true
  try {
    const [overviewData, noticeData, stationData] = await Promise.all([
      getHomeOverview(),
      getPublishedNotices({ pageNum: 1, pageSize: 4 }),
      getRecommendedStations(6)
    ])
    Object.assign(overview, overviewData)
    notices.value = noticeData.records || []
    recommendations.value = stationData || []
  } catch (error) {
    ElMessage.error(error.message || '首页数据加载失败')
  } finally {
    loading.value = false
  }
}

const noticeTypeText = type => ({ 0: '通知', 1: '维护', 2: '提示' }[type] || '公告')

onMounted(loadData)
</script>
