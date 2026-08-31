<script setup lang="ts">
import { InfoFilled } from '@element-plus/icons-vue'

defineProps<{ title: string; description: string }>()
</script>

<template>
  <div class="page-heading">
    <div>
      <h1>{{ title }}</h1>
      <p><el-icon><InfoFilled /></el-icon>{{ description }}</p>
    </div>
    <div class="heading-extra"><slot /></div>
  </div>
</template>

<style scoped lang="scss">
.page-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;

  h1 {
    margin: 0;
    color: #252b37;
    font-size: 21px;
    font-weight: 680;
  }

  p {
    display: flex;
    align-items: center;
    gap: 5px;
    margin: 7px 0 0;
    color: #8a92a0;
    font-size: 12px;
  }
}

.heading-extra { display: flex; gap: 9px; }
</style>
