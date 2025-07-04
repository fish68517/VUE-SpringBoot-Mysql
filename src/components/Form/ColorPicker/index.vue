<!--
 * @Author: wangcong
 * @Date: 2023-05-18 21:01:27
 * @LastEditTime: 2023-08-23 08:45:06
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <n-form :label-placement="labelPlacement">
    <n-form-item :label="label">
      <n-color-picker v-model:value="_value" :swatches="swatches" />
      <n-input type="text" placeholder="请输入颜色值" :value="_value" @dblclick="test"></n-input>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  labelPlacement?: 'top' | 'left'
  label: string
  value: string | undefined
  swatches?: string[]
  compMarginBottom?: string
}

const props = withDefaults(defineProps<Props>(), {
  labelPlacement: 'top',
  // 默认展示的颜色列表
  swatches: () => ['#232324', '#2a2a2b', '#313132', '#373739', '#757575', '#e0e0e0', '#eeeeee', '#fafafa'],
  compMarginBottom: '0'
})

const emit = defineEmits<{
  (e: 'update:value', value: string): void
}>()

const _value = computed({
  get: () => props.value,
  set: value => {
    emit('update:value', value!)
  }
})

const displayVal = computed(() => {
  return props.labelPlacement === 'top' ? 'grid' : 'flex'
})

const test = () => {
  const eyeDropper = new window.EyeDropper()
  eyeDropper.open().then((res: {sRGBHex: string}) => {
    console.log(res)
    // const {sRGBHex} = res
    // emitColor(sRGBHex)
    // emits('update:value', sRGBHex)
    
  }).catch(() => {})
}

</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.n-form-item {
  display: v-bind('displayVal');
  align-items: center;
  margin-bottom: 10px;
}
.n-form-item {
  :deep(.n-form-item-feedback-wrapper) {
    min-height: v-bind('compMarginBottom');
  }
}
</style>
