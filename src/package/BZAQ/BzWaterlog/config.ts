import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BzWaterlog } from './index'
import { CreateComponentType } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'
import { useLoadOption } from '@/package/config/useChartOption'
import { chartInitNewAttrConfig } from '@/package/config/const'
const { useTitle, useGrid, useXAxis, useYAxis, useLegend, useTooltip, useColor, useDataZoom } = useLoadOption()
export const includes = ['legend', 'xAxis', 'yAxis', 'grid']

export const seriesItem = {
  type: 'bar',
  barGap: '0%',
  barCategoryGap: '75%',
  label: {
    show: false,
    position: 'top',
    color: '#fff',
    fontSize: 12
  },
  itemStyle: {
    borderRadius: [0, 0, 0, 0],
    color: ''
  },
  showBackground: false,
  backgroundStyle: {
    color: '#e9e9e90d'
  },
  showMarkPoint: false,
  markMax: true,
  markMin: true,
  markPoint: {
    data: []
  },
  markLineSetting: {
    show: false,
    type: 'average',
    value: 0,
    showOverBaseColor: false,
    useGradient: false,
    color: ['#316EF4', '#316EF4']
  },
  markLine: {
    data: [],
    label: {
      show: true,
      position: 'end',
      fontWeight: 'normal',
      fontFamily: 'Microsoft YaHei',
      fontSize: 12,
      color: '#fff',
      distance: -25
    },
    lineStyle: {
      width: 1,
      type: 'dashed',
      color: '#316EF4'
    }
  },
  data: []
}
export const option = {
  dataset: {
    dimensions: ['time', 'rainfall'],
    source: [
      {
        time: '',
        rainfall: ''
      }
    ]
  },
  requestAddress:'',
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
  public key = BzWaterlog.key
  public chartConfig = cloneDeep(BzWaterlog)
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