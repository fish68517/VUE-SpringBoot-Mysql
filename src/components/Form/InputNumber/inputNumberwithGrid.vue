<!--
 * @Author: kaix
 * @Date: 2023-08-10 17:13:46
 * @LastEditTime: 2023-12-08 11:19:25
 * @LastEditors: zouying
 * @Description: 
-->
<template>
  <div class="wrap">
    <div class="label">{{ label }}</div>
    <div class="content">
      <div class="row">
      <div class="item">
        <CustomInputNumber
          v-model:value="_value[0]"
          :prefixVal="prefixVal"
          :prefix="prefixs"
          :min="min"
          :max="max"
          :step="step"
          el-width="100%"
        />
        <div class="position">上</div>
      </div>
      <div class="item">
        <CustomInputNumber
          v-model:value="_value[2]"
          :prefixVal="prefixVal"
          :prefix="prefixs"
          :min="min"
          :max="max"
          :step="step"
          el-width="100%"
        />
        <div class="position">下</div>
      </div>
    </div>
      <div class="row">
        <div class="item">
        <CustomInputNumber
          v-model:value="_value[3]"
          :prefixVal="prefixVal"
          :prefix="prefixs"
          :min="min"
          :max="max"
          :step="step"
          el-width="100%"
        />
        <div class="position">左</div>
      </div>
      <div class="item">
        <CustomInputNumber
          v-model:value="_value[1]"
          :prefixVal="prefixVal"
          :prefix="prefixs"
          :min="min"
          :max="max"
          :step="step"
          el-width="100%"
        />
        <div class="position">右</div>
      </div>
      </div>
    </div>
    
  </div>
    
   
      
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { CustomInputNumber } from './index'

const props = defineProps({
  label: {
    type: String,
    default: ''
  },
  value: {
    type: Array,
    default: () => [0, 0, 0, 0]
  },
  min: {
    type: Number,
    default: 0
  },
  max: {
    type: Number,
    default: null
  },
  step: {
    type: Number,
    default: 1
  },
  prefixVal: {
    type: String,
    default: null
  },
  prefixs: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits<{
  (e: 'update:value', val: Array<number>): void
}>()

const _value = computed({
  get: () => (props.value.length > 0 ? (props.value as Array<number>) : [0, 0, 0, 0]),
  set: val => {
    val.forEach(it => {
      it > 0 ? Number(it) : 0
    })
    emit('update:value', val)
  }
})
</script>

<style lang="scss" scoped>
// @import '@/styles/pages/form.scss';
.row {
  width: 100%;
  display: flex;
  justify-content: space-around;
  .item {
    width: 45%;
    .position {
      width: 100%;
      text-align: center;
      color: #aaaaaa;
      font-size: 12px;
    }
  }
} 
.wrap {
  display: flex;
  .content {
    width: calc(100% - 40px);
  }
}

.n-slider {
  width: 112px;
}
.label {
  color: #aaaaaa;
  font-size: 12px;
  width: 40px;
}
</style>
