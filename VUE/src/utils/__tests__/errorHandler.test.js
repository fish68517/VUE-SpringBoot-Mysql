import { describe, it, expect, beforeEach, vi } from 'vitest'
import { errorHandler } from '../errorHandler'
import { useErrorStore } from '../../stores/errorStore'
import { setActivePinia, createPinia } from 'pinia'

describe('Error Handler', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  describe('getErrorMessage', () => {
    it('should return custom message from data', () => {
      const message = errorHandler.getErrorMessage(400, { message: 'Custom error' })
      expect(message).toBe('Custom error')
    })

    it('should return status-specific message for 400', () => {
      const message = errorHandler.getErrorMessage(400, {})
      expect(message).toContain('Invalid request')
    })

    it('should return status-specific message for 401', () => {
      const message = errorHandler.getErrorMessage(401, {})
      expect(message).toContain('session has expired')
    })

    it('should return status-specific message for 403', () => {
      const message = errorHandler.getErrorMessage(403, {})
      expect(message).toContain('permission')
    })

    it('should return status-specific message for 404', () => {
      const message = errorHandler.getErrorMessage(404, {})
      expect(message).toContain('not found')
    })

    it('should return default message for unknown status', () => {
      const message = errorHandler.getErrorMessage(999, {})
      expect(message).toContain('error occurred')
    })
  })

  describe('normalizeError', () => {
    it('should normalize axios error', () => {
      const error = {
        response: {
          status: 400,
          data: { message: 'Bad request' }
        },
        message: 'Request failed'
      }
      const normalized = errorHandler.normalizeError(error)
      expect(normalized.code).toBe(400)
      expect(normalized.message).toBe('Bad request')
      expect(normalized.type).toBe('api')
    })

    it('should normalize standard error', () => {
      const error = new Error('Test error')
      error.code = 500
      const normalized = errorHandler.normalizeError(error)
      expect(normalized.code).toBe(500)
      expect(normalized.message).toBe('Test error')
      expect(normalized.type).toBe('error')
    })

    it('should normalize unknown error', () => {
      const normalized = errorHandler.normalizeError('String error')
      expect(normalized.code).toBe(500)
      expect(normalized.message).toBe('String error')
      expect(normalized.type).toBe('unknown')
    })
  })

  describe('handleApiError', () => {
    it('should handle network error', () => {
      const errorStore = useErrorStore()
      const spy = vi.spyOn(errorStore, 'showError')
      
      const error = new Error('Network Error')
      errorHandler.handleApiError(error)
      
      expect(spy).toHaveBeenCalled()
      expect(spy.mock.calls[0][0]).toContain('Network')
    })

    it('should handle API response error', () => {
      const errorStore = useErrorStore()
      const spy = vi.spyOn(errorStore, 'showError')
      
      const error = {
        response: {
          status: 400,
          data: { message: 'Bad request' }
        }
      }
      errorHandler.handleApiError(error)
      
      expect(spy).toHaveBeenCalledWith('Bad request')
    })
  })

  describe('handleValidationError', () => {
    it('should handle array of errors', () => {
      const errorStore = useErrorStore()
      const spy = vi.spyOn(errorStore, 'showError')
      
      const errors = [
        { message: 'Error 1' },
        { message: 'Error 2' }
      ]
      errorHandler.handleValidationError(errors)
      
      expect(spy).toHaveBeenCalledTimes(2)
    })

    it('should handle object of errors', () => {
      const errorStore = useErrorStore()
      const spy = vi.spyOn(errorStore, 'showError')
      
      const errors = {
        email: { message: 'Invalid email' },
        password: { message: 'Password too short' }
      }
      errorHandler.handleValidationError(errors)
      
      expect(spy).toHaveBeenCalledTimes(2)
    })
  })

  describe('handleBusinessError', () => {
    it('should add error and show notification', () => {
      const errorStore = useErrorStore()
      const addErrorSpy = vi.spyOn(errorStore, 'addError')
      const showErrorSpy = vi.spyOn(errorStore, 'showError')
      
      errorHandler.handleBusinessError('Business error', { detail: 'test' })
      
      expect(addErrorSpy).toHaveBeenCalled()
      expect(showErrorSpy).toHaveBeenCalledWith('Business error')
    })
  })

  describe('handleAuthError', () => {
    it('should show auth error notification', () => {
      const errorStore = useErrorStore()
      const spy = vi.spyOn(errorStore, 'showError')
      
      errorHandler.handleAuthError('Auth failed')
      
      expect(spy).toHaveBeenCalledWith('Auth failed')
    })
  })

  describe('handlePermissionError', () => {
    it('should show permission warning', () => {
      const errorStore = useErrorStore()
      const spy = vi.spyOn(errorStore, 'showWarning')
      
      errorHandler.handlePermissionError('No permission')
      
      expect(spy).toHaveBeenCalledWith('No permission')
    })
  })

  describe('handleNotFoundError', () => {
    it('should show not found error', () => {
      const errorStore = useErrorStore()
      const spy = vi.spyOn(errorStore, 'showError')
      
      errorHandler.handleNotFoundError('Travel Record')
      
      expect(spy).toHaveBeenCalledWith('Travel Record not found')
    })
  })

  describe('logError', () => {
    it('should log error with context', () => {
      const consoleSpy = vi.spyOn(console, 'error')
      const error = new Error('Test error')
      
      errorHandler.logError(error, 'Test Context')
      
      expect(consoleSpy).toHaveBeenCalled()
      const call = consoleSpy.mock.calls[0]
      expect(call[0]).toContain('Test Context')
    })
  })
})
