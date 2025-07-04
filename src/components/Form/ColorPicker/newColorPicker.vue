<!--
 * @Author: wangcong
 * @Date: 2023-08-18 16:31:54
 * @LastEditTime: 2025-04-01 15:58:30
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <div class="colorPick" :class="labelPlacement">
    <div class="label">{{ label }}</div>
    <div class="content">
      <n-popover trigger="click" class="colorPickPopover" :on-update:show="handleShow">
        <template #trigger>
          <div class="color">
            <div class="value" />
            <div class="background" />
          </div>
        </template>
        <popover
          v-model:value="_value"
          :usedList="usedList"
          :collectionList="collectionList"
          @collection="handleCollection"
          v-bind="$attrs"
        />
      </n-popover>
      <n-input type="text" placeholder="请输入颜色值" v-model:value="_value" style="height: 32px" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { getColorByType, addColorByType } from './color.api'
import popover from './components/popover.vue'
import { PropType, computed, ref, useAttrs, onBeforeUnmount } from 'vue'
import Color from 'color'
import isNil from 'lodash/isNil'

const props = defineProps({
  label: {
    type: String,
    default: ''
  },
  labelPlacement: {
    type: String as PropType<'top' | 'left'>,
    default: 'top'
  },
  value: {
    type: String as PropType<string | null>,
    default: () => null
  }
})

const token = ref('')
token.value = useAttrs().token as unknown as string

const emits = defineEmits(['update:value'])
const _value = computed({
  get: () => props.value,
  set: value => {
    const colorHex = /^#([0-9a-fA-F]{8}|[0-9a-fA-F]{6}|[0-9a-fA-F]{3})$/
    const colorRgb =
      /^[rR][gG][Bb][Aa]?[\(]([\s]*(2[0-4][0-9]|25[0-5]|[01]?[0-9][0-9]?),){2}[\s]*(2[0-4][0-9]|25[0-5]|[01]?[0-9][0-9]?),?[\s]*(0\.\d{1,2}|1|0)?[\)]{1}$/g
    if (!(colorHex.test(value) || colorRgb.test(value))) {
      return window.$message.warning('请设置正确格式的颜色')
    }
    emits('update:value', value)
  }
})

const usedList = ref<Array<any>>([])
const collectionList = ref<Array<any>>([])

const getColorList = async () => {
  Promise.all([getColorByType(0, token.value), getColorByType(1, token.value)]).then(res => {
    const [usedRes, collectionRes] = res
    if (usedRes?.code === 200) {
      usedList.value = usedRes.data
    }
    if (collectionRes?.code === 200) {
      collectionList.value = collectionRes.data
    }
  })
}

const handleShow = (val: boolean) => {
  if (val) {
    getColorList()
  } else {
    const data = new FormData()
    const { r, g, b } = Color(props.value!, 'rgb').object()
    const a = Color(props.value!, 'rgb').alpha()
    const rgba = `rgba(${r}, ${g}, ${b}, ${a})`
    data.append('color', rgba)
    data.append('colorType', '0')
    addColorByType(data, token.value)
  }
}

onBeforeUnmount(() => {
  const data = new FormData()
  const { r, g, b } = Color(props.value!, 'rgb').object()
  const a = Color(props.value!, 'rgb').alpha()
  const rgba = `rgba(${r}, ${g}, ${b}, ${a})`
  data.append('color', rgba)
  data.append('colorType', '0')
  addColorByType(data, token.value)
})

const handleCollection = () => {
  getColorByType(1, token.value).then(res => {
    if (res?.code === 200) {
      collectionList.value = res.data
    }
  })
}
</script>

<style lang="scss" scoped>
.colorPick {
  * {
    padding: 0;
    margin: 0;
    box-sizing: border-box;
    margin-bottom: 5px;
  }
  display: flex;
  &.top {
    flex-direction: column;
  }
  &.left {
    align-items: center;
    .label {
      width: 60px;
    }
  }
  .label {
    min-height: 22px;
    line-height: 17px;
    padding: 0;
    color: #aaaaaa;
    // color: var(--n-label-text-color);
    font-size: 12px;
  }
  .content {
    flex: 1;
    display: flex;
    align-items: center;
    .color {
      width: 30px;
      height: 30px;
      // background: #10151f;
      border: 1px solid #222831;
      border-right: 1px solid transparent;
      border-color: var(--primary-color);
      &:hover {
        cursor: pointer;
      }
      position: relative;
      .value {
        z-index: 2;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 18px;
        height: 18px;
        border-radius: 50%;
        background: v-bind('props.value');
        border: 1px solid #6e747f;
      }
      .background {
        z-index: 1;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 18px;
        height: 18px;
        border-radius: 50%;
        background-image: linear-gradient(45deg, #404040 25%, transparent 0, transparent 75%, #404040 0),
          linear-gradient(45deg, #404040 25%, transparent 0, transparent 75%, #404040 0);
        background-color: #4a4a4a;
        background-size: 10px 10px;
        background-position: 0 0, 5px 5px;
        border: 1px solid #6e747f;
      }
    }
    .n-input {
      flex: 1;
      --n-height: 28px !important;
      // background: #10151f;
      // border: 1px solid #222831;
      font-size: 12px;
    }
  }
}
</style>

<style>
.colorPickPopover {
  /* --n-color: rgba(24, 29, 39, 0.8) !important; */
  padding: 10px !important;
  /* box-shadow: 0px 4px 20px 0px rgba(0, 0, 0, 0.4); */
  border-radius: 4px !important;
  /* border: 1px solid #2d323c; */
  backdrop-filter: blur(8px);
}
</style>
