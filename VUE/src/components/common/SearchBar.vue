<template>
  <div class="search-bar">
    <el-input
      v-model="searchQuery"
      placeholder="搜索资源、动态、教练..."
      :prefix-icon="Search"
      clearable
      @keyup.enter="handleSearch"
      class="search-input"
    >
      <template #append>
        <el-select
          v-model="searchType"
          placeholder="类型"
          style="width: 100px"
        >
          <el-option label="全部" value="all" />
          <el-option label="资源" value="resource" />
          <el-option label="动态" value="post" />
          <el-option label="教练" value="coach" />
        </el-select>
      </template>
    </el-input>
    <el-button
      type="primary"
      :icon="Search"
      @click="handleSearch"
      class="search-button"
    >
      搜索
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['search'])

const searchQuery = ref('')
const searchType = ref('all')

const handleSearch = () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索内容')
    return
  }

  emit('search', {
    query: searchQuery.value.trim(),
    type: searchType.value
  })
}
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.search-input {
  flex: 1;
}

.search-button {
  white-space: nowrap;
}

/* Responsive design */
@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    gap: 8px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .search-button {
    width: 100%;
  }
}
</style>
