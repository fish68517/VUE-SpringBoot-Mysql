<!--
 * @Author: wangcong
 * @Date: 2023-03-20 08:16:55
 * @LastEditTime: 2023-03-28 16:19:28
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <n-form :label-placement="labelPlacement">
    <n-form-item :label="label">
      <n-input
        v-model:value="_value"
        :type="type"
        :placeholder="placeholder"
        @blur="handleBlur"
        @keydown.stop
      />
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { computed } from "vue";

interface Props {
  label: string;
  labelPlacement?: string;
  value: string;
  placeholder?: string;
  compMarginBottom?: string;
  type?: string;
}
const props = withDefaults(defineProps<Props>(), {
  labelPlacement: "top",
  placeholder: "请输入",
  compMarginBottom: "10px",
  type: "text",
});

const emit = defineEmits<{
  (e: "update:value", val: string): void;
  (e: "on-blur", val: string): void;
}>();

const _value = computed({
  get: () => props.value,
  set: (val) => {
    emit("update:value", val);
  },
});

const handleBlur = () => {
  emit("on-blur", _value.value);
};
</script>

<style lang="scss" scoped>
@import "@/styles/pages/form.scss";
.n-form-item {
  :deep(.n-form-item-feedback-wrapper) {
    min-height: v-bind("compMarginBottom");
  }
}
</style>
