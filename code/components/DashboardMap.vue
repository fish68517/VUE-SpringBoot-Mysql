<template>
  <view :class="['dashboard-map', { 'compact-map': config.compact }]">
    <!-- @vue-ignore renderjs geo is injected by the uni-app view-layer compiler. -->
    <view class="amap-surface" :prop="payload" :change:prop="geo.update" />
    <view v-if="status !== 'ready'" :class="['amap-message', { failed: status === 'error' }]">
      <text>{{ message }}</text>
      <button v-if="status === 'error'" class="button dark compact" @click="retry++">重新加载地图</button>
    </view>
    <text v-if="warning && status === 'ready'" class="amap-warning">{{ warning }}</text>
  </view>
</template>
<script lang="ts">
import { defineComponent } from 'vue'
import { amapCredentials } from '../config/amap'
export default defineComponent({
  props: { config: { type: Object, required: true }, active: { type: Boolean, default: true } },
  emits: ['select'],
  data: () => ({ status: 'loading', message: '正在加载高德地图…', warning: '', retry: 0 }),
  computed: {
    payload() {
      return { ...this.config, active: this.active, credentials: amapCredentials, retry: this.retry }
    },
  },
  methods: {
    onSelect(event: { type: string; id: string }) {
      this.$emit('select', event)
    },
    onStatus(event: { status: string; message?: string; warning?: string }) {
      this.status = event.status
      this.message = event.message || ''
      this.warning = event.warning || ''
    },
  },
})
</script>
<script module="geo" lang="renderjs">
import { loadAMap, regionCenters } from '../platform/amap-loader.js'
import { renderHost } from '../platform/render-host.js'
export default {
  mounted() { this.mountedReady = true; if (this.current) this.update(this.current) },
  methods: {
    update(value, oldValue, owner, instance) {
      if (!value) return
      this.current = value
      this.host = renderHost(this, owner, instance, '.amap-surface')
      if (!this.mountedReady) return
      if (!value.active) { this.release(); return }
      if (this.lastRetry !== value.retry) { this.release(); this.lastRetry = value.retry }
      this.alive = true
      if (!this.host) { this.reportMapStatus('error', '地图容器未就绪，请重新加载地图。'); return }
      if (this.map) { this.draw(); return }
      if (this.pending) return
      this.pending = true
      const generation = this.generation
      this.reportMapStatus('loading', '正在加载高德地图…')
      loadAMap(value.credentials).then(async (AMap) => {
        if (!this.alive || generation !== this.generation) return
        if (!this.host.isConnected) { this.reportMapStatus('error', '地图容器已离开页面，请重新加载地图。'); return }
        this.surface = this.host
        // AMap requires a real HTML div; uni-app's view renders as uni-view on H5.
        this.container = document.createElement('div')
        this.container.style.cssText = 'width:100%;height:100%'
        this.surface.appendChild(this.container)
        this.lastTheme = this.current.theme
        this.map = new AMap.Map(this.container, {
          center: [113.65, 34.80], zoom: 11.8, viewMode: '2D',
          mapStyle: this.lastTheme === 'light' ? 'amap://styles/normal' : 'amap://styles/darkblue', resizeEnable: true,
          showLabel: true, features: ['bg', 'road', 'building', 'point'],
        })
        this.completeTimer = setTimeout(() => {
          if (this.alive && generation === this.generation) this.reportMapStatus('error', '地图响应超时，请检查网络或 Key 授权后重新加载。')
        }, 20000)
        this.map.on('complete', () => { clearTimeout(this.completeTimer); this.mapReady = true; this.reportMapStatus('ready') })
        this.resizeMap = () => { if (this.map) this.map.resize() }
        window.addEventListener('resize', this.resizeMap)
        if (window.ResizeObserver) {
          this.observer = new ResizeObserver(this.resizeMap)
          this.observer.observe(this.surface)
        }
        if (window.MutationObserver) {
          this.removalObserver = new MutationObserver(() => { if (!this.host.isConnected) this.release() })
          this.removalObserver.observe(document.body, { childList: true, subtree: true })
        }
        this.draw()
        const centers = await regionCenters(AMap)
        if (!this.alive || generation !== this.generation) return
        this.centers = centers
        this.draw()
      }).catch((error) => {
        if (!this.alive || generation !== this.generation) return
        this.reportMapStatus('error', error.message === 'CONFIG_MISSING'
          ? '未配置高德地图 Key，请检查本地地图配置。'
          : '高德地图加载失败，请检查网络或 Key 授权后重新加载。')
      }).finally(() => { if (generation === this.generation) this.pending = false })
    },
    reportMapStatus(status, message = '') {
      if (this.alive) this.$ownerInstance.callMethod('onStatus', { status, message, warning: this.warning || '' })
    },
    draw() {
      if (!this.map || !this.alive) return
      const value = this.current
      if (this.lastTheme !== value.theme) {
        // Configure style at creation: a pending setMapStyle request in JS API 2.0
        // can call a destroyed renderer when the user immediately leaves the view.
        this.release()
        this.update(value)
        return
      }
      if (this.lastReset !== value.reset) {
        this.lastReset = value.reset
        this.map.setZoomAndCenter(11.8, [113.65, 34.80])
        this.focusedRegions = ''
        this.lastZoomStep = value.zoomStep || 0
      }
      if (value.zoomStep !== undefined && this.lastZoomStep !== value.zoomStep) {
        this.map.setZoom(Math.max(3, Math.min(18, this.map.getZoom() + value.zoomStep - (this.lastZoomStep || 0))))
        this.lastZoomStep = value.zoomStep
      }
      if (this.markers) this.map.remove(this.markers)
      this.markers = []
      if (!this.centers) return
      const regionKey = (value.regions || []).map(r => r.id).join(',')
      if (value.compact && this.focusedRegions !== regionKey) {
        this.focusedRegions = regionKey
        const single = value.regions.length === 1 ? this.centers[value.regions[0].name] : null
        this.map.setZoomAndCenter(single ? 12 : 10.5, single || [113.65, 34.80])
      }
      for (const region of value.regions || []) {
        const center = this.centers[region.name]
        if (!center) continue
        const button = document.createElement('button')
        button.type = 'button'
        button.className = 'water-region-marker'
        button.dataset.regionId = region.id
        button.style.cssText = 'border:1px solid #408faf;border-radius:8px;background:#082c43ee;color:#d9f7ff;padding:9px 13px;cursor:pointer;font:12px sans-serif;line-height:21px;text-align:left;white-space:nowrap;box-shadow:0 3px 15px #03121e66'
        if (value.compact) button.style.cssText += ';padding:6px 8px;min-height:44px;font-size:11px'
        if (value.selectedRegion === region.id) button.style.borderColor = '#ffc24b'
        const title = document.createElement('strong')
        title.style.cssText = 'display:block;color:#6adced;font-size:13px'
        title.textContent = region.name + (value.compact ? '' : ' · 区域汇总')
        const detail = document.createElement('div')
        detail.textContent = region.facilityCount + ' 处设施 / ' + region.alarmCount + ' 条告警'
        button.appendChild(title)
        if (!value.compact || value.regions.length === 1) button.appendChild(detail)
        const anchor = value.compact && value.regions.length === 1 ? 'center' : { 'REG-001': 'bottom-right', 'REG-002': 'top-center', 'REG-003': 'top-left' }[region.id] || 'bottom-center'
        const marker = new window.AMap.Marker({ position: center, content: button, anchor, zIndex: 120 })
        marker.on('click', () => this.$ownerInstance.callMethod('onSelect', { type: 'region', id: region.id }))
        this.markers.push(marker)
      }
      this.map.add(this.markers)
      this.warning = this.markers.length < (value.regions || []).length ? '部分区域汇总定位暂不可用，可从设施列表联查。' : ''
      // A partial search failure does not replace a successfully loaded base map.
      if (this.mapReady) this.reportMapStatus('ready')
    },
    release() {
      this.alive = false
      this.generation = (this.generation || 0) + 1
      this.pending = false
      clearTimeout(this.completeTimer)
      if (this.observer) this.observer.disconnect()
      if (this.resizeMap) window.removeEventListener('resize', this.resizeMap)
      if (this.removalObserver) this.removalObserver.disconnect()
      if (this.map) { this.map.destroy(); this.map = null }
      if (this.container) { this.container.remove(); this.container = null }
      this.markers = null
      this.mapReady = false
      this.centers = null
      this.warning = ''
      this.lastReset = undefined
      this.lastTheme = undefined
    },
  },
}
</script>
<style>
.dashboard-map {
  position: absolute;
  inset: 0;
  background: #0b263b;
  z-index: 0;
  overflow: hidden;
}
.compact-map .amap-message {
  min-width: 0;
  width: calc(100% - 40px);
  box-sizing: border-box;
  font-size: 13px;
}
.compact-map .amap-warning {
  left: 8px;
  right: 8px;
  bottom: 54px;
  font-size: 11px;
}
.amap-surface {
  width: 100%;
  height: 100%;
}
.amap-message {
  position: absolute;
  z-index: 180;
  top: 42%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 20px;
  border-radius: 8px;
  background: #0b2c46ee;
  color: #d9eff9;
  text-align: center;
  min-width: 260px;
}
.amap-message text {
  display: block;
  margin-bottom: 12px;
}
.amap-message.failed {
  border: 1px solid #d3913f;
}
.amap-warning {
  position: absolute;
  left: 160px;
  bottom: 5px;
  color: #edce8d;
  font-size: 11px;
  background: #082c43df;
  padding: 4px 8px;
}
</style>
