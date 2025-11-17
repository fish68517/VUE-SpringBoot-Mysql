<template>
  <el-card class="plan-card" shadow="hover" @click="handleClick">
    <div class="plan-header">
      <h3 class="plan-name">{{ plan.name }}</h3>
      <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
    </div>
    
    <p class="plan-description">{{ plan.description }}</p>
    
    <div class="plan-info">
      <div class="info-item">
        <el-icon><User /></el-icon>
        <span>Coach: {{ plan.coachName }}</span>
      </div>
      
      <div class="info-item">
        <el-icon><Calendar /></el-icon>
        <span>{{ formatDateRange }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue';
import { Calendar, User } from '@element-plus/icons-vue';

const props = defineProps({
  plan: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['click']);

const statusType = computed(() => {
  const statusMap = {
    active: 'success',
    completed: 'info',
    cancelled: 'danger'
  };
  return statusMap[props.plan.status] || 'info';
});

const statusText = computed(() => {
  const textMap = {
    active: 'Active',
    completed: 'Completed',
    cancelled: 'Cancelled'
  };
  return textMap[props.plan.status] || props.plan.status;
});

const formatDateRange = computed(() => {
  if (!props.plan.startDate || !props.plan.endDate) {
    return 'No dates set';
  }
  return `${props.plan.startDate} to ${props.plan.endDate}`;
});

const handleClick = () => {
  emit('click', props.plan);
};
</script>

<style scoped>
.plan-card {
  cursor: pointer;
  transition: transform 0.2s;
  margin-bottom: 16px;
}

.plan-card:hover {
  transform: translateY(-2px);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.plan-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.plan-description {
  color: #606266;
  margin: 0 0 16px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.plan-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 14px;
}

.info-item .el-icon {
  font-size: 16px;
}
</style>
