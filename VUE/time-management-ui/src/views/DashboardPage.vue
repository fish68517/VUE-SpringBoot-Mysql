<template>
  <div>
    <el-card shadow="never" class="welcome-card">
      <h2>你好，{{ userInfo.campusNickname || '同学' }}！</h2>
      <p>今天是 {{ new Date().toLocaleDateString() }}，继续保持自律，迎接新的一天吧！</p>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="累计专注时长" :value="formatDuration(stats.totalFocusSeconds)" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="进行中的任务" :value="stats.ongoingTasks" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="正在养成的习惯" :value="stats.activeHabits" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="已获得徽章" :value="userInfo.campusBadgeCount || 0" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../api/NetWorkApi.js';

// 假设当前登录的用户ID为1 ("张伟")
// 在真实项目中，这个ID应该从登录状态中获取
const currentUserId = 1;

const userInfo = ref({});
const stats = reactive({
  totalFocusSeconds: 0,
  ongoingTasks: 0,
  activeHabits: 0,
});

// onMounted 生命周期钩子，在组件挂载后执行
onMounted(async () => {
  // 使用 Promise.all 并发请求，提升加载速度
  try {
    const [userRes, tasksRes, habitsRes, focusRecordsRes] = await Promise.all([
      api.campusUserApi.getById(currentUserId),
      api.taskFocusApi.list(),
      api.habitTrackApi.list(),
      api.focusRecordApi.list()
    ]);

    // 1. 处理用户信息
    userInfo.value = userRes.data.data;

    // 2. 处理任务数据
    const allTasks = tasksRes.data.data || [];
    stats.ongoingTasks = allTasks.filter(task => 
      task.campusUserId === currentUserId && task.taskStatusEnum === 'in_progress'
    ).length;

    // 3. 处理习惯数据
    const allHabits = habitsRes.data.data || [];
    stats.activeHabits = allHabits.filter(habit => 
      habit.campusUserId === currentUserId && habit.habitStatusEnum === 'active'
    ).length;

    // 4. 处理专注记录数据
    const allFocusRecords = focusRecordsRes.data.data || [];
    const userFocusRecords = allFocusRecords.filter(record => record.campusUserId === currentUserId);
    stats.totalFocusSeconds = userFocusRecords.reduce((sum, record) => sum + record.focusDurationSeconds, 0);

  } catch (error) {
    console.error("加载仪表盘数据失败:", error);
  }
});

// 辅助函数：将秒格式化为 "X小时 Y分钟"
const formatDuration = (totalSeconds) => {
  if (!totalSeconds) return '0 分钟';
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  let result = '';
  if (hours > 0) {
    result += `${hours} 小时 `;
  }
  if (minutes > 0 || hours === 0) {
    result += `${minutes} 分钟`;
  }
  return result.trim();
};
</script>

<style scoped>
.welcome-card {
  margin-bottom: 20px;
}
.stats-row .el-card {
  text-align: center;
}
</style>