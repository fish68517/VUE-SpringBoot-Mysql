import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs'
import { mobileClient, clientRoute, mobileTab } from '../navigation/clientPolicy'
import { loginDestination, routes } from '../navigation/routeMap'

test('启动页先登录分流，不再由审核页制造首次登录回跳', () => {
  const pages = JSON.parse(fs.readFileSync('pages.json', 'utf8')).pages
  assert.equal(pages[0].path, 'pages/login/index')
  assert.equal(Object.keys(routes)[0], 'login')
  assert.equal(loginDestination('', true), '/pages/mobile/home')
  assert.equal(loginDestination('', false), '/pages/dashboard/index')
})

test('App 横屏仍走移动端；H5 按窗口宽度适配', () => {
  assert.equal(mobileClient('app', 1200), true)
  assert.equal(mobileClient('app-plus', 1600), true)
  assert.equal(mobileClient('web', 390), true)
  assert.equal(mobileClient('web', 1440), false)
})

test('手机回跳映射主入口，保留实体参数和合法详情，拒绝外部回跳', () => {
  assert.equal(
    loginDestination('/pages/map/index?facilityId=FAC-007', true),
    '/pages/mobile/map?facilityId=FAC-007',
  )
  assert.equal(
    loginDestination('/pages/inspection/index?view=history', true),
    '/pages/mobile/tasks?view=history',
  )
  assert.equal(
    loginDestination('/pages/facilities/detail?id=FAC-007', true),
    '/pages/facilities/detail?id=FAC-007',
  )
  assert.equal(loginDestination('https://example.com', true), '/pages/mobile/home')
  assert.equal(
    loginDestination('/pages/map/index?facilityId=FAC-007', false),
    '/pages/map/index?facilityId=FAC-007',
  )
  assert.equal(clientRoute('settings', true), 'profile')
  assert.equal(clientRoute('dashboard', true), 'mobileHome')
  assert.equal(clientRoute('mapChanges', true), 'mapChanges')
  assert.equal(mobileTab('taskDetail'), 'mobileTasks')
  assert.equal(mobileTab('facility'), 'mobileMap')
})
