<template>
  <n-tabs type="line" justify-content="space-around" animated>
    <template v-if="!isUseCustom">
      <n-tab-pane name="样式" tab="样式" v-if="tabs.includes('style')">
        <!-- @vue-ignore -->
        <StyleSetting v-bind="$attrs" :show-grid="showGrid">
          <slot name="style-setting"></slot>
        </StyleSetting>
      </n-tab-pane>
      <n-tab-pane name="配色" tab="配色" v-if="tabs.includes('series')">
        <template v-if="useSelfColorConfig">
          <slot name="series-setting"></slot>
        </template>
         <template  v-else>
          <!-- @vue-ignore -->
          <SeriesSetting v-bind="$attrs">
            <slot name="series-setting"></slot>
          </SeriesSetting>
         </template>
      </n-tab-pane>
      <n-tab-pane name="轴线" tab="轴线" v-if="tabs.includes('axis')">
        <!-- @vue-ignore -->
        <AxisSetting v-bind="$attrs" :show-axis="showAxis">
          <slot name="axis-setting"></slot>
        </AxisSetting>
      </n-tab-pane>
      <n-tab-pane name="其他" tab="其他" v-if="tabs.includes('other')">
        <!-- @vue-ignore -->
        <OtherSetting v-bind="$attrs">
          <template #legend>
            <slot name="other-setting-legend" />
          </template>
          <template #tooltip>
            <slot name="other-setting-tooltip" />
          </template>
          <slot name="other-setting" />
        </OtherSetting>
      </n-tab-pane>
    </template>
    <!-- 自定义tab -->
    <template v-else>
      <n-tab-pane
        v-for="(it, idx) in tabData"
        :key="`tab-${idx}`"
        :name="it.name"
        :tab="it.name"
      >
        <slot :name="it.slotName"></slot>
      </n-tab-pane>
    </template>
  </n-tabs>
</template>

<script setup lang="ts">
import { TableDataType } from "@/types/public.d";
import { StyleSetting, SeriesSetting, AxisSetting, OtherSetting } from './globalSettings'

defineProps({
  tabs: {
    type: Array<string>,
    default: ['style', 'series', 'axis', 'other']
  },
  isUseCustom: {
    type: Boolean,
    default: false
  },
  tabData: {
    type: Array<TableDataType>,
    default: () => [
      {
        name: '样式',
        slotName: 'style'
      }
    ]
  },
  showGrid: {
    type: Boolean,
    default: true
  },
  showAxis: {
    type: Boolean,
    default: true
  },
  useSelfColorConfig: {
    type: Boolean,
    default: false
  }
})

// console.log('GlobalSetting收到的值', props.optionData)
</script>

<style lang="scss" scoped>
@import "@/styles/pages/form.scss";

.font {
  display: flex;
  align-items: center;
}
:deep(.n-tabs-bar) {
  transform: translateX(-25px);
  max-width: 80px !important;
}
.icon {
  width: 28px;
  height: 28px;
  background: #0e1216 #212b40;
  border: 1px solid $primary-color;
}
.n-space {
  padding: 0 20px 0 10px;
  margin-bottom: 15px;
  align-items: center;
  &.flex_between {
    justify-content: space-between !important;
  }
  &.flex_start {
    align-items: flex-start;
  }
  .n-text {
    display: inline-block;
    min-width: 70px;
    font-size: 12px;
    color: #808792;
  }
  .n-select,
  .n-input {
    width: 216px;
  }
  .n-slider {
    width: 115px;
  }
  .n-input-number {
    &.small {
      max-width: 85px;
    }
  }
  .n-color-picker {
    width: 28px;
    height: 28px;
  }
  :deep(.n-color-picker-trigger__value) {
    display: none;
  }
  .right {
    width: 216px;
    display: flex;
    align-items: center;
    .sqare {
      width: 28px;
      height: 28px;
    }
  }
}
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
h6 {
  padding-left: 10px;
  margin-bottom: 15px;
}

.btn {
  width: 100%;
  display: grid;
  place-items: center;
  margin-top: 20px;
  .add_series {
    color: #fff;
    &:hover {
      color: #fff;
    }
  }
}

.series_group {
  .tit {
    display: flex;
    justify-content: space-between;
    padding-right: 20px;
    i {
      cursor: pointer;
    }
  }
}

:deep(.n-tabs-nav) {
  margin-bottom: 16px;
}
.n-tab-pane {
  padding-top: 0 !important;
  :deep(.n-collapse-item__header-main) {
    height: 25.59px;
  }
}
</style>
