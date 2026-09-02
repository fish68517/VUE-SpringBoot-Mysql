<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div class="wrap">
        <div class="config-row">场所关键KPI组件</div>
      </div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { PropType, ref } from 'vue'
import { newAttrInter } from '@/package/index.d'
import { GlobalSetting } from '@/components/Pages/ChartItemSetting'
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
