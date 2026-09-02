import { ConfigType, ChartFrameEnum } from '@/package/index.d'

export const BZBridgeTunnelPoint: ConfigType = {
  key: 'BZBridgeTunnelPoint',
  chartKey: 'VBZBridgeTunnelPoint',
  conKey: 'VCBZBridgeTunnelPoint',
  eventKey: 'VEBZBridgeTunnelPoint',
  title: '桥隧风险感知',
  chartFrame: ChartFrameEnum.ECHARTS
}
