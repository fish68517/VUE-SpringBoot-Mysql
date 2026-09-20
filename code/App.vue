<script lang="ts">
import { defineComponent } from 'vue'
import { useDemo } from './stores/demo'
export default defineComponent({
  onHide() {
    useDemo().pause()
  },
})
</script>
<style lang="scss">
@import './styles/app.scss';
/* #ifdef H5 */
/* 禁用框架默认的外部页面阴影预加载，核心演示资源均来自本地。 */
body::after {
  animation: none !important;
  background-image: none !important;
}
/* #endif */
</style>
