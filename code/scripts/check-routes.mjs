import fs from 'node:fs'
const pages = JSON.parse(fs.readFileSync('pages.json', 'utf8')).pages
for (const p of pages) if (!fs.existsSync(p.path + '.vue')) throw new Error('路由缺少页面 ' + p.path)
if (new Set(pages.map((p) => p.path)).size !== pages.length) throw new Error('路由重复')
const source = fs.readFileSync('navigation/routeMap.ts', 'utf8'),
  definition = source.match(/export const routes[^=]*=\s*\{([\s\S]*?)\n\}/)?.[1] || ''
const keys = [...definition.matchAll(/(\w+):\s*'([^']+)'/g)].map((m) => m[1])
if (keys.length !== pages.length) throw new Error('路由映射数量与 pages.json 不一致')
for (const dir of ['components', 'layouts', 'pages']) {
  for (const file of fs.readdirSync(dir, { recursive: true }).filter((f) => f.endsWith('.vue'))) {
    const text = fs.readFileSync(dir + '/' + file, 'utf8')
    for (const match of text.matchAll(/go\('([^']+)'/g))
      if (!keys.includes(match[1])) throw new Error('未知跳转 ' + match[1] + ' in ' + file)
  }
}
console.log('路由检查通过：' + pages.length + ' 个页面及静态跳转目标存在')
