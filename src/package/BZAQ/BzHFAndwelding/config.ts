import cloneDeep from 'lodash/cloneDeep'
import { CreateComponentType } from '@/package/index.d'
import { chartInitConfig, chartInitNewAttrConfig } from '@/package/config/const'
import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BzHFAndwelding } from './index'

export const includes = []

export const option = {
  dataset: {
    dimensions: [],
    source: []
  },
  requestAddress: '',
  requestMethod: 'get',
  usePublicParam: true,
  enablePopup: true,
  customEvents: [],
  actions: [],
  eventConfig: []
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BzHFAndwelding.key
  public chartConfig = cloneDeep(BzHFAndwelding)
  public attr = { ...chartInitConfig, w: 500, h: 635, zIndex: -1 }
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
