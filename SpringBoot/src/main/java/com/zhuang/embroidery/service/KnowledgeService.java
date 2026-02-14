package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.KnowledgeCreateRequest;
import com.zhuang.embroidery.dto.KnowledgeListResponse;
import com.zhuang.embroidery.dto.KnowledgeResponse;
import com.zhuang.embroidery.dto.KnowledgeUpdateRequest;
import com.zhuang.embroidery.entity.Knowledge;
import com.zhuang.embroidery.repository.KnowledgeRepository;
import com.zhuang.embroidery.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;

    /**
     * 获取知识文章列表（支持分类、分页）
     *
     * @param category 分类（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 知识文章列表响应
     */
    public KnowledgeListResponse getKnowledgeList(String category, Integer pageNum, Integer pageSize) {
        log.info("获取知识文章列表: category={}, pageNum={}, pageSize={}", category, pageNum, pageSize);

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize());

        // 查询知识文章
        Page<Knowledge> page;
        if (category != null && !category.trim().isEmpty()) {
            page = knowledgeRepository.findByCategory(category, pageable);
        } else {
            page = knowledgeRepository.findAll(pageable);
        }

        // 构建响应
        List<KnowledgeResponse> items = page.getContent()
                .stream()
                .map(KnowledgeResponse::fromKnowledge)
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return KnowledgeListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 获取知识文章详情
     *
     * @param knowledgeId 知识ID
     * @return 知识文章响应
     * @throws IllegalArgumentException 当知识文章不存在时抛出
     */
    public KnowledgeResponse getKnowledgeDetail(Long knowledgeId) {
        log.info("获取知识文章详情: knowledgeId={}", knowledgeId);

        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> {
                    log.warn("知识文章不存在: knowledgeId={}", knowledgeId);
                    return new IllegalArgumentException("知识文章不存在");
                });

        return KnowledgeResponse.fromKnowledge(knowledge);
    }

    /**
     * 创建知识文章
     *
     * @param request 知识创建请求
     * @return 知识文章响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public KnowledgeResponse createKnowledge(KnowledgeCreateRequest request) {
        log.info("创建知识文章: title={}", request.getTitle());

        // 验证参数
        validateKnowledgeRequest(request);

        // 创建新知识文章
        Knowledge knowledge = Knowledge.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .author(request.getAuthor())
                .viewCount(0)
                .build();

        knowledge = knowledgeRepository.save(knowledge);
        log.info("知识文章创建成功: knowledgeId={}, title={}", knowledge.getId(), knowledge.getTitle());

        return KnowledgeResponse.fromKnowledge(knowledge);
    }

    /**
     * 编辑知识文章
     *
     * @param knowledgeId 知识ID
     * @param request 知识更新请求
     * @return 知识文章响应
     * @throws IllegalArgumentException 当知识文章不存在或参数验证失败时抛出
     */
    @Transactional
    public KnowledgeResponse updateKnowledge(Long knowledgeId, KnowledgeUpdateRequest request) {
        log.info("编辑知识文章: knowledgeId={}", knowledgeId);

        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> {
                    log.warn("知识文章不存在: knowledgeId={}", knowledgeId);
                    return new IllegalArgumentException("知识文章不存在");
                });

        // 验证参数
        validateKnowledgeUpdateRequest(request);

        // 更新字段
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            knowledge.setTitle(request.getTitle());
        }
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            knowledge.setContent(request.getContent());
        }
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            knowledge.setCategory(request.getCategory());
        }
        if (request.getAuthor() != null && !request.getAuthor().trim().isEmpty()) {
            knowledge.setAuthor(request.getAuthor());
        }

        knowledge = knowledgeRepository.save(knowledge);
        log.info("知识文章编辑成功: knowledgeId={}", knowledgeId);

        return KnowledgeResponse.fromKnowledge(knowledge);
    }

    /**
     * 删除知识文章
     *
     * @param knowledgeId 知识ID
     * @throws IllegalArgumentException 当知识文章不存在时抛出
     */
    @Transactional
    public void deleteKnowledge(Long knowledgeId) {
        log.info("删除知识文章: knowledgeId={}", knowledgeId);

        if (!knowledgeRepository.existsById(knowledgeId)) {
            log.warn("知识文章不存在: knowledgeId={}", knowledgeId);
            throw new IllegalArgumentException("知识文章不存在");
        }

        knowledgeRepository.deleteById(knowledgeId);
        log.info("知识文章删除成功: knowledgeId={}", knowledgeId);
    }

    /**
     * 记录知识文章浏览（增加浏览计数）
     *
     * @param knowledgeId 知识ID
     * @throws IllegalArgumentException 当知识文章不存在时抛出
     */
    @Transactional
    public void recordKnowledgeView(Long knowledgeId) {
        log.info("记录知识文章浏览: knowledgeId={}", knowledgeId);

        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> {
                    log.warn("知识文章不存在: knowledgeId={}", knowledgeId);
                    return new IllegalArgumentException("知识文章不存在");
                });

        knowledge.setViewCount(knowledge.getViewCount() + 1);
        knowledgeRepository.save(knowledge);
        log.debug("知识文章浏览计数已更新: knowledgeId={}, viewCount={}", knowledgeId, knowledge.getViewCount());
    }

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    public List<String> getAllCategories() {
        log.info("获取所有分类");
        return knowledgeRepository.findAllCategories();
    }

    /**
     * 验证知识创建请求参数
     */
    private void validateKnowledgeRequest(KnowledgeCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }

        if (request.getTitle().length() > 255) {
            throw new IllegalArgumentException("文章标题长度不能超过255个字符");
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("文章内容不能为空");
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("分类不能为空");
        }

        if (request.getAuthor() == null || request.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("作者不能为空");
        }
    }

    /**
     * 验证知识更新请求参数
     */
    private void validateKnowledgeUpdateRequest(KnowledgeUpdateRequest request) {
        if (request.getTitle() != null && request.getTitle().length() > 255) {
            throw new IllegalArgumentException("文章标题长度不能超过255个字符");
        }
    }

}
