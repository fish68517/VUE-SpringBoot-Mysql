-- 为“患者1”的健康趋势页面补充近 30 天可展示数据。
-- 用法：在 health_management 数据库中执行本脚本。
-- 特点：可重复执行。脚本会先删除本脚本生成的演示趋势数据，再重新插入。

SET NAMES utf8mb4;

SET @patient1_id = (
  SELECT id
  FROM users
  WHERE username = '患者1'
  ORDER BY id
  LIMIT 1
);

-- 兼容部分演示库中用户名编码异常但用户仍为 1 号记录的情况。
SET @patient1_id = COALESCE(@patient1_id, (
  SELECT id
  FROM users
  WHERE id = 1
  ORDER BY id
  LIMIT 1
));

SET @has_body_temperature = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'health_data'
    AND COLUMN_NAME = 'body_temperature'
);
SET @sql = IF(
  @has_body_temperature = 0,
  'ALTER TABLE health_data ADD COLUMN body_temperature DECIMAL(4,2) AFTER heart_rate',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_blood_oxygen = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'health_data'
    AND COLUMN_NAME = 'blood_oxygen'
);
SET @sql = IF(
  @has_blood_oxygen = 0,
  'ALTER TABLE health_data ADD COLUMN blood_oxygen INT AFTER body_temperature',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_blood_sugar = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'health_data'
    AND COLUMN_NAME = 'blood_sugar'
);
SET @sql = IF(
  @has_blood_sugar = 0,
  'ALTER TABLE health_data ADD COLUMN blood_sugar DECIMAL(5,2) AFTER blood_oxygen',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_sleep_duration = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'health_data'
    AND COLUMN_NAME = 'sleep_duration'
);
SET @sql = IF(
  @has_sleep_duration = 0,
  'ALTER TABLE health_data ADD COLUMN sleep_duration DECIMAL(4,2) AFTER blood_sugar',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

DELETE FROM health_data
WHERE user_id = @patient1_id
  AND data_type = 'ROUTINE'
  AND diet_record LIKE '趋势演示数据%';

INSERT INTO health_data (
  user_id,
  height,
  weight,
  blood_pressure,
  heart_rate,
  body_temperature,
  blood_oxygen,
  blood_sugar,
  sleep_duration,
  diet_record,
  exercise_record,
  check_results,
  review_status,
  review_feedback,
  feedback_date,
  data_type,
  recorded_at,
  created_at
)
SELECT id, 165.00, 61.80, '126/82', 82, 36.50, 98, 5.40, 7.50,
       '趋势演示数据：早餐燕麦牛奶，午餐鸡胸肉和蔬菜，晚餐清淡。',
       '趋势演示数据：快走 35 分钟。',
       '趋势演示数据：基础状态平稳。',
       'REVIEWED',
       '整体指标稳定，建议继续保持规律运动。',
       '2026-04-23 10:30:00',
       'ROUTINE',
       '2026-04-22 08:30:00',
       NOW()
FROM users WHERE id = @patient1_id
UNION ALL
SELECT id, 165.00, 62.40, '132/86', 88, 36.70, 97, 5.90, 6.80,
       '趋势演示数据：近期聚餐较多，主食摄入偏高。',
       '趋势演示数据：无明显运动。',
       '趋势演示数据：血压和血糖略有上升。',
       'REVIEWED',
       '建议减少高盐和高糖饮食，恢复每日运动。',
       '2026-04-27 09:30:00',
       'ROUTINE',
       '2026-04-26 19:30:00',
       NOW()
FROM users WHERE id = @patient1_id
UNION ALL
SELECT id, 165.00, 61.90, '128/83', 80, 36.40, 98, 5.50, 7.20,
       '趋势演示数据：饮食恢复清淡，增加蔬菜摄入。',
       '趋势演示数据：慢跑 3 公里。',
       '趋势演示数据：指标回落。',
       'REVIEWED',
       '血压已有改善，建议继续控制盐分摄入。',
       '2026-05-02 09:20:00',
       'ROUTINE',
       '2026-05-01 08:00:00',
       NOW()
FROM users WHERE id = @patient1_id
UNION ALL
SELECT id, 165.00, 61.30, '124/80', 76, 36.30, 99, 5.20, 7.80,
       '趋势演示数据：三餐规律，晚餐少油。',
       '趋势演示数据：瑜伽 40 分钟。',
       '趋势演示数据：体重与心率改善。',
       'REVIEWED',
       '恢复情况良好，继续保持规律作息。',
       '2026-05-06 11:00:00',
       'ROUTINE',
       '2026-05-05 08:10:00',
       NOW()
FROM users WHERE id = @patient1_id
UNION ALL
SELECT id, 165.00, 60.90, '122/78', 74, 36.40, 99, 5.10, 8.00,
       '趋势演示数据：饮食清淡，水果和蛋白质摄入充足。',
       '趋势演示数据：快走 45 分钟。',
       '趋势演示数据：综合表现良好。',
       'REVIEWED',
       '建议维持当前运动频率。',
       '2026-05-11 09:40:00',
       'ROUTINE',
       '2026-05-10 07:50:00',
       NOW()
FROM users WHERE id = @patient1_id
UNION ALL
SELECT id, 165.00, 60.50, '120/76', 72, 36.50, 99, 4.90, 8.20,
       '趋势演示数据：继续低盐低油饮食。',
       '趋势演示数据：慢跑 4 公里。',
       '趋势演示数据：趋势稳定向好。',
       'REVIEWED',
       '各项指标处于健康范围，建议继续保持。',
       '2026-05-16 10:10:00',
       'ROUTINE',
       '2026-05-15 08:20:00',
       NOW()
FROM users WHERE id = @patient1_id
UNION ALL
SELECT id, 165.00, 60.20, '118/75', 70, 36.40, 99, 4.80, 8.00,
       '趋势演示数据：早餐全麦面包和鸡蛋，晚餐清淡。',
       '趋势演示数据：力量训练 30 分钟。',
       '趋势演示数据：近期健康状态稳定。',
       'REVIEWED',
       '保持现有生活方式，定期记录健康数据。',
       '2026-05-19 09:00:00',
       'ROUTINE',
       '2026-05-18 07:45:00',
       NOW()
FROM users WHERE id = @patient1_id;

SELECT COUNT(*) AS inserted_trend_demo_records
FROM health_data
WHERE user_id = @patient1_id
  AND data_type = 'ROUTINE'
  AND diet_record LIKE '趋势演示数据%';
