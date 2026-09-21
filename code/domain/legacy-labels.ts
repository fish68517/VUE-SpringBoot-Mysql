import type { Facility, History, State } from './types'
import users from '../account/users.json'

// 仅迁移旧版本内置文案，不清空状态，也不改写用户填写的描述或处理结果。
export function migrateLegacyLabels(state: State) {
  const names = new Map(users.map((u) => [u.displayName + '（演示）', u.displayName]))
  const history = (items: History[]) => {
    for (const item of items) {
      item.actor = names.get(item.actor) ?? (item.actor === '系统模拟' ? '系统' : item.actor)
      if (item.text === '创建演示工单') item.text = '创建工单'
    }
  }
  const facility = (item: Facility) => {
    if (item.description === '本地虚构设施，仅用于供水业务演示')
      item.description = '供水设施运行与维护档案'
  }
  state.facilities.forEach(facility)
  state.alarms.forEach((a) => history(a.history))
  state.workorders.forEach((order) => {
    if (order.description === '按计划检查设施并填写现场处理结果（演示）。')
      order.description = '按计划检查设施并填写现场处理结果。'
    history(order.steps)
  })
  state.inspections.forEach((task) => history(task.history))
  state.phase2.changes.forEach((change) => {
    history(change.history)
    if (change.kind === 'facility') {
      if (change.before) facility(change.before as Facility)
      facility(change.proposed as Facility)
    }
  })
  state.phase2.videos.forEach((video) => {
    if (['中原区泵房 · 合成演示', '二七区泵房 · 合成演示'].includes(video.name))
      video.name = video.name.replace(' · 合成演示', '')
  })
  state.phase2.energyPlans.forEach((plan) => {
    if (plan.name === '节能运行样例') plan.name = '节能运行方案'
  })
  history(state.logs)
  for (const log of state.logs) {
    log.text = log.text
      .replace(/^模拟设备设置 (DEV-\d+ \/ \d+ 秒 \/ (?:在线|离线))$/, '设备设置 $1')
      .replace(/^模拟阀门 (FAC-\d+ → (?:open|closed))$/, '阀门 $1')
      .replace(/^执行模拟通知 (ALM-\d+)$/, '生成通知记录 $1')
      .replace(/^保存模拟通知配置$/, '保存通知配置')
      .replace(/^图层字段 (draft|sync|publish)（本地模拟）$/, '图层字段 $1')
  }
}
