/*
 * @Author: kaix
 * @Date: 2023-03-19 21:15:04
 * @LastEditTime: 2023-05-17 14:01:52
 * @LastEditors: wangcong
 * @Description: d3-color 颜色处理工具
 */
import * as d3 from 'd3-color'

/**
 * @description: 颜色渐变色处理
 * @param {string} color 格式支持 rgb、rgba、hex、hls、hlsa、steelblue（类似blue、red等颜色字符串）
 * @param {number} alpha
 * @return {*} 颜色 string
 */
export const getColorAlpha = (color: string, alpha: number):string => {
  return d3.color(color)?.copy({ opacity: alpha })?.toString()
}

export const getColorRgbaArray = (color: string): number[] | null => {
  if (!color) {
    return null
  }
  const { r, g, b, opacity }  = d3.color(color)
  return [r, g ,b, opacity]
}
