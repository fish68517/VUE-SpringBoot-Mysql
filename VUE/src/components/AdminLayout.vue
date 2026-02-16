<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-content">
      <slot />
    </div>
  </div>
</template>

<script setup>
import AdminSidebar from './AdminSidebar.vue'
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-content {
  flex: 1;
  margin-left: 250px;
  padding: 20px;
  overflow-y: auto;
}

/* Responsive */
@media (max-width: 768px) {
  .admin-layout {
    flex-direction: column;
  }

  .admin-content {
    margin-left: 0;
    padding: 12px;
  }
}
</style>
