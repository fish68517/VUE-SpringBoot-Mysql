<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { ArrowRight, Key, Lock, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const year = new Date().getFullYear()
const form = reactive({ username: 'admin', password: '123456', remember: true })
const rules: FormRules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入登录密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于 6 位', trigger: 'blur' },
  ],
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功，欢迎进入数字法治底座')
    await router.replace(String(route.query.redirect || '/dashboard'))
  } finally {
    loading.value = false
  }
}

function keydown(event: KeyboardEvent) {
  if (event.key === 'Enter') submit()
}

onMounted(() => window.addEventListener('keydown', keydown))
onUnmounted(() => window.removeEventListener('keydown', keydown))
</script>

<template>
  <main class="login-page">
    <section class="login-hero">
      <div class="hero-top">
        <div class="logo-mark"><span>法</span></div>
        <div><strong>数字法治底座</strong><small>DIGITAL RULE OF LAW PLATFORM</small></div>
      </div>

      <div class="hero-copy">
        <span class="eyebrow">DIGITAL GOVERNANCE · RULE OF LAW</span>
        <h1>让法治能力沉淀为<br /><em>可信赖的数字基础设施</em></h1>
        <p>统一认证、事件流转、开放接入、消息协同和全链路审计，构建安全、规范、可复用的数字法治业务底座。</p>
        <div class="ability-list">
          <div><i>01</i><span><b>规范统一</b><small>标准 API 与权限体系</small></span></div>
          <div><i>02</i><span><b>安全可信</b><small>身份鉴权与操作审计</small></span></div>
          <div><i>03</i><span><b>开放协同</b><small>多渠道接入与流程联动</small></span></div>
        </div>
      </div>

      <div class="hero-grid" />
      <div class="hero-ring ring-one" /><div class="hero-ring ring-two" />
      <footer>重庆市数字法治统一能力平台 · 前端 Mock 演示环境</footer>
    </section>

    <section class="login-panel">
      <div class="login-box">
        <div class="security-pill"><el-icon><Lock /></el-icon> 安全登录</div>
        <h2>欢迎登录</h2>
        <p class="login-tip">请输入您的平台账号与密码</p>

        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input v-model="form.username" :prefix-icon="User" placeholder="请输入登录账号" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" :prefix-icon="Key" type="password" show-password placeholder="请输入登录密码" />
          </el-form-item>
          <div class="login-options">
            <el-checkbox v-model="form.remember">记住账号</el-checkbox>
            <el-link type="primary" underline="never">忘记密码？</el-link>
          </div>
          <el-button class="submit-button" type="primary" :loading="loading" @click="submit">
            登录平台 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-form>

        <div class="demo-account">
          <span>演示账号</span>
          <code>admin</code><i>/</i><code>123456</code>
        </div>
        <p class="browser-tip">建议使用 Chrome、Edge 等现代浏览器访问</p>
      </div>
      <div class="panel-footer">© {{ year }} 数字法治底座 · Mock Frontend</div>
    </section>
  </main>
</template>

<style scoped lang="scss">
.login-page {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: minmax(560px, 1.25fr) minmax(480px, 0.75fr);
  background: #fff;
}

.login-hero {
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 35px 54px 28px;
  background:
    radial-gradient(circle at 78% 28%, rgba(240, 147, 101, 0.22), transparent 28%),
    linear-gradient(145deg, #262b35 0%, #1e222b 58%, #29242a 100%);
  color: #fff;
}

.hero-top {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 14px;

  > div:last-child { display: flex; flex-direction: column; }
  strong { font-size: 20px; letter-spacing: 1px; }
  small { margin-top: 3px; color: #8d95a3; font-size: 9px; letter-spacing: 1.6px; }
}

.logo-mark {
  width: 45px;
  height: 45px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 13px;
  background: linear-gradient(135deg, #ef8550, #bd4119);
  box-shadow: 0 10px 32px rgba(216, 95, 43, 0.25);
  font-family: "KaiTi", serif;
  font-size: 27px;
  font-weight: 700;
}

.hero-copy {
  position: relative;
  z-index: 2;
  max-width: 650px;
  margin: auto 0;
}

.eyebrow {
  display: inline-block;
  padding-bottom: 9px;
  border-bottom: 2px solid var(--brand);
  color: #d5835f;
  font-size: 11px;
  font-weight: 650;
  letter-spacing: 2.5px;
}

h1 {
  margin: 28px 0 20px;
  font-size: clamp(36px, 3.2vw, 53px);
  font-weight: 680;
  line-height: 1.32;
  letter-spacing: 1px;

  em { color: #ef8a58; font-style: normal; }
}

.hero-copy > p {
  max-width: 610px;
  margin: 0;
  color: #aeb4bf;
  font-size: 15px;
  line-height: 1.9;
}

.ability-list {
  display: flex;
  gap: 28px;
  margin-top: 42px;

  > div { display: flex; align-items: center; gap: 11px; }
  i { color: #ef8250; font-family: Georgia, serif; font-size: 26px; font-style: normal; }
  span { display: flex; flex-direction: column; gap: 3px; }
  b { font-size: 13px; }
  small { color: #858d9b; font-size: 10px; }
}

.hero-grid {
  position: absolute;
  inset: 0;
  opacity: 0.08;
  background-image: linear-gradient(rgba(255,255,255,.2) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,.2) 1px, transparent 1px);
  background-size: 58px 58px;
  mask-image: linear-gradient(90deg, transparent 10%, #000 80%);
}

.hero-ring {
  position: absolute;
  border: 1px solid rgba(228, 115, 65, 0.18);
  border-radius: 50%;
}

.ring-one { right: -180px; top: 100px; width: 520px; height: 520px; }
.ring-two { right: -85px; top: 200px; width: 320px; height: 320px; }

.login-hero footer { position: relative; z-index: 2; color: #707886; font-size: 10px; letter-spacing: .6px; }

.login-panel {
  position: relative;
  display: grid;
  place-items: center;
  padding: 48px;
  background: #fff;
}

.login-box { width: min(390px, 100%); }

.security-pill {
  width: fit-content;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 22px;
  padding: 5px 10px;
  border-radius: 20px;
  background: #fff0e9;
  color: var(--brand);
  font-size: 11px;
}

h2 { margin: 0; color: #252b37; font-size: 29px; }
.login-tip { margin: 9px 0 30px; color: #989fac; font-size: 13px; }

:deep(.el-input__wrapper) { height: 48px; border-radius: 6px; box-shadow: 0 0 0 1px #dde1e8 inset; }
:deep(.el-form-item) { margin-bottom: 20px; }

.login-options { display: flex; justify-content: space-between; align-items: center; margin: -2px 0 24px; font-size: 12px; }

.submit-button {
  width: 100%;
  height: 48px;
  gap: 8px;
  border-radius: 6px;
  box-shadow: 0 9px 24px rgba(216, 95, 43, .22);
  font-size: 15px;
}

.demo-account {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
  padding: 10px;
  border: 1px dashed #e3e6eb;
  border-radius: 6px;
  background: #fafbfc;
  color: #8c94a0;
  font-size: 11px;

  code { color: #555e6c; font-family: inherit; font-weight: 650; }
  i { color: #c5cad2; font-style: normal; }
}

.browser-tip { margin-top: 20px; color: #b0b5bd; font-size: 10px; text-align: center; }
.panel-footer { position: absolute; bottom: 24px; color: #b0b5bd; font-size: 10px; }
</style>
