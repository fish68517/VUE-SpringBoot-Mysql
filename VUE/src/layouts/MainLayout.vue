<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="border-right: 1px solid #eee">
      <div style="padding: 14px; font-weight: 700">兼职平台</div>

      <el-menu :default-active="active" router>
        <template v-if="role === 'JOBSEEKER'">
          <el-menu-item index="/app">首页</el-menu-item>
          <el-menu-item index="/app/jobs">兼职浏览</el-menu-item>
          <el-menu-item index="/app/my-applications">我的报名</el-menu-item>
          <el-menu-item index="/app/my-profile">我的资料</el-menu-item>
          <el-menu-item index="/app/my-messages">消息通知</el-menu-item>
          <el-menu-item index="/app/my-complaints">举报反馈</el-menu-item>
        </template>

        <template v-else-if="role === 'MERCHANT'">
          <el-menu-item index="/app">首页</el-menu-item>
          <el-menu-item index="/app/merchant-company">企业管理</el-menu-item>
          <el-menu-item index="/app/merchant-jobs">岗位管理</el-menu-item>
          <el-menu-item index="/app/merchant-applications">报名管理</el-menu-item>
          <el-menu-item index="/app/merchant-messages">消息通知</el-menu-item>
        </template>

        <template v-else-if="role === 'ADMIN'">
          <el-menu-item index="/app">首页</el-menu-item>
          <el-menu-item index="/app/admin-users">用户管理</el-menu-item>
          <el-menu-item index="/app/admin-companies">企业审核</el-menu-item>
          <el-menu-item index="/app/admin-complaints">举报工单</el-menu-item>
          <el-menu-item index="/app/admin-messages">消息通知</el-menu-item>
        </template>
      </el-menu>

      <div style="padding: 12px">
        <el-button type="danger" plain style="width: 100%" @click="doLogout">退出登录</el-button>
      </div>
    </el-aside>

    <el-container>
      <el-header style="display:flex; align-items:center; justify-content:space-between; border-bottom:1px solid #eee">
        <div>当前角色：{{ role }}</div>
        <div>用户：{{ user?.username }}</div>
      </el-header>

      <el-main style="padding: 16px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const active = computed(() => route.path);
const role = computed(() => auth.role);
const user = computed(() => auth.user);

function doLogout() {
  auth.logout();
  router.push("/login");
}
</script>
