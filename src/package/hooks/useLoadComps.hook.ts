/*
 * @Author: kaix
 * @Date: 2023-04-08 15:57:52
 * @LastEditTime: 2023-04-08 16:02:07
 * @LastEditors: kaix
 * @Description:
 */
export const useLoadComps = (tsModules: Record<string, unknown>, indexModules: Record<string, unknown>) => {
  /**
   * * 获取目标组件配置信息
   * @param targetData
   */
  const createComponent = async (category: string, key: string) => {
    // 此处应该换成远程加载组件
    const chart = await import(`./${category}/${key}/config.ts`)
    return new chart.default()
  }

  /**
   * * 获取config组件
   * @param {string} chartName 名称
   * @param {FetchComFlagType} flag 标识 0为展示组件, 1为配置组件
   */
  const fetchComponentTs = (category: string, configKey: string) => {
    const module = tsModules
    for (const key in module) {
      const [, pChatName, chatName] = key.split('/')
      if (pChatName === category && chatName === configKey) {
        return module[key]
      }
    }
  }

  /**
   * * 获取展示组件
   * @param {ConfigType} dropData 配置项
   */
  const fetchChartComponentTs = (category: string, key: string) => {
    const chart = fetchComponentTs(category, key)
    // console.log('chart', chart)
    // return new (chart as any)!.default()
    // 不应该创建 会导致Id唯一了
    return chart
  }

  /**
   * * 获取组件
   * @param {string} chartName 名称
   * @param {FetchComFlagType} flag 标识 0为展示组件, 1为配置组件
   */
  const fetchComponent = (chartName: string) => {
    const module = indexModules
    for (const key in module) {
      const urlSplit = key.split('/')
      if (urlSplit[urlSplit.length - 2] === chartName) {
        return module[key]
      }
    }
  }

  /**
   * * 获取展示组件
   * @param {ConfigType} dropData 配置项
   */
  const fetchChartComponent = (key: string) => {
    return (fetchComponent(key) as any)?.default
  }

  return {
    createComponent,
    fetchChartComponentTs,
    fetchChartComponent
  }
}
