import request from './request'

export interface TicketSession {
  id: number
  festivalId: number
  name: string
  startTime: string
  endTime: string
  status: string
}

export interface TicketZone {
  id: number
  sessionId: number
  name: string
  totalCapacity: number
  soldCount: number
  remainingCapacity: number
  price: number
}

export interface BuyerInfo {
  idNumber: string
  name: string
}

export interface TicketOrderRequest {
  sessionId: number
  zoneId: number
  buyers: BuyerInfo[]
}

export interface TicketOrderResponse {
  orderId: number
  ticketIds: number[]
  totalAmount: number
  ticketCount: number
  status: string
  message: string
}

export const ticketApi = {
  // Get all ticket sessions
  getSessions: () => request.get<TicketSession[]>('/tickets/sessions'),

  // Get all ticket zones
  getZones: () => request.get<TicketZone[]>('/tickets/zones'),

  // Get ticket zone by ID
  getZoneById: (id: number) => request.get<TicketZone>(`/tickets/zones/${id}`),

  // Create ticket order
  createOrder: (data: TicketOrderRequest) => request.post<TicketOrderResponse>('/tickets/order', data),
}
