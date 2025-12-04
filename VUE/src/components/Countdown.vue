<template>
  <div class="countdown-container">
    <div class="countdown-title">
      <h2>距离音乐节开始</h2>
    </div>
    <div class="countdown-content">
      <div class="countdown-item">
        <div class="countdown-number">{{ countdown.days }}</div>
        <div class="countdown-label">天</div>
      </div>
      <div class="countdown-separator">:</div>
      <div class="countdown-item">
        <div class="countdown-number">{{ String(countdown.hours).padStart(2, '0') }}</div>
        <div class="countdown-label">小时</div>
      </div>
      <div class="countdown-separator">:</div>
      <div class="countdown-item">
        <div class="countdown-number">{{ String(countdown.minutes).padStart(2, '0') }}</div>
        <div class="countdown-label">分钟</div>
      </div>
      <div class="countdown-separator">:</div>
      <div class="countdown-item">
        <div class="countdown-number">{{ String(countdown.seconds).padStart(2, '0') }}</div>
        <div class="countdown-label">秒</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, defineProps } from 'vue'
import { calculateCountdown } from '@/utils/date'

interface Countdown {
  days: number
  hours: number
  minutes: number
  seconds: number
}

const props = defineProps<{
  targetDate: string | Date | number
}>()

const countdown = ref<Countdown>({
  days: 0,
  hours: 0,
  minutes: 0,
  seconds: 0,
})

let intervalId: number | null = null

const updateCountdown = () => {
  countdown.value = calculateCountdown(props.targetDate)
}

onMounted(() => {
  updateCountdown()
  intervalId = window.setInterval(updateCountdown, 1000)
})

onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId)
  }
})
</script>

<style scoped>
.countdown-container {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 40px 20px;
  margin-bottom: 30px;
  text-align: center;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.countdown-title h2 {
  margin: 0 0 30px 0;
  font-size: 28px;
  font-weight: bold;
}

.countdown-content {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.countdown-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.countdown-number {
  font-size: 48px;
  font-weight: bold;
  line-height: 1;
  min-width: 80px;
  background: rgba(255, 255, 255, 0.2);
  padding: 15px;
  border-radius: 8px;
}

.countdown-label {
  font-size: 14px;
  opacity: 0.9;
}

.countdown-separator {
  font-size: 36px;
  font-weight: bold;
  opacity: 0.7;
  margin: 0 5px;
}

@media (max-width: 768px) {
  .countdown-container {
    padding: 30px 15px;
  }

  .countdown-title h2 {
    font-size: 20px;
    margin-bottom: 20px;
  }

  .countdown-number {
    font-size: 32px;
    min-width: 60px;
    padding: 10px;
  }

  .countdown-separator {
    font-size: 24px;
  }
}
</style>
