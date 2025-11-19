<template>
  <el-card class="box">
    <h2>用户注册</h2>

    <el-form :model="form" :rules="rules" ref="formRef">
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

      <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
      <el-link type="primary" @click="goToLogin">已有账号？去登录</el-link>
    </el-form>
  </el-card>
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
.box {
  width: 360px;
  margin: 50px auto;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
}

.el-button {
  width: 100%;
  margin-bottom: 10px;
}

.el-link {
  display: block;
  text-align: center;
  margin-top: 10px;
}
</style>
