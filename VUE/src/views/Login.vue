<template>
  <el-card class="box">
    <h2>用户登录</h2>

    <el-form :model="form">
      <el-form-item label="用户名">
        <el-input v-model="form.username" />
      </el-form-item>

      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" />
      </el-form-item>

      <el-button type="primary" @click="handleLogin">登录</el-button>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from "vue";
import { login } from "@/api/user";

const form = ref({
  username: "",
  password: ""
});

function handleLogin() {
  login(form.value).then(res => {
    alert("登录成功");
  });
}
</script>

<style scoped>
.box {
  width: 360px;
  margin: 100px auto;
}
</style>
