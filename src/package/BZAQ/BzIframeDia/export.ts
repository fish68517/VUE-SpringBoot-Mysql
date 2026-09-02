import * as config from './config'
import configVue from './config.vue'
import indexVue from './index.vue'

const component = indexVue

const BzIframeDia = {
  component,
  config,
  name: component.name,
  version: component.version,
  configVue
}

export { BzIframeDia }
