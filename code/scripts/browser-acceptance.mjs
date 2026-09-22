import { chromium } from 'playwright'
import fs from 'node:fs'
import assert from 'node:assert/strict'
import { isMapRequest, resourceLabel } from './browser-network.mjs'
const base = process.env.DEMO_URL || 'http://127.0.0.1:4173'
const browser = await chromium.launch({ channel: process.env.BROWSER_CHANNEL || 'chrome', headless: true })
const context = await browser.newContext({ viewport: { width: 1920, height: 1080 } }),
  page = await context.newPage()
const errors = [],
  failedRequests = [],
  externalRequests = [],
  checks = [],
  screenshots = []
page.on('pageerror', (e) => errors.push(e.message))
page.on('response', (r) => {
  if (r.status() >= 400) failedRequests.push({ url: resourceLabel(r.url()), status: r.status() })
})
page.on('request', (r) => {
  if (!r.url().startsWith(base) && !r.url().startsWith('data:') && !r.url().startsWith('blob:') && !isMapRequest(r.url()))
    externalRequests.push(resourceLabel(r.url()))
})
async function navigate(route) {
  await page.goto(base + '/#/pages/' + route)
  await page.locator('.page-title,.screen-title,.login-welcome').first().waitFor()
  await page.waitForTimeout(180)
}
async function login(role) {
  await navigate('login/index')
  await page.getByText(role, { exact: true }).click()
  await page.locator('.login-submit').click()
  await page.locator('.screen-title,.greeting-title').first().waitFor()
}
async function shot(name) {
  await page.screenshot({ path: 'unpackage/evidence/' + name + '.png', fullPage: true })
  screenshots.push(name + '.png')
}
async function modal() {
  await page.getByText('确定', { exact: true }).click()
}
async function checked(name, fn) {
  await fn()
  checks.push({ name, passed: true })
  console.log('PASS', name)
}
try {
  await checked('管理员登录、大屏地图和图表均渲染', async () => {
    await login('管理员')
    await page.locator('.chart-view canvas').first().waitFor()
    assert.equal(await page.locator('.chart-view canvas').count(), 1)
    await page.locator('.amap-surface .amap-layer').first().waitFor({ timeout: 45000 })
    await shot('dashboard-1920')
  })
  await checked('地图点选和缩放后点选正确', async () => {
    await navigate('map/index?facilityId=FAC-001')
    await page.locator('.map-canvas canvas').waitFor()
    const facilities = JSON.parse(fs.readFileSync('data/facilities.json', 'utf8'))
    const f = facilities[6]
    const c = page.locator('.map-canvas canvas'),
      r = await c.boundingBox()
    const s = Math.min(r.width / 1000, r.height / 600) * 0.94
    await c.click({
      position: { x: r.width / 2 + (f.position[0] - 500) * s, y: r.height / 2 + (f.position[1] - 300) * s },
    })
    assert.ok((await page.locator('.map-container').locator('..').locator('..').innerText()).includes(f.name))
    await page.getByText('＋', { exact: true }).click()
    await c.click({
      position: {
        x: r.width / 2 + (f.position[0] - 500) * s * 1.25,
        y: r.height / 2 + (f.position[1] - 300) * s * 1.25,
      },
    })
    await shot('map-1920')
  })
  await checked('告警详情实际派单并生成工单', async () => {
    await navigate('alarms/index?id=ALM-001')
    await shot('alarms-1920')
    await page.getByText('受理并分配', { exact: true }).click()
    await page.locator('.handling-form textarea').fill('检查中原路压力，确认管线运行情况')
    await page.getByText('创建维修工单', { exact: true }).click()
    await page.waitForURL(/workorders\/detail/)
    await page.getByText('接单并开始处理', { exact: true }).waitFor()
    assert.ok((await page.locator('.page-body').innerText()).includes('WO-041'))
  })
  await checked('操作员登录、接单、提交验收', async () => {
    await page.locator('.top-actions .text-button').click()
    await login('操作员')
    await navigate('workorders/detail?id=WO-041')
    await page.getByText('接单并开始处理', { exact: true }).click()
    await page.locator('textarea').fill('已完成压力复测，模拟运行状态恢复正常')
    await page.getByText('提交验收', { exact: true }).click()
    await page.getByText('待验收', { exact: true }).first().waitFor()
    assert.equal(await page.getByText('验收通过', { exact: true }).count(), 0)
  })
  await checked('管理员验收关闭告警，刷新后状态保留', async () => {
    await page.locator('.top-actions .text-button').click()
    await login('管理员')
    await navigate('workorders/detail?id=WO-041')
    await page.getByText('验收通过', { exact: true }).click()
    await modal()
    await page.getByText('已完成', { exact: true }).first().waitFor()
    await page.reload()
    await page.getByText('已完成', { exact: true }).first().waitFor()
    await navigate('alarms/detail?id=ALM-001')
    assert.ok((await page.locator('.alarm-details').innerText()).includes('已关闭'))
    assert.ok((await page.locator('.alarm-details').innerText()).includes('已生成记录'))
    await navigate('dashboard/index')
    assert.match(await page.locator('.screen-kpis').innerText(), /22\s*条/)
    await shot('workflow-closed')
  })
  await checked('场景重置恢复初始告警', async () => {
    await navigate('settings/index')
    await page.getByText('重置并加载场景', { exact: true }).click()
    await modal()
    await page.getByText('场景已重置', { exact: true }).waitFor()
    await navigate('alarms/detail?id=ALM-001')
    assert.ok((await page.locator('.alarm-details').innerText()).includes('待处理'))
  })
  await checked('搜索、分页和返回筛选保留', async () => {
    await navigate('workorders/index')
    await page.locator('.search input').fill('管线维修')
    const first = await page.locator('.order-row:not(.header)').first().innerText()
    await page.locator('.order-row:not(.header)').first().click()
    await page.locator('.heading-actions').getByText('返回', { exact: true }).click()
    await page.locator('.search input').waitFor()
    assert.equal(await page.locator('.search input').inputValue(), '管线维修')
    assert.equal(await page.locator('.order-row:not(.header)').first().innerText(), first)
  })
  await checked('全部24个页面可访问，详情及图表不白屏', async () => {
    const routes = [
      'portal/index',
      'portal/detail?type=notice&id=NOTICE-001',
      'alarms/index',
      'alarms/detail?id=ALM-001',
      'workorders/index',
      'workorders/detail?id=WO-001',
      'workorders/edit',
      'map/index',
      'facilities/detail?id=FAC-001',
      'dma/index',
      'dma/detail?id=DMA-001',
      'inspection/index',
      'inspection/detail?id=TASK-002',
      'inspection/edit',
      'datahub/index',
      'datahub/device?deviceId=DEV-001',
      'reports/index',
      'settings/index',
      'mobile/home',
      'mobile/tasks',
      'mobile/map',
      'mobile/profile',
    ]
    for (const route of routes) {
      await navigate(route)
      assert.ok((await page.locator('.page-body').innerText()).trim().length > 20, route)
      if (
        route === 'dma/detail?id=DMA-001' ||
        route === 'reports/index' ||
        route === 'datahub/device?deviceId=DEV-001'
      )
        await page.locator('.chart-view canvas').first().waitFor()
    }
    checks.push({ name: '业务页面逐页检查数量', count: routes.length, passed: true })
  })
  await checked('同一路径切换详情ID更新内容', async () => {
    await navigate('workorders/detail?id=WO-001')
    assert.ok((await page.locator('.page-body').innerText()).includes('WO-001'))
    await navigate('workorders/detail?id=WO-002')
    assert.ok((await page.locator('.page-body').innerText()).includes('WO-002'))
  })
  await checked('巡检界面上报异常、生成工单并完成检查', async () => {
    await navigate('inspection/detail?id=TASK-002')
    const cards = page.locator('.check-card')
    for (let i = 0; i < 3; i++) {
      const card = cards.nth(i)
      await card.getByText(i === 0 ? '发现异常' : '正常', { exact: true }).click()
      await card.locator('textarea').fill(i === 0 ? '发现接头渗漏，已上报处置' : '外观及运行状态正常')
      await card.getByText('保存检查项', { exact: true }).click()
    }
    assert.ok((await cards.first().innerText()).includes('关联工单'))
    await page.getByText('提交完成巡检', { exact: true }).click()
    await page.getByText('已完成', { exact: true }).first().waitFor()
    await shot('inspection-completed')
  })
  await checked('CSV导出产生真实文件', async () => {
    await navigate('alarms/index')
    const download = page.waitForEvent('download')
    await page.getByText('⇩ 批量导出', { exact: true }).click()
    const d = await download
    await d.saveAs('unpackage/evidence/告警导出样例.csv')
    assert.ok(fs.readFileSync('unpackage/evidence/告警导出样例.csv', 'utf8').includes('ALM-001'))
  })
  await checked('错误ID显示可理解的空态', async () => {
    await navigate('facilities/detail?id=missing')
    assert.match(await page.locator('.page-body').innerText(), /不存在/)
    await navigate('workorders/detail?id=missing')
    assert.match(await page.locator('.page-body').innerText(), /不存在/)
  })
  await checked('1366桌面与390手机页面无整页横向溢出', async () => {
    await page.setViewportSize({ width: 1366, height: 768 })
    await navigate('dashboard/index')
    await page.locator('.chart-view canvas').first().waitFor()
    await shot('dashboard-1366')
    await navigate('alarms/index')
    await shot('alarms-1366')
    for (const route of [
      'mobile/home',
      'mobile/tasks',
      'mobile/map',
      'inspection/detail?id=TASK-002',
      'dma/index',
      'reports/index',
    ]) {
      await page.setViewportSize({ width: 390, height: 844 })
      await navigate(route)
      const dimension = await page.evaluate(() => ({
        width: document.documentElement.clientWidth,
        scroll: document.documentElement.scrollWidth,
      }))
      assert.ok(
        dimension.scroll <= dimension.width + 2,
        route + ' horizontal overflow ' + JSON.stringify(dimension),
      )
      if (['mobile/home', 'mobile/map', 'mobile/tasks'].includes(route))
        await shot(route.replace('/', '-') + '-390')
    }
  })
  await checked('访客不能写入；操作员不能访问其他区域', async () => {
    await page.setViewportSize({ width: 1366, height: 768 })
    await login('访客')
    await navigate('workorders/edit')
    assert.match(await page.locator('.page-body').innerText(), /无权/)
    await navigate('alarms/index')
    assert.equal(await page.getByText('受理并分配', { exact: true }).count(), 0)
    await login('操作员')
    await navigate('facilities/detail?id=FAC-002')
    assert.match(await page.locator('.page-body').innerText(), /无权访问/)
  })
  await checked('业务页未登录直达会回到登录并恢复目标', async () => {
    await page.locator('.top-actions .text-button').click()
    await navigate('workorders/detail?id=WO-002')
    await page.locator('.login-welcome').waitFor()
    await page.getByText('管理员', { exact: true }).click()
    await page.locator('.login-submit').click()
    await page.waitForURL(/workorders\/detail/)
    assert.ok((await page.locator('.page-body').innerText()).includes('WO-002'))
  })
  assert.deepEqual(errors, [], '页面运行异常')
  assert.deepEqual(failedRequests, [], '资源加载失败')
  assert.deepEqual(externalRequests, [], '请求了第三方资源')
} catch (e) {
  checks.push({ name: '浏览器验收异常', passed: false, error: e.message })
  await shot('failure')
  console.error(e)
  process.exitCode = 1
} finally {
  fs.writeFileSync(
    'unpackage/evidence/browser-acceptance.json',
    JSON.stringify(
      {
        checkedAt: new Date().toISOString(),
        browser: await browser.version(),
        checks,
        errors,
        failedRequests,
        externalRequests,
        screenshots,
      },
      null,
      2,
    ),
  )
  await browser.close()
}
