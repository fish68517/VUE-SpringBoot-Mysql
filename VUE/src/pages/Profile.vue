<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>个人中心</h1>
    </div>

    <div class="profile-content">
      <!-- 左侧菜单 -->
      <div class="profile-sidebar">
        <div
          v-for="item in menuItems"
          :key="item.id"
          :class="['menu-item', { active: activeTab === item.id }]"
          @click="handleMenuClick(item)"
        >
          <i :class="item.icon" />
          <span>{{ item.label }}</span>
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="profile-main">
        <!-- 个人信息编辑 -->
        <div v-if="activeTab === 'info'" class="tab-content">
          <h2>个人信息</h2>
          <el-form :model="profileForm" :rules="profileRules" label-width="120px">
            <!-- 头像 -->
            <el-form-item label="头像">
              <div class="avatar-upload">
                <img v-if="profileForm.avatar" :src="profileForm.avatar" alt="头像" class="avatar-preview" />
                <div v-else class="avatar-placeholder">
                  <i class="el-icon-picture" />
                </div>
                <el-upload
                  action="#"
                  :auto-upload="false"
                  :on-change="handleAvatarChange"
                  accept="image/*"
                >
                  <el-button type="primary" size="small">上传头像</el-button>
                </el-upload>
              </div>
            </el-form-item>

            <!-- 昵称 -->
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
            </el-form-item>

            <!-- 邮箱 -->
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>

            <!-- 手机号（只读） -->
            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone" disabled />
            </el-form-item>

            <!-- 操作按钮 -->
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="profileLoading">
                保存修改
              </el-button>
              <el-button @click="resetProfileForm">取消</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 实名认证 -->
        <div v-if="activeTab === 'realname'" class="tab-content">
          <h2>实名认证</h2>
          <div v-if="profileForm.isRealNameVerified" class="verified-status">
            <el-alert
              title="已认证"
              type="success"
              description="您已完成实名认证，可以进行购票操作"
              :closable="false"
            />
            <div class="verified-info">
              <p><strong>姓名：</strong> {{ profileForm.realName }}</p>
              <p><strong>身份证号：</strong> {{ maskIdNumber(profileForm.idNumber) }}</p>
            </div>
          </div>
          <el-form v-else :model="realnameForm" :rules="realnameRules" label-width="120px">
            <!-- 姓名 -->
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="realnameForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>

            <!-- 身份证号 -->
            <el-form-item label="身份证号" prop="idNumber">
              <el-input v-model="realnameForm.idNumber" placeholder="请输入18位身份证号" />
            </el-form-item>

            <!-- 操作按钮 -->
            <el-form-item>
              <el-button type="primary" @click="handleRealNameAuth" :loading="realnameLoading">
                提交认证
              </el-button>
              <el-button @click="resetRealnameForm">取消</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 收货地址管理 -->
        <div v-if="activeTab === 'address'" class="tab-content">
          <h2>收货地址</h2>
          <el-button type="primary" @click="showAddressDialog = true" class="add-address-btn">
            <i class="el-icon-plus" /> 新增地址
          </el-button>

          <div v-if="profileForm.addresses && profileForm.addresses.length > 0" class="address-list">
            <div v-for="address in profileForm.addresses" :key="address.id" class="address-item">
              <div class="address-info">
                <div class="address-header">
                  <span class="address-name">{{ address.name }}</span>
                  <span class="address-phone">{{ address.phone }}</span>
                  <el-tag v-if="address.isDefault" type="success" size="small">默认地址</el-tag>
                </div>
                <div class="address-detail">
                  {{ address.province }} {{ address.city }} {{ address.district }} {{ address.address }}
                </div>
              </div>
              <div class="address-actions">
                <el-button type="primary" link @click="handleEditAddress(address)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteAddress(address.id)">删除</el-button>
                <el-button v-if="!address.isDefault" type="success" link @click="handleSetDefault(address.id)">
                  设为默认
                </el-button>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <p>暂无收货地址</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址编辑对话框 -->
    <el-dialog
      v-model="showAddressDialog"
      :title="editingAddressId ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form :model="addressForm" :rules="addressRules" label-width="100px">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>

        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>

        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>

        <el-form-item label="详细地址" prop="address">
          <el-input v-model="addressForm.address" placeholder="请输入详细地址" type="textarea" rows="3" />
        </el-form-item>

        <el-form-item label="默认地址">
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAddress" :loading="addressLoading">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore, Address } from '@/stores/user'
import { userApi } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

// 菜单项
const menuItems = [
  { id: 'info', label: '个人信息', icon: 'el-icon-user' },
  { id: 'realname', label: '实名认证', icon: 'el-icon-document' },
  { id: 'address', label: '收货地址', icon: 'el-icon-location' },
  { id: 'orders', label: '我的订单', icon: 'el-icon-shopping-cart-1', action: 'goToOrders' },
]

const activeTab = ref('info')

// 个人信息表单
const profileForm = reactive({
  phone: '',
  nickname: '',
  avatar: '',
  email: '',
  realName: '',
  idNumber: '',
  isRealNameVerified: false,
  addresses: [] as Address[],
})

const profileRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' },
  ],
}

// 实名认证表单
const realnameForm = reactive({
  realName: '',
  idNumber: '',
})

const realnameRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { len: 18, message: '身份证号必须为18位', trigger: 'blur' },
  ],
}

// 地址表单
const addressForm = reactive({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  address: '',
  isDefault: false,
})

const addressRules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' },
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
}

// 加载状态
const profileLoading = ref(false)
const realnameLoading = ref(false)
const addressLoading = ref(false)
const showAddressDialog = ref(false)
const editingAddressId = ref<string | null>(null)

// 初始化表单
const initProfileForm = () => {
  if (userStore.userInfo) {
    profileForm.phone = userStore.userInfo.phone || ''
    profileForm.nickname = userStore.userInfo.nickname || ''
    profileForm.avatar = userStore.userInfo.avatar || ''
    profileForm.email = userStore.userInfo.email || ''
    profileForm.realName = userStore.userInfo.realName || ''
    profileForm.idNumber = userStore.userInfo.idNumber || ''
    profileForm.isRealNameVerified = userStore.userInfo.isRealNameVerified || false
    profileForm.addresses = userStore.userInfo.addresses || []
  }
}

// 重置个人信息表单
const resetProfileForm = () => {
  initProfileForm()
}

// 重置实名认证表单
const resetRealnameForm = () => {
  realnameForm.realName = ''
  realnameForm.idNumber = ''
}

// 处理菜单点击
const handleMenuClick = (item: any) => {
  if (item.action === 'goToOrders') {
    router.push('/orders')
  } else {
    activeTab.value = item.id
  }
}

// 处理头像上传
const handleAvatarChange = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e: any) => {
    profileForm.avatar = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 更新个人信息
const handleUpdateProfile = async () => {
  profileLoading.value = true
  try {
    const response: any = await userApi.updateProfile({
      nickname: profileForm.nickname,
      email: profileForm.email,
      avatar: profileForm.avatar,
    })

    if (response.code === 200) {
      userStore.updateProfile({
        nickname: profileForm.nickname,
        email: profileForm.email,
        avatar: profileForm.avatar,
      })
      ElMessage.success('个人信息更新成功')
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '更新失败，请稍后重试')
  } finally {
    profileLoading.value = false
  }
}

// 实名认证
const handleRealNameAuth = async () => {
  realnameLoading.value = true
  try {
    const response: any = await userApi.realNameAuth({
      realName: realnameForm.realName,
      idNumber: realnameForm.idNumber,
    })

    if (response.code === 200) {
      userStore.updateProfile({
        realName: realnameForm.realName,
        idNumber: realnameForm.idNumber,
        isRealNameVerified: true,
      })
      profileForm.realName = realnameForm.realName
      profileForm.idNumber = realnameForm.idNumber
      profileForm.isRealNameVerified = true
      ElMessage.success('实名认证成功')
      resetRealnameForm()
    } else {
      ElMessage.error(response.message || '认证失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '认证失败，请稍后重试')
  } finally {
    realnameLoading.value = false
  }
}

// 编辑地址
const handleEditAddress = (address: Address) => {
  editingAddressId.value = address.id || null
  addressForm.name = address.name
  addressForm.phone = address.phone
  addressForm.province = address.province
  addressForm.city = address.city
  addressForm.district = address.district
  addressForm.address = address.address
  addressForm.isDefault = address.isDefault
  showAddressDialog.value = true
}

// 保存地址
const handleSaveAddress = async () => {
  addressLoading.value = true
  try {
    if (editingAddressId.value) {
      // 编辑地址
      userStore.updateAddress(editingAddressId.value, {
        name: addressForm.name,
        phone: addressForm.phone,
        province: addressForm.province,
        city: addressForm.city,
        district: addressForm.district,
        address: addressForm.address,
        isDefault: addressForm.isDefault,
      })
      ElMessage.success('地址更新成功')
    } else {
      // 新增地址
      const newAddress: Address = {
        id: Date.now().toString(),
        name: addressForm.name,
        phone: addressForm.phone,
        province: addressForm.province,
        city: addressForm.city,
        district: addressForm.district,
        address: addressForm.address,
        isDefault: addressForm.isDefault,
      }
      userStore.addAddress(newAddress)
      ElMessage.success('地址添加成功')
    }

    // 刷新表单
    initProfileForm()
    showAddressDialog.value = false
    editingAddressId.value = null

    // 重置地址表单
    addressForm.name = ''
    addressForm.phone = ''
    addressForm.province = ''
    addressForm.city = ''
    addressForm.district = ''
    addressForm.address = ''
    addressForm.isDefault = false
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败，请稍后重试')
  } finally {
    addressLoading.value = false
  }
}

// 删除地址
const handleDeleteAddress = (addressId: string | undefined) => {
  if (!addressId) return

  ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      userStore.deleteAddress(addressId)
      initProfileForm()
      ElMessage.success('地址删除成功')
    })
    .catch(() => {})
}

// 设为默认地址
const handleSetDefault = (addressId: string | undefined) => {
  if (!addressId) return

  if (profileForm.addresses) {
    profileForm.addresses.forEach((addr) => {
      addr.isDefault = addr.id === addressId
    })
    userStore.updateProfile({ addresses: profileForm.addresses })
    ElMessage.success('默认地址设置成功')
  }
}

// 脱敏身份证号
const maskIdNumber = (idNumber: string | undefined) => {
  if (!idNumber) return ''
  return `****${idNumber.slice(-4)}`
}

// 页面加载
onMounted(() => {
  initProfileForm()
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.profile-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.profile-content {
  display: flex;
  gap: 20px;
}

.profile-sidebar {
  width: 200px;
  background: white;
  border-radius: 8px;
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 20px;
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: all 0.3s ease;
  color: #666;
}

.menu-item:hover {
  background: #f5f7fa;
  color: #333;
}

.menu-item.active {
  background: #f0f9ff;
  border-left-color: #667eea;
  color: #667eea;
  font-weight: 500;
}

.menu-item i {
  font-size: 16px;
}

.profile-main {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tab-content h2 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #eee;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: #ccc;
  border: 2px dashed #ddd;
}

.verified-status {
  margin-bottom: 20px;
}

.verified-info {
  background: #f0f9ff;
  padding: 15px;
  border-radius: 4px;
  margin-top: 15px;
}

.verified-info p {
  margin: 8px 0;
  color: #333;
}

.add-address-btn {
  margin-bottom: 20px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.address-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.address-name {
  font-weight: 500;
  color: #333;
}

.address-phone {
  color: #666;
  font-size: 14px;
}

.address-detail {
  color: #999;
  font-size: 14px;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 10px;
  margin-left: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }

  .profile-sidebar {
    width: 100%;
    display: flex;
    gap: 0;
  }

  .menu-item {
    flex: 1;
    border-left: none;
    border-bottom: 3px solid transparent;
    padding: 10px;
    text-align: center;
  }

  .menu-item.active {
    border-left: none;
    border-bottom-color: #667eea;
  }

  .address-item {
    flex-direction: column;
  }

  .address-actions {
    margin-left: 0;
    margin-top: 10px;
  }
}
</style>
