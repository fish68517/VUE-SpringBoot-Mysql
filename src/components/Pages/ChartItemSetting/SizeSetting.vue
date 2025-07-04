<template>
  <n-text>组件尺寸</n-text>
  <div class="box">
    <n-input-number v-model:value="chartAttr.w" :min="50" :disabled="isGroup" size="small" placeholder="px">
      <template #prefix>
        <n-text depth="3">W</n-text>
      </template>
    </n-input-number>
    <i class="iconfont icon-bilisuoding" />
    <n-input-number v-model:value="chartAttr.h" :min="50" :disabled="isGroup" size="small" placeholder="px">
      <template #prefix>
        <n-text depth="3">H</n-text>
      </template>
    </n-input-number>
  </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { PickCreateComponentType } from '@/packages/index.d'
// import { SettingItemBox } from '@/components/Pages/ChartItemSetting'

const props = defineProps({
  chartAttr: {
    type: Object as PropType<Omit<PickCreateComponentType<'attr'>, 'node' | 'conNode'>>,
    required: true
  },
  isGroup: {
    type: Boolean,
    required: false
  }
})
</script>

<style lang="scss" scoped>
.n-text {
  font-size: 12px;
  margin-left: 10px;
  // margin-bottom: 5px;
}
.box {
  display: flex;
  padding: 0 10px;
  justify-content: space-between;
  margin-top: 5px;
  margin-bottom: 20px;
  .icon-bilisuoding {
    margin: 0 7px;
  }
}
</style>
