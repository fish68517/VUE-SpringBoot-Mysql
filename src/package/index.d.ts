/*
 * @Author: kaix
 * @Date: 2023-03-14 20:36:43
 * @LastEditTime: 2025-06-25 14:49:47
 * @LastEditors: wangcong
 * @Description: echarts 的全局属性
 */
// import { defaultOption } from '@/package/config/chart-option'
import defaultOption from '@/package/config/global.theme.json'

import { RequestConfigType } from './public/index.d'

export type GlobalThemeJsonType = typeof defaultOption

export interface GridType {
  left: string | number
  right: string | number
  bottom: string | number
  top: string | number
  containLabel: boolean
  [T: string]: any
}

export enum ChartFrameEnum {
  // 支持 dataset 的 echarts 框架
  ECHARTS = 'echarts',
  // UI 组件框架
  NAIVE_UI = 'naiveUI',
  // 自定义带数据组件
  COMMON = 'common',
  // 无数据变更
  STATIC = 'static',
  MAP = 'map'
}

// 包分类枚举
export enum PackagesCategoryEnum {
  CHARTS = 'Charts',
  TABLES = 'Tables',
  INFORMATIONS = 'Informations',
  DECORATES = 'Decorates'
}

// 全局 option 类型
export interface ChartConfigType extends Partial<GlobalThemeJsonType> {
  dataset?: any
  [T: string]: any
}

// 组件配置
export type ConfigType = {
  key: string
  chartKey: string
  conKey: string
  title: string
  category?: string
  categoryName?: string
  package?: string
  chartFrame?: ChartFrameEnum
  // image: string | (() => Promise<typeof import('*.png')>)
  image?: string
  version?: string,
  viewOnlyCodeBlocks?: boolean,
  chartWithMultiLevel?: boolean,
  noScale?: boolean,
  eventKey?:string
}

// 组件新增条件
export interface newAttrInter {
  // 排序样式
  sortStyle: string
  // 空值隐藏
  emptyHide: boolean
  // 圆角位置
  borderPosition: string
  // 组件配色方案
  themeColor: {
    useGlobalColor: boolean // 是否使用全局主题色
    selfTheme: {
      // 自身主题
      color: string[] // 配色
      value: string // 名称
    }
  }
  // x轴标签展示形式
  xAxisLabelType: string
  // x轴标签换行或者省略字符数
  xAxisLabelCount: number
  // 是否展示x轴单位
  showXaxisName: boolean
  // 是否显示y轴单位
  showYaxisName: boolean
  // 图例位置
  legendPostion: string
  // 折线图是否开启面积
  lineShowArea?: boolean
  // 折线图面积透明度
  lineAreaOpacity?: number
  // 自由配置属性
  [T: string]: any
}

// 滤镜/变换枚举
export enum FilterEnum {
  // 是否启用
  FILTERS_SHOW = 'filterShow',

  // 透明度
  OPACITY = 'opacity',
  // 饱和度
  SATURATE = 'saturate',
  // 对比度
  CONTRAST = 'contrast',
  // 色相
  HUE_ROTATE = 'hueRotate',
  // 亮度
  BRIGHTNESS = 'brightness',

  // 旋转
  ROTATE_Z = 'rotateZ',
  ROTATE_X = 'rotateX',
  ROTATE_Y = 'rotateY',

  // 倾斜
  SKEW_X = 'skewX',
  SKEW_Y = 'skewY',

  // 混合模式
  BLEND_MODE = 'blendMode'
}

// 组件状态
export interface StatusType {
  lock: boolean
  hide: boolean
}

// 基础事件类型(vue不加 on)
export enum BaseEvent {
  // 点击
  ON_CLICK = 'click',
  // 双击
  ON_DBL_CLICK = 'dblclick',
  // 移入
  ON_MOUSE_ENTER = 'mouseenter',
  // 移出
  ON_MOUSE_LEAVE = 'mouseleave'
}

// vue3 生命周期事件
export enum EventLife {
  // 渲染之后
  VNODE_MOUNTED = 'vnodeMounted',
  // 渲染之前
  VNODE_BEFORE_MOUNT = 'vnodeBeforeMount'
}

// 组件实例类
export interface PublicConfigType {
  id: string
  isGroup: boolean
  attr: { x: number; y: number; w: number; h: number; zIndex: number; offsetX: number; offsetY: number }
  styles: {
    [FilterEnum.FILTERS_SHOW]: boolean
    [FilterEnum.OPACITY]: number
    [FilterEnum.SATURATE]: number
    [FilterEnum.CONTRAST]: number
    [FilterEnum.HUE_ROTATE]: number
    [FilterEnum.BRIGHTNESS]: number

    [FilterEnum.ROTATE_Z]: number
    [FilterEnum.ROTATE_X]: number
    [FilterEnum.ROTATE_Y]: number

    [FilterEnum.SKEW_X]: number
    [FilterEnum.SKEW_Y]: number
    [FilterEnum.BLEND_MODE]: string
    // 动画
    animations: string[]
    // 景深
    perspective: number
  }
  filter?: string
  status: StatusType
  events: {
    baseEvent: {
      [K in BaseEvent]?: string
    }
    advancedEvents: {
      [K in EventLife]?: string
    }
  }
  newAttr?: newAttrInter
}

// 组件成组实例类
export interface CreateComponentGroupType extends CreateComponentType {
  groupList: Array<CreateComponentType>
}

// 数据请求
interface requestConfig {
  request: RequestConfigType
}

export interface CreateComponentType extends PublicConfigType, requestConfig {
  key: string
  chartConfig: ConfigType
  option: ChartConfigType | any
}

export const globalThemeJson = { ...defaultOption, dataset: null }

// 配色系列配置
export type ColorConfigType = {
  color: string[]
  label: string
  val: string
}
