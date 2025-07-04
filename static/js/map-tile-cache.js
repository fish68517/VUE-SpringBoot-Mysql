const CACHE_NAME = 'map-tile-cache-v1'
const TILE_CACHE_PREFIX = 'tile-'
const MAX_CACHE_ITEMS = 1000 // 最大缓存数量

// 监听安装事件
self.addEventListener('install', event => {
  self.skipWaiting()
})

// 监听激活事件
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheName !== CACHE_NAME) {
            return caches.delete(cacheName)
          }
        })
      )
    })
  )
})

// 管理缓存大小
async function manageCacheSize(cache) {
  const keys = await cache.keys()
  if (keys.length > MAX_CACHE_ITEMS) {
    // 如果超过最大缓存数量，删除最旧的缓存
    const itemsToDelete = keys.slice(0, keys.length - MAX_CACHE_ITEMS)
    await Promise.all(itemsToDelete.map(key => cache.delete(key)))
  }
}

// 监听请求事件
self.addEventListener('fetch', event => {
  const url = new URL(event.request.url)

  // 处理天地图瓦片请求
  if (url.hostname.includes('tianditu.gov.cn')) {
    event.respondWith(
      caches.match(event.request).then(response => {
        // 如果在缓存中找到响应，则返回缓存的响应
        if (response) {
          return response
        }

        // 如果没有在缓存中找到，则发起网络请求
        return fetch(event.request)
          .then(networkResponse => {
            // 克隆响应，因为响应流只能使用一次
            const responseToCache = networkResponse.clone()

            // 将响应存储到缓存中
            caches.open(CACHE_NAME).then(cache => {
              cache
                .put(event.request, responseToCache)
                .then(() => {
                  manageCacheSize(cache) // 管理缓存大小
                })
                .catch(error => {
                  console.error('Cache storage failed:', error) // 添加错误处理
                })
            })

            return networkResponse
          })
          .catch(error => {
            console.error('Fetch failed:', error) // 添加错误处理
            return new Response('Network request failed', { status: 500 }) // 返回一个错误响应
          })
      })
    )
  } else {
    // console.log('Non-tianditu request:', event.request.url) // 添加调试信息
    return fetch(event.request) // 对非 tianditu 请求直接进行网络请求
  }
})
