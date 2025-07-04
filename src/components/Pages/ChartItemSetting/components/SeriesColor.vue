<!--
 * @Author: wangcong
 * @Date: 2023-08-16 16:58:19
 * @LastEditTime: 2025-01-15 17:14:36
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
      <NewColorPicker label-placement="left" label="背景颜色" v-model:value="_color" v-bind="$attrs" />
    </template>
    <template v-else>
      <NewColorPicker v-bind="$attrs" label-placement="left" label="开始颜色" v-model:value="startColor" />
      <NewColorPicker label-placement="left" label="结束颜色" v-model:value="endColor" v-bind="$attrs" />
    </template>
  </div>
</template>

<script lang="ts" setup>
import { NewColorPicker } from '@/components/Form'
import { computed, ref, watch, PropType } from 'vue'

const props = defineProps({
  color: {
    type: String as PropType<any>,
    default: '#fff'
  },
  seriesIndex: {
    type: Number,
    default: 0
  },
  showTitle: {
    type: Boolean,
    default: true
  },
  chartType: {
    type: String,
    default: 'echarts'
  }
})

const emits = defineEmits(['update:color'])

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
        if (props.chartType === 'highcharts') {
          startColor.value = props.color.stops[0][1]
          endColor.value = props.color.stops[1][1]
          return
        }
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
    if (props.chartType === 'echarts') {
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
    } else {
      // highcharts 渐变色
      const color: any = {
        linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1 },
        stops: [
          [0, color1], // start
          // [0.5, '#ffffff'], // middle
          [1, color2] // end
        ]
      }
      if (isGradient.value) {
        emits('update:color', color)
      }
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
</script>

<style lang="scss" scoped>
$primary-color: var(--primary-color);
.seriesColor {
  .title {
    font-size: 12px;
    // color: #fff;
    display: flex;
    align-items: center;
    margin-bottom: 5px;
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
        :deep(.n-switch__rail) {
          height: 18px;
          min-width: 38px;
          .n-switch__button {
            width: 15px;
            height: 15px;
            background: #808792;
          }
        }
        &.n-switch--active {
          :deep(.n-switch__rail) {
            // background: #212b40;
            border: 1px solid $primary-color;
            .n-switch__button {
              top: 1.5px;
              left: 22px;
              background: var(--n-button-color);
            }
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
