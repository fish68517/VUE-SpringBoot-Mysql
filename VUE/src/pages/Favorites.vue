<template>
  <div class="favorites-container">
    <div class="header-section">
      <h1>我的收藏院校</h1>
      <p class="subtitle">管理你的考研目标院校收藏</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <p>正在加载收藏列表...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button class="btn-retry" @click="loadFavorites">重试</button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="favorites.length === 0" class="empty-state">
      <div class="empty-icon">♡</div>
      <h2>你还没有收藏任何院校</h2>
      <p>去搜索并收藏院校，方便随时追踪你的目标院校。</p>
      <router-link to="/app/schools" class="btn-search">
        搜索院校
      </router-link>
    </div>

    <!-- 收藏列表 -->
    <div v-else class="favorites-content">
      <!-- 结果信息 -->
      <div class="results-info">
        <p>
          你共收藏了 {{ totalElements }} 所院校（第 {{ currentPage + 1 }} / {{ totalPages }} 页）
        </p>
      </div>

      <!-- 收藏卡片 -->
      <div class="favorite-cards">
        <div v-for="school in favorites" :key="school.id" class="favorite-card">
          <div class="card-header">
            <div class="school-info">
              <h3 class="school-name">{{ school.name }}</h3>
              <div class="school-meta">
                <span class="school-tier" :class="'tier-' + school.tier.toLowerCase()">
                  {{ school.tier }}
                </span>
                <span class="school-city">📍 {{ school.city }}</span>
              </div>
            </div>
            <div class="card-actions">
              <router-link :to="`/app/school/${school.id}`" class="btn-view">
                查看详情
              </router-link>
              <button
                class="btn-remove"
                @click="removeFavorite(school.id)"
                :disabled="removingId === school.id"
              >
                {{ removingId === school.id ? '正在取消...' : '取消收藏' }}
              </button>
            </div>
          </div>

          <div class="card-content">
            <div class="intro-text">
              {{ truncateText(school.intro, 200) }}
            </div>
            <div v-if="school.website" class="website-link">
              <a :href="school.website" target="_blank" rel="noopener noreferrer">
                访问官网 →
              </a>
            </div>
          </div>

          <div class="card-footer">
            <span class="added-date">收藏时间：{{ formatDate(school.favoritedAt) }}</span>

          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          class="btn-pagination"
          :disabled="currentPage === 0"
          @click="handlePreviousPage"
        >
          上一页
        </button>

        <div class="page-info">
          第 {{ currentPage + 1 }} / {{ totalPages }} 页
        </div>

        <button
          class="btn-pagination"
          :disabled="currentPage >= totalPages - 1"
          @click="handleNextPage"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import schoolService from '../services/schoolService'

export default {
  name: 'Favorites',
  data() {
    return {
      favorites: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      loading: true,
      error: '',
      removingId: null
    }
  },
  mounted() {
    this.loadFavorites()
  },
  methods: {
    async loadFavorites() {
      this.loading = true
      this.error = ''

      try {
        const response = await schoolService.getUserFavorites(this.currentPage, 20)
        const data = response.data.data

        // ✅ 把 favorite 结构转换成页面需要的 school 结构
        const favoritesContent = data.content || []

        this.favorites = favoritesContent
          .map(fav => ({
            // school 信息（用于展示）
            ...(fav.school || {}),
            // ✅ 保留 favoriteId 方便取消收藏（后面用得到）
            favoriteId: fav.id,
            // ✅ 收藏时间用 fav.createdAt（不是 school.createdAt）
            favoritedAt: fav.createdAt,
            // ✅ schoolId 保留（如果需要）
            schoolId: fav.schoolId
          }))
          .filter(s => s.id) // 防止 school 为空导致渲染出错

        this.totalElements = data.totalElements || 0
        this.totalPages = data.totalPages || 0
        this.currentPage = data.number || 0
      } catch (error) {
        this.error = error.response?.data?.message || '加载收藏列表失败，请稍后重试。'
        this.favorites = []
      } finally {
        this.loading = false
      }
    },


    async handleNextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        await this.loadPage()
      }
    },

    async handlePreviousPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        await this.loadPage()
      }
    },

    async loadPage() {
        this.loading = true
        this.error = ''

        try {
          const response = await schoolService.getUserFavorites(this.currentPage, 20)
          const data = response.data.data

          const favoritesContent = data.content || []
          this.favorites = favoritesContent
            .map(fav => ({
              ...(fav.school || {}),
              favoriteId: fav.id,
              favoritedAt: fav.createdAt,
              schoolId: fav.schoolId
            }))
            .filter(s => s.id)

          this.currentPage = data.number || 0
        } catch (error) {
          this.error = error.response?.data?.message || '加载收藏列表失败，请稍后重试。'
          this.favorites = []
        } finally {
          this.loading = false
        }
    },


    async removeFavorite(schoolId) {
      this.removingId = schoolId

      try {
        await schoolService.removeFavorite(schoolId)
        // 从列表移除该院校
        this.favorites = this.favorites.filter(school => school.id !== schoolId)
        this.totalElements--

        // 当前页删空且不是第一页，则返回上一页
        if (this.favorites.length === 0 && this.currentPage > 0) {
          this.currentPage--
          await this.loadPage()
        }
      } catch (error) {
        this.error = error.response?.data?.message || '取消收藏失败，请稍后重试。'
      } finally {
        this.removingId = null
      }
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
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
/* 样式无需翻译，保留原样 */
</style>


<style scoped>
.favorites-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.header-section {
  max-width: 1200px;
  margin: 0 auto 40px;
  text-align: center;
  color: white;
}

.header-section h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
}

.subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.loading,
.error-message,
.empty-state {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  padding: 60px 40px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.loading {
  color: #666;
  font-size: 16px;
}

.error-message {
  color: #c0392b;
}

.error-message p {
  margin: 0 0 20px 0;
  font-size: 16px;
}

.btn-retry {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-retry:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.empty-state {
  padding: 80px 40px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.empty-state p {
  margin: 0 0 30px 0;
  color: #666;
  font-size: 16px;
}

.btn-search {
  display: inline-block;
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-search:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.favorites-content {
  max-width: 1200px;
  margin: 0 auto;
}

.results-info {
  background: white;
  padding: 15px 20px;
  border-radius: 8px 8px 0 0;
  border-bottom: 1px solid #eee;
  color: #666;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.favorite-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.favorite-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
}

.favorite-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 15px;
}

.school-info {
  flex: 1;
}

.school-name {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.school-meta {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
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

.school-city {
  color: #666;
  font-size: 14px;
}

.card-actions {
  display: flex;
  gap: 10px;
  flex-direction: column;
  min-width: 120px;
}

.btn-view,
.btn-remove {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  text-decoration: none;
  display: block;
}

.btn-view {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-view:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
}

.btn-remove {
  background: #f0f0f0;
  color: #333;
}

.btn-remove:hover:not(:disabled) {
  background: #e74c3c;
  color: white;
}

.btn-remove:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.card-content {
  padding: 15px 20px;
  flex: 1;
  border-bottom: 1px solid #eee;
}

.intro-text {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 10px;
}

.website-link {
  margin-top: 10px;
}

.website-link a {
  color: #667eea;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s;
}

.website-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}

.card-footer {
  padding: 12px 20px;
  background: #f8f9fa;
  font-size: 12px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
  .favorites-container {
    padding: 20px 10px;
  }

  .header-section h1 {
    font-size: 24px;
  }

  .loading,
  .error-message,
  .empty-state {
    padding: 40px 20px;
  }

  .empty-state {
    padding: 60px 20px;
  }

  .favorite-cards {
    grid-template-columns: 1fr;
  }

  .card-header {
    flex-direction: column;
  }

  .card-actions {
    flex-direction: row;
    width: 100%;
  }

  .btn-view,
  .btn-remove {
    flex: 1;
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
