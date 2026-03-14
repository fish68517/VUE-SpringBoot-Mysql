<template>
  <div class="user-manage">
    <h1>用户管理</h1>

    <div class="filter-section">
      <div class="search-group">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名/昵称/邮箱/手机号"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-select v-model="roleFilter" placeholder="按类别筛选" clearable style="width: 180px" @change="applyRoleFilter">
          <el-option label="系统管理员" value="ADMIN" />
          <el-option label="商家用户" value="SHOP" />
          <el-option label="普通用户" value="USER" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>

    <div class="table-section">
      <el-table :data="displayList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />

        <el-table-column label="类别" width="120">
          <template #default="{ row }">
            <el-tag :type="roleType(row.role)">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="danger" size="small" @click="handleUpdateStatus(row, 0)">禁用</el-button>
            <el-button v-else type="success" size="small" @click="handleUpdateStatus(row, 1)">启用</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchUserList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as adminApi from "@/api/admin";

const userList = ref([]);
const loading = ref(false);
const searchKeyword = ref("");
const roleFilter = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const displayList = computed(() => {
  if (!roleFilter.value) return userList.value;
  return userList.value.filter((u) => u.role === roleFilter.value);
});

const roleLabel = (role) => {
  if (role === "ADMIN") return "系统管理员";
  if (role === "SHOP") return "商家用户";
  return "普通用户";
};

const roleType = (role) => {
  if (role === "ADMIN") return "danger";
  if (role === "SHOP") return "warning";
  return "info";
};

const fetchUserList = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    };

    const response = searchKeyword.value
      ? await adminApi.searchUser({ ...params, keyword: searchKeyword.value })
      : await adminApi.getUserList(params);

    userList.value = response.content || [];
    total.value = response.totalElements || 0;
  } catch (error) {
    console.error(error);
    ElMessage.error("获取用户列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchUserList();
};

const applyRoleFilter = () => {
  // 本地过滤，不触发后端请求
};

const resetSearch = () => {
  searchKeyword.value = "";
  roleFilter.value = "";
  currentPage.value = 1;
  fetchUserList();
};

const handleUpdateStatus = (row, status) => {
  ElMessageBox.confirm(
    `确认要${status === 1 ? "启用" : "禁用"}用户 "${row.username}" 吗？`,
    "提示",
    {
      confirmButtonText: "确认",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(async () => {
      await adminApi.updateUserStatus(row.id, status);
      ElMessage.success(`用户已${status === 1 ? "启用" : "禁用"}`);
      fetchUserList();
    })
    .catch(() => {});
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  return new Date(dateString).toLocaleString("zh-CN");
};

onMounted(() => {
  fetchUserList();
});
</script>

<style scoped>
.user-manage {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

h1 {
  margin: 0 0 20px;
  font-size: 24px;
  color: #333;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.search-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-group :deep(.el-input) {
  width: 320px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
