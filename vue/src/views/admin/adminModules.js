const yesNo = [{ label: '正常', value: 1 }, { label: '停用', value: 0 }]
const userType = [{ label: '普通用户', value: 0 }, { label: '管理员', value: 1 }]
const stationStatus = [{ label: '停用', value: 0 }, { label: '运营中', value: 1 }, { label: '维护中', value: 2 }]
const stationType = [{ label: '公共', value: 0 }, { label: '专用', value: 1 }, { label: '高速', value: 2 }]
const connectorType = [{ label: '直流快充', value: 0 }, { label: '交流慢充', value: 1 }]
const workStatus = [
  { label: '空闲', value: 0 }, { label: '使用中', value: 1 }, { label: '预约', value: 2 },
  { label: '离线', value: 3 }, { label: '故障', value: 4 }
]
const recordStatus = [{ label: '进行中', value: 0 }, { label: '已完成', value: 1 }, { label: '异常', value: 2 }]
const pointType = [{ label: '入口', value: 0 }, { label: '停车区', value: 1 }, { label: '充电区', value: 2 }, { label: '服务设施', value: 3 }]
const noticeType = [{ label: '通知', value: 0 }, { label: '维护', value: 1 }, { label: '提示', value: 2 }]
const publishStatus = [{ label: '草稿', value: 0 }, { label: '已发布', value: 1 }, { label: '已下架', value: 2 }]
const feedbackType = [{ label: '建议', value: 0 }, { label: '报错', value: 1 }, { label: '投诉', value: 2 }]
const processStatus = [{ label: '待处理', value: 0 }, { label: '处理中', value: 1 }, { label: '已完成', value: 2 }]

const field = (key, label, extra = {}) => ({ key, label, ...extra })

export const adminModules = {
  users: {
    title: '用户管理', description: '维护普通用户和管理员演示账号。', icon: '👤',
    columns: [field('id', 'ID', { width: 70 }), field('username', '用户名'), field('nickname', '昵称'), field('phone', '手机号'), field('userType', '类型', { options: userType }), field('status', '状态', { options: yesNo })],
    fields: [field('username', '用户名', { required: true }), field('password', '明文密码', { required: true }), field('nickname', '昵称', { required: true }), field('phone', '手机号'), field('email', '邮箱'), field('userType', '用户类型', { type: 'select', options: userType, default: 0 }), field('status', '状态', { type: 'select', options: yesNo, default: 1 })]
  },
  regions: {
    title: '区域管理', description: '维护省、市、区县层级和展示顺序。', icon: '⌖',
    columns: [field('id', 'ID', { width: 70 }), field('regionCode', '区域编码'), field('regionName', '区域名称'), field('parentId', '上级ID'), field('regionLevel', '层级'), field('status', '状态', { options: yesNo })],
    fields: [field('parentId', '上级区域', { type: 'lookup', lookup: 'regions', default: 0, allowZero: true }), field('regionCode', '区域编码', { required: true }), field('regionName', '区域名称', { required: true }), field('regionLevel', '区域层级', { type: 'number', default: 3 }), field('sortNo', '排序', { type: 'number', default: 0 }), field('status', '状态', { type: 'select', options: yesNo, default: 1 })]
  },
  stations: {
    title: '充电站管理', description: '维护站点资料、经纬度，并支持按地址识别坐标。', icon: '⚡', geocode: true,
    columns: [field('id', 'ID', { width: 70 }), field('stationCode', '站点编号'), field('stationName', '站点名称', { minWidth: 180 }), field('regionId', '区域', { lookup: 'regions' }), field('operatorName', '运营商'), field('status', '状态', { options: stationStatus })],
    fields: [field('regionId', '所属区域', { type: 'lookup', lookup: 'regions', required: true }), field('stationCode', '站点编号', { required: true }), field('stationName', '站点名称', { required: true }), field('address', '详细地址', { required: true, span: 2 }), field('longitude', '经度', { type: 'number', required: true, precision: 6 }), field('latitude', '纬度', { type: 'number', required: true, precision: 6 }), field('stationType', '站点类型', { type: 'select', options: stationType, default: 0 }), field('openTime', '营业时间', { default: '00:00-24:00' }), field('operatorName', '运营商'), field('status', '运营状态', { type: 'select', options: stationStatus, default: 1 }), field('parkingDesc', '停车说明', { type: 'textarea', span: 2 }), field('feeDesc', '收费说明', { type: 'textarea', span: 2 }), field('serviceFacilities', '服务设施', { span: 2 })]
  },
  piles: {
    title: '充电桩管理', description: '维护站点下的快充和慢充设备。', icon: '🔌',
    columns: [field('id', 'ID', { width: 70 }), field('pileCode', '桩编号'), field('pileName', '名称'), field('stationId', '所属站点', { lookup: 'stations' }), field('connectorType', '接口', { options: connectorType }), field('ratedPower', '功率(kW)'), field('enableStatus', '状态', { options: yesNo })],
    fields: [field('stationId', '所属站点', { type: 'lookup', lookup: 'stations', required: true }), field('pileCode', '充电桩编号', { required: true }), field('pileName', '充电桩名称', { required: true }), field('connectorType', '接口类型', { type: 'select', options: connectorType, default: 0 }), field('ratedPower', '额定功率(kW)', { type: 'number', required: true, precision: 2 }), field('manufacturer', '生产厂家'), field('installDate', '安装日期', { type: 'date' }), field('enableStatus', '启用状态', { type: 'select', options: yesNo, default: 1 })]
  },
  status: {
    title: '实时状态管理', description: '查看或手工写入充电桩状态快照。', icon: '◉',
    columns: [field('id', 'ID', { width: 70 }), field('pileId', '充电桩', { lookup: 'piles' }), field('workStatus', '工作状态', { options: workStatus }), field('currentPower', '当前功率'), field('alarmCode', '告警码'), field('statusTime', '状态时间', { minWidth: 165 })],
    fields: [field('pileId', '充电桩', { type: 'lookup', lookup: 'piles', required: true }), field('workStatus', '工作状态', { type: 'select', options: workStatus, default: 0 }), field('currentPower', '当前功率', { type: 'number', precision: 2, default: 0 }), field('alarmCode', '告警码'), field('statusTime', '状态时间', { type: 'datetime' }), field('sourceType', '来源类型', { type: 'select', options: [{ label: '模拟', value: 0 }, { label: '导入', value: 1 }, { label: '接口', value: 2 }], default: 0 })]
  },
  'usage-records': {
    title: '使用记录管理', description: '维护模拟充电起止时间、电量和费用。', icon: '▤',
    columns: [field('id', 'ID', { width: 70 }), field('pileId', '充电桩', { lookup: 'piles' }), field('userId', '用户', { lookup: 'users' }), field('startTime', '开始时间', { minWidth: 165 }), field('durationMin', '时长(分)'), field('energyKwh', '电量(kWh)'), field('totalAmount', '金额'), field('recordStatus', '状态', { options: recordStatus })],
    fields: [field('pileId', '充电桩', { type: 'lookup', lookup: 'piles', required: true }), field('userId', '用户', { type: 'lookup', lookup: 'users' }), field('startTime', '开始时间', { type: 'datetime', required: true }), field('endTime', '结束时间', { type: 'datetime' }), field('durationMin', '时长(分钟)', { type: 'number' }), field('energyKwh', '充电量(kWh)', { type: 'number', precision: 2 }), field('serviceFee', '服务费', { type: 'number', precision: 2 }), field('totalAmount', '总金额', { type: 'number', precision: 2 }), field('recordStatus', '记录状态', { type: 'select', options: recordStatus, default: 1 })]
  },
  statistics: {
    title: '区域统计管理', description: '维护每日区域聚合快照，支持重新生成。', icon: '▥', regenerate: true,
    columns: [field('id', 'ID', { width: 70 }), field('regionId', '区域', { lookup: 'regions' }), field('statDate', '统计日期'), field('stationCount', '站点'), field('pileCount', '充电桩'), field('freeCount', '空闲'), field('usingCount', '使用中'), field('faultCount', '故障'), field('usageRate', '使用率(%)')],
    fields: [field('regionId', '区域', { type: 'lookup', lookup: 'regions', required: true }), field('statDate', '统计日期', { type: 'date', required: true }), field('stationCount', '站点数', { type: 'number', default: 0 }), field('pileCount', '充电桩数', { type: 'number', default: 0 }), field('freeCount', '空闲数', { type: 'number', default: 0 }), field('usingCount', '使用中', { type: 'number', default: 0 }), field('faultCount', '故障数', { type: 'number', default: 0 }), field('usageCount', '使用次数', { type: 'number', default: 0 }), field('energyKwh', '充电量(kWh)', { type: 'number', precision: 2, default: 0 }), field('usageRate', '使用率(%)', { type: 'number', precision: 2, default: 0 })]
  },
  'guide-points': {
    title: '导览点管理', description: '维护站内平面图的比例坐标和说明。', icon: '◆',
    columns: [field('id', 'ID', { width: 70 }), field('stationId', '所属站点', { lookup: 'stations' }), field('pointName', '导览点'), field('pointType', '类型', { options: pointType }), field('xRatio', 'X比例'), field('yRatio', 'Y比例'), field('sortNo', '排序'), field('status', '状态', { options: yesNo })],
    fields: [field('stationId', '所属站点', { type: 'lookup', lookup: 'stations', required: true }), field('pointName', '导览点名称', { required: true }), field('pointType', '导览点类型', { type: 'select', options: pointType, default: 0 }), field('xRatio', 'X比例(0-1)', { type: 'number', precision: 4, required: true }), field('yRatio', 'Y比例(0-1)', { type: 'number', precision: 4, required: true }), field('sortNo', '排序', { type: 'number', default: 0 }), field('status', '状态', { type: 'select', options: yesNo, default: 1 }), field('description', '导览说明', { type: 'textarea', span: 2 })]
  },
  notices: {
    title: '公告管理', description: '维护公告草稿、发布和下架状态。', icon: '✦',
    columns: [field('id', 'ID', { width: 70 }), field('title', '标题', { minWidth: 220 }), field('noticeType', '类型', { options: noticeType }), field('publishStatus', '发布状态', { options: publishStatus }), field('publishTime', '发布时间', { minWidth: 165 })],
    fields: [field('title', '公告标题', { required: true, span: 2 }), field('noticeType', '公告类型', { type: 'select', options: noticeType, default: 0 }), field('publishStatus', '发布状态', { type: 'select', options: publishStatus, default: 0 }), field('publishTime', '发布时间', { type: 'datetime' }), field('createBy', '创建人', { type: 'lookup', lookup: 'users', default: 1 }), field('content', '公告内容', { type: 'textarea', required: true, span: 2 })]
  },
  feedback: {
    title: '反馈处理', description: '查看用户建议、报错和投诉，并填写管理员回复。', icon: '✉',
    columns: [field('id', 'ID', { width: 70 }), field('title', '标题', { minWidth: 220 }), field('userId', '用户', { lookup: 'users' }), field('feedbackType', '类型', { options: feedbackType }), field('processStatus', '处理状态', { options: processStatus }), field('createTime', '提交时间', { minWidth: 165 })],
    fields: [field('userId', '用户', { type: 'lookup', lookup: 'users' }), field('feedbackType', '反馈类型', { type: 'select', options: feedbackType, default: 0 }), field('title', '反馈标题', { required: true, span: 2 }), field('content', '反馈内容', { type: 'textarea', required: true, span: 2 }), field('contact', '联系方式'), field('processStatus', '处理状态', { type: 'select', options: processStatus, default: 0 }), field('replyContent', '管理员回复', { type: 'textarea', span: 2 })]
  }
}

export const moduleMenus = Object.entries(adminModules).map(([key, value]) => ({ key, ...value }))

export const lookupLabelKeys = {
  users: ['nickname', 'username'], regions: ['regionName', 'regionCode'],
  stations: ['stationName', 'stationCode'], piles: ['pileName', 'pileCode']
}
