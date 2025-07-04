
/*
 * @Author: kaix
 * @Date: 2025-06-30 10:24:53
 * @LastEditTime: 2025-07-04 12:25:21
 * @LastEditors: kaix
 * @Description: 发布、订阅模式-自定义事件
 */

import mitt, { Emitter } from 'mitt'

type Events = {
  [event: string]: any
}

interface CacheOptions {
  maxCacheSize?: number // 每个事件最大缓存数量
  enableCache?: boolean // 是否启用缓存
}

class CachedEventBus {
  private emitter: Emitter<Events>
  private cache: Map<string, any[]>
  private options: Required<CacheOptions>

  constructor(options: CacheOptions = {}) {
    this.emitter = mitt<Events>()
    this.cache = new Map()
    this.options = {
      maxCacheSize: 100, // 默认最多缓存100条历史事件
      enableCache: true,
      ...options
    }
  }

  emit(event: string, data: any) {
    if (this.options.enableCache) {
      if (!this.cache.has(event)) {
        this.cache.set(event, [])
      }
      const eventCache = this.cache.get(event)!
      eventCache.push(data)
      
      // 限制缓存大小，避免内存泄漏
      if (eventCache.length > this.options.maxCacheSize) {
        eventCache.shift() // 移除最早的事件
      }
    }
    this.emitter.emit(event, data)
  }

  on(event: string, handler: (data: any) => void) {
    // 先把历史事件回放给 handler
    if (this.options.enableCache) {
      const history = this.cache.get(event)
      if (history) {
        history.forEach(item => handler(item))
      }
    }
    // 再监听后续新事件
    this.emitter.on(event, handler)
  }

  off(event: string, handler?: (data: any) => void) {
    this.emitter.off(event, handler)
    
    // 如果没有传入具体的handler，说明要移除该事件的所有监听器
    // 这时应该清除该事件的缓存，而不是所有缓存
    if (handler === undefined) {
      this.cache.delete(event)
    }
  }

  // 获取事件的监听器数量（用于调试）
  getListenerCount(event: string): number {
    const listeners = this.emitter.all.get(event)
    return listeners ? listeners.length : 0
  }

  // 获取缓存的事件数量
  getCacheSize(event?: string): number {
    if (event) {
      return this.cache.get(event)?.length || 0
    }
    return Array.from(this.cache.values()).reduce((total, arr) => total + arr.length, 0)
  }

  // 检查是否有监听器
  hasListeners(event: string): boolean {
    return this.getListenerCount(event) > 0
  }

  clearCache(event?: string) {
    if (event) {
      this.cache.delete(event)
    } else {
      this.cache.clear()
    }
  }

  // 销毁实例，清理所有资源
  destroy() {
    this.emitter.all.clear()
    this.cache.clear()
  }
}

const cachedEventBus = new CachedEventBus()
export default cachedEventBus

// 导出类，方便创建多个实例
export { CachedEventBus }
