<template>
  <el-card>
    <template #header>用户账户管理</template>
    <el-table :data="users" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="fullName" label="全名" />
      <el-table-column prop="roleId" label="角色" v-if="false"/>
      <el-table-column label="部门">
        <template #default="scope">
          {{ scope.row.department?.name || '未分配' }}
        </template>
      </el-table-column>
      <el-table-column prop="isActive" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
            {{ scope.row.isActive ? '已启用' : '已禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button
            size="small"
            :type="scope.row.isActive ? 'danger' : 'success'"
            @click="toggleUserStatus(scope.row)"
          >
            {{ scope.row.isActive ? '禁用' : '启用' }}
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

const fetchDepartments = async (id) => {
  try {
    const res = await api.departmentsApi.getById(id);
    return res.data || [];
  } catch (error) {
    ElMessage.error("部门列表加载失败");
    return [];
  }
};

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
        role: rolesMap.get(user.roleId) || { role_name: '未知' },
        // 为每个用户创建新对象，包含：
       //原有的用户数据 (...user)
      //添加 role 属性
      //添加 department 属性 (通过 depsMap.get(user.departmentId) 获取)
        //这个新对象被存储在 users.value 中，最终传递给 el-table 的 :data="users" 属性
        department: depsMap.get(user.departmentId) || { name: '未分配' },
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