/*
 * @Author: kaix
 * @Date: 2023-03-24 15:44:58
 * @LastEditTime: 2023-09-04 08:58:21
 * @LastEditors: wangcong
 * @Description:
 */
import * as config from './config'
import configVue from './config.vue'
import indexVue from './index.vue'

const component = indexVue

const BarCommon = {
  component,
  config,
  name: component.name,
  version: component.version,
  configVue
}


export { BarCommon }
