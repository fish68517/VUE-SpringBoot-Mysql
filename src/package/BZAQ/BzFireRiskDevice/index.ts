import { ConfigType, ChartFrameEnum } from '@/package/index.d'
import { ChatCategoryEnumName } from '@/package/public/index.d'

export const BzFireRiskDevice: ConfigType = {
  key: 'BzFireRiskDevice',
  chartKey: 'VBzFireRiskDevice',
  conKey: 'VCBzFireRiskDevice',
  eventKey: 'VEBzFireRiskDevice',
  title: '风险点感知设备图层',
  chartFrame: ChartFrameEnum.COMMON,
  categoryName: ChatCategoryEnumName.MORE,
  option: {
    containerMode: 'wide'
  }
}
