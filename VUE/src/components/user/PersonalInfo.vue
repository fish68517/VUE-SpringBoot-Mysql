<template>
  <div class="personal-info-container">
    <div class="page-header">
      <h2>{{ $t('user.profile') || '个人信息' }}</h2>
    </div>

    <el-skeleton v-if="isLoading" :rows="5" animated />

    <div v-else class="info-card">
      <div v-if="!isEditing" class="view-mode">
        <div class="info-grid">
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

        <div class="action-buttons">
          <el-button type="primary" @click="startEdit">
            {{ $t('common.edit') || '编辑' }}
          </el-button>
        </div>
      </div>

      <div v-else class="edit-mode">
        <el-form
          ref="formRef"
          :model="editForm"
          :rules="formRules"
          label-width="100px"
          @submit.prevent="handleSubmit"
        >
          <el-form-item :label="$t('user.name') || '姓名'" prop="name">
            <el-input v-model="editForm.name" clearable />
          </el-form-item>

          <el-form-item :label="$t('auth.email') || '邮箱'" prop="email">
            <el-input v-model="editForm.email" type="email" clearable />
          </el-form-item>

          <el-form-item :label="$t('user.age') || '年龄'" prop="age">
            <el-input-number v-model="editForm.age" :min="0" :max="150" controls-position="right" />
          </el-form-item>

          <el-form-item :label="$t('user.gender') || '性别'" prop="gender">
            <el-select v-model="editForm.gender" clearable>
              <el-option label="男" value="MALE" />
              <el-option label="女" value="FEMALE" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('user.phone') || '电话'" prop="phone">
            <el-input v-model="editForm.phone" clearable />
          </el-form-item>

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

      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        :closable="true"
        @close="errorMessage = ''"
        class="error-alert"
      />
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
import { onMounted, reactive, ref } from 'vue'
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

    const formRules = {
      email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
    }

    const formatGender = (gender) => {
      const genderMap = {
        MALE: '男',
        FEMALE: '女',
        OTHER: '其他'
      }
      return genderMap[gender] || '-'
    }

    const syncUserState = (userData) => {
      userInfo.id = userData.id
      userInfo.username = userData.username || ''
      userInfo.email = userData.email || ''
      userInfo.name = userData.name || ''
      userInfo.age = userData.age ?? null
      userInfo.gender = userData.gender || ''
      userInfo.phone = userData.phone || ''
    }

    const fetchUserProfile = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''

        const currentUser = authService.getUser()
        if (!currentUser?.id) {
          throw new Error('无法获取用户信息')
        }

        const response = await userAPI.getProfile(currentUser.id)
        if (response?.data) {
          syncUserState(response.data)
        }
      } catch (error) {
        console.error('Failed to fetch profile:', error)
        errorMessage.value = error.response?.data?.message || error.message || '获取个人信息失败'
      } finally {
        isLoading.value = false
      }
    }

    const startEdit = () => {
      editForm.name = userInfo.name
      editForm.email = userInfo.email
      editForm.age = userInfo.age
      editForm.gender = userInfo.gender
      editForm.phone = userInfo.phone
      errorMessage.value = ''
      successMessage.value = ''
      isEditing.value = true
    }

    const cancelEdit = () => {
      isEditing.value = false
      errorMessage.value = ''
      formRef.value?.clearValidate()
    }

    const handleSubmit = async () => {
      if (!formRef.value) {
        return
      }

      try {
        await formRef.value.validate()
        isSubmitting.value = true
        errorMessage.value = ''
        successMessage.value = ''

        const currentUser = authService.getUser()
        if (!currentUser?.id) {
          throw new Error('无法获取用户信息')
        }

        const payload = {
          name: editForm.name || null,
          email: editForm.email || null,
          age: editForm.age ?? null,
          gender: editForm.gender || null,
          phone: editForm.phone || null
        }

        const response = await userAPI.updateProfile(payload, currentUser.id)
        if (response?.data) {
          syncUserState(response.data)

          const updatedUser = {
            ...authService.getUser(),
            ...response.data
          }
          authService.setAuth(authService.getToken(), updatedUser, authService.getUserRole())

          isEditing.value = false
          successMessage.value = '个人信息已更新'
          ElMessage.success('个人信息已更新')
        }
      } catch (error) {
        console.error('Failed to update profile:', error)
        errorMessage.value = error.response?.data?.message || error.message || '更新个人信息失败'
      } finally {
        isSubmitting.value = false
      }
    }

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
      formatGender,
      startEdit,
      cancelEdit,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.personal-info-container {
  width: 100%;
  padding: 8px 0 24px;
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
  font-weight: 700;
}

.info-card {
  background: #fff;
  border: 1px solid #e8eef5;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 36px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px 24px;
}

.info-item {
  min-height: 64px;
  padding: 18px 20px;
  border-radius: 12px;
  background: #f8fbff;
  border: 1px solid #edf2f7;
}

.label {
  display: block;
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}

.value {
  color: #1f2d3d;
  font-size: 16px;
  font-weight: 600;
}

.action-buttons {
  margin-top: 28px;
}

.error-alert,
.success-alert {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .info-card {
    padding: 20px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
