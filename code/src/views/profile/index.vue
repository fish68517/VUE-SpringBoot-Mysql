<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Key, Message, OfficeBuilding, Phone, User } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader/index.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const active = ref('profile')
const profile = reactive({ realName: userStore.displayName, username: userStore.user?.username || 'admin', deptName: userStore.user?.deptName || '系统管理部', phone: '138****8820', email: 'admin@digital-law.gov.cn', remark: '数字法治底座平台管理员' })
const password = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

function saveProfile() { ElMessage.success('个人资料保存成功（Mock）') }
function savePassword() {
  if (!password.oldPassword || !password.newPassword) return ElMessage.warning('请完整填写密码信息')
  if (password.newPassword !== password.confirmPassword) return ElMessage.warning('两次输入的新密码不一致')
  ElMessage.success('密码修改成功（Mock）')
  password.oldPassword = password.newPassword = password.confirmPassword = ''
}
</script>

<template>
  <section class="profile-page">
    <PageHeader title="个人中心" description="维护个人资料与登录安全信息。" />
    <div class="profile-grid">
      <aside class="profile-side page-card">
        <div class="profile-avatar">{{ userStore.displayName.slice(0, 1) }}<span /></div>
        <h2>{{ userStore.displayName }}</h2><p>{{ profile.remark }}</p>
        <div class="identity-list">
          <span><el-icon><OfficeBuilding /></el-icon>{{ profile.deptName }}</span>
          <span><el-icon><Phone /></el-icon>{{ profile.phone }}</span>
          <span><el-icon><Message /></el-icon>{{ profile.email }}</span>
        </div>
        <div class="role-row"><span>所属角色</span><el-tag effect="light">超级管理员</el-tag></div>
      </aside>

      <main class="profile-main page-card">
        <el-tabs v-model="active">
          <el-tab-pane name="profile"><template #label><span class="tab-label"><el-icon><User /></el-icon>基本资料</span></template>
            <el-form :model="profile" label-position="top" class="profile-form">
              <div class="form-grid">
                <el-form-item label="登录账号"><el-input v-model="profile.username" disabled /></el-form-item>
                <el-form-item label="用户姓名"><el-input v-model="profile.realName" /></el-form-item>
                <el-form-item label="所属部门"><el-input v-model="profile.deptName" disabled /></el-form-item>
                <el-form-item label="手机号码"><el-input v-model="profile.phone" /></el-form-item>
                <el-form-item label="电子邮箱"><el-input v-model="profile.email" /></el-form-item>
                <el-form-item label="个人说明" class="wide"><el-input v-model="profile.remark" type="textarea" :rows="4" /></el-form-item>
              </div>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form>
          </el-tab-pane>
          <el-tab-pane name="password"><template #label><span class="tab-label"><el-icon><Key /></el-icon>修改密码</span></template>
            <el-form :model="password" label-position="top" class="password-form">
              <el-form-item label="当前密码"><el-input v-model="password.oldPassword" type="password" show-password placeholder="请输入当前密码" /></el-form-item>
              <el-form-item label="新密码"><el-input v-model="password.newPassword" type="password" show-password placeholder="不少于 8 位，建议包含字母和数字" /></el-form-item>
              <el-form-item label="确认新密码"><el-input v-model="password.confirmPassword" type="password" show-password placeholder="请再次输入新密码" /></el-form-item>
              <el-button type="primary" @click="savePassword">确认修改</el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </main>
    </div>
  </section>
</template>

<style scoped lang="scss">
.profile-grid { display: grid; grid-template-columns: 280px minmax(0, 1fr); gap: 15px; }
.profile-side { padding: 30px 24px; text-align: center; }
.profile-avatar { position: relative; width: 76px; height: 76px; display: grid; place-items: center; margin: 0 auto; border-radius: 50%; background: linear-gradient(135deg, #ef9569, #c54a1e); color: #fff; font-size: 28px; font-weight: 700; box-shadow: 0 9px 25px rgba(216,95,43,.23); }
.profile-avatar span { position: absolute; right: 4px; bottom: 4px; width: 13px; height: 13px; border: 3px solid #fff; border-radius: 50%; background: #36aa7d; }
.profile-side h2 { margin: 16px 0 5px; font-size: 18px; }
.profile-side > p { margin: 0 0 25px; color: #8b93a0; font-size: 11px; }
.identity-list { display: flex; flex-direction: column; gap: 14px; padding: 20px 0; border-top: 1px solid #edf0f3; border-bottom: 1px solid #edf0f3; text-align: left; }
.identity-list span { display: flex; align-items: center; gap: 10px; color: #707987; font-size: 12px; }.identity-list .el-icon { color: #a1a7b1; }
.role-row { display: flex; align-items: center; justify-content: space-between; margin-top: 20px; color: #747c89; font-size: 12px; }
.profile-main { min-height: 520px; padding: 10px 25px 25px; }
.tab-label { display: flex; align-items: center; gap: 6px; }
.profile-form { max-width: 760px; padding-top: 18px; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); column-gap: 24px; }.wide { grid-column: 1 / -1; }
.password-form { max-width: 450px; padding-top: 18px; }
</style>
