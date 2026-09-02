/*
 * @Author: kaix
 * @Date: 2023-03-22 10:25:24
 * @LastEditTime: 2023-09-04 08:58:54
 * @LastEditors: wangcong
 * @Description:
 */
import { ConfigType, ChartFrameEnum } from '@/package/index.d'

export const BZDangerArea: ConfigType = {
  key: 'BZDangerArea',
  chartKey: 'VBZDangerArea',
  conKey: 'VCBZDangerArea',
  eventKey: 'VEBZDangerArea',
  title: '区域拥堵',
  chartFrame: ChartFrameEnum.COMMON
}
