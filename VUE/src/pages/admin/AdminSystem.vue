<template>
  <div class="admin-system-page">
    <div class="page-header">
      <h1>系统管理</h1>
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

    <!-- 系统设置标签页 -->
    <div v-if="activeTab === 'settings'" class="tab-content">
      <div class="section-header">
        <h2>网站基础信息设置</h2>
      </div>

      <div class="settings-card">
        <div class="form-group">
          <label>网站名称 *</label>
          <input
            v-model="settingsFormData.siteName"
            type="text"
            placeholder="输入网站名称"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>网站描述 *</label>
          <textarea
            v-model="settingsFormData.siteDescription"
            placeholder="输入网站描述"
            class="form-textarea"
            rows="4"
          ></textarea>
        </div>

        <div class="form-group" v-if="false">
          <label>Logo URL *</label>
          <input
            v-model="settingsFormData.logo"
            type="text"
            placeholder="输入 Logo URL"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>联系邮箱 *</label>
          <input
            v-model="settingsFormData.contactEmail"
            type="email"
            placeholder="输入联系邮箱"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>联系电话 *</label>
          <input
            v-model="settingsFormData.contactPhone"
            type="text"
            placeholder="输入联系电话"
            class="form-input"
          />
        </div>

        <div class="form-actions">
          <button @click="loadSettings" class="btn btn-secondary">重置</button>
          <button @click="saveSettings" class="btn btn-primary" :disabled="isSavingSettings">
            {{ isSavingSettings ? '保存中...' : '保存设置' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 数据备份标签页 -->
    <div v-if="false" class="tab-content">
      <div class="section-header">
        <h2>数据备份和恢复</h2>
      </div>

      <div class="backup-container">
        <!-- 备份部分 -->
        <div class="backup-card">
          <div class="card-header">
            <h3>📦 数据备份</h3>
            <p class="card-description">创建数据库的完整备份副本</p>
          </div>
          <div class="card-body">
            <div class="backup-info">
              <p>最后备份时间: <strong>{{ lastBackupTime || '暂无' }}</strong></p>
              <p>备份文件大小: <strong>{{ backupFileSize || '暂无' }}</strong></p>
            </div>
            <button @click="executeBackup" class="btn btn-primary" :disabled="isBackingUp">
              {{ isBackingUp ? '备份中...' : '执行备份' }}
            </button>
          </div>
        </div>

        <!-- 恢复部分 -->
        <div class="backup-card">
          <div class="card-header">
            <h3>♻️ 数据恢复</h3>
            <p class="card-description">从备份文件恢复数据库</p>
          </div>
          <div class="card-body">
            <div class="restore-warning">
              <p>⚠️ 警告：恢复操作将覆盖当前数据库中的所有数据，请谨慎操作！</p>
            </div>
            <div class="form-group">
              <label>选择备份文件</label>
              <select v-model="selectedBackupFile" class="form-input">
                <option value="">-- 选择备份文件 --</option>
                <option v-for="file in backupFiles" :key="file" :value="file">
                  {{ file }}
                </option>
              </select>
            </div>
            <button
              @click="executeRestore"
              class="btn btn-danger"
              :disabled="!selectedBackupFile || isRestoring"
            >
              {{ isRestoring ? '恢复中...' : '执行恢复' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计分析标签页 -->
    <div v-if="activeTab === 'statistics'" class="tab-content">
      <div class="section-header">
        <h2>访问统计分析</h2>
      </div>

      <div class="statistics-container">
        <!-- 统计卡片 -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon">👥</div>
            <div class="stat-content">
              <div class="stat-label">总用户数</div>
              <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">👁️</div>
            <div class="stat-content">
              <div class="stat-label">总访问量</div>
              <div class="stat-value">{{ statistics.totalVisits || 0 }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">🎨</div>
            <div class="stat-content">
              <div class="stat-label">作品总数</div>
              <div class="stat-value">{{ statistics.totalArtworks || 0 }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">📚</div>
            <div class="stat-content">
              <div class="stat-label">知识文章数</div>
              <div class="stat-value">{{ statistics.totalKnowledge || 0 }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">💬</div>
            <div class="stat-content">
              <div class="stat-label">评论总数</div>
              <div class="stat-value">{{ statistics.totalComments || 0 }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">⭐</div>
            <div class="stat-content">
              <div class="stat-label">投票总数</div>
              <div class="stat-value">{{ statistics.totalVotes || 0 }}</div>
            </div>
          </div>
        </div>

        <!-- 详细统计表格 -->
        <div class="detailed-stats" v-if="false">
          <h3>详细统计数据</h3>
          <div class="table-container">
            <table class="stats-table">
              <thead>
                <tr>
                  <th>统计项</th>
                  <th>数值</th>
                  <th>占比</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>今日新增用户</td>
                  <td>{{ statistics.todayNewUsers || 0 }}</td>
                  <td>{{ calculatePercentage(statistics.todayNewUsers, statistics.totalUsers) }}</td>
                </tr>
                <tr>
                  <td>今日访问量</td>
                  <td>{{ statistics.todayVisits || 0 }}</td>
                  <td>{{ calculatePercentage(statistics.todayVisits, statistics.totalVisits) }}</td>
                </tr>
                <tr>
                  <td>本周新增作品</td>
                  <td>{{ statistics.weekNewArtworks || 0 }}</td>
                  <td>{{ calculatePercentage(statistics.weekNewArtworks, statistics.totalArtworks) }}</td>
                </tr>
                <tr>
                  <td>本月新增知识</td>
                  <td>{{ statistics.monthNewKnowledge || 0 }}</td>
                  <td>{{ calculatePercentage(statistics.monthNewKnowledge, statistics.totalKnowledge) }}</td>
                </tr>
                <tr>
                  <td>平均每日访问量</td>
                  <td>{{ statistics.avgDailyVisits || 0 }}</td>
                  <td>-</td>
                </tr>
                <tr>
                  <td>活跃用户数</td>
                  <td>{{ statistics.activeUsers || 0 }}</td>
                  <td>{{ calculatePercentage(statistics.activeUsers, statistics.totalUsers) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 刷新按钮 -->
        <div class="refresh-section">
          <button @click="loadStatistics" class="btn btn-secondary" :disabled="isLoadingStats">
            {{ isLoadingStats ? '加载中...' : '🔄 刷新数据' }}
          </button>
          <span class="last-update">最后更新: {{ lastUpdateTime }}</span>
        </div>
      </div>
    </div>

    <!-- 恢复确认模态框 -->
    <div class="modal-overlay" v-if="showRestoreConfirm" @click.self="showRestoreConfirm = false">
      <div class="modal modal-small">
        <div class="modal-header">
          <h2>确认恢复</h2>
          <button @click="showRestoreConfirm = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <p>确定要从备份文件 <strong>{{ selectedBackupFile }}</strong> 恢复数据吗？</p>
          <p class="warning-text">⚠️ 此操作将覆盖当前数据库中的所有数据，此操作不可撤销！</p>
        </div>
        <div class="modal-footer">
          <button @click="showRestoreConfirm = false" class="btn btn-secondary">取消</button>
          <button @click="confirmRestore" class="btn btn-danger" :disabled="isRestoring">
            {{ isRestoring ? '恢复中...' : '确认恢复' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Toast 通知 -->
    <Toast ref="toast" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { AdminSystemService } from '../../services'
import Toast from '../../components/Toast.vue'

// 标签页
const tabs = [
  { id: 'settings', label: '网站设置' },
  // { id: 'backup', label: '数据备份' },
  { id: 'statistics', label: '统计分析' }
]

const activeTab = ref('settings')
const toast = ref(null)

// ===== 网站设置状态 =====
const settingsFormData = ref({
  siteName: '',
  siteDescription: '',
  logo: '',
  contactEmail: '',
  contactPhone: ''
})

const isSavingSettings = ref(false)

// ===== 数据备份状态 =====
const isBackingUp = ref(false)
const isRestoring = ref(false)
const lastBackupTime = ref('')
const backupFileSize = ref('')
const backupFiles = ref([])
const selectedBackupFile = ref('')
const showRestoreConfirm = ref(false)

// ===== 统计分析状态 =====
const statistics = ref({
  totalUsers: 0,
  totalVisits: 0,
  totalArtworks: 0,
  totalKnowledge: 0,
  totalComments: 0,
  totalVotes: 0,
  todayNewUsers: 0,
  todayVisits: 0,
  weekNewArtworks: 0,
  monthNewKnowledge: 0,
  avgDailyVisits: 0,
  activeUsers: 0
})

const isLoadingStats = ref(false)
const lastUpdateTime = ref('')

// 工具方法
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const calculatePercentage = (value, total) => {
  if (!total || total === 0) return '0%'
  return `${((value / total) * 100).toFixed(1)}%`
}

// ===== 网站设置方法 =====
const loadSettings = async () => {
  try {
    const response = await AdminSystemService.getSettings()
    if (response && response) {
      settingsFormData.value = {
        siteName: response.siteName || '',
        siteDescription: response.siteDescription || '',
        logo: response.logo || '',
        contactEmail: response.contactEmail || '',
        contactPhone: response.contactPhone || ''
      }
    }
  } catch (error) {
    console.error('加载系统设置错误:', error)
    toast.value.error('加载系统设置失败')
  }
}

const saveSettings = async () => {
  if (!settingsFormData.value.siteName || !settingsFormData.value.contactEmail) {
    toast.value.warning('请填写必填项')
    return
  }

  isSavingSettings.value = true
  try {
    const response = await AdminSystemService.updateSettings(settingsFormData.value)
    if (response) {
      toast.value.success('系统设置保存成功')
      loadSettings()
    } else {
      toast.value.error('保存失败')
    }
  } catch (error) {
    console.error('保存系统设置错误:', error)
    toast.value.error('保存系统设置失败')
  } finally {
    isSavingSettings.value = false
  }
}

// ===== 数据备份方法 =====
const executeBackup = async () => {
  isBackingUp.value = true
  try {
    const response = await AdminSystemService.backupData()
    if (response) {
      toast.value.success('数据备份成功')
      lastBackupTime.value = formatDate(new Date())
      // 重新加载备份文件列表
      loadBackupFiles()
    } else {
      toast.value.error('数据备份失败')
    }
  } catch (error) {
    console.error('执行备份错误:', error)
    toast.value.error('执行备份失败')
  } finally {
    isBackingUp.value = false
  }
}

const loadBackupFiles = async () => {
  try {
    // 模拟加载备份文件列表
    // 实际应该从后端获取
    backupFiles.value = [
      'backup_2024_01_15.sql',
      'backup_2024_01_14.sql',
      'backup_2024_01_13.sql'
    ]
  } catch (error) {
    console.error('加载备份文件列表错误:', error)
  }
}

const executeRestore = () => {
  if (!selectedBackupFile.value) {
    toast.value.warning('请选择备份文件')
    return
  }
  showRestoreConfirm.value = true
}

const confirmRestore = async () => {
  isRestoring.value = true
  try {
    const response = await AdminSystemService.restoreData({
      backupFile: selectedBackupFile.value
    })
    if (response) {
      toast.value.success('数据恢复成功')
      showRestoreConfirm.value = false
      selectedBackupFile.value = ''
    } else {
      toast.value.error('数据恢复失败')
    }
  } catch (error) {
    console.error('执行恢复错误:', error)
    toast.value.error('执行恢复失败')
  } finally {
    isRestoring.value = false
  }
}

// ===== 统计分析方法 =====
const loadStatistics = async () => {
  isLoadingStats.value = true
  try {
    const response = await AdminSystemService.getStatistics()
    if (response && response) {
      statistics.value = {
        totalUsers: response.totalUsers || 0,
        totalVisits: response.totalVisits || 0,
        totalArtworks: response.totalArtworks || 0,
        totalKnowledge: response.totalKnowledge || 0,
        totalComments: response.totalComments || 0,
        totalVotes: response.totalVotes || 0,
        todayNewUsers: response.todayNewUsers || 0,
        todayVisits: response.todayVisits || 0,
        weekNewArtworks: response.weekNewArtworks || 0,
        monthNewKnowledge: response.monthNewKnowledge || 0,
        avgDailyVisits: response.avgDailyVisits || 0,
        activeUsers: response.activeUsers || 0
      }
      lastUpdateTime.value = formatDate(new Date())
    }
  } catch (error) {
    console.error('加载统计数据错误:', error)
    toast.value.error('加载统计数据失败')
  } finally {
    isLoadingStats.value = false
  }
}

// 生命周期
onMounted(() => {
  loadSettings()
  loadBackupFiles()
  loadStatistics()
})
</script>

<style scoped>
.admin-system-page {
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
  margin-bottom: var(--spacing-lg);
}

.section-header h2 {
  color: #1d527c;
  margin: 0;
  font-size: 24px;
}

/* 网站设置 */
.settings-card {
  background-color: #ffffff;
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
  max-width: 600px;
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

.form-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  margin-top: var(--spacing-lg);
}

/* 数据备份 */
.backup-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: var(--spacing-lg);
}

.backup-card {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.card-header {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.card-header h3 {
  margin: 0 0 var(--spacing-sm) 0;
  color: #1d527c;
  font-size: 20px;
}

.card-description {
  margin: 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.card-body {
  padding: var(--spacing-lg);
}

.backup-info {
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
}

.backup-info p {
  margin: var(--spacing-sm) 0;
  color: var(--text-primary);
}

.restore-warning {
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
  background-color: #fff3cd;
  border-left: 4px solid #ffc107;
  border-radius: var(--border-radius-md);
}

.restore-warning p {
  margin: 0;
  color: #856404;
  font-size: var(--font-size-sm);
}

/* 统计分析 */
.statistics-container {
  background-color: #ffffff;
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  border-left: 4px solid var(--primary-color);
}

.stat-icon {
  font-size: var(--font-size-2xl);
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: #60778e;
  font-size: 14px;
  margin-bottom: var(--spacing-xs);
}

.stat-value {
  color: #1d527c;
  font-size: 32px;
  font-weight: 700;
}

.detailed-stats {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.detailed-stats h3 {
  color: var(--primary-color);
  margin-bottom: var(--spacing-md);
}

.table-container {
  overflow-x: auto;
  margin-bottom: var(--spacing-lg);
}

.stats-table {
  width: 100%;
  border-collapse: collapse;
}

.stats-table thead {
  background-color: var(--primary-color);
  color: white;
}

.stats-table th {
  padding: var(--spacing-md);
  text-align: left;
  font-weight: 600;
}

.stats-table td {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

.stats-table tbody tr:hover {
  background-color: var(--bg-secondary);
}

.refresh-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

.last-update {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

/* 按钮样式 */
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
  max-width: 500px;
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

.modal-body p {
  margin: var(--spacing-md) 0;
  color: var(--text-primary);
}

.warning-text {
  color: #e74c3c;
  font-weight: 600;
}

.modal-footer {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

@media (max-width: 768px) {
  .admin-system-page {
    padding: var(--spacing-md);
  }

  .tabs-container {
    flex-wrap: wrap;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .backup-container {
    grid-template-columns: 1fr;
  }

  .settings-card {
    max-width: 100%;
  }

  .modal {
    max-width: 95%;
  }
}
</style>
