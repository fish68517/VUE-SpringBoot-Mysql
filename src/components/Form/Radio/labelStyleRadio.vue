<!--
 * @Author: wangcong
 * @Date: 2023-07-14 15:22:39
 * @LastEditTime: 2023-07-25 15:54:42
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <n-form label-placement="left" label-width="70">
    <n-form-item :label="label">
      <n-radio-group v-model:value="_value" name="radiogroup">
        <n-radio value="all">全部展示</n-radio>
        <n-radio value="wordspace">
          超过
          <n-input-number v-model:value="_number" :min="1">
            <template #minus-icon>
              <i class="iconfont icon-jianshao"></i>
            </template>
            <template #add-icon>
              <i class="iconfont icon-zengjia"></i>
            </template>
          </n-input-number>
          个字换行
        </n-radio>
        <n-radio value="ellisps">
          超过
          <n-input-number v-model:value="_number" :min="1">
            <template #minus-icon>
              <i class="iconfont icon-jianshao"></i>
            </template>
            <template #add-icon>
              <i class="iconfont icon-zengjia"></i>
            </template>
          </n-input-number>
          个字显示...
        </n-radio>
      </n-radio-group>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  label: string
  value: string
  number: number
}

const props = withDefaults(defineProps<Props>(), {})

const emit = defineEmits<{
  (e: 'update:value', value: string): void
  (e: 'update:number', value: number): void
}>()

const _value = computed({
  get: () => props.value,
  set: newVal => {
    emit('update:value', newVal)
  }
})
const _number = computed({
  get: () => props.number,
  set: newVal => {
    emit('update:number', newVal)
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.n-input-number {
  width: 60px;
  position: relative;
  z-index: 1;
}
.n-radio-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.n-radio {
  height: 30px;
  line-height: 30px;
  :deep(.n-radio__dot) {
    &::before {
      top: -5px;
    }
  }
}
:deep(.n-radio__label) {
  display: flex;
  align-items: center;
  padding: 0 0 0 5px;
  gap: 5px;
}
</style>
