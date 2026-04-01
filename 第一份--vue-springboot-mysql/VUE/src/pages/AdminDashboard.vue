<template>
  <div class="admin-dashboard">

    <div class="admin-container">
      
      <div class="dashboard-content">
        <h1>后台仪表板</h1>
        
        <!-- 统计数据卡片 -->
        <div class="statistics-grid">
          <div class="stat-card">
            <div class="stat-icon users-icon">👥</div>
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ statistics.totalUsers }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon patterns-icon">🎨</div>
            <div class="stat-info">
              <div class="stat-label">纹样总数</div>
              <div class="stat-value">{{ statistics.totalPatterns }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon works-icon">✨</div>
            <div class="stat-info">
              <div class="stat-label">原创作品总数</div>
              <div class="stat-value">{{ statistics.totalWorks }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon pending-icon">⏳</div>
            <div class="stat-info">
              <div class="stat-label">待审核作品</div>
              <div class="stat-value">{{ statistics.pendingWorks }}</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon approved-icon">✅</div>
            <div class="stat-info">
              <div class="stat-label">已审核作品</div>
              <div class="stat-value">{{ statistics.approvedWorks }}</div>
            </div>
          </div>
        </div>

        <!-- 快速操作入口 -->
        <div class="quick-actions">
          <h2>快速操作</h2>
          <div class="actions-grid">
            <router-link to="/admin/patterns" class="action-button">
              <span class="action-icon">🎨</span>
              <span class="action-text">纹样管理</span>
            </router-link>

            <router-link to="/admin/users" class="action-button">
              <span class="action-icon">👥</span>
              <span class="action-text">用户管理</span>
            </router-link>

            <router-link to="/admin/review" class="action-button">
              <span class="action-icon">📋</span>
              <span class="action-text">内容审核</span>
            </router-link>

            <!-- <router-link to="/admin/logs" class="action-button">
              <span class="action-icon">📊</span>
              <span class="action-text">日志管理</span>
            </router-link>

            <router-link to="/admin/settings" class="action-button">
              <span class="action-icon">⚙️</span>
              <span class="action-text">参数设置</span>
            </router-link> -->

            <router-link to="/admin/permissions" class="action-button">
              <span class="action-icon">🔐</span>
              <span class="action-text">权限管理</span>
            </router-link>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminAPI } from '../services/api'


import { ElMessage } from 'element-plus'

const statistics = ref({
  totalUsers: 0,
  totalPatterns: 0,
  totalWorks: 0,
  pendingWorks: 0,
  approvedWorks: 0
})

const loadStatistics = async () => {
  try {
    const response = await adminAPI.getDashboardStatistics()
    if (response.code === 200) {
      statistics.value = response.data
    } else {
      ElMessage.error(response.message || '获取统计数据失败')
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败: ' + error.message)
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-container {
  display: flex;
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  gap: 2rem;
  padding: 2rem;
}

.dashboard-content {
  flex: 1;
}

.dashboard-content h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 2.5rem;
  min-width: 60px;
  text-align: center;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 0.875rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: bold;
  color: #333;
}

.quick-actions {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.quick-actions h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #333;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.action-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.action-button:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.action-icon {
  font-size: 2rem;
}

.action-text {
  font-size: 0.875rem;
  font-weight: 500;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .statistics-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 1rem;
  }

  .stat-card {
    padding: 1rem;
  }

  .stat-icon {
    font-size: 2rem;
    min-width: 50px;
  }

  .stat-value {
    font-size: 1.5rem;
  }

  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .dashboard-content h1 {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
  }
}

@media (max-width: 480px) {
  .statistics-grid {
    grid-template-columns: 1fr;
  }

  .actions-grid {
    grid-template-columns: 1fr;
  }

  .admin-container {
    padding: 0.5rem;
  }

  .quick-actions {
    padding: 1rem;
  }
}
</style>
