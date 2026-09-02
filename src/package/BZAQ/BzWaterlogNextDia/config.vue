<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div>请求地址：<input v-model="props.optionData.requestAddress" /></div>
      <div>
        请求方法：
        <select v-model="props.optionData.requestMethod">
          <option value="get">GET</option>
          <option value="post">POST</option>
        </select>
      </div>
      <div>
        使用公共参数：
        <input type="checkbox" v-model="props.optionData.usePublicParam" />
      </div>
      <div>
        允许点击弹窗：
        <input type="checkbox" v-model="props.optionData.enablePopup" />
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
    name: '落图配置',
    slotName: 'other'
  }
])
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  .subtitle {
    height: 17px;
    font-size: 12px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
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