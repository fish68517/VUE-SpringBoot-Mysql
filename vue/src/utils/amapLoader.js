import AMapLoader from '@amap/amap-jsapi-loader'
import { loadAmapConfig } from './amapConfig'

let amapPromise

export async function loadAmap() {
  const config = await loadAmapConfig()

  window._AMapSecurityConfig = {
    securityJsCode: config.securityJsCode
  }

  if (!amapPromise) {
    amapPromise = AMapLoader.load({
      key: config.key,
      version: '2.0',
      plugins: [
        'AMap.ToolBar',
        'AMap.Scale',
        'AMap.Marker',
        'AMap.InfoWindow',
        'AMap.Geocoder'
      ]
    })
  }

  return amapPromise
}
