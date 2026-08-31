export interface User {
  id: number
  username: string
  realName: string
  deptId: number
  deptName: string
  roleName: string
  phone: string
  status: number
  createTime: string
}

export interface ResourceRecord {
  id: number
  status?: number
  createTime?: string
  [key: string]: string | number | boolean | undefined
}

export interface FieldOption {
  label: string
  value: string | number
  tagType?: 'success' | 'warning' | 'danger' | 'info' | 'primary'
}

export type FieldKind = 'text' | 'select' | 'date' | 'datetime' | 'textarea' | 'number' | 'switch'

export interface ResourceField {
  prop: string
  label: string
  kind?: FieldKind
  width?: number
  minWidth?: number
  required?: boolean
  searchable?: boolean
  placeholder?: string
  options?: FieldOption[]
  hideInTable?: boolean
  hideInForm?: boolean
  formatter?: (row: ResourceRecord) => string
}

export interface ResourceConfig {
  key: string
  module: string
  resource: string
  title: string
  description: string
  permissionPrefix: string
  primaryLabel: string
  keywordPlaceholder: string
  fields: ResourceField[]
  allowCreate?: boolean
  allowDelete?: boolean
  allowStatus?: boolean
  readOnly?: boolean
}
