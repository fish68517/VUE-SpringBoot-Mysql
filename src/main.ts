/*
 * @Author: wangcong
 * @Date: 2023-07-25 15:14:47
 * @LastEditTime: 2025-07-04 12:29:53
 * @LastEditors: kaix
 * @Description: 
 */
import { createApp } from 'vue'
// import { createApp } from "vue/dist/vue.esm-browser";
// import { createApp } from "vue/dist/vue.esm-bundler.js";
// import CoolComps from "./package";
import { setupNaive } from '@/plugins/naive'
import { setupStore } from '@/store'
import router from '@/router'
import App from './App.vue'

// import { getColorAlpha } from "./utils/d3-color-tools";

// import Demo from './demo/Demo.vue'
// import Playground from './demo/Playground.vue'
//@ts-ignore
import { CustomIcon } from '@/components/Common/CustomIcon'
// const colorObj = getColorAlpha('rgba(255, 255, 255, 1)', 0.5)

// // colorObj.opacity = 0.8;
// console.log('color', colorObj)

//引入字体
import '@/assets/font/iconfont.css'

import * as CoolComps from './package'

console.log('组件库信息', CoolComps)

import mitt from 'mitt'

const app = createApp(App)
app.config.warnHandler = () => null
app.component('CustomIcon', CustomIcon)
app.config.globalProperties.$Bus = mitt()

app.use(CoolComps)
app.use(router)
app.use(setupStore)
setupNaive(app)

app.mount('#app', true)

// createApp(Demo)
//   .use(CoolComps)
//   .mount("#app");
