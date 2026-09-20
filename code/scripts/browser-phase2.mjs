import { chromium } from 'playwright'
import fs from 'node:fs'
import assert from 'node:assert/strict'
const base = process.env.DEMO_URL || 'http://127.0.0.1:4173',
  out = 'unpackage/evidence/phase2'
fs.mkdirSync(out, { recursive: true })
const browser = await chromium.launch({ channel: 'chrome', headless: true }),
  context = await browser.newContext({ viewport: { width: 1440, height: 1000 } }),
  page = await context.newPage()
const checks = [],
  errors = [],
  httpErrors = [],
  external = []
page.on('pageerror', (e) => errors.push(e.message))
page.on('response', (r) => {
  if (r.status() >= 400) httpErrors.push(r.url() + ' ' + r.status())
})
page.on('request', (r) => {
  if (!r.url().startsWith(base) && !r.url().startsWith('blob:') && !r.url().startsWith('data:'))
    external.push(r.url())
})
const field = (label) =>
  page
    .locator('uni-input,uni-textarea')
    .filter({ has: page.getByText(label, { exact: true }) })
    .locator('input,textarea')
const body = () => page.locator('.page-body').innerText()
const state = () =>
  page.evaluate(() => {
    const raw = localStorage.getItem('smart-water:state:v1')
    let v = JSON.parse(raw)
    return typeof v === 'string' ? JSON.parse(v) : v.data ? JSON.parse(v.data) : v
  })
async function nav(path) {
  await page.goto(base + '/#/pages/' + path)
  await page.locator('.page-title,.screen-title,.login-welcome').first().waitFor()
  await page.waitForTimeout(150)
}
async function login(role = '管理员') {
  await nav('login/index')
  await page.getByText(role, { exact: true }).click()
  await page.locator('.login-submit').click()
  await page.locator('.screen-title,.greeting-title').first().waitFor()
}
async function click(text) {
  await page.getByText(text, { exact: true }).click()
  await page.waitForTimeout(350)
}
async function shot(name) {
  await page.screenshot({ path: out + '/' + name + '.png', fullPage: true })
}
async function check(name, fn) {
  await fn()
  checks.push({ name, passed: true })
  console.log('PASS', name)
}
try {
  await login()
  await check('点设施编辑，六次审核，地图及报表同步，刷新保留', async () => {
    await nav('map/changes?id=FAC-001')
    await field('名称').fill('中原路压力监测点 · 审核更新')
    await click('提交六级审核')
    await page.getByText('第 1 级待审', { exact: false }).waitFor()
    assert.notEqual((await state()).facilities[0].name, '中原路压力监测点 · 审核更新')
    for (let i = 1; i <= 6; i++) {
      await field('审核意见（必填）').fill('第 ' + i + ' 级确认')
      await click('通过第 ' + i + ' 级')
    }
    assert.equal((await state()).facilities[0].name, '中原路压力监测点 · 审核更新')
    await shot('review-effective')
    await page.reload()
    assert.ok((await body()).includes('已生效') || (await state()).phase2.changes[0].status === 'effective')
    await nav('reports/index')
    await page.locator('select').nth(0).selectOption('network')
    assert.ok((await body()).includes('生效修改'))
    await shot('network-report')
  })
  await check('管线修改驳回并重新提交', async () => {
    await nav('map/changes?kind=pipe&id=PIPE-002')
    await field('名称').fill('管线修改示例')
    await click('提交六级审核')
    await field('审核意见（必填）').fill('调整管径')
    await click('驳回')
    await click('修改并重新提交')
    await field('管径 mm').fill('350')
    await click('提交六级审核')
    assert.equal((await state()).phase2.changes.at(-1).stage, 1)
    assert.equal(
      (await state()).phase2.pipes[1].diameterMm,
      JSON.parse(fs.readFileSync('data/pipes.json', 'utf8'))[1].diameterMm,
    )
  })
  await check('字段草稿、模拟同步及发布', async () => {
    await nav('map/config')
    await page.getByText('状态', { exact: true }).click()
    await click('保存草稿')
    await click('模拟同步')
    await click('模拟发布')
    assert.ok((await state()).phase2.layers.fields.includes('status'))
  })
  await check('矩形框选和多边形面选，设施及管线过滤', async () => {
    await nav('map/index')
    await page.locator('select').last().selectOption('box')
    const c = page.locator('.map-canvas canvas')
    const r = await c.boundingBox()
    await page.mouse.move(r.x + 30, r.y + 30)
    await page.mouse.down()
    await page.mouse.move(r.x + r.width / 2, r.y + r.height / 2, { steps: 8 })
    await page.mouse.up()
    await page.waitForTimeout(200)
    assert.ok((await body()).includes('区域已选'))
    await shot('map-box')
    await click('清除区域选择')
    await page.locator('select').last().selectOption('polygon')
    for (const q of [
      [0.1, 0.1],
      [0.6, 0.15],
      [0.5, 0.65],
    ])
      await c.click({ position: { x: r.width * q[0], y: r.height * q[1] } })
    await click('完成面选')
    await shot('map-polygon')
    const text = await body()
    assert.ok(/区域已选\s*[1-9]/.test(text))
  })
  await check('新模板挂接、必填项上报、改派和异常工单独立', async () => {
    await nav('inspection/edit')
    await page.locator('select').nth(0).selectOption('TPL-002')
    await field('填写巡检任务名称').fill('第二阶段模板巡检')
    await page.locator('select').nth(3).selectOption('USER-002')
    await page.locator('.choice').filter({ hasText: '中原路压力监测点' }).click()
    await click('发布任务')
    await page.waitForURL(/inspection\/detail/)
    await click('发现异常')
    await field('检查说明（必填）').fill('演示检查发现泄漏')
    await page.locator('.check-card input[type=number]').fill('0.18')
    await page.locator('.check-card select').selectOption('需清理')
    await click('保存检查项')
    await page.waitForTimeout(120)
    assert.ok((await body()).includes('关联工单'))
    const reassign = page.locator('select').last()
    const values = await reassign.locator('option').evaluateAll((es) => es.map((e) => e.value))
    await reassign.selectOption(values.at(-1))
    await field('改派或取消原因（必填）').fill('调整巡检区域人员')
    await click('保存改派')
    await click('提交完成巡检')
    await shot('inspection-completed')
    assert.ok((await body()).includes('已完成'))
  })
  await check('轨迹播放、暂停、速度与人员工作量', async () => {
    await nav('inspection/replay?id=TASK-001')
    await click('播放轨迹')
    await page.waitForTimeout(1200)
    await click('暂停')
    const before = await body()
    assert.ok(!before.includes('08:00:00 · 执行人'))
    await page.waitForTimeout(700)
    assert.equal(await body(), before)
    await page.locator('select').nth(1).selectOption('4')
    await click('播放轨迹')
    await page.waitForTimeout(600)
    await click('暂停')
    await shot('inspection-replay')
  })
  await check('采样间隔、启停事件、能耗方案和 DMA 阈值', async () => {
    await nav('energy/index?deviceId=DEV-004')
    await field('采样间隔 1—300 秒').fill('15')
    await page.locator('select').nth(2).selectOption('off')
    await click('保存模拟设置')
    assert.equal((await state()).phase2.devices.find((d) => d.id === 'DEV-004').samplingIntervalSec, 15)
    await shot('energy')
    await nav('dma/detail?id=DMA-001')
    await field('漏损阈值 %').fill('5')
    await click('保存阈值 %')
    assert.equal((await state()).phase2.dmas[0].threshold, 0.05)
  })
  await check('视频多画面、本地 MP4 实际播放和离线占位', async () => {
    await nav('video/index')
    assert.equal(await page.locator('video').count(), 2)
    const video = page.locator('video').first()
    await video.evaluate(async (v) => {
      v.muted = true
      await v.play()
    })
    await page.waitForTimeout(1200)
    const status = await video.evaluate((v) => ({
      time: v.currentTime,
      ready: v.readyState,
      error: v.error?.message,
    }))
    assert.ok(status.time > 0 && status.ready >= 2 && !status.error)
    assert.ok((await body()).includes('通道离线'))
    await shot('video-center')
    await nav('portal/index')
    assert.equal(await page.locator('video').count(), 0)
  })
  await check('门户保存、JSON 导出与导入、模拟通知', async () => {
    await nav('settings/advanced')
    await click('＋ 视频中心')
    await click('保存门户布局')
    await click('生成模拟通知记录')
    const download = page.waitForEvent('download')
    await click('导出 JSON 快照')
    const file = await download
    await file.saveAs(out + '/snapshot.json')
    const raw = fs.readFileSync(out + '/snapshot.json', 'utf8')
    assert.ok(!raw.includes('password'))
    const chooser = page.waitForEvent('filechooser')
    await click('读取快照文件')
    await (await chooser).setFiles(out + '/snapshot.json')
    await page.getByText(/已读取快照文件/).waitFor()
    await click('校验并导入快照')
    await click('确定')
    await page.waitForTimeout(200)
    assert.ok((await state()).phase2.portalCards['USER-001'].includes('video'))
    await shot('snapshot-settings')
  })
  await check('六类日报月报及 CSV 下载', async () => {
    await nav('reports/index')
    for (const type of ['usage', 'pressure', 'energy', 'runtime', 'inspection', 'network'])
      for (const period of ['day', 'month']) {
        await page.locator('select').nth(0).selectOption(type)
        await page.locator('select').nth(1).selectOption(period)
        assert.ok((await body()).includes(period === 'day' ? '日报' : '月报'))
      }
    const d = page.waitForEvent('download')
    await click('⇩ 导出当前报表')
    await (await d).saveAs(out + '/report.csv')
  })
  const newPages = [
    'map/changes',
    'map/config',
    'map/analysis',
    'logs/index',
    'inspection/config',
    'inspection/replay',
    'energy/index',
    'video/index',
    'settings/advanced',
    'mobile/report',
  ]
  await check('新增十页面及 390 像素布局，无横向溢出', async () => {
    for (const width of [1440, 390]) {
      await page.setViewportSize({ width, height: 844 })
      for (const path of newPages) {
        await nav(path)
        const overflow = await page.evaluate(() => document.documentElement.scrollWidth > innerWidth + 2)
        assert.equal(overflow, false, path + ' @' + width)
      }
      await shot('mobile-report-' + width)
    }
  })
  await check('移动阀门模拟切换与事件上报', async () => {
    const v = JSON.parse(fs.readFileSync('data/facilities.json', 'utf8')).find((f) => f.type === 'valve')
    await nav('facilities/detail?id=' + v.id)
    const control = page.getByText(/模拟阀门：/)
    const before = await control.innerText()
    await control.click()
    await click('确定')
    assert.notEqual(await control.innerText(), before)
    await nav('mobile/report')
    await field('事件标题（必填）').fill('移动端事件上报')
    await field('事件描述（必填）').fill('模拟井盖周边积水')
    await click('上报并建立工单')
    await page.waitForURL(/workorders\/detail/)
    assert.ok((await body()).includes('移动端事件上报'))
    await shot('mobile-event-order')
  })
  await check('界面新建自定义模板和区域路线', async()=>{
    await nav('inspection/config')
    await field('模板编号，如 TPL-003').fill('TPL-UI')
    await field('模板名称').fill('界面新增检查模板')
    await click('添加字段')
    await field('字段名称').fill('阀门外观')
    await page.locator('.check-card select').selectOption('choice')
    await field('选项，用顿号分隔').fill('完好、异常')
    await click('保存模板')
    assert.equal((await state()).phase2.templates.find(t=>t.id==='TPL-UI').fields[0].type,'choice')
    await field('路线编号').fill('ROUTE-UI')
    await field('路线名称').fill('界面新增区域路线')
    await page.locator('.equal-columns > .panel').nth(1).locator('.choice').first().click()
    await click('保存路线与区域')
    assert.equal((await state()).phase2.routes.find(r=>r.id==='ROUTE-UI').facilityIds.length,1)
  })
  await check('操作员在移动布局新建本人巡检并保留权限边界', async()=>{
    await page.locator('.top-actions .text-button').click()
    await login('操作员')
    await nav('inspection/edit')
    await field('填写巡检任务名称').fill('移动本人巡检')
    await page.locator('.choice').first().click()
    await click('发布任务')
    await page.waitForURL(/inspection\/detail/)
    assert.ok((await body()).includes('移动本人巡检'))
    assert.equal(await page.getByText('保存改派',{exact:true}).count(),0)
    assert.equal((await state()).inspections.at(-1).assigneeId,'USER-002')
    await shot('mobile-own-task')
  })
  await check('第二阶段修改后重置，审核、模板、采样与门户配置恢复',async()=>{
    await page.locator('.top-actions .text-button').click()
    await login('管理员')
    await nav('settings/index')
    await click('重置并加载场景')
    await click('确定')
    const s=await state()
    assert.equal(s.phase2.changes.length,0)
    assert.equal(s.phase2.samples.length,0)
    assert.equal(s.phase2.templates.length,2)
    assert.equal(s.phase2.routes.length,6)
    assert.deepEqual(s.phase2.portalCards,{})
    assert.equal(s.workorders.length,40)
    assert.equal(s.inspections.length,24)
  })
  assert.deepEqual(errors, [])
  assert.deepEqual(httpErrors, [])
  assert.deepEqual(external, [])
} catch (e) {
  checks.push({ name: '第二阶段浏览器验收异常', passed: false, error: e.message.split('\n')[0] })
  console.error(e.message.split('\n')[0])
  process.exitCode = 1
  try {
    await shot('failure')
  } catch {}
} finally {
  fs.writeFileSync(
    out + '/browser.json',
    JSON.stringify(
      {
        checkedAt: new Date().toISOString(),
        passed: checks.every((c) => c.passed) && !errors.length && !httpErrors.length && !external.length,
        checks,
        errors,
        httpErrors,
        external,
      },
      null,
      2,
    ),
  )
  await browser.close()
}
