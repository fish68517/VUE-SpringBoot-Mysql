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

export function uploadUserAvatar(userId, file) {
  const formData = new FormData();
  formData.append("file", file);
  return request.post("/user/avatar", formData, {
    params: { userId },
    headers: {
      "Content-Type": "multipart/form-data"
    }
  });
}
