// This module is imported only by renderjs (the browser / App view layer).
let loading
export function loadAMap(credentials) {
  if (!credentials.key || (!credentials.securityJsCode && !credentials.serviceHost))
    return Promise.reject(new Error('CONFIG_MISSING'))
  if (window.AMap?.Map) return Promise.resolve(window.AMap)
  if (loading) return loading
  // Must be configured before the SDK script is requested.
  window._AMapSecurityConfig = credentials.serviceHost
    ? { serviceHost: credentials.serviceHost }
    : { securityJsCode: credentials.securityJsCode }
  loading = new Promise((resolve, reject) => {
    const script = document.createElement('script')
    const callback = '__waterAMapReady'
    let settled = false
    const finish = (error) => {
      if (settled) return
      settled = true
      clearTimeout(timer)
      delete window[callback]
      script.onerror = null
      if (error) {
        script.remove()
        reject(error)
      } else resolve(window.AMap)
    }
    const timer = setTimeout(() => finish(new Error('SDK_TIMEOUT')), 20000)
    window[callback] = () => finish(window.AMap?.Map ? null : new Error('SDK_UNAVAILABLE'))
    script.onerror = () => finish(new Error('SDK_NETWORK'))
    script.src =
      'https://webapi.amap.com/maps?v=2.0&key=' +
      encodeURIComponent(credentials.key) +
      '&plugin=AMap.DistrictSearch,AMap.Geocoder&callback=' +
      callback
    script.async = true
    document.head.appendChild(script)
  }).catch((error) => {
    loading = undefined
    throw error
  })
  return loading
}

// Regional markers are aggregate indicators, never invented facility coordinates.
export async function regionCenters(AMap) {
  const withDeadline = (start) =>
    new Promise((resolve) => {
      const timer = setTimeout(() => resolve(null), 12000)
      start((value) => {
        clearTimeout(timer)
        resolve(value)
      })
    })
  const [city, east] = await Promise.all([
    withDeadline((done) =>
      new AMap.DistrictSearch({ level: 'city', subdistrict: 1, extensions: 'base' }).search(
        '410100',
        (status, result) => done(status === 'complete' ? result.districtList?.[0] : null),
      ),
    ),
    withDeadline((done) =>
      new AMap.Geocoder({ city: '郑州' }).getLocation('郑州市郑东新区管委会', (status, result) =>
        done(status === 'complete' ? result.geocodes?.[0]?.location : null),
      ),
    ),
  ])
  const centers = {}
  for (const district of city?.districtList || []) centers[district.name] = district.center
  if (east) centers['郑东片区'] = east
  return centers
}
