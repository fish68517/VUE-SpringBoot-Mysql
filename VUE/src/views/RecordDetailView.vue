<template>
  <div class="record-detail-container">
    <!-- Loading state -->
    <el-skeleton v-if="loading" :rows="10" animated />

    <!-- Edit mode -->
    <div v-else-if="isEditing" class="record-detail-content">
      <el-button
        type="primary"
        text
        @click="handleCancelEdit"
        style="margin-bottom: 20px"
      >
        <el-icon><ArrowLeft /></el-icon>
        Back to View
      </el-button>
      <h2>Edit Travel Record</h2>
      <TravelRecordEditor
        :record="record"
        @save="handleEditSave"
        @cancel="handleCancelEdit"
      />
    </div>

    <!-- Record detail -->
    <div v-else-if="record" class="record-detail-content">
      <!-- Header -->
      <div class="record-detail-header">
        <div class="header-left">
          <el-button
            type="primary"
            text
            @click="handleBack"
            style="margin-bottom: 20px"
          >
            <el-icon><ArrowLeft /></el-icon>
            Back to Records
          </el-button>
          <h1>{{ record.title }}</h1>
          <div class="record-meta">
            <el-tag
              :type="record.isPublic ? 'success' : 'info'"
              size="large"
            >
              {{ record.isPublic ? 'Public' : 'Private' }}
            </el-tag>
            <span class="created-date">Created on {{ formatDate(record.createdAt) }}</span>
          </div>
        </div>
        <div class="header-actions" v-if="isOwner">
          <el-button 
            :type="record.isPublic ? 'warning' : 'info'"
            @click="handleTogglePrivacy"
            :loading="updatingPrivacy"
          >
            <el-icon><Share /></el-icon>
            {{ record.isPublic ? 'Make Private' : 'Make Public' }}
          </el-button>
          <el-button type="primary" @click="handleEdit">
            <el-icon><Edit /></el-icon>
            Edit
          </el-button>
          <el-popconfirm
            title="Are you sure to delete this record?"
            confirm-button-text="Yes"
            cancel-button-text="No"
            @confirm="handleDelete"
          >
            <template #reference>
              <el-button type="danger">
                <el-icon><Delete /></el-icon>
                Delete
              </el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>

      <!-- Basic info -->
      <div class="record-info-section">
        <div class="info-item">
          <span class="info-label">
            <el-icon><Location /></el-icon>
            Destination
          </span>
          <span class="info-value">{{ record.destination }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">
            <el-icon><Calendar /></el-icon>
            Travel Dates
          </span>
          <span class="info-value">{{ formatDateRange(record.startDate, record.endDate) }}</span>
        </div>
      </div>

      <!-- Description -->
      <div v-if="record.description" class="section">
        <h3>Description</h3>
        <p class="description-text">{{ record.description }}</p>
      </div>

      <!-- Diary content -->
      <div v-if="record.diaryContent" class="section">
        <h3>Diary</h3>
        <div class="diary-content" v-html="record.diaryContent"></div>
      </div>

      <!-- Map footprints -->
      <div v-if="isOwner" class="section">
        <h3>Map Footprints</h3>
        <MapFootprint
          :travel-record-id="record.id"
          @footprints-updated="handleFootprintsUpdated"
        />
      </div>

      <!-- Multimedia display -->
      <div v-if="multimediaFiles.length > 0" class="section">
        <MultimediaDisplay
          :files="multimediaFiles"
          base-url="http://localhost:8080"
        />
      </div>

      <!-- Multimedia files upload -->
      <div v-if="isOwner" class="section">
        <MultimediaUpload
          :travel-record-id="record.id"
          @upload-complete="handleUploadComplete"
          @file-deleted="handleFileDeleted"
        />
      </div>

      <!-- Like and Comment section -->
      <div v-if="record.isPublic || isOwner" class="section">
        <div class="interaction-section">
          <el-button
            :type="isLiked ? 'danger' : 'info'"
            @click="handleToggleLike"
            :loading="liking"
          >
            <el-icon><Share /></el-icon>
            {{ isLiked ? 'Unlike' : 'Like' }} ({{ record.likeCount || 0 }})
          </el-button>
        </div>
      </div>

      <!-- Comments section -->
      <CommentSection
        v-if="record.isPublic || isOwner"
        :travel-record-id="record.id"
      />

      <!-- Error message -->
      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        closable
        @close="errorMessage = ''"
        style="margin-top: 20px"
      />
    </div>

    <!-- Not found state -->
    <el-empty
      v-else
      description="Record not found"
      style="margin-top: 40px"
    >
      <el-button @click="handleBack">Back to Records</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Location, Calendar, Edit, Delete, Share, Share } from '@element-plus/icons-vue'
import { travelService } from '../services/travelService'
import { useTravelStore } from '../stores/travelStore'
import { useUserStore } from '../stores/userStore'
import TravelRecordEditor from '../components/TravelRecordEditor.vue'
import MultimediaUpload from '../components/MultimediaUpload.vue'
import MultimediaDisplay from '../components/MultimediaDisplay.vue'
import MapFootprint from '../components/MapFootprint.vue'
import CommentSection from '../components/CommentSection.vue'
import fileService from '../services/fileService'
import likeService from '../services/likeService'

const router = useRouter()
const route = useRoute()
const travelStore = useTravelStore()
const userStore = useUserStore()

const record = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const isEditing = ref(false)
const updatingPrivacy = ref(false)
const multimediaFiles = ref([])
const liking = ref(false)
const isLiked = ref(false)

// Check if current user is the owner of the record
const isOwner = computed(() => {
  return record.value && userStore.user && record.value.userId === userStore.user.id
})

// Fetch record on component mount
onMounted(() => {
  fetchRecord()
})

const fetchRecord = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    const recordId = route.params.id
    const response = await travelService.getTravelRecordById(recordId)

    // Handle the ApiResponse structure
    if (response && response.data) {
      record.value = response.data
      travelStore.setCurrentRecord(response.data)
      
      // Fetch multimedia files for this record
      await fetchMultimediaFiles(recordId)
    }
  } catch (error) {
    errorMessage.value = error.message || 'Failed to load travel record'
    console.error('Error fetching record:', error)
  } finally {
    loading.value = false
  }
}

const fetchMultimediaFiles = async (recordId) => {
  try {
    const response = await fileService.getFilesByTravelRecord(recordId)
    if (response && response.data) {
      multimediaFiles.value = response.data
    }
  } catch (error) {
    console.error('Error fetching multimedia files:', error)
    // Don't show error to user, just log it
  }
}

const handleBack = () => {
  router.push('/records')
}

const handleEdit = () => {
  isEditing.value = true
}

const handleCancelEdit = () => {
  isEditing.value = false
}

const handleEditSave = async (updatedRecord) => {
  record.value = updatedRecord
  travelStore.setCurrentRecord(updatedRecord)
  isEditing.value = false
}

const handleDelete = async () => {
  try {
    loading.value = true
    await travelService.deleteTravelRecord(record.value.id)
    ElMessage.success('Travel record deleted successfully!')
    router.push('/records')
  } catch (error) {
    loading.value = false
    errorMessage.value = error.message || 'Failed to delete travel record'
    console.error('Error deleting record:', error)
  }
}

const handleTogglePrivacy = async () => {
  try {
    updatingPrivacy.value = true
    errorMessage.value = ''

    // Prepare update data with toggled privacy setting
    const updatedData = {
      title: record.value.title,
      destination: record.value.destination,
      startDate: record.value.startDate,
      endDate: record.value.endDate,
      description: record.value.description,
      diaryContent: record.value.diaryContent,
      isPublic: !record.value.isPublic
    }

    // Call API to update record
    const response = await travelService.updateTravelRecord(record.value.id, updatedData)

    // Update local record
    record.value = response.data
    travelStore.setCurrentRecord(response.data)

    const message = response.data.isPublic ? 'Record is now public' : 'Record is now private'
    ElMessage.success(message)
  } catch (error) {
    updatingPrivacy.value = false
    errorMessage.value = error.message || 'Failed to update privacy setting'
    console.error('Error updating privacy:', error)
  } finally {
    updatingPrivacy.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatDateRange = (startDate, endDate) => {
  if (!startDate || !endDate) return ''
  const start = new Date(startDate).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric'
  })
  const end = new Date(endDate).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
  return `${start} - ${end}`
}

const handleUploadComplete = async () => {
  ElMessage.success('Files uploaded successfully!')
  // Refresh multimedia files list
  if (record.value) {
    await fetchMultimediaFiles(record.value.id)
  }
}

const handleFileDeleted = async () => {
  ElMessage.success('File deleted successfully!')
  // Refresh multimedia files list
  if (record.value) {
    await fetchMultimediaFiles(record.value.id)
  }
}

const handleFootprintsUpdated = (footprints) => {
  // Handle footprints update event from MapFootprint component
  console.log('Footprints updated:', footprints)
}

const handleToggleLike = async () => {
  if (!userStore.user) {
    ElMessage.warning('Please log in to like records')
    router.push('/login')
    return
  }

  try {
    liking.value = true
    if (isLiked.value) {
      // Unlike
      await likeService.deleteLikeByTravelRecord(record.value.id)
      record.value.likeCount = Math.max(0, (record.value.likeCount || 0) - 1)
      isLiked.value = false
      ElMessage.success('Like removed')
    } else {
      // Like
      await likeService.createLike(record.value.id)
      record.value.likeCount = (record.value.likeCount || 0) + 1
      isLiked.value = true
      ElMessage.success('Liked!')
    }
  } catch (error) {
    ElMessage.error(error.message || 'Failed to update like')
  } finally {
    liking.value = false
  }
}
</script>

<style scoped>
.record-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.record-detail-content {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.record-detail-header {
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-left {
  flex: 1;
}

.header-left h1 {
  margin: 10px 0;
  color: #333;
  font-size: 32px;
}

.header-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.record-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 10px;
}

.created-date {
  font-size: 14px;
  color: #999;
}

.record-info-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.info-value {
  color: #666;
  font-size: 16px;
  padding-left: 24px;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.description-text {
  color: #666;
  line-height: 1.6;
  font-size: 15px;
  margin: 0;
}

.diary-content {
  color: #666;
  line-height: 1.8;
  font-size: 15px;
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

:deep(.diary-content img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 10px 0;
}

:deep(.diary-content p) {
  margin: 10px 0;
}

:deep(.diary-content h1),
:deep(.diary-content h2),
:deep(.diary-content h3) {
  margin: 15px 0 10px 0;
  color: #333;
}

:deep(.diary-content blockquote) {
  border-left: 4px solid #dcdfe6;
  padding-left: 15px;
  margin: 10px 0;
  color: #999;
}

:deep(.diary-content code) {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
}

:deep(.diary-content pre) {
  background: #f0f0f0;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
}

.interaction-section {
  display: flex;
  gap: 10px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .record-detail-content {
    padding: 20px;
  }

  .record-detail-header h1 {
    font-size: 24px;
  }

  .record-info-section {
    grid-template-columns: 1fr;
  }
}
</style>
