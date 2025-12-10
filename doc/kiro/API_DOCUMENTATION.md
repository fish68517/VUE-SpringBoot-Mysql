# Travel Memory System - API Documentation

## Overview

The Travel Memory System API is a RESTful API built with Spring Boot that provides comprehensive endpoints for managing travel records, plans, multimedia files, and social interactions. The API uses JWT (JSON Web Token) for authentication and returns responses in JSON format.

## Base URL

- **Development**: `http://localhost:8080/api`
- **Production**: `https://api.travelmemory.com/api`

## Authentication

All protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

### Obtaining a Token

1. Register a new account via `/auth/register`
2. Login via `/auth/login` to receive a JWT token
3. Include the token in all subsequent requests

### Token Expiration

- Token validity: 24 hours
- Refresh token: Use `/auth/refresh` endpoint to get a new token

## Response Format

All API responses follow a standard format:

### Success Response (200, 201)
```json
{
  "code": 200,
  "message": "Operation successful",
  "data": {
    // Response data
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Error Response (4xx, 5xx)
```json
{
  "code": 400,
  "message": "Error message",
  "errors": [
    {
      "field": "email",
      "message": "Email format is invalid"
    }
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## API Endpoints

### Authentication Endpoints

#### Register User
- **Endpoint**: `POST /auth/register`
- **Description**: Create a new user account
- **Authentication**: Not required
- **Request Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```
- **Response**: 
```json
{
  "code": 201,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400000
  }
}
```

#### Login User
- **Endpoint**: `POST /auth/login`
- **Description**: Authenticate user and receive JWT token
- **Authentication**: Not required
- **Request Body**:
```json
{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```
- **Response**: Same as register endpoint

#### Refresh Token
- **Endpoint**: `POST /auth/refresh`
- **Description**: Generate a new JWT token
- **Authentication**: Required
- **Request Body**:
```json
{
  "token": "expired_jwt_token"
}
```
- **Response**: Returns new token

### User Endpoints

#### Get User Profile
- **Endpoint**: `GET /users/{id}`
- **Description**: Retrieve user profile information
- **Authentication**: Required
- **Path Parameters**: `id` - User ID
- **Response**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "avatarUrl": "https://...",
    "bio": "Travel enthusiast",
    "createdAt": "2024-01-01T12:00:00Z"
  }
}
```

#### Update User Profile
- **Endpoint**: `PUT /users/{id}`
- **Description**: Update user profile information
- **Authentication**: Required
- **Path Parameters**: `id` - User ID
- **Request Body**:
```json
{
  "username": "john_doe_updated",
  "bio": "Updated bio",
  "avatarUrl": "https://..."
}
```
- **Response**: Updated user object

### Travel Record Endpoints

#### Create Travel Record
- **Endpoint**: `POST /travels`
- **Description**: Create a new travel record
- **Authentication**: Required
- **Request Body**:
```json
{
  "title": "Tokyo Adventure",
  "destination": "Tokyo, Japan",
  "startDate": "2024-03-15",
  "endDate": "2024-03-25",
  "description": "Amazing trip to Tokyo",
  "isPublic": true
}
```
- **Response**:
```json
{
  "code": 201,
  "data": {
    "id": 1,
    "userId": 1,
    "title": "Tokyo Adventure",
    "destination": "Tokyo, Japan",
    "startDate": "2024-03-15",
    "endDate": "2024-03-25",
    "description": "Amazing trip to Tokyo",
    "diaryContent": null,
    "isPublic": true,
    "viewCount": 0,
    "createdAt": "2024-01-01T12:00:00Z"
  }
}
```

#### Get Travel Records
- **Endpoint**: `GET /travels?page=0&size=10&sort=createdAt,desc`
- **Description**: Retrieve user's travel records with pagination
- **Authentication**: Required
- **Query Parameters**:
  - `page` - Page number (0-indexed)
  - `size` - Records per page
  - `sort` - Sort field and direction
- **Response**:
```json
{
  "code": 200,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Tokyo Adventure",
        // ... record details
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

#### Get Travel Record Details
- **Endpoint**: `GET /travels/{id}`
- **Description**: Retrieve a specific travel record
- **Authentication**: Required (for private records)
- **Path Parameters**: `id` - Travel record ID
- **Response**: Single travel record object

#### Update Travel Record
- **Endpoint**: `PUT /travels/{id}`
- **Description**: Update a travel record
- **Authentication**: Required
- **Path Parameters**: `id` - Travel record ID
- **Request Body**: Same as create endpoint
- **Response**: Updated travel record object

#### Delete Travel Record
- **Endpoint**: `DELETE /travels/{id}`
- **Description**: Delete a travel record and associated data
- **Authentication**: Required
- **Path Parameters**: `id` - Travel record ID
- **Response**:
```json
{
  "code": 200,
  "message": "Travel record deleted successfully"
}
```

#### Get Public Travel Records
- **Endpoint**: `GET /travels/public?page=0&size=10`
- **Description**: Retrieve public travel records (social feed)
- **Authentication**: Not required
- **Query Parameters**: Same as get travel records
- **Response**: List of public travel records

### Multimedia File Endpoints

#### Upload File
- **Endpoint**: `POST /files/upload`
- **Description**: Upload multimedia file with chunked upload support
- **Authentication**: Required
- **Content-Type**: `multipart/form-data`
- **Request Parameters**:
  - `file` - File to upload
  - `travelRecordId` - Associated travel record ID
  - `chunkIndex` - Chunk index (for chunked upload)
  - `totalChunks` - Total number of chunks
- **Response**:
```json
{
  "code": 201,
  "data": {
    "id": 1,
    "fileName": "photo.jpg",
    "filePath": "/uploads/travel_1/photo.jpg",
    "fileType": "image/jpeg",
    "fileSize": 2048576,
    "uploadDate": "2024-01-01T12:00:00Z"
  }
}
```

#### Get File
- **Endpoint**: `GET /files/{id}`
- **Description**: Retrieve file information
- **Authentication**: Required
- **Path Parameters**: `id` - File ID
- **Response**: File object

#### Delete File
- **Endpoint**: `DELETE /files/{id}`
- **Description**: Delete a multimedia file
- **Authentication**: Required
- **Path Parameters**: `id` - File ID
- **Response**: Success message

### Travel Plan Endpoints

#### Create Travel Plan
- **Endpoint**: `POST /plans`
- **Description**: Create a new travel plan
- **Authentication**: Required
- **Request Body**:
```json
{
  "title": "Southeast Asia Tour",
  "destination": "Thailand, Vietnam, Cambodia",
  "startDate": "2024-10-01",
  "endDate": "2024-10-21",
  "budget": 3500.00,
  "description": "Three-week adventure"
}
```
- **Response**: Created plan object

#### Get Travel Plans
- **Endpoint**: `GET /plans?page=0&size=10`
- **Description**: Retrieve user's travel plans
- **Authentication**: Required
- **Query Parameters**: Same as travel records
- **Response**: List of travel plans

#### Get Travel Plan Details
- **Endpoint**: `GET /plans/{id}`
- **Description**: Retrieve a specific travel plan with itinerary items
- **Authentication**: Required
- **Path Parameters**: `id` - Plan ID
- **Response**: Plan object with itinerary items

#### Update Travel Plan
- **Endpoint**: `PUT /plans/{id}`
- **Description**: Update a travel plan
- **Authentication**: Required
- **Path Parameters**: `id` - Plan ID
- **Request Body**: Same as create endpoint
- **Response**: Updated plan object

#### Delete Travel Plan
- **Endpoint**: `DELETE /plans/{id}`
- **Description**: Delete a travel plan
- **Authentication**: Required
- **Path Parameters**: `id` - Plan ID
- **Response**: Success message

### Itinerary Item Endpoints

#### Add Itinerary Item
- **Endpoint**: `POST /plans/{planId}/items`
- **Description**: Add an item to a travel plan
- **Authentication**: Required
- **Path Parameters**: `planId` - Plan ID
- **Request Body**:
```json
{
  "itemDate": "2024-10-02",
  "itemType": "accommodation",
  "title": "Bangkok Hotel",
  "description": "Stay at Sukhumvit area",
  "location": "Bangkok, Thailand"
}
```
- **Response**: Created itinerary item

#### Update Itinerary Item
- **Endpoint**: `PUT /items/{id}`
- **Description**: Update an itinerary item
- **Authentication**: Required
- **Path Parameters**: `id` - Item ID
- **Request Body**: Same as add endpoint
- **Response**: Updated item

#### Delete Itinerary Item
- **Endpoint**: `DELETE /items/{id}`
- **Description**: Delete an itinerary item
- **Authentication**: Required
- **Path Parameters**: `id` - Item ID
- **Response**: Success message

### Map Footprint Endpoints

#### Add Map Footprint
- **Endpoint**: `POST /footprints`
- **Description**: Add a location marker to a travel record
- **Authentication**: Required
- **Request Body**:
```json
{
  "travelRecordId": 1,
  "locationName": "Senso-ji Temple",
  "latitude": 35.71475,
  "longitude": 139.79695,
  "visitDate": "2024-03-16"
}
```
- **Response**: Created footprint object

#### Get Travel Footprints
- **Endpoint**: `GET /travels/{travelId}/footprints`
- **Description**: Retrieve all footprints for a travel record
- **Authentication**: Required
- **Path Parameters**: `travelId` - Travel record ID
- **Response**: List of footprints

#### Delete Footprint
- **Endpoint**: `DELETE /footprints/{id}`
- **Description**: Delete a map footprint
- **Authentication**: Required
- **Path Parameters**: `id` - Footprint ID
- **Response**: Success message

### Comment Endpoints

#### Add Comment
- **Endpoint**: `POST /comments`
- **Description**: Add a comment to a travel record
- **Authentication**: Required
- **Request Body**:
```json
{
  "travelRecordId": 1,
  "content": "Amazing photos!"
}
```
- **Response**: Created comment object

#### Get Comments
- **Endpoint**: `GET /travels/{travelId}/comments`
- **Description**: Retrieve comments for a travel record
- **Authentication**: Not required
- **Path Parameters**: `travelId` - Travel record ID
- **Response**: List of comments

#### Delete Comment
- **Endpoint**: `DELETE /comments/{id}`
- **Description**: Delete a comment
- **Authentication**: Required
- **Path Parameters**: `id` - Comment ID
- **Response**: Success message

### Like Endpoints

#### Add Like
- **Endpoint**: `POST /likes`
- **Description**: Like a travel record
- **Authentication**: Required
- **Request Body**:
```json
{
  "travelRecordId": 1
}
```
- **Response**: Created like object

#### Delete Like
- **Endpoint**: `DELETE /likes/{id}`
- **Description**: Unlike a travel record
- **Authentication**: Required
- **Path Parameters**: `id` - Like ID
- **Response**: Success message

## Error Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Authentication required or failed |
| 403 | Forbidden - Access denied |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |
| 500 | Internal Server Error - Server error |

## Rate Limiting

- **Limit**: 1000 requests per hour per user
- **Headers**: 
  - `X-RateLimit-Limit`: Total requests allowed
  - `X-RateLimit-Remaining`: Remaining requests
  - `X-RateLimit-Reset`: Time when limit resets

## Pagination

All list endpoints support pagination:

- **page**: 0-indexed page number (default: 0)
- **size**: Records per page (default: 10, max: 100)
- **sort**: Sort field and direction (e.g., `createdAt,desc`)

## File Upload Limits

- **Maximum file size**: 500MB
- **Allowed formats**: jpg, jpeg, png, gif, mp4, avi, mov, mkv
- **Chunked upload**: Recommended for files > 100MB

## Interactive API Documentation

Access the interactive Swagger UI at:
- **Development**: `http://localhost:8080/api/swagger-ui.html`
- **Production**: `https://api.travelmemory.com/api/swagger-ui.html`

## Examples

### Example: Complete User Journey

1. **Register**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'
```

2. **Create Travel Record**
```bash
curl -X POST http://localhost:8080/api/travels \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tokyo Adventure",
    "destination": "Tokyo, Japan",
    "startDate": "2024-03-15",
    "endDate": "2024-03-25",
    "description": "Amazing trip",
    "isPublic": true
  }'
```

3. **Upload File**
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer <token>" \
  -F "file=@photo.jpg" \
  -F "travelRecordId=1"
```

4. **Add Comment**
```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "travelRecordId": 1,
    "content": "Amazing photos!"
  }'
```

## Support

For API support and issues:
- **Email**: api-support@travelmemory.com
- **GitHub Issues**: https://github.com/travelmemory/api/issues
- **Documentation**: https://docs.travelmemory.com

