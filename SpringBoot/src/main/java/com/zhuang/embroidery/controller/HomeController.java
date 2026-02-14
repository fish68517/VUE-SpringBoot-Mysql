package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.AnnouncementResponse;
import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.AccessStatisticsResponse;
import com.zhuang.embroidery.dto.CarouselResponse;
import com.zhuang.embroidery.dto.FeatureResponse;
import com.zhuang.embroidery.service.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页相关 API 控制器
 */
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final SystemService systemService;

    /**
     * 获取轮播内容
     *
     * @return 轮播内容列表
     */
    @GetMapping("/carousel")
    public ApiResponse<List<CarouselResponse>> getCarousel() {
        log.info("获取轮播内容");

        List<CarouselResponse> carousels = new ArrayList<>();

        // 轮播 1：壮族刺绣简介
        carousels.add(CarouselResponse.builder()
                .id(1L)
                .title("壮族刺绣文化")
                .description("探索壮族非遗刺绣的精妙工艺与文化内涵")
                .imageUrl("/images/carousel/embroidery-culture.jpg")
                .link("/artworks")
                .order(1)
                .build());

        // 轮播 2：作品展示
        carousels.add(CarouselResponse.builder()
                .id(2L)
                .title("精美作品展示")
                .description("欣赏传统工艺与现代审美的完美结合")
                .imageUrl("/images/carousel/artwork-showcase.jpg")
                .link("/artworks")
                .order(2)
                .build());

        // 轮播 3：知识科普
        carousels.add(CarouselResponse.builder()
                .id(3L)
                .title("非遗知识科普")
                .description("深入了解刺绣技法、历史文化与传承意义")
                .imageUrl("/images/carousel/knowledge-science.jpg")
                .link("/knowledge")
                .order(3)
                .build());

        log.info("成功获取轮播内容，共 {} 条", carousels.size());
        return ApiResponse.success(carousels);
    }

    /**
     * 获取核心功能导航
     *
     * @return 核心功能导航列表
     */
    @GetMapping("/features")
    public ApiResponse<List<FeatureResponse>> getFeatures() {
        log.info("获取核心功能导航");

        List<FeatureResponse> features = new ArrayList<>();

        // 功能 1：作品展示
        features.add(FeatureResponse.builder()
                .id(1L)
                .name("作品展示")
                .description("浏览壮族刺绣作品，了解不同类型和风格")
                .iconUrl("/images/features/artwork-icon.png")
                .link("/artworks")
                .order(1)
                .build());

        // 功能 2：知识科普
        features.add(FeatureResponse.builder()
                .id(2L)
                .name("知识科普")
                .description("学习刺绣技法、历史文化和政策法规")
                .iconUrl("/images/features/knowledge-icon.png")
                .link("/knowledge")
                .order(2)
                .build());

        // 功能 3：互动交流
        features.add(FeatureResponse.builder()
                .id(3L)
                .name("互动交流")
                .description("参与话题讨论、投票和反馈")
                .iconUrl("/images/features/community-icon.png")
                .link("/community")
                .order(3)
                .build());

        // 功能 4：用户中心
        features.add(FeatureResponse.builder()
                .id(4L)
                .name("用户中心")
                .description("管理个人信息、收藏和浏览历史")
                .iconUrl("/images/features/user-icon.png")
                .link("/user")
                .order(4)
                .build());

        log.info("成功获取核心功能导航，共 {} 条", features.size());
        return ApiResponse.success(features);
    }

    /**
     * 获取最新活动和公告
     *
     * @return 最新活动和公告列表
     */
    @GetMapping("/announcements")
    public ApiResponse<List<AnnouncementResponse>> getAnnouncements() {
        log.info("获取最新活动和公告");

        List<AnnouncementResponse> announcements = new ArrayList<>();

        // 公告 1：平台上线
        announcements.add(AnnouncementResponse.builder()
                .id(1L)
                .title("文山壮族刺绣网站平台正式上线")
                .content("欢迎访问文山壮族刺绣网站平台，这是一个专门为传承和展示壮族非遗刺绣文化而设计的综合性网络平台。")
                .type("announcement")
                .createdAt(LocalDateTime.now().minusDays(7))
                .order(1)
                .build());

        // 公告 2：新作品发布
        announcements.add(AnnouncementResponse.builder()
                .id(2L)
                .title("新作品发布：传统刺绣工艺展")
                .content("本周新发布了多件传统刺绣工艺作品，欢迎大家前往作品展示区欣赏。")
                .type("activity")
                .createdAt(LocalDateTime.now().minusDays(3))
                .order(2)
                .build());

        // 公告 3：知识分享
        announcements.add(AnnouncementResponse.builder()
                .id(3L)
                .title("刺绣技法知识分享")
                .content("本周在知识科普区分享了关于壮族刺绣基本针法的详细教程，欢迎学习。")
                .type("activity")
                .createdAt(LocalDateTime.now().minusDays(1))
                .order(3)
                .build());

        log.info("成功获取最新活动和公告，共 {} 条", announcements.size());
        return ApiResponse.success(announcements);
    }

    /**
     * 获取平台访问数据统计
     *
     * @return 访问统计数据
     */
    @GetMapping("/statistics")
    public ApiResponse<AccessStatisticsResponse> getStatistics() {
        log.info("获取平台访问数据统计");

        AccessStatisticsResponse statistics = systemService.getAccessStatistics();

        log.info("成功获取平台访问数据统计");
        return ApiResponse.success(statistics);
    }

}
