package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.platform.common.PageResult;
import com.charging.platform.dto.FavoriteRequest;
import com.charging.platform.entity.SysUser;
import com.charging.platform.entity.UserFavorite;
import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.SysUserMapper;
import com.charging.platform.mapper.UserFavoriteMapper;
import com.charging.platform.vo.FavoriteStateVO;
import com.charging.platform.vo.FavoriteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserFavoriteMapper favoriteMapper;
    private final SysUserMapper userMapper;
    private final StationService stationService;

    public PageResult<FavoriteVO> getFavorites(Long userId, long pageNum, long pageSize) {
        requireUser(userId);
        Page<FavoriteVO> page = favoriteMapper.selectFavoritePage(new Page<>(pageNum, pageSize), userId);
        return PageResult.from(page);
    }

    public FavoriteStateVO check(Long userId, Long stationId) {
        if (userId == null || stationId == null) {
            return new FavoriteStateVO(false, null);
        }
        UserFavorite favorite = favoriteMapper.selectOne(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getStationId, stationId)
                .last("LIMIT 1"));
        return new FavoriteStateVO(favorite != null, favorite == null ? null : favorite.getId());
    }

    @Transactional
    public FavoriteStateVO add(FavoriteRequest request) {
        requireUser(request.getUserId());
        stationService.getStationDetail(request.getStationId());
        FavoriteStateVO existing = check(request.getUserId(), request.getStationId());
        if (existing.isFavorited()) {
            return existing;
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(request.getUserId());
        favorite.setStationId(request.getStationId());
        try {
            favoriteMapper.insert(favorite);
        } catch (DuplicateKeyException exception) {
            return check(request.getUserId(), request.getStationId());
        }
        return new FavoriteStateVO(true, favorite.getId());
    }

    @Transactional
    public void remove(Long id) {
        if (favoriteMapper.deleteById(id) == 0) {
            throw new BusinessException(404, "收藏记录不存在");
        }
    }

    private SysUser requireUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }
}
