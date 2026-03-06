package com.submission.service;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Author Service - Business logic for author operations
 */
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final UserMapper userMapper;

    /**
     * Get author personal information
     */
    public User getAuthorInfo(Long authorId) {
        User author = userMapper.findById(authorId);
        if (author == null) {
            throw new RuntimeException("Author not found");
        }
        if (!"AUTHOR".equals(author.getRole())) {
            throw new RuntimeException("User is not an author");
        }
        return author;
    }

    /**
     * Update author personal information
     */
    public User updateAuthorInfo(Long authorId, UserDTO userDTO) {
        User author = userMapper.findById(authorId);
        if (author == null) {
            throw new RuntimeException("Author not found");
        }
        if (!"AUTHOR".equals(author.getRole())) {
            throw new RuntimeException("User is not an author");
        }

        // Update only allowed fields for author
        author.setEmail(userDTO.getEmail());
        author.setPhone(userDTO.getPhone());
        author.setAcademicAchievements(userDTO.getAcademicAchievements());

        userMapper.update(author);
        return author;
    }

    /**
     * Update author password
     */
    public void updateAuthorPassword(Long authorId, String oldPassword, String newPassword) {
        User author = userMapper.findById(authorId);
        if (author == null) {
            throw new RuntimeException("Author not found");
        }
        if (!"AUTHOR".equals(author.getRole())) {
            throw new RuntimeException("User is not an author");
        }

        // Verify old password (plain text comparison as per requirements)
        if (!author.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update password
        author.setPassword(newPassword);
        userMapper.update(author);
    }
}
