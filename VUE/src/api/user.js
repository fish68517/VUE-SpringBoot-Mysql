// src/api/user.js
import request from "../utils/request";

// 用户注册
export function register(data) {
  return request.post("/user/register", data);
}

// 用户登录
export function login(data) {
  return request.post("/user/login", data);
}

// 获取用户信息
export function getUserProfile() {
  return request.get("/user/profile");
}

// 更新用户信息
export function updateUserProfile(data) {
  return request.put("/user/profile", data);
}
