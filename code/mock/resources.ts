import type { MockMethod } from 'vite-plugin-mock'
import Mock from 'mockjs'

type Row = { id: number; status?: number; createTime?: string; [key: string]: unknown }

const now = (index: number) => `2026-08-${String(26 - (index % 8)).padStart(2, '0')} ${String(9 + (index % 9)).padStart(2, '0')}:${String(12 + index).padStart(2, '0')}:00`
const expand = (base: Row[], count = 18): Row[] => Array.from({ length: count }, (_, index) => {
  const seed = base[index % base.length]!
  return { ...seed, id: Number(seed.id) + index, createTime: seed.createTime || now(index) }
})

const resources: Record<string, Row[]> = {
  'system/user': expand([
    { id: 10001, username: 'admin', realName: '系统管理员', deptName: '系统管理部', roleName: '超级管理员', phone: '138****8820', status: 1 },
    { id: 10021, username: 'law_supervisor', realName: '王海峰', deptName: '执法监督处', roleName: '业务审核员', phone: '139****5268', status: 1 },
    { id: 10041, username: 'review_chen', realName: '陈思远', deptName: '行政复议处', roleName: '普通用户', phone: '136****1093', status: 1 },
    { id: 10061, username: 'service_liu', realName: '刘明宇', deptName: '公共法律服务处', roleName: '平台管理员', phone: '158****7315', status: 0 },
  ], 24),
  'system/role': expand([
    { id: 20001, roleName: '超级管理员', roleCode: 'super_admin', dataScope: '全部数据', memberCount: 3, remark: '平台最高管理权限', status: 1 },
    { id: 20021, roleName: '平台管理员', roleCode: 'platform_admin', dataScope: '全部数据', memberCount: 8, remark: '平台日常运维管理', status: 1 },
    { id: 20041, roleName: '业务审核员', roleCode: 'business_auditor', dataScope: '本部门及以下', memberCount: 16, remark: '事件业务审核', status: 1 },
    { id: 20061, roleName: '普通用户', roleCode: 'general_user', dataScope: '仅本人数据', memberCount: 126, remark: '平台普通业务用户', status: 1 },
  ], 16),
  'system/menu': expand([
    { id: 30001, menuName: '系统管理', menuType: '目录', routePath: '/system', permission: '', sort: 2, status: 1 },
    { id: 30021, menuName: '用户管理', menuType: '菜单', routePath: '/system/user', permission: 'system:user:list', sort: 1, status: 1 },
    { id: 30041, menuName: '新增用户', menuType: '按钮', routePath: '', permission: 'system:user:add', sort: 2, status: 1 },
    { id: 30061, menuName: '事件中心', menuType: '目录', routePath: '/event', permission: '', sort: 4, status: 1 },
  ], 26),
  'system/dept': expand([
    { id: 40001, deptName: '市司法局', deptCode: 'SFJ', parentName: '-', leader: '张勇', phone: '023-6708****', sort: 1, status: 1 },
    { id: 40021, deptName: '法治建设处', deptCode: 'FZJSC', parentName: '市司法局', leader: '李文军', phone: '023-6708****', sort: 2, status: 1 },
    { id: 40041, deptName: '执法监督处', deptCode: 'ZFJDC', parentName: '市司法局', leader: '王海峰', phone: '023-6708****', sort: 3, status: 1 },
    { id: 40061, deptName: '公共法律服务处', deptCode: 'GGFLFWC', parentName: '市司法局', leader: '刘明宇', phone: '023-6708****', sort: 4, status: 1 },
  ], 15),
  'system/permission': expand([
    { id: 50001, permissionName: '用户列表查询', permissionCode: 'system:user:list', moduleName: '系统管理', resourceType: '菜单', status: 1 },
    { id: 50021, permissionName: '新增用户', permissionCode: 'system:user:add', moduleName: '系统管理', resourceType: '按钮', status: 1 },
    { id: 50041, permissionName: '事件列表查询', permissionCode: 'event:event:list', moduleName: '事件中心', resourceType: '菜单', status: 1 },
    { id: 50061, permissionName: '接入方授权', permissionCode: 'access:grant:update', moduleName: '接入管理', resourceType: '按钮', status: 1 },
  ], 28),
  'system/data-scope': expand([
    { id: 60001, scopeName: '平台全量数据', roleName: '超级管理员', scopeType: '全部数据', deptNames: '全部部门', status: 1 },
    { id: 60021, scopeName: '处室业务数据', roleName: '业务审核员', scopeType: '本部门及以下', deptNames: '用户所属部门', status: 1 },
    { id: 60041, scopeName: '法治建设专项', roleName: '法治建设专员', scopeType: '指定部门', deptNames: '法治建设处、执法监督处', status: 1 },
    { id: 60061, scopeName: '个人经办数据', roleName: '普通用户', scopeType: '仅本人', deptNames: '-', status: 1 },
  ], 14),
  'access/client': expand([
    { id: 70001, appName: '渝快政统一工作台', appId: 'ykz-workbench-prod', channel: 'ykz', contact: '周老师', phone: '139****2018', expireTime: '2027-08-26', status: 1 },
    { id: 70021, appName: '渝快办群众端', appId: 'ykb-citizen-prod', channel: 'ykb', contact: '吴老师', phone: '136****4412', expireTime: '2027-06-30', status: 1 },
    { id: 70041, appName: '市级执法监督平台', appId: 'law-enforce-sync', channel: 'common', contact: '黄工', phone: '158****3619', expireTime: '2026-12-31', status: 1 },
    { id: 70061, appName: '历史数据迁移应用', appId: 'legacy-migrate', channel: 'common', contact: '赵工', phone: '137****0086', expireTime: '2026-09-30', status: 0 },
  ], 14),
  'access/application': expand([
    { id: 71001, applicationName: '统一待办同步', applicationCode: 'TODO_SYNC', clientName: '渝快政统一工作台', channel: 'ykz', scene: '政务工作台统一待办', status: 1 },
    { id: 71021, applicationName: '外部身份登录', applicationCode: 'EXTERNAL_LOGIN', clientName: '渝快办群众端', channel: 'ykb', scene: '群众端免密登录', status: 1 },
    { id: 71041, applicationName: '执法数据交换', applicationCode: 'ENFORCE_EXCHANGE', clientName: '市级执法监督平台', channel: 'common', scene: '执法数据双向同步', status: 1 },
  ], 12),
  'access/api-grant': expand([
    { id: 72001, clientName: '渝快政统一工作台', apiCode: 'EVENT_TASK_PUSH', path: '/api/open/v1/ykz/event/task/push', scope: 'event.task.write', rateLimit: 120, status: 1 },
    { id: 72021, clientName: '渝快办群众端', apiCode: 'EXTERNAL_LOGIN', path: '/api/h5/v1/auth/ykb/login', scope: 'auth.external', rateLimit: 300, status: 1 },
    { id: 72041, clientName: '市级执法监督平台', apiCode: 'USER_SYNC', path: '/api/open/v1/common/system/user/sync', scope: 'system.user.write', rateLimit: 60, status: 1 },
  ], 18),
  'access/ip-whitelist': expand([
    { id: 73001, clientName: '渝快政统一工作台', ipCidr: '10.64.18.0/24', environment: '生产', remark: '渝快政生产出口网段', status: 1 },
    { id: 73021, clientName: '渝快办群众端', ipCidr: '10.72.35.18', environment: '生产', remark: '渝快办网关出口地址', status: 1 },
    { id: 73041, clientName: '市级执法监督平台', ipCidr: '172.20.16.0/28', environment: '测试', remark: '联调测试环境', status: 1 },
  ], 11),
  'access/auth-exempt': expand([
    { id: 74001, method: 'GET', path: '/api/open/v1/common/health', authMode: 'anonymous', riskLevel: '低', owner: '平台运维组', expireTime: '长期', status: 1 },
    { id: 74021, method: 'POST', path: '/api/h5/v1/auth/ykb/login', authMode: 'external-login', riskLevel: '中', owner: '统一认证组', expireTime: '2027-08-26', status: 1 },
    { id: 74041, method: 'POST', path: '/api/open/v1/ykz/event/task/push', authMode: 'open-sign', riskLevel: '中', owner: '事件中心组', expireTime: '2027-06-30', status: 1 },
    { id: 74061, method: 'POST', path: '/api/admin/v1/job/callback', authMode: 'internal', riskLevel: '高', owner: '调度运维组', expireTime: '2026-12-31', status: 1 },
  ], 12),
  'event/event': expand([
    { id: 1001, eventNo: 'SJ202608260018', title: '行政执法案卷评查问题线索核查', category: '行政执法监督', priority: '紧急', processStatus: '办理中', initiator: '系统管理员', source: '平台录入', deptName: '执法监督处', assignee: '刘明宇', deadline: '2026-08-27 16:00:00', description: '核查相关行政执法案卷材料和办理程序。' },
    { id: 1021, eventNo: 'SJ202608260017', title: '法治政府建设年度督察材料补正', category: '法治督察', priority: '高', processStatus: '待受理', initiator: '王海峰', source: '渝快政', deptName: '法治建设处', assignee: '李文军', deadline: '2026-08-28 12:00:00', description: '根据年度督察清单补充完善相关支撑材料。' },
    { id: 1041, eventNo: 'SJ202608250036', title: '行政复议申请材料在线预审', category: '行政复议', priority: '普通', processStatus: '办理中', initiator: '陈思远', source: '渝快办', deptName: '行政复议处', assignee: '陈思远', deadline: '2026-08-29 18:00:00', description: '对申请人在线提交的复议材料进行完整性预审。' },
    { id: 1061, eventNo: 'SJ202608250031', title: '公共法律服务热线协同处置', category: '公共法律服务', priority: '普通', processStatus: '已办结', initiator: '刘明宇', source: 'Open API', deptName: '公共法律服务处', assignee: '赵敏', deadline: '2026-08-26 18:00:00', description: '协调处理公共法律服务热线转办事项。' },
  ], 22),
  'event/todo': expand([
    { id: 11001, eventNo: 'SJ202608260018', taskName: '材料核查', eventTitle: '行政执法案卷评查问题线索核查', initiator: '系统管理员', priority: '紧急', deadline: '2026-08-26 16:00:00' },
    { id: 11021, eventNo: 'SJ202608260017', taskName: '督察材料审核', eventTitle: '法治政府建设年度督察材料补正', initiator: '王海峰', priority: '高', deadline: '2026-08-27 12:00:00' },
    { id: 11041, eventNo: 'SJ202608250036', taskName: '申请材料预审', eventTitle: '行政复议申请材料在线预审', initiator: '陈思远', priority: '普通', deadline: '2026-08-29 18:00:00' },
  ], 12),
  'event/process-record': expand([
    { id: 12001, eventNo: 'SJ202608260018', nodeName: '事件发起', operator: '系统管理员', action: '提交', opinion: '请执法监督处核查办理', duration: '2分钟' },
    { id: 12021, eventNo: 'SJ202608260018', nodeName: '部门受理', operator: '王海峰', action: '受理', opinion: '同意受理，转刘明宇经办', duration: '15分钟' },
    { id: 12041, eventNo: 'SJ202608250031', nodeName: '结果复核', operator: '李文军', action: '通过', opinion: '办理结果符合要求，同意办结', duration: '38分钟' },
  ], 24),
  'message/message': expand([
    { id: 13001, title: '您有新的紧急事件待处理', messageType: '业务提醒', receiver: '刘明宇', channel: '站内信', readStatus: '未读' },
    { id: 13021, title: '接口授权即将到期提醒', messageType: '系统通知', receiver: '系统管理员', channel: '站内信', readStatus: '已读' },
    { id: 13041, title: 'Open API 签名校验连续失败', messageType: '安全告警', receiver: '平台运维组', channel: '短信', readStatus: '未读' },
  ], 20),
  'message/template': expand([
    { id: 14001, templateName: '事件待办提醒', templateCode: 'EVENT_TODO_NOTICE', channel: '站内信', subject: '您有新的事件待处理', content: '事件 ${eventNo} 已到达您的待办，请及时处理。', status: 1 },
    { id: 14021, templateName: '接口到期提醒', templateCode: 'API_EXPIRE_NOTICE', channel: '短信', subject: '接口授权即将到期', content: '接入方 ${clientName} 的接口授权将于 ${date} 到期。', status: 1 },
    { id: 14041, templateName: '安全告警通知', templateCode: 'SECURITY_ALERT', channel: '邮件', subject: '平台安全告警', content: '检测到异常访问，请及时核查。', status: 1 },
  ], 13),
  'message/internal': expand([
    { id: 15001, title: '平台例行维护通知', receiverScope: '全体用户', content: '本周六晚进行例行维护。', sendStatus: '已发送', readCount: '1021/1286' },
    { id: 15021, title: '数据权限年度复核通知', receiverScope: '部门管理员', content: '请于本月底前完成数据权限复核。', sendStatus: '已发送', readCount: '32/48' },
    { id: 15041, title: '新功能上线预告', receiverScope: '业务审核员', content: '事件协同处置功能即将上线。', sendStatus: '待发送', readCount: '0/16' },
  ], 11),
  'message/send-record': expand([
    { id: 16001, serialNo: 'MSG202608260021', title: '您有新的紧急事件待处理', receiver: '刘明宇', channel: '站内信', sendStatus: '成功', failureReason: '-' },
    { id: 16021, serialNo: 'MSG202608260020', title: 'Open API 签名校验连续失败', receiver: '平台运维组', channel: '短信', sendStatus: '成功', failureReason: '-' },
    { id: 16041, serialNo: 'MSG202608250099', title: '接口授权即将到期提醒', receiver: '接入方联系人', channel: '邮件', sendStatus: '失败', failureReason: '接收邮箱地址无效' },
  ], 21),
  'job/job': expand([
    { id: 17001, jobName: '事件超时扫描', jobCode: 'EVENT_TIMEOUT_SCAN', executor: 'event-executor', cron: '0 */5 * * * ?', lastRunTime: '2026-08-26 14:35:00', status: 1 },
    { id: 17021, jobName: '消息补偿发送', jobCode: 'MESSAGE_RETRY', executor: 'message-executor', cron: '0 */10 * * * ?', lastRunTime: '2026-08-26 14:30:00', status: 1 },
    { id: 17041, jobName: '权限缓存版本校验', jobCode: 'PERMISSION_CACHE_CHECK', executor: 'system-executor', cron: '0 0/30 * * * ?', lastRunTime: '2026-08-26 14:30:00', status: 1 },
    { id: 17061, jobName: '历史日志归档', jobCode: 'LOG_ARCHIVE', executor: 'log-executor', cron: '0 0 2 * * ?', lastRunTime: '2026-08-26 02:00:00', status: 0 },
  ], 14),
  'job/record': expand([
    { id: 18001, serialNo: 'JOB20260826143501', jobName: '事件超时扫描', executor: 'event-executor', runStatus: '成功', duration: '1.28s', result: '扫描 342 条，更新 2 条' },
    { id: 18021, serialNo: 'JOB20260826143003', jobName: '消息补偿发送', executor: 'message-executor', runStatus: '成功', duration: '863ms', result: '补偿发送 3 条' },
    { id: 18041, serialNo: 'JOB20260826020008', jobName: '历史日志归档', executor: 'log-executor', runStatus: '失败', duration: '32.4s', result: '对象存储连接超时' },
  ], 28),
  'log/login': expand([
    { id: 19001, username: 'admin', ip: '10.64.18.23', location: '重庆市 政务外网', browser: 'Edge 138', os: 'Windows 11', result: '成功' },
    { id: 19021, username: 'law_supervisor', ip: '10.64.22.16', location: '重庆市 政务外网', browser: 'Chrome 139', os: 'Windows 10', result: '成功' },
    { id: 19041, username: 'unknown_user', ip: '113.204.**.**', location: '重庆市 联通', browser: 'Chrome 137', os: 'Windows 10', result: '失败' },
  ], 32),
  'log/operation': expand([
    { id: 20001, moduleName: '用户管理', operation: '新增用户', operator: '系统管理员', requestUrl: '/api/web/v1/system/user', method: 'POST', duration: '86ms', result: '成功' },
    { id: 20021, moduleName: 'API 授权', operation: '修改接入方授权', operator: '系统管理员', requestUrl: '/api/web/v1/access/api-grant/update', method: 'POST', duration: '103ms', result: '成功' },
    { id: 20041, moduleName: '事件中心', operation: '处理事件', operator: '刘明宇', requestUrl: '/api/web/v1/event/event/update', method: 'POST', duration: '128ms', result: '成功' },
  ], 36),
  'log/exception': expand([
    { id: 21001, exceptionType: 'SocketTimeoutException', message: '调用外部消息通道超时', requestUrl: '/api/web/v1/message/send', traceId: '9fd3a31c7e2b4a89', handled: 1 },
    { id: 21021, exceptionType: 'SignatureVerifyException', message: 'Open API 请求签名校验失败', requestUrl: '/api/open/v1/ykz/event/task/push', traceId: '17bc409d8a2e41f6', handled: 1 },
    { id: 21041, exceptionType: 'ObjectStorageException', message: '附件上传时对象存储连接超时', requestUrl: '/api/web/v1/file/upload', traceId: '8c2e732ab5644db1', handled: 0 },
  ], 17),
}

function normalizeQuery(query: Record<string, string | undefined>) {
  return Object.fromEntries(Object.entries(query || {}).filter(([, value]) => value !== undefined && value !== ''))
}

function endpoints(key: string, initialRows: Row[]): MockMethod[] {
  const [module, resource] = key.split('/') as [string, string]
  const base = `/api/web/v1/${module}/${resource}`
  let rows = initialRows
  return [
    {
      url: `${base}/page`, method: 'get', timeout: 180,
      response: ({ query }: { query: Record<string, string> }) => {
        const params = normalizeQuery(query)
        const pageNum = Number(params.pageNum || 1)
        const pageSize = Number(params.pageSize || 10)
        const controls = new Set(['pageNum', 'pageSize'])
        const filters = Object.entries(params).filter(([name]) => !controls.has(name))
        const filtered = rows.filter((row) => filters.every(([name, value]) => {
          if (name === 'keyword') return Object.values(row).some((cell) => String(cell).toLowerCase().includes(String(value).toLowerCase()))
          return String(row[name] ?? '').toLowerCase().includes(String(value).toLowerCase())
        }))
        const start = (pageNum - 1) * pageSize
        return { code: 200, message: 'success', data: { records: filtered.slice(start, start + pageSize), total: filtered.length, pageNum, pageSize } }
      },
    },
    {
      url: base, method: 'post', timeout: 180,
      response: ({ body }: { body: Row }) => {
        const id = Mock.Random.integer(22000, 99999)
        rows = [{ ...body, id, createTime: now(0) }, ...rows]
        return { code: 200, message: 'success', data: id }
      },
    },
    {
      url: `${base}/update`, method: 'post', timeout: 180,
      response: ({ body }: { body: Row }) => {
        rows = rows.map((row) => row.id === Number(body.id) ? { ...row, ...body } : row)
        return { code: 200, message: 'success', data: null }
      },
    },
    {
      url: `${base}/delete`, method: 'post', timeout: 180,
      response: ({ body }: { body: { id: number } }) => {
        rows = rows.filter((row) => row.id !== Number(body.id))
        return { code: 200, message: 'success', data: null }
      },
    },
    {
      url: `${base}/changeStatus`, method: 'post', timeout: 120,
      response: ({ body }: { body: { id: number; status: number } }) => {
        rows = rows.map((row) => row.id === Number(body.id) ? { ...row, status: Number(body.status) } : row)
        return { code: 200, message: 'success', data: null }
      },
    },
  ]
}

export default Object.entries(resources).flatMap(([key, rows]) => endpoints(key, rows)) as MockMethod[]
