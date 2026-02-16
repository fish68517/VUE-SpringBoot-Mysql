<template>
  <AdminLayout>
    <div class="fund-proof-audit-page">
    <div class="page-header">
      <h1>资金审核</h1>
      <p class="subtitle">审核待审核的资金使用证明</p>
    </div>

    <div class="audit-container">
      <!-- Pending Fund Proofs List -->
      <div class="fund-proofs-list-card">
        <div class="list-header">
          <h2>待审核资金证明列表</h2>
          <span class="count-badge">{{ totalCount }}</span>
        </div>

        <div v-if="loading" class="loading">
          <p>加载资金证明中...</p>
        </div>

        <div v-else-if="fundProofs.length === 0" class="empty-state">
          <p>暂无待审核资金证明</p>
        </div>

        <div v-else class="fund-proofs-list">
          <div v-for="fundProof in fundProofs" :key="fundProof.id" class="fund-proof-item">
            <!-- Fund Proof Header -->
            <div class="fund-proof-header">
              <div class="fund-proof-info">
                <h3 class="fund-proof-title">资金证明 #{{ fundProof.id }}</h3>
                <p class="fund-proof-meta">
                  <span class="meta-item">
                    <strong>活动ID：</strong> {{ fundProof.activityId }}
                  </span>
                  <span class="meta-item">
                    <strong>组织者ID：</strong> {{ fundProof.organizerId }}
                  </span>
                  <span class="meta-item">
                    <strong>提交时间：</strong> {{ formatDate(fundProof.createdAt) }}
                  </span>
                </p>
              </div>
              <div class="fund-proof-status">
                <span class="status-badge pending">待审核</span>
              </div>
            </div>

            <!-- Fund Proof Details -->
            <div class="fund-proof-details">
              <div class="detail-row">
                <span class="detail-label">证明文件：</span>
                <span class="detail-value">
                  <a :href="fundProof.fileUrl" target="_blank" class="file-link">
                    查看文件
                  </a>
                </span>
              </div>
              <div class="detail-row">
                <span class="detail-label">使用说明：</span>
                <span class="detail-value description">{{ fundProof.description || '无说明' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">提交时间：</span>
                <span class="detail-value">{{ formatDateTime(fundProof.createdAt) }}</span>
              </div>
            </div>

            <!-- Audit Actions -->
            <div class="audit-actions">
              <button
                class="btn-approve"
                @click="openApproveDialog(fundProof)"
                :disabled="submittingId === fundProof.id"
              >
                {{ submittingId === fundProof.id ? '处理中...' : '通过' }}
              </button>
              <button
                class="btn-reject"
                @click="openRejectDialog(fundProof)"
                :disabled="submittingId === fundProof.id"
              >
                {{ submittingId === fundProof.id ? '处理中...' : '驳回' }}
              </button>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button
            v-for="page in totalPages"
            :key="page"
            class="page-btn"
            :class="{ active: currentPage === page }"
            @click="loadFundProofs(page - 1)"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>

    <!-- Approve Dialog -->
    <div v-if="showApproveDialog" class="dialog-overlay" @click="closeApproveDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>确认通过审核</h3>
          <button class="close-btn" @click="closeApproveDialog">×</button>
        </div>
        <div class="dialog-body">
          <p>确定要通过审核资金证明 <strong>#{{ selectedFundProof?.id }}</strong> 吗？</p>
          <p class="info-text">通过后，资金使用证明将在活动详情页的"资金使用公示"中显示。</p>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeApproveDialog">取消</button>
          <button class="btn-confirm" @click="submitApprove">确认通过</button>
        </div>
      </div>
    </div>

    <!-- Reject Dialog -->
    <div v-if="showRejectDialog" class="dialog-overlay" @click="closeRejectDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>驳回审核</h3>
          <button class="close-btn" @click="closeRejectDialog">×</button>
        </div>
        <div class="dialog-body">
          <p>确定要驳回资金证明 <strong>#{{ selectedFundProof?.id }}</strong> 吗？</p>
          <div class="form-group">
            <label>驳回原因（必填）</label>
            <textarea
              v-model="rejectReason"
              placeholder="请输入驳回原因，最多500字"
              class="reject-textarea"
              rows="4"
            ></textarea>
            <span v-if="rejectReason.length > 500" class="error-text">
              驳回原因不能超过500字
            </span>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeRejectDialog">取消</button>
          <button
            class="btn-confirm"
            @click="submitReject"
            :disabled="!rejectReason.trim() || rejectReason.length > 500"
          >
            确认驳回
          </button>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../utils/http'
import AdminLayout from '../components/AdminLayout.vue'

const fundProofs = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const totalCount = ref(0)
const submittingId = ref(null)

const showApproveDialog = ref(false)
const showRejectDialog = ref(false)
const selectedFundProof = ref(null)
const rejectReason = ref('')

// Auto-refresh
let refreshInterval = null

/**
 * Load pending audit fund proofs
 */
const loadFundProofs = async (page = 0) => {
  loading.value = true
  try {
    const response = await http.get('/audit/crowdfunding', {
      params: {
        page,
        size: pageSize.value
      }
    })
    fundProofs.value = response.data.content || []
    totalPages.value = response.data.totalPages || 1
    totalCount.value = response.data.totalElements || 0
    currentPage.value = page + 1
  } catch (error) {
    console.error('Failed to load fund proofs:', error)
    ElMessage.error('加载待审核资金证明失败')
  } finally {
    loading.value = false
  }
}

/**
 * Open approve dialog
 */
const openApproveDialog = (fundProof) => {
  selectedFundProof.value = fundProof
  showApproveDialog.value = true
}

/**
 * Close approve dialog
 */
const closeApproveDialog = () => {
  showApproveDialog.value = false
  selectedFundProof.value = null
}

/**
 * Submit approve
 */
const submitApprove = async () => {
  if (!selectedFundProof.value) return

  submittingId.value = selectedFundProof.value.id

  try {
    const response = await http.put(`/audit/crowdfunding/${selectedFundProof.value.id}`, {
      approved: true,
      reason: null
    })

    // Remove approved fund proof from list
    fundProofs.value = fundProofs.value.filter(f => f.id !== selectedFundProof.value.id)
    totalCount.value--

    ElMessage.success('资金证明已通过审核')
    closeApproveDialog()
  } catch (error) {
    ElMessage.error(error.message || '审核失败')
  } finally {
    submittingId.value = null
  }
}

/**
 * Open reject dialog
 */
const openRejectDialog = (fundProof) => {
  selectedFundProof.value = fundProof
  rejectReason.value = ''
  showRejectDialog.value = true
}

/**
 * Close reject dialog
 */
const closeRejectDialog = () => {
  showRejectDialog.value = false
  selectedFundProof.value = null
  rejectReason.value = ''
}

/**
 * Submit reject
 */
const submitReject = async () => {
  if (!selectedFundProof.value || !rejectReason.value.trim()) {
    ElMessage.error('请输入驳回原因')
    return
  }

  if (rejectReason.value.length > 500) {
    ElMessage.error('驳回原因不能超过500字')
    return
  }

  submittingId.value = selectedFundProof.value.id

  try {
    const response = await http.put(`/audit/crowdfunding/${selectedFundProof.value.id}`, {
      approved: false,
      reason: rejectReason.value.trim()
    })

    // Remove rejected fund proof from list
    fundProofs.value = fundProofs.value.filter(f => f.id !== selectedFundProof.value.id)
    totalCount.value--

    ElMessage.success('资金证明已驳回')
    closeRejectDialog()
  } catch (error) {
    ElMessage.error(error.message || '驳回失败')
  } finally {
    submittingId.value = null
  }
}

/**
 * Format date
 */
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

/**
 * Format date time
 */
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * Initialize page
 */
onMounted(() => {
  loadFundProofs()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})

/**
 * Start auto-refresh timer (every 30 seconds)
 */
const startAutoRefresh = () => {
  refreshInterval = setInterval(() => {
    loadFundProofs()
  }, 30000) // 30 seconds
}

/**
 * Stop auto-refresh timer
 */
const stopAutoRefresh = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
    refreshInterval = null
  }
}
</script>

<style scoped>
.fund-proof-audit-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.subtitle {
  color: var(--text-secondary);
  margin: 0;
  font-size: 14px;
}

.audit-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.fund-proofs-list-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.list-header h2 {
  margin: 0;
  font-size: 18px;
  color: var(--text-primary);
}

.count-badge {
  background-color: var(--primary-color);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.fund-proofs-list {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.fund-proof-item {
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 16px;
  transition: box-shadow 0.3s ease;
}

.fund-proof-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.fund-proof-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.fund-proof-info {
  flex: 1;
}

.fund-proof-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.fund-proof-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.fund-proof-status {
  display: flex;
  align-items: center;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.pending {
  background-color: #fff7e6;
  color: #d46b08;
}

.fund-proof-details {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.detail-row {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 13px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 600;
  color: var(--text-primary);
  min-width: 120px;
}

.detail-value {
  color: var(--text-primary);
  flex: 1;
  word-break: break-word;
}

.detail-value.description {
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.file-link {
  color: var(--primary-color);
  text-decoration: none;
  transition: color 0.3s ease;
}

.file-link:hover {
  color: var(--primary-light);
  text-decoration: underline;
}

.audit-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-approve,
.btn-reject {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-approve {
  background-color: #52c41a;
  color: white;
}

.btn-approve:hover:not(:disabled) {
  background-color: #45a049;
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.3);
}

.btn-reject {
  background-color: #f5222d;
  color: white;
}

.btn-reject:hover:not(:disabled) {
  background-color: #d9001b;
  box-shadow: 0 2px 8px rgba(245, 34, 45, 0.3);
}

.btn-approve:disabled,
.btn-reject:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading,
.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--text-secondary);
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid var(--border-color);
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.page-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.page-btn.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

/* Dialog Styles */
.dialog-overlay {
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

.dialog-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.dialog-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--text-primary);
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s ease;
}

.close-btn:hover {
  color: var(--text-primary);
}

.dialog-body {
  padding: 20px;
}

.dialog-body p {
  margin: 0 0 12px 0;
  color: var(--text-primary);
  font-size: 14px;
}

.info-text {
  color: var(--text-secondary);
  font-size: 12px;
}

.form-group {
  margin-top: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
}

.reject-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 13px;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.3s ease;
}

.reject-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.error-text {
  display: block;
  color: #f5222d;
  font-size: 12px;
  margin-top: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.btn-cancel,
.btn-confirm {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-cancel {
  background-color: #f0f0f0;
  color: var(--text-primary);
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-confirm {
  background-color: var(--primary-color);
  color: white;
}

.btn-confirm:hover:not(:disabled) {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .fund-proof-audit-page {
    padding: 12px;
  }

  .page-header h1 {
    font-size: 20px;
  }

  .fund-proof-header {
    flex-direction: column;
    gap: 12px;
  }

  .fund-proof-meta {
    flex-direction: column;
    gap: 8px;
  }

  .audit-actions {
    flex-direction: column;
  }

  .btn-approve,
  .btn-reject {
    width: 100%;
  }

  .dialog-content {
    width: 95%;
  }
}
</style>
