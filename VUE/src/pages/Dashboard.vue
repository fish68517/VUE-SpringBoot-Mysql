<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>仪表盘</h1>
      <div class="header-info">
        <p>欢迎回来，{{ user?.username }}！</p>
      </div>
    </div>
    <div class="dashboard-content">
      <div class="dashboard-grid">
        <div class="dashboard-card">
          <h2>快捷入口</h2>
          <ul class="quick-links">
            <li>
              <router-link to="/app/schools" class="quick-link">
                🔍 搜索院校
              </router-link>
            </li>
            <li>
              <router-link to="/app/favorites" class="quick-link">
                ⭐ 我的收藏
              </router-link>
            </li>
            <li>
              <router-link to="/app/profile" class="quick-link">
                👤 个人资料
              </router-link>
            </li>
            <li>
              <router-link to="/app/announcements" class="quick-link">
                📢 公告通知
              </router-link>
            </li>
            <li>
              <router-link to="/app/feedback" class="quick-link">
                💬 提交反馈
              </router-link>
            </li>
          </ul>
        </div>

        <div class="dashboard-card">
          <h2>我的资料</h2>
          <div class="profile-summary">
            <p><strong>用户 ID：</strong> {{ user?.id }}</p>
            <p><strong>角色：</strong> <span class="role-badge">{{ user?.role }}</span></p>
            <p><strong>状态：</strong> <span class="status-badge">正常</span></p>
            <router-link to="/app/profile" class="btn-edit-profile">
              编辑资料
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import authService from '../services/authService'

export default {
  name: 'Dashboard',
  data() {
    return {
      user: null
    }
  },
  mounted() {
    this.loadUserData()
  },
  methods: {
    /**
     * 从认证服务加载当前用户信息
     */
    loadUserData() {
      this.user = authService.getUser()
      if (!this.user) {
        // 如果没有用户信息，则跳转到登录页
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px 0;
}

.dashboard-header {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.dashboard-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 28px;
}

.header-info p {
  color: #666;
  font-size: 16px;
}

.dashboard-content {
  padding: 0;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.dashboard-card {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s;
}

.dashboard-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.dashboard-card h2 {
  color: #2c3e50;
  font-size: 18px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #3498db;
}

.quick-links {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.quick-link {
  display: block;
  padding: 12px 15px;
  background-color: #f8f9fa;
  color: #2c3e50;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s;
  font-weight: 500;
}

.quick-link:hover {
  background-color: #3498db;
  color: white;
}

.profile-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.profile-summary p {
  color: #555;
  font-size: 14px;
}

.profile-summary strong {
  color: #2c3e50;
}

.role-badge {
  display: inline-block;
  padding: 4px 12px;
  background-color: #3498db;
  color: white;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  background-color: #27ae60;
  color: white;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.btn-edit-profile {
  display: inline-block;
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
  font-weight: 600;
  text-align: center;
}

.btn-edit-profile:hover {
  background-color: #2980b9;
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 20px;
  }

  .dashboard-header h1 {
    font-size: 24px;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>
