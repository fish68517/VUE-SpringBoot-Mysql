import type { RouteRecordRaw } from 'vue-router'
import {
  Bell,
  Briefcase,
  Calendar,
  Connection,
  DataAnalysis,
  Document,
  House,
  Lock,
  Operation,
  Setting,
  User,
} from '@element-plus/icons-vue'

const resourcePage = () => import('@/views/common/ResourcePage.vue')

export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true, hidden: true },
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: House, menu: true },
      },
      {
        path: 'system',
        name: 'System',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: Setting, menu: true },
        children: [
          { path: 'user', name: 'SystemUser', component: resourcePage, meta: { title: '用户管理', resourceKey: 'user', menu: true } },
          { path: 'role', name: 'SystemRole', component: resourcePage, meta: { title: '角色管理', resourceKey: 'role', menu: true } },
          { path: 'menu', name: 'SystemMenu', component: resourcePage, meta: { title: '菜单管理', resourceKey: 'menu', menu: true } },
          { path: 'dept', name: 'SystemDept', component: resourcePage, meta: { title: '部门管理', resourceKey: 'dept', menu: true } },
          { path: 'permission', name: 'SystemPermission', component: resourcePage, meta: { title: '权限管理', resourceKey: 'permission', menu: true } },
          { path: 'data-scope', name: 'SystemDataScope', component: resourcePage, meta: { title: '数据权限', resourceKey: 'dataScope', menu: true } },
        ],
      },
      {
        path: 'access', name: 'Access', redirect: '/access/client',
        meta: { title: '接入管理', icon: Connection, menu: true },
        children: [
          { path: 'client', name: 'AccessClient', component: resourcePage, meta: { title: '三方接入方', resourceKey: 'client', menu: true } },
          { path: 'application', name: 'AccessApplication', component: resourcePage, meta: { title: '应用管理', resourceKey: 'application', menu: true } },
          { path: 'api-grant', name: 'AccessGrant', component: resourcePage, meta: { title: 'API 授权', resourceKey: 'apiGrant', menu: true } },
          { path: 'ip-whitelist', name: 'AccessIp', component: resourcePage, meta: { title: 'IP 白名单', resourceKey: 'ipWhitelist', menu: true } },
          { path: 'auth-exempt', name: 'AccessExempt', component: resourcePage, meta: { title: '免认证接口', resourceKey: 'authExempt', menu: true } },
        ],
      },
      {
        path: 'event', name: 'Event', redirect: '/event/list',
        meta: { title: '事件中心', icon: Briefcase, menu: true },
        children: [
          { path: 'list', name: 'EventList', component: resourcePage, meta: { title: '事件列表', resourceKey: 'event', menu: true } },
          { path: 'create', name: 'EventCreate', component: () => import('@/views/event/create.vue'), meta: { title: '新建事件', menu: true } },
          { path: 'todo', name: 'EventTodo', component: resourcePage, meta: { title: '我的待办', resourceKey: 'todo', menu: true } },
          { path: 'process', name: 'EventProcess', component: resourcePage, meta: { title: '流程记录', resourceKey: 'processRecord', menu: true } },
          { path: 'detail/:id', name: 'EventDetail', component: () => import('@/views/event/detail.vue'), meta: { title: '事件详情', hidden: true } },
        ],
      },
      {
        path: 'message', name: 'Message', redirect: '/message/list',
        meta: { title: '消息中心', icon: Bell, menu: true },
        children: [
          { path: 'list', name: 'MessageList', component: resourcePage, meta: { title: '消息列表', resourceKey: 'message', menu: true } },
          { path: 'template', name: 'MessageTemplate', component: resourcePage, meta: { title: '通知模板', resourceKey: 'messageTemplate', menu: true } },
          { path: 'internal', name: 'InternalMessage', component: resourcePage, meta: { title: '站内消息', resourceKey: 'internalMessage', menu: true } },
          { path: 'record', name: 'SendRecord', component: resourcePage, meta: { title: '发送记录', resourceKey: 'sendRecord', menu: true } },
        ],
      },
      {
        path: 'job', name: 'Job', redirect: '/job/list',
        meta: { title: '定时任务', icon: Calendar, menu: true },
        children: [
          { path: 'list', name: 'JobList', component: resourcePage, meta: { title: '任务管理', resourceKey: 'job', menu: true } },
          { path: 'record', name: 'JobRecord', component: resourcePage, meta: { title: '执行记录', resourceKey: 'jobRecord', menu: true } },
        ],
      },
      {
        path: 'log', name: 'Log', redirect: '/log/login',
        meta: { title: '日志审计', icon: Operation, menu: true },
        children: [
          { path: 'login', name: 'LoginLog', component: resourcePage, meta: { title: '登录日志', resourceKey: 'loginLog', menu: true } },
          { path: 'operation', name: 'OperationLog', component: resourcePage, meta: { title: '操作日志', resourceKey: 'operationLog', menu: true } },
          { path: 'exception', name: 'ExceptionLog', component: resourcePage, meta: { title: '异常日志', resourceKey: 'exceptionLog', menu: true } },
        ],
      },
      {
        path: 'profile', name: 'Profile', component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: User, hidden: true },
      },
      {
        path: 'architecture', name: 'Architecture', component: () => import('@/views/architecture/index.vue'),
        meta: { title: '架构说明', icon: DataAnalysis, menu: true },
      },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard', meta: { hidden: true, icon: Lock, document: Document } },
]

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: unknown
    public?: boolean
    hidden?: boolean
    menu?: boolean
    resourceKey?: string
  }
}
