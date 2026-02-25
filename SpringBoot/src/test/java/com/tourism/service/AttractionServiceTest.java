package com.tourism.service;

import com.tourism.entity.Attraction;
import com.tourism.entity.AttractionTag;
import com.tourism.exception.BusinessException;
import com.tourism.repository.AttractionRepository;
import com.tourism.repository.AttractionTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 景点服务单元测试
 */
@ExtendWith(MockitoExtension.class)
public class AttractionServiceTest {
    
    @Mock
    private AttractionRepository attractionRepository;
    
    @Mock
    private AttractionTagRepository attractionTagRepository;
    
    @InjectMocks
    private AttractionService attractionService;
    
    @Test
    public void testGetAttractionsByTagSuccess() {
        // 准备测试数据
        Attraction attraction1 = new Attraction();
        attraction1.setId(1L);
        attraction1.setName("陈家祠");
        attraction1.setTicketPrice(new BigDecimal("10.00"));
        
        Attraction attraction2 = new Attraction();
        attraction2.setId(2L);
        attraction2.setName("黄埔古港");
        attraction2.setTicketPrice(new BigDecimal("20.00"));
        
        List<Attraction> attractions = Arrays.asList(attraction1, attraction2);
        Page<Attraction> mockPage = new PageImpl<>(attractions, PageRequest.of(0, 10), 2);
        
        when(attractionRepository.findByTagName("文化", PageRequest.of(0, 10)))
            .thenReturn(mockPage);
        
        // 执行按标签查询
        Page<Attraction> result = attractionService.getAttractionsByTag("文化", 0, 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        
        // 验证findByTagName方法被调用
        verify(attractionRepository, times(1)).findByTagName("文化", PageRequest.of(0, 10));
    }
    
    @Test
    public void testGetAttractionsByTagEmpty() {
        // 准备空结果
        Page<Attraction> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        
        when(attractionRepository.findByTagName("不存在的标签", PageRequest.of(0, 10)))
            .thenReturn(emptyPage);
        
        // 执行按标签查询
        Page<Attraction> result = attractionService.getAttractionsByTag("不存在的标签", 0, 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
    }
    
    @Test
    public void testGetGuangzhouSpecialAttractionsSuccess() {
        // 准备测试数据
        Attraction attraction1 = new Attraction();
        attraction1.setId(1L);
        attraction1.setName("陈家祠");
        attraction1.setIsGuangzhouSpecial(true);
        
        Attraction attraction2 = new Attraction();
        attraction2.setId(2L);
        attraction2.setName("珠江夜游");
        attraction2.setIsGuangzhouSpecial(true);
        
        List<Attraction> attractions = Arrays.asList(attraction1, attraction2);
        Page<Attraction> mockPage = new PageImpl<>(attractions, PageRequest.of(0, 10), 2);
        
        when(attractionRepository.findByIsGuangzhouSpecialTrue(PageRequest.of(0, 10)))
            .thenReturn(mockPage);
        
        // 执行获取广州特色景点
        Page<Attraction> result = attractionService.getGuangzhouSpecialAttractions(0, 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().get(0).getIsGuangzhouSpecial());
        
        // 验证findByIsGuangzhouSpecialTrue方法被调用
        verify(attractionRepository, times(1)).findByIsGuangzhouSpecialTrue(PageRequest.of(0, 10));
    }
    
    @Test
    public void testAddTagSuccess() {
        // 准备测试数据
        Long attractionId = 1L;
        String tagName = "文化";
        
        when(attractionRepository.existsById(attractionId)).thenReturn(true);
        when(attractionTagRepository.existsByAttractionIdAndTagName(attractionId, tagName))
            .thenReturn(false);
        
        AttractionTag mockTag = new AttractionTag();
        mockTag.setId(1L);
        mockTag.setAttractionId(attractionId);
        mockTag.setTagName(tagName);
        
        when(attractionTagRepository.save(any(AttractionTag.class))).thenReturn(mockTag);
        
        // 执行添加标签
        AttractionTag result = attractionService.addTag(attractionId, tagName);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(attractionId, result.getAttractionId());
        assertEquals(tagName, result.getTagName());
        
        // 验证save方法被调用
        verify(attractionTagRepository, times(1)).save(any(AttractionTag.class));
    }
    
    @Test
    public void testAddTagWithNonexistentAttraction() {
        // 设置景点不存在
        when(attractionRepository.existsById(999L)).thenReturn(false);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            attractionService.addTag(999L, "文化");
        });
        
        // 验证save方法未被调用
        verify(attractionTagRepository, never()).save(any(AttractionTag.class));
    }
    
    @Test
    public void testAddTagWithDuplicateTag() {
        // 准备测试数据
        Long attractionId = 1L;
        String tagName = "文化";
        
        when(attractionRepository.existsById(attractionId)).thenReturn(true);
        when(attractionTagRepository.existsByAttractionIdAndTagName(attractionId, tagName))
            .thenReturn(true);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            attractionService.addTag(attractionId, tagName);
        });
        
        // 验证save方法未被调用
        verify(attractionTagRepository, never()).save(any(AttractionTag.class));
    }
    
    @Test
    public void testRemoveTagSuccess() {
        // 准备测试数据
        Long attractionId = 1L;
        String tagName = "文化";
        
        when(attractionRepository.existsById(attractionId)).thenReturn(true);
        when(attractionTagRepository.existsByAttractionIdAndTagName(attractionId, tagName))
            .thenReturn(true);
        
        AttractionTag mockTag = new AttractionTag();
        mockTag.setId(1L);
        mockTag.setAttractionId(attractionId);
        mockTag.setTagName(tagName);
        
        when(attractionTagRepository.findByAttractionId(attractionId))
            .thenReturn(Arrays.asList(mockTag));
        
        // 执行删除标签
        attractionService.removeTag(attractionId, tagName);
        
        // 验证deleteById方法被调用
        verify(attractionTagRepository, times(1)).deleteById(1L);
    }
    
    @Test
    public void testRemoveTagWithNonexistentAttraction() {
        // 设置景点不存在
        when(attractionRepository.existsById(999L)).thenReturn(false);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            attractionService.removeTag(999L, "文化");
        });
        
        // 验证deleteById方法未被调用
        verify(attractionTagRepository, never()).deleteById(any());
    }
    
    @Test
    public void testRemoveTagWithNonexistentTag() {
        // 准备测试数据
        Long attractionId = 1L;
        String tagName = "不存在的标签";
        
        when(attractionRepository.existsById(attractionId)).thenReturn(true);
        when(attractionTagRepository.existsByAttractionIdAndTagName(attractionId, tagName))
            .thenReturn(false);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            attractionService.removeTag(attractionId, tagName);
        });
        
        // 验证deleteById方法未被调用
        verify(attractionTagRepository, never()).deleteById(any());
    }
    
    @Test
    public void testGetAttractionTagsSuccess() {
        // 准备测试数据
        Long attractionId = 1L;
        
        AttractionTag tag1 = new AttractionTag();
        tag1.setId(1L);
        tag1.setAttractionId(attractionId);
        tag1.setTagName("文化");
        
        AttractionTag tag2 = new AttractionTag();
        tag2.setId(2L);
        tag2.setAttractionId(attractionId);
        tag2.setTagName("历史");
        
        when(attractionTagRepository.findByAttractionId(attractionId))
            .thenReturn(Arrays.asList(tag1, tag2));
        
        // 执行获取景点标签
        List<AttractionTag> result = attractionService.getAttractionTags(attractionId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("文化", result.get(0).getTagName());
        assertEquals("历史", result.get(1).getTagName());
        
        // 验证findByAttractionId方法被调用
        verify(attractionTagRepository, times(1)).findByAttractionId(attractionId);
    }
    
    @Test
    public void testGetAttractionTagsEmpty() {
        // 准备测试数据
        Long attractionId = 1L;
        
        when(attractionTagRepository.findByAttractionId(attractionId))
            .thenReturn(Arrays.asList());
        
        // 执行获取景点标签
        List<AttractionTag> result = attractionService.getAttractionTags(attractionId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
