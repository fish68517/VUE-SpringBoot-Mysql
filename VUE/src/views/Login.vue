<template>
  <el-card class="box">
    <h2>用户登录</h2>

    <el-form :model="form" :rules="rules" ref="formRef">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" />
      </el-form-item>

      <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
      <el-link type="primary" @click="goToRegister">没有账号？去注册</el-link>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { login } from "@/api/user";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/userStore";
import { getShopByUserId } from "@/api/shop"; // 1. 引入新定义的 API

const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);
const loading = ref(false);

const form = ref({
  username: "",
  password: ""
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
};

function handleLogin() {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res = await login(form.value);
        // 打印res
        console.log("登录后实际返回：" + JSON.stringify(res));
        userStore.setUserInfo(res);
        userStore.setToken(res.token);
        ElMessage.success("登录成功");
        if (res.role === "USER") {
          router.push("/user");
        } else if (res.role === "SHOP") {

            try {
              // ★★★ 核心修改：商家登录后，额外请求店铺信息 ★★★
              const shopRes = await getShopByUserId(res.id);
              console.log("获取店铺信息成功:", shopRes);
              
              // 将 shopId, shopName 等关键信息合并到用户信息中
              const fullUserInfo = { 
                ...res, 
                shopId: shopRes.id, 
                shopName: shopRes.name,
                shopStatus: shopRes.status 
              };
              userStore.setUserInfo(fullUserInfo);
              
              ElMessage.success("商家登录成功");
              router.push("/shop");
              
            } catch (shopError) {
              // 假如用户角色是 SHOP 但还没有创建店铺（极少情况，或者是新注册商家）
              console.error("获取店铺信息失败:", shopError);
              // 依然存入基础信息，跳转去创建店铺或显示错误
              userStore.setUserInfo(res);
              // router.push("/shop/create"); // 可以在这里引导去创建店铺
              ElMessage.warning("未找到您的店铺信息，请联系管理员");
            }
        } else if (res.role === "ADMIN") {
          router.push("/admin");
        }
        
      } catch (error) {
        console.log("登录失败：" + error);
        ElMessage.error(error.response?.data?.message || "登录失败");
      } finally {
        loading.value = false;
      }
    }
  });
}

function goToRegister() {
  router.push("/register");
}
</script>

<style scoped>
.box {
  width: 360px;
  margin: 100px auto;
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
