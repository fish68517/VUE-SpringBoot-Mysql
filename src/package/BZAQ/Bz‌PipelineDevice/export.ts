import * as config from './config'
import configVue from './config.vue'
import indexVue from './index.vue'

const component = indexVue

const Bz‌PipelineDevice = {
  component,
  config,
  name: component.name,
  version: component.version,
  configVue
}

const BzPipelineDevice = Bz‌PipelineDevice

export { Bz‌PipelineDevice, BzPipelineDevice }
