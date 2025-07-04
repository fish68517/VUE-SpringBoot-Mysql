<!--
 * @Author: wangcong
 * @Date: 2023-08-16 16:58:19
 * @LastEditTime: 2025-01-15 17:11:49
 * @LastEditors: wangcong
 * @Description: 非echarts组件的颜色系列
-->
<template>
  <div v-if="color" class="seriesColor">
    <div class="title">
      <span>{{ title ? title : `数据${index + 1}` }}</span>
      <div class="switch">
        <span class="label">启用渐变色</span>
        <n-switch v-model:value="isGradient" />
      </div>
    </div>
    <template v-if="!isGradient">
      <NewColorPicker label-placement="left" :label="labels" v-model:value="_color" v-bind="$attrs" />
    </template>
    <template v-else>
      <CustomInputNumberWithSlider
        label="渐变角度"
        v-model:value="angle"
        :max="360"
        v-if="isAngle && style === 'linear'"
      />
      <NewColorPicker label-placement="left" label="开始颜色" v-model:value="startColor" v-bind="$attrs" />
      <NewColorPicker label-placement="left" label="结束颜色" v-model:value="endColor" v-bind="$attrs" />
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch, PropType } from 'vue'
import { CustomInputNumberWithSlider, CustomInputSelect, NewColorPicker } from '@/components/Form'
const props = defineProps({
  color: {
    type: String as PropType<any>,
    default: '#fff'
  },
  labels: {
    type: String as PropType<any>,
    default: '背景颜色'
  },
  index: {
    type: Number,
    default: 0
  },
  title: {
    type: String,
    default: ''
  },
  //是否显示角度
  isAngle: {
    type: Boolean,
    default: false
  }
})

const emits = defineEmits(['update:color'])

const _color = computed({
  get: () => {
    if (!props.color.includes('gradient')) {
      return props.color
    } else {
      return props.color.split(' ')[1]
    }
  },
  set: val => {
    emits('update:color', val)
  }
})

const isGradient = ref(false)
const startColor = ref('')
const endColor = ref('')
const angle = ref(90)
const style = ref('linear')
watch(
  () => isGradient.value,
  val => {
    if (val) {
      if (!props.color.includes('gradient')) {
        startColor.value = props.color
        endColor.value = props.color
      } else {
        style.value = props.color.split(' ')[0].split('-gradient(')[0]
        if (style.value === 'linear') angle.value = props.color.split(' ')[0].split('-gradient(')[1].split('deg')[0]
        startColor.value = props.color.split(' ')[1]
        endColor.value = props.color.split(' ')[3]
      }
    } else {
      endColor.value = ''
      emits('update:color', startColor.value)
    }
  }
)
watch(
  () => [startColor.value, endColor.value, angle.value, style.value],
  ([color1, color2, angle, style]) => {
    if (isGradient.value) {
      const color: any = `${style}-gradient(${style === 'linear' ? `${angle}deg,` : ''} ${color1} , ${color2} )`
      console.log(color)
      emits('update:color', color)
    }
  }
)

watch(
  () => props.color,
  val => {
    if (!val.includes('gradient')) {
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
@import '@/styles/pages/form.scss';
$primary-color: var(--primary-color);
.seriesColor {
  .title {
    font-size: 12px;
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
        &.n-switch--active {
          :deep(.n-switch__rail) {
            border: 1px solid $primary-color;
            .n-switch__button {
              top: 1.5px;
              left: 22px;
            }
          }
        }
        :deep(.n-switch__rail) {
          height: 18px;
          min-width: 38px;
          .n-switch__button {
            width: 15px;
            height: 15px;
          }
        }
      }
    }
  }
}
</style>
