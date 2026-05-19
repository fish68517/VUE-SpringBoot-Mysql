<template>
  <div class="profile-container">
    <div class="profile-header">
      <el-button type="primary" text @click="router.push('/')">
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </el-button>
      <h1>用户中心</h1>
    </div>

    <div class="profile-content">
      <div class="profile-sidebar">
        <div
          v-for="item in menuItems"
          :key="item.id"
          :class="['menu-item', { active: activeTab === item.id }]"
          @click="handleMenuClick(item)"
        >
          <el-icon>
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.label }}</span>
        </div>
      </div>

      <div class="profile-main" v-loading="pageLoading">
        <div v-if="activeTab === 'info'" class="tab-content">
          <h2>个人信息</h2>
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="120px"
          >
            <el-form-item label="头像">
              <div class="avatar-upload">
                <img v-if="avatarPreview" :src="avatarPreview" alt="头像" class="avatar-preview" />
                <div v-else class="avatar-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
                <el-upload
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  accept="image/*"
                  :on-change="handleAvatarChange"
                >
                  <el-button type="primary" :loading="avatarLoading">
                    <el-icon><Upload /></el-icon>
                    上传头像
                  </el-button>
                </el-upload>
              </div>
            </el-form-item>

            <el-form-item label="登录手机号">
              <el-input v-model="profileForm.phone" disabled />
            </el-form-item>

            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" maxlength="100" />
            </el-form-item>

            <el-form-item label="联系手机" prop="contactPhone">
              <el-input v-model="profileForm.contactPhone" placeholder="请输入联系手机号" maxlength="20" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" maxlength="100" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="profileLoading">
                保存修改
              </el-button>
              <el-button @click="loadProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

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
              <p><strong>姓名：</strong>{{ profileForm.realName }}</p>
              <p><strong>身份证号：</strong>{{ maskIdNumber(profileForm.idNumber) }}</p>
            </div>
          </div>
          <el-form
            v-else
            ref="realnameFormRef"
            :model="realnameForm"
            :rules="realnameRules"
            label-width="120px"
          >
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="realnameForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="身份证号" prop="idNumber">
              <el-input v-model="realnameForm.idNumber" placeholder="请输入18位身份证号" maxlength="18" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleRealNameAuth" :loading="realnameLoading">
                提交认证
              </el-button>
              <el-button @click="resetRealnameForm">取消</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="activeTab === 'address'" class="tab-content">
          <div class="section-title-row">
            <h2>收货地址</h2>
            <el-button type="primary" @click="openAddressDialog()">
              <el-icon><Plus /></el-icon>
              新增地址
            </el-button>
          </div>

          <div v-if="addresses.length > 0" class="address-list">
            <div v-for="address in addresses" :key="address.id" class="address-item">
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
                <el-button type="primary" link @click="openAddressDialog(address)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteAddress(address.id)">删除</el-button>
                <el-button v-if="!address.isDefault" type="success" link @click="handleSetDefault(address.id)">
                  设为默认
                </el-button>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无收货地址" />
        </div>

        <div v-if="activeTab === 'security'" class="tab-content">
          <h2>账户安全</h2>
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
            class="password-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="showAddressDialog"
      :title="editingAddressId ? '编辑地址' : '新增地址'"
      width="520px"
      @closed="resetAddressForm"
    >
      <el-form ref="addressFormRef" :model="addressForm" :rules="addressRules" label-width="100px">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" maxlength="20" />
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
          <el-input v-model="addressForm.address" placeholder="请输入详细地址" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAddress" :loading="addressLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { ArrowLeft, Box, Key, Location, Picture, Plus, Upload, User } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { useUserStore, type Address, type UserProfile } from '@/stores/user'

interface MenuItem {
  id: string
  label: string
  icon: unknown
  action?: string
}

const router = useRouter()
const userStore = useUserStore()

const menuItems: MenuItem[] = [
  { id: 'info', label: '个人信息', icon: User },
  { id: 'realname', label: '实名认证', icon: Box },
  { id: 'address', label: '收货地址', icon: Location },
  { id: 'security', label: '账户安全', icon: Key },
  { id: 'orders', label: '我的订单', icon: Box, action: 'goToOrders' }
]

const activeTab = ref('info')
const pageLoading = ref(false)
const profileLoading = ref(false)
const avatarLoading = ref(false)
const realnameLoading = ref(false)
const addressLoading = ref(false)
const passwordLoading = ref(false)
const showAddressDialog = ref(false)
const editingAddressId = ref<string | null>(null)

const profileFormRef = ref<FormInstance>()
const realnameFormRef = ref<FormInstance>()
const addressFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const profileForm = reactive({
  id: '',
  phone: '',
  nickname: '',
  avatar: '',
  email: '',
  contactPhone: '',
  realName: '',
  idNumber: '',
  isRealNameVerified: false
})

const addresses = ref<Address[]>([])

const realnameForm = reactive({
  realName: '',
  idNumber: ''
})

const addressForm = reactive<Address>({
  id: '',
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  address: '',
  isDefault: false
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules: FormRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
}

const realnameRules: FormRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { len: 18, message: '身份证号必须为18位', trigger: 'blur' }
  ]
}

const addressRules: FormRules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
const serverBaseUrl = apiBaseUrl.replace(/\/api\/?$/, '')

const avatarPreview = computed(() => {
  if (!profileForm.avatar) return ''
  if (/^https?:\/\//.test(profileForm.avatar)) return profileForm.avatar
  if (profileForm.avatar.startsWith('/images/')) return `${serverBaseUrl}${profileForm.avatar}`
  return `${serverBaseUrl}/images/${profileForm.avatar}`
})

const parseAddresses = (shippingAddress?: string) => {
  if (!shippingAddress) return []
  try {
    const parsed = JSON.parse(shippingAddress)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

const syncUserStore = (data: any) => {
  const userInfo: UserProfile = {
    userId: String(data.id || ''),
    phone: data.phone || '',
    nickname: data.nickname || '',
    avatar: data.avatar || '',
    email: data.email || '',
    contactPhone: data.contactPhone || '',
    shippingAddress: data.shippingAddress || '',
    realName: data.realName || '',
    idNumber: data.idNumber || '',
    isRealNameVerified: Boolean(data.isRealNameVerified),
    addresses: parseAddresses(data.shippingAddress)
  }
  userStore.setUserInfo(userInfo)
}

const fillProfileForm = (data: any) => {
  profileForm.id = String(data.id || '')
  profileForm.phone = data.phone || ''
  profileForm.nickname = data.nickname || ''
  profileForm.avatar = data.avatar || ''
  profileForm.email = data.email || ''
  profileForm.contactPhone = data.contactPhone || data.phone || ''
  profileForm.realName = data.realName || ''
  profileForm.idNumber = data.idNumber || ''
  profileForm.isRealNameVerified = Boolean(data.isRealNameVerified)
  addresses.value = parseAddresses(data.shippingAddress)
  syncUserStore(data)
}

const loadProfile = async () => {
  pageLoading.value = true
  try {
    const response: any = await userApi.getProfile()
    if (response.code === 200) {
      fillProfileForm(response.data || {})
    } else {
      ElMessage.error(response.message || '获取个人信息失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '获取个人信息失败')
  } finally {
    pageLoading.value = false
  }
}

const buildProfilePayload = () => ({
  nickname: profileForm.nickname,
  email: profileForm.email,
  contactPhone: profileForm.contactPhone,
  shippingAddress: JSON.stringify(addresses.value)
})

const handleMenuClick = (item: MenuItem) => {
  if (item.action === 'goToOrders') {
    router.push('/orders')
    return
  }
  activeTab.value = item.id
}

const handleAvatarChange = async (uploadFile: UploadFile) => {
  const rawFile = uploadFile.raw
  if (!rawFile) return
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  if (rawFile.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 10MB')
    return
  }

  avatarLoading.value = true
  try {
    const response: any = await userApi.uploadAvatar(rawFile)
    if (response.code === 200) {
      fillProfileForm(response.data || {})
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(response.message || '头像上传失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '头像上传失败')
  } finally {
    avatarLoading.value = false
  }
}

const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return
    profileLoading.value = true
    try {
      const response: any = await userApi.updateProfile(buildProfilePayload())
      if (response.code === 200) {
        fillProfileForm(response.data || {})
        ElMessage.success('个人信息更新成功')
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '更新失败，请稍后重试')
    } finally {
      profileLoading.value = false
    }
  })
}

const handleRealNameAuth = async () => {
  if (!realnameFormRef.value) return
  await realnameFormRef.value.validate(async (valid) => {
    if (!valid) return
    realnameLoading.value = true
    try {
      const response: any = await userApi.realNameAuth({
        realName: realnameForm.realName,
        idNumber: realnameForm.idNumber
      })
      if (response.code === 200) {
        fillProfileForm(response.data || {})
        resetRealnameForm()
        ElMessage.success('实名认证成功')
      } else {
        ElMessage.error(response.message || '认证失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '认证失败，请稍后重试')
    } finally {
      realnameLoading.value = false
    }
  })
}

const resetRealnameForm = () => {
  realnameForm.realName = ''
  realnameForm.idNumber = ''
}

const openAddressDialog = (address?: Address) => {
  if (address) {
    editingAddressId.value = address.id || null
    Object.assign(addressForm, address)
  } else {
    editingAddressId.value = null
    resetAddressForm()
  }
  showAddressDialog.value = true
}

const resetAddressForm = () => {
  addressForm.id = ''
  addressForm.name = ''
  addressForm.phone = ''
  addressForm.province = ''
  addressForm.city = ''
  addressForm.district = ''
  addressForm.address = ''
  addressForm.isDefault = false
  editingAddressId.value = null
  addressFormRef.value?.clearValidate()
}

const persistAddresses = async (nextAddresses: Address[], successMessage: string) => {
  addressLoading.value = true
  try {
    const response: any = await userApi.updateProfile({
      ...buildProfilePayload(),
      shippingAddress: JSON.stringify(nextAddresses)
    })
    if (response.code === 200) {
      fillProfileForm(response.data || {})
      ElMessage.success(successMessage)
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败，请稍后重试')
  } finally {
    addressLoading.value = false
  }
}

const normalizeDefaultAddress = (list: Address[]) => {
  if (list.length === 0) return list
  const hasDefault = list.some((item) => item.isDefault)
  if (!hasDefault) list[0].isDefault = true
  return list
}

const handleSaveAddress = async () => {
  if (!addressFormRef.value) return
  await addressFormRef.value.validate(async (valid) => {
    if (!valid) return

    const nextAddress: Address = {
      id: editingAddressId.value || Date.now().toString(),
      name: addressForm.name,
      phone: addressForm.phone,
      province: addressForm.province,
      city: addressForm.city,
      district: addressForm.district,
      address: addressForm.address,
      isDefault: addressForm.isDefault
    }

    let nextAddresses = addresses.value.map((item) => ({ ...item }))
    if (nextAddress.isDefault) {
      nextAddresses = nextAddresses.map((item) => ({ ...item, isDefault: false }))
    }

    if (editingAddressId.value) {
      nextAddresses = nextAddresses.map((item) => (item.id === editingAddressId.value ? nextAddress : item))
    } else {
      nextAddresses.push(nextAddress)
    }

    nextAddresses = normalizeDefaultAddress(nextAddresses)
    await persistAddresses(nextAddresses, editingAddressId.value ? '地址更新成功' : '地址添加成功')
    showAddressDialog.value = false
  })
}

const handleDeleteAddress = (addressId: string | undefined) => {
  if (!addressId) return
  ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const nextAddresses = normalizeDefaultAddress(addresses.value.filter((item) => item.id !== addressId))
    await persistAddresses(nextAddresses, '地址删除成功')
  }).catch(() => {})
}

const handleSetDefault = async (addressId: string | undefined) => {
  if (!addressId) return
  const nextAddresses = addresses.value.map((item) => ({
    ...item,
    isDefault: item.id === addressId
  }))
  await persistAddresses(nextAddresses, '默认地址设置成功')
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    passwordLoading.value = true
    try {
      const response: any = await userApi.changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
      if (response.code === 200) {
        ElMessage.success('密码修改成功')
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        passwordFormRef.value?.clearValidate()
      } else {
        ElMessage.error(response.message || '密码修改失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '密码修改失败')
    } finally {
      passwordLoading.value = false
    }
  })
}

const maskIdNumber = (idNumber: string | undefined) => {
  if (!idNumber) return ''
  return `****${idNumber.slice(-4)}`
}

onMounted(() => {
  loadProfile()
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
  display: flex;
  align-items: center;
  gap: 16px;
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
  transition: all 0.2s ease;
  color: #666;
}

.menu-item:hover {
  background: #f5f7fa;
  color: #333;
}

.menu-item.active {
  background: #f0f9ff;
  border-left-color: #409eff;
  color: #409eff;
  font-weight: 500;
}

.profile-main {
  flex: 1;
  min-height: 620px;
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tab-content h2,
.section-title-row h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.tab-content > h2 {
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-preview,
.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
}

.avatar-preview {
  object-fit: cover;
  border: 2px solid #eee;
}

.avatar-placeholder {
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: #bbb;
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
  border-radius: 6px;
  transition: all 0.2s ease;
}

.address-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
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

.address-phone,
.address-detail {
  color: #666;
  font-size: 14px;
}

.address-detail {
  color: #999;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 10px;
  margin-left: 20px;
}

.password-form {
  max-width: 520px;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }

  .profile-sidebar {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
  }

  .menu-item {
    border-left: none;
    border-bottom: 3px solid transparent;
    padding: 12px;
  }

  .menu-item.active {
    border-left: none;
    border-bottom-color: #409eff;
  }

  .profile-main {
    padding: 20px;
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
