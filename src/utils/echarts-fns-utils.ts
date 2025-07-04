/*
 * @Author: kaix
 * @Date: 2023-12-15 14:08:32
 * @LastEditTime: 2023-12-18 17:15:29
 * @LastEditors: kaix
 * @Description: echarts的工具函数
 */

/**
 * @description: 计算Y轴的最大值，和划分尺度
 * @param {number} arr
 * @param {number} settingInterval
 * @return {*}
 */
const getYInterval = (arr: number[], settingInterval: number = 5, max: number | string = 'dataMax') => {
  const returnError = () => {
    return {
      interval: undefined,
      max: undefined
    }
  }

  let _max = max === 'dataMax' ? 0 : max
  let interval = 0

  if (!arr) {
    return returnError()
  }

  try {
    arr.forEach(item => {
      _max = Math.max(_max as number, item)
    })
  } catch (e) {
    return returnError()
  }
  _max === 0 ? (max = 1) : ''
  interval = _max as number / settingInterval

  if (interval !== parseInt(interval + '')) {
    interval = Math.ceil(interval)
    // 将最大值取整
    _max = interval * settingInterval
  }

  return {
    interval,
    max: _max
  }
}

// console.log(getYInterval([0.23, 100, 2, 3000.58], 3))

export { getYInterval }
