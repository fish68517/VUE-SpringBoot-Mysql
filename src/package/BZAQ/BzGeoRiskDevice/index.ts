import { ConfigType, ChartFrameEnum } from '@/package/index.d'
import { ChatCategoryEnumName } from '@/package/public/index.d'

export const BzGeoRiskDevice: ConfigType = {
  key: 'BzGeoRiskDevice',
  chartKey: 'VBzGeoRiskDevice',
  conKey: 'VCBzGeoRiskDevice',
  eventKey: 'VEBzGeoRiskDevice',
  title: '风险点感知设备图层',
  chartFrame: ChartFrameEnum.COMMON,
  categoryName: ChatCategoryEnumName.MORE,
  option: {
    containerMode: 'wide'
  }
}
