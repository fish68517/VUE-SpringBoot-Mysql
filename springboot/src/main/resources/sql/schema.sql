CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL COMMENT '学习项目，按需求明文保存',
    nickname VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    user_type TINYINT NOT NULL DEFAULT 0 COMMENT '0普通用户，1管理员，仅用于前端入口区分',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用，1正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS region (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NOT NULL DEFAULT 0,
    region_code VARCHAR(30) NOT NULL UNIQUE,
    region_name VARCHAR(100) NOT NULL,
    region_level TINYINT NOT NULL,
    sort_no INT DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_region_parent (parent_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS charging_station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    region_id BIGINT NOT NULL,
    station_code VARCHAR(40) NOT NULL UNIQUE,
    station_name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NOT NULL,
    longitude DECIMAL(10,6) NOT NULL,
    latitude DECIMAL(10,6) NOT NULL,
    station_type TINYINT NOT NULL COMMENT '0公共，1专用，2高速',
    open_time VARCHAR(50),
    operator_name VARCHAR(100),
    parking_desc VARCHAR(255),
    fee_desc VARCHAR(255),
    service_facilities VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0停用，1运营，2维护',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_station_region FOREIGN KEY (region_id) REFERENCES region(id),
    INDEX idx_station_region_status (region_id, status),
    INDEX idx_station_name (station_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS charging_pile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_id BIGINT NOT NULL,
    pile_code VARCHAR(50) NOT NULL UNIQUE,
    pile_name VARCHAR(100),
    connector_type TINYINT NOT NULL COMMENT '0直流快充，1交流慢充',
    rated_power DECIMAL(8,2) NOT NULL,
    manufacturer VARCHAR(100),
    install_date DATE,
    enable_status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_pile_station FOREIGN KEY (station_id) REFERENCES charging_station(id),
    INDEX idx_pile_station_enable (station_id, enable_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pile_realtime_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pile_id BIGINT NOT NULL,
    work_status TINYINT NOT NULL COMMENT '0空闲，1使用中，2预约，3离线，4故障',
    current_power DECIMAL(8,2),
    alarm_code VARCHAR(50),
    status_time DATETIME NOT NULL,
    source_type TINYINT NOT NULL DEFAULT 0 COMMENT '0模拟，1导入，2接口',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_pile FOREIGN KEY (pile_id) REFERENCES charging_pile(id),
    INDEX idx_status_pile_time (pile_id, status_time),
    INDEX idx_status_work (work_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS charging_usage_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pile_id BIGINT NOT NULL,
    user_id BIGINT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    duration_min INT,
    energy_kwh DECIMAL(10,2),
    service_fee DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    record_status TINYINT NOT NULL DEFAULT 1 COMMENT '0进行中，1完成，2异常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usage_pile FOREIGN KEY (pile_id) REFERENCES charging_pile(id),
    CONSTRAINT fk_usage_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    INDEX idx_usage_pile_start (pile_id, start_time),
    INDEX idx_usage_start (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS region_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    region_id BIGINT NOT NULL,
    stat_date DATE NOT NULL,
    station_count INT NOT NULL DEFAULT 0,
    pile_count INT NOT NULL DEFAULT 0,
    free_count INT NOT NULL DEFAULT 0,
    using_count INT NOT NULL DEFAULT 0,
    fault_count INT NOT NULL DEFAULT 0,
    usage_count INT NOT NULL DEFAULT 0,
    energy_kwh DECIMAL(12,2) NOT NULL DEFAULT 0,
    usage_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_statistics_region FOREIGN KEY (region_id) REFERENCES region(id),
    UNIQUE KEY uk_region_stat_date (region_id, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_favorite_station FOREIGN KEY (station_id) REFERENCES charging_station(id),
    UNIQUE KEY uk_user_station (user_id, station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    feedback_type TINYINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    contact VARCHAR(100),
    process_status TINYINT NOT NULL DEFAULT 0 COMMENT '0待处理，1处理中，2已完成',
    reply_content TEXT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    process_time DATETIME,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    INDEX idx_feedback_status (process_status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_point (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_id BIGINT NOT NULL,
    point_name VARCHAR(100) NOT NULL,
    point_type TINYINT NOT NULL COMMENT '0入口，1停车区，2充电区，3服务设施',
    x_ratio DECIMAL(6,4),
    y_ratio DECIMAL(6,4),
    description VARCHAR(255),
    sort_no INT DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_guide_station FOREIGN KEY (station_id) REFERENCES charging_station(id),
    INDEX idx_guide_station_status (station_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    notice_type TINYINT NOT NULL COMMENT '0通知，1维护，2提示',
    publish_status TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿，1发布，2下架',
    publish_time DATETIME,
    create_by BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_notice_user FOREIGN KEY (create_by) REFERENCES sys_user(id),
    INDEX idx_notice_publish (publish_status, publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
