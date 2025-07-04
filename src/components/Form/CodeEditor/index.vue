<!--
 * @Author: kaix
 * @Date: 2023-04-25 08:28:42
 * @LastEditTime: 2023-04-27 08:56:41
 * @LastEditors: kaix
 * @Description: 
-->
<template>
  <v-ace-editor v-model:value="_value" lang="json" theme="monokai" style="height: 300px" />
</template>

<script setup lang="ts">
import { ref, defineProps, computed } from 'vue'
// import { VAceEditor } from 'vue3-ace-editor'
import './ace.config'

interface Props {
  value: string
}

const props = withDefaults(defineProps<Props>(), {})

const emit = defineEmits<{
  (e: 'update:value', value: string): void
}>()

// JSON格式化
const jsonFormat = (data: string) => {
  return JSON.stringify(JSON.parse(data), null, 2)
}

const _value = computed({
  get: () => jsonFormat(props.value),
  set: value => {
    emit('update:value', value!)
  }
})
</script>
