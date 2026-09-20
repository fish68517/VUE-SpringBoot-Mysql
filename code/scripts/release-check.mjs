import fs from 'node:fs'
import path from 'node:path'
import crypto from 'node:crypto'
const root = path.resolve(process.argv[2] || 'unpackage/dist/build/h5')
if (!fs.existsSync(path.join(root, 'index.html'))) throw new Error('发行目录缺少 index.html')
const files = fs
  .readdirSync(root, { recursive: true })
  .filter((f) => fs.statSync(path.join(root, f)).isFile())
if (files.some((f) => /\.xlsx$|开发方案|文档\.txt|\.keystore$|\.jks$|create-initial/.test(f)))
  throw new Error('发布目录包含不应发布的材料')
const requirement = path.resolve('../doc/请 分析阅读：城市智慧供水-功能模块.xlsx 文档.txt')
const secretCandidates = fs.existsSync(requirement)
  ? fs.readFileSync(requirement, 'utf8').match(/\b[a-f0-9]{32}\b|(?<=ClientSecret：\s*)[A-Za-z0-9+/=]+/g) ||
    []
  : []
for (const file of files.filter((f) => /\.(js|html|json)$/.test(f))) {
  const content = fs.readFileSync(path.join(root, file), 'utf8')
  if (secretCandidates.some((value) => content.includes(value)))
    throw new Error('检测到原始部署参数，停止发布检查')
}
const result = {
  ok: true,
  deployed: false,
  cloudChecks: 'USER_MANAGED',
  files: files.length,
  bytes: files.reduce((sum, f) => sum + fs.statSync(path.join(root, f)).size, 0),
  indexSha256: crypto
    .createHash('sha256')
    .update(fs.readFileSync(path.join(root, 'index.html')))
    .digest('hex'),
  checkedAt: new Date().toISOString(),
}
fs.mkdirSync('unpackage/evidence', { recursive: true })
fs.writeFileSync('unpackage/evidence/release-check.json', JSON.stringify(result, null, 2))
console.log(JSON.stringify(result, null, 2))
