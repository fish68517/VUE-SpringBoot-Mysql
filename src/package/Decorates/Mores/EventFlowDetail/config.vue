<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div class="wrap"></div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { PropType, ref } from 'vue'
import { GlobalSetting } from '@/components/Pages/ChartItemSetting'
import { ChartFrameEnum } from '@/package/index.d'

const props = defineProps({
  optionData: {
    type: Object as PropType<any>,
    required: true
  }
})

const tabData = ref([
  {
    label: '基础',
    value: 'base',
    type: ChartFrameEnum.COMMON
  }
])

void props
</script>

<style lang="scss" scoped>
.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}
</style>
