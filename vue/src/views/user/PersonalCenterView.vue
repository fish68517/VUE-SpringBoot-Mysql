<template>
  <div class="personal-center-page">
    <el-result v-if="!userStore.isLoggedIn" icon="warning" title="请先登录" sub-title="登录后可以维护资料、收藏站点和提交反馈">
      <template #extra><router-link to="/login"><el-button type="primary">前往登录</el-button></router-link></template>
    </el-result>

    <template v-else>
      <section class="profile-banner">
        <div class="user-avatar">{{ userStore.displayName.slice(0, 1) }}</div>
        <div>
          <p class="eyebrow">PERSONAL CENTER</p>
          <h1>{{ userStore.displayName }}</h1>
          <p>@{{ userStore.user.username }} · {{ userStore.isAdmin ? '管理员账号' : '普通用户' }}</p>
        </div>
        <div class="profile-banner-stat"><strong>{{ favoriteTotal }}</strong><span>收藏站点</span></div>
        <div class="profile-banner-stat"><strong>{{ feedbackTotal }}</strong><span>反馈记录</span></div>
      </section>

      <section class="personal-center-card">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="个人资料" name="profile">
            <div class="profile-form-wrap" v-loading="profileLoading">
              <div class="panel-intro"><h2>个人资料</h2><p>维护用于页面展示和反馈联系的基本信息。</p></div>
              <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top">
                <div class="two-column-form">
                  <el-form-item label="用户名"><el-input :model-value="userStore.user.username" disabled /></el-form-item>
                  <el-form-item label="昵称" prop="nickname"><el-input v-model="profileForm.nickname" maxlength="50" /></el-form-item>
                  <el-form-item label="手机号" prop="phone"><el-input v-model="profileForm.phone" maxlength="20" clearable /></el-form-item>
                  <el-form-item label="邮箱" prop="email"><el-input v-model="profileForm.email" maxlength="100" clearable /></el-form-item>
                </div>
                <el-button type="primary" :loading="profileSaving" @click="saveProfile">保存个人资料</el-button>
              </el-form>
            </div>
          </el-tab-pane>

          <el-tab-pane label="我的收藏" name="favorites">
            <div class="panel-intro"><h2>我的收藏</h2><p>快速查看已经收藏的充电站和当前空闲数量。</p></div>
            <div v-loading="favoritesLoading" class="favorite-card-grid">
              <el-empty v-if="!favoritesLoading && favorites.length === 0" description="暂未收藏充电站" />
              <article v-for="favorite in favorites" :key="favorite.favoriteId" class="favorite-station-card">
                <div><span>{{ favorite.regionName }}</span><el-tag :type="favorite.status === 1 ? 'success' : 'warning'" size="small">{{ favorite.status === 1 ? '运营中' : '维护中' }}</el-tag></div>
                <h3>{{ favorite.stationName }}</h3>
                <p>{{ favorite.address }}</p>
                <div class="favorite-card-footer">
                  <span><strong>{{ favorite.freeCount }}</strong> 空闲 / {{ favorite.pileCount }} 总桩</span>
                  <div><router-link :to="`/stations/${favorite.stationId}`">查看</router-link><button type="button" @click="cancelFavorite(favorite)">取消收藏</button></div>
                </div>
              </article>
            </div>
          </el-tab-pane>

          <el-tab-pane label="意见反馈" name="feedback">
            <div class="feedback-layout">
              <div class="feedback-form-panel">
                <div class="panel-intro"><h2>提交新反馈</h2><p>可以提交建议、站点信息报错或使用投诉。</p></div>
                <el-form ref="feedbackFormRef" :model="feedbackForm" :rules="feedbackRules" label-position="top">
                  <el-form-item label="反馈类型" prop="feedbackType">
                    <el-radio-group v-model="feedbackForm.feedbackType">
                      <el-radio-button :value="0">建议</el-radio-button>
                      <el-radio-button :value="1">报错</el-radio-button>
                      <el-radio-button :value="2">投诉</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="反馈标题" prop="title"><el-input v-model="feedbackForm.title" maxlength="150" show-word-limit /></el-form-item>
                  <el-form-item label="详细内容" prop="content"><el-input v-model="feedbackForm.content" type="textarea" :rows="5" maxlength="2000" show-word-limit /></el-form-item>
                  <el-form-item label="联系方式"><el-input v-model="feedbackForm.contact" maxlength="100" clearable /></el-form-item>
                  <el-button type="primary" :loading="feedbackSubmitting" @click="sendFeedback">提交反馈</el-button>
                </el-form>
              </div>

              <div class="feedback-history-panel">
                <div class="panel-intro"><h2>处理记录</h2><p>管理员回复后会显示在对应记录中。</p></div>
                <div v-loading="feedbackLoading" class="feedback-list">
                  <el-empty v-if="!feedbackLoading && feedbacks.length === 0" description="暂无反馈记录" />
                  <article v-for="item in feedbacks" :key="item.id" class="feedback-item">
                    <div><span>{{ feedbackTypeText(item.feedbackType) }}</span><el-tag :type="feedbackStatusType(item.processStatus)" size="small">{{ feedbackStatusText(item.processStatus) }}</el-tag></div>
                    <h3>{{ item.title }}</h3>
                    <p>{{ item.content }}</p>
                    <small>{{ item.createTime }}</small>
                    <div v-if="item.replyContent" class="feedback-reply"><strong>管理员回复</strong><p>{{ item.replyContent }}</p></div>
                  </article>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </section>
    </template>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'
import {
  getProfile,
  updateProfile,
  getFavorites,
  removeFavorite,
  getFeedbacks,
  submitFeedback
} from '../../api/user'

const userStore = useUserStore()
const activeTab = ref('profile')
const profileFormRef = ref()
const feedbackFormRef = ref()
const profileLoading = ref(false)
const profileSaving = ref(false)
const favoritesLoading = ref(false)
const feedbackLoading = ref(false)
const feedbackSubmitting = ref(false)
const favorites = ref([])
const feedbacks = ref([])
const favoriteTotal = ref(0)
const feedbackTotal = ref(0)

const profileForm = reactive({ nickname: '', phone: '', email: '' })
const feedbackForm = reactive({ feedbackType: 0, title: '', content: '', contact: '' })

const profileRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

const feedbackRules = {
  feedbackType: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入反馈标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
}

const loadProfile = async () => {
  if (!userStore.user?.id) return
  profileLoading.value = true
  try {
    const data = await getProfile(userStore.user.id)
    Object.assign(profileForm, { nickname: data.nickname || '', phone: data.phone || '', email: data.email || '' })
    userStore.saveUser(data)
  } catch (error) {
    ElMessage.error(error.message || '个人资料加载失败')
  } finally {
    profileLoading.value = false
  }
}

const saveProfile = async () => {
  try {
    await profileFormRef.value.validate()
    profileSaving.value = true
    const user = await updateProfile(userStore.user.id, profileForm)
    userStore.saveUser(user)
    ElMessage.success('个人资料已保存')
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  } finally {
    profileSaving.value = false
  }
}

const loadFavorites = async () => {
  if (!userStore.user?.id) return
  favoritesLoading.value = true
  try {
    const data = await getFavorites({ userId: userStore.user.id, pageNum: 1, pageSize: 100 })
    favorites.value = data.records || []
    favoriteTotal.value = data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '收藏列表加载失败')
  } finally {
    favoritesLoading.value = false
  }
}

const cancelFavorite = async favorite => {
  try {
    await ElMessageBox.confirm(`确定取消收藏“${favorite.stationName}”吗？`, '取消收藏', { type: 'warning' })
    await removeFavorite(favorite.favoriteId)
    ElMessage.success('已取消收藏')
    await loadFavorites()
  } catch (error) {
    if (error instanceof Error && error.message !== 'cancel') ElMessage.error(error.message)
  }
}

const loadFeedbacks = async () => {
  if (!userStore.user?.id) return
  feedbackLoading.value = true
  try {
    const data = await getFeedbacks({ userId: userStore.user.id, pageNum: 1, pageSize: 100 })
    feedbacks.value = data.records || []
    feedbackTotal.value = data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '反馈记录加载失败')
  } finally {
    feedbackLoading.value = false
  }
}

const sendFeedback = async () => {
  try {
    await feedbackFormRef.value.validate()
    feedbackSubmitting.value = true
    await submitFeedback({ ...feedbackForm, userId: userStore.user.id })
    Object.assign(feedbackForm, { feedbackType: 0, title: '', content: '', contact: profileForm.phone || profileForm.email || '' })
    feedbackFormRef.value.resetFields()
    ElMessage.success('反馈提交成功')
    await loadFeedbacks()
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  } finally {
    feedbackSubmitting.value = false
  }
}

const handleTabChange = name => {
  if (name === 'favorites') loadFavorites()
  if (name === 'feedback') loadFeedbacks()
}

const feedbackTypeText = value => ({ 0: '建议', 1: '报错', 2: '投诉' }[value] || '反馈')
const feedbackStatusText = value => ({ 0: '待处理', 1: '处理中', 2: '已完成' }[value] || '未知')
const feedbackStatusType = value => ({ 0: 'warning', 1: 'primary', 2: 'success' }[value] || 'info')

onMounted(async () => {
  if (!userStore.isLoggedIn) return
  await Promise.all([loadProfile(), loadFavorites(), loadFeedbacks()])
  feedbackForm.contact = profileForm.phone || profileForm.email || ''
})
</script>
