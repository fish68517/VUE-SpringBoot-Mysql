<!--
 * @Author: kaix
 * @Date: 2023-03-12 21:58:55
 * @LastEditTime: 2025-07-03 18:23:48
 * @LastEditors: kaix
 * @Description: 
-->
<template>
  <div class="go-decorates-number" :style="`width:${w}px;height:${h}px;`" @click="clickHandle">
    <div
      :style="`color:${timeColor};line-height:${maxHeight}px;
      letter-spacing:${timeTextIndent}px;font-weight:${fontWeight};
      text-shadow: ${boxShadow};flex-direction: ${timeStyle === 'one' ? 'row' : 'column'};`"
    >
      <div
        :style="{
          marginLeft: `${lrSpacing}px`,
          lineHeight:`${maxHeight + 1}px`,
          fontFamily: yearMonthDayOption.fontFamily,
          fontSize: `${yearMonthDayOption.fontSize}px`,
          fontWeight: yearMonthDayOption.fontWeight,
          color: yearMonthDayOption.textColor
        }"
      >{{ newYear }}</div>
      <div :style="`flex-direction: ${timeFormatter === 'weekTime' ? 'row' : 'row-reverse'}; justify-content: ${timeFormatter === 'weekTime' ? 'flex-start' : 'flex-end'}`">
        <div
          :style="{
            marginLeft: `${lrSpacing}px`,
            fontFamily: weekOption.fontFamily,
            fontSize: `${weekOption.fontSize}px`,
            lineHeight:`${maxHeight}px`,
            fontWeight: weekOption.fontWeight,color: weekOption.textColor
          }"
        > {{ newWeek }}</div>
        <div
          :style="{
            marginLeft: `${lrSpacing}px`,
            lineHeight:`${maxHeight + 1}px`,
            fontFamily: dateOption.fontFamily,
            fontSize: `${dateOption.fontSize}px`,
            fontWeight: dateOption.fontWeight,
            color: dateOption.textColor
          }"
        > {{ newDate }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {PropType, toRefs, ref, reactive, watch, onMounted, onUnmounted, computed} from 'vue'
import { CreateComponentType } from '../../../index.d'
import dayjs from 'dayjs'

const emits = defineEmits(['finishedFn'])

// GlobalParams的类型定义
interface GlobalParams {
  params: any,
  setParam: (param: string, value: any) => void
  getParam: (param: string) => void
  removeParam: (param: string) => void
}

interface EventBus {
  on: (eventName: string, callback: (data: any) => void) => void
  emit: (eventName: string, data: any) => void
  off: (eventName: string) => void
}

let theme = true
const clickHandle = (it: any) => {
  console.log('clickHandle')
  theme = !theme
  props.globalParams?.setParam('theme', theme ? 'dark' : 'light')
  props.bus?.emit('update:theme', {
    theme: 'light'
  })
  setTimeout(() => {
    if (props.globalParams && props.globalParams.params) {
      props.globalParams.params.theme = 'xxxx'
    }
  }, 3000)
}

const props = defineProps({
  chartConfig: {
    type: Object as PropType<CreateComponentType>,
    required: true
  },
  // 数据更新
  useChartDataFetch: {
    type: Function,
    default: () => {}
  },
  globalParams: {
    type: Object as () => GlobalParams,
    required: false
  },
  bus: {
    type: Object as () => EventBus,
    required: false
  }
})

let yearMonthDay = ref('')
let nowData = ref('')
let newData = ref('')
let newYear = ref('')
let newWeek = ref('')
let newDate = ref('')
let boxShadow = ref('')
let week = ref('')

let timer = -1

const { w, h } = toRefs(props.chartConfig.attr)

const weekList = ref(['星期日', '星期一' ,'星期二', '星期三', '星期四', '星期五', '星期六'])

let {
  timeColor,
  timeSize,
  timeLineHeight,
  timeTextIndent,
  fontWeight,
  lrSpacing,
  timeStyle,
  showShadow,
  hShadow,
  vShadow,
  blurShadow,
  timeFormatter,
  colorShadow,
  yearMonthDayOption,
  weekOption,
  dateOption
} = toRefs(props.chartConfig.option)

// 兼容时间展示是否显示秒钟
if (!Object.prototype.hasOwnProperty.call(props.chartConfig.option.dateOption, "showSeconds")) {
  props.chartConfig.option.dateOption.showSeconds = true
}

const maxHeight = computed(() => {
    return Math.max(props.chartConfig.option.yearMonthDayOption.fontSize, props.chartConfig.option.weekOption.fontSize, props.chartConfig.option.dateOption.fontSize)
});

watch(
  props.chartConfig.option,
  () => {
    try {
      if (props.chartConfig.option.showShadow) {
        boxShadow.value = `${props.chartConfig.option.hShadow}px ${props.chartConfig.option.vShadow}px ${props.chartConfig.option.blurShadow}px ${props.chartConfig.option.colorShadow}`
      } else {
        boxShadow.value = 'none'
      }
    } catch (error) {
      console.log(error)
    }
  },
  {
    immediate: true
  }
)
onMounted(() => {
  timer = setInterval(() => {
    var datetime = new Date()
    var year = datetime.getFullYear()
    var month = datetime.getMonth() + 1 < 10 ? '0' + (datetime.getMonth() + 1) : datetime.getMonth() + 1
    var date = datetime.getDate() < 10 ? '0' + datetime.getDate() : datetime.getDate()
    var hh = datetime.getHours() // 时
    var mm = datetime.getMinutes() // 分
    var ss = datetime.getSeconds() // 分
    let time = ''
    if (hh < 10) time += '0'
    time += hh + ':'
    if (mm < 10) time += '0'
    // 是否展示秒钟
    if (dateOption.value.showSeconds) {
      time += mm + ':'
      if (ss < 10) time += '0'
      time += ss
    } else {
      time += mm
    }
    yearMonthDay.value = props.chartConfig.option.isYearMonthDayText ? `${year}-${month}-${date}` : ''
    week.value = props.chartConfig.option.isWeekText ? weekList.value[datetime.getDay()] : ''
    nowData.value = props.chartConfig.option.isDateText ? time : ''
    newData.value = props.chartConfig.option.timeFormatter === 'weekTime' ? yearMonthDay.value + ' ' + week.value + ' ' + nowData.value : yearMonthDay.value + ' ' + nowData.value + ' ' + week.value
    let customDate = handleDateFormatter(props.chartConfig.option.inputDate, 'date')
    let customTime = handleDateFormatter(props.chartConfig.option.inputTime, 'type')
    let customWeek = props.chartConfig.option.inputDate ? weekList.value[new Date(props.chartConfig.option.inputDate).getDay()]  : ''
    newYear.value = customDate || yearMonthDay.value
    newDate.value = customTime || nowData.value
    newWeek.value = customWeek || week.value
    const handleYear = (format) => {
      return customDate || yearMonthDay.value ? dayjs(customDate || yearMonthDay.value).format(format) : ''
    }
    // 26种有点长
    switch (props.chartConfig.option.dateType) {
      case "1":
        newYear.value = handleYear('YYYY年MM月DD日')
        newWeek.value = customWeek || week.value
        newDate.value = customTime || nowData.value
        break
      case "2":
        newYear.value = handleYear('YYYY年M月D日')
        newWeek.value = customWeek || week.value
        newDate.value = customTime || nowData.value
        break
      case "3":
        newYear.value = handleYear('YYYY-MM-DD')
        newWeek.value = customWeek || week.value
        newDate.value = customTime || nowData.value
        break
      case "4":
        newYear.value = handleYear('YYYY-M-D')
        newWeek.value = customWeek || week.value
        newDate.value = customTime || nowData.value
        break
      case "5":
        newYear.value = handleYear('YYYY.M.D')
        newWeek.value = customWeek || week.value
        newDate.value = customTime || nowData.value
        break
      case "6":
        newYear.value = handleYear('YYYY.MM.DD')
        newWeek.value = customWeek || week.value
        newDate.value = customTime || nowData.value
        break
      case "7":
        newYear.value = handleYear('YYYY年M月D日')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "8":
        newYear.value = handleYear('YYYY-M-D')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "9":
        newYear.value = handleYear('YYYY-MM-DD')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "10":
        newYear.value = handleYear('YYYY/MM/DD')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "11":
        newYear.value = handleYear('M月D日')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "12":
        newYear.value = handleYear('MM月DD日')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "13":
        newYear.value = handleYear('MM月DD日')
        newWeek.value = customWeek || week.value
        newDate.value = props.chartConfig.option.isDateText ? dayjs(datetime).format('HH时') : ''
      break
      case "14":
        newYear.value = handleYear('M-D')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "15":
        newYear.value = handleYear('MM-DD')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "16":
        newYear.value = handleYear('M.D')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "17":
        newYear.value = handleYear('M月')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "18":
        newYear.value = handleYear('MM月')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "19":
        newYear.value = handleYear('M(月)')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "20":
        newYear.value = handleYear('MM(月)')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "21":
        newYear.value = handleYear('D日')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "22":
        newYear.value = handleYear('DD日')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "23":
        newYear.value = handleYear('D(日)')
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      case "24":
        newYear.value = ''
        newWeek.value = customWeek || week.value
        newDate.value = ''
        break
      default:
        break
    }  
  }, 500)

  emits('finishedFn')
})
onUnmounted(() => {
  clearInterval(timer)
})

const handleDateFormatter = (datetime, type) => {
  if(!datetime){
    return
  }
  if(type === 'date'){
    let year = new Date(datetime).getFullYear()
    let month = new Date(datetime).getMonth() + 1 < 10 ? '0' + (new Date(datetime).getMonth() + 1) : new Date(datetime).getMonth() + 1
    let date = new Date(datetime).getDate() < 10 ? '0' + new Date(datetime).getDate() : new Date(datetime).getDate()
    return `${year}-${month}-${date}`
  } else {
    let hh = new Date(datetime).getHours() // 时
    let mm = new Date(datetime).getMinutes() // 分
    let ss = new Date(datetime).getSeconds() // 分
    let time = ''
    if (hh < 10) time += '0'
    time += hh + ':'
    if (mm < 10) time += '0'
    if (dateOption.value.showSeconds) {
      time += mm + ':'
      if (ss < 10) time += '0'
      time += ss
    } else {
      time += mm
    }
    return time
  }
}

props.useChartDataFetch()
// useChartDataFetch(props.chartConfig, useChartEditStore)
</script>

<script lang="ts">
export default {
  name: 'TimeCommon',
  version: '2.3.2'
}
</script>

<style lang="scss" scoped>
.go-decorates-number {
  text-align: center;
  div:first-child{
    display: flex;
  }
  div:nth-child(2){
    display: flex;
  }
}
</style>
