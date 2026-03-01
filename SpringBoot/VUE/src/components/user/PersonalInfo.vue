<template>
  <div class="personal-info-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('user.profile') || '个人信息' }}</h2>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="5" animated />

    <!-- 个人信息卡片 -->
    <div v-else class="info-card">
      <!-- 显示模式 -->
      <div v-if="!isEditing" class="view-mode">
        <div class="info-grid">
          <div class="info-item">
            <span class="label">{{ $t('auth.username') || '用户名' }}:</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">{{ $t('user.name') || '姓名' }}:</span>
            <span class="value">{{ userInfo.name || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">{{ $t('auth.email') || '邮箱' }}:</span>
            <span class="value">{{ userInfo.email || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">{{ $t('user.age') || '年龄' }}:</span>
            <span class="value">{{ userInfo.age || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">{{ $t('user.gender') || '性别' }}:</span>
            <span class="value">{{ formatGender(userInfo.gender) }}</span>
          </div>
          <div class="info-item">
            <span class="label">{{ $t('user.phone') || '电话' }}:</span>
            <span class="value">{{ userInfo.phone || '-' }}</span>
          </div>
        </div>

        <!-- 编辑按钮 -->
        <div class="action-buttons">
          <el-button type="primary" @click="startEdit">
            {{ $t('common.edit') || '编辑' }}
          </el-button>
        </div>
      </div>

      <!-- 编辑模式 -->
      <div v-else class="edit-mode">
        <el-form
          ref="formRef"
          :model="editForm"
          :rules="formRules"
          label-width="100px"
          @submit.prevent="handleSubmit"
        >
          <el-form-item :label="$t('user.name') || '姓名'" prop="name">
            <el-input
              v-model="editForm.name"
              :placeholder="$t('user.name') || '姓名'"
              clearable
            />
          </el-form-item>

          <el-form-item :label="$t('auth.email') || '邮箱'" prop="email">
            <el-input
              v-model="editForm.email"
              :placeholder="$t('auth.email') || '邮箱'"
              type="email"
              clearable
            />
          </el-form-item>

          <el-form-item :label="$t('user.age') || '年龄'" prop="age">
            <el-input-number
              v-model="editForm.age"
              :min="0"
              :max="150"
              controls-position="right"
            />
          </el-form-item>

          <el-form-item :label="$t('user.gender') || '性别'" prop="gender">
            <el-select
              v-model="editForm.gender"
              :placeholder="$t('user.gender') || '性别'"
              clearable
            >
              <el-option label="男" value="MALE" />
              <el-option label="女" value="FEMALE" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('user.phone') || '电话'" prop="phone">
            <el-input
              v-model="editForm.phone"
              :placeholder="$t('user.phone') || '电话'"
              clearable
            />
          </el-form-item>

          <!-- 操作按钮 -->
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="isSubmitting">
              {{ $t('common.save') || '保存' }}
            </el-button>
            <el-button @click="cancelEdit">
              {{ $t('common.cancel') || '取消' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>

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
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userAPI } from '../../services/api'
import { authService } from '../../services/auth'

export default {
  name: 'PersonalInfo',
  setup() {
    const formRef = ref(null)
    const isLoading = ref(true)
    const isSubmitting = ref(false)
    const isEditing = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')

    const userInfo = reactive({
      id: null,
      username: '',
      email: '',
      name: '',
      age: null,
      gender: '',
      phone: ''
    })

    const editForm = reactive({
      name: '',
      email: '',
      age: null,
      gender: '',
      phone: ''
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: false, message: '姓名为必填项', trigger: 'blur' }
      ],
      email: [
        { required: false, message: '邮箱为必填项', trigger: 'blur' },
        { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
      ],
      age: [
        { required: false, message: '年龄为必填项', trigger: 'blur' }
      ],
      gender: [
        { required: false, message: '性别为必填项', trigger: 'blur' }
      ],
      phone: [
        { required: false, message: '电话为必填项', trigger: 'blur' }
      ]
    }

    // 获取用户个人信息
    const fetchUserProfile = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''
        
        // 从认证服务获取当前用户ID
        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取用户信息')
        }

        // 调用API获取用户个人信息
        const response = await userAPI.getProfile(currentUser.id)
        
        if (response && response.data) {
          const userData = response.data
          userInfo.id = userData.id
          userInfo.username = userData.username
          userInfo.email = userData.email || ''
          userInfo.name = userData.name || ''
          userInfo.age = userData.age || null
          userInfo.gender = userData.gender || ''
          userInfo.phone = userData.phone || ''
        }
      } catch (error) {
        console.error('获取个人信息失败:', error)
        errorMessage.value = error.response?.data?.message || '获取个人信息失败'
      } finally {
        isLoading.value = false
      }
    }

    // 开始编辑
    const startEdit = () => {
      editForm.name = userInfo.name
      editForm.email = userInfo.email
      editForm.age = userInfo.age
      editForm.gender = userInfo.gender
      editForm.phone = userInfo.phone
      isEditing.value = true
    }

    // 取消编辑
    const cancelEdit = () => {
      isEditing.value = false
      errorMessage.value = ''
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }

    // 提交表单
    const handleSubmit = async () => {
      if (!formRef.value) return

      try {
        await formRef.value.validate()
        isSubmitting.value = true
        errorMessage.value = ''
        successMessage.value = ''

        // 获取当前用户ID
        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取用户信息')
        }

        // 构建更新请求
        const updateRequest = {
          name: editForm.name || null,
          email: editForm.email || null,
          age: editForm.age || null,
          gender: editForm.gender || null,
          phone: editForm.phone || null
        }

        // 调用API更新用户信息
        const response = await userAPI.updateProfile(updateRequest, currentUser.id)

        if (response && response.data) {
          const updatedData = response.data
          userInfo.name = updatedData.name || ''
          userInfo.email = updatedData.email || ''
          userInfo.age = updatedData.age || null
          userInfo.gender = updatedData.gender || ''
          userInfo.phone = updatedData.phone || ''

          // 更新认证服务中的用户信息
          const updatedUser = authService.getUser()
          updatedUser.name = updatedData.name
          updatedUser.email = updatedData.email
          updatedUser.age = updatedData.age
          updatedUser.gender = updatedData.gender
          updatedUser.phone = updatedData.phone
          authService.setAuth(authService.getToken(), updatedUser, authService.getUserRole())

          isEditing.value = false
          successMessage.value = '个人信息已更新'
          ElMessage.success('个人信息已更新')
        }
      } catch (error) {
        console.error('更新个人信息失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '更新个人信息失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '更新个人信息失败'
        }
      } finally {
        isSubmitting.value = false
      }
    }

    // 格式化性别显示
    const formatGender = (gender) => {
      const genderMap = {
        'MALE': '男',
        'FEMALE': '女',
        'OTHER': '其他'
      }
      return genderMap[gender] || '-'
    }

    // 页面加载时获取用户信息
    onMounted(() => {
      fetchUserProfile()
    })

    return {
      formRef,
      isLoading,
      isSubmitting,
      isEditing,
      errorMessage,
      successMessage,
      userInfo,
      editForm,
      formRules,
      startEdit,
      cancelEdit,
      handleSubmit,
      formatGender
    }
  }
}
</script>

<style scoped>
.personal-info-container {
  max-width: 800px;
  margin: 0 auto;
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

.info-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.view-mode {
  display: flex;
  flex-direction: column;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  flex-direction: column;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.info-item .label {
  font-weight: bold;
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.info-item .value {
  color: #333;
  font-size: 16px;
  word-break: break-all;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: flex-start;
}

.edit-mode {
  display: flex;
  flex-direction: column;
}

.error-alert {
  margin-top: 20px;
}

.success-alert {
  margin-top: 20px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  padding: 8px 12px;
}

:deep(.el-select) {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .personal-info-container {
    padding: 10px;
  }

  .info-card {
    padding: 20px;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .info-item {
    padding: 12px;
  }

  .info-item .label {
    font-size: 12px;
  }

  .info-item .value {
    font-size: 14px;
  }
}
</style>
