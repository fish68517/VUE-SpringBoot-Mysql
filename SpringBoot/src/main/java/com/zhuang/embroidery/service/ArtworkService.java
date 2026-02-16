package com.zhuang.embroidery.service;

import com.zhuang.embroidery.dto.ArtworkCreateRequest;
import com.zhuang.embroidery.dto.ArtworkListResponse;
import com.zhuang.embroidery.dto.ArtworkResponse;
import com.zhuang.embroidery.dto.ArtworkUpdateRequest;
import com.zhuang.embroidery.entity.Artwork;
import com.zhuang.embroidery.repository.ArtworkRepository;
import com.zhuang.embroidery.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 作品业务逻辑服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArtworkService {

    private final ArtworkRepository artworkRepository;

    /**
     * 获取作品列表（支持分类、分页）
     *
     * @param category 分类（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 作品列表响应
     */
    public ArtworkListResponse getArtworkList(String category, Integer pageNum, Integer pageSize) {
        log.info("获取作品列表: category={}, pageNum={}, pageSize={}", category, pageNum, pageSize);

        // 验证分页参数
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);

        // 创建分页对象
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize());

        // 查询已批准的作品
        Page<Artwork> page;
        if (category != null && !category.trim().isEmpty()) {
            page = artworkRepository.findByCategoryAndStatus(category, "approved", pageable);
        } else {
            page = artworkRepository.findByStatus("approved", pageable);
        }

        // 构建响应
        List<ArtworkResponse> items = page.getContent()
                .stream()
                .map(ArtworkResponse::fromArtwork)
                .collect(Collectors.toList());

        pageUtil.setTotal(page.getTotalElements());
        pageUtil.calculateTotalPages();

        return ArtworkListResponse.builder()
                .items(items)
                .pageNum(pageUtil.getPageNum())
                .pageSize(pageUtil.getPageSize())
                .total(pageUtil.getTotal())
                .totalPages(pageUtil.getTotalPages())
                .build();
    }

    /**
     * 获取作品详情
     *
     * @param artworkId 作品ID
     * @return 作品响应
     * @throws IllegalArgumentException 当作品不存在时抛出
     */
    public ArtworkResponse getArtworkDetail(Long artworkId) {
        log.info("获取作品详情: artworkId={}", artworkId);

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("作品不存在: artworkId={}", artworkId);
                    return new IllegalArgumentException("作品不存在");
                });

        return ArtworkResponse.fromArtwork(artwork);
    }

    /**
     * 创建作品
     *
     * @param request 作品创建请求
     * @return 作品响应
     * @throws IllegalArgumentException 当参数验证失败时抛出
     */
    @Transactional
    public ArtworkResponse createArtwork(ArtworkCreateRequest request) {
        log.info("创建作品: title={}", request.getTitle());

        // 验证参数
        validateArtworkRequest(request);

        // 创建新作品
        Artwork artwork = Artwork.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .creator(request.getCreator())
                .technique(request.getTechnique())
                .status("draft")
                .viewCount(0)
                .collectCount(0)
                .build();

        artwork = artworkRepository.save(artwork);
        log.info("作品创建成功: artworkId={}, title={}", artwork.getId(), artwork.getTitle());

        return ArtworkResponse.fromArtwork(artwork);
    }

    /**
     * 编辑作品
     *
     * @param artworkId 作品ID
     * @param request 作品更新请求
     * @return 作品响应
     * @throws IllegalArgumentException 当作品不存在或参数验证失败时抛出
     */
    @Transactional
    public ArtworkResponse updateArtwork(Long artworkId, ArtworkUpdateRequest request) {
        log.info("编辑作品: artworkId={}", artworkId);

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("作品不存在: artworkId={}", artworkId);
                    return new IllegalArgumentException("作品不存在");
                });

        // 验证参数
        validateArtworkUpdateRequest(request);

        // 更新字段
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            artwork.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            artwork.setDescription(request.getDescription());
        }
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            artwork.setCategory(request.getCategory());
        }
        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            artwork.setImageUrl(request.getImageUrl());
        }
        if (request.getCreator() != null && !request.getCreator().trim().isEmpty()) {
            artwork.setCreator(request.getCreator());
        }
        if (request.getTechnique() != null && !request.getTechnique().trim().isEmpty()) {
            artwork.setTechnique(request.getTechnique());
        }

        artwork = artworkRepository.save(artwork);
        log.info("作品编辑成功: artworkId={}", artworkId);

        return ArtworkResponse.fromArtwork(artwork);
    }

    /**
     * 删除作品
     *
     * @param artworkId 作品ID
     * @throws IllegalArgumentException 当作品不存在时抛出
     */
    @Transactional
    public void deleteArtwork(Long artworkId) {
        log.info("删除作品: artworkId={}", artworkId);

        if (!artworkRepository.existsById(artworkId)) {
            log.warn("作品不存在: artworkId={}", artworkId);
            throw new IllegalArgumentException("作品不存在");
        }

        artworkRepository.deleteById(artworkId);
        log.info("作品删除成功: artworkId={}", artworkId);
    }

    /**
     * 批准作品
     *
     * @param artworkId 作品ID
     * @return 作品响应
     * @throws IllegalArgumentException 当作品不存在时抛出
     */
    @Transactional
    public ArtworkResponse approveArtwork(Long artworkId) {
        log.info("批准作品: artworkId={}", artworkId);

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("作品不存在: artworkId={}", artworkId);
                    return new IllegalArgumentException("作品不存在");
                });

        artwork.setStatus("approved");
        artwork = artworkRepository.save(artwork);
        log.info("作品批准成功: artworkId={}", artworkId);

        return ArtworkResponse.fromArtwork(artwork);
    }

    /**
     * 拒绝作品
     *
     * @param artworkId 作品ID
     * @return 作品响应
     * @throws IllegalArgumentException 当作品不存在时抛出
     */
    @Transactional
    public ArtworkResponse rejectArtwork(Long artworkId) {
        log.info("拒绝作品: artworkId={}", artworkId);

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("作品不存在: artworkId={}", artworkId);
                    return new IllegalArgumentException("作品不存在");
                });

        artwork.setStatus("rejected");
        artwork = artworkRepository.save(artwork);
        log.info("作品拒绝成功: artworkId={}", artworkId);

        return ArtworkResponse.fromArtwork(artwork);
    }

    /**
     * 下架作品
     *
     * @param artworkId 作品ID
     * @return 作品响应
     * @throws IllegalArgumentException 当作品不存在时抛出
     */
    @Transactional
    public ArtworkResponse offlineArtwork(Long artworkId) {
        log.info("下架作品: artworkId={}", artworkId);

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("作品不存在: artworkId={}", artworkId);
                    return new IllegalArgumentException("作品不存在");
                });

        artwork.setStatus("offline");
        artwork = artworkRepository.save(artwork);
        log.info("作品下架成功: artworkId={}", artworkId);

        return ArtworkResponse.fromArtwork(artwork);
    }

    /**
     * 记录作品浏览（增加浏览计数）
     *
     * @param artworkId 作品ID
     * @throws IllegalArgumentException 当作品不存在时抛出
     */
    @Transactional
    public void recordArtworkView(Long artworkId) {
        log.info("记录作品浏览: artworkId={}", artworkId);

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("作品不存在: artworkId={}", artworkId);
                    return new IllegalArgumentException("作品不存在");
                });

        artwork.setViewCount(artwork.getViewCount() + 1);
        artworkRepository.save(artwork);
        log.debug("作品浏览计数已更新: artworkId={}, viewCount={}", artworkId, artwork.getViewCount());
    }

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    public List<String> getAllCategories() {
        log.info("获取所有分类");
        return artworkRepository.findAllCategories();
    }

    /**
     * 验证作品创建请求参数
     */
    private void validateArtworkRequest(ArtworkCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("作品名称不能为空");
        }

        if (request.getTitle().length() > 255) {
            throw new IllegalArgumentException("作品名称长度不能超过255个字符");
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("分类不能为空");
        }

        if (request.getCreator() == null || request.getCreator().trim().isEmpty()) {
            throw new IllegalArgumentException("创作者名称不能为空");
        }
    }

    /**
     * 验证作品更新请求参数
     */
    private void validateArtworkUpdateRequest(ArtworkUpdateRequest request) {
        if (request.getTitle() != null && request.getTitle().length() > 255) {
            throw new IllegalArgumentException("作品名称长度不能超过255个字符");
        }
    }


    public Map<String, Object> getArtworkPage(Integer pageNum, Integer pageSize) {
        PageUtil pageUtil = PageUtil.validate(pageNum, pageSize);
        Pageable pageable = PageRequest.of(pageUtil.getPageNum() - 1, pageUtil.getPageSize(), Sort.by("id").descending());
        Page<Artwork> page = artworkRepository.findAll(pageable);
        List<ArtworkResponse> list = page.getContent().stream()
                .map(ArtworkResponse::fromArtwork) // 假设你有这个转换方法
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", page.getTotalElements());
        return result;
    }

}
