/*
 * @Author: kaix
 * @Date: 2024-07-26 09:52:32
 * @LastEditTime: 2024-07-26 09:58:41
 * @LastEditors: kaix
 * @Description:
 */
import { ref, onMounted } from 'vue'
// 动态加载字体
const loadTextFonts = (emit, fonts) => {
  onMounted(() => {
    emit('loadFonts', fonts)
  })
}
export const useFonts = () => {
  return {
    loadTextFonts
  }
}
