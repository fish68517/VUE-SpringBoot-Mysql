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
