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
    { id: 'project-a', attachmentName: '示例.pdf', attachmentUrl: 'sample/恒拓检测1台-吉林金科陶瓷有限公司-9月17日.pdf', unit: '测试甲物业', nature: '定期检验', provincialPayment: 964.44, receivable: 600, receivedAmount: 50, createdBy: 'operator1', date: '2026-10-02', dueDate: '2027-01-01' },
    { id: 'project-b', inspector: '历史检验员', invoice: '旧发票001', report: '报告009', unit: '测试乙物业', nature: '定期检验', provincialPayment: 20, receivable: 400, receivedAmount: 0, createdBy: 'operator2', date: '2026-10-02', dueDate: '2027-02-01' },
  ],
  payments: [{ id: 'pay-unmatched', payer: '测试付款公司', amount: 1000, date: '2026-10-02', bank: '测试银行', createdBy: 'admin', status: '待确认' }],
  properties: Array.from({ length: 20 }, (_, i) => ({ id: `device-${i}`, unit: `测试物业${i}`, maintainer: i < 2 ? '甲维保公司' : '乙维保公司', internalNo: `T${i}`, deviceCode: `MOCK${i}`, dueDate: '2027-01-01' })),
  settings: { reminderDays: 30 }, _revisions: { shared: 1, admin: 1, operator1: 1, operator2: 1 },
});
let saved = freshState();
let saveCount = 0;
let uploadedFiles = [];
let propertyImportCount = 0;
const errors = [];
const checks = [];
const user = role => ({ id: role === 'admin' ? 'admin' : role === 'operator2' ? 'operator2' : 'operator1', name: '本地测试账号', role: role === 'admin' ? 'admin' : 'operator', mustChangePassword: false, permissions: { canExport: role === 'admin', canRecordPayments: role === 'admin' } });

const server = http.createServer(async (req, res) => {
  try {
    const pathname = decodeURIComponent(new URL(req.url, 'http://localhost').pathname);
    if (pathname === '/mock-api') {
      let body = ''; for await (const chunk of req) body += chunk;
      const input = JSON.parse(body);
      const role = req.headers.authorization?.includes('operator2') ? 'operator2' : req.headers.authorization?.includes('operator') ? 'operator' : 'admin';
      let data;
      if (input.action === 'login') data = { token: `${input.username}-token`, user: user(input.username) };
      else if (input.action === 'me') data = { user: user(role) };
      else if (input.action === 'loadMeta') data = { settings: saved.settings, _revisions: saved._revisions };
      else if (input.action === 'loadChunk') data = { items: saved[input.section], nextOffset: null };
      else if (input.action === 'listUsers') data = { users: [user('admin'), user('operator')] };
      else if (input.action === 'uploadAttachment') { uploadedFiles.push(input.fileName); data = { fileID: `mock-file-${uploadedFiles.length}` }; }
      else if (input.action === 'importProperties') {
        propertyImportCount++;
        saved.properties.push(...input.properties);
        saved._revisions.shared++;
        data = { properties: saved.properties, revision: saved._revisions.shared };
      }
      else if (input.action === 'logout') data = { loggedOut: true };
      else if (input.action === 'save') {
        if (role !== 'admin') {
          const ownerId = role === 'operator2' ? 'operator2' : 'operator1';
          saved.projects = [...saved.projects.filter(p => p.createdBy !== ownerId), ...structuredClone(input.state.projects.filter(p => p.createdBy === ownerId))];
          saved._revisions[ownerId]++;
        } else saved = { ...structuredClone(input.state), _revisions: Object.fromEntries(Object.entries(saved._revisions).map(([k, v]) => [k, v + 1])) };
        saveCount++;
        data = { saved: true, revisions: saved._revisions };
      } else throw new Error(`Unexpected mock action: ${input.action}`);
      res.writeHead(200, { 'Content-Type': 'application/json' });
      return res.end(JSON.stringify({ code: 0, data }));
    }
    const dist = path.resolve(root, process.env.TEST_WEB_ROOT || 'dist/build/h5');
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
    if (route.request().url() === 'https://ledger-test.invalid/api' || route.request().url() === 'https://fc-mp-f4c892a2-7540-459d-9a6f-ccae78f87037.next.bspapp.com/zsh-ledger-api') {
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
    await page.locator('#loginForm input[name=username]').waitFor();
    assert.ok(await page.locator('#loginForm input[name=username]').evaluate(el => el.getBoundingClientRect().height >= 40));
    checks.push('原生表单 CSS 未被 uni-app 标签转换破坏');
    await page.locator('#loginForm input[name=username]').fill('admin');
    await page.locator('#loginForm input[name=password]').fill('local-test-only');
    await page.locator('#loginForm button').click();
    await page.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    // 四个菜单均使用实收门槛；应收金额故意设置成不同值，防止回退到旧判断。
    const statusCases = [
      { actual: 1285.92, receivedAmount: 1285.92, expected: '已确款' },
      { actual: 1285.92, receivedAmount: 1400, expected: '已确款' },
      { actual: 1285.92, receivedAmount: 1285.91, expected: '部分到账' },
      { actual: 1285.92, receivedAmount: 0, expected: '待收款' },
      { actual: 0, receivedAmount: 0, expected: '待收款' },
      { receivedAmount: 2000, expected: '部分到账' },
    ];
    for (const [kind, nature] of [['detection', '自行检测'], ['inspection', '定期检验'], ['speedGovernor', '限速器校验'], ['loadTest', '125%额定载重试验']]) {
      saved.projects = statusCases.map((item, index) => ({ id: `status-${index}`, unit: `金额判断${index}`, date: '2026-10-02', createdBy: 'admin', nature, receivable: index === 2 ? 1000 : 2000, ...item }));
      await page.reload();
      await page.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
      await page.locator(`[data-ledger-kind=${kind}]`).click();
      await page.locator('#ledgerMonthFilter').fill('');
      for (const [index, item] of statusCases.entries()) {
        const row = page.locator('#ledgerTable tr').filter({ has: page.locator(`[data-detail="status-${index}"]`) });
        assert.equal(await row.locator('.status-stack:has(strong) .status-tag').innerText(), item.expected);
        assert.equal(Number((await row.locator('.status-stack strong').innerText()).replace(/[¥￥,]/g, '')), item.receivedAmount);
      }
      await page.locator('#paymentFilter').selectOption('已确款');
      assert.equal(await page.locator('#ledgerTable tr').count(), 2);
      await page.locator('#paymentFilter').selectOption('部分到账');
      assert.equal(await page.locator('#ledgerTable tr').count(), 2);
      await page.locator('#paymentFilter').selectOption('待收款');
      assert.equal(await page.locator('#ledgerTable tr').count(), 2);
      assert.equal(saveCount, 0);
      checks.push(`${kind}按实收金额确款、状态筛选正确且未修改到款金额`);
    }
    saved = freshState();
    await page.reload();
    await page.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await page.locator('[data-ledger-kind=inspection]').click();
    await page.locator('#ledgerMonthFilter').fill('');
    assert.match(await page.locator('#ledgerSummary').innerText(), /省市打款/);
    assert.match(await page.locator('#ledgerTableHead').innerText(), /省市打款/);
    await page.locator('#ledgerTable [data-detail="project-a"]').click();
    assert.match(await page.locator('#detailContent').innerText(), /省市打款/);
    await page.locator('#detailModal [data-close=detailModal]').click();
    checks.push('管理员省市打款汇总、表格、详情保持可见');
    await page.locator('#ledgerTable [data-view-pdf=project-a]').click();
    await page.locator('#pdfPages canvas').first().waitFor({ state: 'visible' });
    assert.ok(await page.locator('#pdfPages canvas').first().evaluate(el => el.width > 0 && el.height > 0));
    await page.locator('#pdfModal [data-close=pdfModal]').click();
    checks.push('迁移后旧 sample 附件路径和打包后的 PDF Worker 正常渲染');
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

    await page.locator('#propertyTable [data-property-detail="device-2"]').click();
    await page.locator('#propertyEditForm input[name=maintainer]').fill('不应保存');
    await page.locator('#propertyEditForm .modal-actions [data-close]').click();
    assert.equal(saved.properties.find(p => p.id === 'device-2').maintainer, '乙维保公司');
    await page.locator('#propertyTable [data-property-detail="device-2"]').click();
    const changed = { unit: '手工更新物业', equipmentType: '乘客电梯', deviceCode: '00123456789012345678', internalNo: 'L002', dueDate: '2028-03-01', registrationNo: '登记0002', inspectionAgency: '手工检测机构', address: '手工更新地址', maintainer: '编辑后维保公司', emergencyPhone: '04310001', contact: '张先生', phone: '04310002', phone1: '00123', source: '原始样例.xlsx', notes: '详情中直接修改' };
    for (const [name, value] of Object.entries(changed)) await page.locator(`#propertyEditForm [name=${name}]`).fill(value);
    await page.locator('#propertyEditForm [name=followUpStatus]').selectOption('跟进中');
    await page.screenshot({ path: path.join(output, 'property-detail-edit.png'), fullPage: true, animations: 'disabled', style: '#toastStack { visibility: hidden; }' });
    await page.locator('#propertyEditForm button[type=submit]').click();
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    for (const [name, value] of Object.entries(changed)) assert.equal(saved.properties.find(p => p.id === 'device-2')[name], value);
    await page.reload();
    await page.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await page.locator('[data-view=alerts]').click();
    await page.locator('#propertyTable [data-property-detail="device-2"]').click();
    for (const [name, value] of Object.entries(changed)) assert.equal(await page.locator(`#propertyEditForm [name=${name}]`).inputValue(), value);
    await page.locator('#propertyEditForm .modal-actions [data-close]').click();
    checks.push('查看弹窗全部字段编辑、取消不保存、保存刷新回显');

    await page.setViewportSize({ width: 390, height: 844 });
    await page.screenshot({ path: path.join(output, 'alerts-mobile.png'), fullPage: true, animations: 'disabled', style: '#toastStack { visibility: hidden; }' });
    assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth + 1));
    checks.push('390px 页面无整体横向溢出');

    for (const account of ['operator', 'operator2']) {
    const operator = await browser.newContext({ viewport: { width: 1440, height: 1000 } });
    await operator.route('**/*', mockRoute);
    const opPage = await operator.newPage();
    opPage.on('pageerror', error => errors.push(error.message));
    await opPage.goto(base);
    await opPage.locator('#loginForm input[name=username]').fill(account);
    await opPage.locator('#loginForm input[name=password]').fill('local-test-only');
    await opPage.locator('#loginForm button').click();
    await opPage.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await opPage.locator('[data-ledger-kind=inspection]').click();
    await opPage.locator('#ledgerMonthFilter').fill('');
    assert.doesNotMatch(await opPage.locator('#ledgerSummary').innerText(), /省市打款/);
    assert.doesNotMatch(await opPage.locator('#ledgerTableHead').innerText(), /省市打款/);
    await opPage.locator(`#ledgerTable [data-detail="${account === 'operator' ? 'project-a' : 'project-b'}"]`).click();
    assert.doesNotMatch(await opPage.locator('#detailContent').innerText(), /省市打款/);
    await opPage.locator('#detailModal [data-close=detailModal]').click();
    await opPage.locator('[data-view=alerts]').click();
    assert.equal(await opPage.locator('#deleteSelectedProperties').isVisible(), false);
    assert.equal(await opPage.locator('[data-select-property]').count(), 0);
    assert.equal(await opPage.locator('[data-edit-property]').count(), 0);
    await opPage.locator('#propertyTable [data-property-detail="device-2"]').click();
    assert.match(await opPage.locator('#detailContent').innerText(), /编辑后维保公司/);
    assert.equal(await opPage.locator('#detailContent input').count(), 0);
    await opPage.locator('#detailModal [data-close=detailModal]').click();
    await opPage.locator('[data-go=payments]').first().click();
    assert.equal(await opPage.locator('#unmatchedList').isVisible(), false);
    assert.equal(await opPage.locator('[data-delete-payment]').count(), 0);
    checks.push(`${account}省市内容隐藏、设备详情只读`);
    for (const [kind, nature] of [['detection', '自行检测'], ['inspection', '定期检验'], ['speedGovernor', '限速器校验'], ['loadTest', '125%额定载重试验']]) {
      await opPage.locator(`[data-ledger-kind=${kind}]`).click();
      assert.equal(await opPage.locator('#openManualProject').isVisible(), true);
      await opPage.locator('#openManualProject').click();
      assert.equal(await opPage.locator('#projectForm [name=nature]').inputValue(), nature);
      await opPage.locator('#projectForm [data-close=projectModal]').last().click();
    }
    await opPage.locator('[data-ledger-kind=inspection]').click();
    await opPage.locator('#openManualProject').click();
    await opPage.locator('#projectForm [name=unit]').fill(`${account}手工新增单位`);
    await opPage.locator('#projectForm [name=quantity]').fill('2');
    await opPage.locator('#projectForm [name=unitPrice]').fill('50');
    await opPage.locator('#projectForm [name=actual]').fill('80');
    await opPage.locator('#projectForm button[type=submit]').click();
    await opPage.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    const created = saved.projects.find(p => p.unit === `${account}手工新增单位`);
    assert.equal(created.createdBy, account === 'operator' ? 'operator1' : 'operator2');
    assert.equal(created.receivable, 100);
    assert.equal(created.actual, 80);
    assert.equal(created.receivedAmount, 0);
    await opPage.reload();
    await opPage.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await opPage.locator('[data-ledger-kind=inspection]').click();
    await opPage.locator('#ledgerMonthFilter').fill('');
    assert.ok((await opPage.locator('#ledgerTable').innerText()).includes(`${account}手工新增单位`));
    assert.equal(await opPage.locator('#ledgerTable [data-edit-project]').count(), 0);
    checks.push(`${account}四类台账新增入口及默认分类正确，新增保存刷新保留且补录权限未扩大`);

    await operator.close();
    }
    // Use real PDF/XLSX parsing with all uploads redirected to the local mock server.
    const zip = new (require('jszip'))();
    zip.file('xl/workbook.xml', '<workbook xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"><sheets><sheet name="设备" r:id="rId1" /></sheets></workbook>');
    zip.file('xl/_rels/workbook.xml.rels', '<Relationships><Relationship Id="rId1" Target="worksheets/sheet1.xml" /></Relationships>');
    zip.file('xl/worksheets/sheet1.xml', '<worksheet><sheetData><row><c r="A1" t="inlineStr"><is><t>使用单位</t></is></c><c r="B1" t="inlineStr"><is><t>设备代码</t></is></c></row><row><c r="A2" t="inlineStr"><is><t>拖拽测试物业</t></is></c><c r="B2" t="inlineStr"><is><t>DROP-001</t></is></c></row></sheetData></worksheet>');
    const excelBytes = await zip.generateAsync({ type: 'nodebuffer' });
    const pdfBytes = fs.readFileSync(path.join(root, 'src/static/sample/恒拓检测1台-吉林金科陶瓷有限公司-9月17日.pdf'));
    const drop = async (files, twice = false) => page.evaluate(({files, twice}) => {
      const transfer = new DataTransfer();
      for (const file of files) transfer.items.add(new File([Uint8Array.from(atob(file.data), char => char.charCodeAt(0))], file.name, { type: file.type }));
      const target = document.querySelector('.workspace');
      target.dispatchEvent(new DragEvent('dragenter', { bubbles: true, cancelable: true, dataTransfer: transfer }));
      target.dispatchEvent(new DragEvent('dragover', { bubbles: true, cancelable: true, dataTransfer: transfer }));
      target.dispatchEvent(new DragEvent('drop', { bubbles: true, cancelable: true, dataTransfer: transfer }));
      if (twice) target.dispatchEvent(new DragEvent('drop', { bubbles: true, cancelable: true, dataTransfer: transfer }));
    }, {files, twice});
    const droppedPdf = { name: '拖入台账.PDF', type: 'application/pdf', data: pdfBytes.toString('base64') };
    const droppedExcel = { name: '拖入物业.xlsx', type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', data: excelBytes.toString('base64') };
    await page.locator('[data-ledger-kind=inspection]').click();
    await drop([droppedPdf]);
    assert.equal(uploadedFiles.length, 0);
    assert.equal(await page.locator('#ledgerDropHint').isVisible(), false);
    await page.locator('[data-ledger-kind=detection]').click();
    assert.equal(await page.locator('#ledgerDropHint').isVisible(), true);
    await drop([{ name: '不支持.xls', type: 'application/vnd.ms-excel', data: excelBytes.toString('base64') }]);
    assert.equal(propertyImportCount, 0);
    await drop([droppedExcel, droppedPdf], true);
    await page.waitForFunction(() => document.querySelector('#ledgerDropHint').getAttribute('aria-busy') === 'false');
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    assert.deepEqual(uploadedFiles, ['拖入台账.PDF']);
    assert.equal(propertyImportCount, 1);
    assert.ok(saved.properties.some(item => item.deviceCode === 'DROP-001'));
    assert.equal(saved.projects.find(item => item.attachmentName === '拖入台账.PDF').nature, '自行检测');
    assert.equal(await page.locator('.is-file-dragging').count(), 0);
    checks.push('检测页混合拖拽 PDF/XLSX 导入成功，忙碌时不重复导入，其他台账和不支持类型不导入');
    await page.locator('#excelInput').setInputFiles({ name: '按钮物业.xlsx', mimeType: droppedExcel.type, buffer: excelBytes });
    await page.waitForFunction(() => document.querySelector('#ledgerDropHint').getAttribute('aria-busy') === 'false');
    assert.equal(propertyImportCount, 2);
    await page.locator('#pdfInput').setInputFiles({ name: '按钮台账.pdf', mimeType: droppedPdf.type, buffer: pdfBytes });
    await page.waitForFunction(() => document.querySelector('#ledgerDropHint').getAttribute('aria-busy') === 'false');
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    assert.deepEqual(uploadedFiles, ['拖入台账.PDF', '按钮台账.pdf']);
    checks.push('原 Excel/PDF 文件选择入口仍能导入');
    await page.setViewportSize({ width: 1440, height: 1000 });
    await page.locator('[data-ledger-kind=inspection]').click();
    await page.locator('#ledgerMonthFilter').fill('');
    await page.locator('[data-edit-project="project-a"]').click();
    const manualForm = page.locator('#ledgerEditForm');
    assert.ok((await manualForm.locator('[data-manual-choice=inspector] option').allTextContents()).includes('历史检验员'));
    const savesBeforeCancel = saveCount;
    await manualForm.locator('[data-manual-choice=inspector]').selectOption('custom');
    await manualForm.locator('input[name=inspector]').fill('取消的人员');
    await manualForm.locator('[data-close=ledgerEditModal]').last().click();
    assert.equal(saveCount, savesBeforeCancel);
    await page.locator('[data-edit-project="project-a"]').click();
    assert.ok(!(await manualForm.locator('[data-manual-choice=inspector] option').allTextContents()).includes('取消的人员'));
    await manualForm.locator('[data-manual-choice=inspector]').selectOption('custom');
    await manualForm.locator('input[name=inspector]').fill('新检验员<甲>');
    await manualForm.locator('[data-manual-choice=invoice]').selectOption({ label: '专票' });
    await manualForm.locator('[data-manual-choice=report]').selectOption({ label: '已出' });
    await manualForm.locator('button[type=submit]').click();
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    assert.equal(saved.projects.find(p => p.id === 'project-a').inspector, '新检验员<甲>');
    assert.equal(saved.projects.find(p => p.id === 'project-a').invoice, '专票');
    assert.equal(saved.projects.find(p => p.id === 'project-a').report, '已出');
    await page.reload();
    await page.locator('#storageStatusTitle').filter({ hasText: '云端已连接' }).waitFor();
    await page.locator('[data-ledger-kind=inspection]').click();
    await page.locator('#ledgerMonthFilter').fill('');
    await page.locator('[data-edit-project="project-b"]').click();
    assert.equal(await manualForm.locator('[data-manual-choice=invoice] option:checked').innerText(), '旧发票001');
    assert.equal(await manualForm.locator('[data-manual-choice=report] option:checked').innerText(), '报告009');
    await manualForm.locator('[data-manual-choice=inspector]').selectOption({ label: '新检验员<甲>' });
    await manualForm.locator('button[type=submit]').click();
    await page.waitForFunction(() => document.querySelector('#storageStatusTitle').textContent === '云端已同步');
    const editedB = saved.projects.find(p => p.id === 'project-b');
    assert.equal(editedB.inspector, '新检验员<甲>');
    assert.equal(editedB.invoice, '旧发票001');
    assert.equal(editedB.report, '报告009');
    checks.push('补录人员可新增并跨记录复用，取消不新增，发票/报告选择可保存且旧内容不丢失');
    assert.deepEqual(errors, []);
    fs.writeFileSync(path.join(output, 'browser-report.json'), JSON.stringify({ passed: true, buildRoot: process.env.TEST_WEB_ROOT || 'dist/build/h5', environment: 'local HTTP and mock API, no live cloud requests', checks, errors }, null, 2));
    console.log(JSON.stringify({ passed: true, checks, output }, null, 2));
  } finally { await browser.close(); }
})().catch(error => { console.error(error); process.exitCode = 1; }).finally(() => server.close());
