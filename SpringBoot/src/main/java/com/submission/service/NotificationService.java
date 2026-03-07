package com.submission.service;

import com.submission.entity.Message;
import com.submission.entity.NotificationTemplate;
import com.submission.entity.User;
import com.submission.entity.Manuscript;
import com.submission.mapper.MessageMapper;
import com.submission.mapper.NotificationTemplateMapper;
import com.submission.mapper.UserMapper;
import com.submission.mapper.ManuscriptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Notification Service - Handles all system notifications
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MessageMapper messageMapper;
    private final NotificationTemplateMapper notificationTemplateMapper;
    private final UserMapper userMapper;
    private final ManuscriptMapper manuscriptMapper;

    /**
     * Send user registration approval notification
     */
    public void sendUserApprovalNotification(Long userId, boolean approved) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String templateName = approved ? "USER_APPROVED" : "USER_REJECTED";
        NotificationTemplate template = notificationTemplateMapper.findByName(templateName);
        
        String content;
        if (template != null) {
            content = template.getTemplateContent()
                    .replace("{username}", user.getUsername())
                    .replace("{role}", user.getRole());
        } else {
            content = generateDefaultUserApprovalNotification(user, approved);
        }

        // Send notification from system (userId = 0 or null for system)
        sendNotification(0L, userId, content, "NOTIFICATION", null);
    }

    /**
     * Send manuscript status change notification
     */
    public void sendManuscriptStatusNotification(Long authorId,Long manuscriptId, String oldStatus, String newStatus) {
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        User author = userMapper.findById(manuscript.getAuthorId());
        if (author == null) {
            throw new RuntimeException("Author not found");
        }

        String templateName = getTemplateNameForStatus(newStatus);
        NotificationTemplate template = notificationTemplateMapper.findByName(templateName);
        
        String content;
        if (template != null) {
            content = template.getTemplateContent()
                    .replace("{manuscriptTitle}", manuscript.getTitle())
                    .replace("{manuscriptId}", manuscript.getId().toString())
                    .replace("{status}", newStatus)
                    .replace("{authorName}", author.getUsername());
        } else {
            content = generateDefaultManuscriptStatusNotification(manuscript, newStatus);
        }

        // Send notification from system to author
        sendNotification(authorId, author.getId(), content, "NOTIFICATION", manuscriptId);
    }

    /**
     * Send reviewer assignment notification
     */
    public void sendReviewerAssignmentNotification(Long reviewerId, Long manuscriptId, Long editorId) {
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }

        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        User editor = userMapper.findById(editorId);
        String editorName = editor != null ? editor.getUsername() : "Editor";

        NotificationTemplate template = notificationTemplateMapper.findByName("REVIEWER_ASSIGNED");
        
        String content;
        if (template != null) {
            content = template.getTemplateContent()
                    .replace("{reviewerName}", reviewer.getUsername())
                    .replace("{manuscriptTitle}", manuscript.getTitle())
                    .replace("{manuscriptId}", manuscript.getId().toString())
                    .replace("{editorName}", editorName);
        } else {
            content = generateDefaultReviewerAssignmentNotification(reviewer, manuscript, editorName);
        }

        // Send notification from editor to reviewer
        sendNotification(editorId, reviewerId, content, "NOTIFICATION", manuscriptId);
    }

    /**
     * Send initial review result notification to author
     */
    public void sendInitialReviewNotification(Long manuscriptId, String reviewStatus, String opinion) {
        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        User author = userMapper.findById(manuscript.getAuthorId());
        if (author == null) {
            throw new RuntimeException("Author not found");
        }

        String templateName = getTemplateNameForInitialReview(reviewStatus);
        NotificationTemplate template = notificationTemplateMapper.findByName(templateName);
        
        String content;
        if (template != null) {
            content = template.getTemplateContent()
                    .replace("{authorName}", author.getUsername())
                    .replace("{manuscriptTitle}", manuscript.getTitle())
                    .replace("{manuscriptId}", manuscript.getId().toString())
                    .replace("{opinion}", opinion != null ? opinion : "");
        } else {
            content = generateDefaultInitialReviewNotification(author, manuscript, reviewStatus, opinion);
        }

        // Send notification from system to author
        sendNotification(0L, author.getId(), content, "NOTIFICATION", manuscriptId);
    }

    /**
     * Send review submission notification to editor
     */
    public void sendReviewSubmissionNotification(Long reviewId, Long manuscriptId, Long reviewerId, Long editorId) {
        User reviewer = userMapper.findById(reviewerId);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }

        Manuscript manuscript = manuscriptMapper.findById(manuscriptId);
        if (manuscript == null) {
            throw new RuntimeException("Manuscript not found");
        }

        String content = generateDefaultReviewSubmissionNotification(reviewer, manuscript);

        // Send notification from reviewer to editor
        sendNotification(reviewerId, editorId, content, "NOTIFICATION", manuscriptId);
    }

    /**
     * Send generic notification
     */
    public void sendNotification(Long senderId, Long recipientId, String content, String type, Long manuscriptId) {
        Message message = Message.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .manuscriptId(manuscriptId)
                .content(content)
                .type(type)
                .isRead(false)
                .build();

        messageMapper.insert(message);
    }

    /**
     * Get template name based on manuscript status
     */
    private String getTemplateNameForStatus(String status) {
        switch (status) {
            case "REJECTED":
                return "MANUSCRIPT_REJECTED";
            case "REVISION_REQUIRED":
                return "MANUSCRIPT_REVISION_REQUIRED";
            case "ACCEPTED":
                return "MANUSCRIPT_ACCEPTED";
            case "UNDER_REVIEW":
                return "MANUSCRIPT_UNDER_REVIEW";
            default:
                return "MANUSCRIPT_STATUS_CHANGED";
        }
    }

    /**
     * Get template name based on initial review status
     */
    private String getTemplateNameForInitialReview(String reviewStatus) {
        switch (reviewStatus) {
            case "PASS":
                return "INITIAL_REVIEW_PASS";
            case "REJECT":
                return "INITIAL_REVIEW_REJECT";
            case "REVISION_REQUIRED":
                return "INITIAL_REVIEW_REVISION";
            default:
                return "INITIAL_REVIEW_COMPLETED";
        }
    }

    /**
     * Generate default user approval notification
     */
    private String generateDefaultUserApprovalNotification(User user, boolean approved) {
        if (approved) {
            return "尊敬的 " + user.getUsername() + "：\n\n" +
                    "恭喜！您的 " + user.getRole() + " 账号注册申请已被批准。\n" +
                    "您现在可以登录系统并开始使用相关功能。\n\n" +
                    "此致\n敬礼\n\n编辑部";
        } else {
            return "尊敬的 " + user.getUsername() + "：\n\n" +
                    "很遗憾，您的 " + user.getRole() + " 账号注册申请已被驳回。\n" +
                    "如有疑问，请联系系统管理员。\n\n" +
                    "此致\n敬礼\n\n编辑部";
        }
    }

    /**
     * Generate default manuscript status notification
     */
    private String generateDefaultManuscriptStatusNotification(Manuscript manuscript, String newStatus) {
        String statusMessage;
        switch (newStatus) {
            case "REJECTED":
                statusMessage = "已被驳回";
                break;
            case "REVISION_REQUIRED":
                statusMessage = "需要修改";
                break;
            case "ACCEPTED":
                statusMessage = "已被接受";
                break;
            case "UNDER_REVIEW":
                statusMessage = "正在审稿";
                break;
            default:
                statusMessage = "状态已更新为 " + newStatus;
        }

        return "尊敬的作者：\n\n" +
                "您的稿件《" + manuscript.getTitle() + "》（编号：" + manuscript.getId() + "）" + statusMessage + "。\n" +
                "请登录系统查看详细信息。\n\n" +
                "此致\n敬礼\n\n编辑部";
    }

    /**
     * Generate default reviewer assignment notification
     */
    private String generateDefaultReviewerAssignmentNotification(User reviewer, Manuscript manuscript, String editorName) {
        return "尊敬的 " + reviewer.getUsername() + "：\n\n" +
                "编辑 " + editorName + " 邀请您审稿。\n" +
                "稿件标题：《" + manuscript.getTitle() + "》\n" +
                "稿件编号：" + manuscript.getId() + "\n\n" +
                "请登录系统查看稿件详情并提交审稿意见。\n\n" +
                "此致\n敬礼\n\n编辑部";
    }

    /**
     * Generate default initial review notification
     */
    private String generateDefaultInitialReviewNotification(User author, Manuscript manuscript, String reviewStatus, String opinion) {
        String statusMessage;
        switch (reviewStatus) {
            case "PASS":
                statusMessage = "已通过初审，将进入专家审稿阶段";
                break;
            case "REJECT":
                statusMessage = "未通过初审";
                break;
            case "REVISION_REQUIRED":
                statusMessage = "需要修改后重新提交";
                break;
            default:
                statusMessage = "初审已完成";
        }

        String content = "尊敬的 " + author.getUsername() + "：\n\n" +
                "您的稿件《" + manuscript.getTitle() + "》（编号：" + manuscript.getId() + "）" + statusMessage + "。\n";
        
        if (opinion != null && !opinion.isEmpty()) {
            content += "\n编辑意见：\n" + opinion + "\n";
        }
        
        content += "\n请登录系统查看详细信息。\n\n" +
                "此致\n敬礼\n\n编辑部";

        return content;
    }

    /**
     * Generate default review submission notification
     */
    private String generateDefaultReviewSubmissionNotification(User reviewer, Manuscript manuscript) {
        return "审稿人 " + reviewer.getUsername() + " 已提交对稿件《" + manuscript.getTitle() + "》（编号：" + 
                manuscript.getId() + "）的审稿意见。\n\n" +
                "请登录系统查看审稿意见。";
    }
}
