<template>
  <div class="artist-list-container">
    <div class="section-title">
      <h2>参演艺人</h2>
      <p>沈阳音乐节汇聚国内外顶级艺人</p>
    </div>
    <div class="artist-grid">
      <div
        v-for="artist in artists"
        :key="artist.id"
        class="artist-card"
        @click="handleArtistClick(artist)"
      >
        <div class="artist-image-wrapper">
          <img :src="artist.imageUrl" :alt="artist.name" class="artist-image" />
          <div v-if="artist.isLocalBand" class="local-band-badge">沈阳本土乐队</div>
        </div>
        <div class="artist-info">
          <h3>{{ artist.name }}</h3>
          <p class="artist-description">{{ artist.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'

interface Artist {
  id: number
  name: string
  description: string
  imageUrl: string
  isLocalBand: boolean
}

defineProps<{
  artists: Artist[]
}>()

const emit = defineEmits<{
  selectArtist: [artist: Artist]
}>()

const handleArtistClick = (artist: Artist) => {
  emit('selectArtist', artist)
}
</script>

<style scoped>
.artist-list-container {
  margin-bottom: 40px;
}

.section-title {
  text-align: center;
  margin-bottom: 30px;
}

.section-title h2 {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px 0;
}

.section-title p {
  color: #999;
  font-size: 14px;
  margin: 0;
}

.artist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.artist-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.artist-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.artist-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
}

.artist-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.artist-card:hover .artist-image {
  transform: scale(1.1);
}

.local-band-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #ff6b6b;
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.artist-info {
  padding: 15px;
}

.artist-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.artist-description {
  margin: 0;
  font-size: 13px;
  color: #999;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

@media (max-width: 768px) {
  .artist-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }

  .artist-image-wrapper {
    height: 150px;
  }

  .section-title h2 {
    font-size: 22px;
  }
}
</style>
