/*
 * @Author: kaix
 * @Date: 2023-03-14 20:51:32
 * @LastEditTime: 2023-04-04 17:01:53
 * @LastEditors: kaix
 * @Description:
 */
import { App } from 'vue'
import merge from 'lodash/merge'
import { defaultOption } from '@/package/config/chart-option'
import { ChartConfigType } from '../index.d'

// 解析全局 属性
export const useParseConfig = (app: App, config: ChartConfigType) => {
  const mergeConfig: ChartConfigType = merge(defaultOption, config)
  // app.provide('defaultConfig', mergeConfig)
  return config
}
