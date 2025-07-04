function importJS(filePath) {
  if (filePath.match(/\.js$/)) {
    document.write(`<script src="${filePath}" type="text/javascript"></script>`)
  }
}

function importCSS(filePath) {
  if (filePath.match(/\.css$/)) {
    document.write(`<link rel="stylesheet" href="${filePath}" type="text/css" />`)
  }
}

function importFiles() {
  importCSS('/lib/gis3d/Cesium/Widgets/widgets.css')
  importCSS('/lib/gis3d/mars3d/mars3d.css')

  importJS('/lib/gis3d/Cesium/Cesium.js')
  importJS('/lib/gis3d/mars3d/mars3d.js')
  importJS('/lib/gis3d/mars3d/heatmap.js')
  importJS('/lib/gis3d/mars3d/mars3d-heatmap.js')
}

importFiles()
