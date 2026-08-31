<script setup lang="ts">
import { nextTick, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const mainRef = ref<HTMLElement>()

watch(
  () => route.fullPath,
  async () => {
    await nextTick()
    mainRef.value?.scrollTo({ top: 0, left: 0 })
  },
)
</script>

<template>
  <main ref="mainRef" class="app-main">
    <router-view v-slot="{ Component, route }">
      <transition name="fade-slide" mode="out-in">
        <component :is="Component" :key="route.fullPath" />
      </transition>
    </router-view>
  </main>
</template>

<style scoped>
.app-main {
  min-width: 0;
  min-height: 0;
  padding: 18px;
  overflow: auto;
  background: var(--canvas);
}
</style>
