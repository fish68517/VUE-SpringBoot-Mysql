<!--
 * @Author: wangcong
 * @Date: 2023-08-16 16:58:19
 * @LastEditTime: 2024-06-07 15:54:23
 * @LastEditors: kaix
 * @Description: 
-->
<template>
  <div v-if="color" class="seriesColor">
    <div class="title">
      <span>序号{{ seriesIndex }}</span>
    </div>
    <template v-if="!isGradient">
      <NewColorPicker v-bind="$attrs" label-placement="left" label="文本颜色" v-model:value="_color" />
    </template>
  </div>
</template>

<script lang="ts" setup>
import { NewColorPicker } from "@/components/Form";
import { computed, ref, watch, PropType } from "vue";

const props = defineProps({
  color: {
    type: String as PropType<any>,
    default: "",
  },
  seriesIndex: {
    type: Number,
    default: 0,
  },
});

const emits = defineEmits(["update:color"]);

const _color = computed({
  get: () => {
    if (typeof props.color === "string") {
      return props.color;
    } else {
      return props.color.colorStops[0].color;
    }
  },
  set: (val) => {
    emits("update:color", val);
  },
});

const isGradient = ref(false);
const startColor = ref("");
const endColor = ref("");
watch(
  () => isGradient.value,
  (val) => {
    if (val) {
      if (typeof props.color === "string") {
        startColor.value = props.color;
        endColor.value = props.color;
      } else {
        startColor.value = props.color.colorStops[0].color;
        endColor.value = props.color.colorStops[1].color;
      }
    } else {
      endColor.value = "";
      emits("update:color", startColor.value);
    }
  }
);
watch(
  () => [startColor.value, endColor.value],
  ([color1, color2]) => {
    const color: any = {
      type: "linear",
      x: 0, // 渐变起点
      y: 0, // 渐变起点
      x2: 0, // 渐变终点
      y2: 1, // 渐变终点
      colorStops: [
        {
          offset: 0,
          color: color1, // 0% 处的颜色
        },
        {
          offset: 1,
          color: color2, // 100% 处的颜色
        },
      ],
    };
    if (isGradient.value) {
      emits("update:color", color);
    }
  }
);

watch(
  () => props.color,
  (val) => {
    if (typeof val === "string") {
      startColor.value = props.color;
      isGradient.value = false;
    } else {
      isGradient.value = true;
    }
  },
  {
    immediate: true,
    deep: true,
  }
);
</script>

<style lang="scss" scoped>
$primary-color: var(--primary-color);
.seriesColor {
  .title {
    font-size: 12px;
    // color: #fff;
    display: flex;
    align-items: center;
    margin-bottom: 5px;
    .switch {
      display: flex;
      align-items: center;
      margin-left: auto;
      .label {
        font-size: 12px;
        margin-right: 5px;
      }
      .n-switch {
        max-width: 40px;
        height: 20px;
        :deep(.n-switch__rail) {
          height: 18px;
          min-width: 38px;
          .n-switch__button {
            width: 15px;
            height: 15px;
            background: #808792;
          }
        }
        &.n-switch--active {
          :deep(.n-switch__rail) {
            // background: #212b40;
            border: 1px solid $primary-color;
            .n-switch__button {
              top: 1.5px;
              left: 22px;
              background: var(--n-button-color);
            }
          }
        }
      }
      :deep(.n-switch__rail) {
        // background: #10151f;
        // border: 1px solid #222831;
      }
    }
  }
}
</style>
