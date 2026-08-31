export interface LoginForm {
  username: string
  password: string
}

export interface LoginUser {
  id: number
  username: string
  realName: string
  avatar?: string
  deptName?: string
}

export interface LoginResult {
  accessToken: string
  user: LoginUser
  permissions: string[]
}

export interface UserInfoResult {
  user: LoginUser
  permissions: string[]
}
