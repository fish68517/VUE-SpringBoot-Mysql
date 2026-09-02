<template>
  <div class="bz-green-fire" :class="{ 'bz-green-fire--no-bg': !showBackground }" :style="containerStyle">
    <div class="park-card-grid">
      <!-- 第一行：鹅岭公园、虎头岩公园 -->
      <div class="park-row park-row--top">
        <button
          v-for="park in topParks"
          :key="park.name"
          type="button"
          class="park-card park-card--large"
          :class="{
            'park-card--warn': park.warningCount > 0,
            'park-card--safe': park.warningCount <= 0
          }"
        >
          <div class="park-card__title card-title card-title--large" @click="openParkDetail(park)">{{ park.name }}</div>
          <div class="park-card__body card-level card-level--large" :class="{ yellow: park.warningCount > 0, cyan: park.warningCount <= 0 }" @click="openGreenFireDia(park)">
            <span>预警：</span>
            <strong>{{ park.warningCount }}</strong>
            <em>/{{ park.warningTotal }}</em>
          </div>
        </button>
      </div>
      <!-- 第二行：其余六个公园 -->
      <div class="park-row park-row--bottom">
        <button
          v-for="park in bottomParks"
          :key="park.name"
          type="button"
          class="park-card park-card--small park-card--blue"
        >
          <div class="park-card__title card-title card-title--small" @click="openParkDetail(park)">{{ park.name }}</div>
          <div class="park-card__body card-level card-level--small" :class="{ yellow: park.warningCount > 0, cyan: park.warningCount <= 0 }" @click="openGreenFireDia(park)">
            <div class="warn-label">预警</div>
            <div class="warn-value"><strong>{{ park.warningCount }}</strong><em>/{{ park.warningTotal }}</em></div>
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  publicParamList: {
    type: Object,
    required: false,
    default: () => []
  },
  bus: {
    type: Object,
    required: false
  }
})

const sourceName = 'BZGreenFire'
const OPEN_GREEN_FIRE_DIA_EVENT = 'OPEN_GREEN_FIRE_DIA'

const parks = [
  { name: '鹅岭公园', imageKey: 'eling', deviceCode: '50010300106001000165', area: '6.5', manager: '陈俊', phone: '13452847983', address: '鹅岭正街176号', warningCount: 0, warningTotal: 0, warningLevel: '高风险' },
  { name: '虎头岩公园', imageKey: 'hutouyan', deviceCode: '50010300106001000135', area: '14.82', manager: '刘东', phone: '13752953417', address: '虎头岩', warningCount: 0, warningTotal: 0, warningLevel: '高风险' },
  { name: '佛图关公园', imageKey: 'fotuguan', deviceCode: '50010300106001000152', area: '41.84', manager: '封逆', phone: '13908340170', address: '长江一路', warningCount: 0, warningTotal: 0, warningLevel: '高风险' },
  { name: '人民公园', imageKey: 'renmin', area: '1.02', manager: '刘素芳', phone: '13508389696', address: '人民路', warningCount: 0, warningTotal: 0, warningLevel: '高风险' },
  { name: '红岩公园', imageKey: 'hongyangongyuan', deviceCode: '50010300106001000184', area: '18.21', manager: '肖镇', phone: '13708367331', address: '红岩村', warningCount: 0, warningTotal: 0, warningLevel: '高风险' },
  { name: '天地湖公园', imageKey: 'tiandihu', deviceCode: '50010300056001023000', area: '6.12', manager: '肖镇', phone: '13708367331', address: '天地湖', warningCount: 0, warningTotal: 0, warningLevel: '高风险' },
  { name: '珊瑚公园', imageKey: 'shanhugongyuan', deviceCode: '50010300056004000918', area: '10', manager: '梁雪岗', phone: '13808352604', address: '珊瑚路', warningCount: 0, warningTotal: 0, warningLevel: '高风险' },
  { name: '滨江公园', imageKey: 'binjiang', deviceCode: '50010300056001025502', area: '6.28', manager: '梁雪岗', phone: '13808352604', address: '滨江路', warningCount: 0, warningTotal: 0, warningLevel: '高风险' }
]

const topParks = computed(() => parks.slice(0, 2))
const bottomParks = computed(() => parks.slice(2))

const eventBus = {
  emit(event: string, data?: any) {
    props.bus?.emit(event, { data, source: sourceName })
  }
}

function openParkDetail(park) {
  // 原打开弹窗事件暂时注释
  // eventBus.emit(OPEN_GREEN_FIRE_DIA_EVENT, { park })

  // 改为触发摄像头组件事件总线，传递基础信息 + 摄像头编码
  if (!park.deviceCode) {
    console.warn(`${park.name} 缺少 deviceCode，无法查看摄像头`)
    return
  }
  const payload = {
    '设备编码': park.deviceCode,
    '所属区县': '渝中区',
    '感知类型': '绿地防火',
    '设备名称': park.name,
    '所属单位': park.manager || '-',
    '设备风险点': park.address || '-',
    '设备状态': '启用',
    deviceIds: [park.deviceCode]
  }
  eventBus.emit('YZVideoPop:openPop', payload)
}

function openGreenFireDia(park) {
  eventBus.emit(OPEN_GREEN_FIRE_DIA_EVENT, { park })
}

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '540px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '245px'
  }
})

const showBackground = computed(() => props.chartConfig?.option?.showBackground !== 'hide')


</script>

<script lang="ts">
export default {
  name: 'BZGreenFire',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'BZGreenFirePuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

.bz-green-fire {
  box-sizing: border-box;
  padding: 8px 12px;
  overflow: hidden;
  color: #d9f7ff;
  background: #041f3c;
  font-family: 'BZGreenFirePuHuiTi', Microsoft YaHei, Arial, sans-serif;
}

.bz-green-fire--no-bg {
  background: transparent;
}

.park-card-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  height: 100%;
}

.park-row {
  display: flex;
  gap: 8px;
  flex: 1;
  min-height: 0;
}

.park-row--top {
  flex: 0 0 45%;
}

.park-row--bottom {
  flex: 1;
}

.park-card {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 0;
  flex: 1;
  height: 100%;
  padding: 2px 4px;
  border: 0;
  border-radius: 4px;
  background: url('./img/warn-card-bg-cyan.png') no-repeat center / 100% 100%;
  cursor: pointer;
  outline: none;
  transition: transform 0.18s ease, filter 0.18s ease;
}

.park-card--small {
  padding: 12px 4px;
}

.park-card--large {
  padding: 5px 4px 2px;
}

.park-card--warn {
  background-image: url('./img/warn-card-bg-red.png');
  border: 2px solid #F84444;
  box-shadow: 0 0 16px rgba(248, 68, 68, 0.6);
}

.park-card--safe {
  border: 2px solid #00C853;
  box-shadow: 0 0 16px rgba(0, 200, 83, 0.5);
}

.park-card--blue {
  border: 2px solid #00BFFF;
  box-shadow: 0 0 16px rgba(0, 191, 255, 0.6);
}

.park-card:hover {
  transform: translateY(-1px);
  filter: brightness(1.08);
}

.park-card__title {
  width: 100%;
  text-align: center;
  word-break: break-all;
  line-height: 1.2;
}

.card-title {
  font-family: 'BZGreenFirePuHuiTi', Microsoft YaHei, sans-serif;
  font-size: 16px;
  font-style: normal;
  font-weight: 800;
  background: linear-gradient(0deg, #5fbcff 1.4%, #fff 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.card-title--large {
  font-size: 18px;
  line-height: 1.3;
}

.card-title--small {
  font-size: 13px;
  line-height: 1.2;
}

.park-card__body {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  width: 100%;
  white-space: nowrap;
}

.card-level {
  color: #F84444;
  font-feature-settings: 'liga' off, 'clig' off;
  font-family: 'BZGreenFirePuHuiTi', Microsoft YaHei, sans-serif;
  font-size: 18px;
  font-style: normal;
  font-weight: 800;
  line-height: normal;
  letter-spacing: 0.48px;
}

.card-level.yellow {
  background: linear-gradient(180deg, #F84444 0%, #F84444 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.card-level.cyan {
  color: #5FEAFF;
}

.card-level--large {
  font-size: 20px;
}

.card-level--small {
  font-size: 14px;
  flex-direction: column;
  gap: 2px;
  margin-top: 16px;
}

.warn-value {
  display: flex;
  align-items: center;
  gap: 1px;
}

.park-card__body strong,
.park-card__body em {
  font-style: normal;
  font-weight: 800;
}
</style>
