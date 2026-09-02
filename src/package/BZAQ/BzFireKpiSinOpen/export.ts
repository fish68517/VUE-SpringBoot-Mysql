import * as config from './config'
import configVue from './config.vue'
import indexVue from './index.vue'

const component = indexVue

const BzFireKpiSinOpen = {
  component,
  config,
  name: component.name,
  version: component.version,
  configVue
}

export { BzFireKpiSinOpen }
