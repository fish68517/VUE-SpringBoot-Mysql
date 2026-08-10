package com.charging.platform.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegionTreeVO {
    private Long id;
    private Long parentId;
    private String regionCode;
    private String regionName;
    private Integer regionLevel;
    private Integer sortNo;
    private List<RegionTreeVO> children = new ArrayList<>();
}
