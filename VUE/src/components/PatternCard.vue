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
  display: flex;
  flex-direction: column;
  height: 100%;
}

.pattern-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  flex-shrink: 0;
}

h3 {
  padding: 1rem;
  margin: 0;
  font-size: 1.1rem;
  flex-grow: 1;
  display: flex;
  align-items: center;
}

p {
  padding: 0 1rem 1rem;
  margin: 0;
  color: #666;
  font-size: 0.95rem;
}

/* Responsive Design */
@media (max-width: 991px) {
  img {
    height: 180px;
  }

  h3 {
    padding: 0.9rem;
    font-size: 1rem;
  }

  p {
    padding: 0 0.9rem 0.9rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 767px) {
  img {
    height: 160px;
  }

  h3 {
    padding: 0.75rem;
    font-size: 0.95rem;
  }

  p {
    padding: 0 0.75rem 0.75rem;
    font-size: 0.85rem;
  }
}

@media (max-width: 479px) {
  img {
    height: 140px;
  }

  h3 {
    padding: 0.6rem;
    font-size: 0.85rem;
  }

  p {
    padding: 0 0.6rem 0.6rem;
    font-size: 0.75rem;
  }
}
</style>
