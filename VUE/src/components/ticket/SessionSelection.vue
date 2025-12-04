<template>
  <div class="session-selection">
    <h2>选择场次</h2>
    <p class="subtitle">请选择您要购票的演出场次</p>

    <el-skeleton v-if="loading" :rows="3" animated />

    <div v-else class="sessions-grid">
      <div
        v-for="session in sessions"
        :key="session.id"
        :class="['session-card', { disabled: session.status !== 'available' }]"
        @click="handleSelectSession(session)"
      >
        <div class="session-header">
          <h3>{{ session.name }}</h3>
          <el-tag :type="getStatusType(session.status)">{{ getStatusText(session.status) }}</el-tag>
        </div>

        <div class="session-details">
          <div class="detail-row">
            <span class="label">开始时间：</span>
            <span class="value">{{ formatDateTime(session.startTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">结束时间：</span>
            <span class="value">{{ formatDateTime(session.endTime) }}</span>
          </div>
        </div>

        <div class="session-action">
          <el-button
            v-if="session.status === 'available'"
            type="primary"
            @click.stop="handleSelectSession(session)"
          >
            选择
          </el-button>
          <el-button v-else disabled>{{ getStatusText(session.status) }}</el-button>
        </div>
      </div>
    </div>

    <div v-if="!loading && sessions.length === 0" class="empty-state">
      <p>暂无可用场次</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TicketSession } from '@/api/ticket'
import { formatDateTime } from '@/utils/date'

interface Props {
  sessions: TicketSession[]
  loading: boolean
}

interface Emits {
  (e: 'select', session: TicketSession): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const handleSelectSession = (session: TicketSession) => {
  if (session.status === 'available') {
    emit('select', session)
  }
}

const getStatusType = (status: string): string => {
  const statusMap: Record<string, string> = {
    available: 'success',
    sold_out: 'danger',
    ended: 'info',
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    available: '可购票',
    sold_out: '已售罄',
    ended: '已结束',
  }
  return statusMap[status] || status
}
</script>

<style scoped>
.session-selection {
  width: 100%;
}

.session-selection h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: bold;
}

.subtitle {
  color: #999;
  font-size: 14px;
  margin: 0 0 30px 0;
}

.sessions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.session-card {
  background: white;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.session-card:hover:not(.disabled) {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}

.session-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.session-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
  flex: 1;
}

.session-details {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-row .label {
  color: #999;
  min-width: 80px;
}

.detail-row .value {
  color: #333;
  font-weight: 500;
}

.session-action {
  display: flex;
  gap: 10px;
}

.session-action :deep(.el-button) {
  flex: 1;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

@media (max-width: 768px) {
  .sessions-grid {
    grid-template-columns: 1fr;
  }
}
</style>
