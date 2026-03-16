<template>
  <div class="coaches-container">
    <div class="page-header">
      <el-button class="back-btn" text @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h1>我的教练</h1>
      <p class="subtitle">选择适合你的教练并管理训练关系</p>
    </div>

    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索教练姓名或简介..."
        :prefix-icon="Search"
        clearable
        class="search-input"
      />
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated count="4" class="skeleton-grid" />
    </div>

    <div v-else-if="filteredCoaches.length > 0" class="coach-grid">
      <el-card
        v-for="coach in filteredCoaches"
        :key="coach.id"
        class="coach-card"
        :class="{ 'is-member': isActiveMember(coach) }"
        shadow="hover"
      >
        <div v-if="getRelationStatus(coach) !== 'none'" class="status-badge">
          <el-tag :type="getRelationStatus(coach) === 'active' ? 'success' : 'danger'" effect="dark">
            {{ getRelationStatus(coach) === 'active' ? '会员有效' : '会员已过期' }}
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

            <p v-if="isActiveMember(coach)" class="expire-info active">
              <el-icon><Timer /></el-icon> 到期：{{ formatDate(coach.relationship?.expireAt) }}
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

        <div class="card-footer">
          <el-button
  
            v-if="false"
            type="primary"
            size="small"
            class="action-btn"
            @click.stop="goToTrainingPlan(coach)"
          >
            <el-icon><Calendar /></el-icon> 训练计划
          </el-button>

          <el-button
            v-if="isActiveMember(coach)"
            type="success"
            size="small"
            class="action-btn"
            @click.stop="showFeedbackDialog(coach)"
          >
            <el-icon><Star /></el-icon> 学员反馈
          </el-button>

          <el-button
            v-else
            type="warning"
            plain
            size="small"
            class="action-btn"
            @click.stop="handleRenew(coach)"
          >
            <el-icon><Money /></el-icon> {{ getRelationStatus(coach) === 'expired' ? '续费' : '开通会员' }}
          </el-button>

          <el-button @click.stop="showCoachDetails(coach)" size="small" plain>查看详情</el-button>
        </div>
      </el-card>
    </div>

    <div v-else class="empty-state">
      <el-empty description="暂无匹配的教练" />
    </div>

    <el-dialog
      v-model="feedbackDialogVisible"
      title="学员反馈"
      width="680px"
      center
      destroy-on-close
      class="feedback-dialog"
    >
      <el-form ref="feedbackFormRef" :model="feedbackForm" :rules="feedbackRules" label-width="96px">
        <el-form-item label="反馈日期" prop="feedback_date">
          <el-date-picker
            v-model="feedbackForm.feedback_date"
            type="date"
            placeholder="选择反馈日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
            :disabled-date="(time) => time.getTime() > Date.now()"
          />
        </el-form-item>

        <el-form-item label="训练感受" prop="feeling">
          <el-radio-group v-model="feedbackForm.feeling">
            <el-radio-button label="easy">轻松</el-radio-button>
            <el-radio-button label="normal">正常</el-radio-button>
            <el-radio-button label="hard">较难</el-radio-button>
            <el-radio-button label="exhausted">非常累</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="评分" prop="rating">
          <el-rate v-model="feedbackForm.rating" :max="5" show-text :texts="['很差', '较差', '一般', '不错', '很好']" />
        </el-form-item>

        <el-form-item label="反馈内容" prop="content">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请输入反馈内容（可只上传附件）"
          />
        </el-form-item>

        <el-form-item label="上传图片">
          <el-upload
            list-type="picture-card"
            :limit="6"
            :http-request="handleImageUpload"
            :on-remove="handleImageRemove"
            :before-upload="beforeImageUpload"
            :file-list="imageFileList"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">支持 jpg/png/webp，单张不超过 5MB</div>
        </el-form-item>

        <el-form-item label="上传视频">
          <el-upload
            :limit="2"
            :http-request="handleVideoUpload"
            :on-remove="handleVideoRemove"
            :before-upload="beforeVideoUpload"
            :file-list="videoFileList"
            accept="video/*"
          >
            <el-button type="primary" plain :loading="uploadingVideo">上传视频</el-button>
          </el-upload>
          <div class="upload-tip">支持 mp4/mov/avi，单个不超过 100MB</div>
        </el-form-item>

        <el-form-item label="上传文档">
          <el-upload
            :limit="3"
            :http-request="handleDocumentUpload"
            :on-remove="handleDocumentRemove"
            :before-upload="beforeDocumentUpload"
            :file-list="documentFileList"
            accept=".pdf,.doc,.docx,.txt"
          >
            <el-button type="primary" plain :loading="uploadingDocument">上传文档</el-button>
          </el-upload>
          <div class="upload-tip">支持 pdf/doc/docx/txt，单个不超过 20MB</div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback" :loading="submittingFeedback">提交反馈</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="dialogVisible"
      :title="selectedCoach?.username ? selectedCoach.username + ' - 教练详情' : '教练详情'"
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

        <div class="detail-section highlight-section" v-if="getRelationStatus(selectedCoach) !== 'none'">
          <h3><el-icon><UserFilled /></el-icon> 会员状态</h3>
          <div class="membership-info">
            <div class="info-row">
              <span class="label">当前状态</span>
              <span :class="['value', getRelationStatus(selectedCoach)]">
                {{ getRelationStatus(selectedCoach) === 'active' ? '有效' : '已过期' }}
              </span>
            </div>
            <div class="info-row">
              <span class="label">开通时间</span>
              <span class="value">{{ formatDate(selectedCoach.relationship?.createdAt) }}</span>
            </div>
            <div class="info-row">
              <span class="label">到期时间</span>
              <span class="value">{{ formatDate(selectedCoach.relationship?.expireAt) }}</span>
            </div>
            <el-button type="primary" style="width: 100%; margin-top: 10px" @click="handleRenew(selectedCoach)">
              {{ getRelationStatus(selectedCoach) === 'active' ? '续费会员' : '开通会员' }}
            </el-button>
          </div>
        </div>

        <div class="detail-section">
          <h3>教练简介</h3>
          <p class="detail-intro">{{ selectedCoach.intro || '暂无简介' }}</p>
        </div>

        <div class="detail-section" v-if="selectedCoach.specialties">
          <h3>擅长方向</h3>
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

    <el-dialog v-model="renewDialogVisible" title="开通/续费会员" width="400px" center>
      <div v-if="targetRenewCoach" class="renew-content">
        <p>为教练 <strong>{{ targetRenewCoach.username }}</strong> 购买会员：</p>
        <div class="price-options">
          <el-radio-group v-model="renewDuration">
            <el-radio-button label="1">1个月 / ￥100</el-radio-button>
            <el-radio-button label="3">3个月 / ￥270</el-radio-button>
            <el-radio-button label="12">12个月 / ￥960</el-radio-button>
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
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  ArrowLeft,
  Calendar,
  Money,
  Plus,
  Search,
  Star,
  Timer,
  UserFilled,
  Warning
} from '@element-plus/icons-vue'
import { getCoaches, getCoachById, getCoachStudentsById, renewCoachStudent } from '@/api/coach.js'
import { submitNewFeedback } from '@/api/feedback.js'
import { uploadDocument, uploadImage, uploadVideo } from '@/api/upload.js'
import { getUserInfo } from '@/utils/auth.js'
import { showError, showSuccess } from '@/utils/feedback.js'

const router = useRouter()
const coaches = ref([])
const loading = ref(false)
const searchQuery = ref('')
const dialogVisible = ref(false)
const selectedCoach = ref(null)

const feedbackDialogVisible = ref(false)
const submittingFeedback = ref(false)
const feedbackFormRef = ref(null)
const currentCoachInfo = ref(null)

const uploadingVideo = ref(false)
const uploadingDocument = ref(false)
const imageFileList = ref([])
const videoFileList = ref([])
const documentFileList = ref([])

const renewDialogVisible = ref(false)
const targetRenewCoach = ref(null)
const renewDuration = ref('1')
const renewing = ref(false)

const feedbackForm = reactive({
  plan_id: null,
  feedback_date: '',
  feeling: 'normal',
  rating: 5,
  content: ''
})

const validateFeedbackContent = (_rule, _value, callback) => {
  const hasText = !!(feedbackForm.content && feedbackForm.content.trim())
  const hasAttachment =
    imageFileList.value.length > 0 || videoFileList.value.length > 0 || documentFileList.value.length > 0

  if (!hasText && !hasAttachment) {
    callback(new Error('请至少填写文字反馈或上传一个附件'))
    return
  }
  callback()
}

const feedbackRules = {
  feedback_date: [{ required: true, message: '请选择反馈日期', trigger: 'change' }],
  feeling: [{ required: true, message: '请选择训练感受', trigger: 'change' }],
  rating: [{ required: true, message: '请评分', trigger: 'change' }],
  content: [{ validator: validateFeedbackContent, trigger: 'blur' }]
}

const filteredCoaches = computed(() => {
  if (!searchQuery.value) return coaches.value
  const query = searchQuery.value.toLowerCase()
  return coaches.value.filter((coach) => {
    return (
      coach.username?.toLowerCase().includes(query) ||
      (coach.intro && coach.intro.toLowerCase().includes(query))
    )
  })
})

const goBack = () => {
  router.back()
}

const getRelationStatus = (coach) => {
  if (!coach?.relationship) return 'none'

  const now = new Date()
  const expireAt = coach.relationship.expireAt ? new Date(coach.relationship.expireAt) : null
  if (coach.relationship.status === 1 && expireAt && expireAt > now) {
    return 'active'
  }
  return 'expired'
}

const isActiveMember = (coach) => getRelationStatus(coach) === 'active'

const formatGender = (gender) => {
  const map = { Male: '男', Female: '女', Other: '其他' }
  return map[gender] || '未知'
}

const formatDate = (dateString) => {
  if (!dateString) return '--'
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text
}

const fetchCoaches = async () => {
  loading.value = true
  try {
    const response = await getCoaches()
    const rawCoaches = Array.isArray(response) ? response : (response.content || [])

    const userInfo = getUserInfo()
    const studentId = userInfo?.userId

    if (!studentId) {
      coaches.value = rawCoaches.map((item) => ({ ...item, relationship: null }))
      return
    }

    const coachesWithRelation = await Promise.all(
      rawCoaches.map(async (coach) => {
        try {
          const relation = await getCoachStudentsById(coach.id, studentId)
          return { ...coach, relationship: relation || null }
        } catch {
          return { ...coach, relationship: null }
        }
      })
    )

    coaches.value = coachesWithRelation
  } catch (error) {
    console.error('获取教练列表失败:', error)
    showError('获取教练列表失败')
  } finally {
    loading.value = false
  }
}

const showCoachDetails = async (coach) => {
  selectedCoach.value = coach
  dialogVisible.value = true

  try {
    const detail = await getCoachById(coach.id)
    selectedCoach.value = { ...selectedCoach.value, ...detail }
  } catch (error) {
    console.error('获取教练详情失败:', error)
  }
}

const goToTrainingPlan = (coach) => {
  router.push({
    name: 'CreateTrainingPlanStudent',
    params: { coachId: coach.id }
  })
}

const handleRenew = (coach) => {
  targetRenewCoach.value = coach
  renewDialogVisible.value = true
}

const confirmRenew = async () => {
  renewing.value = true
  try {
    const userInfo = getUserInfo()
    const studentId = userInfo?.userId
    const coachId = targetRenewCoach.value?.id

    if (!studentId || !coachId) {
      throw new Error('缺少续费必要参数')
    }

    const now = new Date()
    let startDate = now

    const currentStatus = getRelationStatus(targetRenewCoach.value)
    if (currentStatus === 'active') {
      const currentExpire = new Date(targetRenewCoach.value.relationship.expireAt)
      if (currentExpire > now) startDate = currentExpire
    }

    const months = Number.parseInt(renewDuration.value, 10)
    const newExpireDate = new Date(startDate)
    newExpireDate.setMonth(newExpireDate.getMonth() + months)

    await renewCoachStudent({
      coachId,
      studentId,
      expireAt: newExpireDate.toISOString()
    })

    showSuccess(`已为 ${targetRenewCoach.value.username} 完成会员续费`)
    renewDialogVisible.value = false

    await fetchCoaches()

    if (dialogVisible.value && selectedCoach.value?.id === coachId) {
      const relation = await getCoachStudentsById(coachId, studentId)
      selectedCoach.value.relationship = relation || null
    }
  } catch (error) {
    console.error('续费失败:', error)
    showError(error.message || '续费失败，请稍后再试')
  } finally {
    renewing.value = false
  }
}

const resetFeedbackForm = () => {
  feedbackForm.plan_id = null
  feedbackForm.feedback_date = new Date().toISOString().split('T')[0]
  feedbackForm.feeling = 'normal'
  feedbackForm.rating = 5
  feedbackForm.content = ''
  imageFileList.value = []
  videoFileList.value = []
  documentFileList.value = []
}

const resolvePlanId = (coach) => {
  return coach.currentPlanId || coach.relationship?.planId || coach.planId || 1
}

const showFeedbackDialog = (coach) => {
  currentCoachInfo.value = coach
  feedbackDialogVisible.value = true

  nextTick(() => {
    feedbackFormRef.value?.resetFields()
    resetFeedbackForm()
    feedbackForm.plan_id = resolvePlanId(coach)
  })
}

const normalizeUploadedUrl = (res) => {
  if (!res) return ''
  if (typeof res === 'string') return res
  if (res.url) return res.url
  if (res.data?.url) return res.data.url
  return ''
}

const beforeImageUpload = (file) => {
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) ElMessage.error('图片大小不能超过 5MB')
  return isLt5M
}

const beforeVideoUpload = (file) => {
  const isLt100M = file.size / 1024 / 1024 < 100
  if (!isLt100M) ElMessage.error('视频大小不能超过 100MB')
  return isLt100M
}

const beforeDocumentUpload = (file) => {
  const isLt20M = file.size / 1024 / 1024 < 20
  if (!isLt20M) ElMessage.error('文档大小不能超过 20MB')
  return isLt20M
}

const handleImageUpload = async ({ file, onSuccess, onError }) => {
  try {
    const res = await uploadImage(file)
    const url = normalizeUploadedUrl(res)
    imageFileList.value.push({ name: file.name, url })
    onSuccess?.(res)
  } catch (error) {
    onError?.(error)
  }
}

const handleVideoUpload = async ({ file, onSuccess, onError }) => {
  uploadingVideo.value = true
  try {
    const res = await uploadVideo(file)
    const url = normalizeUploadedUrl(res)
    videoFileList.value.push({ name: file.name, url })
    onSuccess?.(res)
  } catch (error) {
    onError?.(error)
  } finally {
    uploadingVideo.value = false
  }
}

const handleDocumentUpload = async ({ file, onSuccess, onError }) => {
  uploadingDocument.value = true
  try {
    const res = await uploadDocument(file)
    const url = normalizeUploadedUrl(res)
    documentFileList.value.push({ name: file.name, url })
    onSuccess?.(res)
  } catch (error) {
    onError?.(error)
  } finally {
    uploadingDocument.value = false
  }
}

const handleImageRemove = (file) => {
  imageFileList.value = imageFileList.value.filter((item) => item.url !== file.url)
}

const handleVideoRemove = (file) => {
  videoFileList.value = videoFileList.value.filter((item) => item.url !== file.url)
}

const handleDocumentRemove = (file) => {
  documentFileList.value = documentFileList.value.filter((item) => item.url !== file.url)
}

const submitFeedback = async () => {
  if (!feedbackFormRef.value) return

  await feedbackFormRef.value.validate(async (valid) => {
    if (!valid) return

    if (!feedbackForm.plan_id) {
       // 给默认训练计划
       feedbackForm.plan_id = resolvePlanId(currentCoachInfo.value)
      // return
    }

    const userInfo = getUserInfo()
    const studentId = userInfo?.userId

    if (!studentId || !currentCoachInfo.value?.id) {
      ElMessage.error('用户信息异常，请重新登录后再试')
      return
    }

    submittingFeedback.value = true
    try {
      const payload = {
        planId: feedbackForm.plan_id,
        studentId,
        coachId: currentCoachInfo.value.id,
        feedbackDate: feedbackForm.feedback_date,
        rating: feedbackForm.rating,
        feeling: feedbackForm.feeling,
        content: feedbackForm.content?.trim() || '',
        imageUrls: imageFileList.value.map((item) => item.url).join(','),
        videoUrls: videoFileList.value.map((item) => item.url).join(','),
        documentUrls: documentFileList.value.map((item) => item.url).join(',')
      }

      await submitNewFeedback(payload)
      ElMessage.success('反馈提交成功')
      feedbackDialogVisible.value = false
    } catch (error) {
      console.error('提交反馈失败:', error)
      ElMessage.error('提交反馈失败，请稍后再试')
    } finally {
      submittingFeedback.value = false
    }
  })
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
  position: relative;
  text-align: center;
  margin-bottom: 32px;
}

.back-btn {
  position: absolute;
  left: 0;
  top: 0;
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

.coach-card.is-member {
  border-color: #e1f3d8;
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

.feedback-dialog :deep(.el-upload-list__item-name) {
  max-width: 380px;
}

.upload-tip {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
  margin-top: 6px;
}

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
  border-left: 4px solid #409eff;
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

  .back-btn {
    position: static;
    margin-bottom: 12px;
  }

  .page-header {
    text-align: left;
  }

  .page-header h1 {
    font-size: 28px;
  }
}
</style>
