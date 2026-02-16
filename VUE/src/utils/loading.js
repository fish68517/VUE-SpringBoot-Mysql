import { ElLoading } from 'element-plus'

let loadingInstance = null

/**
 * Show full-screen loading
 */
export function showLoading(text = 'Loading...') {
  if (window.$loading) {
    window.$loading.show(text)
  } else if (!loadingInstance) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: text,
      background: 'rgba(0, 0, 0, 0.7)'
    })
  }
}

/**
 * Hide full-screen loading
 */
export function hideLoading() {
  if (window.$loading) {
    window.$loading.hide()
  } else if (loadingInstance) {
    loadingInstance.close()
    loadingInstance = null
  }
}

/**
 * Execute async function with loading
 */
export async function withLoading(asyncFn, loadingText = 'Loading...') {
  showLoading(loadingText)
  try {
    return await asyncFn()
  } finally {
    hideLoading()
  }
}
