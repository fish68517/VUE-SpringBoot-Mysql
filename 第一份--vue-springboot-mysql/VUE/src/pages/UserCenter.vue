<template>
  <div class="user-center-container">
    <div class="user-center-wrapper">
      <!-- Sidebar Navigation -->
      <div class="sidebar">
        <div class="user-profile-card">
          <div class="avatar">{{ userInitial }}</div>
          <h3>{{ currentUser?.username }}</h3>
          <p>{{ currentUser?.email }}</p>
        </div>

        <nav class="sidebar-nav">
          <button
            v-for="item in navItems"
            :key="item.id"
            :class="['nav-item', { active: activeTab === item.id }]"
            @click="handleNavClick(item)"
          >
            {{ item.label }}
          </button>
        </nav>
      </div>

      <!-- Main Content -->
      <div class="main-content">
        <!-- Account Information Tab -->
        <div v-if="activeTab === 'account'" class="tab-content">
          <h2>账号信息</h2>
          <div class="info-section">
            <div class="info-item">
              <label>用户名</label>
              <p>{{ currentUser?.username }}</p>
            </div>
            <div class="info-item">
              <label>邮箱</label>
              <p>{{ currentUser?.email }}</p>
              <p class="info-hint">邮箱用于账号恢复和通知</p>
            </div>
            <div class="info-item">
              <label>注册时间</label>
              <p>{{ formatDate(currentUser?.createdAt) }}</p>
            </div>
            <div class="info-item">
              <label>账号状态</label>
              <p :class="['status', currentUser?.status]">
                {{ currentUser?.status === 'ACTIVE' ? '正常' : '已禁用' }}
              </p>
            </div>
          </div>
        </div>

        <!-- Edit Profile Tab -->
        <div v-if="activeTab === 'profile'" class="tab-content">
          <h2>编辑资料</h2>
          <form @submit.prevent="handleUpdateProfile" class="edit-form">
            <div class="form-group">
              <label for="nickname">昵称</label>
              <input
                id="nickname"
                v-model="editForm.nickname"
                type="text"
                placeholder="请输入昵称"
                @blur="validateNickname"
              />
              <span v-if="editErrors.nickname" class="error-message">{{ editErrors.nickname }}</span>
            </div>

            <div class="form-group">
              <label for="bio">个人简介</label>
              <textarea
                id="bio"
                v-model="editForm.bio"
                placeholder="请输入个人简介（可选）"
                rows="4"
              ></textarea>
            </div>

            <button type="submit" class="submit-btn" :disabled="editLoading">
              <span v-if="!editLoading">保存修改</span>
              <span v-else>保存中...</span>
            </button>
          </form>

          <div class="works-entry-card">
            <h3>我的作品</h3>
            <p>查看自己上传的作品列表，并点击作品查看详情。</p>
            <button type="button" class="secondary-btn" @click="router.push('/user-center/my-works')">
              进入我的作品
            </button>
          </div>
        </div>

        <!-- Change Password Tab -->
        <div v-if="activeTab === 'password'" class="tab-content">
          <h2>修改密码</h2>
          <form @submit.prevent="handleChangePassword" class="edit-form">
            <div class="form-group">
              <label for="oldPassword">旧密码</label>
              <input
                id="oldPassword"
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入旧密码"
                @blur="validateOldPassword"
              />
              <span v-if="passwordErrors.oldPassword" class="error-message">
                {{ passwordErrors.oldPassword }}
              </span>
            </div>

            <div class="form-group">
              <label for="newPassword">新密码</label>
              <input
                id="newPassword"
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（至少6位）"
                @blur="validateNewPassword"
              />
              <span v-if="passwordErrors.newPassword" class="error-message">
                {{ passwordErrors.newPassword }}
              </span>
            </div>

            <div class="form-group">
              <label for="confirmPassword">确认新密码</label>
              <input
                id="confirmPassword"
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                @blur="validateConfirmPassword"
              />
              <span v-if="passwordErrors.confirmPassword" class="error-message">
                {{ passwordErrors.confirmPassword }}
              </span>
            </div>

            <button type="submit" class="submit-btn" :disabled="passwordLoading">
              <span v-if="!passwordLoading">修改密码</span>
              <span v-else>修改中...</span>
            </button>
          </form>
        </div>

        <!-- Collections Tab -->
        <div v-if="activeTab === 'collections'" class="tab-content">
          <h2>我的收藏</h2>
          <div v-if="collectionsLoading" class="loading">加载中...</div>
          <div v-else-if="collections.length === 0" class="empty-state">
            <p>还没有收藏任何纹样</p>
            <router-link to="/patterns" class="link-btn">去浏览纹样</router-link>
          </div>
          <div v-else class="collections-grid">
            <div v-for="collection in collections" :key="collection.id" class="collection-card">
              <div class="card-image">
                <img :src="collection.imageUrl" :alt="collection.name" />
              </div>
              <div class="card-content">
                <h4>{{ collection.name }}</h4>
                <p class="category">{{ collection.category }}</p>
                <div class="card-actions">
                  <router-link :to="`/patterns/${collection.id}`" class="view-btn">查看详情</router-link>
                  <button @click="handleRemoveCollection(collection.id)" class="remove-btn">
                    取消收藏
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Operation History Tab -->
        <div v-if="activeTab === 'history'" class="tab-content">
          <h2>操作历史</h2>
          <div v-if="historyLoading" class="loading">加载中...</div>
          <div v-else-if="history.length === 0" class="empty-state">
            <p>暂无操作历史</p>
          </div>
          <div v-else class="history-list">
            <div v-for="record in history" :key="record.id" class="history-item">
              <div class="history-icon" :class="record.operationType">
                {{ getOperationIcon(record.operationType) }}
              </div>
              <div class="history-content">
                <p class="operation">{{ getOperationLabel(record.operationType) }}</p>
                <p class="details">{{ record.details }}</p>
              </div>
              <div class="history-time">{{ formatDate(record.createdAt) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userAPI, collectionAPI } from '../services/api'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()

// State
const activeTab = ref('account')
const currentUser = ref(null)
const collections = ref([])
const history = ref([])

const collectionsLoading = ref(false)
const historyLoading = ref(false)
const editLoading = ref(false)
const passwordLoading = ref(false)

const editForm = reactive({
  nickname: '',
  bio: ''
})

const editErrors = reactive({
  nickname: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordErrors = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const navItems = [
  { id: 'account', label: '账号信息' },
  { id: 'profile', label: '编辑资料' },
  // { id: 'password', label: '修改密码' }
  // { id: 'collections', label: '我的收藏' }
  // { id: 'history', label: '操作历史' }
]

navItems.splice(2, 0, { id: 'my-works', label: '我的作品', route: '/user-center/my-works' })

// Computed
const userInitial = computed(() => {
  return currentUser.value?.username?.charAt(0).toUpperCase() || 'U'
})

const handleNavClick = (item) => {
  if (item.route) {
    router.push(item.route)
    return
  }

  activeTab.value = item.id
}

// Methods
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getOperationIcon = (type) => {
  const icons = {
    view: '👁️',
    search: '🔍',
    download: '⬇️',
    collection: '⭐',
    comment: '💬',
    question: '❓'
  }
  return icons[type] || '📝'
}

const getOperationLabel = (type) => {
  const labels = {
    view: '浏览纹样',
    search: '搜索纹样',
    download: '下载素材',
    collection: '收藏纹样',
    comment: '发布评论',
    question: '发布提问'
  }
  return labels[type] || '操作'
}

// Load user data
const loadUserData = async () => {
  try {
    const userId = userStore.user?.id
    if (!userId) {
      ElMessage.warning('此功能需要登录才能访问')
      router.push('/home')
      return
    }

    const response = await userAPI.getUser(userId)
    if (response.code === 200) {
      currentUser.value = response.data
      editForm.nickname = response.data.nickname || ''
      editForm.bio = response.data.bio || ''
    }
  } catch (error) {
    console.error('Failed to load user data:', error)
    ElMessage.error('加载用户信息失败')
  }
}

// Load collections
const loadCollections = async () => {
  collectionsLoading.value = true
  try {
    const userId = userStore.user?.id
    const response = await userAPI.getCollections(userId)
    if (response.code === 200) {
      collections.value = response.data || []
    }
  } catch (error) {
    console.error('Failed to load collections:', error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    collectionsLoading.value = false
  }
}

// Load operation history
const loadHistory = async () => {
  historyLoading.value = true
  try {
    const userId = userStore.user?.id
    const response = await userAPI.getHistory(userId, { page: 0, size: 20 })
    if (response.code === 200) {
      // Handle both paginated and direct array responses
      if (response.data && response.data.content) {
        history.value = response.data.content
      } else if (Array.isArray(response.data)) {
        history.value = response.data
      } else {
        history.value = []
      }
    }
  } catch (error) {
    console.error('Failed to load history:', error)
    ElMessage.error('加载操作历史失败')
  } finally {
    historyLoading.value = false
  }
}

// Validation functions
const validateNickname = () => {
  editErrors.nickname = ''
  if (editForm.nickname && editForm.nickname.length > 50) {
    editErrors.nickname = '昵称最多50个字符'
  }
}

const validateOldPassword = () => {
  passwordErrors.oldPassword = ''
  if (!passwordForm.oldPassword) {
    passwordErrors.oldPassword = '旧密码不能为空'
  }
}

const validateNewPassword = () => {
  passwordErrors.newPassword = ''
  if (!passwordForm.newPassword) {
    passwordErrors.newPassword = '新密码不能为空'
  } else if (passwordForm.newPassword.length < 6) {
    passwordErrors.newPassword = '新密码至少6个字符'
  }
}

const validateConfirmPassword = () => {
  passwordErrors.confirmPassword = ''
  if (!passwordForm.confirmPassword) {
    passwordErrors.confirmPassword = '请确认新密码'
  } else if (passwordForm.confirmPassword !== passwordForm.newPassword) {
    passwordErrors.confirmPassword = '两次输入的密码不一致'
  }
}

const validatePasswordForm = () => {
  validateOldPassword()
  validateNewPassword()
  validateConfirmPassword()
  return !passwordErrors.oldPassword && !passwordErrors.newPassword && !passwordErrors.confirmPassword
}

// Handle update profile
const handleUpdateProfile = async () => {
  validateNickname()
  if (editErrors.nickname) {
    ElMessage.error('请修正表单错误')
    return
  }

  editLoading.value = true
  try {
    const userId = userStore.user?.id
    const response = await userAPI.updateUser(userId, {
      nickname: editForm.nickname,
      bio: editForm.bio
    })

    if (response.code === 200) {
      currentUser.value = response.data
      userStore.updateUser(response.data)
      ElMessage.success('资料更新成功')
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('Failed to update profile:', error)
    ElMessage.error('更新资料失败')
  } finally {
    editLoading.value = false
  }
}

// Handle change password
const handleChangePassword = async () => {
  if (!validatePasswordForm()) {
    ElMessage.error('请修正表单错误')
    return
  }

  passwordLoading.value = true
  try {
    const userId = userStore.user?.id
    const response = await userAPI.changePassword(userId, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    if (response.code === 200) {
      ElMessage.success('密码修改成功')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      ElMessage.error(response.message || '修改失败')
    }
  } catch (error) {
    console.error('Failed to change password:', error)
    const errorMessage = error.response?.data?.message || '修改密码失败'
    ElMessage.error(errorMessage)
  } finally {
    passwordLoading.value = false
  }
}

// Handle remove collection
const handleRemoveCollection = async (collectionId) => {
  try {
    const response = await collectionAPI.deleteCollection(collectionId)
    if (response.code === 200) {
      collections.value = collections.value.filter(c => c.id !== collectionId)
      ElMessage.success('已取消收藏')
    } else {
      ElMessage.error(response.message || '取消收藏失败')
    }
  } catch (error) {
    console.error('Failed to remove collection:', error)
    ElMessage.error('取消收藏失败')
  }
}

// Watch active tab to load data
const handleTabChange = () => {
  if (activeTab.value === 'collections' && collections.value.length === 0) {
    loadCollections()
  } else if (activeTab.value === 'history' && history.value.length === 0) {
    loadHistory()
  }
}

// Lifecycle
onMounted(() => {
  loadUserData()
})

watch(activeTab, handleTabChange)
</script>

<style scoped>
.user-center-container {
  min-height: calc(100vh - 80px);
  background: #f5f7fa;
  padding: 2rem 1rem;
}

.user-center-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 2rem;
}

/* Sidebar */
.sidebar {
  background: white;
  border-radius: 12px;
  padding: 2rem 1.5rem;
  height: fit-content;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.user-profile-card {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #eee;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  margin: 0 auto 1rem;
}

.user-profile-card h3 {
  margin: 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
}

.user-profile-card p {
  margin: 0.25rem 0;
  color: #999;
  font-size: 0.9rem;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.nav-item {
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  border-left: 3px solid transparent;
  color: #666;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.95rem;
}

.nav-item:hover {
  background: #f5f7fa;
  color: #333;
}

.nav-item.active {
  background: #f0f2ff;
  border-left-color: #667eea;
  color: #667eea;
  font-weight: 600;
}

/* Main Content */
.main-content {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tab-content h2 {
  margin: 0 0 1.5rem 0;
  color: #333;
  font-size: 1.5rem;
  border-bottom: 2px solid #f0f2ff;
  padding-bottom: 1rem;
}

/* Info Section */
.info-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.info-item {
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
}

.info-item label {
  display: block;
  color: #999;
  font-size: 0.85rem;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-item p {
  margin: 0;
  color: #333;
  font-size: 1rem;
}

.info-hint {
  color: #999;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.status {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.status.active {
  background: #d4edda;
  color: #155724;
}

.status.disabled {
  background: #f8d7da;
  color: #721c24;
}

/* Edit Form */
.edit-form {
  max-width: 500px;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
  font-size: 0.95rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.95rem;
  font-family: inherit;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.error-message {
  display: block;
  color: #ff6b6b;
  font-size: 0.85rem;
  margin-top: 0.3rem;
}

.submit-btn {
  padding: 0.75rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.works-entry-card {
  margin-top: 1.5rem;
  max-width: 500px;
  padding: 1.25rem;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #f9fafb;
}

.works-entry-card h3 {
  margin: 0 0 0.5rem;
  color: #333;
  font-size: 1.05rem;
}

.works-entry-card p {
  margin: 0 0 1rem;
  color: #666;
  line-height: 1.6;
}

.secondary-btn {
  padding: 0.65rem 1.2rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background 0.2s ease;
}

.secondary-btn:hover {
  background: #5a67d8;
}

/* Collections Grid */
.collections-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.collection-card {
  background: #f9fafb;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.collection-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-image {
  width: 100%;
  height: 150px;
  overflow: hidden;
  background: #eee;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-content {
  padding: 1rem;
}

.card-content h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 0.95rem;
}

.category {
  margin: 0 0 1rem 0;
  color: #999;
  font-size: 0.85rem;
}

.card-actions {
  display: flex;
  gap: 0.5rem;
}

.view-btn,
.remove-btn {
  flex: 1;
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s;
}

.view-btn {
  background: #667eea;
  color: white;
  text-decoration: none;
  text-align: center;
}

.view-btn:hover {
  background: #764ba2;
}

.remove-btn {
  background: #f0f2ff;
  color: #667eea;
}

.remove-btn:hover {
  background: #e0e3ff;
}

/* History List */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.history-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
  align-items: flex-start;
}

.history-icon {
  font-size: 1.5rem;
  min-width: 40px;
  text-align: center;
}

.history-content {
  flex: 1;
}

.operation {
  margin: 0 0 0.25rem 0;
  color: #333;
  font-weight: 600;
  font-size: 0.95rem;
}

.details {
  margin: 0;
  color: #999;
  font-size: 0.85rem;
}

.history-time {
  color: #999;
  font-size: 0.85rem;
  white-space: nowrap;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: #999;
}

.empty-state p {
  margin: 0 0 1rem 0;
  font-size: 1rem;
}

.link-btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  transition: background 0.3s;
}

.link-btn:hover {
  background: #764ba2;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #999;
}

/* Responsive */
@media (max-width: 1199px) {
  .user-center-wrapper {
    grid-template-columns: 220px 1fr;
    gap: 1.5rem;
  }

  .sidebar {
    padding: 1.5rem;
  }

  .main-content {
    padding: 1.5rem;
  }

  .collections-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }

  .info-section {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
}

@media (max-width: 991px) {
  .user-center-container {
    padding: 1.5rem 1rem;
  }

  .user-center-wrapper {
    grid-template-columns: 200px 1fr;
    gap: 1.25rem;
  }

  .sidebar {
    padding: 1.25rem;
  }

  .avatar {
    width: 70px;
    height: 70px;
    font-size: 1.8rem;
  }

  .user-profile-card h3 {
    font-size: 1rem;
  }

  .user-profile-card p {
    font-size: 0.85rem;
  }

  .nav-item {
    padding: 0.65rem 0.9rem;
    font-size: 0.9rem;
  }

  .main-content {
    padding: 1.25rem;
  }

  .tab-content h2 {
    font-size: 1.3rem;
    margin-bottom: 1.25rem;
  }

  .info-section {
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 1.25rem;
  }

  .info-item {
    padding: 0.9rem;
  }

  .collections-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 1.25rem;
  }

  .card-image {
    height: 130px;
  }

  .history-item {
    gap: 0.75rem;
    padding: 0.9rem;
  }

  .history-icon {
    font-size: 1.3rem;
  }
}

@media (max-width: 767px) {
  .user-center-container {
    padding: 1rem;
  }

  .user-center-wrapper {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .sidebar {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
    padding: 1rem;
  }

  .user-profile-card {
    grid-column: 1 / -1;
    margin-bottom: 0;
    padding-bottom: 0.75rem;
  }

  .avatar {
    width: 60px;
    height: 60px;
    font-size: 1.5rem;
    margin: 0 auto 0.75rem;
  }

  .user-profile-card h3 {
    font-size: 0.95rem;
    margin: 0.4rem 0;
  }

  .user-profile-card p {
    font-size: 0.8rem;
    margin: 0.2rem 0;
  }

  .sidebar-nav {
    grid-column: 1 / -1;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 0.4rem;
  }

  .nav-item {
    flex: 1;
    min-width: 100px;
    border-left: none;
    border-bottom: 3px solid transparent;
    padding: 0.5rem 0.6rem;
    font-size: 0.8rem;
  }

  .nav-item.active {
    border-left: none;
    border-bottom-color: #667eea;
  }

  .main-content {
    padding: 1rem;
  }

  .tab-content h2 {
    font-size: 1.2rem;
    margin-bottom: 1rem;
  }

  .info-section {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .info-item {
    padding: 0.75rem;
  }

  .info-item label {
    font-size: 0.8rem;
    margin-bottom: 0.4rem;
  }

  .info-item p {
    font-size: 0.95rem;
  }

  .edit-form {
    max-width: 100%;
  }

  .form-group {
    margin-bottom: 1.25rem;
  }

  .form-group label {
    font-size: 0.9rem;
    margin-bottom: 0.4rem;
  }

  .form-group input,
  .form-group textarea {
    padding: 0.65rem;
    font-size: 0.9rem;
  }

  .submit-btn {
    padding: 0.65rem 1.5rem;
    font-size: 0.95rem;
  }

  .collections-grid {
    grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
    gap: 1rem;
  }

  .card-image {
    height: 110px;
  }

  .card-content {
    padding: 0.75rem;
  }

  .card-content h4 {
    font-size: 0.9rem;
    margin: 0 0 0.4rem 0;
  }

  .category {
    font-size: 0.8rem;
    margin: 0 0 0.75rem 0;
  }

  .view-btn,
  .remove-btn {
    padding: 0.4rem;
    font-size: 0.8rem;
  }

  .history-list {
    gap: 0.75rem;
  }

  .history-item {
    gap: 0.6rem;
    padding: 0.75rem;
  }

  .history-icon {
    font-size: 1.2rem;
    min-width: 35px;
  }

  .operation {
    font-size: 0.9rem;
    margin: 0 0 0.2rem 0;
  }

  .details {
    font-size: 0.8rem;
  }

  .history-time {
    font-size: 0.8rem;
  }

  .empty-state {
    padding: 2rem 1rem;
  }

  .empty-state p {
    font-size: 0.95rem;
  }

  .link-btn {
    padding: 0.6rem 1.25rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 479px) {
  .user-center-container {
    padding: 0.75rem;
  }

  .user-center-wrapper {
    gap: 0.75rem;
  }

  .sidebar {
    grid-template-columns: 1fr;
    gap: 0.75rem;
    padding: 0.75rem;
  }

  .avatar {
    width: 50px;
    height: 50px;
    font-size: 1.2rem;
    margin: 0 auto 0.5rem;
  }

  .user-profile-card h3 {
    font-size: 0.9rem;
    margin: 0.3rem 0;
  }

  .user-profile-card p {
    font-size: 0.75rem;
    margin: 0.15rem 0;
  }

  .sidebar-nav {
    gap: 0.3rem;
  }

  .nav-item {
    min-width: 80px;
    padding: 0.4rem 0.5rem;
    font-size: 0.75rem;
  }

  .main-content {
    padding: 0.75rem;
  }

  .tab-content h2 {
    font-size: 1.1rem;
    margin-bottom: 0.75rem;
  }

  .info-section {
    gap: 0.75rem;
  }

  .info-item {
    padding: 0.6rem;
  }

  .info-item label {
    font-size: 0.75rem;
    margin-bottom: 0.3rem;
  }

  .info-item p {
    font-size: 0.9rem;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .form-group label {
    font-size: 0.85rem;
    margin-bottom: 0.3rem;
  }

  .form-group input,
  .form-group textarea {
    padding: 0.55rem;
    font-size: 16px;
  }

  .error-message {
    font-size: 0.8rem;
  }

  .submit-btn {
    padding: 0.55rem 1.25rem;
    font-size: 0.9rem;
  }

  .collections-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 0.75rem;
  }

  .card-image {
    height: 90px;
  }

  .card-content {
    padding: 0.6rem;
  }

  .card-content h4 {
    font-size: 0.8rem;
    margin: 0 0 0.3rem 0;
  }

  .category {
    font-size: 0.75rem;
    margin: 0 0 0.5rem 0;
  }

  .card-actions {
    gap: 0.4rem;
  }

  .view-btn,
  .remove-btn {
    padding: 0.35rem;
    font-size: 0.75rem;
  }

  .history-list {
    gap: 0.6rem;
  }

  .history-item {
    gap: 0.5rem;
    padding: 0.6rem;
  }

  .history-icon {
    font-size: 1rem;
    min-width: 30px;
  }

  .operation {
    font-size: 0.85rem;
    margin: 0 0 0.15rem 0;
  }

  .details {
    font-size: 0.75rem;
  }

  .history-time {
    font-size: 0.75rem;
  }

  .empty-state {
    padding: 1.5rem 0.75rem;
  }

  .empty-state p {
    font-size: 0.9rem;
  }

  .link-btn {
    padding: 0.5rem 1rem;
    font-size: 0.85rem;
  }

  .loading {
    padding: 1.5rem;
    font-size: 0.9rem;
  }
}
</style>
