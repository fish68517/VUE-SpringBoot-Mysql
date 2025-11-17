<template>
  <el-card class="resource-card" shadow="hover" @click="handleClick">
    <div class="resource-card-content">
      <!-- Resource Icon/Thumbnail -->
      <div class="resource-icon">
        <el-icon :size="48" :color="getIconColor()">
          <component :is="getIconComponent()" />
        </el-icon>
      </div>

      <!-- Resource Info -->
      <div class="resource-info">
        <div class="resource-header">
          <h3 class="resource-title">{{ resource.title }}</h3>
          <el-tag :type="getTagType()" size="small">{{ resource.contentType }}</el-tag>
        </div>

        <p class="resource-description">{{ truncatedDescription }}</p>

        <div class="resource-footer">
          <div class="resource-meta">
            <el-icon><View /></el-icon>
            <span>{{ resource.viewCount || 0 }} views</span>
          </div>
          
          <div v-if="resource.creatorName" class="resource-creator">
            <el-icon><User /></el-icon>
            <span>{{ resource.creatorName }}</span>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { VideoPlay, Document, Reading, View, User } from '@element-plus/icons-vue';

const props = defineProps({
  resource: {
    type: Object,
    required: true
  }
});

const router = useRouter();

const truncatedDescription = computed(() => {
  if (!props.resource.description) return '';
  const maxLength = 100;
  return props.resource.description.length > maxLength
    ? props.resource.description.substring(0, maxLength) + '...'
    : props.resource.description;
});

const getIconComponent = () => {
  const type = props.resource.contentType?.toLowerCase();
  switch (type) {
    case 'video':
      return VideoPlay;
    case 'document':
      return Document;
    case 'article':
      return Reading;
    default:
      return Document;
  }
};

const getIconColor = () => {
  const type = props.resource.contentType?.toLowerCase();
  switch (type) {
    case 'video':
      return '#409EFF';
    case 'document':
      return '#67C23A';
    case 'article':
      return '#E6A23C';
    default:
      return '#909399';
  }
};

const getTagType = () => {
  const type = props.resource.contentType?.toLowerCase();
  switch (type) {
    case 'video':
      return 'primary';
    case 'document':
      return 'success';
    case 'article':
      return 'warning';
    default:
      return 'info';
  }
};

const handleClick = () => {
  router.push(`/resources/${props.resource.id}`);
};
</script>

<style scoped>
.resource-card {
  cursor: pointer;
  transition: transform var(--transition-base, 0.3s) ease, 
              box-shadow var(--transition-base, 0.3s) ease;
  height: 100%;
}

.resource-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg, 0 10px 15px -3px rgba(0, 0, 0, 0.1));
}

.resource-card-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.resource-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.resource-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.resource-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.resource-description {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.resource-meta,
.resource-creator {
  display: flex;
  align-items: center;
  gap: 4px;
}

.resource-meta .el-icon,
.resource-creator .el-icon {
  font-size: 14px;
}
</style>
