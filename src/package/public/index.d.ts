/*
 * @Author: kaix
 * @Date: 2023-03-16 17:17:14
 * @LastEditTime: 2023-04-18 08:59:19
 * @LastEditors: wangcong
 * @Description:
 */
import { RequestHttpIntervalEnum, RequestDataTypeEnum, RequestHttpEnum, RequestContentTypeEnum, RequestBodyEnum } from "../enmus/httpEnum";
import { RequestParams } from "../http.d";


export interface EchartsDataType {
  dimensions: string[]
  source: any[]
}

export enum ChatCategoryEnum {
  BAR = 'Bars',
  PIE = 'Pies',
  LINE = 'Lines',
  SCATTER = 'Scatters',
  MAP = 'Maps',
  MORE = 'Mores',
  BORDER = 'Borders',
  DECORATE = 'Decorates',
  TEXT='Texts',
  ELEMENT = 'Elements',
}

export enum ChatCategoryEnumName {
  BAR = '柱状图',
  PIE = '饼图',
  LINE = '折线图',
  SCATTER = '散点图',
  MAP = '地图',
  MORE = '更多',
  BORDER = '边框',
  DECORATE = '装饰',
  TEXT='文字',
  ELEMENT = '元素'
}

// 请求公共类型
type RequestPublicConfigType = {
  // 时间单位（时分秒）
  requestIntervalUnit: RequestHttpIntervalEnum
  // 请求内容
  requestParams: RequestParams
}

// 单个图表请求配置
export interface RequestConfigType extends RequestPublicConfigType {
  // 所选全局数据池的对应 id
  requestDataPondId?: string
  // 组件定制轮询时间
  requestInterval?: number
  // 获取数据的方式
  requestDataType: RequestDataTypeEnum
  // 请求方式 get/post/del/put/patch
  requestHttpType: RequestHttpEnum
  // 源后续的 url
  requestUrl?: string
  // 请求内容主体方式 普通/sql
  requestContentType: RequestContentTypeEnum
  // 请求体类型
  requestParamsBodyType: RequestBodyEnum
  // SQL 请求对象
  requestSQLContent: {
    sql: string
  }
}
