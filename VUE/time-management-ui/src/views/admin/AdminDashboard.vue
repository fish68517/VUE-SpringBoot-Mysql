<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="注册用户总数" :value="stats.totalUsers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待审核用户" :value="stats.pendingUsers">
            <template #suffix>
              <el-icon style="vertical-align: -0.125em">
                <Warning />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="已创建任务总数" :value="stats.totalTasks" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="学习资源总数" :value="stats.totalResources" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { Warning } from '@element-plus/icons-vue';
import api from '../../api/NetWorkApi.js';

const stats = reactive({
  totalUsers: 0,
  pendingUsers: 0,
  totalTasks: 0,
  totalResources: 0,
});

onMounted(async () => {
  try {
    const [usersRes, tasksRes, resourcesRes] = await Promise.all([
      api.campusUserApi.list(),
      api.taskFocusApi.list(),
      api.learnResourceApi.list()
    ]);

    const allUsers = usersRes.data.data || [];
    stats.totalUsers = allUsers.length;
    stats.pendingUsers = allUsers.filter(user => user.campusStatusFlag === 0).length;

    stats.totalTasks = (tasksRes.data.data || []).length;
    stats.totalResources = (resourcesRes.data.data || []).length;
  } catch (error) {
    console.error("加载管理后台统计数据失败:", error);
  }
});
</script>

<style scoped>
.el-card {
  text-align: center;
}
</style>