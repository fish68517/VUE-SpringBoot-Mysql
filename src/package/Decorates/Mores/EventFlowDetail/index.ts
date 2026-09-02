import { ConfigType, PackagesCategoryEnum } from '@/package/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '@/package/public/index.d'

export const EventFlowDetailConfig: ConfigType = {
  key: 'EventFlowDetail',
  chartKey: 'VEventFlowDetail',
  conKey: 'VCEventFlowDetail',
  title: '事件流转详情',
  category: ChatCategoryEnum.MORE,
  categoryName: ChatCategoryEnumName.MORE,
  package: PackagesCategoryEnum.DECORATES
}
