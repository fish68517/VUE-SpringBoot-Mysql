import type { State } from './types'
import { regions, users } from '../repositories/seed'
export function validatePhase2(s: State) {
  const p = s.phase2
  const bad = (message: string): never => {
    throw new Error(message)
  }
  if (!p || typeof p !== 'object') bad('缺少第二阶段数据')
  const histories = [
    s.logs,
    ...s.alarms.map((a) => a.history),
    ...s.workorders.map((w) => w.steps),
    ...s.inspections.map((t) => t.history),
  ]
  for (const hs of histories)
    if (
      !Array.isArray(hs) ||
      hs.length > 10000 ||
      hs.some(
        (h) =>
          typeof h.text !== 'string' || typeof h.actor !== 'string' || !Number.isFinite(Date.parse(h.time)),
      )
    )
      bad('业务历史格式无效')
  for (const key of [
    'samples',
    'pipes',
    'dmas',
    'devices',
    'changes',
    'templates',
    'routes',
    'tracks',
    'deviceEvents',
    'power',
    'dmaHistory',
    'energyPlans',
    'bursts',
    'videos',
  ] as const) {
    if (!Array.isArray(p[key]) || p[key].length > 10000) bad('第二阶段数据集合无效')
  }
  for (const key of [
    'pipes',
    'dmas',
    'devices',
    'changes',
    'templates',
    'routes',
    'energyPlans',
    'bursts',
    'videos',
  ] as const) {
    const ids = p[key].map((v) => v.id)
    if (ids.some((id) => !id || typeof id !== 'string') || new Set(ids).size !== ids.length)
      bad('第二阶段编号重复或无效')
  }
  const point = (v: number[]) =>
    Array.isArray(v) && v.length === 2 && v.every((n) => Number.isFinite(n) && n >= 0 && n <= 1000)
  for (const f of s.facilities)
    if (
      !f.name?.trim() ||
      !['valve', 'sensor', 'meter', 'pump'].includes(f.type) ||
      !point(f.position) ||
      !p.dmas.some((d) => d.id === f.dmaId && d.regionId === f.regionId)
    )
      bad('设施名称、类型、坐标或分区无效')
  for (const v of p.pipes)
    if (
      !v.name?.trim() ||
      !Number.isFinite(v.diameterMm) ||
      v.diameterMm <= 0 ||
      !regions.some((r) => r.id === v.regionId) ||
      !Array.isArray(v.path) ||
      v.path.length < 2 ||
      !v.path.every(point) ||
      !s.facilities.some((f) => f.id === v.fromFacilityId) ||
      !s.facilities.some((f) => f.id === v.toFacilityId) ||
      v.fromFacilityId === v.toFacilityId
    )
      bad('管线端点或路径无效')
  for (const pipe of p.pipes) {
    const a = s.facilities.find((f) => f.id === pipe.fromFacilityId)!,
      b = s.facilities.find((f) => f.id === pipe.toFacilityId)!
    if (
      JSON.stringify(pipe.path[0]) !== JSON.stringify(a.position) ||
      JSON.stringify(pipe.path[pipe.path.length - 1]) !== JSON.stringify(b.position)
    )
      bad('管线路径须连接起终点设施')
  }
  for (const q of p.samples)
    if (
      !p.devices.some((d) => d.id === q.deviceId) ||
      !Number.isFinite(Date.parse(q.time)) ||
      !Number.isFinite(q.value)
    )
      bad('采样数据无效')
  for (const d of p.devices)
    if (
      !s.facilities.some((f) => f.id === d.facilityId) ||
      !Number.isInteger(d.samplingIntervalSec) ||
      d.samplingIntervalSec < 1 ||
      d.samplingIntervalSec > 300 ||
      !['online', 'offline'].includes(d.status)
    )
      bad('设备配置无效')
  for (const d of p.dmas)
    if (
      !Number.isFinite(d.threshold) ||
      d.threshold < 0 ||
      d.threshold > 1 ||
      ['inlet', 'transfer', 'subMeter', 'unmetered', 'produced', 'sold'].some(
        (k) => !Number.isFinite((d as any)[k]),
      )
    )
      bad('DMA 数据无效')
  for (const t of p.templates) {
    if (
      !t.name?.trim() ||
      !Array.isArray(t.fields) ||
      t.fields.length > 20 ||
      new Set(t.fields.map((f) => f.id)).size !== t.fields.length
    )
      bad('模板无效')
    for (const f of t.fields)
      if (
        !/^[a-zA-Z][\w-]{0,39}$/.test(f.id) ||
        !f.label?.trim() ||
        !['text', 'number', 'choice'].includes(f.type) ||
        typeof f.required !== 'boolean' ||
        !Array.isArray(f.options) ||
        (f.type === 'choice' &&
          (!f.options.length || f.options.some((o) => typeof o !== 'string' || !o.trim())))
      )
        bad('模板字段无效')
  }
  for (const r of p.routes)
    if (
      !r.name?.trim() ||
      !regions.some((v) => v.id === r.regionId) ||
      !r.facilityIds.length ||
      new Set(r.facilityIds).size !== r.facilityIds.length ||
      !r.facilityIds.every((id) => s.facilities.some((f) => f.id === id && f.regionId === r.regionId))
    )
      bad('巡检路线区域或设施无效')
  if (
    !Array.isArray(p.reviewers) ||
    p.reviewers.length !== 6 ||
    !p.reviewers.every((id) => users.some((u) => u.id === id && u.roleId === 'admin' && u.enabled))
  )
    bad('审核配置无效')
  for (const c of p.changes)
    if (
      !['facility', 'pipe'].includes(c.kind) ||
      !['add', 'edit', 'delete'].includes(c.action) ||
      !['pending', 'rejected', 'effective'].includes(c.status) ||
      !Number.isInteger(c.stage) ||
      c.stage < 1 ||
      c.stage > 6 ||
      c.reviewers.length !== 6 ||
      c.proposed.id !== c.targetId ||
      !Array.isArray(c.history)
    )
      bad('地图变更记录无效')
  for (const e of p.deviceEvents)
    if (
      !p.devices.some((d) => d.id === e.deviceId) ||
      !Number.isFinite(Date.parse(e.time)) ||
      typeof e.on !== 'boolean'
    )
      bad('设备启停记录无效')
  for (const v of p.power)
    if (
      !p.devices.some((d) => d.id === v.deviceId) ||
      !Number.isFinite(Date.parse(v.time)) ||
      !Number.isFinite(v.kw) ||
      v.kw < 0 ||
      (v.meterKwh !== null && (!Number.isFinite(v.meterKwh) || v.meterKwh < 0))
    )
      bad('功率样本无效')
  for (const t of p.tracks) {
    let previous = 0
    if (!s.inspections.some((v) => v.id === t.taskId) || !Array.isArray(t.points)) bad('轨迹关联无效')
    for (const q of t.points) {
      const time = Date.parse(q.time)
      if (!point(q.position) || !Number.isFinite(time) || time <= previous) bad('轨迹时间或位置无效')
      previous = time
    }
  }
  for (const v of p.videos)
    if (
      !regions.some((r) => r.id === v.regionId) ||
      typeof v.online !== 'boolean' ||
      (v.online && v.src !== '/static/demo-assets/pump-demo.mp4')
    )
      bad('视频资源仅支持本地演示文件')
  if (
    !p.noticeConfig ||
    typeof p.noticeConfig.sms !== 'boolean' ||
    typeof p.noticeConfig.email !== 'boolean' ||
    !Array.isArray(p.noticeConfig.levels) ||
    p.noticeConfig.levels.some((v) => !['urgent', 'important', 'normal'].includes(v))
  )
    bad('通知配置无效')
  if (
    !p.layers ||
    !Array.isArray(p.layers.fields) ||
    !Array.isArray(p.layers.draftFields) ||
    !p.portalCards ||
    typeof p.portalCards !== 'object'
  )
    bad('门户或图层配置无效')
  const fields = ['name', 'type', 'regionId', 'dmaId', 'status', 'description']
  if ([...p.layers.fields, ...p.layers.draftFields].some((f) => !fields.includes(f))) bad('图层字段无效')
  for (const [id, cards] of Object.entries(p.portalCards))
    if (
      !users.some((u) => u.id === id) ||
      !Array.isArray(cards) ||
      cards.length > 7 ||
      cards.some(
        (c) => !['alarms', 'orders', 'inspection', 'map', 'video', 'energy', 'mobileReport'].includes(c),
      )
    )
      bad('门户卡片无效')
  for (const d of p.dmaHistory)
    if (
      !p.dmas.some((v) => v.id === d.dmaId) ||
      !/^\d{4}-\d{2}-\d{2}$/.test(d.date) ||
      !Number.isFinite(Date.parse(d.date)) ||
      [d.inlet, d.transfer, d.subMeter, d.unmetered].some((v) => !Number.isFinite(v))
    )
      bad('DMA 历史数据无效')
  for (const plan of p.energyPlans)
    if (
      !plan.name?.trim() ||
      !Number.isFinite(plan.factor) ||
      plan.factor <= 0 ||
      !Number.isFinite(plan.minPressure) ||
      !Number.isFinite(plan.maxPressure) ||
      plan.minPressure < 0 ||
      plan.minPressure > plan.maxPressure
    )
      bad('节能方案无效')
  for (const b of p.bursts)
    if (
      !p.pipes.some((v) => v.id === b.pipeId) ||
      !Array.isArray(b.facilityIds) ||
      !Array.isArray(b.valveIds) ||
      !b.facilityIds.every((id) => s.facilities.some((f) => f.id === id)) ||
      !b.valveIds.every((id) => s.facilities.some((f) => f.id === id && f.type === 'valve')) ||
      !Number.isInteger(b.households) ||
      b.households < 0
    )
      bad('爆管场景关联无效')
  for (const task of s.inspections) {
    if (
      task.template &&
      (!Array.isArray(task.template.fields) || !p.templates.some((t) => t.id === task.templateId))
    )
      bad('任务模板快照无效')
    for (const c of task.checks) {
      if (
        c.values &&
        (typeof c.values !== 'object' ||
          Array.isArray(c.values) ||
          Object.values(c.values).some((v) => typeof v !== 'string'))
      )
        bad('巡检字段值无效')
    }
  }
  if (
    s.notifications.length > 10000 ||
    s.notifications.some(
      (n) =>
        !s.alarms.some((a) => a.id === n.alarmId) ||
        !['sms', 'email'].includes(n.channel) ||
        n.status !== 'simulated',
    )
  )
    bad('模拟通知记录无效')
}
