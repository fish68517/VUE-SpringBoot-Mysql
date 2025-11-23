// src/store/userStore.js
import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useUserStore = defineStore("user", () => {
  // 1. 修改初始化逻辑：尝试从 localStorage 读取 userInfo
  const initUserInfo = () => {
    try {
      const stored = localStorage.getItem("userInfo");
      return stored ? JSON.parse(stored) : null;
    } catch (e) {
      return null;
    }
  };
  const userInfo = ref(initUserInfo());
  const token = ref(localStorage.getItem("token") || null);

  const isLogin = computed(() => !!token.value);

  function setUserInfo(info) {
    userInfo.value = info;
    localStorage.setItem("userInfo", JSON.stringify(info));
  }

  function setToken(newToken) {
    token.value = newToken;
    localStorage.setItem("token", newToken);
  }

  function logout() {
    userInfo.value = null;
    token.value = null;
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
  }

  function clearToken() {
    token.value = null;
    localStorage.removeItem("token");
  }

  return {
    userInfo,
    token,
    isLogin,
    setUserInfo,
    setToken,
    logout,
    clearToken
  };
});
