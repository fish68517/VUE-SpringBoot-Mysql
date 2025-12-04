import request from './request'
import { Product, ProductCategory } from './product'

export const adminProductApi = {
  // --- 分类管理 ---
  getCategories: () => request.get<ProductCategory[]>('/admin/product/categories'),
  createCategory: (data: { name: string }) => request.post('/admin/product/categories', data),
  deleteCategory: (id: number) => request.delete(`/admin/product/categories/${id}`),

  // --- 商品管理 ---
  getProducts: (params: any) => request.get('/admin/products', { params }),
  
  createProduct: (data: Partial<Product>) => request.post('/admin/products', data),
  
  updateProduct: (id: number, data: Partial<Product>) => request.put(`/admin/products/${id}`, data),
  
  // 上下架状态更新
  updateStatus: (id: number, isActive: boolean) => request.put(`/admin/products/${id}/status`, { isActive }),
  
  deleteProduct: (id: number) => request.delete(`/admin/products/${id}`),
}