<template>
  <div class="map-footprint-container">
    <!-- Map container -->
    <div id="amap-container" class="amap-container"></div>

    <!-- Footprint list and controls -->
    <div class="footprint-controls">
      <div class="control-header">
        <h3>Map Footprints</h3>
        <el-button 
          type="primary" 
          size="small"
          @click="handleAddFootprint"
          :loading="addingFootprint"
        >
          <el-icon><Plus /></el-icon>
          Add Footprint
        </el-button>
      </div>

      <!-- Footprint list -->
      <div class="footprint-list">
        <el-empty 
          v-if="footprints.length === 0" 
          description="No footprints yet"
        />
        <div 
          v-for="footprint in footprints" 
          :key="footprint.id"
          class="footprint-item"
          @click="selectFootprint(footprint)"
          :class="{ active: selectedFootprint?.id === footprint.id }"
        >
          <div class="footprint-info">
            <div class="location-name">{{ footprint.locationName }}</div>
            <div class="coordinates">
              {{ footprint.latitude.toFixed(6) }}, {{ footprint.longitude.toFixed(6) }}
            </div>
            <div v-if="footprint.visitDate" class="visit-date">
              {{ formatDate(footprint.visitDate) }}
            </div>
          </div>
          <el-button 
            type="danger" 
            text 
            size="small"
            @click.stop="handleDeleteFootprint(footprint.id)"
            :loading="deletingFootprintId === footprint.id"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Add footprint dialog -->
    <el-dialog 
      v-model="showAddDialog" 
      title="Add Map Footprint"
      width="500px"
      @close="resetForm"
    >
      <el-form 
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="Location Name" prop="locationName">
          <el-input 
            v-model="formData.locationName"
            placeholder="Enter location name"
          />
        </el-form-item>

        <el-form-item label="Latitude" prop="latitude">
          <el-input-number 
            v-model="formData.latitude"
            :min="-90"
            :max="90"
            :step="0.000001"
            :precision="6"
          />
        </el-form-item>

        <el-form-item label="Longitude" prop="longitude">
          <el-input-number 
            v-model="formData.longitude"
            :min="-180"
            :max="180"
            :step="0.000001"
            :precision="6"
          />
        </el-form-item>

        <el-form-item label="Visit Date" prop="visitDate">
          <el-date-picker 
            v-model="formData.visitDate"
            type="date"
            placeholder="Select visit date"
          />
        </el-form-item>

        <el-form-item label="Geocoding">
          <el-input 
            v-model="geocodingInput"
            placeholder="Enter location name to search"
            @keyup.enter="handleGeocoding"
          >
            <template #append>
              <el-button 
                @click="handleGeocoding"
                :loading="geocodingLoading"
              >
                Search
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <div v-if="geocodingResults.length > 0" class="geocoding-results">
          <div 
            v-for="result in geocodingResults"
            :key="result.id"
            class="geocoding-result-item"
            @click="selectGeocodingResult(result)"
          >
            <div class="result-name">{{ result.name }}</div>
            <div class="result-address">{{ result.address }}</div>
          </div>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">Cancel</el-button>
        <el-button 
          type="primary" 
          @click="handleSubmitFootprint"
          :loading="submittingFootprint"
        >
          Add
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { footprintService } from '../services/footprintService'

const props = defineProps({
  travelRecordId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['footprints-updated'])

// State
const map = ref(null)
const footprints = ref([])
const selectedFootprint = ref(null)
const showAddDialog = ref(false)
const addingFootprint = ref(false)
const deletingFootprintId = ref(null)
const submittingFootprint = ref(false)
const geocodingLoading = ref(false)
const geocodingInput = ref('')
const geocodingResults = ref([])

const formRef = ref(null)
const formData = ref({
  locationName: '',
  latitude: null,
  longitude: null,
  visitDate: null
})

const formRules = {
  locationName: [
    { required: true, message: 'Location name is required', trigger: 'blur' }
  ],
  latitude: [
    { required: true, message: 'Latitude is required', trigger: 'blur' }
  ],
  longitude: [
    { required: true, message: 'Longitude is required', trigger: 'blur' }
  ]
}

// Initialize map
onMounted(async () => {
  try {
    // Load AMap with proper error handling
    const AMap = await AMapLoader.load({
      key: import.meta.env.VITE_AMAP_KEY || 'YOUR_AMAP_KEY', // Use env variable or fallback
      version: '2.0',
      plugins: ['AMap.Geocoder', 'AMap.PlaceSearch']
    })

    // Store AMap globally for use in other methods
    window.AMap = AMap

    // Create map instance
    map.value = new AMap.Map('amap-container', {
      viewMode: '2D',
      zoom: 10,
      center: [116.397428, 39.90923] // Default to Beijing
    })

    // Load existing footprints
    await loadFootprints()
  } catch (error) {
    console.error('Failed to initialize map:', error)
    ElMessage.error('Failed to initialize map. Please check your AMap API key.')
  }
})

// Load footprints from API
const loadFootprints = async () => {
  try {
    addingFootprint.value = true
    const response = await footprintService.getFootprints(props.travelRecordId)
    if (response.data.code === 200) {
      footprints.value = response.data.data
      drawFootprints()
      emit('footprints-updated', footprints.value)
    }
  } catch (error) {
    console.error('Failed to load footprints:', error)
    ElMessage.error('Failed to load footprints')
  } finally {
    addingFootprint.value = false
  }
}

// Draw footprints on map
const drawFootprints = () => {
  if (!map.value || !window.AMap) return

  // Clear existing markers and overlays
  map.value.clearMap()

  // Sort footprints by visit date for polyline drawing
  const sortedFootprints = [...footprints.value].sort((a, b) => {
    if (!a.visitDate) return 1
    if (!b.visitDate) return -1
    return new Date(a.visitDate) - new Date(b.visitDate)
  })

  // Add markers for each footprint
  sortedFootprints.forEach((footprint, index) => {
    const AMap = window.AMap
    const marker = new AMap.Marker({
      position: [footprint.longitude, footprint.latitude],
      title: footprint.locationName,
      label: {
        content: String(index + 1),
        direction: 'top'
      }
    })
    marker.setMap(map.value)
  })

  // Draw polyline connecting all footprints in chronological order
  if (sortedFootprints.length > 1) {
    const AMap = window.AMap
    const path = sortedFootprints.map(f => [f.longitude, f.latitude])
    const polyline = new AMap.Polyline({
      path: path,
      strokeColor: '#0066ff',
      strokeWeight: 2,
      strokeOpacity: 0.8,
      isOutline: true,
      outlineColor: '#ffffff',
      outlineWidth: 3
    })
    polyline.setMap(map.value)
  }

  // Fit map to show all markers
  if (sortedFootprints.length > 0) {
    const positions = sortedFootprints.map(f => [f.longitude, f.latitude])
    const bounds = new window.AMap.Bounds(positions)
    map.value.fitBounds(bounds)
  }
}

// Select footprint
const selectFootprint = (footprint) => {
  selectedFootprint.value = footprint
  if (map.value) {
    map.value.setCenter([footprint.longitude, footprint.latitude])
    map.value.setZoom(15)
  }
}

// Handle add footprint button
const handleAddFootprint = () => {
  showAddDialog.value = true
}

// Handle geocoding search
const handleGeocoding = async () => {
  if (!geocodingInput.value.trim()) {
    ElMessage.warning('Please enter a location name')
    return
  }

  try {
    geocodingLoading.value = true
    if (!window.AMap) {
      ElMessage.error('Map not initialized')
      return
    }

    const AMap = window.AMap
    const geocoder = new AMap.Geocoder()

    geocoder.getLocation(geocodingInput.value, (status, result) => {
      geocodingLoading.value = false
      if (status === 'complete' && result.geocodes && result.geocodes.length > 0) {
        geocodingResults.value = result.geocodes.map(item => ({
          id: item.address_component?.adcode || Math.random(),
          name: item.formatted_address,
          address: (item.address_component?.province || '') + (item.address_component?.city || ''),
          latitude: item.location.lat,
          longitude: item.location.lng
        }))
      } else {
        ElMessage.warning('No results found')
        geocodingResults.value = []
      }
    })
  } catch (error) {
    console.error('Geocoding error:', error)
    ElMessage.error('Geocoding failed')
    geocodingLoading.value = false
  }
}

// Select geocoding result
const selectGeocodingResult = (result) => {
  formData.value.locationName = result.name
  formData.value.latitude = result.latitude
  formData.value.longitude = result.longitude
  geocodingResults.value = []
  geocodingInput.value = ''
}

// Submit footprint
const handleSubmitFootprint = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submittingFootprint.value = true

    const data = {
      travelRecordId: props.travelRecordId,
      locationName: formData.value.locationName,
      latitude: formData.value.latitude,
      longitude: formData.value.longitude,
      visitDate: formData.value.visitDate
    }

    const response = await footprintService.addFootprint(data)
    if (response.data.code === 201 || response.data.code === 200) {
      ElMessage.success('Footprint added successfully')
      showAddDialog.value = false
      resetForm()
      await loadFootprints()
    }
  } catch (error) {
    console.error('Failed to add footprint:', error)
    ElMessage.error(error.response?.data?.message || 'Failed to add footprint')
  } finally {
    submittingFootprint.value = false
  }
}

// Delete footprint
const handleDeleteFootprint = async (footprintId) => {
  try {
    await ElMessageBox.confirm(
      'Are you sure to delete this footprint?',
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    deletingFootprintId.value = footprintId
    const response = await footprintService.deleteFootprint(footprintId)
    if (response.data.code === 200) {
      ElMessage.success('Footprint deleted successfully')
      await loadFootprints()
      if (selectedFootprint.value?.id === footprintId) {
        selectedFootprint.value = null
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete footprint:', error)
      ElMessage.error('Failed to delete footprint')
    }
  } finally {
    deletingFootprintId.value = null
  }
}

// Reset form
const resetForm = () => {
  formData.value = {
    locationName: '',
    latitude: null,
    longitude: null,
    visitDate: null
  }
  geocodingInput.value = ''
  geocodingResults.value = []
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// Format date
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString()
}

// Watch for travel record ID changes
watch(() => props.travelRecordId, () => {
  loadFootprints()
})
</script>

<style scoped>
.map-footprint-container {
  display: flex;
  gap: 20px;
  height: 600px;
}

.amap-container {
  flex: 1;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.footprint-controls {
  width: 300px;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.control-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.control-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.footprint-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.footprint-item {
  padding: 12px;
  margin-bottom: 8px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footprint-item:hover {
  background-color: #f5f7fa;
  border-color: #409eff;
}

.footprint-item.active {
  background-color: #e6f7ff;
  border-color: #409eff;
}

.footprint-info {
  flex: 1;
  min-width: 0;
}

.location-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.coordinates {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.visit-date {
  font-size: 12px;
  color: #909399;
}

.geocoding-results {
  margin-top: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.geocoding-result-item {
  padding: 8px 12px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.geocoding-result-item:hover {
  background-color: #f5f7fa;
}

.geocoding-result-item:last-child {
  border-bottom: none;
}

.result-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.result-address {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .map-footprint-container {
    flex-direction: column;
    height: auto;
  }

  .amap-container {
    height: 400px;
  }

  .footprint-controls {
    width: 100%;
    height: 300px;
  }
}
</style>
