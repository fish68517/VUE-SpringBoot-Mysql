<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户交互监控</span>
        <div class="header-actions">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索用户或资源名称" 
            style="width: 200px; margin-right: 10px;" 
            clearable
          />
        </div>
      </div>
    </template>

    <el-table :data="filteredRecords" v-loading="loading" style="width: 100%">
      <el-table-column label="用户" width="180">
        <template #default="scope">
          <div class="user-info">
            <el-avatar :size="24" :src="scope.row.userAvatar" style="margin-right: 8px;" />
            <span>{{ scope.row.userName }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="互动资源" min-width="200">
        <template #default="scope">
          <span>{{ scope.row.resourceName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="交互状态" width="180">
        <template #default="scope">
          <el-tag v-if="scope.row.isLiked" type="danger" effect="dark" style="margin-right: 5px;">
            <el-icon><Pointer /></el-icon> 已点赞
          </el-tag>
          <el-tag v-if="scope.row.isCollected" type="warning" effect="dark">
            <el-icon><Star /></el-icon> 已收藏
          </el-tag>
          <el-tag v-if="!scope.row.isLiked && !scope.row.isCollected" type="info">
            无互动
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="actionTime" label="最后操作时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.actionTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-popconfirm title="确定删除这条记录吗？" @confirm="handleDelete(scope.row.actionId)">
            <template #reference>
              <el-button size="small" type="danger" circle>
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Pointer, Star, Delete } from '@element-plus/icons-vue';
import api from '../../api/NetWorkApi.js';

const rawRecords = ref([]);
const loading = ref(false);
const searchKeyword = ref('');

const fetchData = async () => {
  loading.value = true;
  try {
    // 并发获取：交互记录、用户表、资源表
    const [actionRes, userRes, resourceRes] = await Promise.all([
      api.userResourceActionApi.list(),
      api.campusUserApi.list(),
      api.learnResourceApi.list()
    ]);

    const actions = actionRes.data.data || actionRes.data || [];
    const users = userRes.data.data || userRes.data || [];
    const resources = resourceRes.data.data || resourceRes.data || [];

    // 建立映射表
    const userMap = {};
    users.forEach(u => userMap[u.campusUserId] = u);
    
    const resourceMap = {};
    resources.forEach(r => resourceMap[r.learnResourceId] = r);

    // 组装数据
    rawRecords.value = actions.map(item => {
      const u = userMap[item.campusUserId] || { campusNickname: '未知用户', campusAvatarUrl: '' };
      const r = resourceMap[item.learnResourceId] || { resourceNameText: '已删除资源' };
      
      return {
        ...item,
        userName: u.campusNickname,
        userAvatar: u.campusAvatarUrl,
        resourceName: r.resourceNameText
      };
    }).sort((a, b) => new Date(b.actionTime) - new Date(a.actionTime));

  } catch (e) {
    ElMessage.error('数据加载失败');
  } finally {
    loading.value = false;
  }
};

const filteredRecords = computed(() => {
  if (!searchKeyword.value) return rawRecords.value;
  const k = searchKeyword.value.toLowerCase();
  return rawRecords.value.filter(r => 
    r.userName.toLowerCase().includes(k) || 
    r.resourceName.toLowerCase().includes(k)
  );
});

const handleDelete = async (id) => {
  try {
    await api.userResourceActionApi.delete(id);
    ElMessage.success('删除成功');
    fetchData();
  } catch (e) {
    ElMessage.error('删除失败');
  }
};

const formatDateTime = (ts) => ts ? ts.replace('T', ' ').substring(0, 19) : '-';

onMounted(fetchData);
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.user-info { display: flex; align-items: center; }
</style>