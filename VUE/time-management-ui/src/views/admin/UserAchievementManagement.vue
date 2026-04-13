<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <div>
          <div class="header-title">{{ labels.title }}</div>
          <div class="header-subtitle">{{ labels.subtitle }}</div>
        </div>
        <el-input
          v-model="searchKeyword"
          :placeholder="labels.searchPlaceholder"
          style="width: 240px"
          clearable
        />
      </div>
    </template>

    <el-alert
      :title="labels.ruleNotice"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    />

    <el-table :data="filteredRecords" v-loading="loading" style="width: 100%">
      <el-table-column :label="labels.userColumn" width="180">
        <template #default="scope">
          <div class="user-info">
            <el-avatar :size="24" :src="scope.row.userAvatar" style="margin-right: 8px" />
            <span>{{ scope.row.userName }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column :label="labels.badgeColumn" width="220">
        <template #default="scope">
          <div class="badge-info">
            <el-image :src="scope.row.badgeIcon" style="width: 30px; height: 30px; margin-right: 8px" />
            <el-tag type="warning" effect="plain">{{ scope.row.badgeName }}</el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="achieveTimestamp" :label="labels.timeColumn" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.achieveTimestamp) }}
        </template>
      </el-table-column>

      <el-table-column prop="achieveConditionText" :label="labels.conditionColumn" show-overflow-tooltip />

      <el-table-column :label="labels.actionColumn" width="120">
        <template #default="scope">
          <el-popconfirm :title="labels.revokeConfirm" @confirm="handleDelete(scope.row.userAchieveId)">
            <template #reference>
              <el-button size="small" type="danger">{{ labels.revokeButton }}</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';

const labels = {
  title: '\u7528\u6237\u6210\u5c31\u8bb0\u5f55',
  subtitle: '\u6210\u5c31\u5fbd\u7ae0\u6539\u4e3a\u7528\u6237\u8fbe\u6210\u76ee\u6807\u540e\u81ea\u52a8\u83b7\u53d6\uff0c\u7ba1\u7406\u5458\u4ec5\u67e5\u770b\u8bb0\u5f55\u4e0e\u64a4\u9500\u3002',
  searchPlaceholder: '\u641c\u7d22\u7528\u6237\u6216\u5fbd\u7ae0\u540d\u79f0',
  ruleNotice: '\u5f53\u524d\u89c4\u5219\uff1a\u7528\u6237\u5728 Android \u7aef\u4e60\u60ef\u6253\u5361\u7d2f\u8ba1\u6216\u8fde\u7eed\u8fbe\u5230 5 \u5929\u540e\uff0c\u7cfb\u7edf\u4f1a\u81ea\u52a8\u5199\u5165\u6210\u5c31\u8bb0\u5f55\u3002',
  userColumn: '\u83b7\u5956\u7528\u6237',
  badgeColumn: '\u83b7\u5f97\u5fbd\u7ae0',
  timeColumn: '\u83b7\u5f97\u65f6\u95f4',
  conditionColumn: '\u8fbe\u6210\u8bf4\u660e',
  actionColumn: '\u64cd\u4f5c',
  revokeConfirm: '\u786e\u5b9a\u64a4\u9500\u8be5\u7528\u6237\u7684\u6b64\u5fbd\u7ae0\u5417\uff1f',
  revokeButton: '\u64a4\u9500',
  unknownUser: '\u672a\u77e5\u7528\u6237',
  deletedBadge: '\u5df2\u5220\u9664\u5fbd\u7ae0',
  loadFailed: '\u6570\u636e\u52a0\u8f7d\u5931\u8d25',
  revokeSuccess: '\u64a4\u9500\u6210\u529f',
  revokeFailed: '\u64a4\u9500\u5931\u8d25'
};

const rawRecords = ref([]);
const users = ref([]);
const achievements = ref([]);
const loading = ref(false);
const searchKeyword = ref('');

const fetchData = async () => {
  loading.value = true;
  try {
    const [relRes, userRes, achRes] = await Promise.all([
      api.userAchieveRelApi.list(),
      api.campusUserApi.list(),
      api.achievementApi.list()
    ]);

    const records = relRes.data.data || relRes.data || [];
    users.value = userRes.data.data || userRes.data || [];
    achievements.value = achRes.data.data || achRes.data || [];

    const userMap = {};
    users.value.forEach(user => {
      userMap[user.campusUserId] = user;
    });

    const achievementMap = {};
    achievements.value.forEach(achievement => {
      achievementMap[achievement.achievementId] = achievement;
    });

    rawRecords.value = records
      .map(record => {
        const user = userMap[record.campusUserId] || {
          campusNickname: labels.unknownUser,
          campusAvatarUrl: ''
        };
        const achievement = achievementMap[record.achievementId] || {
          achieveNameText: labels.deletedBadge,
          achieveIconUrl: ''
        };

        return {
          ...record,
          userName: user.campusNickname,
          userAvatar: user.campusAvatarUrl,
          badgeName: achievement.achieveNameText,
          badgeIcon: achievement.achieveIconUrl
        };
      })
      .sort((left, right) => new Date(right.achieveTimestamp) - new Date(left.achieveTimestamp));
  } catch (error) {
    ElMessage.error(labels.loadFailed);
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const filteredRecords = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase();
  if (!keyword) {
    return rawRecords.value;
  }
  return rawRecords.value.filter(record =>
    (record.userName || '').toLowerCase().includes(keyword) ||
    (record.badgeName || '').toLowerCase().includes(keyword)
  );
});

const handleDelete = async id => {
  try {
    await api.userAchieveRelApi.delete(id);
    ElMessage.success(labels.revokeSuccess);
    fetchData();
  } catch (error) {
    ElMessage.error(labels.revokeFailed);
  }
};

const formatDateTime = timestamp => (timestamp ? timestamp.replace('T', ' ').substring(0, 16) : '-');

onMounted(fetchData);
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}

.user-info,
.badge-info {
  display: flex;
  align-items: center;
}
</style>
