ALTER TABLE `training_feedback`
ADD COLUMN `coach_reply_image_urls` text COLLATE utf8mb4_unicode_ci COMMENT '教练回复图片URL' AFTER `reply_at`,
ADD COLUMN `coach_reply_video_urls` text COLLATE utf8mb4_unicode_ci COMMENT '教练回复视频URL' AFTER `coach_reply_image_urls`,
ADD COLUMN `coach_reply_document_urls` text COLLATE utf8mb4_unicode_ci COMMENT '教练回复文档URL' AFTER `coach_reply_video_urls`;