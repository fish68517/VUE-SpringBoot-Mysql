import { regions, users, dictionary } from '../repositories/seed'
export const regionName = (id: string) => regions.find((r) => r.id === id)?.name || id
export const personName = (id: string | null) => users.find((u) => u.id === id)?.displayName || '未分配'
export const facilityType = (id: string) => (dictionary.facilityTypes as Record<string, string>)[id] || id
export const orderType = (id: string) => (dictionary.orderTypes as Record<string, string>)[id] || id
export const options = (values: Record<string, string>, all = '') => [
  ...(all ? [{ value: '', label: all }] : []),
  ...Object.entries(values).map(([value, label]) => ({ value, label })),
]
export const confirm = (title: string, content: string) =>
  new Promise<boolean>((resolve) =>
    uni.showModal({ title, content, success: (r) => resolve(r.confirm), fail: () => resolve(false) }),
  )
