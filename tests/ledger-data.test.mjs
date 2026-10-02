import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import vm from 'node:vm';
import { createRequire } from 'node:module';

const source = fs.readFileSync(new URL('../dist/ledger-data.js', import.meta.url), 'utf8');
const { associatePayment, removePayment, detachProjectPayments, paymentAllocations, validInspectionDate } = await import(`data:text/javascript;base64,${Buffer.from(source).toString('base64')}`);
const fixture = () => ({
  projects: [{ id: 'a', receivedAmount: 50 }, { id: 'b', receivedAmount: 0 }],
  payments: [{ id: 'pay', amount: 100, date: '2026-10-02', payer: '测试付款单位', bank: '测试银行' }],
});

test('一笔流水分配多个项目，保留原有手工金额，重复确认不重复入账', () => {
  const state = fixture();
  const rows = [{ projectId: 'a', amount: '60.01' }, { projectId: 'b', amount: '39.99' }];
  associatePayment(state, 'pay', rows);
  associatePayment(state, 'pay', rows);
  assert.deepEqual(state.projects.map(p => p.receivedAmount), [110.01, 39.99]);
  assert.equal(state.payments[0].projectId, '');
  assert.equal(state.payments[0].status, '已确款');
});

for (const [name, rows] of [
  ['金额不平', [{ projectId: 'a', amount: '99' }]],
  ['重复项目', [{ projectId: 'a', amount: '50' }, { projectId: 'a', amount: '50' }]],
  ['无效项目', [{ projectId: 'missing', amount: '100' }]],
  ['负金额', [{ projectId: 'a', amount: '-1' }, { projectId: 'b', amount: '101' }]],
  ['零金额', [{ projectId: 'a', amount: '0' }, { projectId: 'b', amount: '100' }]],
  ['过多小数', [{ projectId: 'a', amount: '100.001' }]],
  ['空项目列表', []],
]) {
  test(`${name}拒绝保存且不产生部分修改`, () => {
    const state = fixture(), before = structuredClone(state);
    assert.throws(() => associatePayment(state, 'pay', rows));
    assert.deepEqual(state, before);
  });
}

test('删除多项目流水仅撤回对应分配，保留其他到款', () => {
  const state = fixture();
  associatePayment(state, 'pay', [{ projectId: 'a', amount: '60' }, { projectId: 'b', amount: '40' }]);
  state.payments.push({ id: 'other', amount: 20, date: '2026-10-03', payer: '另一付款单位' });
  associatePayment(state, 'other', [{ projectId: 'b', amount: '20' }]);
  removePayment(state, 'pay');
  assert.deepEqual(state.projects.map(p => p.receivedAmount), [50, 20]);
  assert.equal(state.projects[1].paymentCounterparty, '另一付款单位');
  assert.equal(state.payments.length, 1);
});

test('删除最后一笔流水清除自动填写的到款说明', () => {
  const state = fixture();
  associatePayment(state, 'pay', [{ projectId: 'b', amount: '100' }]);
  removePayment(state, 'pay');
  assert.equal(state.projects[1].receivedAmount, 0);
  assert.equal(state.projects[1].paymentDate, '');
  assert.equal(state.projects[1].paymentCounterparty, '');
});

test('旧单项目流水兼容删除；未关联流水删除不影响项目', () => {
  const state = fixture();
  state.projects[0].receivedAmount = 150;
  state.payments[0].projectId = 'a';
  assert.deepEqual(paymentAllocations(state.payments[0]), [{ projectId: 'a', amount: 100 }]);
  removePayment(state, 'pay');
  assert.equal(state.projects[0].receivedAmount, 50);
  state.payments.push({ id: 'unmatched', amount: 500 });
  removePayment(state, 'unmatched');
  assert.equal(state.projects[0].receivedAmount, 50);
});

test('删除一个关联项目后保留其他分配，补关联只增加差额', () => {
  const state = fixture();
  associatePayment(state, 'pay', [{ projectId: 'a', amount: '60' }, { projectId: 'b', amount: '40' }]);
  state.projects = state.projects.filter(p => p.id !== 'a');
  detachProjectPayments(state, 'a');
  assert.equal(state.payments[0].status, '部分关联');
  associatePayment(state, 'pay', [{ projectId: 'b', amount: '100' }]);
  assert.equal(state.projects[0].receivedAmount, 100);
});

test('按分校验避免 0.1 + 0.2 浮点误差', () => {
  const state = fixture();
  state.payments[0].amount = 0.3;
  associatePayment(state, 'pay', [{ projectId: 'a', amount: '0.1' }, { projectId: 'b', amount: '0.2' }]);
  assert.equal(state.projects[0].receivedAmount, 50.1);
});

test('检验日期接受真实日期或留空，拒绝不存在的日期', () => {
  for (const value of ['', '2028-02-29', '2026-12-31']) assert.equal(validInspectionDate(value), true);
  for (const value of ['2026-02-29', '2026-02-30', '2026-13-01', 'not-date', '0000-01-01']) assert.equal(validInspectionDate(value), false);
});

function cloudFixture() {
  const docs = new Map([
    ['shared-properties', { properties: [{ id: 'device1', dueDate: '2027-01-01' }, { id: 'device2' }], revision: 1 }],
    ['user-admin', { projects: [], payments: [{ id: 'pay', amount: 100, createdBy: 'admin', allocations: [{ projectId: 'a', amount: 60 }, { projectId: 'b', amount: 40 }] }], revision: 1 }],
    ['user-operator1', { projects: [{ id: 'a', createdBy: 'operator1', receivedAmount: 60 }], payments: [], revision: 1 }],
    ['user-operator2', { projects: [{ id: 'b', createdBy: 'operator2', receivedAmount: 40 }], payments: [], revision: 1 }],
  ]);
  const collection = { doc: id => ({
    get: async () => ({ data: docs.has(id) ? [structuredClone(docs.get(id))] : [] }),
    set: async value => { docs.set(id, structuredClone(value)); },
  }) };
  const context = { require: createRequire(import.meta.url), exports: {}, console, Buffer, uniCloud: { database: () => ({ collection: () => collection }) } };
  vm.createContext(context);
  vm.runInContext(fs.readFileSync(new URL('../uniCloud-aliyun/cloudfunctions/zsh-ledger-api/index.js', import.meta.url), 'utf8'), context);
  return { context, docs };
}

test('云函数按多项目关联返回业务员付款记录，隐藏其他业务员关联明细', async () => {
  const { context } = cloudFixture();
  const state = await context.readState({ _id: 'operator1', role: 'operator' });
  assert.equal(state.payments.length, 1);
  assert.deepEqual(JSON.parse(JSON.stringify(state.payments[0].allocations)), [{ projectId: 'a', amount: 60 }]);
  assert.equal(state.projects.length, 1);
});

test('云函数管理员保存删除流水、金额撤回、设备批删及日期修改，重读结果一致', async () => {
  const { context } = cloudFixture();
  const actor = { _id: 'admin', role: 'admin' };
  const state = await context.readState(actor);
  removePayment(state, 'pay');
  state.properties = state.properties.filter(p => p.id !== 'device2');
  state.properties[0].dueDate = '2028-02-29';
  await context.saveState(actor, state, state._revisions);
  const saved = await context.readState(actor);
  assert.equal(saved.payments.length, 0);
  assert.deepEqual(Array.from(saved.projects, p => p.receivedAmount), [0, 0]);
  assert.equal(saved.properties.length, 1);
  assert.equal(saved.properties[0].dueDate, '2028-02-29');
});

test('新增多项目关联经过云函数保存后完整返回，旧单项目数据仍可见', async () => {
  const { context, docs } = cloudFixture();
  const actor = { _id: 'admin', role: 'admin' };
  const state = await context.readState(actor);
  state.payments.push({ id: 'new', amount: 10, createdBy: 'admin', date: '2026-10-02', payer: '测试' });
  associatePayment(state, 'new', [{ projectId: 'a', amount: 3 }, { projectId: 'b', amount: 7 }]);
  await context.saveState(actor, state, state._revisions);
  const loaded = await context.readState(actor);
  assert.equal(loaded.payments.find(p => p.id === 'new').allocations.length, 2);
  assert.deepEqual(Array.from(loaded.projects, p => p.receivedAmount), [63, 47]);
  docs.get('user-admin').payments.push({ id: 'legacy', amount: 1, createdBy: 'admin', projectId: 'a' });
  const operator = await context.readState({ _id: 'operator1', role: 'operator' });
  assert.ok(operator.payments.some(p => p.id === 'legacy'));
});

test('云函数仍拒绝业务员借 save 修改收款、删除流水或共享设备', async () => {
  const { context, docs } = cloudFixture();
  const actor = { _id: 'operator1', role: 'operator' };
  const state = await context.readState(actor);
  state.projects[0].receivedAmount = 999;
  state.payments = [];
  state.properties = [];
  await context.saveState(actor, state, state._revisions);
  assert.equal(docs.get('user-operator1').projects[0].receivedAmount, 60);
  assert.equal(docs.get('user-admin').payments.length, 1);
  assert.equal(docs.get('shared-properties').properties.length, 2);
});

test('云函数冲突版本拒绝写入', async () => {
  const { context, docs } = cloudFixture();
  const actor = { _id: 'admin', role: 'admin' };
  const state = await context.readState(actor);
  state.properties = [];
  await assert.rejects(context.saveState(actor, state, { ...state._revisions, shared: 0 }), error => error.status === 409);
  assert.equal(docs.get('shared-properties').properties.length, 2);
});
