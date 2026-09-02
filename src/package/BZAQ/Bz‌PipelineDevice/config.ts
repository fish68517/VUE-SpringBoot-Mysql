import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { Bz‌PipelineDevice } from './index'
import { CreateComponentType } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitNewAttrConfig } from '@/package/config/const'

export const includes = []

export const option = {
  dataset: null,
  customEvents: [],
  actions: [],
  eventConfig: []
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = Bz‌PipelineDevice.key
  public chartConfig = cloneDeep(Bz‌PipelineDevice)
  public option = echartOptionProfixHandle(option, includes)
  public newAttr = {
    ...chartInitNewAttrConfig,
    w: 188,
    h: 220
  }
}
