import { ConfigType, ChartFrameEnum } from '@/package/index.d'
import { ChatCategoryEnumName } from '@/package/public/index.d'

export const BzWaterRiskDevice: ConfigType = {
  key: 'BzWaterRiskDevice',
  chartKey: 'VBzWaterRiskDevice',
  conKey: 'VCBzWaterRiskDevice',
  eventKey: 'VEBzWaterRiskDevice',
  title: '风险点感知设备图层',
  chartFrame: ChartFrameEnum.COMMON,
  categoryName: ChatCategoryEnumName.MORE,
  option: {
    containerMode: 'wide'
  }
}
