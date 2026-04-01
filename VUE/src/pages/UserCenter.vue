<template>
  <div class="user-center-page">
    <section class="hero-section">
      <div class="hero-main">
        <div class="hero-avatar">{{ userInitial }}</div>

        <div class="hero-copy">
          <span class="hero-kicker">{{ copy.heroKicker }}</span>
          <h1>{{ displayName }}</h1>
          <p>{{ displayBio }}</p>

          <div class="hero-tags">
            <span class="hero-tag">{{ userInfo.email || copy.noEmail }}</span>
            <span class="hero-tag">{{ copy.memberLabel }} #{{ authStore.user?.id || '--' }}</span>
          </div>
        </div>
      </div>

      <div class="hero-stats">
        <div class="hero-stat-card">
          <span>{{ copy.collectionStat }}</span>
          <strong>{{ collectionsTotalItems }}</strong>
        </div>
        <div class="hero-stat-card">
          <span>{{ copy.historyStat }}</span>
          <strong>{{ historyTotalItems }}</strong>
        </div>
        <div class="hero-stat-card">
          <span>{{ copy.activeTabStat }}</span>
          <strong>{{ activeTabLabel }}</strong>
        </div>
      </div>
    </section>

    <section class="dashboard-shell">
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

      <div class="tabs-content">
        <div v-if="activeTab === 'profile'" class="tab-pane profile-pane">
          <section class="content-card">
            <div class="section-header">
              <div>
                <span class="section-kicker">{{ copy.profileKicker }}</span>
                <h2>{{ copy.profileTitle }}</h2>
                <p>{{ copy.profileSubtitle }}</p>
              </div>
            </div>

            <form class="profile-form" @submit.prevent="handleUpdateProfile">
              <label class="form-field">
                <span>{{ copy.usernameLabel }}</span>
                <input v-model="profileForm.username" type="text" disabled />
              </label>

              <label class="form-field">
                <span>{{ copy.emailLabel }}</span>
                <input
                  v-model="profileForm.email"
                  type="email"
                  :placeholder="copy.emailPlaceholder"
                />
              </label>

              <label class="form-field form-field--full">
                <span>{{ copy.bioLabel }}</span>
                <textarea
                  v-model="profileForm.bio"
                  rows="5"
                  maxlength="200"
                  :placeholder="copy.bioPlaceholder"
                ></textarea>
              </label>

              <div class="form-footer">
                <span class="form-hint">{{ (profileForm.bio || '').length }}/200</span>
                <button class="primary-btn" type="submit" :disabled="isUpdatingProfile">
                  {{ isUpdatingProfile ? copy.savingProfile : copy.saveProfile }}
                </button>
              </div>
            </form>
          </section>

          <aside class="content-card content-card--aside">
            <div class="section-header">
              <div>
                <span class="section-kicker">{{ copy.tipKicker }}</span>
                <h2>{{ copy.tipTitle }}</h2>
                <p>{{ copy.tipSubtitle }}</p>
              </div>
            </div>

            <div class="tip-list">
              <div class="tip-item">
                <strong>{{ copy.tipOneTitle }}</strong>
                <p>{{ copy.tipOneText }}</p>
              </div>
              <div class="tip-item">
                <strong>{{ copy.tipTwoTitle }}</strong>
                <p>{{ copy.tipTwoText }}</p>
              </div>
              <div class="tip-item">
                <strong>{{ copy.tipThreeTitle }}</strong>
                <p>{{ copy.tipThreeText }}</p>
              </div>
            </div>
          </aside>
        </div>

        <div v-if="activeTab === 'collections'" class="tab-pane">
          <section class="content-card">
            <div class="section-header">
              <div>
                <span class="section-kicker">{{ copy.collectionKicker }}</span>
                <h2>{{ copy.collectionTitle }}</h2>
                <p>{{ copy.collectionSubtitle }}</p>
              </div>
            </div>

            <div v-if="isLoadingCollections" class="loading-container">
              <div class="spinner"></div>
              <p>{{ copy.loadingCollections }}</p>
            </div>

            <div v-else-if="collections.length > 0" class="collection-grid">
              <article
                v-for="item in collections"
                :key="item.id"
                class="collection-card"
              >
                <img
                  :src="item.artworkResponse?.imageUrl"
                  :alt="item.artworkResponse?.title"
                  class="collection-image"
                />

                <div class="collection-body">
                  <div class="collection-tags">
                    <span class="content-tag">
                      {{ item.artworkResponse?.category || copy.defaultCategory }}
                    </span>
                    <span class="content-tag content-tag--light">
                      {{ copy.collectPrefix }} {{ formatDate(item.collectedAt) }}
                    </span>
                  </div>

                  <h3>{{ item.artworkResponse?.title }}</h3>
                  <p>{{ getCollectionDescription(item) }}</p>

                  <div class="collection-footer">
                    <div class="collection-meta">
                      <span>{{ copy.viewsPrefix }} {{ item.artworkResponse?.viewCount || 0 }}</span>
                      <span class="meta-divider"></span>
                      <span>{{ copy.collectCountPrefix }} {{ item.artworkResponse?.collectCount || 0 }}</span>
                    </div>

                    <router-link
                      :to="`/artworks/${item.artworkResponse?.id}`"
                      class="text-link"
                    >
                      {{ copy.viewArtwork }}
                    </router-link>
                  </div>
                </div>
              </article>

              <Pagination
                :current-page="collectionsPage"
                :total-pages="collectionsTotalPages"
                :total-items="collectionsTotalItems"
                @update:current-page="handleCollectionsPageChange"
              />
            </div>

            <div v-else class="empty-state">
              <div class="empty-icon">{{ copy.emptyCollectionIcon }}</div>
              <h3>{{ copy.emptyCollectionTitle }}</h3>
              <p>{{ copy.emptyCollectionText }}</p>
              <router-link to="/artworks" class="primary-btn primary-btn--link">
                {{ copy.goExplore }}
              </router-link>
            </div>
          </section>
        </div>

        <div v-if="activeTab === 'history'" class="tab-pane">
          <section class="content-card">
            <div class="section-header">
              <div>
                <span class="section-kicker">{{ copy.historyKicker }}</span>
                <h2>{{ copy.historyTitle }}</h2>
                <p>{{ copy.historySubtitle }}</p>
              </div>
            </div>

            <div v-if="isLoadingHistory" class="loading-container">
              <div class="spinner"></div>
              <p>{{ copy.loadingHistory }}</p>
            </div>

            <div v-else-if="viewHistory.length > 0" class="history-list">
              <article
                v-for="item in viewHistory"
                :key="item.id"
                class="history-card"
              >
                <div class="history-icon" :class="item.contentType === 'artwork' ? 'is-artwork' : 'is-knowledge'">
                  {{ item.contentType === 'artwork' ? copy.artworkIcon : copy.knowledgeIcon }}
                </div>

                <div class="history-body">
                  <div class="history-tags">
                    <span class="content-tag">
                      {{ item.contentType === 'artwork' ? copy.artworkType : copy.knowledgeType }}
                    </span>
                    <span class="content-tag content-tag--light">
                      {{ formatDate(item.viewedAt) }}
                    </span>
                  </div>

                  <h3>{{ item.title }}</h3>
                  <p>{{ item.subtitle }}</p>

                  <router-link :to="item.route" class="text-link">
                    {{ copy.viewDetail }}
                  </router-link>
                </div>
              </article>

              <Pagination
                :current-page="historyPage"
                :total-pages="historyTotalPages"
                :total-items="historyTotalItems"
                @update:current-page="handleHistoryPageChange"
              />
            </div>

            <div v-else class="empty-state">
              <div class="empty-icon">{{ copy.emptyHistoryIcon }}</div>
              <h3>{{ copy.emptyHistoryTitle }}</h3>
              <p>{{ copy.emptyHistoryText }}</p>
            </div>
          </section>
        </div>

        <div v-if="activeTab === 'feedback'" class="tab-pane feedback-pane">
          <section class="content-card">
            <div class="section-header">
              <div>
                <span class="section-kicker">{{ copy.feedbackKicker }}</span>
                <h2>{{ copy.feedbackTitle }}</h2>
                <p>{{ copy.feedbackSubtitle }}</p>
              </div>
            </div>

            <form class="feedback-form" @submit.prevent="handleSubmitFeedback">
              <label class="form-field">
                <span>{{ copy.feedbackEmailLabel }}</span>
                <input
                  v-model="feedbackForm.email"
                  type="email"
                  :placeholder="copy.emailPlaceholder"
                  required
                />
              </label>

              <label class="form-field form-field--full">
                <span>{{ copy.feedbackContentLabel }}</span>
                <textarea
                  v-model="feedbackForm.content"
                  rows="7"
                  maxlength="1000"
                  :placeholder="copy.feedbackPlaceholder"
                  required
                ></textarea>
              </label>

              <div class="form-footer">
                <span class="form-hint">{{ (feedbackForm.content || '').length }}/1000</span>
                <button class="primary-btn" type="submit" :disabled="isSubmittingFeedback">
                  {{ isSubmittingFeedback ? copy.submittingFeedback : copy.submitFeedback }}
                </button>
              </div>
            </form>
          </section>

          <aside class="content-card content-card--aside">
            <div class="section-header">
              <div>
                <span class="section-kicker">{{ copy.feedbackTipKicker }}</span>
                <h2>{{ copy.feedbackTipTitle }}</h2>
                <p>{{ copy.feedbackTipSubtitle }}</p>
              </div>
            </div>

            <div class="tip-list">
              <div class="tip-item">
                <strong>{{ copy.feedbackTipOneTitle }}</strong>
                <p>{{ copy.feedbackTipOneText }}</p>
              </div>
              <div class="tip-item">
                <strong>{{ copy.feedbackTipTwoTitle }}</strong>
                <p>{{ copy.feedbackTipTwoText }}</p>
              </div>
              <div class="tip-item">
                <strong>{{ copy.feedbackTipThreeTitle }}</strong>
                <p>{{ copy.feedbackTipThreeText }}</p>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import Pagination from '../components/Pagination.vue'
import { ArtworkService, FeedbackService, KnowledgeService, UserService } from '../services'
import { useAuthStore } from '../stores/authStore'
import { useToast } from '../utils/useToast'

const copy = {
  heroKicker: '用户中心',
  memberLabel: '用户编号',
  noEmail: '未填写邮箱',
  collectionStat: '收藏作品',
  historyStat: '浏览记录',
  activeTabStat: '当前板块',
  profileKicker: '个人资料',
  profileTitle: '编辑个人信息',
  profileSubtitle: '更新邮箱和个人简介，让你的个人主页信息更完整。',
  usernameLabel: '用户名',
  emailLabel: '邮箱',
  bioLabel: '个人简介',
  emailPlaceholder: '请输入邮箱地址',
  bioPlaceholder: '写一段简短的个人简介，介绍你的兴趣或擅长的方向。',
  saveProfile: '保存修改',
  savingProfile: '保存中...',
  tipKicker: '资料建议',
  tipTitle: '让你的主页更完整',
  tipSubtitle: '补充清晰的邮箱和简介，方便在社区互动中展示更完整的个人形象。',
  tipOneTitle: '保持简介简洁',
  tipOneText: '一句话说明你的兴趣方向，会比空白资料更容易建立识别度。',
  tipTwoTitle: '使用常用邮箱',
  tipTwoText: '后续反馈和通知会更方便联系到你。',
  tipThreeTitle: '定期更新资料',
  tipThreeText: '如果你的兴趣方向有变化，也可以及时调整个人简介。',
  collectionKicker: '收藏作品',
  collectionTitle: '我的收藏',
  collectionSubtitle: '这里展示你保存过的作品，方便后续继续浏览与回看。',
  loadingCollections: '正在加载收藏作品...',
  defaultCategory: '未分类',
  collectPrefix: '收藏于',
  viewsPrefix: '浏览',
  collectCountPrefix: '收藏',
  viewArtwork: '查看作品',
  emptyCollectionIcon: '藏',
  emptyCollectionTitle: '还没有收藏作品',
  emptyCollectionText: '看到喜欢的刺绣作品时，可以先收藏起来，方便后续继续查看。',
  goExplore: '去浏览作品',
  historyKicker: '浏览记录',
  historyTitle: '最近浏览',
  historySubtitle: '这里保留你最近看过的内容，方便快速返回继续浏览。',
  loadingHistory: '正在加载浏览记录...',
  artworkIcon: '绣',
  knowledgeIcon: '知',
  artworkType: '作品',
  knowledgeType: '知识文章',
  viewDetail: '查看详情',
  emptyHistoryIcon: '史',
  emptyHistoryTitle: '还没有浏览记录',
  emptyHistoryText: '你浏览过作品或知识内容后，会在这里自动生成记录。',
  feedbackKicker: '意见反馈',
  feedbackTitle: '提交反馈',
  feedbackSubtitle: '告诉我们你在使用中的建议、问题或希望新增的功能。',
  feedbackEmailLabel: '联系邮箱',
  feedbackContentLabel: '反馈内容',
  feedbackPlaceholder: '请尽量描述清楚问题场景、预期效果或建议内容。',
  submitFeedback: '提交反馈',
  submittingFeedback: '提交中...',
  feedbackTipKicker: '反馈建议',
  feedbackTipTitle: '怎样的反馈更有效',
  feedbackTipSubtitle: '越具体的描述，越有助于后续定位和优化。',
  feedbackTipOneTitle: '描述操作路径',
  feedbackTipOneText: '说明你在哪个页面、进行了什么操作，问题更容易复现。',
  feedbackTipTwoTitle: '写清楚预期结果',
  feedbackTipTwoText: '告诉我们你原本希望看到什么结果，会更方便判断差异。',
  feedbackTipThreeTitle: '一次聚焦一个问题',
  feedbackTipThreeText: '把多个问题拆开描述，处理效率会更高。',
  profileSaveSuccess: '个人信息已更新',
  profileLoadFail: '加载用户信息失败',
  collectionLoadFail: '加载收藏列表失败',
  historyLoadFail: '加载浏览记录失败',
  feedbackSuccess: '反馈已提交，感谢你的建议',
  feedbackFail: '提交反馈失败，请稍后重试',
  emailRequired: '请输入邮箱地址',
  feedbackRequired: '请填写完整的反馈信息',
  unknownArtwork: '作品内容',
  unknownKnowledge: '知识内容',
}

const authStore = useAuthStore()
const { success, error } = useToast()

const tabs = [
  { id: 'profile', label: '个人信息' },
  { id: 'collections', label: '我的收藏' },
  { id: 'history', label: '浏览记录' },
  { id: 'feedback', label: '提交反馈' },
]

const activeTab = ref('profile')

const userInfo = ref({
  username: '',
  email: '',
  bio: '',
  avatar: '',
})

const profileForm = ref({
  username: '',
  email: '',
  bio: '',
  avatar: '',
})

const isUpdatingProfile = ref(false)

const collections = ref([])
const collectionsPage = ref(1)
const collectionsTotalPages = ref(1)
const collectionsTotalItems = ref(0)
const isLoadingCollections = ref(false)

const viewHistory = ref([])
const historyPage = ref(1)
const historyTotalPages = ref(1)
const historyTotalItems = ref(0)
const isLoadingHistory = ref(false)

const feedbackForm = ref({
  email: '',
  content: '',
})
const isSubmittingFeedback = ref(false)

const displayName = computed(() => userInfo.value.username || authStore.user?.username || '--')
const displayBio = computed(() => userInfo.value.bio || '补充一段简介，让其他用户更快认识你。')
const userInitial = computed(() => String(displayName.value).trim().slice(0, 1).toUpperCase() || 'U')
const activeTabLabel = computed(() => tabs.find((tab) => tab.id === activeTab.value)?.label || '--')

const loadUserInfo = async () => {
  try {
    const response = await UserService.getUserInfo(authStore.user.id)
    userInfo.value = response || {}
    profileForm.value = {
      username: response?.username || authStore.user?.username || '',
      email: response?.email || authStore.user?.email || '',
      bio: response?.bio || '',
      avatar: response?.avatar || '',
    }
    feedbackForm.value.email = response?.email || authStore.user?.email || ''
  } catch (err) {
    console.error('Failed to load user info:', err)
    error(copy.profileLoadFail)
  }
}

const loadCollections = async () => {
  isLoadingCollections.value = true

  try {
    const response = await UserService.getCollections(authStore.user.id, {
      pageNum: collectionsPage.value,
      pageSize: 6,
    })

    collections.value = response.items || []
    collectionsTotalPages.value = response.totalPages || 1
    collectionsTotalItems.value = response.total || 0
  } catch (err) {
    console.error('Failed to load collections:', err)
    error(copy.collectionLoadFail)
  } finally {
    isLoadingCollections.value = false
  }
}

const enrichHistoryItem = async (item) => {
  const route =
    item.contentType === 'artwork'
      ? `/artworks/${item.contentId}`
      : `/knowledge/${item.contentId}`

  try {
    if (item.contentType === 'artwork') {
      const detail = await ArtworkService.getArtworkDetail(item.contentId)
      return {
        ...item,
        title: detail.title || `${copy.unknownArtwork} #${item.contentId}`,
        subtitle: detail.category || copy.artworkType,
        route,
      }
    }

    const detail = await KnowledgeService.getKnowledgeDetail(item.contentId)
    return {
      ...item,
      title: detail.title || `${copy.unknownKnowledge} #${item.contentId}`,
      subtitle: detail.category || copy.knowledgeType,
      route,
    }
  } catch (err) {
    return {
      ...item,
      title:
        item.contentType === 'artwork'
          ? `${copy.unknownArtwork} #${item.contentId}`
          : `${copy.unknownKnowledge} #${item.contentId}`,
      subtitle: item.contentType === 'artwork' ? copy.artworkType : copy.knowledgeType,
      route,
    }
  }
}

const loadViewHistory = async () => {
  isLoadingHistory.value = true

  try {
    const response = await UserService.getViewHistory(authStore.user.id, {
      pageNum: historyPage.value,
      pageSize: 6,
    })

    const items = response.items || []
    viewHistory.value = await Promise.all(items.map((item) => enrichHistoryItem(item)))
    historyTotalPages.value = response.totalPages || 1
    historyTotalItems.value = response.total || 0
  } catch (err) {
    console.error('Failed to load history:', err)
    error(copy.historyLoadFail)
  } finally {
    isLoadingHistory.value = false
  }
}

const handleUpdateProfile = async () => {
  if (!profileForm.value.email) {
    error(copy.emailRequired)
    return
  }

  isUpdatingProfile.value = true

  try {
    await UserService.updateUserInfo(authStore.user.id, {
      email: profileForm.value.email,
      bio: profileForm.value.bio,
      avatar: profileForm.value.avatar,
    })

    userInfo.value = {
      ...userInfo.value,
      email: profileForm.value.email,
      bio: profileForm.value.bio,
      avatar: profileForm.value.avatar,
      username: profileForm.value.username,
    }

    authStore.setUser({
      ...authStore.user,
      email: profileForm.value.email,
      bio: profileForm.value.bio,
      avatar: profileForm.value.avatar,
      username: profileForm.value.username,
    })

    feedbackForm.value.email = profileForm.value.email
    success(copy.profileSaveSuccess)
  } catch (err) {
    console.error('Failed to update profile:', err)
    error(err.response?.message || copy.profileLoadFail)
  } finally {
    isUpdatingProfile.value = false
  }
}

const handleSubmitFeedback = async () => {
  if (!feedbackForm.value.email || !feedbackForm.value.content.trim()) {
    error(copy.feedbackRequired)
    return
  }

  isSubmittingFeedback.value = true

  try {
    await FeedbackService.submitFeedback({
      userId: authStore.user.id,
      email: feedbackForm.value.email,
      content: feedbackForm.value.content.trim(),
    })

    success(copy.feedbackSuccess)
    feedbackForm.value = {
      email: feedbackForm.value.email,
      content: '',
    }
  } catch (err) {
    console.error('Failed to submit feedback:', err)
    error(err.response?.message || copy.feedbackFail)
  } finally {
    isSubmittingFeedback.value = false
  }
}

const handleCollectionsPageChange = (newPage) => {
  collectionsPage.value = newPage
  loadCollections()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleHistoryPageChange = (newPage) => {
  historyPage.value = newPage
  loadViewHistory()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const getCollectionDescription = (item) => {
  const text = String(item.artworkResponse?.description || '').replace(/\s+/g, ' ').trim()
  return text ? (text.length > 90 ? `${text.slice(0, 90)}...` : text) : '收藏一件喜欢的作品，随时回来继续浏览。'
}

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
})
</script>

<style scoped>
.user-center-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 1180px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(300px, 0.8fr);
  gap: var(--spacing-lg);
  padding: 32px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.18), transparent 32%),
    linear-gradient(135deg, #15466f 0%, #0c6a8a 48%, #49a7a7 100%);
  box-shadow: 0 24px 48px rgba(21, 70, 111, 0.22);
}

.hero-main {
  display: flex;
  align-items: center;
  gap: 20px;
}

.hero-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 110px;
  height: 110px;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.16);
  color: #ffffff;
  font-size: 40px;
  font-weight: 800;
  border: 1px solid rgba(255, 255, 255, 0.22);
  backdrop-filter: blur(8px);
  flex-shrink: 0;
}

.hero-copy {
  display: flex;
  flex-direction: column;
  gap: 12px;
  color: #ffffff;
}

.hero-kicker,
.section-kicker {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.hero-kicker {
  background: rgba(255, 255, 255, 0.16);
}

.hero-copy h1 {
  margin: 0;
  font-size: 38px;
  line-height: 1.15;
  font-weight: 800;
}

.hero-copy p {
  margin: 0;
  max-width: 620px;
  color: rgba(255, 255, 255, 0.88);
  line-height: 1.85;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-tag {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
  font-size: 13px;
  font-weight: 600;
}

.hero-stats {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.hero-stat-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.16);
  color: #ffffff;
  backdrop-filter: blur(10px);
}

.hero-stat-card span {
  color: rgba(255, 255, 255, 0.74);
  font-size: 13px;
}

.hero-stat-card strong {
  font-size: 28px;
  font-weight: 800;
}

.dashboard-shell {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.tabs-nav {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 12px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid #e5ecf3;
  box-shadow: 0 12px 26px rgba(15, 59, 105, 0.06);
}

.tab-btn {
  padding: 12px 18px;
  border: none;
  border-radius: 14px;
  background: #f5f8fb;
  color: #4d6379;
  font-size: 15px;
  font-weight: 700;
  transition: background 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}

.tab-btn.active {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #ffffff;
  box-shadow: 0 10px 20px rgba(0, 102, 204, 0.18);
}

.tabs-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.tab-pane {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.profile-pane,
.feedback-pane {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(280px, 0.9fr);
  gap: var(--spacing-lg);
}

.content-card {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 24px;
  border-radius: 22px;
  background: #ffffff;
  border: 1px solid #e6edf5;
  box-shadow: 0 16px 30px rgba(15, 59, 105, 0.06);
}

.content-card--aside {
  background: linear-gradient(180deg, #fbfdff, #f7fbff);
}

.section-header h2 {
  margin: 10px 0 8px;
  font-size: 26px;
  color: var(--text-primary);
}

.section-header p {
  margin: 0;
  color: var(--text-secondary);
}

.section-kicker {
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
}

.profile-form,
.feedback-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-field--full {
  grid-column: 1 / -1;
}

.form-field span {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 700;
}

.form-field input,
.form-field textarea {
  padding: 14px 16px;
  border: 1px solid #dbe5ef;
  border-radius: 16px;
  background: #fbfcfe;
  color: var(--text-primary);
  font-size: 15px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.form-field input:focus,
.form-field textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(0, 102, 204, 0.08);
  background: #ffffff;
}

.form-field input:disabled {
  background: #f3f6f9;
  color: #6e8093;
}

.form-footer {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.form-hint {
  color: var(--text-light);
  font-size: 13px;
}

.primary-btn,
.primary-btn--link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 22px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #ffffff;
  font-weight: 700;
  box-shadow: 0 12px 22px rgba(0, 102, 204, 0.18);
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.primary-btn--link {
  width: fit-content;
  text-decoration: none;
}

.tip-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.tip-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 18px;
  border-radius: 18px;
  background: rgba(0, 102, 204, 0.04);
  border: 1px solid rgba(0, 102, 204, 0.08);
}

.tip-item strong,
.collection-card h3,
.history-card h3 {
  color: var(--text-primary);
}

.tip-item p,
.collection-card p,
.history-card p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.8;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 40px 16px;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.collection-grid {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.collection-card {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  gap: 20px;
  overflow: hidden;
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff, #fbfcfe);
  border: 1px solid #e6edf5;
  box-shadow: 0 14px 30px rgba(15, 59, 105, 0.05);
}

.collection-image {
  width: 100%;
  height: 100%;
  min-height: 220px;
  object-fit: cover;
  display: block;
}

.collection-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 22px 22px 22px 0;
}

.collection-tags,
.history-tags,
.collection-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.content-tag {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-size: 12px;
  font-weight: 700;
}

.content-tag--light {
  background: #f3f7fb;
  color: #60778e;
}

.collection-card h3,
.history-card h3 {
  margin: 0;
  font-size: 24px;
  line-height: 1.35;
}

.collection-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: auto;
}

.collection-meta {
  color: var(--text-light);
  font-size: 13px;
}

.meta-divider {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #c8d2dd;
}

.text-link {
  color: var(--primary-color);
  font-weight: 700;
  text-decoration: none;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-card {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr);
  gap: 18px;
  padding: 22px;
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff, #fbfcfe);
  border: 1px solid #e6edf5;
  box-shadow: 0 14px 30px rgba(15, 59, 105, 0.05);
}

.history-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  border-radius: 22px;
  font-size: 28px;
  font-weight: 800;
}

.history-icon.is-artwork {
  background: rgba(184, 71, 47, 0.12);
  color: #b8472f;
}

.history-icon.is-knowledge {
  background: rgba(0, 102, 204, 0.1);
  color: var(--primary-color);
}

.history-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 46px 24px;
  border-radius: 20px;
  background: linear-gradient(180deg, #fbfbfb, #f7f8fa);
  border: 1px dashed #dde4ea;
  text-align: center;
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58px;
  height: 58px;
  border-radius: 50%;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-size: 22px;
  font-weight: 800;
}

.empty-state h3,
.empty-state p {
  margin: 0;
}

.empty-state p {
  color: var(--text-secondary);
}

@media (max-width: 980px) {
  .hero-section,
  .profile-pane,
  .feedback-pane {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .user-center-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .hero-section,
  .content-card {
    padding: 20px;
    border-radius: 18px;
  }

  .hero-main {
    align-items: flex-start;
    flex-direction: column;
  }

  .hero-copy h1 {
    font-size: 32px;
  }

  .tabs-nav {
    gap: 10px;
  }

  .tab-btn {
    width: 100%;
  }

  .profile-form,
  .feedback-form,
  .collection-card,
  .history-card {
    grid-template-columns: 1fr;
  }

  .collection-body {
    padding: 0 18px 18px;
  }

  .collection-image {
    min-height: 200px;
  }

  .history-icon {
    width: 60px;
    height: 60px;
    border-radius: 18px;
  }
}
</style>
