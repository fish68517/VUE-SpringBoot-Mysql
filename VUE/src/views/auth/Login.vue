<template>
  <!-- 
    修改点 1: 
    background: 保持淡绿色渐变
    flex-direction: column;  关键！让标题和卡片垂直对齐，而不是左右排
  -->
  <div style="height: 100vh; display:flex; flex-direction: column; align-items:center; justify-content:center; background: linear-gradient(135deg, #f0f9eb 0%, #d1e9d3 100%);">
    
    <!-- 修改点 2: 新增的大标题 -->
    <div style="margin-bottom: 30px; font-size: 34px; font-weight: 800; color: #2e5c33; letter-spacing: 2px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1);">
      求职招聘系统
    </div>

    <!-- 登录卡片 -->
    <el-card style="width: 420px; box-shadow: 0 8px 16px rgba(0,0,0,0.08); border-radius: 12px;">
      <template #header>
        <div style="font-weight:700; text-align: center; font-size: 18px;">用户登录</div>
      </template>

      <el-form :model="form" label-width="80px" size="large">
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="求职者" value="JOBSEEKER" />
            <el-option label="商家" value="MERCHANT" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>

        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号">
            <!-- 这是一个小细节：可以加图标，如果没有引入图标库可忽略 -->
          </el-input>
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item>
          <!-- 按钮使用 success 绿色主题 -->
          <el-button type="success" style="width:100%; font-weight: bold; letter-spacing: 1px;" :loading="loading" @click="onLogin">
            立即登录
          </el-button>
        </el-form-item>

        <div style="display:flex; justify-content:space-between; padding: 0 10px;">
          <el-link type="success" :underline="false" @click="$router.push('/register')">注册新账号</el-link>
          <!-- 预留找回密码位置，保持对称美观 -->
          <el-link type="info" :underline="false" disabled>忘记密码?</el-link>
        </div>
      </el-form>
    </el-card>
    
    <!-- 底部版权信息 (可选，增加系统完整感) -->
    <div style="margin-top: 40px; color: #889489; font-size: 12px;">
      &copy; 2026 求职招聘平台 版权所有
    </div>

  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { AuthApi } from "@/api/Api"; 

const router = useRouter();
const auth = useAuthStore();
const loading = ref(false);

const form = reactive({
  role: "JOBSEEKER",
  username: "",
  password: "",
});

async function onLogin() {
  loading.value = true;
  try {
    const user = await AuthApi.login(form);
    auth.setUser(user);
    ElMessage.success("登录成功");
    router.push("/app");
  } catch (e) {
    ElMessage.error(e?.message || "登录失败");
  } finally {
    loading.value = false;
  }
}
</script>