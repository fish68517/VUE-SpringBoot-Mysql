<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人资料</span>
        </div>
      </template>
      
      <el-form
        :model="form"
        label-width="100px"
      >
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-tabs style="margin-top: 20px">
      <el-tab-pane label="浏览历史">
        <el-empty description="暂无浏览历史" />
      </el-tab-pane>
      
      <el-tab-pane label="我的收藏">
        <el-empty description="暂无收藏" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../../api/user'

const form = ref({
  username: '',
  realName: '',
  email: '',
  phone: ''
})

const originalForm = ref({
  username: '',
  realName: '',
  email: '',
  phone: ''
})

onMounted(async () => {
  const user = localStorage.getItem('user')
  if (user) {
    const userInfo = JSON.parse(user)
    form.value = {
      username: userInfo.username,
      realName: userInfo.realName || '',
      email: userInfo.email || '',
      phone: userInfo.phone || ''
    }
    originalForm.value = { ...form.value }
  }
})

const handleSave = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user'))
    const response = await userApi.updateProfile(user.id, {
      realName: form.value.realName,
      email: form.value.email,
      phone: form.value.phone
    })
    
    if (response.code === 0) {
      ElMessage.success('保存成功')
      const updatedUser = { ...user, ...response.data }
      localStorage.setItem('user', JSON.stringify(updatedUser))
      originalForm.value = { ...form.value }
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleCancel = () => {
  form.value = { ...originalForm.value }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
