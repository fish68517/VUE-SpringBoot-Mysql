// src/store/userStore.js
import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useUserStore = defineStore("user", () => {
  const userInfo = ref(null);
  const token = ref(localStorage.getItem("token") || null);

  const isLogin = computed(() => !!token.value);

  function setUserInfo(info) {
    userInfo.value = info;
  }

  function setToken(newToken) {
    token.value = newToken;
    localStorage.setItem("token", newToken);
  }

  function logout() {
    userInfo.value = null;
    token.value = null;
    localStorage.removeItem("token");
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
