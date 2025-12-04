import request from './request'

export interface Carousel {
  id?: number
  imageUrl: string
  linkUrl?: string
  title?: string
  sortOrder: number
}

export interface News {
  id?: number
  title: string
  content: string // 富文本
  coverImage?: string
  publishTime?: string
}

export interface Artist {
  id?: number
  name: string
  description: string
  imageUrl: string
  isLocalBand: boolean // 是否沈阳本土乐队
  tags?: string[]
}

export const contentApi = {
  // --- 轮播图 ---
  getCarousels: () => request.get<Carousel[]>('/admin/content/carousels'),
  saveCarousel: (data: Carousel) => request.post('/admin/content/carousels', data),
  deleteCarousel: (id: number) => request.delete(`/admin/content/carousels/${id}`),

  // --- 新闻公告 ---
  getNewsList: (params: any) => request.get('/admin/content/news', { params }),
  saveNews: (data: News) => request.post('/admin/content/news', data), // id存在则更新，不存在则新增
  deleteNews: (id: number) => request.delete(`/admin/content/news/${id}`),

  // --- 艺人信息 ---
  getArtists: (params: any) => request.get('/admin/content/artists', { params }),
  saveArtist: (data: Artist) => request.post('/admin/content/artists', data),
  deleteArtist: (id: number) => request.delete(`/admin/content/artists/${id}`),

  // --- 日程与静态页 ---
  saveSchedule: (data: any) => request.post('/admin/content/schedule', data),
  saveStaticPage: (type: string, content: string) => request.post('/admin/content/static-page', { type, content }),
}