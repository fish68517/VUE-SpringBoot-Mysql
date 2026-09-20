import { defineStore } from 'pinia'
import { computed, ref, shallowRef } from 'vue'
import { Phase2Engine as DemoEngine } from '../services/mock/phase2-engine'
import { storage } from '../platform/storage'
import { canSee, dashboardMetrics } from '../domain/metrics'
export const useDemo = defineStore('demo', () => {
  const engine = new DemoEngine(storage),
    state = shallowRef(engine.state),
    user = shallowRef(engine.user),
    warning = ref(engine.warning),
    playing = ref(false)
  const filters = ref<Record<string, any>>({}),
    selection = ref('FAC-001')
  let timer: ReturnType<typeof setInterval> | null = null
  const sync = () => {
    state.value = engine.state
    user.value = engine.user
    warning.value = engine.warning
  }
  async function run<T>(fn: (engine: DemoEngine) => T) {
    const result = await engine.execute(() => fn(engine))
    sync()
    if (!result.success) uni.showToast({ title: result.error.message, icon: 'none', duration: 3000 })
    return result
  }
  const has = (permission: string) => !!user.value && engine.has(permission)
  const facilities = computed(() => state.value.facilities.filter((f) => canSee(user.value, f)))
  const ids = computed(() => new Set(facilities.value.map((f) => f.id)))
  const alarms = computed(() => state.value.alarms.filter((a) => ids.value.has(a.facilityId)))
  const orders = computed(() => state.value.workorders.filter((w) => ids.value.has(w.facilityId)))
  const tasks = computed(() =>
    state.value.inspections.filter(
      (t) => user.value && (user.value.roleId !== 'operator' || t.assigneeId === user.value.id),
    ),
  )
  const metrics = computed(() => dashboardMetrics(state.value, user.value))
  function pause() {
    playing.value = false
    if (timer) clearInterval(timer)
    timer = null
  }
  function play() {
    if (!user.value || playing.value || warning.value) return
    playing.value = true
    timer = setInterval(async () => {
      const r = await run((e) => e.advance(60))
      if (!r.success) pause()
    }, 5000)
  }
  async function logout() {
    pause()
    await run((e) => e.logout())
    filters.value = {}
    uni.reLaunch({ url: '/pages/login/index' })
  }
  return {
    state,
    user,
    warning,
    playing,
    filters,
    selection,
    run,
    has,
    facilities,
    alarms,
    orders,
    tasks,
    metrics,
    pause,
    play,
    logout,
  }
})
