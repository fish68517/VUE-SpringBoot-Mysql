<!--
 * @Author: kaix
 * @Date: 2023-08-08 11:11:06
 * @LastEditTime: 2025-03-27 14:48:39
 * @LastEditors: wangcong
 * @Description: 其他的全局配置-如tooltip、legend、基准标线
-->
<template>
  <tempalte v-if="other.includes('legend') && legend.legendType != 'define'">
    <collapse-item
      v-if="legend"
      name="图例设置"
      :expanded="legend.show"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="legend.show"></n-switch>
      </template>
      <div class="wrap">
        <CustomRadio label="图例形状" v-model:value="legend.icon" :options="selectlegendStyleOption" />
        <template v-if="legend.icon === 'circle'">
          <InputNumberwithLabel label="图例直径" :min="1" v-model:value="legend.itemWidth" />
        </template>
        <template v-if="legend.icon === 'rect'">
          <InputNumberwithLabel label="图例宽度" :min="1" v-model:value="legend.itemWidth" />
          <InputNumberwithLabel label="图例高度" :min="1" v-model:value="legend.itemHeight" />
        </template>
        <CustomInputSelect label="位置" v-model:value="newAttr.legendPostion" :options="selectlegendPositionOption" />
        <CustomInputSelect label="排列方式" v-model:value="legend.orient" :options="selectlegendOrientOption" />
        <CustomInputNumberWithSlider label="外间距" :max="500" v-model:value="newAttr.legendPadding" />
        <CustomInputNumberWithSlider
          v-if="optionData.series.length > 1 || optionData.series[0].type !== 'bar'"
          label="图例间距"
          v-model:value="legend.itemGap"
          :max="500"
        />

        <!-- 可能要注释的地方 405-423 -->
        <n-divider style="margin: 16px 0"></n-divider>

        <div class="subtitle">文本样式</div>
        <CustomInputSelect label="字体" v-model:value="legend.textStyle.fontFamily" :options="fontFamilyOption" />

        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="legend.textStyle.fontSize" :min="1" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>

        <CustomInputSelect
          label="样式"
          v-model:value="legend.textStyle.fontWeight"
          :options="fontWeightOption"
          :sign="true"
        />

        <NewColorPicker v-bind="$attrs" label-placement="left" label="颜色" v-model:value="legend.textStyle.color" />
        <slot name="legend" />
      </div>
    </collapse-item>

    <n-divider style="margin: 9px 0"></n-divider>
  </tempalte>
  <template v-if="other.includes('animation')">
    <collapse-item
      name="动画效果"
      :expanded="optionData.animation"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="optionData.animation"></n-switch>
      </template>
    </collapse-item>
    <collapse-item
      v-if="animation && optionData.animation"
      name="循环动画"
      :expanded="animation.animationLoop"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="animation.animationLoop"></n-switch>
      </template>
      <div class="wrap">
        <n-form label-placement="left">
          <n-form-item label="循环间隔">
            <CustomInputNumber
              v-model:value="animation.cycleInterval"
              :min="1"
              :prefix="false"
              :step="100"
              :suffix-val="'ms'"
              el-width="100%"
            />
          </n-form-item>
        </n-form>
        <n-form label-placement="left">
          <n-form-item label="动画时长">
            <CustomInputNumber
              v-model:value="animation.animationDuration"
              :min="1"
              :step="100"
              :prefix="false"
              :suffix-val="'ms'"
              el-width="100%"
            />
          </n-form-item>
        </n-form>
      </div>
    </collapse-item>
    <n-divider style="margin: 9px 0"></n-divider>
  </template>
  <template v-if="other.includes('tooltip')">
    <collapse-item v-if="tooltip" name="悬停Tooltip设置" :expanded="tooltip.show" :isControl="true">
      <template #header>
        <n-switch v-model:value="tooltip.show"></n-switch>
      </template>

      <div class="wrap">
        <CustomSwitch label="展示单位" v-model:value="tooltip.showUnit" elMarginBottom="10px" v-if="optionData.yAxis" />
        <CustomSwitch
          v-if="tooltip.showUnit && optionData.yAxis"
          label="根据轴系列显示单位"
          v-model:value="tooltip.isNearYAxis"
          elMarginBottom="10px"
        />
        <CustomInput
          v-if="tooltip.showUnit && !tooltip.isNearYAxis && optionData.yAxis"
          label="自定义单位"
          labelPlacement="left"
          v-model:value="tooltip.unit"
          placeholder="请输入单位"
        />
        <n-divider
          style="margin: 6px 0 9px"
          v-if="tooltip.showUnit && !tooltip.isNearYAxis && optionData.yAxis"
        ></n-divider>
        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="tooltip.textStyle.fontSize" :min="1" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>
        <NewColorPicker v-bind="$attrs" label-placement="left" label="背景色" v-model:value="tooltip.backgroundColor" />
        <NewColorPicker
          v-bind="$attrs"
          label-placement="left"
          label="字体颜色"
          v-model:value="tooltip.textStyle.color"
        />
        <NewColorPicker v-bind="$attrs" label-placement="left" label="边框颜色" v-model:value="tooltip.borderColor" />
        <CustomInputSelect
          v-if="optionData.yAxis"
          label="指示器类型"
          v-model:value="tooltip.axisPointer.type"
          :options="selectlegendTypeOption"
        />
        <NewColorPicker
          v-if="tooltip.axisPointer.type === 'line'"
          v-bind="$attrs"
          label-placement="left"
          label="指示器颜色"
          v-model:value="tooltip.axisPointer.lineStyle.color"
        />
        <n-divider style="margin: 6px 0 9px"></n-divider>
        <div v-if="!tooltip.type">
          <CustomSwitch label="Tooltip轮播" v-model:value="tooltip.isToolTipInterval" elMarginBottom="10px" />
          <InputNumberwithLabel
            label="轮播间隔"
            :min="100"
            :step="500"
            suffix="ms"
            v-model:value="tooltip.toolTipIntervalTime"
            v-if="tooltip.isToolTipInterval"
          />
        </div>

        <slot name="tooltip" />
      </div>
    </collapse-item>
    <n-divider style="margin: 6px 0 9px"></n-divider>
  </template>

  <!-- dataZoom 设置 -->
  <template v-if="dataZoom">
    <collapse-item name="dataZoom拖动条设置" :expanded="optionData.showDataZoom" :isControl="true">
      <template #header>
        <n-switch v-model:value="optionData.showDataZoom"></n-switch>
      </template>

      <div class="wrap">
        <CustomSwitch label="是否轮播" v-model:value="dataZoom[0].isOpenInterval" elMarginBottom="10px" />
        <InputNumberwithLabel
          label="轮播间隔"
          :min="100"
          :step="500"
          suffix="ms"
          v-model:value="dataZoom[0].intervalTime"
          v-if="dataZoom[0].isOpenInterval"
        />
        <InputNumberwithLabel
          label="轮播数量"
          :min="1"
          :step="1"
          suffix="个"
          v-model:value="dataZoom[0].scrollNum"
          v-if="dataZoom[0].isOpenInterval"
        />
        <CustomSwitch
          v-if="!dataZoom[0].isOpenInterval"
          label="是否锁定滚动条"
          v-model:value="dataZoom[0].zoomLock"
          elMarginBottom="10px"
        />
        <!-- <CustomSwitch v-if="!dataZoom[0].isOpenInterval" label="是否显示信息" v-model:value="dataZoom[0].showDetail" elMarginBottom="10px" /> -->
        <InputNumberwithLabel
          label="拖动条高度"
          :min="1"
          :step="1"
          suffix="px"
          v-if="!dataZoom[0].isOpenInterval"
          v-model:value="dataZoom[0].height"
        />
        <!-- <InputNumberwithLabel
          v-if="!dataZoom[0].isOpenInterval"
          label="起始数值"
          :min="0"
          :step="1"
          prefix="第"
          suffix="个"
          v-model:value="dataZoom[0].startValue"
        />
        <InputNumberwithLabel
          v-if="!dataZoom[0].isOpenInterval"
          label="结束数值"
          :min="1"
          :step="1"
          prefix="第"
          suffix="个"
          v-model:value="dataZoom[0].endValue"
        /> -->
        <InputNumberwithLabel
          v-if="!dataZoom[0].isOpenInterval"
          label="可见数量"
          :min="0"
          :step="1"
          suffix="个"
          v-model:value="dataZoom[0].scrollNum"
        />
      </div>
    </collapse-item>
    <n-divider style="margin: 6px 0 9px"></n-divider>
  </template>

  <slot />

  <!-- <collapse-item v-if="visualMap" name="视觉映射">
    <template #header>
      <n-switch v-model:value="visualMap.show" size="small"></n-switch>
    </template>

    <setting-item-box name="范围">
      <setting-item name="最小值">
        <n-input-number v-model:value="visualMap.min" size="small"></n-input-number>
      </setting-item>
      <setting-item name="最大值">
        <n-input-number v-model:value="visualMap.max" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>

    <setting-item-box name="颜色">
      <setting-item :name="`层级-${index + 1}`" v-for="(item, index) in visualMap.inRange.color" :key="index">
        <n-color-picker v-model:value="visualMap.inRange.color[index]" size="small"></n-color-picker>
      </setting-item>
    </setting-item-box>

    <setting-item-box name="控制块">
      <setting-item name="放置方向">
        <n-select v-model:value="visualMap.orient" size="small" :options="axisConfig.visualMap.orient"></n-select>
      </setting-item>
      <setting-item name="宽度">
        <n-input-number v-model:value="visualMap.itemWidth" :min="5" size="small"></n-input-number>
      </setting-item>
      <setting-item name="高度">
        <n-input-number v-model:value="visualMap.itemHeight" :min="5" size="small"></n-input-number>
      </setting-item>
      <setting-item name="反转">
        <n-space>
          <n-switch v-model:value="visualMap.inverse" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name="拖拽组件实时更新">
        <n-space>
          <n-switch v-model:value="visualMap.realtime" size="small"></n-switch>
        </n-space>
      </setting-item>
    </setting-item-box>
    <global-setting-position :targetData="visualMap"></global-setting-position>
  </collapse-item> -->
  <!-- 每个组件单独设置的配置 -->
</template>

<script setup lang="ts">
import { PropType, computed, reactive, ref, watch } from 'vue'
import { GlobalThemeJsonType } from '@/package/index.d'
import { axisConfig } from '@/package/config/axisConfig'
import {
  CustomInput,
  CustomInputNumber,
  CustomInputNumberWithSlider,
  CustomInputSelect,
  NewColorPicker,
  CustomSwitch,
  CustomRadio,
  LabelStyleRadio,
  InputNumberwithLabel
} from '@/components/Form'
import {
  CollapseItem,
  SettingItemBox,
  SettingItem,
  GlobalSettingPosition,
  ColorSetting,
  RadioSelectCustom
} from '@/components/Pages/ChartItemSetting'
import { fontFamilyOption, fontWeightOption } from './config'
import isNil from 'lodash/isNil'
import { useLoadOption } from '@/package/config/useChartOption'
import { getUUID } from '@/utils'
import { cloneDeep } from 'lodash'

const { useLegend, useTooltip, useAnimation } = useLoadOption()

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
  },
  other: {
    type: Array as PropType<Array<string>>,
    default: () => ['legend', 'tooltip', 'animation']
  }
})

const selectlegendStyleOption = [
  { label: '圆形', value: 'circle' },
  { label: '方形', value: 'rect' }
]
const selectlegendPositionOption = [
  { label: '顶部居左', value: '顶部居左' },
  { label: '顶部居中', value: '顶部居中' },
  { label: '顶部居右', value: '顶部居右' },
  { label: '底部居中', value: '底部居中' },
  { label: '右边', value: '右边' },
  { label: '左边', value: '左边' }
]

const selectlegendOrientOption = [
  { label: '水平排列', value: 'horizontal' },
  { label: '垂直排列', value: 'vertical' }
]

const selectlegendTypeOption = [
  { label: '直线', value: 'line' },
  { label: '阴影', value: 'shadow' },
  { label: '十字准星', value: 'cross' },
  { label: '无', value: 'none' }
]

const legend = computed(() => {
  return props.optionData?.legend ?? useLegend()
})

// 2024-08-26 修复基础班马图图例不展示的bug
if (props.optionData.series.length === 3 && props.optionData.series[0].type === 'pictorialBar') {
  watch(
    () => props.optionData.color,
    val => {
      props.optionData.legend = {
        ...props.optionData.legend,
        itemStyle: {
          color: val[0]
        }
      }
    },
    {
      deep: true,
      immediate: true
    }
  )
}

const tooltip = computed(() => {
  return props.optionData?.tooltip ?? useTooltip()
})

const animation = computed(() => {
  return props.optionData?.animationLoop ?? useAnimation()
})

// 视觉映射
const visualMap = computed(() => {
  return props.optionData.visualMap
})

// dataZoom 设置 -->
const dataZoom = computed(() => {
  return props.optionData.dataZoom
})

// 数值标记
const _sign = ref(false)
// 基准标线
const _line = ref(false)

// 图例位置
watch(
  () => props.newAttr.legendPostion,
  val => {
    if (isNil(legend.value)) {
      return
    }
    switch (val) {
      case '顶部居左': {
        legend.value.top = 'top'
        legend.value.left = 'left'
        break
      }
      case '顶部居中': {
        legend.value.top = 'top'
        legend.value.left = 'center'
        break
      }
      case '顶部居右': {
        legend.value.top = 'top'
        legend.value.left = 'right'
        break
      }
      case '底部居中': {
        legend.value.top = 'bottom'
        legend.value.left = 'center'
        break
      }
      case '右边': {
        legend.value.top = 'middle'
        legend.value.left = 'right'
        break
      }
      case '左边': {
        legend.value.top = 'middle'
        legend.value.left = 'left'
        break
      }
    }
  },
  {
    immediate: true
  }
)
//上下间距
watch(
  () => props.newAttr.legendPadding,
  val => {
    if (isNil(legend.value)) {
      return
    }
    // console.log(val);
    legend.value.padding = val ?? 0
  },
  {
    immediate: true
  }
)

// 圆形图例大小
watch(
  () => [legend.value.icon, legend.value.itemWidth],
  ([icon, w]) => {
    if (icon !== 'circle') return
    legend.value.itemHeight = w
  },
  { immediate: true }
)

// 循坏播放动画
watch(
  () => animation.value,
  val => {
    // 给当前组件的 options 上 加上animationLoop 属性
    // globalWatch 中有监听该属性 实现播放
    props.optionData.animationLoop = val
    props.optionData.animationDuration = val.animationDuration
  },
  { immediate: true, deep: true }
)
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
