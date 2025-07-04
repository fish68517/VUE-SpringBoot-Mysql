/*
 * @Author: kaix
 * @Date: 2023-03-12 16:01:58
 * @LastEditTime: 2023-07-31 17:39:36
 * @LastEditors: kaix
 * @Description: 
 * 首先设计一个入口，包含两个功能：
    导出全部组件；
    实现一个 Vue 插件，插件中编写 install 方法，将所有组件安装到 vue 实例中。
 */

export { default, install } from './preset'

export { default as create } from './create'
