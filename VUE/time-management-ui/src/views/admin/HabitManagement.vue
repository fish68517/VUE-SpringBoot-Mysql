<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户习惯监管</span>
        <div class="header-actions">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索习惯名称或用户昵称" 
            style="width: 200px; margin-right: 10px;" 
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
      
      <el-table-column prop="habitNameText" label="习惯名称" width="180">
        <template #default="scope">
          <span :style="{ color: scope.row.habitColorHex, fontWeight: 'bold' }">
            {{ scope.row.habitNameText }}
          </span>
        </template>
      </el-table-column>

      <el-table-column prop="habitFrequencyEnum" label="频率" width="100">
        <template #default="scope">
          <el-tag>{{ scope.row.habitFrequencyEnum === 'daily' ? '每天' : '每周' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="habitStreakCount" label="连续打卡" width="100" sortable />
      <el-table-column prop="habitTotalCount" label="总打卡" width="100" sortable />
      
      <el-table-column prop="habitStatusEnum" label="状态" width="100">
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

      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" @click="viewCheckins(scope.row)">
            查看打卡
          </el-button>
          <el-popconfirm title="确定删除这个习惯及其打卡记录吗？" @confirm="handleDelete(scope.row.habitTrackId)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="`打卡记录 - ${currentHabitName}`" width="600px">
      <el-table :data="currentCheckins" height="400">
        <el-table-column prop="checkinDate" label="打卡日期" width="120" sortable />
        <el-table-column prop="checkinTimestamp" label="打卡时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.checkinTimestamp) }}
          </template>
        </el-table-column>
        <el-table-column prop="checkinStatusEnum" label="状态" width="100">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.checkinStatusEnum === 'completed'">完成</el-tag>
            <el-tag type="warning" v-else>跳过</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkinNoteText" label="心得/备注" show-overflow-tooltip />
      </el-table>

      <div class="stat-summary" v-if="currentCheckins.length > 0">
        <p>共打卡 <b>{{ currentCheckins.length }}</b> 次</p>
      </div>
      <div v-else class="empty-text">暂无打卡记录</div>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';

const allHabits = ref([]);
const usersMap = ref({}); // 用于ID转姓名
const loading = ref(false);
const searchKeyword = ref('');

// 弹窗相关
const dialogVisible = ref(false);
const currentHabitName = ref('');
const currentCheckins = ref([]);
const allCheckinsCache = ref([]); // 缓存所有打卡记录，避免重复请求

// 初始化加载
const fetchData = async () => {
  loading.value = true;
  try {
    // 并发请求：习惯列表、用户列表（用于显示名字）、所有打卡记录（如果数据量大建议改为按需请求）
    const [habitsRes, usersRes, checkinsRes] = await Promise.all([
      api.habitTrackApi.list(),
      api.campusUserApi.list(),
      api.habitCheckinApi.list()
    ]);

    const rawHabits = habitsRes.data.data || habitsRes.data || [];
    const users = usersRes.data.data || usersRes.data || [];
    allCheckinsCache.value = checkinsRes.data.data || checkinsRes.data || [];

    // 构建用户映射表: { userId: { name, avatar } }
    users.forEach(u => {
      usersMap.value[u.campusUserId] = {
        name: u.campusNickname,
        avatar: u.campusAvatarUrl
      };
    });

    // 组装习惯数据，加入用户信息
    allHabits.value = rawHabits.map(habit => {
      const user = usersMap.value[habit.campusUserId] || { name: '未知用户', avatar: '' };
      return {
        ...habit,
        userName: user.name,
        userAvatar: user.avatar
      };
    });

  } catch (e) {
    console.error(e);
    ElMessage.error('数据加载失败');
  } finally {
    loading.value = false;
  }
};

// 前端搜索过滤
const filteredHabits = computed(() => {
  if (!searchKeyword.value) return allHabits.value;
  const keyword = searchKeyword.value.toLowerCase();
  return allHabits.value.filter(h => 
    h.habitNameText.toLowerCase().includes(keyword) || 
    h.userName.toLowerCase().includes(keyword)
  );
});

// 查看打卡记录
const viewCheckins = (habit) => {
  currentHabitName.value = habit.habitNameText;
  // 从缓存中筛选当前习惯的打卡记录
  // 注意：实际项目中如果打卡记录非常多，这里应该调用 api.habitCheckinApi.getByHabitId(id)
  currentCheckins.value = allCheckinsCache.value
    .filter(c => c.habitTrackId === habit.habitTrackId)
    .sort((a, b) => new Date(b.checkinDate) - new Date(a.checkinDate)); // 按日期倒序
  
  dialogVisible.value = true;
};

// 删除习惯
const handleDelete = async (id) => {
  try {
    await api.habitTrackApi.delete(id);
    ElMessage.success('删除成功');
    // 重新获取数据刷新列表
    fetchData();
  } catch (e) {
    ElMessage.error('删除失败');
  }
};

// 工具函数：日期格式化
const formatDate = (ts) => {
  if (!ts) return '-';
  return ts.split('T')[0];
};
const formatDateTime = (ts) => {
  if (!ts) return '-';
  return ts.replace('T', ' ').substring(0, 16);
};

onMounted(fetchData);
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.user-info { display: flex; align-items: center; }
.stat-summary { margin-top: 15px; text-align: right; color: #666; }
.empty-text { text-align: center; color: #999; margin: 20px 0; }
</style>