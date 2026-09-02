<template>
<!--  <CMapVue-->
<!--    :data="{-->
<!--      data: {-->
<!--        矢量地图: true, // 矢量地图-->
<!--        遥感影像: true, // 遥感影像-->
<!--        电子影像: true, // 电子影像-->
<!--        三维实景: true // 三维实景-->
<!--      }-->
<!--    }"-->
<!--    @loaded="mapLoaded"-->
<!--    :chartConfig="{-->
<!--      ...chartConfig,-->
<!--      option: {-->
<!--        layer: {-->
<!--          矢量地图: true, // 矢量地图-->
<!--          遥感影像: true, // 遥感影像-->
<!--          电子影像: true, // 电子影像-->
<!--          三维实景: true // 三维实景-->
<!--        },-->
<!--        areaConfig: [],-->
<!--        state: {-->
<!--          scope: {-->
<!--            // 默认行政区编码-->
<!--            adcode: '500102',-->
<!--            // 矩形范围-->
<!--            bbox: [105.06, 36.06, 105.06, 36.06],-->
<!--            // 中心点-->
<!--            center: [106.56, 29.56],-->
<!--            // 缩放-->
<!--            // zoom: 10,-->
<!--            // 俯仰角-->
<!--            pitch: 0,-->
<!--            // 倾斜角-->
<!--            heading: 0,-->
<!--            // 高度-->
<!--            height: 6700,-->
<!--            // 最大高度-->
<!--            maxHeight: 1000000,-->
<!--            // 最小高度-->
<!--            minHeight: 1000,-->
<!--            // 底图-->
<!--            tile: '矢量地图',-->
<!--            cesiumMapToken: '32697850912581',-->
<!--            layerBg: false,-->
<!--            mapTypeShow: true,-->
<!--            control: true-->
<!--          }-->
<!--        }-->
<!--      }-->
<!--    }"-->
<!--  >-->
<!--  </CMapVue>-->

  <div class="button" @click="isClick = !isClick">
    <span>{{ isClick ? '关闭客流热力图' : '启动客流热力图' }}</span>
  </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, reactive, onMounted, onUnmounted, nextTick, provide, watch } from 'vue'
// import { CMapVue } from '@/package/components/cmap/index'
import type { GlobalParams, EventBus } from '@/package/index.d'
import axios from 'axios'

const sourceName = 'BzPeoHeatMap'

const addValue = ref('洪崖洞')

const isClick = ref(false)
const showClick = () => {
  isClick.value = true
}

const options = ref([
  {
    value: '解放碑',
    label: '解放碑'
  },
  {
    value: '洪崖洞',
    label: '洪崖洞'
  }
])
// 3. 选择变化事件（可选）
const handleSelectChange = () => {
  // console.log('选中的值：', addValue.value)
  // 可在此处处理表单提交、数据请求等逻辑
}

const isDevelopment = process.env.NODE_ENV === 'development'
const mapReady = ref(false)
const heatMapContainer = ref(null)

const cesiumContainer = ref(null)
let heatmapController = null

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  // 全局参数
  globalParams: {
    type: Object as () => GlobalParams,
    required: false,
    default: () => ({
      params: reactive({
        yzt_tab: '',
        yzt_block: { class1: '', class2: '', class3: '' },
        yzt_map_ready: true,
        yzt_activeDispatch: {},
        heatMapAddress: ''
      })
    })
  },
  bus: {
    type: Object as () => EventBus,
    required: false
  }
})

// 回调事件对象
const eventListeners = {
  // 地图加载完成
  'CMap:yzt_map_ready': () => {
    mapReady.value = true
    nextTick(() => {
      // getLineData()
      console.log('🚀 BzPeoHeatMap ready is ', mapReady.value)
      // dataMappingRef.value?.setEnabledContextMenu(contextMenuKeys.value)
      // 获取全局配置的固定围栏
      // geoFences.value = props.globalParams.params.areaConfig.map(item => JSON.stringify(item.data))
    })
  }
}
window.eventListeners = eventListeners

const mapLoaded = () => {
  mapReady.value = true
  viewer.scene.debugShowFramesPerSecond = isDevelopment
}

const points = new Array(300).fill('').map(() => ({
  lnglat: [
    106.56 + Math.random() * 0.1 * (Math.random() > 0.5 ? 1 : -1),
    29.56 + Math.random() * 0.1 * (Math.random() > 0.5 ? 1 : -1)
  ],
  value: 1000 * Math.random()
}))
// 事件总线定义，方便组件间通信，本地暂时不能触发，只能上传测试环境测试,注意emit中的source是组件名称，方便区分事件来源
const eventBus = {
  on: (event: string, callback: (data: any) => void) => {
    props.bus?.on(event, ({ source, data }) => {
      console.log('🚀 ~ eventBus on:', event, source, data)
      if (source === sourceName) return
      callback(data)
    })
  },
  off: (event: string, callback: (eventData?: any) => void) => {
    props.bus?.off(event, callback)
  },
  emit: (event: string, data?: any) => {
    console.log('🚀 ~ eventBus emit:', event, data)
    props.bus?.emit(event, { data, source: sourceName })
  }
}

onMounted(() => {
  Object.keys(eventListeners).forEach(key => {
    eventBus.on(key, eventListeners[key])
  })
  // 监听 BZDangerArea 组件的地点点击事件，仅在热力图启用时处理
  eventBus.on('BZDANGER_AREA_CLICK', (address) => {
    console.log('🚀 BzPeoHeatMap 收到地点点击:', address)
    if (!isClick.value) {
      console.log('热力图未启用，忽略事件')
      return
    }
    // 销毁旧热力图，用新地点重新初始化
    heatmapController?.destroy?.()
    heatmapController = null
    initHeatMap(address)
  })
})
onUnmounted(() => {
  heatmapController?.destroy?.()
  heatmapController = null
})

watch(
  () => isClick.value,
  newVal => {
    const viewer = window.viewer
    console.log('进入步骤3---------------------')

    if (newVal) {
      let heatMapAddress = props.chartConfig.option.heatMapAddress
      // heatMapAddress = '解放碑'
      initHeatMap(heatMapAddress)
    } else {
      heatmapController?.destroy?.()
      heatmapController = null
    }
  }
)

//计算中位数
function calculateMedian(dataPoints) {
  if (!dataPoints || dataPoints.length === 0) return 0

  // 提取所有的 value，并进行排序
  const sortedValues = dataPoints.map(p => parseFloat(p.value)).sort((a, b) => a - b)

  // 如果数据点的个数为偶数，取中间两个数的平均值
  const middle = Math.floor(sortedValues.length / 2)
  if (sortedValues.length % 2 === 0) {
    return (sortedValues[middle - 1] + sortedValues[middle]) / 2
  } else {
    return sortedValues[middle]
  }
}

function calculateQuantile(points, q = 0.9) {
  if (!points || !points.length) return 1

  const values = points
      .map(p => p.value || 0)
      .sort((a, b) => a - b)

  const index = Math.floor(values.length * q)
  return values[index] || values[values.length - 1]
}


///////////////////////////////////////////////////////////////////
/////////////////////////开始新的代码////////////////////////////////
///////////////////////////////////////////////////////////////////

async function initHeatMap(heatMapAddress) {
    viewer = window.viewer

    viewer._cesiumWidget._creditContainer.style.display = 'none'
    const points = await axios
        .post(`http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/heatMap?name=${heatMapAddress}`)
        .then(res => {
          if (res.data.code !== '000000') return
          return res.data.data
        })
        .catch(err => {
          console.log(err, '接口错误')
        })
    // const points = new Array(30000).fill('').map(() => ({
    //   lnglat: [
    //     106.56 + Math.random() * 0.1 * (Math.random() > 0.5 ? 1 : -1),
    //     29.56 + Math.random() * 0.1 * (Math.random() > 0.5 ? 1 : -1)
    //   ],
    //   value: 1000 * Math.random()
    // }))
    console.log(points)
    if (heatmapController) {
      heatmapController.update(points)
    } else {
      heatmapController = create2DHeatmap(viewer, {
        dataPoints: points,
        average: calculateQuantile(points, 0.99),
        zindex: 999,
        radius: 50,
        gridDegree: 0.002,
        colorGradient: {
          0.25: 'blue',
          0.55: 'green',
          0.85: 'yellow',
          1.0: 'red'
        }
      })
    }
  }

  var HeatmapConfig = {
    defaultRadius: 40,
    defaultRenderer: 'canvas2d',
    defaultGradient: {
      0.25: 'rgb(0,0,255)',
      0.55: 'rgb(0,255,0)',
      0.85: 'yellow',
      1.0: 'rgb(255,0,0)'
    },
    defaultMaxOpacity: 1,
    defaultMinOpacity: 0,
    defaultBlur: 0.85,
    defaultXField: 'x',
    defaultYField: 'y',
    defaultValueField: 'value',
    plugins: {}
  }
  var Store = (function StoreClosure() {
    var Store = function Store(config) {
      this._coordinator = {}
      this._data = []
      this._radi = []
      this._min = 0
      this._max = 1
      this._xField = config['xField'] || config.defaultXField
      this._yField = config['yField'] || config.defaultYField
      this._valueField = config['valueField'] || config.defaultValueField

      if (config['radius']) {
        this._cfgRadius = config['radius']
      }
    }

    var defaultRadius = HeatmapConfig.defaultRadius

    Store.prototype = {
      // when forceRender = false -> called from setData, omits renderall event
      _organiseData: function (dataPoint, forceRender) {
        var x = dataPoint[this._xField]
        var y = dataPoint[this._yField]
        var radi = this._radi
        var store = this._data
        var max = this._max
        var min = this._min
        var value = dataPoint[this._valueField] || 1
        var radius = dataPoint.radius || this._cfgRadius || defaultRadius

        if (!store[x]) {
          store[x] = []
          radi[x] = []
        }

        if (!store[x][y]) {
          store[x][y] = value
          radi[x][y] = radius
        } else {
          store[x][y] += value
        }

        if (store[x][y] > max) {
          if (!forceRender) {
            this._max = store[x][y]
          } else {
            this.setDataMax(store[x][y])
          }
          return false
        } else {
          return {
            x: x,
            y: y,
            value: value,
            radius: radius,
            min: min,
            max: max
          }
        }
      },
      _unOrganizeData: function () {
        var unorganizedData = []
        var data = this._data
        var radi = this._radi

        for (var x in data) {
          for (var y in data[x]) {
            unorganizedData.push({
              x: x,
              y: y,
              radius: radi[x][y],
              value: data[x][y]
            })
          }
        }
        return {
          min: this._min,
          max: this._max,
          data: unorganizedData
        }
      },
      _onExtremaChange: function () {
        this._coordinator.emit('extremachange', {
          min: this._min,
          max: this._max
        })
      },
      addData: function () {
        if (arguments[0].length > 0) {
          var dataArr = arguments[0]
          var dataLen = dataArr.length
          while (dataLen--) {
            this.addData.call(this, dataArr[dataLen])
          }
        } else {
          // add to store
          var organisedEntry = this._organiseData(arguments[0], true)
          if (organisedEntry) {
            this._coordinator.emit('renderpartial', {
              min: this._min,
              max: this._max,
              data: [organisedEntry]
            })
          }
        }
        return this
      },
      setData: function (data) {
        var dataPoints = data.data
        var pointsLen = dataPoints.length

        // reset data arrays
        this._data = []
        this._radi = []

        for (var i = 0; i < pointsLen; i++) {
          this._organiseData(dataPoints[i], false)
        }
        this._max = data.max
        this._min = data.min || 0

        this._onExtremaChange()
        this._coordinator.emit('renderall', this._getInternalData())
        return this
      },
      removeData: function () {
        // TODO: implement
      },
      setDataMax: function (max) {
        this._max = max
        this._onExtremaChange()
        this._coordinator.emit('renderall', this._getInternalData())
        return this
      },
      setDataMin: function (min) {
        this._min = min
        this._onExtremaChange()
        this._coordinator.emit('renderall', this._getInternalData())
        return this
      },
      setCoordinator: function (coordinator) {
        this._coordinator = coordinator
      },
      _getInternalData: function () {
        return {
          max: this._max,
          min: this._min,
          data: this._data,
          radi: this._radi
        }
      },
      getData: function () {
        return this._unOrganizeData()
      }
    }

    return Store
  })()

  var Canvas2dRenderer = (function Canvas2dRendererClosure() {
    var _getColorPalette = function (config) {
      var gradientConfig = config.gradient || config.defaultGradient
      var paletteCanvas = document.createElement('canvas')
      var paletteCtx = paletteCanvas.getContext('2d')

      paletteCanvas.width = 256
      paletteCanvas.height = 1

      var gradient = paletteCtx.createLinearGradient(0, 0, 256, 1)
      for (var key in gradientConfig) {
        gradient.addColorStop(key, gradientConfig[key])
      }

      paletteCtx.fillStyle = gradient
      paletteCtx.fillRect(0, 0, 256, 1)

      return paletteCtx.getImageData(0, 0, 256, 1).data
    }

    var _getPointTemplate = function (radius, blurFactor) {
      var tplCanvas = document.createElement('canvas')
      var tplCtx = tplCanvas.getContext('2d')
      var x = radius
      var y = radius
      tplCanvas.width = tplCanvas.height = radius * 2

      if (blurFactor == 1) {
        tplCtx.beginPath()
        tplCtx.arc(x, y, radius, 0, 2 * Math.PI, false)
        tplCtx.fillStyle = 'rgba(0,0,0,1)'
        tplCtx.fill()
      } else {
        var gradient = tplCtx.createRadialGradient(x, y, radius * blurFactor, x, y, radius)
        gradient.addColorStop(0, 'rgba(0,0,0,1)')
        gradient.addColorStop(1, 'rgba(0,0,0,0)')
        tplCtx.fillStyle = gradient
        tplCtx.fillRect(0, 0, 2 * radius, 2 * radius)
      }

      return tplCanvas
    }

    var _prepareData = function (data) {
      var renderData = []
      var min = data.min
      var max = data.max
      var radi = data.radi
      var data = data.data

      var xValues = Object.keys(data)
      var xValuesLen = xValues.length

      while (xValuesLen--) {
        var xValue = xValues[xValuesLen]
        var yValues = Object.keys(data[xValue])
        var yValuesLen = yValues.length
        while (yValuesLen--) {
          var yValue = yValues[yValuesLen]
          var value = data[xValue][yValue]
          var radius = radi[xValue][yValue]
          renderData.push({
            x: xValue,
            y: yValue,
            value: value,
            radius: radius
          })
        }
      }

      return {
        min: min,
        max: max,
        data: renderData
      }
    }

    function Canvas2dRenderer(config) {
      var container = config.container
      var shadowCanvas = (this.shadowCanvas = document.createElement('canvas'))
      var canvas = (this.canvas = config.canvas || document.createElement('canvas'))
      var renderBoundaries = (this._renderBoundaries = [10000, 10000, 0, 0])

      var computed = getComputedStyle(config.container) || {}

      canvas.className = 'heatmap-canvas'

      this._width = canvas.width = shadowCanvas.width = +computed.width.replace(/px/, '')
      this._height = canvas.height = shadowCanvas.height = +computed.height.replace(/px/, '')

      this.shadowCtx = shadowCanvas.getContext('2d')
      this.ctx = canvas.getContext('2d')

      // @TODO:
      // conditional wrapper

      canvas.style.cssText = shadowCanvas.style.cssText = 'position:absolute;left:0;top:0;'

      container.style.position = 'absolute'
      container.appendChild(canvas)

      this._palette = _getColorPalette(config)
      this._templates = {}

      this._setStyles(config)
    }

    Canvas2dRenderer.prototype = {
      renderPartial: function (data) {
        this._drawAlpha(data)
        this._colorize()
      },
      renderAll: function (data) {
        // reset render boundaries
        this._clear()
        this._drawAlpha(_prepareData(data))
        this._colorize()
      },
      _updateGradient: function (config) {
        this._palette = _getColorPalette(config)
      },
      updateConfig: function (config) {
        if (config['gradient']) {
          this._updateGradient(config)
        }
        this._setStyles(config)
      },
      setDimensions: function (width, height) {
        this._width = width
        this._height = height
        this.canvas.width = this.shadowCanvas.width = width
        this.canvas.height = this.shadowCanvas.height = height
      },
      _clear: function () {
        this.shadowCtx.clearRect(0, 0, this._width, this._height)
        this.ctx.clearRect(0, 0, this._width, this._height)
      },
      _setStyles: function (config) {
        this._blur = config.blur == 0 ? 0 : config.blur || config.defaultBlur

        if (config.backgroundColor) {
          this.canvas.style.backgroundColor = config.backgroundColor
        }

        this._opacity = (config.opacity || 0) * 255
        this._maxOpacity = (config.maxOpacity || config.defaultMaxOpacity) * 255
        this._minOpacity = (config.minOpacity || config.defaultMinOpacity) * 255
        this._useGradientOpacity = !!config.useGradientOpacity
      },
      _drawAlpha: function (data) {
        var min = (this._min = data.min)
        var max = (this._max = data.max)
        var data = data.data || []
        var dataLen = data.length
        // on a point basis?
        var blur = 1 - this._blur

        while (dataLen--) {
          var point = data[dataLen]

          var x = point.x
          var y = point.y
          var radius = point.radius
          // if value is bigger than max
          // use max as value
          var value = Math.min(point.value, max)
          var rectX = x - radius
          var rectY = y - radius
          var shadowCtx = this.shadowCtx

          var tpl
          if (!this._templates[radius]) {
            this._templates[radius] = tpl = _getPointTemplate(radius, blur)
          } else {
            tpl = this._templates[radius]
          }
          // value from minimum / value range
          // => [0, 1]
          shadowCtx.globalAlpha = (value - min) / (max - min)

          shadowCtx.drawImage(tpl, rectX, rectY)

          // update renderBoundaries
          if (rectX < this._renderBoundaries[0]) {
            this._renderBoundaries[0] = rectX
          }
          if (rectY < this._renderBoundaries[1]) {
            this._renderBoundaries[1] = rectY
          }
          if (rectX + 2 * radius > this._renderBoundaries[2]) {
            this._renderBoundaries[2] = rectX + 2 * radius
          }
          if (rectY + 2 * radius > this._renderBoundaries[3]) {
            this._renderBoundaries[3] = rectY + 2 * radius
          }
        }
      },
      _colorize: function () {
        var x = this._renderBoundaries[0]
        var y = this._renderBoundaries[1]
        var width = this._renderBoundaries[2] - x
        var height = this._renderBoundaries[3] - y
        var maxWidth = this._width
        var maxHeight = this._height
        var opacity = this._opacity
        var maxOpacity = this._maxOpacity
        var minOpacity = this._minOpacity
        var useGradientOpacity = this._useGradientOpacity

        if (x < 0) {
          x = 0
        }
        if (y < 0) {
          y = 0
        }
        if (x + width > maxWidth) {
          width = maxWidth - x
        }
        if (y + height > maxHeight) {
          height = maxHeight - y
        }

        var img = this.shadowCtx.getImageData(x, y, width, height)
        var imgData = img.data
        var len = imgData.length
        var palette = this._palette

        for (var i = 3; i < len; i += 4) {
          var alpha = imgData[i]
          var offset = alpha * 4

          if (!offset) {
            continue
          }

          var finalAlpha
          if (opacity > 0) {
            finalAlpha = opacity
          } else {
            if (alpha < maxOpacity) {
              if (alpha < minOpacity) {
                finalAlpha = minOpacity
              } else {
                finalAlpha = alpha
              }
            } else {
              finalAlpha = maxOpacity
            }
          }

          imgData[i - 3] = palette[offset]
          imgData[i - 2] = palette[offset + 1]
          imgData[i - 1] = palette[offset + 2]
          imgData[i] = useGradientOpacity ? palette[offset + 3] : finalAlpha
        }
        // Object.defineProperty(img, 'data', {
        //   value: imgData,
        //   writable: true,
        //   configurable: true,
        //   enumerable: true
        // })
        // img.data = imgData;
        this.ctx.putImageData(img, x, y)

        this._renderBoundaries = [1000, 1000, 0, 0]
      },
      getValueAt: function (point) {
        var value
        var shadowCtx = this.shadowCtx
        var img = shadowCtx.getImageData(point.x, point.y, 1, 1)
        var data = img.data[3]
        var max = this._max
        var min = this._min

        value = (Math.abs(max - min) * (data / 255)) >> 0

        return value
      },
      getDataURL: function () {
        return this.canvas.toDataURL()
      }
    }

    return Canvas2dRenderer
  })()

  const Renderer = (function RendererClosure() {
    var rendererFn = false

    if (HeatmapConfig['defaultRenderer'] === 'canvas2d') {
      rendererFn = Canvas2dRenderer
    }

    return rendererFn
  })()

  const Util = {
    merge: function () {
      var merged = {}
      var argsLen = arguments.length
      for (var i = 0; i < argsLen; i++) {
        var obj = arguments[i]
        for (var key in obj) {
          merged[key] = obj[key]
        }
      }
      return merged
    }
  }
// Heatmap Constructor
  const Heatmap = (function HeatmapClosure() {
    var Coordinator = (function CoordinatorClosure() {
      function Coordinator() {
        this.cStore = {}
      }

      Coordinator.prototype = {
        on: function (evtName, callback, scope) {
          var cStore = this.cStore

          if (!cStore[evtName]) {
            cStore[evtName] = []
          }
          cStore[evtName].push(function (data) {
            return callback.call(scope, data)
          })
        },
        emit: function (evtName, data) {
          var cStore = this.cStore
          if (cStore[evtName]) {
            var len = cStore[evtName].length
            for (var i = 0; i < len; i++) {
              var callback = cStore[evtName][i]
              callback(data)
            }
          }
        }
      }

      return Coordinator
    })()

    var _connect = function (scope) {
      var renderer = scope._renderer
      var coordinator = scope._coordinator
      var store = scope._store

      coordinator.on('renderpartial', renderer.renderPartial, renderer)
      coordinator.on('renderall', renderer.renderAll, renderer)
      coordinator.on('extremachange', function (data) {
        scope._config.onExtremaChange &&
        scope._config.onExtremaChange({
          min: data.min,
          max: data.max,
          gradient: scope._config['gradient'] || scope._config['defaultGradient']
        })
      })
      store.setCoordinator(coordinator)
    }

    function Heatmap() {
      var config = (this._config = Util.merge(HeatmapConfig, arguments[0] || {}))
      this._coordinator = new Coordinator()
      if (config['plugin']) {
        var pluginToLoad = config['plugin']
        if (!HeatmapConfig.plugins[pluginToLoad]) {
          throw new Error("Plugin '" + pluginToLoad + "' not found. Maybe it was not registered.")
        } else {
          var plugin = HeatmapConfig.plugins[pluginToLoad]
          // set plugin renderer and store
          this._renderer = new plugin.renderer(config)
          this._store = new plugin.store(config)
        }
      } else {
        this._renderer = new Renderer(config)
        this._store = new Store(config)
      }
      _connect(this)
    }

    // @TODO:
    // add API documentation
    Heatmap.prototype = {
      addData: function () {
        this._store.addData.apply(this._store, arguments)
        return this
      },
      removeData: function () {
        this._store.removeData && this._store.removeData.apply(this._store, arguments)
        return this
      },
      setData: function () {
        this._store.setData.apply(this._store, arguments)
        return this
      },
      setDataMax: function () {
        this._store.setDataMax.apply(this._store, arguments)
        return this
      },
      setDataMin: function () {
        this._store.setDataMin.apply(this._store, arguments)
        return this
      },
      configure: function (config) {
        this._config = Util.merge(this._config, config)
        this._renderer.updateConfig(this._config)
        this._coordinator.emit('renderall', this._store._getInternalData())
        return this
      },
      repaint: function () {
        this._coordinator.emit('renderall', this._store._getInternalData())
        return this
      },
      getData: function () {
        return this._store.getData()
      },
      getDataURL: function () {
        return this._renderer.getDataURL()
      },
      getValueAt: function (point) {
        if (this._store.getValueAt) {
          return this._store.getValueAt(point)
        } else if (this._renderer.getValueAt) {
          return this._renderer.getValueAt(point)
        } else {
          return null
        }
      }
    }

    return Heatmap
  })()

  const h337 = {
    create: function (config) {
      return new Heatmap(config)
    },
    register: function (pluginKey, plugin) {
      HeatmapConfig.plugins[pluginKey] = plugin
    }
  }


  /**
   * 创建二维热力图2
   * @param {Cesium.Viewer} viewer 地图viewer对象
   * @param {Object} options 基础参数
   * @param {Array} options.dataPoints 热力值数组
   * @param {Array} options.radius 热力点半径
   * @param {Array} options.colorGradient 颜色配置
   */
  function create2DHeatmap(viewer, options = {}) {
    const heatmapState = {
      viewer,
      dataPoints: options.dataPoints || [],
      average: options.average || 100,
      containerElement: null,
      heatmapInstance: null,
      instanceId: (Date.now() + Math.random() * 1000) | 0,
      frameCount: 0 // 帧数计数器
    }

    console.log('2222222222',heatmapState)

    if (heatmapState.dataPoints.length < 2) {
      console.warn('热力图点位不得少于 2 个')
      return
    }

    /* 1️⃣ 找到 Cesium canvas */
    const canvas = viewer.scene.canvas

    /* 2️⃣ 创建覆盖容器 */
    const container = document.createElement('div')
    container.id = `heatmap-${heatmapState.instanceId}`
    container.style.position = 'absolute'
    container.style.left = '0'
    container.style.top = '0'
    container.style.width = '100%'
    container.style.height = '100%'
    container.style.pointerEvents = 'none'
    container.style.zIndex = '9999'

    /* 关键：插到 canvas 后面 */
    canvas.parentNode.appendChild(container)
    heatmapState.containerElement = container
    console.log('333333333')

    /* heatmap.js */
    heatmapState.heatmapInstance = h337.create({
      container,
      radius: options.radius || 30,
      maxOpacity: 0.7,
      minOpacity: 0,
      blur: 0.75,
      gradient: options.colorGradient
    })
    console.log('444444444')

    /* 经纬度 → 屏幕坐标 */
    function update() {
      if (heatmapState.frameCount < 10) {
        // 每10帧计算一次
        heatmapState.frameCount++
        return // 跳过更新
      }

      const data = []
      let max = 0

      heatmapState.dataPoints.forEach(p => {
        const cartesian = Cesium.Cartesian3.fromDegrees(p.lnglat[0], p.lnglat[1])
        const screen = Cesium.SceneTransforms.wgs84ToWindowCoordinates(viewer.scene, cartesian)

        if (!screen) return

        const raw = p.value || 1
        const value = Math.log(raw + 1)
        max = Math.max(max, value)

        data.push({
          x: Math.round(screen.x),
          y: Math.round(screen.y),
          value
        })
      })
      console.log('max', max)
      heatmapState.heatmapInstance.setData({ max: heatmapState.average, data })
      // heatmapState.heatmapInstance.setData({ max, data })
      heatmapState.frameCount = 0 // 重置帧计数器

    }

    viewer.scene.postRender.addEventListener(update)
    update()

    return {
      update(points) {
        if (points) heatmapState.dataPoints = points
        update()
      },
      destroy() {
        viewer.scene.postRender.removeEventListener(update)
        container.remove()
      }
    }
}


</script>

<script lang="ts">
export default {
  name: 'BzPeoHeatMap',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.button {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  z-index: 100000000;
  cursor: pointer;
  width: 180px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  /* 按钮背景图 */
  background: url('./img/bg_mid_bottom_data.png') center / 100% 100% no-repeat;
  font-size: 16px;
  font-weight: 700;
  border: none;
  transition: all 0.2s ease;
  border-radius: 6px;
}

.button span {
  /* 文字渐变 */
  background: linear-gradient(180deg, #FFFFFF 0%, #5FBCFF 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
</style>
