import type { State, User } from './types'
import { canSee } from './metrics'
import { runtime, energy } from './phase2'
export function businessReport(
  s: State,
  user: User | null,
  type: string,
  date: string,
  monthly: boolean,
  regionId = '',
) {
  const start = Date.parse((monthly ? date.slice(0, 7) + '-01' : date) + 'T00:00:00+08:00'),
    d = new Date(start + 8 * 3600000),
    end = monthly ? Date.UTC(d.getUTCFullYear(), d.getUTCMonth() + 1, 1) - 8 * 3600000 : start + 86400000
  const fs = s.facilities.filter((f) => canSee(user, f) && (!regionId || f.regionId === regionId)),
    ids = new Set(fs.map((f) => f.id)),
    rows: { label: string; value: number }[] = []
  if (type === 'energy' || type === 'runtime') {
    for (const dev of s.phase2.devices.filter((d) => ids.has(d.facilityId))) {
      if (type === 'energy') {
        const e = energy(s.phase2.power, dev.id, start, end)
        if (e.kwh !== null) rows.push({ label: dev.name, value: Number(e.kwh.toFixed(2)) })
      } else if (
        s.phase2.deviceEvents.some((e) => e.deviceId === dev.id && Date.parse(e.time) < end) &&
        start < Date.parse(s.simulationTime)
      )
        rows.push({
          label: dev.name,
          value: Number(
            runtime(
              s.phase2.deviceEvents,
              dev.id,
              start,
              Math.min(end, Date.parse(s.simulationTime)),
            ).toFixed(2),
          ),
        })
    }
  } else if (type === 'inspection') {
    const tasks = s.inspections.filter(
      (t) =>
        t.status !== 'cancelled' &&
        t.checks.some((c) => ids.has(c.facilityId)) &&
        (user?.roleId !== 'operator' || t.assigneeId === user.id) &&
        (monthly ? t.plannedAt.startsWith(date.slice(0, 7)) : t.plannedAt === date),
    )
    if (tasks.length)
      rows.push(
        { label: '有效任务数', value: tasks.length },
        { label: '完成任务数', value: tasks.filter((t) => t.status === 'completed').length },
        {
          label: '已检查设施项',
          value: tasks.reduce((n, t) => n + t.checks.filter((c) => c.result).length, 0),
        },
        {
          label: '异常设施项',
          value: tasks.reduce((n, t) => n + t.checks.filter((c) => c.result === 'abnormal').length, 0),
        },
      )
  } else if (type === 'network') {
    const changes = s.phase2.changes.filter(
      (c) =>
        c.status === 'effective' &&
        user?.regionIds.includes(c.proposed.regionId) &&
        (!regionId || c.proposed.regionId === regionId) &&
        Date.parse(c.history[c.history.length - 1].time) >= start &&
        Date.parse(c.history[c.history.length - 1].time) < end,
    )
    if (changes.length)
      for (const [action, label] of [
        ['add', '生效新增'],
        ['edit', '生效修改'],
        ['delete', '生效删除'],
      ])
        rows.push({ label, value: changes.filter((c) => c.action === action).length })
  }
  return rows
}
