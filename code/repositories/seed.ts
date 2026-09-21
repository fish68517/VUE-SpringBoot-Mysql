import seedTemplates from '../data/inspection-templates.json'
import seedRoutes from '../data/inspection-routes.json'
import seedPipes from '../data/pipes.json'
import seedDmas from '../data/dma.json'
import seedDevices from '../data/devices.json'
import phase2 from '../data/phase2.json'
import type { Phase2 } from '../domain/phase2'
import manifest from '../data/manifest.json'
import facilities from '../data/facilities.json'
import alarms from '../data/alarms.json'
import workorders from '../data/workorders.json'
import inspections from '../data/inspections.json'
import scenarios0 from '../data/scenarios/pressure-alarm.json'
import scenarios1 from '../data/scenarios/normal.json'
import scenarios2 from '../data/scenarios/dma-leakage.json'
import type { State } from '../domain/types'
export { manifest }
export { default as users } from '../account/users.json'
export { default as roles } from '../account/roles.json'
export { default as permissions } from '../account/permissions.json'
export { default as regions } from '../data/regions.json'
export { default as pipes } from '../data/pipes.json'
export { default as dmas } from '../data/dma.json'
export { default as devices } from '../data/devices.json'
export { default as pressureSeries } from '../data/metrics/series.json'
export { default as usage } from '../data/metrics/usage.json'
export { default as portal } from '../data/portal.json'
export { default as dictionary } from '../data/dictionaries.json'
export { default as attachments } from '../data/assets/attachments.json'
export const scenarios = [scenarios0, scenarios1, scenarios2]
export const clone = <T>(v: T): T => JSON.parse(JSON.stringify(v))
export function makeState(scenarioId = manifest.defaultScenario): State {
  const scenario = scenarios.find((s) => s.id === scenarioId)
  if (!scenario) throw new Error('未知业务场景')
  return {
    version: manifest.datasetVersion,
    phase2: {
      ...clone(phase2),
      templates: clone(seedTemplates),
      routes: clone(seedRoutes),
      dmaHistory: clone(phase2.dmaHistory).map((h) =>
        scenarioId === 'dma-leakage' && h.dmaId === 'DMA-003' && h.date === '2026-09-20'
          ? { ...h, subMeter: Math.round(h.subMeter * 0.8) }
          : h,
      ),
      pipes: clone(seedPipes),
      devices: clone(seedDevices),
      dmas: clone(seedDmas).map((d) =>
        scenarioId === 'dma-leakage' && d.id === 'DMA-003'
          ? { ...d, subMeter: Math.round(d.subMeter * 0.8) }
          : d,
      ),
    } as Phase2,
    scenarioId,
    simulationTime: scenario.time,
    revision: 0,
    facilities: clone(facilities),
    alarms: clone(alarms).map((a) => ({ ...a, status: scenario.closed ? 'closed' : a.status })),
    workorders: clone(workorders),
    inspections: clone(inspections),
    logs: [],
    notifications: [],
  }
}
