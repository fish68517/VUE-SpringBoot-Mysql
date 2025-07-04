/*
 * @Author: kaix
 * @Date: 2023-03-24 15:44:58
 * @LastEditTime: 2023-07-13 20:02:37
 * @LastEditors: kaix
 * @Description:
 */
import * as config from './config'

import configVue from './config.vue'
import indexVue from './index.vue'

const component = indexVue
const TimeCommon = {
  component: component,
  config,
  name: component.name,
  version: component.version,
  configVue
}

export { TimeCommon }
