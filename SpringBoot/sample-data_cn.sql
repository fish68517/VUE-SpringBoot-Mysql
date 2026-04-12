-- =====================================================
-- 旅行记忆系统 - 中文示例数据脚本（专为中国留学生毕设打造）
-- 使用前请先执行 database-init.sql 创建好表结构
-- =====================================================

USE travel_memory_system;

-- 清空旧数据（可选，第一次运行可注释掉）
-- TRUNCATE TABLE likes;
-- TRUNCATE TABLE comments;
-- TRUNCATE TABLE multimedia_files;
-- TRUNCATE TABLE map_footprints;
-- TRUNCATE TABLE itinerary_items;
-- TRUNCATE TABLE travel_plans;
-- TRUNCATE TABLE travel_records;
-- TRUNCATE TABLE users;

-- ====================== 1. 插入 8 位中国风格用户 ======================
INSERT INTO users (username, email, password_hash, avatar_url, bio, created_at) VALUES
('小明爱旅行',    'xiaoming@example.com',    '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',   '背包客一枚，喜欢自驾和拍照', NOW()),
('小红的环球梦',  'xiaohong@example.com',    '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',   '省钱旅行达人，专攻东南亚', NOW()),
('摄影师阿杰',    'ajie@example.com',        '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.dicebear.com/7.x/avataaars/svg?seed=ajie',       '专业风光摄影，爱拍日出日落', NOW()),
('吃货胖胖',      'pangpang@example.com',    '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.dicebear.com/7.x/avataaars/svg?seed=pangpang',   '为了吃跑遍大江南北', NOW()),
('自由行小雨',    'xiaoyu@example.com',      '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoyu',     '自由行攻略狂魔，已打卡30+城市', NOW()),
('川藏线老王',    'laowang@example.com',     '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.dicebear.com/7.x/avataaars/svg?seed=laowang',    '骑行川藏线三次，318永远的神', NOW()),
('海岛控小美',    'xiaomei@example.com',     '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaomei',    '一年至少飞三次海岛', NOW()),
('学生党阿华',    'ahua@example.com',        '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KfzmAIbVvwr4Qu5jey', 'https://api.dicebear.com/7.x/avataaars/svg?seed=ahua',       '穷游大学生，寒暑假必跑', NOW());

-- ====================== 2. 插入旅行记录（真实中文标题） ======================
INSERT INTO travel_records (user_id, title, destination, start_date, end_date, description, diary_content, is_public, view_count, created_at) VALUES
(1, '2024东京樱花季8天7夜', '日本·东京', '2024-03-25', '2024-04-02', '一个人在樱花雨里走了800公里', '<p>Day1：成田机场 → 上野公园看樱花，人山人海但超美！</p><p>Day3：去了镰仓大佛和江之岛，吹着海风吃生蚝</p><p>Day6：在新宿歌舞伎町迷路到凌晨2点，笑死</p>', TRUE, 128, NOW()),
(1, '京都赏枫红叶深度游', '日本·京都', '2024-11-18', '2024-11-24', '穿和服走在哲学之道，太出片了', '<p>去了东福寺、通天桥、清水寺，红叶真的绝绝子！</p>', TRUE, 95, NOW()),
(2, '泰国曼谷+芭提雅7天吃吃吃', '泰国·曼谷/芭提雅', '2024-07-10', '2024-07-17', '人均消费800块，吃到扶墙走', '<p>冬阴功汤、芒果糯米饭、船面、烤鱿鱼……我真的会谢</p>', TRUE, 203, NOW()),
(2, '普吉岛自由行5天4晚', '泰国·普吉岛', '2024-12-20', '2024-12-25', '圣诞节在海边看烟花，太浪漫了', '<p>浮潜看到小丑鱼，晚上去了芭东夜市</p>', FALSE, 0, NOW()),
(3, '川西小环线自驾9天', '四川·稻城亚丁', '2024-10-01', '2024-10-09', '国庆节拼车去川西，蓝天白云雪山', '<p>牛奶海、五色海、洛绒牛场，氧气瓶带了三个哈哈哈</p>', TRUE, 312, NOW()),
(4, '厦门+鼓浪屿3天2夜', '福建·厦门', '2024-09-12', '2024-09-15', '曾厝垵吃遍所有小吃', '<p>沙茶面、海蛎煎、花生汤、土笋冻，通通拿下！</p>', TRUE, 187, NOW()),
(5, '越南芽庄+大叻6天5夜', '越南', '2024-08-03', '2024-08-09', '3000块玩转越南南部，性价比绝了', '<p>骑摩托环游大叻，喝滴漏咖啡，吃法棍三明治</p>', TRUE, 156, NOW()),
(6, '新疆伊犁环线10天', '新疆·伊犁', '2024-06-20', '2024-06-30', '那拉提、喀拉峻、琼库什台，草原上的童话', '<p>6月去正好，薰衣草还没开但野花开疯了</p>', TRUE, 289, NOW());

-- ====================== 3. 插入旅行计划（未来计划） ======================
INSERT INTO travel_plans (user_id, title, destination, start_date, end_date, budget, description, created_at) VALUES
(1, '2025日本本州15天自由行', '日本本州（东京-京都-大阪-奈良）', '2025-03-25', '2025-04-08', 18000, '樱花季二刷日本，准备住青旅+民宿', NOW()),
(2, '2025暑假东南亚四国30天', '泰国→老挝→越南→柬埔寨', '2025-07-01', '2025-07-31', 15000, '毕业旅行！穷游版东南亚大环线', NOW()),
(3, '2025国庆川藏南线自驾', '成都→林芝→拉萨→纳木错', '2025-10-01', '2025-10-15', 25000, '和朋友拼车进藏，梦想实现', NOW()),
(4, '2025冰岛极光摄影10天', '冰岛一圈', '2025-02-10', '2025-02-20', 35000, '攒了一年钱就为了看极光', NOW()),
(5, '2025清明节云南6天', '昆明→大理→丽江→泸沽湖', '2025-04-03', '2025-04-09', 6000, '学生党清明小长假拼假云南行', NOW());

-- ====================== 4. 插入行程安排（对应上面的计划） ======================
INSERT INTO itinerary_items (plan_id, item_date, item_type, title, description, location, created_at) VALUES
(1, '2025-03-25', 'flight', '北京→东京羽田', '红眼航班，春秋航空', '日本·东京', NOW()),
(1, '2025-03-26', 'accommodation', '上野胶囊酒店', '3晚，省钱大法', '日本·东京上野', NOW()),
(1, '2025-03-29', 'transportation', '新干线到京都', '买了JR Pass超值', '日本·京都', NOW()),
(2, '2025-07-01', 'flight', '广州→曼谷', '亚航特价票', '泰国·曼谷', NOW()),
(2, '2025-07-10', 'transportation', '曼谷→万象', '夜巴士省钱', '老挝·万象', NOW());

-- ====================== 5. 插入地图足迹点 ======================
INSERT INTO map_footprints (travel_record_id, location_name, latitude, longitude, visit_date, created_at) VALUES
(1, '上野公园', 35.712827, 139.774592, '2024-03-26', NOW()),
(1, '浅草寺', 35.714819, 139.796661, '2024-03-27', NOW()),
(1, '东京晴空塔', 35.710062, 139.810700, '2024-03-28', NOW()),
(3, '曼谷大皇宫', 13.756331, 100.491531, '2024-07-11', NOW()),
(3, '玉佛寺', 13.751389, 100.492778, '2024-07-11', NOW()),
(5, '稻城亚丁景区', 29.090000, 100.290000, '2024-10-05', NOW());

-- ====================== 6. 插入评论（中文语气） ======================
INSERT INTO comments (travel_record_id, user_id, content, created_at) VALUES
(1, 2, '天哪樱花也太美了吧！求攻略！', NOW()),
(1, 3, '上野那张逆光樱花绝了！什么相机拍的？', NOW()),
(1, 4, '我下个月也去东京，求带路哈哈哈', NOW()),
(3, 1, '曼谷的路边摊真的yyds！我上次也吃到撑', NOW()),
(3, 5, '求问芭提雅的酒店怎么选呀？第一次去', NOW()),
(5, 6, '川西真的绝美！国庆人会很多吗？', NOW()),
(6, 1, '厦门曾厝垵真的好吃到炸！下次还去！', NOW());

-- ====================== 7. 插入点赞 ======================
INSERT INTO likes (travel_record_id, user_id, created_at) VALUES
(1, 2, NOW()),(1, 3, NOW()),(1, 4, NOW()),(1, 5, NOW()),(1, 6, NOW()),
(3, 1, NOW()),(3, 3, NOW()),(3, 5, NOW()),(3, 7, NOW()),
(5, 1, NOW()),(5, 2, NOW()),(5, 8, NOW());

-- ====================== 8. 插入示例照片 ======================
INSERT INTO multimedia_files (travel_record_id, file_name, file_path, file_type, file_size, upload_date) VALUES
(1, '东京樱花雨.jpg', '/uploads/records/1/cherry-blossom-tokyo.jpg', 'image/jpeg', 2897345, NOW()),
(1, '浅草寺夜景.jpg', '/uploads/records/1/sensoji-night.jpg', 'image/jpeg', 3124567, NOW()),
(3, '曼谷街边小吃摊.mp4', '/uploads/records/3/bangkok-street-food.mp4', 'video/mp4', 68794321, NOW()),
(5, '稻城亚丁牛奶海.jpg', '/uploads/records/5/aden-milk-lake.jpg', 'image/jpeg', 4235678, NOW());

-- ====================== 完成提示 ======================
SELECT '中文示例数据插入成功！系统已准备好展示给导师啦！' AS 状态;

SELECT
  (SELECT COUNT(*) FROM users) AS 用户数量,
  (SELECT COUNT(*) FROM travel_records) AS 旅行记录数,
  (SELECT COUNT(*) FROM travel_plans) AS 旅行计划数,
  (SELECT COUNT(*) FROM comments) AS 评论数量,
  (SELECT COUNT(*) FROM likes) AS 点赞数量;