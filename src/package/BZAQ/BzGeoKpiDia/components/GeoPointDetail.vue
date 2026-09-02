<template>
  <div
    class="point-modal"
    :class="{
      'point-modal--trend': trendVisible,
      'point-modal--hazard-detail': !isDeviceAlarm && !trendVisible,
      'point-modal--device-detail': isDeviceAlarm && !trendVisible
    }"
  >
    <header class="modal-header">
      <button
        v-if="trendVisible"
        type="button"
        class="back-btn"
        aria-label="返回"
        @click="trendVisible = false"
      >
        ‹ 返回
      </button>
      <span>{{ trendVisible ? '预警趋势' : title }}</span>
      <button type="button" class="close-btn" aria-label="关闭" @click="$emit('close')">×</button>
    </header>

    <section
      v-if="!trendVisible"
      class="info-card"
      :class="{
        'info-card--clickable': canQueryTrend,
        'info-card--hazard': !isDeviceAlarm,
        'info-card--device-alarm': isDeviceAlarm
      }"
      @click="openTrend"
    >
      <dl>
        <div v-for="item in detailRows" :key="item.label">
          <dt>{{ item.label }}：</dt>
          <dd :title="item.value">{{ item.value }}</dd>
        </div>
      </dl>
      <span v-if="canQueryTrend" class="detail-arrow">›</span>
    </section>

    <section v-else class="trend-content">
      <v-chart class="trend-chart" :option="trendChartOption" :loading="curveLoading" autoresize />
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import axios from 'axios'

const props = withDefaults(defineProps<{
  detail?: Record<string, any>
  showTrend?: boolean
  pointType?: string
}>(), {
  detail: () => ({}),
  showTrend: false,
  pointType: 'hazard'
})
defineEmits(['close'])

const trendVisible = ref(false)
const curveLoading = ref(false)
const curveData = ref<Record<string, any>[]>([])
const resolvedDetail = ref<Record<string, any>>({ ...props.detail })
let detailRequestVersion = 0
let curveRequestVersion = 0
const value = (...keys: string[]) => keys.map(key => resolvedDetail.value?.[key]).find(item => item !== undefined && item !== null && item !== '')
const field = (...keys: string[]) => String(value(...keys) ?? '-')
const isGeoWarnAlarm = computed(() => props.pointType === 'geoWarnAlarm')
const isRiskArea = computed(() => props.pointType === 'riskArea')
const isDeviceAlarm = computed(() => props.pointType === 'alarm' || isGeoWarnAlarm.value)
const isCrackMonitor = computed(() => String(
  value('devTypeName', 'deviceTypeName', 'deviceType') || ''
).includes('裂缝'))
const isDisplacementMonitor = computed(() => String(
  value('devTypeName', 'deviceTypeName', 'deviceType') || ''
).includes('位移'))
const isDeformationMonitor = computed(() => String(
  value('devTypeName', 'deviceTypeName', 'deviceType') || ''
).includes('形变'))
const isRainfallMonitor = computed(() => String(
  value('devTypeName', 'deviceTypeName', 'deviceType') || ''
).includes('雨量'))
// 是否允许进入趋势页只由点位类型和外部开关决定，不额外校验设备类型文本。
const canQueryTrend = computed(() => props.showTrend && isDeviceAlarm.value)
const title = computed(() => isDeviceAlarm.value
  ? field('devName', 'deviceName', 'name', 'monitorName', 'pointName', 'disasterName', 'deviceId')
  : isRiskArea.value
  ? field('name', 'DLWZ', 'street', 'riskZonin')
  : field('disasterName', 'hazardName', 'pointName', 'name'))

/** 将是否销号代码转换为可读文本。 */
const formatIsCancelled = () => {
  const isXh = value('isXh')
  if (String(isXh) === '0') return '否'
  if (String(isXh) === '1') return '是'
  return String(isXh ?? '-')
}

// 隐患点基础信息接口返回字段全部展示，并按两列布局。
const hazardRows = computed(() => [
  { label: '隐患点编码', value: field('unifiedCode', 'unifiedcode') },
  { label: '风险等级代码', value: field('riskLevel') },
  { label: '风险等级描述', value: field('riskLevelDesc') },
  { label: '灾害类型', value: field('type') },
  { label: '规模等级', value: field('scaleLevel') },
  { label: '隐患点名称', value: field('name', 'disasterName') },
  { label: '地理位置', value: field('location') },
  { label: '是否销号', value: formatIsCancelled() },
  { label: '当前稳定状态', value: field('curStableStatus') },
  { label: '险情等级', value: field('dangerLevel') },
  { label: '威胁对象', value: field('threatenObj') },
  { label: '威胁人口', value: field('threatenPeople') },
  { label: '威胁资产（万元）', value: field('threatenedAssets') },
  { label: '面积（平方米）', value: field('area') },
  { label: '体积（立方米）', value: field('volume') }
])

/** 将库区查询标识转换为可读文本。 */
const formatIsKq = () => {
  const isKq = value('isKq')
  if (String(isKq) === '0') return '否'
  if (String(isKq) === '1') return '是'
  return String(isKq ?? '-')
}

/** 将设备告警预警等级编码转换为可读文本。 */
const formatWarnLevel = () => {
  const warnLevel = value('warnLevel')
  const warnLevelMap: Record<string, string> = {
    '1': '蓝色预警',
    '2': '黄色预警',
    '3': '橙色预警',
    '4': '红色预警'
  }
  return warnLevelMap[String(warnLevel ?? '')] || String(warnLevel ?? '-')
}

/** 将卫星网络标识转换为可读文本。 */
const formatSatelliteNetwork = () => {
  const satelliteNetwork = resolvedDetail.value?.issatellitenetwork
  if (Number(satelliteNetwork) === 0) return '否'
  if (Number(satelliteNetwork) === 1) return '是'
  return String(satelliteNetwork ?? '-')
}

const geoWarnAlarmRows = computed(() => [
  { label: '时间', value: field('time') },
  { label: '隐患点编码', value: field('unifiedcode', 'unifiedCode') },
  { label: '隐患点名称', value: field('disasterName') },
  { label: '项目名称', value: field('projectName') },
  { label: '设备类型', value: field('deviceType', 'devTypeName') },
  { label: '设备编号', value: field('deviceId', 'devId') },
  { label: '区县编码', value: field('city', 'cityCode') },
  { label: '乡镇编码', value: field('town', 'townCode') },
  { label: '是否为库区查询', value: formatIsKq() },
  { label: '预警等级', value: formatWarnLevel() },
  { label: '告警描述', value: field('msg') }
])

const deviceAlarmRows = computed(() => [
  { label: '设备等级', value: field('level') },
  { label: '设备编号', value: field('devId', 'deviceId') },
  { label: '隐患点名称', value: field('disasterName') },
  { label: '乡镇编码', value: field('townCode', 'town') },
  { label: '设备类型', value: field('devType') },
  { label: '设备类型名称', value: field('devTypeName', 'deviceType') },
  { label: '设备名称', value: field('devName') },
  { label: '隐患点编码', value: field('unifiedcode', 'unifiedCode') },
  { label: '区县编码', value: field('cityCode', 'city') },
  { label: '关联隐患点编码', value: field('disasterCode') },
  { label: '是否卫星网络', value: formatSatelliteNetwork() }
])

const riskAreaRows = computed(() => [
  { label: '名称', value: field('name', 'DLWZ') },
  { label: '街道', value: field('street') },
  { label: '规模等级', value: field('scaleLeve', 'scaleLevel') },
  { label: '稳定性', value: field('stability') },
  { label: '户数', value: field('households') },
  { label: '人数', value: field('peoples', 'people') },
  { label: '安置点Geojson', value: field('placementGeojson') },
  { label: '风险分区', value: field('riskZonin', 'riskZoning') },
  { label: '街道电话', value: field('streetPho', 'streetPhone') },
  { label: '村电话', value: field('villagePh', 'villagePhone') },
  { label: '街道负责人', value: field('streetUse', 'streetUser') },
  { label: '村负责人', value: field('villageUs', 'villageUser') },
  { label: '安置点', value: field('placement') }
])

/** 根据点位来源选择对应详情字段，避免设备告警和监测设备字段互相覆盖。 */
const detailRows = computed(() => {
  if (isRiskArea.value) return riskAreaRows.value
  if (isGeoWarnAlarm.value) return geoWarnAlarmRows.value
  return isDeviceAlarm.value ? deviceAlarmRows.value : hazardRows.value
})

/**
 * 根据隐患点编码请求基础详情。
 * 请求失败时保留地图落点携带的原始摘要数据，避免弹窗完全空白。
 */
const requestHazardBasicInfo = async () => {
  const requestVersion = ++detailRequestVersion
  curveRequestVersion += 1
  curveData.value = []
  curveLoading.value = false
  trendVisible.value = false
  resolvedDetail.value = { ...props.detail }
  if (isDeviceAlarm.value || isRiskArea.value) return

  const unifiedCode = props.detail?.unifiedCode || props.detail?.unifiedcode
  if (!unifiedCode) return

  try {
    const res = await axios.post(
      'http://23.99.16.179:11001/api/boot/system/land/landHazardBasicInfo',
      { unifiedCode }
    )
    if (requestVersion !== detailRequestVersion) return
    const data = res?.data?.data ?? res?.data
    if (data && typeof data === 'object') {
      resolvedDetail.value = { ...props.detail, ...data }
    }
  } catch (error) {
    console.error('获取地灾隐患点基础详情失败:', error)
  }
}

watch(
  () => [props.pointType, props.detail?.unifiedCode, props.detail?.unifiedcode, props.detail],
  requestHazardBasicInfo,
  { immediate: true }
)

const curveFieldOptions = [
  { key: 'rainfallIntensityNum', label: '雨量强度(mm)' },
  { key: 'yl', label: '压力值(KN)' },
  { key: 'nowXNum', label: '当前X轴角度' },
  { key: 'nowYNum', label: '当前Y轴角度' },
  { key: 'nowZNum', label: '当前Z轴角度' },
  { key: 'nowPullLineNum', label: '当前拉线值(mm)' },
  { key: 'speedXNum', label: 'X轴速度' },
  { key: 'speedYNum', label: 'Y轴速度' },
  { key: 'speedZNum', label: 'Z轴速度' },
  { key: 'shiftXNum', label: 'X轴位移' },
  { key: 'shiftYNum', label: 'Y轴位移' },
  { key: 'shiftZNum', label: 'Z轴位移' },
  { key: 'angleXNum', label: 'X轴角度' },
  { key: 'angleYNum', label: 'Y轴角度' },
  { key: 'angleZNum', label: 'Z轴角度' },
  { key: 'displacementNum', label: '合位移量' },
  { key: 'xdisplacementNum', label: 'X方向合位移量' },
  { key: 'ydisplacementNum', label: 'Y方向合位移量' }
]

const currentDayHours = Array.from(
  { length: 24 },
  (_, hour) => `${String(hour).padStart(2, '0')}:00`
)

/** 将曲线接口值转换为有效数字，空字符串和空值不按 0 处理。 */
const getCurveNumber = (rawValue: any): number | null => {
  if (rawValue === null || rawValue === undefined || rawValue === '') return null
  const number = Number(rawValue)
  return Number.isFinite(number) ? number : null
}

const curveTimes = computed(() => {
  if (isCrackMonitor.value) return currentDayHours
  return curveData.value.map((item, index) => (
    item.date || item.recordTime || item.sendTime || item.warnTime || `${index + 1}`
  ))
})

// 仅为接口实际返回过有效数值的指标生成折线，避免出现大量空曲线。
const curveSeries = computed(() => {
  if (isCrackMonitor.value) {
    const hourlyData: Array<number | null> = Array(24).fill(null)
    curveData.value.forEach((item, index) => {
      const rawValue = item?.now_pull_line_num ?? item?.nowPullLineNum
      const number = getCurveNumber(rawValue)
      if (number === null) return

      const time = item?.date || item?.recordTime || item?.sendTime || item?.warnTime
      const date = time ? new Date(time) : null
      const hour = date && !Number.isNaN(date.getTime()) ? date.getHours() : index % 24
      hourlyData[hour] = number
    })
    return [{
      name: '拉线位移(毫米)',
      type: 'line',
      smooth: false,
      connectNulls: true,
      showSymbol: false,
      data: hourlyData,
      lineStyle: { width: 2, color: '#3aa5ff' },
      itemStyle: { color: '#3aa5ff' },
      emphasis: { focus: 'series', scale: true }
    }]
  }

  if (isDisplacementMonitor.value) {
    const displacementFields = [
      {
        key: 'displacement_num',
        name: '合位移量(毫米)',
        getValue: (item: Record<string, any>) => {
          const x = getCurveNumber(item.shift_x_num ?? item.shiftXNum)
          const y = getCurveNumber(item.shift_y_num ?? item.shiftYNum)
          const z = getCurveNumber(item.shift_z_num ?? item.shiftZNum)
          return x !== null && y !== null && z !== null
            ? Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))
            : null
        }
      },
      {
        key: 'xdisplacement_num',
        name: 'X方向合位移量(毫米)',
        getValue: (item: Record<string, any>) => {
          const x = getCurveNumber(item.shift_x_num ?? item.shiftXNum)
          const y = getCurveNumber(item.shift_y_num ?? item.shiftYNum)
          return x !== null && y !== null
            ? Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))
            : null
        }
      },
      {
        key: 'ydisplacement_num',
        name: 'Y方向合位移量(毫米)',
        getValue: (item: Record<string, any>) => {
          const y = getCurveNumber(item.shift_y_num ?? item.shiftYNum)
          const z = getCurveNumber(item.shift_z_num ?? item.shiftZNum)
          return y !== null && z !== null
            ? Math.sqrt(Math.pow(y, 2) + Math.pow(z, 2))
            : null
        }
      },
      {
        key: 'shift_x_num',
        name: 'X方向位移量(毫米)',
        getValue: (item: Record<string, any>) => {
          return getCurveNumber(item.shift_x_num ?? item.shiftXNum)
        }
      },
      {
        key: 'shift_y_num',
        name: 'Y方向位移量(毫米)',
        getValue: (item: Record<string, any>) => {
          return getCurveNumber(item.shift_y_num ?? item.shiftYNum)
        }
      },
      {
        key: 'shift_z_num',
        name: 'Z方向位移量(毫米)',
        getValue: (item: Record<string, any>) => {
          return getCurveNumber(item.shift_z_num ?? item.shiftZNum)
        }
      }
    ]
    const colors = ['#a99cff', '#4cafff', '#5cd6c0', '#ffb85c', '#ff7d95', '#7d91ff']
    return displacementFields.map((option, index) => ({
      name: option.name,
      type: 'line',
      smooth: true,
      connectNulls: true,
      showSymbol: false,
      data: curveData.value.map(option.getValue),
      lineStyle: { width: 2, color: colors[index] },
      itemStyle: { color: colors[index] },
      emphasis: { focus: 'series', scale: true }
    }))
  }

  if (isDeformationMonitor.value) {
    return [{
      name: '应力值(KN)',
      type: 'line',
      smooth: true,
      connectNulls: true,
      showSymbol: false,
      data: curveData.value.map(item => {
        return getCurveNumber(item?.yl)
      }),
      lineStyle: { width: 2, color: '#3aa5ff' },
      itemStyle: { color: '#3aa5ff' },
      emphasis: { focus: 'series', scale: true }
    }]
  }

  if (isRainfallMonitor.value) {
    return [{
      name: '雨量强度(mm)',
      type: 'bar',
      barMaxWidth: 18,
      data: curveData.value.map(item => {
        return getCurveNumber(item?.rainfall_intensity_num ?? item?.rainfallIntensityNum)
      }),
      itemStyle: {
        color: '#369dff',
        borderRadius: [2, 2, 0, 0]
      },
      emphasis: { focus: 'series' }
    }]
  }

  return curveFieldOptions.filter(option => curveData.value.some(item => (
    item?.[option.key] !== null
    && item?.[option.key] !== undefined
    && getCurveNumber(item[option.key]) !== null
  ))).map((option, index) => ({
    name: option.label,
    type: 'line',
    smooth: true,
    connectNulls: true,
    showSymbol: false,
    symbol: 'circle',
    symbolSize: 7,
    data: curveData.value.map(item => {
      return getCurveNumber(item?.[option.key])
    }),
    lineStyle: {
      width: 3,
      color: ['#aaa0ff', '#43c8ff', '#52d89c', '#ffbf69', '#ff7895'][index % 5]
    },
    emphasis: { focus: 'series', scale: true }
  }))
})

const trendChartOption = computed(() => ({
  animationDuration: 700,
  grid: { left: 62, right: 24, top: 52, bottom: 42 },
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, .96)',
    borderColor: '#d7e2ee',
    textStyle: { color: '#5c6773', fontSize: 12 },
    axisPointer: { type: 'line', lineStyle: { color: '#aec9de' } }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: curveTimes.value,
    axisLine: { lineStyle: { color: '#bdd6e9' } },
    axisTick: { show: false },
    axisLabel: { color: '#7eb5df', fontSize: 11, hideOverlap: true },
    splitLine: { show: true, lineStyle: { color: '#e2edf5' } }
  },
  yAxis: {
    type: 'value',
    scale: true,
    name: isCrackMonitor.value
      ? '拉线位移(毫米)'
      : isDisplacementMonitor.value
      ? '位移量(毫米)'
      : isDeformationMonitor.value
      ? '应力值(KN)'
      : isRainfallMonitor.value
      ? '雨量强度(mm)'
      : '',
    nameTextStyle: { color: '#3aa5ff', fontSize: 11 },
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: { color: '#72afe0', fontSize: 11 },
    splitLine: { lineStyle: { color: '#dceaf4' } }
  },
  legend: {
    top: 2,
    textStyle: { color: '#54708a', fontSize: 11 }
  },
  series: curveSeries.value
}))

/** 将日期格式化为曲线接口需要的日期时间字符串。 */
const formatCurveDateTime = (date: Date) => {
  const pad = (value: number) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

/**
 * 查询设备预警趋势。
 * 所有设备统一查询今天 00:00:00 至 23:59:59 的监测数据。
 */
const requestDeviceCurve = async () => {
  const deviceId = value('devId', 'deviceId', 'deviceCode')
  if (!deviceId) {
    curveData.value = []
    return
  }

  const requestVersion = ++curveRequestVersion
  const now = new Date()
  const startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0)
  const endDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59)
  curveLoading.value = true
  try {
    const res = await axios.post(
      'http://23.99.16.179:11001/api/boot/system/land/landDeviceCurve',
      {
        deviceId,
        startTime: formatCurveDateTime(startDate),
        endTime: formatCurveDateTime(endDate)
      }
    )
    if (requestVersion !== curveRequestVersion) return
    const data = res?.data?.data ?? res?.data
    curveData.value = Array.isArray(data) ? data : []
  } catch (error) {
    if (requestVersion === curveRequestVersion) curveData.value = []
    console.error('获取设备预警趋势失败:', error)
  } finally {
    if (requestVersion === curveRequestVersion) curveLoading.value = false
  }
}

/** 打开趋势页，并根据设备类型加载对应时间范围和指标的监测曲线。 */
function openTrend() {
  if (!canQueryTrend.value) return
  trendVisible.value = true
  requestDeviceCurve()
}
</script>

<style scoped lang="scss">
.point-modal {
  position: absolute; transform: translate(-50%, -50%); z-index: 1001;
  width: 1000px; height: 500px; box-sizing: border-box; overflow: hidden;
  color: #d8ebff; font-family: "Microsoft YaHei", sans-serif;
  background: url('../img/Rectangle_346242153.png') no-repeat center/cover;
  background-size: 100% 100%;
  transition: width .2s ease, height .2s ease;
}
.point-modal--trend { width: 745px; height: 526px; }
.point-modal--hazard-detail { width: 1000px; height: 500px; }
.point-modal--device-detail { width: 1000px; height: 500px; }
.modal-header {
  position: relative; height: 54px; box-sizing: border-box; display: flex; align-items: center;
  padding: 0 52px 0 24px; font-size: 22px; font-weight: 700; color: #b9dfff;
  background: linear-gradient(90deg, rgba(18, 105, 192, .48), transparent);
  border-bottom: 1px solid rgba(45, 139, 224, .35);
}
.close-btn, .back-btn { border: 0; color: #b9dfff; background: transparent; cursor: pointer; }
.close-btn { position: absolute; right: 14px; top: 9px; font-size: 30px; line-height: 30px; }
.back-btn { margin-right: 14px; padding: 4px 8px; font-size: 15px; border: 1px solid rgba(91, 166, 230, .5); }
.info-card {
  position: relative; height: 249px; margin: 24px; box-sizing: border-box; padding: 13px 22px;
  background: linear-gradient(100deg, rgba(15, 82, 150, .67), rgba(5, 48, 94, .7));
  border: 1px solid rgba(42, 125, 204, .33);
  overflow: auto;
}
.info-card--clickable { padding-right: 42px; cursor: pointer; }
.info-card--hazard { height: 398px; }
.info-card--device-alarm { height: 398px; padding-right: 42px; }
.info-card--hazard dl,
.info-card--device-alarm dl {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 36px;
  row-gap: 5px;
}
dl { margin: 0; font-size: 15px; line-height: 1.82; }
dl div { display: flex; align-items: flex-start; min-width: 0; }
dt { flex: none; color: #80acd2; line-height: 1.82; }
dd {
  min-width: 0;
  margin: 0;
  color: #e2f1ff;
  font-weight: 600;
  line-height: 1.82;
  white-space: normal;
  overflow: visible;
  text-overflow: clip;
  overflow-wrap: anywhere;
  word-break: break-all;
}
.detail-arrow { position: absolute; right: 8px; top: 50%; transform: translateY(-50%); color: #d69722; font-size: 54px; text-shadow: 0 0 10px #e59d1e; }
.trend-content { height: 396px; margin: 20px 24px; background: #fff; overflow: hidden; }
.trend-chart { width: 100%; height: 100%; background: linear-gradient(#fff, #fbfcff); }
</style>
