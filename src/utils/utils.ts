/*
 * @Author: kaix
 * @Date: 2023-03-16 21:56:59
 * @LastEditTime: 2025-02-25 14:27:23
 * @LastEditors: sundaohui 2459997429@qq.com
 * @Description:
 */
/**
 * * 生成一个不重复的ID
 * @param { Number } randomLength
 */
import isObject from 'lodash/isObject'
export const getUUID = (randomLength = 10) => {
  return Number(Math.random().toString().substring(2, randomLength) + Date.now()).toString(36)
}

export function isString(p: any): p is string {
  return typeof p === 'string'
}

export function isNumber(p: any): p is number {
  return typeof p === 'number'
}

export function isArray(p: any): p is [] {
  return Array.isArray(p)
}

/**
 * * 判断是否是预览页、发布页
 * @returns boolean
 */
export const isPreview = () => {
  const arr = ['preview', 'publish']
  // return document.location.hash.includes('preview')
  return arr.some(it => document.location.hash.includes(it))
}

/**
 * * 通过硬解析获取当前路由下的参数
 * @returns object
 */
export const fetchRouteParamsLocation = () => {
  try {
    return document.location.hash.split('/').pop() || ''
  } catch (error) {
    window['$message'].warning('查询路由信息失败，请联系管理员！')
    return ''
  }
}

/**
 * @description: 给图表属性的对象添加默认值属性赋值
 * @param {any} targetObj
 * @param {any} defaultObj
 * @return {*}
 */
export function setDefaultValue(targetObj: any, defaultObj: any) {
  if (!targetObj) {
    return defaultObj
  }
  // 判断是否有对应的属性
  for (let key in defaultObj) {
    if (defaultObj.hasOwnProperty(key) && !targetObj.hasOwnProperty(key)) {
      targetObj[key] = defaultObj[key]
    }
  }
  return targetObj
}

/**
 * @description: 设置 option 的属性 排除对象
 * @param {any} option
 * @param {any} targetObj
 * @param {string} exclude
 * @return {*}
 */
export function setOptionValue(option: any, targetObj: any, exclude = []) {
  // 判断是否有对应的属性
  for (let key in targetObj) {
    if (targetObj.hasOwnProperty(key)) {
      if(isObject(targetObj[key]) || exclude.includes(key)) return null
      const value = targetObj[key]
      const oldValue = option[key]
      if (!oldValue) {
        option[key] = targetObj[key]
      } else if (oldValue !== value) {
        option[key] = targetObj[key]
      }
    }
  }
  return option
}

export function isNumeric(str) {
  return !isNaN(str) && !isNaN(parseFloat(str));
}

export function isBackgroundClipTextSupported() {
  const div = document.createElement('div');
  div.style.backgroundClip = 'text';
  return div.style.backgroundClip === 'text';
  // return false
}

// 随机生成一个色值
export function getRandomColor(num: number = 6) {
  // 定义十六进制字符集
  const hexChars = '0123456789abcdef'
  let color = '#'
  // 循环 num 次，每次随机选择一个十六进制字符
  for (let i = 0; i < num; i++) {
    const randomIndex = Math.floor(Math.random() * 16)
    color += hexChars[randomIndex]
  }
  return color
}
