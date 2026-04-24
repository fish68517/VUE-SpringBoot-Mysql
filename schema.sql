SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS power_inspection
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE power_inspection;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    phone VARCHAR(30) NULL,
    email VARCHAR(100) NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_sys_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS device_category (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_device_category_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS device (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    device_code VARCHAR(50) NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    last_inspection_time DATETIME NULL,
    image_name VARCHAR(255) NULL,
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_device_device_code (device_code),
    KEY idx_device_category_id (category_id),
    CONSTRAINT fk_device_category FOREIGN KEY (category_id) REFERENCES device_category (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inspection_standard (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    standard_value VARCHAR(255) NULL,
    check_method VARCHAR(255) NULL,
    cycle_days INT NULL,
    abnormal_rule VARCHAR(255) NULL,
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_inspection_standard_category_id (category_id),
    CONSTRAINT fk_inspection_standard_category FOREIGN KEY (category_id) REFERENCES device_category (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inspection_task (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    task_no VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    device_id BIGINT NOT NULL,
    inspector_id BIGINT NOT NULL,
    planned_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_inspection_task_task_no (task_no),
    KEY idx_inspection_task_device_id (device_id),
    KEY idx_inspection_task_inspector_id (inspector_id),
    CONSTRAINT fk_inspection_task_device FOREIGN KEY (device_id) REFERENCES device (id),
    CONSTRAINT fk_inspection_task_inspector FOREIGN KEY (inspector_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inspection_task_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    standard_id BIGINT NULL,
    item_name VARCHAR(100) NOT NULL,
    standard_value VARCHAR(255) NULL,
    check_method VARCHAR(255) NULL,
    abnormal_rule VARCHAR(255) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    checked_value VARCHAR(255) NULL,
    item_result VARCHAR(20) NULL,
    item_remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_inspection_task_item_task_id (task_id),
    KEY idx_inspection_task_item_standard_id (standard_id),
    CONSTRAINT fk_inspection_task_item_task FOREIGN KEY (task_id) REFERENCES inspection_task (id),
    CONSTRAINT fk_inspection_task_item_standard FOREIGN KEY (standard_id) REFERENCES inspection_standard (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inspection_record (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    inspector_id BIGINT NOT NULL,
    result VARCHAR(20) NOT NULL,
    status_description VARCHAR(500) NULL,
    image_name VARCHAR(255) NULL,
    inspection_time DATETIME NOT NULL,
    need_repair TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_inspection_record_task_id (task_id),
    KEY idx_inspection_record_device_id (device_id),
    KEY idx_inspection_record_inspector_id (inspector_id),
    CONSTRAINT fk_inspection_record_task FOREIGN KEY (task_id) REFERENCES inspection_task (id),
    CONSTRAINT fk_inspection_record_device FOREIGN KEY (device_id) REFERENCES device (id),
    CONSTRAINT fk_inspection_record_inspector FOREIGN KEY (inspector_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS defect (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    level VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    description VARCHAR(500) NOT NULL,
    image_name VARCHAR(255) NULL,
    reported_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_defect_record_id (record_id),
    KEY idx_defect_device_id (device_id),
    KEY idx_defect_reporter_id (reporter_id),
    CONSTRAINT fk_defect_record FOREIGN KEY (record_id) REFERENCES inspection_record (id),
    CONSTRAINT fk_defect_device FOREIGN KEY (device_id) REFERENCES device (id),
    CONSTRAINT fk_defect_reporter FOREIGN KEY (reporter_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS repair_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL,
    defect_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    maintainer_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    measures VARCHAR(500) NULL,
    result VARCHAR(500) NULL,
    image_name VARCHAR(255) NULL,
    finished_at DATETIME NULL,
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_repair_order_order_no (order_no),
    UNIQUE KEY uk_repair_order_defect_id (defect_id),
    KEY idx_repair_order_device_id (device_id),
    KEY idx_repair_order_maintainer_id (maintainer_id),
    CONSTRAINT fk_repair_order_defect FOREIGN KEY (defect_id) REFERENCES defect (id),
    CONSTRAINT fk_repair_order_device FOREIGN KEY (device_id) REFERENCES device (id),
    CONSTRAINT fk_repair_order_maintainer FOREIGN KEY (maintainer_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notice (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    pinned TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    publish_time DATETIME NOT NULL,
    publisher_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_notice_publisher_id (publisher_id),
    CONSTRAINT fk_notice_publisher FOREIGN KEY (publisher_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    module VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    username VARCHAR(50) NULL,
    real_name VARCHAR(50) NULL,
    role VARCHAR(20) NULL,
    request_method VARCHAR(10) NULL,
    request_path VARCHAR(255) NULL,
    ip VARCHAR(50) NULL,
    success TINYINT(1) NOT NULL DEFAULT 1,
    message VARCHAR(255) NULL,
    details VARCHAR(2000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
