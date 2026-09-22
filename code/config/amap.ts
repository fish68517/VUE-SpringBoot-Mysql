type MapCredentials = { key: string; securityJsCode: string; serviceHost: string }
const local = import.meta.glob('./amap.local.json', { eager: true, import: 'default' })
export const amapCredentials: MapCredentials = {
  key: '',
  securityJsCode: '',
  serviceHost: '',
  ...((local['./amap.local.json'] || {}) as Partial<MapCredentials>),
}
