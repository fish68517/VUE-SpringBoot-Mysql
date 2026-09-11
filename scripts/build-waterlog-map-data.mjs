import crypto from 'node:crypto'
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { createRequire } from 'node:module'

const require = createRequire(import.meta.url)
const { bboxClip, cleanCoords, simplify } = require('@turf/turf')

const SCRIPT_DIR = path.dirname(fileURLToPath(import.meta.url))
const ROOT_DIR = path.resolve(SCRIPT_DIR, '..')
const SOURCE_DIR = path.join(SCRIPT_DIR, 'map-source')
const OUTPUT_DIR = path.join(
  ROOT_DIR,
  'src/package/BZAQ/BzWaterKpiDia/components/map-data'
)

const ADMIN_INPUT = path.join(SOURCE_DIR, 'china-province-city.full.geojson')
const REGIONS = ['chongqing', 'sichuan', 'guizhou', 'hubei', 'hunan']
const BBOX = [105.1171875, 28.613459424004414, 109.6875, 31.353636941500987]
const ADMIN_SOURCE_CRS = 'GCJ-02'

function readJson(filePath) {
  if (!fs.existsSync(filePath)) throw new Error(`输入文件不存在：${filePath}`)
  const data = JSON.parse(fs.readFileSync(filePath, 'utf8'))
  if (data?.type !== 'FeatureCollection' || !Array.isArray(data.features)) {
    throw new Error(`不是合法 FeatureCollection：${filePath}`)
  }
  return data
}

function sha256(filePath) {
  return crypto.createHash('sha256').update(fs.readFileSync(filePath)).digest('hex')
}

function outOfChina(lng, lat) {
  return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271
}

function transformLat(lng, lat) {
  let value = -100 + 2 * lng + 3 * lat + 0.2 * lat * lat + 0.1 * lng * lat
  value += 0.2 * Math.sqrt(Math.abs(lng))
  value += (20 * Math.sin(6 * lng * Math.PI) + 20 * Math.sin(2 * lng * Math.PI)) * 2 / 3
  value += (20 * Math.sin(lat * Math.PI) + 40 * Math.sin(lat / 3 * Math.PI)) * 2 / 3
  value += (160 * Math.sin(lat / 12 * Math.PI) + 320 * Math.sin(lat * Math.PI / 30)) * 2 / 3
  return value
}

function transformLng(lng, lat) {
  let value = 300 + lng + 2 * lat + 0.1 * lng * lng + 0.1 * lng * lat
  value += 0.1 * Math.sqrt(Math.abs(lng))
  value += (20 * Math.sin(6 * lng * Math.PI) + 20 * Math.sin(2 * lng * Math.PI)) * 2 / 3
  value += (20 * Math.sin(lng * Math.PI) + 40 * Math.sin(lng / 3 * Math.PI)) * 2 / 3
  value += (150 * Math.sin(lng / 12 * Math.PI) + 300 * Math.sin(lng / 30 * Math.PI)) * 2 / 3
  return value
}

function wgs84ToGcj02(coordinate) {
  const [lng, lat, ...rest] = coordinate
  if (outOfChina(lng, lat)) return coordinate
  const axis = 6378245
  const eccentricity = 0.006693421622965943
  let dLat = transformLat(lng - 105, lat - 35)
  let dLng = transformLng(lng - 105, lat - 35)
  const radLat = lat / 180 * Math.PI
  let magic = Math.sin(radLat)
  magic = 1 - eccentricity * magic * magic
  const sqrtMagic = Math.sqrt(magic)
  dLat = dLat * 180 / ((axis * (1 - eccentricity)) / (magic * sqrtMagic) * Math.PI)
  dLng = dLng * 180 / (axis / sqrtMagic * Math.cos(radLat) * Math.PI)
  return [lng + dLng, lat + dLat, ...rest]
}

function mapCoordinates(coordinates, transform) {
  if (!Array.isArray(coordinates)) return coordinates
  if (coordinates.length >= 2 && typeof coordinates[0] === 'number') {
    return transform(coordinates)
  }
  return coordinates.map(item => mapCoordinates(item, transform))
}

function transformFeature(feature, transform) {
  return {
    ...feature,
    geometry: feature.geometry
      ? { ...feature.geometry, coordinates: mapCoordinates(feature.geometry.coordinates, transform) }
      : feature.geometry,
  }
}

function roundCoordinate(coordinate) {
  const [lng, lat, ...rest] = coordinate
  return [Number(lng.toFixed(6)), Number(lat.toFixed(6)), ...rest]
}

function clipAndSimplify(feature, tolerance) {
  let clipped
  try {
    clipped = bboxClip(feature, BBOX)
  } catch (error) {
    console.warn(`跳过无法裁剪的要素 ${feature.id || feature.properties?.name || ''}: ${error.message}`)
    return null
  }
  if (!clipped?.geometry?.coordinates?.length) return null
  try {
    return cleanCoords(simplify(clipped, { tolerance, highQuality: false, mutate: false }))
  } catch (error) {
    console.warn(`跳过无法简化的要素 ${feature.id || feature.properties?.name || ''}: ${error.message}`)
    return null
  }
}

function buildAdmin() {
  const source = readJson(ADMIN_INPUT)
  const features = []
  for (const feature of source.features) {
    const properties = feature.properties || {}
    const adcode = Number(properties.adcode)
    if (!String(adcode).startsWith('50')) continue
    let next = clipAndSimplify(feature, 0.002)
    if (!next) continue
    if (ADMIN_SOURCE_CRS === 'WGS84') next = transformFeature(next, wgs84ToGcj02)
    next = transformFeature(next, roundCoordinate)
    const mapPoint = value => (
      Array.isArray(value) && value.length >= 2 && ADMIN_SOURCE_CRS === 'WGS84'
        ? wgs84ToGcj02(value)
        : value
    )
    next.properties = {
      name: String(properties.name || ''),
      adcode,
      level: 'district',
      center: mapPoint(properties.center),
      centroid: mapPoint(properties.centroid),
    }
    features.push(next)
  }
  return { type: 'FeatureCollection', features }
}

function buildRivers() {
  const seen = new Set()
  const features = []
  const counts = Object.fromEntries(REGIONS.map(region => [region, 0]))

  for (const region of REGIONS) {
    const input = path.join(SOURCE_DIR, `${region}-waterways.raw.geojson`)
    const source = readJson(input)
    for (const feature of source.features) {
      const osmId = String(feature.id || feature.properties?.id || '')
      if (!osmId || seen.has(osmId)) continue
      seen.add(osmId)
      const properties = feature.properties || {}
      const osmWaterway = properties.waterway
      if (!['river', 'stream', 'canal'].includes(osmWaterway)) continue
      const name = String(properties.name || '')
      const nameZh = String(properties['name:zh'] || '')
      if (/长江|嘉陵江|Yangtze|Jialing/i.test(`${name} ${nameZh}`)) continue

      let next = clipAndSimplify(feature, osmWaterway === 'stream' ? 0.001 : 0.0006)
      if (!next) continue
      next = transformFeature(next, wgs84ToGcj02)
      next = transformFeature(next, roundCoordinate)
      next.id = osmId
      next.properties = {
        name: name || undefined,
        nameZh: nameZh || undefined,
        osmId,
        osmWaterway,
        rank: osmWaterway === 'river' ? 1 : osmWaterway === 'canal' ? 2 : 3,
        business: false,
      }
      features.push(next)
      counts[region] += 1
    }
  }
  return { collection: { type: 'FeatureCollection', features }, counts }
}

function writeJson(fileName, data) {
  fs.mkdirSync(OUTPUT_DIR, { recursive: true })
  const output = path.join(OUTPUT_DIR, fileName)
  fs.writeFileSync(output, `${JSON.stringify(data)}\n`, 'utf8')
  return { output, bytes: fs.statSync(output).size, sha256: sha256(output) }
}

const admin = buildAdmin()
const rivers = buildRivers()
const adminResult = writeJson('waterlog-admin.gcj02.geojson', admin)
const riverResult = writeJson('waterlog-rivers.gcj02.geojson', rivers.collection)

console.log(JSON.stringify({
  bbox: BBOX,
  displayCrs: 'GCJ-02',
  adminSourceCrs: ADMIN_SOURCE_CRS,
  admin: { features: admin.features.length, ...adminResult },
  rivers: { features: rivers.collection.features.length, byFirstRegion: rivers.counts, ...riverResult },
}, null, 2))
