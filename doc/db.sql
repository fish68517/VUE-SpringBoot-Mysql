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
