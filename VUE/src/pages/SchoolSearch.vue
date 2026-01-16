<template>
  <div class="school-search-container">
    <div class="search-section">
      <h1>Search Schools</h1>
      
      <!-- Filter Form -->
      <div class="filter-box">
        <form @submit.prevent="handleSearch">
          <div class="filter-row">
            <!-- Region Filter -->
            <div class="filter-group">
              <label for="city">Region</label>
              <select id="city" v-model="filters.city">
                <option value="">All Regions</option>
                <option value="Nanjing">Nanjing</option>
                <option value="Suzhou">Suzhou</option>
                <option value="Wuxi">Wuxi</option>
                <option value="Changzhou">Changzhou</option>
                <option value="Zhenjiang">Zhenjiang</option>
                <option value="Yangzhou">Yangzhou</option>
                <option value="Taizhou">Taizhou</option>
                <option value="Nantong">Nantong</option>
                <option value="Yancheng">Yancheng</option>
                <option value="Lianyungang">Lianyungang</option>
                <option value="Huai'an">Huai'an</option>
                <option value="Xuzhou">Xuzhou</option>
              </select>
            </div>

            <!-- School Tier Filter -->
            <div class="filter-group">
              <label for="tier">School Tier</label>
              <select id="tier" v-model="filters.tier">
                <option value="">All Tiers</option>
                <option value="985">985</option>
                <option value="211">211</option>
                <option value="DOUBLE_NON">Double-Non</option>
                <option value="OTHER">Other</option>
              </select>
            </div>

            <!-- Expected Score Range -->
            <div class="filter-group">
              <label for="expectedScoreMin">Min Score</label>
              <input
                id="expectedScoreMin"
                v-model.number="filters.expectedScoreMin"
                type="number"
                min="0"
                max="500"
                placeholder="Min score"
              >
            </div>

            <div class="filter-group">
              <label for="expectedScoreMax">Max Score</label>
              <input
                id="expectedScoreMax"
                v-model.number="filters.expectedScoreMax"
                type="number"
                min="0"
                max="500"
                placeholder="Max score"
              >
            </div>

            <!-- Major Filter -->
            <div class="filter-group">
              <label for="major">Major</label>
              <input
                id="major"
                v-model="filters.major"
                type="text"
                placeholder="Enter major name"
              >
            </div>
          </div>

          <!-- Search and Reset Buttons -->
          <div class="button-group">
            <button type="submit" class="btn-search" :disabled="loading">
              {{ loading ? 'Searching...' : 'Search' }}
            </button>
            <button type="button" class="btn-reset" @click="handleReset">
              Reset Filters
            </button>
          </div>

          <!-- Error Message -->
          <div v-if="message.error" class="message error-message">
            {{ message.error }}
          </div>
        </form>
      </div>
    </div>

    <!-- Results Section -->
    <div class="results-section">
      <div v-if="loading && schools.length === 0" class="loading">
        Loading schools...
      </div>

      <div v-else-if="schools.length === 0 && searched" class="no-results">
        <p>No schools found matching your criteria. Try adjusting your filters.</p>
      </div>

      <div v-else class="schools-list">
        <div v-if="schools.length > 0" class="results-info">
          <p>Found {{ totalElements }} schools (Page {{ currentPage + 1 }} of {{ totalPages }})</p>
        </div>

        <!-- School Cards -->
        <div class="school-cards">
          <div v-for="school in schools" :key="school.id" class="school-card">
            <div class="school-header">
              <h3 class="school-name">{{ school.name }}</h3>
              <span class="school-tier" :class="'tier-' + school.tier.toLowerCase()">
                {{ school.tier }}
              </span>
            </div>

            <div class="school-info">
              <div class="info-item">
                <span class="label">Location:</span>
                <span class="value">{{ school.city }}</span>
              </div>
              <div class="info-item">
                <span class="label">Website:</span>
                <a v-if="school.website" :href="school.website" target="_blank" class="value link">
                  {{ school.website }}
                </a>
                <span v-else class="value">N/A</span>
              </div>
            </div>

            <div class="school-intro">
              <p>{{ truncateText(school.intro, 150) }}</p>
            </div>

            <div class="school-actions">
              <router-link :to="`/school/${school.id}`" class="btn-detail">
                View Details
              </router-link>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button
            class="btn-pagination"
            :disabled="currentPage === 0"
            @click="handlePreviousPage"
          >
            Previous
          </button>

          <div class="page-info">
            Page {{ currentPage + 1 }} of {{ totalPages }}
          </div>

          <button
            class="btn-pagination"
            :disabled="currentPage >= totalPages - 1"
            @click="handleNextPage"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import schoolService from '../services/schoolService'

export default {
  name: 'SchoolSearch',
  data() {
    return {
      filters: {
        city: '',
        tier: '',
        expectedScoreMin: null,
        expectedScoreMax: null,
        major: '',
        page: 0,
        size: 20
      },
      schools: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      loading: false,
      searched: false,
      message: {
        error: ''
      }
    }
  },
  mounted() {
    // Load initial schools on component mount
    this.handleSearch()
  },
  methods: {
    async handleSearch() {
      this.loading = true
      this.message.error = ''
      this.filters.page = 0
      this.currentPage = 0

      try {
        const response = await schoolService.searchSchools(this.filters)
        const data = response.data.data

        this.schools = data.content || []
        this.totalElements = data.totalElements || 0
        this.totalPages = data.totalPages || 0
        this.currentPage = data.number || 0
        this.searched = true
      } catch (error) {
        this.message.error = error.response?.data?.message || 'Failed to search schools. Please try again.'
        this.schools = []
        this.searched = true
      } finally {
        this.loading = false
      }
    },

    async handleNextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.filters.page = this.currentPage + 1
        await this.loadPage()
      }
    },

    async handlePreviousPage() {
      if (this.currentPage > 0) {
        this.filters.page = this.currentPage - 1
        await this.loadPage()
      }
    },

    async loadPage() {
      this.loading = true
      this.message.error = ''

      try {
        const response = await schoolService.searchSchools(this.filters)
        const data = response.data.data

        this.schools = data.content || []
        this.currentPage = data.number || 0
      } catch (error) {
        this.message.error = error.response?.data?.message || 'Failed to load schools. Please try again.'
        this.schools = []
      } finally {
        this.loading = false
      }
    },

    handleReset() {
      this.filters = {
        city: '',
        tier: '',
        expectedScoreMin: null,
        expectedScoreMax: null,
        major: '',
        page: 0,
        size: 20
      }
      this.message.error = ''
      this.handleSearch()
    },

    truncateText(text, length) {
      if (!text) return ''
      if (text.length <= length) return text
      return text.substring(0, length) + '...'
    }
  }
}
</script>

<style scoped>
.school-search-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.search-section {
  max-width: 1200px;
  margin: 0 auto 40px;
}

h1 {
  text-align: center;
  color: white;
  margin-bottom: 30px;
  font-size: 32px;
}

.filter-box {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.filter-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.filter-group label {
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
  font-size: 14px;
}

.filter-group input,
.filter-group select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
  font-family: inherit;
}

.filter-group input:focus,
.filter-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn-search,
.btn-reset {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-search {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  flex: 1;
}

.btn-search:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-search:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-reset {
  background: #f0f0f0;
  color: #333;
  flex: 1;
}

.btn-reset:hover {
  background: #e0e0e0;
}

.message {
  margin-top: 15px;
  padding: 12px;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
}

.error-message {
  background-color: #fadbd8;
  color: #c0392b;
  border: 1px solid #e74c3c;
}

.results-section {
  max-width: 1200px;
  margin: 0 auto;
}

.loading,
.no-results {
  background: white;
  padding: 40px;
  border-radius: 8px;
  text-align: center;
  color: #666;
  font-size: 16px;
}

.results-info {
  background: white;
  padding: 15px 20px;
  border-radius: 8px 8px 0 0;
  border-bottom: 1px solid #eee;
  color: #666;
  font-size: 14px;
}

.school-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.school-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
}

.school-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.school-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
}

.school-name {
  margin: 0;
  color: #333;
  font-size: 18px;
  flex: 1;
}

.school-tier {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.tier-985 {
  background: #fff3cd;
  color: #856404;
}

.tier-211 {
  background: #d1ecf1;
  color: #0c5460;
}

.tier-double_non {
  background: #d4edda;
  color: #155724;
}

.tier-other {
  background: #e2e3e5;
  color: #383d41;
}

.school-info {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .label {
  font-weight: 600;
  color: #555;
  min-width: 80px;
}

.info-item .value {
  color: #666;
  word-break: break-word;
}

.info-item .link {
  color: #667eea;
  text-decoration: none;
}

.info-item .link:hover {
  text-decoration: underline;
}

.school-intro {
  padding: 15px 20px;
  flex: 1;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.school-actions {
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.btn-detail {
  display: inline-block;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  width: 100%;
  text-align: center;
  box-sizing: border-box;
}

.btn-detail:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-top: 20px;
}

.btn-pagination {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-pagination:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .school-search-container {
    padding: 20px 10px;
  }

  h1 {
    font-size: 24px;
  }

  .filter-box {
    padding: 20px;
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .button-group {
    flex-direction: column;
  }

  .school-cards {
    grid-template-columns: 1fr;
  }

  .pagination {
    flex-direction: column;
    gap: 10px;
  }

  .btn-pagination {
    width: 100%;
  }
}
</style>
