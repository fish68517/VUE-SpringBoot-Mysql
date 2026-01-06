// src/api/Api.js
// 目的：把前端 Vue 与后端 SpringBoot Controller 的所有 CRUD 接口对接（联调优先）
//
// 约定：后端接口前缀为 /api
// - /api/users
// - /api/jobseeker-profiles
// - /api/companies
// - /api/job-posts
// - /api/applications
// - /api/reviews
// - /api/complaints
// - /api/messages
//
// 使用：
// import { UserApi, JobPostApi } from "@/api/Api";
// const list = await JobPostApi.list();

import axios from "axios";

/** 你可以在 .env 里配置：VITE_API_BASE_URL=http://localhost:8080 */
const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

/** Axios 实例 */
export const http = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: { "Content-Type": "application/json" },
});

/** 请求拦截：自动带 token（如果你后面做登录/JWT） */
http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token"); // 例如：Bearer xxxxx
    if (token) config.headers.Authorization = token.startsWith("Bearer") ? token : `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

/** 响应拦截：统一处理错误 */
http.interceptors.response.use(
  (resp) => resp,
  (error) => {
    const status = error?.response?.status;
    const msg = error?.response?.data || error.message;

    // 你如果做了登录：遇到 401 可以跳转登录
    if (status === 401) {
      console.warn("401 Unauthorized:", msg);
      // window.location.href = "/login";
    } else {
      console.warn("API Error:", status, msg);
    }
    return Promise.reject(error);
  }
);

/** 工具：通用 CRUD 工厂 */
function createCrud(resourcePath) {
  return {
    /** GET /resource */
    async list(params = {}) {
      const { data } = await http.get(resourcePath, { params });
      return data;
    },
    /** GET /resource/{id} */
    async get(id) {
      const { data } = await http.get(`${resourcePath}/${id}`);
      return data;
    },
    /** POST /resource */
    async create(payload) {
      const { data } = await http.post(resourcePath, payload);
      return data;
    },
    /** PUT /resource/{id} */
    async update(id, payload) {
      const { data } = await http.put(`${resourcePath}/${id}`, payload);
      return data;
    },
    /** DELETE /resource/{id} */
    async remove(id) {
      const { data } = await http.delete(`${resourcePath}/${id}`);
      return data;
    },
  };
}

/** ============= 8 个模块：与后端 Controller 一一对应 ============= */
export const UserApi = createCrud("/api/users");
export const JobseekerProfileApi = createCrud("/api/jobseeker-profiles");
export const CompanyApi = createCrud("/api/companies");
export const JobPostApi = createCrud("/api/job-posts");
export const ApplicationApi = createCrud("/api/applications");
export const ReviewApi = createCrud("/api/reviews");
export const ComplaintApi = createCrud("/api/complaints");
export const MessageApi = createCrud("/api/messages");

/** ============= 可选：一些联调常用的小工具（不影响你老师要求） ============= */

/**
 * 统一把 Date / dayjs 转成 ISO 字符串，避免后端 LocalDateTime 解析失败
 * - 你也可以自己在表单里直接填 "2026-01-08T18:00:00"
 */
export function toIsoDatetime(value) {
  if (!value) return value;
  if (value instanceof Date) return value.toISOString();
  // 如果是 "2026-01-08 18:00:00" 这种格式，建议你在前端改成 ISO 或自己处理
  return value;
}

/**
 * 示例：构造 JobPost payload（用于你表单联调时少踩坑）
 * 关联对象只需要传 id：
 * { company: { id: 1 }, title: "...", ... }
 */
export function buildRef(id) {
  return id ? { id } : null;
}


// ======= 追加：简易 AuthApi（临时用于联调） =======
// import { UserApi } from "./Api"; // 如果你 Api.js 本身定义在同文件，别加这行；直接用 UserApi 即可

export const AuthApi = {
  async register({ username, password, phone, email, role }) {
    // 你后端字段叫 passwordHash，这里先直接存明文用于演示（毕设演示OK）
    // 真正规范：后端做 BCrypt Hash
    return await UserApi.create({
      username,
      passwordHash: password,
      phone,
      email,
      role,
      status: "NORMAL",
    });
  },

  async login({ username, password, role }) {
    // 临时方案：拉全量 user，前端匹配（只用于联调）
    const users = await UserApi.list();
    const hit = users.find(
      (u) => u.username === username && u.passwordHash === password && u.role === role
    );
    if (!hit) throw new Error("账号/密码/角色不匹配");
    return hit;
  },
};

