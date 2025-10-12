<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="注册用户总数" :value="stats.totalUsers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="待审核用户" :value="stats.pendingUsers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="已创建任务总数" :value="stats.totalTasks" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="已完成任务数" :value="stats.completedTasks" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>
            <div>近7日用户注册量</div>
          </template>
          <v-chart class="chart" :option="userRegBarOption" autoresize />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div>平台任务状态分布</div>
          </template>
          <v-chart class="chart" :option="taskStatusPieOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted, provide, ref } from 'vue';
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
import api from '../../api/NetWorkApi.js';

// ECharts 注册
use([CanvasRenderer, PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent]);
provide(THEME_KEY, 'light');

const stats = reactive({
  totalUsers: 0,
  pendingUsers: 0,
  totalTasks: 0,
  completedTasks: 0,
});

const userRegBarOption = ref({});
const taskStatusPieOption = ref({});

onMounted(async () => {
  try {
    const [usersRes, tasksRes] = await Promise.all([
      api.campusUserApi.list(),
      api.taskFocusApi.list(),
    ]);

    const allUsers = usersRes.data || [];
    const allTasks = tasksRes.data || [];

    // 1. 处理顶部统计卡片
    stats.totalUsers = allUsers.length;
    stats.pendingUsers = allUsers.filter(u => u.campusStatusFlag === 0).length;
    stats.totalTasks = allTasks.length;
    stats.completedTasks = allTasks.filter(t => t.taskStatusEnum === 'completed').length;

    // 2. 准备近7日用户注册量数据
    const last7Days = Array.from({ length: 7 }, (_, i) => {
        const d = new Date();
        d.setDate(d.getDate() - i);
        return d.toISOString().split('T')[0];
    }).reverse();

    const userRegData = last7Days.map(day => {
        return allUsers.filter(u => u.campusCreateTimestamp.startsWith(day)).length;
    });

    // 3. 准备平台任务状态分布数据
    const statusData = allTasks.reduce((acc, task) => {
        const status = task.taskStatusEnum || 'unknown';
        acc[status] = (acc[status] || 0) + 1;
        return acc;
    }, {});
    const pieData = Object.keys(statusData).map(key => ({
        name: key,
        value: statusData[key]
    }));

    // 更新 ECharts 配置
    updateCharts(pieData, last7Days.map(d => d.slice(5)), userRegData);

  } catch (error) {
    console.error("加载管理后台统计数据失败:", error);
  }
});

const updateCharts = (pieData, barXAxis, barSeries) => {
  userRegBarOption.value = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: barXAxis },
    yAxis: { type: 'value' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [{
      name: '注册用户数',
      data: barSeries,
      type: 'bar',
      color: '#5470C6'
    }]
  };
  
  taskStatusPieOption.value = {
    tooltip: { trigger: 'item', formatter: '{b} : {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: '任务状态',
      type: 'pie',
      radius: '80%',
      center: ['65%', '50%'],
      data: pieData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  };
};

</script>

<style scoped>
.stat-card {
  text-align: center;
}
.chart {
  height: 350px;
}
</style>