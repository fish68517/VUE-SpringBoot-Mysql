import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import MultimediaDisplay from '../MultimediaDisplay.vue'

describe('MultimediaDisplay Component', () => {
  let wrapper

  const mockFiles = [
    {
      id: 1,
      fileName: 'photo1.jpg',
      filePath: '/uploads/photo1.jpg',
      fileType: 'jpg',
      fileSize: 1024000,
      uploadDate: '2024-01-01T12:00:00'
    },
    {
      id: 2,
      fileName: 'video1.mp4',
      filePath: '/uploads/video1.mp4',
      fileType: 'mp4',
      fileSize: 5242880,
      uploadDate: '2024-01-02T12:00:00'
    },
    {
      id: 3,
      fileName: 'photo2.png',
      filePath: '/uploads/photo2.png',
      fileType: 'png',
      fileSize: 2048000,
      uploadDate: '2024-01-03T12:00:00'
    }
  ]

  beforeEach(() => {
    wrapper = mount(MultimediaDisplay, {
      props: {
        files: mockFiles,
        baseUrl: 'http://localhost:8080'
      },
      global: {
        stubs: {
          'el-card': true,
          'el-dialog': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-icon': true
        }
      }
    })
  })

  it('should render component with files', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('should display correct number of gallery items', () => {
    const galleryItems = wrapper.findAll('.gallery-item')
    expect(galleryItems).toHaveLength(3)
  })

  it('should identify image files correctly', () => {
    const component = wrapper.vm
    expect(component.isImage(mockFiles[0])).toBe(true)
    expect(component.isImage(mockFiles[1])).toBe(false)
    expect(component.isImage(mockFiles[2])).toBe(true)
  })

  it('should identify video files correctly', () => {
    const component = wrapper.vm
    expect(component.isVideo(mockFiles[0])).toBe(false)
    expect(component.isVideo(mockFiles[1])).toBe(true)
    expect(component.isVideo(mockFiles[2])).toBe(false)
  })

  it('should format file size correctly', () => {
    const component = wrapper.vm
    expect(component.formatFileSize(1024)).toBe('1 KB')
    expect(component.formatFileSize(1024000)).toBe('1000 KB')
    expect(component.formatFileSize(1048576)).toBe('1 MB')
    expect(component.formatFileSize(0)).toBe('0 B')
  })

  it('should construct correct file URL', () => {
    const component = wrapper.vm
    const file = mockFiles[0]
    const url = component.getFileUrl(file)
    expect(url).toBe('http://localhost:8080/uploads/photo1.jpg')
  })

  it('should handle full URL file paths', () => {
    const component = wrapper.vm
    const fileWithFullUrl = {
      ...mockFiles[0],
      filePath: 'https://cdn.example.com/photo1.jpg'
    }
    const url = component.getFileUrl(fileWithFullUrl)
    expect(url).toBe('https://cdn.example.com/photo1.jpg')
  })

  it('should format date correctly', () => {
    const component = wrapper.vm
    const dateString = '2024-01-01T12:00:00'
    const formatted = component.formatDate(dateString)
    expect(formatted).toBeTruthy()
    expect(formatted).toContain('2024')
  })

  it('should not render card when files array is empty', async () => {
    await wrapper.setProps({ files: [] })
    const card = wrapper.find('.display-card')
    expect(card.exists()).toBe(false)
  })

  it('should display file names in gallery items', () => {
    const fileNames = wrapper.findAll('.file-name')
    expect(fileNames).toHaveLength(3)
    expect(fileNames[0].text()).toBe('photo1.jpg')
    expect(fileNames[1].text()).toBe('video1.mp4')
    expect(fileNames[2].text()).toBe('photo2.png')
  })

  it('should display file sizes in gallery items', () => {
    const fileSizes = wrapper.findAll('.file-size')
    expect(fileSizes).toHaveLength(3)
    expect(fileSizes[0].text()).toContain('KB')
    expect(fileSizes[1].text()).toContain('MB')
    expect(fileSizes[2].text()).toContain('KB')
  })

  it('should open preview when gallery item is clicked', async () => {
    const galleryItem = wrapper.find('.gallery-item')
    await galleryItem.trigger('click')
    
    expect(wrapper.vm.previewVisible).toBe(true)
    expect(wrapper.vm.currentFile).toEqual(mockFiles[0])
  })

  it('should support multiple image formats', () => {
    const component = wrapper.vm
    const imageFormats = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp']
    
    imageFormats.forEach(format => {
      const file = { fileType: format }
      expect(component.isImage(file)).toBe(true)
    })
  })

  it('should support multiple video formats', () => {
    const component = wrapper.vm
    const videoFormats = ['mp4', 'avi', 'mov', 'mkv', 'webm', 'flv']
    
    videoFormats.forEach(format => {
      const file = { fileType: format }
      expect(component.isVideo(file)).toBe(true)
    })
  })

  it('should handle case-insensitive file type detection', () => {
    const component = wrapper.vm
    const file = { fileType: 'JPG' }
    expect(component.isImage(file)).toBe(true)
    
    const videoFile = { fileType: 'MP4' }
    expect(component.isVideo(videoFile)).toBe(true)
  })

  it('should handle image load errors', async () => {
    const component = wrapper.vm
    const mockEvent = {
      target: {
        src: '',
        classList: {
          add: vi.fn()
        }
      }
    }
    
    component.handleImageError(mockEvent)
    
    expect(mockEvent.target.classList.add).toHaveBeenCalledWith('load-error')
    expect(mockEvent.target.src).toContain('data:image/svg+xml')
  })

  it('should handle video load errors', async () => {
    const component = wrapper.vm
    const mockEvent = {
      target: {
        classList: {
          add: vi.fn()
        }
      }
    }
    
    component.handleVideoError(mockEvent)
    
    expect(mockEvent.target.classList.add).toHaveBeenCalledWith('load-error')
  })

  it('should render empty state when no files provided', async () => {
    await wrapper.setProps({ files: [] })
    const emptyDisplay = wrapper.find('.multimedia-display')
    expect(emptyDisplay.exists()).toBe(true)
  })

  it('should display file count in card header', async () => {
    const cardHeader = wrapper.find('.card-header')
    expect(cardHeader.text()).toContain('3')
  })
})
