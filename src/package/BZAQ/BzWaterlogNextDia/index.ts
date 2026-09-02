/*
 * @Author: kaix
 * @Date: 2023-03-22 10:25:24
 * @LastEditTime: 2023-09-04 08:58:54
 * @LastEditors: wangcong
 * @Description:
 */
import { ConfigType, ChartFrameEnum } from '@/package/index.d'

export const BzWaterlogNextDia: ConfigType = {
  key: 'BzWaterlogNextDia',
  chartKey: 'VBzWaterlogNextDia',
  conKey: 'VCBzWaterlogNextDia',
  eventKey: 'VEBzWaterlogNextDia',
  title: '内涝防汛弹窗',
  chartFrame: ChartFrameEnum.ECHARTS
}