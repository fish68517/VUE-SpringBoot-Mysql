<template>
  <!-- 非受控模式 -->
  <n-collapse
    v-if="!isControl"
    arrow-placement="right"
    accordion
    :default-expanded-names="expanded ? name : null"
    @update:expanded-names="change"
    :style="defineStyle"
  >
    <template #arrow>
      <i class="iconfont icon-a-tuceng-jiantouweizhankai"></i>
    </template>

    <n-collapse-item :title="name" :name="name">
      <n-divider :style="dividerMargin"></n-divider>
      <slot></slot>
    </n-collapse-item>
  </n-collapse>

  <!-- 受控模式 -->
  <n-collapse arrow-placement="right" accordion :expanded-names="expanded ? name : null" :style="defineStyle" v-else>
    <!-- 右侧 -->
    <template #header-extra>
      <slot name="header"></slot>
    </template>

    <n-collapse-item :title="name" :name="name">
      <n-divider :style="dividerMargin"></n-divider>
      <slot></slot>
    </n-collapse-item>
  </n-collapse>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { NCollapse, NCollapseItem, NDivider } from 'naive-ui';
import { BorderBottom } from '@vicons/carbon';
const props = defineProps({
  isControl: {
    type: Boolean,
    required: false,
    default: false
  },
  name: {
    type: String,
    required: true
  },
  expanded: {
    type: Boolean,
    required: false,
    default: false
  },
  dividerMargin: {
    type: String,
    required: false,
    default: 'margin: 9px 0 16px'
  },
  defineStyle: {
    type: Object,
    required: false,
    default: {}
  }
})

const emit = defineEmits<{
  (e: 'change', val: []): void
}>()

const displayVal = computed(() => {
  return props.isControl ? 'none' : 'flex'
})

const click = (e: MouseEvent) => {
  e.preventDefault()
  e.stopPropagation()
}

const change = (val: []) => {
  emit('change', val)
}
</script>

<style lang="scss" scoped>
.n-collapse {
  :deep(.n-collapse-item) {
    .n-collapse-item__content-wrapper {
      .n-collapse-item__content-inner {
        padding-top: 0;
      }
    }
    .n-collapse-item__header {
      .n-collapse-item__header-main {
        font-weight: 500;
        // color: #fff;
        justify-content: space-between;
      }
    }
  }
  :deep(.n-collapse-item:first-child) {
    & > .n-collapse-item__header {
      padding: 0 20px;
    }
  }
  :deep(.n-collapse-item-arrow) {
    display: v-bind('displayVal') !important;
  }
}
</style>
