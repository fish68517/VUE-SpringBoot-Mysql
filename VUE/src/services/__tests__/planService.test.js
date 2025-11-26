import { describe, it, expect, beforeEach, vi } from 'vitest'
import { planService } from '../planService'
import api from '../api'

vi.mock('../api')

describe('Plan Service', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getUserTravelPlans', () => {
    it('should fetch user travel plans with pagination', async () => {
      const mockResponse = {
        code: 200,
        data: {
          content: [
            {
              id: 1,
              userId: 1,
              title: 'Summer Vacation',
              destination: 'Paris',
              startDate: '2024-06-01',
              endDate: '2024-06-15',
              budget: 5000.00,
              description: 'A wonderful summer trip',
              createdAt: '2024-01-01T10:00:00',
              updatedAt: '2024-01-01T10:00:00'
            }
          ],
          totalElements: 1,
          totalPages: 1,
          number: 0,
          size: 10
        }
      }

      api.get.mockResolvedValueOnce(mockResponse)

      const result = await planService.getUserTravelPlans(0, 10)

      expect(api.get).toHaveBeenCalledWith('/plans', {
        params: {
          page: 0,
          size: 10
        }
      })
      expect(result).toEqual(mockResponse)
      expect(result.data.content).toHaveLength(1)
    })

    it('should use default pagination parameters', async () => {
      const mockResponse = {
        code: 200,
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0,
          number: 0,
          size: 10
        }
      }

      api.get.mockResolvedValueOnce(mockResponse)

      await planService.getUserTravelPlans()

      expect(api.get).toHaveBeenCalledWith('/plans', {
        params: {
          page: 0,
          size: 10
        }
      })
    })

    it('should handle error when fetching plans', async () => {
      const mockError = new Error('Failed to fetch plans')
      api.get.mockRejectedValueOnce(mockError)

      await expect(planService.getUserTravelPlans(0, 10)).rejects.toThrow('Failed to fetch plans')
    })

    it('should return empty list when no plans exist', async () => {
      const mockResponse = {
        code: 200,
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0,
          number: 0,
          size: 10
        }
      }

      api.get.mockResolvedValueOnce(mockResponse)

      const result = await planService.getUserTravelPlans(0, 10)

      expect(result.data.content).toHaveLength(0)
    })
  })

  describe('getTravelPlanById', () => {
    it('should fetch a travel plan by ID', async () => {
      const planId = 1
      const mockResponse = {
        code: 200,
        data: {
          id: 1,
          userId: 1,
          title: 'Summer Vacation',
          destination: 'Paris',
          startDate: '2024-06-01',
          endDate: '2024-06-15',
          budget: 5000.00,
          description: 'A wonderful summer trip',
          createdAt: '2024-01-01T10:00:00',
          updatedAt: '2024-01-01T10:00:00'
        }
      }

      api.get.mockResolvedValueOnce(mockResponse)

      const result = await planService.getTravelPlanById(planId)

      expect(api.get).toHaveBeenCalledWith(`/plans/${planId}`)
      expect(result).toEqual(mockResponse)
      expect(result.data.id).toBe(1)
    })

    it('should handle error when plan not found', async () => {
      const planId = 999
      const mockError = new Error('Plan not found')
      api.get.mockRejectedValueOnce(mockError)

      await expect(planService.getTravelPlanById(planId)).rejects.toThrow('Plan not found')
    })

    it('should handle access denied error', async () => {
      const planId = 1
      const mockError = new Error('Access denied')
      api.get.mockRejectedValueOnce(mockError)

      await expect(planService.getTravelPlanById(planId)).rejects.toThrow('Access denied')
    })
  })

  describe('createTravelPlan', () => {
    it('should create a new travel plan', async () => {
      const planData = {
        title: 'Summer Vacation',
        destination: 'Paris',
        startDate: '2024-06-01',
        endDate: '2024-06-15',
        budget: 5000.00,
        description: 'A wonderful summer trip'
      }

      const mockResponse = {
        code: 201,
        data: {
          id: 1,
          userId: 1,
          ...planData,
          createdAt: '2024-01-01T10:00:00',
          updatedAt: '2024-01-01T10:00:00'
        }
      }

      api.post.mockResolvedValueOnce(mockResponse)

      const result = await planService.createTravelPlan(planData)

      expect(api.post).toHaveBeenCalledWith('/plans', planData)
      expect(result).toEqual(mockResponse)
    })
  })

  describe('updateTravelPlan', () => {
    it('should update a travel plan', async () => {
      const planId = 1
      const planData = {
        title: 'Updated Summer Vacation',
        destination: 'London',
        startDate: '2024-06-01',
        endDate: '2024-06-20',
        budget: 6000.00,
        description: 'Updated trip'
      }

      const mockResponse = {
        code: 200,
        data: {
          id: 1,
          userId: 1,
          ...planData,
          createdAt: '2024-01-01T10:00:00',
          updatedAt: '2024-01-02T10:00:00'
        }
      }

      api.put.mockResolvedValueOnce(mockResponse)

      const result = await planService.updateTravelPlan(planId, planData)

      expect(api.put).toHaveBeenCalledWith(`/plans/${planId}`, planData)
      expect(result).toEqual(mockResponse)
    })
  })

  describe('deleteTravelPlan', () => {
    it('should delete a travel plan', async () => {
      const planId = 1
      const mockResponse = {
        code: 200,
        message: 'Travel plan deleted successfully'
      }

      api.delete.mockResolvedValueOnce(mockResponse)

      const result = await planService.deleteTravelPlan(planId)

      expect(api.delete).toHaveBeenCalledWith(`/plans/${planId}`)
      expect(result).toEqual(mockResponse)
    })

    it('should handle error when deleting plan', async () => {
      const planId = 1
      const mockError = new Error('Delete failed')
      api.delete.mockRejectedValueOnce(mockError)

      await expect(planService.deleteTravelPlan(planId)).rejects.toThrow('Delete failed')
    })
  })
})
