/*
 * @Author: kaix
 * @Date: 2023-03-15 22:03:36
 * @LastEditTime: 2023-07-31 17:42:22
 * @LastEditors: kaix
 * @Description: 导出组件 以及注册
 */
// @ts-ignore
const components = COMPONENT_LIST

import create from './create'

const coolComps = create({
  components: Object.keys(components).map(key => components[key as keyof typeof components])
})

export default coolComps
export const install = coolComps.install
