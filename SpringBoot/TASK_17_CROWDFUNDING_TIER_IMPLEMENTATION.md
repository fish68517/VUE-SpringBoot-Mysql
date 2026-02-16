# Task 17: Crowdfunding Tier Management Implementation

## Overview
This document summarizes the implementation of Task 17 - Crowdfunding Tier Management for the Campus Activity Crowdfunding Platform.

## Implementation Details

### Backend Implementation

#### 1. Database Schema
- Created `crowdfunding_tiers` table with the following columns:
  - `id`: Primary key (BIGINT AUTO_INCREMENT)
  - `activity_id`: Foreign key to activities table
  - `name`: Tier name (VARCHAR 100)
  - `amount`: Tier amount (DECIMAL 10,2)
  - `description`: Tier description (LONGTEXT)
  - `is_preset`: Boolean flag for preset vs custom tiers
  - `display_order`: Display order for sorting
  - `created_at`, `updated_at`: Timestamps
  - Index on `activity_id` for efficient queries

#### 2. Entity Classes
- **CrowdfundingTier.java**: JPA entity representing a crowdfunding tier
  - Extends BaseEntity for common fields (id, createdAt, updatedAt)
  - Many-to-one relationship with Activity
  - Supports both preset and custom tiers
  - Display order for tier ordering

- **Updated CrowdfundingSupport.java**: Added relationship to CrowdfundingTier
  - Added `tier` field with ManyToOne relationship
  - Maintains `tierId` foreign key

- **Updated Activity.java**: Added relationship to CrowdfundingTier
  - Added `crowdfundingTiers` field with OneToMany relationship

#### 3. Data Transfer Objects
- **CrowdfundingTierDTO.java**: DTO for tier data transfer
  - Includes conversion methods: `fromEntity()` and `toEntity()`
  - Supports JSON serialization with `@JsonInclude`

#### 4. Repository
- **CrowdfundingTierRepository.java**: Data access layer
  - `findByActivityIdOrderByDisplayOrder()`: Get all tiers for an activity
  - `findPresetTiersByActivityId()`: Get preset tiers only
  - `findCustomTiersByActivityId()`: Get custom tiers only

#### 5. Service Layer
- **CrowdfundingTierService.java**: Business logic layer
  - `getTiersByActivityId()`: Retrieve all tiers for an activity
  - `getPresetTiersByActivityId()`: Retrieve preset tiers
  - `getCustomTiersByActivityId()`: Retrieve custom tiers
  - `createTier()`: Create a new tier
  - `updateTier()`: Update an existing tier
  - `deleteTier()`: Delete a tier
  - `initializeDefaultTiers()`: Initialize default preset tiers (10元, 50元, 100元)

#### 6. Controller
- **CrowdfundingTierController.java**: REST API endpoints
  - `GET /api/crowdfunding/tiers/{activityId}`: Get all tiers for an activity
  - `GET /api/crowdfunding/tiers/{activityId}/preset`: Get preset tiers
  - `GET /api/crowdfunding/tiers/{activityId}/custom`: Get custom tiers
  - All endpoints return standardized ApiResponse format

#### 7. Tests
- **CrowdfundingTierServiceTest.java**: Service layer tests
  - Tests for retrieving tiers (all, preset, custom)
  - Tests for creating, updating, and deleting tiers
  - Tests for initializing default tiers
  - Tests for error handling (tier not found)
  - All tests passed ✓

- **CrowdfundingTierControllerTest.java**: Controller layer tests
  - Tests for all API endpoints
  - Tests for response format and status codes
  - Tests for empty results
  - All tests passed ✓

### Frontend Implementation

#### 1. ActivityDetail.vue Updates
- Added `crowdfundingTiers` ref to store tier data
- Added `fetchCrowdfundingTiers()` function to fetch tiers from API
- Updated `fetchActivityDetail()` to fetch tiers when crowdfunding is enabled
- Added new "众筹档位" (Crowdfunding Tiers) card in the sidebar
  - Displays tier name and amount
  - Shows tier description if available
  - Styled with blue left border for visual hierarchy

#### 2. Styling
- Added CSS classes for tier display:
  - `.tier-item`: Container for each tier
  - `.tier-header`: Header with name and amount
  - `.tier-name`: Tier name styling
  - `.tier-amount`: Tier amount styling (bold, primary color)
  - `.tier-description`: Tier description styling

## API Endpoints

### Get Crowdfunding Tiers
```
GET /api/crowdfunding/tiers/{activityId}
Response: List<CrowdfundingTierDTO>
```

### Get Preset Tiers
```
GET /api/crowdfunding/tiers/{activityId}/preset
Response: List<CrowdfundingTierDTO>
```

### Get Custom Tiers
```
GET /api/crowdfunding/tiers/{activityId}/custom
Response: List<CrowdfundingTierDTO>
```

## Requirements Coverage

This implementation covers Requirement 4.1:
- ✓ Created crowdfunding_tiers table to record preset and custom tiers
- ✓ Implemented GET /api/crowdfunding/tiers/{activityId} endpoint
- ✓ Frontend displays crowdfunding tiers in activity detail page

## Testing Results

All tests passed successfully:
- CrowdfundingTierServiceTest: 8 tests passed
- CrowdfundingTierControllerTest: 4 tests passed

## Default Tiers

When crowdfunding is enabled for an activity, the system can initialize three default preset tiers:
- 10元档 (10 yuan tier)
- 50元档 (50 yuan tier)
- 100元档 (100 yuan tier)

These can be customized or additional custom tiers can be added as needed.

## Next Steps

The next task (Task 18) will implement the crowdfunding support functionality, which will allow users to select a tier and complete the payment process.
