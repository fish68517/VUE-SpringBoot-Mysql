<template>
  <div class="coach-feedback-page">
    <div class="page-header">
      <el-button class="back-btn" text @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2>{{ coachName }} 的反馈记录</h2>
      <p class="sub-title">上方为学员反馈，下方为教练回复</p>
    </div>

    <div v-loading="loading" class="feedback-wrapper">
      <el-empty v-if="!loading && feedbackList.length === 0" description="暂无反馈记录" />

      <div v-for="item in feedbackList" :key="item.id" class="feedback-item">
        <el-card shadow="hover">
          <div class="section student-section">
            <div class="section-header">
              <span class="name">{{ studentName }}</span>
              <span class="time">{{ formatDate(item.feedbackDate || item.createdAt, true) }}</span>
            </div>
            <div class="section-content">
              <div class="text-content">
                {{ item.content || '（无文字反馈）' }}
              </div>
              <div v-if="item.imageUrls.length > 0" class="attachment-block">
                <div class="attachment-title">
                  <el-icon><Picture /></el-icon>
                  <span>图片（{{ item.imageUrls.length }}）</span>
                </div>
                <div class="image-grid">
                  <el-image
                    v-for="(url, index) in item.imageUrls"
                    :key="`${item.id}-student-img-${index}`"
                    :src="url"
                    :preview-src-list="item.imageUrls"
                    :initial-index="index"
                    fit="cover"
                    class="feedback-image"
                  />
                </div>
              </div>
              <div v-if="item.videoUrls.length > 0" class="attachment-block">
                <div class="attachment-title">
                  <el-icon><VideoPlay /></el-icon>
                  <span>视频（{{ item.videoUrls.length }}）</span>
                </div>
                <div class="video-list">
                  <video
                    v-for="(url, index) in item.videoUrls"
                    :key="`${item.id}-student-video-${index}`"
                    :src="url"
                    controls
                    class="feedback-video"
                  />
                </div>
              </div>
              <div v-if="item.documentUrls.length > 0" class="attachment-block">
                <div class="attachment-title">
                  <el-icon><Document /></el-icon>
                  <span>文档（{{ item.documentUrls.length }}）</span>
                </div>
                <div class="doc-list">
                  <el-link
                    v-for="(url, index) in item.documentUrls"
                    :key="`${item.id}-student-doc-${index}`"
                    :href="url"
                    target="_blank"
                    type="primary"
                  >
                    {{ getFileName(url) }}
                  </el-link>
                </div>
              </div>
            </div>
          </div>

          <div class="divider"></div>

          <div class="section coach-section">
            <div class="section-header">
              <span class="name">{{ coachName }}</span>
              <span class="time">{{ formatDate(item.replyAt, true) || '暂未回复' }}</span>
            </div>
            <div class="section-content">
              <template v-if="hasCoachReply(item)">
                <div class="text-content" v-if="item.coachReply">
                  {{ item.coachReply }}
                </div>
                <div v-if="item.coachReplyImageUrls.length > 0" class="attachment-block">
                  <div class="attachment-title">
                    <el-icon><Picture /></el-icon>
                    <span>图片（{{ item.coachReplyImageUrls.length }}）</span>
                  </div>
                  <div class="image-grid">
                    <el-image
                      v-for="(url, index) in item.coachReplyImageUrls"
                      :key="`${item.id}-coach-img-${index}`"
                      :src="url"
                      :preview-src-list="item.coachReplyImageUrls"
                      :initial-index="index"
                      fit="cover"
                      class="feedback-image"
                    />
                  </div>
                </div>
                <div v-if="item.coachReplyVideoUrls.length > 0" class="attachment-block">
                  <div class="attachment-title">
                    <el-icon><VideoPlay /></el-icon>
                    <span>视频（{{ item.coachReplyVideoUrls.length }}）</span>
                  </div>
                  <div class="video-list">
                    <video
                      v-for="(url, index) in item.coachReplyVideoUrls"
                      :key="`${item.id}-coach-video-${index}`"
                      :src="url"
                      controls
                      class="feedback-video"
                    />
                  </div>
                </div>
                <div v-if="item.coachReplyDocumentUrls.length > 0" class="attachment-block">
                  <div class="attachment-title">
                    <el-icon><Document /></el-icon>
                    <span>文档（{{ item.coachReplyDocumentUrls.length }}）</span>
                  </div>
                  <div class="doc-list">
                    <el-link
                      v-for="(url, index) in item.coachReplyDocumentUrls"
                      :key="`${item.id}-coach-doc-${index}`"
                      :href="url"
                      target="_blank"
                      type="primary"
                    >
                      {{ getFileName(url) }}
                    </el-link>
                  </div>
                </div>
              </template>
              <el-empty v-else description="教练暂未回复" :image-size="56" />
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Document, Picture, VideoPlay } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { getCoachById } from '@/api/coach.js'
import { getFeedbackListByStudent } from '@/api/training.js'
import { getUserInfo } from '@/utils/auth.js'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const feedbackList = ref([])
const coachDisplayName = ref(route.query.coachName || '')

const userInfo = getUserInfo() || {}
const studentId = userInfo.userId || userInfo.id
const studentName = computed(() => userInfo.username || '学员')

const coachId = computed(() => Number(route.params.coachId || 0))
const coachName = computed(() => coachDisplayName.value || `教练#${coachId.value}`)

const goBack = () => {
  router.back()
}

const getStaticBaseUrl = () => {
  const apiBase = import.meta.env.VITE_API_BASE_URL || ''
  if (!apiBase) return ''
  return apiBase.replace(/\/api\/?$/, '')
}

const toAbsoluteUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  const base = getStaticBaseUrl()
  if (!base) return url
  return url.startsWith('/') ? `${base}${url}` : `${base}/${url}`
}

const splitUrls = (raw) => {
  if (!raw) return []
  return String(raw)
    .split(',')
    .map((v) => v.trim())
    .filter(Boolean)
    .map((v) => toAbsoluteUrl(v))
}

const normalizeFeedbackItem = (item) => {
  return {
    ...item,
    id: item.id,
    feedbackDate: item.feedbackDate || item.feedback_date,
    createdAt: item.createdAt || item.created_at,
    content: item.content || '',
    imageUrls: splitUrls(item.imageUrls || item.image_urls),
    videoUrls: splitUrls(item.videoUrls || item.video_urls),
    documentUrls: splitUrls(item.documentUrls || item.document_urls),
    coachReply: item.coachReply || item.coach_reply || '',
    replyAt: item.replyAt || item.reply_at || null,
    coachReplyImageUrls: splitUrls(item.coachReplyImageUrls || item.coach_reply_image_urls),
    coachReplyVideoUrls: splitUrls(item.coachReplyVideoUrls || item.coach_reply_video_urls),
    coachReplyDocumentUrls: splitUrls(item.coachReplyDocumentUrls || item.coach_reply_document_urls)
  }
}

const hasCoachReply = (item) => {
  return Boolean(
    item.coachReply ||
      item.coachReplyImageUrls.length > 0 ||
      item.coachReplyVideoUrls.length > 0 ||
      item.coachReplyDocumentUrls.length > 0
  )
}

const getFileName = (url) => {
  if (!url) return '附件'
  const clean = url.split('?')[0]
  return clean.substring(clean.lastIndexOf('/') + 1) || '附件'
}

const formatDate = (dateStr, withTime = false) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return ''
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' }
  if (withTime) {
    options.hour = '2-digit'
    options.minute = '2-digit'
  }
  return date.toLocaleString('zh-CN', options)
}

const fetchCoachName = async () => {
  if (coachDisplayName.value || !coachId.value) return
  try {
    const detail = await getCoachById(coachId.value)
    coachDisplayName.value = detail?.username || ''
  } catch {
    coachDisplayName.value = ''
  }
}

const fetchFeedbackList = async () => {
  if (!coachId.value || !studentId) {
    ElMessage.error('缺少教练或学员信息')
    return
  }
  loading.value = true
  try {
    const res = await getFeedbackListByStudent({
      coachId: coachId.value,
      studentId
    })
    const list = Array.isArray(res) ? res : (res?.content || [])
    feedbackList.value = list.map(normalizeFeedbackItem).filter(hasCoachReply)
  } catch (error) {
    console.error(error)
    ElMessage.error('加载反馈记录失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchCoachName()
  await fetchFeedbackList()
})
</script>

<style scoped>
.coach-feedback-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  position: relative;
  text-align: center;
  margin-bottom: 24px;
}

.back-btn {
  position: absolute;
  left: 0;
  top: 0;
}

.page-header h2 {
  margin: 0;
}

.sub-title {
  margin-top: 8px;
  color: #909399;
}

.feedback-wrapper {
  min-height: 220px;
}

.feedback-item {
  margin-bottom: 16px;
}

.section {
  border-radius: 8px;
  padding: 12px;
}

.student-section {
  background: #f5f8ff;
  border: 1px solid #dbe5ff;
}

.coach-section {
  background: #f6fbf4;
  border: 1px solid #dcefd3;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.name {
  font-weight: 600;
}

.time {
  color: #909399;
  font-size: 13px;
}

.text-content {
  color: #303133;
  line-height: 1.7;
}

.divider {
  height: 1px;
  background: #ebeef5;
  margin: 14px 0;
}

.attachment-block {
  margin-top: 10px;
  padding: 10px 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
}

.attachment-title {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  color: #606266;
  font-size: 13px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}

.feedback-image {
  width: 100%;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
}

.video-list {
  display: grid;
  gap: 10px;
}

.feedback-video {
  width: 100%;
  max-height: 320px;
  border-radius: 8px;
  background: #000;
}

.doc-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

@media (max-width: 768px) {
  .coach-feedback-page {
    padding: 16px;
  }

  .back-btn {
    position: static;
    margin-bottom: 8px;
  }

  .page-header {
    text-align: left;
  }
}
</style>
