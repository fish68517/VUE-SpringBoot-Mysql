package com.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyroom.dto.Result;
import com.studyroom.entity.FavoriteSeat;
import com.studyroom.entity.Seat;
import com.studyroom.mapper.FavoriteSeatMapper;
import com.studyroom.mapper.SeatMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteSeatService {

    private final FavoriteSeatMapper favoriteSeatMapper;
    private final SeatMapper seatMapper;

    public FavoriteSeatService(FavoriteSeatMapper favoriteSeatMapper, SeatMapper seatMapper) {
        this.favoriteSeatMapper = favoriteSeatMapper;
        this.seatMapper = seatMapper;
    }

    public Result<String> addFavorite(Long userId, Long seatId) {
        // 检查座位是否存在
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null) {
            return Result.error("座位不存在");
        }

        // 检查是否已收藏
        FavoriteSeat existing = favoriteSeatMapper.selectOne(
                new LambdaQueryWrapper<FavoriteSeat>()
                        .eq(FavoriteSeat::getUserId, userId)
                        .eq(FavoriteSeat::getSeatId, seatId)
        );
        if (existing != null) {
            return Result.error("已收藏该座位");
        }

        FavoriteSeat favorite = new FavoriteSeat();
        favorite.setUserId(userId);
        favorite.setSeatId(seatId);
        favoriteSeatMapper.insert(favorite);
        return Result.success("收藏成功");
    }

    public Result<String> removeFavorite(Long userId, Long seatId) {
        favoriteSeatMapper.delete(
                new LambdaQueryWrapper<FavoriteSeat>()
                        .eq(FavoriteSeat::getUserId, userId)
                        .eq(FavoriteSeat::getSeatId, seatId)
        );
        return Result.success("取消收藏");
    }

    public Result<List<Map<String, Object>>> getUserFavorites(Long userId) {
        List<FavoriteSeat> favorites = favoriteSeatMapper.selectList(
                new LambdaQueryWrapper<FavoriteSeat>()
                        .eq(FavoriteSeat::getUserId, userId)
                        .orderByDesc(FavoriteSeat::getCreateTime)
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (FavoriteSeat f : favorites) {
            Seat seat = seatMapper.selectById(f.getSeatId());
            if (seat != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", f.getId());
                map.put("seatId", seat.getId());
                map.put("seatNo", seat.getSeatNo());
                map.put("area", seat.getArea());
                map.put("status", seat.getStatus());
                map.put("createTime", f.getCreateTime());
                result.add(map);
            }
        }
        return Result.success(result);
    }

    public Result<Boolean> isFavorite(Long userId, Long seatId) {
        Long count = favoriteSeatMapper.selectCount(
                new LambdaQueryWrapper<FavoriteSeat>()
                        .eq(FavoriteSeat::getUserId, userId)
                        .eq(FavoriteSeat::getSeatId, seatId)
        );
        return Result.success(count > 0);
    }
}
