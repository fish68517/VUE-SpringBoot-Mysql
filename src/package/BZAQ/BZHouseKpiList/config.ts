import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BZHouseKpiList } from './index'
import { CreateComponentType } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitNewAttrConfig } from '@/package/config/const'

export const includes = []

export const option = {
  dataset: null,
  requestAddress: '',
  requestMethod: 'get',
  usePublicParam: true,
  customEvents: [],
  actions: [],
  eventConfig: []
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BZHouseKpiList.key
  public chartConfig = cloneDeep(BZHouseKpiList)
  public option = echartOptionProfixHandle(option, includes)
  public newAttr = {
    ...chartInitNewAttrConfig,
    w: 456,
    h: 620,
    themeColor: {
      useGlobalColor: false,
      selfTheme: {
        color: ['#04bcfa', '#0454cb', '#5fbfff'],
        value: 'technology'
      }
    }
  }
}
