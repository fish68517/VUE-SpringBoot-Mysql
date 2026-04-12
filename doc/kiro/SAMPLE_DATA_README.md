# Sample Data Guide

## Overview

The `sample-data.sql` script provides comprehensive sample data for testing and demonstration of the Travel Memory System. It includes realistic data for users, travel records, plans, multimedia files, and social interactions.

## Sample Data Contents

### Users (5 users)
- **alice_traveler**: Adventure seeker and photography enthusiast
- **bob_explorer**: Love exploring hidden gems around the world
- **carol_planner**: Travel planner and budget conscious explorer
- **david_photographer**: Professional travel photographer
- **emma_blogger**: Travel blogger sharing stories and tips

**Note**: All sample users have the same password hash (bcrypt): `password123`

### Travel Records (8 records)
- Tokyo Adventure 2024 (Public)
- Kyoto Cultural Journey (Public)
- Bangkok Street Food Tour (Public)
- Phuket Beach Relaxation (Private)
- Paris City Break (Public)
- Swiss Alps Hiking (Public)
- New York Photography Tour (Public)
- Barcelona Gastronomic Experience (Public)

### Travel Plans (5 plans)
- Southeast Asia Tour 2024
- European Grand Tour
- New Zealand Adventure
- Iceland Photography Expedition
- Japan Spring Festival Tour

### Itinerary Items (10 items)
Sample itinerary items for each travel plan including:
- Flights
- Accommodations
- Attractions
- Transportation
- Activities

### Map Footprints (16 footprints)
Geographic locations with coordinates for:
- Tokyo landmarks (Senso-ji Temple, Shibuya Crossing, Tokyo Tower, Meiji Shrine)
- Kyoto temples (Fushimi Inari, Arashiyama Bamboo Grove, Kinkaku-ji)
- Bangkok attractions (Chatuchak Market, Wat Pho, Lumphini Park)
- Paris landmarks (Eiffel Tower, Louvre, Arc de Triomphe)
- New York attractions (Statue of Liberty, Central Park, Times Square)

### Comments (10 comments)
Sample comments on public travel records from various users

### Likes (19 likes)
Sample likes distributed across public travel records

### Multimedia Files (12 files)
Sample multimedia file records including:
- JPEG images (photos)
- MP4 videos

## Installation Instructions

### Prerequisites
- MySQL 8.0 or higher
- Database initialized with `database-init.sql`

### Step 1: Ensure Database Exists
```bash
mysql -u root -p < database-init.sql
```

### Step 2: Load Sample Data
```bash
mysql -u root -p travel_memory_system < sample-data.sql
```

Or if using a different user:
```bash
mysql -u travel_user -p travel_memory_system < sample-data.sql
```

### Step 3: Verify Data
```bash
mysql -u root -p travel_memory_system
```

Then run verification queries:
```sql
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as travel_record_count FROM travel_records;
SELECT COUNT(*) as plan_count FROM travel_plans;
SELECT COUNT(*) as comment_count FROM comments;
SELECT COUNT(*) as like_count FROM likes;
```

Expected output:
```
user_count: 5
travel_record_count: 8
plan_count: 5
comment_count: 10
like_count: 19
```

## Testing with Sample Data

### 1. Authentication Testing

#### Login with Sample User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

### 2. Travel Records Testing

#### Get User's Travel Records
```bash
curl -X GET "http://localhost:8080/api/travels?page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

#### Get Public Travel Records (Social Feed)
```bash
curl -X GET "http://localhost:8080/api/travels/public?page=0&size=10"
```

#### Get Specific Travel Record
```bash
curl -X GET http://localhost:8080/api/travels/1 \
  -H "Authorization: Bearer <token>"
```

### 3. Travel Plans Testing

#### Get User's Travel Plans
```bash
curl -X GET "http://localhost:8080/api/plans?page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

#### Get Plan with Itinerary Items
```bash
curl -X GET http://localhost:8080/api/plans/1 \
  -H "Authorization: Bearer <token>"
```

### 4. Map Footprints Testing

#### Get Footprints for a Travel Record
```bash
curl -X GET http://localhost:8080/api/travels/1/footprints \
  -H "Authorization: Bearer <token>"
```

### 5. Comments and Likes Testing

#### Get Comments for a Record
```bash
curl -X GET http://localhost:8080/api/travels/1/comments
```

#### Add a Comment
```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "travelRecordId": 1,
    "content": "Great travel record!"
  }'
```

#### Add a Like
```bash
curl -X POST http://localhost:8080/api/likes \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "travelRecordId": 1
  }'
```

## Sample Data Statistics

### Data Distribution
- **Total Users**: 5
- **Total Travel Records**: 8 (6 public, 2 private)
- **Total Travel Plans**: 5
- **Total Itinerary Items**: 10
- **Total Map Footprints**: 16
- **Total Comments**: 10
- **Total Likes**: 19
- **Total Multimedia Files**: 12

### Geographic Coverage
- **Japan**: Tokyo, Kyoto
- **Thailand**: Bangkok, Phuket
- **France**: Paris
- **Switzerland**: Interlaken
- **USA**: New York
- **Spain**: Barcelona

### Date Range
- **Travel Records**: March 2024 - September 2024
- **Travel Plans**: October 2024 - April 2025

## Customizing Sample Data

### Adding More Users
```sql
INSERT INTO users (username, email, password_hash, avatar_url, bio, created_at) VALUES
('new_user', 'new@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://...', 'Bio', NOW());
```

### Adding More Travel Records
```sql
INSERT INTO travel_records (user_id, title, destination, start_date, end_date, description, diary_content, is_public, view_count, created_at) VALUES
(1, 'New Trip', 'Destination', '2024-01-01', '2024-01-10', 'Description', '<p>Content</p>', TRUE, 0, NOW());
```

### Adding More Comments
```sql
INSERT INTO comments (travel_record_id, user_id, content, created_at) VALUES
(1, 2, 'Comment text', NOW());
```

## Resetting Sample Data

### Option 1: Delete and Reload
```bash
# Delete all data
mysql -u root -p travel_memory_system -e "
  DELETE FROM likes;
  DELETE FROM comments;
  DELETE FROM multimedia_files;
  DELETE FROM map_footprints;
  DELETE FROM itinerary_items;
  DELETE FROM travel_plans;
  DELETE FROM travel_records;
  DELETE FROM users;
"

# Reload sample data
mysql -u root -p travel_memory_system < sample-data.sql
```

### Option 2: Recreate Database
```bash
# Drop and recreate database
mysql -u root -p -e "DROP DATABASE travel_memory_system;"
mysql -u root -p < database-init.sql
mysql -u root -p travel_memory_system < sample-data.sql
```

## Performance Testing with Sample Data

### Query Performance
```sql
-- Check query execution time
SET PROFILING = 1;

-- Get all public travel records
SELECT * FROM travel_records WHERE is_public = TRUE ORDER BY created_at DESC;

-- Show profile
SHOW PROFILES;
```

### Index Verification
```sql
-- Check if indexes are being used
EXPLAIN SELECT * FROM travel_records WHERE user_id = 1 ORDER BY created_at DESC;
EXPLAIN SELECT * FROM comments WHERE travel_record_id = 1;
EXPLAIN SELECT * FROM likes WHERE travel_record_id = 1;
```

## Troubleshooting

### Issue: Foreign Key Constraint Error
**Solution**: Ensure `database-init.sql` is run first to create all tables with proper constraints.

### Issue: Duplicate Entry Error
**Solution**: The sample data script uses `INSERT INTO` without `IGNORE`. If running multiple times, delete data first or use `INSERT IGNORE`.

### Issue: Password Hash Not Working
**Solution**: The sample password hash is for `password123`. If you need a different password, generate a new bcrypt hash using:
```bash
# Using online tool or command line
echo -n "your_password" | htpasswd -bnBC 10 "" | tr -d ':\n' | sed 's/$2y/$2a/'
```

## Notes

- All sample users use the same password hash for simplicity
- Sample data includes realistic but fictional information
- Multimedia file paths are references only; actual files are not included
- Sample data is suitable for development and testing only
- For production, use real data and implement proper data migration strategies

## Next Steps

After loading sample data:
1. Start the backend application
2. Access Swagger UI at `http://localhost:8080/api/swagger-ui.html`
3. Test API endpoints with sample data
4. Verify frontend integration with backend
5. Perform load testing with sample data

