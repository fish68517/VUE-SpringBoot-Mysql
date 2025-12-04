<template>
  <div class="zone-selection">
    <div class="header">
      <el-button type="text" @click="handleBack">← 返回</el-button>
      <h2>选择分区</h2>
      <div></div>
    </div>

    <p class="subtitle">{{ selectedSession?.name }} - 请选择您要购票的分区</p>

    <el-skeleton v-if="loading" :rows="3" animated />

    <div v-else class="zones-grid">
      <div
        v-for="zone in zones"
        :key="zone.id"
        :class="['zone-card', { 'sold-out': zone.remainingCapacity === 0 }]"
        @click="handleSelectZone(zone)"
      >
        <div class="zone-name">{{ zone.name }}</div>

        <div class="zone-info">
          <div class="info-item">
            <span class="label">价格：</span>
            <span class="price">¥{{ zone.price }}</span>
          </div>
          <div class="info-item">
            <span class="label">剩余票量：</span>
            <span :class="['remaining', { low: zone.remainingCapacity < 10 }]">
              {{ zone.remainingCapacity }}
            </span>
          </div>
        </div>

        <div class="zone-capacity">
          <div class="capacity-bar">
            <div class="capacity-used" :style="{ width: getCapacityPercentage(zone) + '%' }"></div>
          </div>
          <span class="capacity-text">
            已售 {{ zone.soldCount }}/{{ zone.totalCapacity }}
          </span>
        </div>

        <div class="zone-action">
          <el-button
            v-if="zone.remainingCapacity > 0"
            type="primary"
            @click.stop="handleSelectZone(zone)"
          >
            选择
          </el-button>
          <el-button v-else disabled>已售罄</el-button>
        </div>
      </div>
    </div>

    <div v-if="!loading && zones.length === 0" class="empty-state">
      <p>暂无可用分区</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TicketSession, TicketZone } from '@/api/ticket'

interface Props {
  zones: TicketZone[]
  selectedSession: TicketSession | null
  loading: boolean
}

interface Emits {
  (e: 'select', zone: TicketZone): void
  (e: 'back'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const handleSelectZone = (zone: TicketZone) => {
  if (zone.remainingCapacity > 0) {
    emit('select', zone)
  }
}

const handleBack = () => {
  emit('back')
}

const getCapacityPercentage = (zone: TicketZone): number => {
  return (zone.soldCount / zone.totalCapacity) * 100
}
</script>

<style scoped>
.zone-selection {
  width: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
  font-weight: bold;
  flex: 1;
  text-align: center;
}

.header :deep(.el-button) {
  color: #409eff;
}

.subtitle {
  color: #999;
  font-size: 14px;
  margin: 0 0 30px 0;
}

.zones-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.zone-card {
  background: white;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.zone-card:hover:not(.sold-out) {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}

.zone-card.sold-out {
  opacity: 0.6;
  cursor: not-allowed;
}

.zone-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}

.zone-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  font-size: 12px;
  color: #999;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.remaining {
  font-size: 16px;
  font-weight: bold;
  color: #67c23a;
}

.remaining.low {
  color: #f56c6c;
}

.zone-capacity {
  margin-bottom: 15px;
}

.capacity-bar {
  width: 100%;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.capacity-used {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #67c23a);
  transition: width 0.3s ease;
}

.capacity-text {
  font-size: 12px;
  color: #999;
}

.zone-action {
  display: flex;
  gap: 10px;
}

.zone-action :deep(.el-button) {
  flex: 1;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

@media (max-width: 768px) {
  .zones-grid {
    grid-template-columns: 1fr;
  }

  .header {
    flex-direction: column;
    gap: 15px;
  }

  .header h2 {
    text-align: center;
  }
}
</style>
