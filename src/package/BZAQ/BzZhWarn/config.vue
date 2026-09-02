<template>
  <!-- Echarts 全局设置 -->
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div class="config-row">
        三连屏模式：
        <input type="checkbox" v-model="props.optionData.tripleScreen" />
      </div>
      <div class="config-row" v-if="!props.optionData.tripleScreen">
        内涝防汛模式：
        <input type="checkbox" v-model="props.optionData.waterlogMode" />
      </div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { PropType, ref, computed, watch, reactive } from 'vue'
import { newAttrInter } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import { GlobalSetting, CollapseItem } from '@/components/Pages/ChartItemSetting'
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
    name: '落图配置',
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

.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  .subtitle {
    height: 17px;
    font-size: 12px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
    // color: #ffffff;
    line-height: 17px;
    margin-bottom: 10px;
    &.control {
      display: flex;
      justify-content: space-between;
      .btns {
        display: flex;
        gap: 10px;
        i {
          font-size: 16px;
          cursor: pointer;
        }
      }
    }
  }
}
</style>
