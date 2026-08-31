import type { MockMethod } from 'vite-plugin-mock'

export default [
  {
    url: '/api/web/v1/dashboard/overview',
    method: 'get',
    timeout: 260,
    response: () => ({
      code: 200,
      message: 'success',
      data: {
        cards: [
          { key: 'users', label: '平台用户', value: 1286, suffix: '人', trend: 8.2, tone: 'orange' },
          { key: 'events', label: '本月事件', value: 342, suffix: '件', trend: 12.5, tone: 'blue' },
          { key: 'todos', label: '我的待办', value: 6, suffix: '项', trend: -18.4, tone: 'green' },
          { key: 'messages', label: '未读消息', value: 18, suffix: '条', trend: 5.6, tone: 'purple' },
        ],
        trend: [
          { date: '08-20', created: 32, completed: 24 }, { date: '08-21', created: 45, completed: 31 },
          { date: '08-22', created: 38, completed: 36 }, { date: '08-23', created: 52, completed: 41 },
          { date: '08-24', created: 47, completed: 43 }, { date: '08-25', created: 61, completed: 48 },
          { date: '08-26', created: 56, completed: 51 },
        ],
        events: [
          { id: 1001, eventNo: 'SJ202608260018', title: '行政执法案卷评查问题线索核查', category: '行政执法监督', priority: '紧急', status: '办理中', createTime: '2026-08-26 09:20:16' },
          { id: 1002, eventNo: 'SJ202608260017', title: '法治政府建设年度督察材料补正', category: '法治督察', priority: '高', status: '待受理', createTime: '2026-08-26 08:45:31' },
          { id: 1003, eventNo: 'SJ202608250036', title: '行政复议申请材料在线预审', category: '行政复议', priority: '普通', status: '办理中', createTime: '2026-08-25 16:32:08' },
          { id: 1004, eventNo: 'SJ202608250031', title: '公共法律服务热线协同处置', category: '公共法律服务', priority: '普通', status: '已办结', createTime: '2026-08-25 14:08:47' },
        ],
        todos: [
          { id: 1, title: '执法案卷评查问题线索核查', source: '行政执法监督', deadline: '今天 16:00', priority: '紧急' },
          { id: 2, title: '年度法治督察材料审核', source: '法治督察', deadline: '明天 12:00', priority: '高' },
          { id: 3, title: '行政复议申请材料预审', source: '行政复议', deadline: '08-29 18:00', priority: '普通' },
          { id: 4, title: '接入方 API 授权复核', source: '接入管理', deadline: '08-30 18:00', priority: '普通' },
        ],
      },
    }),
  },
] as MockMethod[]
