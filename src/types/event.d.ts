/*
 * @Author: kaix
 * @Date: 2023-09-15 11:27:31
 * @LastEditTime: 2024-07-23 16:05:58
 * @LastEditors: likang
 * @Description:
 */
import { DetermineEnum } from './eventEnum'
import type { DefaultLabelFormatterCallbackParams } from 'echarts'

export interface ChartEventType extends DefaultLabelFormatterCallbackParams {
  type: string
}
// 条件的生效：0 表示满足其一 1 全部满足
export type EffectType = 0 | 1

// 事件条件类型
export interface EventCondition {
  Field: string
  value: string | number
  // 判断条件
  determine: DetermineEnum
}

export interface EventListType {
  id: string
  // 条件
  eventCondition?: Array<EventCondition>
  // 条件生效类型 二选一
  conditionEffectType?: EffectType
}

// 自定义事件的函数类型
export type CustomFnType = {
  name: string
  fnName: string
  keys?: string[]
  layerName?: string
  layerKey?: string
  type?: string
}
