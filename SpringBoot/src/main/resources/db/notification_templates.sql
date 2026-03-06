-- Insert default notification templates

-- User approval templates
INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('USER_APPROVED', '尊敬的 {username}：\n\n恭喜！您的 {role} 账号注册申请已被批准。\n您现在可以登录系统并开始使用相关功能。\n\n此致\n敬礼\n\n编辑部', 'SUBMISSION', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('USER_REJECTED', '尊敬的 {username}：\n\n很遗憾，您的 {role} 账号注册申请已被驳回。\n如有疑问，请联系系统管理员。\n\n此致\n敬礼\n\n编辑部', 'SUBMISSION', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

-- Manuscript status templates
INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('MANUSCRIPT_REJECTED', '尊敬的作者：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）已被驳回。\n请登录系统查看详细信息。\n\n此致\n敬礼\n\n编辑部', 'REJECTION', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('MANUSCRIPT_REVISION_REQUIRED', '尊敬的作者：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）需要修改。\n请登录系统查看编辑意见并提交修改稿件。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('MANUSCRIPT_ACCEPTED', '尊敬的作者：\n\n恭喜！您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）已被接受。\n我们将尽快安排您的稿件出版。\n\n此致\n敬礼\n\n编辑部', 'ACCEPTANCE', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('MANUSCRIPT_UNDER_REVIEW', '尊敬的作者：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）已通过初审，现已进入专家审稿阶段。\n我们将尽快完成审稿并通知您结果。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

-- Initial review templates
INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('INITIAL_REVIEW_PASS', '尊敬的 {authorName}：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）已通过初审，将进入专家审稿阶段。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('INITIAL_REVIEW_REJECT', '尊敬的 {authorName}：\n\n很遗憾，您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）未通过初审。\n\n编辑意见：\n{opinion}\n\n此致\n敬礼\n\n编辑部', 'REJECTION', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('INITIAL_REVIEW_REVISION', '尊敬的 {authorName}：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）需要修改后重新提交。\n\n编辑意见：\n{opinion}\n\n请登录系统查看详细信息并提交修改稿件。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);

-- Reviewer assignment template
INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) 
VALUES ('REVIEWER_ASSIGNED', '尊敬的 {reviewerName}：\n\n编辑 {editorName} 邀请您审稿。\n稿件标题：《{manuscriptTitle}》\n稿件编号：{manuscriptId}\n\n请登录系统查看稿件详情并提交审稿意见。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', NOW(), NOW())
ON DUPLICATE KEY UPDATE template_content = VALUES(template_content);
