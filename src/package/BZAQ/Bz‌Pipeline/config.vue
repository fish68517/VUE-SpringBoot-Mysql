<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div class="wrap">
        <CustomRadio label="背景" v-model:value="optionData.showBackground" :options="backgroundOptions" />
        <CustomInput label="调度地址" v-model:value="optionData.dispatchUrl" placeholder="请输入调度页面地址" />
      </div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { PropType, ref } from 'vue'
import { newAttrInter } from '@/package/index.d'
import { GlobalSetting } from '@/components/Pages/ChartItemSetting'
import { CustomInput, CustomRadio } from '@/components/Form'
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
