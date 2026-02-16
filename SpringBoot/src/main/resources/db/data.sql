-- Sample Data for Campus Activity Crowdfunding Platform
-- This file contains sample data for testing and development

-- Insert sample users
INSERT INTO users (username, password, email, phone, nickname, role, status) VALUES
('student001', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy990qm', 'student001@example.com', '13800000001', 'Alice', 'USER', 'ACTIVE'),
('student002', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy990qm', 'student002@example.com', '13800000002', 'Bob', 'USER', 'ACTIVE'),
('organizer001', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy990qm', 'organizer001@example.com', '13800000003', 'Charlie', 'ORGANIZER', 'ACTIVE'),
('admin001', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy990qm', 'admin001@example.com', '13800000004', 'Admin', 'ADMIN', 'ACTIVE');

-- Insert sample activities
INSERT INTO activities (organizer_id, title, description, type, start_time, end_time, location, max_participants, registration_deadline, enable_crowdfunding, crowdfunding_target, status) VALUES
(3, 'Spring Boot Workshop', 'Learn Spring Boot development from scratch', 'LECTURE', '2024-03-15 14:00:00', '2024-03-15 16:00:00', 'Room 101', 50, '2024-03-14 23:59:59', TRUE, 1000.00, 'APPROVED'),
(3, 'Programming Competition', 'Annual programming competition for all students', 'COMPETITION', '2024-04-20 09:00:00', '2024-04-20 17:00:00', 'Main Hall', 100, '2024-04-19 23:59:59', TRUE, 5000.00, 'APPROVED'),
(3, 'Charity Fundraiser', 'Fundraiser for local community', 'CHARITY', '2024-05-10 10:00:00', '2024-05-10 18:00:00', 'Campus Square', 200, '2024-05-09 23:59:59', TRUE, 10000.00, 'PENDING_AUDIT');

-- Insert sample registrations
INSERT INTO registrations (activity_id, user_id, status) VALUES
(1, 1, 'REGISTERED'),
(1, 2, 'REGISTERED'),
(2, 1, 'REGISTERED'),
(3, 2, 'REGISTERED');

-- Insert sample crowdfunding supports
INSERT INTO crowdfunding_supports (activity_id, user_id, amount, payment_status) VALUES
(1, 1, 50.00, 'COMPLETED'),
(1, 2, 100.00, 'COMPLETED'),
(2, 1, 200.00, 'COMPLETED'),
(3, 2, 150.00, 'PENDING');

-- Insert sample feedback
INSERT INTO feedback (activity_id, user_id, rating, content) VALUES
(1, 1, 5, 'Great workshop! Very informative and well-organized.'),
(1, 2, 4, 'Good content, but could use more hands-on exercises.'),
(2, 1, 5, 'Excellent competition! Looking forward to next year.');
