<template>
  <div>
    <h1>用户列表</h1>
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="campusUserId" label="ID" />
      <el-table-column prop="campusNickname" label="昵称" />
      <el-table-column prop="campusEmailAddr" label="邮箱" />
    </el-table>
  </div>
</template>

<script>
import NetWorkApi from '../api/NetWorkApi';

export default {
  name: 'UserList',
  data() {
    return {
      users: []
    };
  },
  async created() {
    try {
      const response = await NetWorkApi.getUsers();
      this.users = response.data;
    } catch (error) {
      console.error('获取用户列表失败:', error);
    }
  }
};
</script>
