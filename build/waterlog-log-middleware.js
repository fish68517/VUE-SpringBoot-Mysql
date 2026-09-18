// 浏览器不能直接写磁盘，通过本地开发服务器保存诊断日志；不提供任意路径写入。
const fs = require('fs')
const path = require('path')
const express = require('express')
const directory = path.resolve(__dirname, '../src/package/BZAQ/BzHFAndwelding')

function saveLog(data) {
  const date = new Date()
  const pad = value => String(value).padStart(2, '0')
  const stamp = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}_${pad(date.getHours())}-${pad(date.getMinutes())}-${pad(date.getSeconds())}`
  const body = JSON.stringify(data, (key, value) => /token|authorization|password|cookie|secret/i.test(key) ? '[已隐藏]' : value, 2)
  // 同一秒内多次请求增加序号，避免覆盖前一份日志。
  for (let index = 0; ; index++) {
    const name = `水情接口日志_${stamp}${index ? '_' + index : ''}.json`
    try {
      fs.writeFileSync(path.join(directory, name), body, { encoding: 'utf8', flag: 'wx' })
      return name
    } catch (error) {
      if (error.code !== 'EEXIST') throw error
    }
  }
}

function install(app) {
  app.post('/__waterlog_diagnostics', (req, res, next) => {
    const address = req.socket.remoteAddress
    const local = ['127.0.0.1', '::1', '::ffff:127.0.0.1'].includes(address)
    const origin = req.headers.origin
    if (!local || (origin && origin !== `http://${req.headers.host}` && origin !== `https://${req.headers.host}`)) {
      return res.status(403).json({ message: '仅允许本机同源保存日志' })
    }
    next()
  }, express.json({ limit: '5mb' }), (req, res) => {
    try {
      if (!req.body || req.body.日志类型 !== '水情站点接口诊断') {
        return res.status(400).json({ message: '日志格式不正确' })
      }
      res.json({ 文件名: saveLog(req.body) })
    } catch (error) {
      res.status(500).json({ message: `日志保存失败：${error.message}` })
    }
  })
}

module.exports = { install, saveLog }
