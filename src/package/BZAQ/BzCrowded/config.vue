<template>
  <!-- Echarts 全局设置 -->
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div class="crowd-config-wrap">
        <div class="crowd-config-item">
          <span class="crowd-config-label">是否大客流</span>
          <n-checkbox v-model:checked="isBigCrowd" />
        </div>
      </div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { PropType, ref, watch } from 'vue'
import { NCheckbox } from 'naive-ui'
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

// 是否大客流模式配置
const isBigCrowd = ref(props.optionData?.isBigCrowd === true)
watch(isBigCrowd, (val) => {
  if (props.optionData) {
    props.optionData.isBigCrowd = val
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
.crowd-config-wrap {
  padding: 8px 20px;
  .crowd-config-item {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 10px;
    .crowd-config-label {
      font-size: 12px;
      color: #fff;
    }
  }
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
