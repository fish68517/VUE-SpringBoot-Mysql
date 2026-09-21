// Local business times use the project's fixed Beijing-time format.
export function validEventTime(value: unknown): value is string {
  if (typeof value !== 'string' || !/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\+08:00$/.test(value)) return false
  const time = Date.parse(value)
  return (
    Number.isFinite(time) && new Date(time + 8 * 3600000).toISOString().slice(0, 19) === value.slice(0, 19)
  )
}
