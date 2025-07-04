/*
 * @Author: kaix
 * @Date: 2023-03-12 21:58:55
 * @LastEditTime: 2023-07-14 15:52:37
 * @LastEditors: kaix
 * @Description:
 */
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@/package/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '@/package/public/index.d'

export const TimeCommonConfig: ConfigType = {
  key: 'TimeCommon',
  chartKey: 'VTimeCommon',
  conKey: 'VCTimeCommon',
  title: '通用时间',
  category: ChatCategoryEnum.MORE,
  categoryName: ChatCategoryEnumName.MORE,
  package: PackagesCategoryEnum.DECORATES
}
