import { describe, it, expect, beforeEach, vi } from 'vitest'
import { footprintService } from '../footprintService'
import api from '../api'

vi.mock('../api')

describe('Footprint Service', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('addFootprint', () => {
    it('should add a footprint with correct endpoint', async () => {
      const footprintData = {
        travelRecordId: 1,
        locationName: 'Beijing',
        latitude: 39.9042,
        longitude: 116.4074,
        visitDate: '2024-01-15'
      }

      const mockResponse = {
        data: {
          code: 201,
          data: {
            id: 1,
            ...footprintData
          }
        }
      }

      api.post.mockResolvedValueOnce(mockResponse)

      const result = await footprintService.addFootprint(footprintData)

      expect(api.post).toHaveBeenCalledWith(
        '/api/travels/1/footprints',
        footprintData
      )
      expect(result).toEqual(mockResponse)
    })

    it('should handle error when adding footprint', async () => {
      const footprintData = {
        travelRecordId: 1,
        locationName: 'Beijing',
        latitude: 39.9042,
        longitude: 116.4074
      }

      const mockError = new Error('Network error')
      api.post.mockRejectedValueOnce(mockError)

      await expect(footprintService.addFootprint(footprintData)).rejects.toThrow('Network error')
    })
  })

  describe('getFootprints', () => {
    it('should fetch footprints for a travel record', async () => {
      const travelRecordId = 1
      const mockResponse = {
        data: {
          code: 200,
          data: [
            {
              id: 1,
              travelRecordId: 1,
              locationName: 'Beijing',
              latitude: 39.9042,
              longitude: 116.4074,
              visitDate: '2024-01-15'
            },
            {
              id: 2,
              travelRecordId: 1,
              locationName: 'Shanghai',
              latitude: 31.2304,
              longitude: 121.4737,
              visitDate: '2024-01-16'
            }
          ]
        }
      }

      api.get.mockResolvedValueOnce(mockResponse)

      const result = await footprintService.getFootprints(travelRecordId)

      expect(api.get).toHaveBeenCalledWith(`/api/travels/${travelRecordId}/footprints`)
      expect(result).toEqual(mockResponse)
      expect(result.data.data).toHaveLength(2)
    })

    it('should handle error when fetching footprints', async () => {
      const travelRecordId = 1
      const mockError = new Error('Failed to fetch')
      api.get.mockRejectedValueOnce(mockError)

      await expect(footprintService.getFootprints(travelRecordId)).rejects.toThrow('Failed to fetch')
    })

    it('should return empty list when no footprints exist', async () => {
      const travelRecordId = 1
      const mockResponse = {
        data: {
          code: 200,
          data: []
        }
      }

      api.get.mockResolvedValueOnce(mockResponse)

      const result = await footprintService.getFootprints(travelRecordId)

      expect(result.data.data).toHaveLength(0)
    })
  })

  describe('deleteFootprint', () => {
    it('should delete a footprint', async () => {
      const footprintId = 1
      const mockResponse = {
        data: {
          code: 200,
          message: 'Footprint deleted successfully'
        }
      }

      api.delete.mockResolvedValueOnce(mockResponse)

      const result = await footprintService.deleteFootprint(footprintId)

      expect(api.delete).toHaveBeenCalledWith(`/api/travels/footprints/${footprintId}`)
      expect(result).toEqual(mockResponse)
    })

    it('should handle error when deleting footprint', async () => {
      const footprintId = 1
      const mockError = new Error('Delete failed')
      api.delete.mockRejectedValueOnce(mockError)

      await expect(footprintService.deleteFootprint(footprintId)).rejects.toThrow('Delete failed')
    })
  })
})
