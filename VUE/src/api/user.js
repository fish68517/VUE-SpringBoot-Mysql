import request from "../utils/request";

export function register(data) {
  return request.post("/user/register", data);
}

export function login(data) {
  return request.post("/user/login", data);
}

export function getUserProfile(userId) {
  return request.get("/user/profile", {
    params: { userId }
  });
}

export function updateUserProfile(userId, data) {
  return request.put("/user/profile", data, {
    params: { userId }
  });
}
