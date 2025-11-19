-- Little Shark Fitness Management System - Sample Data
-- 小鲨鱼健身管理系统 - 模拟数据

USE shark_fitness;

-- ==================== 用户数据 ====================
-- Insert Users - 插入用户数据

-- 管理员用户
INSERT INTO user (username, password, role, avatar, gender, intro, created_at, updated_at) VALUES
('admin', 'admin123', 'admin', 'https://api.example.com/avatars/admin.jpg', 'male', '系统管理员', NOW(), NOW());

-- 教练用户
INSERT INTO user (username, password, role, avatar, gender, intro, created_at, updated_at) VALUES
('coach_zhang', 'coach123', 'coach', 'https://api.example.com/avatars/coach_zhang.jpg', 'male', '资深健身教练，擅长力量训练和体型塑造', NOW(), NOW()),
('coach_li', 'coach123', 'coach', 'https://api.example.com/avatars/coach_li.jpg', 'female', '瑜伽和普拉提专家，帮助您改善身体柔韧性', NOW(), NOW()),
('coach_wang', 'coach123', 'coach', 'https://api.example.com/avatars/coach_wang.jpg', 'male', '有氧运动和减脂专家，5年教学经验', NOW(), NOW());

-- 普通用户
INSERT INTO user (username, password, role, avatar, gender, intro, created_at, updated_at) VALUES
('user_xiaoming', 'user123', 'user', 'https://api.example.com/avatars/user_xiaoming.jpg', 'male', '健身爱好者，坚持运动改变生活', NOW(), NOW()),
('user_xiaohong', 'user123', 'user', 'https://api.example.com/avatars/user_xiaohong.jpg', 'female', '瑜伽爱好者，追求身心健康', NOW(), NOW()),
('user_liming', 'user123', 'user', 'https://api.example.com/avatars/user_liming.jpg', 'male', '健身新手，正在学习正确的训练方法', NOW(), NOW()),
('user_wangfang', 'user123', 'user', 'https://api.example.com/avatars/user_wangfang.jpg', 'female', '减脂目标用户，已坚持3个月', NOW(), NOW()),
('user_zhangjie', 'user123', 'user', 'https://api.example.com/avatars/user_zhangjie.jpg', 'male', '力量训练爱好者，目标是增肌', NOW(), NOW()),
('user_liuyan', 'user123', 'user', 'https://api.example.com/avatars/user_liuyan.jpg', 'female', '健身达人，分享健身心得和经验', NOW(), NOW());


-- ==================== 健身资源数据 ====================
-- Insert Fitness Resources - 插入健身资源

-- 视频资源
INSERT INTO fitness_resource (title, description, content_type, file_url, creator_id, view_count, created_at, updated_at) VALUES
('初级健身入门教程', '适合健身初学者的基础训练教程，包括热身、基本动作和放松', 'video', 'https://api.example.com/videos/beginner_tutorial.mp4', 1, 1250, NOW(), NOW()),
('高效腹肌训练30分钟', '针对腹肌的高效训练课程，每天30分钟，坚持4周见效', 'video', 'https://api.example.com/videos/abs_training.mp4', 1, 2840, NOW(), NOW()),
('瑜伽基础课程', '适合所有年龄段的瑜伽基础课程，帮助改善身体柔韧性和平衡能力', 'video', 'https://api.example.com/videos/yoga_basics.mp4', 2, 1560, NOW(), NOW()),
('有氧运动减脂指南', '科学的有氧运动方法，帮助您有效减脂，提高心肺功能', 'video', 'https://api.example.com/videos/cardio_guide.mp4', 3, 3200, NOW(), NOW()),
('力量训练完全指南', '从零开始学习力量训练，包括各种器械的正确使用方法', 'video', 'https://api.example.com/videos/strength_guide.mp4', 1, 2100, NOW(), NOW());

-- 文章资源
INSERT INTO fitness_resource (title, description, content_type, content, creator_id, view_count, created_at, updated_at) VALUES
('健身初学者必读：如何制定合理的训练计划', '本文详细介绍了如何根据自己的目标和身体状况制定合理的训练计划，包括训练频率、强度和恢复时间的安排。', 'article', '健身初学者常常不知道如何开始。首先，你需要明确你的目标：是增肌、减脂还是提高体能？其次，根据你的目标选择合适的训练方式。最后，制定一个循序渐进的计划，并坚持执行。记住，健身是一个长期的过程，不要急功近利。', 1, 890, NOW(), NOW()),
('营养指南：健身期间应该吃什么', '详细介绍了健身期间的营养需求，包括蛋白质、碳水化合物和脂肪的合理比例，以及推荐的食物清单。', 'article', '健身效果的50%来自训练，另外50%来自营养。蛋白质是肌肉的基础，建议每天摄入1.6-2.2克/公斤体重。碳水化合物提供能量，脂肪帮助激素分泌。选择全谷物、瘦肉、鸡蛋、豆类和新鲜蔬菜。', 2, 1240, NOW(), NOW()),
('如何避免健身中的常见错误', '列举了健身过程中常见的错误做法，并提供了改正建议，帮助您更安全有效地进行训练。', 'article', '常见错误包括：1. 过度训练导致过度疲劳；2. 忽视热身和放松；3. 动作不标准导致受伤；4. 只做有氧运动忽视力量训练；5. 不注意恢复和睡眠。避免这些错误，您的健身效果会大大提升。', 3, 756, NOW(), NOW()),
('瑜伽的益处和如何开始', '介绍了瑜伽对身心健康的益处，以及初学者如何安全地开始瑜伽练习。', 'article', '瑜伽不仅能增强身体柔韧性和力量，还能改善心理健康，减少压力和焦虑。初学者应该从基础课程开始，学习正确的呼吸技巧和姿势。每周练习3-4次，坚持8周就能看到明显效果。', 2, 1100, NOW(), NOW()),
('运动恢复的重要性', '详细说明了运动后恢复的重要性，包括睡眠、营养和主动恢复的方法。', 'article', '恢复和训练同样重要。充足的睡眠（7-9小时）对肌肉生长至关重要。营养恢复包括训练后30分钟内摄入蛋白质和碳水化合物。主动恢复包括轻度有氧运动、拉伸和按摩。', 1, 945, NOW(), NOW());


-- ==================== 训练计划数据 ====================
-- Insert Training Plans - 插入训练计划

INSERT INTO training_plan (name, description, exercises, coach_id, student_id, start_date, end_date, status, created_at, updated_at) VALUES
('8周增肌计划', '针对初学者的增肌训练计划，通过循序渐进的力量训练帮助您增加肌肉质量', '[{"day":"周一","exercises":[{"name":"卧推","sets":4,"reps":8,"weight":"60kg"},{"name":"杠铃深蹲","sets":4,"reps":8,"weight":"80kg"}]},{"day":"周三","exercises":[{"name":"硬拉","sets":3,"reps":5,"weight":"100kg"},{"name":"杠铃划船","sets":4,"reps":8,"weight":"70kg"}]},{"day":"周五","exercises":[{"name":"肩上推举","sets":3,"reps":8,"weight":"40kg"},{"name":"卧推","sets":3,"reps":10,"weight":"55kg"}]}]', 1, 3, '2024-01-15', '2024-03-15', 'active', NOW(), NOW()),
('12周减脂计划', '科学的减脂训练计划，结合有氧运动和力量训练，帮助您安全有效地减脂', '[{"day":"周一","exercises":[{"name":"跑步","duration":"30分钟","intensity":"中等"},{"name":"腹肌训练","sets":3,"reps":15}]},{"day":"周三","exercises":[{"name":"动感单车","duration":"45分钟","intensity":"高"},{"name":"全身力量训练","duration":"30分钟"}]},{"day":"周五","exercises":[{"name":"椭圆机","duration":"30分钟","intensity":"中等"},{"name":"核心训练","sets":3,"reps":20}]}]', 3, 4, '2024-01-20', '2024-04-20', 'active', NOW(), NOW()),
('瑜伽柔韧性提升计划', '为期6周的瑜伽课程，帮助您改善身体柔韧性和平衡能力', '[{"day":"周一","exercises":[{"name":"基础瑜伽","duration":"60分钟","type":"hatha"}]},{"day":"周三","exercises":[{"name":"流瑜伽","duration":"60分钟","type":"vinyasa"}]},{"day":"周五","exercises":[{"name":"阴瑜伽","duration":"60分钟","type":"yin"}]}]', 2, 2, '2024-01-10', '2024-02-21', 'active', NOW(), NOW()),
('4周核心力量训练', '专注于核心肌群的训练计划，提高身体稳定性和运动表现', '[{"day":"周一","exercises":[{"name":"平板支撑","duration":"3分钟","sets":3},{"name":"卷腹","sets":3,"reps":20}]},{"day":"周三","exercises":[{"name":"死虫式","sets":3,"reps":15},{"name":"鸟狗式","sets":3,"reps":12}]},{"day":"周五","exercises":[{"name":"俄罗斯转体","sets":3,"reps":20},{"name":"山羊挺身","sets":3,"reps":15}]}]', 1, 5, '2024-02-01', '2024-02-29', 'completed', NOW(), NOW()),
('全身综合训练计划', '适合有一定基础的健身爱好者，全面提升身体各部位的力量和耐力', '[{"day":"周一","exercises":[{"name":"卧推","sets":4,"reps":8},{"name":"杠铃深蹲","sets":4,"reps":8}]},{"day":"周三","exercises":[{"name":"硬拉","sets":3,"reps":5},{"name":"引体向上","sets":3,"reps":8}]},{"day":"周五","exercises":[{"name":"肩上推举","sets":3,"reps":8},{"name":"杠铃划船","sets":4,"reps":8}]}]', 1, 6, '2024-01-25', '2024-04-25', 'active', NOW(), NOW());


-- ==================== 社区动态数据 ====================
-- Insert Community Posts (Dynamic) - 插入社区动态

INSERT INTO dynamic (content, image_urls, user_id, like_count, comment_count, status, created_at, updated_at) VALUES
('今天完成了我的第100次打卡！坚持健身真的改变了我的生活，感谢小鲨鱼健身平台的陪伴！💪', 'https://api.example.com/images/checkin_100.jpg', 3, 45, 8, 'approved', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('分享一下我的早餐：鸡蛋、燕麦和蓝莓。健身从饮食开始！', 'https://api.example.com/images/breakfast.jpg', 4, 32, 5, 'approved', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('刚完成了一个小时的瑜伽课程，感觉身心都得到了放松。瑜伽真的很棒！🧘‍♀️', 'https://api.example.com/images/yoga_session.jpg', 2, 28, 4, 'approved', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('健身房新增了几台器械，今天试了试新的腿部训练机，效果不错！', 'https://api.example.com/images/gym_equipment.jpg', 5, 18, 3, 'approved', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('坚持了3个月的减脂计划，终于看到效果了！从80kg减到75kg，继续加油！', 'https://api.example.com/images/weight_loss.jpg', 4, 56, 12, 'approved', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('今天的训练计划完成！感谢教练张的专业指导，每次都能学到新东西。', 'https://api.example.com/images/training_session.jpg', 6, 22, 6, 'approved', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
('健身不仅改变了我的身体，更改变了我的心态。每天都充满了正能量！', 'https://api.example.com/images/motivation.jpg', 7, 89, 15, 'approved', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
('分享一个简单有效的腹肌训练动作：卷腹。每天坚持20个，4周见效！', 'https://api.example.com/images/abs_exercise.jpg', 3, 67, 11, 'approved', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
('今天参加了健身房的团体课程，和大家一起运动真的很有动力！', 'https://api.example.com/images/group_class.jpg', 5, 34, 7, 'approved', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
('健身小贴士：运动前一定要充分热身，这样可以减少受伤的风险。', 'https://api.example.com/images/warmup_tips.jpg', 1, 41, 9, 'approved', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY));


-- ==================== 打卡数据 ====================
-- Insert Check-in Records - 插入打卡记录

INSERT INTO check_in (user_id, check_date, check_time) VALUES
(3, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 29 DAY), DATE_SUB(NOW(), INTERVAL 29 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, CURDATE(), NOW()),
(4, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, CURDATE(), NOW()),
(5, DATE_SUB(CURDATE(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(5, DATE_SUB(CURDATE(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(5, DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(5, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, CURDATE(), NOW());


-- ==================== 饮食记录数据 ====================
-- Insert Diet Records - 插入饮食记录

INSERT INTO diet_record (user_id, meal_type, food_items, calories, meal_date, created_at, updated_at) VALUES
(3, 'breakfast', '鸡蛋2个、燕麦粥、牛奶', 350, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 'lunch', '鸡胸肉200g、糙米饭、青菜', 550, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 'dinner', '鱼肉150g、红薯、沙拉', 480, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 'snack', '蛋白粉、香蕉', 200, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 'breakfast', '全麦面包、鸡蛋、牛奶', 380, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'lunch', '牛肉200g、米饭、蔬菜', 620, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'dinner', '鸡胸肉150g、甘薯、绿菜', 450, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'snack', '酸奶、坚果', 180, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'breakfast', '燕麦、蓝莓、牛奶', 320, CURDATE(), NOW(), NOW()),
(3, 'lunch', '鸡胸肉、糙米、西兰花', 580, CURDATE(), NOW(), NOW()),
(4, 'breakfast', '鸡蛋、面包、果汁', 400, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 'lunch', '鱼肉、米饭、蔬菜', 520, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 'dinner', '鸡肉、红薯、沙拉', 480, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 'breakfast', '酸奶、谷物、水果', 350, CURDATE(), NOW(), NOW()),
(4, 'lunch', '牛肉、糙米、青菜', 600, CURDATE(), NOW(), NOW()),
(5, 'breakfast', '鸡蛋、全麦面包', 320, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 'lunch', '鸡胸肉、米饭、蔬菜', 550, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 'dinner', '鱼肉、红薯、沙拉', 450, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 'breakfast', '燕麦、香蕉、牛奶', 380, CURDATE(), NOW(), NOW()),
(5, 'lunch', '鸡肉、糙米、西兰花', 580, CURDATE(), NOW(), NOW()),
(6, 'breakfast', '鸡蛋、面包、牛奶', 400, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 'lunch', '牛肉、米饭、蔬菜', 620, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 'dinner', '鱼肉、红薯、沙拉', 480, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 'breakfast', '全麦面包、鸡蛋、果汁', 420, CURDATE(), NOW(), NOW()),
(6, 'lunch', '鸡胸肉、糙米、青菜', 560, CURDATE(), NOW(), NOW());


-- ==================== 评论数据 ====================
-- Insert Comments - 插入评论

INSERT INTO comment (content, user_id, dynamic_id, created_at) VALUES
('太棒了！坚持就是胜利！', 4, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('我也要开始打卡了，向你学习！', 5, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('这个早餐看起来很健康，我也要试试！', 3, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('营养搭配得很好，赞！', 6, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('瑜伽真的很舒服，我也喜欢！', 3, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('请问在哪里可以学瑜伽呢？', 5, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('新器械效果怎么样？', 4, 4, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('我也想试试新器械！', 2, 4, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('太厉害了！减脂5kg，我也要加油！', 3, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('分享一下你的减脂秘诀吧！', 6, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('教练张真的很专业！', 4, 6, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('我也想找一个好教练！', 5, 6, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('这句话说得太好了！', 2, 7, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('健身改变人生！', 3, 7, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('我试过这个动作，真的很有效！', 4, 8, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('坚持做这个动作，效果真的不错！', 5, 8, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('团体课程真的很有动力！', 3, 9, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('我也想参加团体课程！', 6, 9, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('这个建议很实用，谢谢分享！', 4, 10, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('热身真的很重要，我之前就因为没热身受过伤！', 5, 10, DATE_SUB(NOW(), INTERVAL 10 DAY));

-- ==================== 点赞数据 ====================
-- Insert Likes - 插入点赞

INSERT INTO like_record (user_id, dynamic_id, created_at) VALUES
(4, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(6, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 4, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 4, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 4, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(6, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, 6, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 6, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(5, 6, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(3, 7, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(4, 7, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(5, 7, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(6, 7, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 7, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(3, 8, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 8, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(5, 8, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(6, 8, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(3, 9, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(4, 9, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(5, 9, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(3, 10, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 10, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(5, 10, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(6, 10, DATE_SUB(NOW(), INTERVAL 10 DAY));


-- ==================== 教练-学生关系数据 ====================
-- Insert Coach-Student Relationships - 插入教练-学生关系

INSERT INTO coach_student (coach_id, student_id, created_at) VALUES
(1, 3, NOW()),
(1, 5, NOW()),
(1, 6, NOW()),
(2, 2, NOW()),
(2, 7, NOW()),
(3, 4, NOW()),
(3, 6, NOW());

-- ==================== 资源收藏数据 ====================
-- Insert Collections - 插入资源收藏

INSERT INTO collection (user_id, resource_id, created_at) VALUES
(3, 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(3, 2, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(3, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 2, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(4, 4, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(5, 3, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 2, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(6, 4, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 3, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(2, 5, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(7, 4, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ==================== 数据统计 ====================
-- Display data statistics - 显示数据统计

SELECT '✓ Sample data inserted successfully!' AS status;
SELECT CONCAT('用户总数: ', COUNT(*)) AS user_count FROM user;
SELECT CONCAT('健身资源总数: ', COUNT(*)) AS resource_count FROM fitness_resource;
SELECT CONCAT('训练计划总数: ', COUNT(*)) AS training_plan_count FROM training_plan;
SELECT CONCAT('社区动态总数: ', COUNT(*)) AS dynamic_count FROM dynamic;
SELECT CONCAT('打卡记录总数: ', COUNT(*)) AS checkin_count FROM check_in;
SELECT CONCAT('饮食记录总数: ', COUNT(*)) AS diet_record_count FROM diet_record;
SELECT CONCAT('评论总数: ', COUNT(*)) AS comment_count FROM comment;
SELECT CONCAT('点赞总数: ', COUNT(*)) AS like_count FROM like_record;
SELECT CONCAT('教练-学生关系总数: ', COUNT(*)) AS coach_student_count FROM coach_student;
SELECT CONCAT('资源收藏总数: ', COUNT(*)) AS collection_count FROM collection;
