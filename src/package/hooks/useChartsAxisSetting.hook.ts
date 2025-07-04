/*
 * @Author: wangcong
 * @Date: 2023-11-17 09:39:31
 * @LastEditTime: 2024-01-25 09:59:04
 * @LastEditors: zouying
 * @Description: 
 */
import { CreateComponentType } from "@/package/index.d"
import { isArray } from "lodash";
import {watch} from 'vue'



// 监听标签展示
const allFormart = (v: any) => {
  // 全部展示
  return v;
};
const wordSpceFormart = (num: number) => {
  // 超过num个字符换行
  function insertElementAtInterval(interval: number, array: Array<string>) {
    for (var i = interval; i < array.length; i += interval + 1) {
      array.splice(i, 0, "\n");
    }
    return array.join("");
  }
  return function (v: any) {
    let arr = v.split("");
    return insertElementAtInterval(num, arr);
  };
};
const ellispsFormart = (num: number) => {
  // 超过num个字符省略
  return function (v: any) {
    let arr = v.split("");
    let { length } = arr;
    arr = arr.slice(0, num);
    if (length > num) {
      arr.push("...");
    }
    return arr.join("");
  };
};




export const useChartsAxisSetting = (config: CreateComponentType) => {
  const watchXaxisLabel = () => {
    const {newAttr, option: {xAxis, yAxis}} = config
    if (!newAttr) return
    watch(
      [() => newAttr.xAxisLabelType, () => newAttr.xAxisLabelCount],
      ([xAxisLabelType, xAxisLabelCount]) => {
        if (!xAxisLabelCount) return;
        switch (xAxisLabelType) {
          case "all": {
            if (xAxis.type === "category") {
              xAxis.axisLabel.formatter = allFormart;
            } else if (yAxis.type === "category") {
              yAxis.axisLabel.formatter = allFormart;
              console.log(yAxis)
            }
            break;
          }
          case "wordspace": {
            if (xAxis.type === "category") {
              xAxis.axisLabel.formatter = wordSpceFormart(xAxisLabelCount);
            } else if (yAxis.type=== "category") {
              yAxis.axisLabel.formatter = wordSpceFormart(xAxisLabelCount);
            }
            break;
          }
          case "ellisps": {
            if (xAxis.type === "category") {
              xAxis.axisLabel.formatter = ellispsFormart(xAxisLabelCount);
            } else if (yAxis.type === "category") {
              yAxis.axisLabel.formatter = ellispsFormart(xAxisLabelCount);
            }
            break;
          }
        }
      },
      {
        immediate: true,
      }
    );
  }
  return {
    watchXaxisLabel
  }
}