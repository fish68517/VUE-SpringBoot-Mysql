<template>
  <el-card>
    <template #header>用户审核管理</template>
    <el-table :data="users" v-loading="loading" style="width: 100%">
      <el-table-column prop="campusUserId" label="ID" width="80" />
      <el-table-column prop="campusNickname" label="昵称" />
      <el-table-column prop="campusEmailAddr" label="邮箱" />
      <el-table-column prop="campusSchoolId" label="学号" />
      <el-table-column prop="campusStatusFlag" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.campusStatusFlag)">
            {{ formatStatus(scope.row.campusStatusFlag) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button
            v-if="scope.row.campusStatusFlag === 0"
            size="small"
            type="success"
            @click="updateUserStatus(scope.row, 1)"
          >
            审核通过
          </el-button>
          <el-button
            v-if="scope.row.campusStatusFlag !== 2"
            size="small"
            type="danger"
            @click="updateUserStatus(scope.row, 2)"
          >
            禁用
          </el-button>
           <el-button
            v-if="scope.row.campusStatusFlag === 2"
            size="small"
            type="warning"
            @click="updateUserStatus(scope.row, 1)"
          >
            启用
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';

const users = ref([]);
const loading = ref(true);

const fetchUsers = async () => {
  loading.value = true;
  try {
    const res = await api.campusUserApi.list();
    users.value = res.data.data || [];
  } catch (error) {
    ElMessage.error("用户列表加载失败");
  } finally {
    loading.value = false;
  }
};

onMounted(fetchUsers);

const updateUserStatus = async (user, status) => {
  try {
    const updatedUser = { ...user, campusStatusFlag: status };
    await api.campusUserApi.update(updatedUser);
    ElMessage.success("状态更新成功！");
    fetchUsers(); // 重新加载数据
  } catch (error) {
    ElMessage.error("操作失败");
    console.error(error);
  }
};

const formatStatus = (status) => {
  if (status === 0) return '待审核';
  if (status === 1) return '已审核';
  if (status === 2) return '已禁用';
  return '未知';
};

const getStatusType = (status) => {
  if (status === 0) return 'warning';
  if (status === 1) return 'success';
  if (status === 2) return 'danger';
  return 'info';
};
</script>