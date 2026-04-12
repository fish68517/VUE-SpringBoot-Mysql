<template>
  <div class="statistics-card">
    <div class="card-header">
      <h3>{{ title }}</h3>
    </div>
    <div class="card-content">
      <div class="stat-value">{{ value }}</div>
      <div class="stat-label">{{ label }}</div>
    </div>
    <div class="card-icon">
      <i :class="iconClass"></i>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  label: {
    type: String,
    required: true
  },
  iconClass: {
    type: String,
    default: 'el-icon-data-analysis'
  }
})
</script>

<style scoped>
.statistics-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 20px;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.statistics-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.15);
}

.card-header {
  margin-bottom: 12px;
}

.card-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  opacity: 0.9;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
}

.card-icon {
  position: absolute;
  top: 12px;
  right: 16px;
  font-size: 32px;
  opacity: 0.2;
}

/* Different color variants */
.statistics-card.records {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.statistics-card.plans {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.statistics-card.likes {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.statistics-card.comments {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.statistics-card.footprints {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.statistics-card.multimedia {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}

@media (max-width: 768px) {
  .statistics-card {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }

  .card-icon {
    font-size: 24px;
  }
}
</style>
