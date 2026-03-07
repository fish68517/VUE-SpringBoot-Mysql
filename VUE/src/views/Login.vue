<template>
  <div class="login-container">
    <el-card class="box">
      <h2>用户登录</h2>

      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>

        <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%; margin-bottom: 15px;">登录</el-button>
        <div style="text-align: right;">
          <el-link type="primary" @click="goToRegister">没有账号？去注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
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
/* 背景容器样式：占满全屏，居中显示内部卡片 */
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 请替换为真实的图片名称和格式（如 .png 或 .jpg） */
  background-image: url('@/assets/bg.jpg'); 
  background-size: cover; /* 背景图覆盖全屏 */
  background-position: center; /* 背景图居中 */
  background-repeat: no-repeat;
}

/* 登录卡片样式优化 */
.box {
  width: 400px; /* 固定卡片宽度 */
  padding: 20px;
  border-radius: 10px; /* 圆角看起来更柔和 */
  /* 给白色卡片加 90% 透明度，并加上一点阴影让它浮在背景之上 */
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
