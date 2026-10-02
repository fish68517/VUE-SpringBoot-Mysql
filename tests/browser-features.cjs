// 本地 HTTP + Mock 数据验证，不访问真实云空间。
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const http = require('node:http');
const { chromium } = require(process.env.PLAYWRIGHT_MODULE_PATH || 'playwright');
const root = path.resolve(__dirname, '..');
const output = path.join(root, 'artifacts/local-tests');
fs.mkdirSync(output, { recursive: true });
const freshState = () => ({
  projects: [
    { id: 'project-a', unit: '测试甲物业', nature: '自行检测', receivable: 600, receivedAmount: 50, createdBy: 'operator1', date: '2026-10-02', dueDate: '2027-01-01' },
    { id: 'project-b', unit: '测试乙物业', nature: '自行检测', receivable: 400, receivedAmount: 0, createdBy: 'operator2', date: '2026-10-02', dueDate: '2027-02-01' },
  ],
  payments: [{ id: 'pay-unmatched', payer: '测试付款公司', amount: 1000, date: '2026-10-02', bank: '测试银行', createdBy: 'admin', status: '待确认' }],
  properties: Array.from({ length: 20 }, (_, i) => ({ id: `device-${i}`, unit: `测试物业${i}`, maintainer: i < 2 ? '甲维保公司' : '乙维保公司', internalNo: `T${i}`, deviceCode: `MOCK${i}`, dueDate: '2027-01-01' })),
  settings: { reminderDays: 30 }, _revisions: { shared: 1, admin: 1, operator1: 1, operator2: 1 },
});
let saved = freshState();
let saveCount = 0;
const errors = [];
const checks = [];
const user = role => ({ id: role === 'admin' ? 'admin' : 'operator1', name: '本地测试账号', role, mustChangePassword: false, permissions: { canExport: role === 'admin', canRecordPayments: role === 'admin' } });

const server = http.createServer(async (req, res) => {
  try {
    const pathname = decodeURIComponent(new URL(req.url, 'http://localhost').pathname);
    if (pathname === '/mock-api') {
      let body = ''; for await (const chunk of req) body += chunk;
      const input = JSON.parse(body);
      const role = req.headers.authorization?.includes('operator') ? 'operator' : 'admin';
      let data;
      if (input.action === 'login') data = { token: input.username === 'operator' ? 'operator-token' : 'admin-token', user: user(input.username === 'operator' ? 'operator' : 'admin') };
      else if (input.action === 'me') data = { user: user(role) };
      else if (input.action === 'loadMeta') data = { settings: saved.settings, _revisions: saved._revisions };
      else if (input.action === 'loadChunk') data = { items: saved[input.section], nextOffset: null };
      else if (input.action === 'listUsers') data = { users: [user('admin'), user('operator')] };
      else if (input.action === 'logout') data = { loggedOut: true };
      else if (input.action === 'save') {
        assert.equal(role, 'admin');
        saved = { ...structuredClone(input.state), _revisions: Object.fromEntries(Object.entries(saved._revisions).map(([k, v]) => [k, v + 1])) };
        saveCount++;
        data = { saved: true, revisions: saved._revisions };
      } else throw new Error(`Unexpected mock action: ${input.action}`);
      res.writeHead(200, { 'Content-Type': 'application/json' });
      return res.end(JSON.stringify({ code: 0, data }));
    }
    if (pathname === '/cloud-config.js') {
      res.writeHead(200, { 'Content-Type': 'text/javascript' });
      return res.end(`export const CLOUD_CONFIG = {enabled:true,apiBaseUrl:'https://ledger-test.invalid/api',syncDelayMs:20,requestTimeoutMs:15000,maxPdfBytes:1048576};`);
    }
    const dist = path.join(root, 'dist');
    const file = path.resolve(dist, `.${pathname === '/' ? '/index.html' : pathname}`);
    if (!file.startsWith(dist + path.sep) || !fs.existsSync(file)) { res.writeHead(404); return res.end(); }
    const types = { '.html': 'text/html', '.js': 'text/javascript', '.mjs': 'text/javascript', '.css': 'text/css' };
    res.writeHead(200, { 'Content-Type': `${types[path.extname(file)] || 'application/octet-stream'}; charset=utf-8`, 'Cache-Control': 'no-store' });
    res.end(fs.readFileSync(file));
  } catch (error) {
    errors.push(error.message); res.writeHead(500); res.end(JSON.stringify({ code: 500, message: error.message }));
  }
});

(async () => {
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  const base = `http://127.0.0.1:${server.address().port}`;
  const mockRoute = async route => {
    if (route.request().url() === 'https://ledger-test.invalid/api') {
      const response = await route.fetch({ url: `${base}/mock-api` });
      return route.fulfill({ response, headers: { ...response.headers(), 'access-control-allow-origin': '*' } });
    }
    return route.request().url().startsWith(base + '/') ? route.continue() : route.abort();
  };
  const browser = await chromium.launch({ channel: process.env.BROWSER_CHANNEL || 'msedge', headless: true });
  try {
    const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } });
    await context.route('**/*', mockRoute);
    const page = await context.newPage();
    page.on('pageerror', error => errors.push(error.message));
    await page.goto(base);
    await page.locator('#loginForm input[name=username]').fill('admin');
    await page.locator('#loginForm input[name=password]').fill('local-test-only');
    await page.locator('#loginForm button').click();
    await page.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await page.locator('[data-go=payments]').first().click();
    const card = page.locator('[data-payment-match="pay-unmatched"]');
    await card.locator('[data-allocation-project]').selectOption('project-a');
    await card.locator('[data-allocation-amount]').fill('600');
    await card.locator('[data-add-allocation]').click();
    await card.locator('[data-allocation-project]').nth(1).selectOption('project-b');
    await card.locator('[data-allocation-amount]').nth(1).fill('399');
    await card.locator('[data-manual-match]').click();
    await page.getByText('各项目关联金额合计必须等于本笔到账金额', { exact: true }).waitFor();
    assert.equal(saveCount, 0);
    checks.push('金额合计错误阻止入账');
    await card.locator('[data-allocation-amount]').nth(1).fill('400');
    await page.screenshot({ path: path.join(output, 'payments-allocation.png'), fullPage: true, animations: 'disabled', style: '#toastStack { visibility: hidden; }' });
    await page.setViewportSize({ width: 390, height: 844 });
    assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth + 1));
    await page.screenshot({ path: path.join(output, 'payments-mobile.png'), fullPage: true, animations: 'disabled', style: '#toastStack { visibility: hidden; }' });
    await page.setViewportSize({ width: 1440, height: 1000 });
    checks.push('390px 多项目填写面板无整体横向溢出');
    await card.locator('[data-manual-match]').click();
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    assert.deepEqual(saved.projects.map(p => p.receivedAmount), [650, 400]);
    assert.equal(saved.payments[0].allocations.length, 2);
    checks.push('多项目分配及保存');
    await page.reload();
    await page.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await page.locator('[data-go=payments]').first().click();
    await page.getByText('已关联 2 个项目', { exact: true }).waitFor();
    assert.equal(await page.locator('[data-payment-match]').count(), 0);
    checks.push('刷新后多项目关联保留');
    page.once('dialog', dialog => dialog.dismiss());
    await page.locator('[data-delete-payment]').click();
    assert.equal(saved.payments.length, 1);
    page.once('dialog', dialog => dialog.accept());
    await page.locator('[data-delete-payment]').click();
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    assert.deepEqual(saved.projects.map(p => p.receivedAmount), [50, 0]);
    assert.equal(saved.payments.length, 0);
    checks.push('删除取消不改数据；确认删除撤回分配金额');

    await page.locator('[data-view=alerts]').click();
    await page.locator('#propertyMaintainerFilter').selectOption({ label: '甲维保公司' });
    assert.equal(await page.locator('#propertyTable tr').count(), 2);
    await page.locator('[data-select-property="device-0"]').check();
    assert.equal(await page.locator('#propertySelectAll').evaluate(el => el.indeterminate), true);
    await page.locator('#propertyMaintainerFilter').selectOption({ label: '乙维保公司' });
    assert.equal(await page.locator('[data-select-property]:checked').count(), 0);
    await page.locator('#propertyMaintainerFilter').selectOption({ label: '甲维保公司' });
    checks.push('维保单位筛选、半选状态、筛选切换清除勾选');
    await page.locator('.property-date-edit[data-edit-property="device-0"]').click();
    await page.locator('#propertyEditForm input[name=dueDate]').fill('2028-02-29');
    await page.locator('#propertyEditForm button[type=submit]').click();
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    assert.equal(saved.properties.find(p => p.id === 'device-0').dueDate, '2028-02-29');
    await page.locator('.property-date-edit[data-edit-property="device-0"]').filter({ hasText: '2028-02' }).waitFor();
    checks.push('手动修改下次检验日期并同步到期年月');

    // 横向和纵向滚动同时检查表头/首列固定，避免重叠或首列滚出视口。
    await page.locator('#propertyMaintainerFilter').selectOption('');
    const pinned = await page.locator('.alert-table-wrap').evaluate(wrapper => {
      const first = wrapper.querySelector('tbody tr td:first-child');
      const left = first.getBoundingClientRect().left;
      wrapper.scrollLeft = 800; wrapper.scrollTop = 250;
      return new Promise(resolve => requestAnimationFrame(() => resolve({ before: left, after: first.getBoundingClientRect().left, headerTop: wrapper.querySelector('thead th').getBoundingClientRect().top, wrapperTop: wrapper.getBoundingClientRect().top, scrollLeft: wrapper.scrollLeft, scrollTop: wrapper.scrollTop })));
    });
    assert.ok(pinned.scrollLeft > 0 && pinned.scrollTop > 0);
    assert.ok(Math.abs(pinned.before - pinned.after) < 2);
    assert.ok(Math.abs(pinned.headerTop - pinned.wrapperTop) < 3);
    checks.push('到期年月列横向固定、表头纵向固定');
    await page.locator('.alert-table-wrap').evaluate(el => { el.scrollLeft = 0; el.scrollTop = 0; });
    await page.screenshot({ path: path.join(output, 'alerts-desktop.png'), fullPage: true, animations: 'disabled', style: '#toastStack { visibility: hidden; }' });
    await page.locator('#propertyMaintainerFilter').selectOption({ label: '甲维保公司' });
    await page.locator('#propertySelectAll').check();
    page.once('dialog', dialog => dialog.dismiss());
    await page.locator('#deleteSelectedProperties').click();
    assert.equal(saved.properties.length, 20);
    page.once('dialog', dialog => dialog.accept());
    await page.locator('#deleteSelectedProperties').click();
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    assert.equal(saved.properties.length, 18);
    assert.ok(saved.properties.every(p => p.maintainer === '乙维保公司'));
    checks.push('批量删除取消不生效；确认仅删除筛选选中设备');
    await page.reload();
    await page.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await page.locator('[data-view=alerts]').click();
    assert.equal(await page.locator('#propertyTable tr').count(), 18);
    checks.push('批量删除刷新后保持');

    await page.setViewportSize({ width: 390, height: 844 });
    await page.screenshot({ path: path.join(output, 'alerts-mobile.png'), fullPage: true, animations: 'disabled', style: '#toastStack { visibility: hidden; }' });
    assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth + 1));
    checks.push('390px 页面无整体横向溢出');

    const operator = await browser.newContext({ viewport: { width: 1440, height: 1000 } });
    await operator.route('**/*', mockRoute);
    const opPage = await operator.newPage();
    opPage.on('pageerror', error => errors.push(error.message));
    await opPage.goto(base);
    await opPage.locator('#loginForm input[name=username]').fill('operator');
    await opPage.locator('#loginForm input[name=password]').fill('local-test-only');
    await opPage.locator('#loginForm button').click();
    await opPage.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await opPage.locator('[data-view=alerts]').click();
    assert.equal(await opPage.locator('#deleteSelectedProperties').isVisible(), false);
    assert.equal(await opPage.locator('[data-select-property]').count(), 0);
    assert.equal(await opPage.locator('[data-edit-property]').count(), 0);
    await opPage.locator('[data-go=payments]').first().click();
    assert.equal(await opPage.locator('#unmatchedList').isVisible(), false);
    assert.equal(await opPage.locator('[data-delete-payment]').count(), 0);
    checks.push('业务员保留只读权限');
    assert.deepEqual(errors, []);
    fs.writeFileSync(path.join(output, 'browser-report.json'), JSON.stringify({ passed: true, environment: 'local HTTP and mock API, no live cloud requests', checks, errors }, null, 2));
    console.log(JSON.stringify({ passed: true, checks, output }, null, 2));
  } finally { await browser.close(); }
})().catch(error => { console.error(error); process.exitCode = 1; }).finally(() => server.close());
