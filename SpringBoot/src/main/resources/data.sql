SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS power_inspection
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE power_inspection;

INSERT INTO sys_user (username, password, real_name, role, phone, email, status, created_at, updated_at)
SELECT 'admin', '123456', '系统管理员', 'ADMIN', '13800000001', 'admin@power.com', 'ENABLED', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

INSERT INTO sys_user (username, password, real_name, role, phone, email, status, created_at, updated_at)
SELECT 'inspector1', '123456', '巡检员张三', 'INSPECTOR', '13800000002', 'inspector1@power.com', 'ENABLED', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'inspector1');

INSERT INTO sys_user (username, password, real_name, role, phone, email, status, created_at, updated_at)
SELECT 'maintainer1', '123456', '维护员李四', 'MAINTAINER', '13800000003', 'maintainer1@power.com', 'ENABLED', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'maintainer1');

INSERT INTO device_category (code, name, description, created_at, updated_at)
SELECT 'TRANSFORMER', '变压器', '主变设备分类', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM device_category WHERE code = 'TRANSFORMER');

INSERT INTO device_category (code, name, description, created_at, updated_at)
SELECT 'SWITCH', '开关柜', '开关设备分类', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM device_category WHERE code = 'SWITCH');

INSERT INTO device (category_id, device_code, device_name, location, status, image_name, remark, created_at, updated_at)
SELECT dc.id, 'DEV-001', '1号主变压器', '东区变电站', '运行中', NULL, '系统初始化设备', NOW(), NOW()
FROM device_category dc
WHERE dc.code = 'TRANSFORMER'
  AND NOT EXISTS (SELECT 1 FROM device WHERE device_code = 'DEV-001');

INSERT INTO device (category_id, device_code, device_name, location, status, image_name, remark, created_at, updated_at)
SELECT dc.id, 'DEV-002', '2号高压开关柜', '西区配电室', '运行中', NULL, '系统初始化设备', NOW(), NOW()
FROM device_category dc
WHERE dc.code = 'SWITCH'
  AND NOT EXISTS (SELECT 1 FROM device WHERE device_code = 'DEV-002');

INSERT INTO inspection_standard (category_id, item_name, standard_value, check_method, cycle_days, abnormal_rule, remark, created_at, updated_at)
SELECT dc.id, '油温检查', '40-85℃', '现场目视与温度计核验', 7, '超出标准范围判定异常', '适用于主变压器日常巡检', NOW(), NOW()
FROM device_category dc
WHERE dc.code = 'TRANSFORMER'
  AND NOT EXISTS (SELECT 1 FROM inspection_standard WHERE item_name = '油温检查');

INSERT INTO inspection_standard (category_id, item_name, standard_value, check_method, cycle_days, abnormal_rule, remark, created_at, updated_at)
SELECT dc.id, '柜体异响检查', '无明显异响', '现场听诊', 3, '出现异响判定异常', '适用于高压开关柜巡检', NOW(), NOW()
FROM device_category dc
WHERE dc.code = 'SWITCH'
  AND NOT EXISTS (SELECT 1 FROM inspection_standard WHERE item_name = '柜体异响检查');

INSERT INTO notice (title, content, pinned, status, publish_time, publisher_id, created_at, updated_at)
SELECT '巡检任务填报规范', '巡检员提交巡检结果时，请同步上传现场图片，异常情况必须勾选需要维修并填写问题描述。', 1, 'ACTIVE', NOW(), su.id, NOW(), NOW()
FROM sys_user su
WHERE su.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM notice WHERE title = '巡检任务填报规范');

INSERT INTO notice (title, content, pinned, status, publish_time, publisher_id, created_at, updated_at)
SELECT '维护工单处理要求', '维护员收到工单后，应及时填写处理措施、处理结果和现场图片，待管理员闭环确认后工单才算完成。', 0, 'ACTIVE', NOW(), su.id, NOW(), NOW()
FROM sys_user su
WHERE su.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM notice WHERE title = '维护工单处理要求');
