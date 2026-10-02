<template>
  <!-- H5-only native forms and tables: keep their semantics and existing event handlers. -->
  <!-- #ifdef H5 -->
  <div ref="ledgerRoot" class="ledger-root" v-html="layout"></div>
  <!-- #endif -->
  <!-- #ifndef H5 -->
  <view>本项目用于 PC Web / 手机 H5，请选择网站发行。</view>
  <!-- #endif -->
</template>
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
// #ifdef H5
import layout from '../../ledger/layout.html?raw';
import nativeStyles from '../../ledger/styles.css?raw';
import { mountLedger } from '../../ledger/app.js';
const ledgerRoot = ref(null);
let dispose;
let styleElement;
onMounted(() => {
  // Keep native input/label/button selectors; uni-app rewrites compiled CSS tags.
  styleElement = document.createElement('style');
  styleElement.textContent = nativeStyles;
  document.head.appendChild(styleElement);
  dispose = mountLedger(ledgerRoot.value);
});
onBeforeUnmount(() => { dispose?.(); styleElement?.remove(); });
// #endif
</script>
