// src/api/community.js
import request from "../utils/request";

// 发布帖子
export function createPost(data) {
  return request.post("/community/post", data);
}

// 获取帖子列表（分页）
export function getPostList(params) {
  return request.get("/community/post/list", { params });
}

// 获取帖子详情
export function getPostDetail(postId) {
  return request.get(`/community/post/${postId}`);
}

// 删除帖子
export function deletePost(postId) {
  return request.delete(`/community/post/${postId}`);
}

// 发布评论
export function createReply(data) {
  return request.post("/community/reply", data);
}

// 获取帖子评论列表
export function getPostReplies(postId) {
  return request.get(`/community/reply/${postId}`);
}

// 点赞/取消点赞
export function toggleLike(postId) {
  return request.post(`/community/like/${postId}`);
}
