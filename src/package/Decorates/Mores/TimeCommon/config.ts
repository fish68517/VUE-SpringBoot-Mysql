/*
 * @Author: kaix
 * @Date: 2023-03-12 21:58:55
 * @LastEditTime: 2024-10-16 09:51:00
 * @LastEditors: sundaohui 2459997429@qq.com
 * @Description:
 */
import { PublicConfigClass } from '../../../public/index'
import { TimeCommonConfig } from './index'
import { CreateComponentType } from '../../../index.d'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitConfig } from '@/package/config/const'

export enum FontWeightEnum {
  NORMAL = '常规',
  BOLD = '加粗'
}

export const FontWeightObject = {
  [FontWeightEnum.NORMAL]: 'normal',
  [FontWeightEnum.BOLD]: 'bold'
}

export const option = {
  // 数据说明
  timeSize: 24,
  timeLineHeight: 50,
  timeTextIndent: 2,
  timeColor: '#E6F7FF',
  fontWeight: 'normal',
  timeFormatter: 'weekTime',
  timeStyle: 'one',
  lrSpacing: 0,
  isYearMonthDayText: true,
  isWeekText: true,
  dateType: '',
  isDateText: true,
  inputTime: null,
  inputDate: null,
  yearMonthDayOption: {
    fontSize: 24,
    fontFamily: '微软雅黑',
    fontWeight: 'normal',
    textColor: '#fff'
  },
  weekOption: {
    fontSize: 24,
    fontFamily: '微软雅黑',
    fontWeight: 'normal',
    textColor: '#fff'
  },
  dateOption: {
    showSeconds: true, // 展示秒钟
    fontSize: 24,
    fontFamily: '微软雅黑',
    fontWeight: 'normal',
    textColor: '#fff'
  },

  //阴影
  showShadow: true,
  hShadow: 0,
  vShadow: 0,
  blurShadow: 8,
  colorShadow: '#0075ff'
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = TimeCommonConfig.key
  public attr = { ...chartInitConfig, w: 380, h: 50, zIndex: -1 }
  public chartConfig = cloneDeep(TimeCommonConfig)
  public option = cloneDeep(option)
}
