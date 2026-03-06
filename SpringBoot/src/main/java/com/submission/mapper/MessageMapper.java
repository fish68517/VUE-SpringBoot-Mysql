package com.submission.mapper;

import com.submission.entity.Message;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * Message Mapper - Data access layer for Message entity
 */
@Mapper
public interface MessageMapper {

    /**
     * Insert a new message
     */
    @Insert("INSERT INTO messages (sender_id, recipient_id, manuscript_id, content, type, is_read, created_at) " +
            "VALUES (#{senderId}, #{recipientId}, #{manuscriptId}, #{content}, #{type}, #{isRead}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    /**
     * Find message by id
     */
    @Select("SELECT id, sender_id, recipient_id, manuscript_id, content, type, is_read, created_at " +
            "FROM messages WHERE id = #{id}")
    Message findById(Long id);

    /**
     * Find all messages for a recipient, ordered by creation date descending
     */
    @Select("SELECT id, sender_id, recipient_id, manuscript_id, content, type, is_read, created_at " +
            "FROM messages WHERE recipient_id = #{recipientId} ORDER BY created_at DESC")
    List<Message> findByRecipientId(Long recipientId);

    /**
     * Update message read status
     */
    @Update("UPDATE messages SET is_read = #{isRead} WHERE id = #{id}")
    int updateReadStatus(Long id, Boolean isRead);

    /**
     * Count unread messages for a recipient
     */
    @Select("SELECT COUNT(*) FROM messages WHERE recipient_id = #{recipientId} AND is_read = FALSE")
    int countUnreadMessages(Long recipientId);

    /**
     * Find messages between two users (editor and author/reviewer)
     */
    @Select("SELECT id, sender_id, recipient_id, manuscript_id, content, type, is_read, created_at " +
            "FROM messages WHERE (sender_id = #{userId1} AND recipient_id = #{userId2}) " +
            "OR (sender_id = #{userId2} AND recipient_id = #{userId1}) " +
            "ORDER BY created_at DESC")
    List<Message> findMessagesBetweenUsers(Long userId1, Long userId2);

    /**
     * Find messages sent by a specific user to a recipient
     */
    @Select("SELECT id, sender_id, recipient_id, manuscript_id, content, type, is_read, created_at " +
            "FROM messages WHERE sender_id = #{senderId} AND recipient_id = #{recipientId} " +
            "ORDER BY created_at DESC")
    List<Message> findMessagesBySenderToRecipient(Long senderId, Long recipientId);
}
