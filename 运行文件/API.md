# API Documentation

## Overview

The Little Shark Fitness Management System provides a RESTful API for all client-server interactions. All endpoints return a unified response format and require authentication via Bearer tokens.

## Base URL

```
http://localhost:8080/api
```

## Response Format

All API responses follow this unified format:

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

**Response Fields:**
- `code`: HTTP status code (200 for success, 4xx/5xx for errors)
- `msg`: Human-readable message
- `data`: Response payload (object, array, or null)

## Authentication

### Token-Based Authentication

The system uses Bearer token authentication. Include the token in the Authorization header:

```
Authorization: Bearer {token}
```

### Obtaining a Token

Tokens are obtained through the login endpoint and stored in the client's localStorage.

## Error Responses

Error responses follow the same format with appropriate status codes:

```json
{
  "code": 401,
  "msg": "Unauthorized",
  "data": null
}
```

**Common Error Codes:**
- `400`: Bad Request - Invalid input
- `401`: Unauthorized - Missing or invalid token
- `403`: Forbidden - Insufficient permissions
- `404`: Not Found - Resource doesn't exist
- `500`: Internal Server Error

## Endpoints

### Authentication Endpoints

#### Register User

```
POST /auth/register
```

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "role": "user|coach|admin"
}
```

**Response:**
```json
{
  "code": 200,
  "msg": "Registration successful",
  "data": {
    "id": 1,
    "username": "john_doe",
    "role": "user"
  }
}
```

**Validation:**
- Username: Required, unique, 3-50 characters
- Password: Required, 6+ characters
- Role: Required, must be valid role

---

#### Login

```
POST /auth/login
```

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "code": 200,
  "msg": "Login successful",
  "data": {
    "token": "uuid_userId",
    "user": {
      "id": 1,
      "username": "john_doe",
      "role": "user",
      "avatar": "url",
      "gender": "male",
      "intro": "string"
    }
  }
}
```

---

### User Profile Endpoints

#### Get Current User Profile

```
GET /users/profile
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "john_doe",
    "role": "user",
    "avatar": "url",
    "gender": "male",
    "intro": "Fitness enthusiast",
    "createdAt": "2025-01-15T10:30:00"
  }
}
```

---

#### Update User Profile

```
PUT /users/profile
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "gender": "male|female|other",
  "intro": "string"
}
```

**Response:**
```json
{
  "code": 200,
  "msg": "Profile updated successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "gender": "male",
    "intro": "Updated intro"
  }
}
```

---

#### Upload Avatar

```
POST /users/avatar
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Form Data:**
- `file`: Image file (JPG, PNG, GIF, max 5MB)

**Response:**
```json
{
  "code": 200,
  "msg": "Avatar uploaded successfully",
  "data": {
    "avatarUrl": "/uploads/images/filename.jpg"
  }
}
```

---

### Fitness Resource Endpoints

#### List Resources

```
GET /resources?page=1&size=20&type=all
```

**Query Parameters:**
- `page`: Page number (default: 1)
- `size`: Items per page (default: 20)
- `type`: Filter by type (all, video, article, document)

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Beginner Yoga",
        "description": "Introduction to yoga",
        "contentType": "video",
        "fileUrl": "/uploads/videos/yoga.mp4",
        "creator": "coach_name",
        "viewCount": 150,
        "createdAt": "2025-01-15T10:30:00"
      }
    ],
    "totalElements": 45,
    "totalPages": 3,
    "currentPage": 1
  }
}
```

---

#### Get Resource Details

```
GET /resources/{id}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "title": "Beginner Yoga",
    "description": "Introduction to yoga",
    "contentType": "video",
    "fileUrl": "/uploads/videos/yoga.mp4",
    "content": null,
    "creator": {
      "id": 2,
      "username": "coach_name",
      "avatar": "url"
    },
    "viewCount": 150,
    "createdAt": "2025-01-15T10:30:00"
  }
}
```

---

#### Create Resource (Admin/Coach)

```
POST /resources
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "contentType": "video|article|document",
  "fileUrl": "string (for video/document)",
  "content": "string (for article)"
}
```

**Response:**
```json
{
  "code": 200,
  "msg": "Resource created successfully",
  "data": {
    "id": 1,
    "title": "New Resource",
    "contentType": "video"
  }
}
```

---

#### Update Resource

```
PUT /resources/{id}
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "content": "string (for articles)"
}
```

---

#### Delete Resource (Admin)

```
DELETE /resources/{id}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

### Collection Endpoints

#### Get User Collections

```
GET /collections
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "resource": {
        "id": 1,
        "title": "Beginner Yoga",
        "contentType": "video"
      },
      "createdAt": "2025-01-15T10:30:00"
    }
  ]
}
```

---

#### Add to Collection

```
POST /collections
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "resourceId": 1
}
```

---

#### Remove from Collection

```
DELETE /collections/{resourceId}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

### Training Plan Endpoints

#### List Training Plans

```
GET /training-plans?page=1&size=20
```

**Query Parameters:**
- `page`: Page number
- `size`: Items per page

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "8-Week Strength",
        "description": "Build muscle strength",
        "coach": "coach_name",
        "student": "student_name",
        "startDate": "2025-01-15",
        "endDate": "2025-03-15",
        "status": "active",
        "exercises": "[...]"
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "currentPage": 1
  }
}
```

---

#### Get Training Plan Details

```
GET /training-plans/{id}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "8-Week Strength",
    "description": "Build muscle strength",
    "exercises": [
      {
        "name": "Bench Press",
        "sets": 4,
        "reps": 8,
        "duration": "60 seconds rest"
      }
    ],
    "coach": "coach_name",
    "student": "student_name",
    "startDate": "2025-01-15",
    "endDate": "2025-03-15",
    "status": "active"
  }
}
```

---

#### Create Training Plan (Coach)

```
POST /training-plans
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "studentId": 1,
  "name": "string",
  "description": "string",
  "exercises": [
    {
      "name": "string",
      "sets": 4,
      "reps": 8,
      "duration": "string"
    }
  ],
  "startDate": "2025-01-15",
  "endDate": "2025-03-15"
}
```

---

#### Update Training Plan

```
PUT /training-plans/{id}
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:** Same as create

---

#### Delete Training Plan

```
DELETE /training-plans/{id}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

### Community Endpoints

#### List Community Posts

```
GET /dynamics?page=1&size=20
```

**Query Parameters:**
- `page`: Page number
- `size`: Items per page

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "content": "Just completed my workout!",
        "imageUrls": ["url1", "url2"],
        "user": {
          "id": 1,
          "username": "john_doe",
          "avatar": "url"
        },
        "likeCount": 5,
        "commentCount": 2,
        "status": "approved",
        "createdAt": "2025-01-15T10:30:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 1
  }
}
```

---

#### Get Post Details

```
GET /dynamics/{id}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "content": "Just completed my workout!",
    "imageUrls": ["url1", "url2"],
    "user": {
      "id": 1,
      "username": "john_doe",
      "avatar": "url"
    },
    "likeCount": 5,
    "commentCount": 2,
    "status": "approved",
    "createdAt": "2025-01-15T10:30:00"
  }
}
```

---

#### Create Post

```
POST /dynamics
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "string",
  "imageUrls": ["url1", "url2"]
}
```

---

#### Update Post

```
PUT /dynamics/{id}
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "string",
  "imageUrls": ["url1", "url2"]
}
```

---

#### Delete Post

```
DELETE /dynamics/{id}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Like Post

```
POST /dynamics/{id}/like
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Unlike Post

```
DELETE /dynamics/{id}/like
```

**Headers:**
```
Authorization: Bearer {token}
```

---

### Comment Endpoints

#### Get Comments for Post

```
GET /dynamics/{id}/comments
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "content": "Great workout!",
      "user": {
        "id": 2,
        "username": "jane_doe",
        "avatar": "url"
      },
      "createdAt": "2025-01-15T10:30:00"
    }
  ]
}
```

---

#### Create Comment

```
POST /comments
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "dynamicId": 1,
  "content": "string"
}
```

---

#### Delete Comment

```
DELETE /comments/{id}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

### Check-In Endpoints

#### Perform Check-In

```
POST /checkins
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "msg": "Check-in successful",
  "data": {
    "id": 1,
    "checkDate": "2025-01-15",
    "checkTime": "2025-01-15T10:30:00"
  }
}
```

---

#### Get Check-In History

```
GET /checkins?page=1&size=20
```

**Query Parameters:**
- `page`: Page number
- `size`: Items per page

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "checkDate": "2025-01-15",
        "checkTime": "2025-01-15T10:30:00"
      }
    ],
    "totalElements": 45,
    "totalPages": 3,
    "currentPage": 1
  }
}
```

---

#### Get Check-In Statistics

```
GET /checkins/stats
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "totalCount": 45,
    "currentStreak": 7,
    "longestStreak": 15
  }
}
```

---

### Diet Record Endpoints

#### List Diet Records

```
GET /diet-records?page=1&size=20&startDate=2025-01-01&endDate=2025-01-31
```

**Query Parameters:**
- `page`: Page number
- `size`: Items per page
- `startDate`: Filter start date (YYYY-MM-DD)
- `endDate`: Filter end date (YYYY-MM-DD)

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "mealType": "breakfast",
        "foodItems": "Eggs, toast, orange juice",
        "calories": 450,
        "mealDate": "2025-01-15",
        "createdAt": "2025-01-15T08:00:00"
      }
    ],
    "totalElements": 30,
    "totalPages": 2,
    "currentPage": 1
  }
}
```

---

#### Create Diet Record

```
POST /diet-records
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "mealType": "breakfast|lunch|dinner|snack",
  "foodItems": "string",
  "calories": 450,
  "mealDate": "2025-01-15"
}
```

---

#### Update Diet Record

```
PUT /diet-records/{id}
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:** Same as create

---

#### Delete Diet Record

```
DELETE /diet-records/{id}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Get Daily Summary

```
GET /diet-records/summary?date=2025-01-15
```

**Query Parameters:**
- `date`: Date to summarize (YYYY-MM-DD)

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "date": "2025-01-15",
    "totalCalories": 2100,
    "mealBreakdown": {
      "breakfast": 450,
      "lunch": 650,
      "dinner": 900,
      "snack": 100
    }
  }
}
```

---

### Coach Endpoints

#### List All Coaches

```
GET /coaches?page=1&size=20
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "id": 2,
        "username": "coach_john",
        "avatar": "url",
        "intro": "Certified fitness coach",
        "studentCount": 5,
        "contentCount": 12
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "currentPage": 1
  }
}
```

---

#### Get Coach Profile

```
GET /coaches/{id}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 2,
    "username": "coach_john",
    "avatar": "url",
    "intro": "Certified fitness coach",
    "studentCount": 5,
    "contentCount": 12,
    "createdAt": "2025-01-01T00:00:00"
  }
}
```

---

#### Get Coach's Students

```
GET /coaches/students
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "username": "student_john",
      "avatar": "url",
      "activePlans": 2,
      "joinDate": "2025-01-15"
    }
  ]
}
```

---

#### Add Student

```
POST /coaches/students/{userId}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Remove Student

```
DELETE /coaches/students/{userId}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Get Student Check-Ins

```
GET /coaches/students/{studentId}/checkins?page=1&size=20
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Get Student Diet Records

```
GET /coaches/students/{studentId}/diet-records?startDate=2025-01-01&endDate=2025-01-31
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Get Student Analytics

```
GET /coaches/analytics/{studentId}
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "checkInFrequency": 0.85,
    "totalCheckIns": 25,
    "averageCalories": 2100,
    "activeDays": 28,
    "planCompletionRate": 0.9
  }
}
```

---

### Admin Endpoints

#### List All Users

```
GET /admin/users?page=1&size=20&role=all&username=
```

**Query Parameters:**
- `page`: Page number
- `size`: Items per page
- `role`: Filter by role (all, user, coach, admin)
- `username`: Search by username

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Get User Details

```
GET /admin/users/{id}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Update User

```
PUT /admin/users/{id}
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "gender": "string",
  "intro": "string",
  "role": "user|coach|admin"
}
```

---

#### Delete User

```
DELETE /admin/users/{id}
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Get System Statistics

```
GET /admin/statistics
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "totalUsers": 150,
    "totalCoaches": 10,
    "totalResources": 45,
    "totalPosts": 200,
    "activeUsers": 120
  }
}
```

---

#### Get Moderation Queue

```
GET /admin/moderation?page=1&size=20
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "type": "post",
        "content": "Post content",
        "author": "username",
        "status": "pending",
        "createdAt": "2025-01-15T10:30:00"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 1
  }
}
```

---

#### Approve Content

```
POST /admin/moderation/{id}/approve
```

**Headers:**
```
Authorization: Bearer {token}
```

---

#### Reject Content

```
POST /admin/moderation/{id}/reject
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "reason": "string"
}
```

---

### File Upload Endpoints

#### Upload Image

```
POST /upload/image
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Form Data:**
- `file`: Image file (JPG, PNG, GIF, max 5MB)

**Response:**
```json
{
  "code": 200,
  "msg": "Image uploaded successfully",
  "data": {
    "fileUrl": "/uploads/images/filename.jpg"
  }
}
```

---

#### Upload Video

```
POST /upload/video
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Form Data:**
- `file`: Video file (MP4, AVI, max 100MB)

**Response:**
```json
{
  "code": 200,
  "msg": "Video uploaded successfully",
  "data": {
    "fileUrl": "/uploads/videos/filename.mp4"
  }
}
```

---

### Search Endpoints

#### Search Content

```
GET /search?q=yoga&type=all
```

**Query Parameters:**
- `q`: Search query (required)
- `type`: Content type filter (all, resource, post, coach)

**Response:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "type": "resource",
      "id": 1,
      "title": "Yoga for Beginners",
      "description": "Introduction to yoga",
      "imageUrl": "url",
      "relevance": 0.95
    },
    {
      "type": "post",
      "id": 5,
      "title": "My Yoga Journey",
      "description": "Post content preview",
      "imageUrl": "url",
      "relevance": 0.85
    }
  ]
}
```

---

## Rate Limiting

The API implements rate limiting to prevent abuse:
- **Limit**: 100 requests per minute per user
- **Headers**: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`

---

## Pagination

List endpoints support pagination with the following parameters:
- `page`: Page number (1-indexed, default: 1)
- `size`: Items per page (default: 20, max: 100)

Response includes:
- `content`: Array of items
- `totalElements`: Total number of items
- `totalPages`: Total number of pages
- `currentPage`: Current page number

---

## Date Format

All dates use ISO 8601 format:
- Date: `YYYY-MM-DD`
- DateTime: `YYYY-MM-DDTHH:mm:ss`

---

## Versioning

Current API Version: 1.0.0

Future versions will be available at `/api/v2/`, etc.

---

## Support

For API issues or questions:
1. Check this documentation
2. Review error messages in responses
3. Check application logs
4. Contact technical support

