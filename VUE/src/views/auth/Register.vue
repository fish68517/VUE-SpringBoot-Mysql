<template>
  <div style="height: 100vh; display:flex; align-items:center; justify-content:center;">
    <el-card style="width: 480px">
      <template #header>
        <div style="font-weight:700">注册</div>
      </template>

      <el-form :model="form" label-width="90px">
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="求职者" value="JOBSEEKER" />
            <el-option label="商家" value="MERCHANT" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>

        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="username" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="password" />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="138xxxx" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="xx@xx.com" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" @click="onRegister">
            注册
          </el-button>
        </el-form-item>

        <el-link @click="$router.push('/login')">返回登录</el-link>
      </el-form>
    </el-card>
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
