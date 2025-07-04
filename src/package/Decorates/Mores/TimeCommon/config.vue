<!--
 * @Author: kaix
 * @Date: 2023-04-19 19:17:12
 * @LastEditTime: 2024-10-16 14:35:06
 * @LastEditors: sundaohui 2459997429@qq.com
 * @Description:
-->
<template>
    <GlobalSetting :is-use-custom="true" :tabData="tabData">
        <template #styles>
            <div class="wrap">
                <div class="subtitle">展示</div>
                <CustomRadio label="格式" v-model:value="optionData.timeFormatter" :options="formatterOption" />
                <CustomRadio label="样式" v-model:value="optionData.timeStyle" :options="styleOption" />
                <CustomInputNumberWithSlider label="左右间距" :min="0" :max="50" v-model:value="optionData.lrSpacing" />
                <CustomInputSelect label="显示形式" v-model:value="optionData.dateType" :options="optionType" />
                <n-divider n-divider style="margin: 9px 0 16px" />
                <CustomSwitch
                    label="年月日文本"
                    v-model:value="optionData.isYearMonthDayText"
                    elMarginBottom="10px"
                />
                <template v-if="optionData.isYearMonthDayText">
                    <CustomInputSelect label="字体" v-model:value="optionData.yearMonthDayOption.fontFamily" :options="fontFamilyOption" />
                    <InputNumberwithLabel label="字号" :min="0" v-model:value="optionData.yearMonthDayOption.fontSize" />
                    <CustomInputSelect
                        label="样式"
                        v-model:value="optionData.yearMonthDayOption.fontWeight"
                        :options="fontWeightOption"
                        :sign="true"
                    />
                    <NewColorPicker v-bind="$attrs" label-placement="left" v-model:value="optionData.yearMonthDayOption.textColor" label="颜色" />
                </template>
                <n-divider n-divider style="margin: 9px 0 16px" />
                <CustomSwitch
                    label="星期文本"
                    v-model:value="optionData.isWeekText"
                    elMarginBottom="10px"
                />
                <template v-if="optionData.isWeekText">
                    <CustomInputSelect label="字体" v-model:value="optionData.weekOption.fontFamily" :options="fontFamilyOption" />
                    <InputNumberwithLabel label="字号" :min="0" v-model:value="optionData.weekOption.fontSize" />
                    <CustomInputSelect
                        label="样式"
                        v-model:value="optionData.weekOption.fontWeight"
                        :options="fontWeightOption"
                        :sign="true"
                    />
                    <NewColorPicker v-bind="$attrs" label-placement="left" v-model:value="optionData.weekOption.textColor" label="颜色" />
                </template>
                <n-divider n-divider style="margin: 9px 0 16px" />
                <CustomSwitch
                    label="时间文本"
                    v-model:value="optionData.isDateText"
                    elMarginBottom="10px"
                />
                <template v-if="optionData.isDateText">
                    <CustomSwitch label="展示秒钟" v-model:value="optionData.dateOption.showSeconds" elMarginBottom="10px"/>
                    <CustomInputSelect label="字体" v-model:value="optionData.dateOption.fontFamily" :options="fontFamilyOption" />
                    <InputNumberwithLabel label="字号" :min="0" v-model:value="optionData.dateOption.fontSize" />
                    <CustomInputSelect
                        label="样式"
                        v-model:value="optionData.dateOption.fontWeight"
                        :options="fontWeightOption"
                        :sign="true"
                    />
                    <NewColorPicker v-bind="$attrs" label-placement="left" v-model:value="optionData.dateOption.textColor" label="颜色" />
                </template>
            </div>
        </template>
        <template #texts>
            <div class="wrap">
                <div class="subtitle">设定一个标记时间</div>
                <CustomDatePicker placeholder="输入日期" v-model:value="optionData.inputDate" label="输入日期"></CustomDatePicker>
                <CustomTimePicker placeholder="输入时间" v-model:value="optionData.inputTime" label="输入时间"></CustomTimePicker>
            </div>
        </template>
    </GlobalSetting>
</template>

<script setup lang="ts">
  import {PropType, ref} from "vue";
  import {
    CollapseItem,
    SettingItemBox,
    SettingItem, GlobalSetting,
  } from "@/components/Pages/ChartItemSetting";
  import { option, FontWeightEnum, FontWeightObject } from "./config";
  import {TableDataType} from "@/types/public";
  import {
    InputNumberwithLabel,
    CustomInputNumberWithSlider,
    CustomInputSelect,
    CustomSwitch,
    CustomTimePicker,
    CustomInput,
    NewColorPicker,
    CustomRadio,
    CustomDatePicker,
    LabelStyleRadio
  } from '@/components/Form'
  import { fontFamilyOption, fontWeightOption } from '@/components/Pages/ChartItemSetting/globalSettings/config'

  const props = defineProps({
    optionData: {
      type: Object as PropType<typeof option>,
      required: true,
    },
  });
  const tabData = ref<TableDataType[]>([
    {
        name: '样式',
        slotName: 'styles'
    },
    {
        name: '设定',
        slotName: 'texts'
    }
  ])

  const formatterOption = [
    { label: '星期-时间', value: 'weekTime' },
    { label: '时间-星期', value: 'timeWeek' },
  ]

  // 先写死太多了，后期加时区统一改
  const optionType = [
    { label: '2024年01月01日 08:00:00', value: '1' },
    { label: '2024年1月1日 08:00:00', value: '2' },
    { label: '2024-01-01 08:00:00', value: '3' },
    { label: '2024-1-1 08:00:00', value: '4' },
    { label: '2024.1.1 08:00:00', value: '5' },
    { label: '2024.01.01 08:00:00', value: '6' },
    { label: '2024年1月1日', value: '7' },
    { label: '2024-1-1', value: '8' },
    { label: '2024-01-01', value: '9' },
    { label: '2024/01/01', value: '10' },
    { label: '1月1日', value: '11' },
    { label: '01月01日', value: '12' },
    { label: '01月01日01时', value: '13' },
    { label: '1-1', value: '14' },
    { label: '01-01', value: '15' },
    { label: '1.1', value: '16' },
    { label: '1月', value: '17' },
    { label: '01月', value: '18' },
    { label: '1(月)', value: '19' },
    { label: '01(月)', value: '20' },
    { label: '1日', value: '21' },
    { label: '01日', value: '22' },
    { label: '1(日)', value: '23' },
    { label: '星期一', value: '24' }
  ]

  const styleOption = [
    { label: '一行', value: 'one' },
    { label: '两行', value: 'two' },
  ]

  const fontWeightOptions = [
    {
      label: FontWeightEnum.NORMAL,
      value: FontWeightObject[FontWeightEnum.NORMAL],
    },
    {
      label: FontWeightEnum.BOLD,
      value: FontWeightObject[FontWeightEnum.BOLD],
    },
  ];
  </script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  .subtitle {
    height: 17px;
    font-size: 12px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
    color: #ffffff;
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
