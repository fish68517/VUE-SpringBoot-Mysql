import {
  makeState,
  users,
  permissions,
  pipes,
  pressureSeries,
  scenarios,
  devices,
} from '../repositories/seed'
import { validateState } from '../domain/validation'
for (const scenario of scenarios) validateState(makeState(scenario.id))
const state = makeState(),
  ids = new Set(state.facilities.map((f) => f.id))
for (const p of pipes)
  if (!ids.has(p.fromFacilityId) || !ids.has(p.toFacilityId)) throw new Error('管线端点不存在 ' + p.id)
for (const d of devices)
  if (
    !ids.has(d.facilityId) ||
    !Number.isInteger(d.samplingIntervalSec) ||
    d.samplingIntervalSec < 1 ||
    d.samplingIntervalSec > 300
  )
    throw new Error('设备数据无效 ' + d.id)
for (const series of pressureSeries) {
  if (!ids.has(series.entityId)) throw new Error('曲线引用无效')
  let previous = 0
  for (const p of series.points) {
    const time = Date.parse(p.time)
    if (!Number.isFinite(time) || time <= previous || !Number.isFinite(p.value))
      throw new Error('曲线值或时间无效')
    previous = time
  }
}
for (const u of users) if (!(u.roleId in permissions) || !u.password) throw new Error('角色或演示密码未配置')
console.log(
  JSON.stringify(
    {
      ok: true,
      scenarios: scenarios.length,
      facilities: state.facilities.length,
      pipes: pipes.length,
      alarms: state.alarms.length,
      workorders: state.workorders.length,
      tasks: state.inspections.length,
      devices: devices.length,
    },
    null,
    2,
  ),
)
