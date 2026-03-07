<template>
  <div class="register-container">
    <el-card class="box">
      <h2>用户注册</h2>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%; margin-bottom: 15px;">注册</el-button>
        <div style="text-align: right;">
          <el-link type="primary" @click="goToLogin">已有账号？去登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { register } from "@/api/user";
import { useRouter } from "vue-router";

const router = useRouter();
const formRef = ref(null);
const loading = ref(false);

const form = ref({
  username: "",
  password: "",
  confirmPassword: "",
  email: "",
  phone: ""
});

const validatePassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请输入密码"));
  } else if (value.length < 6) {
    callback(new Error("密码长度不能少于6位"));
  } else {
    callback();
  }
};

const validateConfirmPassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== form.value.password) {
    callback(new Error("两次输入密码不一致"));
  } else {
    callback();
  }
};

const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (value === "") {
    callback(new Error("请输入邮箱"));
  } else if (!emailRegex.test(value)) {
    callback(new Error("邮箱格式不正确"));
  } else {
    callback();
  }
};

const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (value === "") {
    callback(new Error("请输入手机号"));
  } else if (!phoneRegex.test(value)) {
    callback(new Error("手机号格式不正确"));
  } else {
    callback();
  }
};

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度在3-20之间", trigger: "blur" }
  ],
  password: [{ validator: validatePassword, trigger: "blur" }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: "blur" }],
  email: [{ validator: validateEmail, trigger: "blur" }],
  phone: [{ validator: validatePhone, trigger: "blur" }]
};

function handleRegister() {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const { confirmPassword, ...data } = form.value;
        await register(data);
        ElMessage.success("注册成功，请登录");
        router.push("/login");
      } catch (error) {
        ElMessage.error(error.response?.data?.message || "注册失败");
      } finally {
        loading.value = false;
      }
    }
  });
}

function goToLogin() {
  router.push("/login");
}
</script>

<style scoped>
/* 注册背景容器 */
.register-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 与登录页保持一样的背景图，或者用另一张也可以 */
  background-image: url('@/assets/bg.jpg'); 
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

/* 注册卡片样式优化 */
.box {
  width: 450px; /* 注册表单项目多，卡片稍微宽一点点 */
  padding: 20px;
  border-radius: 10px;
  background-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  border: none;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
