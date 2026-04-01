<template>
  <div class="admin-resources-page">
    <div class="page-header">
      <h1>资源管理</h1>
    </div>

    <!-- 标签页导航 -->
    <div class="tabs-container">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        :class="['tab-button', { active: activeTab === tab.id }]"
        @click="activeTab = tab.id"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 作品管理标签页 -->
    <div v-if="activeTab === 'artworks'" class="tab-content">
      <div class="section-header">
        <h2>作品管理</h2>
        <button class="btn btn-primary" @click="showArtworkUploadModal = true">
          <span>➕</span> 上传资源
        </button>
      </div>

      <!-- 作品列表 -->
      <div class="table-container" v-if="!showArtworkDetail">
        <table class="resources-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>作品名称</th>
              <th>分类</th>
              <th>状态</th>
              <th>浏览次数</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="artworks.length === 0">
              <td colspan="7" class="empty-message">暂无作品数据</td>
            </tr>
            <tr v-for="artwork in artworks" :key="artwork.id" class="resource-row">
              <td>{{ artwork.id }}</td>
              <td>{{ artwork.title }}</td>
              <td>{{ artwork.category }}</td>
              <td>
                <span class="status-badge" :class="`status-${artwork.status}`">
                  {{ getStatusLabel(artwork.status) }}
                </span>
              </td>
              <td>{{ artwork.viewCount }}</td>
              <td>{{ formatDate(artwork.createdAt) }}</td>
              <td class="action-cell">
                <button @click="viewArtworkDetail(artwork)" class="btn-action btn-view" title="查看">
                  👁️
                </button>
                <button @click="editArtwork(artwork)" class="btn-action btn-edit" title="编辑">
                  ✏️
                </button>
                <button v-if="artwork.status === 'draft'" @click="approveArtwork(artwork)" class="btn-action btn-approve" title="批准">
                  ✅
                </button>
                <button v-if="artwork.status === 'draft'" @click="rejectArtworkConfirm(artwork)" class="btn-action btn-reject" title="拒绝">
                  ❌
                </button>
                <button v-if="artwork.status === 'approved'" @click="offlineArtwork(artwork)" class="btn-action btn-offline" title="下架">
                  📴
                </button>
                <button @click="deleteArtworkConfirm(artwork)" class="btn-action btn-delete" title="删除">
                  🗑️
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <Pagination
          :current-page="artworkCurrentPage"
          :total-pages="artworkTotalPages"
          :total-items="artworkTotalItems"
          @update:current-page="handleArtworkPageChange"
        />
      </div>

      <!-- 作品详情视图 -->
      <div class="detail-view" v-if="showArtworkDetail">
        <button @click="showArtworkDetail = false" class="btn btn-secondary">← 返回列表</button>
        <div class="detail-card">
          <h2>作品详情</h2>
          <div class="detail-grid">
            <div class="detail-item">
              <label>作品ID:</label>
              <span>{{ selectedArtwork.id }}</span>
            </div>
            <div class="detail-item">
              <label>作品名称:</label>
              <span>{{ selectedArtwork.title }}</span>
            </div>
            <div class="detail-item">
              <label>分类:</label>
              <span>{{ selectedArtwork.category }}</span>
            </div>
            <div class="detail-item">
              <label>状态:</label>
              <span class="status-badge" :class="`status-${selectedArtwork.status}`">
                {{ getStatusLabel(selectedArtwork.status) }}
              </span>
            </div>
            <div class="detail-item">
              <label>创作者:</label>
              <span>{{ selectedArtwork.creator }}</span>
            </div>
            <div class="detail-item">
              <label>刺绣技法:</label>
              <span>{{ selectedArtwork.technique }}</span>
            </div>
            <div class="detail-item">
              <label>浏览次数:</label>
              <span>{{ selectedArtwork.viewCount }}</span>
            </div>
            <div class="detail-item">
              <label>收藏次数:</label>
              <span>{{ selectedArtwork.collectCount }}</span>
            </div>
            <div class="detail-item full-width">
              <label>描述:</label>
              <span>{{ selectedArtwork.description }}</span>
            </div>
            <div class="detail-item full-width">
              <label>图片:</label>
              <img v-if="selectedArtwork.imageUrl" :src="getImageUrl(selectedArtwork.imageUrl)" class="detail-image" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 资讯管理标签页 -->
    <div v-if="activeTab === 'news'" class="tab-content">
      <div class="section-header">
        <h2>资讯管理</h2>
        <button class="btn btn-primary" @click="showNewsPublishModal = true">
          <span>➕</span> 发布资讯
        </button>
      </div>

      <!-- 资讯列表 -->
      <div class="table-container">
        <table class="resources-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>标题</th>
              <th>分类</th>
              <th>作者</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="newsList.length === 0">
              <td colspan="6" class="empty-message">暂无资讯数据</td>
            </tr>
            <tr v-for="news in newsList" :key="news.id" class="resource-row">
              <td>{{ news.id }}</td>
              <td>{{ news.title }}</td>
              <td>{{ news.category }}</td>
              <td>{{ news.author }}</td>
              <td>{{ formatDate(news.createdAt) }}</td>
              <td class="action-cell">
                <button @click="editNews(news)" class="btn-action btn-edit" title="编辑">
                  ✏️
                </button>
                <button @click="deleteNewsConfirm(news)" class="btn-action btn-delete" title="删除">
                  🗑️
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <Pagination
          :current-page="newsCurrentPage"
          :total-pages="newsTotalPages"
          :total-items="newsTotalItems"
          @update:current-page="handleNewsPageChange"
        />
      </div>
    </div>

    <!-- 活动管理标签页 -->
    <div v-if="activeTab === 'activities'" class="tab-content">
      <div class="section-header">
        <h2>活动管理</h2>
        <button class="btn btn-primary" @click="showActivityPublishModal = true">
          <span>➕</span> 发布活动
        </button>
      </div>

      <!-- 活动列表 -->
      <div class="table-container">
        <table class="resources-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>活动名称</th>
              <th>开始时间</th>
              <th>结束时间</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="activities.length === 0">
              <td colspan="6" class="empty-message">暂无活动数据</td>
            </tr>
            <tr v-for="activity in activities" :key="activity.id" class="resource-row">
              <td>{{ activity.id }}</td>
              <td>{{ activity.title }}</td>
              <td>{{ formatDate(activity.startTime) }}</td>
              <td>{{ formatDate(activity.endTime) }}</td>
              <td>{{ formatDate(activity.createdAt) }}</td>
              <td class="action-cell">
                <button @click="editActivity(activity)" class="btn-action btn-edit" title="编辑">
                  ✏️
                </button>
                <button @click="deleteActivityConfirm(activity)" class="btn-action btn-delete" title="删除">
                  🗑️
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <Pagination
          :current-page="activityCurrentPage"
          :total-pages="activityTotalPages"
          :total-items="activityTotalItems"
          @update:current-page="handleActivityPageChange"
        />
      </div>
    </div>

    <!-- 作品上传/编辑模态框 -->
    <div class="modal-overlay" v-if="showArtworkUploadModal || showArtworkEditModal" @click.self="closeArtworkModals">
      <div class="admin-modal">
        <div class="modal-header">
          <h2>{{ showArtworkEditModal ? '编辑作品' : '上传资源' }}</h2>
          <button @click="closeArtworkModals" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>作品名称 *</label>
            <input v-model="artworkFormData.title" type="text" placeholder="输入作品名称" class="form-input" />
          </div>
          <div class="form-group">
            <label>分类 *</label>
            <select v-model="artworkFormData.category" class="form-input">
              <option value="">选择分类</option>
              <option value="日常生活类">日常生活类</option>
              <option value="节日母题类">节日母题类</option>
              <option value="针法风格类">针法风格类</option>
            </select>
          </div>
          <div class="form-group">
            <label>创作者 *</label>
            <input v-model="artworkFormData.creator" type="text" placeholder="输入创作者名称" class="form-input" />
          </div>
          <div class="form-group">
            <label>刺绣技法 *</label>
            <input v-model="artworkFormData.technique" type="text" placeholder="输入刺绣技法" class="form-input" />
          </div>
          <div class="form-group">
              <label>作品图片 *</label>
              <div class="image-upload-container">
                <input
                  type="file"
                  id="artworkImageUpload"
                  accept="image/*"
                  @change="handleImageUpload"
                  class="hidden-file-input"
                />
                
                <label for="artworkImageUpload" class="btn btn-secondary upload-btn" :class="{ disabled: isUploadingImage }">
                  {{ isUploadingImage ? '上传中...' : '选择本地图片' }}
                </label>

                <div v-if="artworkFormData.imageUrl" class="image-preview">
                  <img :src="getImageUrl(artworkFormData.imageUrl)" alt="作品预览" />
                  <button class="remove-image-btn" @click="removeImage" title="移除图片">×</button>
                </div>
              </div>
          </div>
          <div class="form-group">
            <label>描述 *</label>
            <textarea v-model="artworkFormData.description" placeholder="输入作品描述" class="form-textarea" rows="4"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeArtworkModals" class="btn btn-secondary">取消</button>
          <button @click="saveArtwork" class="btn btn-primary" :disabled="isSavingArtwork">
            {{ isSavingArtwork ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 资讯发布/编辑模态框 -->
    <div class="modal-overlay" v-if="showNewsPublishModal || showNewsEditModal" @click.self="closeNewsModals">
      <div class="admin-modal">
        <div class="modal-header">
          <h2>{{ showNewsEditModal ? '编辑资讯' : '发布资讯' }}</h2>
          <button @click="closeNewsModals" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标题 *</label>
            <input v-model="newsFormData.title" type="text" placeholder="输入资讯标题" class="form-input" />
          </div>
          <div class="form-group">
            <label>分类 *</label>
            <select v-model="newsFormData.category" class="form-input">
              <option value="">选择分类</option>
              <option value="技法知识">技法知识</option>
              <option value="历史文化">历史文化</option>
              <option value="政策法规">政策法规</option>
              <option value="常见问题">常见问题</option>
            </select>
          </div>
          <div class="form-group">
            <label>作者 *</label>
            <input v-model="newsFormData.author" type="text" placeholder="输入作者名称" class="form-input" />
          </div>
          <div class="form-group">
            <label>内容 *</label>
            <textarea v-model="newsFormData.content" placeholder="输入资讯内容" class="form-textarea" rows="6"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeNewsModals" class="btn btn-secondary">取消</button>
          <button @click="saveNews" class="btn btn-primary" :disabled="isSavingNews">
            {{ isSavingNews ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 活动发布/编辑模态框 -->
    <div class="modal-overlay" v-if="showActivityPublishModal || showActivityEditModal" @click.self="closeActivityModals">
      <div class="admin-modal">
        <div class="modal-header">
          <h2>{{ showActivityEditModal ? '编辑活动' : '发布活动' }}</h2>
          <button @click="closeActivityModals" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>活动名称 *</label>
            <input v-model="activityFormData.title" type="text" placeholder="输入活动名称" class="form-input" />
          </div>
          <div class="form-group">
            <label>描述 *</label>
            <textarea v-model="activityFormData.description" placeholder="输入活动描述" class="form-textarea" rows="4"></textarea>
          </div>
          <div class="form-group">
            <label>开始时间 *</label>
            <input v-model="activityFormData.startTime" type="datetime-local" class="form-input" />
          </div>
          <div class="form-group">
            <label>结束时间 *</label>
            <input v-model="activityFormData.endTime" type="datetime-local" class="form-input" />
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeActivityModals" class="btn btn-secondary">取消</button>
          <button @click="saveActivity" class="btn btn-primary" :disabled="isSavingActivity">
            {{ isSavingActivity ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认模态框 -->
    <div class="modal-overlay" v-if="showDeleteConfirm" @click.self="showDeleteConfirm = false">
      <div class="admin-modal admin-modal--sm">
        <div class="modal-header">
          <h2>确认删除</h2>
          <button @click="showDeleteConfirm = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <p>确定要删除 <strong>{{ itemToDelete.title || itemToDelete.name }}</strong> 吗？此操作不可撤销。</p>
        </div>
        <div class="modal-footer">
          <button @click="showDeleteConfirm = false" class="btn btn-secondary">取消</button>
          <button @click="confirmDelete" class="btn btn-danger" :disabled="isDeleting">
            {{ isDeleting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 拒绝作品模态框 -->
    <div class="modal-overlay" v-if="showRejectModal" @click.self="showRejectModal = false">
      <div class="admin-modal">
        <div class="modal-header">
          <h2>拒绝作品</h2>
          <button @click="showRejectModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>拒绝原因 *</label>
            <textarea v-model="rejectReason" placeholder="输入拒绝原因" class="form-textarea" rows="4"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showRejectModal = false" class="btn btn-secondary">取消</button>
          <button @click="confirmReject" class="btn btn-danger" :disabled="isRejecting">
            {{ isRejecting ? '处理中...' : '确认拒绝' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Toast 通知 -->
    <Toast ref="toast" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { AdminResourceService } from '../../services'
import Pagination from '../../components/Pagination.vue'
import Toast from '../../components/Toast.vue'


// 在 <script setup> 靠上的位置（定义变量的地方）加上这个：
const isUploadingImage = ref(false)

// 获取后端基础路径 (根据你的环境配置)
const BASE_URL = 'http://localhost:8080';

// 图片地址拼接函数，用于回显图片
const getImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `${BASE_URL}${path}`;
}

// ========================
// 在下方的方法区加上以下三个函数：
// ========================

// 1. 处理文件上传
const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证格式和大小 (比如限制 5MB)
  if (!file.type.startsWith('image/')) {
    toast.value.warning('请选择图片文件')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    toast.value.warning('图片大小不能超过 5MB')
    return
  }

  const formData = new FormData()
  formData.append('file', file)

  isUploadingImage.value = true
  try {
    // 调用后端上传接口
    const response = await fetch(`${BASE_URL}/api/upload/image`, {
      method: 'POST',
      body: formData
    })
    
    const res = await response.json()
    if (res.code === 200) {
      // 这里的 res.data 就是后端返回的 "/images/upload/xxxx.jpg"
      // 把它赋值给表单，这样点"保存"时就会存入数据库
      artworkFormData.value.imageUrl = res.data
      toast.value.success('图片上传成功')
    } else {
      toast.value.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传图片错误:', error)
    toast.value.error('网络或服务器错误')
  } finally {
    isUploadingImage.value = false
    // 清空 input file，允许用户删掉图片后重复上传同一张图片
    event.target.value = '' 
  }
}

// 2. 移除已上传的图片
const removeImage = () => {
  artworkFormData.value.imageUrl = ''
}



// 标签页
const tabs = [
  { id: 'artworks', label: '作品管理' },
  { id: 'news', label: '资讯管理' },
  { id: 'activities', label: '活动管理' }
]

const activeTab = ref('artworks')
const toast = ref(null)

// ===== 作品管理状态 =====
const artworks = ref([])
const artworkCurrentPage = ref(1)
const artworkPageSize = ref(10)
const artworkTotalItems = ref(0)
const selectedArtwork = ref(null)
const showArtworkDetail = ref(false)
const showArtworkUploadModal = ref(false)
const showArtworkEditModal = ref(false)
const isSavingArtwork = ref(false)

const artworkFormData = ref({
  title: '',
  category: '',
  creator: '',
  technique: '',
  imageUrl: '',
  description: ''
})

// ===== 资讯管理状态 =====
const newsList = ref([])
const newsCurrentPage = ref(1)
const newsPageSize = ref(10)
const newsTotalItems = ref(0)
const showNewsPublishModal = ref(false)
const showNewsEditModal = ref(false)
const isSavingNews = ref(false)

const newsFormData = ref({
  title: '',
  category: '',
  author: '',
  content: ''
})

// ===== 活动管理状态 =====
const activities = ref([])
const activityCurrentPage = ref(1)
const activityPageSize = ref(10)
const activityTotalItems = ref(0)
const showActivityPublishModal = ref(false)
const showActivityEditModal = ref(false)
const isSavingActivity = ref(false)

const activityFormData = ref({
  title: '',
  description: '',
  startTime: '',
  endTime: ''
})

// ===== 通用状态 =====
const showDeleteConfirm = ref(false)
const showRejectModal = ref(false)
const itemToDelete = ref(null)
const deleteType = ref(null)
const isDeleting = ref(false)
const isRejecting = ref(false)
const rejectReason = ref('')
const artworkToReject = ref(null)

// 计算属性
const artworkTotalPages = computed(() => Math.ceil(artworkTotalItems.value / artworkPageSize.value))
const newsTotalPages = computed(() => Math.ceil(newsTotalItems.value / newsPageSize.value))
const activityTotalPages = computed(() => Math.ceil(activityTotalItems.value / activityPageSize.value))

// 工具方法
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const getStatusLabel = (status) => {
  const labels = {
    draft: '草稿',
    approved: '已批准',
    rejected: '已拒绝',
    offline: '已下架'
  }
  return labels[status] || status
}

// ===== 作品管理方法 =====
const loadArtworks = async () => {
  try {
    const params = {
      pageNum: artworkCurrentPage.value,
      pageSize: artworkPageSize.value
    }
    const response = await AdminResourceService.getArtworks?.(params)
    if (response && response.list) {
      artworks.value = response.list || []
      artworkTotalItems.value = response.total || 0
    }
  } catch (error) {
    console.error('加载作品列表错误:', error)
    toast.value.error('加载作品列表失败')
  }
}

const handleArtworkPageChange = (page) => {
  artworkCurrentPage.value = page
  loadArtworks()
}

const viewArtworkDetail = (artwork) => {
  selectedArtwork.value = artwork
  showArtworkDetail.value = true
}

const editArtwork = (artwork) => {
  selectedArtwork.value = artwork
  artworkFormData.value = {
    title: artwork.title,
    category: artwork.category,
    creator: artwork.creator,
    technique: artwork.technique,
    imageUrl: artwork.imageUrl,
    description: artwork.description
  }
  showArtworkEditModal.value = true
}

const saveArtwork = async () => {
  if (!artworkFormData.value.title || !artworkFormData.value.category) {
    toast.value.warning('请填写必填项')
    return
  }

  isSavingArtwork.value = true
  try {
    let response
    if (showArtworkEditModal.value) {
      response = await AdminResourceService.updateArtwork(selectedArtwork.value.id, artworkFormData.value)
    } else {
      response = await AdminResourceService.uploadArtwork(artworkFormData.value)
    }

    if (response) {
      toast.value.success(showArtworkEditModal.value ? '作品更新成功' : '作品上传成功')
      closeArtworkModals()
      loadArtworks()
    } else {
      toast.value.error('操作失败')
    }
  } catch (error) {
    console.error('保存作品错误:', error)
    toast.value.error('保存作品失败')
  } finally {
    isSavingArtwork.value = false
  }
}

const approveArtwork = async (artwork) => {
  try {
    const response = await AdminResourceService.approveArtwork(artwork.id)
    if (response) {
      toast.value.success('作品已批准')
      loadArtworks()
    } else {
      toast.value.error('批准失败')
    }
  } catch (error) {
    console.error('批准作品错误:', error)
    toast.value.error('批准作品失败')
  }
}

const rejectArtworkConfirm = (artwork) => {
  artworkToReject.value = artwork
  rejectReason.value = ''
  showRejectModal.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value) {
    toast.value.warning('请输入拒绝原因')
    return
  }

  isRejecting.value = true
  try {
    const response = await AdminResourceService.rejectArtwork(artworkToReject.value.id, {
      reason: rejectReason.value
    })
    if (response) {
      toast.value.success('作品已拒绝')
      showRejectModal.value = false
      loadArtworks()
    } else {
      toast.value.error('拒绝失败')
    }
  } catch (error) {
    console.error('拒绝作品错误:', error)
    toast.value.error('拒绝作品失败')
  } finally {
    isRejecting.value = false
  }
}

const offlineArtwork = async (artwork) => {
  try {
    const response = await AdminResourceService.offlineArtwork(artwork.id)
    if (response) {
      toast.value.success('作品已下架')
      loadArtworks()
    } else {
      toast.value.error('下架失败')
    }
  } catch (error) {
    console.error('下架作品错误:', error)
    toast.value.error('下架作品失败')
  }
}

const deleteArtworkConfirm = (artwork) => {
  itemToDelete.value = artwork
  deleteType.value = 'artwork'
  showDeleteConfirm.value = true
}

const closeArtworkModals = () => {
  showArtworkUploadModal.value = false
  showArtworkEditModal.value = false
  artworkFormData.value = {
    title: '',
    category: '',
    creator: '',
    technique: '',
    imageUrl: '',
    description: ''
  }
}

// ===== 资讯管理方法 =====
const loadNews = async () => {
  try {
    const params = {
      pageNum: newsCurrentPage.value,
      pageSize: newsPageSize.value
    }
    const response = await AdminResourceService.getNews?.(params)
    if (response && response.list) {
      newsList.value = response.list || []
      newsTotalItems.value = response.total || 0
    }
  } catch (error) {
    console.error('加载资讯列表错误:', error)
    toast.value.error('加载资讯列表失败')
  }
}

const handleNewsPageChange = (page) => {
  newsCurrentPage.value = page
  loadNews()
}

// 修改 3：编辑资讯时携带 ID
const editNews = (news) => {
  newsFormData.value = {
    id: news.id, // 👈 带上 ID
    title: news.title,
    category: news.category,
    author: news.author,
    content: news.content
  }
  showNewsEditModal.value = true
}



const deleteNewsConfirm = (news) => {
  itemToDelete.value = news
  deleteType.value = 'news'
  showDeleteConfirm.value = true
}



// 修改 4：修复 saveNews 里的恐怖 Bug
const saveNews = async () => {
  if (!newsFormData.value.title || !newsFormData.value.category) {
    toast.value.warning('请填写必填项')
    return
  }

  isSavingNews.value = true
  try {
    let response
    if (showNewsEditModal.value) {
      // 🚨 这里的 ID 原来写错了，现在改成了正确的 newsFormData.value.id
      response = await AdminResourceService.updateNews(newsFormData.value.id, newsFormData.value)
    } else {
      response = await AdminResourceService.publishNews(newsFormData.value)
    }

    if (response) {
      toast.value.success(showNewsEditModal.value ? '资讯更新成功' : '资讯发布成功')
      closeNewsModals()
      loadNews()
    } else {
      toast.value.error('操作失败')
    }
  } catch (error) {
    console.error('保存资讯错误:', error)
    toast.value.error('保存资讯失败')
  } finally {
    isSavingNews.value = false
  }
}

// 修改 5：关闭资讯弹窗时清空 ID
const closeNewsModals = () => {
  showNewsPublishModal.value = false
  showNewsEditModal.value = false
  newsFormData.value = {
    id: null, // 👈 清空
    title: '',
    category: '',
    author: '',
    content: ''
  }
}

// ===== 活动管理方法 =====
const loadActivities = async () => {
  try {
    const params = {
      pageNum: activityCurrentPage.value,
      pageSize: activityPageSize.value
    }
    const response = await AdminResourceService.getActivities?.(params)
    if (response && response.list) {
      activities.value = response.list || []
      activityTotalItems.value = response.total || 0
    }
  } catch (error) {
    console.error('加载活动列表错误:', error)
    toast.value.error('加载活动列表失败')
  }
}

const handleActivityPageChange = (page) => {
  activityCurrentPage.value = page
  loadActivities()
}

// 修改 1：点击编辑活动时，携带 ID
const editActivity = (activity) => {
  activityFormData.value = {
    id: activity.id, // 👈 必须把 id 带上，否则后端不知道更新谁！
    title: activity.title,
    description: activity.description,
    startTime: activity.startTime,
    endTime: activity.endTime
  }
  showActivityEditModal.value = true
}

const saveActivity = async () => {
  if (!activityFormData.value.title || !activityFormData.value.startTime) {
    toast.value.warning('请填写必填项')
    return
  }

  isSavingActivity.value = true
  // 打印 activityFormData 的值，检查日期格式
  console.log('保存活动 - 表单数据:', activityFormData.value)
  try {
    let response
    if (showActivityEditModal.value) {
      response = await AdminResourceService.updateActivity(activityFormData.value.id, activityFormData.value)
    } else {
      response = await AdminResourceService.publishActivity(activityFormData.value)
    }

    if (response) {
      toast.value.success(showActivityEditModal.value ? '活动更新成功' : '活动发布成功')
      closeActivityModals()
      loadActivities()
    } else {
      toast.value.error('操作失败')
    }
  } catch (error) {
    console.error('保存活动错误:', error)
    toast.value.error('保存活动失败')
  } finally {
    isSavingActivity.value = false
  }
}

const deleteActivityConfirm = (activity) => {
  itemToDelete.value = activity
  deleteType.value = 'activity'
  showDeleteConfirm.value = true
}

// 修改 2：关闭弹窗时，把 ID 清空，防止污染下一个“发布活动”操作
const closeActivityModals = () => {
  showActivityPublishModal.value = false
  showActivityEditModal.value = false
  activityFormData.value = {
    id: null, // 👈 增加清空 id
    title: '',
    description: '',
    startTime: '',
    endTime: ''
  }
}

// ===== 通用删除方法 =====
const confirmDelete = async () => {
  isDeleting.value = true
  try {
    let response
    if (deleteType.value === 'artwork') {
      response = await AdminResourceService.deleteArtwork(itemToDelete.value.id)
    } else if (deleteType.value === 'news') {
      response = await AdminResourceService.deleteNews(itemToDelete.value.id)
    } else if (deleteType.value === 'activity') {
      response = await AdminResourceService.deleteActivity(itemToDelete.value.id)
    }

    if (true) {
      toast.value.success('删除成功')
      showDeleteConfirm.value = false
      if (deleteType.value === 'artwork') loadArtworks()
      else if (deleteType.value === 'news') loadNews()
      else if (deleteType.value === 'activity') loadActivities()
    } else {
      toast.value.error('删除失败')
    }
  } catch (error) {
    console.error('删除错误:', error)
    toast.value.error('删除失败')
  } finally {
    isDeleting.value = false
  }
}

// 生命周期
onMounted(() => {
  loadArtworks()
  loadNews()
  loadActivities()
})
</script>

<style scoped>

/* 图片上传区域样式 */
.hidden-file-input {
  display: none;
}

.image-upload-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xs);
}

.upload-btn {
  width: fit-content;
  cursor: pointer;
  display: inline-block;
  text-align: center;
}

.upload-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

.image-preview {
  position: relative;
  width: fit-content;
  border: 1px dashed var(--border-color);
  border-radius: var(--border-radius-md);
  padding: 4px;
  background-color: white;
}

.image-preview img {
  max-width: 250px;
  max-height: 200px;
  object-fit: cover;
  border-radius: var(--border-radius-sm);
  display: block;
}

.remove-image-btn {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 24px;
  height: 24px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
  transition: transform 0.2s ease;
}

.remove-image-btn:hover {
  transform: scale(1.1);
  background-color: #c0392b;
}

.admin-resources-page {
  padding: var(--spacing-lg);
  background-color: #f4f7fb;
  min-height: 100vh;
  color: #24425c;
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  color: #1d527c;
  margin: 0;
  font-size: 32px;
  letter-spacing: 0.02em;
}

/* 标签页 */
.tabs-container {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  border-bottom: 2px solid var(--border-color);
}

.tab-button {
  padding: var(--spacing-md) var(--spacing-lg);
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  font-size: 15px;
  font-weight: 700;
  color: #5a7087;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-button:hover {
  color: #1d527c;
}

.tab-button.active {
  color: #1d527c;
  border-bottom-color: #1d527c;
}

.tab-content {
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.section-header h2 {
  color: #1d527c;
  margin: 0;
  font-size: 24px;
}

/* 表格 */
.table-container {
  background-color: #ffffff;
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  margin-bottom: var(--spacing-lg);
}

.resources-table {
  width: 100%;
  border-collapse: collapse;
}

.resources-table thead {
  background-color: #1d527c;
  color: white;
}

.resources-table th {
  padding: var(--spacing-md);
  text-align: left;
  font-weight: 700;
  font-size: 15px;
  border-bottom: 2px solid var(--border-color);
}

.resources-table td {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
  font-size: 15px;
}

.resource-row:hover {
  background-color: var(--bg-secondary);
}

.empty-message {
  text-align: center;
  color: var(--text-secondary);
  padding: var(--spacing-lg) !important;
}

.status-badge {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  font-weight: 600;
}

.status-draft {
  background-color: #95a5a6;
  color: white;
}

.status-approved {
  background-color: #27ae60;
  color: white;
}

.status-rejected {
  background-color: #e74c3c;
  color: white;
}

.status-offline {
  background-color: #f39c12;
  color: white;
}

.action-cell {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.btn-action {
  background: none;
  border: none;
  font-size: var(--font-size-lg);
  cursor: pointer;
  padding: var(--spacing-xs);
  transition: transform 0.2s ease;
}

.btn-action:hover {
  transform: scale(1.2);
}

.btn-view {
  color: #0066cc;
}

.btn-edit {
  color: #ffa500;
}

.btn-approve {
  color: #27ae60;
}

.btn-reject {
  color: #e74c3c;
}

.btn-offline {
  color: #f39c12;
}

.btn-delete {
  color: #e74c3c;
}

/* 详情视图 */
.detail-view {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
}

.detail-card {
  margin-top: var(--spacing-lg);
}

.detail-card h2 {
  color: #1d527c;
  margin-bottom: var(--spacing-lg);
  font-size: 24px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-item label {
  font-weight: 600;
  color: var(--text-primary);
}

.detail-item span {
  color: var(--text-secondary);
  padding: var(--spacing-md);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  word-break: break-word;
}

.detail-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: var(--border-radius-md);
}

/* 按钮 */
.btn {
  padding: var(--spacing-md) var(--spacing-lg);
  border: none;
  border-radius: var(--border-radius-md);
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.btn-primary {
  background-color: #1d527c;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #153f63;
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover:not(:disabled) {
  background-color: var(--border-color);
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);

  /* ✅ 居中 */
  display: flex;
  align-items: center;
  justify-content: center;

  z-index: 999999; /* 提高层级，防止被侧边栏/头部盖住 */
}

/* ✅ 避免全局 .modal 冲突：使用 admin-modal */
.admin-modal {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);

  width: min(600px, calc(100vw - 48px));
  height: auto;          /* ✅ 高度自适应内容 */
  min-height: unset;     /* ✅ 清掉全局 min-height */
  max-height: calc(100vh - 48px); /* ✅ 防止过高 */
  overflow: auto;        /* ✅ 内容多时滚动 */
}

/* 小弹窗：删除确认等 */
.admin-modal--sm {
  width: min(420px, calc(100vw - 48px));
}

/* 删除确认弹窗：内容少时更紧凑（可选） */
.admin-modal--sm .modal-body {
  padding: 16px;
}
.admin-modal--sm .modal-header,
.admin-modal--sm .modal-footer {
  padding: 14px 16px;
}


.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  margin: 0;
  color: #1d527c;
  font-size: 24px;
}

.modal-close {
  background: none;
  border: none;
  font-size: var(--font-size-2xl);
  cursor: pointer;
  color: var(--text-secondary);
  transition: color 0.3s ease;
}

.modal-close:hover {
  color: var(--text-primary);
}

.modal-body {
  padding: var(--spacing-lg);
}

.modal-footer {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-weight: 700;
  font-size: 15px;
  color: #24425c;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid #c9d8e6;
  border-radius: var(--border-radius-md);
  font-size: 15px;
  background-color: #f9fbfd;
  color: #24425c;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #1d527c;
  box-shadow: 0 0 0 3px rgba(29, 82, 124, 0.12);
}

@media (max-width: 768px) {
  .admin-resources-page {
    padding: var(--spacing-md);
  }

  .tabs-container {
    flex-wrap: wrap;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .resources-table {
    font-size: var(--font-size-sm);
  }

  .resources-table th,
  .resources-table td {
    padding: var(--spacing-sm);
  }

  .action-cell {
    flex-direction: column;
  }

  .modal {
    max-width: 95%;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
