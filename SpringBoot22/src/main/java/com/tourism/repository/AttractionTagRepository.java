package com.tourism.repository;

import com.tourism.entity.AttractionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 景点标签数据访问接口
 */
@Repository
public interface AttractionTagRepository extends JpaRepository<AttractionTag, Long> {
    
    /**
     * 根据景点ID查询所有标签
     * @param attractionId 景点ID
     * @return 标签列表
     */
    List<AttractionTag> findByAttractionId(Long attractionId);
    
    /**
     * 根据标签名称查询所有标签
     * @param tagName 标签名称
     * @return 标签列表
     */
    List<AttractionTag> findByTagName(String tagName);
    
    /**
     * 删除景点的所有标签
     * @param attractionId 景点ID
     */
    void deleteByAttractionId(Long attractionId);
    
    /**
     * 检查景点是否拥有指定标签
     * @param attractionId 景点ID
     * @param tagName 标签名称
     * @return 是否存在
     */
    boolean existsByAttractionIdAndTagName(Long attractionId, String tagName);
}
