import * as config from './config'
import configVue from './config.vue'
import indexVue from './index.vue'

const component = indexVue

const BzPipeline = {
  component,
  config,
  name: component.name,
  version: component.version,
  configVue
}

const Bz‌Pipeline = BzPipeline

export { BzPipeline, Bz‌Pipeline }
