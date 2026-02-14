<template>
  <div class="user-center-page">
    <div class="user-center-container">
      <!-- 用户信息卡片 -->
      <div class="user-header">
        <div class="user-avatar">
          <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.username" />
        </div>
        <div class="user-details">
          <h1>{{ userInfo.username }}</h1>
          <p class="user-bio">{{ userInfo.bio || '这个用户还没有添加个人简介' }}</p>
          <p class="user-email">📧 {{ userInfo.email }}</p>
        </div>
      </div>

      <!-- 标签页导航 -->
      <div class="tabs-container">
        <div class="tabs-nav">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            class="tab-btn"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 标签页内容 -->
        <div class="tabs-content">
          <!-- 个人信息标签页 -->
          <div v-if="activeTab === 'profile'" class="tab-pane">
            <div class="section-title">编辑个人信息</div>
            <form @submit.prevent="handleUpdateProfile" class="profile-form">
              <div class="form-group">
                <label for="username" class="form-label">用户名</label>
                <input
                  id="username"
                  v-model="profileForm.username"
                  type="text"
                  class="form-input"
                  disabled
                />
              </div>

              <div class="form-group">
                <label for="email" class="form-label">邮箱</label>
                <input
                  id="email"
                  v-model="profileForm.email"
                  type="email"
                  class="form-input"
                  placeholder="请输入邮箱地址"
                />
              </div>

              <div class="form-group">
                <label for="bio" class="form-label">个人简介</label>
                <textarea
                  id="bio"
                  v-model="profileForm.bio"
                  class="form-textarea"
                  placeholder="请输入个人简介（最多200字）"
                  maxlength="200"
                  rows="4"
                />
              </div>

              <div class="form-group">
                <label for="avatar" class="form-label">头像 URL</label>
                <input
                  id="avatar"
                  v-model="profileForm.avatar"
                  type="url"
                  class="form-input"
                  placeholder="请输入头像图片 URL"
                />
              </div>

              <button type="submit" class="submit-btn" :disabled="isUpdatingProfile">
                {{ isUpdatingProfile ? '保存中...' : '保存修改' }}
              </button>
            </form>
          </div>

          <!-- 收藏夹标签页 -->
          <div v-if="activeTab === 'collections'" class="tab-pane">
            <div class="section-title">我的收藏</div>
            <div v-if="collections.length > 0" class="collections-list">
              <div v-for="item in collections" :key="item.id" class="collection-item">
                <img :src="item.artwork.imageUrl" :alt="item.artwork.title" class="collection-image" />
                <div class="collection-info">
                  <h3>{{ item.artwork.title }}</h3>
                  <p class="collection-category">{{ item.artwork.category }}</p>
                  <p class="collection-date">收藏于 {{ formatDate(item.collectedAt) }}</p>
                </div>
                <router-link :to="`/artworks/${item.artwork.id}`" class="view-btn">查看作品</router-link>
              </div>
            </div>
            <div v-else class="empty-state">
              <p>还没有收藏任何作品</p>
              <router-link to="/artworks" class="explore-btn">去浏览作品</router-link>
            </div>
            <Pagination
              v-if="collections.length > 0"
              :current-page="collectionsPage"
              :total-pages="collectionsTotalPages"
              @change="collectionsPage = $event"
            />
          </div>

          <!-- 浏览历史标签页 -->
          <div v-if="activeTab === 'history'" class="tab-pane">
            <div class="section-title">浏览历史</div>
            <div v-if="viewHistory.length > 0" class="history-list">
              <div v-for="item in viewHistory" :key="item.id" class="history-item">
                <div class="history-icon">
                  {{ item.contentType === 'artwork' ? '🎨' : '📚' }}
                </div>
                <div class="history-info">
                  <h3>{{ item.title }}</h3>
                  <p class="history-type">
                    {{ item.contentType === 'artwork' ? '作品' : '知识文章' }}
                  </p>
                  <p class="history-date">浏览于 {{ formatDate(item.viewedAt) }}</p>
                </div>
                <router-link
                  :to="`/${item.contentType === 'artwork' ? 'artworks' : 'knowledge'}/${item.contentId}`"
                  class="view-btn"
                >
                  查看
                </router-link>
              </div>
            </div>
            <div v-else class="empty-state">
              <p>还没有浏览历史</p>
            </div>
            <Pagination
              v-if="viewHistory.length > 0"
              :current-page="historyPage"
              :total-pages="historyTotalPages"
              @change="historyPage = $event"
            />
          </div>

          <!-- 反馈表单标签页 -->
          <div v-if="activeTab === 'feedback'" class="tab-pane">
            <div class="section-title">提交反馈</div>
            <form @submit.prevent="handleSubmitFeedback" class="feedback-form">
              <div class="form-group">
                <label for="feedbackEmail" class="form-label">联系邮箱</label>
                <input
                  id="feedbackEmail"
                  v-model="feedbackForm.email"
                  type="email"
                  class="form-input"
                  placeholder="请输入您的邮箱地址"
                  required
                />
              </div>

              <div class="form-group">
                <label for="feedbackContent" class="form-label">反馈内容</label>
                <textarea
                  id="feedbackContent"
                  v-model="feedbackForm.content"
                  class="form-textarea"
                  placeholder="请输入您的反馈内容（最多1000字）"
                  maxlength="1000"
                  rows="6"
                  required
                />
              </div>

              <button type="submit" class="submit-btn" :disabled="isSubmittingFeedback">
                {{ isSubmittingFeedback ? '提交中...' : '提交反馈' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { UserService, FeedbackService } from '../services'
import { useAuthStore } from '../stores/authStore'
import { useToast } from '../utils/useToast'
import Pagination from '../components/Pagination.vue'

const authStore = useAuthStore()
const { success, error } = useToast()

const defaultAvatar = 'https://via.placeholder.com/120?text=User'

const activeTab = ref('profile')
const tabs = [
  { id: 'profile', label: '个人信息' },
  { id: 'collections', label: '我的收藏' },
  { id: 'history', label: '浏览历史' },
  { id: 'feedback', label: '提交反馈' },
]

// 用户信息
const userInfo = ref({
  username: '',
  email: '',
  bio: '',
  avatar: '',
})

// 个人信息表单
const profileForm = ref({
  username: '',
  email: '',
  bio: '',
  avatar: '',
})
const isUpdatingProfile = ref(false)

// 收藏列表
const collections = ref([])
const collectionsPage = ref(1)
const collectionsTotalPages = computed(() => Math.ceil(collections.value.length / 10))

// 浏览历史
const viewHistory = ref([])
const historyPage = ref(1)
const historyTotalPages = computed(() => Math.ceil(viewHistory.value.length / 10))

// 反馈表单
const feedbackForm = ref({
  email: '',
  content: '',
})
const isSubmittingFeedback = ref(false)

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await UserService.getUserInfo(authStore.user.id)
    userInfo.value = response.data
    profileForm.value = { ...response.data }
  } catch (err) {
    error('加载用户信息失败')
  }
}

// 加载收藏列表
const loadCollections = async () => {
  try {
    const response = await UserService.getCollections(authStore.user.id, {
      page: collectionsPage.value,
      pageSize: 10,
    })
    collections.value = response.data || []
  } catch (err) {
    error('加载收藏列表失败')
  }
}

// 加载浏览历史
const loadViewHistory = async () => {
  try {
    const response = await UserService.getViewHistory(authStore.user.id, {
      page: historyPage.value,
      pageSize: 10,
    })
    viewHistory.value = response.data || []
  } catch (err) {
    error('加载浏览历史失败')
  }
}

// 更新个人信息
const handleUpdateProfile = async () => {
  if (!profileForm.value.email) {
    error('请输入邮箱地址')
    return
  }

  isUpdatingProfile.value = true
  try {
    await UserService.updateUserInfo(authStore.user.id, {
      email: profileForm.value.email,
      bio: profileForm.value.bio,
      avatar: profileForm.value.avatar,
    })

    // 更新本地用户信息
    userInfo.value = { ...profileForm.value }
    authStore.user.email = profileForm.value.email
    authStore.user.bio = profileForm.value.bio
    authStore.user.avatar = profileForm.value.avatar

    success('个人信息已更新')
  } catch (err) {
    error(err.response?.data?.message || '更新个人信息失败')
  } finally {
    isUpdatingProfile.value = false
  }
}

// 提交反馈
const handleSubmitFeedback = async () => {
  if (!feedbackForm.value.email || !feedbackForm.value.content) {
    error('请填写所有字段')
    return
  }

  isSubmittingFeedback.value = true
  try {
    await FeedbackService.submitFeedback({
      userId: authStore.user.id,
      email: feedbackForm.value.email,
      content: feedbackForm.value.content,
    })

    success('反馈已提交，感谢您的意见')
    feedbackForm.value = {
      email: userInfo.value.email,
      content: '',
    }
  } catch (err) {
    error(err.response?.data?.message || '提交反馈失败')
  } finally {
    isSubmittingFeedback.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  loadUserInfo()
  loadCollections()
  loadViewHistory()
  feedbackForm.value.email = authStore.user.email
})
</script>

<style scoped>
.user-center-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg);
  max-width: 1200px;
  margin: 0 auto;
}

/* 用户信息头部 */
.user-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-xl);
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--primary-color) 100%);
  border-radius: var(--border-radius-lg);
  color: white;
  box-shadow: var(--shadow-lg);
}

.user-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid white;
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.user-details h1 {
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-sm);
  font-weight: 700;
}

.user-bio {
  font-size: var(--font-size-base);
  margin-bottom: var(--spacing-sm);
  opacity: 0.9;
}

.user-email {
  font-size: var(--font-size-sm);
  opacity: 0.8;
}

/* 标签页 */
.tabs-container {
  background: white;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.tabs-nav {
  display: flex;
  border-bottom: 2px solid var(--border-color);
  background-color: var(--bg-primary);
}

.tab-btn {
  flex: 1;
  padding: var(--spacing-lg);
  background: none;
  border: none;
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
}

.tab-btn:hover {
  color: var(--primary-color);
}

.tab-btn.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

.tabs-content {
  padding: var(--spacing-xl);
}

.tab-pane {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.section-title {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
}

/* 表单样式 */
.profile-form,
.feedback-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  max-width: 600px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-label {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.form-input,
.form-textarea {
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  font-family: inherit;
  transition: all 0.3s ease;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
}

.form-input:disabled {
  background-color: var(--bg-primary);
  cursor: not-allowed;
}

.submit-btn {
  padding: var(--spacing-md);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  align-self: flex-start;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 收藏列表 */
.collections-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.collection-item {
  display: flex;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.collection-item:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--primary-color);
}

.collection-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: var(--border-radius-md);
  flex-shrink: 0;
}

.collection-info {
  flex: 1;
}

.collection-info h3 {
  font-size: var(--font-size-lg);
  margin-bottom: var(--spacing-sm);
  color: var(--text-primary);
}

.collection-category {
  font-size: var(--font-size-sm);
  color: var(--primary-color);
  margin-bottom: var(--spacing-sm);
}

.collection-date {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.view-btn {
  display: inline-block;
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--primary-color);
  color: white;
  border-radius: var(--border-radius-md);
  text-decoration: none;
  font-size: var(--font-size-sm);
  font-weight: 600;
  transition: all 0.3s ease;
  align-self: flex-start;
}

.view-btn:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

/* 浏览历史 */
.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.history-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  border-left: 4px solid var(--primary-color);
  transition: all 0.3s ease;
}

.history-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateX(4px);
}

.history-icon {
  font-size: 32px;
  min-width: 50px;
  text-align: center;
}

.history-info {
  flex: 1;
}

.history-info h3 {
  font-size: var(--font-size-base);
  margin-bottom: var(--spacing-sm);
  color: var(--text-primary);
}

.history-type {
  font-size: var(--font-size-sm);
  color: var(--primary-color);
  margin-bottom: var(--spacing-sm);
}

.history-date {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--text-secondary);
}

.empty-state p {
  font-size: var(--font-size-base);
  margin-bottom: var(--spacing-lg);
}

.explore-btn {
  display: inline-block;
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--primary-color);
  color: white;
  border-radius: var(--border-radius-md);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.explore-btn:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-center-page {
    padding: var(--spacing-md);
  }

  .user-header {
    flex-direction: column;
    text-align: center;
    gap: var(--spacing-lg);
  }

  .user-avatar {
    width: 100px;
    height: 100px;
  }

  .tabs-nav {
    flex-wrap: wrap;
  }

  .tab-btn {
    flex: 1;
    min-width: 100px;
    padding: var(--spacing-md);
  }

  .tabs-content {
    padding: var(--spacing-lg);
  }

  .collection-item,
  .history-item {
    flex-direction: column;
  }

  .collection-image {
    width: 100%;
    height: 200px;
  }

  .profile-form,
  .feedback-form {
    max-width: 100%;
  }
}
</style>
