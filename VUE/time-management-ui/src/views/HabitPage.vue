<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="habit in userHabits" :key="habit.habitTrackId">
        <el-card class="habit-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>{{ habit.habitNameText }}</span>
              <el-tag :color="habit.habitColorHex" style="color: white">进行中</el-tag>
            </div>
          </template>
          <div>连续打卡：{{ habit.habitStreakCount }} 天</div>
          <div>总计打卡：{{ habit.habitTotalCount }} 次</div>
          <div class="reminder">提醒时间：{{ habit.habitReminderTime }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../api/NetWorkApi.js';

const currentUserId = 1;
const userHabits = ref([]);
const loading = ref(true);

const fetchHabits = async () => {
  loading.value = true;
  try {
    const response = await api.habitTrackApi.list();
    const allHabits = response.data.data || [];
    userHabits.value = allHabits.filter(habit => habit.campusUserId === currentUserId);
  } catch (error) {
    ElMessage.error('习惯列表加载失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchHabits);
</script>

<style scoped>
.habit-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.reminder {
  margin-top: 10px;
  font-size: 14px;
  color: #999;
}
</style>