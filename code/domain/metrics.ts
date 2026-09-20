import type { State, User, Facility } from './types'
import { dmas, usage } from '../repositories/seed'
export const canSee = (user: User | null, facility: Facility | undefined) =>
  !!user && !!facility && (user.roleId !== 'operator' || user.regionIds.includes(facility.regionId))
export function dmaMetrics(dma: (typeof dmas)[number]) {
  const net = dma.inlet - dma.transfer,
    loss = net - dma.subMeter - dma.unmetered
  return {
    net,
    loss,
    lossRate: net > 0 ? loss / net : null,
    ratio: dma.inlet > 0 ? dma.subMeter / dma.inlet : null,
    salesGap: dma.produced - dma.sold,
    salesGapRate: dma.produced > 0 ? (dma.produced - dma.sold) / dma.produced : null,
  }
}
export function dashboardMetrics(state: State, user: User | null) {
  const facilities = state.facilities.filter((f) => canSee(user, f)),
    ids = new Set(facilities.map((f) => f.id))
  const alarms = state.alarms.filter((a) => ids.has(a.facilityId) && a.status !== 'closed')
  const orders = state.workorders.filter((w) => ids.has(w.facilityId))
  const tasks = state.inspections.filter(
    (t) => t.status !== 'cancelled' && user && (user.roleId !== 'operator' || t.assigneeId === user.id),
  )
  return {
    facilities: facilities.length,
    alarms: alarms.length,
    urgent: alarms.filter((a) => a.level === 'urgent').length,
    orders: orders.filter((w) => w.status !== 'completed').length,
    tasks: tasks.length,
    completion: tasks.length ? tasks.filter((t) => t.status === 'completed').length / tasks.length : null,
  }
}
export function usageFor(regionIds: string[], date: string, monthly = false) {
  return usage
    .filter((r) => (monthly ? r.date.startsWith(date.slice(0, 7)) : r.date === date))
    .reduce(
      (sum, r) =>
        sum + r.regions.filter((v) => regionIds.includes(v.regionId)).reduce((s, v) => s + v.volume, 0),
      0,
    )
}
export const percent = (n: number | null) => (n === null ? '—' : (n * 100).toFixed(1) + '%')
export function formatTime(t: string) {
  return t ? t.replace('T', ' ').replace('+08:00', '').slice(0, 19) : '—'
}
