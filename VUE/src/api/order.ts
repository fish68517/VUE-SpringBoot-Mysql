import requestClient from './request'

export interface OrderItemDTO {
  id: number
  orderId: number
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  subtotal: number
}

export interface OrderDTO {
  id: number
  userId: number
  orderType: string
  totalAmount: number
  status: string
  shippingAddress: string
  trackingNumber?: string
  items: OrderItemDTO[]
  createdAt: string
  updatedAt: string
}

export interface CreateProductOrderRequest {
  cartItemIds: string[]
  shippingAddress: string
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: string
}

export const orderApi = {
  createProductOrder: (req: CreateProductOrderRequest) =>
    requestClient.post<ApiResponse<OrderDTO>>('/orders/product', req),
  getProductOrders: (status?: string) => {
    const params: Record<string, any> = {}
    if (status) {
      params.status = status
    }
    return requestClient.get<ApiResponse<OrderDTO[]>>('/orders/product', { params })
  },
  getOrderById: (id: number) => requestClient.get<ApiResponse<OrderDTO>>(`/orders/${id}`),
}
