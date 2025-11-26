# Map Footprint Implementation Guide

## Overview
This guide documents the implementation of the Map Footprint feature for the Travel Memory System. The feature allows users to add, view, and delete map footprints (locations) for their travel records.

## Backend Implementation

### 1. Entity: MapFootprint
**File:** `com.travelMemory.entity.MapFootprint`

The MapFootprint entity represents a location marked on a map for a travel record.

**Key Fields:**
- `id`: Unique identifier
- `travelRecordId`: Foreign key to TravelRecord
- `locationName`: Name of the location
- `latitude`: Latitude coordinate (precision: 10.8)
- `longitude`: Longitude coordinate (precision: 11.8)
- `visitDate`: Optional date of visit
- `createdAt`: Timestamp of creation

**Database Table:** `map_footprints`

### 2. Repository: MapFootprintRepository
**File:** `com.travelMemory.repository.MapFootprintRepository`

Provides data access methods for MapFootprint entities.

**Key Methods:**
- `findByTravelRecordId(Long travelRecordId)`: Get all footprints for a travel record
- `deleteByTravelRecordId(Long travelRecordId)`: Delete all footprints for a travel record

### 3. DTOs

#### CreateMapFootprintRequest
**File:** `com.travelMemory.dto.CreateMapFootprintRequest`

Request DTO for creating a new map footprint.

**Fields:**
- `travelRecordId`: Required, the travel record ID
- `locationName`: Required, the location name
- `latitude`: Required, latitude coordinate (-90 to 90)
- `longitude`: Required, longitude coordinate (-180 to 180)
- `visitDate`: Optional, the visit date

#### MapFootprintResponse
**File:** `com.travelMemory.dto.MapFootprintResponse`

Response DTO for map footprint data.

**Fields:**
- `id`: Footprint ID
- `travelRecordId`: Travel record ID
- `locationName`: Location name
- `latitude`: Latitude coordinate
- `longitude`: Longitude coordinate
- `visitDate`: Visit date
- `createdAt`: Creation timestamp

### 4. Service: FootprintService
**File:** `com.travelMemory.service.FootprintService`

Business logic layer for map footprint operations.

**Key Methods:**

#### addFootprint(Long userId, CreateMapFootprintRequest request)
- Adds a new map footprint to a travel record
- Verifies user ownership of the travel record
- Validates coordinates
- Returns MapFootprintResponse

#### getFootprintsByTravelRecord(Long travelRecordId, Long userId)
- Retrieves all footprints for a travel record
- Verifies user ownership
- Returns List<MapFootprintResponse>

#### deleteFootprint(Long footprintId, Long userId)
- Deletes a specific footprint
- Verifies user ownership
- Throws IllegalArgumentException if access denied

#### deleteFootprintsByTravelRecord(Long travelRecordId)
- Deletes all footprints for a travel record
- Called when travel record is deleted

### 5. Controller: FootprintController
**File:** `com.travelMemory.controller.FootprintController`

REST API endpoints for map footprint operations.

**Endpoints:**

#### POST /footprints
- Add a new map footprint
- Request: CreateMapFootprintRequest
- Response: MapFootprintResponse
- Status: 201 Created
- Auth: Required

#### GET /footprints/travels/{travelRecordId}
- Get all footprints for a travel record
- Response: List<MapFootprintResponse>
- Status: 200 OK
- Auth: Required

#### DELETE /footprints/{footprintId}
- Delete a map footprint
- Status: 200 OK
- Auth: Required

## Frontend Implementation

### 1. Service: footprintService.js
**File:** `src/services/footprintService.js`

Encapsulates all API calls related to map footprints.

**Methods:**

#### addFootprint(data)
- Adds a new footprint
- Parameters: { travelRecordId, locationName, latitude, longitude, visitDate }
- Returns: Promise

#### getFootprints(travelRecordId)
- Gets all footprints for a travel record
- Parameters: travelRecordId
- Returns: Promise

#### deleteFootprint(footprintId)
- Deletes a footprint
- Parameters: footprintId
- Returns: Promise

### 2. Component: MapFootprint.vue
**File:** `src/components/MapFootprint.vue`

Vue component for displaying and managing map footprints.

**Features:**
- Interactive map display using AMap (高德地图)
- Add footprints with geocoding support
- View all footprints in a list
- Delete footprints
- Draw polyline connecting all footprints
- Responsive design

**Props:**
- `travelRecordId`: The travel record ID (required)

**Emits:**
- `footprints-updated`: Emitted when footprints are updated

**Key Methods:**
- `loadFootprints()`: Load footprints from API
- `drawFootprints()`: Draw markers and polyline on map
- `handleGeocoding()`: Search for location by name
- `handleSubmitFootprint()`: Add new footprint
- `handleDeleteFootprint()`: Delete footprint

**Important Notes:**
- Requires AMap API key to be configured
- Replace `YOUR_AMAP_KEY` with actual AMap key
- Supports both manual coordinate entry and geocoding search

## Integration Steps

### Backend Integration

1. **Ensure FootprintService is injected in TravelService**
   - Already done in the implementation
   - Footprints are automatically deleted when travel record is deleted

2. **Verify database schema**
   - The `map_footprints` table should exist
   - Run `schema.sql` if not already executed

3. **Test API endpoints**
   - Use Postman or similar tool to test endpoints
   - Verify authentication and authorization

### Frontend Integration

1. **Import MapFootprint component in RecordDetailView**
   ```vue
   import MapFootprint from '../components/MapFootprint.vue'
   ```

2. **Add component to template**
   ```vue
   <MapFootprint 
     :travelRecordId="record.id"
     @footprints-updated="handleFootprintsUpdated"
   />
   ```

3. **Configure AMap API key**
   - Get API key from AMap (https://lbs.amap.com/)
   - Replace `YOUR_AMAP_KEY` in MapFootprint.vue

4. **Test component**
   - Add footprints to a travel record
   - Verify markers appear on map
   - Test geocoding search
   - Test deletion

## API Response Format

### Success Response
```json
{
  "code": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "travelRecordId": 1,
    "locationName": "Beijing",
    "latitude": 39.9042,
    "longitude": 116.4074,
    "visitDate": "2024-01-01",
    "createdAt": "2024-01-01T12:00:00"
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Error Response
```json
{
  "code": 400,
  "message": "Latitude must be between -90 and 90",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## Error Handling

### Backend Errors
- **400 Bad Request**: Invalid input (coordinates out of range, missing required fields)
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: User does not own the travel record
- **404 Not Found**: Travel record or footprint not found
- **500 Internal Server Error**: Server error

### Frontend Errors
- Displays user-friendly error messages using ElMessage
- Validates form input before submission
- Handles network errors gracefully

## Testing

### Backend Testing
1. Test coordinate validation
2. Test ownership verification
3. Test cascade deletion when travel record is deleted
4. Test API endpoints with various inputs

### Frontend Testing
1. Test map initialization
2. Test adding footprints
3. Test geocoding search
4. Test deleting footprints
5. Test responsive design

## Performance Considerations

1. **Database Indexes**: The `map_footprints` table has an index on `travel_record_id` for efficient queries
2. **Lazy Loading**: Footprints are loaded only when needed
3. **Map Optimization**: Markers and polylines are cleared before redrawing
4. **Pagination**: Not implemented for footprints as they are typically limited per record

## Security Considerations

1. **Ownership Verification**: All operations verify user ownership of the travel record
2. **Input Validation**: Coordinates are validated to be within valid ranges
3. **Authentication**: All endpoints require JWT authentication
4. **Authorization**: Users can only access their own footprints

## Future Enhancements

1. **Batch Operations**: Add ability to add multiple footprints at once
2. **Route Optimization**: Calculate optimal route between footprints
3. **Clustering**: Group nearby footprints on map
4. **Export**: Export footprints as KML or GPX format
5. **Sharing**: Share footprints with other users
6. **Offline Support**: Cache footprints for offline viewing
