<template>
  <n-form label-placement="left">
    <n-form-item :label="label">
      <n-select
        v-model:value="_value"
        :placeholder="placeholder"
        :options="options"
        :multiple="multiple"
        :filterable="filterable"
        :clearable="clearable"
      />
      <div class="icon" v-if="sign">
        <i class="iconfont icon-peizhi-xieti"></i>
      </div>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { OptionsInter } from './index.d'

interface Props {
  clearable?: boolean
  label: string
  value: string | number | null
  placeholder?: string
  options: OptionsInter[]
  sign?: boolean
  multiple?: boolean //是否 多选
  filterable?: boolean //搜索
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '请选择',
  sign: false
})

const emit = defineEmits<{
  (e: 'update:value', val: string | number | (string | number)[] | null): void // 修改这里，支持数组类型
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
