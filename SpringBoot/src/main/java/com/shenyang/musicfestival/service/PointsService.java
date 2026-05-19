package com.shenyang.musicfestival.service;
import com.shenyang.musicfestival.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PointsService {
    List<CheckinTaskDTO> getTasks(Long userId);
    void checkin(Long userId, Long taskId, MultipartFile photo, String description);
    List<CheckinRecordDTO> getMyRecords(Long userId);
    List<PointsMallItemDTO> getMallItems();
    void exchangeItem(Long userId, Long itemId, String address);
    List<PointsHistoryDTO> getHistory(Long userId);
    Long getBalance(Long userId);
}
