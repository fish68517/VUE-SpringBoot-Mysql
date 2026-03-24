<template>
  <div class="feedback-page">
    <div class="student-sidebar">
      <div class="sidebar-header">
         <el-button class="back-btn" text @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
        <h3>我的学员</h3>
        <el-input
          v-model="studentSearch"
          placeholder="搜索学员姓名..."
          :prefix-icon="Search"
          clearable
        />
      </div>

      <div class="student-list" v-loading="loadingStudents">
        <div
          v-for="student in filteredStudents"
          :key="student.id"
          class="student-item"
          :class="{ active: currentStudent?.id === student.id }"
          @click="selectStudent(student)"
        >
          <el-avatar :size="40" :src="student.avatar || '/default-avatar.png'">
            {{ student.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <div class="student-info">
            <div class="name">{{ student.username }}</div>
          </div>
          <el-icon v-if="currentStudent?.id === student.id"><ArrowRight /></el-icon>
        </div>

        <el-empty v-if="filteredStudents.length === 0" description="未找到学员" :image-size="60" />
      </div>
    </div>

    <div class="feedback-main">
      <div class="main-header" v-if="currentStudent">
        <div class="header-left">
          <h2>{{ currentStudent.username }} 的训练反馈</h2>
          <el-tag type="info" size="small">共 {{ feedbackList.length }} 条记录</el-tag>
        </div>
      </div>

      <div class="feedback-content" v-loading="loadingFeedback">
        <div v-if="!currentStudent" class="empty-placeholder">
          <el-empty description="请在左侧选择一名学员查看反馈" />
        </div>
        <div v-else-if="feedbackList.length === 0" class="empty-placeholder">
          <el-empty description="该学员暂无训练反馈" />
        </div>

        <el-timeline v-else>
          <el-timeline-item
            v-for="item in feedbackList"
            :key="item.id"
            :timestamp="formatDate(item.feedbackDate || item.createdAt)"
            placement="top"
            :type="getFeelingColor(item.feeling)"
            size="large"
          >
            <el-card class="feedback-card">
              <div class="card-header-row">
                <div class="meta-left">
                  <el-rate v-model="item.rating" disabled show-score text-color="#ff9900" />
                  <el-tag size="small" type="info">{{ getFeelingText(item.feeling) }}</el-tag>
                </div>
                <div class="meta-right" v-if="false">
                  <el-button type="danger" link :icon="Delete" @click="handleDelete(item)">删除</el-button>
                </div>
              </div>

              <div class="feedback-text">
                <strong>学员反馈：</strong>
                <span>{{ item.content || '（无文字反馈）' }}</span>
              </div>

              <div class="feedback-text" v-if="item.coachReply">
                <strong>教练回复：</strong>
                <span>{{ item.coachReply }}</span>
              </div>

              <div v-if="item.imageUrls.length > 0" class="attachment-block">
                <div class="attachment-title">
                  <el-icon><Picture /></el-icon>
                  <span>图片（{{ item.imageUrls.length }}）</span>
                </div>
                <div class="image-grid">
                  <el-image
                    v-for="(url, index) in item.imageUrls"
                    :key="`${item.id}-img-${index}`"
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
                    :key="`${item.id}-video-${index}`"
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
                    :key="`${item.id}-doc-${index}`"
                    :href="url"
                    target="_blank"
                    type="primary"
                    class="doc-link"
                  >
                    {{ getFileName(url) }}
                  </el-link>
                </div>
              </div>

              <div class="reply-section">
                <div v-if="hasCoachReply(item)" class="reply-box answered">
                  <div class="reply-header">
                    <span class="coach-label"><el-icon><Avatar /></el-icon> 我的回复：</span>
                    <span class="reply-time">{{ formatDate(item.replyAt, true) }}</span>
                  </div>
                  <div class="reply-content" v-if="item.coachReply">{{ item.coachReply }}</div>
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
                        class="doc-link"
                      >
                        {{ getFileName(url) }}
                      </el-link>
                    </div>
                  </div>
                  <el-button type="primary" link size="small" @click="openReplyDialog(item)">修改回复</el-button>
                </div>

                <div v-else class="reply-box un-answered">
                  <el-button type="primary" plain size="small" @click="openReplyDialog(item)">
                    <el-icon><ChatLineSquare /></el-icon>
                    立即回复
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <el-dialog v-model="replyDialogVisible" title="回复学员反馈" width="500px" append-to-body>
      <el-form :model="replyForm">
        <el-form-item>
          <div class="original-content-preview">学员说："{{ currentReplyItem?.content || '（无文字反馈）' }}"</div>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入给学员的建议或鼓励..."
          />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload
            list-type="picture-card"
            :limit="6"
            :http-request="handleReplyImageUpload"
            :on-remove="handleReplyImageRemove"
            :before-upload="beforeReplyImageUpload"
            :file-list="replyImageFileList"
            accept="image/*"
          >
            <el-icon><Picture /></el-icon>
          </el-upload>
          <div class="upload-tip">支持 jpg/png/webp，单张不超过 5MB</div>
        </el-form-item>
        <el-form-item label="上传视频">
          <el-upload
            :limit="2"
            :http-request="handleReplyVideoUpload"
            :on-remove="handleReplyVideoRemove"
            :before-upload="beforeReplyVideoUpload"
            :file-list="replyVideoFileList"
            accept="video/*"
          >
            <el-button type="primary" plain :loading="uploadingReplyVideo">上传视频</el-button>
          </el-upload>
          <div class="upload-tip">支持 mp4/mov/avi，单个不超过 100MB</div>
        </el-form-item>
        <el-form-item label="上传文档">
          <el-upload
            :limit="3"
            :http-request="handleReplyDocumentUpload"
            :on-remove="handleReplyDocumentRemove"
            :before-upload="beforeReplyDocumentUpload"
            :file-list="replyDocumentFileList"
            accept=".pdf,.doc,.docx,.txt"
          >
            <el-button type="primary" plain :loading="uploadingReplyDocument">上传文档</el-button>
          </el-upload>
          <div class="upload-tip">支持 pdf/doc/docx/txt，单个不超过 20MB</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply" :loading="submittingReply">发送回复</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowRight,
  Avatar,
  ChatLineSquare,
  Delete,
  Document,
  Picture,
  Search,
  VideoPlay
} from '@element-plus/icons-vue'
import { getUserInfo } from '@/utils/auth.js'
import { getMyStudents } from '@/api/coach.js'
import { deleteFeedback, getFeedbackListByStudent, replyFeedback } from '@/api/training.js'
import { uploadDocument, uploadImage, uploadVideo } from '@/api/upload.js'

const loadingStudents = ref(false)
const loadingFeedback = ref(false)
const studentSearch = ref('')
const students = ref([])
const currentStudent = ref(null)
const feedbackList = ref([])

const replyDialogVisible = ref(false)
const submittingReply = ref(false)
const currentReplyItem = ref(null)
const replyForm = reactive({
  content: ''
})
const uploadingReplyVideo = ref(false)
const uploadingReplyDocument = ref(false)
const replyImageFileList = ref([])
const replyVideoFileList = ref([])
const replyDocumentFileList = ref([])


const router = useRouter();
import { useRouter } from 'vue-router'
const goBack = () => {
  router.back()
}

const filteredStudents = computed(() => {
  if (!studentSearch.value) return students.value
  const keyword = studentSearch.value.toLowerCase()
  return students.value.filter((s) => s.username.toLowerCase().includes(keyword))
})

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

const normalizeUploadedUrl = (res) => {
  if (!res) return ''
  if (typeof res === 'string') return res
  if (res.url) return res.url
  if (res.data?.url) return res.data.url
  return ''
}

const mapFilesFromUrls = (urls) => {
  return (urls || []).map((url) => ({
    name: getFileName(url),
    url
  }))
}

const hasCoachReply = (item) => {
  if (!item) return false
  return Boolean(
    item.coachReply ||
    (item.coachReplyImageUrls && item.coachReplyImageUrls.length > 0) ||
    (item.coachReplyVideoUrls && item.coachReplyVideoUrls.length > 0) ||
    (item.coachReplyDocumentUrls && item.coachReplyDocumentUrls.length > 0)
  )
}

const normalizeStudent = (item) => {
  const profile = item.student || item.user || item
  return {
    id: item.id ?? profile.id,
    studentId: profile.id ?? item.studentId,
    username: profile.username || '未命名学员',
    avatar: profile.avatar || '',
    raw: item
  }
}

const normalizeFeedbackItem = (item) => {
  const normalized = {
    ...item,
    id: item.id,
    feedbackDate: item.feedbackDate || item.feedback_date,
    createdAt: item.createdAt || item.created_at,
    rating: Number(item.rating || 0),
    feeling: item.feeling || 'normal',
    content: item.content || '',
    coachReply: item.coachReply || item.coach_reply || '',
    coachReplyImageUrls: splitUrls(item.coachReplyImageUrls || item.coach_reply_image_urls),
    coachReplyVideoUrls: splitUrls(item.coachReplyVideoUrls || item.coach_reply_video_urls),
    coachReplyDocumentUrls: splitUrls(item.coachReplyDocumentUrls || item.coach_reply_document_urls),
    replyAt: item.replyAt || item.reply_at || null,
    imageUrls: splitUrls(item.imageUrls || item.image_urls),
    videoUrls: splitUrls(item.videoUrls || item.video_urls),
    documentUrls: splitUrls(item.documentUrls || item.document_urls)
  }
  return normalized
}

const fetchStudents = async () => {
  loadingStudents.value = true
  try {
    const res = await getMyStudents()
    const list = Array.isArray(res) ? res : (res?.content || [])
    students.value = list.map(normalizeStudent)
  } catch (error) {
    console.error(error)
    ElMessage.error('获取学员列表失败')
  } finally {
    loadingStudents.value = false
  }
}

const selectStudent = async (student) => {
  currentStudent.value = student
  loadingFeedback.value = true

  try {
    const userInfo = getUserInfo()
    const coachId = userInfo?.userId || userInfo?.id

    if (!coachId || !student?.studentId) {
      feedbackList.value = []
      return
    }

    const res = await getFeedbackListByStudent({
      coachId,
      studentId: student.studentId
    })

    const list = Array.isArray(res) ? res : (res?.content || [])
    feedbackList.value = list.map(normalizeFeedbackItem)
  } catch (error) {
    console.error(error)
    ElMessage.error('加载反馈数据失败')
  } finally {
    loadingFeedback.value = false
  }
}

const openReplyDialog = (item) => {
  currentReplyItem.value = item
  replyForm.content = item.coachReply || ''
  replyImageFileList.value = mapFilesFromUrls(item.coachReplyImageUrls)
  replyVideoFileList.value = mapFilesFromUrls(item.coachReplyVideoUrls)
  replyDocumentFileList.value = mapFilesFromUrls(item.coachReplyDocumentUrls)
  replyDialogVisible.value = true
}

const beforeReplyImageUpload = (file) => {
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) ElMessage.error('图片大小不能超过 5MB')
  return isLt5M
}

const beforeReplyVideoUpload = (file) => {
  const isLt100M = file.size / 1024 / 1024 < 100
  if (!isLt100M) ElMessage.error('视频大小不能超过 100MB')
  return isLt100M
}

const beforeReplyDocumentUpload = (file) => {
  const isLt20M = file.size / 1024 / 1024 < 20
  if (!isLt20M) ElMessage.error('文档大小不能超过 20MB')
  return isLt20M
}

const handleReplyImageUpload = async ({ file, onSuccess, onError }) => {
  try {
    const res = await uploadImage(file)
    const url = normalizeUploadedUrl(res)
    if (!url) throw new Error('Image upload url is empty')
    replyImageFileList.value.push({ name: file.name, url: toAbsoluteUrl(url) })
    onSuccess?.(res)
  } catch (error) {
    ElMessage.error('图片上传失败')
    onError?.(error)
  }
}

const handleReplyVideoUpload = async ({ file, onSuccess, onError }) => {
  uploadingReplyVideo.value = true
  try {
    const res = await uploadVideo(file)
    const url = normalizeUploadedUrl(res)
    if (!url) throw new Error('Video upload url is empty')
    replyVideoFileList.value.push({ name: file.name, url: toAbsoluteUrl(url) })
    onSuccess?.(res)
  } catch (error) {
    ElMessage.error('视频上传失败')
    onError?.(error)
  } finally {
    uploadingReplyVideo.value = false
  }
}

const handleReplyDocumentUpload = async ({ file, onSuccess, onError }) => {
  uploadingReplyDocument.value = true
  try {
    const res = await uploadDocument(file)
    const url = normalizeUploadedUrl(res)
    if (!url) throw new Error('Document upload url is empty')
    replyDocumentFileList.value.push({ name: file.name, url: toAbsoluteUrl(url) })
    onSuccess?.(res)
  } catch (error) {
    ElMessage.error('文档上传失败')
    onError?.(error)
  } finally {
    uploadingReplyDocument.value = false
  }
}

const handleReplyImageRemove = (file) => {
  replyImageFileList.value = replyImageFileList.value.filter((item) => item.url !== file.url)
}

const handleReplyVideoRemove = (file) => {
  replyVideoFileList.value = replyVideoFileList.value.filter((item) => item.url !== file.url)
}

const handleReplyDocumentRemove = (file) => {
  replyDocumentFileList.value = replyDocumentFileList.value.filter((item) => item.url !== file.url)
}

const toStoredUrl = (url) => {
  if (!url) return ''
  const base = getStaticBaseUrl()
  if (base && url.startsWith(base)) {
    return url.substring(base.length) || '/'
  }
  if (url.startsWith('http://') || url.startsWith('https://')) {
    try {
      return new URL(url).pathname || url
    } catch (_) {
      return url
    }
  }
  return url
}

const submitReply = async () => {
  const coachReply = (replyForm.content || '').trim()
  const imageUrls = replyImageFileList.value.map((item) => toStoredUrl(item.url)).filter(Boolean)
  const videoUrls = replyVideoFileList.value.map((item) => toStoredUrl(item.url)).filter(Boolean)
  const documentUrls = replyDocumentFileList.value.map((item) => toStoredUrl(item.url)).filter(Boolean)

  if (!coachReply && imageUrls.length === 0 && videoUrls.length === 0 && documentUrls.length === 0) {
    ElMessage.warning('请输入回复内容或上传至少一个附件')
    return
  }
  if (!currentReplyItem.value?.id) {
    ElMessage.error('当前反馈项无效，请刷新后重试')
    return
  }

  submittingReply.value = true
  try {
    await replyFeedback({
      id: currentReplyItem.value.id,
      coach_reply: coachReply,
      coach_reply_image_urls: imageUrls.join(','),
      coach_reply_video_urls: videoUrls.join(','),
      coach_reply_document_urls: documentUrls.join(',')
    })

    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    await selectStudent(currentStudent.value)
  } catch (error) {
    console.error(error)
    ElMessage.error('回复失败')
  } finally {
    submittingReply.value = false
  }
}

const handleDelete = (item) => {
  ElMessageBox.confirm('确定要删除这条学员反馈吗？此操作无法恢复。', '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteFeedback(item.id)
      ElMessage.success('删除成功')
      feedbackList.value = feedbackList.value.filter((f) => f.id !== item.id)
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
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

const getFeelingColor = (feeling) => {
  const map = {
    easy: 'success',
    normal: 'primary',
    hard: 'warning',
    exhausted: 'danger'
  }
  return map[feeling] || 'info'
}

const getFeelingText = (feeling) => {
  const map = {
    easy: '轻松',
    normal: '正常',
    hard: '较难',
    exhausted: '非常累'
  }
  return map[feeling] || '未知'
}

const getFileName = (url) => {
  if (!url) return '附件'
  const clean = url.split('?')[0]
  return clean.substring(clean.lastIndexOf('/') + 1) || '附件'
}

onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.feedback-page {
  display: flex;
  height: calc(100vh - 84px);
  background-color: #f5f7fa;
  margin: -24px;
}

.student-sidebar {
  width: 300px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 50px;
  border-bottom: 1px solid #f0f0f0;
}

.sidebar-header h3 {
  margin: 0 0 12px 0;
  color: #303133;
}

.student-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.student-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 6px;
}

.student-item:hover {
  background-color: #f5f7fa;
}

.student-item.active {
  background-color: #ecf5ff;
  border-right: 3px solid #409eff;
}

.student-info {
  margin-left: 12px;
  flex: 1;
  overflow: hidden;
}

.student-info .name {
  font-weight: 500;
  color: #303133;
}

.feedback-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main-header {
  background: #fff;
  padding: 20px 24px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main-header h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
}

.feedback-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.empty-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  background: #fff;
  border-radius: 12px;
}

.feedback-card {
  border-radius: 10px;
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.meta-left {
  display: flex;
  gap: 10px;
  align-items: center;
}

.feedback-text {
  margin-bottom: 10px;
  line-height: 1.7;
  color: #303133;
}

.attachment-block {
  margin-top: 12px;
  padding: 10px 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafcff;
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

.doc-link {
  width: fit-content;
}

.reply-section {
  margin-top: 14px;
}

.reply-box {
  border-radius: 8px;
  padding: 12px;
}

.reply-box.answered {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.reply-box.un-answered {
  background: #f5f7fa;
  border: 1px dashed #dcdfe6;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #606266;
}

.reply-content {
  color: #303133;
  line-height: 1.6;
}

.original-content-preview {
  font-size: 13px;
  color: #606266;
  background: #f5f7fa;
  border-radius: 6px;
  padding: 8px 10px;
  width: 100%;
}

@media (max-width: 992px) {
  .feedback-page {
    flex-direction: column;
    height: auto;
    min-height: calc(100vh - 84px);
  }

  .student-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #e6e6e6;
  }
}
</style>
