<!--
 * @Author: kaix
 * @Date: 2023-08-08 11:11:06
 * @LastEditTime: 2024-03-13 16:47:01
 * @LastEditors: kaix
 * @Description: 样式的全局配置
-->
<template>
  <collapse-item v-if="title && showTitle" name="标题" :expanded="title.show" :isControl="true">
    <template #header>
      <n-switch v-model:value="title.show"></n-switch>
    </template>
    <div class="wrap">
      <CustomInput
        label="文本"
        labelPlacement="left"
        v-model:value="titleText"
        placeholder="请输入标题名称"
      />
      <CustomInputSelect
        label="字体"
        v-model:value="title.textStyle.rich.title.fontFamily"
        :options="fontFamilyOption"
      />
      <n-form label-placement="left">
        <n-form-item label="字号">
          <CustomInputNumber
            v-model:value="title.textStyle.rich.title.fontSize"
            :min="1"
            :prefix="false"
            el-width="100%"
          />
        </n-form-item>
      </n-form>
      <CustomInputSelect
        label="样式"
        v-model:value="title.textStyle.rich.title.fontWeight"
        :options="fontWeightOption"
        :sign="true"
      />
      <!-- <CustomColorPicker
        label-placement="left"
        label="颜色"
        v-model:value="title.textStyle.color"
      /> -->
      <NewColorPicker
        v-bind="$attrs"
        label-placement="left"
        label="颜色"
        v-model:value="title.textStyle.color"
      />
      <CustomInputSelect
        label="位置"
        v-model:value="titlePosition"
        :options="titlePositionOption"
      />
      <InputNumberwithLabel
        label="上下边距"
        :min="0"
        v-model:value="title.textStyle.lineHeight"
      />
      <InputNumberwithLabel
        label="左右边距"
        :min="0"
        v-model:value="title.textStyle.rich.title.padding[1]"
      />
    </div>
  </collapse-item>
  <n-divider style="margin: 9px 0 16px" v-if="grid && showGrid && optionData.yAxis" />
  <collapse-item v-if="grid && showGrid && optionData.yAxis" name="边距" :expanded="true">
    <div class="wrap" v-if="typeof grid.top === 'number'">
      <InputNumberwithLabel label="上边距" :min="0" v-model:value="grid.top" />
      <InputNumberwithLabel label="下边距" :min="0" v-model:value="grid.bottom" />
      <InputNumberwithLabel label="左边距" :min="0" v-model:value="grid.left" />
      <InputNumberwithLabel label="右边距" :min="0" v-model:value="grid.right" />
    </div>
    <div class="wrap" v-if="typeof grid.top === 'string'">
      <CustomInput label="上边距" v-model:value="grid.top" labelPlacement="left" />
      <CustomInput label="下边距" v-model:value="grid.bottom" labelPlacement="left" />
      <CustomInput label="左边距" v-model:value="grid.left" labelPlacement="left" />
      <CustomInput label="右边距" v-model:value="grid.right" labelPlacement="left" />
    </div>
  </collapse-item>
  <slot />
</template>

<script setup lang="ts">
import { PropType, computed, reactive, ref, watch } from "vue";
import { GlobalThemeJsonType } from "@/package/index.d";
import { fontFamilyOption, fontWeightOption } from "./config";
import {
  CustomInput,
  CustomInputNumber,
  InputNumberwithLabel,
  CustomInputNumberWithSlider,
  CustomInputSelect,
  CustomColorPicker,
  NewColorPicker,
  CustomSwitch,
  CustomRadio,
  LabelStyleRadio,
} from "@/components/Form";
import {
  CollapseItem,
  SettingItemBox,
  SettingItem,
  GlobalSettingPosition,
  ColorSetting,
  RadioSelectCustom,
} from "@/components/Pages/ChartItemSetting";
import { useLoadOption } from "@/package/config/useChartOption";

const { useTitle, useGrid } = useLoadOption();

const props = defineProps({
  optionData: {
    type: Object as PropType<any>,
    required: true,
  },
  inChart: {
    type: Boolean,
    required: false,
    default: false,
  },
  newAttr: {
    type: Object,
    default: () => {},
  },
  showGrid: {
    type: Boolean,
    default: true,
  },
  showTitle: {
    type: Boolean,
    default: true,
  }
});

const title = computed(() => {
  // 做组件的配置项兼容
  const _title = props.optionData?.title ?? useTitle();
  // 对内部属性做兼容处理
  if (!_title.textStyle) {
    const {
      textStyle: _textStyle,
      textStyle: { rich },
    } = useTitle();
    _title.textStyle = _textStyle;
    if (!_title.textStyle.rich) {
      _title.textStyle.rich = rich;
    }
  } else {
    const {
      textStyle: _textStyle,
      textStyle: { rich },
    } = useTitle();
    _title.textStyle = {
      ..._textStyle,
      ..._title.textStyle,
    };
    if (!_title.textStyle.rich) {
      _title.textStyle.rich = rich;
    }
    // const {
    //   textStyle: { rich },
    // } = _title;
    // if (!_title.textStyle.rich) {
    //   _title.textStyle.rich = rich;
    // }
  }
  return _title;
});

const grid = computed(() => {
  const res = props.optionData?.grid ?? useGrid();
  const map = ["bottom", "top", "left", "right"];
  map.forEach((key) => {
    if (typeof res[key] === "string") {
      res[key] = parseFloat(res[key]);
    }
  });
  return res;
});

const titlePositionOption = [
  { label: "顶部居左", value: "顶部居左" },
  { label: "顶部居中", value: "顶部居中" },
  { label: "顶部居右", value: "顶部居右" },
  { label: "中部居左", value: "中部居左" },
  { label: "居中", value: "居中" },
  { label: "中部居右", value: "中部居右" },
  { label: "底部居左", value: "底部居左" },
  { label: "底部居中", value: "底部居中" },
  { label: "底部居右", value: "底部居右" },
];

const titlePosition = ref("顶部居左");

watch(
  () => title.value,
  (newVal) => {
    if (newVal) {
      const _left = newVal.left;
      const _top = newVal.top;
      if (_left && !_top) {
        switch (_left) {
          case "left":
            titlePosition.value = "顶部居左";
            break;
          case "center":
            titlePosition.value = "顶部居中";
            break;
          case "right":
            titlePosition.value = "顶部居右";
            break;
        }
      } else if (!_left && _top) {
        switch (_top) {
          case "top":
            titlePosition.value = "顶部居左";
            break;
          case "middle":
            titlePosition.value = "中部居左";
            break;
          case "bottom":
            titlePosition.value = "底部居左";
            break;
        }
      } else {
        switch (`${_left}&${_top}`) {
          case "left&top":
            titlePosition.value = "顶部居左";
            break;
          case "left&middle":
            titlePosition.value = "中部居左";
            break;
          case "left&bottom":
            titlePosition.value = "底部居左";
            break;
          case "center&top":
            titlePosition.value = "顶部居中";
            break;
          case "center&middle":
            titlePosition.value = "居中";
            break;
          case "center&bottom":
            titlePosition.value = "底部居中";
            break;
          case "right&top":
            titlePosition.value = "顶部居右";
            break;
          case "right&middle":
            titlePosition.value = "中部居右";
            break;
          case "right&bottom":
            titlePosition.value = "底部居右";
            break;
        }
      }
    }
  },
  {
    deep: true,
    immediate: true,
  }
);
watch(
  () => titlePosition.value,
  (newVal) => {
    switch (newVal) {
      case "顶部居左":
        title.value!.left = "left";
        title.value!.top = "top";
        break;
      case "顶部居中":
        title.value!.left = "center";
        title.value!.top = "top";
        break;
      case "顶部居右":
        title.value!.left = "right";
        title.value!.top = "top";
        break;
      case "中部居左":
        title.value!.left = "left";
        title.value!.top = "middle";
        break;
      case "居中":
        title.value!.left = "center";
        title.value!.top = "middle";
        break;
      case "中部居右":
        title.value!.left = "right";
        title.value!.top = "middle";
        break;
      case "底部居左":
        title.value!.left = "left";
        title.value!.top = "bottom";
        break;
      case "底部居中":
        title.value!.left = "center";
        title.value!.top = "bottom";
        break;
      case "底部居右":
        title.value!.left = "right";
        title.value!.top = "bottom";
        break;
    }
  }
);

const titleText = ref(
  title.value?.text
    ? title.value.text.substring(1, title.value.text.length - 1).split("title|")[1]
    : ""
);
watch(
  () => titleText.value,
  (val) => {
    title.value.text = `{title|${val}}`;
  }
);

// if (title.value && title.value.textStyle) {
//   title.value.textStyle.rich = {
//     title: {
//       padding: [0, 0],
//     },
//   };
// }
</script>

<style lang="scss" scoped>
@import "@/styles/pages/form.scss";
.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  .subtitle {
    height: 17px;
    font-size: 12px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
    // color: #ffffff;
    color: var(--n-text-color);
    line-height: 17px;
    margin-bottom: 10px;
    &.control {
      display: flex;
      justify-content: space-between;
      .btns {
        display: flex;
        gap: 10px;
        i {
          font-size: 16px;
          cursor: pointer;
        }
      }
    }
  }
}
</style>
