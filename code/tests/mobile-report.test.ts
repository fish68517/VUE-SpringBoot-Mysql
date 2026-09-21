import test from 'node:test'
import assert from 'node:assert/strict'
import { Phase2Engine } from '../services/mock/phase2-engine'
import { clone, users } from '../repositories/seed'

test('现场发生时间独立于创建时间，拒绝无效日期，并跨重载和快照保留', () => {
  const memory = new Map<string, string>()
  const storage = {
    get: (key: string) => memory.get(key) || null,
    set: (key: string, value: string) => {
      memory.set(key, value)
    },
    remove: (key: string) => {
      memory.delete(key)
    },
  }
  const engine = new Phase2Engine(storage)
  engine.login(users[1].username, users[1].password)
  const input = {
    title: '现场上报',
    type: 'repair',
    facilityId: 'FAC-001',
    assigneeId: users[1].id,
    description: '现场检查记录',
  }
  const before = engine.state.workorders.length
  for (const occurredAt of ['', 'bad', '2026-02-30T08:35:00+08:00', '2026-09-19T25:35:00+08:00']) {
    assert.throws(() => engine.createOrder({ ...input, occurredAt }), /发生时间/)
    assert.equal(engine.state.workorders.length, before)
  }
  const occurredAt = '2026-09-19T08:35:00+08:00'
  const id = engine.createOrder({ ...input, occurredAt })
  const order = engine.state.workorders.find((w) => w.id === id)!
  assert.equal(order.occurredAt, occurredAt)
  assert.equal(order.createdAt, engine.state.simulationTime)
  assert.notEqual(order.createdAt, order.occurredAt)
  const restored = new Phase2Engine(storage)
  assert.equal(restored.warning, '')
  assert.equal(restored.state.workorders.find((w) => w.id === id)?.occurredAt, occurredAt)
  restored.login(users[0].username, users[0].password)
  const snapshot = {
    format: 'smart-water-snapshot',
    version: restored.state.version,
    state: clone(restored.state),
  }
  restored.importSnapshot(JSON.stringify(snapshot))
  assert.equal(restored.state.workorders.find((w) => w.id === id)?.occurredAt, occurredAt)
  snapshot.state.workorders.find((w) => w.id === id)!.occurredAt = '2026-02-30T08:35:00+08:00'
  assert.throws(() => restored.importSnapshot(JSON.stringify(snapshot)))
  assert.equal(restored.state.workorders.find((w) => w.id === id)?.occurredAt, occurredAt)
  // Existing non-report orders and older snapshots need no backfill or invented occurrence time.
  assert.doesNotThrow(() => restored.createOrder(input))
})
