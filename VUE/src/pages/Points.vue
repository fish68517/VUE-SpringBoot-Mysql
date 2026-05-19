<template>
  <div class="points-container">
    <div class="points-header">
      <div class="header-content">
        <el-button type="primary" text @click="handleBackToHome">
          <el-icon><ArrowLeft /></el-icon>
          返回首页
        </el-button>
        <h1>打卡与积分商城</h1>
        <div class="user-points">
          <el-icon><Coin /></el-icon>
          当前积分：<span class="points-num">{{ userPoints }}</span>
        </div>
      </div>
    </div>

    <div class="points-content">
      <div class="tabs-section">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="打卡任务" name="tasks">
            <el-empty v-if="tasks.length === 0" description="暂无打卡任务" />
            <div v-else class="task-grid">
              <div v-for="task in tasks" :key="task.id" class="task-card">
                <div class="task-info">
                  <div class="task-title-row">
                    <h3>{{ task.name }}</h3>
                    <el-tag :type="task.status === 'completed' ? 'info' : 'success'">
                      {{ task.status === 'completed' ? '已打卡' : '可打卡' }}
                    </el-tag>
                  </div>
                  <p>{{ task.description }}</p>
                  <div class="task-reward">
                    <el-tag type="warning" effect="dark">+{{ task.points }} 积分</el-tag>
                  </div>
                </div>
                <div class="task-action">
                  <el-button
                    :type="task.status === 'completed' ? 'info' : 'primary'"
                    :disabled="task.status === 'completed'"
                    @click="openCheckinDialog(task)"
                  >
                    {{ task.status === 'completed' ? '已完成' : '上传照片打卡' }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="我的打卡" name="records">
            <el-empty v-if="checkinRecords.length === 0" description="暂无打卡记录" />
            <div v-else class="record-grid">
              <div v-for="record in checkinRecords" :key="record.id" class="record-card">
                <el-image
                  :src="getCheckinImageUrl(record.photo)"
                  :preview-src-list="[getCheckinImageUrl(record.photo)]"
                  fit="cover"
                  class="record-image"
                >
                  <template #error>
                    <div class="image-error">图片加载失败</div>
                  </template>
                </el-image>
                <div class="record-info">
                  <div class="record-title-row">
                    <h3>{{ record.taskName }}</h3>
                    <el-tag :type="getRecordStatusType(record.status)">
                      {{ getRecordStatusText(record.status) }}
                    </el-tag>
                  </div>
                  <p class="record-desc">{{ record.description || '未填写文字描述' }}</p>
                  <div class="record-meta">
                    <span>+{{ record.points }} 积分</span>
                    <span>{{ formatDate(record.createdAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="积分商城" name="mall">
            <el-empty v-if="mallItems.length === 0" description="暂无可兑换商品" />
            <div v-else class="mall-grid">
              <div v-for="item in mallItems" :key="item.id" class="mall-card">
                <div class="mall-info">
                  <h3>{{ item.name }}</h3>
                  <p class="desc">{{ item.description }}</p>
                  <div class="mall-meta">
                    <span class="cost">{{ item.pointsRequired }} 积分</span>
                    <span class="stock">仅剩 {{ item.stock }} 件</span>
                  </div>
                  <el-button
                    type="primary"
                    class="exchange-btn"
                    :disabled="item.stock <= 0 || userPoints < item.pointsRequired"
                    @click="handleExchange(item)"
                  >
                    {{ userPoints < item.pointsRequired ? '积分不足' : '立即兑换' }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="积分明细" name="history">
            <el-empty v-if="history.length === 0" description="暂无积分明细" />
            <el-timeline v-else class="history-timeline">
              <el-timeline-item
                v-for="record in history"
                :key="record.id"
                :type="record.changeAmount > 0 ? 'success' : 'warning'"
                :timestamp="formatDate(record.createdAt)"
                placement="top"
              >
                <el-card>
                  <h4>{{ record.description }}</h4>
                  <p :class="record.changeAmount > 0 ? 'plus' : 'minus'">
                    {{ record.changeAmount > 0 ? '+' : '' }}{{ record.changeAmount }}
                  </p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <el-dialog
      v-model="checkinDialogVisible"
      title="提交打卡"
      width="540px"
      @closed="resetCheckinForm"
    >
      <el-form label-position="top">
        <el-form-item label="打卡任务">
          <div class="dialog-task">
            <strong>{{ currentTask?.name }}</strong>
            <el-tag type="warning">+{{ currentTask?.points || 0 }} 积分</el-tag>
          </div>
        </el-form-item>

        <el-form-item label="打卡照片" required>
          <el-upload
            drag
            action="#"
            :auto-upload="false"
            :limit="1"
            accept="image/*"
            :file-list="uploadFileList"
            :on-change="handlePhotoChange"
            :on-remove="handlePhotoRemove"
            :on-exceed="handlePhotoExceed"
            class="upload-box"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">点击选择或拖拽本地图片到这里</div>
            <template #tip>
              <div class="el-upload__tip">支持 jpg、jpeg、png、gif、webp，最大 10MB</div>
            </template>
          </el-upload>
          <div v-if="previewUrl" class="preview-box">
            <img :src="previewUrl" alt="打卡照片预览" />
          </div>
        </el-form-item>

        <el-form-item label="文字描述">
          <el-input
            v-model="checkinForm.description"
            type="textarea"
            :rows="4"
            maxlength="1000"
            show-word-limit
            placeholder="记录现场感受、打卡地点或音乐节体验"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="checkinDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submittingCheckin" @click="submitCheckin">
          提交打卡
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile, UploadFiles, UploadProps, UploadUserFile } from 'element-plus'
import { ArrowLeft, Coin, UploadFilled } from '@element-plus/icons-vue'
import { pointsApi, type CheckinRecord, type CheckinTask } from '@/api/points'

interface MallItem {
  id: number
  name: string
  description: string
  pointsRequired: number
  stock: number
  image?: string
}

interface PointsHistory {
  id: number
  changeAmount: number
  description: string
  createdAt: string
}

const router = useRouter()
const activeTab = ref('tasks')
const userPoints = ref(0)
const tasks = ref<CheckinTask[]>([])
const mallItems = ref<MallItem[]>([])
const history = ref<PointsHistory[]>([])
const checkinRecords = ref<CheckinRecord[]>([])

const checkinDialogVisible = ref(false)
const submittingCheckin = ref(false)
const currentTask = ref<CheckinTask | null>(null)
const selectedPhoto = ref<File | null>(null)
const previewUrl = ref('')
const uploadFileList = ref<UploadUserFile[]>([])
const checkinForm = reactive({
  description: ''
})

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
const serverBaseUrl = apiBaseUrl.replace(/\/api\/?$/, '')

const handleBackToHome = () => {
  router.push('/')
}

const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getCheckinImageUrl = (photo: string) => {
  if (!photo) return ''
  if (/^https?:\/\//.test(photo)) return photo
  if (photo.startsWith('/images/')) return `${serverBaseUrl}${photo}`
  return `${serverBaseUrl}/images/${photo}`
}

const loadBalance = async () => {
  try {
    const res: any = await pointsApi.getUserPoints()
    if (res.code === 200) userPoints.value = res.data || 0
  } catch (error) {
    console.error('获取积分余额失败', error)
  }
}

const loadTasks = async () => {
  try {
    const res: any = await pointsApi.getTasks()
    tasks.value = res.code === 200 ? res.data || [] : []
  } catch (error) {
    console.error('获取打卡任务失败', error)
    tasks.value = []
  }
}

const loadMallItems = async () => {
  try {
    const res: any = await pointsApi.getMallItems()
    mallItems.value = res.code === 200 ? res.data || [] : []
  } catch (error) {
    console.error('获取积分商城失败', error)
    mallItems.value = []
  }
}

const loadHistory = async () => {
  try {
    const res: any = await pointsApi.getHistory()
    history.value = res.code === 200 ? res.data || [] : []
  } catch (error) {
    console.error('获取积分明细失败', error)
    history.value = []
  }
}

const loadCheckinRecords = async () => {
  try {
    const res: any = await pointsApi.getCheckinRecords()
    checkinRecords.value = res.code === 200 ? res.data || [] : []
  } catch (error) {
    console.error('获取打卡记录失败', error)
    checkinRecords.value = []
  }
}

const loadData = async () => {
  await Promise.all([
    loadBalance(),
    loadTasks(),
    loadMallItems(),
    loadHistory(),
    loadCheckinRecords()
  ])
}

const openCheckinDialog = (task: CheckinTask) => {
  resetCheckinForm()
  currentTask.value = task
  checkinDialogVisible.value = true
}

const revokePreviewUrl = () => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = ''
  }
}

const clearPhoto = () => {
  selectedPhoto.value = null
  uploadFileList.value = []
  revokePreviewUrl()
}

const handlePhotoChange: UploadProps['onChange'] = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
  const rawFile = uploadFile.raw
  if (!rawFile) return

  if (!rawFile.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    clearPhoto()
    return
  }

  if (rawFile.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 10MB')
    clearPhoto()
    return
  }

  revokePreviewUrl()
  selectedPhoto.value = rawFile
  previewUrl.value = URL.createObjectURL(rawFile)
  uploadFileList.value = uploadFiles.slice(-1)
}

const handlePhotoRemove: UploadProps['onRemove'] = () => {
  clearPhoto()
}

const handlePhotoExceed: UploadProps['onExceed'] = () => {
  ElMessage.warning('只能上传一张打卡照片')
}

const resetCheckinForm = () => {
  currentTask.value = null
  checkinForm.description = ''
  clearPhoto()
  submittingCheckin.value = false
}

const submitCheckin = async () => {
  if (!currentTask.value) return
  if (!selectedPhoto.value) {
    ElMessage.warning('请先选择打卡照片')
    return
  }

  const formData = new FormData()
  formData.append('photo', selectedPhoto.value)
  formData.append('description', checkinForm.description)

  submittingCheckin.value = true
  try {
    const res: any = await pointsApi.checkin(currentTask.value.id, formData)
    if (res.code === 200) {
      ElMessage.success(`打卡成功，获得 ${currentTask.value.points} 积分`)
      checkinDialogVisible.value = false
      activeTab.value = 'records'
      await loadData()
    } else {
      ElMessage.error(res.message || '打卡失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '打卡过程中出现错误')
  } finally {
    submittingCheckin.value = false
  }
}

const handleExchange = (item: MallItem) => {
  ElMessageBox.prompt('请输入收货地址及联系方式（虚拟物品填无）', '积分兑换', {
    confirmButtonText: '确认兑换',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    try {
      const res: any = await pointsApi.exchangeItem(item.id, value || '虚拟物品无需地址')
      if (res.code === 200) {
        ElMessage.success('兑换成功，系统已扣除相应积分')
        await loadData()
      } else {
        ElMessage.error(res.message || '兑换失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '兑换过程中出现错误')
    }
  }).catch(() => {})
}

const getRecordStatusType = (status: string) => {
  const map: Record<string, 'warning' | 'success' | 'danger' | 'info'> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

const getRecordStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return map[status] || status
}

onMounted(() => {
  loadData()
})

onBeforeUnmount(() => {
  revokePreviewUrl()
})
</script>

<style scoped>
.points-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.points-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: white;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.points-header h1 {
  color: #333;
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.user-points {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #666;
  white-space: nowrap;
}

.points-num {
  font-size: 24px;
  font-weight: bold;
  color: #e74c3c;
}

.points-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.tabs-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.task-grid,
.record-grid,
.mall-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.task-card,
.record-card,
.mall-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.2s, transform 0.2s;
}

.task-card {
  padding: 20px;
  border-left: 4px solid #409eff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.task-card:hover,
.record-card:hover,
.mall-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.task-title-row,
.record-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.task-info h3,
.record-info h3,
.mall-info h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
  font-weight: bold;
  line-height: 1.4;
}

.task-info p {
  color: #666;
  font-size: 13px;
  line-height: 1.6;
  margin: 0 0 15px 0;
}

.task-reward {
  margin-bottom: 15px;
}

.task-action {
  text-align: right;
}

.record-image {
  width: 100%;
  height: 190px;
  display: block;
  background: #f5f7fa;
}

.image-error {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 13px;
}

.record-info,
.mall-info {
  padding: 16px;
}

.record-desc {
  color: #666;
  font-size: 13px;
  line-height: 1.6;
  min-height: 42px;
  margin: 0 0 12px 0;
  word-break: break-word;
}

.record-meta,
.mall-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  color: #909399;
  font-size: 12px;
}

.record-meta span:first-child,
.mall-meta .cost {
  color: #e74c3c;
  font-weight: bold;
}

.mall-info .desc {
  color: #777;
  font-size: 13px;
  line-height: 1.6;
  min-height: 42px;
  margin: 0 0 15px 0;
}

.mall-meta {
  margin-bottom: 15px;
}

.exchange-btn {
  width: 100%;
}

.history-timeline {
  padding: 20px;
  max-width: 640px;
  margin: 0 auto;
}

.history-timeline h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #333;
}

.history-timeline .plus {
  color: #67c23a;
  font-weight: bold;
  font-size: 16px;
  margin: 0;
}

.history-timeline .minus {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
  margin: 0;
}

.dialog-task {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.upload-box {
  width: 100%;
}

.preview-box {
  margin-top: 12px;
  width: 100%;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
}

.preview-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

@media (max-width: 768px) {
  .header-content {
    align-items: flex-start;
    flex-direction: column;
  }

  .points-header h1 {
    font-size: 20px;
  }

  .user-points {
    font-size: 14px;
  }

  .points-num {
    font-size: 20px;
  }

  .points-content {
    padding: 20px 12px;
  }

  .tabs-section {
    padding: 14px;
  }

  .task-grid,
  .record-grid,
  .mall-grid {
    grid-template-columns: 1fr;
  }
}
</style>
