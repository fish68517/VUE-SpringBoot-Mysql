<template>
  <n-space>
    <n-text>{{ label }}</n-text>
    <div class="right">
      <n-radio :checked="checkedValue === '全部展示'" value="全部展示" label="全部展示" @change="handleChange" />
      <n-radio :checked="checkedValue === 'wordspace'" value="wordspace" @change="handleChange">
        超过
        <n-input-number v-model:value="val1" :min="0" size="small" placeholder="px"> </n-input-number>
        个字换行展示
      </n-radio>
      <n-radio :checked="checkedValue === 'ellisps'" value="ellisps" @change="handleChange">
        超过
        <n-input-number v-model:value="val2" :min="0" size="small" placeholder="px"> </n-input-number>
        个字以...代替
      </n-radio>
    </div>
  </n-space>
</template>

<script setup lang="ts">
defineProps<{
  label: string
}>()
const checkedValue = ref('全部展示')
const handleChange = (e: Event) => {
  checkedValue.value = (e.target as HTMLInputElement).value
  console.log(checkedValue.value)
}
const val1 = ref(5)
const val2 = ref(5)
</script>

<style lang="scss" scoped>
.n-space {
  align-items: flex-start;
}
.n-text {
  display: inline-block;
  width: 70px;
  font-size: 12px;
  color: #808792;
}
.right {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 216px;
}
:deep(.n-radio__label) {
  display: flex;
  font-size: 12px;
  align-items: center;
}
.n-input-number {
  width: 80px;
  position: relative;
  z-index: 1000000;
}
</style>
