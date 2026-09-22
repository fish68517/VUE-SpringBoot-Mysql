import { chromium } from 'playwright'
import assert from 'node:assert/strict'
import fs from 'node:fs'
const base = process.env.DASHBOARD_URL || 'http://127.0.0.1:5173/web/'
const out = 'unpackage/evidence/dashboard-map'
fs.mkdirSync(out, { recursive: true })
const browser = await chromium.launch({ channel: 'chrome', headless: true })
const checks = [],
  errors = [],
  amapResponses = []
const context = await browser.newContext({ viewport: { width: 1920, height: 1080 } })
const page = await context.newPage()
const redact = (text) => text.replace(/[a-f0-9]{32}/gi, '[redacted]')
page.on('pageerror', (e) => errors.push(redact(e.message)))
page.on('response', (r) => {
  if (new URL(r.url()).hostname.endsWith('amap.com'))
    amapResponses.push({
      host: new URL(r.url()).hostname,
      path: new URL(r.url()).pathname,
      status: r.status(),
    })
})
let legacyChartRequests = 0
await context.route('**/static/vendor/echarts.min.js', (route) => {
  legacyChartRequests++
  return route.abort()
})
async function login() {
  await page.goto(base + '#/pages/login/index')
  await page.getByText('管理员', { exact: true }).click()
  await page.locator('.login-submit').click()
  await page.locator('.screen-title').waitFor()
}
async function checked(name, fn) {
  await fn()
  checks.push(name)
  console.log('PASS', name)
}
try {
  await checked('高德真实底图及区域汇总正常加载；旧图表资源不可用时曲线仍正常', async () => {
    const tileResponse = page.waitForResponse(
      (r) =>
        new URL(r.url()).hostname.endsWith('amap.com') && r.url().includes('/tile/') && r.status() === 200,
      { timeout: 45000 },
    )
    await login()
    await page.locator('.chart-view canvas').waitFor()
    await page.locator('.amap-surface .amap-layer').first().waitFor({ timeout: 45000 })
    await page.locator('.water-region-marker').first().waitFor({ timeout: 45000 })
    await page.waitForFunction(() => !document.querySelector('.amap-message'), { timeout: 45000 })
    assert.equal(legacyChartRequests, 0)
    assert.equal(await page.locator('.screen-map-area .map-canvas').count(), 0)
    assert.ok((await page.locator('.water-region-marker').count()) >= 5)
    assert.ok(amapResponses.some((r) => r.status === 200 && r.path !== '/maps'))
    await tileResponse
    // SDK complete can precede the first vector tile paint; capture after that paint settles.
    await page.waitForTimeout(2000)
    await page.screenshot({ path: out + '/dashboard.png', fullPage: true })
  })
  await checked('区域点选、设施切换、底图切换与复位可用', async () => {
    await page.locator('[data-region-id="REG-004"]').click()
    await page.waitForFunction(() => document.querySelector('.screen-map-card').textContent.includes('金水'))
    await page.locator('.screen-map-card select').selectOption('FAC-001')
    await page.locator('.chart-view canvas').waitFor()
    await page.getByText('标准底图', { exact: true }).click()
    await page.getByText('深色底图', { exact: true }).waitFor()
    await page.getByText('深色底图', { exact: true }).click()
    await page.getByText('定位复位', { exact: true }).click()
    await page.waitForTimeout(1200)
    await page.screenshot({ path: out + '/selection.png', fullPage: true })
  })
  await checked('离开大屏销毁地图，返回后地图和曲线恢复', async () => {
    await page.getByText('业务工作台 ↗', { exact: true }).click()
    await page.locator('.sidebar').waitFor()
    assert.equal(await page.locator('.amap-surface .amap-layer').count(), 0)
    await page.goto(base + '#/pages/dashboard/index')
    await page.locator('.water-region-marker').first().waitFor({ timeout: 45000 })
    await page.locator('.chart-view canvas').waitFor()
  })
  await checked('地图网络失败明确提示且可重试，不影响本地图表', async () => {
    await context.route('https://webapi.amap.com/maps?**', (route) => route.abort())
    await page.reload()
    await page.getByText('重新加载地图', { exact: true }).waitFor({ timeout: 45000 })
    await page.locator('.chart-view canvas').waitFor()
    await page.screenshot({ path: out + '/network-error.png', fullPage: true })
    await context.unroute('https://webapi.amap.com/maps?**')
    await page.getByText('重新加载地图', { exact: true }).click()
    await page.locator('.water-region-marker').first().waitFor({ timeout: 45000 })
  })
  await checked('设施、DMA、报表的共用图表均正常渲染', async () => {
    for (const route of ['facilities/detail?id=FAC-001', 'dma/detail?id=DMA-001', 'reports/index']) {
      await page.goto(base + '#/pages/' + route)
      await page.locator('.chart-view canvas').first().waitFor()
      assert.ok(!(await page.locator('body').innerText()).includes('图表资源加载失败'))
    }
    assert.equal(legacyChartRequests, 0)
  })
  assert.deepEqual(errors, [])
  fs.writeFileSync(
    out + '/result.json',
    JSON.stringify(
      { checks, errors, legacyChartRequests, amapResponses, sdkMocked: false, packaged: false },
      null,
      2,
    ),
  )
  console.log('PASS all ' + checks.length + ' groups')
} catch (error) {
  await page.screenshot({ path: out + '/failure.png', fullPage: true }).catch(() => {})
  fs.writeFileSync(
    out + '/failure.json',
    JSON.stringify({ checks, errors, amapResponses, message: redact(error.message) }, null, 2),
  )
  throw new Error(redact(error.message))
} finally {
  await browser.close()
}
