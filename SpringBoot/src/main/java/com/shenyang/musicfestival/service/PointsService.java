package com.shenyang.musicfestival.service;
import com.shenyang.musicfestival.dto.*;
import java.util.List;

public interface PointsService {
    List<CheckinTaskDTO> getTasks(Long userId);
    void checkin(Long userId, Long taskId);
    List<PointsMallItemDTO> getMallItems();
    void exchangeItem(Long userId, Long itemId, String address);
    List<PointsHistoryDTO> getHistory(Long userId);
    Long getBalance(Long userId);
}