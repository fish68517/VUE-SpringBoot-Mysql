package com.travelMemory.service;

import com.travelMemory.dto.CreateTravelRecordRequest;
import com.travelMemory.dto.TravelRecordResponse;
import com.travelMemory.dto.UserInfo;
import com.travelMemory.entity.TravelRecord;
import com.travelMemory.entity.User;
import com.travelMemory.repository.CommentRepository;
import com.travelMemory.repository.LikeRepository;
import com.travelMemory.repository.TravelRecordRepository;
import com.travelMemory.repository.UserRepository;
import com.travelMemory.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminTravelService {

    private final TravelRecordRepository travelRecordRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FileService fileService;
    private final FootprintService footprintService;

    @Transactional(readOnly = true)
    public Page<TravelRecordResponse> getTravelRecords(Pageable pageable) {
        return travelRecordRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TravelRecordResponse getTravelRecord(Long recordId) {
        return toResponse(findRecord(recordId));
    }

    public TravelRecordResponse updateTravelRecord(Long recordId, CreateTravelRecordRequest request) {
        TravelRecord record = findRecord(recordId);

        record.setTitle(SecurityUtils.sanitizeText(request.getTitle()));
        record.setDestination(SecurityUtils.sanitizeText(request.getDestination()));
        record.setStartDate(request.getStartDate());
        record.setEndDate(request.getEndDate());
        record.setDescription(SecurityUtils.sanitizeText(request.getDescription()));
        record.setDiaryContent(SecurityUtils.sanitizeHtml(request.getDiaryContent()));
        if (request.getIsPublic() != null) {
            record.setIsPublic(request.getIsPublic());
        }

        return toResponse(travelRecordRepository.save(record));
    }

    public void deleteTravelRecord(Long recordId) {
        TravelRecord record = findRecord(recordId);

        try {
            fileService.deleteFilesByTravelRecord(recordId);
        } catch (IOException ignored) {
        }
        try {
            footprintService.deleteFootprintsByTravelRecord(recordId);
        } catch (Exception ignored) {
        }

        travelRecordRepository.delete(record);
    }

    private TravelRecord findRecord(Long recordId) {
        return travelRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("旅行记录不存在"));
    }

    private TravelRecordResponse toResponse(TravelRecord record) {
        User user = userRepository.findById(record.getUserId()).orElse(null);
        UserInfo userInfo = user != null ? UserInfo.from(user) : null;
        TravelRecordResponse response = TravelRecordResponse.from(record, userInfo);
        response.setLikeCount((int) likeRepository.countByTravelRecordId(record.getId()));
        response.setCommentCount((int) commentRepository.countByTravelRecordId(record.getId()));
        return response;
    }
}
