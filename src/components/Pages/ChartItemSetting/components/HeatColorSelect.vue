<template>
  <div class="color">
    <div class="option" @click="showTooltip" :class="{ active: visible }">
      <input
        v-for="(item, index) in datas[selectedIndex].color"
        :key="index"
        v-model="datas[selectedIndex].color[index]"
        disabled
        type="color"
        :style="{ backgroundColor: datas[selectedIndex].color[index] }"
      />
    </div>
    <div v-if="visible" class="dialog">
      <div class="option1">
        <div v-for="(item, idx1) in datas" :key="idx1" @click="change(idx1, item)" class="item">
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
import { PropType, reactive, ref, toRefs } from 'vue'
import { ColorConfigType } from '@/package/index.d'

const props = defineProps({
  data: {
    type: Array,
    default: () => {
      return [
        {
          color: ['#F3E1AF', '#DBBF92', '#D78851', '#BE5C37', '#DD2222'],
          label: '方案1'
        },
        {
          color: ['#DEEED4', '#B8DCA1', '#86C06C', '#5B9C4B', '#669933'],
          label: '方案2'
        },
        {
          color: ['#DEDCEA', '#AFB7DB', '#8197C6', '#507AAF', '#6633FF'],
          label: '方案3'
        }
      ]
    }
  },
  selectedIndex: {
    type: Number,
    default: 0
  }
})
const datas = reactive(props.data)
const visible = ref(false)
let { selectedIndex } = toRefs(props)

const showTooltip = () => {
  visible.value = !visible.value
}

const emit = defineEmits<{
  (e: 'transTheme', item: ColorConfigType, index: number): any
}>()

const change = (index: number, item: ColorConfigType) => {
  emit('transTheme', item, index)
  selectedIndex.value = index
  visible.value = false
}
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
  // background: #0c121d;
  border-radius: 4px;
  border: 1px solid #222831;
  display: flex;
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
  input[type='color'] {
    //-webkit-appearance: none;
    //background-color: transparent;
    //border: none;
    padding: 0;
  }
  input[type='color']::-webkit-color-swatch-wrapper {
    padding-right: 20;
    margin: 0;
  }

  input[type='color']::-webkit-color-swatch {
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
  //height: 108px;
  // background: #181d27;
  box-shadow: 0px 10px 30px 0px rgba(0, 0, 0, 0.3);
  border-radius: 4px;
  position: relative;
  //left: 80px;
  top: 8px;

  .option1 {
    width: 100%;
    border: 1px solid rgba(255, 255, 255, 0.3);
    .item {
      cursor: pointer;
      &:hover {
        background: var(--n-color-segment);
      }
      span {
        font-size: 16px;
        font-family: PingFangSC-Regular, PingFang SC;
        font-weight: 400;
        // color: #ffffff;
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
      input[type='color'] {
        //-webkit-appearance: none;
        //background-color: #000;
        //border: none;
        padding: 0;
      }
      input[type='color']::-webkit-color-swatch-wrapper {
        padding-right: 20;
        margin: 0;
      }

      input[type='color']::-webkit-color-swatch {
        border: none;
      }
    }
  }
}
</style>
