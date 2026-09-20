import type { State, User, StorageAdapter, Result, History } from '../../domain/types'
import { clone, makeState, users, permissions, attachments, scenarios } from '../../repositories/seed'
import { validateState } from '../../domain/validation'
import { canSee } from '../../domain/metrics'
export const STATE_KEY = 'smart-water:state:v1',
  SESSION_KEY = 'smart-water:session:v1'
class BusinessError extends Error {
  constructor(
    public code: string,
    message: string,
  ) {
    super(message)
  }
}
const fail = (code: string, message: string): never => {
  throw new BusinessError(code, message)
}
export class DemoEngine {
  state: State = makeState()
  user: User | null = null
  warning = ''
  constructor(private storage: StorageAdapter) {
    try {
      const value = storage.get(STATE_KEY)
      if (value) {
        let s = JSON.parse(value)
        if (s.version === 'phase1-20260920-v1')
          s = { ...s, version: this.state.version, phase2: clone(this.state.phase2) }
        validateState(s)
        this.state = s
      }
    } catch {
      this.warning = '本机演示记录无法恢复或版本已变化。当前显示初始数据；管理员确认重置后才允许保存。'
    }
    try {
      const id = storage.get(SESSION_KEY)
      this.user = users.find((u) => u.id === id && u.enabled) ?? null
    } catch {
      this.user = null
    }
  }
  has(permission: string) {
    return !!this.user && (permissions as Record<string, string[]>)[this.user.roleId]?.includes(permission)
  }
  require(permission: string) {
    if (!this.has(permission)) fail('FORBIDDEN', '当前账号无权执行此操作')
  }
  facility(id: string) {
    const f = this.state.facilities.find((v) => v.id === id)
    if (!f) return fail('NOT_FOUND', '设施不存在')
    if (!canSee(this.user, f)) return fail('FORBIDDEN', '当前账号无权访问该区域')
    return f
  }
  protected event(text: string): History {
    return { time: this.state.simulationTime, actor: this.user?.displayName ?? '系统模拟', text }
  }
  protected newId(prefix: string, list: { id: string }[]) {
    let i = 1
    while (list.some((v) => v.id === `${prefix}-${String(i).padStart(3, '0')}`)) i++
    return `${prefix}-${String(i).padStart(3, '0')}`
  }
  protected assignee(id: string, regionId: string) {
    const u = users.find(
      (v) => v.id === id && v.enabled && v.roleId === 'operator' && v.regionIds.includes(regionId),
    )
    if (!u) fail('VALIDATION_ERROR', '请选择负责该区域的操作员')
    return id
  }
  protected save(next: State) {
    validateState(next)
    try {
      this.storage.set(STATE_KEY, JSON.stringify(next))
    } catch {
      fail('STORAGE_WRITE_FAILED', '本机保存失败，操作未生效，请检查存储空间')
    }
    this.state = next
  }
  protected transaction<T>(action: (next: State) => T): T {
    if (this.warning) fail('VERSION_MISMATCH', this.warning)
    const next = clone(this.state)
    const value = action(next)
    next.revision++
    this.save(next)
    return value
  }
  async execute<T>(action: () => T): Promise<Result<T>> {
    try {
      return { success: true, data: action(), error: null }
    } catch (e) {
      return {
        success: false,
        data: null,
        error: {
          code: e instanceof BusinessError ? e.code : 'VALIDATION_ERROR',
          message: e instanceof Error ? e.message : '操作失败',
        },
      }
    }
  }
  login(username: string, password: string) {
    const u = users.find((u) => u.username === username.trim() && u.password === password && u.enabled)
    if (!u) fail('VALIDATION_ERROR', '账号或密码不正确')
    this.storage.set(SESSION_KEY, u!.id)
    this.user = u!
    return { id: u!.id, displayName: u!.displayName }
  }
  logout() {
    this.storage.remove(SESSION_KEY)
    this.user = null
  }
  reset(scenarioId: string) {
    this.require('reset')
    if (!scenarios.some((s) => s.id === scenarioId)) fail('VALIDATION_ERROR', '场景不存在')
    this.save(makeState(scenarioId))
    this.warning = ''
  }
  advance(seconds: number) {
    if (!this.user || this.warning) return
    this.transaction((s) => {
      s.simulationTime = new Date(Date.parse(s.simulationTime) + seconds * 1000 + 8 * 3600 * 1000)
        .toISOString()
        .replace('Z', '+08:00')
    })
  }
  createFromAlarm(alarmId: string, assigneeId: string, description: string) {
    this.require('write')
    const alarm = this.state.alarms.find((a) => a.id === alarmId)
    if (!alarm) return fail('NOT_FOUND', '告警不存在')
    const f = this.facility(alarm.facilityId)
    if (alarm.workOrderId) return alarm.workOrderId
    if (alarm.status !== 'pending') fail('INVALID_TRANSITION', '此告警已关闭或不允许重复处理')
    if (!description.trim()) fail('VALIDATION_ERROR', '请填写处置要求')
    if (!this.has('dispatch') && assigneeId !== this.user!.id) fail('FORBIDDEN', '操作员只能受理到本人名下')
    this.assignee(assigneeId, f.regionId)
    return this.transaction((s) => {
      const a = s.alarms.find((v) => v.id === alarmId)!
      const id = this.newId('WO', s.workorders)
      s.workorders.push({
        id,
        title: alarm.title + ' · ' + f.name,
        type: 'repair',
        status: 'assigned',
        facilityId: f.id,
        assigneeId,
        createdBy: this.user!.id,
        sourceType: 'alarm',
        sourceId: a.id,
        createdAt: s.simulationTime,
        description: description.trim(),
        result: '',
        steps: [this.event('由告警创建并派发工单')],
      })
      a.status = 'accepted'
      a.assigneeId = assigneeId
      a.workOrderId = id
      a.history.push(this.event('受理告警，关联工单 ' + id))
      s.logs.push(this.event('创建工单 ' + id))
      for (const channel of ['sms', 'email'] as const) {
        if (!s.phase2.noticeConfig[channel] || !s.phase2.noticeConfig.levels.includes(a.level)) continue
        s.notifications.push({
          id: this.newId('NOTICE', s.notifications),
          alarmId: a.id,
          channel,
          status: 'simulated',
        })
      }
      return id
    })
  }
  createOrder(input: {
    title: string
    type: string
    facilityId: string
    assigneeId: string
    description: string
  }) {
    this.require('write')
    const f = this.facility(input.facilityId)
    if (
      !input.title.trim() ||
      !input.description.trim() ||
      !['hotline', 'repair', 'leak', 'inspection'].includes(input.type)
    )
      fail('VALIDATION_ERROR', '请填写标题、类型和处理说明')
    if (input.assigneeId) {
      if (!this.has('dispatch') && input.assigneeId !== this.user!.id) fail('FORBIDDEN', '不能派发给其他人员')
      this.assignee(input.assigneeId, f.regionId)
    }
    return this.transaction((s) => {
      const id = this.newId('WO', s.workorders)
      s.workorders.push({
        id,
        ...input,
        title: input.title.trim(),
        assigneeId: input.assigneeId || null,
        status: input.assigneeId ? 'assigned' : 'pending',
        createdBy: this.user!.id,
        sourceType: 'manual',
        sourceId: null,
        createdAt: s.simulationTime,
        result: '',
        steps: [this.event('新建工单')],
      })
      return id
    })
  }
  transitionOrder(id: string, expectedStatus: string, action: string, payload: string) {
    this.require('write')
    const w = this.state.workorders.find((o) => o.id === id)
    if (!w) return fail('NOT_FOUND', '工单不存在')
    const f = this.facility(w.facilityId)
    if (w.status !== expectedStatus) fail('INVALID_TRANSITION', '状态已变化，请刷新详情')
    const transitions: Record<string, [string, string]> = {
      dispatch: ['pending', 'assigned'],
      start: ['assigned', 'processing'],
      submit: ['processing', 'review'],
      approve: ['review', 'completed'],
      reject: ['review', 'processing'],
    }
    const transition = transitions[action]
    if (!transition || w.status !== transition[0]) fail('INVALID_TRANSITION', '当前状态不能执行此操作')
    if (action === 'dispatch') {
      this.require('dispatch')
      this.assignee(payload, f.regionId)
    } else if (action === 'approve' || action === 'reject') this.require('review')
    else if (this.user!.roleId !== 'admin' && w.assigneeId !== this.user!.id)
      fail('FORBIDDEN', '只有执行人可以处理此工单')
    if (['submit', 'reject'].includes(action) && !payload.trim())
      fail('VALIDATION_ERROR', '请填写处理结果或驳回原因')
    return this.transaction((s) => {
      const order = s.workorders.find((o) => o.id === id)!
      order.status = transition[1]
      if (action === 'dispatch') order.assigneeId = payload
      if (action === 'submit') order.result = payload.trim()
      const labels: Record<string, string> = {
        dispatch: '派发工单',
        start: '接单并开始处理',
        submit: '提交处理结果',
        approve: '验收通过',
        reject: '验收驳回',
      }
      const event = this.event(labels[action] + (payload ? '：' + payload : ''))
      order.steps.push(event)
      if (order.sourceType === 'alarm') {
        const a = s.alarms.find((a) => a.id === order.sourceId)!
        a.status = (
          { assigned: 'accepted', processing: 'processing', review: 'review', completed: 'closed' } as Record<
            string,
            string
          >
        )[order.status]
        a.history.push(event)
      }
      s.logs.push(event)
      return id
    })
  }
  createTask(input: {
    name: string
    regionId: string
    assigneeId: string
    plannedAt: string
    facilityIds: string[]
    templateId?: string
    routeId?: string
  }) {
    this.require('write')
    if (!this.has('dispatch') && input.assigneeId !== this.user!.id) fail('FORBIDDEN', '只能创建本人任务')
    const template = this.state.phase2.templates.find((t) => t.id === (input.templateId || 'TPL-001'))
    if (!template) fail('VALIDATION_ERROR', '模板不存在')
    if (
      input.routeId &&
      !this.state.phase2.routes.some(
        (r) =>
          r.id === input.routeId &&
          r.regionId === input.regionId &&
          r.facilityIds.length === new Set(input.facilityIds).size &&
          r.facilityIds.every((id) => input.facilityIds.includes(id)),
      )
    )
      fail('VALIDATION_ERROR', '路线与设施不一致')
    if (
      !input.name.trim() ||
      !/^\d{4}-\d{2}-\d{2}$/.test(input.plannedAt) ||
      !Number.isFinite(Date.parse(input.plannedAt)) ||
      !input.facilityIds.length
    )
      fail('VALIDATION_ERROR', '请填写任务名称、日期并选择至少一个设施')
    this.assignee(input.assigneeId, input.regionId)
    const ids = [...new Set(input.facilityIds)]
    for (const id of ids)
      if (this.facility(id).regionId !== input.regionId) fail('VALIDATION_ERROR', '设施不属于任务区域')
    return this.transaction((s) => {
      const id = this.newId('TASK', s.inspections)
      s.inspections.push({
        id,
        name: input.name.trim(),
        regionId: input.regionId,
        assigneeId: input.assigneeId,
        status: 'pending',
        plannedAt: input.plannedAt,
        createdBy: this.user!.id,
        routeId: input.routeId || 'custom',
        templateId: template!.id,
        template: clone(template!),
        checks: ids.map((facilityId) => ({
          facilityId,
          result: '',
          description: '',
          workOrderId: null,
          attachmentId: null,
        })),
        history: [this.event('发布巡检任务')],
      })
      return id
    })
  }
  checkTask(
    taskId: string,
    facilityId: string,
    result: string,
    description: string,
    attachmentId: string | null,
    values: Record<string, string> = {},
  ) {
    this.require('write')
    const task = this.state.inspections.find((t) => t.id === taskId)
    if (!task) return fail('NOT_FOUND', '任务不存在')
    this.facility(facilityId)
    if (this.user!.roleId !== 'admin' && task.assigneeId !== this.user!.id)
      fail('FORBIDDEN', '只有任务执行人可以上报')
    if (['completed', 'cancelled'].includes(task.status)) fail('INVALID_TRANSITION', '已完成任务不能修改')
    const check = task.checks.find((c) => c.facilityId === facilityId)
    if (!check) fail('NOT_FOUND', '设施不属于此任务')
    if (!['normal', 'abnormal'].includes(result) || !description.trim())
      fail('VALIDATION_ERROR', '请选择检查结果并填写说明')
    if (attachmentId && !attachments.some((a) => a.id === attachmentId))
      fail('VALIDATION_ERROR', '附件不存在')
    if (check!.workOrderId && result !== 'abnormal')
      fail('INVALID_TRANSITION', '已生成异常工单，请保留异常记录并通过工单处理')
    for (const field of task.template?.fields || []) {
      const v = values[field.id] || ''
      if (field.required && !v.trim()) fail('VALIDATION_ERROR', '请填写：' + field.label)
      if (
        v &&
        ((field.type === 'number' && !Number.isFinite(Number(v))) ||
          (field.type === 'choice' && !field.options.includes(v)))
      )
        fail('VALIDATION_ERROR', '字段值无效：' + field.label)
    }
    return this.transaction((s) => {
      const t = s.inspections.find((v) => v.id === taskId)!
      const c = t.checks.find((v) => v.facilityId === facilityId)!
      Object.assign(c, {
        result,
        description: description.trim(),
        attachmentId,
        values: clone(values),
        reportedAt: s.simulationTime,
      })
      t.status = 'processing'
      if (result === 'abnormal' && !c.workOrderId) {
        const id = this.newId('WO', s.workorders)
        s.workorders.push({
          id,
          title: '巡检异常 · ' + this.facility(facilityId).name,
          type: 'inspection',
          status: 'assigned',
          facilityId,
          assigneeId: t.assigneeId,
          createdBy: this.user!.id,
          sourceType: 'inspection',
          sourceId: taskId,
          createdAt: s.simulationTime,
          description,
          result: '',
          steps: [this.event('巡检异常自动生成工单')],
        })
        c.workOrderId = id
      }
      t.history.push(this.event('上报设施 ' + facilityId + '：' + description))
      return c.workOrderId
    })
  }
  completeTask(id: string) {
    this.require('write')
    const t = this.state.inspections.find((v) => v.id === id)
    if (!t) return fail('NOT_FOUND', '任务不存在')
    if (this.user!.roleId !== 'admin' && t.assigneeId !== this.user!.id) fail('FORBIDDEN', '无权提交此任务')
    for (const c of t.checks) this.facility(c.facilityId)
    if (
      ['completed', 'cancelled'].includes(t.status) ||
      t.checks.some((c) => !c.result || !c.description || (c.result === 'abnormal' && !c.workOrderId))
    )
      fail('INVALID_TRANSITION', '请先完成全部必填检查项')
    return this.transaction((s) => {
      const task = s.inspections.find((v) => v.id === id)!
      task.status = 'completed'
      task.history.push(this.event('完成巡检，异常继续由关联工单处理'))
      return id
    })
  }
}
