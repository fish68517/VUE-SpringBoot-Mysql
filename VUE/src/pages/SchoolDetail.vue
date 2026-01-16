<template>
  <div class="school-detail-container">
    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <p>Loading school details...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <router-link to="/schools" class="btn-back">Back to Search</router-link>
    </div>

    <!-- School Detail Content -->
    <div v-else-if="school" class="detail-content">
      <!-- Header Section -->
      <div class="header-section">
        <router-link to="/schools" class="btn-back">← Back to Search</router-link>
        
        <div class="school-header">
          <div class="header-info">
            <h1 class="school-name">{{ school.name }}</h1>
            <div class="header-meta">
              <span class="school-tier" :class="'tier-' + school.tier.toLowerCase()">
                {{ school.tier }}
              </span>
              <span class="school-city">📍 {{ school.city }}</span>
            </div>
          </div>

          <div class="header-actions">
            <button
              class="btn-favorite"
              :class="{ 'is-favorited': isFavorited }"
              @click="toggleFavorite"
              :disabled="favoriteLoading"
            >
              {{ isFavorited ? '★ Favorited' : '☆ Add to Favorites' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Main Content Grid -->
      <div class="main-grid">
        <!-- Left Column: School Info -->
        <div class="left-column">
          <!-- Introduction Section -->
          <section class="section">
            <h2>Introduction</h2>
            <div class="intro-text">
              {{ school.intro || 'No introduction available' }}
            </div>
            <div v-if="school.website" class="website-link">
              <a :href="school.website" target="_blank" rel="noopener noreferrer">
                Visit Official Website →
              </a>
            </div>
          </section>

          <!-- Exam Subjects Section -->
          <section class="section">
            <h2>Exam Subjects</h2>
            <div v-if="examSubjects.length > 0" class="subjects-list">
              <div v-for="subject in examSubjects" :key="subject.id" class="subject-item">
                <div class="subject-name">{{ subject.subjectName }}</div>
                <div class="subject-code">Code: {{ subject.subjectCode }}</div>
              </div>
            </div>
            <div v-else class="no-data">
              No exam subjects available
            </div>
          </section>

          <!-- Requirements Section -->
          <section class="section">
            <h2>Reexamination Requirements</h2>
            <div v-if="requirements.length > 0" class="requirements-list">
              <div v-for="req in requirements" :key="req.id" class="requirement-item">
                <div class="requirement-content" v-html="formatRequirement(req.content)"></div>
              </div>
            </div>
            <div v-else class="no-data">
              No requirements available
            </div>
          </section>
        </div>

        <!-- Right Column: Statistics and Comments -->
        <div class="right-column">
          <!-- Favorite Statistics Section -->
          <section class="section stats-section">
            <h2>Favorite Statistics</h2>
            <div v-if="favoriteStats" class="stats-content">
              <div class="stat-item">
                <div class="stat-label">Total Favorites</div>
                <div class="stat-value">{{ favoriteStats.totalFavorites }}</div>
              </div>

              <div v-if="favoriteStats.undergradTierDistribution" class="stat-group">
                <div class="stat-group-title">Undergraduate Tier Distribution</div>
                <div class="distribution-bars">
                  <div
                    v-for="(count, tier) in favoriteStats.undergradTierDistribution"
                    :key="'tier-' + tier"
                    class="distribution-item"
                  >
                    <div class="distribution-label">{{ formatTierLabel(tier) }}</div>
                    <div class="distribution-bar">
                      <div
                        class="bar-fill"
                        :style="{ width: calculatePercentage(count, favoriteStats.totalFavorites) + '%' }"
                      ></div>
                    </div>
                    <div class="distribution-value">{{ count }} ({{ calculatePercentage(count, favoriteStats.totalFavorites) }}%)</div>
                  </div>
                </div>
              </div>

              <div v-if="favoriteStats.cet4BucketDistribution" class="stat-group">
                <div class="stat-group-title">CET-4 Score Distribution</div>
                <div class="distribution-bars">
                  <div
                    v-for="(count, bucket) in favoriteStats.cet4BucketDistribution"
                    :key="'cet4-' + bucket"
                    class="distribution-item"
                  >
                    <div class="distribution-label">{{ bucket }}</div>
                    <div class="distribution-bar">
                      <div
                        class="bar-fill"
                        :style="{ width: calculatePercentage(count, favoriteStats.totalFavorites) + '%' }"
                      ></div>
                    </div>
                    <div class="distribution-value">{{ count }} ({{ calculatePercentage(count, favoriteStats.totalFavorites) }}%)</div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="no-data">
              No statistics available yet
            </div>
          </section>

          <!-- Comments Section -->
          <section class="section comments-section">
            <Comments :schoolId="schoolId" :pageSize="20" />
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import schoolService from '../services/schoolService'
import Comments from '../components/Comments.vue'

export default {
  name: 'SchoolDetail',
  components: {
    Comments
  },
  data() {
    return {
      school: null,
      examSubjects: [],
      requirements: [],
      favoriteStats: null,
      isFavorited: false,
      loading: true,
      favoriteLoading: false,
      error: ''
    }
  },
  computed: {
    schoolId() {
      return this.$route.params.id
    }
  },
  mounted() {
    this.loadSchoolDetail()
  },
  methods: {
    async loadSchoolDetail() {
      this.loading = true
      this.error = ''

      try {
        // Load school details
        const schoolResponse = await schoolService.getSchoolDetail(this.schoolId)
        this.school = schoolResponse.data.data

        // Load exam subjects
        const subjectsResponse = await schoolService.getSchoolExamSubjects(this.schoolId)
        this.examSubjects = subjectsResponse.data.data || []

        // Load requirements
        const requirementsResponse = await schoolService.getSchoolRequirements(this.schoolId)
        this.requirements = requirementsResponse.data.data || []

        // Load favorite statistics
        const statsResponse = await schoolService.getSchoolFavoriteStats(this.schoolId)
        this.favoriteStats = statsResponse.data.data

        // Check if school is favorited
        this.checkFavoriteStatus()
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to load school details. Please try again.'
        console.error('Error loading school detail:', error)
      } finally {
        this.loading = false
      }
    },

    async toggleFavorite() {
      this.favoriteLoading = true

      try {
        if (this.isFavorited) {
          await schoolService.removeFavorite(this.schoolId)
          this.isFavorited = false
        } else {
          await schoolService.addFavorite(this.schoolId)
          this.isFavorited = true
        }

        // Reload statistics after favorite status changes
        const statsResponse = await schoolService.getSchoolFavoriteStats(this.schoolId)
        this.favoriteStats = statsResponse.data.data
      } catch (error) {
        console.error('Error toggling favorite:', error)
      } finally {
        this.favoriteLoading = false
      }
    },

    checkFavoriteStatus() {
      // This would typically check if the current user has favorited this school
      // For now, we'll assume it's not favorited on initial load
      this.isFavorited = false
    },

    calculatePercentage(count, total) {
      if (total === 0) return 0
      return Math.round((count / total) * 100)
    },

    formatTierLabel(tier) {
      const tierMap = {
        '985': '985',
        '211': '211',
        'DOUBLE_NON': 'Double-Non',
        'OTHER': 'Other'
      }
      return tierMap[tier] || tier
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    formatRequirement(content) {
      if (!content) return ''
      // Replace line breaks with <br> tags
      return content.replace(/\n/g, '<br>')
    }
  }
}
</script>

<style scoped>
.school-detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.loading,
.error-message {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  padding: 40px;
  border-radius: 8px;
  text-align: center;
  color: #666;
  font-size: 16px;
}

.error-message {
  color: #c0392b;
}

.btn-back {
  display: inline-block;
  margin-bottom: 20px;
  padding: 10px 20px;
  background: white;
  color: #667eea;
  text-decoration: none;
  border-radius: 4px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #f0f0f0;
  transform: translateX(-2px);
}

.detail-content {
  max-width: 1200px;
  margin: 0 auto;
}

.header-section {
  margin-bottom: 30px;
}

.school-header {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.header-info {
  flex: 1;
}

.school-name {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 32px;
}

.header-meta {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.school-tier {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
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

.school-city {
  color: #666;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.btn-favorite {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-favorite:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-favorite:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-favorite.is-favorited {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.main-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

.section {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.section h2 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.intro-text {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.website-link {
  margin-top: 15px;
}

.website-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
}

.website-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}

.subjects-list,
.requirements-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.subject-item,
.requirement-item {
  padding: 15px;
  background: #f8f9fa;
  border-left: 4px solid #667eea;
  border-radius: 4px;
}

.subject-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.subject-code {
  font-size: 13px;
  color: #999;
}

.requirement-content {
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

.no-data {
  padding: 20px;
  text-align: center;
  color: #999;
  background: #f8f9fa;
  border-radius: 4px;
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 4px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-group-title {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.distribution-bars {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.distribution-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.distribution-label {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.distribution-bar {
  height: 20px;
  background: #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.distribution-value {
  font-size: 12px;
  color: #999;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  padding: 15px;
  background: #f8f9fa;
  border-left: 4px solid #667eea;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;
}

.comment-author {
  font-weight: 600;
  color: #333;
}

.comment-date {
  color: #999;
}

.comment-content {
  color: #666;
  line-height: 1.5;
  font-size: 14px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.comments-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.btn-pagination {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-pagination:hover:not(:disabled) {
  background: #764ba2;
  transform: translateY(-2px);
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 13px;
  font-weight: 600;
}

.right-column {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

@media (max-width: 1024px) {
  .main-grid {
    grid-template-columns: 1fr;
  }

  .school-header {
    flex-direction: column;
  }

  .header-actions {
    width: 100%;
  }

  .btn-favorite {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .school-detail-container {
    padding: 20px 10px;
  }

  .school-name {
    font-size: 24px;
  }

  .school-header {
    padding: 20px;
  }

  .section {
    padding: 20px;
  }

  .section h2 {
    font-size: 18px;
  }

  .header-meta {
    flex-direction: column;
    align-items: flex-start;
  }

  .comments-pagination {
    flex-direction: column;
    gap: 10px;
  }

  .btn-pagination {
    width: 100%;
  }
}
</style>
