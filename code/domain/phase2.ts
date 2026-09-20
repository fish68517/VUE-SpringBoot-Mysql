import type { Facility, History } from './types'
import type pipes from '../data/pipes.json'
import type dmas from '../data/dma.json'
import type devices from '../data/devices.json'
export type Pipe = (typeof pipes)[number]
export interface Field {
  id: string
  label: string
  type: 'text' | 'number' | 'choice'
  required: boolean
  options: string[]
}
export interface Template {
  id: string
  name: string
  fields: Field[]
}
export interface Route {
  id: string
  name: string
  regionId: string
  facilityIds: string[]
}
export interface Change {
  id: string
  kind: 'facility' | 'pipe'
  action: 'add' | 'edit' | 'delete'
  targetId: string
  before: Facility | Pipe | null
  proposed: Facility | Pipe
  status: 'pending' | 'rejected' | 'effective'
  stage: number
  reviewers: string[]
  createdBy: string
  history: History[]
}
export interface Track {
  taskId: string
  points: { time: string; position: number[] }[]
}
export interface Phase2 {
  samples: { deviceId: string; time: string; value: number }[]
  pipes: Pipe[]
  dmas: (typeof dmas)[number][]
  devices: (typeof devices)[number][]
  changes: Change[]
  reviewers: string[]
  templates: Template[]
  routes: Route[]
  tracks: Track[]
  deviceEvents: { deviceId: string; time: string; on: boolean }[]
  power: { deviceId: string; time: string; kw: number; meterKwh: number | null }[]
  dmaHistory: {
    dmaId: string
    date: string
    inlet: number
    transfer: number
    subMeter: number
    unmetered: number
  }[]
  layers: { draftFields: string[]; fields: string[]; publishedAt: string; syncedAt: string }
  noticeConfig: { sms: boolean; email: boolean; levels: string[] }
  portalCards: Record<string, string[]>
  energyPlans: { id: string; name: string; factor: number; minPressure: number; maxPressure: number }[]
  bursts: {
    id: string
    name: string
    pipeId: string
    facilityIds: string[]
    valveIds: string[]
    households: number
  }[]
  videos: { id: string; name: string; regionId: string; src: string; online: boolean }[]
}
export function inside(point: number[], polygon: number[][]) {
  let result = false
  for (let i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
    const a = polygon[i],
      b = polygon[j]
    const cross = (point[0] - a[0]) * (b[1] - a[1]) - (point[1] - a[1]) * (b[0] - a[0])
    if (
      Math.abs(cross) < 1e-7 &&
      point[0] >= Math.min(a[0], b[0]) &&
      point[0] <= Math.max(a[0], b[0]) &&
      point[1] >= Math.min(a[1], b[1]) &&
      point[1] <= Math.max(a[1], b[1])
    )
      return true
    if (
      a[1] > point[1] !== b[1] > point[1] &&
      point[0] < ((b[0] - a[0]) * (point[1] - a[1])) / (b[1] - a[1]) + a[0]
    )
      result = !result
  }
  return result
}
export function intersects(path: number[][], polygon: number[][]) {
  if (path.some((p) => inside(p, polygon))) return true
  const cross = (a: number[], b: number[], c: number[]) =>
    (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0])
  for (let i = 1; i < path.length; i++)
    for (let j = 0; j < polygon.length; j++) {
      const a = path[i - 1],
        b = path[i],
        c = polygon[j],
        d = polygon[(j + 1) % polygon.length]
      if (
        Math.max(a[0], b[0]) < Math.min(c[0], d[0]) ||
        Math.max(c[0], d[0]) < Math.min(a[0], b[0]) ||
        Math.max(a[1], b[1]) < Math.min(c[1], d[1]) ||
        Math.max(c[1], d[1]) < Math.min(a[1], b[1])
      )
        continue
      if (cross(a, b, c) * cross(a, b, d) <= 0 && cross(c, d, a) * cross(c, d, b) <= 0) return true
    }
  return false
}
export function runtime(events: Phase2['deviceEvents'], deviceId: string, from: number, to: number) {
  const list = events
    .filter((e) => e.deviceId === deviceId)
    .sort((a, b) => Date.parse(a.time) - Date.parse(b.time))
  let on = false,
    cursor = from,
    total = 0
  for (const e of list) {
    const t = Date.parse(e.time)
    if (t <= from) {
      on = e.on
      continue
    }
    if (t >= to) break
    if (on) total += t - cursor
    cursor = t
    on = e.on
  }
  if (on) total += to - cursor
  return Math.max(0, total / 3600000)
}
export function energy(points: Phase2['power'], deviceId: string, from: number, to: number) {
  const list = points
    .filter((p) => p.deviceId === deviceId)
    .sort((a, b) => Date.parse(a.time) - Date.parse(b.time))
  let total = 0,
    covered = 0
  for (let i = 1; i < list.length; i++) {
    const a = list[i - 1],
      b = list[i],
      start = Date.parse(a.time),
      end = Date.parse(b.time),
      hours = (end - start) / 3600000
    // Only complete adjacent hourly samples are counted; gaps and partial intervals stay missing.
    if (start < from || end > to || hours <= 0 || hours > 1.01) continue
    if (a.meterKwh !== null && b.meterKwh !== null) {
      if (b.meterKwh < a.meterKwh) continue
      total += b.meterKwh - a.meterKwh
    } else total += ((a.kw + b.kw) / 2) * hours
    covered += hours
  }
  return { kwh: covered ? total : null, hours: covered }
}
export function trackStats(track: Track | undefined) {
  const p = track?.points || []
  let distance = 0
  for (let i = 1; i < p.length; i++)
    distance += Math.hypot(p[i].position[0] - p[i - 1].position[0], p[i].position[1] - p[i - 1].position[1])
  return {
    km: distance * 0.01,
    hours: p.length > 1 ? (Date.parse(p[p.length - 1].time) - Date.parse(p[0].time)) / 3600000 : 0,
  }
}
