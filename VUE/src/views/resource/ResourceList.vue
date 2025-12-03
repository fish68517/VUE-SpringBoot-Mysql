<template>
  <div class="resource-list-container">
    <div class="resource-list-header">
      <h1>健身资源</h1>
      <p class="subtitle">您可以浏览健身视频，文章，和教程</p>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <el-select
        v-model="selectedType"
        placeholder="过滤类型"
        @change="handleFilterChange"
        style="width: 200px"
      >
        <el-option label="所有" value="" />
        <el-option label="视频" value="video" />
        <el-option label="文章" value="article" />
        <el-option label="教程" value="document" />
      </el-select>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40">
        <Loading />
      </el-icon>
      <p>Loading resources...</p>
    </div>

    <!-- Resource Grid -->
    <div v-else-if="resources.length > 0" class="resource-grid">
      <ResourceCard
        v-for="resource in resources"
        :key="resource.id"
        :resource="resource"
      />
    </div>

    <!-- Empty State -->
    <el-empty
      v-else
      description="No resources found"
      :image-size="200"
    >
      <template #description>
        <p>{{ emptyMessage }}</p>
      </template>
    </el-empty>

    <!-- Pagination -->
    <div v-if="total > pageSize" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 48]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { Loading } from '@element-plus/icons-vue';
import ResourceCard from '@/components/resource/ResourceCard.vue';
import { getResources } from '@/api/resource';
import { showError } from '@/utils/feedback';
import { getResourcesAll } from '../../api/resource';

const resources = ref([]);
const loading = ref(false);
const selectedType = ref('');
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);

const emptyMessage = computed(() => {
  if (selectedType.value) {
    return `No ${selectedType.value} resources found. Try selecting a different type.`;
  }
  return 'No resources available at the moment. Check back later!';
});

const fetchResources = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1, // Backend uses 0-based indexing
      size: pageSize.value
    };
    
    if (selectedType.value) {
      params.contentType = selectedType.value;
    }

    const response = await getResourcesAll(params);
    resources.value = response.content || [];
    total.value = response.totalElements || 0;
  } catch (error) {
    console.error('Failed to fetch resources:', error);
    showError('Failed to load resources');
    resources.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  currentPage.value = 1; // Reset to first page when filter changes
  fetchResources();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchResources();
  // Scroll to top
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1; // Reset to first page when page size changes
  fetchResources();
};

onMounted(() => {
  fetchResources();
});
</script>

<style scoped>
.resource-list-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.resource-list-header {
  margin-bottom: 32px;
}

.resource-list-header h1 {
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

.filter-section {
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
  align-items: center;
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

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

/* Responsive Design */
@media (max-width: 768px) {
  .resource-list-container {
    padding: 16px;
  }

  .resource-list-header h1 {
    font-size: 24px;
  }

  .subtitle {
    font-size: 14px;
  }

  .resource-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-section .el-select {
    width: 100% !important;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .resource-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
