import { PublicConfigClass } from '../../../public/index'
import { EventFlowDetailConfig } from './index'
import { CreateComponentType } from '../../../index.d'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitConfig } from '@/package/config/const'

export const option = {}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = EventFlowDetailConfig.key
  public attr = { ...chartInitConfig, w: 540, h: 360, zIndex: -1 }
  public chartConfig = cloneDeep(EventFlowDetailConfig)
  public option = cloneDeep(option)
}
