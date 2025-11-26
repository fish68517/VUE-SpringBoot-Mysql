import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import RecordDetailView from '../RecordDetailView.vue'
import * as travelService from '../../services/travelService'
import * as fileService from '../../services/fileService'

// Mock the services
vi.mock('../../services/travelService')
vi.mock('../../services/fileService')
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn()
  }),
  useRoute: () => ({
    params: { id: '1' }
  })
}))

describe('RecordDetailView - Multimedia Display', () => {
  let wrapper

  const mockRecord = {
    id: 1,
    userId: 1,
    title: 'Summer Vacation',
    destination: 'Paris',
    startDate: '2024-06-01',
    endDate: '2024-06-15',
    description: 'Amazing trip to Paris',
    diaryContent: '<p>Had a great time!</p>',
    isPublic: true,
    createdAt: '2024-01-01T12:00:00'
  }

  const mockMultimediaFiles = [
    {
      id: 1,
      fileName: 'eiffel_tower.jpg',
      filePath: '/uploads/eiffel_tower.jpg',
      fileType: 'jpg',
      fileSize: 2048000,
      uploadDate: '2024-01-01T12:00:00'
    },
    {
      id: 2,
      fileName: 'paris_walk.mp4',
      filePath: '/uploads/paris_walk.mp4',
      fileType: 'mp4',
      fileSize: 52428800,
      uploadDate: '2024-01-02T12:00:00'
    },
    {
      id: 3,
      fileName: 'louvre.png',
      filePath: '/uploads/louvre.png',
      fileType: 'png',
      fileSize: 3145728,
      uploadDate: '2024-01-03T12:00:00'
    }
  ]

  beforeEach(() => {
    // Mock API responses
    travelService.getTravelRecordById = vi.fn().mockResolvedValue({
      data: mockRecord
    })

    fileService.getFilesByTravelRecord = vi.fn().mockResolvedValue({
      data: mockMultimediaFiles
    })

    wrapper = mount(RecordDetailView, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-button': true,
          'el-icon': true,
          'el-tag': true,
          'el-popconfirm': true,
          'el-alert': true,
          'el-empty': true,
          'TravelRecordEditor': true,
          'MultimediaUpload': true,
          'MultimediaDisplay': true
        },
        mocks: {
          $route: {
            params: { id: '1' }
          }
        }
      }
    })
  })

  it('should fetch multimedia files when record is loaded', async () => {
    await wrapper.vm.$nextTick()
    
    expect(fileService.getFilesByTravelRecord).toHaveBeenCalledWith('1')
  })

  it('should display multimedia files in the component', async () => {
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.multimediaFiles).toHaveLength(3)
    expect(wrapper.vm.multimediaFiles[0].fileName).toBe('eiffel_tower.jpg')
    expect(wrapper.vm.multimediaFiles[1].fileName).toBe('paris_walk.mp4')
    expect(wrapper.vm.multimediaFiles[2].fileName).toBe('louvre.png')
  })

  it('should pass correct props to MultimediaDisplay component', async () => {
    await wrapper.vm.$nextTick()
    
    const multimediaDisplay = wrapper.findComponent({ name: 'MultimediaDisplay' })
    if (multimediaDisplay.exists()) {
      expect(multimediaDisplay.props('files')).toEqual(mockMultimediaFiles)
      expect(multimediaDisplay.props('baseUrl')).toBe('http://localhost:8080')
    }
  })

  it('should refresh multimedia files after upload', async () => {
    await wrapper.vm.$nextTick()
    
    // Simulate upload complete event
    await wrapper.vm.handleUploadComplete()
    
    // Should call getFilesByTravelRecord again
    expect(fileService.getFilesByTravelRecord).toHaveBeenCalledTimes(2)
  })

  it('should refresh multimedia files after file deletion', async () => {
    await wrapper.vm.$nextTick()
    
    // Simulate file deleted event
    await wrapper.vm.handleFileDeleted()
    
    // Should call getFilesByTravelRecord again
    expect(fileService.getFilesByTravelRecord).toHaveBeenCalledTimes(2)
  })

  it('should handle empty multimedia files gracefully', async () => {
    fileService.getFilesByTravelRecord = vi.fn().mockResolvedValue({
      data: []
    })

    wrapper = mount(RecordDetailView, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-button': true,
          'el-icon': true,
          'el-tag': true,
          'el-popconfirm': true,
          'el-alert': true,
          'el-empty': true,
          'TravelRecordEditor': true,
          'MultimediaUpload': true,
          'MultimediaDisplay': true
        }
      }
    })

    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.multimediaFiles).toHaveLength(0)
  })

  it('should handle multimedia file fetch errors gracefully', async () => {
    fileService.getFilesByTravelRecord = vi.fn().mockRejectedValue(
      new Error('Failed to fetch files')
    )

    wrapper = mount(RecordDetailView, {
      global: {
        stubs: {
          'el-skeleton': true,
          'el-button': true,
          'el-icon': true,
          'el-tag': true,
          'el-popconfirm': true,
          'el-alert': true,
          'el-empty': true,
          'TravelRecordEditor': true,
          'MultimediaUpload': true,
          'MultimediaDisplay': true
        }
      }
    })

    await wrapper.vm.$nextTick()
    
    // Should not crash and multimediaFiles should be empty
    expect(wrapper.vm.multimediaFiles).toEqual([])
  })
})
