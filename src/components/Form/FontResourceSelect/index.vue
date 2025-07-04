<template>
  <n-form label-placement="left">
    <n-form-item :label="label">
      <n-select v-model:value="_value" :placeholder="placeholder" :options="options" />
      <div class="icon">
        <i class="iconfont icon-peizhi-xieti"></i>
      </div>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface Props {
  label: string
  value: string | number | null
  placeholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '请选择',
  sign: false
})
const token = window.localStorage.getItem('token')
const baseUrl = window.location.href.includes('localhost') ? 'http://coolv.ctyun-devops.com/api/' : '/api/'

const options = ref<any>([])
const emit = defineEmits<{
  (e: 'update:value', val: string | number | null): void
}>()

const _value = computed({
  get: () => props.value,
  set: val => {
    emit('update:value', val)
  }
})

const elWidth = computed(() => {
  return props.sign ? '120px' : '100%'
})
const getOriginOptions = () => {
  let params = new FormData()
  params.append('resourceCategoryType', '0')
  params.append('resourceClassification', 'SVG')

  window
    .fetch(baseUrl + `api/resource/findWeb/999/1`, {
      method: 'post',
      headers: {
        Authorization: token,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        parentId: null,
        resourceCategoryType: 0,
        resourceClassification: 'FONT'
      })
    })
    .then(res => res.json())
    .then(res => {
      // console.log('res', res)
      const { data } = res.data
      options.value = data.map((item: any) => {
        return {
          label: item.resourceName,
          value: item.resourceFile,
          disabled: item.resourceFile === !item.usable
        }
      })
    })
}
getOriginOptions()
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';

.n-form-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  :deep(.n-form-item-blank) {
    justify-content: flex-start;
  }
}
.n-select {
  width: v-bind('elWidth');
}

.icon {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  // background: #212b40;
  border: 1px solid $primary-color;
  margin-left: 10px;
  .iconfont {
    font-size: 12px;
    color: $primary-color;
  }
}
</style>
