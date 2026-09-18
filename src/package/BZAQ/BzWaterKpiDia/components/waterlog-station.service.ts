import axios from 'axios'
import {
  DISPLAY_ROUTE_ORDER,
  STATIONS,
  STATION_NAME_ALIASES
} from './flood-data'
import type { Station } from './flood-data'
import {
  MOCK_ALL_HYDROLOGY_RESPONSE,
  USE_STATION_MOCK
} from './waterlog-station.mock'

const HYDROLOGY_BASE_URL = 'http://23.210.227.34:23343/yzqzlzx'
export const ALL_STATION_URL = `${HYDROLOGY_BASE_URL}/api/boot/system/common/getAllHydrologyStationInfo`

type RawStation = Record<string, any>

export type StationDataSource = 'base' | 'mock' | 'api'

export interface StationLoadResult {
  stations: Station[]
  source: StationDataSource
  error?: Error
}

function normalizeStationName(value: unknown): string {
  const original = String(value ?? '').trim()
  if (!original) return ''
  const normalized = original.replace(/\(/g, '（').replace(/\)/g, '）')
  return STATION_NAME_ALIASES[original] || STATION_NAME_ALIASES[normalized] || normalized
}

function firstFiniteNumber(...values: unknown[]): number | null {
  for (const value of values) {
    if (value === null || value === undefined || typeof value === 'boolean' || (typeof value === 'string' && !value.trim())) continue
    const numberValue = Number(value)
    if (Number.isFinite(numberValue)) return numberValue
  }
  return null
}

function firstPositiveNumber(...values: unknown[]): number | null {
  for (const value of values) {
    if (value === null || value === undefined || typeof value === 'boolean' || (typeof value === 'string' && !value.trim())) continue
    const parsed = Number(value)
    if (Number.isFinite(parsed) && parsed > 0) return parsed
  }
  return null
}

function validLongitude(value: number | null): value is number {
  return value !== null && value >= -180 && value <= 180 && value !== 0
}

function validLatitude(value: number | null): value is number {
  return value !== null && value >= -90 && value <= 90 && value !== 0
}

function getRawStations(response: any): RawStation[] {
  const candidates = [
    response?.data?.data?.stations,
    response?.data?.stations,
    response?.stations
  ]
  const stations = candidates.find(Array.isArray)
  if (!stations) throw new Error('水情站点接口响应缺少 data.stations 数组')
  return stations
}

export function normalizeAndMergeStations(
  rawStations: RawStation[],
  source: Exclude<StationDataSource, 'base'>
): Station[] {
  const rawStationMap = new Map<string, RawStation>()
  rawStations.forEach(rawStation => {
    if (!rawStation || typeof rawStation !== 'object') return
    const name = normalizeStationName(rawStation.stnmShort || rawStation.stnm)
    if (name) rawStationMap.set(name, rawStation)
  })

  const baseStationMap = new Map(STATIONS.map(station => [station.name, station]))

  return DISPLAY_ROUTE_ORDER.flatMap(name => {
    const baseStation = baseStationMap.get(name)
    if (!baseStation) return []
    const rawStation = rawStationMap.get(name)
    if (!rawStation) {
      return [{
        ...baseStation,
        warning: null,
        guarantee: null,
        z: null,
        source,
        missingFields: [
          '站点未返回', '当前水位', '警戒水位', '保证水位'
        ]
      }]
    }

    const longitude = firstFiniteNumber(rawStation.lon, rawStation.lgtd)
    const latitude = firstFiniteNumber(rawStation.lat, rawStation.lttd)
    const warning = firstPositiveNumber(rawStation.ivhz, rawStation.wrz, rawStation.ogrsw, rawStation.taz)
    const guarantee = firstPositiveNumber(rawStation.grz)
    const distance = firstFiniteNumber(rawStation.dist, rawStation.distance)
    const currentLevel = firstFiniteNumber(rawStation.z, rawStation.sw)
    const stationCodeValue = rawStation.stcd ?? rawStation.reqStcd

    return [{
      ...baseStation,
      lon: validLongitude(longitude) ? longitude : baseStation.lon,
      lat: validLatitude(latitude) ? latitude : baseStation.lat,
      dist: distance !== null && distance >= 0 ? distance : baseStation.dist,
      warning,
      guarantee,
      z: currentLevel,
      stcd: stationCodeValue === null || stationCodeValue === undefined
        ? baseStation.stcd
        : String(stationCodeValue),
      source,
      missingFields: [
        ...(currentLevel == null ? ['当前水位'] : []),
        ...(warning == null ? ['警戒水位'] : []),
        ...(guarantee == null ? ['保证水位'] : [])
      ]
    }]
  })
}

function cloneBaseStations(): Station[] {
  return DISPLAY_ROUTE_ORDER.flatMap(name => {
    const station = STATIONS.find(item => item.name === name)
    return station ? [{ ...station, warning: null, guarantee: null, z: null, source: 'base' as const }] : []
  })
}

export async function loadHydrologyStations(): Promise<StationLoadResult> {
  if (USE_STATION_MOCK) {
    try {
      return {
        stations: normalizeAndMergeStations(getRawStations(MOCK_ALL_HYDROLOGY_RESPONSE), 'mock'),
        source: 'mock'
      }
    } catch (error) {
      return {
        stations: cloneBaseStations(),
        source: 'base',
        error: error instanceof Error ? error : new Error(String(error))
      }
    }
  }

  const started = Date.now()
  let rawResponse: unknown = null
  let httpStatus: number | null = null
  let output: StationLoadResult
  try {
    const response = await axios.post(ALL_STATION_URL, null, { timeout: 10000 })
    rawResponse = response.data
    httpStatus = response.status
    output = {
      stations: normalizeAndMergeStations(getRawStations(response), 'api'),
      source: 'api'
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      httpStatus = error.response?.status ?? null
      rawResponse = error.response?.data ?? rawResponse
    }
    output = {
      stations: cloneBaseStations(),
      source: 'base',
      error: error instanceof Error ? error : new Error(String(error))
    }
  }
  const diagnostic = {
    日志类型: '水情站点接口诊断',
    记录时间: new Date().toISOString(),
    说明: '当前水位缺失或请求失败时留空；不使用模拟水位或本地阈值兜底。坐标和距离缺失时沿用地图基础定位资料。',
    请求: { 地址: ALL_STATION_URL, 方法: 'POST', 参数: null, 超时毫秒: 10000 },
    HTTP状态: httpStatus,
    耗时毫秒: Date.now() - started,
    结果: output.error ? '请求或响应解析失败' : output.stations.some(s => s.missingFields?.length) ? '接口返回，但部分站点缺少数据' : '站点数据完整',
    错误说明: output.error?.message ?? null,
    排查提示: httpStatus === null ? '未收到HTTP响应，请检查接口网络、服务状态、浏览器跨域或混合内容拦截；不能仅凭此断定后端错误。' : '对照接口原始响应和站点检查，确认站名及 z/sw、ivhz/wrz、grz 字段。',
    接口原始响应: rawResponse,
    站点检查: output.stations.map(station => ({
      站名: station.name,
      当前水位: station.z ?? null,
      警戒水位: station.warning,
      保证水位: station.guarantee,
      缺失项目: station.missingFields ?? (output.error ? ['接口失败，数据不可用'] : []),
      数据来源: station.source
    }))
  }
  // 不记录请求头，避免把登录凭据写入日志。
  const json = JSON.stringify(diagnostic, (key, value) => /token|authorization|password|cookie|secret/i.test(key) ? '[已隐藏]' : value, 2)
  console.info(json)
  if (process.env.NODE_ENV === 'development') {
    try {
      const saved = await axios.post('/__waterlog_diagnostics', JSON.parse(json), { timeout: 3000 })
      console.info(JSON.stringify({ 水情日志保存成功: saved.data }, null, 2))
    } catch (error) {
      console.error(JSON.stringify({ 水情日志保存失败: error instanceof Error ? error.message : String(error), 处理方式: '请重启 npm run dev；原始诊断JSON仍保留在控制台。' }, null, 2))
    }
  }
  return output
}
