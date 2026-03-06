<template>
  <div class="user-manage">
    <h1>用户管理</h1>
    
    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="search-group">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名或邮箱"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>

    <!-- 用户列表表格 -->
    <div class="table-section">
      <el-table
        :data="userList"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleDisableUser(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleEnableUser(row)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as adminApi from "@/api/admin";

const userList = ref([]);
const loading = ref(false);
const searchKeyword = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const fetchUserList = async () => {
  loading.value = true;
  try {

     // ★★★ 修改点开始 ★★★
    const params = {
      // 1. 前端页码(1开始) -> 后端页码(0开始)
      page: currentPage.value - 1,
      size: pageSize.value 
    };
    // ★★★ 修改点结束 ★★★
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }
    
    const response = await adminApi.getUserList(params);
    userList.value = response.content || [];
    total.value = response.totalElements || 0;
  } catch (error) {
    ElMessage.error("获取用户列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchUserList();
};

const resetSearch = () => {
  searchKeyword.value = "";
  currentPage.value = 1;
  fetchUserList();
};

const handleDisableUser = (row) => {
  ElMessageBox.confirm(
    `确定要禁用用户 "${row.username}" 吗？`,
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(async () => {
      try {
        await adminApi.updateUserStatus(row.id, { status: 0 });
        ElMessage.success("用户已禁用");
        fetchUserList();
      } catch (error) {
        ElMessage.error("禁用用户失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const handleEnableUser = (row) => {
  ElMessageBox.confirm(
    `确定要启用用户 "${row.username}" 吗？`,
    "确认",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "info"
    }
  )
    .then(async () => {
      try {
        await adminApi.updateUserStatus(row.id, { status: 1 });
        ElMessage.success("用户已启用");
        fetchUserList();
      } catch (error) {
        ElMessage.error("启用用户失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
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
  margin: 0 0 20px 0;
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
  width: 300px;
}

.table-section {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .search-group {
    flex-direction: column;
  }

  .search-group :deep(.el-input) {
    width: 100%;
  }
}
</style>
