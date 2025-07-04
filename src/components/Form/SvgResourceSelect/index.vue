<!--
 * @Author: wangcong
 * @Date: 2023-03-20 08:16:55
 * @LastEditTime: 2024-07-01 15:33:45
 * @LastEditors: chenlong2
 * @Description: 
-->
<template>
  <n-form :label-placement="labelPlacement">
    <n-form-item :label="label">
      <!-- <n-input v-model:value="_value" :type="type" :placeholder="placeholder" @blur="handleBlur" @keydown.stop /> -->
      <n-cascader
        v-model:value="_value"
        :placeholder="placeholder"
        :options="options"
        check-strategy="all"
        :show-path="true"
        remote
        :on-load="handleLoad"
        @update:value="getCheckedData"
      />
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { computed, ref, useAttrs } from 'vue'
import axios from 'axios'
import { Label } from '@vicons/carbon'

interface Props {
  label: string
  labelPlacement?: string
  value?: string
  placeholder?: string
  compMarginBottom?: string
  type?: string
  svgUrl: string
}
const props = withDefaults(defineProps<Props>(), {
  labelPlacement: 'left',
  placeholder: '请选择',
  compMarginBottom: '10px',
  type: 'text'
})
const svgUrl = ref('')
const emit = defineEmits<{
  (e: 'update:value', val: string): void
  (e: 'update:svgUrl', val: string): void
  (e: 'on-blur', val: string): void
}>()
const getCheckedData = (value, option) => {
  svgUrl.value = baseUrl + 'api/download/' + option.url
  console.log('svgUrl.value', svgUrl.value)
  emit('update:svgUrl', svgUrl.value)
}
const _value = computed({
  get: () => props.value,
  set: val => {
    emit('update:value', val)
  }
})
const token = window.localStorage.getItem('token')
const baseUrl = window.location.href.includes('localhost') ? 'http://coolv.ctyun-devops.com/api/' : '/api/'

const options = ref<any>([])
const handleLoad = (option: any) => {
  return new Promise<void>(resolve => {
    getChildren(option).then(res => {
      option.children = res
    })
    resolve()
  })
}
const getAllKidFn = data => {
  data?.forEach(item => {
    ;(item.label = item.resourceName),
      (item.value = item.id),
      (item.url = item.resourceFile),
      (item.isLeaf = item.children.length > 0 ? false : true),
      (item.disabled = !item.usable)
    if (item.children.length > 0) {
      getAllKidFn(item.children)
    }
  })
  return data
}
const getChildren = async (option: any) => {
  const a = window
    .fetch(baseUrl + `api/resource/findWeb/999/1`, {
      method: 'post',
      headers: {
        Authorization: token,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        parentId: option.value,
        resourceCategoryType: 0,
        resourceClassification: 'SVG'
      })
    })
    .then(res => res.json())
    .then(res => {
      // console.log('res', res)
      const { data } = res.data

      return getAllKidFn(data)
    })
  // console.log('a', a)
  return a
}

const getOriginOptions = () => {
  let params = new FormData()
  params.append('resourceCategoryType', '0')
  params.append('resourceClassification', 'SVG')

  window
    .fetch(baseUrl + `api/resource/findMenuTreeWeb?resourceCategoryType=0&resourceClassification=SVG`, {
      method: 'get',
      headers: {
        Authorization: token
      }
    })
    .then(res => res.json())
    .then(res => {
      // console.log('res', res)
      options.value = res.data.map((item: any) => {
        return {
          label: item.resourceName,
          value: item.id || '',
          url: item.resourceFile,
          children: item.children.map((child: any) => {
            return {
              label: child.resourceName,
              value: child.id,
              url: item.resourceFile,
              isLeaf: false
            }
          })
        }
      })
      // console.log('options', options.value)
    })
}
getOriginOptions()
const handleBlur = () => {
  emit('on-blur', _value.value)
}
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.n-form-item {
  :deep(.n-form-item-feedback-wrapper) {
    min-height: v-bind('compMarginBottom');
  }
}
</style>
