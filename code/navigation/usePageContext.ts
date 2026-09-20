import { ref, nextTick } from 'vue'
import { onLoad, onShow, onHide, onUnload, onPageScroll } from '@dcloudio/uni-app'
import { useDemo } from '../stores/demo'
import { routes, urlFor } from './routeMap'
export function usePageContext(mode: string) {
  const demo = useDemo(),
    query = ref<Record<string, string>>({}),
    active = ref(true),
    ready = ref(false)
  const scrollKey = mode + ':scroll'
  let redirecting = false
  function requireLogin() {
    if (!demo.user && !redirecting) {
      redirecting = true
      uni.reLaunch({ url: urlFor('login', { redirect: urlFor(mode, query.value) }) })
    }
  }
  function syncHash() {
    // #ifdef H5
    const [pathname, search = ''] = window.location.hash.slice(1).split('?')
    if (pathname === '/pages/' + routes[mode]) {
      const value = Object.fromEntries(new URLSearchParams(search))
      if (JSON.stringify(query.value) !== JSON.stringify(value)) query.value = value
    }
    // #endif
  }
  onLoad((q: any) => {
    query.value = q || {}
    ready.value = true
    syncHash()
    requireLogin()
  })
  onShow(() => {
    active.value = true
    syncHash()
    if (ready.value) requireLogin()
    nextTick(() => {
      if (demo.filters[scrollKey]) uni.pageScrollTo({ scrollTop: demo.filters[scrollKey], duration: 0 })
    })
  })
  onPageScroll((e) => {
    demo.filters[scrollKey] = e.scrollTop
  })
  onHide(() => {
    active.value = false
    demo.pause()
  })
  // #ifdef H5
  window.addEventListener('hashchange', syncHash)
  // #endif
  onUnload(() => {
    active.value = false
    demo.pause()
    // #ifdef H5
    window.removeEventListener('hashchange', syncHash)
    // #endif
  })
  return { demo, query, active, ready }
}
