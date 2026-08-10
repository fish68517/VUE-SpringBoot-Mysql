package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.platform.entity.Region;
import com.charging.platform.mapper.RegionMapper;
import com.charging.platform.vo.RegionTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionMapper regionMapper;

    public List<RegionTreeVO> getRegionTree() {
        List<Region> regions = regionMapper.selectList(new LambdaQueryWrapper<Region>()
                .eq(Region::getStatus, 1)
                .orderByAsc(Region::getSortNo, Region::getId));

        Map<Long, RegionTreeVO> nodeMap = new LinkedHashMap<>();
        for (Region region : regions) {
            RegionTreeVO node = new RegionTreeVO();
            node.setId(region.getId());
            node.setParentId(region.getParentId());
            node.setRegionCode(region.getRegionCode());
            node.setRegionName(region.getRegionName());
            node.setRegionLevel(region.getRegionLevel());
            node.setSortNo(region.getSortNo());
            nodeMap.put(node.getId(), node);
        }

        List<RegionTreeVO> roots = new ArrayList<>();
        for (RegionTreeVO node : nodeMap.values()) {
            RegionTreeVO parent = nodeMap.get(node.getParentId());
            if (parent == null) {
                roots.add(node);
            } else {
                parent.getChildren().add(node);
            }
        }
        sortNodes(roots);
        return roots;
    }

    private void sortNodes(List<RegionTreeVO> nodes) {
        nodes.sort(Comparator.comparing(RegionTreeVO::getSortNo, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RegionTreeVO::getId));
        nodes.forEach(node -> sortNodes(node.getChildren()));
    }
}
