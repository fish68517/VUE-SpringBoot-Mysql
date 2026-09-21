import test from 'node:test'
import assert from 'node:assert/strict'
import { Phase2Engine } from '../services/mock/phase2-engine'
import { STATE_KEY } from '../services/mock/engine'
import { users, clone, makeState } from '../repositories/seed'
import { validateState } from '../domain/validation'
import { energy, runtime, inside, trackStats } from '../domain/phase2'
import { businessReport } from '../domain/reports'
import { dashboardMetrics, dmaMetrics } from '../domain/metrics'
const fixture = () => {
  const memory = new Map<string, string>()
  const storage = {
    get: (k: string) => memory.get(k) || null,
    set: (k: string, v: string) => {
      memory.set(k, v)
    },
    remove: (k: string) => {
      memory.delete(k)
    },
  }
  const e = new Phase2Engine(storage)
  e.login(users[0].username, users[0].password)
  return { e, storage, memory }
}

test('旧本机记录及快照更新内置标签，保留用户修改和业务状态', () => {
  const { e, storage } = fixture()
  const old = clone(e.state)
  old.facilities[0].description = '本地虚构设施，仅用于供水业务演示'
  old.facilities[1].description = '用户填写：演示检查备注，不应被自动改写'
  old.workorders[0].description = '按计划检查设施并填写现场处理结果（演示）。'
  old.workorders[0].result = '保留现场处理结果'
  old.alarms[0].history[0].actor = '陈明（演示）'
  old.phase2.videos[0].name = '中原区泵房 · 合成演示'
  old.logs.push({ time: old.simulationTime, actor: '系统模拟', text: '模拟设备设置 DEV-001 / 30 秒 / 在线' })
  storage.set(STATE_KEY, JSON.stringify(old))
  const restored = new Phase2Engine(storage)
  assert.equal(restored.warning, '')
  assert.equal(restored.state.facilities[0].description, '供水设施运行与维护档案')
  assert.equal(restored.state.facilities[1].description, old.facilities[1].description)
  assert.equal(restored.state.workorders[0].result, '保留现场处理结果')
  assert.equal(restored.state.alarms[0].history[0].actor, '陈明')
  assert.equal(restored.state.phase2.videos[0].name, '中原区泵房')
  assert.equal(restored.state.logs.at(-1)?.text, '设备设置 DEV-001 / 30 秒 / 在线')
  assert.equal(restored.state.revision, old.revision)
  assert.equal(restored.state.simulationTime, old.simulationTime)
  restored.login(users[0].username, users[0].password)
  restored.importSnapshot(JSON.stringify({ format: 'smart-water-snapshot', version: old.version, state: old }))
  assert.equal(restored.state.workorders[0].description, '按计划检查设施并填写现场处理结果。')
  assert.equal(restored.state.facilities[1].description, old.facilities[1].description)
})
test('六级审核逐步执行，第六级才写入台账并更新关联管线端点', () => {
  const { e } = fixture(),
    f = clone(e.state.facilities[0]),
    old = clone(f)
  f.name = '审核后名称'
  f.position = [100, 105]
  const id = e.propose('facility', 'edit', f)
  for (let i = 1; i < 6; i++) {
    e.review(id, i, true, '复核通过')
    assert.deepEqual(e.state.facilities[0], old)
  }
  e.review(id, 6, true, '终审通过')
  assert.equal(e.state.facilities[0].name, f.name)
  assert.deepEqual(e.state.phase2.pipes[0].path[0], f.position)
  assert.throws(() => e.review(id, 6, true, '重复'))
  assert.equal(
    businessReport(e.state, e.user, 'network', '2026-09-20', false).find((r) => r.label === '生效修改')
      ?.value,
    1,
  )
})
test('驳回后重新提交从一级开始，保留历史；审核配置冻结', () => {
  const { e } = fixture(),
    f = { ...e.state.facilities[0], name: '新名称' }
  const id = e.propose('facility', 'edit', f)
  e.review(id, 1, false, '名称需修正')
  const c = e.state.phase2.changes[0]
  assert.equal(c.status, 'rejected')
  const again = e.propose('facility', 'edit', { ...f, name: '已修正' }, id)
  assert.equal(again, id)
  assert.equal(e.state.phase2.changes[0].stage, 1)
  assert.equal(e.state.phase2.changes[0].history.length, 3)
  assert.throws(() => e.configureReviewers([users[1].id]))
  assert.throws(() => e.review(id, 2, true, '跳级'))
})
test('点线新增修改删除、引用保护及并发申请保护', () => {
  const { e } = fixture()
  assert.throws(() => e.propose('facility', 'delete', e.state.facilities[0]))
  const f = { ...clone(e.state.facilities[0]), id: 'FAC-NEW', deviceId: null, name: '新设施' }
  const approve = (id: string) => {
    for (let i = 1; i <= 6; i++) e.review(id, i, true, '通过')
  }
  approve(e.propose('facility', 'add', f))
  assert.ok(e.state.facilities.some((v) => v.id === f.id))
  approve(e.propose('facility', 'delete', f))
  assert.ok(!e.state.facilities.some((v) => v.id === f.id))
  const p = { ...clone(e.state.phase2.pipes[0]), name: '编辑管段', diameterMm: 400 }
  const id = e.propose('pipe', 'edit', p)
  assert.throws(() => e.propose('pipe', 'edit', p))
  approve(id)
  assert.equal(e.state.phase2.pipes[0].diameterMm, 400)
})
test('字段草稿必须同步后发布；新模板和区域路线校验', () => {
  const { e } = fixture()
  e.layer('draft', ['name', 'status'])
  assert.throws(() => e.layer('publish', ['name', 'status']))
  e.layer('sync', ['name', 'status'])
  e.layer('publish', ['name', 'status'])
  assert.deepEqual(e.state.phase2.layers.fields, ['name', 'status'])
  assert.throws(() =>
    e.saveTemplate({
      id: 'T',
      name: '坏模板',
      fields: [{ id: 'a', label: 'A', type: 'choice', required: true, options: [] }],
    }),
  )
  assert.throws(() =>
    e.saveRoute({ id: 'R', name: '跨区路线', regionId: 'REG-001', facilityIds: ['FAC-002'] }),
  )
})
test('模板快照与必填文本/数值/单选规则；改派不转移已存在异常维修单', () => {
  const { e } = fixture()
  const id = e.createTask({
    name: '模板巡检',
    regionId: 'REG-001',
    assigneeId: 'USER-002',
    plannedAt: '2026-09-20',
    facilityIds: ['FAC-001'],
    templateId: 'TPL-002',
  })
  assert.throws(() => e.checkTask(id, 'FAC-001', 'normal', '正常', null))
  assert.throws(() =>
    e.checkTask(id, 'FAC-001', 'normal', '正常', null, { pressure: '错误', environment: '整洁' }),
  )
  assert.throws(() =>
    e.checkTask(id, 'FAC-001', 'normal', '正常', null, { pressure: '0.3', environment: '未知' }),
  )
  const wo = e.checkTask(id, 'FAC-001', 'abnormal', '漏水', null, { pressure: '0.1', environment: '需清理' })
  const alternate = users.find(
    (u) => u.roleId === 'operator' && u.regionIds.includes('REG-001') && u.id !== 'USER-002',
  )!
  e.reassignTask(id, alternate.id, '调整人员')
  assert.equal(e.state.workorders.find((w) => w.id === wo)!.assigneeId, 'USER-002')
  e.completeTask(id)
  assert.equal(e.state.workorders.find((w) => w.id === wo)!.status, 'assigned')
})
test('操作员创建本人区域任务，不能给别人派任务；访客只读', () => {
  const { e } = fixture()
  e.login(users[1].username, users[1].password)
  const data = {
    name: '本人任务',
    regionId: 'REG-001',
    assigneeId: users[1].id,
    plannedAt: '2026-09-20',
    facilityIds: ['FAC-001'],
  }
  assert.ok(e.createTask(data))
  assert.throws(() => e.createTask({ ...data, assigneeId: users[2].id }))
  assert.throws(() => e.setThreshold('DMA-001', 0.1))
  assert.throws(() => e.propose('facility', 'edit', e.state.facilities[1]))
  const guest = users.find((u) => u.roleId === 'guest')!
  e.login(guest.username, guest.password)
  assert.throws(() => e.toggleValve(e.state.facilities.find((f) => f.type === 'valve')!.id))
  assert.throws(() => e.exportSnapshot())
})
test('取消任务排除完成率分母，保留业务记录且拒绝后续上报', () => {
  const { e } = fixture()
  const t = e.state.inspections.find((t) => t.status === 'pending')!
  const before = dashboardMetrics(e.state, e.user)
  e.cancelTask(t.id, '演示取消')
  assert.equal(dashboardMetrics(e.state, e.user).tasks, before.tasks - 1)
  assert.throws(() => e.checkTask(t.id, t.checks[0].facilityId, 'normal', '正常', null))
  assert.throws(() => e.completeTask(t.id))
})
test('采样间隔决定样本数，离线停止采样，保留上限；阀门只改模拟状态', () => {
  const { e } = fixture()
  e.deviceSetting('DEV-002', 10, true)
  e.advance(60)
  assert.equal(e.state.phase2.samples.filter((p) => p.deviceId === 'DEV-002').length, 6)
  e.deviceSetting('DEV-002', 10, false)
  e.advance(60)
  assert.equal(e.state.phase2.samples.filter((p) => p.deviceId === 'DEV-002').length, 6)
  assert.throws(() => e.deviceSetting('DEV-002', 0, true))
  assert.throws(() => e.advance(-1))
  const v = e.state.facilities.find((f) => f.type === 'valve')!,
    old = v.valveState
  e.toggleValve(v.id)
  assert.notEqual(e.state.facilities.find((f) => f.id === v.id)!.valveState, old)
  assert.throws(() => e.toggleValve('FAC-001'))
  e.advance(3600)
  assert.ok(e.state.phase2.samples.length <= 3000)
})
test('运行时长正确截断跨日事件；能耗优先电表、积分回退、缺测不补', () => {
  const from = Date.parse('2026-09-20T00:00:00+08:00'),
    to = from + 86400000
  const events = [
    { deviceId: 'D', time: '2026-09-19T23:00:00+08:00', on: true },
    { deviceId: 'D', time: '2026-09-20T02:00:00+08:00', on: false },
    { deviceId: 'D', time: '2026-09-20T23:00:00+08:00', on: true },
  ]
  assert.equal(runtime(events, 'D', from, to), 3)
  const points = [
    { deviceId: 'D', time: '2026-09-20T00:00:00+08:00', kw: 10, meterKwh: 100 },
    { deviceId: 'D', time: '2026-09-20T01:00:00+08:00', kw: 20, meterKwh: 112 },
  ]
  assert.equal(energy(points, 'D', from, to).kwh, 12)
  assert.equal(
    energy(
      points.map((p) => ({ ...p, meterKwh: null })),
      'D',
      from,
      to,
    ).kwh,
    15,
  )
  assert.equal(
    energy([{ ...points[0] }, { ...points[1], time: '2026-09-20T03:00:00+08:00' }], 'D', from, to).kwh,
    null,
  )
})
test('模拟通知尊重渠道与级别；DMA 阈值和预设漏损场景', () => {
  const { e } = fixture()
  e.notices({ sms: false, email: true, levels: ['urgent'] })
  const urgent = e.state.alarms.find((a) => a.level === 'urgent')!
  e.notify(urgent.id)
  assert.deepEqual(
    e.state.notifications.map((n) => n.channel),
    ['email'],
  )
  const normal = e.state.alarms.find((a) => a.level === 'normal')!
  e.notify(normal.id)
  assert.equal(e.state.notifications.length, 1)
  e.setThreshold('DMA-001', 0.05)
  assert.equal(e.state.phase2.dmas[0].threshold, 0.05)
  e.reset('dma-leakage')
  const d = e.state.phase2.dmas[2]
  assert.ok(dmaMetrics(d).lossRate! > d.threshold)
})
test('快照无账号密码，导入完整恢复，损坏导入原子拒绝；第一阶段兼容保留', () => {
  const { e, memory, storage } = fixture()
  e.cards(['video', 'map'])
  const text = e.exportSnapshot()
  assert.ok(!text.includes('password'))
  e.cards(['map'])
  e.importSnapshot(text)
  assert.deepEqual(e.state.phase2.portalCards['USER-001'], ['video', 'map'])
  const revision = e.state.revision
  const bad = JSON.parse(text)
  bad.state.phase2.pipes[0].fromFacilityId = 'missing'
  assert.throws(() => e.importSnapshot(JSON.stringify(bad)))
  assert.equal(e.state.revision, revision)
  const old: any = clone(makeState())
  old.version = 'phase1-20260920-v1'
  delete old.phase2
  old.workorders[0].title = '第一阶段保留工单'
  memory.set(STATE_KEY, JSON.stringify(old))
  const restored = new Phase2Engine(storage)
  assert.equal(restored.warning, '')
  assert.equal(restored.state.workorders[0].title, old.workorders[0].title)
  validateState(restored.state)
})
test('矩形和凹多边形判定、边界点与轨迹里程按样例比例计算', () => {
  assert.ok(
    inside(
      [0, 0],
      [
        [0, 0],
        [10, 0],
        [10, 10],
        [0, 10],
      ],
    ),
  )
  assert.ok(
    inside(
      [5, 5],
      [
        [0, 0],
        [10, 0],
        [10, 10],
        [0, 10],
      ],
    ),
  )
  assert.ok(
    !inside(
      [15, 5],
      [
        [0, 0],
        [10, 0],
        [10, 10],
        [0, 10],
      ],
    ),
  )
  assert.equal(
    trackStats({
      taskId: 'T',
      points: [
        { position: [0, 0], time: '2026-09-20T08:00:00+08:00' },
        { position: [3, 4], time: '2026-09-20T09:00:00+08:00' },
      ],
    }).km,
    0.05,
  )
})
