package com.tourism.service;

import com.tourism.entity.Attraction;
import com.tourism.entity.AttractionTag;
import com.tourism.exception.BusinessException;
import com.tourism.repository.AttractionRepository;
import com.tourism.repository.AttractionTagRepository;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 景点业务逻辑服务
 */
@Service
public class AttractionService {
    
    private static final Logger logger = LoggerUtil.getLogger(AttractionService.class);
    
    @Autowired
    private AttractionRepository attractionRepository;
    
    @Autowired
    private AttractionTagRepository attractionTagRepository;
    
    /**
     * 获取景点列表（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 景点分页数据
     */
    public Page<Attraction> getAttractionList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attraction> attractions = attractionRepository.findAll(pageable);
        LoggerUtil.info(logger, "获取景点列表成功 - 页码: " + page + ", 每页数量: " + size);
        return attractions;
    }
    
    /**
     * 根据关键词搜索景点（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 景点分页数据
     */
    public Page<Attraction> searchAttractions(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attraction> attractions = attractionRepository.searchByKeyword(keyword, pageable);
        
        LoggerUtil.info(logger, "搜索景点成功 - 关键词: " + keyword + ", 页码: " + page);
        return attractions;
    }
    
    /**
     * 根据标签查询景点（分页）
     * @param tagName 标签名称
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 景点分页数据
     */
    public Page<Attraction> getAttractionsByTag(String tagName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attraction> attractions = attractionRepository.findByTagName(tagName, pageable);
        
        LoggerUtil.info(logger, "按标签查询景点成功 - 标签: " + tagName + ", 页码: " + page);
        return attractions;
    }
    
    /**
     * 获取广州特色景点（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 景点分页数据
     */
    public Page<Attraction> getGuangzhouSpecialAttractions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attraction> attractions = attractionRepository.findByIsGuangzhouSpecialTrue(pageable);
        
        LoggerUtil.info(logger, "获取广州特色景点成功 - 页码: " + page);
        return attractions;
    }
    
    /**
     * 根据ID查询景点详情
     * @param id 景点ID
     * @return 景点对象
     */
    public Attraction getAttractionDetail(Long id) {
        Optional<Attraction> attractionOptional = attractionRepository.findById(id);
        if (!attractionOptional.isPresent()) {
            LoggerUtil.warn(logger, "获取景点详情失败：景点不存在 - ID: " + id);
            throw new BusinessException("景点不存在");
        }
        
        LoggerUtil.info(logger, "获取景点详情成功 - ID: " + id);
        return attractionOptional.get();
    }
    
    /**
     * 获取景点的所有标签
     * @param attractionId 景点ID
     * @return 标签列表
     */
    public List<AttractionTag> getAttractionTags(Long attractionId) {
        List<AttractionTag> tags = attractionTagRepository.findByAttractionId(attractionId);
        LoggerUtil.info(logger, "获取景点标签成功 - 景点ID: " + attractionId + ", 标签数: " + tags.size());
        return tags;
    }
    
    /**
     * 创建景点（管理员功能）
     * @param name 景点名称
     * @param description 景点描述
     * @param location 位置
     * @param ticketPrice 门票价格
     * @param openingHours 营业时间
     * @param imageUrl 景点图片
     * @param isGuangzhouSpecial 是否为广州特色
     * @return 创建的景点对象
     */
    public Attraction createAttraction(String name, String description, String location,
                                      java.math.BigDecimal ticketPrice, String openingHours,
                                      String imageUrl, Boolean isGuangzhouSpecial) {
        // 检查景点名称是否已存在
        if (attractionRepository.existsByName(name)) {
            LoggerUtil.warn(logger, "创建景点失败：景点名称已存在 - " + name);
            throw new BusinessException("景点名称已存在");
        }
        
        // 创建新景点
        Attraction attraction = new Attraction();
        attraction.setName(name);
        attraction.setDescription(description);
        attraction.setLocation(location);
        attraction.setTicketPrice(ticketPrice);
        attraction.setOpeningHours(openingHours);
        attraction.setImageUrl(imageUrl);
        attraction.setIsGuangzhouSpecial(isGuangzhouSpecial != null ? isGuangzhouSpecial : false);
        
        // 保存景点
        Attraction savedAttraction = attractionRepository.save(attraction);
        LoggerUtil.info(logger, "创建景点成功 - 景点名称: " + name);
        
        return savedAttraction;
    }
    
    /**
     * 更新景点信息（管理员功能）
     * @param id 景点ID
     * @param name 景点名称
     * @param description 景点描述
     * @param location 位置
     * @param ticketPrice 门票价格
     * @param openingHours 营业时间
     * @param imageUrl 景点图片
     * @param isGuangzhouSpecial 是否为广州特色
     * @return 更新后的景点对象
     */
    public Attraction updateAttraction(Long id, String name, String description, String location,
                                      java.math.BigDecimal ticketPrice, String openingHours,
                                      String imageUrl, Boolean isGuangzhouSpecial) {
        Optional<Attraction> attractionOptional = attractionRepository.findById(id);
        if (!attractionOptional.isPresent()) {
            LoggerUtil.warn(logger, "更新景点失败：景点不存在 - ID: " + id);
            throw new BusinessException("景点不存在");
        }
        
        Attraction attraction = attractionOptional.get();
        
        // 检查新名称是否已被其他景点使用
        if (name != null && !name.isEmpty() && !name.equals(attraction.getName())) {
            if (attractionRepository.existsByName(name)) {
                LoggerUtil.warn(logger, "更新景点失败：景点名称已存在 - " + name);
                throw new BusinessException("景点名称已存在");
            }
        }
        
        // 更新景点信息
        if (name != null && !name.isEmpty()) {
            attraction.setName(name);
        }
        if (description != null) {
            attraction.setDescription(description);
        }
        if (location != null && !location.isEmpty()) {
            attraction.setLocation(location);
        }
        if (ticketPrice != null) {
            attraction.setTicketPrice(ticketPrice);
        }
        if (openingHours != null && !openingHours.isEmpty()) {
            attraction.setOpeningHours(openingHours);
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            attraction.setImageUrl(imageUrl);
        }
        if (isGuangzhouSpecial != null) {
            attraction.setIsGuangzhouSpecial(isGuangzhouSpecial);
        }
        
        Attraction updatedAttraction = attractionRepository.save(attraction);
        LoggerUtil.info(logger, "更新景点成功 - ID: " + id);
        
        return updatedAttraction;
    }
    
    /**
     * 删除景点（管理员功能）
     * @param id 景点ID
     */
    public void deleteAttraction(Long id) {
        Optional<Attraction> attractionOptional = attractionRepository.findById(id);
        if (!attractionOptional.isPresent()) {
            LoggerUtil.warn(logger, "删除景点失败：景点不存在 - ID: " + id);
            throw new BusinessException("景点不存在");
        }
        
        Attraction attraction = attractionOptional.get();
        
        // 删除景点的所有标签
        attractionTagRepository.deleteByAttractionId(id);
        
        // 删除景点
        attractionRepository.deleteById(id);
        LoggerUtil.info(logger, "删除景点成功 - ID: " + id + ", 景点名称: " + attraction.getName());
    }
    
    /**
     * 添加景点标签（管理员功能）
     * @param attractionId 景点ID
     * @param tagName 标签名称
     * @return 创建的标签对象
     */
    public AttractionTag addTag(Long attractionId, String tagName) {
        // 检查景点是否存在
        if (!attractionRepository.existsById(attractionId)) {
            LoggerUtil.warn(logger, "添加标签失败：景点不存在 - ID: " + attractionId);
            throw new BusinessException("景点不存在");
        }
        
        // 检查标签是否已存在
        if (attractionTagRepository.existsByAttractionIdAndTagName(attractionId, tagName)) {
            LoggerUtil.warn(logger, "添加标签失败：标签已存在 - 景点ID: " + attractionId + ", 标签: " + tagName);
            throw new BusinessException("标签已存在");
        }
        
        // 创建新标签
        AttractionTag tag = new AttractionTag();
        tag.setAttractionId(attractionId);
        tag.setTagName(tagName);
        
        AttractionTag savedTag = attractionTagRepository.save(tag);
        LoggerUtil.info(logger, "添加标签成功 - 景点ID: " + attractionId + ", 标签: " + tagName);
        
        return savedTag;
    }
    
    /**
     * 删除景点标签（管理员功能）
     * @param attractionId 景点ID
     * @param tagName 标签名称
     */
    public void removeTag(Long attractionId, String tagName) {
        // 检查景点是否存在
        if (!attractionRepository.existsById(attractionId)) {
            LoggerUtil.warn(logger, "删除标签失败：景点不存在 - ID: " + attractionId);
            throw new BusinessException("景点不存在");
        }
        
        // 检查标签是否存在
        if (!attractionTagRepository.existsByAttractionIdAndTagName(attractionId, tagName)) {
            LoggerUtil.warn(logger, "删除标签失败：标签不存在 - 景点ID: " + attractionId + ", 标签: " + tagName);
            throw new BusinessException("标签不存在");
        }
        
        // 删除标签
        List<AttractionTag> tags = attractionTagRepository.findByAttractionId(attractionId);
        for (AttractionTag tag : tags) {
            if (tag.getTagName().equals(tagName)) {
                attractionTagRepository.deleteById(tag.getId());
                break;
            }
        }
        
        LoggerUtil.info(logger, "删除标签成功 - 景点ID: " + attractionId + ", 标签: " + tagName);
    }
}
