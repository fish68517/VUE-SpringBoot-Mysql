// 国际化配置 - 简体中文
import zhCN from './zh-CN.js'

// 当前语言
let currentLocale = 'zh-CN'

// 获取翻译
export const t = (key) => {
  const keys = key.split('.')
  let value = zhCN
  
  for (const k of keys) {
    value = value[k]
    if (!value) return key
  }
  
  return value
}

// 设置语言
export const setLocale = (locale) => {
  currentLocale = locale
}

// 获取当前语言
export const getLocale = () => {
  return currentLocale
}

export default {
  t,
  setLocale,
  getLocale
}
