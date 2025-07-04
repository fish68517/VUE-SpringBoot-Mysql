<!--
 * @Author: wangcong
 * @Date: 2023-09-18 15:53:52
 * @LastEditTime: 2023-10-25 09:56:48
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <n-form label-placement="left">
    <n-form-item :label="label">
      <n-radio-group v-model:value="_value" name="radiogroup">
        <n-radio v-for="item in options" :key="item.value" :value="item.value">
          {{ item.label }}
        </n-radio>
      </n-radio-group>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { OptionsInter } from "./index.d";

interface Props {
  label: string;
  value: string | boolean;
  options: OptionsInter[];
}

const props = withDefaults(defineProps<Props>(), {});

const emit = defineEmits<{
  (e: "update:value", value: string | boolean): void;
}>();

const _value = computed({
  get: () => props.value,
  set: (newVal) => {
    emit("update:value", newVal);
  },
});
</script>

<style lang="scss" scoped>
@import "@/styles/pages/form.scss";
.n-radio-group {
  display: flex;
  gap: 12px 0;
  flex-wrap: wrap;
}
</style>
