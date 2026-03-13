<template>
  <div class="login-container">
    <el-card class="box">
      <h2>系统登录</h2>

      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="用户登录" name="USER"></el-tab-pane>
        <el-tab-pane label="商家登录" name="SHOP"></el-tab-pane>
      </el-tabs>

      <el-form :model="form" :rules="rules" ref="formRef" style="margin-top: 20px;">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>

        <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%; margin-bottom: 15px;">
          登录
        </el-button>
        <div style="text-align: right;">
          <el-link type="primary" @click="goToRegister">没有账号？去注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { login } from "@/api/user";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/store/userStore";
import { getShopByUserId } from "@/api/shop";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const formRef = ref(null);
const loading = ref(false);

// 新增：当前选中的登录身份
const activeTab = ref("USER");

const form = ref({
  username: "",
  password: ""
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
};

// 监听如果有从注册页面传来的角色，默认选中对应的 tab
onMounted(() => {
  if (route.query.role) {
    activeTab.value = route.query.role;
  }
});

function handleLogin() {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // 部分后端在登录时可能也需要知道 role，这里带上它。
        // 如果后端登录接口不需要 role 参数，多传也不会报错。
        const loginData = { ...form.value, role: activeTab.value };
        const res = await login(loginData);
        console.log("登录后实际返回：" + JSON.stringify(res));

        // ★★★ 核心修改：前端身份校验机制 ★★★
        // 确保登录身份和当前选择的 Tab 相匹配（管理员放行）
        if (res.role !== 'ADMIN' && res.role !== activeTab.value) {
          userStore.clearToken(); // 清除可能写入的非法 token
          ElMessage.error(
            activeTab.value === 'SHOP' 
            ? "该账号不是商家账号，请切换到普通用户登录" 
            : "该账号是商家账号，请切换到商家登录"
          );
          return;
        }

        userStore.setUserInfo(res);
        userStore.setToken(res.token);
        
        if (res.role === "USER") {
          ElMessage.success("用户登录成功");
          router.push("/user");
        } else if (res.role === "SHOP") {
            try {
              const shopRes = await getShopByUserId(res.id);
              console.log("获取店铺信息成功:", shopRes);
              
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
              console.error("获取店铺信息失败:", shopError);
              userStore.setUserInfo(res);
              ElMessage.warning("未找到您的店铺信息，请联系管理员或前往创建店铺");
              // 如果需要引导创建店铺，可在此处放开注释
              // router.push("/shop/create"); 
            }
        } else if (res.role === "ADMIN") {
          ElMessage.success("管理员登录成功");
          router.push("/admin");
        }
        
      } catch (error) {
        console.log("登录失败：" + error);
        ElMessage.error(error.response?.data?.message || "账号或密码错误");
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
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('@/assets/bg.jpg'); 
  background-size: cover; 
  background-position: center; 
  background-repeat: no-repeat;
}

.box {
  width: 400px; 
  padding: 20px;
  border-radius: 10px; 
  background-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  border: none;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
</style>