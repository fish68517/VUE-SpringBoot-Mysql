import { getCurrentInstance } from 'vue'

/**
 * Composable hook for using http client in Vue components
 * Usage: const http = useHttp()
 */
export function useHttp() {
  const instance = getCurrentInstance()
  return instance?.proxy?.$http
}
