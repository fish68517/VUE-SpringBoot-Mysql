import { ConfigType, ChartFrameEnum } from '@/package/index.d'

export const BZBridgeTunnel: ConfigType = {
  key: 'BZBridgeTunnel',
  chartKey: 'VBZBridgeTunnel',
  conKey: 'VCBZBridgeTunnel',
  eventKey: 'VEBZBridgeTunnel',
  title: '桥隧监测',
  chartFrame: ChartFrameEnum.ECHARTS
}
