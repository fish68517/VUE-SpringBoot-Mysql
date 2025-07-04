<template>
  <n-form inline>
    <n-form-item :label="label">
      <n-input-number v-model:value="_val1" :min="min" :max="max" :disabled="_isLock" @update:value="change1">
        <template #prefix>
          <n-text depth="3">{{ prefix1 }}</n-text>
        </template>
        <template #minus-icon>
          <i class="iconfont icon-jianshao"></i>
        </template>
        <template #add-icon>
          <i class="iconfont icon-zengjia"></i>
        </template>
      </n-input-number>
      <div class="lock" :class="{ active: _isLock }" @click="lock">
        <i class="iconfont icon-bilisuoding" />
      </div>
      <n-input-number v-model:value="_val2" :min="min" :max="max" :disabled="_isLock" @update:value="change2">
        <template #prefix>
          <n-text depth="3">{{ prefix2 }}</n-text>
        </template>
        <template #minus-icon>
          <i class="iconfont icon-jianshao"></i>
        </template>
        <template #add-icon>
          <i class="iconfont icon-zengjia"></i>
        </template>
      </n-input-number>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  isLock: boolean
  label: string
  val1: number
  val2: number
  min?: number
  max?: number
  prefix1: string
  prefix2: string
  elWidth?: string
  prefixMarginRight?: string
}
const props = withDefaults(defineProps<Props>(), {
  min: 0,
  max: 99999,
  elWidth: '110px',
  prefixMarginRight: '20px'
})

const emit = defineEmits<{
  (e: 'update:isLock', val: boolean): void
  (e: 'update:val1', val: number): void
  (e: 'update:val2', val: number): void
  (e: 'change'): void
}>()

const _isLock = ref<boolean | null>(null)
const _val1 = ref(0)
const _val2 = ref(0)

watch(
  () => [props.isLock, props.val1, props.val2],
  newVal => {
    _isLock.value = newVal[0] as boolean
    _val1.value = newVal[1] as number
    _val2.value = newVal[2] as number
  },
  { immediate: true }
)

const lock = () => {
  _isLock.value = !_isLock.value
  emit('update:isLock', _isLock.value!)
}
const change1 = (val: number) => {
  emit('update:val1', val)
  emit('change')
}
const change2 = (val: number) => {
  emit('update:val2', val)
  emit('change')
}
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';

.n-input-number {
  width: v-bind('elWidth');
}
:deep(.n-input__prefix) {
  margin-right: v-bind('prefixMarginRight');
}
.lock {
  display: grid;
  place-items: center;
  width: 20px;
  height: 20px;
  border-radius: 2px;
  &.active {
    background: $primary-color;
  }
}
</style>
