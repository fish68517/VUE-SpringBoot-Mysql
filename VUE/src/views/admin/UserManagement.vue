<template>
  <div class="user-management">
    <h1>User Management</h1>

    <!-- Search and Filter Bar -->
    <el-card class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="Search by username"
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
          placeholder="Filter by role"
          clearable
          style="width: 200px"
          @change="handleSearch"
        >
          <el-option label="All Roles" value="" />
          <el-option label="User" value="user" />
          <el-option label="Coach" value="coach" />
          <el-option label="Admin" value="admin" />
        </el-select>

        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          Search
        </el-button>
      </div>
    </el-card>

    <!-- Users Table -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="users"
        style="width: 100%"
        stripe
      >
        <el-table-column label="Avatar" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              {{ row.username.charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="Username" min-width="150" />

        <el-table-column label="Role" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">
              {{ row.role }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Registration Date" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="Student Count" width="130" v-if="showCoachColumns">
          <template #default="{ row }">
            <span v-if="row.role === 'coach'">{{ row.studentCount || 0 }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="Content Count" width="130" v-if="showCoachColumns">
          <template #default="{ row }">
            <span v-if="row.role === 'coach'">{{ row.contentCount || 0 }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
              link
            >
              <el-icon><Edit /></el-icon>
              Edit
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteUser(row)"
              link
            >
              <el-icon><Delete /></el-icon>
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Empty State -->
      <div v-if="!loading && users.length === 0" class="empty-state">
        <el-empty description="No users found" />
      </div>

      <!-- Pagination -->
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

    <!-- Edit User Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="Edit User"
      width="500px"
      @close="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="120px"
      >
        <el-form-item label="Username">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>

        <el-form-item label="Role">
          <el-input v-model="editForm.role" disabled />
        </el-form-item>

        <el-form-item label="Gender" prop="gender">
          <el-select v-model="editForm.gender" placeholder="Select gender">
            <el-option label="Male" value="male" />
            <el-option label="Female" value="female" />
            <el-option label="Other" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="Introduction" prop="intro">
          <el-input
            v-model="editForm.intro"
            type="textarea"
            :rows="4"
            placeholder="Enter user introduction"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="saving">
          Save
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
    { max: 500, message: 'Introduction cannot exceed 500 characters', trigger: 'blur' }
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

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
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
        successMessage: 'User updated successfully',
        errorMessage: 'Failed to update user',
        validationMessage: 'Please check the form fields'
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
      `user "${user.username}"`,
      {
        successMessage: 'User deleted successfully',
        errorMessage: 'Failed to delete user'
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
