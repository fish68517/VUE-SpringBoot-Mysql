import * as config from './config'
import configVue from './config.vue'
import indexVue from './index.vue'

const component = indexVue

const Bz‌PipelineDig = {
  component,
  config,
  name: component.name,
  version: component.version,
  configVue
}

const BzPipelineDig = Bz‌PipelineDig

export { Bz‌PipelineDig, BzPipelineDig }
