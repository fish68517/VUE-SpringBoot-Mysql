package com.admin.mapper;

import com.admin.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    Comment findById(@Param("id") Long id);

    List<Comment> findByPostId(@Param("postId") Long postId);

    int insert(Comment comment);

    int deleteById(@Param("id") Long id);

    int deleteByPostId(@Param("postId") Long postId);
}
