declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, unknown>
  export default component
}

declare module 'lodash/*'
declare module 'dom-helpers'

declare module 'vue3-clickoutside-component'

declare module 'd3-color'

declare module 'maptalks'
declare module '@maptalks/gl-layers'

declare module 'ace-builds'

declare module 'element-resize-detector'