<template>
  <div class="my-collections-container">
    <div class="collections-header">
      <h1>My Collections</h1>
      <p class="subtitle">Your saved fitness resources</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40">
        <Loading />
      </el-icon>
      <p>Loading your collections...</p>
    </div>

    <!-- Collections Grid -->
    <div v-else-if="collections.length > 0" class="collections-content">
      <div class="collections-stats">
        <el-card shadow="never">
          <div class="stats-content">
            <el-icon :size="32" color="#409EFF"><Star /></el-icon>
            <div>
              <h3>{{ collections.length }}</h3>
              <p>Resources Collected</p>
            </div>
          </div>
        </el-card>
      </div>

      <div class="collections-grid">
        <div
          v-for="resource in collections"
          :key="resource.id"
          class="collection-item"
        >
          <ResourceCard :resource="resource" />
          <div class="collection-actions">
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="handleRemove(resource)"
              :loading="removingId === resource.id"
            >
              Remove
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <el-empty
      v-else
      description="No collections yet"
      :image-size="200"
    >
      <template #description>
        <div class="empty-description">
          <p>You haven't collected any resources yet.</p>
          <p>Browse our fitness resources and save your favorites!</p>
        </div>
      </template>
      <el-button type="primary" @click="goToResources">
        Browse Resources
      </el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Loading, Star, Delete } from '@element-plus/icons-vue';
import ResourceCard from '@/components/resource/ResourceCard.vue';
import { getCollections, removeCollection } from '@/api/collection';
import { showSuccess, showError, confirmRemove } from '@/utils/feedback';

const router = useRouter();

const collections = ref([]);
const loading = ref(false);
const removingId = ref(null);

const fetchCollections = async () => {
  loading.value = true;
  try {
    collections.value = await getCollections();
  } catch (error) {
    console.error('Failed to fetch collections:', error);
    showError('Failed to load your collections');
    collections.value = [];
  } finally {
    loading.value = false;
  }
};

const handleRemove = async (resource) => {
  try {
    await confirmRemove(`"${resource.title}"`);

    removingId.value = resource.id;
    await removeCollection(resource.id);
    
    // Remove from local array
    collections.value = collections.value.filter(c => c.id !== resource.id);
    
    showSuccess('Removed from collection');
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      console.error('Failed to remove collection:', error);
      showError('Failed to remove from collection');
    }
  } finally {
    removingId.value = null;
  }
};

const goToResources = () => {
  router.push('/resources');
};

onMounted(() => {
  fetchCollections();
});
</script>

<style scoped>
.my-collections-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.collections-header {
  margin-bottom: 32px;
}

.collections-header h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 16px;
  color: #606266;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #909399;
}

.loading-container p {
  margin-top: 16px;
  font-size: 16px;
}

.collections-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.collections-stats {
  margin-bottom: 8px;
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-content h3 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stats-content p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.collections-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.collection-item {
  position: relative;
}

.collection-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.empty-description {
  margin-bottom: 16px;
}

.empty-description p {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .my-collections-container {
    padding: 16px;
  }

  .collections-header h1 {
    font-size: 24px;
  }

  .subtitle {
    font-size: 14px;
  }

  .collections-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .stats-content h3 {
    font-size: 24px;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .collections-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
