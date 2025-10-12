<template>
  <el-card>
    <template #header>用户账户管理</template>
    <el-table :data="users" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="full_name" label="全名" />
      <el-table-column prop="role.role_name" label="角色" />
      <el-table-column prop="department.name" label="部门" />
      <el-table-column prop="is_active" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.is_active ? 'success' : 'danger'">
            {{ scope.row.is_active ? '已启用' : '已禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button
            size="small"
            :type="scope.row.is_active ? 'danger' : 'success'"
            @click="toggleUserStatus(scope.row)"
          >
            {{ scope.row.is_active ? '禁用' : '启用' }}
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
    const [usersRes, rolesRes, depsRes] = await Promise.all([
        api.usersApi.list(),
        api.rolesApi.list(),
        api.departmentsApi.list(),
    ]);
    
    const allUsers = usersRes.data || [];
    const rolesMap = new Map((rolesRes.data || []).map(r => [r.id, r]));
    const depsMap = new Map((depsRes.data || []).map(d => [d.id, d]));

    users.value = allUsers.map(user => ({
        ...user,
        role: rolesMap.get(user.role_id) || { role_name: '未知' },
        department: depsMap.get(user.department_id) || { name: '未分配' },
    }));

  } catch (error) {
    ElMessage.error("用户列表加载失败");
  } finally {
    loading.value = false;
  }
};

onMounted(fetchUsers);

const toggleUserStatus = async (user) => {
  try {
    const updatedUser = { ...user, is_active: !user.is_active };
    await api.usersApi.update(updatedUser);
    ElMessage.success("状态更新成功！");
    fetchUsers();
  } catch (error) {
    ElMessage.error("操作失败");
    console.error(error);
  }
};
</script>