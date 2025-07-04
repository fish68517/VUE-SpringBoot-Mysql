/*
 * @Author: kaix
 * @Date: 2023-08-09 15:53:52
 * @LastEditTime: 2023-11-17 10:48:13
 * @LastEditors: kaix
 * @Description: 全局配置的参数
 */

/*
 * 字体 基座可引入动态字体
 */
const _fontFamilyOption = [
  { label: '微软雅黑', value: 'Microsoft YaHei' },
  { label: '宋体', value: 'SongTi' },
  { label: '黑体', value: 'HeiTi' },
  { label: '庞门正道', value: 'PangMenZhengDao' },
  { label: 'DINPro', value: 'DINPro' },
  {label: '优设标题黑', value: 'YouShe'},
  {label: '锐字锐线怒放黑简', value: 'RuiZiRuiXian'}
]
export const fontFamilyOption = window['componentConfig']?.fontFamilyOption ?? _fontFamilyOption

const _fontWeightOption = [
  { label: 'Normal', value: 'normal' },
  { label: 'Bold', value: 'bold' },
  { label: 'Bolder', value: 'bolder' },
  { label: 'Lighter', value: 'lighter' }
]
/*
 * 字体粗细样式
 */
export const fontWeightOption = window['componentConfig']?.fontWeightOption ?? _fontWeightOption

const _selectTextStyleOption = [
  { label: 'Normal', value: 'normal' },
  { label: 'Italic', value: 'italic' },
  { label: 'Oblique', value: 'oblique' }
]
/*
 * 字体描述样式
 */
export const selectTextStyleOption = window['componentConfig']?.selectTextStyleOption ?? _selectTextStyleOption

export const selectSortOption = [
  { label: '按原始数据', value: 'origin' },
  { label: '按数据大小', value: 'sort' }
]

/*
* echarts标记形状
*/
export const symbols = [
  {label: '水滴', value: 'pin'},
  {label: '圆形', value: 'circle'},
  {label: '矩形', value: 'rect'},
  {label: '圆角矩形', value: 'roundRect'},
  {label: '三角形', value: 'triangle'},
  {label: '菱形', value: 'diamond'},
  {label: '箭头', value: 'arrow'},
]


/**
 * 饼图数值样式选项
 */
export const valueOption = [
  {
    value: 'value',
    label: '数值'
  },
  {
    value: 'percent',
    label: '百分比'
  }
]
export const precisionOption = [
  {
    value: 'origin',
    label: '默认值'
  },
  {
    value: 'two',
    label: '保留两位小数'
  }
]
export const valueSortOption = [
  {
    value: 'value',
    label: '数值位于第一行'
  },
  {
    value: 'percent',
    label: '百分比位于第一行'
  },
  {
    value: 'value1',
    label: '数值位于前面'
  },
  {
    value: 'percent1',
    label: '百分比位于前面'
  },
]
export const legendSortOption = [
  {
    value: 'right',
    label: '在图例文本右侧'
  },
  {
    value: 'bottom',
    label: '在图例文本下方'
  }
]
export const legendValueOption = [
  {
    value: 'left',
    label: '数值在左，百分比在右'
  },
  {
    value: 'right',
    label: '数值在右，百分比在左'
  }
]
export const legendValueOption1 = [
  {
    value: 'left',
    label: '数值在上，百分比在下'
  },
  {
    value: 'right',
    label: '数值在下，百分比在上'
  }
]

export const textAlignOptions = [
  {label: '左对齐', value: 'left'},
  {label: '居中对齐', value: 'center'},
  {label: '右对齐', value: 'right'}
]

export const objectFits = [
  { label: "拉伸填充", value: "fill" },
  { label: "保持原大小", value: "none" },
  { label: "等比缩放", value: "contain" },
  { label: "等比裁剪", value: "cover" },
  { label: "适应短边", value: "scale-down" },
];