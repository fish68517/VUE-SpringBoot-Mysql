/*
 * @Author: kaix
 * @Date: 2023-03-22 10:25:24
 * @LastEditTime: 2023-09-04 08:58:54
 * @LastEditors: wangcong
 * @Description: 
 */
import { ConfigType, ChartFrameEnum } from '@/package/index.d'
import { ChatCategoryEnumName } from '@/package/public/index.d'

export const BzPeoLWLayer: ConfigType = {
  key: 'BzPeoLWLayer',
  chartKey: 'VBzPeoLWLayer',
  conKey: 'VCBzPeoLWLayer',
  title: '线图层组件',
  chartFrame: ChartFrameEnum.COMMON,
  categoryName: ChatCategoryEnumName.MORE,
}
