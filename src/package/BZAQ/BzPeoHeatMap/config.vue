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
      <div>热力地址：<input v-model="props.optionData.heatMapAddress"/></div>
    </template>
  </GlobalSetting>
</template>

<script setup lang="ts">
import { PropType, toRefs, watch, ref, h, defineProps, onMounted } from 'vue'
import { CollapseItem, GlobalSetting } from '@/components/Pages/ChartItemSetting'
import { TableDataType } from '@/types/public.d'
import {
  CustomInput,
  CustomInputNumber,
  CustomInputNumberWithSlider,
  CustomInputSelect,
  CustomColorPicker,
  NewColorPicker,
  CustomSwitch,
  CustomRadio,
  LabelStyleRadio,
  InputNumberwithLabel
} from '@/components/Form'
import { getUUID } from '@/utils'
import type { UploadFileInfo } from 'naive-ui'
import PointCard from './componenets/pointCard.vue'
import { icon } from '@/plugins/index'

// const heatAddress = ref('')

const props = defineProps({
  optionData: {
    type: Object,
    required: true
  }
})
const type = ref('')
const { setWay, normal, custom, pointData, pointControl, isShowCollectBtn } = toRefs(props.optionData)
const normalfileList = ref<UploadFileInfo[]>([])
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
    name: '落图配置',
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
onMounted(() => {
  console.log(props.optionData,'props------------')
  // if (normal.value.file) {
  //   normalfileList.value = [normal.value.file]
  // }
})
const doAdd = () => {
  if (!type.value) {
    window.$message.error('类型不能为空')
    return
  }
  const res = typeOptions.find(v => v.value === type.value)
  const index = custom.value.findIndex(v => v.type === res.type)
  if (index > -1) {
    window.$message.error('类型已存在')
    return
  }
  custom.value.push({
    id: getUUID(),
    type: res?.label ?? '',
    value: res.value ?? '',
    file: null
    // bgColor: '',
    // listColor: '',
    // fileList: []
  })
}
const doDel = (item: any) => {
  const index = custom.value.findIndex(v => v.id === item.id)
  custom.value.splice(index, 1)
  if (checkedValue.value === item.value) {
    checkedValue.value = null
  }
  // window.closePopup(item.value)
}
const beforeUpload = (data: { file: UploadFileInfo; fileList: UploadFileInfo[] }) => {
  if (data.file.file?.type !== 'image/png') {
    window.$message.error('只能上传png格式的图片文件，请重新上传')
    return false
  }
  if (data.file.file.size > 1024 * 1024 * 10) {
    window.$message.error('图片大小不能超过10M，请重新上传')
    return false
  }
  if (data.fileList.length > 1) {
    window.$message.error('最多只能上传1张图片')
    return false
  } else {
    const index = data.fileList.findIndex(v => v.name === data.file.name)
    if (index > -1) {
      window.$message.error('该文件已存在，请更换其他文件进行上传')
    }
  }
  return true
}
// 独立弹窗-点位选择时 切换样式
// const handleChange = (data: any) => {
//   console.log('🚀 ~ handleChange ~ data:', data)
//   const checked = data?.target
//   console.log('🚀 ~ handleChange ~ checked:', checked)
//   checkedValue.value = checked?.value
// }
const customRequest2 = ({ file, data, headers, withCredentials, action, onFinish, onError, onProgress }) => {
  console.log('🚀 ~ customRequest2 ~ file:', file)
  let imageUrl = null
  if (file) {
    const reader = new FileReader()
    reader.readAsDataURL(file.file)
    reader.onload = e => {
      imageUrl = e.target.result
      normal.value.file = imageUrl
    }
  }
}
const customRequest = ({ file, data, headers, withCredentials, action, onFinish, onError, onProgress }) => {
  // console.log('🚀 ~ customRequest ~ formData:', file, data)
  let imageUrl = null
  if (file) {
    const reader = new FileReader()
    reader.readAsDataURL(file.file)
    reader.onload = e => {
      imageUrl = e.target.result
      custom.value.find(v => v.type === data.type).file = imageUrl
    }
  }
}
// 撒点配置
const setPointData = val => {
  // console.log('🚀 ~ setPointData ~ val:', val)
  pointData.value = val
}

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
