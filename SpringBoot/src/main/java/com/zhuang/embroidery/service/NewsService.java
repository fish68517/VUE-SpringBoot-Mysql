package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.NewsCreateRequest;
import com.zhuang.embroidery.dto.NewsResponse;
import com.zhuang.embroidery.dto.NewsUpdateRequest;
import com.zhuang.embroidery.entity.News;
import com.zhuang.embroidery.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资讯业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;

    /**
     * 创建资讯
     *
     * @param request 资讯创建请求
     * @return 资讯响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public NewsResponse createNews(NewsCreateRequest request) {
        log.info("创建资讯: title={}", request.getTitle());

        // 验证参数
        validateNewsRequest(request);

        // 创建新资讯
        News news = News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .build();

        news = newsRepository.save(news);
        log.info("资讯创建成功: newsId={}, title={}", news.getId(), news.getTitle());

        return NewsResponse.fromNews(news);
    }

    /**
     * 编辑资讯
     *
     * @param newsId 资讯ID
     * @param request 资讯更新请求
     * @return 资讯响应
     * @throws IllegalArgumentException 当资讯不存在或参数验证失败时抛出
     */
    @Transactional
    public NewsResponse updateNews(Long newsId, NewsUpdateRequest request) {
        log.info("编辑资讯: newsId={}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> {
                    log.warn("资讯不存在: newsId={}", newsId);
                    return new IllegalArgumentException("资讯不存在");
                });

        // 验证参数
        validateNewsUpdateRequest(request);

        // 更新字段
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            news.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            news.setContent(request.getContent());
        }
        if (request.getAuthor() != null && !request.getAuthor().trim().isEmpty()) {
            news.setAuthor(request.getAuthor());
        }

        news = newsRepository.save(news);
        log.info("资讯编辑成功: newsId={}", newsId);

        return NewsResponse.fromNews(news);
    }

    /**
     * 删除资讯
     *
     * @param newsId 资讯ID
     * @throws IllegalArgumentException 当资讯不存在时抛出
     */
    @Transactional
    public void deleteNews(Long newsId) {
        log.info("删除资讯: newsId={}", newsId);

        if (!newsRepository.existsById(newsId)) {
            log.warn("资讯不存在: newsId={}", newsId);
            throw new IllegalArgumentException("资讯不存在");
        }

        newsRepository.deleteById(newsId);
        log.info("资讯删除成功: newsId={}", newsId);
    }

    /**
     * 验证资讯创建请求参数
     */
    private void validateNewsRequest(NewsCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("资讯标题不能为空");
        }

        if (request.getTitle().length() > 255) {
            throw new IllegalArgumentException("资讯标题长度不能超过255个字符");
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("资讯内容不能为空");
        }
    }

    /**
     * 验证资讯更新请求参数
     */
    private void validateNewsUpdateRequest(NewsUpdateRequest request) {
        if (request.getTitle() != null && request.getTitle().length() > 255) {
            throw new IllegalArgumentException("资讯标题长度不能超过255个字符");
        }
    }
}
