import { ConfigType, ChartFrameEnum } from '@/package/index.d'

export const BzWaterlog: ConfigType = {
  key: 'BzWaterlog',
  chartKey: 'VBzWaterlog',
  conKey: 'VCBzWaterlog',
  eventKey: 'VEBzWaterlog',
  title: '内涝防汛',
  chartFrame: ChartFrameEnum.ECHARTS
}