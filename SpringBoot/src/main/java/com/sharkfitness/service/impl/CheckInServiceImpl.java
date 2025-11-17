package com.sharkfitness.service.impl;

import com.sharkfitness.entity.CheckIn;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.BusinessException;
import com.sharkfitness.repository.CheckInRepository;
import com.sharkfitness.service.CheckInService;
import com.sharkfitness.vo.CheckInStatsVO;
import com.sharkfitness.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CheckInService
 */
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {
    
    private final CheckInRepository checkInRepository;
    private final com.sharkfitness.repository.CoachStudentRepository coachStudentRepository;
    private final com.sharkfitness.repository.UserRepository userRepository;
    
    @Override
    @Transactional
    public CheckInVO performCheckIn(User user) {
        LocalDate today = LocalDate.now();
        
        // Check if user already checked in today
        Optional<CheckIn> existingCheckIn = checkInRepository.findByUserAndCheckDate(user, today);
        if (existingCheckIn.isPresent()) {
            throw new BusinessException(400, "You have already checked in today");
        }
        
        // Create new check-in
        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);
        checkIn.setCheckDate(today);
        checkIn.setCheckTime(LocalDateTime.now());
        
        CheckIn saved = checkInRepository.save(checkIn);
        
        return convertToVO(saved);
    }
    
    @Override
    public List<CheckInVO> getHistory(User user) {
        List<CheckIn> checkIns = checkInRepository.findByUserOrderByCheckDateDesc(user);
        return checkIns.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CheckInStatsVO calculateStats(User user) {
        // Get total count
        long totalCount = checkInRepository.countByUser(user);
        
        // Get all check-ins ordered by date ascending
        List<CheckIn> checkIns = checkInRepository.findByUserOrderByCheckDateAsc(user);
        
        if (checkIns.isEmpty()) {
            return new CheckInStatsVO(0L, 0, 0);
        }
        
        // Calculate current streak
        int currentStreak = calculateCurrentStreak(checkIns);
        
        // Calculate longest streak
        int longestStreak = calculateLongestStreak(checkIns);
        
        return new CheckInStatsVO(totalCount, currentStreak, longestStreak);
    }
    
    /**
     * Calculate current consecutive check-in streak
     */
    private int calculateCurrentStreak(List<CheckIn> checkIns) {
        if (checkIns.isEmpty()) {
            return 0;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        // Check if the most recent check-in is today or yesterday
        CheckIn lastCheckIn = checkIns.get(checkIns.size() - 1);
        LocalDate lastCheckDate = lastCheckIn.getCheckDate();
        
        // If last check-in is not today or yesterday, streak is broken
        if (!lastCheckDate.equals(today) && !lastCheckDate.equals(yesterday)) {
            return 0;
        }
        
        // Count backwards from the most recent check-in
        int streak = 1;
        for (int i = checkIns.size() - 2; i >= 0; i--) {
            LocalDate currentDate = checkIns.get(i + 1).getCheckDate();
            LocalDate previousDate = checkIns.get(i).getCheckDate();
            
            // Check if dates are consecutive
            long daysBetween = ChronoUnit.DAYS.between(previousDate, currentDate);
            if (daysBetween == 1) {
                streak++;
            } else {
                break;
            }
        }
        
        return streak;
    }
    
    /**
     * Calculate longest consecutive check-in streak
     */
    private int calculateLongestStreak(List<CheckIn> checkIns) {
        if (checkIns.isEmpty()) {
            return 0;
        }
        
        int longestStreak = 1;
        int currentStreak = 1;
        
        for (int i = 1; i < checkIns.size(); i++) {
            LocalDate previousDate = checkIns.get(i - 1).getCheckDate();
            LocalDate currentDate = checkIns.get(i).getCheckDate();
            
            // Check if dates are consecutive
            long daysBetween = ChronoUnit.DAYS.between(previousDate, currentDate);
            if (daysBetween == 1) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }
        
        return longestStreak;
    }
    
    @Override
    public List<CheckInVO> getStudentCheckIns(Long coachId, Long studentId) {
        // Validate that coach-student relationship exists
        if (!coachStudentRepository.existsByCoachIdAndStudentId(coachId, studentId)) {
            throw new BusinessException(403, "You do not have access to this student's data");
        }
        
        // Get the student user
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(404, "Student not found"));
        
        // Return the student's check-in history
        return getHistory(student);
    }
    
    /**
     * Convert CheckIn entity to CheckInVO
     */
    private CheckInVO convertToVO(CheckIn checkIn) {
        CheckInVO vo = new CheckInVO();
        vo.setId(checkIn.getId());
        vo.setCheckDate(checkIn.getCheckDate());
        vo.setCheckTime(checkIn.getCheckTime());
        return vo;
    }
}
