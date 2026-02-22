-- 创建数据库
CREATE DATABASE IF NOT EXISTS weather_agricultural_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE weather_agricultural_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '电话',
  user_type ENUM('farmer', 'merchant', 'admin') NOT NULL COMMENT '用户类型',
  region VARCHAR(100) COMMENT '地区',
  status ENUM('active', 'inactive', 'locked') DEFAULT 'active' COMMENT '账户状态',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_user_type (user_type),
  INDEX idx_region (region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建气象数据表
CREATE TABLE IF NOT EXISTS weather_data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  region VARCHAR(100) NOT NULL COMMENT '地区',
  temperature DECIMAL(5, 2) COMMENT '温度',
  humidity INT COMMENT '湿度',
  precipitation DECIMAL(8, 2) COMMENT '降水量',
  wind_speed DECIMAL(5, 2) COMMENT '风速',
  weather_condition VARCHAR(50) COMMENT '天气状况',
  recorded_at TIMESTAMP COMMENT '记录时间',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_region (region),
  INDEX idx_recorded_at (recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='气象数据表';

-- 创建预警表
CREATE TABLE IF NOT EXISTS warnings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warning_type VARCHAR(50) NOT NULL COMMENT '预警类型',
  region VARCHAR(100) NOT NULL COMMENT '地区',
  severity ENUM('low', 'medium', 'high', 'critical') NOT NULL COMMENT '预警等级',
  description TEXT COMMENT '预警描述',
  start_time TIMESTAMP COMMENT '开始时间',
  end_time TIMESTAMP COMMENT '结束时间',
  status ENUM('active', 'expired', 'cancelled') DEFAULT 'active' COMMENT '预警状态',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_region (region),
  INDEX idx_warning_type (warning_type),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警表';

-- 创建农资产品表
CREATE TABLE IF NOT EXISTS agricultural_products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
  category VARCHAR(50) COMMENT '产品类别',
  description TEXT COMMENT '产品描述',
  price DECIMAL(10, 2) COMMENT '价格',
  stock INT COMMENT '库存',
  merchant_id BIGINT COMMENT '商家ID',
  applicable_weather VARCHAR(100) COMMENT '适用天气',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (merchant_id) REFERENCES users(id),
  INDEX idx_category (category),
  INDEX idx_merchant_id (merchant_id),
  INDEX idx_stock (stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农资产品表';

-- 创建作物表
CREATE TABLE IF NOT EXISTS crops (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  crop_name VARCHAR(50) NOT NULL COMMENT '作物名称',
  growth_stage VARCHAR(50) COMMENT '生育期',
  region VARCHAR(100) COMMENT '地区',
  user_id BIGINT COMMENT '用户ID',
  planting_date DATE COMMENT '种植日期',
  expected_harvest_date DATE COMMENT '预期收获日期',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_user_id (user_id),
  INDEX idx_region (region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作物表';

-- 创建推荐表
CREATE TABLE IF NOT EXISTS recommendations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warning_id BIGINT COMMENT '预警ID',
  product_id BIGINT COMMENT '产品ID',
  user_id BIGINT COMMENT '用户ID',
  priority INT COMMENT '优先级',
  reason TEXT COMMENT '推荐原因',
  status ENUM('pending', 'accepted', 'rejected') DEFAULT 'pending' COMMENT '推荐状态',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (warning_id) REFERENCES warnings(id),
  FOREIGN KEY (product_id) REFERENCES agricultural_products(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_warning_id (warning_id),
  INDEX idx_user_id (user_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐表';

-- 创建订单表
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_number VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
  user_id BIGINT COMMENT '用户ID',
  product_id BIGINT COMMENT '产品ID',
  quantity INT COMMENT '数量',
  total_price DECIMAL(10, 2) COMMENT '总价',
  status ENUM('pending', 'paid', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending' COMMENT '订单状态',
  payment_method VARCHAR(50) COMMENT '支付方式',
  delivery_address TEXT COMMENT '收货地址',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES agricultural_products(id),
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  INDEX idx_order_number (order_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 插入用户数据
INSERT INTO users (username, password, email, phone, user_type, region, status) VALUES
('farmer_001', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'farmer001@example.com', '13800138001', 'farmer', '河南省郑州市', 'active'),
('farmer_002', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'farmer002@example.com', '13800138002', 'farmer', '山东省济南市', 'active'),
('merchant_001', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'merchant001@example.com', '13900139001', 'merchant', '北京市朝阳区', 'active'),
('merchant_002', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'merchant002@example.com', '13900139002', 'merchant', '上海市浦东新区', 'active'),
('admin_001', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'admin001@example.com', '13700137001', 'admin', '北京市', 'active'),
('farmer_003', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'farmer003@example.com', '13800138003', 'farmer', '江苏省南京市', 'active');

-- 插入气象数据
INSERT INTO weather_data (region, temperature, humidity, precipitation, wind_speed, weather_condition, recorded_at) VALUES
('河南省郑州市', 28.5, 65, 0.0, 3.2, '晴天', '2024-02-20 14:00:00'),
('山东省济南市', 25.3, 72, 2.5, 4.1, '多云', '2024-02-20 14:30:00'),
('北京市朝阳区', 22.1, 58, 0.0, 2.8, '晴天', '2024-02-20 15:00:00'),
('上海市浦东新区', 26.7, 68, 5.2, 3.5, '小雨', '2024-02-20 15:30:00'),
('江苏省南京市', 24.9, 70, 1.8, 3.9, '阴天', '2024-02-20 16:00:00'),
('浙江省杭州市', 27.2, 62, 0.0, 2.5, '晴天', '2024-02-20 16:30:00');

-- 插入预警数据
INSERT INTO warnings (warning_type, region, severity, description, start_time, end_time, status) VALUES
('暴雨', '河南省郑州市', 'high', '未来24小时内可能出现暴雨，请做好防护准备', '2024-02-21 08:00:00', '2024-02-22 08:00:00', 'active'),
('冰雹', '山东省济南市', 'critical', '预计明天下午可能出现冰雹，请立即采取防护措施', '2024-02-21 10:00:00', '2024-02-21 18:00:00', 'active'),
('大风', '北京市朝阳区', 'medium', '明天风力较大，请加固农作物支架', '2024-02-21 12:00:00', '2024-02-22 12:00:00', 'active'),
('霜冻', '江苏省南京市', 'high', '后天早晨可能出现霜冻，请做好保温工作', '2024-02-22 06:00:00', '2024-02-22 12:00:00', 'active'),
('干旱', '浙江省杭州市', 'medium', '近期降水较少，建议及时灌溉', '2024-02-20 00:00:00', '2024-03-05 00:00:00', 'active'),
('洪涝', '上海市浦东新区', 'high', '预计本周降水量较大，请做好排水准备', '2024-02-21 00:00:00', '2024-02-25 00:00:00', 'active');

-- 插入农资产品数据
INSERT INTO agricultural_products (product_name, category, description, price, stock, merchant_id, applicable_weather) VALUES
('防雨布', '防护用品', '高质量防雨布，防水防晒，适合暴雨天气使用', 89.99, 150, 3, '暴雨'),
('冰雹防护网', '防护用品', '专业冰雹防护网，保护农作物免受冰雹伤害', 199.99, 80, 3, '冰雹'),
('支架加固材料', '支架材料', '钢铁支架加固材料，适合大风天气加固使用', 45.50, 200, 4, '大风'),
('保温被', '保温用品', '农用保温被，有效防止霜冻伤害', 129.99, 120, 3, '霜冻'),
('灌溉管道', '灌溉设备', '高效灌溉管道系统，适合干旱地区使用', 299.99, 60, 4, '干旱'),
('排水泵', '排水设备', '大功率排水泵，快速排除积水', 1299.99, 25, 4, '洪涝');

-- 插入作物数据
INSERT INTO crops (crop_name, growth_stage, region, user_id, planting_date, expected_harvest_date) VALUES
('小麦', '拔节期', '河南省郑州市', 1, '2023-10-15', '2024-06-01'),
('玉米', '苗期', '山东省济南市', 2, '2024-04-01', '2024-10-15'),
('水稻', '分蘖期', '江苏省南京市', 6, '2024-05-01', '2024-10-01'),
('大豆', '开花期', '河南省郑州市', 1, '2024-05-15', '2024-10-30'),
('棉花', '苗期', '山东省济南市', 2, '2024-04-20', '2024-11-15'),
('蔬菜', '生长期', '北京市朝阳区', 1, '2024-02-01', '2024-05-01');

-- 插入推荐数据
INSERT INTO recommendations (warning_id, product_id, user_id, priority, reason, status) VALUES
(1, 1, 1, 1, '暴雨预警，建议使用防雨布保护农作物', 'pending'),
(2, 2, 2, 1, '冰雹预警，强烈建议使用冰雹防护网', 'pending'),
(3, 3, 1, 2, '大风预警，建议加固支架', 'pending'),
(4, 4, 6, 1, '霜冻预警，建议使用保温被', 'pending'),
(5, 5, 6, 2, '干旱预警，建议使用灌溉设备', 'pending'),
(6, 6, 1, 1, '洪涝预警，建议准备排水设备', 'pending');

-- 插入订单数据
INSERT INTO orders (order_number, user_id, product_id, quantity, total_price, status, payment_method, delivery_address) VALUES
('ORD20240220001', 1, 1, 2, 179.98, 'paid', '支付宝', '河南省郑州市中原区农业路123号'),
('ORD20240220002', 2, 2, 1, 199.99, 'shipped', '微信支付', '山东省济南市历下区泉城路456号'),
('ORD20240220003', 6, 4, 3, 389.97, 'pending', '银行转账', '江苏省南京市鼓楼区中山路789号'),
('ORD20240220004', 1, 3, 5, 227.50, 'delivered', '支付宝', '河南省郑州市金水区文化路321号'),
('ORD20240220005', 2, 5, 1, 299.99, 'paid', '微信支付', '山东省济南市槐荫区经十路654号'),
('ORD20240220006', 6, 6, 1, 1299.99, 'pending', '银行转账', '江苏省南京市浦口区浦珠路987号');
