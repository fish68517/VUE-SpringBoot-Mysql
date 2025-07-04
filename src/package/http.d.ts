/*
 * @Author: kaix
 * @Date: 2023-03-16 22:12:27
 * @LastEditTime: 2023-03-18 14:17:43
 * @LastEditors: kaix
 * @Description:
 */

import { RequestParamsTypeEnum, RequestBodyEnum, RequestHttpIntervalEnum, RequestParamsObjType } from './enmus/httpEnum'

export type RequestParams = {
  [RequestParamsTypeEnum.PARAMS]: RequestParamsObjType
  [RequestParamsTypeEnum.HEADER]: RequestParamsObjType
  [RequestParamsTypeEnum.BODY]: {
    [RequestBodyEnum.FORM_DATA]: RequestParamsObjType
    [RequestBodyEnum.X_WWW_FORM_URLENCODED]: RequestParamsObjType
    [RequestBodyEnum.JSON]: string
    [RequestBodyEnum.XML]: string
  }
}

// 请求公共类型
type RequestPublicConfigType = {
  // 时间单位（时分秒）
  requestIntervalUnit: RequestHttpIntervalEnum
  // 请求内容
  requestParams: RequestParams
}
