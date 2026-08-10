<template>
  <div class="guide-page">
    <div class="detail-breadcrumb">
      <router-link to="/stations">充电站地图</router-link><span>/</span>
      <router-link v-if="station" :to="`/stations/${station.id}`">{{ station.stationName }}</router-link><span>/</span><span>站内导览</span>
    </div>

    <section class="page-heading-panel compact-heading">
      <div>
        <p class="eyebrow">VISUAL GUIDE</p>
        <h1>{{ station?.stationName || '站内可视化导览' }}</h1>
        <p>二维平面示意图按照数据库中的比例坐标显示入口、充电区和服务设施。</p>
      </div>
      <router-link v-if="station" :to="`/stations/${station.id}`"><el-button>返回站点详情</el-button></router-link>
    </section>

    <section v-loading="loading" class="guide-workspace">
      <div class="site-plan" aria-label="站内二维导览图">
        <div class="site-plan-title"><strong>站内平面示意图</strong><span>非真实比例 · 仅用于导览展示</span></div>
        <div class="plan-canvas">
          <div class="plan-road road-main"><span>车辆通道</span></div>
          <div class="plan-road road-branch"></div>
          <div class="plan-zone parking-zone"><strong>P</strong><span>停车区域</span></div>
          <div class="plan-zone fast-zone"><strong>DC</strong><span>快充区</span></div>
          <div class="plan-zone slow-zone"><strong>AC</strong><span>慢充区</span></div>
          <div class="plan-zone service-zone"><strong>休</strong><span>服务设施</span></div>
          <svg class="route-line" viewBox="0 0 1000 600" preserveAspectRatio="none" aria-hidden="true">
            <polyline points="90,510 270,510 270,310 520,310 760,165" fill="none" stroke="#10a674" stroke-width="8" stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="14 12" />
          </svg>
          <button
            v-for="(point, index) in guidePoints"
            :key="point.id"
            type="button"
            :class="['guide-point-marker', `point-type-${point.pointType}`, { active: activePoint?.id === point.id }]"
            :style="pointStyle(point)"
            :title="point.description"
            @click="activePoint = point"
          >
            <span>{{ index + 1 }}</span>
            <small>{{ point.pointName }}</small>
          </button>
        </div>
      </div>

      <aside class="guide-steps-panel">
        <div>
          <p class="eyebrow">ROUTE STEPS</p>
          <h2>推荐导览步骤</h2>
          <p>点击左侧标记可查看对应导览说明。</p>
        </div>
        <el-empty v-if="!loading && guidePoints.length === 0" description="该站点暂未配置导览点" :image-size="90" />
        <ol v-else class="guide-step-list">
          <li
            v-for="(point, index) in guidePoints"
            :key="point.id"
            :class="{ active: activePoint?.id === point.id }"
            @click="activePoint = point"
          >
            <span>{{ String(index + 1).padStart(2, '0') }}</span>
            <div>
              <small>{{ pointTypeText(point.pointType) }}</small>
              <strong>{{ point.pointName }}</strong>
              <p>{{ point.description || '暂无详细说明' }}</p>
            </div>
          </li>
        </ol>
        <div v-if="activePoint" class="active-guide-note">
          <span>当前导览点</span>
          <strong>{{ activePoint.pointName }}</strong>
          <p>{{ activePoint.description }}</p>
        </div>
      </aside>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getGuidePoints, getStationDetail } from '../../api/station'

const props = defineProps({ id: { type: String, required: true } })
const loading = ref(true)
const station = ref(null)
const guidePoints = ref([])
const activePoint = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const [stationData, points] = await Promise.all([
      getStationDetail(props.id),
      getGuidePoints(props.id)
    ])
    station.value = stationData
    guidePoints.value = points || []
    activePoint.value = guidePoints.value[0] || null
  } catch (error) {
    ElMessage.error(error.message || '站内导览加载失败')
  } finally {
    loading.value = false
  }
}

const pointStyle = point => ({
  left: `${Number(point.xRatio || 0.5) * 100}%`,
  top: `${Number(point.yRatio || 0.5) * 100}%`
})

const pointTypeText = value => ({ 0: '入口', 1: '停车区域', 2: '充电区域', 3: '服务设施' }[value] || '导览点')

onMounted(loadData)
</script>
