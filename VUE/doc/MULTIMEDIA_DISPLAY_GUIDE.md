# Multimedia Display Implementation Guide

## Overview

The multimedia display feature allows users to view photos and videos associated with their travel records. The implementation includes:

- Gallery grid layout for displaying thumbnails
- Image and video preview functionality
- Lazy loading with Intersection Observer for performance optimization
- Error handling for failed image/video loads
- Responsive design for different screen sizes
- File information display (name, size, type, upload date)

## Components

### MultimediaDisplay.vue

Main component for displaying multimedia files in a gallery format.

**Props:**
- `files` (Array): Array of multimedia file objects
  - `id` (Number): File ID
  - `fileName` (String): Original file name
  - `filePath` (String): File path or URL
  - `fileType` (String): File extension (jpg, png, mp4, etc.)
  - `fileSize` (Number): File size in bytes
  - `uploadDate` (String): Upload timestamp

- `baseUrl` (String): Base URL for constructing file URLs (default: 'http://localhost:8080')

**Features:**

1. **Gallery Grid Layout**
   - Responsive grid that adapts to screen size
   - Minimum item width: 150px on desktop, 120px on tablet, 100px on mobile
   - Hover effects with zoom and shadow

2. **Image and Video Support**
   - Supported image formats: jpg, jpeg, png, gif, webp, bmp
   - Supported video formats: mp4, avi, mov, mkv, webm, flv
   - Automatic format detection based on file extension

3. **Lazy Loading**
   - Uses Intersection Observer API for efficient loading
   - Images load only when they come into viewport
   - 50px margin for preloading before entering viewport
   - Shimmer animation during loading
   - Error placeholder for failed loads

4. **Preview Dialog**
   - Click on thumbnail to open full preview
   - Shows full-size image or video player
   - Displays file details (name, size, type, upload date)
   - Responsive preview sizing

5. **Error Handling**
   - Graceful fallback for failed image loads
   - Error placeholder SVG with "Failed to load" message
   - Video error handling with visual indicator

## Usage

### Basic Usage

```vue
<template>
  <MultimediaDisplay
    :files="multimediaFiles"
    base-url="http://localhost:8080"
  />
</template>

<script setup>
import { ref } from 'vue'
import MultimediaDisplay from '@/components/MultimediaDisplay.vue'

const multimediaFiles = ref([
  {
    id: 1,
    fileName: 'photo.jpg',
    filePath: '/uploads/photo.jpg',
    fileType: 'jpg',
    fileSize: 1024000,
    uploadDate: '2024-01-01T12:00:00'
  }
])
</script>
```

### Integration with RecordDetailView

The component is integrated in `RecordDetailView.vue`:

```vue
<!-- Multimedia display -->
<div v-if="multimediaFiles.length > 0" class="section">
  <MultimediaDisplay
    :files="multimediaFiles"
    base-url="http://localhost:8080"
  />
</div>
```

Files are fetched using the fileService:

```javascript
const fetchMultimediaFiles = async (recordId) => {
  try {
    const response = await fileService.getFilesByTravelRecord(recordId)
    if (response && response.data) {
      multimediaFiles.value = response.data
    }
  } catch (error) {
    console.error('Error fetching multimedia files:', error)
  }
}
```

## Lazy Loading Implementation

### Intersection Observer Directive

The `v-lazy` directive is registered in `main.js` and uses Intersection Observer for efficient image loading:

```javascript
app.directive('lazy', {
  mounted(el) {
    const imageUrl = el.getAttribute('v-lazy')
    if (!imageUrl) return

    el.classList.add('lazy-loading')

    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const img = new Image()
          img.onload = () => {
            el.src = imageUrl
            el.classList.remove('lazy-loading')
            el.classList.add('lazy-loaded')
            observer.unobserve(el)
          }
          img.onerror = () => {
            el.src = 'data:image/svg+xml,...'
            el.classList.remove('lazy-loading')
            el.classList.add('lazy-error')
            observer.unobserve(el)
          }
          img.src = imageUrl
        }
      })
    }, {
      rootMargin: '50px'
    })

    observer.observe(el)
  }
})
```

**Benefits:**
- Only loads images when they're about to enter the viewport
- Reduces initial page load time
- Saves bandwidth by not loading off-screen images
- 50px margin for preloading before entering viewport

### CSS Animations

Shimmer animation during loading:

```css
.thumbnail.lazy-loading {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
```

## Error Handling

### Image Load Errors

When an image fails to load:

```javascript
const handleImageError = (event) => {
  event.target.src = 'data:image/svg+xml,...'
  event.target.classList.add('load-error')
}
```

### Video Load Errors

When a video fails to load:

```javascript
const handleVideoError = (event) => {
  event.target.classList.add('load-error')
}
```

## Responsive Design

The component adapts to different screen sizes:

**Desktop (> 768px):**
- Grid columns: auto-fill with minmax(150px, 1fr)
- Gap: 15px

**Tablet (≤ 768px):**
- Grid columns: auto-fill with minmax(120px, 1fr)
- Gap: 10px
- Preview max-height: 400px

**Mobile (≤ 480px):**
- Grid columns: auto-fill with minmax(100px, 1fr)
- Gap: 8px
- Preview max-height: 300px

## Performance Optimization

1. **Lazy Loading**: Images load only when visible
2. **Chunked Upload**: Large files are uploaded in chunks
3. **Responsive Images**: Different sizes for different devices
4. **Efficient Grid**: CSS Grid for optimal layout
5. **Error Handling**: Graceful fallbacks prevent broken UI

## Testing

### Unit Tests

Tests for MultimediaDisplay component:

```bash
npm run test -- src/components/__tests__/MultimediaDisplay.test.js --run
```

**Test Coverage:**
- Component rendering
- Gallery item count
- Image/video type detection
- File size formatting
- URL construction
- Date formatting
- Preview functionality
- Empty state handling
- Error handling

### Integration Tests

Tests for RecordDetailView multimedia display:

```bash
npm run test -- src/views/__tests__/RecordDetailView.multimedia.test.js --run
```

**Test Coverage:**
- Multimedia file fetching
- File display in component
- Props passing
- Upload/delete refresh
- Error handling

## API Integration

### File Service

The `fileService.js` provides methods for multimedia operations:

```javascript
// Get all files for a travel record
const response = await fileService.getFilesByTravelRecord(travelRecordId)

// Upload a file chunk
const response = await fileService.uploadFileChunk(
  travelRecordId,
  chunk,
  chunkIndex,
  totalChunks,
  fileName,
  fileSize
)

// Delete a file
await fileService.deleteFile(fileId)
```

### Backend API Endpoints

- `GET /api/files/travel/{travelRecordId}` - Get all files for a record
- `POST /api/files/upload` - Upload file chunk
- `DELETE /api/files/{fileId}` - Delete a file

## Browser Compatibility

- Intersection Observer: Supported in all modern browsers
- CSS Grid: Supported in all modern browsers
- Video element: Supported in all modern browsers
- Lazy loading attribute: Supported in all modern browsers

## Future Enhancements

1. **Image Optimization**: Automatic image compression and resizing
2. **CDN Integration**: Serve images from CDN for faster loading
3. **Caching**: Browser caching for frequently accessed images
4. **Progressive Loading**: Show low-quality placeholder while loading
5. **Lightbox Gallery**: Enhanced preview with navigation
6. **Batch Operations**: Select and delete multiple files
7. **Drag and Drop**: Reorder files in gallery
8. **Filters**: Filter by file type or date range
