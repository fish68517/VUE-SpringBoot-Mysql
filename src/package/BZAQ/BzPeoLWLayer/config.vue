<!--
 * @Author: wangcong
 * @Date: 2023-04-18 16:07:27
 * @LastEditTime: 2024-03-15 09:05:40
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <GlobalSetting :is-use-custom="true" :tabData="tabData">
    <template #other>
      <div>接口地址：<input :src="instanceUrl"/></div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { toRefs, ref, defineProps, onMounted } from 'vue'
import { GlobalSetting } from '@/components/Pages/ChartItemSetting'
import { TableDataType } from '@/types/public.d'
import type { UploadFileInfo } from 'naive-ui'

const props = defineProps({
  optionData: {
    type: Object,
    required: true
  }
})
const type = ref('')
const { setWay, normal, custom, pointData, pointControl, isShowCollectBtn } = toRefs(props.optionData)
const normalfileList = ref<UploadFileInfo[]>([])

const instanceUrl = ref('')
// const pointControl = ref({
//   isDefault: false,
//   pointNumLimit: 200
// })
// const uploadUrl = ref('http://23.210.227.13:25082/prod-api/imgUpload/uploadImage') // 上传的图片服务器地址
// const headers = {
//   Authorization:
//     'Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6Ijc4YmVhYzA3LWI4OTctNDZhZS1hYmU3LTlkZjU3ODFjMGQ0ZiJ9.B-NTwj-wX2d8cf_A_iTqVBqVsrr6eWAqv3hNb-1k8WLm8Hk5CORjBLxIJcFF5C0oU7m_0__NdAraprMXzAgOyw'
// }

const tabData = ref<Array<TableDataType>>([
  {
    name: '数据配置',
    slotName: 'other'
  }
])
const setOptions = [
  { label: '通用配置', value: 'normal' },
  { label: '独立配置', value: 'custom' }
]
const typeOptions = [
  { label: '单兵、摄像头、布控球、无人机', value: 'videoOnMap' },
  { label: '一标三实', value: 'fangwudian' },
  { label: '风险点', value: 'fxd' },
  { label: 'RFID设备', value: 'rfid' },
  { label: '事件信息', value: 'shijiandian' },
  { label: '网格员', value: 'wgy' },
  { label: '应急队伍', value: 'yjdwdetails' },
  { label: '物质仓库', value: 'wuziku' },
  { label: '轨道站', value: 'gdz' },
  { label: '住宿场所', value: 'zscs' },
  { label: '酒店、民宿', value: 'ZscsHomestay' },
  { label: '河道水位站', value: 'hdsweiz' },
  { label: '道路车辆', value: 'dlyscl' },
  { label: '船舶', value: 'chuanbodetail' },
  { label: '企业', value: 'qiyepage' }
]
const { layer } = toRefs(props.optionData)
const list = ref([])
const checkedValue = ref(null)


</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';

.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  .point-control {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .info-desc {
      display: flex;
      align-items: center;
    }
  }
  .subtitle {
    height: 17px;
    font-size: 12px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
    color: #ffffff;
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

  .list-item {
    margin-top: 10px;

    .list-name {
      font-size: 12px;
      font-family: PingFangSC-Medium, PingFang SC;
      font-weight: 500;
      color: #ffffff;
      line-height: 17px;
      margin-bottom: 10px;
    }
  }
}
</style>
