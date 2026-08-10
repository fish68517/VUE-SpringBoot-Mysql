let configPromise

export function loadAmapConfig() {
  if (!configPromise) {
    configPromise = fetch('/config/amap.txt')
      .then(response => {
        if (!response.ok) throw new Error('高德地图配置读取失败')
        return response.text()
      })
      .then(text => Object.fromEntries(
        text.split(/\r?\n/)
          .map(line => line.trim())
          .filter(line => line && !line.startsWith('#'))
          .map(line => {
            const index = line.indexOf('=')
            if (index < 1) throw new Error(`高德地图配置格式错误：${line}`)
            return [line.slice(0, index).trim(), line.slice(index + 1).trim()]
          })
      ))
      .then(config => {
        if (!config.key || !config.securityJsCode) {
          throw new Error('高德地图 key 或 securityJsCode 缺失')
        }
        return config
      })
  }
  return configPromise
}
