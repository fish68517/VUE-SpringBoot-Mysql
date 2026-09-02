/** 防汛预警算法 (JS 版, 翻译自 engine.cpp) */

import type { Station } from './flood-data'

const LEVEL_NAME = ['无', '蓝色', '黄色', '红色']
const LEVEL_VELOCITY = [0, 4.0, 5.5, 7.0]

export function levelName(l: number): string {
  return LEVEL_NAME[l] || '无'
}

function levelVelocity(l: number): number {
  return LEVEL_VELOCITY[l] || 0
}

export function fmtHours(h: number): string {
  const totalMin = Math.round(h * 60)
  const hrs = Math.floor(totalMin / 60)
  const min = totalMin % 60
  let s = ''
  if (hrs > 0) s += hrs + '小时'
  if (min > 0) s += min + '分钟'
  if (!hrs && !min) s = '0分钟'
  return s
}

interface RuleGroup {
  stations: string[]
  require: number
}

interface Rule {
  river: string
  desc: string
  threshold: number // 0=警戒水位, 1=保证水位
  result: number    // 1=蓝色 2=黄色 3=红色
  groups: RuleGroup[]
}

function buildRules(): Rule[] {
  const upstreamJ = ['鸭嘴', '东津沱', '北碚（三）']
  const nearJ = ['大溪沟', '化龙桥', '千厮门']
  const upstreamY = ['泸州（三）', '塔坪', '钓二嘴']
  const nearY = ['菜园坝', '玄坛庙']
  const allJ = ['鸭嘴', '东津沱', '北碚（三）', '磁器口', '大溪沟', '化龙桥', '千厮门']
  const allY = ['泸州（三）', '塔坪', '钓二嘴', '李家沱', '菜园坝', '玄坛庙']
  const G = (stations: string[], require: number): RuleGroup => ({ stations, require })
  const rules: Rule[] = []
  const push = (river: string, desc: string, th: number, res: number, groups: RuleGroup[]) =>
    rules.push({ river, desc, threshold: th, result: res, groups })

  push('嘉陵江', '1个·磁器口·保证水位', 1, 1, [G(['磁器口'], 1)])
  push('嘉陵江', '1个·大溪沟/化龙桥/千厮门·警戒水位', 0, 1, [G(nearJ, 1)])
  push('嘉陵江', '1个·大溪沟/化龙桥/千厮门·保证水位', 1, 2, [G(nearJ, 1)])
  push('嘉陵江', '2个·鸭嘴/东津沱/北碚(任意2)·警戒水位', 0, 1, [G(upstreamJ, 2)])
  push('嘉陵江', '2个·鸭嘴/东津沱/北碚(任意2)·保证水位', 1, 2, [G(upstreamJ, 2)])
  push('嘉陵江', '2个·(上游任意1)+(下游任意1)·警戒水位', 0, 1, [G(upstreamJ, 1), G(nearJ, 1)])
  push('嘉陵江', '2个·(上游任意1)+(下游任意1)·保证水位', 1, 2, [G(upstreamJ, 1), G(nearJ, 1)])
  push('嘉陵江', '2个·大溪沟/化龙桥/千厮门(任意2)·警戒水位', 0, 2, [G(nearJ, 2)])
  push('嘉陵江', '2个·大溪沟/化龙桥/千厮门(任意2)·保证水位', 1, 3, [G(nearJ, 2)])
  push('嘉陵江', '3个及以上·大溪沟/化龙桥/千厮门·警戒水位', 0, 3, [G(nearJ, 3)])
  push('嘉陵江', '3个及以上·任意3个·警戒水位', 0, 2, [G(allJ, 3)])
  push('嘉陵江', '3个及以上·任意3个·保证水位', 1, 3, [G(allJ, 3)])

  push('长江', '1个·菜园坝/玄坛庙·警戒水位', 0, 1, [G(nearY, 1)])
  push('长江', '1个·菜园坝/玄坛庙·保证水位', 1, 2, [G(nearY, 1)])
  push('长江', '1个·李家沱·保证水位', 1, 1, [G(['李家沱'], 1)])
  push('长江', '2个·泸州/塔坪/钓二嘴(任意2)·警戒水位', 0, 1, [G(upstreamY, 2)])
  push('长江', '2个·泸州/塔坪/钓二嘴(任意2)·保证水位', 1, 2, [G(upstreamY, 2)])
  push('长江', '2个·(上游任意1)+(下游任意1)·警戒水位', 0, 1, [G(upstreamY, 1), G(nearY, 1)])
  push('长江', '2个·(上游任意1)+(下游任意1)·保证水位', 1, 2, [G(upstreamY, 1), G(nearY, 1)])
  push('长江', '2个·菜园坝+玄坛庙·警戒水位', 0, 2, [G(nearY, 2)])
  push('长江', '2个·菜园坝+玄坛庙·保证水位', 1, 3, [G(nearY, 2)])
  push('长江', '3个及以上·李家沱+菜园坝+玄坛庙·警戒水位', 0, 3, [G(['李家沱', '菜园坝', '玄坛庙'], 3)])
  push('长江', '3个及以上·任意3个·警戒水位', 0, 2, [G(allY, 3)])
  push('长江', '3个及以上·任意3个·保证水位', 1, 3, [G(allY, 3)])

  push('两江联合', '嘉陵江上游任意1 + 长江上游任意1·警戒水位', 0, 1, [G(upstreamJ, 1), G(upstreamY, 1)])
  push('两江联合', '嘉陵江上游任意1 + 长江上游任意1·保证水位', 1, 2, [G(upstreamJ, 1), G(upstreamY, 1)])

  return rules
}

function ruleMatches(rule: Rule, levels: Record<string, number>, stationMap: Record<string, Station>): boolean {
  for (const g of rule.groups) {
    let hit = 0
    for (const name of g.stations) {
      const st = stationMap[name]
      if (!st) continue
      const level = levels[name] || 0
      const threshold = rule.threshold === 0 ? st.warning : st.guarantee
      if (threshold == null || threshold < 0) continue
      if (level >= threshold) hit++
    }
    if (hit < g.require) return false
  }
  return true
}

export interface WarningResult {
  hasWarning: boolean
  level: number
  levelName: string
  velocity: number
  nearestStation: string
  nearestDistanceKm: number
  arrivalHours: number
  warningHours: number
  warningImmediate: boolean
  reachedWarningStations: string[]
  matchedRules: string[]
  arrivalTime?: string
  warningTime?: string
  waterRise?: number
  exceedWarning?: number
  exitHours?: number
}

export function evaluateWarning(stations: Station[], levels: Record<string, number>): WarningResult {
  const stationMap: Record<string, Station> = {}
  stations.forEach(s => { stationMap[s.name] = s })
  const triggerNames = stations
    .filter(s => s.name !== '朝天门' && s.name !== '郭家沱')
    .map(s => s.name)

  const reached: string[] = []
  for (const name of triggerNames) {
    const st = stationMap[name]
    if (!st) continue
    const level = levels[name] || 0
    if (st.warning != null && level >= st.warning) reached.push(name)
  }

  let finalLevel = 0
  const matched: string[] = []
  for (const rule of buildRules()) {
    if (ruleMatches(rule, levels, stationMap)) {
      matched.push(rule.river + '·' + rule.desc + '→' + levelName(rule.result))
      if (rule.result > finalLevel) finalLevel = rule.result
    }
  }

  const hasWarning = finalLevel !== 0
  let nearest = '', nearestDist = 0
  for (const name of reached) {
    const d = stationMap[name].dist
    if (!nearest || d < nearestDist) { nearest = name; nearestDist = d }
  }

  let velocity = 0, arrivalHours = 0, warningHours = 0, warningImmediate = false
  let waterRise = 0, exceedWarning = 0, exitHours = 0
  if (hasWarning) {
    velocity = levelVelocity(finalLevel)
    arrivalHours = nearestDist / velocity
    warningImmediate = arrivalHours < 1.0
    warningHours = arrivalHours - 1.0
    if (warningHours < 0) warningHours = 0

    // 朝天门预估水位上涨量（基于最近触发站点的超警量 + 沿程衰减系数 0.7）
    const nearSt = stationMap[nearest]
    const ctmSt = stationMap['朝天门']
    if (nearSt && ctmSt) {
      const nearLevel = levels[nearest] || 0
      const nearExceed = nearSt.warning != null ? nearLevel - nearSt.warning : 0
      waterRise = Math.max(0, nearExceed * 0.7)
      if (ctmSt.warning != null) {
        exceedWarning = Math.max(0, waterRise - 0) // 超过朝天门警戒水位的量
      }
    }

    // 洪峰出境时间 = 到达朝天门后继续向下游传播，朝天门距出境约14km（郭家沱）
    const exitDist = 14.0
    exitHours = arrivalHours + exitDist / velocity
  }

  return {
    hasWarning,
    level: finalLevel,
    levelName: levelName(finalLevel),
    velocity,
    nearestStation: nearest,
    nearestDistanceKm: nearestDist,
    arrivalHours,
    warningHours,
    warningImmediate,
    reachedWarningStations: reached,
    matchedRules: matched,
    waterRise,
    exceedWarning,
    exitHours
  }
}
