package com.postgraduate.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SchoolStatsDTO {
    // 本科层次分布 (例如: {"985": 0.1, "211": 0.3, "DOUBLE_NON": 0.6})
    private Map<String, Double> tierDistribution;
    
    // 四级分数段分布 (例如: {"<425": 0.2, "425-500": 0.5, ">500": 0.3})
    private Map<String, Double> cet4Distribution;
    
    // 总收藏人数 (让用户知道样本量大小)
    private Long totalFavorites;
}