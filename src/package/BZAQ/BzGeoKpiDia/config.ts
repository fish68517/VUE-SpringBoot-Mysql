import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BzGeoKpiDia } from './index'
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

export const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/geo'
// export const BASE_URL = 'http://192.168.112.165:11001/api/boot/system/geo';

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BzGeoKpiDia.key
  public chartConfig = cloneDeep(BzGeoKpiDia)
  public option = echartOptionProfixHandle(option, includes)
  public newAttr = {
    ...chartInitNewAttrConfig,
    w: 1920,
    h: 1080,
    themeColor: {
      useGlobalColor: false,
      selfTheme: {
        color: ['#04bcfa', '#0454cb', '#5fbfff'],
        value: 'technology'
      }
    }
  }
}
