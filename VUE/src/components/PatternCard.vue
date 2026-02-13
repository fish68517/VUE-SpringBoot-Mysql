<template>
  <div class="pattern-card">
    <img :src="pattern.imageUrl" :alt="pattern.name" />
    <h3>{{ pattern.name }}</h3>
    <p>{{ pattern.category }}</p>
  </div>
</template>

<script setup>
defineProps({
  pattern: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.pattern-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s;
}

.pattern-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

h3 {
  padding: 1rem;
  margin: 0;
  font-size: 1.1rem;
}

p {
  padding: 0 1rem 1rem;
  margin: 0;
  color: #666;
}
</style>
