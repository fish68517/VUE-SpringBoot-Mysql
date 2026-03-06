// src/utils/request.js
import axios from "axios";
import { useUserStore } from "@/store/userStore";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 5000
});

// 请求拦截器：自动携带 token
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers.Authorization = userStore.token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器：处理错误和 token 过期
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code === 200) {
      return res.data;
    } else if (res.code === 401) {
      // Token 过期或无效
      const userStore = useUserStore();
      userStore.clearToken();
      ElMessage.error("登录已过期，请重新登录");
      window.location.href = "/login";
      return Promise.reject(new Error("Token expired"));
    } else {
      ElMessage.error(res.message || "请求失败");
      return Promise.reject(new Error(res.message || "请求失败"));
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      const userStore = useUserStore();
      userStore.clearToken();
      ElMessage.error("登录已过期，请重新登录");
      window.location.href = "/login";
    } else {
      ElMessage.error(error.message || "网络错误");
    }
    return Promise.reject(error);
  }
);

export default request;
