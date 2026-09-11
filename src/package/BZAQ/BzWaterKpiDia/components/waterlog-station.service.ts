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
    if (value === '' || value === null || value === undefined) continue
    const numberValue = Number(value)
    if (Number.isFinite(numberValue)) return numberValue
  }
  return null
}

function firstPositiveNumber(...values: unknown[]): number | null {
  for (const value of values) {
    if (value === '' || value === null || value === undefined) continue
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
        source: 'base' as const,
        missingFields: [
          ...(baseStation.warning == null ? ['warning'] : []),
          ...(baseStation.guarantee == null ? ['guarantee'] : [])
        ]
      }]
    }

    const longitude = firstFiniteNumber(rawStation.lon, rawStation.lgtd)
    const latitude = firstFiniteNumber(rawStation.lat, rawStation.lttd)
    const warning = firstPositiveNumber(rawStation.ivhz, rawStation.wrz, rawStation.ogrsw, rawStation.taz)
      ?? baseStation.warning
    const guarantee = firstPositiveNumber(rawStation.grz) ?? baseStation.guarantee
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
      z: currentLevel ?? baseStation.z ?? null,
      stcd: stationCodeValue === null || stationCodeValue === undefined
        ? baseStation.stcd
        : String(stationCodeValue),
      source,
      missingFields: [
        ...(warning == null ? ['warning'] : []),
        ...(guarantee == null ? ['guarantee'] : [])
      ]
    }]
  })
}

function cloneBaseStations(): Station[] {
  return DISPLAY_ROUTE_ORDER.flatMap(name => {
    const station = STATIONS.find(item => item.name === name)
    return station ? [{ ...station, source: 'base' as const }] : []
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

  try {
    const response = await axios.post(ALL_STATION_URL, null, { timeout: 10000 })
    return {
      stations: normalizeAndMergeStations(getRawStations(response), 'api'),
      source: 'api'
    }
  } catch (error) {
    return {
      stations: cloneBaseStations(),
      source: 'base',
      error: error instanceof Error ? error : new Error(String(error))
    }
  }
}
