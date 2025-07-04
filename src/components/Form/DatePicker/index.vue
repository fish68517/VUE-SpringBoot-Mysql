<template>
    <n-form label-placement="left">
        <n-form-item :label="label">
            <n-date-picker v-model:value="_value" :placeholder="placeholder" type="date" :clearable="true"/>
        </n-form-item>
    </n-form>
</template>

<script setup lang="ts">
import {computed} from 'vue'

interface Props {
    label: string
    value: number | null
    placeholder?: string
    sign?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    placeholder: '请选择',
    sign: false
})

const emit = defineEmits<{
    (e: 'update:value', val: number | null): void
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

  :deep(.n-space) {
    width: 100%;

    div:first-child {
      width: 100%
    }
  }

  :deep(.n-form-item-blank) {
    justify-content: flex-start;
  }

  :deep(.n-input-wrapper) {
    width: 100%;
    height: 30px;
    background: #10151f;
    border: 1px solid #222831;
    font-size: 12px;
  }

    :deep(.n-date-picker){
        width: 100%
    }
    :deep(.n-input__placeholder){
        color: rgba(255, 255, 255, 0.82);
    }
}
</style>
