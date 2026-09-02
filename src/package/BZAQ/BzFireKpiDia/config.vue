<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div class="config-row">消防KPI弹窗组件</div>
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
    name: '配置',
    slotName: 'other'
  }
])
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';

.config-row {
  padding: 0 20px;
  line-height: 32px;
}
</style>
