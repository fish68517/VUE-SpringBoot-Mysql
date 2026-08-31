import type { ResourceConfig } from '@/types/system'

const statusOptions = [
  { label: '启用', value: 1, tagType: 'success' as const },
  { label: '停用', value: 0, tagType: 'info' as const },
]

const yesNoOptions = [
  { label: '是', value: 1, tagType: 'success' as const },
  { label: '否', value: 0, tagType: 'info' as const },
]

const channelOptions = [
  { label: '渝快政', value: 'ykz', tagType: 'primary' as const },
  { label: '渝快办', value: 'ykb', tagType: 'success' as const },
  { label: '通用渠道', value: 'common', tagType: 'info' as const },
]

const commonTail = [
  { prop: 'status', label: '状态', kind: 'select' as const, options: statusOptions, width: 100 },
  { prop: 'createTime', label: '创建时间', kind: 'datetime' as const, minWidth: 168, hideInForm: true },
]

export const resourceConfigs: Record<string, ResourceConfig> = {
  user: {
    key: 'user', module: 'system', resource: 'user', title: '用户管理',
    description: '维护平台登录用户、组织归属、角色与账号状态。', permissionPrefix: 'system:user',
    primaryLabel: '用户', keywordPlaceholder: '请输入登录名、姓名或手机号',
    fields: [
      { prop: 'username', label: '登录名称', required: true, searchable: true, minWidth: 130 },
      { prop: 'realName', label: '用户姓名', required: true, searchable: true, minWidth: 120 },
      { prop: 'deptName', label: '所属部门', kind: 'select', searchable: true, minWidth: 130, options: [
        { label: '法治建设处', value: '法治建设处' }, { label: '执法监督处', value: '执法监督处' },
        { label: '公共法律服务处', value: '公共法律服务处' }, { label: '系统管理部', value: '系统管理部' },
      ] },
      { prop: 'roleName', label: '角色', kind: 'select', minWidth: 130, options: [
        { label: '超级管理员', value: '超级管理员' }, { label: '平台管理员', value: '平台管理员' },
        { label: '业务审核员', value: '业务审核员' }, { label: '普通用户', value: '普通用户' },
      ] },
      { prop: 'phone', label: '手机号码', minWidth: 130 }, ...commonTail,
    ],
  },
  role: {
    key: 'role', module: 'system', resource: 'role', title: '角色管理',
    description: '配置角色权限、数据范围和成员归属。', permissionPrefix: 'system:role',
    primaryLabel: '角色', keywordPlaceholder: '请输入角色名称或编码',
    fields: [
      { prop: 'roleName', label: '角色名称', required: true, searchable: true, minWidth: 160 },
      { prop: 'roleCode', label: '权限字符', required: true, searchable: true, minWidth: 170 },
      { prop: 'dataScope', label: '数据范围', kind: 'select', minWidth: 140, options: [
        { label: '全部数据', value: '全部数据' }, { label: '本部门及以下', value: '本部门及以下' },
        { label: '仅本人数据', value: '仅本人数据' }, { label: '自定义', value: '自定义' },
      ] },
      { prop: 'memberCount', label: '成员数', kind: 'number', width: 100 },
      { prop: 'remark', label: '备注', kind: 'textarea', minWidth: 180 }, ...commonTail,
    ],
  },
  menu: {
    key: 'menu', module: 'system', resource: 'menu', title: '菜单管理',
    description: '维护管理端菜单层级、路由地址与按钮权限标识。', permissionPrefix: 'system:menu',
    primaryLabel: '菜单', keywordPlaceholder: '请输入菜单名称或权限标识',
    fields: [
      { prop: 'menuName', label: '菜单名称', required: true, searchable: true, minWidth: 170 },
      { prop: 'menuType', label: '类型', kind: 'select', width: 100, options: [
        { label: '目录', value: '目录', tagType: 'primary' }, { label: '菜单', value: '菜单', tagType: 'success' },
        { label: '按钮', value: '按钮', tagType: 'warning' },
      ] },
      { prop: 'routePath', label: '路由地址', minWidth: 180 },
      { prop: 'permission', label: '权限标识', searchable: true, minWidth: 190 },
      { prop: 'sort', label: '排序', kind: 'number', width: 80 }, ...commonTail,
    ],
  },
  dept: {
    key: 'dept', module: 'system', resource: 'dept', title: '部门管理',
    description: '维护组织机构层级、负责人和有效状态。', permissionPrefix: 'system:dept',
    primaryLabel: '部门', keywordPlaceholder: '请输入部门名称或负责人',
    fields: [
      { prop: 'deptName', label: '部门名称', required: true, searchable: true, minWidth: 180 },
      { prop: 'deptCode', label: '部门编码', required: true, minWidth: 150 },
      { prop: 'parentName', label: '上级部门', minWidth: 160 },
      { prop: 'leader', label: '负责人', searchable: true, minWidth: 120 },
      { prop: 'phone', label: '联系电话', minWidth: 130 },
      { prop: 'sort', label: '排序', kind: 'number', width: 80 }, ...commonTail,
    ],
  },
  permission: {
    key: 'permission', module: 'system', resource: 'permission', title: '权限管理',
    description: '集中查看业务资源与操作权限编码。', permissionPrefix: 'system:permission',
    primaryLabel: '权限', keywordPlaceholder: '请输入权限名称或编码',
    fields: [
      { prop: 'permissionName', label: '权限名称', required: true, searchable: true, minWidth: 180 },
      { prop: 'permissionCode', label: '权限编码', required: true, searchable: true, minWidth: 220 },
      { prop: 'moduleName', label: '所属模块', kind: 'select', minWidth: 130, options: [
        { label: '系统管理', value: '系统管理' }, { label: '接入管理', value: '接入管理' },
        { label: '事件中心', value: '事件中心' }, { label: '消息中心', value: '消息中心' },
      ] },
      { prop: 'resourceType', label: '资源类型', minWidth: 110 }, ...commonTail,
    ],
  },
  dataScope: {
    key: 'dataScope', module: 'system', resource: 'data-scope', title: '数据权限',
    description: '配置角色可访问的组织与业务数据范围。', permissionPrefix: 'system:dataScope',
    primaryLabel: '数据权限', keywordPlaceholder: '请输入规则名称或角色',
    fields: [
      { prop: 'scopeName', label: '规则名称', required: true, searchable: true, minWidth: 180 },
      { prop: 'roleName', label: '适用角色', required: true, searchable: true, minWidth: 150 },
      { prop: 'scopeType', label: '范围类型', kind: 'select', minWidth: 150, options: [
        { label: '全部数据', value: '全部数据' }, { label: '本部门及以下', value: '本部门及以下' },
        { label: '指定部门', value: '指定部门' }, { label: '仅本人', value: '仅本人' },
      ] },
      { prop: 'deptNames', label: '授权部门', minWidth: 240 }, ...commonTail,
    ],
  },
  client: {
    key: 'client', module: 'access', resource: 'client', title: '三方接入方',
    description: '管理接入主体、渠道、联系人和凭证有效期。', permissionPrefix: 'access:client',
    primaryLabel: '接入方', keywordPlaceholder: '请输入接入方名称、App ID 或联系人',
    fields: [
      { prop: 'appName', label: '接入方名称', required: true, searchable: true, minWidth: 180 },
      { prop: 'appId', label: 'App ID', required: true, searchable: true, minWidth: 190 },
      { prop: 'channel', label: '接入渠道', kind: 'select', searchable: true, options: channelOptions, width: 110 },
      { prop: 'contact', label: '联系人', minWidth: 110 },
      { prop: 'phone', label: '联系电话', minWidth: 130 },
      { prop: 'expireTime', label: '有效期至', kind: 'date', minWidth: 130 }, ...commonTail,
    ],
  },
  application: {
    key: 'application', module: 'access', resource: 'application', title: '应用管理',
    description: '维护接入应用、所属接入方与应用场景。', permissionPrefix: 'access:application',
    primaryLabel: '应用', keywordPlaceholder: '请输入应用名称或编码',
    fields: [
      { prop: 'applicationName', label: '应用名称', required: true, searchable: true, minWidth: 180 },
      { prop: 'applicationCode', label: '应用编码', required: true, searchable: true, minWidth: 170 },
      { prop: 'clientName', label: '所属接入方', minWidth: 180 },
      { prop: 'channel', label: '渠道', kind: 'select', options: channelOptions, width: 110 },
      { prop: 'scene', label: '应用场景', minWidth: 180 }, ...commonTail,
    ],
  },
  apiGrant: {
    key: 'apiGrant', module: 'access', resource: 'api-grant', title: 'API 授权',
    description: '按接入方配置 API、Scope、限流和数据范围。', permissionPrefix: 'access:grant',
    primaryLabel: 'API 授权', keywordPlaceholder: '请输入 API 编码、路径或接入方',
    fields: [
      { prop: 'clientName', label: '接入方', required: true, searchable: true, minWidth: 170 },
      { prop: 'apiCode', label: 'API 编码', required: true, searchable: true, minWidth: 190 },
      { prop: 'path', label: '请求路径', searchable: true, minWidth: 260 },
      { prop: 'scope', label: 'Scope', minWidth: 150 },
      { prop: 'rateLimit', label: '限流/分钟', kind: 'number', width: 105 }, ...commonTail,
    ],
  },
  ipWhitelist: {
    key: 'ipWhitelist', module: 'access', resource: 'ip-whitelist', title: 'IP 白名单',
    description: '维护三方接入来源 IP 或 CIDR 网段。', permissionPrefix: 'access:ip',
    primaryLabel: '白名单', keywordPlaceholder: '请输入接入方或 IP/CIDR',
    fields: [
      { prop: 'clientName', label: '接入方', required: true, searchable: true, minWidth: 190 },
      { prop: 'ipCidr', label: 'IP / CIDR', required: true, searchable: true, minWidth: 180 },
      { prop: 'environment', label: '环境', kind: 'select', width: 110, options: [
        { label: '生产', value: '生产', tagType: 'danger' }, { label: '测试', value: '测试', tagType: 'warning' },
      ] },
      { prop: 'remark', label: '用途说明', kind: 'textarea', minWidth: 240 }, ...commonTail,
    ],
  },
  authExempt: {
    key: 'authExempt', module: 'access', resource: 'auth-exempt', title: '免认证接口',
    description: '精确配置方法、路径、认证模式、风险等级与有效期。', permissionPrefix: 'access:exempt',
    primaryLabel: '免认证接口', keywordPlaceholder: '请输入路径、负责人或认证模式',
    fields: [
      { prop: 'method', label: '方法', kind: 'select', width: 90, options: [
        { label: 'GET', value: 'GET', tagType: 'success' }, { label: 'POST', value: 'POST', tagType: 'warning' },
      ] },
      { prop: 'path', label: '接口路径', required: true, searchable: true, minWidth: 290 },
      { prop: 'authMode', label: '认证模式', kind: 'select', minWidth: 140, options: [
        { label: '完全匿名', value: 'anonymous' }, { label: '开放签名', value: 'open-sign' },
        { label: '外部登录', value: 'external-login' }, { label: '内部信任', value: 'internal' },
      ] },
      { prop: 'riskLevel', label: '风险等级', kind: 'select', width: 110, options: [
        { label: '低', value: '低', tagType: 'success' }, { label: '中', value: '中', tagType: 'warning' },
        { label: '高', value: '高', tagType: 'danger' },
      ] },
      { prop: 'owner', label: '负责人', searchable: true, minWidth: 110 },
      { prop: 'expireTime', label: '有效期至', kind: 'date', minWidth: 130 }, ...commonTail,
    ],
  },
  event: {
    key: 'event', module: 'event', resource: 'event', title: '事件列表',
    description: '统一查看、筛选和跟踪平台业务事件。', permissionPrefix: 'event:event',
    primaryLabel: '事件', keywordPlaceholder: '请输入事件编号、标题或发起人',
    fields: [
      { prop: 'eventNo', label: '事件编号', required: true, searchable: true, minWidth: 170 },
      { prop: 'title', label: '事件标题', required: true, searchable: true, minWidth: 220 },
      { prop: 'category', label: '事件类型', kind: 'select', searchable: true, minWidth: 130, options: [
        { label: '行政执法监督', value: '行政执法监督' }, { label: '法治督察', value: '法治督察' },
        { label: '行政复议', value: '行政复议' }, { label: '公共法律服务', value: '公共法律服务' },
      ] },
      { prop: 'priority', label: '优先级', kind: 'select', width: 100, options: [
        { label: '紧急', value: '紧急', tagType: 'danger' }, { label: '高', value: '高', tagType: 'warning' },
        { label: '普通', value: '普通', tagType: 'info' },
      ] },
      { prop: 'processStatus', label: '办理状态', kind: 'select', minWidth: 110, options: [
        { label: '待受理', value: '待受理', tagType: 'warning' }, { label: '办理中', value: '办理中', tagType: 'primary' },
        { label: '已办结', value: '已办结', tagType: 'success' },
      ] },
      { prop: 'initiator', label: '发起人', minWidth: 110 },
      { prop: 'createTime', label: '发起时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  todo: {
    key: 'todo', module: 'event', resource: 'todo', title: '我的待办',
    description: '聚合当前用户待处理的流程任务。', permissionPrefix: 'event:todo',
    primaryLabel: '待办', keywordPlaceholder: '请输入任务标题或事件编号', readOnly: true,
    fields: [
      { prop: 'eventNo', label: '事件编号', searchable: true, minWidth: 170 },
      { prop: 'taskName', label: '当前任务', required: true, searchable: true, minWidth: 210 },
      { prop: 'eventTitle', label: '事件标题', minWidth: 220 },
      { prop: 'initiator', label: '发起人', minWidth: 100 },
      { prop: 'priority', label: '优先级', minWidth: 90 },
      { prop: 'deadline', label: '截止时间', kind: 'datetime', minWidth: 168 },
      { prop: 'createTime', label: '到达时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  processRecord: {
    key: 'processRecord', module: 'event', resource: 'process-record', title: '流程记录',
    description: '查询事件流程节点、办理意见与流转结果。', permissionPrefix: 'event:process',
    primaryLabel: '流程记录', keywordPlaceholder: '请输入事件编号、节点或办理人', readOnly: true,
    fields: [
      { prop: 'eventNo', label: '事件编号', searchable: true, minWidth: 170 },
      { prop: 'nodeName', label: '流程节点', searchable: true, minWidth: 160 },
      { prop: 'operator', label: '办理人', searchable: true, minWidth: 110 },
      { prop: 'action', label: '办理动作', minWidth: 110 },
      { prop: 'opinion', label: '办理意见', minWidth: 260 },
      { prop: 'duration', label: '办理耗时', minWidth: 110 },
      { prop: 'createTime', label: '办理时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  message: {
    key: 'message', module: 'message', resource: 'message', title: '消息列表',
    description: '查看平台通知、业务提醒和系统公告。', permissionPrefix: 'message:message',
    primaryLabel: '消息', keywordPlaceholder: '请输入消息标题、类型或接收人',
    fields: [
      { prop: 'title', label: '消息标题', required: true, searchable: true, minWidth: 230 },
      { prop: 'messageType', label: '消息类型', kind: 'select', searchable: true, minWidth: 110, options: [
        { label: '业务提醒', value: '业务提醒', tagType: 'warning' }, { label: '系统通知', value: '系统通知', tagType: 'primary' },
        { label: '安全告警', value: '安全告警', tagType: 'danger' },
      ] },
      { prop: 'receiver', label: '接收人', minWidth: 120 },
      { prop: 'channel', label: '发送渠道', minWidth: 110 },
      { prop: 'readStatus', label: '阅读状态', minWidth: 100 },
      { prop: 'createTime', label: '发送时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  messageTemplate: {
    key: 'messageTemplate', module: 'message', resource: 'template', title: '通知模板',
    description: '维护站内信、短信和流程通知模板。', permissionPrefix: 'message:template',
    primaryLabel: '模板', keywordPlaceholder: '请输入模板名称或编码',
    fields: [
      { prop: 'templateName', label: '模板名称', required: true, searchable: true, minWidth: 190 },
      { prop: 'templateCode', label: '模板编码', required: true, searchable: true, minWidth: 180 },
      { prop: 'channel', label: '渠道', kind: 'select', minWidth: 110, options: [
        { label: '站内信', value: '站内信' }, { label: '短信', value: '短信' }, { label: '邮件', value: '邮件' },
      ] },
      { prop: 'subject', label: '消息主题', minWidth: 220 },
      { prop: 'content', label: '模板内容', kind: 'textarea', minWidth: 260, hideInTable: true }, ...commonTail,
    ],
  },
  internalMessage: {
    key: 'internalMessage', module: 'message', resource: 'internal', title: '站内消息',
    description: '创建并发布面向用户或角色的站内消息。', permissionPrefix: 'message:internal',
    primaryLabel: '站内消息', keywordPlaceholder: '请输入标题或接收范围',
    fields: [
      { prop: 'title', label: '标题', required: true, searchable: true, minWidth: 230 },
      { prop: 'receiverScope', label: '接收范围', searchable: true, minWidth: 170 },
      { prop: 'content', label: '消息内容', kind: 'textarea', minWidth: 260, hideInTable: true },
      { prop: 'sendStatus', label: '发送状态', minWidth: 110 },
      { prop: 'readCount', label: '已读/总数', minWidth: 110 },
      { prop: 'createTime', label: '创建时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  sendRecord: {
    key: 'sendRecord', module: 'message', resource: 'send-record', title: '发送记录',
    description: '查询消息发送结果、渠道回执和失败原因。', permissionPrefix: 'message:record',
    primaryLabel: '发送记录', keywordPlaceholder: '请输入消息标题、接收人或流水号', readOnly: true,
    fields: [
      { prop: 'serialNo', label: '发送流水号', searchable: true, minWidth: 190 },
      { prop: 'title', label: '消息标题', searchable: true, minWidth: 220 },
      { prop: 'receiver', label: '接收人', searchable: true, minWidth: 120 },
      { prop: 'channel', label: '渠道', minWidth: 100 },
      { prop: 'sendStatus', label: '发送结果', minWidth: 110 },
      { prop: 'failureReason', label: '失败原因', minWidth: 180 },
      { prop: 'createTime', label: '发送时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  job: {
    key: 'job', module: 'job', resource: 'job', title: '任务管理',
    description: '维护定时任务、执行器、Cron 表达式和运行状态。', permissionPrefix: 'job:job',
    primaryLabel: '任务', keywordPlaceholder: '请输入任务名称、编码或执行器',
    fields: [
      { prop: 'jobName', label: '任务名称', required: true, searchable: true, minWidth: 190 },
      { prop: 'jobCode', label: '任务编码', required: true, searchable: true, minWidth: 180 },
      { prop: 'executor', label: '执行器', searchable: true, minWidth: 160 },
      { prop: 'cron', label: 'Cron 表达式', minWidth: 150 },
      { prop: 'lastRunTime', label: '最近执行', kind: 'datetime', minWidth: 168 }, ...commonTail,
    ],
  },
  jobRecord: {
    key: 'jobRecord', module: 'job', resource: 'record', title: '执行记录',
    description: '查询定时任务执行结果、耗时和调度信息。', permissionPrefix: 'job:record',
    primaryLabel: '执行记录', keywordPlaceholder: '请输入任务名称、流水号或执行器', readOnly: true,
    fields: [
      { prop: 'serialNo', label: '执行流水号', searchable: true, minWidth: 190 },
      { prop: 'jobName', label: '任务名称', searchable: true, minWidth: 190 },
      { prop: 'executor', label: '执行器', minWidth: 150 },
      { prop: 'runStatus', label: '执行状态', minWidth: 110 },
      { prop: 'duration', label: '执行耗时', minWidth: 100 },
      { prop: 'result', label: '执行结果', minWidth: 210 },
      { prop: 'createTime', label: '开始时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  loginLog: {
    key: 'loginLog', module: 'log', resource: 'login', title: '登录日志',
    description: '审计用户登录结果、来源地址和客户端信息。', permissionPrefix: 'log:login',
    primaryLabel: '登录日志', keywordPlaceholder: '请输入登录名、IP 或结果', readOnly: true,
    fields: [
      { prop: 'username', label: '登录名称', searchable: true, minWidth: 130 },
      { prop: 'ip', label: '登录地址', searchable: true, minWidth: 140 },
      { prop: 'location', label: '登录地点', minWidth: 140 },
      { prop: 'browser', label: '浏览器', minWidth: 130 },
      { prop: 'os', label: '操作系统', minWidth: 130 },
      { prop: 'result', label: '登录结果', minWidth: 100 },
      { prop: 'createTime', label: '登录时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  operationLog: {
    key: 'operationLog', module: 'log', resource: 'operation', title: '操作日志',
    description: '审计写操作、敏感操作、请求耗时与结果。', permissionPrefix: 'log:operation',
    primaryLabel: '操作日志', keywordPlaceholder: '请输入模块、操作人或请求地址', readOnly: true,
    fields: [
      { prop: 'moduleName', label: '业务模块', searchable: true, minWidth: 140 },
      { prop: 'operation', label: '操作内容', minWidth: 180 },
      { prop: 'operator', label: '操作人', searchable: true, minWidth: 110 },
      { prop: 'requestUrl', label: '请求地址', searchable: true, minWidth: 240 },
      { prop: 'method', label: '方法', width: 90 },
      { prop: 'duration', label: '耗时', width: 90 },
      { prop: 'result', label: '结果', width: 90 },
      { prop: 'createTime', label: '操作时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
  exceptionLog: {
    key: 'exceptionLog', module: 'log', resource: 'exception', title: '异常日志',
    description: '集中查看接口异常、链路标识与处置状态。', permissionPrefix: 'log:exception',
    primaryLabel: '异常日志', keywordPlaceholder: '请输入异常类型、接口或 Trace ID', readOnly: true,
    fields: [
      { prop: 'exceptionType', label: '异常类型', searchable: true, minWidth: 190 },
      { prop: 'message', label: '异常摘要', minWidth: 260 },
      { prop: 'requestUrl', label: '请求地址', searchable: true, minWidth: 240 },
      { prop: 'traceId', label: 'Trace ID', searchable: true, minWidth: 180 },
      { prop: 'handled', label: '处置状态', kind: 'select', options: yesNoOptions, width: 105 },
      { prop: 'createTime', label: '发生时间', kind: 'datetime', minWidth: 168, hideInForm: true },
    ],
  },
}
