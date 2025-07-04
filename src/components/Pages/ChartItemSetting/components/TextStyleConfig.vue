<!--
 * @Author: wangcong
 * @Date: 2025-04-01 09:31:19
 * @LastEditTime: 2025-04-01 10:04:29
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <CustomInputSelect label="字体" v-model:value="textStyle.fontFamily" :options="fontFamilyOption" />
  <n-form label-placement="left">
    <n-form-item label="字号">
      <CustomInputNumber v-model:value="textStyle.fontSize" :min="1" :prefix="false" el-width="100%" />
    </n-form-item>
  </n-form>
  <CustomInputSelect
    label="样式"
    v-model:value="textStyle.fontWeight"
    :options="fontWeightOption"
    :sign="true"
  />
  <NewColorPicker v-bind="$attrs" label-placement="left" label="颜色" v-model:value="textStyle.color" />
</template>

<script setup lang="ts">
import {
  fontFamilyOption,
  fontWeightOption,
} from '@/components/Pages/ChartItemSetting/globalSettings/config'
import {
  CustomInputNumber,
  CustomInputSelect,
  NewColorPicker,
} from '@/components/Form'
import { PropType, computed } from 'vue'
import { cloneDeep } from 'lodash'

const props = defineProps({
  textStyle: {
    type: Object as PropType<{
      color: string,
      fontSize: number,
      fontWeight: string,
      fontFamily: string
    }>,
    required: true,
    default: () => {
      return {
        color: '#fff',
        fontSize: 12,
        fontWeight: 'normal',
        fontFamily: 'Microsoft YaHei'
      }
    }
  }
})


</script>

<style scoped lang="scss">
@import '@/styles/pages/form.scss';
</style>