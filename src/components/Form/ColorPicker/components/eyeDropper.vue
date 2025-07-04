<!--
 * @Author: wangcong
 * @Date: 2023-08-21 14:02:08
 * @LastEditTime: 2024-01-15 16:49:46
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <div class="eyedropper">
    <div class="tool">
      <div class="icon" @click="handleEyeDropper">
        <img :src="eyeDropperIcon" alt="" />
      </div>
      <div class="opacity">
        <div
          class="background"
          :style="{ background }"
          ref="$dropperAlpha"
          @click.self="handleClick($event)"
        >
          <div
            class="switch"
            @mousedown="changeAlpha"
            :style="{
              left: finallySwitchLeft,
              background: switchColor,
            }"
            ref="$switch"
          />
        </div>
        <div class="progress" />
      </div>
    </div>
    <div class="colorName">
      <n-select class="nameType" :options="nameOptions" v-model:value="curName" />
      <template v-if="curName === ColorType.HEX">
        <n-input
          class="text"
          v-model:value="valueText"
          @blur="getColorText(ColorType.HEX, true, valueText)"
        />
      </template>
      <template v-else>
        <n-input-number
          class="number"
          :show-button="false"
          v-model:value="r"
          :min="0"
          :max="255"
          @blur="
            getColorText(ColorType.RGB, true, `rgba(${r}, ${g}, ${b}, ${alpha / 100})`)
          "
        />
        <n-input-number
          class="number"
          :show-button="false"
          v-model:value="g"
          :min="0"
          :max="255"
          @blur="
            getColorText(ColorType.RGB, true, `rgba(${r}, ${g}, ${b}, ${alpha / 100})`)
          "
        />
        <n-input-number
          class="number"
          :show-button="false"
          v-model:value="b"
          :min="0"
          :max="255"
          @blur="
            getColorText(ColorType.RGB, true, `rgba(${r}, ${g}, ${b}, ${alpha / 100})`)
          "
        />
      </template>
      <div class="alpha">
        <n-input-number
          v-model:value="alpha"
          :show-button="false"
          :min="0"
          :max="100"
          :on-update:value="getOffsetX"
        />
        <div class="suffix">
          <span class="unit">%</span>
          <div class="icons">
            <div class="btn">
              <customIcon
                icon="zengjia"
                :width="16"
                :height="8"
                color="#808792"
                @click="handleAlpha(1)"
              />
            </div>
            <div class="btn">
              <customIcon
                icon="jianshao"
                :width="16"
                :height="8"
                color="#808792"
                @click="handleAlpha(-1)"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { watch, PropType, ref, computed, onMounted } from "vue";
import { ColorType } from "./color.d";
import Color from "color";

const props = defineProps({
  value: {
    type: String as PropType<string | null>,
    default: () => null,
  },
});

const emits = defineEmits(["update:value", "changeCurName", "changeAlpha"]);

const getOffsetX = (value: number) => {
  const a = value / 100;
  const maxOffsetX = $dropperAlpha.value!.offsetWidth - $switch.value!.offsetWidth;
  switchLeft.value = a * maxOffsetX;
};

const alpha = ref(0);
const background = ref("");
const switchColor = ref("");
const valueText = ref("");
const r = ref(0);
const g = ref(0);
const b = ref(0);

const getColorText = (
  val: ColorType | null,
  emitColorFlag: boolean = false,
  color: string | null = props.value
) => {
  if (!val || !color) return;
  try {
    switch (val) {
      case ColorType.HEX: {
        let hex = Color(color, "hex")!.hex();
        hex += Math.round((alpha.value / 100) * 255)
          .toString(16)
          .padStart(2, "0");
        valueText.value = hex.toUpperCase();
        if (emitColorFlag) {
          emits("update:value", valueText.value);
        }
        break;
      }
      case ColorType.RGB: {
        const { r: red, g: green, b: blue } = Color(color, "rgb")!.object();
        r.value = red;
        g.value = green;
        b.value = blue;
        if (emitColorFlag) {
          emits(
            "update:value",
            `rgba(${r.value}, ${g.value}, ${b.value}, ${alpha.value / 100})`
          );
        }
        break;
      }
    }
  } catch (e) {
    window.$message.error("请输入正确的颜色格式");
    console.log(e);
  }
};

onMounted(() => {
  watch(
    () => props.value,
    async (val: any) => {
      let r = 0;
      let g = 0;
      let b = 0;
      let a = 1;
      try {
        if (val.includes("#")) {
          curName.value = ColorType.HEX;
        } else if (val.includes("rgb")) {
          curName.value = ColorType.RGB;
        }
        const { r: red, g: green, b: blue } = Color(val, "rgb")!.object();
        r = red;
        g = green;
        b = blue;
        a = Color(val, "rgb")!.alpha() as number;
      } catch (e) {
        console.log(e);
        curName.value = ColorType.RGB;
      }
      const colorLeft = `rgba(${r}, ${g}, ${b}, 0)`;
      const colorRight = `rgba(${r}, ${g}, ${b}, 1)`;
      background.value = `linear-gradient(to right, ${colorLeft}, ${colorRight})`;
      switchColor.value = `rgba(${r}, ${g}, ${b}, ${a})`;
      if (!moveFlag.value) {
        alpha.value = Math.round(a * 100);
        getOffsetX(alpha.value);
      }
      getColorText(curName.value);
    },
    { immediate: true }
  );

  watch(
    () => curName.value,
    (val) => {
      emits("changeCurName", val);
      getColorText(val, true);
    },
    { immediate: true }
  );
});

const eyeDropperIcon = `data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAARpJREFUOE+lkr0uRFEURtdXSXQSWk/gIZBIppCIwhMohwatByAqimk04gEkGo2IQicqEZ5Bo/IzTHyyJ+dM7lzu3DtxkptT3Ky1v73PFmMc2zPABdAFliW9aBRvex7oSbpJ8BUwl5g7YKlSYHsWeAK+gTVgvwDnuud1CbaBA+AtfdOlxCd1gkNgM0HvwCuQJTGL1VEtZPgZOAW2gI8kue3DUvdPge0ivCjpwfYxsJ5aWZF0Gcl+CSrgFnAGTKR2YiateJ0hge0jYAOI2LlyEW4Dk8BO+v84EDSBJXUige2pWKJBC+PA5cXTf+B+AttO1gVJ17aHes6xq1a+KLgH9oB4rph2uw4uJ8hFIlEjOAt6wBfwme7dJpVztR8OEpZux+bbkgAAAABJRU5ErkJggg==`;

const handleEyeDropper = () => {
  if (!window.EyeDropper) {
    window.$message.error("该环境不支持颜色吸取");
    return;
  }
  const eyeDropper = new window.EyeDropper();
  eyeDropper
    .open()
    .then((res: { sRGBHex: string }) => {
      const { sRGBHex } = res;
      alpha.value = 100;
      getColorText(curName.value, true, sRGBHex);
    })
    .catch(() => {});
};

const $dropperAlpha = ref<HTMLDivElement | null>(null);
const $switch = ref<HTMLDivElement | null>(null);
const switchLeft = ref(0);
const offsetX = ref(0);
const moveFlag = ref(false);

const changeAlpha = (e: MouseEvent) => {
  document.querySelector("body")!.style.cursor = "pointer";
  moveFlag.value = true;
  const startX = e.x;
  function moveSwitch(event: MouseEvent) {
    const currentX = event.x;
    if (moveFlag.value) {
      offsetX.value = currentX - startX;
    }
  }
  document.addEventListener("mousemove", moveSwitch);
  document.addEventListener("mouseup", (e) => {
    if ($switch.value?.offsetLeft || $switch.value?.offsetLeft === 0) {
      offsetX.value = 0;
      switchLeft.value = $switch.value?.offsetLeft as number;
    }
    moveFlag.value = false;
    document.removeEventListener("mousemove", moveSwitch);
    document.querySelector("body")!.style.cursor = "default";
  });
};

const finallySwitchLeft = computed(() => {
  let left = 0;
  if ($dropperAlpha.value && $switch.value) {
    const maxOffsetX = $dropperAlpha.value!.offsetWidth - $switch.value!.offsetWidth;
    const minOffsetX = 0;
    left = switchLeft.value + offsetX.value;
    if (left > maxOffsetX) {
      left = maxOffsetX;
    } else if (left <= minOffsetX) {
      left = 0;
    }
  }
  return left + "px";
});

watch(
  () => finallySwitchLeft.value,
  (val) => {
    if ($dropperAlpha.value && $switch.value) {
      const maxOffsetX = $dropperAlpha.value!.offsetWidth - $switch.value!.offsetWidth;
      const left = parseFloat(val);
      alpha.value = Math.round((left / maxOffsetX) * 100);
    } else {
      alpha.value = 0;
    }
  }
);

const curName = ref<ColorType | null>(null);
const nameOptions = [
  { label: "HEX", value: ColorType.HEX },
  { label: "RGB", value: ColorType.RGB },
];

const handleAlpha = async (num: number) => {
  alpha.value += num;
  if (alpha.value >= 100) {
    alpha.value = 100;
  } else if (alpha.value <= 0) {
    alpha.value = 0;
  }
};

watch(
  () => alpha.value,
  (val) => {
    let color = "";
    switch (curName.value) {
      case ColorType.HEX: {
        color = Color(valueText.value)!.hex();
        color += Math.round((val / 100) * 255)
          .toString(16)
          .padStart(2, "0")
          .toUpperCase();
        valueText.value = color;
        break;
      }
      case ColorType.RGB: {
        color = `rgba(${r.value}, ${g.value}, ${b.value}, ${val / 100})`;
        break;
      }
    }
    emits("update:value", color);
    emits("changeAlpha", val / 100);
  }
);

const handleClick = (e: MouseEvent) => {
  const maxOffsetX = $dropperAlpha.value!.offsetWidth - $switch.value!.offsetWidth;
  const minOffsetX = $switch.value!.offsetWidth;
  const offsetX = e.offsetX;
  if (offsetX <= minOffsetX) {
    switchLeft.value = 0;
  } else if (offsetX >= maxOffsetX) {
    switchLeft.value = maxOffsetX;
  } else {
    switchLeft.value = offsetX - $switch.value!.offsetWidth / 2;
  }
};
</script>

<style scoped lang="scss">
.eyedropper {
  * {
    user-select: none;
  }
  .tool {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    .icon {
      width: 16px;
      height: 16px;
      &:hover {
        cursor: pointer;
        opacity: 0.8;
      }
      img {
        width: 100%;
        height: 100%;
        -webkit-user-drag: none;
      }
      margin-right: 12px;
    }
    .opacity {
      flex: 1;
      height: 6px;
      position: relative;
      border-radius: 3px;
      border: 1px solid rgba(255, 255, 255, 0.25);
      .background {
        position: absolute;
        top: 0;
        left: 0;
        z-index: 2;
        width: 100%;
        height: 100%;
        border-radius: 3px;
        &:hover {
          cursor: pointer;
        }
        .switch {
          width: 12px;
          height: 12px;
          position: absolute;
          top: 50%;
          transform: translateY(-50%);
          border-radius: 50%;
          border: 2px solid #fff;
        }
      }
      .progress {
        z-index: 1;
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        width: 100%;
        background-image: linear-gradient(
            45deg,
            #404040 25%,
            transparent 0,
            transparent 75%,
            #404040 0
          ),
          linear-gradient(45deg, #404040 25%, transparent 0, transparent 75%, #404040 0);
        background-color: #4a4a4a;
        background-size: 10px 10px;
        background-position: 0 0, 5px 5px;
        border-radius: 3px;
      }
    }
  }
  .colorName {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .nameType {
      width: 80px;
      :deep(.n-base-selection) {
        box-sizing: border-box !important;
        --n-height: 30px !important;
        // --n-color: #10151f !important;
        --n-border-radius: 0 !important;
        // --n-border: 1px solid #222831 !important;
      }
    }
    .text {
      width: 140px;
      box-sizing: border-box !important;
      --n-height: 30px !important;
      // --n-color: #10151f !important;
      --n-border-radius: 0 !important;
      // --n-border: 1px solid #222831 !important;
    }
    .number {
      width: 45px;
      :deep(.n-input) {
        box-sizing: border-box !important;
        --n-height: 30px !important;
        // --n-color: rgba(16, 21, 31, 1) !important;
        // --n-border: 1px solid #222831 !important;
        --n-border-radius: 0 !important;
        --n-padding-right: 12px !important;
        font-size: 12px;
      }
    }
    .alpha {
      width: 80px;
      position: relative;
      .suffix {
        position: absolute;
        display: flex;
        align-items: center;
        top: 0;
        right: 0;
        .unit {
          font-size: 12px;
          font-family: PingFangSC-Regular, PingFang SC;
          font-weight: 400;
          color: #808792;
          line-height: 17px;
          margin-right: 2px;
          user-select: none;
        }
        .icons {
          width: 30px;
          height: 30px;
          .btn {
            width: 100%;
            height: 15px;
            border-left: 1px solid #222831;
            &:nth-child(1) {
              border-bottom: 1px solid #222831;
            }
            display: flex;
            align-items: center;
            justify-content: center;
            &:hover {
              cursor: pointer;
              opacity: 0.8;
            }
          }
        }
        &.disabled {
          &:hover {
            cursor: not-allowed;
          }
          .icons {
            .btn {
              &:hover {
                cursor: not-allowed;
              }
            }
          }
        }
      }
      :deep(.n-input-number) {
        .n-input {
          box-sizing: border-box !important;
          --n-height: 30px !important;
          // --n-color: rgba(16, 21, 31, 1) !important;
          // --n-border: 1px solid #222831 !important;
          --n-border-radius: 0 !important;
          --n-padding-right: 40px !important;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
