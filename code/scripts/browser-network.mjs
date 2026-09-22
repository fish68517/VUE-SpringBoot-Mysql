export function isMapRequest(raw) {
  const url = new URL(raw)
  return url.protocol === 'https:' && (url.hostname === 'amap.com' || url.hostname.endsWith('.amap.com'))
}
export function resourceLabel(raw) {
  const url = new URL(raw)
  // Request queries can contain browser map credentials. Keep them out of evidence and logs.
  return url.origin + url.pathname
}
