import { chromium } from 'playwright'
import assert from 'node:assert/strict'
import fs from 'node:fs'
import path from 'node:path'
import { pathToFileURL } from 'node:url'
const base = process.env.MOBILE_URL || 'http://127.0.0.1:5173/web/'
const out = 'unpackage/evidence/mobile-amap'
fs.mkdirSync(out, { recursive: true })
const browser = await chromium.launch({ channel: 'chrome', headless: true })
const context = await browser.newContext({
  viewport: { width: 390, height: 844 },
  isMobile: true,
  hasTouch: true,
})
const page = await context.newPage(),
  checks = [],
  errors = []
const sanitize = (s) => s.replace(/[a-f0-9]{32}/gi, '[redacted]')
let currentCheck = ''
page.on('pageerror', (e) =>
  errors.push({ check: currentCheck, message: sanitize(e.message), stack: sanitize(e.stack || '') }),
)
async function nav(route) {
  await page.goto(base + '#/pages/' + route)
}
async function ready() {
  await page.locator('.amap-surface .amap-layer').first().waitFor({ timeout: 45000 })
  await page.locator('.water-region-marker').first().waitFor({ timeout: 45000 })
  await page.waitForFunction(() => !document.querySelector('.amap-message'))
  await page.waitForTimeout(1500)
}
async function check(name, action) {
  currentCheck = name
  await action()
  checks.push(name)
  console.log('PASS', name)
}
try {
  await check('访客移动地图默认加载高德底图和可触控区域标记', async () => {
    await nav('login/index')
    await page.getByText('访客', { exact: true }).tap()
    await page.locator('.login-submit').tap()
    await page.locator('.mobile-workbench').waitFor()
    await page.locator('.mobile-nav-item').filter({ hasText: '地图' }).tap()
    await ready()
    assert.equal(await page.locator('.water-region-marker').count(), 6)
    assert.equal(await page.locator('.map-canvas').count(), 0)
    const rect = await page.locator('.amap-surface').boundingBox()
    assert.ok(rect.width > 250 && rect.height >= 320)
    await page.screenshot({ path: out + '/guest-map.png', fullPage: true })
  })
  await check('区域触控联查、任务设施过滤与详情抽屉保持一致', async () => {
    await page.locator('[data-region-id="REG-002"]').tap()
    await page.getByText('结果 20', { exact: true }).waitFor()
    await nav('mobile/map?taskId=TASK-002')
    await ready()
    assert.equal(await page.locator('.water-region-marker').count(), 1)
    assert.ok(await page.locator('[data-region-id="REG-002"]').count())
    await page.getByText('结果 3', { exact: true }).tap()
    const rows = page.locator('.mobile-map > .panel').last().locator('.list-item')
    assert.equal(await rows.count(), 3)
    await rows.first().tap()
    await page.locator('.map-sheet').waitFor()
    assert.equal(await page.locator('.map-sheet').getByText('事件上报', { exact: true }).count(), 0)
    await page.locator('.map-sheet').getByText('关闭', { exact: true }).tap()
    await page.locator('.amap-surface').scrollIntoViewIfNeeded()
    await page.screenshot({ path: out + '/task-map.png' })
  })
  await check('图层切换、放大复位、横屏和离开返回可用', async () => {
    await page.getByText('＋', { exact: true }).tap()
    await page.getByText('复位', { exact: true }).tap()
    await page.getByText('图层', { exact: true }).tap()
    await page.getByText('深色底图', { exact: true }).tap()
    await page.getByText('管网视图', { exact: true }).tap()
    await page.locator('.map-canvas canvas').waitFor()
    assert.equal(await page.locator('.amap-surface').count(), 0)
    await page.getByText('浅色底图', { exact: true }).tap()
    await ready()
    await page.setViewportSize({ width: 844, height: 390 })
    assert.equal(await page.evaluate(() => document.documentElement.scrollWidth > innerWidth + 2), false)
    await page.locator('.mobile-nav-item').filter({ hasText: '我的' }).tap()
    assert.equal(await page.locator('.amap-surface').count(), 0)
    await page.locator('.mobile-nav-item').filter({ hasText: '地图' }).tap()
    await ready()
    await page.setViewportSize({ width: 390, height: 844 })
  })
  await check('高德网络失败显示重试，恢复网络可重新加载', async () => {
    await context.route('https://webapi.amap.com/maps?**', (route) => route.abort())
    await page.reload()
    await page.getByText('重新加载地图', { exact: true }).waitFor()
    await context.unroute('https://webapi.amap.com/maps?**')
    await page.getByText('重新加载地图', { exact: true }).tap()
    await ready()
  })
  await check('本地 file 视图运行实际 renderjs，缺少 ResizeObserver 也能加载地图', async () => {
    // View-layer compatibility test only: this is not an Android device / APK acceptance test.
    const fixture = path.resolve(out, 'view-layer.html')
    fs.writeFileSync(
      fixture,
      '<!doctype html><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><div id="root" style="width:360px;height:540px"><div class="amap-surface" style="width:100%;height:100%"></div></div>',
    )
    const source = fs
      .readFileSync('components/DashboardMap.vue', 'utf8')
      .match(/<script module="geo" lang="renderjs">([\s\S]*?)<\/script>/)[1]
      .replace(/^import .*$/gm, '')
      .replace('export default', 'window.mapDefinition =')
    const loader = fs.readFileSync('platform/amap-loader.js', 'utf8').replace(/export /g, '')
    const host = fs.readFileSync('platform/render-host.js', 'utf8').replace(/export /g, '')
    const credentials = JSON.parse(fs.readFileSync('config/amap.local.json', 'utf8'))
    const nativeView = await context.newPage()
    nativeView.on('pageerror', (e) =>
      errors.push({ check: currentCheck, message: sanitize(e.message), stack: sanitize(e.stack || '') }),
    )
    await nativeView.goto(pathToFileURL(fixture).href)
    await nativeView.evaluate(() => {
      window.ResizeObserver = undefined
    })
    await nativeView.addScriptTag({ content: host + '\n' + loader + '\n' + source })
    await nativeView.evaluate((credentials) => {
      const renderer = {
        // The App renderer's own Vue root is a detached comment, not the visible view.
        $el: document.createComment('app-renderjs'),
        $ownerInstance: {
          $el: document.querySelector('#root'),
          callMethod(name, event) {
            if (name === 'onStatus') window.mapStatus = event
          },
        },
      }
      Object.assign(renderer, window.mapDefinition.methods)
      // Deliberately deliver props before mounted, as can happen across the App bridge.
      renderer.update({
        active: true,
        compact: true,
        credentials,
        retry: 0,
        reset: 0,
        theme: 'light',
        regions: [{ id: 'REG-002', name: '二七区', facilityCount: 3, alarmCount: 1 }],
      })
      window.mapDefinition.mounted.call(renderer)
      window.renderer = renderer
    }, credentials)
    await nativeView.waitForFunction(() => window.mapStatus?.status === 'ready', null, { timeout: 45000 })
    await nativeView.locator('.water-region-marker').waitFor({ timeout: 45000 })
    await nativeView.waitForTimeout(1500)
    await nativeView.screenshot({ path: out + '/file-view.png' })
    await nativeView.evaluate(() => window.renderer.release())
    assert.equal(await nativeView.locator('.amap-layer').count(), 0)
    await nativeView.close()
  })
  assert.deepEqual(errors, [])
  fs.writeFileSync(
    out + '/result.json',
    JSON.stringify(
      { checks, errors, sdkMocked: false, apkBuilt: false, androidDeviceTested: false },
      null,
      2,
    ),
  )
} catch (e) {
  fs.writeFileSync(out + '/failure.json', JSON.stringify({ checks, errors }, null, 2))
  await page.screenshot({ path: out + '/failure.png', fullPage: true }).catch(() => {})
  throw new Error(sanitize(e.message))
} finally {
  await browser.close()
}
