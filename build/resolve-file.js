/*
 * @Author: kaix
 * @Date: 2023-07-27 16:27:48
 * @LastEditTime: 2023-09-04 16:02:47
 * @LastEditors: kaix
 * @Description: 解析 file 模块
 */
const fs = require('fs')
const path = require('path')
const { compare } = require('compare-versions')
const defaultVersion = require('./defaultVersion')

// Border03: 'src/package/Decorates/Borders/Border03/export.ts',
// Border08: 'src/package/Decorates/Borders/Border08/export.ts'

const components = []

function getComponents() {
  const componentsDir = readDirsRecursively('./src/package')
  // console.log('componentsDir', componentsDir)
  componentsDir.forEach(dir => {
    const dirName = path.basename(dir);
    // console.log('dirName', dirName)
    const _arr = fs.readdirSync(dir).filter(it => it === 'export.ts')
    _arr.forEach(componentName => {
      components.push({
        entry: path.join(dir, componentName),
        name: dirName
      })
    })
  })
    // console.log('components', components)
  return components
}

function readDirsRecursively(directory) {
  const files = fs.readdirSync(directory)
  const result = []

  files.forEach(function (file) {
    const filePath = path.join(directory, file)
    const stat = fs.statSync(filePath)

    if (stat && stat.isDirectory()) {
      result.push(filePath)
      const subDirs = readDirsRecursively(filePath)
      if (subDirs.length !== 0) {
        result.push(...subDirs)
      }
    }
  })

  return result
}

/*
 * 读取文件信息 获取文本内容
 */
function readContentByFile(filePath) {
  try {
    // 使用 fs.readFileSync 方法读取文件内容，文件内容将直接返回并赋值给 fileContent 变量
    const fileContent = fs.readFileSync(filePath, 'utf8')
    return fileContent
  } catch (err) {
    console.error('Error reading file:', err)
  }
}

/*
 * 根据正则匹配版本号
 */
function getVersionNum(inputString) {
  // 定义正则表达式
  const regex = /<script\s*lang="ts">([\s\S]*?)<\/script>/

  // 使用正则表达式的 exec 方法进行匹配
  const match = regex.exec(inputString)

  // console.log(match[1])
  // 获取版本号
  let version = null
  if (match && match[1]) {
    const content = match[1]
    const versionRegex = /version:\s*["']([\d.]+)["']/
    const versionMatch = versionRegex.exec(content)
    // console.log('versionMatch', versionMatch)
    version = versionMatch ? versionMatch[1] : null
  }
  return version
}

/*
 * 核心方法：获取指定组件的 名称和版本号等信息
 * 根据正则匹配版本号
 */
function parseComponent(compName) {
  const components = getComponents()
  //   console.log('components', components)
  const _component = components.find(it => it.name === compName)
  if (!_component) {
    throw new Error(`找不到对应的组件：组件名【${compName}】`)
  }
  const { entry } = _component
  /*
   * 校验 导出的组件的组件名称 和 版本号
   * 默认当前版本是 0.2.53 小于这个版本视为错误 提示用户去更改组件版本
   * 目前获取 版本号没有好的办法 通过正则匹配文件内容
   */
  const stringContent = readContentByFile(entry.replace('export.ts', 'index.vue'))
  // console.log(stringContent)
  const version = getVersionNum(stringContent)
  // console.log('version', version)
  const isLe = compare(version, defaultVersion, '<')
  if (isLe) {
    throw new Error(
      `请检查组件的版本号,当前版本：${version}，目前要求组件版本不得小于【${defaultVersion}】, 文件存在：${entry.replace(
        'export.ts',
        'index.vue'
      )} 中，请改下当前版本号`
    )
  }

  return [entry, version]
}

module.exports = {
  getComponents,
  parseComponent
}
