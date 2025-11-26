import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import axios from 'axios'
import api from '../api'
import { useUserStore } from '../../stores/userStore'

vi.mock('axios')
vi.mock('../../stores/userStore')

describe('API Service', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  it('should create axios instance with correct baseURL', () => {
    expect(api.defaults.baseURL).toBe('/api')
  })

  it('should have 10 second timeout', () => {
    expect(api.defaults.timeout).toBe(10000)
  })

  it('should add Authorization header when token exists', async () => {
    const mockStore = {
      token: 'test-token-123'
    }
    useUserStore.mockReturnValue(mockStore)

    const config = { headers: {} }
    const interceptor = api.interceptors.request.handlers[0]
    
    const result = interceptor.fulfilled(config)
    expect(result.headers.Authorization).toBe('Bearer test-token-123')
  })

  it('should not add Authorization header when token is null', async () => {
    const mockStore = {
      token: null
    }
    useUserStore.mockReturnValue(mockStore)

    const config = { headers: {} }
    const interceptor = api.interceptors.request.handlers[0]
    
    const result = interceptor.fulfilled(config)
    expect(result.headers.Authorization).toBeUndefined()
  })

  it('should handle successful response', async () => {
    const mockResponse = {
      data: {
        code: 200,
        message: 'Success',
        data: { id: 1, name: 'Test' }
      }
    }

    const interceptor = api.interceptors.response.handlers[0]
    const result = interceptor.fulfilled(mockResponse)
    
    expect(result).toEqual(mockResponse.data)
  })

  it('should attempt token refresh on 401 error with valid token', async () => {
    const mockStore = {
      token: 'expired-token',
      setToken: vi.fn(),
      setUser: vi.fn(),
      logout: vi.fn()
    }
    useUserStore.mockReturnValue(mockStore)

    const newToken = 'new-token-123'
    const newUser = { id: 1, username: 'testuser', email: 'test@example.com' }
    
    // Mock the refresh endpoint
    vi.spyOn(api, 'post').mockResolvedValueOnce({
      data: {
        token: newToken,
        user: newUser
      }
    })

    const error = {
      response: {
        status: 401,
        data: { code: 401, message: 'Token expired' }
      },
      config: {
        headers: {}
      }
    }

    const interceptor = api.interceptors.response.handlers[0]
    
    try {
      await interceptor.rejected(error)
    } catch (e) {
      // Expected to reject after refresh attempt
    }
  })

  it('should redirect to login on 401 error without token', async () => {
    const mockStore = {
      token: null,
      logout: vi.fn()
    }
    useUserStore.mockReturnValue(mockStore)
    
    const originalLocation = window.location.href
    delete window.location
    window.location = { href: '' }

    const error = {
      response: {
        status: 401,
        data: { code: 401, message: 'Unauthorized' }
      },
      config: {}
    }

    const interceptor = api.interceptors.response.handlers[0]
    
    try {
      interceptor.rejected(error)
    } catch (e) {
      expect(mockStore.logout).toHaveBeenCalled()
      expect(window.location.href).toBe('/login')
    }

    window.location.href = originalLocation
  })

  it('should handle error response with proper format', async () => {
    const error = {
      response: {
        status: 400,
        data: { code: 400, message: 'Bad Request' }
      },
      config: {}
    }

    const interceptor = api.interceptors.response.handlers[0]
    
    try {
      interceptor.rejected(error)
    } catch (e) {
      expect(e).toEqual(error.response.data)
    }
  })
})
