-- Create database if not exists
CREATE DATABASE IF NOT EXISTS submission_system;
USE submission_system;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL COMMENT 'AUTHOR, EDITOR, REVIEWER, ADMIN',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED, ACTIVE, INACTIVE',
    academic_achievements TEXT COMMENT 'For authors',
    work_email VARCHAR(100) COMMENT 'For editors',
    expertise_areas VARCHAR(255) COMMENT 'For reviewers',
    research_directions VARCHAR(255) COMMENT 'For reviewers',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    requirements TEXT,
    file_format VARCHAR(255),
    max_file_size BIGINT,
    word_count_min INT,
    word_count_max INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Manuscripts table
CREATE TABLE IF NOT EXISTS manuscripts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    author_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    abstract TEXT,
    content LONGTEXT,
    file_path VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT, SUBMITTED, UNDER_REVIEW, REVISION_REQUIRED, ACCEPTED, REJECTED',
    submission_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_author_id (author_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_submission_date (submission_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Initial Reviews table
CREATE TABLE IF NOT EXISTS initial_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    manuscript_id BIGINT NOT NULL,
    editor_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL COMMENT 'PASS, REJECT, REVISION_REQUIRED',
    opinion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manuscript_id) REFERENCES manuscripts(id),
    FOREIGN KEY (editor_id) REFERENCES users(id),
    INDEX idx_manuscript_id (manuscript_id),
    INDEX idx_editor_id (editor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    manuscript_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    editor_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, ACCEPTED, REJECTED, SUBMITTED',
    opinion TEXT,
    score INT,
    recommendation VARCHAR(50) COMMENT 'ACCEPT, ACCEPT_WITH_REVISION, REJECT',
    submitted_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manuscript_id) REFERENCES manuscripts(id),
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
    FOREIGN KEY (editor_id) REFERENCES users(id),
    INDEX idx_manuscript_id (manuscript_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_editor_id (editor_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    manuscript_id BIGINT,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'NOTIFICATION, COMMUNICATION',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id),
    FOREIGN KEY (manuscript_id) REFERENCES manuscripts(id),
    INDEX idx_recipient_id (recipient_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- System Config table
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Notification Templates table
CREATE TABLE IF NOT EXISTS notification_templates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_name VARCHAR(100) NOT NULL,
    template_content TEXT NOT NULL,
    template_type VARCHAR(50) COMMENT 'SUBMISSION, REVIEW, ACCEPTANCE, REJECTION',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_template_name (template_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
