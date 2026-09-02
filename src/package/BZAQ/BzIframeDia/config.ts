import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BzIframeDia } from './index'
import { CreateComponentType } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitNewAttrConfig } from '@/package/config/const'

export const includes = []

export const option = {
  dataset: null,
  requestAddress: '',
  requestMethod: 'get',
  usePublicParam: true,
  enablePopup: true,
  customEvents: [],
  actions: [],
  eventConfig: []
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BzIframeDia.key
  public chartConfig = cloneDeep(BzIframeDia)
  public option = echartOptionProfixHandle(option, includes)
  public newAttr = {
    ...chartInitNewAttrConfig,
    w: 960,
    h: 540,
    themeColor: {
      useGlobalColor: false,
      selfTheme: {
        color: ['#2f8cff', '#11d9ff', '#00f09e'],
        value: 'technology'
      }
    }
  }
}
