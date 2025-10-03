-- 创建数据库
CREATE DATABASE IF NOT EXISTS `time_management_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `time_management_db`;

-- 1. 校园用户表 (CampusUser)
CREATE TABLE `campus_user` (
  `campus_user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '校园用户唯一标识',
  `campus_nickname` VARCHAR(50) NOT NULL COMMENT '用户昵称',
  `campus_avatar_url` VARCHAR(255) NULL COMMENT '头像 URL',
  `campus_school_id` VARCHAR(20) NULL COMMENT '学号',
  `campus_email_addr` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址',
  `campus_status_flag` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态 (0 - 未审核, 1 - 已审核, 2 - 禁用)',
  `campus_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `campus_level_code` INT UNSIGNED DEFAULT 0 COMMENT '用户等级代码',
  `campus_badge_count` INT UNSIGNED DEFAULT 0 COMMENT '用户徽章个数',
  `campus_last_login_ts` TIMESTAMP NULL COMMENT '最后登录时间',
  `campus_user_type` VARCHAR(20) DEFAULT 'student' COMMENT '用户类型 (student, admin)',
  PRIMARY KEY (`campus_user_id`)
) COMMENT '校园用户表';

-- 2. 任务专注表 (TaskFocus)
CREATE TABLE `task_focus` (
  `task_focus_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务专注标识',
  `campus_user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `task_title_text` VARCHAR(100) NOT NULL COMMENT '任务标题文本',
  `task_description_text` TEXT NULL COMMENT '任务描述文本',
  `task_deadline_timestamp` TIMESTAMP NULL COMMENT '截止时间戳',
  `task_status_enum` VARCHAR(20) DEFAULT 'pending' COMMENT '任务状态 (pending, in_progress, completed, archived)',
  `task_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `task_priority_code` TINYINT UNSIGNED DEFAULT 1 COMMENT '优先级代码 (0 - 低, 1 - 中, 2 - 高)',
  `task_reminder_timestamp` TIMESTAMP NULL COMMENT '提醒时间戳',
  `task_category_code` VARCHAR(20) NULL COMMENT '任务分类代码',
  `task_focus_duration_mins` INT UNSIGNED DEFAULT 30 COMMENT '专注时长 (分钟)',
  `task_break_duration_mins` INT UNSIGNED DEFAULT 5 COMMENT '休息时长 (分钟)',
  `task_app_block_flag` BOOLEAN DEFAULT FALSE COMMENT '是否启用应用拦截',
  `task_actual_minutes` INT UNSIGNED DEFAULT 0 COMMENT '实际分钟数',
  PRIMARY KEY (`task_focus_id`),
  FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user`(`campus_user_id`)
) COMMENT '任务专注表';

-- 3. 专注记录表 (FocusRecord)
CREATE TABLE `focus_record` (
  `focus_record_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '专注记录标识',
  `campus_user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `task_focus_id` BIGINT UNSIGNED NOT NULL COMMENT '关联任务 ID',
  `focus_start_timestamp` TIMESTAMP NOT NULL COMMENT '开始时间戳',
  `focus_end_timestamp` TIMESTAMP NULL COMMENT '结束时间戳',
  `focus_duration_seconds` INT UNSIGNED NOT NULL COMMENT '专注时长 (秒)',
  `focus_type_enum` VARCHAR(20) COMMENT '专注类型 (e.g., pomodoro, custom)',
  `focus_status_enum` VARCHAR(20) COMMENT '专注状态 (completed, interrupted)',
  `focus_interruption_count` INT UNSIGNED DEFAULT 0 COMMENT '中断次数',
  `focus_pause_count` INT UNSIGNED DEFAULT 0 COMMENT '暂停次数',
  `focus_note_text` TEXT NULL COMMENT '专注备注文本',
  `focus_app_block_flag` BOOLEAN DEFAULT FALSE COMMENT '是否启用应用拦截',
  PRIMARY KEY (`focus_record_id`),
  FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user`(`campus_user_id`),
  FOREIGN KEY (`task_focus_id`) REFERENCES `task_focus`(`task_focus_id`)
) COMMENT '专注记录表';

-- 4. 习惯追踪表 (HabitTrack)
CREATE TABLE `habit_track` (
  `habit_track_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '习惯追踪标识',
  `campus_user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `habit_name_text` VARCHAR(50) NOT NULL COMMENT '习惯名称文本',
  `habit_frequency_enum` VARCHAR(20) COMMENT '频率 (daily, weekly)',
  `habit_reminder_time` TIME NULL COMMENT '提醒时间',
  `habit_status_enum` VARCHAR(20) DEFAULT 'active' COMMENT '习惯状态 (active, archived)',
  `habit_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `habit_streak_count` INT UNSIGNED DEFAULT 0 COMMENT '连续打卡天数',
  `habit_total_count` INT UNSIGNED DEFAULT 0 COMMENT '总打卡次数',
  `habit_goal_count` INT UNSIGNED DEFAULT 0 COMMENT '目标打卡次数',
  `habit_color_hex` CHAR(7) DEFAULT '#FFFFFF' COMMENT '习惯颜色 HEX',
  `habit_icon_code` VARCHAR(20) NULL COMMENT '习惯图标代码',
  PRIMARY KEY (`habit_track_id`),
  FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user`(`campus_user_id`)
) COMMENT '习惯追踪表';

-- 5. 习惯打卡表 (HabitCheckin)
CREATE TABLE `habit_checkin` (
  `habit_checkin_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '习惯打卡标识',
  `habit_track_id` BIGINT UNSIGNED NOT NULL COMMENT '关联习惯 ID',
  `checkin_date` DATE NOT NULL COMMENT '打卡日期',
  `checkin_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间戳',
  `checkin_status_enum` VARCHAR(20) DEFAULT 'completed' COMMENT '打卡状态 (completed, skipped)',
  `checkin_note_text` VARCHAR(255) NULL COMMENT '打卡备注文本',
  `checkin_streak_count` INT UNSIGNED DEFAULT 0 COMMENT '当前连续打卡数',
  PRIMARY KEY (`habit_checkin_id`),
  FOREIGN KEY (`habit_track_id`) REFERENCES `habit_track`(`habit_track_id`)
) COMMENT '习惯打卡表';

-- 6. 成就徽章表 (Achievement)
CREATE TABLE `achievement` (
  `achievement_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '成就徽章标识',
  `achieve_name_text` VARCHAR(50) NOT NULL COMMENT '徽章名称文本',
  `achieve_description_text` VARCHAR(255) NULL COMMENT '徽章描述文本',
  `achieve_icon_url` VARCHAR(255) NOT NULL COMMENT '徽章图标 URL',
  `achieve_rule_text` VARCHAR(500) NOT NULL COMMENT '获取规则文本',
  `achieve_type_enum` VARCHAR(20) COMMENT '徽章类型 (e.g., focus, habit, social)',
  `achieve_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `achieve_active_flag` BOOLEAN DEFAULT TRUE COMMENT '是否激活',
  PRIMARY KEY (`achievement_id`)
) COMMENT '成就徽章表';

-- 7. 用户成就关联表 (UserAchieveRel)
CREATE TABLE `user_achieve_rel` (
  `user_achieve_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户成就关联标识',
  `campus_user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `achievement_id` BIGINT UNSIGNED NOT NULL COMMENT '关联徽章 ID',
  `achieve_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间戳',
  `achieve_condition_text` VARCHAR(500) NULL COMMENT '达成条件描述',
  PRIMARY KEY (`user_achieve_id`),
  FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user`(`campus_user_id`),
  FOREIGN KEY (`achievement_id`) REFERENCES `achievement`(`achievement_id`)
) COMMENT '用户成就关联表';

-- 8. 资源分类表 (ResourceCategory)
CREATE TABLE `resource_category` (
  `resource_category_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '资源分类标识',
  `category_name_text` VARCHAR(50) NOT NULL COMMENT '分类名称文本',
  `category_description_text` VARCHAR(255) NULL COMMENT '分类描述文本',
  `category_order_number` INT UNSIGNED DEFAULT 0 COMMENT '显示顺序号',
  `category_icon_code` VARCHAR(20) NULL COMMENT '分类图标代码',
  `category_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  PRIMARY KEY (`resource_category_id`)
) COMMENT '资源分类表';

-- 9. 学习资源表 (LearnResource)
CREATE TABLE `learn_resource` (
  `learn_resource_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '学习资源标识',
  `resource_category_id` BIGINT UNSIGNED NOT NULL COMMENT '资源分类 ID',
  `resource_name_text` VARCHAR(100) NOT NULL COMMENT '资源名称文本',
  `resource_url_text` VARCHAR(500) NOT NULL COMMENT '资源链接文本',
  `resource_description_text` TEXT NULL COMMENT '资源描述文本',
  `resource_type_enum` VARCHAR(20) DEFAULT 'link' COMMENT '资源类型 (link, document)',
  `resource_recommend_level` TINYINT UNSIGNED DEFAULT 3 COMMENT '推荐级别 (0-5)',
  `resource_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `resource_update_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
  `resource_click_count` INT UNSIGNED DEFAULT 0 COMMENT '点击次数',
  PRIMARY KEY (`learn_resource_id`),
  FOREIGN KEY (`resource_category_id`) REFERENCES `resource_category`(`resource_category_id`)
) COMMENT '学习资源表';

-- 10. 校园好友表 (CampusFriend)
CREATE TABLE `campus_friend` (
  `campus_friend_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '校园好友标识',
  `campus_user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户 ID',
  `friend_user_id` BIGINT UNSIGNED NOT NULL COMMENT '好友 ID',
  `friend_status_enum` VARCHAR(20) DEFAULT 'pending' COMMENT '好友状态 (pending, accepted, blocked)',
  `friend_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `friend_update_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
  `friend_visibility_flag` BOOLEAN DEFAULT TRUE COMMENT '是否可见对方动态',
  PRIMARY KEY (`campus_friend_id`),
  FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user`(`campus_user_id`),
  FOREIGN KEY (`friend_user_id`) REFERENCES `campus_user`(`campus_user_id`)
) COMMENT '校园好友表';

-- 11. 用户设置表 (UserSetting)
CREATE TABLE `user_setting` (
  `user_setting_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户设置标识',
  `campus_user_id` BIGINT UNSIGNED NOT NULL UNIQUE COMMENT '关联用户 ID',
  `setting_theme_enum` VARCHAR(20) DEFAULT 'light' COMMENT '主题 (light, dark)',
  `setting_default_focus_mins` INT UNSIGNED DEFAULT 30 COMMENT '默认专注时长 (分钟)',
  `setting_default_break_mins` INT UNSIGNED DEFAULT 5 COMMENT '默认休息时长 (分钟)',
  `setting_notification_flag` BOOLEAN DEFAULT TRUE COMMENT '是否启用通知',
  `setting_privacy_level` TINYINT UNSIGNED DEFAULT 1 COMMENT '隐私级别 (0-公开, 1-好友可见, 2-私密)',
  `setting_daily_reminder_time` TIME NULL COMMENT '每日提醒时间',
  `setting_week_start_enum` VARCHAR(10) DEFAULT 'monday' COMMENT '周起始日 (sunday, monday)',
  `setting_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `setting_update_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
  PRIMARY KEY (`user_setting_id`),
  FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user`(`campus_user_id`)
) COMMENT '用户设置表';

-- 12. 通知中心表 (NotificationCenter)
CREATE TABLE `notification_center` (
  `notification_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知标识',
  `campus_user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `notification_type_enum` VARCHAR(20) COMMENT '通知类型 (system, friend_request, task_reminder)',
  `notification_title_text` VARCHAR(100) NOT NULL COMMENT '通知标题文本',
  `notification_content_text` TEXT NULL COMMENT '通知内容文本',
  `notification_is_read_flag` BOOLEAN DEFAULT FALSE COMMENT '是否已读标志',
  `notification_create_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `notification_expire_timestamp` TIMESTAMP NULL COMMENT '过期时间戳',
  `notification_action_url` VARCHAR(500) NULL COMMENT '动作 URL',
  `notification_priority_code` TINYINT UNSIGNED DEFAULT 1 COMMENT '优先级代码 (0 - 低, 1 - 中, 2 - 高)',
  PRIMARY KEY (`notification_id`),
  FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user`(`campus_user_id`)
) COMMENT '通知中心表';



-- 使用目标数据库
USE `time_management_db`;

-- 在插入新数据前，清空所有表中的旧数据（注意外键约束，按顺序清空）
-- 注意：SET FOREIGN_KEY_CHECKS=0; 和 SET FOREIGN_KEY_CHECKS=1; 用于临时禁用和恢复外键检查，以便能按任意顺序清空表。
SET FOREIGN_KEY_CHECKS=0;
TRUNCATE TABLE `notification_center`;
TRUNCATE TABLE `user_setting`;
TRUNCATE TABLE `campus_friend`;
TRUNCATE TABLE `learn_resource`;
TRUNCATE TABLE `resource_category`;
TRUNCATE TABLE `user_achieve_rel`;
TRUNCATE TABLE `achievement`;
TRUNCATE TABLE `habit_checkin`;
TRUNCATE TABLE `habit_track`;
TRUNCATE TABLE `focus_record`;
TRUNCATE TABLE `task_focus`;
TRUNCATE TABLE `campus_user`;
SET FOREIGN_KEY_CHECKS=1;


-- 1. 校园用户表 (campus_user)
INSERT INTO `campus_user` (`campus_user_id`, `campus_nickname`, `campus_avatar_url`, `campus_school_id`, `campus_email_addr`, `campus_status_flag`, `campus_level_code`, `campus_badge_count`, `campus_user_type`) VALUES
(1, '张伟', 'https://example.com/avatars/zhangwei.png', '20210001', 'zhangwei@university.edu.cn', 1, 5, 2, 'student'),
(2, '李静', 'https://example.com/avatars/lijing.png', '20210002', 'lijing@university.edu.cn', 1, 3, 1, 'student'),
(3, '王磊', 'https://example.com/avatars/wanglei.png', '20220001', 'wanglei@university.edu.cn', 0, 1, 0, 'student'),
(4, 'Admin', 'https://example.com/avatars/admin.png', '00000000', 'admin@university.edu.cn', 1, 99, 0, 'admin');

-- 2. 任务专注表 (task_focus)
INSERT INTO `task_focus` (`campus_user_id`, `task_title_text`, `task_description_text`, `task_deadline_timestamp`, `task_status_enum`, `task_priority_code`, `task_category_code`) VALUES
(1, '完成毕业论文第三章', '撰写关于市场分析的部分，约3000字。', '2025-10-15 23:59:59', 'in_progress', 2, '毕业设计'),
(1, '学习Vue3新特性', '观看官方文档的组合式API部分，并做笔记。', '2025-10-05 22:00:00', 'pending', 1, '专业学习'),
(2, '准备四级英语单词', '背诵List 10 - List 12的核心单词。', '2025-10-03 18:00:00', 'completed', 2, '英语学习'),
(2, '小组项目UI设计', '使用Figma完成项目首页和个人中心的UI原型图。', '2025-10-08 20:00:00', 'pending', 1, '课程作业');

-- 3. 专注记录表 (focus_record)
-- 关联张伟的“完成毕业论文第三章”（task_focus_id=1）
INSERT INTO `focus_record` (`campus_user_id`, `task_focus_id`, `focus_start_timestamp`, `focus_end_timestamp`, `focus_duration_seconds`, `focus_type_enum`, `focus_status_enum`, `focus_interruption_count`) VALUES
(1, 1, '2025-10-03 09:00:00', '2025-10-03 09:25:00', 1500, 'pomodoro', 'completed', 0),
(1, 1, '2025-10-03 09:30:00', '2025-10-03 09:55:00', 1500, 'pomodoro', 'completed', 1),
-- 关联李静的“准备四级英语单词”（task_focus_id=3）
(2, 3, '2025-10-02 10:00:00', '2025-10-02 10:30:00', 1800, 'custom', 'completed', 0);

-- 4. 习惯追踪表 (habit_track)
INSERT INTO `habit_track` (`campus_user_id`, `habit_name_text`, `habit_frequency_enum`, `habit_reminder_time`, `habit_streak_count`, `habit_total_count`, `habit_color_hex`) VALUES
(1, '每天阅读半小时', 'daily', '21:30:00', 15, 102, '#409EFF'),
(1, '每周健身3次', 'weekly', '18:00:00', 4, 30, '#67C23A'),
(2, '坚持早起', 'daily', '06:30:00', 7, 50, '#E6A23C');

-- 5. 习惯打卡表 (habit_checkin)
-- 关联张伟的“每天阅读半小时”（habit_track_id=1）
INSERT INTO `habit_checkin` (`habit_track_id`, `checkin_date`, `checkin_note_text`) VALUES
(1, '2025-10-01', '读了《三体》第一章'),
(1, '2025-10-02', '继续读《三体》'),
-- 关联李静的“坚持早起”（habit_track_id=3）
(3, '2025-09-26', '成功早起！'),
(3, '2025-09-27', '又是元气满满的一天'),
(3, '2025-09-28', '周末也没有赖床'),
(3, '2025-09-29', '坚持就是胜利'),
(3, '2025-09-30', 'Good morning'),
(3, '2025-10-01', '国庆节快乐！'),
(3, '2025-10-02', '连续7天达成！');

-- 6. 成就徽章表 (achievement)
INSERT INTO `achievement` (`achievement_id`, `achieve_name_text`, `achieve_description_text`, `achieve_icon_url`, `achieve_rule_text`, `achieve_type_enum`) VALUES
(1, '早起鸟', '连续7天在早上7点前完成早起打卡', 'https://example.com/badges/early_bird.png', '连续7天早起', 'habit'),
(2, '专注大师', '累计完成50个番茄钟（25分钟专注）', 'https://example.com/badges/focus_master.png', '完成50个番茄钟', 'focus'),
(3, '任务达人', '成功完成10个高优先级的任务', 'https://example.com/badges/task_expert.png', '完成10个高优任务', 'task');

-- 7. 用户成就关联表 (user_achieve_rel)
-- 李静获得了“早起鸟”徽章
INSERT INTO `user_achieve_rel` (`campus_user_id`, `achievement_id`, `achieve_condition_text`) VALUES
(2, 1, '于2025-10-02达成连续7天早起'),
-- 张伟获得了“专注大师”徽章
(1, 2, '于2025-10-03累计完成50个番茄钟');

-- 8. 资源分类表 (resource_category)
INSERT INTO `resource_category` (`category_name_text`, `category_description_text`, `category_order_number`) VALUES
('官方推荐', '学校官方推荐的核心学习资源', 1),
('学科论坛', '各大学科领域的专业交流论坛', 2),
('慕课平台', '国内外知名的在线课程学习网站', 3);

-- 9. 学习资源表 (learn_resource)
INSERT INTO `learn_resource` (`resource_category_id`, `resource_name_text`, `resource_url_text`, `resource_description_text`, `resource_recommend_level`) VALUES
(1, '学校图书馆', 'https://lib.university.edu.cn', '我校图书馆官网，内含丰富的电子期刊和图书资源。', 5),
(1, '教务系统', 'https://jwc.university.edu.cn', '查询成绩、课表和选课。', 5),
(2, 'CSDN', 'https://www.csdn.net/', '专业的中文IT技术社区。', 4),
(3, '中国大学MOOC', 'https://www.icourse163.org/', '汇集国内顶尖高校的在线课程。', 4);

-- 10. 校园好友表 (campus_friend)
-- 张伟和李静是好友
INSERT INTO `campus_friend` (`campus_user_id`, `friend_user_id`, `friend_status_enum`) VALUES
(1, 2, 'accepted'),
(2, 1, 'accepted'),
-- 王磊向张伟发起了好友请求
(3, 1, 'pending');

-- 11. 用户设置表 (user_setting)
INSERT INTO `user_setting` (`campus_user_id`, `setting_theme_enum`, `setting_default_focus_mins`, `setting_default_break_mins`, `setting_privacy_level`, `setting_daily_reminder_time`) VALUES
(1, 'dark', 30, 5, 1, '22:00:00'),
(2, 'light', 25, 5, 0, '21:00:00');

-- 12. 通知中心表 (notification_center)
INSERT INTO `notification_center` (`campus_user_id`, `notification_type_enum`, `notification_title_text`, `notification_content_text`, `notification_action_url`) VALUES
(1, 'task_reminder', '任务即将到期', '您的任务“学习Vue3新特性”将在今天晚上22:00截止。', '/task/2'),
(1, 'friend_request', '新的好友请求', '用户“王磊”请求添加您为好友。', '/friends'),
(2, 'system', '系统更新通知', '自律引导系统V1.1版本已发布，新增了年度数据报告功能。', '/settings/update-log');
