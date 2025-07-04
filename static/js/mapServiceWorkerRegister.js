if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker
      .register('./js/map-tile-cache.js')
      .then(registration => {
        console.log('地图Service Worker 注册成功:', registration.scope)
      })
      .catch(error => {
        console.error('Service Worker 注册失败:', error)
      })
  })
}
