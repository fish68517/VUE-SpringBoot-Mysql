<template>
  <el-card :body-style="{ padding: '20px' }" class="card">
    <template #header v-if="title">
      <div class="card-header">
        <span>{{ title }}</span>
        <slot name="header-extra"></slot>
      </div>
    </template>
    <slot></slot>
  </el-card>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: '',
  },
})
</script>

<style scoped>
.card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
