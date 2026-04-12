-- Travel Memory System - Sample Data Script
-- This script populates the database with sample data for testing and demonstration
-- Run this script after database-init.sql

USE travel_memory_system;

-- Insert Sample Users
INSERT INTO users (username, email, password_hash, avatar_url, bio, created_at) VALUES
('alice_traveler', 'alice@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.example.com/avatars/alice.jpg', 'Adventure seeker and photography enthusiast', NOW()),
('bob_explorer', 'bob@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.example.com/avatars/bob.jpg', 'Love exploring hidden gems around the world', NOW()),
('carol_planner', 'carol@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.example.com/avatars/carol.jpg', 'Travel planner and budget conscious explorer', NOW()),
('david_photographer', 'david@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.example.com/avatars/david.jpg', 'Professional travel photographer', NOW()),
('emma_blogger', 'emma@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.example.com/avatars/emma.jpg', 'Travel blogger sharing stories and tips', NOW());

-- Insert Sample Travel Records
INSERT INTO travel_records (user_id, title, destination, start_date, end_date, description, diary_content, is_public, view_count, created_at) VALUES
(1, 'Tokyo Adventure 2024', 'Tokyo, Japan', '2024-03-15', '2024-03-25', 'Amazing trip to Tokyo with cherry blossoms', '<p>Day 1: Arrived at Narita Airport. The city is bustling with energy!</p><p>Day 2: Visited Senso-ji Temple and Shibuya Crossing. Incredible experience!</p>', TRUE, 45, NOW()),
(1, 'Kyoto Cultural Journey', 'Kyoto, Japan', '2024-04-01', '2024-04-08', 'Exploring temples and traditional culture', '<p>Visited 15 temples in Kyoto. Each one has unique architecture and history.</p>', TRUE, 32, NOW()),
(2, 'Bangkok Street Food Tour', 'Bangkok, Thailand', '2024-02-10', '2024-02-18', 'Street food exploration and night markets', '<p>Tried 50+ different street food items. Best experience ever!</p>', TRUE, 28, NOW()),
(2, 'Phuket Beach Relaxation', 'Phuket, Thailand', '2024-05-01', '2024-05-07', 'Beach vacation and water sports', '<p>Relaxing on pristine beaches and trying scuba diving.</p>', FALSE, 0, NOW()),
(3, 'Paris City Break', 'Paris, France', '2024-06-15', '2024-06-22', 'Classic European city exploration', '<p>Eiffel Tower, Louvre Museum, and charming cafes.</p>', TRUE, 56, NOW()),
(3, 'Swiss Alps Hiking', 'Interlaken, Switzerland', '2024-07-01', '2024-07-10', 'Mountain hiking and alpine scenery', '<p>Hiked Jungfrau region. Breathtaking views!</p>', TRUE, 41, NOW()),
(4, 'New York Photography Tour', 'New York, USA', '2024-08-05', '2024-08-12', 'Capturing the city that never sleeps', '<p>Photographed Times Square, Central Park, and Brooklyn Bridge.</p>', TRUE, 67, NOW()),
(5, 'Barcelona Gastronomic Experience', 'Barcelona, Spain', '2024-09-10', '2024-09-17', 'Food and culture in Catalonia', '<p>Tapas tasting, La Sagrada Familia, and Park Güell.</p>', TRUE, 38, NOW());

-- Insert Sample Travel Plans
INSERT INTO travel_plans (user_id, title, destination, start_date, end_date, budget, description, created_at) VALUES
(1, 'Southeast Asia Tour 2024', 'Thailand, Vietnam, Cambodia', '2024-10-01', '2024-10-21', 3500.00, 'Three-week adventure through Southeast Asia', NOW()),
(2, 'European Grand Tour', 'France, Italy, Spain', '2024-11-01', '2024-11-30', 5000.00, 'Month-long European exploration', NOW()),
(3, 'New Zealand Adventure', 'New Zealand', '2024-12-15', '2025-01-15', 4200.00, 'Adventure sports and nature in New Zealand', NOW()),
(4, 'Iceland Photography Expedition', 'Iceland', '2025-02-01', '2025-02-14', 2800.00, 'Capturing Iceland\'s natural wonders', NOW()),
(5, 'Japan Spring Festival Tour', 'Japan', '2025-03-20', '2025-04-10', 3800.00, 'Cherry blossoms and spring festivals', NOW());

-- Insert Sample Itinerary Items
INSERT INTO itinerary_items (plan_id, item_date, item_type, title, description, location, created_at) VALUES
(1, '2024-10-01', 'flight', 'Flight to Bangkok', 'Depart from home airport to Bangkok', 'Bangkok, Thailand', NOW()),
(1, '2024-10-02', 'accommodation', 'Bangkok Hotel Check-in', 'Stay at Sukhumvit area hotel', 'Bangkok, Thailand', NOW()),
(1, '2024-10-03', 'attraction', 'Grand Palace Tour', 'Visit the famous Grand Palace', 'Bangkok, Thailand', NOW()),
(1, '2024-10-05', 'transportation', 'Travel to Phuket', 'Flight from Bangkok to Phuket', 'Phuket, Thailand', NOW()),
(1, '2024-10-06', 'activity', 'Island Hopping', 'Boat tour to nearby islands', 'Phuket, Thailand', NOW()),
(2, '2024-11-01', 'flight', 'Flight to Paris', 'International flight to Paris', 'Paris, France', NOW()),
(2, '2024-11-02', 'accommodation', 'Paris Apartment', 'Rent apartment in Marais district', 'Paris, France', NOW()),
(2, '2024-11-03', 'attraction', 'Eiffel Tower', 'Visit Eiffel Tower and climb to top', 'Paris, France', NOW()),
(2, '2024-11-05', 'transportation', 'Train to Rome', 'High-speed train from Paris to Rome', 'Rome, Italy', NOW()),
(2, '2024-11-06', 'attraction', 'Colosseum Tour', 'Guided tour of the Colosseum', 'Rome, Italy', NOW());

-- Insert Sample Map Footprints
INSERT INTO map_footprints (travel_record_id, location_name, latitude, longitude, visit_date, created_at) VALUES
(1, 'Senso-ji Temple', 35.71475, 139.79695, '2024-03-16', NOW()),
(1, 'Shibuya Crossing', 35.65953, 139.70050, '2024-03-16', NOW()),
(1, 'Tokyo Tower', 35.65858, 139.74542, '2024-03-17', NOW()),
(1, 'Meiji Shrine', 35.67616, 139.70029, '2024-03-18', NOW()),
(2, 'Fushimi Inari Shrine', 34.86722, 135.77306, '2024-04-02', NOW()),
(2, 'Arashiyama Bamboo Grove', 35.01639, 135.75389, '2024-04-03', NOW()),
(2, 'Kinkaku-ji Temple', 35.03944, 135.72889, '2024-04-04', NOW()),
(3, 'Chatuchak Weekend Market', 13.80194, 100.55278, '2024-02-11', NOW()),
(3, 'Wat Pho Temple', 13.64667, 100.49167, '2024-02-12', NOW()),
(3, 'Lumphini Park', 13.73167, 100.54639, '2024-02-13', NOW()),
(5, 'Eiffel Tower', 48.85837, 2.29447, '2024-06-16', NOW()),
(5, 'Louvre Museum', 48.86162, 2.33602, '2024-06-17', NOW()),
(5, 'Arc de Triomphe', 48.87361, 2.29556, '2024-06-18', NOW()),
(7, 'Statue of Liberty', 40.68925, -74.04515, '2024-08-06', NOW()),
(7, 'Central Park', 40.78500, -73.96833, '2024-08-07', NOW()),
(7, 'Times Square', 40.75812, -73.98555, '2024-08-08', NOW());

-- Insert Sample Comments
INSERT INTO comments (travel_record_id, user_id, content, created_at) VALUES
(1, 2, 'Amazing photos! Tokyo looks incredible!', NOW()),
(1, 3, 'I\'m planning a trip to Tokyo next year. Any recommendations?', NOW()),
(1, 4, 'Great documentation of your journey!', NOW()),
(3, 1, 'The street food looks delicious! Must try!', NOW()),
(3, 4, 'I love Bangkok! Did you try the night markets?', NOW()),
(5, 2, 'Paris is always magical. Beautiful shots!', NOW()),
(5, 4, 'The Eiffel Tower at sunset must be stunning!', NOW()),
(7, 1, 'Professional photography! Love the composition!', NOW()),
(7, 3, 'New York is on my bucket list!', NOW()),
(8, 1, 'Barcelona food scene is incredible!', NOW());

-- Insert Sample Likes
INSERT INTO likes (travel_record_id, user_id, created_at) VALUES
(1, 2, NOW()),
(1, 3, NOW()),
(1, 4, NOW()),
(1, 5, NOW()),
(2, 3, NOW()),
(2, 4, NOW()),
(3, 1, NOW()),
(3, 4, NOW()),
(3, 5, NOW()),
(5, 2, NOW()),
(5, 3, NOW()),
(5, 4, NOW()),
(7, 1, NOW()),
(7, 2, NOW()),
(7, 3, NOW()),
(7, 5, NOW()),
(8, 1, NOW()),
(8, 2, NOW()),
(8, 4, NOW());

-- Insert Sample Multimedia Files
INSERT INTO multimedia_files (travel_record_id, file_name, file_path, file_type, file_size, upload_date) VALUES
(1, 'tokyo_senso_temple.jpg', '/uploads/travel_1/tokyo_senso_temple.jpg', 'image/jpeg', 2048576, NOW()),
(1, 'tokyo_shibuya_crossing.jpg', '/uploads/travel_1/tokyo_shibuya_crossing.jpg', 'image/jpeg', 1876543, NOW()),
(1, 'tokyo_tower_night.jpg', '/uploads/travel_1/tokyo_tower_night.jpg', 'image/jpeg', 2156789, NOW()),
(2, 'kyoto_bamboo_grove.jpg', '/uploads/travel_2/kyoto_bamboo_grove.jpg', 'image/jpeg', 1945678, NOW()),
(2, 'kyoto_temple.jpg', '/uploads/travel_2/kyoto_temple.jpg', 'image/jpeg', 2234567, NOW()),
(3, 'bangkok_street_food.jpg', '/uploads/travel_3/bangkok_street_food.jpg', 'image/jpeg', 1654321, NOW()),
(3, 'bangkok_market_video.mp4', '/uploads/travel_3/bangkok_market_video.mp4', 'video/mp4', 52428800, NOW()),
(5, 'paris_eiffel_tower.jpg', '/uploads/travel_5/paris_eiffel_tower.jpg', 'image/jpeg', 2345678, NOW()),
(5, 'paris_louvre.jpg', '/uploads/travel_5/paris_louvre.jpg', 'image/jpeg', 2123456, NOW()),
(7, 'nyc_times_square.jpg', '/uploads/travel_7/nyc_times_square.jpg', 'image/jpeg', 2567890, NOW()),
(7, 'nyc_central_park.jpg', '/uploads/travel_7/nyc_central_park.jpg', 'image/jpeg', 2345678, NOW()),
(8, 'barcelona_sagrada_familia.jpg', '/uploads/travel_8/barcelona_sagrada_familia.jpg', 'image/jpeg', 2456789, NOW());

-- Print completion message
SELECT 'Sample data inserted successfully!' AS status;
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as travel_record_count FROM travel_records;
SELECT COUNT(*) as plan_count FROM travel_plans;
SELECT COUNT(*) as comment_count FROM comments;
SELECT COUNT(*) as like_count FROM likes;
