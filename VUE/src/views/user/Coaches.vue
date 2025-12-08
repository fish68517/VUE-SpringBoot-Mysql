<template>
  <div class="coaches-container">
    <div class="page-header">
      <h1>找教练</h1>
      <p class="subtitle">发现专业的健身教练，为您量身定制训练计划</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索教练姓名..."
        :prefix-icon="Search"
        clearable
        class="search-input"
        @input="handleSearch"
      />
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated count="4" class="skeleton-grid" />
    </div>

    <!-- 教练列表 -->
    <div v-else-if="filteredCoaches.length > 0" class="coach-grid">
      <el-card
        v-for="coach in filteredCoaches"
        :key="coach.id"
        class="coach-card"
        :class="{ 'is-member': isActiveMember(coach) }"
        shadow="hover"
      >
        <!-- 状态角标 -->
        <div v-if="getRelationStatus(coach) !== 'none'" class="status-badge">
          <el-tag :type="getRelationStatus(coach) === 'active' ? 'success' : 'danger'" effect="dark">
            {{ getRelationStatus(coach) === 'active' ? '签约会员' : '合约已过期' }}
          </el-tag>
        </div>

        <div class="coach-card-body" @click="showCoachDetails(coach)">
          <div class="coach-avatar-wrapper">
            <el-avatar :size="80" :src="coach.avatar || '/default-avatar.png'" class="coach-avatar">
              {{ coach.username?.charAt(0).toUpperCase() }}
            </el-avatar>
          </div>
          <div class="coach-info">
            <h3 class="coach-name">{{ coach.username }}</h3>
            
            <!-- 会员有效期显示 -->
            <p v-if="isActiveMember(coach)" class="expire-info active">
              <el-icon><Timer /></el-icon> 有效期至：{{ formatDate(coach.relationship?.expireAt) }}
            </p>
            <p v-else-if="getRelationStatus(coach) === 'expired'" class="expire-info expired">
              <el-icon><Warning /></el-icon> 已于 {{ formatDate(coach.relationship?.expireAt) }} 过期
            </p>
            <p v-else class="coach-intro">{{ truncateText(coach.intro || '暂无简介', 40) }}</p>

            <div class="coach-tags">
              <el-tag size="small" type="info" effect="plain" v-if="coach.gender">
                {{ formatGender(coach.gender) }}
              </el-tag>
              <el-tag size="small" type="warning" effect="plain" v-if="coach.specialties">
                {{ coach.specialties.split(',')[0] }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 底部操作按钮组 -->
        <div class="card-footer">
          <!-- 情况1: 有效会员 -> 制定计划 -->
          <el-button 
            v-if="isActiveMember(coach)" 
            type="primary" 
            size="small" 
            class="action-btn"
            @click.stop="goToTrainingPlan(coach)"
          >
            <el-icon><Calendar /></el-icon> 制定计划
          </el-button>

          <!-- 情况2: 过期或未签约 -> 续约/签约 -->
          <el-button 
            v-else
            type="warning" 
            plain 
            size="small" 
            class="action-btn"
            @click.stop="handleRenew(coach)"
          >
            <el-icon><Money /></el-icon> {{ getRelationStatus(coach) === 'expired' ? '立即续约' : '办理会员' }}
          </el-button>

          <el-button @click.stop="showCoachDetails(coach)" size="small" plain>详情</el-button>
        </div>
      </el-card>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="暂无符合条件的教练" />
    </div>

    <!-- 教练详情弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedCoach?.username + ' - 教练详情'"
      width="500px"
      destroy-on-close
      center
      class="coach-dialog"
    >
      <div v-if="selectedCoach" class="coach-detail">
        <div class="detail-header">
          <el-avatar :size="100" :src="selectedCoach.avatar || '/default-avatar.png'">
            {{ selectedCoach.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <h2 class="detail-name">
            {{ selectedCoach.username }}
            <el-tag v-if="isActiveMember(selectedCoach)" type="success" size="small" effect="dark">会员中</el-tag>
          </h2>
        </div>

        <!-- 会员状态专区 -->
        <div class="detail-section highlight-section" v-if="getRelationStatus(selectedCoach) !== 'none'">
          <h3><el-icon><UserFilled /></el-icon> 会员状态</h3>
          <div class="membership-info">
            <div class="info-row">
              <span class="label">当前状态：</span>
              <span :class="['value', getRelationStatus(selectedCoach)]">
                {{ getRelationStatus(selectedCoach) === 'active' ? '正常服务中' : '服务已暂停 (已过期)' }}
              </span>
            </div>
            <div class="info-row">
              <span class="label">签约时间：</span>
              <span class="value">{{ formatDate(selectedCoach.relationship?.createdAt) }}</span>
            </div>
            <div class="info-row">
              <span class="label">到期时间：</span>
              <span class="value">{{ formatDate(selectedCoach.relationship?.expireAt) }}</span>
            </div>
            <el-button type="primary" style="width: 100%; margin-top: 10px;" @click="handleRenew(selectedCoach)">
              {{ getRelationStatus(selectedCoach) === 'active' ? '延长有效期' : '重新激活会员' }}
            </el-button>
          </div>
        </div>

        <div class="detail-section">
          <h3>个人简介</h3>
          <p class="detail-intro">{{ selectedCoach.intro || '这位教练很神秘，还没有写简介。' }}</p>
        </div>

        <div class="detail-section" v-if="selectedCoach.specialties">
          <h3>擅长领域</h3>
          <div class="specialties-list">
            <el-tag 
              v-for="(tag, index) in (selectedCoach.specialties ? selectedCoach.specialties.split(',') : [])" 
              :key="index" 
              class="specialty-tag"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 续约/支付确认弹窗 (模拟) -->
    <el-dialog
      v-model="renewDialogVisible"
      title="办理会员续约"
      width="400px"
      center
    >
      <div v-if="targetRenewCoach" class="renew-content">
        <p>您正在与教练 <strong>{{ targetRenewCoach.username }}</strong> 办理续约。</p>
        <div class="price-options">
          <el-radio-group v-model="renewDuration">
            <el-radio-button label="1">1个月 / ￥300</el-radio-button>
            <el-radio-button label="3">3个月 / ￥800</el-radio-button>
            <el-radio-button label="12">12个月 / ￥3000</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="renewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRenew" :loading="renewing">确认支付</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Timer, Warning, Calendar, Money, UserFilled } from '@element-plus/icons-vue'
import Layout from '@/components/common/Layout.vue'
import { getCoaches, getCoachById, getCoachStudentsById, renewCoachStudent } from '@/api/coach.js'
import { getUserInfo } from '@/utils/auth.js'
import { showSuccess, showError, showInfo } from '@/utils/feedback.js'

const router = useRouter()
const coaches = ref([])
const loading = ref(false)
const searchQuery = ref('')
const dialogVisible = ref(false)
const selectedCoach = ref(null)

// 续约相关状态
const renewDialogVisible = ref(false)
const targetRenewCoach = ref(null)
const renewDuration = ref('1')
const renewing = ref(false)

// 过滤教练列表
const filteredCoaches = computed(() => {
  if (!searchQuery.value) return coaches.value
  const query = searchQuery.value.toLowerCase()
  return coaches.value.filter(coach => 
    coach.username.toLowerCase().includes(query) ||
    (coach.intro && coach.intro.toLowerCase().includes(query))
  )
})

// 获取所有教练
const fetchCoaches = async () => {
  loading.value = true
  try {
    const response = await getCoaches()
    let rawCoaches = Array.isArray(response) ? response : (response.content || [])
    
    const userInfo = getUserInfo()
    const userId = userInfo ? userInfo.userId : null

    console.log("当前用户ID:", userId)

    if (userId) {
      // 并行获取所有教练与当前用户的关系
      // 使用 Promise.all 确保所有状态加载完毕后再渲染列表，避免UI闪烁
      const coachesWithRelation = await Promise.all(rawCoaches.map(async (coach) => {
        try {
          const relation = await getCoachStudentsById(coach.id, userId)
          console.log(`教练 ${coach.id} 的关系数据:`, relation== null ? '无关系' : relation)
          return {
            ...coach,
            relationship: relation || null // 如果返回 undefined/null 则设为 null
          }
        } catch (err) {
          console.error(`获取教练 ${coach.id} 关系失败:`, err)
          return { ...coach, relationship: null }
        }
      }))
      coaches.value = coachesWithRelation
    } else {
      // 未登录或无ID，显示普通列表
      coaches.value = rawCoaches.map(c => ({ ...c, relationship: null }))
    }
    
  } catch (error) {
    console.error('获取教练列表失败:', error)
    showError('无法加载教练列表')
  } finally {
    loading.value = false
  }
}

// 辅助函数：判断关系状态
// 返回值: 'active' (会员中), 'expired' (已过期), 'none' (无关系)
const getRelationStatus = (coach) => {
  if (!coach.relationship) return 'none'
  
  // 逻辑对应 SQL: status=1 且 expire_at > now
  const now = new Date()
  const expireDate = new Date(coach.relationship.expireAt)
  const status = coach.relationship.status

  // status: 1-正常, 0-已过期/冻结
  if (status === 1 && expireDate > now) {
    return 'active'
  }
  return 'expired'
}

// 辅助函数：是否为有效会员
const isActiveMember = (coach) => {
  return getRelationStatus(coach) === 'active'
}

// 详情查看
const showCoachDetails = async (coach) => {
  selectedCoach.value = coach
  dialogVisible.value = true
  
  try {
    const detail = await getCoachById(coach.id)
    // 合并详情数据，保留列表中的 relationship 信息
    selectedCoach.value = { ...selectedCoach.value, ...detail }
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 跳转到制定计划页面
const goToTrainingPlan = (coach) => {
  // 直接跳转到现有页面，通过 query 传递教练ID (如果目标页面支持)
  // router.push({ 
  //   path: '/training-plans',
  //   query: { coachId: coach.id, action: 'create' }
  // })

    // 跳转到新页面，通过路由参数传递教练ID (如果目标页面不支持 query)

    const userInfo = getUserInfo()
    const studentId = userInfo.userId
    router.push({ 
      path: '/user/training-plans/create',
      params: { coachId: coach.id, studentId: studentId }
    })
}

// 打开续约弹窗
const handleRenew = (coach) => {
  targetRenewCoach.value = coach
  renewDialogVisible.value = true
}

// 确认续约逻辑
// 确认续约逻辑 (真实逻辑)
const confirmRenew = async () => {
  renewing.value = true
  try {
    const userInfo = getUserInfo()
    const studentId = userInfo.userId
    const coachId = targetRenewCoach.value.id
    
    // 1. 获取当前基础时间
    const now = new Date()
    let startDate = now
    
    // 如果已经是活跃会员，则在当前到期时间基础上累加
    const currentStatus = getRelationStatus(targetRenewCoach.value)
    if (currentStatus === 'active') {
      const currentExpire = new Date(targetRenewCoach.value.relationship.expireAt)
      if (currentExpire > now) {
        startDate = currentExpire
      }
    }
    
    // 2. 计算新的到期时间
    const months = parseInt(renewDuration.value)
    const newExpireDate = new Date(startDate)
    newExpireDate.setMonth(newExpireDate.getMonth() + months)
    
    // 3. 调用 API 更新
    console.log(`正在续约: 教练=${coachId}, 学员=${studentId}, 新到期时间=${newExpireDate.toISOString()}`)
    
    await renewCoachStudent({
      coachId: coachId,
      studentId: studentId,
      expireAt: newExpireDate.toISOString() // 发送 ISO 格式字符串
    })

    showSuccess(`成功为 ${targetRenewCoach.value.username} 办理了续约！`)
    renewDialogVisible.value = false
    
    // 4. 刷新列表状态
    await fetchCoaches()
    
    // 如果详情弹窗开着，也尝试刷新详情数据
    if (dialogVisible.value && selectedCoach.value?.id === coachId) {
      const relation = await getCoachStudentsById(coachId, studentId)
      selectedCoach.value.relationship = relation
    }

  } catch (error) {
    console.error('续约失败:', error)
    showError(error.message || '续约失败，请重试')
  } finally {
    renewing.value = false
  }
}

const handleSearch = () => { /* Computed handled */ }

// 工具函数
const formatGender = (gender) => {
  const map = { 'Male': '男', 'Female': '女', 'Other': '其他' }
  return map[gender] || '未知'
}

const formatDate = (dateString) => {
  if (!dateString) return '--'
  return new Date(dateString).toLocaleDateString('zh-CN', { 
    year: 'numeric', month: '2-digit', day: '2-digit' 
  })
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

onMounted(() => {
  fetchCoaches()
})
</script>

<style scoped>
.coaches-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  color: #303133;
}

.subtitle {
  color: #909399;
  font-size: 16px;
}

.search-section {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.search-input {
  width: 100%;
  max-width: 500px;
}

.coach-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.coach-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  border: 1px solid #ebeef5;
}

.coach-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

/* 会员高亮样式 */
.coach-card.is-member {
  border-color: #e1f3d8; /* 浅绿色边框 */
  background: linear-gradient(to bottom, #f0f9eb 0%, #ffffff 50px);
}

.status-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 10;
}

.coach-card-body {
  padding: 20px 20px 10px;
  text-align: center;
  cursor: pointer;
}

.coach-avatar-wrapper {
  margin-bottom: 12px;
}

.coach-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.expire-info {
  font-size: 13px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.expire-info.active {
  color: #67c23a;
  font-weight: 500;
}

.expire-info.expired {
  color: #f56c6c;
  font-weight: 500;
}

.coach-intro {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 12px;
}

.coach-tags {
  display: flex;
  gap: 6px;
  justify-content: center;
  flex-wrap: wrap;
}

.card-footer {
  padding: 12px 16px 16px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
  border-top: 1px solid #f2f2f2;
  background-color: #fafafa;
}

.action-btn {
  flex: 1;
}

/* 详情弹窗样式 */
.highlight-section {
  background-color: #f0f9eb;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e1f3d8;
  margin-bottom: 20px;
}

.highlight-section h3 {
  color: #67c23a;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.membership-info .info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row .label {
  color: #606266;
}

.info-row .value {
  font-weight: 500;
  color: #303133;
}

.info-row .value.active {
  color: #67c23a;
}

.info-row .value.expired {
  color: #f56c6c;
}

.detail-header {
  text-align: center;
  margin-bottom: 20px;
}

.detail-name {
  margin-top: 12px;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h3 {
  font-size: 16px;
  margin-bottom: 10px;
  padding-left: 10px;
  border-left: 4px solid #409EFF;
}

.detail-intro {
  background-color: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
}

.renew-content {
  text-align: center;
}

.price-options {
  margin-top: 20px;
  margin-bottom: 10px;
}

@media (max-width: 768px) {
  .coaches-container {
    padding: 16px;
  }
  
  .coach-grid {
    grid-template-columns: 1fr;
  }
}
</style>