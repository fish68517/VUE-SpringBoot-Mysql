<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div class="wrap">
        <CustomRadio label="背景" v-model:value="optionData.showBackground" :options="backgroundOptions" />
        <CustomRadio label="屏幕模式" v-model:value="optionData.screenMode" :options="screenModeOptions" />
      </div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { PropType, ref } from 'vue'
import { newAttrInter } from '@/package/index.d'
import { GlobalSetting } from '@/components/Pages/ChartItemSetting'
import { CustomRadio } from '@/components/Form'
import { TableDataType } from '@/types/public.d'

defineProps({
  optionData: {
    type: Object as PropType<any>,
    required: true
  },
  newAttr: {
    type: Object as PropType<newAttrInter>,
    required: true
  }
})

const backgroundOptions = [
  { label: '显示', value: 'show' },
  { label: '隐藏', value: 'hide' }
]

const screenModeOptions = [
  { label: '单屏', value: 'single' },
  { label: '三联屏', value: 'triple' }
]

const tabData = ref<Array<TableDataType>>([
  {
    name: '基础配置',
    slotName: 'other'
  }
])
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
</style>
