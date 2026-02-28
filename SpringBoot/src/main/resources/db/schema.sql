-- 个人健康管理系统 - 数据库初始化脚本

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  email VARCHAR(100) COMMENT '邮箱',
  name VARCHAR(100) COMMENT '姓名',
  age INT COMMENT '年龄',
  gender VARCHAR(10) COMMENT '性别',
  phone VARCHAR(20) COMMENT '电话',
  role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER(普通用户), DOCTOR(医师), ADMIN(管理员)',
  status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '账户状态: ACTIVE(活跃), INACTIVE(禁用)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_role (role),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建医师表
CREATE TABLE IF NOT EXISTS doctors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '医师ID',
  user_id BIGINT UNIQUE NOT NULL COMMENT '关联的用户ID',
  license_number VARCHAR(50) UNIQUE NOT NULL COMMENT '执业证号',
  specialization VARCHAR(100) COMMENT '专科',
  hospital VARCHAR(100) COMMENT '医院',
  verified BOOLEAN DEFAULT FALSE COMMENT '是否认证',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_license_number (license_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医师表';

-- 创建健康数据表
CREATE TABLE IF NOT EXISTS health_data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '健康数据ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  height DECIMAL(5,2) COMMENT '身高(cm)',
  weight DECIMAL(5,2) COMMENT '体重(kg)',
  blood_pressure VARCHAR(20) COMMENT '血压',
  heart_rate INT COMMENT '心率(次/分钟)',
  diet_record TEXT COMMENT '饮食记录',
  exercise_record TEXT COMMENT '运动记录',
  data_type VARCHAR(50) COMMENT '数据类型: ROUTINE(常规), GENDER_SPECIFIC(性别相关)',
  recorded_at TIMESTAMP COMMENT '记录时间',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_recorded_at (recorded_at),
  INDEX idx_data_type (data_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康数据表';

-- 创建咨询表
CREATE TABLE IF NOT EXISTS consultations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '咨询ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  doctor_id BIGINT COMMENT '医师ID',
  question TEXT NOT NULL COMMENT '咨询问题',
  answer TEXT COMMENT '医师回答',
  status VARCHAR(20) DEFAULT 'PENDING' COMMENT '咨询状态: PENDING(待回答), ANSWERED(已回答)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  answered_at TIMESTAMP COMMENT '回答时间',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL,
  INDEX idx_user_id (user_id),
  INDEX idx_doctor_id (doctor_id),
  INDEX idx_status (status),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询表';

-- 创建健康建议表
CREATE TABLE IF NOT EXISTS health_advice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '健康建议ID',
  doctor_id BIGINT NOT NULL COMMENT '医师ID',
  user_id BIGINT NOT NULL COMMENT '患者ID',
  advice_content TEXT NOT NULL COMMENT '建议内容',
  recommendation TEXT COMMENT '推荐信息',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_doctor_id (doctor_id),
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康建议表';

-- 创建审计日志表
CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '审计日志ID',
  user_id BIGINT COMMENT '用户ID',
  action VARCHAR(100) COMMENT '操作类型',
  resource VARCHAR(100) COMMENT '操作的资源',
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间戳',
  details TEXT COMMENT '操作详情',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_user_id (user_id),
  INDEX idx_action (action),
  INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';
