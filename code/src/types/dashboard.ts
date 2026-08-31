export interface DashboardCard {
  key: string
  label: string
  value: number
  suffix?: string
  trend: number
  tone: 'orange' | 'blue' | 'green' | 'purple'
}

export interface TrendPoint {
  date: string
  created: number
  completed: number
}

export interface DashboardEvent {
  id: number
  eventNo: string
  title: string
  category: string
  priority: string
  status: string
  createTime: string
}

export interface TodoItem {
  id: number
  title: string
  source: string
  deadline: string
  priority: string
}

export interface DashboardData {
  cards: DashboardCard[]
  trend: TrendPoint[]
  events: DashboardEvent[]
  todos: TodoItem[]
}
