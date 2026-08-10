package com.charging.platform.service;

import com.charging.platform.mapper.HomeMapper;
import com.charging.platform.vo.HomeOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;

    public HomeOverviewVO getOverview() {
        return homeMapper.selectOverview();
    }
}
