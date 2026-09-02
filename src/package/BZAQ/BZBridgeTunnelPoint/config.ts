import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BZBridgeTunnelPoint } from './index'
import { CreateComponentType } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'
import { chartInitConfig, chartInitNewAttrConfig } from '@/package/config/const'

export const includes = []

export const option = {
  dataset: dataJson,
  requestAddress: '',
  requestMethod: 'get',
  usePublicParam: true,
  screenMode: 'single',
  customEvents: [],
  actions: []
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BZBridgeTunnelPoint.key
  public chartConfig = cloneDeep(BZBridgeTunnelPoint)
  public option = echartOptionProfixHandle(option, includes)
  public attr = { ...chartInitConfig, w: 230, h: 42, zIndex: 1 }
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
