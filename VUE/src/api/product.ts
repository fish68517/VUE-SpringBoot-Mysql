import request from './request'

export interface ProductCategory {
  id: number
  name: string
  description: string
}

export interface Product {
  id: number
  categoryId: number
  name: string
  description: string
  images: string[]
  originalPrice: number
  currentPrice: number
  stock: number
  specs: Record<string, string[]>
  isActive: boolean
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  empty: boolean
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: string
}

export const productApi = {
  getCategories: () => request.get<ApiResponse<ProductCategory[]>>('/products/categories'),
  getProducts: (page: number = 0, size: number = 10, categoryId?: number) => {
    const params: Record<string, any> = { page, size }
    if (categoryId) {
      params.categoryId = categoryId
    }
    return request.get<ApiResponse<PageResponse<Product>>>('/products', { params })
  },
  getProductById: (id: number) => request.get<ApiResponse<Product>>(`/products/${id}`),
}

