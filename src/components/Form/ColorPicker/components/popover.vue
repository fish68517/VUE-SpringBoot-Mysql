<!--
 * @Author: wangcong
 * @Date: 2023-08-21 09:12:23
 * @LastEditTime: 2024-01-05 11:14:13
 * @LastEditors: zouying
 * @Description: 
-->
<template>
  <div class="popover">
    <div class="label">
      <span class="text">{{ labelText }}</span>
      <div class="toggle">
        <div
          class="btn"
          v-for="item in btns"
          :key="item.id"
          :class="{ active: item.id === curBtn }"
          @click="toggleBtn(item.id)"
        >
          <img :src="item.icon" alt="" />
        </div>
      </div>
    </div>
    <colorBrand
      v-if="curBtn === 'colorBrand'"
      v-model:value="_value"
      :usedList="usedList"
      :collectionList="collectionList"
      @collection="handleCollection"
      v-bind="$attrs"
    />
    <colorSpace v-else v-model:value="_value" />
  </div>
</template>

<script setup lang="ts">
import colorBrand from "./colorBrand.vue";
import colorSpace from "./colorSpace.vue";
import { ref, computed, PropType } from "vue";

const props = defineProps({
  value: {
    type: String as PropType<string | null>,
    default: "",
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

const emits = defineEmits(["update:value", "collection"]);

const _value = computed({
  get() {
    return props.value;
  },
  set(val) {
    emits("update:value", val);
  },
});

const colorBrandIcon =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAAXNSR0IArs4c6QAAAQpJREFUOE+F0s0qRVEUB/DfEkNkIt6AzBVl6uMJlKJkbGRgdEsGhh7BgCQylI+ZkTC65TlMKDMt7ds5Om73umty6tRvr7X+e4eqMnMcV2hFxGv9v983uuADZrA8CP/CckDV9QVTWI2I54EdK7iBU3xiCGsR8dQLN0et0SV2cYvZfrgDM7OJtiLiuxq77NwTR2au4xylUwd1BXaHuSqwsn+nCnzHFw7wixp7jeII7YhYasJ7rAy6NxxHxF5mjkXER+k4hoIX/sEX2MQ+djBfh/MfrlF5GG0M46R5Hb1wB1Upt3BYTfXZ/XIKvkEJ4QzbdcqZuYhHjOD6D6zutLyYiYgoaf+pzJzGJN5+AIrxbU+puhrvAAAAAElFTkSuQmCC";
const colorSpaceIcon =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAAAXNSR0IArs4c6QAAASNJREFUKFN10r1L1mEUxvHPGXRocXdoCVrcBPEFt4Je1MnhGRSiQRt8JrFB/AP6B5oKEqGlxnwZxUHQhqyl0SZpDWxzOXLwfkR89B5+cB9+13W+5zp3aCczB7GERYy08m98xseIuKha1Cczh7GNUXzHYRNMYxwnmIuIv9Gcj/AIq/iC13gRETOZOdu6nGKyBCt4Xw5Yww9s4jEKcx0bjaBbgnKPiJjIzIf4HxH/Gmrdp/AN+0VfgnN8iIhy7zuZ+RS7+ISFEpTbZkQU/12CB63LcyyXYAdj6ETEwY2Y64dXeBsRZ5l53EOqdPbakGfYwjt8xUu8QdULq9vbwwAqvg7m8ast8Qn+tNSuYr0NnZnPWsfqOoRa3s/rxd0zaOHUDH1P4xLAcnJJSwYr9gAAAABJRU5ErkJggg==";

const curBtn = ref<"colorBrand" | "colorSpace">("colorBrand");
const btns = [
  { label: "色板", icon: colorBrandIcon, id: "colorBrand" },
  { label: "色彩空间", icon: colorSpaceIcon, id: "colorSpace" },
] as const;

const toggleBtn = (id: "colorBrand" | "colorSpace") => {
  if (id === curBtn.value) return;
  curBtn.value = id;
};

const labelText = computed(() => {
  return btns.find((item) => item.id === curBtn.value)!.label;
});

const handleCollection = () => {
  emits("collection");
};
</script>

<style lang="scss" scoped>
.popover {
  * {
    box-sizing: border-box;
  }
  width: 330px;
  .label {
    display: flex;
    align-items: center;
    margin-bottom: 6px;
    .text {
      font-size: 14px;
      font-family: PingFangSC-Semibold, PingFang SC;
      font-weight: 600;
      // color: #ffffff;
      line-height: 22px;
    }
    .toggle {
      margin-left: auto;
      display: flex;
      align-items: center;
      .btn {
        width: 20px;
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        & + .btn {
          margin-left: 9px;
        }
        &.active {
          background: var(--primary-color);
          border-radius: 4px;
        }
        &:not(.active):hover {
          img {
            cursor: pointer;
            opacity: 0.8;
          }
        }
        img {
          width: 14px;
          height: 14px;
          user-select: none;
        }
      }
    }
  }
}
</style>
