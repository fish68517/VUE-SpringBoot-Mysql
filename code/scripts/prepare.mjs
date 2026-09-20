import fs from 'node:fs'
import path from 'node:path'
const root = process.cwd()
for (const dir of ['static/vendor', 'static/demo-assets', 'generated', 'unpackage/evidence'])
  fs.mkdirSync(path.join(root, dir), { recursive: true })
fs.copyFileSync('node_modules/echarts/dist/echarts.min.js', 'static/vendor/echarts.min.js')
fs.copyFileSync('node_modules/echarts/LICENSE', 'static/vendor/ECHARTS-LICENSE.txt')
for (const file of fs.readdirSync('data/assets'))
  if (!file.endsWith('.json'))
    fs.copyFileSync(path.join('data/assets', file), path.join('static/demo-assets', file))
fs.writeFileSync(
  'generated/build-info.json',
  JSON.stringify({ version: 'phase2', preparedAt: new Date().toISOString(), source: 'code/data' }, null, 2),
)
console.log('演示资源准备完成，全部资源使用本地文件。')
