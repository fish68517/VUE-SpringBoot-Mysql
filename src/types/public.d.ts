/*
 * @Author: kaix
 * @Date: 2023-09-14 14:15:18
 * @LastEditTime: 2024-07-05 15:29:47
 * @LastEditors: wangcong
 * @Description: 
 */
export interface TableDataType {
  name: string
  slotName: string
}

export type paramType = {
  id: string,
  name: string,
  content: string,
  variableType: 0 | 1 | 2
}
