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
      <div class="icons" @click="addSeries">
        <n-icon class="add">
          <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 12 12">
            <g fill="none">
              <path
                d="M6.5 1.75a.75.75 0 0 0-1.5 0V5H1.75a.75.75 0 0 0 0 1.5H5v3.25a.75.75 0 0 0 1.5 0V6.5h3.25a.75.75 0 0 0 0-1.5H6.5V1.75z"
                fill="currentColor"
              ></path>
            </g>
          </svg>
        </n-icon>
        <n-icon @click="deleteSeries" v-if="count != 1">
          <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 32 32">
            <path d="M12 12h2v12h-2z" fill="currentColor"></path>
            <path d="M18 12h2v12h-2z" fill="currentColor"></path>
            <path d="M4 6v2h2v20a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V8h2V6zm4 22V8h16v20z" fill="currentColor"></path>
            <path d="M12 2h8v2h-8z" fill="currentColor"></path>
          </svg>
        </n-icon>
        <!-- <span>+</span>
        <span>-</span> -->
      </div>
    </div>
    <CustomSwitch label="启用渐变色" v-model:value="isGradient" label-color="#fff" el-margin-bottom="10px" />
    <!-- <div class="switch">
      <span class="label">启用渐变色</span>
      <n-switch v-model:value="isGradient" />
    </div> -->
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
import { computed, ref, watch, PropType, onMounted } from 'vue'
import { CustomSwitch } from '@/components/Form'

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
  },
  count: {
    type: Number
  },
  optionData: {
    type: Object as PropType<any>,
    required: true
  }
})

const emits = defineEmits(['update:color', 'updateCount'])

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
    console.log(val, '系列颜色变化了')
    if (typeof val === 'string') {
      startColor.value = props.color
      isGradient.value = false
    } else {
      isGradient.value = true
    }
    if (props.seriesIndex == props.count) {
      console.log('配置最后一项')
      if (props.optionData.color.length > props.seriesIndex) {
        const sortIndex = props.optionData.color.length - props.seriesIndex
        for (let i = 0; i < sortIndex; i++) {
          props.optionData.color[props.optionData.color.length - 1 - i] = val
        }
      }
    }
  },
  {
    immediate: true,
    deep: true
  }
)

const addSeries = e => {
  console.log(props.seriesIndex, 'add')
  e.stopPropagation()
  const value = props.count + 1
  emits('updateCount', value, props.seriesIndex,'add')
}
const deleteSeries = e => {
  e.stopPropagation()
  console.log(props.seriesIndex, 'delete')
  if (props.seriesIndex == 1) {
    return
  }
  const values = props.count - 1
  emits('updateCount', values, props.seriesIndex,'delete')
}
onMounted(() => {
  console.log(props.optionData, props.color, '传过来的color')
})
</script>

<style lang="scss" scoped>
$primary-color: var(--primary-color);
.seriesColor {
  .title {
    font-size: 12px;
    // color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 5px;
    .icons {
      cursor: pointer;
      .add {
        margin-right: 20px;
      }
    }

    .switch {
      display: flex;
      justify-content: space-around;
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
