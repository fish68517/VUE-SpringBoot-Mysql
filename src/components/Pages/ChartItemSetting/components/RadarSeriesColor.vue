<!--
 * @Author: wangcong
 * @Date: 2023-08-16 16:58:19
 * @LastEditTime: 2024-08-26 09:18:11
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <div v-if="color" class="seriesColor">
    <div class="title">
      <span v-if="showTitle">系列{{ seriesIndex }}</span>
      <div class="switch">
        <span class="label">启用渐变色</span>
        <n-switch v-model:value="isGradient" />
      </div>
    </div>
    <template v-if="!isGradient">
      <NewColorPicker label-placement="left" label="折线颜色" v-model:value="_color" v-bind="$attrs" />
    </template>
    <template v-else>
      <NewColorPicker v-bind="$attrs" label-placement="left" label="开始颜色" v-model:value="startColor" />
      <NewColorPicker label-placement="left" label="结束颜色" v-model:value="endColor" v-bind="$attrs" />
    </template>
    <CustomInputSelect label="折线样式" v-model:value="_lineStyle" :options="lineStyleOptions" />
    <CustomSwitch label="启用背景色" v-model:value="_useArea" />
    <template v-if="_useArea">
      <CustomSwitch label="背景渐变" v-model:value="areaIsGradient" />
      <template v-if="!areaIsGradient">
        <NewColorPicker v-bind="$attrs" label-placement="left" label="背景色" v-model:value="_bgColor" />
      </template>
      <template v-else>
        <NewColorPicker v-bind="$attrs" label-placement="left" label="开始颜色" v-model:value="areaStartColor" />
        <NewColorPicker label-placement="left" label="结束颜色" v-model:value="areaEndColor" v-bind="$attrs" />
      </template>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { NewColorPicker, CustomSwitch, CustomInputSelect } from '@/components/Form'
import { computed, ref, watch, PropType } from 'vue'

const props = defineProps({
  color: {
    type: String as PropType<any>,
    default: ''
  },
  seriesIndex: {
    type: Number,
    default: 0
  },
  showTitle: {
    type: Boolean,
    default: true
  },
  useArea: {
    type: Boolean as PropType<boolean>,
    default: false
  },
  bgColor: {
    type: String as PropType<any>,
    default: 'rgba(0,0,0,0)'
  },
  optionData: {
    type: Object,
    default: {}
  }
})
const lineStyle = ref('solid')
const lineStyleOptions = [
  { label: '实线', value: 'solid' },
  { label: '虚线', value: 'dashed' }
]

const emits = defineEmits(['update:color', 'update:useArea', 'update:bgColor', 'update:lineStyle'])

const _lineStyle = computed({
  get: () => {
    return props.optionData?.series?.[props.seriesIndex - 1]?.lineStyle?.type || 'solid'
  },
  set: val => {
    emits('update:lineStyle', val)
  }
})

watch(
  () => _lineStyle.value,
  newVal => {
    if (props.optionData?.series?.[props.seriesIndex - 1]) {
      const currentSeries = props.optionData.series[props.seriesIndex - 1]
      currentSeries.lineStyle = {
        ...currentSeries.lineStyle,
        type: newVal
      }
    }
  }
)

const _color = computed({
  get: () => {
    if (typeof props.color === 'string') {
      return props.color
    } else {
      return props.color.colorStops[0].color
    }
  },
  set: val => {
    emits('update:color', val)
  }
})
const isGradient = ref(false)
const startColor = ref('')
const endColor = ref('')
watch(
  () => isGradient.value,
  val => {
    if (val) {
      if (typeof props.color === 'string') {
        startColor.value = props.color
        endColor.value = props.color
      } else {
        startColor.value = props.color.colorStops[0].color
        endColor.value = props.color.colorStops[1].color
      }
    } else {
      endColor.value = ''
      emits('update:color', startColor.value)
    }
  }
)
watch(
  () => [startColor.value, endColor.value],
  ([color1, color2]) => {
    const color: any = {
      type: 'linear',
      x: 0, // 渐变起点
      y: 0, // 渐变起点
      x2: 0, // 渐变终点
      y2: 1, // 渐变终点
      colorStops: [
        {
          offset: 0,
          color: color1 // 0% 处的颜色
        },
        {
          offset: 1,
          color: color2 // 100% 处的颜色
        }
      ]
    }
    if (isGradient.value) {
      emits('update:color', color)
    }
  }
)

watch(
  () => props.color,
  val => {
    if (typeof val === 'string') {
      startColor.value = props.color
      isGradient.value = false
    } else {
      isGradient.value = true
    }
  },
  {
    immediate: true,
    deep: true
  }
)

// 面积相关
const _useArea = computed({
  get: () => props.useArea,
  set: val => {
    console.log(val, props.optionData, '1111')
    emits('update:useArea', val)
  }
})

const _bgColor = computed({
  get: () => {
    if (typeof props.bgColor === 'string') {
      return props.bgColor
    } else {
      return props.bgColor.colorStops[0].color
    }
  },
  set: val => {
    console.log(val, props.optionData, 2222)
    emits('update:bgColor', val)
  }
})

const areaIsGradient = ref(false)
const areaStartColor = ref('')
const areaEndColor = ref('')
watch(
  () => areaIsGradient.value,
  val => {
    if (val) {
      if (typeof props.bgColor === 'string') {
        areaStartColor.value = props.bgColor
        areaEndColor.value = props.bgColor
      } else {
        areaStartColor.value = props.bgColor.colorStops[0].color
        areaEndColor.value = props.bgColor.colorStops[1].color
      }
    } else {
      areaEndColor.value = ''
      emits('update:bgColor', areaStartColor.value)
    }
  }
)
watch(
  () => [areaStartColor.value, areaEndColor.value],
  ([color1, color2]) => {
    const color: any = {
      type: 'radial', // 径向渐变类型
      x: 0.5, // 中心点 X 位置（相对容器宽度，0.5=居中）
      y: 0.5, // 中心点 Y 位置
      r: 0.8, // 渐变半径（相对容器短边的比例）
      colorStops: [
        {
          offset: 0,
          color: color1 // 0% 处的颜色
        },
        {
          offset: 1,
          color: color2 // 100% 处的颜色
        }
      ]
    }
    if (areaIsGradient.value) {
      emits('update:bgColor', color)
    }
  }
)

watch(
  () => props.bgColor,
  val => {
    if (typeof val === 'string') {
      areaStartColor.value = props.bgColor
      areaIsGradient.value = false
    } else {
      areaIsGradient.value = true
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped>
$primary-color: var(--primary-color);
.seriesColor {
  margin-bottom: 40px;
  .title {
    font-size: 12px;
    // color: #fff;
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    .switch {
      display: flex;
      align-items: center;
      margin-left: auto;
      .label {
        font-size: 12px;
        margin-right: 5px;
      }
      .n-switch {
        max-width: 40px;
        height: 20px;
        &.n-switch--active {
          :deep(.n-switch__rail) {
            background: #212b40;
            border: 1px solid $primary-color;
            .n-switch__button {
              top: 1.5px;
              left: 22px;
              background: $primary-color;
            }
          }
        }
        :deep(.n-switch__rail) {
          height: 18px;
          min-width: 38px;
          .n-switch__button {
            width: 15px;
            height: 15px;
            background: #808792;
          }
        }
      }
      :deep(.n-switch__rail) {
        // background: #10151f;
        // border: 1px solid #222831;
      }
    }
  }
}
</style>
