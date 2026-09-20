import { spawn } from 'node:child_process'
import path from 'node:path'
const [mode = 'dev', platform = 'h5'] = process.argv.slice(2)
const root = process.cwd()
const args = [path.join(root, 'node_modules/@dcloudio/vite-plugin-uni/bin/uni.js')]
if (mode === 'build') args.push('build')
args.push('-p', platform)
const child = spawn(process.execPath, args, {
  stdio: 'inherit',
  env: {
    ...process.env,
    UNI_INPUT_DIR: root,
    UNI_OUTPUT_DIR: path.join(root, 'unpackage/dist', mode, platform),
    NODE_ENV: mode === 'build' ? 'production' : 'development',
  },
})
child.on('exit', (code) => process.exit(code ?? 1))
