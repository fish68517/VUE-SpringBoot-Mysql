<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div class="config-row">
        功能模式：
        <select v-model="props.optionData.mode">
          <option value="waterlog">内涝</option>
          <option value="flood">防汛</option>
        </select>
      </div>
      <div class="config-row">
        容器宽度：
        <select v-model="props.optionData.containerMode">
          <option value="wide">宽屏容器 1208px</option>
          <option value="single">单屏容器 736px</option>
        </select>
      </div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { PropType, ref } from 'vue'
import { newAttrInter } from '@/package/index.d'
import { GlobalSetting } from '@/components/Pages/ChartItemSetting'
import { TableDataType } from '@/types/public.d'

const props = defineProps({
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
