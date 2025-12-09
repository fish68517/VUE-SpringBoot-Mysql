<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>专注与任务监管</span>
        <div class="header-actions">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索用户、任务标题" 
            style="width: 250px; margin-right: 10px;" 
            clearable
          />
        </div>
      </div>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="专注记录流水" name="records">
        <el-table :data="filteredRecords" v-loading="loading" style="width: 100%">
          <el-table-column label="用户" width="140">
            <template #default="scope">
              <div class="user-info">
                <el-avatar :size="24" :src="scope.row.userAvatar" style="margin-right: 8px;" />
                <span>{{ scope.row.userName }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="关联任务" min-width="180">
            <template #default="scope">
              <span style="font-weight: bold">{{ scope.row.taskTitle }}</span>
            </template>
          </el-table-column>

          <el-table-column label="开始时间" width="170">
            <template #default="scope">
              {{ formatDateTime(scope.row.focusStartTimestamp) }}
            </template>
          </el-table-column>

          <el-table-column label="专注时长" width="120">
            <template #default="scope">
              <el-tag type="info" effect="plain">
                {{ formatDuration(scope.row.focusDurationSeconds) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.focusStatusEnum === 'completed' ? 'success' : 'danger'">
                {{ scope.row.focusStatusEnum === 'completed' ? '完成' : '中断' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="中断/暂停" width="100">
            <template #default="scope">
              <span v-if="scope.row.focusInterruptionCount > 0" style="color: #F56C6C">
                {{ scope.row.focusInterruptionCount }} 次中断
              </span>
              <span v-else style="color: #909399">-</span>
            </template>
          </el-table-column>

          <el-table-column label="类型" width="100">
            <template #default="scope">
              <el-tag size="small" effect="dark" :type="scope.row.focusTypeEnum === 'pomodoro' ? 'danger' : 'primary'">
                {{ scope.row.focusTypeEnum === 'pomodoro' ? '番茄钟' : '自定义' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="全校任务库" name="tasks">
        <el-table :data="filteredTasks" v-loading="loading" style="width: 100%">
          <el-table-column label="发布人" width="140">
            <template #default="scope">
              <div class="user-info">
                <el-avatar :size="24" :src="scope.row.userAvatar" style="margin-right: 8px;" />
                <span>{{ scope.row.userName }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="taskTitleText" label="任务标题" min-width="200" show-overflow-tooltip />
          
          <el-table-column label="截止时间" width="170">
            <template #default="scope">
              <span :class="{ 'overdue': isOverdue(scope.row.taskDeadlineTimestamp) }">
                {{ formatDateTime(scope.row.taskDeadlineTimestamp) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column label="优先级" width="100">
            <template #default="scope">
              <el-tag :type="getPriorityType(scope.row.taskPriorityCode)">
                {{ getPriorityLabel(scope.row.taskPriorityCode) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="当前状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.taskStatusEnum)">
                {{ scope.row.taskStatusEnum }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="累计专注" width="120">
            <template #default="scope">
              {{ scope.row.taskActualMinutes }} 分钟
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';

const activeTab = ref('records');
const loading = ref(false);
const searchKeyword = ref('');

// 原始数据
const rawRecords = ref([]);
const rawTasks = ref([]);
const usersMap = ref({});

const fetchData = async () => {
  loading.value = true;
  try {
    // 并发请求：专注记录、任务列表、用户列表
    const [recordRes, taskRes, userRes] = await Promise.all([
      api.focusRecordApi.list(),
      api.taskFocusApi.list(),
      api.campusUserApi.list()
    ]);

    const users = userRes.data.data || userRes.data || [];
    const tasks = taskRes.data.data || taskRes.data || [];
    const records = recordRes.data.data || recordRes.data || [];

    // 建立 ID 映射表，方便快速查找
    users.forEach(u => {
      usersMap.value[u.campusUserId] = { name: u.campusNickname, avatar: u.campusAvatarUrl };
    });
    const tasksMap = {};
    tasks.forEach(t => {
      tasksMap[t.taskFocusId] = t.taskTitleText;
    });

    // 处理任务数据 (添加用户信息)
    rawTasks.value = tasks.map(t => ({
      ...t,
      userName: usersMap.value[t.campusUserId]?.name || '未知',
      userAvatar: usersMap.value[t.campusUserId]?.avatar || ''
    }));

    // 处理专注记录数据 (添加用户和任务信息)
    rawRecords.value = records.map(r => ({
      ...r,
      userName: usersMap.value[r.campusUserId]?.name || '未知',
      userAvatar: usersMap.value[r.campusUserId]?.avatar || '',
      taskTitle: tasksMap[r.taskFocusId] || '已删除任务'
    })).sort((a, b) => new Date(b.focusStartTimestamp) - new Date(a.focusStartTimestamp)); // 按时间倒序

  } catch (e) {
    ElMessage.error('数据加载失败');
    console.error(e);
  } finally {
    loading.value = false;
  }
};

// 过滤逻辑
const filteredRecords = computed(() => {
  if (!searchKeyword.value) return rawRecords.value;
  const k = searchKeyword.value.toLowerCase();
  return rawRecords.value.filter(r => r.userName.toLowerCase().includes(k) || r.taskTitle.toLowerCase().includes(k));
});

const filteredTasks = computed(() => {
  if (!searchKeyword.value) return rawTasks.value;
  const k = searchKeyword.value.toLowerCase();
  return rawTasks.value.filter(t => t.userName.toLowerCase().includes(k) || t.taskTitleText.toLowerCase().includes(k));
});

// 工具函数
const formatDateTime = (ts) => ts ? ts.replace('T', ' ').substring(0, 16) : '-';
const formatDuration = (seconds) => {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${m}分${s}秒`;
};
const isOverdue = (ts) => ts && new Date(ts) < new Date();

// 优先级样式
const getPriorityLabel = (code) => ['低', '中', '高'][code] || '普通';
const getPriorityType = (code) => ['info', 'warning', 'danger'][code] || 'info';

// 任务状态样式
const getStatusType = (status) => {
  const map = { 'completed': 'success', 'in_progress': 'primary', 'pending': 'info', 'archived': 'warning' };
  return map[status] || 'info';
};

onMounted(fetchData);
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.user-info { display: flex; align-items: center; }
.overdue { color: #F56C6C; font-weight: bold; }
</style>