import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BzFireKpiList } from './index'
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

export const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/fire'
// export const BASE_URL = 'http://192.168.112.165:11001/api/boot/system/fire'

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BzFireKpiList.key
  public chartConfig = cloneDeep(BzFireKpiList)
  public option = echartOptionProfixHandle(option, includes)
  public newAttr = {
    ...chartInitNewAttrConfig,
    w: 580,
    h: 748,
    themeColor: {
      useGlobalColor: false,
      selfTheme: {
        color: ['#04bcfa', '#0454cb', '#5fbfff'],
        value: 'technology'
      }
    }
  }
}
