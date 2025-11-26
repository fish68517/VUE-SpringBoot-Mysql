<template>
  <div class="virtual-list-container" @scroll="handleScroll" ref="containerRef">
    <div class="virtual-list-spacer" :style="{ height: offsetY + 'px' }"></div>
    <div class="virtual-list-content">
      <slot
        v-for="(item, index) in visibleItems"
        :key="item.id || index"
        :item="item"
        :index="startIndex + index"
      />
    </div>
    <div class="virtual-list-spacer" :style="{ height: (totalHeight - offsetY - visibleHeight) + 'px' }"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'

const props = defineProps({
  items: {
    type: Array,
    required: true
  },
  itemHeight: {
    type: Number,
    required: true
  },
  containerHeight: {
    type: Number,
    default: 600
  }
})

const containerRef = ref(null)
const scrollTop = ref(0)
const visibleHeight = ref(0)

onMounted(() => {
  if (containerRef.value) {
    visibleHeight.value = containerRef.value.clientHeight || props.containerHeight
  }
})

watch(() => props.containerHeight, (newHeight) => {
  visibleHeight.value = newHeight
})

const startIndex = computed(() => {
  return Math.floor(scrollTop.value / props.itemHeight)
})

const endIndex = computed(() => {
  return Math.ceil((scrollTop.value + visibleHeight.value) / props.itemHeight)
})

const visibleItems = computed(() => {
  return props.items.slice(startIndex.value, Math.min(endIndex.value + 1, props.items.length))
})

const offsetY = computed(() => {
  return startIndex.value * props.itemHeight
})

const totalHeight = computed(() => {
  return props.items.length * props.itemHeight
})

const handleScroll = (event) => {
  scrollTop.value = event.target.scrollTop
}
</script>

<style scoped>
.virtual-list-container {
  overflow-y: auto;
  overflow-x: hidden;
  height: 100%;
  position: relative;
}

.virtual-list-spacer {
  flex-shrink: 0;
}

.virtual-list-content {
  display: flex;
  flex-direction: column;
}
</style>
