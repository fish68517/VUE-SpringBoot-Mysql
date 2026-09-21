export function mobileClient(platform: string, width: number) {
  return ['app', 'app-plus'].includes(platform) || width < 900
}

const mobileAliases: Record<string, string> = {
  dashboard: 'mobileHome',
  portal: 'mobileHome',
  inspection: 'mobileTasks',
  map: 'mobileMap',
  settings: 'profile',
}

export function clientRoute(key: string, mobile: boolean) {
  return mobile ? mobileAliases[key] || key : key
}

export function mobileTab(key: string) {
  if (['mobileTasks', 'taskDetail', 'taskEdit', 'inspectionReplay', 'inspectionConfig'].includes(key))
    return 'mobileTasks'
  if (['mobileMap', 'facility', 'device', 'mapChanges', 'mapConfig', 'mapAnalysis'].includes(key))
    return 'mobileMap'
  if (['profile', 'advancedSettings', 'logs'].includes(key)) return 'profile'
  return 'mobileHome'
}
