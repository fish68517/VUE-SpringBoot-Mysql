import { downloadText } from './files'
export function csvText(rows: unknown[][]) {
  return (
    '\uFEFF' +
    rows
      .map((row) =>
        row
          .map((v) => {
            let s = String(v ?? '')
            if (/^[=+@\-]/.test(s)) s = "'" + s
            return '"' + s.replace(/"/g, '""') + '"'
          })
          .join(','),
      )
      .join('\r\n')
  )
}
export function exportCsv(name: string, rows: unknown[][]) {
  return downloadText(name + '.csv', csvText(rows), 'text/csv')
}
