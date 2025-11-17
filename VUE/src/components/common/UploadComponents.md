# Upload Components Documentation

## ImageUpload Component

A reusable component for uploading images with preview, validation, and progress tracking.

### Features
- ✅ Drag-and-drop support
- ✅ Image preview after selection
- ✅ File type validation (JPG, PNG, GIF)
- ✅ File size validation (max 5MB)
- ✅ Upload progress indicator
- ✅ Remove image functionality
- ✅ Error message display
- ✅ Image preview dialog

### Usage

```vue
<template>
  <ImageUpload v-model="imageUrl" @upload-success="handleSuccess" />
</template>

<script setup>
import { ref } from 'vue';
import ImageUpload from '@/components/common/ImageUpload.vue';

const imageUrl = ref('');

const handleSuccess = (url) => {
  console.log('Image uploaded:', url);
};
</script>
```

### Props
- `modelValue` (String): The current image URL (v-model support)

### Events
- `update:modelValue`: Emitted when image URL changes
- `upload-success`: Emitted when upload succeeds (payload: URL string)
- `upload-error`: Emitted when upload fails (payload: error object)

---

## VideoUpload Component

A reusable component for uploading videos with validation, progress tracking, and cancel functionality.

### Features
- ✅ Drag-and-drop support
- ✅ Video preview with controls
- ✅ File type validation (MP4, AVI)
- ✅ File size validation (max 100MB)
- ✅ Upload progress bar
- ✅ Cancel upload functionality
- ✅ Remove video functionality
- ✅ Error message display
- ✅ File info display

### Usage

```vue
<template>
  <VideoUpload v-model="videoUrl" @upload-success="handleSuccess" />
</template>

<script setup>
import { ref } from 'vue';
import VideoUpload from '@/components/common/VideoUpload.vue';

const videoUrl = ref('');

const handleSuccess = (url) => {
  console.log('Video uploaded:', url);
};
</script>
```

### Props
- `modelValue` (String): The current video URL (v-model support)

### Events
- `update:modelValue`: Emitted when video URL changes
- `upload-success`: Emitted when upload succeeds (payload: URL string)
- `upload-error`: Emitted when upload fails (payload: error object)

---

## Integration Examples

### In Profile Avatar Upload
```vue
<ImageUpload v-model="userProfile.avatar" />
```

### In Community Post Creation
```vue
<ImageUpload 
  v-for="(img, index) in postImages" 
  :key="index"
  v-model="postImages[index]"
/>
```

### In Resource Creation (Admin/Coach)
```vue
<VideoUpload 
  v-if="resourceType === 'video'"
  v-model="resourceFileUrl"
/>
```
