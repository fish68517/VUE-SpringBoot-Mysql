<!--
 * @Author: kaix
 * @Date: 2023-08-08 11:11:06
 * @LastEditTime: 2025-01-22 09:11:06
 * @LastEditors: wangcong
 * @Description: 轴线的全局配置
-->
<template>
  <!-- 单X轴 -->
  <template v-if="!Array.isArray(xAxis)">
    <collapse-item
      v-if="xAxis && showAxis"
      name="X轴设置"
      :expanded="xAxis.show"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="xAxis.show"></n-switch>
      </template>
      <div class="wrap">
        <CustomRadio label="x轴位置" v-model:value="xAxis.position" :options="selectxAxisPositionOption" />

        <n-divider n-divider style="margin: 1px 0 16px"></n-divider>

        <div class="subtitle">文本样式</div>
        <CustomInputSelect label="字体" v-model:value="xAxis.axisLabel.fontFamily" :options="fontFamilyOption" />

        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="xAxis.axisLabel.fontSize" :min="1" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>

        <!-- 可能要注释的地方 -->
        <CustomInputSelect
          label="样式"
          v-model:value="xAxis.axisLabel.fontWeight"
          :options="fontWeightOption"
          :sign="true"
        />

        <NewColorPicker v-bind="$attrs" label-placement="left" label="颜色" v-model:value="xAxis.axisLabel.color" />

        <n-divider n-divider style="margin: 16px 0"></n-divider>

        <div class="subtitle">轴标签</div>
        <template v-if="xAxisType === 'category'">
          <CustomInputNumberWithSlider
            label="标签倾斜度"
            v-model:value="xAxis.axisLabel.rotate"
            :min="0"
            :max="360"
            :step="1"
          />
          <n-form label-placement="left" label-width="70">
            <n-form-item label="标签间隔">
              <CustomInputNumber v-model:value="xAxis.axisLabel.interval" :prefix="false" el-width="100%" />
            </n-form-item>
          </n-form>
          <LabelStyleRadio
            label="标签展示"
            v-model:value="newAttr.xAxisLabelType"
            v-model:number="newAttr.xAxisLabelCount"
          />
          <CustomInputNumberWithSlider label="与轴线距离" v-model:value="xAxis.axisLabel.margin" />
        </template>
        <template v-else-if="xAxisType === 'value'">
          <n-form label-placement="left">
            <n-form-item label="最小值">
              <CustomInputNumber v-model:value="xAxis.min" :min="0" :prefix="false" el-width="100%" />
            </n-form-item>
            <CustomInput v-model:value="xAxis.max" :min="0" el-width="100%" label="最大值" labelPlacement="left" />
            <!-- <n-form-item label="标签数量">
              <CustomInputNumber
                v-model:value="xAxis.splitNumber"
                :prefix="false"
                el-width="100%"
              />
            </n-form-item> -->
          </n-form>
        </template>

        <n-divider n-divider style="margin: 16px 0 10px"></n-divider>

        <CustomSwitch label="轴单位" v-model:value="newAttr.showXaxisName" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="newAttr.showXaxisName">
          <CustomInput label="名称" labelPlacement="left" v-model:value="xAxis.name" placeholder="请输入单位" />
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis.nameTextStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="字号">
              <CustomInputNumber
                v-model:value="xAxis.nameTextStyle.fontSize"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="左右边距">
              <CustomInputNumber
                v-model:value="xAxis.nameTextStyle.padding[3]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="上下边距">
              <CustomInputNumber
                v-model:value="xAxis.nameTextStyle.padding[0]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch label="轴线" v-model:value="xAxis.axisLine.show" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="xAxis.axisLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis.axisLine.lineStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="粗细">
              <CustomInputNumber
                v-model:value="xAxis.axisLine.lineStyle.width"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch label="网格线" v-model:value="xAxis.splitLine.show" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="xAxis.splitLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis.splitLine.lineStyle.color"
          />
          <CustomInputSelect
            label="类型"
            v-model:value="xAxis.splitLine.lineStyle.type"
            :options="selectSplitLineTypeOption"
          />
        </div>
      </div>
    </collapse-item>
  </template>
  <!-- 双X轴 -->
  <template v-else>
    <collapse-item
      v-if="xAxis && showAxis"
      name="X轴设置(上方)"
      :expanded="xAxis[0].show"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="xAxis[0].show"></n-switch>
      </template>
      <div class="wrap">
        <CustomRadio label="x轴位置" v-model:value="xAxis[0].position" :options="selectxAxisPositionOption" />

        <n-divider n-divider style="margin: 1px 0 16px"></n-divider>

        <div class="subtitle">文本样式</div>
        <CustomInputSelect label="字体" v-model:value="xAxis[0].axisLabel.fontFamily" :options="fontFamilyOption" />

        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="xAxis[0].axisLabel.fontSize" :min="1" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>

        <!-- 可能要注释的地方 -->
        <CustomInputSelect
          label="样式"
          v-model:value="xAxis[0].axisLabel.fontWeight"
          :options="fontWeightOption"
          :sign="true"
        />

        <NewColorPicker v-bind="$attrs" label-placement="left" label="颜色" v-model:value="xAxis[0].axisLabel.color" />

        <n-divider n-divider style="margin: 16px 0"></n-divider>

        <div class="subtitle">轴标签</div>
        <template v-if="xAxisType === 'category'">
          <CustomInputNumberWithSlider
            label="标签倾斜度"
            v-model:value="xAxis[0].axisLabel.rotate"
            :min="0"
            :max="360"
            :step="1"
          />
          <n-form label-placement="left" label-width="70">
            <n-form-item label="标签间隔">
              <CustomInputNumber v-model:value="xAxis[0].axisLabel.interval" :prefix="false" el-width="100%" />
            </n-form-item>
          </n-form>
          <LabelStyleRadio
            label="标签展示"
            v-model:value="newAttr.xAxisLabelType"
            v-model:number="newAttr.xAxisLabelCount"
          />
          <CustomInputNumberWithSlider label="与轴线距离" v-model:value="xAxis[0].axisLabel.margin" />
        </template>
        <template v-else-if="xAxis[0].type === 'value'">
          <n-form label-placement="left">
            <n-form-item label="最小值">
              <CustomInputNumber v-model:value="xAxis[0].min" :min="0" :prefix="false" el-width="100%" />
            </n-form-item>
            <CustomInput v-model:value="xAxis[0].max" :min="0" el-width="100%" label="最大值" labelPlacement="left" />
            <!-- <n-form-item label="标签数量">
              <CustomInputNumber
                v-model:value="xAxis[0].splitNumber"
                :prefix="false"
                el-width="100%"
              />
            </n-form-item> -->
          </n-form>
        </template>

        <n-divider n-divider style="margin: 16px 0 10px"></n-divider>

        <CustomSwitch label="轴单位" v-model:value="newAttr.showXaxisName" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="newAttr.showXaxisName">
          <CustomInput label="名称" labelPlacement="left" v-model:value="xAxis[0].name" placeholder="请输入单位" />
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis[0].nameTextStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="字号">
              <CustomInputNumber
                v-model:value="xAxis[0].nameTextStyle.fontSize"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="左右边距">
              <CustomInputNumber
                v-model:value="xAxis[0].nameTextStyle.padding[3]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="上下边距">
              <CustomInputNumber
                v-model:value="xAxis[0].nameTextStyle.padding[0]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch label="轴线" v-model:value="xAxis[0].axisLine.show" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="xAxis[0].axisLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis[0].axisLine.lineStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="粗细">
              <CustomInputNumber
                v-model:value="xAxis[0].axisLine.lineStyle.width"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch
          label="网格线"
          v-model:value="xAxis[0].splitLine.show"
          label-color="#fff"
          el-margin-bottom="10px"
        />
        <div v-if="xAxis[0].splitLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis[0].splitLine.lineStyle.color"
          />
          <CustomInputSelect
            label="类型"
            v-model:value="xAxis[0].splitLine.lineStyle.type"
            :options="selectSplitLineTypeOption"
          />
        </div>
      </div>
    </collapse-item>

    <n-divider style="margin: 9px 0" v-if="yAxis && showAxis"></n-divider>

    <collapse-item
      v-if="xAxis && showAxis"
      name="X轴设置(下方)"
      :expanded="xAxis[1].show"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="xAxis[1].show"></n-switch>
      </template>
      <div class="wrap">
        <CustomRadio label="x轴位置" v-model:value="xAxis[1].position" :options="selectxAxisPositionOption" />

        <n-divider n-divider style="margin: 1px 0 16px"></n-divider>

        <div class="subtitle">文本样式</div>
        <CustomInputSelect label="字体" v-model:value="xAxis[1].axisLabel.fontFamily" :options="fontFamilyOption" />

        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="xAxis[1].axisLabel.fontSize" :min="1" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>

        <!-- 可能要注释的地方 -->
        <CustomInputSelect
          label="样式"
          v-model:value="xAxis[1].axisLabel.fontWeight"
          :options="fontWeightOption"
          :sign="true"
        />

        <NewColorPicker v-bind="$attrs" label-placement="left" label="颜色" v-model:value="xAxis[1].axisLabel.color" />

        <n-divider n-divider style="margin: 16px 0"></n-divider>

        <div class="subtitle">轴标签</div>
        <template v-if="xAxisType === 'category'">
          <CustomInputNumberWithSlider
            label="标签倾斜度"
            v-model:value="xAxis[1].axisLabel.rotate"
            :min="0"
            :max="360"
            :step="1"
          />
          <n-form label-placement="left" label-width="70">
            <n-form-item label="标签间隔">
              <CustomInputNumber v-model:value="xAxis[1].axisLabel.interval" :prefix="false" el-width="100%" />
            </n-form-item>
          </n-form>
          <LabelStyleRadio
            label="标签展示"
            v-model:value="newAttr.xAxisLabelType"
            v-model:number="newAttr.xAxisLabelCount"
          />
          <CustomInputNumberWithSlider label="与轴线距离" v-model:value="xAxis[1].axisLabel.margin" />
        </template>
        <template v-else-if="xAxis[1].type === 'value'">
          <n-form label-placement="left">
            <n-form-item label="最小值">
              <CustomInputNumber v-model:value="xAxis[1].min" :min="0" :prefix="false" el-width="100%" />
            </n-form-item>
            <CustomInput v-model:value="xAxis[1].max" :min="0" el-width="100%" label="最大值" labelPlacement="left" />
            <!-- <n-form-item label="标签数量">
              <CustomInputNumber
                v-model:value="xAxis[1].splitNumber"
                :prefix="false"
                el-width="100%"
              />
            </n-form-item> -->
          </n-form>
        </template>

        <n-divider n-divider style="margin: 16px 0 10px"></n-divider>

        <CustomSwitch label="轴单位" v-model:value="newAttr.showXaxisName" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="newAttr.showXaxisName">
          <CustomInput label="名称" labelPlacement="left" v-model:value="xAxis[1].name" placeholder="请输入单位" />
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis[1].nameTextStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="字号">
              <CustomInputNumber
                v-model:value="xAxis[1].nameTextStyle.fontSize"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="左右边距">
              <CustomInputNumber
                v-model:value="xAxis[1].nameTextStyle.padding[3]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="上下边距">
              <CustomInputNumber
                v-model:value="xAxis[1].nameTextStyle.padding[0]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch label="轴线" v-model:value="xAxis[1].axisLine.show" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="xAxis[1].axisLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis[1].axisLine.lineStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="粗细">
              <CustomInputNumber
                v-model:value="xAxis[1].axisLine.lineStyle.width"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch
          label="网格线"
          v-model:value="xAxis[1].splitLine.show"
          label-color="#fff"
          el-margin-bottom="10px"
        />
        <div v-if="xAxis[1].splitLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="xAxis[1].splitLine.lineStyle.color"
          />
          <CustomInputSelect
            label="类型"
            v-model:value="xAxis[1].splitLine.lineStyle.type"
            :options="selectSplitLineTypeOption"
          />
        </div>
      </div>
    </collapse-item>
  </template>
  <n-divider style="margin: 9px 0" v-if="yAxis && showAxis"></n-divider>

  <!-- 单y轴 -->
  <template v-if="!Array.isArray(yAxis)">
    <collapse-item
      v-if="yAxis && showAxis"
      name="Y轴设置"
      :expanded="yAxis.show"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="yAxis.show"></n-switch>
      </template>
      <div class="wrap">
        <CustomRadio label="y轴位置" v-model:value="yAxis.position" :options="selectyAxisPositionOption" />

        <n-divider n-divider style="margin: 1px 0 16px"></n-divider>

        <!-- 文本样式 -->
        <div class="subtitle">文本样式</div>
        <CustomInputSelect label="字体" v-model:value="yAxis.axisLabel.fontFamily" :options="fontFamilyOption" />

        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="yAxis.axisLabel.fontSize" :min="1" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>

        <!-- 可能要注释的地方 -->
        <CustomInputSelect
          label="样式"
          v-model:value="yAxis.axisLabel.fontWeight"
          :options="fontWeightOption"
          :sign="true"
        />

        <NewColorPicker v-bind="$attrs" label-placement="left" label="颜色" v-model:value="yAxis.axisLabel.color" />

        <n-divider n-divider style="margin: 16px 0"></n-divider>

        <div class="subtitle">轴标签</div>
        <template v-if="yAxisType === 'value'">
          <n-form label-placement="left">
            <CustomInput
              v-model:value="yAxis.min"
              el-width="100%"
              label="最小值"
              :min="-Infinity"
              :max="Infinity"
              labelPlacement="left"
            />
            <CustomInput
              v-model:value="yAxis.max"
              :min="yAxis.min"
              :max="Infinity"
              el-width="100%"
              label="最大值"
              labelPlacement="left"
            />
          </n-form>
          <CustomCheckbox label="刻度自适应" v-model:value="yAxis.isScaleSelfadaption" :options="valueOption" />
          <div v-if="yAxis.isScaleSelfadaption && yAxis.isScaleSelfadaption[0] !== '1'">
            <CustomRadio label="刻度模式" v-model:value="yAxis.scaleType" :options="scaleTypeOption" />
            <n-form label-placement="left" label-width="70">
              <n-form-item label="标签数量" v-if="yAxis.scaleType === '1'">
                <CustomInputNumber
                  v-model:value="yAxis.mySplitNumber"
                  el-width="100%"
                  :prefix="false"
                  :min="2"
                  :max="Infinity"
                />
              </n-form-item>
              <n-form-item label="步长" v-if="yAxis.scaleType === '2'">
                <CustomInputNumber
                  v-model:value="yAxis.myMinInterval"
                  el-width="100%"
                  :prefix="false"
                  :min="0"
                  :max="Infinity"
                />
              </n-form-item>
            </n-form>
          </div>
        </template>
        <template v-else-if="yAxisType === 'category'">
          <CustomInputNumberWithSlider
            label="标签倾斜度"
            v-model:value="yAxis.axisLabel.rotate"
            :min="0"
            :max="360"
            :step="1"
          />
          <n-form label-placement="left" label-width="70">
            <n-form-item label="标签间隔">
              <CustomInputNumber v-model:value="yAxis.axisLabel.interval" :prefix="false" el-width="100%" />
            </n-form-item>
          </n-form>
          <LabelStyleRadio
            label="标签展示"
            v-model:value="newAttr.xAxisLabelType"
            v-model:number="newAttr.xAxisLabelCount"
          />
          <CustomInputNumberWithSlider label="与轴线距离" v-model:value="yAxis.axisLabel.margin" />
        </template>

        <n-divider n-divider style="margin: 16px 0 10px"></n-divider>

        <CustomSwitch label="轴单位" v-model:value="newAttr.showYaxisName" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="newAttr.showYaxisName">
          <CustomInput label="名称" labelPlacement="left" v-model:value="yAxis.name" placeholder="请输入单位" />
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis.nameTextStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="字号">
              <CustomInputNumber
                v-model:value="yAxis.nameTextStyle.fontSize"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="左右边距">
              <CustomInputNumber
                v-model:value="yAxis.nameTextStyle.padding[3]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="上下边距">
              <CustomInputNumber
                v-model:value="yAxis.nameTextStyle.padding[0]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch label="轴线" v-model:value="yAxis.axisLine.show" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="yAxis.axisLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis.axisLine.lineStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="粗细">
              <CustomInputNumber
                v-model:value="yAxis.axisLine.lineStyle.width"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch label="网格线" v-model:value="yAxis.splitLine.show" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="yAxis.splitLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis.splitLine.lineStyle.color"
          />
          <CustomInputSelect
            label="类型"
            v-model:value="yAxis.splitLine.lineStyle.type"
            :options="selectSplitLineTypeOption"
          />
        </div>
      </div>
    </collapse-item>
  </template>
  <!-- 双y轴 -->
  <template v-else>
    <collapse-item
      v-if="yAxis && showAxis"
      name="Y轴设置(左侧)"
      :expanded="yAxis[0].show"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="yAxis[0].show"></n-switch>
      </template>
      <div class="wrap">
        <CustomRadio label="y轴位置" v-model:value="yAxis[0].position" :options="selectyAxisPositionOption" />

        <n-divider n-divider style="margin: 1px 0 16px"></n-divider>

        <!-- 文本样式 -->
        <div class="subtitle">文本样式</div>
        <CustomInputSelect label="字体" v-model:value="yAxis[0].axisLabel.fontFamily" :options="fontFamilyOption" />

        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="yAxis[0].axisLabel.fontSize" :min="1" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>

        <!-- 可能要注释的地方 -->
        <CustomInputSelect
          label="样式"
          v-model:value="yAxis[0].axisLabel.fontWeight"
          :options="fontWeightOption"
          :sign="true"
        />

        <NewColorPicker v-bind="$attrs" label-placement="left" label="颜色" v-model:value="yAxis[0].axisLabel.color" />

        <n-divider n-divider style="margin: 16px 0"></n-divider>

        <div class="subtitle">轴标签</div>
        <template v-if="yAxis[0].type === 'value'">
          <n-form label-placement="left">
            <!-- <n-form-item label="最小值">
              <CustomInputNumber
                v-model:value="yAxis[0].min"
                :min="-99999999"
                :prefix="false"
                el-width="100%"
              />
            </n-form-item> -->
            <CustomInput
              v-model:value="yAxis[0].min"
              :min="-99999999"
              el-width="100%"
              label="最小值"
              labelPlacement="left"
            />
            <CustomInput
              v-model:value="yAxis[0].maxNum"
              :min="-99999999"
              el-width="100%"
              label="最大值"
              labelPlacement="left"
            />
            <CustomCheckbox label="刻度自适应" v-model:value="yAxis[0].isScaleSelfadaption" :options="valueOption" />
            <!-- <CustomRadio
              label="刻度自适应"
              v-model:value="yAxis.isScaleSelfadaption"
              :options="valueOption"
            /> -->
            <div v-if="yAxis[0].isScaleSelfadaption && yAxis[0].isScaleSelfadaption[0] !== '1'">
              <CustomRadio label="刻度模式" v-model:value="yAxis[0].scaleType" :options="scaleTypeOption1" />
              <n-form label-placement="left" label-width="70">
                <!-- <n-form-item label="标签数量" v-if="yAxis[0].scaleType === '1'">
                  <CustomInputNumber
                    v-model:value="yAxis[0].mySplitNumber"
                    el-width="100%"
                    :prefix="false"
                    :min="0"
                    :max="9999999998"
                  />
                </n-form-item> -->
                <n-form-item label="步长" v-if="yAxis[0].scaleType === '2'">
                  <CustomInputNumber
                    v-model:value="yAxis[0].myMinInterval"
                    el-width="100%"
                    :prefix="false"
                    :min="0"
                    :max="9999999998"
                  />
                </n-form-item>
              </n-form>
            </div>
            <!-- <n-form-item label="标签数量">
              <CustomInputNumber
                v-model:value="yAxis[0].splitNumber"
                :prefix="false"
                el-width="100%"
              />
            </n-form-item> -->
          </n-form>
        </template>
        <template v-else-if="yAxis[0].type === 'category'">
          <CustomInputNumberWithSlider
            label="标签倾斜度"
            v-model:value="yAxis[0].axisLabel.rotate"
            :min="0"
            :max="360"
            :step="1"
          />
          <n-form label-placement="left" label-width="70">
            <n-form-item label="标签间隔">
              <CustomInputNumber v-model:value="yAxis[0].axisLabel.interval" :prefix="false" el-width="100%" />
            </n-form-item>
          </n-form>
          <LabelStyleRadio
            label="标签展示"
            v-model:value="newAttr.xAxisLabelType"
            v-model:number="newAttr.xAxisLabelCount"
          />
          <CustomInputNumberWithSlider label="与轴线距离" v-model:value="yAxis[0].axisLabel.margin" />
        </template>

        <n-divider n-divider style="margin: 16px 0 10px"></n-divider>

        <CustomSwitch label="轴单位" v-model:value="newAttr.showYaxisName" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="newAttr.showYaxisName">
          <CustomInput label="名称" labelPlacement="left" v-model:value="yAxis[0].name" placeholder="请输入单位" />
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis[0].nameTextStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="字号">
              <CustomInputNumber
                v-model:value="yAxis[0].nameTextStyle.fontSize"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="左右边距">
              <CustomInputNumber
                v-model:value="yAxis[0].nameTextStyle.padding[3]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="上下边距">
              <CustomInputNumber
                v-model:value="yAxis[0].nameTextStyle.padding[0]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch label="轴线" v-model:value="yAxis[0].axisLine.show" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="yAxis[0].axisLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis[0].axisLine.lineStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="粗细">
              <CustomInputNumber
                v-model:value="yAxis[0].axisLine.lineStyle.width"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch
          label="网格线"
          v-model:value="yAxis[0].splitLine.show"
          label-color="#fff"
          el-margin-bottom="10px"
        />
        <div v-if="yAxis[0].splitLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis[0].splitLine.lineStyle.color"
          />
          <CustomInputSelect
            label="类型"
            v-model:value="yAxis[0].splitLine.lineStyle.type"
            :options="selectSplitLineTypeOption"
          />
        </div>
      </div>
    </collapse-item>

    <n-divider style="margin: 9px 0" v-if="yAxis && showAxis"></n-divider>

    <collapse-item
      v-if="yAxis && showAxis"
      name="Y轴设置(右侧)"
      :expanded="yAxis[1].show"
      :isControl="true"
      divider-margin="margin: 9px 0 11px"
    >
      <template #header>
        <n-switch v-model:value="yAxis[1].show"></n-switch>
      </template>
      <div class="wrap">
        <CustomRadio label="y轴位置" v-model:value="yAxis[1].position" :options="selectyAxisPositionOption" />

        <n-divider n-divider style="margin: 1px 0 16px"></n-divider>

        <!-- 文本样式 -->
        <div class="subtitle">文本样式</div>
        <CustomInputSelect label="字体" v-model:value="yAxis[1].axisLabel.fontFamily" :options="fontFamilyOption" />

        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="yAxis[1].axisLabel.fontSize" :min="1" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>

        <!-- 可能要注释的地方 -->
        <CustomInputSelect
          label="样式"
          v-model:value="yAxis[1].axisLabel.fontWeight"
          :options="fontWeightOption"
          :sign="true"
        />

        <NewColorPicker v-bind="$attrs" label-placement="left" label="颜色" v-model:value="yAxis[1].axisLabel.color" />

        <n-divider n-divider style="margin: 16px 0"></n-divider>

        <div class="subtitle">轴标签</div>
        <template v-if="yAxis[1].type === 'value'">
          <n-form label-placement="left">
            <!-- <n-form-item label="最小值">
              <CustomInputNumber
                v-model:value="yAxis[1].min"
                :min="-99999999"
                :prefix="false"
                el-width="100%"
              />
            </n-form-item> -->
            <CustomInput
              v-model:value="yAxis[1].min"
              :min="-99999999"
              el-width="100%"
              label="最小值"
              labelPlacement="left"
            />
            <CustomInput
              v-model:value="yAxis[1].maxNum"
              :min="99999999"
              el-width="100%"
              label="最大值"
              labelPlacement="left"
            />
            <!-- <n-form-item label="标签数量">
              <CustomInputNumber
                v-model:value="yAxis[1].splitNumber"
                :prefix="false"
                el-width="100%"
              />
            </n-form-item> -->
            <CustomCheckbox label="刻度自适应" v-model:value="yAxis[1].isScaleSelfadaption" :options="valueOption" />
            <!-- <CustomRadio
              label="刻度自适应"
              v-model:value="yAxis.isScaleSelfadaption"
              :options="valueOption"
            /> -->
            <div v-if="yAxis[1].isScaleSelfadaption && yAxis[1].isScaleSelfadaption[0] !== '1'">
              <CustomRadio label="刻度模式" v-model:value="yAxis[1].scaleType" :options="scaleTypeOption1" />
              <n-form label-placement="left" label-width="70">
                <!-- <n-form-item label="标签数量" v-if="yAxis[1].scaleType === '1'">
                  <CustomInputNumber
                    v-model:value="yAxis[1].mySplitNumber"
                    el-width="100%"
                    :prefix="false"
                    :min="0"
                    :max="9999999998"
                  />
                </n-form-item> -->
                <n-form-item label="步长" v-if="yAxis[1].scaleType === '2'">
                  <CustomInputNumber
                    v-model:value="yAxis[1].myMinInterval"
                    el-width="100%"
                    :prefix="false"
                    :min="0"
                    :max="9999999998"
                  />
                </n-form-item>
              </n-form>
            </div>
          </n-form>
        </template>
        <template v-else-if="yAxis[1].type === 'category'">
          <CustomInputNumberWithSlider
            label="标签倾斜度"
            v-model:value="yAxis[1].axisLabel.rotate"
            :min="0"
            :max="360"
            :step="1"
          />
          <n-form label-placement="left" label-width="70">
            <n-form-item label="标签间隔">
              <CustomInputNumber v-model:value="yAxis[1].axisLabel.interval" :prefix="false" el-width="100%" />
            </n-form-item>
          </n-form>
          <LabelStyleRadio
            label="标签展示"
            v-model:value="newAttr.xAxisLabelType"
            v-model:number="newAttr.xAxisLabelCount"
          />
          <CustomInputNumberWithSlider label="与轴线距离" v-model:value="yAxis[1].axisLabel.margin" />
        </template>

        <n-divider n-divider style="margin: 16px 0 10px"></n-divider>

        <CustomSwitch label="轴单位" v-model:value="newAttr.showYaxisName" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="newAttr.showYaxisName">
          <CustomInput label="名称" labelPlacement="left" v-model:value="yAxis[1].name" placeholder="请输入单位" />
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis[1].nameTextStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="字号">
              <CustomInputNumber
                v-model:value="yAxis[1].nameTextStyle.fontSize"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="左右边距">
              <CustomInputNumber
                v-model:value="yAxis[1].nameTextStyle.padding[3]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
          <n-form label-placement="left">
            <n-form-item label="上下边距">
              <CustomInputNumber
                v-model:value="yAxis[1].nameTextStyle.padding[0]"
                :prefix="false"
                el-width="100%"
                :min="-9999"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch label="轴线" v-model:value="yAxis[1].axisLine.show" label-color="#fff" el-margin-bottom="10px" />
        <div v-if="yAxis[1].axisLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis[1].axisLine.lineStyle.color"
          />
          <n-form label-placement="left">
            <n-form-item label="粗细">
              <CustomInputNumber
                v-model:value="yAxis[1].axisLine.lineStyle.width"
                :prefix="false"
                :min="1"
                el-width="100%"
              />
            </n-form-item>
          </n-form>
        </div>

        <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

        <CustomSwitch
          label="网格线"
          v-model:value="yAxis[1].splitLine.show"
          label-color="#fff"
          el-margin-bottom="10px"
        />
        <div v-if="yAxis[1].splitLine.show">
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="颜色"
            v-model:value="yAxis[1].splitLine.lineStyle.color"
          />
          <CustomInputSelect
            label="类型"
            v-model:value="yAxis[1].splitLine.lineStyle.type"
            :options="selectSplitLineTypeOption"
          />
        </div>
      </div>
    </collapse-item>
  </template>
  <!-- 每个组件单独设置的配置 -->
  <slot />
</template>

<script setup lang="ts">
import { PropType, computed, reactive, ref, watch } from 'vue'
import { GlobalThemeJsonType } from '@/package/index.d'
import {
  CustomInput,
  CustomInputNumber,
  CustomInputNumberWithSlider,
  CustomInputSelect,
  NewColorPicker,
  CustomSwitch,
  CustomRadio,
  LabelStyleRadio,
  CustomCheckbox
} from '@/components/Form'
import {
  CollapseItem,
  SettingItemBox,
  SettingItem,
  GlobalSettingPosition,
  ColorSetting,
  RadioSelectCustom
} from '@/components/Pages/ChartItemSetting'
import { fontFamilyOption, fontWeightOption } from './config'
import { getUUID } from '@/utils'
import { useLoadOption } from '@/package/config/useChartOption'
import { getYInterval } from '@/utils'

const { useXAxis, useYAxis } = useLoadOption()

const props = defineProps({
  optionData: {
    type: Object as PropType<any>,
    required: true
  },
  inChart: {
    type: Boolean,
    required: false,
    default: false
  },
  newAttr: {
    type: Object,
    default: () => {}
  },
  showAxis: {
    type: Boolean,
    default: true
  }
})

const xAxis = computed(() => {
  return props.optionData?.xAxis ?? useXAxis()
})

const xAxisType = computed(() => {
  return xAxis.value?.type
})

const yAxis = computed(() => {
  return props.optionData?.yAxis ?? useYAxis()
})

const yAxisType = computed(() => {
  return yAxis.value?.type
})

if (Array.isArray(props.optionData.yAxis)) {
  props.optionData.yAxis[0].scaleType = '2'
  props.optionData.yAxis[1].scaleType = '2'
  watch(
    () => props.optionData.yAxis[0].name,
    val => {
      props.optionData.chartKey = getUUID()
    },
    { deep: true }
  )
}
const valueOption = [
  {
    value: '1',
    label: ''
  }
]
const scaleTypeOption = [
  {
    value: '1',
    label: '数量强制'
  },
  {
    value: '2',
    label: '步长强制'
  }
]
const scaleTypeOption1 = [
  {
    value: '2',
    label: '步长强制'
  }
]
const selectxAxisPositionOption = [
  { label: '图表上方', value: 'top' },
  { label: '图表下方', value: 'bottom' }
]
const selectyAxisPositionOption = [
  { label: '图表左边', value: 'left' },
  { label: '图表右边', value: 'right' }
]

// 网格线
const selectSplitLineTypeOption = [
  { label: '虚线', value: 'dashed' },
  { label: '实线', value: 'solid' },
  { label: '点', value: 'dotted' }
]

// 是否展示x轴单位
watch(
  () => props.newAttr.showXaxisName,
  val => {
    if (!val) {
      xAxis.value.name = ''
    }
  },
  {
    immediate: true
  }
)

// 兼容x轴y轴名称位置调整
if (props.showAxis) {
  if (Array.isArray(xAxis.value)) {
    xAxis.value.forEach(item => {
      item.nameTextStyle.verticalAlign = 'top'
    })
  } else {
    xAxis.value.nameTextStyle.verticalAlign = 'top'
  }
  if (Array.isArray(yAxis.value)) {
    yAxis.value.forEach(item => {
      item.nameTextStyle.verticalAlign = 'top'
    })
  } else {
    yAxis.value.nameTextStyle.verticalAlign = 'top'
  }
}
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
