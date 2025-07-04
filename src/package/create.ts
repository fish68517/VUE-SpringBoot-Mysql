/*
 * @Author: kaix
 * @Date: 2023-03-15 21:58:42
 * @LastEditTime: 2023-03-15 22:13:29
 * @LastEditors: kaix
 * @Description:
 */
import { App } from 'vue'
import version from './version'

import { useLoadEcharts } from './hooks/useLoadEcharts.hook'
import { useParseConfig } from './hooks/useParseConfig.hook'
import { ChartConfigType } from './index.d'

type ComponentType = any

export interface CoolInstance {
  version: string
  // eslint-disable-next-line no-unused-vars
  install: (app: App, chartConfig: ChartConfigType) => void
}

interface CoolCreateOptions {
  components?: ComponentType[]
}

function create({ components = [] }: CoolCreateOptions = {}): CoolInstance {
  const installTargets: App[] = []
  function registerComponent(app: App, name: string, component: ComponentType): void {
    const registered = app.component(name)
    if (!registered) {
      app.component(name, component)
    }
  }
  function install(app: App, chartConfig: ChartConfigType): void {
    if (installTargets.includes(app)) return
    installTargets.push(app)

    // 加载注册vue-echarts
    useLoadEcharts(app)

    useParseConfig(app, chartConfig)

    components.forEach(component => {
      const { name, alias } = component
      registerComponent(app, name, component)
      if (alias) {
        alias.forEach((aliasName: string) => {
          registerComponent(app, aliasName, component)
        })
      }
    })
  }
  return {
    version,
    install
  }
}

export default create
