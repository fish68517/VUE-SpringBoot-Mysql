<template>
  <div class="doctor-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('admin.doctorManagement') || '医师管理' }}</h2>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        :placeholder="$t('auth.username') || '用户名或医师名'"
        clearable
        style="width: 200px; margin-right: 10px"
        @input="handleSearch"
      />
      <el-button type="primary" @click="fetchDoctors" :loading="isLoading">
        {{ $t('common.search') || '搜索' }}
      </el-button>
      <el-button @click="resetSearch">
        {{ $t('common.reset') || '重置' }}
      </el-button>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="5" animated />

    <!-- 医师列表表格 -->
    <div v-else class="table-container">
      <el-table
        :data="filteredDoctors"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'id', order: 'ascending' }"
      >
        <!-- ID列 -->
        <el-table-column prop="id" label="ID" width="80" sortable />

        <!-- 用户名列 -->
        <el-table-column prop="user.username" :label="$t('auth.username') || '用户名'" width="150" />

        <!-- 医师名称列 -->
        <el-table-column prop="user.name" :label="$t('user.name') || '医师名称'" width="120" />

        <!-- 执业证号列 -->
        <el-table-column prop="licenseNumber" :label="$t('admin.licenseNumber') || '执业证号'" width="150" />

        <!-- 专科列 -->
        <el-table-column prop="specialization" :label="$t('admin.specialization') || '专科'" width="120" />

        <!-- 医院列 -->
        <el-table-column prop="hospital" :label="$t('admin.hospital') || '医院'" width="150" />

        <!-- 认证状态列 -->
        <el-table-column prop="verified" :label="$t('admin.verified') || '认证状态'" width="120">
          <template #default="{ row }">
            <el-tag :type="row.verified ? 'success' : 'info'">
              {{ row.verified ? $t('admin.verified') || '已认证' : $t('admin.unverified') || '未认证' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 创建时间列 -->
        <el-table-column prop="createdAt" :label="$t('common.createdAt') || '创建时间'" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column :label="$t('common.action') || '操作'" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="openEditDialog(row)"
            >
              {{ $t('common.edit') || '编辑' }}
            </el-button>
            <el-popconfirm
              :title="$t('common.confirm') || '确认删除此医师？'"
              @confirm="deleteDoctor(row.id)"
            >
              <template #reference>
                <el-button type="danger" size="small">
                  {{ $t('common.delete') || '删除' }}
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑医师信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="$t('admin.editDoctorInfo') || '编辑医师信息'"
      width="500px"
      @close="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editFormRules"
        label-width="100px"
      >
        <el-form-item label="用户名">
          <span>{{ editForm.username }}</span>
        </el-form-item>

        <el-form-item label="医师名称">
          <span>{{ editForm.name }}</span>
        </el-form-item>

        <el-form-item label="执业证号">
          <span>{{ editForm.licenseNumber }}</span>
        </el-form-item>

        <el-form-item :label="$t('admin.specialization') || '专科'" prop="specialization">
          <el-input
            v-model="editForm.specialization"
            :placeholder="$t('admin.specialization') || '请输入专科'"
          />
        </el-form-item>

        <el-form-item :label="$t('admin.hospital') || '医院'" prop="hospital">
          <el-input
            v-model="editForm.hospital"
            :placeholder="$t('admin.hospital') || '请输入医院名称'"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">
          {{ $t('common.cancel') || '取消' }}
        </el-button>
        <el-button type="primary" @click="submitEditForm" :loading="isSubmitting">
          {{ $t('common.confirm') || '确认' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      @close="errorMessage = ''"
      class="error-alert"
    />

    <!-- 成功提示 -->
    <el-alert
      v-if="successMessage"
      :title="successMessage"
      type="success"
      :closable="true"
      @close="successMessage = ''"
      class="success-alert"
    />
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminAPI } from '../../services/api'

export default {
  name: 'DoctorManagement',
  setup() {
    const editFormRef = ref(null)
    const isLoading = ref(true)
    const isSubmitting = ref(false)
    const editDialogVisible = ref(false)
    const searchQuery = ref('')
    const errorMessage = ref('')
    const successMessage = ref('')

    const doctors = ref([])

    const editForm = reactive({
      id: null,
      username: '',
      name: '',
      licenseNumber: '',
      specialization: '',
      hospital: ''
    })

    // 编辑表单验证规则
    const editFormRules = {
      specialization: [
        { required: true, message: '专科为必填项', trigger: 'blur' }
      ],
      hospital: [
        { required: true, message: '医院为必填项', trigger: 'blur' }
      ]
    }

    // 过滤后的医师列表
    const filteredDoctors = computed(() => {
      if (!searchQuery.value) {
        return doctors.value
      }
      return doctors.value.filter(doctor =>
        (doctor.user && doctor.user.username && doctor.user.username.toLowerCase().includes(searchQuery.value.toLowerCase())) ||
        (doctor.user && doctor.user.name && doctor.user.name.toLowerCase().includes(searchQuery.value.toLowerCase())) ||
        (doctor.licenseNumber && doctor.licenseNumber.toLowerCase().includes(searchQuery.value.toLowerCase())) ||
        (doctor.specialization && doctor.specialization.toLowerCase().includes(searchQuery.value.toLowerCase())) ||
        (doctor.hospital && doctor.hospital.toLowerCase().includes(searchQuery.value.toLowerCase()))
      )
    })

    // 获取所有医师
    const fetchDoctors = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''
        const response = await adminAPI.getDoctors()

        if (response && response.data) {
          doctors.value = response.data
        }
      } catch (error) {
        console.error('获取医师列表失败:', error)
        errorMessage.value = error.response?.data?.message || '获取医师列表失败'
      } finally {
        isLoading.value = false
      }
    }

    // 搜索处理
    const handleSearch = () => {
      // 实时搜索，通过计算属性filteredDoctors实现
    }

    // 重置搜索
    const resetSearch = () => {
      searchQuery.value = ''
    }

    // 打开编辑对话框
    const openEditDialog = (doctor) => {
      editForm.id = doctor.id
      editForm.username = doctor.user?.username || ''
      editForm.name = doctor.user?.name || ''
      editForm.licenseNumber = doctor.licenseNumber || ''
      editForm.specialization = doctor.specialization || ''
      editForm.hospital = doctor.hospital || ''
      editDialogVisible.value = true
    }

    // 重置编辑表单
    const resetEditForm = () => {
      editForm.id = null
      editForm.username = ''
      editForm.name = ''
      editForm.licenseNumber = ''
      editForm.specialization = ''
      editForm.hospital = ''
      if (editFormRef.value) {
        editFormRef.value.resetFields()
      }
    }

    // 提交编辑表单
    const submitEditForm = async () => {
      if (!editFormRef.value) return

      try {
        await editFormRef.value.validate()
        isSubmitting.value = true
        errorMessage.value = ''
        successMessage.value = ''

        const response = await adminAPI.updateDoctor(editForm.id, {
          specialization: editForm.specialization,
          hospital: editForm.hospital
        })

        if (response && response.data) {
          // 更新本地医师列表
          const doctorIndex = doctors.value.findIndex(d => d.id === editForm.id)
          if (doctorIndex !== -1) {
            doctors.value[doctorIndex].specialization = editForm.specialization
            doctors.value[doctorIndex].hospital = editForm.hospital
          }

          successMessage.value = '医师信息已更新'
          ElMessage.success('医师信息已更新')
          editDialogVisible.value = false
        }
      } catch (error) {
        console.error('编辑医师信息失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '编辑医师信息失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '编辑医师信息失败'
        }
      } finally {
        isSubmitting.value = false
      }
    }

    // 删除医师
    const deleteDoctor = async (doctorId) => {
      try {
        errorMessage.value = ''
        successMessage.value = ''

        const response = await adminAPI.deleteDoctor(doctorId)

        if (response && response.code === 200) {
          // 从本地医师列表中移除
          doctors.value = doctors.value.filter(d => d.id !== doctorId)

          successMessage.value = '医师已删除'
          ElMessage.success('医师已删除')
        }
      } catch (error) {
        console.error('删除医师失败:', error)
        errorMessage.value = error.response?.data?.message || '删除医师失败'
      }
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }

    // 页面加载时获取医师列表
    onMounted(() => {
      fetchDoctors()
    })

    return {
      editFormRef,
      isLoading,
      isSubmitting,
      editDialogVisible,
      searchQuery,
      errorMessage,
      successMessage,
      doctors,
      filteredDoctors,
      editForm,
      editFormRules,
      fetchDoctors,
      handleSearch,
      resetSearch,
      openEditDialog,
      resetEditForm,
      submitEditForm,
      deleteDoctor,
      formatDate
    }
  }
}
</script>

<style scoped>
.doctor-management-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.error-alert {
  margin-top: 20px;
}

.success-alert {
  margin-top: 20px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table__header th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #f5f7fa;
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-button--small) {
  padding: 6px 12px;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .doctor-management-container {
    padding: 10px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .search-bar {
    flex-direction: column;
    gap: 8px;
  }

  .search-bar :deep(.el-input) {
    width: 100%;
  }

  .table-container {
    padding: 10px;
    overflow-x: auto;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table__body tr td) {
    padding: 8px 0;
  }
}
</style>
