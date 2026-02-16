package com.zhuang.embroidery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuang.embroidery.dto.VoteCreateRequest;
import com.zhuang.embroidery.dto.VoteListResponse;
import com.zhuang.embroidery.dto.VoteResponse;
import com.zhuang.embroidery.dto.VoteStatisticsResponse;
import com.zhuang.embroidery.dto.VoteSubmitRequest;
import com.zhuang.embroidery.entity.User;
import com.zhuang.embroidery.entity.Vote;
import com.zhuang.embroidery.entity.VoteRecord;
import com.zhuang.embroidery.repository.UserRepository;
import com.zhuang.embroidery.repository.VoteRecordRepository;
import com.zhuang.embroidery.repository.VoteRepository;
import com.zhuang.embroidery.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 投票业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * 获取投票列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 投票列表响应
     */
    public VoteListResponse getVoteList(Integer pageNum, Integer pageSize) {
        log.info("获取投票列表: pageNum={}, pageSize={}", pageNum, pageSize);

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize());

        // 查询所有投票
        Page<Vote> page = voteRepository.findAll(pageable);

        // 构建响应
        List<VoteResponse> items = page.getContent()
                .stream()
                .map(VoteResponse::fromVote)
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return VoteListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 获取投票详情
     *
     * @param voteId 投票ID
     * @return 投票响应
     * @throws IllegalArgumentException 当投票不存在时抛出
     */
    public VoteResponse getVoteDetail(Long voteId) {
        log.info("获取投票详情: voteId={}", voteId);

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> {
                    log.warn("投票不存在: voteId={}", voteId);
                    return new IllegalArgumentException("投票不存在");
                });

        return VoteResponse.fromVote(vote);
    }

    /**
     * 创建投票
     *
     * @param request 投票创建请求
     * @return 投票响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public VoteResponse createVote(VoteCreateRequest request) {
        log.info("创建投票: title={}", request.getTitle());

        // 验证参数
        validateVoteCreateRequest(request);

        // 将选项列表转换为 JSON 字符串
        String optionsJson;
        try {
            optionsJson = objectMapper.writeValueAsString(request.getOptions());
        } catch (Exception e) {
            log.error("投票选项 JSON 序列化失败", e);
            throw new IllegalArgumentException("投票选项格式错误");
        }

        // 创建新投票
        Vote vote = Vote.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .options(optionsJson)
                .status("active")
                .endAt(request.getEndAt())
                .build();

        vote = voteRepository.save(vote);
        log.info("投票创建成功: voteId={}, title={}", vote.getId(), vote.getTitle());

        return VoteResponse.fromVote(vote);
    }

    /**
     * 提交投票
     *
     * @param request 投票提交请求
     * @throws IllegalArgumentException 当参数验证失败或投票不存在时抛出
     */
    @Transactional
    public void submitVote(VoteSubmitRequest request) {
        log.info("提交投票: voteId={}, userId={}, selectedOption={}", request.getVoteId(), request.getUserId(), request.getSelectedOption());

        // 验证参数
        validateVoteSubmitRequest(request);

        // 验证投票存在
        Vote vote = voteRepository.findById(request.getVoteId())
                .orElseThrow(() -> {
                    log.warn("投票不存在: voteId={}", request.getVoteId());
                    return new IllegalArgumentException("投票不存在");
                });

        // 验证投票状态
        if (!"active".equals(vote.getStatus())) {
            log.warn("投票已关闭: voteId={}", request.getVoteId());
            throw new IllegalArgumentException("投票已关闭");
        }

        // 验证投票未过期
        if (vote.getEndAt() != null && LocalDateTime.now().isAfter(vote.getEndAt())) {
            log.warn("投票已过期: voteId={}", request.getVoteId());
            throw new IllegalArgumentException("投票已过期");
        }

        // 验证用户存在
        if (!userRepository.existsById(request.getUserId())) {
            log.warn("用户不存在: userId={}", request.getUserId());
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证用户未投过票
        if (voteRecordRepository.findByVoteIdAndUserId(request.getVoteId(), request.getUserId()).isPresent()) {
            log.warn("用户已投过票: voteId={}, userId={}", request.getVoteId(), request.getUserId());
            // 投过票就更新
              /*  VoteRecord record = voteRecordRepository.findByVoteIdAndUserId(request.getVoteId(), request.getUserId()).get();
                record.setSelectedOption(request.getSelectedOption());
                record.setVoteId(request.getVoteId());*/
                voteRecordRepository.updateSelectedOptionByVoteIdAndUserId(request.getVoteId(), request.getUserId(), request.getSelectedOption());
                log.info("用户投票更新成功: voteId={}, userId={}", request.getVoteId(), request.getUserId());
                return;

        }

        // 验证选项有效
        List<String> options = parseVoteOptions(vote.getOptions());
        if (!options.contains(request.getSelectedOption())) {
            log.warn("无效的投票选项: voteId={}, selectedOption={}", request.getVoteId(), request.getSelectedOption());
            throw new IllegalArgumentException("无效的投票选项");
        }

        // 创建投票记录
        VoteRecord record = VoteRecord.builder()
                .voteId(request.getVoteId())
                .userId(request.getUserId())
                .selectedOption(request.getSelectedOption())
                .build();

        voteRecordRepository.save(record);
        log.info("投票提交成功: voteId={}, userId={}", request.getVoteId(), request.getUserId());
    }

    /**
     * 获取投票统计
     *
     * @param voteId 投票ID
     * @return 投票统计响应
     * @throws IllegalArgumentException 当投票不存在时抛出
     */
    public VoteStatisticsResponse getVoteStatistics(Long voteId) {
        log.info("获取投票统计: voteId={}", voteId);

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> {
                    log.warn("投票不存在: voteId={}", voteId);
                    return new IllegalArgumentException("投票不存在");
                });

        // 获取投票选项
        List<String> options = parseVoteOptions(vote.getOptions());

        // 统计每个选项的投票数
        Map<String, Long> statistics = new HashMap<>();
        for (String option : options) {
            Long count = voteRecordRepository.countByVoteIdAndSelectedOption(voteId, option);
            statistics.put(option, count);
        }

        // 计算总投票数
        Long totalVotes = voteRecordRepository.countByVoteId(voteId);

        return VoteStatisticsResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .options(options)
                .status(vote.getStatus())
                .createdAt(vote.getCreatedAt())
                .endAt(vote.getEndAt())
                .statistics(statistics)
                .totalVotes(totalVotes)
                .build();
    }

    /**
     * 关闭投票
     *
     * @param voteId 投票ID
     * @throws IllegalArgumentException 当投票不存在时抛出
     */
    @Transactional
    public void closeVote(Long voteId) {
        log.info("关闭投票: voteId={}", voteId);

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> {
                    log.warn("投票不存在: voteId={}", voteId);
                    return new IllegalArgumentException("投票不存在");
                });

        vote.setStatus("closed");
        voteRepository.save(vote);
        log.info("投票关闭成功: voteId={}", voteId);
    }

    /**
     * 验证投票创建请求参数
     */
    private void validateVoteCreateRequest(VoteCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("投票标题不能为空");
        }

        if (request.getTitle().length() > 255) {
            throw new IllegalArgumentException("投票标题长度不能超过255个字符");
        }

        if (request.getOptions() == null || request.getOptions().isEmpty()) {
            throw new IllegalArgumentException("投票选项不能为空");
        }

        if (request.getOptions().size() < 2) {
            throw new IllegalArgumentException("投票选项至少需要2个");
        }

        // 验证选项不重复
        if (request.getOptions().size() != request.getOptions().stream().distinct().count()) {
            throw new IllegalArgumentException("投票选项不能重复");
        }
    }

    /**
     * 验证投票提交请求参数
     */
    private void validateVoteSubmitRequest(VoteSubmitRequest request) {
        if (request.getVoteId() == null || request.getVoteId() <= 0) {
            throw new IllegalArgumentException("投票ID不能为空");
        }

        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (request.getSelectedOption() == null || request.getSelectedOption().trim().isEmpty()) {
            throw new IllegalArgumentException("选择的选项不能为空");
        }
    }

    /**
     * 解析投票选项 JSON 字符串
     */
    private List<String> parseVoteOptions(String optionsJson) {
        if (optionsJson == null || optionsJson.isEmpty()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(optionsJson, List.class);
        } catch (Exception e) {
            log.error("投票选项 JSON 反序列化失败", e);
            return List.of();
        }
    }
}
