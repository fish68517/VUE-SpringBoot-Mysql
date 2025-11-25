好的，这是 “用户管理” (User Management) 页面的完整汉化版本。

主要修改包括：

界面标题：User Management -> 用户管理, Search/Filter -> 搜索与筛选 等。

筛选选项：Search by username -> 按用户名搜索, Filter by role -> 按角色筛选, All Roles -> 所有角色 等。

表格列名：Avatar -> 头像, Username -> 用户名, Role -> 角色, Registration Date -> 注册时间, Student/Content Count -> 学员数/内容数 等。

操作按钮：Edit -> 编辑, Delete -> 删除, Search -> 搜索, Save -> 保存, Cancel -> 取消。

编辑弹窗：Gender -> 性别, Introduction -> 简介, Male/Female/Other -> 男/女/其他。

反馈提示：更新成功、删除成功、加载失败等。

日期格式：改为中文 年-月-日 时:分 格式。

请复制以下代码覆盖：

code
Html
play_circle
download
content_copy
expand_less
<template>
  <div class="user-management">
    <h1>用户管理</h1>

    <!-- 搜索与筛选栏 (Search and Filter Bar) -->
    <el-card class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="按用户名搜索"
          clearable
          style="width: 300px"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="roleFilter"
          placeholder="按角色筛选"
          clearable
          style="width: 200px"
          @change="handleSearch"
        >
          <el-option label="所有角色" value="" />
          <el-option label="普通用户" value="user" />
          <el-option label="教练" value="coach" />
          <el-option label="管理员" value="admin" />
        </el-select>

        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>
    </el-card>

    <!-- 用户列表表格 (Users Table) -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="users"
        style="width: 100%"
        stripe
      >
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              {{ row.username.charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="用户名" min-width="150" />

        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">
              {{ formatRole(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="学员数量" width="130" v-if="showCoachColumns">
          <template #default="{ row }">
            <span v-if="row.role === 'coach'">{{ row.studentCount || 0 }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="内容数量" width="130" v-if="showCoachColumns">
          <template #default="{ row }">
            <span v-if="row.role === 'coach'">{{ row.contentCount || 0 }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
              link
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteUser(row)"
              link
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 (Empty State) -->
      <div v-if="!loading && users.length === 0" class="empty-state">
        <el-empty description="未找到用户" />
      </div>

      <!-- 分页 (Pagination) -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 编辑用户弹窗 (Edit User Dialog) -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑用户"
      width="500px"
      @close="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="120px"
      >
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>

        <el-form-item label="角色">
          <el-input :model-value="formatRole(editForm.role)" disabled />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-select v-model="editForm.gender" placeholder="请选择性别">
            <el-option label="男" value="male" />
            <el-option label="女" value="female" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="简介" prop="intro">
          <el-input
            v-model="editForm.intro"
            type="textarea"
            :rows="4"
            placeholder="请输入用户简介"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Search, Edit, Delete } from '@element-plus/icons-vue';
import { getUsers, updateUser, deleteUser } from '@/api/admin';
import { showSuccess, showError, handleDelete, handleFormSubmit, showLoading } from '@/utils/feedback';

const loading = ref(false);
const saving = ref(false);
const users = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);
const searchQuery = ref('');
const roleFilter = ref('');

const editDialogVisible = ref(false);
const editFormRef = ref(null);
const editForm = ref({
  id: null,
  username: '',
  role: '',
  gender: '',
  intro: ''
});

const editRules = {
  intro: [
    { max: 500, message: '简介不能超过 500 个字符', trigger: 'blur' }
  ]
};

const showCoachColumns = computed(() => {
  return roleFilter.value === 'coach' || roleFilter.value === '';
});

const fetchUsers = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    };

    if (searchQuery.value) {
      params.username = searchQuery.value;
    }

    if (roleFilter.value) {
      params.role = roleFilter.value;
    }

    const data = await getUsers(params);
    
    // Handle both paginated and non-paginated responses
    if (data.content) {
      users.value = data.content;
      total.value = data.totalElements || data.content.length;
    } else if (Array.isArray(data)) {
      users.value = data;
      total.value = data.length;
    } else {
      users.value = [];
      total.value = 0;
    }
  } catch (error) {
    // Error already handled by request interceptor
    console.error('Failed to load users:', error);
    users.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchUsers();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchUsers();
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchUsers();
};

const getRoleTagType = (role) => {
  const types = {
    admin: 'danger',
    coach: 'success',
    user: 'info'
  };
  return types[role] || 'info';
};

const formatRole = (role) => {
  const map = {
    admin: '管理员',
    coach: '教练',
    user: '用户'
  };
  return map[role] || role;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const handleEdit = (user) => {
  editForm.value = {
    id: user.id,
    username: user.username,
    role: user.role,
    gender: user.gender || '',
    intro: user.intro || ''
  };
  editDialogVisible.value = true;
};

const handleSaveEdit = async () => {
  if (!editFormRef.value) return;

  saving.value = true;
  try {
    await handleFormSubmit(
      editFormRef.value,
      async () => {
        await updateUser(editForm.value.id, {
          gender: editForm.value.gender,
          intro: editForm.value.intro
        });
        
        editDialogVisible.value = false;
        await fetchUsers();
      },
      {
        successMessage: '用户更新成功',
        errorMessage: '用户更新失败',
        validationMessage: '请检查表单字段'
      }
    );
  } catch (error) {
    // Error already handled
  } finally {
    saving.value = false;
  }
};

const resetEditForm = () => {
  editForm.value = {
    id: null,
    username: '',
    role: '',
    gender: '',
    intro: ''
  };
  if (editFormRef.value) {
    editFormRef.value.clearValidate();
  }
};

const handleDeleteUser = async (user) => {
  try {
    await handleDelete(
      async () => {
        await deleteUser(user.id);
        
        // Refresh the list
        if (users.value.length === 1 && currentPage.value > 1) {
          currentPage.value--;
        }
        await fetchUsers();
      },
      `用户 "${user.username}"`, // 删除提示文本
      {
        successMessage: '用户删除成功',
        errorMessage: '删除用户失败'
      }
    );
  } catch (error) {
    // Error already handled
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
/* 样式保持不变 */
.user-management {
  padding: 20px;
}

h1 {
  margin-bottom: 24px;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.table-card {
  margin-bottom: 20px;
}

.empty-state {
  padding: 40px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-bar .el-input,
  .filter-bar .el-select,
  .filter-bar .el-button {
    width: 100% !important;
  }
}
</style>