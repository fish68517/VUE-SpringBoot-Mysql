package com.submission.service;

import com.submission.dto.MessageDTO;
import com.submission.entity.Message;
import com.submission.entity.User;
import com.submission.mapper.MessageMapper;
import com.submission.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Message Service - Business logic for message operations
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    /**
     * Get message by id
     */
    public MessageDTO getMessageById(Long messageId) {
        Message message = messageMapper.findById(messageId);
        if (message == null) {
            throw new RuntimeException("Message not found");
        }
        return convertToDTO(message);
    }

    /**
     * Get all messages for a recipient (author)
     */
    public List<MessageDTO> getMessagesByRecipient(Long recipientId) {
        List<Message> messages = messageMapper.findByRecipientId(recipientId);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark a message as read
     */
    public void markMessageAsRead(Long messageId) {
        Message message = messageMapper.findById(messageId);
        if (message == null) {
            throw new RuntimeException("Message not found");
        }
        messageMapper.updateReadStatus(messageId, true);
    }

    /**
     * Send a message to a recipient
     */
    public MessageDTO sendMessage(Long senderId, Long recipientId, String content, String type, Long manuscriptId) {
        Message message = Message.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .manuscriptId(manuscriptId)
                .content(content)
                .type(type)
                .isRead(false)
                .build();

        System.out.println("Message: " + message);
        messageMapper.insert(message);
        return convertToDTO(message);
    }

    /**
     * Get unread message count for a recipient
     */
    public int getUnreadMessageCount(Long recipientId) {
        return messageMapper.countUnreadMessages(recipientId);
    }

    /**
     * Get messages between editor and author
     */
    public List<MessageDTO> getMessagesWithAuthor(Long editorId, Long authorId) {
        List<Message> messages = messageMapper.findMessagesBetweenUsers(editorId, authorId);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get messages between editor and reviewer
     */
    public List<MessageDTO> getMessagesWithReviewer(Long editorId, Long reviewerId) {
        List<Message> messages = messageMapper.findMessagesBetweenUsers(editorId, reviewerId);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Send message from editor to author
     */
    public MessageDTO sendMessageToAuthor(Long editorId, Long authorId, String content, Long manuscriptId) {
        return sendMessage(editorId, authorId, content, "COMMUNICATION", manuscriptId);
    }

    /**
     * Send message from editor to reviewer
     */
    public MessageDTO sendMessageToReviewer(Long editorId, Long reviewerId, String content, Long manuscriptId) {
        return sendMessage(editorId, reviewerId, content, "COMMUNICATION", manuscriptId);
    }

    /**
     * Convert Message entity to MessageDTO
     */
    private MessageDTO convertToDTO(Message message) {
        User sender = userMapper.findById(message.getSenderId());
        String senderName = sender != null ? sender.getUsername() : "System";

        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .recipientId(message.getRecipientId())
                .manuscriptId(message.getManuscriptId())
                .content(message.getContent())
                .type(message.getType())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
