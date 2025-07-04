<!-- eslint-disable vue/multi-word-component-names -->
<!--
 * @Author: kaix
 * @Date: 2023-03-13 17:02:38
 * @LastEditTime: 2023-11-07 09:50:10
 * @LastEditors: Please set LastEditors
 * @Description: 
-->
<template>
  <div>组件库测试页面</div>
  <!-- <div style="margin-bottom: 20px">
    <SButton color="blue">主要按钮</SButton>
    <SButton color="green">绿色按钮</SButton>
    <SButton color="gray">灰色按钮</SButton>
    <SButton color="yellow">黄色按钮</SButton>
    <SButton color="red">红色按钮</SButton>
  </div>
  <div style="margin-bottom: 20px">
    <SButton color="blue" plain>朴素按钮</SButton>
    <SButton color="green" plain>绿色按钮</SButton>
    <SButton color="gray" plain>灰色按钮</SButton>
    <SButton color="yellow" plain>黄色按钮</SButton>
    <SButton color="red" plain>红色按钮</SButton>
  </div>
  <div style="margin-bottom: 20px">
    <SButton size="small" plain>小按钮</SButton>
    <SButton size="medium" plain>中按钮</SButton>
    <SButton size="large" plain>大按钮</SButton>
  </div>
  <div style="margin-bottom: 20px">
    <SButton color="blue" round plain icon="search">搜索按钮</SButton>
    <SButton color="green" round plain icon="edit">编辑按钮</SButton>
    <SButton color="gray" round plain icon="check">成功按钮</SButton>
    <SButton color="yellow" round plain icon="message">提示按钮</SButton>
    <SButton color="red" round plain icon="delete">删除按钮</SButton>
  </div>
  <div style="margin-bottom: 20px">
    <SButton color="blue" round plain icon="search"></SButton>
    <SButton color="green" round plain icon="edit"></SButton>
    <SButton color="gray" round plain icon="check"></SButton>
    <SButton color="yellow" round plain icon="message"></SButton>
    <SButton color="red" round plain icon="delete"></SButton>
  </div> -->
  <!-- <JSXButton></JSXButton> -->
  <!-- <SFCButton></SFCButton> -->
  <div style="display: flex; flex-wrap: wrap">
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <!-- <bar-chart /> -->
      <VBarCommon :chartConfig="VBarCommonConfig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <!-- <bar-chart /> -->
      <lineCommon :chartConfig="lineCommonConfig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <colComponent :chartConfig="colConfig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <waterComponent :chartConfig="waterConfig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <towerComponent :chartConfig="towerConfig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <timeComponent :chartConfig="timeConfig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <numComponent :chartConfig="numConfig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <MapAmapComponent :chartConfig="AmapConfig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px;">
      <TextCommonComponent :chartConfig="TextCommonConig" />
    </div>
    <div style="margin-bottom: 20px; width: 500px; height: 400px">
      <MapLeafletComponent :chartConfig="MapLeafletConfig" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { BarCommon, LineCommon, GroupColChart, WaterPolo, BarTowerChart, TimeCommon, Number, MapAmap, MapLeaflet, TextCommon } from '@/package'

const { component: VBarCommon, config: VBarCommonConfig } = BarCommon

const { component: lineCommon, config: lineCommonConfig } = LineCommon

const { component: colComponent, config: colConfig } = GroupColChart

const { component: waterComponent, config: waterConfig } = WaterPolo

const { component: towerComponent, config: towerConfig } = BarTowerChart

const { component: timeComponent, config: timeConfig } = TimeCommon

const { component: numComponent, config: numConfig } = Number

const { component: MapAmapComponent, config: AmapConfig } = MapAmap

const { component: TextCommonComponent, config: TextCommonConig } = TextCommon

const { component: MapLeafletComponent, config: MapLeafletConfig } = MapLeaflet

</script>
