import { ref, onMounted, onUnmounted } from 'vue'
import { mobileClient } from '../navigation/clientPolicy'

export function isMobileClient() {
  const info = uni.getSystemInfoSync()
  return mobileClient(info.uniPlatform || '', info.windowWidth)
}

export function useMobileClient() {
  const mobile = ref(isMobileClient())
  const update = () => {
    mobile.value = isMobileClient()
  }
  onMounted(() => uni.onWindowResize(update))
  onUnmounted(() => uni.offWindowResize(update))
  return mobile
}
