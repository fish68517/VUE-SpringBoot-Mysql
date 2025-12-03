没问题！这是 “管理员仪表盘” (Admin Dashboard) 页面的完整汉化版本。

主要修改包括：

界面标题：Admin Dashboard -> 管理员仪表盘, User Distribution -> 用户角色分布 等。

统计指标：Total Users -> 总用户数, Total Coaches -> 总教练数, Pending Moderation -> 待审核内容 等。

图表标签：Regular Users -> 普通用户, Administrators -> 管理员。

表格列名：Avatar -> 头像, Role -> 角色, Registration Date -> 注册时间 等。

系统活动：Total Posts -> 总动态数, Total Check-ins -> 总打卡数 等。

快捷操作：User Management -> 用户管理, Moderation Queue -> 内容审核, Resource Management -> 资源管理。

日期格式：改为中文 年-月-日 格式。

请复制以下代码覆盖：

code
Html
play_circle
download
content_copy
expand_less
<template>
  <div class="admin-dashboard">
    <h1>管理员仪表盘</h1>

    <!-- 统计卡片 (Statistics Cards) -->
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <el-icon class="stat-icon" :size="40" color="#409EFF">
            <User />
          </el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
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
            <div class="stat-label">总教练数</div>
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
            <div class="stat-label">总资源数</div>
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
            <div class="stat-label">待审核内容</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 用户角色分布 (User Count by Role Chart) -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>用户角色分布</span>
        </div>
      </template>
      <div class="chart-container">
        <div class="role-stats">
          
          <!-- 普通用户 -->
          <div class="role-item">
            <div class="role-label">普通用户</div>
            <el-progress
              :percentage="getUserPercentage('user')"
              :color="'#409EFF'"
            />
            <!-- 修改点：usersByRole.user -> totalRegularUsers -->
            <div class="role-count">{{ statistics.totalRegularUsers || 0 }} 人</div>
          </div>

          <!-- 教练 -->
          <div class="role-item">
            <div class="role-label">教练</div>
            <el-progress
              :percentage="getUserPercentage('coach')"
              :color="'#67C23A'"
            />
            <!-- 修改点：usersByRole.coach -> totalCoaches -->
            <div class="role-count">{{ statistics.totalCoaches || 0 }} 人</div>
          </div>

          <!-- 管理员 -->
          <div class="role-item">
            <div class="role-label">管理员</div>
            <el-progress
              :percentage="getUserPercentage('admin')"
              :color="'#E6A23C'"
            />
            <!-- 修改点：usersByRole.admin -> totalAdmins -->
            <div class="role-count">{{ statistics.totalAdmins || 0 }} 人</div>
          </div>

        </div>
      </div>
    </el-card>

    <!-- 最近注册 (Recent Registrations) -->
    <el-card class="recent-card">
      <template #header>
        <div class="card-header">
          <span>最近注册用户</span>
        </div>
      </template>
      <el-table :data="recentUsers" style="width: 100%">
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              {{ row.username.charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column label="角色">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">{{ formatRole(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>
      <div v-if="recentUsers.length === 0" class="empty-state">
        <el-empty description="暂无新注册用户" />
      </div>
    </el-card>

    <!-- 系统活动概览 (System Activity Overview) -->
    <el-card class="activity-card">
      <template #header>
        <div class="card-header">
          <span>系统活动概览</span>
        </div>
      </template>
      <div class="activity-grid">
        <div class="activity-item">
          <div class="activity-label">总动态数</div>
          <div class="activity-value">{{ statistics.totalDynamicPosts || 0 }}</div>
        </div>
        <div class="activity-item" v-if="false">
          <div class="activity-label">总评论数</div>
          <div class="activity-value">{{ statistics.totalComments || 0 }}</div>
        </div>
        <div class="activity-item">
          <div class="activity-label">总资源数</div>
          <div class="activity-value">{{ statistics.totalResources || 0 }}</div>
        </div>
        <div class="activity-item">
          <div class="activity-label">总训练计划</div>
          <div class="activity-value">{{ statistics.totalTrainingPlans || 0 }}</div>
        </div>
      </div>
    </el-card>

    <!-- 快捷操作 (Quick Actions) -->
    <div class="quick-actions">
      <el-button type="primary" size="large" @click="goToUserManagement">
        <el-icon><User /></el-icon>
        用户管理
      </el-button>
      <el-button type="warning" size="large" @click="goToModeration">
        <el-icon><Warning /></el-icon>
        内容审核
      </el-button>
      <el-button type="success" size="large" @click="goToResourceManagement">
        <el-icon><Document /></el-icon>
        资源管理
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { User, Avatar, Document, Warning } from '@element-plus/icons-vue';
import { getStatistics, getUsers } from '@/api/admin';
import { showError } from '@/utils/feedback';

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
    showError('加载统计数据失败');
  }
};

const fetchRecentUsers = async () => {
  try {
    const data = await getUsers({ page: 0, size: 5 });
    recentUsers.value = data.content || data.slice(0, 5);
  } catch (error) {
    showError('加载最近注册用户失败');
  }
};

const getUserPercentage = (role) => {
  // 1. 安全检查：如果 statistics 还是 undefined (数据未加载)，直接返回 0
  if (!statistics.value) return 0;

  const total = statistics.value.totalUsers;
  if (total === 0) return 0;

  // 2. 核心修改：根据 role 字符串手动匹配对应的字段名
  // 日志显示你的数据字段是 totalRegularUsers, totalCoaches, totalAdmins
  let count = 0;
  
  if (role === 'user') {
    count = statistics.value.totalRegularUsers || 0;
  } else if (role === 'coach') {
    count = statistics.value.totalCoaches || 0;
  } else if (role === 'admin') {
    count = statistics.value.totalAdmins || 0;
  } 
  // 如果你的 role 还有其他名字，可以在这里继续加 else if

  // 3. 计算百分比
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

const formatRole = (role) => {
  const map = {
    admin: '管理员',
    coach: '教练',
    user: '用户'
  };
  return map[role] || role;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
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
/* 样式保持不变 */
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