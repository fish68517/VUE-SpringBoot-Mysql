import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BzRisk } from './index'
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
    source: [
      {
        name: '风险总数',
        value: 31
      },
      {
        name: '整改督办',
        value: 41
      },
      {
        name: '部门',
        value: 0
      },
      {
        name: '已完成',
        value: 0
      },
      {
        name: '第三次力量',
        value: 19
      }
    ]
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
  public key = BzRisk.key
  public chartConfig = cloneDeep(BzRisk)
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
