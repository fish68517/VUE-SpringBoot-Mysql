<template>
  <!-- 
    1. 背景调整：使用与登录页一致的淡绿色渐变 background: linear-gradient(...)
    2. 布局调整：flex-direction: column 让标题显示在卡片上方
  -->
  <div style="height: 100vh; display:flex; flex-direction: column; align-items:center; justify-content:center; background: linear-gradient(135deg, #f0f9eb 0%, #d1e9d3 100%);">
    
    <!-- 3. 新增的大标题 -->
    <div style="margin-bottom: 30px; font-size: 34px; font-weight: 800; color: #2e5c33; letter-spacing: 2px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1);">
      求职招聘系统
    </div>

    <!-- 注册卡片：增加圆角和阴影 -->
    <el-card style="width: 480px; box-shadow: 0 8px 16px rgba(0,0,0,0.08); border-radius: 12px;">
      <template #header>
        <div style="font-weight:700; text-align: center; font-size: 18px;">注册新账号</div>
      </template>

      <!-- size="large" 让输入框稍微高一点，更好看 -->
      <el-form :model="form" label-width="90px" size="large">
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="求职者" value="JOBSEEKER" />
            <el-option label="商家" value="MERCHANT" />
            <!-- 通常注册不开放管理员，如果需要保留即可 -->
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>

        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item>
          <!-- 按钮改为 success (绿色) -->
          <el-button type="success" style="width:100%; font-weight: bold;" :loading="loading" @click="onRegister">
            立即注册
          </el-button>
        </el-form-item>

        <div style="text-align: right; padding-right: 10px;">
          <span style="font-size: 14px; color: #666; margin-right: 5px;">已有账号?</span>
          <el-link type="success" :underline="false" @click="$router.push('/login')">返回登录</el-link>
        </div>
      </el-form>
    </el-card>

    <!-- 底部版权 (保持统一) -->
    <div style="margin-top: 40px; color: #889489; font-size: 12px;">
      &copy; 2024 求职招聘平台 版权所有
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { AuthApi } from "@/api/Api";

const router = useRouter();
const loading = ref(false);

const form = reactive({
  role: "JOBSEEKER",
  username: "",
  password: "",
  phone: "",
  email: "",
});

async function onRegister() {
  loading.value = true;
  try {
    await AuthApi.register(form);
    ElMessage.success("注册成功，请登录");
    router.push("/login");
  } catch (e) {
    const msg = e?.response?.data || e?.message || "注册失败";
    ElMessage.error(msg);
  } finally {
    loading.value = false;
  }
}
</script>