<template>
  <div class="admin-dashboard">
    <h1>Admin Dashboard</h1>

    <!-- Statistics Cards -->
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <el-icon class="stat-icon" :size="40" color="#409EFF">
            <User />
          </el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalUsers }}</div>
            <div class="stat-label">Total Users</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <el-icon class="stat-icon" :size="40" color="#67C23A">
            <Avatar />
          </el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalCoaches }}</div>
            <div class="stat-label">Total Coaches</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <el-icon class="stat-icon" :size="40" color="#E6A23C">
            <Document />
          </el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalResources }}</div>
            <div class="stat-label">Total Resources</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <el-icon class="stat-icon" :size="40" color="#F56C6C">
            <Warning />
          </el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.pendingModeration }}</div>
            <div class="stat-label">Pending Moderation</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- User Count by Role Chart -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>User Distribution by Role</span>
        </div>
      </template>
      <div class="chart-container">
        <div class="role-stats">
          <div class="role-item">
            <div class="role-label">Regular Users</div>
            <el-progress
              :percentage="getUserPercentage('user')"
              :color="'#409EFF'"
            />
            <div class="role-count">{{ statistics.usersByRole.user || 0 }}</div>
          </div>
          <div class="role-item">
            <div class="role-label">Coaches</div>
            <el-progress
              :percentage="getUserPercentage('coach')"
              :color="'#67C23A'"
            />
            <div class="role-count">{{ statistics.usersByRole.coach || 0 }}</div>
          </div>
          <div class="role-item">
            <div class="role-label">Administrators</div>
            <el-progress
              :percentage="getUserPercentage('admin')"
              :color="'#E6A23C'"
            />
            <div class="role-count">{{ statistics.usersByRole.admin || 0 }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Recent Registrations -->
    <el-card class="recent-card">
      <template #header>
        <div class="card-header">
          <span>Recent Registrations</span>
        </div>
      </template>
      <el-table :data="recentUsers" style="width: 100%">
        <el-table-column label="Avatar" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              {{ row.username.charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="Username" />
        <el-table-column label="Role">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Registration Date">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>
      <div v-if="recentUsers.length === 0" class="empty-state">
        <el-empty description="No recent registrations" />
      </div>
    </el-card>

    <!-- System Activity Overview -->
    <el-card class="activity-card">
      <template #header>
        <div class="card-header">
          <span>System Activity Overview</span>
        </div>
      </template>
      <div class="activity-grid">
        <div class="activity-item">
          <div class="activity-label">Total Posts</div>
          <div class="activity-value">{{ statistics.totalPosts || 0 }}</div>
        </div>
        <div class="activity-item">
          <div class="activity-label">Total Comments</div>
          <div class="activity-value">{{ statistics.totalComments || 0 }}</div>
        </div>
        <div class="activity-item">
          <div class="activity-label">Total Check-ins</div>
          <div class="activity-value">{{ statistics.totalCheckIns || 0 }}</div>
        </div>
        <div class="activity-item">
          <div class="activity-label">Total Training Plans</div>
          <div class="activity-value">{{ statistics.totalTrainingPlans || 0 }}</div>
        </div>
      </div>
    </el-card>

    <!-- Quick Actions -->
    <div class="quick-actions">
      <el-button type="primary" size="large" @click="goToUserManagement">
        <el-icon><User /></el-icon>
        User Management
      </el-button>
      <el-button type="warning" size="large" @click="goToModeration">
        <el-icon><Warning /></el-icon>
        Moderation Queue
      </el-button>
      <el-button type="success" size="large" @click="goToResourceManagement">
        <el-icon><Document /></el-icon>
        Resource Management
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Avatar, Document, Warning } from '@element-plus/icons-vue';
import { getStatistics, getUsers } from '@/api/admin';

const router = useRouter();

const statistics = ref({
  totalUsers: 0,
  totalCoaches: 0,
  totalResources: 0,
  pendingModeration: 0,
  usersByRole: {
    user: 0,
    coach: 0,
    admin: 0
  },
  totalPosts: 0,
  totalComments: 0,
  totalCheckIns: 0,
  totalTrainingPlans: 0
});

const recentUsers = ref([]);

const fetchStatistics = async () => {
  try {
    const data = await getStatistics();
    statistics.value = data;
  } catch (error) {
    ElMessage.error('Failed to load statistics');
  }
};

const fetchRecentUsers = async () => {
  try {
    const data = await getUsers({ page: 0, size: 5 });
    recentUsers.value = data.content || data.slice(0, 5);
  } catch (error) {
    ElMessage.error('Failed to load recent users');
  }
};

const getUserPercentage = (role) => {
  const total = statistics.value.totalUsers;
  if (total === 0) return 0;
  const count = statistics.value.usersByRole[role] || 0;
  return Math.round((count / total) * 100);
};

const getRoleTagType = (role) => {
  const types = {
    admin: 'danger',
    coach: 'success',
    user: 'info'
  };
  return types[role] || 'info';
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};

const goToUserManagement = () => {
  router.push('/admin/users');
};

const goToModeration = () => {
  router.push('/admin/moderation');
};

const goToResourceManagement = () => {
  router.push('/admin/resources');
};

onMounted(() => {
  fetchStatistics();
  fetchRecentUsers();
});
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

h1 {
  margin-bottom: 24px;
  color: #303133;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  cursor: default;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-card,
.recent-card,
.activity-card {
  margin-bottom: 24px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  padding: 20px 0;
}

.role-stats {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.role-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.role-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.role-count {
  font-size: 14px;
  color: #909399;
  text-align: right;
}

.empty-state {
  padding: 40px 0;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
  padding: 20px 0;
}

.activity-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.activity-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.activity-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.quick-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  flex: 1;
  min-width: 200px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .activity-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-actions {
    flex-direction: column;
  }

  .quick-actions .el-button {
    width: 100%;
  }
}
</style>
