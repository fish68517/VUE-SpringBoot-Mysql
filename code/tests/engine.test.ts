import test from 'node:test'
import assert from 'node:assert/strict'
import { DemoEngine, STATE_KEY } from '../services/mock/engine'
import { makeState, clone, dmas, users } from '../repositories/seed'
import { validateState } from '../domain/validation'
import { dmaMetrics, dashboardMetrics, usageFor } from '../domain/metrics'
import { csvText } from '../platform/export'
import { safeRedirect } from '../navigation/routeMap'
import type { StorageAdapter } from '../domain/types'
function fixture() {
  const memory = new Map<string, string>()
  const storage: StorageAdapter = {
    get: (k) => memory.get(k) || null,
    set: (k, v) => {
      memory.set(k, v)
    },
    remove: (k) => {
      memory.delete(k)
    },
  }
  const engine = new DemoEngine(storage)
  engine.login('admin', users[0].password)
  return { engine, storage, memory }
}
test('三个场景均可加载；JSON主外键一致', () => {
  for (const id of ['normal', 'pressure-alarm', 'dma-leakage']) validateState(makeState(id))
})
test('密码错误不创建会话；禁用与未知账户不登录', () => {
  const { engine } = fixture()
  engine.logout()
  assert.throws(() => engine.login('admin', 'bad'))
  assert.equal(engine.user, null)
  assert.throws(() => engine.login('missing', users[0].password))
})
test('访客可看但所有业务变更被拒绝', () => {
  const { engine } = fixture()
  engine.login('guest', users[0].password)
  const before = clone(engine.state)
  assert.throws(() => engine.createFromAlarm('ALM-001', 'USER-002', '检查'))
  assert.throws(() => engine.reset('normal'))
  assert.throws(() =>
    engine.createTask({
      name: '任务',
      regionId: 'REG-001',
      assigneeId: 'USER-002',
      plannedAt: '2026-09-20',
      facilityIds: ['FAC-001'],
    }),
  )
  assert.deepEqual(engine.state, before)
})
test('操作员区域隔离且不能派单给其他人', () => {
  const { engine } = fixture()
  engine.login('operator', users[0].password)
  assert.throws(() => engine.facility('FAC-002'))
  assert.throws(() => engine.createFromAlarm('ALM-002', 'USER-002', '检查'))
  assert.throws(() => engine.createFromAlarm('ALM-001', 'USER-008', '检查'))
  assert.equal(dashboardMetrics(engine.state, engine.user).facilities, 20)
})
test('告警→工单→驳回→验收关闭，计数同步且重复创建幂等', () => {
  const { engine } = fixture()
  const count = dashboardMetrics(engine.state, engine.user).alarms
  const id = engine.createFromAlarm('ALM-001', 'USER-002', '检查压力')
  assert.equal(engine.createFromAlarm('ALM-001', 'USER-002', '重复点击'), id)
  assert.equal(engine.state.workorders.length, 41)
  assert.equal(engine.state.notifications.length, 2)
  assert.ok(engine.state.notifications.every((n) => n.status === 'simulated'))
  assert.equal(engine.state.alarms[0].status, 'accepted')
  engine.login('operator', users[0].password)
  engine.transitionOrder(id, 'assigned', 'start', '')
  assert.equal(engine.state.alarms[0].status, 'processing')
  assert.throws(() => engine.transitionOrder(id, 'processing', 'submit', ''))
  engine.transitionOrder(id, 'processing', 'submit', '已检查管线')
  assert.equal(engine.state.alarms[0].status, 'review')
  assert.throws(() => engine.transitionOrder(id, 'review', 'approve', ''))
  engine.login('admin', users[0].password)
  assert.throws(() => engine.transitionOrder(id, 'review', 'reject', ''))
  engine.transitionOrder(id, 'review', 'reject', '补充压力复测')
  assert.equal(engine.state.alarms[0].status, 'processing')
  engine.transitionOrder(id, 'processing', 'submit', '复测压力正常')
  engine.transitionOrder(id, 'review', 'approve', '通过')
  assert.equal(engine.state.alarms[0].status, 'closed')
  assert.equal(dashboardMetrics(engine.state, engine.user).alarms, count - 1)
  assert.throws(() => engine.transitionOrder(id, 'review', 'approve', '重复'))
})
test('非本人已分配工单不能被操作员处理', () => {
  const { engine } = fixture()
  const id = engine.createFromAlarm('ALM-001', 'USER-008', '检查')
  engine.login('operator', users[0].password)
  assert.throws(() => engine.transitionOrder(id, 'assigned', 'start', ''))
})
test('错误状态不产生历史，工单派发必须选区域内操作员', () => {
  const { engine } = fixture()
  assert.throws(() => engine.transitionOrder('WO-004', 'pending', 'dispatch', 'USER-002'))
  const before = clone(engine.state)
  assert.throws(() => engine.transitionOrder('WO-004', 'pending', 'submit', '直接完成'))
  assert.deepEqual(engine.state, before)
})
test('存储失败不提交内存状态、不显示假成功', () => {
  const { engine, storage } = fixture()
  const before = clone(engine.state)
  storage.set = () => {
    throw new Error('quota')
  }
  assert.throws(() => engine.createFromAlarm('ALM-001', 'USER-002', '检查'), /保存失败/)
  assert.deepEqual(engine.state, before)
})
test('新引擎恢复同机状态；重置清除新增记录并恢复数量', () => {
  const { engine, storage } = fixture()
  engine.createFromAlarm('ALM-001', 'USER-002', '检查')
  const next = new DemoEngine(storage)
  assert.equal(next.state.alarms[0].status, 'accepted')
  assert.equal(next.user?.id, 'USER-001')
  next.reset('pressure-alarm')
  assert.equal(next.state.workorders.length, 40)
  assert.equal(next.state.alarms[0].workOrderId, null)
  assert.equal(next.state.alarms[0].status, 'pending')
})
test('旧版本数据不静默覆盖；确认重置前禁止写入', () => {
  const { storage } = fixture()
  storage.set(STATE_KEY, JSON.stringify({ ...makeState(), version: 'old' }))
  const engine = new DemoEngine(storage)
  assert.ok(engine.warning)
  assert.throws(() => engine.createFromAlarm('ALM-001', 'USER-002', '检查'))
  engine.reset('normal')
  assert.equal(engine.warning, '')
  assert.ok(engine.state.alarms.every((a) => a.status === 'closed'))
})
test('新任务、必填检查、异常工单双向关联；任务完成不自动完成维修', () => {
  const { engine } = fixture()
  const id = engine.createTask({
    name: '中原路测试巡检',
    regionId: 'REG-001',
    assigneeId: 'USER-002',
    plannedAt: '2026-09-20',
    facilityIds: ['FAC-001', 'FAC-007'],
  })
  engine.login('operator', users[0].password)
  assert.throws(() => engine.completeTask(id))
  engine.checkTask(id, 'FAC-001', 'normal', '设施完好', 'ATT-001')
  const order = engine.checkTask(id, 'FAC-007', 'abnormal', '发现渗漏', null)
  assert.ok(order)
  assert.equal(engine.checkTask(id, 'FAC-007', 'abnormal', '复查渗漏', null), order)
  assert.throws(() => engine.checkTask(id, 'FAC-007', 'normal', '改为正常', null))
  engine.completeTask(id)
  assert.equal(engine.state.inspections.find((t) => t.id === id)?.status, 'completed')
  assert.equal(engine.state.workorders.find((w) => w.id === order)?.status, 'assigned')
  validateState(engine.state)
})
test('任务区域、非法设施、虚构附件及越权上报被拒绝', () => {
  const { engine } = fixture()
  assert.throws(() =>
    engine.createTask({
      name: '非法区域',
      regionId: 'REG-001',
      assigneeId: 'USER-002',
      plannedAt: '2026-09-20',
      facilityIds: ['FAC-002'],
    }),
  )
  engine.login('operator', users[0].password)
  assert.throws(() => engine.checkTask('TASK-008', 'FAC-008', 'normal', '正常', null))
  assert.throws(() => engine.checkTask('TASK-013', 'FAC-013', 'normal', '正常', 'missing'))
})
test('日报与月报计算口径可复核，DMA不吞掉负数和零分母', () => {
  const m = dmaMetrics(dmas[0])
  assert.equal(m.net, 11400)
  assert.equal(m.loss, 920)
  assert.equal(m.ratio, 10300 / 12000)
  assert.equal(dmaMetrics({ ...dmas[0], inlet: 0, transfer: 0 }).lossRate, null)
  assert.ok(dmaMetrics({ ...dmas[0], subMeter: 20000 }).loss < 0)
  const monthly = Array.from({ length: 30 }, (_, i) =>
    usageFor(['REG-001'], `2026-09-${String(i + 1).padStart(2, '0')}`),
  ).reduce((a, b) => a + b, 0)
  assert.equal(usageFor(['REG-001'], '2026-09-20', true), monthly)
})
test('回放时间保持北京时间且每次只推进指定间隔', () => {
  const { engine } = fixture()
  const before = Date.parse(engine.state.simulationTime)
  engine.advance(60)
  assert.equal(Date.parse(engine.state.simulationTime) - before, 60000)
  assert.ok(engine.state.simulationTime.endsWith('+08:00'))
})
test('所有设施所属DMA与区域一致，分配人有区域权限', () => {
  const s = makeState()
  for (const f of s.facilities) assert.equal(dmas.find((d) => d.id === f.dmaId)?.regionId, f.regionId)
  for (const w of s.workorders)
    if (w.assigneeId)
      assert.ok(
        users
          .find((u) => u.id === w.assigneeId)
          ?.regionIds.includes(s.facilities.find((f) => f.id === w.facilityId)!.regionId),
      )
})
test('CSV正确转义逗号双引号和公式文本；登录返回地址有白名单', () => {
  assert.equal(csvText([['=1+1', 'a,b', 'a"b']]), '\uFEFF"\'=1+1","a,b","a""b"')
  assert.equal(safeRedirect('https://evil.test'), '')
  assert.equal(safeRedirect('/pages/login/index'), '')
  assert.equal(safeRedirect('/pages/workorders/detail?id=WO-001'), '/pages/workorders/detail?id=WO-001')
})
test('损坏关联和重复ID数据不能作为有效快照使用', () => {
  const s = makeState()
  s.facilities.push(clone(s.facilities[0]))
  assert.throws(() => validateState(s))
  const t = makeState()
  t.alarms[0].workOrderId = 'missing'
  assert.throws(() => validateState(t))
})
