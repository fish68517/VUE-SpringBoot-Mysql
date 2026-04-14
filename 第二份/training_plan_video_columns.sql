ALTER TABLE training_plan
    ADD COLUMN IF NOT EXISTS video_url VARCHAR(500) NULL COMMENT '训练计划视频访问地址';

ALTER TABLE training_plan
    ADD COLUMN IF NOT EXISTS video_name VARCHAR(255) NULL COMMENT '训练计划视频原始文件名';
