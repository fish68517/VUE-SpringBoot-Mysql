import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BzFeedback } from './index'
import { CreateComponentType } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'
import { useLoadOption } from '@/package/config/useChartOption'
import { chartInitNewAttrConfig } from '@/package/config/const'

const { useTitle } = useLoadOption()
export const includes = []

export const option = {
  dataset: {
    dimensions: ['name', 'value'],
    source: dataJson.source
  },
  requestAddress: '',
  requestMethod: 'get',
  usePublicParam: true,
  enablePopup: true,
  customEvents: [
    { label: '单击', value: 'customClick' },
    { label: '双击', value: 'customDblclick' }
  ],
  actions: [
    { label: '组件显示隐藏', value: 'showHide' },
    { label: '更新公共参数', value: 'updatePublcParam' },
    { label: '信息提示', value: 'showMessage' },
    { label: '跳转页面', value: 'jumpPage' },
    { label: '打开链接', value: 'openLink' },
    { label: '更新状态', value: 'updateStatus' },
    { label: '更新组件数据', value: 'updateComponentData' },
    { label: '数据导出', value: 'dataExport' },
    { label: '更新配置项', value: 'editOption' }
  ],
  eventConfig: []
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BzFeedback.key
  public chartConfig = cloneDeep(BzFeedback)
  public option = echartOptionProfixHandle(option, includes)
  public newAttr = {
    ...chartInitNewAttrConfig,
    themeColor: {
      useGlobalColor: false,
      selfTheme: {
        color: ['#04bcfa', '#0454cb', '#056ff1', '#47dea2', '#16b8d6', '#f1b736'],
        value: 'technology'
      }
    }
  }
}
