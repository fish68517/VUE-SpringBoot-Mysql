<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never" class="welcome-card">
          <div class="welcome-content">
            <el-avatar :size="60" :src="userInfo.campusAvatarUrl" />
            <div class="welcome-text">
              <h2>你好，{{ userInfo.campusNickname || '同学' }}！</h2>
              <p>今天是 {{ new Date().toLocaleDateString() }}，又是充满希望的一天！</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="进行中的任务" :value="stats.ongoingTasks" />
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="正在养成的习惯" :value="stats.activeHabits" />
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="已获得徽章" :value="userInfo.campusBadgeCount || 0" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div>任务分类占比</div>
          </template>
          <v-chart class="chart" :option="taskPieOption" autoresize />
        </el-card>
      </el-col>
      
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>
            <div>近7日专注时长 (分钟)</div>
          </template>
          <v-chart class="chart" :option="focusBarOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, provide } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart, BarChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components';
import VChart, { THEME_KEY } from 'vue-echarts';
import api from '../api/NetWorkApi.js';

// 注册 ECharts 组件
use([
  CanvasRenderer,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
]);

// 为 vue-echarts 提供主题
provide(THEME_KEY, 'light');

// 假设当前登录的用户ID为1 ("张伟")
const currentUserId = 1;

const userInfo = ref({});
const stats = reactive({
  ongoingTasks: 0,
  activeHabits: 0,
});

// ECharts 图表配置项
const taskPieOption = ref({});
const focusBarOption = ref({});

onMounted(async () => {
  try {
    const [userRes, tasksRes, habitsRes, focusRecordsRes] = await Promise.all([
      api.campusUserApi.getById(currentUserId),
      api.taskFocusApi.list(),
      api.habitTrackApi.list(),
      api.focusRecordApi.list()
    ]);

    // 1. 处理用户信息和顶部统计卡片
    userInfo.value = userRes.data;
    const allTasks = tasksRes.data || [];
    const allHabits = habitsRes.data || [];
    stats.ongoingTasks = allTasks.filter(t => t.campusUserId === currentUserId && t.taskStatusEnum === 'in_progress').length;
    stats.activeHabits = allHabits.filter(h => h.campusUserId === currentUserId && h.habitStatusEnum === 'active').length;

    // 2. 准备任务分类饼图数据
    const userTasks = allTasks.filter(t => t.campusUserId === currentUserId);
    const categoryData = userTasks.reduce((acc, task) => {
        const category = task.taskCategoryCode || '未分类';
        acc[category] = (acc[category] || 0) + 1;
        return acc;
    }, {});
    const pieData = Object.keys(categoryData).map(key => ({
        name: key,
        value: categoryData[key]
    }));
    
    // 3. 准备近7日专注时长柱状图数据
    const userFocusRecords = (focusRecordsRes.data || []).filter(r => r.campusUserId === currentUserId);
    const last7Days = Array.from({ length: 7 }, (_, i) => {
        const d = new Date();
        d.setDate(d.getDate() - i);
        return d.toISOString().split('T')[0];
    }).reverse();

    const barData = last7Days.map(day => {
        const dayRecords = userFocusRecords.filter(r => r.focusStartTimestamp.startsWith(day));
        const totalMinutes = dayRecords.reduce((sum, r) => sum + r.focusDurationSeconds, 0) / 60;
        return Math.round(totalMinutes);
    });

    // 更新 ECharts 配置
    updateCharts(pieData, last7Days.map(d => d.slice(5)), barData);

  } catch (error) {
    console.error("加载仪表盘数据失败:", error);
  }
});

const updateCharts = (pieData, barXAxis, barSeries) => {
  taskPieOption.value = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [{
      name: '任务分类',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
      labelLine: { show: false },
      data: pieData,
    }]
  };
  
  focusBarOption.value = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: barXAxis },
    yAxis: { type: 'value' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [{
      name: '专注时长',
      data: barSeries,
      type: 'bar',
      showBackground: true,
      backgroundStyle: { color: 'rgba(180, 180, 180, 0.2)' }
    }]
  };
};

</script>

<style scoped>
.welcome-card {
  height: 100%;
}
.welcome-content {
  display: flex;
  align-items: center;
}
.welcome-text {
  margin-left: 20px;
}
.welcome-text h2 {
  margin: 0;
  font-size: 24px;
}
.welcome-text p {
  margin: 5px 0 0;
  color: #888;
}
.stat-card {
  text-align: center;
  height: 100%;
}
.chart {
  height: 300px;
}
</style>