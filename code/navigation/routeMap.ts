export const routes: Record<string, string> = {
  mapChanges: 'map/changes',
  mapConfig: 'map/config',
  mapAnalysis: 'map/analysis',
  logs: 'logs/index',
  inspectionConfig: 'inspection/config',
  inspectionReplay: 'inspection/replay',
  energy: 'energy/index',
  video: 'video/index',
  advancedSettings: 'settings/advanced',
  mobileReport: 'mobile/report',
  login: 'login/index',
  dashboard: 'dashboard/index',
  portal: 'portal/index',
  portalDetail: 'portal/detail',
  alarms: 'alarms/index',
  alarmDetail: 'alarms/detail',
  orders: 'workorders/index',
  orderDetail: 'workorders/detail',
  orderEdit: 'workorders/edit',
  map: 'map/index',
  facility: 'facilities/detail',
  dma: 'dma/index',
  dmaDetail: 'dma/detail',
  inspection: 'inspection/index',
  taskDetail: 'inspection/detail',
  taskEdit: 'inspection/edit',
  datahub: 'datahub/index',
  device: 'datahub/device',
  reports: 'reports/index',
  settings: 'settings/index',
  mobileHome: 'mobile/home',
  mobileTasks: 'mobile/tasks',
  mobileMap: 'mobile/map',
  profile: 'mobile/profile',
}
export const titles: Record<string, string> = {
  mapChanges: '点线变更与审核',
  mapConfig: '图层与审核配置',
  mapAnalysis: '爆管影响分析',
  logs: '操作日志',
  inspectionConfig: '巡检模板与路线',
  inspectionReplay: '人员轨迹与统计',
  energy: '能耗与设备运行',
  video: '视频中心',
  advancedSettings: '门户、通知与快照',
  mobileReport: '移动事件上报',
  dashboard: '供水运行总览',
  portal: '工作门户',
  portalDetail: '信息详情',
  alarms: '报警中心',
  alarmDetail: '告警详情',
  orders: '工单中心',
  orderDetail: '工单详情',
  orderEdit: '新建工单',
  map: '管控一张图',
  facility: '设施详情',
  dma: 'DMA 分区计量',
  dmaDetail: '分区详情',
  inspection: '管网巡检',
  taskDetail: '巡检任务详情',
  taskEdit: '发布巡检任务',
  datahub: '设备监测',
  device: '设备详情',
  reports: '综合统计报表',
  settings: '演示设置',
  mobileHome: '个人工作台',
  mobileTasks: '我的任务',
  mobileMap: '移动地图',
  profile: '我的账号',
}
export function urlFor(key: string, params: Record<string, unknown> = {}) {
  const route = routes[key]
  if (!route) throw new Error('未注册页面 ' + key)
  const q = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null && v !== '')
    .map(([k, v]) => encodeURIComponent(k) + '=' + encodeURIComponent(String(v)))
    .join('&')
  return '/pages/' + route + (q ? '?' + q : '')
}
export function go(key: string, params: Record<string, unknown> = {}, replace = false) {
  const url = urlFor(key, params)
  if (replace) uni.redirectTo({ url })
  else uni.navigateTo({ url, fail: () => uni.redirectTo({ url }) })
}
export function back() {
  if (getCurrentPages().length > 1) uni.navigateBack()
  else go('portal', {}, true)
}
export function safeRedirect(value: string) {
  const path = value.split('?')[0]
  return Object.values(routes).some((r) => '/pages/' + r === path) && path !== '/pages/login/index'
    ? value
    : ''
}
