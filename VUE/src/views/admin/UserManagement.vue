<template>
  <div class="user-management">
    <div style="display: flex; align-items: center; margin-bottom: 24px;">
      <el-button @click="$router.back()" style="margin-right: 16px;">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h1 style="margin: 0; color: #303133;">用户管理</h1>
    </div>

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

        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.role === 'coach'"
              type="warning"
              size="small"
              @click="handleAudit(row)"
              link
            >
              <el-icon><Warning /></el-icon>
              审核资质
            </el-button>
            
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

      <div v-if="!loading && users.length === 0" class="empty-state">
        <el-empty description="未找到用户" />
      </div>

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

    <el-dialog
      v-model="auditDialogVisible"
      title="教练资格审核"
      width="600px"
    >
      <div v-loading="auditLoading" class="audit-container">
        <div v-if="selectedCoach" class="coach-info">
          <div class="info-section">
            <p><strong>用户名：</strong>{{ selectedCoach.username }}</p>
            <p><strong>简介：</strong>{{ selectedCoach.intro || '暂无简介' }}</p>
          </div>

          <el-divider>资质审核</el-divider>

          <div v-if="coachCertifications && coachCertifications.length > 0">
            <el-card v-for="cert in coachCertifications" :key="cert.id" class="cert-card">
              <div class="cert-header">
                <span class="cert-name">{{ cert.name || '未命名证书' }}</span>
                <el-tag :type="getCertStatusType(cert.status)" size="small">
                  {{ formatCertStatus(cert.status) }}
                </el-tag>
              </div>
              <div class="cert-body">
                <p><strong>发证机构：</strong>{{ cert.issuingAuthority || '未知' }}</p>
                <p v-if="cert.issueDate"><strong>颁发日期：</strong>{{ cert.issueDate }}</p>
                <p v-if="cert.expiryDate"><strong>到期日期：</strong>{{ cert.expiryDate }}</p>
                <p v-if="cert.createdAt"><strong>提交时间：</strong>{{ formatDate(cert.createdAt) }}</p>
                
                <div class="cert-image" v-if="cert.imageUrl">
                  <el-image 
                    :src="getFullImageUrl(cert.imageUrl)" 
                    :preview-src-list="[getFullImageUrl(cert.imageUrl)]" 
                    fit="contain" 
                    class="preview-img"
                  />
                </div>
              </div>
              
              <div class="cert-actions">
                <el-button 
                  type="success" 
                  size="small" 
                  :disabled="cert.status === 1" 
                  @click="updateCertStatus(cert.id, 1)"
                >
                  通过
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  :disabled="cert.status === 2" 
                  @click="updateCertStatus(cert.id, 2)"
                >
                  拒绝
                </el-button>
              </div>
            </el-card>
          </div>
          <div v-else class="empty-certs">
            <el-empty description="该教练尚未上传资质证明" />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="auditDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Edit, Delete, ArrowLeft, Warning } from '@element-plus/icons-vue';
import { getUsers, updateUser, deleteUser } from '@/api/admin';
import { getCoachCertificationById } from '@/api/coach';
import request from '@/utils/request';
import { showSuccess, showError, handleDelete, handleFormSubmit } from '@/utils/feedback';

const router = useRouter();

const loading = ref(false);
const saving = ref(false);
const users = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);
const searchQuery = ref('');
const roleFilter = ref('');

// 编辑相关状态
const editDialogVisible = ref(false);
const editFormRef = ref(null);
const editForm = ref({
  id: null,
  username: '',
  role: '',
  gender: '',
  intro: ''
});

// 审核相关状态
const auditDialogVisible = ref(false);
const auditLoading = ref(false);
const selectedCoach = ref(null);
const coachCertifications = ref([]);

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
    
    // 兼容多种返回结构
    const list = data.data || data.content || (Array.isArray(data) ? data : []);
    users.value = list;
    total.value = data.totalElements || list.length || 0;
  } catch (error) {
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
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 拼接完整的图片URL，解决相对路径无法显示的问题
const getFullImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
  return `${baseUrl}${url}`;
};

/* --- 编辑用户逻辑 --- */
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
    // 错误已拦截
  } finally {
    saving.value = false;
  }
};

const resetEditForm = () => {
  editForm.value = { id: null, username: '', role: '', gender: '', intro: '' };
  if (editFormRef.value) {
    editFormRef.value.clearValidate();
  }
};

/* --- 删除用户逻辑 --- */
const handleDeleteUser = async (user) => {
  try {
    await handleDelete(
      async () => {
        await deleteUser(user.id);
        if (users.value.length === 1 && currentPage.value > 1) {
          currentPage.value--;
        }
        await fetchUsers();
      },
      `用户 "${user.username}"`, 
      {
        successMessage: '用户删除成功',
        errorMessage: '删除用户失败'
      }
    );
  } catch (error) {
    // 错误已拦截
  }
};

/* --- 资格审核逻辑 --- */
const handleAudit = async (row) => {
  selectedCoach.value = row;
  auditDialogVisible.value = true;
  auditLoading.value = true;
  coachCertifications.value = [];
  
  try {
    const res = await getCoachCertificationById(row.id);
    // 从后端拿数据。如果接口直接返回的是证书数组 (如你的说明)，这里会兼容处理
    // 如果返回格式是 { data: { certifications: [...] } }，也能适配
    console.log(res);
    let certs = res ||  [];
    
    // 如果接口直接返回了一个对象数组，直接赋值
    if (Array.isArray(res)) {
        certs = res;
    } else  {
        // 如果只返回了单个证书对象
        certs = [res];
    }
    
    coachCertifications.value = certs;
  } catch (error) {
    showError('获取教练资质信息失败');
  } finally {
    auditLoading.value = false;
  }
};

// 更新状态逻辑 (传递数字: 1为通过, 2为拒绝)
const updateCertStatus = async (certId, status) => {
  try {
    // 调用后端管理员修改状态接口（请确保此处 URL 和后端接口对应）
    await request({
      url: `/api/coach/certifications/${certId}/status`,
      method: 'put',
      data: { status: status } 
    });
    
    showSuccess(`资质已${status === 1 ? '通过' : '拒绝'}`);
    
    // 刷新数据
    handleAudit(selectedCoach.value);
  } catch (error) {
    showError('审批操作失败');
  }
};

// 兼容数字状态判断 (0: 待审核, 1: 已通过, 2: 已拒绝)
const getCertStatusType = (status) => {
  const map = { 
    0: 'warning', 1: 'success', 2: 'danger',
    'pending': 'warning', 'approved': 'success', 'rejected': 'danger' // 兼容旧文本状态
  };
  return map[status] || 'info';
};

const formatCertStatus = (status) => {
  const map = { 
    0: '待审核', 1: '已通过', 2: '已拒绝',
    'pending': '待审核', 'approved': '已通过', 'rejected': '已拒绝' // 兼容旧文本状态
  };
  return map[status] || '未知';
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.user-management {
  padding: 20px;
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

/* 审核弹窗样式 */
.audit-container {
  min-height: 200px;
}
.info-section p {
  margin: 8px 0;
  color: #606266;
}
.cert-card {
  margin-bottom: 16px;
}
.cert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}
.cert-name {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}
.cert-body p {
  margin: 6px 0;
  font-size: 14px;
  color: #606266;
}
.cert-image {
  margin-top: 12px;
  text-align: center;
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 10px;
}
.preview-img {
  max-width: 100%;
  max-height: 200px;
}
.cert-actions {
  margin-top: 16px;
  text-align: right;
}
.empty-certs {
  padding: 30px 0;
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