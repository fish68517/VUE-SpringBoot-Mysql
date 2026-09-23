import fs from 'node:fs'
import path from 'node:path'
import { routes, titles } from '../navigation/routeMap'
for (const [key, route] of Object.entries(routes)) {
  if (key === 'login' || key === 'dashboard') continue
  const file = path.resolve('pages', route + '.vue')
  fs.mkdirSync(path.dirname(file), { recursive: true })
  fs.writeFileSync(
    file,
    `<template><PageHost v-if="ready && demo.user" :key="JSON.stringify(query)" mode="${key}" :query="query" :active="active" /></template>
<script setup lang="ts">
import PageHost from '../../components/PageHost.vue'
import { usePageContext } from '../../navigation/usePageContext'
const {demo,query,active,ready}=usePageContext('${key}')
</script>
`,
  )
}
fs.writeFileSync(
  'pages.json',
  JSON.stringify(
    {
      pages: Object.entries(routes).map(([key, route]) => ({
        path: 'pages/' + route,
        style: { navigationStyle: 'custom', navigationBarTitleText: titles[key] || '账号登录' },
      })),
      globalStyle: {
        navigationStyle: 'custom',
        backgroundColor: '#f4f7fb',
        navigationBarTextStyle: 'black',
        navigationBarTitleText: '智慧供水',
      },
    },
    null,
    2,
  ) + '\n',
)
console.log('已创建 ' + Object.keys(routes).length + ' 个注册页面')
