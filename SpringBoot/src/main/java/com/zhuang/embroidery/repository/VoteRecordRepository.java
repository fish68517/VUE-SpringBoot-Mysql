package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 投票记录数据访问层接口
 */
@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    /**
     * 根据投票ID查询投票记录列表
     *
     * @param voteId 投票ID
     * @return 投票记录列表
     */
    List<VoteRecord> findByVoteId(Long voteId);

    /**
     * 根据用户ID查询投票记录列表
     *
     * @param userId 用户ID
     * @return 投票记录列表
     */
    List<VoteRecord> findByUserId(Long userId);

    /**
     * 查询用户对某个投票的投票记录
     *
     * @param voteId 投票ID
     * @param userId 用户ID
     * @return 投票记录
     */
    Optional<VoteRecord> findByVoteIdAndUserId(Long voteId, Long userId);

    /**
     * 根据投票ID统计投票记录数
     *
     * @param voteId 投票ID
     * @return 投票记录数
     */
    Long countByVoteId(Long voteId);

    /**
     * 根据投票ID和选项统计投票数
     *
     * @param voteId 投票ID
     * @param selectedOption 选项
     * @return 投票数
     */
    Long countByVoteIdAndSelectedOption(Long voteId, String selectedOption);
}
