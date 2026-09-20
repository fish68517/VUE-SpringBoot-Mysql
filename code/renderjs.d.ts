import '@vue/runtime-core'
declare module '@vue/runtime-core' {
  // uni-app 编译器注入的 renderjs 模块，vue-tsc 不解析第二个 script 块。
  interface ComponentCustomProperties {
    chart: { update: (value: unknown) => void }
    map: { update: (value: unknown) => void }
  }
}
