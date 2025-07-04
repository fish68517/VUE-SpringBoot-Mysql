;(function webpackUniversalModuleDefinition(root, factory) {
  if (typeof exports === 'object' && typeof module === 'object') module.exports = factory()
  else if (typeof define === 'function' && define.amd) define([], factory)
  else {
    var a = factory()
    for (var i in a) (typeof exports === 'object' ? exports : root)[i] = a[i]
  }
})(self, function() {
  return /******/ (() => {
    // webpackBootstrap
    /******/ 'use strict' // The require scope
    /******/ /******/ var __webpack_require__ = {} /* webpack/runtime/define property getters */ /******/
    /******/

    /************************************************************************/
    /******/ ;(() => {
      /******/ // define getter functions for harmony exports
      /******/ __webpack_require__.d = (exports, definition) => {
        /******/ for (var key in definition) {
          /******/ if (
            __webpack_require__.o(definition, key) &&
            !__webpack_require__.o(exports, key)
          ) {
            /******/ Object.defineProperty(exports, key, { enumerable: true, get: definition[key] })
            /******/
          }
          /******/
        }
        /******/
      }
      /******/
    })() /* webpack/runtime/hasOwnProperty shorthand */ /******/
    /******/

    /******/
    ;(() => {
      /******/ __webpack_require__.o = (obj, prop) =>
        Object.prototype.hasOwnProperty.call(obj, prop)
      /******/
    })() /* webpack/runtime/make namespace object */ /******/
    /******/

    /******/
    ;(() => {
      /******/ // define __esModule on exports
      /******/ __webpack_require__.r = exports => {
        /******/ if (typeof Symbol !== 'undefined' && Symbol.toStringTag) {
          /******/ Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' })
          /******/
        }
        /******/ Object.defineProperty(exports, '__esModule', { value: true })
        /******/
      }
      /******/
    })()
    /******/

    /************************************************************************/
    var __webpack_exports__ = {}
    __webpack_require__.r(__webpack_exports__)
    /* harmony export */ __webpack_require__.d(__webpack_exports__, {
      /* harmony export */ ImageMask: () => /* binding */ ImageMask,
      /* harmony export */ imageMask: () => /* binding */ imageMask
      /* harmony export */
    })

    var ImageMask = (L.ImageMask = L.ImageOverlay.extend({
      // @section
      // @aka ImageOverlay options
      options: {
        // @option opacity: Number = 1.0
        // The opacity of the image overlay.
        opacity: 1,

        // @option alt: String = ''
        // Text for the `alt` attribute of the image (useful for accessibility).
        alt: '',

        // @option interactive: Boolean = false
        // If `true`, the image overlay will emit [mouse events](#interactive-layer) when clicked or hovered.
        interactive: false,

        // @option crossOrigin: Boolean|String = false
        // Whether the crossOrigin attribute will be added to the image.
        // If a String is provided, the image will have its crossOrigin attribute set to the String provided. This is needed if you want to access image pixel data.
        // Refer to [CORS Settings](https://developer.mozilla.org/en-US/docs/Web/HTML/CORS_settings_attributes) for valid String values.
        crossOrigin: false,

        // @option errorOverlayUrl: String = ''
        // URL to the overlay image to show in place of the overlay that failed to load.
        errorOverlayUrl: '',

        // @option zIndex: Number = 1
        // The explicit [zIndex](https://developer.mozilla.org/docs/Web/CSS/CSS_Positioning/Understanding_z_index) of the overlay layer.
        zIndex: 1,

        // @option className: String = ''
        // A custom class name to assign to the image. Empty by default.
        className: '',

        //图形集合,用于表示显示背景图或者不显示背景图的区域
        polygons: null,

        //显示模式:clip:polygons表示的区域不显示背景图,show:polygons表示的区域显示背景图
        mode: 'clip',

        //围绕地图视图（相对于其大小）扩展剪辑区域多少，例如 0.1 将是每个方向地图视图的 10%
        padding: 0
      },
      initialize: function(url, options) {
        // (String, LatLngBounds, Object)
        this._url = url
        this.options = Object.assign(this.options, options)
        L.Util.setOptions(this, options)

        this._mode = this.options.mode
        this._polygons = this.options.polygons
        this._padding = this.options.padding
      },
      onAdd: function() {
        if (this._url.tagName === 'IMG') {
          this._img = this._url
          this._add()
        } else {
          this._img = new Image()
          this._img.src = this._url
          this._img.onload = function() {
            this._add()
          }.bind(this)
        }
      },
      _add() {
        if (!this._image) {
          this._initImage()

          if (this.options.opacity < 1) {
            this._updateOpacity()
          }
        }

        if (this.options.interactive) {
          DomUtil.addClass(this._image, 'leaflet-interactive')
          this.addInteractiveTarget(this._image)
        }

        this.getPane().appendChild(this._image)
        this._reset()
      },
      /**
       * 初始化容器
       */
      _initContainer() {
        this._container = this._image = document.createElement('canvas')
        this._ctx = this._container.getContext('2d')
      },

      //初始化图片
      _initImage: function() {
        if (!this._container) {
          this._initContainer()
        }

        var p = this._padding,
          size = this._map.getSize(),
          min = this._map.containerPointToLayerPoint(size.multiplyBy(-p)).round()
        let bounds = new L.Bounds(min, min.add(size.multiplyBy(1 + p * 2)).round())
        size = bounds.getSize()

        this._container.width = size.x
        this._container.height = size.y
        this._ctx.translate(-min.x, -min.y)
        this._ctx.clearRect(0, 0, size.x, size.y)

        this._ctx.beginPath()
        for (let i = 0; i < this._polygons.length; i++) {
          this._drawPolygon(this._ctx, this._polygons[i])
        }

        if (this._mode == 'clip') {
          this._ctx.fillStyle = 'rgba(0,0,0,1)'
          this._ctx.fill()
          this._ctx.globalCompositeOperation = 'source-out'
        } else {
          this._ctx.clip()
        }

        this._ctx.drawImage(this._img, min.x, min.y, this._container.width, this._container.height)

        L.DomUtil.addClass(this._container, 'leaflet-image-layer')
        if (this._zoomAnimated) {
          L.DomUtil.addClass(this._container, 'leaflet-zoom-animated')
        }
        if (this.options.className) {
          L.DomUtil.addClass(this._container, this.options.className)
        }

        this._container.onselectstart = L.Util.falseFn
        this._container.onmousemove = L.Util.falseFn

        // @event load: Event
        // Fired when the ImageOverlay layer has loaded its image
        this._container.onload = L.Util.bind(this.fire, this, 'load')
        this._container.onerror = L.Util.bind(this._overlayOnError, this, 'error')

        if (this.options.crossOrigin || this.options.crossOrigin === '') {
          this._container.crossOrigin =
            this.options.crossOrigin === true ? '' : this.options.crossOrigin
        }

        if (this.options.zIndex) {
          this._updateZIndex()
        }
      },

      _drawPolygon(ctx, polygon) {
        polygon.addTo(this._map)
        var pxBounds = new L.Bounds()
        let rings = []
        polygon._projectLatlngs(polygon._latlngs, rings, pxBounds)
        this._map.removeLayer(polygon)

        this._drawLatLngs(ctx, rings)
      },
      _drawLatLngs(ctx, latLngs) {
        for (let i = 0; i < latLngs.length; i++) {
          if (latLngs[i] instanceof Array) {
            this._drawLatLngs(ctx, latLngs[i])
          } else {
            if (i == 0) {
              ctx.moveTo(latLngs[i].x, latLngs[i].y)
            } else {
              ctx.lineTo(latLngs[i].x, latLngs[i].y)
            }
          }
        }
      },

      /**
       * 缩放动画效果
       * @param {*} e
       */
      _animateZoom: function(e) {
        this._updateTransform(e.center, e.zoom)
      },

      //图层事件
      getEvents: function() {
        var events = {
          zoom: this.refresh,
          viewreset: this.refresh,
          moveend: this.refresh
        }

        if (this._zoomAnimated) {
          events.zoomanim = this._animateZoom
        }

        return events
      },

      //调整图片位置
      _reset: function() {
        var p = this._padding,
          image = this._image,
          size = this._map.getSize(),
          min = this._map.containerPointToLayerPoint(size.multiplyBy(-p)).round()
        let bounds = new L.Bounds(min, min.add(size.multiplyBy(1 + p * 2)).round())
        size = bounds.getSize()

        L.DomUtil.setPosition(image, bounds.min)
      },

      //刷新图片
      refresh() {
        this._initImage()
        this._reset()
        return this
      },

      _updateTransform: function(center, zoom) {
        this._center = this._map.getCenter()
        var scale = this._map.getZoomScale(zoom, this._zoom),
          position = L.DomUtil.getPosition(this._image),
          viewHalf = this._map.getSize().multiplyBy(0.5 + this._padding),
          currentCenterPoint = this._map.project(this._center, zoom),
          destCenterPoint = this._map.project(center, zoom),
          centerOffset = destCenterPoint.subtract(currentCenterPoint),
          topLeftOffset = viewHalf
            .multiplyBy(-scale)
            .add(position)
            .add(viewHalf)
            .subtract(centerOffset)
        L.DomUtil.setTransform(this._image, topLeftOffset, scale)
      },

      /**
       * 获取显示模式
       * @returns 显示模式
       */
      getMode() {
        return this._mode
      },

      /**
       * 设置显示模式
       * @param {string} 显示模式
       */
      setMode(mode) {
        this._mode = mode
        this.refresh()
        return this
      },

      /**
       * 获取面集合
       * @returns {Array<L.Polygon>} 面集合
       */
      getPolygons() {
        return this._polygons
      },

      /**
       * 设置面集合
       * @param {Array<L.Polygon>} 面集合
       */
      setPolygons(polygons) {
        this._polygons = polygons
        this.refresh()
        return this
      }
    }))

    var imageMask = (L.imageMask = function(image, options) {
      return new L.ImageMask(image, options)
    })

    /******/ return __webpack_exports__
    /******/
  })()
})
//# sourceMappingURL=leaflet-imagemask.js.map
