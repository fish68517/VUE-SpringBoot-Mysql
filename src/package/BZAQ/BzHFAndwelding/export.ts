import * as config from './config'
import configVue from './config.vue'
import indexVue from './index.vue'

const BzHFAndwelding = {
  component: indexVue,
  config,
  name: indexVue.name,
  version: indexVue.version,
  configVue
}

export { BzHFAndwelding }
