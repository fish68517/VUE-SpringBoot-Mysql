<template>
  <div class="pattern-library">
    <!-- Header -->
    <div class="library-header">
      <h1>纹样资源库</h1>
      <p>探索纳西族四类核心纹样的完整资源库</p>
    </div>

    <!-- Category Navigation -->
    <div class="category-nav">
      <button 
        v-for="cat in categories" 
        :key="cat.value"
        :class="['category-btn', { active: selectedCategory === cat.value }]"
        @click="selectCategory(cat.value)"
      >
        {{ cat.label }}
      </button>
    </div>

    <!-- Search and Filters Section -->
    <div class="filters-section">
      <div class="search-group">
        <input 
          v-model="searchKeyword"
          type="text"
          placeholder="搜索纹样名称或文化内涵..."
          class="search-input"
          @keyup.enter="performSearch"
        />
        <button @click="performSearch" class="search-btn">搜索</button>
        <button v-if="isSearching" @click="clearSearch" class="clear-btn">清除搜索</button>
      </div>
      <div class="filter-group" v-if="false">
        <label>应用场景：</label>
        <select v-model="selectedScenario" @change="applyFilters">
          <option value="">全部场景</option>
          <option value="传统服饰">传统服饰</option>
          <option value="现代设计">现代设计</option>
          <option value="文创产品">文创产品</option>
          <option value="其他">其他</option>
        </select>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- Patterns Grid -->
    <div v-else class="patterns-grid">
      <div v-if="filteredPatterns.length === 0" class="no-data">
        <p>暂无纹样数据</p>
      </div>
      <router-link 
        v-for="pattern in filteredPatterns" 
        :key="pattern.id"
        :to="`/patterns/${pattern.id}`"
        class="pattern-link"
      >
        <PatternCard :pattern="pattern" />
      </router-link>
    </div>

    <!-- Pagination -->
    <div v-if="filteredPatterns.length > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="totalItems"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { patternAPI } from '../services/api'
import { operationLogService } from '../services/operationLog'
import PatternCard from '../components/PatternCard.vue'
import { ElMessage } from 'element-plus'

// Categories
const categories = [
  { label: '全部纹样', value: '' },
  { label: '七星纹', value: '七星纹' },
  { label: '东巴衍生纹', value: '东巴衍生纹' },
  { label: '日月纹', value: '日月纹' },
  { label: '云纹水纹', value: '云纹水纹' }
]

// State
const selectedCategory = ref('')
const selectedScenario = ref('')
const searchKeyword = ref('')
const isSearching = ref(false)
const patterns = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

// Computed
const filteredPatterns = computed(() => {
  let result = patterns.value

  // Filter by scenario if selected
  if (selectedScenario.value) {
    result = result.filter(pattern => {
      if (!pattern.applicationScenarios) return false
      return pattern.applicationScenarios.includes(selectedScenario.value)
    })
  }

  return result
})

// Methods
const selectCategory = (category) => {
  selectedCategory.value = category
  currentPage.value = 1
  fetchPatterns()
}

const applyFilters = () => {
  currentPage.value = 1
  fetchPatterns()
}

const performSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  isSearching.value = true
  currentPage.value = 1
  fetchPatterns()
}

const clearSearch = () => {
  searchKeyword.value = ''
  isSearching.value = false
  currentPage.value = 1
  fetchPatterns()
}

const fetchPatterns = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }

    let response

    // If searching, use search API
    if (isSearching.value && searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value
      if (selectedCategory.value) {
        params.category = selectedCategory.value
      }
      response = await patternAPI.searchPatterns(searchKeyword.value, params)
      
      // Record search operation
      const resultCount = response && response.data.content ? response.data.content.length : 0
      operationLogService.recordSearch(searchKeyword.value, resultCount)
    } else {
      // Otherwise use regular getPatterns API
      if (selectedCategory.value) {
        params.category = selectedCategory.value
      }
      response = await patternAPI.getPatterns(params)
    }
    
    // Handle paginated response
    if (response && response.data.content) {
      patterns.value = response.data.content
      totalItems.value = response.totalElements
    } else if (Array.isArray(response)) {
      patterns.value = response
      totalItems.value = response.length
    } else {
      patterns.value = []
      totalItems.value = 0
    }

    // Show message if no results
    if (patterns.value.length === 0 && isSearching.value) {
      ElMessage.info('未找到相关纹样')
    }
  } catch (error) {
    console.error('Failed to fetch patterns:', error)
    ElMessage.error('加载纹样数据失败')
    patterns.value = []
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchPatterns()
}

const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchPatterns()
}

// Lifecycle
onMounted(() => {
  fetchPatterns()
})
</script>

<style scoped>
.pattern-library {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 2rem 1rem;
}

/* Header */
.library-header {
  text-align: center;
  margin-bottom: 2rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.library-header h1 {
  font-size: 2.5rem;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.library-header p {
  font-size: 1.1rem;
  color: #666;
  margin: 0;
}

/* Category Navigation */
.category-nav {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.category-btn {
  padding: 0.75rem 1.5rem;
  border: 2px solid #ddd;
  background-color: white;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
  color: #333;
}

.category-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.category-btn.active {
  background-color: #667eea;
  color: white;
  border-color: #667eea;
}

/* Filters Section */
.filters-section {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-group {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 0.75rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  color: #333;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.search-input::placeholder {
  color: #999;
}

.search-btn {
  padding: 0.75rem 1.5rem;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
  white-space: nowrap;
}

.search-btn:hover {
  background-color: #5568d3;
}

.search-btn:active {
  background-color: #4557c0;
}

.clear-btn {
  padding: 0.75rem 1.5rem;
  background-color: #f0f0f0;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.clear-btn:hover {
  background-color: #e0e0e0;
  border-color: #999;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.filter-group label {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.filter-group select {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  background-color: white;
  color: #333;
}

.filter-group select:hover {
  border-color: #667eea;
}

.filter-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

/* Loading Container */
.loading-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

/* Patterns Grid */
.patterns-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  max-width: 1200px;
  margin: 0 auto 2rem;
  padding: 0 1rem;
}

.pattern-link {
  text-decoration: none;
  color: inherit;
  transition: transform 0.3s;
}

.pattern-link:hover {
  transform: scale(1.02);
}

.no-data {
  grid-column: 1 / -1;
  text-align: center;
  padding: 3rem 1rem;
  color: #999;
  font-size: 1.1rem;
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

/* Responsive Design */
@media (max-width: 1199px) {
  .library-header h1 {
    font-size: 2.2rem;
  }

  .library-header p {
    font-size: 1rem;
  }

  .patterns-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 1.25rem;
  }
}

@media (max-width: 991px) {
  .pattern-library {
    padding: 1.5rem 1rem;
  }

  .library-header h1 {
    font-size: 2rem;
    margin: 0 0 0.4rem 0;
  }

  .library-header p {
    font-size: 0.95rem;
  }

  .category-nav {
    gap: 0.75rem;
    margin-bottom: 1.5rem;
  }

  .category-btn {
    padding: 0.65rem 1.25rem;
    font-size: 0.95rem;
  }

  .filters-section {
    padding: 1.25rem;
    margin-bottom: 1.5rem;
  }

  .search-group {
    gap: 0.6rem;
    margin-bottom: 0.75rem;
  }

  .search-input {
    min-width: 180px;
    padding: 0.65rem 0.9rem;
    font-size: 0.95rem;
  }

  .search-btn,
  .clear-btn {
    padding: 0.65rem 1.25rem;
    font-size: 0.95rem;
  }

  .filter-group {
    gap: 0.75rem;
  }

  .filter-group select {
    padding: 0.45rem 0.9rem;
    font-size: 0.95rem;
  }

  .patterns-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
    margin-bottom: 1.5rem;
  }
}

@media (max-width: 767px) {
  .pattern-library {
    padding: 1rem;
  }

  .library-header h1 {
    font-size: 1.6rem;
    margin: 0 0 0.3rem 0;
  }

  .library-header p {
    font-size: 0.9rem;
  }

  .category-nav {
    gap: 0.5rem;
    margin-bottom: 1.25rem;
  }

  .category-btn {
    padding: 0.5rem 1rem;
    font-size: 0.85rem;
  }

  .filters-section {
    padding: 1rem;
    margin-bottom: 1.25rem;
  }

  .search-group {
    flex-direction: column;
    gap: 0.5rem;
    margin-bottom: 0.75rem;
  }

  .search-input {
    width: 100%;
    min-width: unset;
    padding: 0.6rem 0.85rem;
    font-size: 0.9rem;
  }

  .search-btn,
  .clear-btn {
    width: 100%;
    padding: 0.6rem 1rem;
    font-size: 0.9rem;
  }

  .filter-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .filter-group label {
    font-size: 0.9rem;
  }

  .filter-group select {
    width: 100%;
    padding: 0.6rem 0.85rem;
    font-size: 0.9rem;
  }

  .patterns-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 0.75rem;
    margin-bottom: 1.25rem;
  }

  .no-data {
    padding: 2rem 1rem;
    font-size: 1rem;
  }

  .pagination-container {
    margin-top: 1.25rem;
  }
}

@media (max-width: 479px) {
  .pattern-library {
    padding: 0.75rem;
  }

  .library-header h1 {
    font-size: 1.3rem;
    margin: 0 0 0.2rem 0;
  }

  .library-header p {
    font-size: 0.8rem;
  }

  .category-nav {
    gap: 0.3rem;
    margin-bottom: 1rem;
  }

  .category-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.75rem;
  }

  .filters-section {
    padding: 0.75rem;
    margin-bottom: 1rem;
  }

  .search-group {
    gap: 0.4rem;
    margin-bottom: 0.5rem;
  }

  .search-input {
    padding: 0.5rem 0.75rem;
    font-size: 16px;
  }

  .search-btn,
  .clear-btn {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }

  .filter-group {
    gap: 0.4rem;
  }

  .filter-group label {
    font-size: 0.8rem;
  }

  .filter-group select {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }

  .patterns-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 0.5rem;
    margin-bottom: 1rem;
  }

  .no-data {
    padding: 1.5rem 0.75rem;
    font-size: 0.9rem;
  }

  .pagination-container {
    margin-top: 1rem;
  }
}
</style>
