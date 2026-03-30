<template>
  <div class="habit-page">
    <el-row :gutter="16" class="overview-row">
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <el-statistic title="习惯总数" :value="overviewStats.totalHabits" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <el-statistic title="进行中习惯" :value="overviewStats.activeHabits" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <el-statistic title="累计完成打卡" :value="overviewStats.completedCheckins" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <el-statistic title="平均连续打卡" :value="overviewStats.avgStreak" suffix="天/周" />
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户习惯监管</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索习惯名称或用户昵称"
              style="width: 240px; margin-right: 10px;"
              clearable
            />
          </div>
        </div>
      </template>

      <el-table :data="filteredHabits" v-loading="loading" style="width: 100%">
        <el-table-column label="用户" width="150">
          <template #default="scope">
            <div class="user-info">
              <el-avatar :size="24" :src="scope.row.userAvatar" style="margin-right: 8px;" />
              <span>{{ scope.row.userName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="habitNameText" label="习惯名称" min-width="180">
          <template #default="scope">
            <span :style="{ color: scope.row.habitColorHex || '#409eff', fontWeight: 'bold' }">
              {{ scope.row.habitNameText }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="habitFrequencyEnum" label="频率" width="100">
          <template #default="scope">
            <el-tag>{{ scope.row.habitFrequencyEnum === 'weekly' ? '每周' : '每日' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="提醒时间" width="110">
          <template #default="scope">
            {{ formatReminderTime(scope.row.habitReminderTime) }}
          </template>
        </el-table-column>

        <el-table-column prop="derivedStreak" label="连续打卡" width="110" sortable />
        <el-table-column prop="derivedCompletedCount" label="完成次数" width="110" sortable />
        <el-table-column prop="derivedPeriodCount" label="总天数/周数" width="120" sortable />

        <el-table-column label="完成率" width="110">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.completionRate"
              :stroke-width="10"
              :show-text="true"
            />
          </template>
        </el-table-column>

        <el-table-column prop="habitStatusEnum" label="习惯状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.habitStatusEnum === 'active' ? 'success' : 'info'">
              {{ scope.row.habitStatusEnum === 'active' ? '进行中' : '已归档' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="habitCreateTimestamp" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.habitCreateTimestamp) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="190" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openHabitDetail(scope.row)">
              查看详情
            </el-button>
            <el-popconfirm title="确定删除这个习惯及其打卡记录吗？" @confirm="handleDelete(scope.row.habitTrackId)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" width="1180px" destroy-on-close>
      <template #header>
        <div class="detail-header">
          <div>
            <div class="detail-title">{{ currentHabit?.habitNameText || '习惯详情' }}</div>
            <div class="detail-subtitle">
              {{ currentHabit?.userName || '-' }} · {{ currentHabit?.habitFrequencyEnum === 'weekly' ? '每周习惯' : '每日习惯' }}
            </div>
          </div>
          <el-tag :type="currentHabit?.habitStatusEnum === 'active' ? 'success' : 'info'">
            {{ currentHabit?.habitStatusEnum === 'active' ? '进行中' : '已归档' }}
          </el-tag>
        </div>
      </template>

      <template v-if="currentHabit">
        <el-row :gutter="16" class="detail-stats">
          <el-col :span="6">
            <el-card shadow="hover" class="detail-stat-card">
              <el-statistic title="当前连续打卡" :value="currentHabit.derivedStreak" suffix="次" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="detail-stat-card">
              <el-statistic title="累计完成次数" :value="currentHabit.derivedCompletedCount" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="detail-stat-card">
              <el-statistic title="总天数/周数" :value="currentHabit.derivedPeriodCount" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="detail-stat-card">
              <el-statistic title="目标次数" :value="currentHabit.habitGoalCount || 0" />
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="16" class="detail-content">
          <el-col :span="16">
            <el-card shadow="never" class="trend-card">
              <template #header>
                <div class="section-title">习惯趋势图</div>
              </template>
              <div v-if="trendHasData">
                <v-chart class="trend-chart" :option="trendOption" autoresize />
              </div>
              <div v-else class="empty-text">暂无趋势数据</div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="never" class="setting-card">
              <template #header>
                <div class="section-title">提醒时间设置</div>
              </template>
              <div class="reminder-editor">
                <el-time-picker
                  v-model="reminderTime"
                  value-format="HH:mm:ss"
                  format="HH:mm"
                  placeholder="选择提醒时间"
                  style="width: 100%;"
                  clearable
                />
                <el-button type="primary" :loading="savingReminder" @click="handleReminderSave">
                  保存提醒时间
                </el-button>
              </div>
            </el-card>

            <el-card shadow="never" class="streak-card">
              <template #header>
                <div class="section-title">连续打卡情况</div>
              </template>
              <div v-if="recentPeriods.length" class="streak-list">
                <div v-for="period in recentPeriods" :key="period.key" class="streak-item">
                  <span>{{ period.label }}</span>
                  <el-tag :type="statusMeta(period.summaryStatus).type">{{ statusMeta(period.summaryStatus).label }}</el-tag>
                </div>
              </div>
              <div v-else class="empty-text">暂无打卡记录</div>
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="never" class="records-card">
          <template #header>
            <div class="section-title">每日 / 每周打卡签到记录完成情况</div>
          </template>

          <el-tabs v-model="activeRecordTab">
            <el-tab-pane label="每日记录" name="daily">
              <el-table :data="currentCheckins" max-height="380">
                <el-table-column prop="checkinDate" label="打卡日期" width="120" sortable />
                <el-table-column label="所属周期" width="130">
                  <template #default="scope">
                    {{ getPeriodLabel(scope.row.checkinDate, currentHabit.habitFrequencyEnum) }}
                  </template>
                </el-table-column>
                <el-table-column prop="checkinTimestamp" label="打卡时间" width="180">
                  <template #default="scope">
                    {{ formatDateTime(scope.row.checkinTimestamp) }}
                  </template>
                </el-table-column>
                <el-table-column label="当前状态" width="160">
                  <template #default="scope">
                    <el-select v-model="scope.row.editableStatus" size="small">
                      <el-option label="进行中" value="in_progress" />
                      <el-option label="中断" value="interrupted" />
                      <el-option label="完成" value="completed" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column prop="checkinStreakCount" label="记录连击" width="100" />
                <el-table-column prop="checkinNoteText" label="心得 / 备注" show-overflow-tooltip />
                <el-table-column label="操作" width="120" fixed="right">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="handleCheckinStatusSave(scope.row)">
                      保存
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div v-if="!currentCheckins.length" class="empty-text">暂无每日签到记录</div>
            </el-tab-pane>

            <el-tab-pane label="每周汇总" name="weekly">
              <el-table :data="weeklySummary" max-height="380">
                <el-table-column prop="label" label="周期" width="160" />
                <el-table-column prop="total" label="总记录数" width="100" />
                <el-table-column prop="completed" label="完成" width="100" />
                <el-table-column prop="inProgress" label="进行中" width="100" />
                <el-table-column prop="interrupted" label="中断" width="100" />
                <el-table-column label="完成率" width="120">
                  <template #default="scope">
                    {{ scope.row.total ? `${Math.round((scope.row.completed / scope.row.total) * 100)}%` : '0%' }}
                  </template>
                </el-table-column>
                <el-table-column label="周期状态" min-width="140">
                  <template #default="scope">
                    <el-tag :type="statusMeta(scope.row.summaryStatus).type">
                      {{ statusMeta(scope.row.summaryStatus).label }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <div v-if="!weeklySummary.length" class="empty-text">暂无每周汇总记录</div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart, LineChart } from 'echarts/charts';
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from 'echarts/components';
import VChart from 'vue-echarts';
import api from '../../api/NetWorkApi.js';

use([CanvasRenderer, BarChart, LineChart, GridComponent, LegendComponent, TooltipComponent]);

const HABIT_STATUS_META = {
  completed: { label: '完成', type: 'success' },
  in_progress: { label: '进行中', type: 'warning' },
  interrupted: { label: '中断', type: 'danger' },
};

const allHabits = ref([]);
const usersMap = ref({});
const allCheckinsCache = ref([]);
const loading = ref(false);
const searchKeyword = ref('');
const dialogVisible = ref(false);
const currentHabit = ref(null);
const currentCheckins = ref([]);
const reminderTime = ref('');
const savingReminder = ref(false);
const activeRecordTab = ref('daily');

const normalizeList = (response) => response?.data?.data || response?.data || [];

const normalizeCheckinStatus = (status) => {
  if (status === 'completed') return 'completed';
  if (status === 'in_progress') return 'in_progress';
  if (status === 'interrupted' || status === 'skipped') return 'interrupted';
  return 'in_progress';
};

const statusMeta = (status) => HABIT_STATUS_META[normalizeCheckinStatus(status)] || HABIT_STATUS_META.in_progress;

const getStatusPriority = (status) => {
  const normalized = normalizeCheckinStatus(status);
  if (normalized === 'completed') return 3;
  if (normalized === 'in_progress') return 2;
  return 1;
};

const formatDate = (ts) => {
  if (!ts) return '-';
  return String(ts).split('T')[0];
};

const formatDateTime = (ts) => {
  if (!ts) return '-';
  return String(ts).replace('T', ' ').substring(0, 16);
};

const formatReminderTime = (time) => {
  if (!time) return '未设置';
  return String(time).substring(0, 5);
};

const startOfWeek = (dateString) => {
  const date = new Date(`${dateString}T00:00:00`);
  const day = date.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  date.setDate(date.getDate() + diff);
  return date.toISOString().split('T')[0];
};

const getWeekLabel = (dateString) => {
  const start = startOfWeek(dateString);
  const endDate = new Date(`${start}T00:00:00`);
  endDate.setDate(endDate.getDate() + 6);
  const end = endDate.toISOString().split('T')[0];
  return `${start} 至 ${end}`;
};

const getPeriodKey = (dateString, frequency) => {
  if (!dateString) return '';
  return frequency === 'weekly' ? startOfWeek(dateString) : dateString;
};

const getPeriodLabel = (dateString, frequency) => {
  if (!dateString) return '-';
  return frequency === 'weekly' ? getWeekLabel(dateString) : dateString;
};

const isConsecutivePeriod = (currentKey, nextKey, frequency) => {
  const currentDate = new Date(`${currentKey}T00:00:00`);
  const nextDate = new Date(`${nextKey}T00:00:00`);
  const diff = (currentDate - nextDate) / (1000 * 60 * 60 * 24);
  return frequency === 'weekly' ? diff === 7 : diff === 1;
};

const buildPeriodSummaries = (checkins, frequency) => {
  const summaryMap = new Map();

  checkins.forEach((item) => {
    const key = getPeriodKey(item.checkinDate, frequency);
    const current = summaryMap.get(key) || {
      key,
      label: getPeriodLabel(item.checkinDate, frequency),
      completed: 0,
      inProgress: 0,
      interrupted: 0,
      total: 0,
      summaryStatus: 'in_progress',
      latestDate: item.checkinDate,
    };

    const status = normalizeCheckinStatus(item.checkinStatusEnum);
    current.total += 1;
    current.latestDate = item.checkinDate > current.latestDate ? item.checkinDate : current.latestDate;

    if (status === 'completed') current.completed += 1;
    else if (status === 'interrupted') current.interrupted += 1;
    else current.inProgress += 1;

    const currentPriority = getStatusPriority(current.summaryStatus);
    const nextPriority = getStatusPriority(status);
    if (nextPriority >= currentPriority) {
      current.summaryStatus = status;
    }

    summaryMap.set(key, current);
  });

  return Array.from(summaryMap.values()).sort((a, b) => new Date(a.key) - new Date(b.key));
};

const calcCurrentStreak = (checkins, frequency) => {
  const summaries = buildPeriodSummaries(checkins, frequency).sort((a, b) => new Date(b.key) - new Date(a.key));
  let streak = 0;

  for (let i = 0; i < summaries.length; i += 1) {
    const current = summaries[i];
    if (current.summaryStatus !== 'completed') {
      break;
    }

    if (i > 0 && !isConsecutivePeriod(summaries[i - 1].key, current.key, frequency)) {
      break;
    }

    streak += 1;
  }

  return streak;
};

const decorateHabits = (habits) => {
  return habits.map((habit) => {
    const user = usersMap.value[habit.campusUserId] || { name: '未知用户', avatar: '' };
    const checkins = allCheckinsCache.value.filter((item) => item.habitTrackId === habit.habitTrackId);
    const periodSummaries = buildPeriodSummaries(checkins, habit.habitFrequencyEnum);
    const completedPeriods = periodSummaries.filter((item) => item.summaryStatus === 'completed').length;
    const completedCheckins = checkins.filter((item) => normalizeCheckinStatus(item.checkinStatusEnum) === 'completed').length;
    const streak = calcCurrentStreak(checkins, habit.habitFrequencyEnum);
    const periodCount = periodSummaries.length;

      const hasCheckinData = checkins.length > 0;

      return {
        ...habit,
        userName: user.name,
        userAvatar: user.avatar,
        derivedStreak: hasCheckinData ? streak : (habit.habitStreakCount || 0),
        derivedCompletedCount: hasCheckinData ? completedCheckins : (habit.habitTotalCount || 0),
        derivedPeriodCount: hasCheckinData ? periodCount : (habit.habitTotalCount || 0),
        completionRate: periodCount ? Math.round((completedPeriods / periodCount) * 100) : 0,
      };
  });
};

const fetchData = async (retainHabitId = currentHabit.value?.habitTrackId) => {
  loading.value = true;
  try {
    const [habitsRes, usersRes, checkinsRes] = await Promise.all([
      api.habitTrackApi.list(),
      api.campusUserApi.list(),
      api.habitCheckinApi.list(),
    ]);

    const rawHabits = normalizeList(habitsRes);
    const users = normalizeList(usersRes);

    usersMap.value = {};
    users.forEach((user) => {
      usersMap.value[user.campusUserId] = {
        name: user.campusNickname,
        avatar: user.campusAvatarUrl,
      };
    });

    allCheckinsCache.value = normalizeList(checkinsRes);
    allHabits.value = decorateHabits(rawHabits);

    if (retainHabitId) {
      const latestHabit = allHabits.value.find((item) => item.habitTrackId === retainHabitId);
      if (latestHabit && dialogVisible.value) {
        openHabitDetail(latestHabit, false);
      }
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('习惯数据加载失败');
  } finally {
    loading.value = false;
  }
};

const filteredHabits = computed(() => {
  if (!searchKeyword.value) return allHabits.value;
  const keyword = searchKeyword.value.toLowerCase();
  return allHabits.value.filter((habit) =>
    String(habit.habitNameText || '').toLowerCase().includes(keyword)
    || String(habit.userName || '').toLowerCase().includes(keyword),
  );
});

const overviewStats = computed(() => {
  const totalHabits = allHabits.value.length;
  const activeHabits = allHabits.value.filter((item) => item.habitStatusEnum === 'active').length;
  const completedCheckins = allHabits.value.reduce((sum, item) => sum + (item.derivedCompletedCount || 0), 0);
  const avgStreak = totalHabits
    ? Number((allHabits.value.reduce((sum, item) => sum + (item.derivedStreak || 0), 0) / totalHabits).toFixed(1))
    : 0;

  return {
    totalHabits,
    activeHabits,
    completedCheckins,
    avgStreak,
  };
});

const trendData = computed(() => {
  if (!currentHabit.value) return [];
  const summaries = buildPeriodSummaries(currentCheckins.value, currentHabit.value.habitFrequencyEnum);
  const limit = currentHabit.value.habitFrequencyEnum === 'weekly' ? 12 : 14;
  return summaries.slice(-limit);
});

const trendHasData = computed(() => trendData.value.length > 0);

const trendOption = computed(() => {
  const isDark = document.documentElement.classList.contains('dark');
  const textColor = isDark ? '#d0d5dd' : '#475467';

  return {
    tooltip: { trigger: 'axis' },
    legend: {
      top: 0,
      textStyle: { color: textColor },
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: trendData.value.map((item) => item.label),
      axisLabel: { color: textColor },
      axisLine: { lineStyle: { color: textColor } },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: textColor },
      splitLine: { lineStyle: { color: isDark ? '#344054' : '#eaecf0' } },
    },
    series: [
      {
        name: '完成',
        type: 'bar',
        stack: 'status',
        data: trendData.value.map((item) => item.completed),
        itemStyle: { color: '#67c23a' },
      },
      {
        name: '进行中',
        type: 'bar',
        stack: 'status',
        data: trendData.value.map((item) => item.inProgress),
        itemStyle: { color: '#e6a23c' },
      },
      {
        name: '中断',
        type: 'bar',
        stack: 'status',
        data: trendData.value.map((item) => item.interrupted),
        itemStyle: { color: '#f56c6c' },
      },
      {
        name: '总记录数',
        type: 'line',
        smooth: true,
        data: trendData.value.map((item) => item.total),
        itemStyle: { color: '#409eff' },
        lineStyle: { width: 3, color: '#409eff' },
      },
    ],
  };
});

const recentPeriods = computed(() => {
  if (!currentHabit.value) return [];
  return buildPeriodSummaries(currentCheckins.value, currentHabit.value.habitFrequencyEnum)
    .slice(-8)
    .reverse();
});

const weeklySummary = computed(() => {
  return buildPeriodSummaries(currentCheckins.value, 'weekly').reverse();
});

const openHabitDetail = (habit, openDialog = true) => {
  currentHabit.value = { ...habit };
  reminderTime.value = habit.habitReminderTime || '';
  currentCheckins.value = allCheckinsCache.value
    .filter((item) => item.habitTrackId === habit.habitTrackId)
    .sort((a, b) => new Date(b.checkinDate) - new Date(a.checkinDate))
    .map((item) => ({
      ...item,
      editableStatus: normalizeCheckinStatus(item.checkinStatusEnum),
    }));
  activeRecordTab.value = 'daily';

  if (openDialog) {
    dialogVisible.value = true;
  }
};

const buildHabitPayload = (habit, nextReminderTime) => ({
  habitTrackId: habit.habitTrackId,
  campusUserId: habit.campusUserId,
  habitNameText: habit.habitNameText,
  habitFrequencyEnum: habit.habitFrequencyEnum,
  habitReminderTime: nextReminderTime || null,
  habitStatusEnum: habit.habitStatusEnum,
  habitCreateTimestamp: habit.habitCreateTimestamp,
  habitStreakCount: habit.habitStreakCount,
  habitTotalCount: habit.habitTotalCount,
  habitGoalCount: habit.habitGoalCount,
  habitColorHex: habit.habitColorHex,
  habitIconCode: habit.habitIconCode,
});

const handleReminderSave = async () => {
  if (!currentHabit.value) return;

  savingReminder.value = true;
  try {
    await api.habitTrackApi.update(buildHabitPayload(currentHabit.value, reminderTime.value));
    ElMessage.success('提醒时间已更新');
    await fetchData(currentHabit.value.habitTrackId);
  } catch (error) {
    console.error(error);
    ElMessage.error('提醒时间更新失败');
  } finally {
    savingReminder.value = false;
  }
};

const buildCheckinPayload = (row) => ({
  habitCheckinId: row.habitCheckinId,
  habitTrackId: row.habitTrackId,
  checkinDate: row.checkinDate,
  checkinTimestamp: row.checkinTimestamp,
  checkinStatusEnum: row.editableStatus,
  checkinNoteText: row.checkinNoteText,
  checkinStreakCount: row.checkinStreakCount,
});

const handleCheckinStatusSave = async (row) => {
  try {
    await api.habitCheckinApi.update(buildCheckinPayload(row));
    ElMessage.success('签到状态已更新');
    await fetchData(currentHabit.value?.habitTrackId);
  } catch (error) {
    console.error(error);
    ElMessage.error('签到状态更新失败');
  }
};

const handleDelete = async (habitTrackId) => {
  try {
    await api.habitTrackApi.delete(habitTrackId);
    if (currentHabit.value?.habitTrackId === habitTrackId) {
      dialogVisible.value = false;
      currentHabit.value = null;
      currentCheckins.value = [];
    }
    ElMessage.success('习惯删除成功');
    await fetchData();
  } catch (error) {
    console.error(error);
    ElMessage.error('习惯删除失败');
  }
};

onMounted(fetchData);
</script>

<style scoped>
.habit-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.overview-row,
.detail-stats,
.detail-content {
  margin-bottom: 16px;
}

.overview-card,
.detail-stat-card {
  border: 1px solid var(--app-border-color);
}

.card-header,
.header-actions,
.user-info,
.detail-header,
.streak-item,
.reminder-editor {
  display: flex;
  align-items: center;
}

.card-header,
.detail-header {
  justify-content: space-between;
}

.detail-header {
  gap: 16px;
}

.detail-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--app-text-color);
}

.detail-subtitle,
.empty-text {
  color: var(--app-text-secondary);
}

.detail-subtitle {
  margin-top: 4px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--app-text-color);
}

.trend-card,
.setting-card,
.streak-card,
.records-card {
  border: 1px solid var(--app-border-color);
}

.trend-chart {
  height: 320px;
}

.reminder-editor {
  flex-direction: column;
  gap: 14px;
  align-items: stretch;
}

.streak-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.streak-item {
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 12px;
  background: var(--app-surface-muted);
}

.empty-text {
  text-align: center;
  padding: 24px 0;
}
</style>
