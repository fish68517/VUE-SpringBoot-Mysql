<!--
 * @Author: wangcong
 * @Date: 2023-08-21 10:43:40
 * @LastEditTime: 2023-08-30 11:15:32
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <div class="colorSpace">
    <div class="colors">
      <div class="left">
        <div class="color bg1" />
        <div class="color bg2" ref="$sv" @click.self="changeSaturationAndValue" />
        <div
          @mousedown="changeSV"
          class="switch"
          :style="{
            top: finallySwitchTop,
            left: finallySwitchLeft,
          }"
          ref="$switch"
        />
      </div>
      <div class="right" ref="$hue" @click.self="changeHue">
        <div
          class="arrow"
          @mousedown="changeH"
          ref="$arrow"
          :style="{ top: finallyArrowTop }"
        />
      </div>
    </div>
    <eye-dropper
      v-model:value="_value"
      @changeCurName="handleChangeCurName"
      @changeAlpha="handleChangeAlpha"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, PropType, computed, watch, onMounted } from "vue";
import eyeDropper from "./eyeDropper.vue";
import { ColorType } from "./color.d";
import Color from "color";

const props = defineProps({
  value: {
    type: String as PropType<string | null>,
    default: null,
  },
});

const emits = defineEmits(["update:value"]);

const _value = computed({
  get: () => props.value,
  set: (val) => {
    emits("update:value", val);
  },
});

const curName = ref<ColorType | null>();
const alpha = ref(1);
const handleChangeCurName = (val: ColorType) => {
  curName.value = val;
};
const handleChangeAlpha = (val: number) => {
  alpha.value = val;
};

const h = ref(0);
const s = ref(0);
const v = ref(0);
const bgColor = ref("");

const moveFlag = ref(false);
const offsetY = ref(0);
const $arrow = ref<HTMLDivElement | null>(null);
const arrowTop = ref(-5);
const $hue = ref<HTMLDivElement | null>(null);
const changeH = (e: MouseEvent) => {
  document.querySelector("body")!.style.cursor = "pointer";
  moveFlag.value = true;
  const startY = e.y;
  function moveSwitch(event: MouseEvent) {
    const currentY = event.y;
    if (moveFlag.value) {
      offsetY.value = currentY - startY;
    }
  }
  document.addEventListener("mousemove", moveSwitch);
  document.addEventListener("mouseup", (e) => {
    if ($arrow.value?.offsetTop || $arrow.value?.offsetTop === 0) {
      offsetY.value = 0;
      arrowTop.value = $arrow.value?.offsetTop as number;
    }
    moveFlag.value = false;
    document.removeEventListener("mousemove", moveSwitch);
    document.querySelector("body")!.style.cursor = "default";
  });
};

const finallyArrowTop = computed(() => {
  let top = -5;
  if ($hue.value && $arrow.value) {
    const maxOffsetY = $hue.value!.offsetHeight - $arrow.value!.offsetHeight / 2;
    const minOffsetY = -($arrow.value!.offsetHeight / 2);
    top = arrowTop.value + offsetY.value;
    if (top > maxOffsetY) {
      top = maxOffsetY;
    } else if (top <= minOffsetY) {
      top = minOffsetY;
    }
  }
  return top + "px";
});

watch(
  () => finallyArrowTop.value,
  (val) => {
    const offsetTop = parseInt(val) + 5;
    h.value = (offsetTop / 280) * 360;
  }
);

const $sv = ref<HTMLDivElement | null>(null);
const $switch = ref<HTMLDivElement | null>(null);
const offsetS = ref(0); // x
const offsetV = ref(0); // y
const switchLeft = ref(0);
const switchTop = ref(0);
const changeSV = (e: MouseEvent) => {
  document.querySelector("body")!.style.cursor = "pointer";
  const startX = e.x;
  const startY = e.y;
  moveFlag.value = true;
  function moveSwitch(event: MouseEvent) {
    const currentY = event.y;
    const currentX = event.x;
    if (moveFlag.value) {
      offsetS.value = currentX - startX;
      offsetV.value = currentY - startY;
    }
  }
  document.addEventListener("mousemove", moveSwitch);
  document.addEventListener("mouseup", (e) => {
    if ($switch.value?.offsetTop || $switch.value?.offsetTop === 0) {
      offsetV.value = 0;
      switchTop.value = $switch.value?.offsetTop as number;
    }
    if ($switch.value?.offsetLeft || $switch.value?.offsetLeft === 0) {
      offsetS.value = 0;
      switchLeft.value = $switch.value?.offsetLeft as number;
    }
    moveFlag.value = false;
    document.removeEventListener("mousemove", moveSwitch);
    document.querySelector("body")!.style.cursor = "default";
  });
};

const finallySwitchTop = computed(() => {
  let top = 0;
  if ($sv.value && $switch.value) {
    const maxOffsetY = $sv.value!.offsetHeight;
    const minOffsetY = 0;
    top = switchTop.value + offsetV.value;
    if (top > maxOffsetY) {
      top = maxOffsetY;
    } else if (top <= minOffsetY) {
      top = minOffsetY;
    }
  }
  return top + "px";
});

const finallySwitchLeft = computed(() => {
  let left = 0;
  if ($sv.value && $switch.value) {
    const maxOffsetX = $sv.value!.offsetWidth;
    const minOffsetX = 0;
    left = switchLeft.value + offsetS.value;
    if (left > maxOffsetX) {
      left = maxOffsetX;
    } else if (left <= minOffsetX) {
      left = minOffsetX;
    }
  }
  return left + "px";
});

watch(
  () => [finallySwitchLeft.value, finallySwitchTop.value],
  ([finallyS, finallyV]) => {
    const _s = parseInt(finallyS);
    const _v = parseInt(finallyV);
    s.value = Math.round((_s / 280) * 100);
    v.value = 100 - Math.round((_v / 280) * 100);
  }
);

const changeHue = (e: MouseEvent) => {
  const maxOffsetY = $hue.value!.offsetHeight - $arrow.value!.offsetHeight / 2;
  const minOffsetY = -($arrow.value!.offsetHeight / 2);
  const offsetY = e.offsetY;
  if (offsetY > maxOffsetY) {
    arrowTop.value = maxOffsetY;
  } else if (offsetY <= minOffsetY) {
    arrowTop.value = minOffsetY;
  } else {
    arrowTop.value = offsetY - $arrow.value!.offsetHeight / 2;
  }
};

const changeSaturationAndValue = (e: MouseEvent) => {
  const maxX = $sv.value!.offsetWidth;
  const minX = 0;
  const maxY = $sv.value!.offsetHeight;
  const minY = 0;
  const offsetY = e.offsetY;
  const offsetX = e.offsetX;

  if (offsetY > maxY) {
    switchTop.value = maxY;
  } else if (offsetY <= minY) {
    switchTop.value = minY;
  } else {
    switchTop.value = offsetY;
  }

  if (offsetX > maxX) {
    switchLeft.value = maxX;
  } else if (offsetX <= minX) {
    switchLeft.value = minX;
  } else {
    switchLeft.value = offsetX;
  }
};

const emitColor = (hsvArray: number[]) => {
  let color = "";
  switch (curName.value) {
    case ColorType.HEX: {
      let hex = Color(hsvArray, "hsv")!.hex();
      hex += Math.round(alpha.value * 255)
        .toString(16)
        .padStart(2, "0");
      color = hex.toUpperCase();
      break;
    }
    case ColorType.RGB: {
      const hex = Color(hsvArray, "hsv")!.hex();
      const { r, g, b } = Color(hex, "rgb")!.object();
      color = `rgba(${r}, ${g}, ${b}, ${alpha.value})`;
      break;
    }
  }
  emits("update:value", color);
};

onMounted(() => {
  watch(
    () => props.value,
    (val) => {
      if (val) {
        if (!moveFlag.value) {
          //@ts-ignore
          const color = Color(props.value!).hsv().color;
          h.value = color[0];
          s.value = color[1];
          v.value = color[2];
          arrowTop.value = (h.value / 360) * 280 - 5;
          switchLeft.value = (s.value / 100) * 280;
          switchTop.value = ((100 - v.value) / 100) * 280;
        }
      }
    },
    { immediate: true }
  );

  watch(
    () => h.value,
    (val) => {
      bgColor.value = Color([val, 100, 100], "hsv")!.hex();
    },
    { immediate: true }
  );

  watch(
    () => [h.value, s.value, v.value],
    ([h, s, v]) => {
      emitColor([h, s, v]);
    }
  );
});
</script>

<style scoped lang="scss">
.colorSpace {
  * {
    box-sizing: border-box;
  }
  .colors {
    display: flex;
    align-items: center;
    margin-bottom: 11px;
    .left {
      width: 280px;
      height: 280px;
      position: relative;
      .color {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        &:hover {
          cursor: pointer;
        }
        &.bg1 {
          background-image: linear-gradient(90deg, white, v-bind("bgColor"));
        }
        &.bg2 {
          background-image: linear-gradient(rgba(0, 0, 0, 0), rgb(0, 0, 0));
        }
      }
      .switch {
        width: 16px;
        height: 16px;
        border-radius: 50%;
        border: 2px solid #fff;
        box-shadow: 0 0 2px 0 rgba(0, 0, 0, 0.45);
        position: absolute;
        transform: translate(-50%, -50%);
        z-index: 999;
        &:hover {
          cursor: pointer;
        }
      }
    }
    .right {
      margin-left: 20px;
      height: 280px;
      width: 20px;
      background-image: linear-gradient(
        180deg,
        rgb(255, 0, 0) 0%,
        rgb(255, 255, 0) 16.66%,
        rgb(0, 255, 0) 33.33%,
        rgb(0, 255, 255) 50%,
        rgb(0, 0, 255) 66.66%,
        rgb(255, 0, 255) 83.33%,
        rgb(255, 0, 0) 100%
      );
      position: relative;
      &:hover {
        cursor: pointer;
      }
      .arrow {
        top: -5px;
        left: 50%;
        transform: translateX(-50%);
        position: absolute;
        width: 41px;
        height: 10px;
        background: transparent;
        &:hover {
          cursor: pointer;
        }
        &::before {
          content: "";
          position: absolute;
          width: 0;
          height: 0;
          display: block;
          top: 0;
          left: 0;
          border-top: 5px solid transparent;
          border-right: 6px solid transparent;
          border-bottom: 5px solid transparent;
          border-left: 6px solid #fff;
        }
        &::after {
          content: "";
          position: absolute;
          width: 0;
          height: 0;
          display: block;
          top: 0;
          right: 0;
          border-top: 5px solid transparent;
          border-right: 6px solid #fff;
          border-bottom: 5px solid transparent;
          border-left: 6px solid transparent;
        }
      }
    }
  }
}
</style>
