<!--
 * @Author: kaix
 * @Date: 2023-08-08 11:11:06
 * @LastEditTime: 2024-01-05 09:38:17
 * @LastEditors: zouying
 * @Description: 配色系列的全局配置
-->
<template>
  <div class="wrap">
    <CustomSwitch
      label="使用全局配色"
      v-model:value="newAttr.themeColor.useGlobalColor"
      label-color="#fff"
      el-margin-bottom="10px"
    />
    <n-form label-placement="left" v-if="!newAttr.themeColor.useGlobalColor && !optionData.useSeriesColor">
      <n-form-item label="配色方案">
        <ColorSelect :newAttr="newAttr" :optionData="optionData" />
      </n-form-item>
    </n-form>
    <CustomSwitch
      v-if="!newAttr.themeColor.useGlobalColor && !optionData.commonSeries"
      label="开启系列配色"
      v-model:value="optionData.useSeriesColor"
      label-color="#fff"
      el-margin-bottom="10px"
    />
    <template v-if="optionData.useSeriesColor && !newAttr.themeColor.useGlobalColor && !optionData.commonSeries">
      <SeriesColors
        v-for="(item, index) in count"
        :key="index"
        v-model:color="optionData.color[index]"
        :series-index="index + 1"
        v-bind="$attrs"
        :count="count"
        :optionData="optionData"
        @updateCount="changeSeriesColor"
      />
    </template>
  </div>
  <slot />
</template>

<script setup lang="ts">
import { PropType, watch, computed, ref, onMounted } from 'vue'
import { GlobalThemeJsonType } from '@/package/index.d'
import { CustomSwitch } from '@/components/Form'
import ColorSelect from '@/components/Pages/ChartItemSetting/components/ColorSelect.vue'
import { SeriesColors } from '@/components/Pages/ChartItemSetting/components/index'
const series = computed(() => {
  return props.optionData.series
})
const count = ref(6)
const props = defineProps({
  optionData: {
    type: Object as PropType<any>,
    required: true
  },
  inChart: {
    type: Boolean,
    required: false,
    default: false
  },
  newAttr: {
    type: Object,
    default: () => {}
  }
})
const changeSeriesColor = (val, index, type) => {
  count.value = val
  if (type == 'add') {
    props.optionData.color.splice(index, 0, '#ffff')
  } else {
    props.optionData.color.splice(index - 1, 1)
  }
  getSeriesColor()
}
const getSeriesColor = () => {
  props.optionData.series.map((item, index) => {
    if (index + 1 > count.value) {
      props.optionData.color[index] = props.optionData.color[count.value - 1]
    }
  })
}
watch(
  () => props.optionData.useSeriesColor,
  val => {
    //开启系列配色
    if (val) {
      keepColors()
    }
  }
)

const keepColors = () => {
  const colors = props.optionData.color
  // 如果数组长度为 0，使用默认红色填充
  if ((colors && colors.length === 0) || !colors) {
    props.optionData.color = Array(6).fill('#FF0000') // 默认红色
  }

  // 如果数组长度小于 6，使用第一项填充到 6
  if (colors.length < 6) {
    const temp = 6 - colors.length
    for (let i = 0; i < temp; i++) {
      props.optionData.color.push(colors[colors.length - 1])
    }
  }
  if (props.optionData.series.length > 6) {
    const sortIndex = props.optionData.series.length - 6
    for (let i = 0; i < sortIndex; i++) {
      props.optionData.color.push(colors[5])
    }
    console.log('长度大于6', props.optionData.color)
  }
  //适配pieCommon组件
  if (props.optionData.series[0].type == 'pie' && props.optionData.series[0].data.length > 6) {
    const sortIndex = props.optionData.series[0].data.length - 6
    for (let i = 0; i < sortIndex; i++) {
      props.optionData.color.push(colors[5])
    }
  }
  // //适配雷达图
  // if (props.optionData.series[0].type == 'radar') {
  //   const sortIndex = props.optionData.series[0].data.length - 6
  //   for (let i = 0; i < sortIndex; i++) {
  //     props.optionData.color.push(colors[5])
  //   }
  // }
}
onMounted(() => {
  if (props.optionData.useSeriesColor) {
    keepColors()
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  .subtitle {
    height: 17px;
    font-size: 12px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
    // color: #ffffff;
    color: var(--n-text-color);
    line-height: 17px;
    margin-bottom: 10px;
    &.control {
      display: flex;
      justify-content: space-between;
      .btns {
        display: flex;
        gap: 10px;
        i {
          font-size: 16px;
          cursor: pointer;
        }
      }
    }
  }
}
</style>
