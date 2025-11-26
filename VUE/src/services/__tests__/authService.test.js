import { describe, it, expect, beforeEach, vi } from 'vitest'
import { authService } from '../authService'
import api from '../api'

vi.mock('../api')

describe('Auth Service', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should call register endpoint with user data', async () => {
    const userData = {
      email: 'test@example.com',
      password: 'password123',
      username: 'testuser'
    }

    api.post.mockResolvedValue({
      code: 201,
      message: 'User registered successfully',
      data: { id: 1, email: userData.email, username: userData.username }
    })

    const result = await authService.register(userData)

    expect(api.post).toHaveBeenCalledWith('/auth/register', userData)
    expect(result.code).toBe(201)
  })

  it('should call login endpoint with credentials', async () => {
    const credentials = {
      email: 'test@example.com',
      password: 'password123'
    }

    api.post.mockResolvedValue({
      code: 200,
      message: 'Login successful',
      data: {
        user: { id: 1, email: credentials.email },
        token: 'jwt-token-123'
      }
    })

    const result = await authService.login(credentials)

    expect(api.post).toHaveBeenCalledWith('/auth/login', credentials)
    expect(result.data.token).toBe('jwt-token-123')
  })

  it('should call refresh token endpoint', async () => {
    api.post.mockResolvedValue({
      code: 200,
      message: 'Token refreshed',
      data: { token: 'new-jwt-token-456' }
    })

    const result = await authService.refreshToken()

    expect(api.post).toHaveBeenCalledWith('/auth/refresh')
    expect(result.data.token).toBe('new-jwt-token-456')
  })

  it('should remove token from localStorage on logout', () => {
    localStorage.setItem('token', 'test-token')
    
    authService.logout()

    expect(localStorage.getItem('token')).toBeNull()
  })
})
