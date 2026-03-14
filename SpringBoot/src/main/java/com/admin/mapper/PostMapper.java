package com.admin.mapper;

import com.admin.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    Post findById(@Param("id") Long id);

    List<Post> findAll(@Param("offset") int offset, @Param("limit") int limit);

    int countAll();

    int insert(Post post);

    int incrementLikes(@Param("id") Long id);

    int deleteById(@Param("id") Long id);
}
