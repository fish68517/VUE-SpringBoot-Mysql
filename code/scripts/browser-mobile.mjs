import { chromium } from 'playwright'
import assert from 'node:assert/strict'
import fs from 'node:fs'

const base = process.env.MOBILE_URL || 'http://127.0.0.1:5173/web/'
const out = 'unpackage/evidence/mobile-adaptation'
fs.mkdirSync(out, { recursive: true })
const browser = await chromium.launch({ channel: 'chrome', headless: true })
const errors = [],
  checks = []
async function create(width = 390, height = 844) {
  const context = await browser.newContext({ viewport: { width, height } })
  const page = await context.newPage()
  page.on('pageerror', (e) => errors.push(e.message))
  return { context, page }
}
async function nav(page, route = '') {
  await page.goto(base + (route ? '#/pages/' + route : ''))
  await page.locator('.page-title,.screen-title,.login-welcome').first().waitFor()
}
const field = (page, label) =>
  page
    .locator('uni-input,uni-textarea')
    .filter({ has: page.getByText(label, { exact: true }) })
    .locator('input,textarea')
async function click(page, label) {
  await page.getByText(label, { exact: true }).click()
}
async function login(page, role) {
  await nav(page)
  await page.locator('.login-welcome').waitFor()
  await click(page, role)
  await page.locator('.login-submit').click()
  await page.locator('.mobile-workbench,.screen-title').first().waitFor()
}
async function state(page) {
  return page.evaluate(() => {
    const value = JSON.parse(localStorage.getItem('smart-water:state:v1'))
    return typeof value === 'string' ? JSON.parse(value) : value.data ? JSON.parse(value.data) : value
  })
}
async function check(name, action) {
  await action()
  checks.push(name)
  console.log('PASS', name)
}
async function noOverflow(page) {
  assert.equal(await page.evaluate(() => document.documentElement.scrollWidth > innerWidth + 2), false)
}
try {
  const { page, context } = await create()
  await check('手机从真实启动入口登录进入工作台，底部四入口且无桌面外壳', async () => {
    await login(page, '操作员')
    assert.match(page.url(), /mobile\/home/)
    assert.equal(await page.locator('.mobile-nav-item').count(), 4)
    assert.equal(await page.locator('.sidebar').count(), 0)
    assert.ok(!(await page.locator('body').innerText()).includes('返回大屏'))
    await noOverflow(page)
    await page.screenshot({ path: out + '/home.png', fullPage: true })
    await page.reload()
    await page.locator('.mobile-workbench').waitFor()
    assert.match(page.url(), /mobile\/home/)
  })
  await check('我的页面独立于系统设置，操作员看不到管理配置', async () => {
    await page.locator('.mobile-nav-item').filter({ hasText: '我的' }).click()
    await page.locator('.mobile-profile').waitFor()
    const text = await page.locator('.mobile-content').innerText()
    for (const forbidden of ['业务场景', '回放时钟', '管理工具']) assert.ok(!text.includes(forbidden))
    await page.screenshot({ path: out + '/profile.png', fullPage: true })
    await nav(page, 'map/config')
    assert.ok((await page.locator('.mobile-content').innerText()).includes('当前账号无权访问'))
  })
  let taskId, facilityId
  await check('本人任务与历史分类、任务地图只展示关联设施', async () => {
    await nav(page, 'inspection/edit')
    await field(page, '填写巡检任务名称').fill('移动地图联动检查任务')
    await page.locator('.form-wide .choice').first().click()
    await click(page, '发布任务')
    await page.waitForURL(/inspection\/detail/)
    await nav(page, 'mobile/tasks?view=pending')
    await page.locator('.task-card').first().waitFor()
    const tasks = (await state(page)).inspections.filter(
      (t) => t.assigneeId === 'USER-002' && t.status === 'pending',
    )
    assert.equal(await page.locator('.task-card').count(), tasks.length)
    taskId = tasks.at(-1).id
    const card = page.locator('.task-card').filter({ hasText: taskId })
    await card.getByText('任务地图', { exact: true }).click()
    await page.locator('.amap-surface .amap-layer').first().waitFor({ timeout: 45000 })
    await page.getByText('图层', { exact: true }).click()
    await page.getByText('管网视图', { exact: true }).click()
    await page.locator('.map-canvas canvas').waitFor()
    await page.getByText(/^结果 \d+$/).click()
    const rows = page.locator('.mobile-map > .panel').last().locator('.list-item')
    assert.equal(await rows.count(), tasks.at(-1).checks.length)
    facilityId = tasks.at(-1).checks[0].facilityId
    // Exercise the actual canvas hit and drag handlers, not only the result list.
    await page.locator('.map-canvas canvas').scrollIntoViewIfNeeded()
    const rect = await page.locator('.map-canvas canvas').boundingBox()
    const position = (await state(page)).facilities.find((f) => f.id === facilityId).position
    const scale = Math.min(rect.width / 1000, rect.height / 600) * 0.94
    const x = rect.x + rect.width / 2 + (position[0] - 500) * scale
    const y = rect.y + rect.height / 2 + (position[1] - 300) * scale
    await page.mouse.click(x, y)
    await page.locator('.map-sheet').waitFor()
    assert.ok((await page.locator('.map-sheet').innerText()).includes(facilityId))
    await page.locator('.map-sheet').getByText('关闭', { exact: true }).click()
    await page
      .locator('select')
      .filter({ has: page.locator('option[value="box"]') })
      .selectOption('box')
    await page.locator('.map-canvas canvas').scrollIntoViewIfNeeded()
    const boxRect = await page.locator('.map-canvas canvas').boundingBox()
    const bx = boxRect.x + boxRect.width / 2 + (position[0] - 500) * scale
    const by = boxRect.y + boxRect.height / 2 + (position[1] - 300) * scale
    await page.mouse.move(bx - 12, by - 12)
    await page.mouse.down()
    await page.mouse.move(bx + 12, by + 12, { steps: 8 })
    await page.mouse.up()
    await page.getByText(/区域已选 1 个设施/).waitFor()
    await rows.first().click()
    await page.locator('.map-sheet').waitFor()
    assert.ok((await page.locator('.map-sheet').innerText()).includes(facilityId))
    await page.screenshot({ path: out + '/map-sheet.png' })
  })
  await check('地图上报继承设施，创建工单并保持移动端返回流程', async () => {
    await page.locator('.map-sheet').getByText('事件上报', { exact: true }).click()
    await field(page, '事件标题（必填）').fill('移动端现场积水检查')
    await field(page, '事件描述（必填）').fill('已检查现场，需安排排查处理。')
    await field(page, 'YYYY-MM-DD HH:mm').fill('2026-09-19 08:35')
    await click(page, '上报并建立工单')
    await page.waitForURL(/workorders\/detail/)
    const order = (await state(page)).workorders.find((w) => w.title === '移动端现场积水检查')
    assert.equal(order.facilityId, facilityId)
    assert.equal(order.occurredAt, '2026-09-19T08:35:00+08:00')
    await page.getByText('2026-09-19 08:35:00', { exact: true }).waitFor()
    assert.equal(await page.locator('.mobile-shell').count(), 1)
    await page.locator('.mobile-back').click()
    await page.waitForURL(/mobile\/map/)
    assert.ok(page.url().includes(taskId))
  })
  await check('移动新建巡检、必填校验、完成后进入历史并重载保留', async () => {
    await nav(page, 'inspection/edit')
    await field(page, '填写巡检任务名称').fill('移动端补齐回归任务')
    await page.locator('.form-wide .choice').first().click()
    await click(page, '发布任务')
    await page.waitForURL(/inspection\/detail/)
    const task = (await state(page)).inspections.find((t) => t.name === '移动端补齐回归任务')
    assert.equal(task.assigneeId, 'USER-002')
    await click(page, '提交完成巡检')
    await page.waitForTimeout(250)
    assert.notEqual((await state(page)).inspections.find((t) => t.id === task.id).status, 'completed')
    await page.locator('.check-card').getByText('正常', { exact: true }).click()
    await field(page, '检查说明（必填）').fill('设施外观及周边环境检查正常。')
    await click(page, '保存检查项')
    await click(page, '提交完成巡检')
    await nav(page, 'mobile/tasks?view=history')
    await page.getByText('移动端补齐回归任务', { exact: true }).waitFor()
    await page.reload()
    await page.getByText('移动端补齐回归任务', { exact: true }).waitFor()
    await page.screenshot({ path: out + '/history.png', fullPage: true })
  })
  await check('阀门控制保留确认和状态保存，纠错保留审核提交', async () => {
    const valve = (await state(page)).facilities.find((f) => f.type === 'valve' && f.regionId === 'REG-001')
    await nav(page, 'facilities/detail?id=' + valve.id)
    await page.getByText(/阀门：/).click()
    await click(page, '确定')
    await page.waitForTimeout(200)
    assert.notEqual(
      (await state(page)).facilities.find((f) => f.id === valve.id).valveState,
      valve.valveState,
    )
    await click(page, '纠错与编辑')
    await field(page, '名称').fill(valve.name + '校核')
    await click(page, '提交六级审核')
    await page.waitForTimeout(300)
    assert.ok(
      (await state(page)).phase2.changes.some((c) => c.targetId === valve.id && c.status === 'pending'),
    )
  })
  await check('合法深链接转移动地图并保留参数，轨迹与横屏页面可用', async () => {
    await nav(page, 'map/index?facilityId=' + facilityId)
    await page.waitForURL(/mobile\/map/)
    assert.ok(page.url().includes(facilityId))
    await nav(page, 'inspection/replay?id=' + taskId)
    await page.locator('.map-canvas canvas').waitFor()
    assert.ok(!(await page.locator('.mobile-content').innerText()).includes('模板与路线'))
    await page.setViewportSize({ width: 844, height: 390 })
    await noOverflow(page)
    await page.screenshot({ path: out + '/landscape.png', fullPage: true })
    await nav(page, 'mobile/profile')
    await click(page, '退出登录')
    await click(page, '确定')
    await page.locator('.login-welcome').waitFor()
    await click(page, '访客')
    await page.locator('.login-submit').click()
    await page.locator('.mobile-workbench').waitFor()
    assert.equal(await page.getByText('新建巡检', { exact: true }).count(), 0)
    await nav(page, 'mobile/report')
    assert.ok((await page.locator('.mobile-content').innerText()).includes('当前账号无权访问'))
  })
  await context.close()
  const admin = await create()
  await check('手机管理员具备管理入口，区域任务需显式进入', async () => {
    await login(admin.page, '管理员')
    await nav(admin.page, 'mobile/profile')
    await admin.page.getByText('管理工具', { exact: true }).waitFor()
    await admin.page.getByText(/区域巡检与分派/).click()
    await admin.page.getByText('区域巡检任务', { exact: true }).waitFor()
    assert.ok((await admin.page.locator('.task-card').count()) > 0)
  })
  await admin.context.close()
  const desktop = await create(1440, 1000)
  await check('桌面登录仍进入大屏，门户及地图保留桌面管理入口', async () => {
    await login(desktop.page, '管理员')
    assert.match(desktop.page.url(), /dashboard\/index/)
    await nav(desktop.page, 'portal/index')
    await desktop.page.locator('.sidebar').waitFor()
    assert.equal(await desktop.page.locator('.mobile-shell').count(), 0)
    await nav(desktop.page, 'map/index')
    await desktop.page.getByText('图层配置', { exact: true }).waitFor()
    assert.ok((await desktop.page.locator('.table-row').count()) > 0)
  })
  await desktop.context.close()
  assert.deepEqual(errors, [])
  fs.writeFileSync(
    out + '/result.json',
    JSON.stringify({ checks, errors, packaged: false, androidDeviceTested: false }, null, 2),
  )
  console.log('PASS all ' + checks.length + ' groups')
} catch (e) {
  for (const context of browser.contexts())
    for (const page of context.pages())
      await page.screenshot({ path: out + '/failure.png', fullPage: true }).catch(() => {})
  throw e
} finally {
  await browser.close()
}
