<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData" />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { GlobalSetting } from '@/components/Pages/ChartItemSetting'
import { TableDataType } from '@/types/public.d'

const tabData = ref<Array<TableDataType>>([
  {
    name: '配置',
    slotName: 'other'
  }
])
</script>
