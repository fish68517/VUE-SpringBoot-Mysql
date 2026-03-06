// src/api/pet.js
import request from "../utils/request";

// 创建宠物档案
export function createPet(data) {
  return request.post("/pet", data);
}

// 获取宠物列表
export function getPetList(userId) {
  return request.get("/pet/list", {
    params: { userId }
  });
}

// 获取宠物详情
export function getPetDetail(id) {
  return request.get(`/pet/${id}`);
}

// 更新宠物信息
export function updatePet(id, data) {
  return request.put(`/pet/${id}`, data);
}

// 删除宠物档案
export function deletePet(id, userId) {
  return request.delete(`/pet/${id}`, {
    params: { userId }
  });
}
