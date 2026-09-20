export async function downloadText(name: string, text: string, mime = 'text/plain') {
  // #ifdef H5
  const url = URL.createObjectURL(new Blob([text], { type: mime + ';charset=utf-8' })),
    a = document.createElement('a')
  a.href = url
  a.download = name
  a.click()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
  // #endif
  // #ifdef APP-PLUS
  await new Promise<void>((resolve, reject) => {
    plus.io.requestFileSystem(
      plus.io.PRIVATE_DOC,
      (fs) => {
        fs.root!.getFile(
          name,
          { create: true },
          (entry) => {
            entry.createWriter((writer) => {
              let cleared = false
              writer.onwrite = () => {
                if (!cleared) {
                  cleared = true
                  writer.seek(0)
                  writer.write(text)
                  return
                }
                uni.showModal({ title: '文件已保存', content: entry.toLocalURL(), showCancel: false })
                resolve()
              }
              writer.onerror = reject
              writer.truncate(0)
            }, reject)
          },
          reject,
        )
      },
      reject,
    )
  })
  // #endif
}
export async function chooseText(): Promise<string> {
  // #ifdef H5
  return new Promise((resolve, reject) => {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = '.json,application/json'
    input.onchange = async () => {
      const file = input.files?.[0]
      if (!file) return reject(new Error('未选择文件'))
      if (file.size > 8_000_000) return reject(new Error('文件超过 8 MB'))
      try {
        resolve(await file.text())
      } catch {
        reject(new Error('读取失败'))
      }
    }
    input.oncancel = () => reject(new Error('已取消选择'))
    input.click()
  })
  // #endif
  // #ifdef APP-PLUS
  return new Promise((resolve, reject) => {
    plus.io.resolveLocalFileSystemURL(
      '_doc/智慧供水快照.json',
      (entry) => {
        ;(entry as unknown as PlusIoFileEntry).file((file) => {
          const reader = new plus.io.FileReader()
          reader.onloadend = () => resolve(String(reader.result))
          reader.onerror = () => reject(new Error('读取失败'))
          reader.readAsText(file, 'utf-8')
        }, reject)
      },
      () => reject(new Error('请先导出快照，或在输入框粘贴快照内容')),
    )
  })
  // #endif
}
