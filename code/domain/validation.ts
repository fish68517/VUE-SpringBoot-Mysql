import { validatePhase2 } from './validate-phase2'
import { validEventTime } from './event-time'
import type { State } from './types'
import { users, manifest, regions, scenarios, dmas, attachments } from '../repositories/seed'
export function validateState(s: State) {
  if (!s || s.version !== manifest.datasetVersion)
    throw new Error('数据版本不兼容，请保留原记录并重置当前场景')
  if (!scenarios.some((v) => v.id === s.scenarioId) || !Number.isFinite(Date.parse(s.simulationTime)))
    throw new Error('场景或时间无效')
  if (!Number.isInteger(s.revision) || s.revision < 0) throw new Error('数据修订号无效')
  for (const key of ['facilities', 'alarms', 'workorders', 'inspections'] as const) {
    if (!Array.isArray(s[key]) || s[key].length > 5000) throw new Error('数据集大小或结构无效')
    const ids = s[key].map((v) => v.id)
    if (ids.some((id) => typeof id !== 'string' || !id) || new Set(ids).size !== ids.length)
      throw new Error(key + '存在重复或无效ID')
  }
  const facilityIds = new Set(s.facilities.map((f) => f.id)),
    userIds = new Set(users.map((u) => u.id))
  for (const f of s.facilities)
    if (
      !regions.some((r) => r.id === f.regionId) ||
      !dmas.some((d) => d.id === f.dmaId) ||
      !Array.isArray(f.position) ||
      f.position.length !== 2 ||
      f.position.some((v) => !Number.isFinite(v))
    )
      throw new Error('设施关联或坐标无效')
  for (const a of s.alarms) {
    if (
      !facilityIds.has(a.facilityId) ||
      !['pending', 'accepted', 'processing', 'review', 'closed'].includes(a.status) ||
      !['urgent', 'important', 'normal'].includes(a.level) ||
      !Array.isArray(a.history)
    )
      throw new Error('告警关联或状态无效')
    if (
      a.workOrderId &&
      !s.workorders.some((w) => w.id === a.workOrderId && w.sourceId === a.id && w.sourceType === 'alarm')
    )
      throw new Error('告警工单双向关联不一致')
  }
  for (const w of s.workorders) {
    if (w.occurredAt !== undefined && !validEventTime(w.occurredAt)) throw new Error('事件发生时间无效')
    if (
      !facilityIds.has(w.facilityId) ||
      (w.assigneeId && !userIds.has(w.assigneeId)) ||
      !['pending', 'assigned', 'processing', 'review', 'completed'].includes(w.status) ||
      !Array.isArray(w.steps)
    )
      throw new Error('工单关联或状态无效')
    if (w.sourceType === 'alarm' && !s.alarms.some((a) => a.id === w.sourceId && a.workOrderId === w.id))
      throw new Error('工单告警关联无效')
    if (
      w.sourceType === 'inspection' &&
      !s.inspections.some((t) => t.id === w.sourceId && t.checks.some((c) => c.workOrderId === w.id))
    )
      throw new Error('工单任务关联无效')
  }
  for (const t of s.inspections) {
    if (
      !userIds.has(t.assigneeId) ||
      !Array.isArray(t.checks) ||
      !['draft', 'pending', 'processing', 'completed', 'cancelled'].includes(t.status)
    )
      throw new Error('任务状态或人员无效')
    for (const c of t.checks)
      if (
        !facilityIds.has(c.facilityId) ||
        !['', 'normal', 'abnormal'].includes(c.result) ||
        (c.attachmentId && !attachments.some((a) => a.id === c.attachmentId)) ||
        (c.workOrderId && !s.workorders.some((w) => w.id === c.workOrderId && w.sourceId === t.id))
      )
        throw new Error('巡检项关联无效')
  }
  validatePhase2(s)
  if (!Array.isArray(s.logs) || !Array.isArray(s.notifications)) throw new Error('操作记录无效')
}
