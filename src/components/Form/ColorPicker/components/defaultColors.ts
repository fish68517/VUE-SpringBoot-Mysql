/*
 * @Author: wangcong
 * @Date: 2023-08-21 10:46:32
 * @LastEditTime: 2023-08-29 09:26:31
 * @LastEditors: wangcong
 * @Description: 
 */

const colors = [
  '#000000',
  '#FFFFFF',
  '#555555',
  '#CED4DE',
  '#FBE7AC',
  '#E86452',
  '#F8D0CB',
  '#6DC8EC',
  '#945FB9',
  '#FF99C3',

  '#B3B9AD',
  '#3E6F51',
  '#174F7F',
  '#5E8389',
  '#A2D99B',
  '#31A354',
  '#026D2B',
  '#3C82A4',
  '#C919F8',
  '#0000E1',

  '#DE2910',
  '#FFDE00',
  '#02F7FE',
  '#FF9201',
  '#FFCA6D',
  '#8A71FF',
  '#83A2EC',
  '#9AA93B',
  '#4E74B0',
  '#00B567',

  '#F26B68',
  '#EF5B21',
  '#FDF0EA',
  '#6094E6',
  '#D6DAF7',
  '#7A5F6B',
  '#937372',
  '#C1C09C',
  '#A2AAB3',
  '#D3A56E'
]

export const defaultColors = colors.map((item, index) => {
  return {
    color: item,
    id: index + 1
  }
})