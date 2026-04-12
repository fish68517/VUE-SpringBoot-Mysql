import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useErrorStore } from '../errorStore'

describe('Error Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  describe('Error Management', () => {
    it('should add error', () => {
      const store = useErrorStore()
      const errorId = store.addError({
        code: 400,
        message: 'Test error'
      })
      
      expect(store.errors).toHaveLength(1)
      expect(store.errors[0].message).toBe('Test error')
      expect(store.errors[0].id).toBe(errorId)
    })

    it('should remove error by id', () => {
      const store = useErrorStore()
      const errorId = store.addError({
        code: 400,
        message: 'Test error'
      })
      
      store.removeError(errorId)
      expect(store.errors).toHaveLength(0)
    })

    it('should clear all errors', () => {
      const store = useErrorStore()
      store.addError({ code: 400, message: 'Error 1' })
      store.addError({ code: 500, message: 'Error 2' })
      
      store.clearErrors()
      expect(store.errors).toHaveLength(0)
    })
  })

  describe('Notification Management', () => {
    it('should add notification', () => {
      const store = useErrorStore()
      const notificationId = store.addNotification({
        message: 'Test notification',
        type: 'info'
      })
      
      expect(store.notifications).toHaveLength(1)
      expect(store.notifications[0].message).toBe('Test notification')
      expect(store.notifications[0].id).toBe(notificationId)
    })

    it('should remove notification by id', () => {
      const store = useErrorStore()
      const notificationId = store.addNotification({
        message: 'Test notification'
      })
      
      store.removeNotification(notificationId)
      expect(store.notifications).toHaveLength(0)
    })

    it('should clear all notifications', () => {
      const store = useErrorStore()
      store.addNotification({ message: 'Notification 1' })
      store.addNotification({ message: 'Notification 2' })
      
      store.clearNotifications()
      expect(store.notifications).toHaveLength(0)
    })

    it('should auto-remove notification after duration', async () => {
      const store = useErrorStore()
      store.addNotification({
        message: 'Test',
        duration: 100
      })
      
      expect(store.notifications).toHaveLength(1)
      
      // Wait for auto-removal
      await new Promise(resolve => setTimeout(resolve, 150))
      
      expect(store.notifications).toHaveLength(0)
    })

    it('should not auto-remove notification with duration 0', async () => {
      const store = useErrorStore()
      store.addNotification({
        message: 'Test',
        duration: 0
      })
      
      await new Promise(resolve => setTimeout(resolve, 100))
      
      expect(store.notifications).toHaveLength(1)
    })
  })

  describe('Convenience Methods', () => {
    it('should show success notification', () => {
      const store = useErrorStore()
      store.showSuccess('Success message')
      
      expect(store.notifications).toHaveLength(1)
      expect(store.notifications[0].type).toBe('success')
      expect(store.notifications[0].message).toBe('Success message')
    })

    it('should show error notification', () => {
      const store = useErrorStore()
      store.showError('Error message')
      
      expect(store.notifications).toHaveLength(1)
      expect(store.notifications[0].type).toBe('error')
      expect(store.notifications[0].message).toBe('Error message')
    })

    it('should show warning notification', () => {
      const store = useErrorStore()
      store.showWarning('Warning message')
      
      expect(store.notifications).toHaveLength(1)
      expect(store.notifications[0].type).toBe('warning')
      expect(store.notifications[0].message).toBe('Warning message')
    })

    it('should show info notification', () => {
      const store = useErrorStore()
      store.showInfo('Info message')
      
      expect(store.notifications).toHaveLength(1)
      expect(store.notifications[0].type).toBe('info')
      expect(store.notifications[0].message).toBe('Info message')
    })

    it('should use custom duration', () => {
      const store = useErrorStore()
      const notification = store.addNotification({
        message: 'Test',
        duration: 5000
      })
      
      expect(store.notifications[0].duration).toBe(5000)
    })
  })

  describe('Error Details', () => {
    it('should include timestamp in error', () => {
      const store = useErrorStore()
      const before = new Date()
      store.addError({ code: 400, message: 'Test' })
      const after = new Date()
      
      const error = store.errors[0]
      expect(error.timestamp).toBeInstanceOf(Date)
      expect(error.timestamp.getTime()).toBeGreaterThanOrEqual(before.getTime())
      expect(error.timestamp.getTime()).toBeLessThanOrEqual(after.getTime())
    })

    it('should include error type', () => {
      const store = useErrorStore()
      store.addError({
        code: 400,
        message: 'Test',
        type: 'validation'
      })
      
      expect(store.errors[0].type).toBe('validation')
    })

    it('should include error details', () => {
      const store = useErrorStore()
      store.addError({
        code: 400,
        message: 'Test',
        details: { field: 'email', reason: 'invalid' }
      })
      
      expect(store.errors[0].details).toEqual({ field: 'email', reason: 'invalid' })
    })
  })
})
