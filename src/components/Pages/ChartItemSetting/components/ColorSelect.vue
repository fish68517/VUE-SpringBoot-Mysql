<template>
  <div class="color">
    <div class="option" @click="visible = !visible" :class="{ active: visible }">
      <div class="mask" />
      <input
        v-for="(item, index) in data[value].color"
        :key="index"
        v-model="data[value].color[index]"
        disabled
        type="color"
        :style="{ backgroundColor: data[value].color[index] }"
      />
    </div>
    <div v-if="visible" class="dialog">
      <div class="option1">
        <div v-for="(item, idx1) in data" :key="idx1" @click="change(item)" class="item">
          <div class="mask" />
          <input
            v-for="(it, idx2) in item.color"
            :key="idx2"
            v-model="item.color[idx2]"
            type="color"
            disabled
            :style="{ backgroundColor: it }"
          />
          <span class="label">{{ item.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PropType, watch, ref, computed } from "vue";
import { ColorConfigType, GlobalThemeJsonType } from "@/package/index.d";
import newColor from "@/package/config/chartThemes/themes/newColor.json";

const props = defineProps({
  data: {
    type: Array as PropType<ColorConfigType[]>,
    default: () => {
      return [
        newColor.technology,
        {
          color: ["#8A71FF", "#FFD400", "#00F7FF", "#FF9201", "#FFCA6E", "#DB4545"],
          label: "神秘色",
          val: "mystical",
        },
        {
          color: ["#00F7FF", "#00A0FF", "#FFD400", "#8A71FF", "#FF6600", "#E200FF"],
          label: "糖果色",
          val: "candy",
        },
        {
          color: ["#4CECAA", "#00F7FF", "#FFD400", "#FF9201", "#DCFF5B", "#00A0FF"],
          label: "流行色",
          val: "retro",
        },
        newColor.morandi,
        newColor.fog,
        newColor.party,
        newColor.china,
        newColor.country,
      ];
    },
  },
  newAttr: {
    type: Object as PropType<any>,
    default: () => {},
  },
  optionData: {
    type: Object as PropType<GlobalThemeJsonType | null>,
    default: () => {},
  },
});

// console.log(props.newAttr);

const value = computed(() => {
  const val = props.newAttr?.themeColor.selfTheme.value;
  return val ? props.data.findIndex((item) => item.val === val) : 0;
});

const emit = defineEmits<{
  (e: "transTheme", item: ColorConfigType): any;
}>();
const visible = ref(false);

const change = (item: ColorConfigType) => {
  if (!props.optionData) {
    emit("transTheme", item);
  } else {
    props.newAttr.themeColor.selfTheme.color = item.color;
    props.newAttr.themeColor.selfTheme.value = item.val;
  }
  visible.value = false;
};

watch(
  //@ts-ignore
  () => props.optionData?.useSeriesColor,
  (val) => {
    if (val === undefined) {
      return;
    } else {
      if (!val) {
        const originValue = value.value;
        props.newAttr.themeColor.selfTheme.color = [];
        props.newAttr.themeColor.selfTheme.value = "";
        change(props.data[originValue]);
      }
    }
  },
  { deep: true, immediate: true }
);
</script>

<style lang="scss" scoped>
.color {
  width: 100%;
  height: 100%;
}
.option {
  cursor: pointer;
  position: relative;
  width: 100%;
  height: 32px;
  border-radius: 4px;
  // border: 1px solid #222831;
  display: flex;
  .mask {
    background: transparent;
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }
  &.active {
    box-shadow: inset 0px 0px 10px 2px var(--primary-color);
    border: 1px solid var(--primary-color);
  }
  input {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    margin-left: 10px;
    margin-top: 7px;
  }
  input[type="color"] {
    padding: 0;
  }
  input[type="color"]::-webkit-color-swatch-wrapper {
    padding-right: 20;
    margin: 0;
  }

  input[type="color"]::-webkit-color-swatch {
    border: none;
  }
  img {
    width: 10px;
    height: 6px;
    position: absolute;
    right: 10px;
    top: 12px;
  }
}
.dialog {
  width: 100%;
  // box-shadow: 0px 10px 30px 0px rgba(0, 0, 0, 0.3);
  border-radius: 4px;
  position: relative;
  top: 8px;
  .option1 {
    width: 100%;
    border: 1px solid rgba(255, 255, 255, 0.3);
    position: relative;
    .item {
      cursor: pointer;
      position: relative;
      &:hover {
        background: var(--n-color-segment);
      }
      .mask {
        background: transparent;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
      }
      span {
        font-size: 16px;
        font-family: PingFangSC-Regular, PingFang SC;
        font-weight: 400;
        // color: #ffffff;
        color: var(--n-text-color);
        line-height: 32px;
        position: absolute;
        right: 10px;
      }
      input {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        margin: 7px 0px 7px 7px;
      }
      .label {
        font-size: 14px;
      }
      input[type="color"] {
        //-webkit-appearance: none;
        //background-color: #000;
        //border: none;
        padding: 0;
      }
      input[type="color"]::-webkit-color-swatch-wrapper {
        padding-right: 20;
        margin: 0;
      }

      input[type="color"]::-webkit-color-swatch {
        border: none;
      }
    }
  }
}
</style>
