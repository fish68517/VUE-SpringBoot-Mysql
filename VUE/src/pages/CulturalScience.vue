<template>
  <div class="cultural-science">
    <!-- Header -->
    <div class="science-header">
      <h1>文化科普</h1>
      <p>深入了解纳西族纹样的文化内涵与应用场景</p>
    </div>

    <!-- Tab Navigation -->
    <div class="tab-navigation">
      <button 
        :class="['tab-btn', { active: activeTab === 'encyclopedia' }]"
        @click="activeTab = 'encyclopedia'"
      >
        纹样百科
      </button>
      <button 
        :class="['tab-btn', { active: activeTab === 'cases' }]"
        @click="activeTab = 'cases'"
      >
        场景案例库
      </button>
    </div>

    <!-- Encyclopedia Tab -->
    <div v-if="activeTab === 'encyclopedia'" class="tab-content">
      <!-- Search Section -->
      <div class="search-section">
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
        <div class="category-filter">
          <label>纹样分类：</label>
          <select v-model="selectedCategory" @change="applyFilters">
            <option value="">全部分类</option>
            <option value="七星纹">七星纹</option>
            <option value="东巴衍生纹">东巴衍生纹</option>
            <option value="日月纹">日月纹</option>
            <option value="云纹水纹">云纹水纹</option>
          </select>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- Encyclopedia Content -->
      <div v-else class="encyclopedia-content">
        <div v-if="filteredPatterns.length === 0" class="no-data">
          <p>暂无相关纹样数据</p>
        </div>
        <div v-else class="encyclopedia-list">
          <div 
            v-for="pattern in filteredPatterns" 
            :key="pattern.id"
            class="encyclopedia-item"
          >
            <!-- Pattern Image -->
            <div class="pattern-image-container">
              <img :src="pattern.imageUrl" :alt="pattern.name" class="pattern-image" />
              <div class="category-badge">{{ pattern.category }}</div>
            </div>

            <!-- Pattern Info -->
            <div class="pattern-info">
              <h3 class="pattern-name">{{ pattern.name }}</h3>
              
              <!-- Origin Section -->
              <div class="info-section">
                <h4 class="section-title">起源</h4>
                <p class="section-content">
                  {{ pattern.description || '暂无起源信息' }}
                </p>
              </div>

              <!-- Cultural Background Section -->
              <div class="info-section">
                <h4 class="section-title">文化内涵</h4>
                <p class="section-content">
                  {{ pattern.culturalBackground || '暂无文化内涵信息' }}
                </p>
              </div>

              <!-- Application Scenarios Section -->
              <div class="info-section">
                <h4 class="section-title">应用场景</h4>
                <p class="section-content">
                  {{ pattern.applicationScenarios || '暂无应用场景信息' }}
                </p>
              </div>

              <!-- Statistics -->
              <div class="pattern-stats">
                <span class="stat-item">
                  <i class="icon">👁</i>
                  浏览: {{ pattern.viewCount || 0 }}
                </span>
                <span class="stat-item">
                  <i class="icon">⬇</i>
                  下载: {{ pattern.downloadCount || 0 }}
                </span>
                <span class="stat-item">
                  <i class="icon">❤</i>
                  收藏: {{ pattern.collectionCount || 0 }}
                </span>
              </div>

              <!-- Action Buttons -->
              <div class="action-buttons">
                <router-link :to="`/patterns/${pattern.id}`" class="detail-btn">
                  查看详情
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="filteredPatterns.length > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 15, 20]"
            :total="totalItems"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handlePageChange"
            @size-change="handlePageSizeChange"
          />
        </div>
      </div>
    </div>

    <!-- Cases Tab -->
    <div v-if="activeTab === 'cases'" class="tab-content">
      <!-- Cases Filter Section -->
      <div class="cases-filter-section">
        <div class="filter-group">
          <label>应用场景：</label>
          <select v-model="selectedScenario" @change="filterCases">
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

      <!-- Cases Content -->
      <div v-else class="cases-content">
        <div v-if="filteredCases.length === 0" class="no-data">
          <p>暂无相关案例数据</p>
        </div>
        <div v-else class="cases-grid">
          <div 
            v-for="caseItem in filteredCases" 
            :key="caseItem.id"
            class="case-card"
          >
            <!-- Case Image -->
            <div class="case-image-container">
              <img :src="caseItem.imageUrl" :alt="caseItem.name" class="case-image" />
              <div class="scenario-badge">{{ caseItem.scenario }}</div>
            </div>

            <!-- Case Info -->
            <div class="case-info">
              <h4 class="case-name">{{ caseItem.name }}</h4>
              <p class="case-description">{{ caseItem.description }}</p>
              <div class="case-category">
                <span class="category-tag">{{ caseItem.category }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Cases Pagination -->
        <div v-if="filteredCases.length > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="casesCurrentPage"
            v-model:page-size="casesPageSize"
            :page-sizes="[6, 12, 18, 24]"
            :total="totalCases"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handleCasesPageChange"
            @size-change="handleCasesPageSizeChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { patternAPI } from '../services/api'
import { ElMessage, ElSkeleton } from 'element-plus'

// Tab state
const activeTab = ref('encyclopedia')

// Encyclopedia state
const searchKeyword = ref('')
const isSearching = ref(false)
const selectedCategory = ref('')
const patterns = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)
const totalItems = ref(0)

// Cases state
const selectedScenario = ref('')
const cases = ref([])
const casesCurrentPage = ref(1)
const casesPageSize = ref(6)
const totalCases = ref(0)

// Computed
const filteredPatterns = computed(() => {
  return patterns.value
})

const filteredCases = computed(() => {
  let result = cases.value

  if (selectedScenario.value) {
    result = result.filter(caseItem => caseItem.scenario === selectedScenario.value)
  }

  return result
})

// Methods
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
  selectedCategory.value = ''
  currentPage.value = 1
  fetchPatterns()
}

const applyFilters = () => {
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

    if (isSearching.value && searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value
      if (selectedCategory.value) {
        params.category = selectedCategory.value
      }
      response = await patternAPI.searchPatterns(searchKeyword.value, params)
    } else {
      if (selectedCategory.value) {
        params.category = selectedCategory.value
      }
      response = await patternAPI.getPatterns(params)
    }

    // Handle paginated response
    if (response && response.content) {
      patterns.value = response.content
      totalItems.value = response.totalElements
    } else if (Array.isArray(response)) {
      patterns.value = response
      totalItems.value = response.length
    } else {
      patterns.value = []
      totalItems.value = 0
    }

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

const fetchCases = async () => {
  loading.value = true
  try {
    const params = {
      page: casesCurrentPage.value - 1,
      size: casesPageSize.value
    }

    const response = await patternAPI.getPatterns(params)

    // Transform patterns into cases by extracting application scenarios
    if (response && response.content) {
      const casesData = []
      response.content.forEach(pattern => {
        if (pattern.applicationScenarios) {
          const scenarios = pattern.applicationScenarios.split('、').map(s => s.trim())
          scenarios.forEach(scenario => {
            casesData.push({
              id: `${pattern.id}-${scenario}`,
              name: pattern.name,
              category: pattern.category,
              description: pattern.culturalBackground || pattern.description,
              imageUrl: pattern.imageUrl,
              scenario: scenario
            })
          })
        }
      })
      cases.value = casesData
      totalCases.value = casesData.length
    } else {
      cases.value = []
      totalCases.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch cases:', error)
    ElMessage.error('加载案例数据失败')
    cases.value = []
  } finally {
    loading.value = false
  }
}

const filterCases = () => {
  casesCurrentPage.value = 1
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

const handleCasesPageChange = (page) => {
  casesCurrentPage.value = page
}

const handleCasesPageSizeChange = (size) => {
  casesPageSize.value = size
  casesCurrentPage.value = 1
}

// Lifecycle
onMounted(() => {
  fetchPatterns()
  fetchCases()
})
</script>

<style scoped>
.cultural-science {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 2rem 1rem;
}

/* Header */
.science-header {
  text-align: center;
  margin-bottom: 2rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.science-header h1 {
  font-size: 2.5rem;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.science-header p {
  font-size: 1.1rem;
  color: #666;
  margin: 0;
}

/* Tab Navigation */
.tab-navigation {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.tab-btn {
  padding: 0.75rem 2rem;
  border: 2px solid #ddd;
  background-color: white;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
  color: #333;
  font-weight: 500;
}

.tab-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.tab-btn.active {
  background-color: #667eea;
  color: white;
  border-color: #667eea;
}

/* Tab Content */
.tab-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

/* Search Section */
.search-section {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
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

.category-filter {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.category-filter label {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.category-filter select {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  background-color: white;
  color: #333;
}

.category-filter select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

/* Loading Container */
.loading-container {
  padding: 2rem 1rem;
}

/* Encyclopedia Content */
.encyclopedia-content {
  background-color: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.encyclopedia-list {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.encyclopedia-item {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 2rem;
  padding: 1.5rem;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: box-shadow 0.3s;
}

.encyclopedia-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.pattern-image-container {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background-color: #f5f5f5;
}

.pattern-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  display: block;
}

.category-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #667eea;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.pattern-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.pattern-name {
  font-size: 1.5rem;
  color: #333;
  margin: 0;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.section-content {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.pattern-stats {
  display: flex;
  gap: 1.5rem;
  padding: 1rem 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.95rem;
  color: #666;
}

.icon {
  font-size: 1.2rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

.detail-btn {
  padding: 0.75rem 1.5rem;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s;
  display: inline-block;
}

.detail-btn:hover {
  background-color: #5568d3;
}

/* Cases Filter Section */
.cases-filter-section {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
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

.filter-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

/* Cases Content */
.cases-content {
  background-color: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.case-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.3s, transform 0.3s;
  background-color: white;
}

.case-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.case-image-container {
  position: relative;
  overflow: hidden;
  background-color: #f5f5f5;
}

.case-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
}

.scenario-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #667eea;
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 500;
}

.case-info {
  padding: 1rem;
}

.case-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.case-description {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.5;
  margin: 0 0 0.75rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.case-category {
  display: flex;
  gap: 0.5rem;
}

.category-tag {
  display: inline-block;
  background-color: #f0f0f0;
  color: #666;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.85rem;
}

/* No Data */
.no-data {
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
}

/* Responsive Design */
@media (max-width: 768px) {
  .cultural-science {
    padding: 1rem;
  }

  .science-header h1 {
    font-size: 1.8rem;
  }

  .science-header p {
    font-size: 1rem;
  }

  .tab-navigation {
    gap: 0.5rem;
    margin-bottom: 1.5rem;
  }

  .tab-btn {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }

  .search-section {
    padding: 1rem;
    margin-bottom: 1.5rem;
  }

  .search-group {
    flex-direction: column;
    gap: 0.5rem;
    margin-bottom: 1rem;
  }

  .search-input {
    width: 100%;
    min-width: unset;
  }

  .search-btn,
  .clear-btn {
    width: 100%;
  }

  .category-filter {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .category-filter select {
    width: 100%;
  }

  .encyclopedia-item {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .pattern-image {
    height: 250px;
  }

  .cases-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
  }

  .case-image {
    height: 150px;
  }

  .tab-content {
    padding: 0;
  }

  .encyclopedia-content,
  .cases-content {
    padding: 1rem;
  }
}

@media (max-width: 480px) {
  .science-header h1 {
    font-size: 1.5rem;
  }

  .tab-navigation {
    gap: 0.25rem;
  }

  .tab-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.8rem;
  }

  .search-input {
    font-size: 16px;
  }

  .pattern-stats {
    flex-direction: column;
    gap: 0.75rem;
  }

  .cases-grid {
    grid-template-columns: 1fr;
  }

  .encyclopedia-item {
    padding: 1rem;
  }

  .pattern-name {
    font-size: 1.2rem;
  }

  .section-title {
    font-size: 0.95rem;
  }

  .section-content {
    font-size: 0.9rem;
  }
}
</style>
