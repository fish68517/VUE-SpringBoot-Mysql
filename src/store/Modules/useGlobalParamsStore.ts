/*
 * @Author: kaix
 * @Date: 2025-07-04 12:31:07
 * @LastEditTime: 2025-07-04 12:31:13
 * @LastEditors: kaix
 * @Description: 
 */
// stores/globalParams.ts
import { defineStore } from 'pinia'
import { reactive } from 'vue'

export const useGlobalParamsStore = defineStore('globalParams', () => {
  // 用对象管理所有全局参数
  const params = reactive<{ [key: string]: any }>({})

  function setParam(key: string, value: any) {
    params[key] = value
  }

  function getParam(key: string) {
    return params[key]
  }

  function removeParam(key: string) {
    delete params[key]
  }

  return { params, setParam, getParam, removeParam }
})
