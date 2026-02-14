package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.ViewHistoryListResponse;
import com.zhuang.embroidery.dto.ViewHistoryResponse;
import com.zhuang.embroidery.entity.ViewHistory;
import com.zhuang.embroidery.repository.ViewHistoryRepository;
import com.zhuang.embroidery.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 浏览历史业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewHistoryService {

    private final ViewHistoryRepository viewHistoryRepository;

    /**
     * 记录浏览历史
     *
     * @param userId 用户ID
     * @param contentType 内容类型（artwork/knowledge）
     * @param contentId 内容ID
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public void recordViewHistory(Long userId, String contentType, Long contentId) {
        log.info("记录浏览历史: userId={}, contentType={}, contentId={}", userId, contentType, contentId);

        // 验证参数
        validateViewHistoryRequest(userId, contentType, contentId);

        // 创建浏览历史记录
        ViewHistory viewHistory = ViewHistory.builder()
                .userId(userId)
                .contentType(contentType)
                .contentId(contentId)
                .build();

        viewHistoryRepository.save(viewHistory);
        log.info("浏览历史记录成功: userId={}, contentType={}, contentId={}", userId, contentType, contentId);
    }

    /**
     * 获取用户浏览历史列表（支持分页）
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 浏览历史列表响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    public ViewHistoryListResponse getUserViewHistory(Long userId, Integer pageNum, Integer pageSize) {
        log.info("获取用户浏览历史: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空或小于等于0");
        }

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象，按浏览时间倒序排列
        Pageable pageable = PageRequest.of(
                pageUtil.getPageNum() - 1,
                pageUtil.getPageSize(),
                Sort.by(Sort.Direction.DESC, "viewedAt")
        );

        // 查询浏览历史
        Page<ViewHistory> page = viewHistoryRepository.findByUserId(userId, pageable);

        // 构建响应
        List<ViewHistoryResponse> items = page.getContent()
                .stream()
                .map(ViewHistoryResponse::fromViewHistory)
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return ViewHistoryListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 获取用户特定内容类型的浏览历史列表（支持分页）
     *
     * @param userId 用户ID
     * @param contentType 内容类型（artwork/knowledge）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 浏览历史列表响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    public ViewHistoryListResponse getUserViewHistoryByType(Long userId, String contentType, Integer pageNum, Integer pageSize) {
        log.info("获取用户特定类型浏览历史: userId={}, contentType={}, pageNum={}, pageSize={}", userId, contentType, pageNum, pageSize);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空或小于等于0");
        }

        // 验证内容类型
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("内容类型不能为空");
        }

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象，按浏览时间倒序排列
        Pageable pageable = PageRequest.of(
                pageUtil.getPageNum() - 1,
                pageUtil.getPageSize(),
                Sort.by(Sort.Direction.DESC, "viewedAt")
        );

        // 查询浏览历史
        Page<ViewHistory> page = viewHistoryRepository.findByUserIdAndContentType(userId, contentType, pageable);

        // 构建响应
        List<ViewHistoryResponse> items = page.getContent()
                .stream()
                .map(ViewHistoryResponse::fromViewHistory)
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return ViewHistoryListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 清除用户浏览历史
     *
     * @param userId 用户ID
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public void clearUserViewHistory(Long userId) {
        log.info("清除用户浏览历史: userId={}", userId);

        // 验证用户ID
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空或小于等于0");
        }

        viewHistoryRepository.deleteByUserId(userId);
        log.info("用户浏览历史清除成功: userId={}", userId);
    }

    /**
     * 验证浏览历史记录请求参数
     */
    private void validateViewHistoryRequest(Long userId, String contentType, Long contentId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空或小于等于0");
        }

        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("内容类型不能为空");
        }

        if (!contentType.equals("artwork") && !contentType.equals("knowledge")) {
            throw new IllegalArgumentException("内容类型只能是 artwork 或 knowledge");
        }

        if (contentId == null || contentId <= 0) {
            throw new IllegalArgumentException("内容ID不能为空或小于等于0");
        }
    }

}
