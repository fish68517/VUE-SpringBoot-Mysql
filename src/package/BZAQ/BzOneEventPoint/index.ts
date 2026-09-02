import { ConfigType, ChartFrameEnum } from '@/package/index.d'
import { ChatCategoryEnumName } from '@/package/public/index.d'

export const BzOneEventPoint: ConfigType = {
  key: 'BzOneEventPoint',
  chartKey: 'VBzOneEventPoint',
  conKey: 'VCBzOneEventPoint',
  eventKey: 'VEBzOneEventPoint',
  title: '一件事点图层',
  chartFrame: ChartFrameEnum.COMMON,
  categoryName: ChatCategoryEnumName.MORE
}
