<template>
  <div class="fire-video-player">
    <div class="fire-video-player__header">
      <div class="fire-video-player__title">{{ title || '视频播放' }}</div>
      <button class="fire-video-player__close" type="button" @click="handleClose">×</button>
    </div>
    <div class="fire-video-player__body">
      <video
        v-if="videoUrl"
        class="fire-video-player__video"
        :src="videoUrl"
        @loadedmetadata="handleVideoReady"
        @ended="handleVideoEnded"
        autoplay
        muted
        loop
        playsinline
        controls
      ></video>
      <div v-else class="fire-video-player__empty">暂无视频</div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  title: {
    type: String,
    default: ''
  },
  videoUrl: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close'])

function handleClose() {
  emit('close')
}

function handleVideoReady(event: Event) {
  const video = event.target as HTMLVideoElement
  video.muted = true
  video.loop = true
  video.play?.().catch(() => {})
}

function handleVideoEnded(event: Event) {
  const video = event.target as HTMLVideoElement
  video.currentTime = 0
  video.play?.().catch(() => {})
}
</script>

<script lang="ts">
export default {
  name: 'FireVideoPlayer'
}
</script>

<style scoped lang="scss">
.fire-video-player {
  position: absolute;
  z-index: 1001;
  width: 600px;
  height: 560px;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  pointer-events: auto;
  background:
    url('../img/Group_2136640490.png') top center / 100% 58px no-repeat,
    linear-gradient(180deg, rgba(6, 31, 73, 0.98) 0%, rgba(3, 16, 42, 0.98) 100%);
  border: 1px solid rgba(55, 182, 255, 0.62);
  box-shadow:
    0 0 22px rgba(0, 180, 255, 0.28),
    inset 0 0 18px rgba(43, 149, 255, 0.22);
}

.fire-video-player__header {
  height: 58px;
  flex: 0 0 58px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 18px 0 22px;
  text-align: center;
}

.fire-video-player__title {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 18px;
  font-weight: 700;
  color: #dff7ff;
  text-align: center;
  text-shadow: 0 0 8px rgba(43, 185, 255, 0.7);
}

.fire-video-player__close {
  width: 28px;
  height: 28px;
  border: 1px solid rgba(88, 196, 255, 0.72);
  border-radius: 2px;
  color: #bcecff;
  background: rgba(3, 27, 63, 0.72);
  font-size: 22px;
  line-height: 24px;
  cursor: pointer;
}

.fire-video-player__body {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-bottom: 2px;
}

.fire-video-player__video,
.fire-video-player__empty {
  width: 500px;
  height: 500px;
  background: rgba(2, 13, 35, 0.92);
  border: 1px solid rgba(57, 184, 255, 0.38);
  box-shadow: inset 0 0 14px rgba(38, 156, 255, 0.18);
}

.fire-video-player__video {
  display: block;
  object-fit: contain;
}

.fire-video-player__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(205, 238, 255, 0.78);
  font-size: 16px;
}
</style>
