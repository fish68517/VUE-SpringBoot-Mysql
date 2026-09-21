<template>
  <view class="login-page">
    <view class="login-art">
      <view class="login-brand">
        ≈
        <text>郑州城市智慧供水</text>
      </view>
      <view class="login-story">
        <text class="login-small">ZHENGZHOU · SMART WATER</text>
        <text class="login-title">
          让城市供水
          <br />
          清晰可见。
        </text>
        <text class="login-description">
          从一张图感知运行，以一个流程闭环处置。
          <br />
          连接每一处设施，关注每一滴水。
        </text>
        <view class="login-lines">
          <view />
          <view />
          <view />
          <view />
        </view>
        <text class="login-caption">供水运行 / 分区计量 / 业务协同</text>
      </view>
      <text class="login-bottom">城市智慧供水管理系统</text>
    </view>
    <view class="login-form">
      <view class="login-box">
        <text class="login-welcome">欢迎登录</text>
        <text class="subtle">进入您的智慧水务工作空间</text>
        <view class="form-field">
          <text class="field-label">账号</text>
          <input class="field" v-model="username" placeholder="请输入账号" />
        </view>
        <view class="form-field">
          <text class="field-label">密码</text>
          <input class="field" v-model="password" password placeholder="请输入密码" @confirm="login" />
        </view>
        <text v-if="error" class="login-error">{{ error }}</text>
        <button class="button login-submit" :disabled="busy" @click="login">
          {{ busy ? '正在登录…' : '登 录' }}
        </button>
        <view class="login-demo">
          <text class="subtle">选择身份，自动填入账号</text>
          <view class="role-options">
            <view
              v-for="u in examples"
              :key="u.id"
              :class="['choice', { active: username === u.username }]"
              @click="fill(u)"
            >
              {{ roleName(u.roleId) }}
            </view>
          </view>
          <text class="subtle">业务记录保存在当前设备</text>
        </view>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useDemo } from '../../stores/demo'
import { users, roles } from '../../repositories/seed'
import { safeRedirect, loginDestination } from '../../navigation/routeMap'
const demo = useDemo(),
  username = ref(''),
  password = ref(''),
  busy = ref(false),
  error = ref(''),
  redirect = ref('')
const examples = [users[0], users[1], users[users.length - 1]]
const roleName = (id: string) => roles.find((r) => r.id === id)?.name
const fill = (u: (typeof users)[number]) => {
  username.value = u.username
  password.value = u.password
  error.value = ''
}
onLoad((q: any) => {
  redirect.value = safeRedirect(q?.redirect ? decodeURIComponent(q.redirect) : '')
  if (demo.user) uni.reLaunch({ url: loginDestination(redirect.value) })
})
async function login() {
  if (busy.value) return
  busy.value = true
  const r = await demo.run((e) => e.login(username.value, password.value))
  busy.value = false
  if (!r.success) {
    error.value = r.error.message
    return
  }
  uni.reLaunch({ url: loginDestination(redirect.value) })
}
</script>
<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  background: white;
}
.login-art {
  background: radial-gradient(ellipse at 20% 50%, #15466c, #091f35 75%);
  padding: 45px 60px;
  color: #dff6ff;
  position: relative;
  overflow: hidden;
}
.login-brand {
  display: flex;
  gap: 14px;
  align-items: center;
  font-size: 44px;
  color: #51d7ef;
}
.login-brand text {
  font-size: 22px;
  letter-spacing: 3px;
  color: #e1f5ff;
}
.login-story {
  margin-top: 95px;
  position: relative;
  z-index: 1;
}
.login-small {
  font-size: 10px;
  letter-spacing: 4px;
  color: #6c9bb6;
}
.login-title {
  display: block;
  font-size: 51px;
  line-height: 1.5;
  margin: 25px 0;
  font-weight: 600;
  letter-spacing: 4px;
}
.login-description {
  font-size: 14px;
  line-height: 2.1;
  color: #84a8c2;
}
.login-caption {
  font-size: 11px;
  color: #7fb3d0;
  letter-spacing: 3px;
}
.login-lines {
  height: 110px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 20px 0;
}
.login-lines view {
  width: 70px;
  height: 70px;
  border: 1px solid #2c6b8b;
  transform: rotate(45deg);
  background: linear-gradient(120deg, #143e5e, #0c2a41);
  box-shadow: 0 0 20px #114868;
}
.login-lines view:nth-child(2) {
  height: 95px;
  width: 95px;
  border-color: #51d7ef;
}
.login-bottom {
  position: absolute;
  bottom: 35px;
  font-size: 10px;
  color: #557b97;
  letter-spacing: 2px;
}
.login-form {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 45px;
}
.login-box {
  width: 370px;
}
.login-welcome {
  font-size: 29px;
  font-weight: 600;
  display: block;
  margin-bottom: 10px;
}
.login-box > .subtle {
  display: block;
  margin-bottom: 36px;
}
.login-box .field {
  height: 46px;
}
.login-submit {
  width: 100%;
  margin-top: 27px;
  height: 47px;
}
.login-demo {
  border-top: 1px solid #edf2f7;
  margin-top: 36px;
  padding-top: 23px;
}
.role-options {
  display: flex;
  gap: 10px;
  margin: 14px 0 17px;
}
.login-error {
  font-size: 12px;
  color: #ee4656;
  display: block;
}
@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }
  .login-art {
    padding: 25px;
  }
  .login-story,
  .login-bottom {
    display: none;
  }
  .login-brand text {
    font-size: 19px;
  }
  .login-form {
    padding: 45px 30px;
    align-items: flex-start;
  }
  .login-box {
    width: 100%;
    max-width: 400px;
  }
}
</style>
