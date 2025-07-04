/*
 * @Author: wangcong
 * @Date: 2023-08-28 17:24:58
 * @LastEditTime: 2023-08-28 17:27:22
 * @LastEditors: wangcong
 * @Description: 
 */
import { ColorType } from "./color.d"

export const getColor = (type: ColorType, value: string | null): string => {
  if (value) {
    
  } else {
    switch (type) {
      case ColorType.HEX: {
        return '#000000ff'
      }
      case ColorType.RGB: {
        return 'rgba(0,0,0,1)'
      }
    }
  }
}