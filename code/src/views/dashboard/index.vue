<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ArrowRight, Bell, Briefcase, CircleCheck, Connection, MoreFilled, User } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader/index.vue'
import StatusTag from '@/components/StatusTag/index.vue'
import { getDashboardApi } from '@/api/dashboard'
import type { DashboardData } from '@/types/dashboard'

const router = useRouter()
const loading = ref(true)
const chartRef = ref<HTMLElement>()
let chart: echarts.ECharts | undefined
const data = ref<DashboardData>({ cards: [], trend: [], events: [], todos: [] })
const cardIcons = { users: User, events: Briefcase, todos: CircleCheck, messages: Bell }

async function load() {
  loading.value = true
  try {
    data.value = (await getDashboardApi()).data
    await nextTick()
    renderChart()
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  chart.setOption({
    color: ['#d85f2b', '#3e77b6'],
    tooltip: { trigger: 'axis', backgroundColor: '#2b303b', borderWidth: 0, textStyle: { color: '#fff', fontSize: 12 } },
    legend: { right: 4, top: 0, data: ['新增事件', '办结事件'], textStyle: { color: '#7e8794', fontSize: 11 }, itemWidth: 12, itemHeight: 7 },
    grid: { top: 48, left: 42, right: 18, bottom: 28 },
    xAxis: { type: 'category', boundaryGap: false, data: data.value.trend.map((item) => item.date), axisLine: { lineStyle: { color: '#e8ebf0' } }, axisTick: { show: false }, axisLabel: { color: '#939ba8' } },
    yAxis: { type: 'value', axisLine: { show: false }, axisTick: { show: false }, splitLine: { lineStyle: { color: '#eff1f4', type: 'dashed' } }, axisLabel: { color: '#939ba8' } },
    series: [
      { name: '新增事件', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, data: data.value.trend.map((item) => item.created), lineStyle: { width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(216,95,43,.20)' }, { offset: 1, color: 'rgba(216,95,43,0)' }]) } },
      { name: '办结事件', type: 'line', smooth: true, symbol: 'circle', symbolSize: 5, data: data.value.trend.map((item) => item.completed), lineStyle: { width: 2 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(62,119,182,.13)' }, { offset: 1, color: 'rgba(62,119,182,0)' }]) } },
    ],
  })
}

function resize() { chart?.resize() }
onMounted(() => { load(); window.addEventListener('resize', resize) })
onBeforeUnmount(() => { window.removeEventListener('resize', resize); chart?.dispose() })
</script>

<template>
  <section v-loading="loading" class="dashboard-page">
    <PageHeader title="工作台" description="汇总平台关键数据、待办事项和系统运行状态。">
      <el-button :icon="Connection" @click="router.push('/architecture')">查看架构</el-button>
      <el-button type="primary" @click="router.push('/event/create')">发起事件</el-button>
    </PageHeader>

    <div class="welcome-banner">
      <div class="welcome-copy">
        <span>GOOD MORNING</span>
        <h2>上午好，管理员</h2>
        <p>今天是值得高效推进的一天，您有 <b>6</b> 项待办任务需要处理。</p>
      </div>
      <div class="banner-seal"><div>法</div><span>规范 · 协同 · 安全 · 高效</span></div>
      <div class="banner-line" />
    </div>

    <div class="stat-grid">
      <article v-for="card in data.cards" :key="card.key" class="stat-card page-card" :class="card.tone">
        <div class="stat-icon"><el-icon><component :is="cardIcons[card.key as keyof typeof cardIcons]" /></el-icon></div>
        <div class="stat-copy">
          <span>{{ card.label }}</span>
          <strong>{{ card.value.toLocaleString() }}<small>{{ card.suffix }}</small></strong>
          <p><b :class="{ down: card.trend < 0 }">{{ card.trend >= 0 ? '↑' : '↓' }} {{ Math.abs(card.trend) }}%</b> 较上周</p>
        </div>
        <el-icon class="stat-more"><MoreFilled /></el-icon>
      </article>
    </div>

    <div class="dashboard-grid">
      <article class="chart-card page-card">
        <div class="card-heading"><div><h3>事件趋势</h3><p>近七日事件新增与办结趋势</p></div><el-radio-group size="small" model-value="week"><el-radio-button value="week">本周</el-radio-button><el-radio-button value="month">本月</el-radio-button></el-radio-group></div>
        <div ref="chartRef" class="trend-chart" />
      </article>
      <article class="todo-card page-card">
        <div class="card-heading"><div><h3>我的待办</h3><p>优先处理临近截止事项</p></div><el-button link type="primary" @click="router.push('/event/todo')">全部待办 <el-icon><ArrowRight /></el-icon></el-button></div>
        <div class="todo-list">
          <div v-for="todo in data.todos" :key="todo.id" class="todo-item" @click="router.push('/event/todo')">
            <span class="priority-dot" :class="todo.priority" />
            <div><strong>{{ todo.title }}</strong><p>{{ todo.source }} · 截止 {{ todo.deadline }}</p></div>
            <el-tag size="small" effect="plain" :type="todo.priority === '紧急' ? 'danger' : todo.priority === '高' ? 'warning' : 'info'">{{ todo.priority }}</el-tag>
          </div>
        </div>
      </article>
    </div>

    <article class="event-card page-card">
      <div class="card-heading"><div><h3>最新事件</h3><p>平台最近发起的业务事件</p></div><el-button link type="primary" @click="router.push('/event/list')">查看全部 <el-icon><ArrowRight /></el-icon></el-button></div>
      <el-table :data="data.events">
        <el-table-column prop="eventNo" label="事件编号" min-width="150" />
        <el-table-column prop="title" label="事件标题" min-width="260" />
        <el-table-column prop="category" label="事件类型" min-width="150" />
        <el-table-column label="优先级" width="100"><template #default="{ row }"><StatusTag :status="row.priority" /></template></el-table-column>
        <el-table-column label="状态" width="110"><template #default="{ row }"><StatusTag :status="row.status" /></template></el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="165" />
        <el-table-column label="操作" width="90" align="center"><template #default="{ row }"><el-button link type="primary" @click="router.push(`/event/detail/${row.id}`)">查看</el-button></template></el-table-column>
      </el-table>
    </article>
  </section>
</template>

<style scoped lang="scss">
.dashboard-page { min-height: 100%; }

.welcome-banner {
  position: relative;
  min-height: 145px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 24px 34px;
  border-radius: 8px;
  overflow: hidden;
  background: linear-gradient(112deg, #343a46 0%, #262b35 72%, #3b2a27 100%);
  color: #fff;
}

.welcome-copy { position: relative; z-index: 2; }
.welcome-copy > span { color: #df855e; font-size: 9px; letter-spacing: 2.4px; }
.welcome-copy h2 { margin: 8px 0 8px; font-size: 24px; font-weight: 650; }
.welcome-copy p { margin: 0; color: #adb4c0; font-size: 13px; }
.welcome-copy b { color: #ef8554; }

.banner-seal { position: relative; z-index: 2; display: flex; align-items: center; gap: 16px; margin-right: 45px; color: #8d949e; font-size: 11px; letter-spacing: 2px; }
.banner-seal div { width: 64px; height: 64px; display: grid; place-items: center; border: 2px solid rgba(224, 105, 58, .5); border-radius: 50%; color: #e27649; font-family: KaiTi, serif; font-size: 32px; }
.banner-line { position: absolute; right: 0; top: -80px; width: 390px; height: 280px; border: 1px solid rgba(232,119,72,.15); border-radius: 50%; }

.stat-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; margin-bottom: 14px; }
.stat-card { position: relative; min-width: 0; display: flex; align-items: center; gap: 15px; padding: 19px; }
.stat-icon { width: 48px; height: 48px; flex: 0 0 48px; display: grid; place-items: center; border-radius: 10px; font-size: 22px; }
.stat-card.orange .stat-icon { background: #fff0e8; color: #d85f2b; }
.stat-card.blue .stat-icon { background: #eaf2fb; color: #3976b8; }
.stat-card.green .stat-icon { background: #e9f7f1; color: #37a77d; }
.stat-card.purple .stat-icon { background: #f0edfb; color: #7462bc; }
.stat-copy { min-width: 0; display: flex; flex-direction: column; }
.stat-copy > span { color: #8a92a0; font-size: 12px; }
.stat-copy strong { margin: 4px 0 3px; color: #2d3340; font-size: 25px; font-weight: 650; }
.stat-copy small { margin-left: 3px; font-size: 11px; font-weight: 400; }
.stat-copy p { margin: 0; color: #a0a6b1; font-size: 10px; }
.stat-copy p b { margin-right: 3px; color: #35a279; font-weight: 600; }
.stat-copy p b.down { color: #df5a60; }
.stat-more { position: absolute; top: 15px; right: 14px; color: #c4c8cf; }

.dashboard-grid { display: grid; grid-template-columns: minmax(0, 1.65fr) minmax(330px, .8fr); gap: 14px; margin-bottom: 14px; }
.chart-card, .todo-card, .event-card { padding: 18px 20px; }
.card-heading { display: flex; align-items: center; justify-content: space-between; }
.card-heading h3 { margin: 0; color: #303644; font-size: 15px; }
.card-heading p { margin: 5px 0 0; color: #9aa1ad; font-size: 10px; }
.trend-chart { width: 100%; height: 275px; margin-top: 5px; }

.todo-list { margin-top: 12px; }
.todo-item { display: grid; grid-template-columns: 9px minmax(0, 1fr) auto; align-items: center; gap: 10px; padding: 15px 1px; border-bottom: 1px solid #edf0f3; cursor: pointer; }
.todo-item:hover strong { color: var(--brand); }
.todo-item strong { color: #3b414d; font-size: 12px; font-weight: 600; }
.todo-item p { margin: 4px 0 0; color: #9ca3ae; font-size: 10px; }
.priority-dot { width: 6px; height: 6px; border-radius: 50%; background: #98a1af; }
.priority-dot.紧急 { background: #e45b62; box-shadow: 0 0 0 4px #fdecee; }
.priority-dot.高 { background: #e4a23e; box-shadow: 0 0 0 4px #fff7e9; }

@media (max-width: 1300px) {
  .stat-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .banner-seal { margin-right: 0; }
}
</style>
