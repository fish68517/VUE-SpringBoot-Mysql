package com.admin.service;

import com.admin.dto.CommentDTO;
import com.admin.dto.PostDTO;
import com.admin.entity.Comment;
import com.admin.entity.Post;
import com.admin.mapper.CommentMapper;
import com.admin.mapper.PostMapper;
import com.admin.vo.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    public PostDTO getPostById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }

        Post post = postMapper.findById(id);
        if (post == null) {
            return null;
        }

        return convertToDTO(post);
    }

    public PageResponse<PostDTO> getPostList(int page, int pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;
        List<Post> posts = postMapper.findAll(offset, pageSize);
        int total = postMapper.countAll();

        List<PostDTO> postDTOs = posts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(postDTOs, total, page, pageSize);
    }

    public PostDTO createPost(Post post) {
        if (post == null || post.getUserId() == null || post.getUserId() <= 0) {
            return null;
        }
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            return null;
        }

        post.setContent(post.getContent().trim());
        if (post.getLikesCount() == null) {
            post.setLikesCount(0);
        }

        int result = postMapper.insert(post);
        if (result <= 0 || post.getId() == null) {
            return null;
        }
        return getPostById(post.getId());
    }

    public PostDTO likePost(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        if (postMapper.findById(id) == null) {
            return null;
        }

        int result = postMapper.incrementLikes(id);
        if (result <= 0) {
            return null;
        }
        return getPostById(id);
    }

    public boolean deletePost(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        Post existingPost = postMapper.findById(id);
        if (existingPost == null) {
            return false;
        }

        commentMapper.deleteByPostId(id);
        return postMapper.deleteById(id) > 0;
    }

    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setContent(post.getContent());
        dto.setImagePath(post.getImagePath());
        dto.setLikesCount(post.getLikesCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        List<Comment> comments = commentMapper.findByPostId(post.getId());
        List<CommentDTO> commentDTOs = comments.stream()
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList());
        dto.setComments(commentDTOs);
        return dto;
    }

    private CommentDTO convertCommentToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}
