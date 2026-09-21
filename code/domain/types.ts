import type { Phase2, Template } from './phase2'
export interface User {
  id: string
  username: string
  password: string
  displayName: string
  roleId: string
  regionIds: string[]
  enabled: boolean
}
export interface History {
  time: string
  actor: string
  text: string
}
export interface Facility {
  id: string
  name: string
  type: string
  regionId: string
  dmaId: string
  position: number[]
  status: string
  deviceId: string | null
  valveState: string
  description: string
}
export interface Alarm {
  id: string
  title: string
  facilityId: string
  dmaId: string
  level: string
  status: string
  occurredAt: string
  ruleId: string
  value: number
  unit: string
  assigneeId: string | null
  workOrderId: string | null
  history: History[]
}
export interface WorkOrder {
  occurredAt?: string
  id: string
  title: string
  type: string
  status: string
  facilityId: string
  assigneeId: string | null
  createdBy: string
  sourceType: string
  sourceId: string | null
  createdAt: string
  description: string
  result: string
  steps: History[]
}
export interface Check {
  values?: Record<string, string>
  reportedAt?: string
  facilityId: string
  result: string
  description: string
  workOrderId: string | null
  attachmentId: string | null
}
export interface Task {
  template?: Template
  id: string
  name: string
  regionId: string
  assigneeId: string
  status: string
  plannedAt: string
  createdBy: string
  routeId: string
  templateId: string
  checks: Check[]
  history: History[]
}
export interface State {
  phase2: Phase2
  version: string
  scenarioId: string
  simulationTime: string
  revision: number
  facilities: Facility[]
  alarms: Alarm[]
  workorders: WorkOrder[]
  inspections: Task[]
  logs: History[]
  notifications: { id: string; alarmId: string; channel: string; status: string }[]
}
export interface StorageAdapter {
  get(key: string): string | null
  set(key: string, value: string): void
  remove(key: string): void
}
export type Result<T> =
  | { success: true; data: T; error: null }
  | { success: false; data: null; error: { code: string; message: string } }
