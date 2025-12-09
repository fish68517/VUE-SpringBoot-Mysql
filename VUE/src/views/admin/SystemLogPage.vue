<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>系统安全审计日志</span>
        <el-button @click="fetchLogs" :icon="Refresh" circle></el-button>
      </div>
    </template>

    <div class="filter-container">
        <el-input v-model="searchText" placeholder="搜索操作人或详情..." style="width: 250px;" clearable @input="handleSearch" prefix-icon="Search" />
    </div>

    <el-table :data="filteredLogs" v-loading="loading" stripe style="width: 100%" height="500">
      <el-table-column prop="id" label="ID" width="80" />
      
      <el-table-column prop="operatorName" label="操作员" width="120">
          <template #default="scope">
              <el-tag effect="dark" type="info">{{ scope.row.operatorName }}</el-tag>
          </template>
      </el-table-column>

      <el-table-column prop="action" label="动作类型" width="150">
        <template #default="scope">
          <el-tag :type="getActionTagType(scope.row.action)">{{ scope.row.action }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="details" label="详细描述" min-width="300" show-overflow-tooltip />
      
      <el-table-column prop="ipAddress" label="IP地址" width="140" />
      
      <el-table-column prop="createdAt" label="操作时间" width="180">
          <template #default="scope">
              {{ formatTime(scope.row.createdAt) }}
          </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh, Search } from '@element-plus/icons-vue';
import api from '../../api/NetWorkApi.js';

const logs = ref([]);
const loading = ref(true);
const searchText = ref('');

const filteredLogs = computed(() => {
    if (!searchText.value) return logs.value;
    const lowerSearch = searchText.value.toLowerCase();
    return logs.value.filter(log => 
        (log.operatorName && log.operatorName.toLowerCase().includes(lowerSearch)) ||
        (log.details && log.details.toLowerCase().includes(lowerSearch)) || 
        (log.action && log.action.toLowerCase().includes(lowerSearch))
    );
});

const fetchLogs = async () => {
  loading.value = true;
  try {
    const res = await api.systemLogsApi.list();
    logs.value = res.data || [];
  } catch (error) {
    console.error(error);
    ElMessage.error("日志加载失败");
  } finally {
    loading.value = false;
  }
};

const getActionTagType = (action) => {
    if (action.includes('DELETE')) return 'danger';
    if (action.includes('UPDATE')) return 'warning';
    if (action.includes('CREATE') || action.includes('ADD')) return 'success';
    return '';
};

const formatTime = (timeStr) => {
    if (!timeStr) return '-';
    return timeStr.replace('T', ' ').substring(0, 19);
};

const handleSearch = () => {
    // computed property handles filtering automatically
};

onMounted(fetchLogs);
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filter-container {
    margin-bottom: 20px;
}
</style>