/**
 * 性能监控工具
 * 用于监控API响应时间和前端性能
 */

class PerformanceMonitor {
  constructor() {
    this.metrics = new Map()
    this.thresholds = {
      login: 500,
      getProducts: 300,
      createOrder: 500,
      getWarnings: 200,
      getWeather: 200,
    }
  }

  /**
   * 记录API调用时间
   * @param {string} apiName - API名称
   * @param {number} duration - 耗时（毫秒）
   */
  recordAPICall(apiName, duration) {
    if (!this.metrics.has(apiName)) {
      this.metrics.set(apiName, [])
    }

    const calls = this.metrics.get(apiName)
    calls.push({
      timestamp: Date.now(),
      duration,
    })

    // 只保留最近100条记录
    if (calls.length > 100) {
      calls.shift()
    }

    // 检查是否超过阈值
    const threshold = this.thresholds[apiName]
    if (threshold && duration > threshold) {
      console.warn(
        `⚠️ ${apiName} 响应时间过长: ${duration}ms (阈值: ${threshold}ms)`
      )
    }
  }

  /**
   * 获取API的平均响应时间
   * @param {string} apiName - API名称
   * @returns {number} 平均响应时间
   */
  getAverageTime(apiName) {
    const calls = this.metrics.get(apiName)
    if (!calls || calls.length === 0) return 0

    const total = calls.reduce((sum, call) => sum + call.duration, 0)
    return Math.round(total / calls.length)
  }

  /**
   * 获取API的最大响应时间
   * @param {string} apiName - API名称
   * @returns {number} 最大响应时间
   */
  getMaxTime(apiName) {
    const calls = this.metrics.get(apiName)
    if (!calls || calls.length === 0) return 0

    return Math.max(...calls.map(call => call.duration))
  }

  /**
   * 获取API的最小响应时间
   * @param {string} apiName - API名称
   * @returns {number} 最小响应时间
   */
  getMinTime(apiName) {
    const calls = this.metrics.get(apiName)
    if (!calls || calls.length === 0) return 0

    return Math.min(...calls.map(call => call.duration))
  }

  /**
   * 获取所有API的性能统计
   * @returns {Object} 性能统计数据
   */
  getStats() {
    const stats = {}

    for (const [apiName, calls] of this.metrics) {
      if (calls.length > 0) {
        stats[apiName] = {
          count: calls.length,
          average: this.getAverageTime(apiName),
          max: this.getMaxTime(apiName),
          min: this.getMinTime(apiName),
        }
      }
    }

    return stats
  }

  /**
   * 打印性能报告
   */
  printReport() {
    console.log('=== API 性能报告 ===')
    const stats = this.getStats()

    for (const [apiName, data] of Object.entries(stats)) {
      const threshold = this.thresholds[apiName] || 'N/A'
      console.log(`
${apiName}:
  调用次数: ${data.count}
  平均时间: ${data.average}ms
  最大时间: ${data.max}ms
  最小时间: ${data.min}ms
  阈值: ${threshold}ms
      `)
    }
  }

  /**
   * 清空所有指标
   */
  clear() {
    this.metrics.clear()
  }

  /**
   * 设置API的响应时间阈值
   * @param {string} apiName - API名称
   * @param {number} threshold - 阈值（毫秒）
   */
  setThreshold(apiName, threshold) {
    this.thresholds[apiName] = threshold
  }
}

// 创建全局实例
export const performanceMonitor = new PerformanceMonitor()

/**
 * 创建性能监控装饰器
 * @param {string} apiName - API名称
 * @returns {Function} 装饰器函数
 */
export function withPerformanceMonitoring(apiName) {
  return function (target, propertyKey, descriptor) {
    const originalMethod = descriptor.value

    descriptor.value = async function (...args) {
      const startTime = performance.now()
      try {
        const result = await originalMethod.apply(this, args)
        const duration = Math.round(performance.now() - startTime)
        performanceMonitor.recordAPICall(apiName, duration)
        return result
      } catch (error) {
        const duration = Math.round(performance.now() - startTime)
        performanceMonitor.recordAPICall(apiName, duration)
        throw error
      }
    }

    return descriptor
  }
}

/**
 * 测量函数执行时间
 * @param {string} name - 函数名称
 * @param {Function} fn - 要测量的函数
 * @returns {*} 函数返回值
 */
export async function measureTime(name, fn) {
  const startTime = performance.now()
  try {
    const result = await fn()
    const duration = Math.round(performance.now() - startTime)
    console.log(`⏱️ ${name} 耗时: ${duration}ms`)
    return result
  } catch (error) {
    const duration = Math.round(performance.now() - startTime)
    console.error(`❌ ${name} 失败，耗时: ${duration}ms`)
    throw error
  }
}

/**
 * 获取页面加载性能指标
 * @returns {Object} 性能指标
 */
export function getPagePerformanceMetrics() {
  if (!window.performance || !window.performance.timing) {
    return null
  }

  const timing = window.performance.timing
  const navigation = window.performance.navigation

  return {
    // 页面加载时间
    pageLoadTime: timing.loadEventEnd - timing.navigationStart,
    // DOM解析时间
    domParseTime: timing.domContentLoadedEventEnd - timing.navigationStart,
    // 资源加载时间
    resourceLoadTime: timing.loadEventEnd - timing.domContentLoadedEventEnd,
    // DNS查询时间
    dnsTime: timing.responseEnd - timing.fetchStart,
    // TCP连接时间
    tcpTime: timing.responseStart - timing.connectStart,
    // 服务器响应时间
    serverTime: timing.responseEnd - timing.requestStart,
    // 页面渲染时间
    renderTime: timing.domComplete - timing.domLoading,
    // 导航类型
    navigationType: navigation.type,
  }
}

/**
 * 监控内存使用情况
 * @returns {Object} 内存信息
 */
export function getMemoryInfo() {
  if (!performance.memory) {
    return null
  }

  return {
    // 已使用内存
    usedJSHeapSize: Math.round(performance.memory.usedJSHeapSize / 1048576),
    // 总堆大小
    totalJSHeapSize: Math.round(performance.memory.totalJSHeapSize / 1048576),
    // 堆大小限制
    jsHeapSizeLimit: Math.round(performance.memory.jsHeapSizeLimit / 1048576),
  }
}

/**
 * 监控长任务
 * @param {Function} callback - 长任务完成时的回调
 */
export function observeLongTasks(callback) {
  if ('PerformanceObserver' in window) {
    try {
      const observer = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
          callback({
            name: entry.name,
            duration: entry.duration,
            startTime: entry.startTime,
          })
        }
      })

      observer.observe({ entryTypes: ['longtask'] })
      return observer
    } catch (e) {
      console.warn('Long Task API not supported')
    }
  }
}

/**
 * 获取首屏加载时间
 * @returns {number} 首屏加载时间（毫秒）
 */
export function getFirstPaintTime() {
  if ('PerformanceObserver' in window) {
    try {
      const observer = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
          if (entry.name === 'first-paint') {
            return entry.startTime
          }
        }
      })

      observer.observe({ entryTypes: ['paint'] })
    } catch (e) {
      console.warn('Paint Timing API not supported')
    }
  }

  return null
}

/**
 * 获取最大内容绘制时间
 * @returns {Promise<number>} 最大内容绘制时间（毫秒）
 */
export function getLargestContentfulPaint() {
  return new Promise((resolve) => {
    if ('PerformanceObserver' in window) {
      try {
        const observer = new PerformanceObserver((list) => {
          const entries = list.getEntries()
          const lastEntry = entries[entries.length - 1]
          resolve(lastEntry.renderTime || lastEntry.loadTime)
        })

        observer.observe({ entryTypes: ['largest-contentful-paint'] })

        // 超时后返回null
        setTimeout(() => resolve(null), 5000)
      } catch (e) {
        console.warn('Largest Contentful Paint API not supported')
        resolve(null)
      }
    } else {
      resolve(null)
    }
  })
}

export default performanceMonitor
