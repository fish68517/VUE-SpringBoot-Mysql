<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户成就记录</span>
        <div class="header-actions">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索用户或徽章名称" 
            style="width: 200px; margin-right: 10px;" 
            clearable
          />
          <el-button type="primary" @click="openDialog">
            <el-icon><Trophy /></el-icon> 手动颁发徽章
          </el-button>
        </div>
      </div>
    </template>

    <el-table :data="filteredRecords" v-loading="loading" style="width: 100%">
      <el-table-column label="获奖用户" width="180">
        <template #default="scope">
          <div class="user-info">
            <el-avatar :size="24" :src="scope.row.userAvatar" style="margin-right: 8px;" />
            <span>{{ scope.row.userName }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="获得徽章" width="200">
        <template #default="scope">
          <div class="badge-info">
            <el-image :src="scope.row.badgeIcon" style="width: 30px; height: 30px; margin-right: 8px;" />
            <el-tag type="warning" effect="plain">{{ scope.row.badgeName }}</el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="achieveTimestamp" label="获得时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.achieveTimestamp) }}
        </template>
      </el-table-column>

      <el-table-column prop="achieveConditionText" label="达成说明/备注" show-overflow-tooltip />

      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-popconfirm title="确定撤销该用户的此徽章吗？" @confirm="handleDelete(scope.row.userAchieveId)">
            <template #reference>
              <el-button size="small" type="danger">撤销</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="手动颁发徽章" width="500px">
      <el-form :model="form" label-width="100px">
        
        <el-form-item label="选择用户">
          <el-select 
            v-model="form.campusUserId" 
            filterable 
            placeholder="搜索并选择用户" 
            style="width: 100%"
          >
            <el-option
              v-for="user in users"
              :key="user.campusUserId"
              :label="user.campusNickname + ' (' + user.campusSchoolId + ')'"
              :value="user.campusUserId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="选择徽章">
          <el-select 
            v-model="form.achievementId" 
            placeholder="请选择徽章" 
            style="width: 100%"
          >
            <el-option
              v-for="ach in achievements"
              :key="ach.achievementId"
              :label="ach.achieveNameText"
              :value="ach.achievementId"
            >
              <span style="float: left">{{ ach.achieveNameText }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ ach.achieveTypeEnum }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="颁发理由">
          <el-input 
            v-model="form.achieveConditionText" 
            type="textarea" 
            placeholder="例如：系统管理员手动奖励" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定颁发</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Trophy } from '@element-plus/icons-vue';
import api from '../../api/NetWorkApi.js';

// 数据源
const rawRecords = ref([]);
const users = ref([]);
const achievements = ref([]);
const loading = ref(false);
const searchKeyword = ref('');

// 弹窗控制
const dialogVisible = ref(false);
const form = reactive({
  campusUserId: null,
  achievementId: null,
  achieveConditionText: '管理员手动颁发'
});

// 初始化数据加载
const fetchData = async () => {
  loading.value = true;
  try {
    // 并发请求：关联表、用户表、徽章表
    const [relRes, userRes, achRes] = await Promise.all([
      api.userAchieveRelApi.list(),
      api.campusUserApi.list(),
      api.achievementApi.list()
    ]);

    const records = relRes.data.data || relRes.data || [];
    users.value = userRes.data.data || userRes.data || [];
    achievements.value = achRes.data.data || achRes.data || [];

    // 构建映射表以便快速查找
    const userMap = {};
    users.value.forEach(u => userMap[u.campusUserId] = u);
    
    const achMap = {};
    achievements.value.forEach(a => achMap[a.achievementId] = a);

    // 组装显示数据
    rawRecords.value = records.map(r => {
      const u = userMap[r.campusUserId] || { campusNickname: '未知用户', campusAvatarUrl: '' };
      const a = achMap[r.achievementId] || { achieveNameText: '已删除徽章', achieveIconUrl: '' };
      
      return {
        ...r,
        userName: u.campusNickname,
        userAvatar: u.campusAvatarUrl,
        badgeName: a.achieveNameText,
        badgeIcon: a.achieveIconUrl
      };
    }).sort((a, b) => new Date(b.achieveTimestamp) - new Date(a.achieveTimestamp)); // 按时间倒序

  } catch (e) {
    ElMessage.error('数据加载失败');
    console.error(e);
  } finally {
    loading.value = false;
  }
};

// 搜索过滤
const filteredRecords = computed(() => {
  if (!searchKeyword.value) return rawRecords.value;
  const k = searchKeyword.value.toLowerCase();
  return rawRecords.value.filter(r => 
    r.userName.toLowerCase().includes(k) || 
    r.badgeName.toLowerCase().includes(k)
  );
});

// 打开弹窗
const openDialog = () => {
  form.campusUserId = null;
  form.achievementId = null;
  form.achieveConditionText = '管理员手动颁发';
  dialogVisible.value = true;
};

// 提交颁发
const handleSubmit = async () => {
  if (!form.campusUserId || !form.achievementId) {
    ElMessage.warning('请选择用户和徽章');
    return;
  }
  try {
    await api.userAchieveRelApi.create(form);
    ElMessage.success('颁发成功');
    dialogVisible.value = false;
    fetchData(); // 刷新列表
  } catch (e) {
    ElMessage.error('操作失败');
  }
};

// 撤销徽章
const handleDelete = async (id) => {
  try {
    await api.userAchieveRelApi.delete(id);
    ElMessage.success('撤销成功');
    fetchData();
  } catch (e) {
    ElMessage.error('撤销失败');
  }
};

const formatDateTime = (ts) => ts ? ts.replace('T', ' ').substring(0, 16) : '-';

onMounted(fetchData);
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-actions { display: flex; align-items: center; }
.user-info, .badge-info { display: flex; align-items: center; }
</style>