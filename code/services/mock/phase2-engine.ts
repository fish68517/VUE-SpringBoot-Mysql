import { DemoEngine } from './engine'
import { migrateLegacyLabels } from '../../domain/legacy-labels'
import { clone, users, manifest } from '../../repositories/seed'
import { validateState } from '../../domain/validation'
import type { State, Facility } from '../../domain/types'
import type { Change, Pipe, Template, Route, Phase2 } from '../../domain/phase2'
export class Phase2Engine extends DemoEngine {
  override advance(seconds: number) {
    if (!this.user || this.warning) return
    if (!Number.isInteger(seconds) || seconds < 1 || seconds > 3600) throw new Error('单次回放应为 1—3600 秒')
    this.transaction((s) => {
      const start = Date.parse(s.simulationTime),
        end = start + seconds * 1000
      s.simulationTime = new Date(end + 8 * 3600000).toISOString().replace('Z', '+08:00')
      for (const d of s.phase2.devices) {
        if (d.status !== 'online') continue
        const step = d.samplingIntervalSec * 1000
        for (let t = Math.floor(start / step) * step + step; t <= end; t += step)
          s.phase2.samples.push({
            deviceId: d.id,
            time: new Date(t + 8 * 3600000).toISOString().replace('Z', '+08:00'),
            value: Number((0.32 + Math.sin(t / 60000 + Number(d.id.slice(-3))) * 0.04).toFixed(3)),
          })
      }
      s.phase2.samples = s.phase2.samples.slice(-3000)
    })
  }
  propose(kind: Change['kind'], action: Change['action'], proposed: Facility | Pipe, rejectedId = '') {
    this.require('write')
    const list = kind === 'facility' ? this.state.facilities : this.state.phase2.pipes
    const before = list.find((v) => v.id === proposed.id)
    if (action === 'add' ? !!before : !before) throw new Error('目标编号已存在或不存在')
    if (!this.user!.regionIds.includes(proposed.regionId)) throw new Error('无权修改此区域')
    if (before && !this.user!.regionIds.includes(before.regionId)) throw new Error('无权修改原区域')
    if (kind === 'pipe' && action !== 'delete') {
      const p = proposed as Pipe
      this.facility(p.fromFacilityId)
      this.facility(p.toFacilityId)
    }
    if (kind === 'facility' && before && action === 'edit') {
      const f = proposed as Facility,
        old = before as Facility
      if (
        (f.regionId !== old.regionId || f.dmaId !== old.dmaId) &&
        (this.state.alarms.some((a) => a.facilityId === f.id) ||
          this.state.workorders.some((w) => w.facilityId === f.id) ||
          this.state.inspections.some((t) => t.checks.some((c) => c.facilityId === f.id)) ||
          this.state.phase2.routes.some((r) => r.facilityIds.includes(f.id)))
      )
        throw new Error('设施已有区域业务关联，不能直接迁移区域或 DMA')
    }
    const old = this.state.phase2.changes.find((c) => c.id === rejectedId)
    if (
      rejectedId &&
      (!old ||
        old.status !== 'rejected' ||
        old.createdBy !== this.user!.id ||
        old.kind !== kind ||
        old.action !== action ||
        old.targetId !== proposed.id)
    )
      throw new Error('不能重新提交此申请')
    if (
      this.state.phase2.changes.some(
        (c) => c.targetId === proposed.id && c.kind === kind && c.status === 'pending',
      )
    )
      throw new Error('此对象已有待审核申请')
    const candidate = clone(this.state)
    this.applyChange(candidate, { kind, action, targetId: proposed.id, proposed } as Change)
    validateState(candidate)
    return this.transaction((s) => {
      const id = old?.id || this.newId('CHANGE', s.phase2.changes)
      const change: Change = {
        id,
        kind,
        action,
        targetId: proposed.id,
        before: clone(before || null),
        proposed: clone(proposed),
        status: 'pending',
        stage: 1,
        reviewers: [...s.phase2.reviewers],
        createdBy: this.user!.id,
        history: [...(old?.history || []), this.event('提交变更，等待第 1 级审核')],
      }
      if (old) s.phase2.changes[s.phase2.changes.findIndex((c) => c.id === id)] = change
      else s.phase2.changes.push(change)
      s.logs.push(this.event('提交地图变更 ' + id))
      return id
    })
  }
  private applyChange(s: State, c: Change) {
    if (c.kind === 'facility') {
      if (c.action === 'delete') {
        if (
          s.phase2.pipes.some((p) => [p.fromFacilityId, p.toFacilityId].includes(c.targetId)) ||
          s.alarms.some((a) => a.facilityId === c.targetId) ||
          s.workorders.some((w) => w.facilityId === c.targetId) ||
          s.inspections.some((t) => t.checks.some((k) => k.facilityId === c.targetId)) ||
          s.phase2.devices.some((d) => d.facilityId === c.targetId) ||
          s.phase2.routes.some((r) => r.facilityIds.includes(c.targetId)) ||
          s.phase2.bursts.some((b) => b.facilityIds.includes(c.targetId) || b.valveIds.includes(c.targetId))
        )
          throw new Error('设施仍被管线、设备或业务引用，不能删除')
        s.facilities = s.facilities.filter((f) => f.id !== c.targetId)
      } else {
        const f = clone(c.proposed as Facility),
          i = s.facilities.findIndex((v) => v.id === c.targetId)
        if (c.action === 'add') s.facilities.push(f)
        else s.facilities[i] = f
        for (const p of s.phase2.pipes) {
          if (p.fromFacilityId === f.id) p.path[0] = [...f.position]
          if (p.toFacilityId === f.id) p.path[p.path.length - 1] = [...f.position]
        }
      }
    } else {
      if (c.action === 'delete') {
        if (s.phase2.bursts.some((b) => b.pipeId === c.targetId)) throw new Error('管线被爆管场景引用')
        s.phase2.pipes = s.phase2.pipes.filter((p) => p.id !== c.targetId)
      } else {
        const p = clone(c.proposed as Pipe)
        if (c.action === 'add') s.phase2.pipes.push(p)
        else s.phase2.pipes[s.phase2.pipes.findIndex((v) => v.id === c.targetId)] = p
      }
    }
  }
  review(id: string, stage: number, approve: boolean, reason: string) {
    this.require('review')
    const c = this.state.phase2.changes.find((c) => c.id === id)
    if (!c || c.status !== 'pending' || c.stage !== stage) throw new Error('审核状态已变化')
    if (c.reviewers[stage - 1] !== this.user!.id) throw new Error('当前账号不是本级审核人')
    if (!reason.trim()) throw new Error('请填写审核意见')
    return this.transaction((s) => {
      const next = s.phase2.changes.find((v) => v.id === id)!
      if (!approve) next.status = 'rejected'
      else if (stage === 6) {
        const list = c.kind === 'facility' ? s.facilities : s.phase2.pipes
        const current = list.find((v) => v.id === c.targetId) || null
        if (JSON.stringify(current) !== JSON.stringify(c.before))
          throw new Error('有效数据已变化，请驳回后重新提交')
        this.applyChange(s, next)
        next.status = 'effective'
      } else next.stage++
      const event = this.event(`第 ${stage} 级${approve ? '通过' : '驳回'}：${reason}`)
      next.history.push(event)
      s.logs.push(event)
    })
  }
  configureReviewers(ids: string[]) {
    this.require('review')
    if (
      ids.length !== 6 ||
      ids.some((id) => !users.some((u) => u.id === id && u.roleId === 'admin' && u.enabled))
    )
      throw new Error('需要六个有效管理员审核节点')
    return this.transaction((s) => {
      s.phase2.reviewers = [...ids]
      s.logs.push(this.event('更新六级审核配置，仅作用于新申请'))
    })
  }
  layer(action: 'draft' | 'sync' | 'publish', fields: string[]) {
    this.require('dispatch')
    if (
      !fields.length ||
      fields.some((f) => !['name', 'type', 'regionId', 'dmaId', 'status', 'description'].includes(f))
    )
      throw new Error('请选择有效字段')
    return this.transaction((s) => {
      const l = s.phase2.layers
      if (action === 'draft') {
        l.draftFields = [...new Set(fields)]
        l.syncedAt = ''
      }
      if (action === 'sync') l.syncedAt = s.simulationTime
      if (action === 'publish') {
        if (!l.syncedAt) throw new Error('请先同步当前字段')
        l.fields = [...l.draftFields]
        l.publishedAt = s.simulationTime
      }
      s.logs.push(this.event('图层字段 ' + action))
    })
  }
  saveTemplate(template: Template) {
    this.require('dispatch')
    if (this.state.phase2.templates.some((t) => t.id === template.id))
      throw new Error('模板编号已存在，请使用新编号')
    return this.transaction((s) => {
      s.phase2.templates.push(clone(template))
      s.logs.push(this.event('新建巡检模板 ' + template.name))
    })
  }
  saveRoute(route: Route) {
    this.require('dispatch')
    return this.transaction((s) => {
      const i = s.phase2.routes.findIndex((r) => r.id === route.id)
      if (i < 0) s.phase2.routes.push(clone(route))
      else s.phase2.routes[i] = clone(route)
      s.logs.push(this.event('配置巡检路线 ' + route.name))
    })
  }
  reassignTask(id: string, assigneeId: string, reason: string) {
    this.require('dispatch')
    const task = this.state.inspections.find((t) => t.id === id)
    if (!task || ['completed', 'cancelled'].includes(task.status)) throw new Error('当前任务不能改派')
    this.assignee(assigneeId, task.regionId)
    if (!reason.trim()) throw new Error('请填写改派原因')
    return this.transaction((s) => {
      const t = s.inspections.find((t) => t.id === id)!
      t.assigneeId = assigneeId
      const h = this.event('改派给 ' + users.find((u) => u.id === assigneeId)!.displayName + '：' + reason)
      t.history.push(h)
      s.logs.push(h)
    })
  }
  cancelTask(id: string, reason: string) {
    this.require('dispatch')
    if (!reason.trim()) throw new Error('请填写取消原因')
    return this.transaction((s) => {
      const t = s.inspections.find((t) => t.id === id)
      if (!t || ['completed', 'cancelled'].includes(t.status)) throw new Error('不能取消此任务')
      t.status = 'cancelled'
      t.history.push(this.event('取消：' + reason))
    })
  }
  deviceSetting(id: string, interval: number, on: boolean) {
    this.require('write')
    const d = this.state.phase2.devices.find((d) => d.id === id)
    if (!d) throw new Error('设备不存在')
    this.facility(d.facilityId)
    if (!Number.isInteger(interval) || interval < 1 || interval > 300)
      throw new Error('采样间隔必须为 1—300 秒整数')
    return this.transaction((s) => {
      const device = s.phase2.devices.find((d) => d.id === id)!
      device.samplingIntervalSec = interval
      if ((device.status === 'online') !== on) {
        device.status = on ? 'online' : 'offline'
        s.phase2.deviceEvents.push({ deviceId: id, time: s.simulationTime, on })
      }
      s.logs.push(this.event('设备设置 ' + id + ' / ' + interval + ' 秒 / ' + (on ? '在线' : '离线')))
    })
  }
  toggleValve(id: string) {
    this.require('write')
    const f = this.facility(id)
    if (f.type !== 'valve') throw new Error('只有阀门可切换开关')
    return this.transaction((s) => {
      const valve = s.facilities.find((v) => v.id === id)!
      valve.valveState = valve.valveState === 'open' ? 'closed' : 'open'
      s.logs.push(this.event('阀门 ' + id + ' → ' + valve.valveState))
    })
  }
  setThreshold(id: string, value: number) {
    this.require('dispatch')
    if (!Number.isFinite(value) || value < 0 || value > 1) throw new Error('阈值应在 0—100% 范围')
    return this.transaction((s) => {
      const d = s.phase2.dmas.find((d) => d.id === id)
      if (!d) throw new Error('分区不存在')
      d.threshold = value
      s.logs.push(this.event('更新 ' + id + ' 漏损阈值 ' + value))
    })
  }
  notices(config: Phase2['noticeConfig']) {
    this.require('dispatch')
    return this.transaction((s) => {
      s.phase2.noticeConfig = clone(config)
      s.logs.push(this.event('保存通知配置'))
    })
  }
  notify(alarmId: string) {
    this.require('write')
    const a = this.state.alarms.find((a) => a.id === alarmId)
    if (!a) throw new Error('告警不存在')
    this.facility(a.facilityId)
    return this.transaction((s) => {
      const config = s.phase2.noticeConfig
      for (const channel of ['sms', 'email'] as const)
        if (config[channel] && config.levels.includes(a.level))
          s.notifications.push({
            id: this.newId('NOTICE', s.notifications),
            alarmId,
            channel,
            status: 'simulated',
          })
      s.logs.push(this.event('生成通知记录 ' + alarmId))
    })
  }
  cards(ids: string[]) {
    this.require('read')
    if (
      ids.some(
        (id) => !['alarms', 'orders', 'inspection', 'map', 'video', 'energy', 'mobileReport'].includes(id),
      )
    )
      throw new Error('门户卡片无效')
    return this.transaction((s) => {
      s.phase2.portalCards[this.user!.id] = [...new Set(ids)]
    })
  }
  exportSnapshot() {
    this.require('reset')
    return JSON.stringify(
      { format: 'smart-water-snapshot', version: manifest.datasetVersion, state: this.state },
      null,
      2,
    )
  }
  importSnapshot(text: string) {
    this.require('reset')
    if (text.length > 8_000_000) throw new Error('快照超过 8 MB')
    const value = JSON.parse(text)
    if (
      value.format !== 'smart-water-snapshot' ||
      value.version !== manifest.datasetVersion ||
      Object.keys(value).some((k) => !['format', 'version', 'state'].includes(k))
    )
      throw new Error('快照格式或版本无效')
    if (/"(?:password|token|secret|users|account)"\s*:/i.test(text))
      throw new Error('快照不能包含账号或凭据字段')
    validateState(value.state)
    const next = clone(value.state as State)
    migrateLegacyLabels(next)
    next.logs.push(this.event('管理员导入本地快照'))
    next.revision++
    this.save(next)
    this.warning = ''
  }
}
