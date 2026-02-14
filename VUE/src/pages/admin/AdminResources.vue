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
          <span>➕</span> 上传作品
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
              <img v-if="selectedArtwork.imageUrl" :src="selectedArtwork.imageUrl" class="detail-image" />
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
      <div class="modal">
        <div class="modal-header">
          <h2>{{ showArtworkEditModal ? '编辑作品' : '上传作品' }}</h2>
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
            <label>图片URL *</label>
            <input v-model="artworkFormData.imageUrl" type="text" placeholder="输入图片URL" class="form-input" />
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
      <div class="modal">
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
      <div class="modal">
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
      <div class="modal modal-small">
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
      <div class="modal">
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
    if (response && response.artworks) {
      artworks.value = response.artworks || []
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
    if (response && response.news) {
      newsList.value = response.news || []
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

const editNews = (news) => {
  newsFormData.value = {
    title: news.title,
    category: news.category,
    author: news.author,
    content: news.content
  }
  showNewsEditModal.value = true
}

const saveNews = async () => {
  if (!newsFormData.value.title || !newsFormData.value.category) {
    toast.value.warning('请填写必填项')
    return
  }

  isSavingNews.value = true
  try {
    let response
    if (showNewsEditModal.value) {
      response = await AdminResourceService.updateNews(itemToDelete.value.id, newsFormData.value)
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

const deleteNewsConfirm = (news) => {
  itemToDelete.value = news
  deleteType.value = 'news'
  showDeleteConfirm.value = true
}

const closeNewsModals = () => {
  showNewsPublishModal.value = false
  showNewsEditModal.value = false
  newsFormData.value = {
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
    if (response && response.activities) {
      activities.value = response.activities || []
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

const editActivity = (activity) => {
  activityFormData.value = {
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
  try {
    let response
    if (showActivityEditModal.value) {
      response = await AdminResourceService.updateActivity(itemToDelete.value.id, activityFormData.value)
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

const closeActivityModals = () => {
  showActivityPublishModal.value = false
  showActivityEditModal.value = false
  activityFormData.value = {
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

    if (response) {
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
.admin-resources-page {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  min-height: 100vh;
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  color: var(--primary-color);
  margin: 0;
  font-size: var(--font-size-2xl);
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
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-button:hover {
  color: var(--primary-color);
}

.tab-button.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
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
  color: var(--primary-color);
  margin: 0;
  font-size: var(--font-size-xl);
}

/* 表格 */
.table-container {
  background-color: var(--bg-primary);
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
  background-color: var(--primary-color);
  color: white;
}

.resources-table th {
  padding: var(--spacing-md);
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid var(--border-color);
}

.resources-table td {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
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
  color: var(--primary-color);
  margin-bottom: var(--spacing-lg);
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
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.btn-primary {
  background-color: var(--primary-color);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--primary-dark);
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
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-small {
  max-width: 400px;
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
  color: var(--primary-color);
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
  font-weight: 600;
  color: var(--text-primary);
}

.form-input,
.form-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
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
