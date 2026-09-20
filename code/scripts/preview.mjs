import http from 'node:http'
import fs from 'node:fs'
import path from 'node:path'
const root = path.resolve('unpackage/dist/build/h5'),
  port = Number(process.env.PORT || 4173)
const mime = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.mp4': 'video/mp4',
  '.woff2': 'font/woff2',
}
http
  .createServer((req, res) => {
    let url
    try {
      url = decodeURIComponent((req.url || '/').split('?')[0])
    } catch {
      res.writeHead(400)
      return res.end('Bad URL')
    }
    const file = path.resolve(root, '.' + (url === '/' ? '/index.html' : url))
    if (!file.startsWith(root + path.sep)) {
      res.writeHead(403)
      return res.end('Forbidden')
    }
    if (!fs.existsSync(file) || !fs.statSync(file).isFile()) {
      res.writeHead(404)
      return res.end('Not found')
    }
    res.writeHead(200, {
      'Content-Type': mime[path.extname(file)] || 'application/octet-stream',
      'Cache-Control': 'no-cache',
    })
    fs.createReadStream(file).pipe(res)
  })
  .listen(port, '127.0.0.1', () => console.log('本地发行预览 http://127.0.0.1:' + port))
