<!--
 * @Author: wangcong
 * @Date: 2023-08-21 09:11:45
 * @LastEditTime: 2024-01-05 11:19:03
 * @LastEditors: zouying
 * @Description: 
-->
<template>
  <div class="colorBrand">
    <div class="defaultColors">
      <div
        class="color"
        v-for="item in colors"
        :key="item.id"
        :style="{ background: item.color }"
        @click="selectColor(curColorEnum.DEFAULT, item.id)"
        :class="{
          active: curColor.type === curColorEnum.DEFAULT && item.id === curColor.id,
        }"
      />
    </div>

    <eye-dropper v-model:value="_value" @changeCurName="handleChangeCurName" />

    <div class="line" />

    <div class="usedColor">
      <div class="title">最近使用</div>
      <div class="colors">
        <div
          class="color"
          v-for="item in usedList"
          :key="item.id"
          :style="{ background: item.color }"
          @click="selectColor(curColorEnum.USED, item.id)"
          :class="{
            active: curColor.type === curColorEnum.USED && item.id === curColor.id,
          }"
        />
      </div>
    </div>
    <div class="collectionColor">
      <div class="title">
        <span class="text">我的收藏</span>
        <n-button
          type="primary"
          text
          class="del"
          v-if="curColor.type === curColorEnum.COLLECTION"
          @click="delCollection"
          :loading="delLoading"
          >删除</n-button
        >
        <div class="btns">
          <div class="icon" @click="collectColor">
            <customIcon icon="xinjianshujuyuan" :width="14" :height="14" />
          </div>
          <div
            class="icon"
            @click="open = !open"
            v-if="collectionList && collectionList.length"
          >
            <customIcon :icon="open ? 'zengjia' : 'jianshao'" :width="14" :height="14" />
          </div>
        </div>
      </div>
      <template v-if="collectionList && collectionList.length">
        <div class="colors" :class="{ open }">
          <div
            class="color"
            v-for="item in collectionList"
            :key="item.id"
            :style="{ background: item.color }"
            @click="selectColor(curColorEnum.COLLECTION, item.id)"
            :class="{
              active:
                curColor.type === curColorEnum.COLLECTION && item.id === curColor.id,
            }"
          />
        </div>
      </template>
      <template v-else>
        <div class="empty">暂无收藏内容</div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defaultColors } from "./defaultColors";
import eyeDropper from "./eyeDropper.vue";
import { PropType, ref, computed, useAttrs } from "vue";
import Color from "color";
import { addColorByType, delColorByID } from "../color.api";
import { curColorEnum, curColorType, ColorType } from "./color.d";

const props = defineProps({
  colors: {
    type: Array as PropType<{ color: string; id: string }[]>,
    default: () => defaultColors,
  },
  value: {
    type: String as PropType<string | null>,
    default: null,
  },
  usedList: {
    type: Array as PropType<any[]>,
    defualt: () => [],
  },
  collectionList: {
    type: Array as PropType<any[]>,
    defualt: () => [],
  },
});

const token = ref("");
token.value = (useAttrs().token as unknown) as string;

const emits = defineEmits(["update:value", "collection"]);

const _value = computed({
  get: () => props.value,
  set: (val) => {
    emits("update:value", val);
  },
});

const curColor = ref<curColorType>({
  type: null,
  id: null,
  curName: null,
});
const handleChangeCurName = (val: ColorType) => {
  curColor.value.curName = val;
};

const selectColor = (type: curColorEnum | null, id: string | null) => {
  curColor.value.type = type;
  curColor.value.id = id;
  if (type && id) {
    let color = "";
    switch (type) {
      case curColorEnum.COLLECTION: {
        color = props.collectionList?.find((item) => item.id === id)!.color;
        break;
      }
      case curColorEnum.USED: {
        color = props.usedList?.find((item) => item.id === id)!.color;
        break;
      }
      case curColorEnum.DEFAULT: {
        color = props.colors.find((item) => item.id === id)!.color;
        break;
      }
    }
    switch (curColor.value.curName) {
      case ColorType.HEX: {
        let hex = Color(color, "hex")!.hex();
        hex += Math.round((Color(color, "rgb")!.alpha() as number) * 255)
          .toString(16)
          .padStart(2, "0");
        color = hex.toUpperCase();
        break;
      }
      case ColorType.RGB: {
        const { r, g, b } = Color(color, "rgb")!.object();
        const a = Math.round((Color(color, "rgb")!.alpha() as number) * 100) / 100;
        color = `rgba(${r}, ${g}, ${b}, ${a})`;
        break;
      }
    }
    emits("update:value", color);
  }
};

const delLoading = ref(false);
const open = ref(true);
const collectColor = () => {
  if (props.collectionList && props.collectionList.length >= 20) {
    window.$message.error("收藏颜色超过20个");
    return;
  }
  const data = new FormData();
  const { r, g, b } = Color(props.value!, "rgb")!.object();
  const a = Color(props.value!, "rgb")!.alpha();
  const rgba = `rgba(${r}, ${g}, ${b}, ${a})`;
  data.append("color", rgba);
  data.append("colorType", "1");
  let msg = window.$message.loading("颜色收藏中", {
    duration: 0,
  });
  addColorByType(data, token.value)
    .then((res) => {
      if (res?.code === 200) {
        msg.destroy();
        window.$message.success(res.msg);
        emits("collection");
        //@ts-ignore
      } else {
        msg.destroy();
        window.$message.error(res?.msg || "收藏失败");
      }
    })
    .catch(() => {
      msg.destroy();
      window.$message.error("收藏失败");
    });
};
const delCollection = () => {
  if (curColor.value.type !== curColorEnum.COLLECTION || !curColor.value.id) {
    return;
  }
  let msg = window.$message.loading("颜色删除中", {
    duration: 0,
  });
  delLoading.value = true;
  delColorByID(curColor.value.id as string, token.value)
    .then((res) => {
      if (res?.code === 200) {
        msg.destroy();
        window.$message.success("删除颜色成功");
        selectColor(null, null);
        emits("collection");
      } else {
        msg.destroy();
        window.$message.error("删除失败");
      }
    })
    .catch(() => {
      msg.destroy();
      window.$message.error("删除失败");
    })
    .finally(() => {
      delLoading.value = false;
    });
};
</script>

<style scoped lang="scss">
.colorBrand {
  width: 100%;
  * {
    box-sizing: border-box;
  }
  .defaultColors {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(10, 24px);
    grid-template-rows: repeat(4, 24px);
    gap: 10px;
    margin-bottom: 6px;
    .color {
      place-content: center;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      &.active {
        border: 3px solid var(--primary-color);
      }
      &:not(.active) {
        &:hover {
          cursor: pointer;
          opacity: 0.8;
        }
      }
    }
  }
  .line {
    height: 1px;
    width: 100%;
    background: #2d333f;
    margin: 5px 0;
  }
  .usedColor {
    margin-bottom: 6px;
    .title {
      font-size: 14px;
      font-family: PingFangSC-Semibold, PingFang SC;
      font-weight: 600;
      // color: #ffffff;
      line-height: 22px;
      margin-bottom: 6px;
    }
    .colors {
      width: 100%;
      display: grid;
      grid-template-columns: repeat(10, 24px);
      grid-template-rows: repeat(2, 24px);
      gap: 10px;
      .color {
        place-content: center;
        width: 100%;
        height: 100%;
        border-radius: 50%;
        position: relative;
        &.active {
          border: 3px solid var(--primary-color);
        }
        &:not(.active) {
          &:hover {
            cursor: pointer;
          }
        }
        &::after {
          content: "";
          display: block;
          width: 100%;
          height: 100%;
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
          position: absolute;
          z-index: -1;
          border-radius: 50%;
        }
      }
    }
  }
  .collectionColor {
    .title {
      margin-bottom: 6px;
      display: flex;
      align-items: center;
      .text {
        font-size: 14px;
        font-family: PingFangSC-Semibold, PingFang SC;
        font-weight: 600;
        // color: #ffffff;
        line-height: 22px;
      }
      .del {
        margin-left: 10px;
      }
      .btns {
        margin-left: atuo;
        display: flex;
        align-items: center;
        margin-left: auto;
        .icon {
          margin-left: 3px;
          width: 14px;
          height: 14px;
          &:hover {
            cursor: pointer;
            opacity: 0.8;
          }
        }
      }
    }
    .colors {
      width: 100%;
      display: grid;
      grid-template-columns: repeat(10, 24px);
      grid-template-rows: repeat(2, 24px);
      gap: 10px;
      height: 0;
      overflow: hidden;
      transition: height 0.3s;
      &.open {
        height: 58px;
      }
      .color {
        place-content: center;
        width: 100%;
        height: 100%;
        border-radius: 50%;
        position: relative;
        &.active {
          border: 3px solid var(--primary-color);
        }
        &:not(.active) {
          &:hover {
            cursor: pointer;
          }
        }
        &::after {
          content: "";
          display: block;
          width: 100%;
          height: 100%;
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
          position: absolute;
          z-index: -1;
          border-radius: 50%;
        }
      }
    }
  }
}
</style>
