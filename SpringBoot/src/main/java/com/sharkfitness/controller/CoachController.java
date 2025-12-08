package com.sharkfitness.controller;

import com.sharkfitness.dto.CoachStudentDTO;
import com.sharkfitness.entity.CoachStudent;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.BusinessException;
import com.sharkfitness.repository.CoachStudentRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.AnalyticsService;
import com.sharkfitness.service.CheckInService;
import com.sharkfitness.service.DietRecordService;
import com.sharkfitness.vo.AnalyticsVO;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.CheckInVO;
import com.sharkfitness.vo.DietRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for coach-specific operations
 */
@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
public class CoachController {
    
    private final CheckInService checkInService;
    private final DietRecordService dietRecordService;
    private final AnalyticsService analyticsService;
    private final CoachStudentRepository coachStudentRepository;
    private final UserRepository userRepository;

    /**
     * Get check-in history for a specific student
     * @param currentUser Current authenticated user (coach)
     * @param studentId Student's user ID
     * @return List of check-ins
     */
    @GetMapping("/students/{studentId}/checkins")
    public ApiResponse<List<CheckInVO>> getStudentCheckIns(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable Long studentId) {
        
        List<CheckInVO> checkIns = checkInService.getStudentCheckIns(currentUser.getId(), studentId);
        return ApiResponse.success(checkIns);
    }

    // 获取教练所指导学生列表
    @GetMapping("/students")
    public ApiResponse<List<CoachStudent>> getCoachStudents(
            @RequestAttribute("currentUser") User currentUser) {

        List<CoachStudent> students = coachStudentRepository.findByCoachId(currentUser.getId());
        return ApiResponse.success(students);
    }

    @GetMapping("/coachId/{coachId}/students/{studentId}")
    public ApiResponse<CoachStudent> getCoachStudentsById(@PathVariable Long coachId,@PathVariable Long studentId) {

        CoachStudent students = coachStudentRepository.findByCoachIdAndStudentId(coachId,studentId);

        return ApiResponse.success(students);
    }
    
    /**
     * Get diet records for a specific student
     * @param currentUser Current authenticated user (coach)
     * @param studentId Student's user ID
     * @param startDate Start date for filtering (optional)
     * @param endDate End date for filtering (optional)
     * @return List of diet records
     */
    @GetMapping("/students/{studentId}/diet-records")
    public ApiResponse<List<DietRecordVO>> getStudentDietRecords(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<DietRecordVO> dietRecords = dietRecordService.getStudentDietRecords(
                currentUser.getId(), studentId, startDate, endDate);
        return ApiResponse.success(dietRecords);
    }
    
    /**
     * Get training progress analytics for a specific student
     * @param currentUser Current authenticated user (coach)
     * @param studentId Student's user ID
     * @param days Number of days to analyze (default 30)
     * @return Analytics data
     */
    @GetMapping("/analytics/{studentId}")
    public ApiResponse<AnalyticsVO> getStudentAnalytics(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "30") int days) {
        
        // Validate that the coach has access to this student's data
        if (!coachStudentRepository.existsByCoachIdAndStudentId(currentUser.getId(), studentId)) {
            throw new BusinessException(403, "You do not have access to this student's data");
        }
        
        // Validate days parameter
        if (days <= 0 || days > 365) {
            throw new BusinessException(400, "Days parameter must be between 1 and 365");
        }
        
        AnalyticsVO analytics = analyticsService.calculateStudentAnalytics(studentId, days);
        return ApiResponse.success(analytics);
    }

    // 获取教练列表
    @GetMapping("/list")
    public ApiResponse<List<User>> getCoaches() {
        List<User> users = userRepository.findByRole("coach");
        return ApiResponse.success(users);
    }

    // 根据id 获取教练信息
    @GetMapping("/{coachId}")
    public ApiResponse<User> getCoachById(@PathVariable Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new BusinessException(404, "Coach not found"));
        if (!"coach".equals(coach.getRole())) {
            throw new BusinessException(400, "User is not a coach");
        }
        return ApiResponse.success(coach);
    }

    // api/coaches/students/6. 添加学生
    @PostMapping("/students/{studentId}")
    public ApiResponse<Void> addStudent(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable Long studentId) {

        // 检查学生是否存在
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(404, "Student not found"));

        if (!"user".equals(student.getRole())) {
            throw new BusinessException(400, "User is not a student");
        }

        // 检查关系是否已存在
        if (coachStudentRepository.existsByCoachIdAndStudentId(currentUser.getId(), studentId)) {
            throw new BusinessException(400, "已经是你的学员了");
        }

        // 创建教练-学生关系
        CoachStudent coachStudent = new CoachStudent();
        coachStudent.setStudent(student);
        coachStudent.setCoach(currentUser);
        coachStudentRepository.save(coachStudent);

        return ApiResponse.success(null);
    }

    // 添加接口  ： DELETE
    //	http://localhost:8080/api/coaches/students/5
    @DeleteMapping("/students/{studentId}")
    public ApiResponse<Void> removeStudent(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable Long studentId) {


        // 删除教练-学生关系
        coachStudentRepository.deleteByStudentId(studentId);

        return ApiResponse.success(null);
    }

    // 续约接口 {"coachId":3,"studentId":5,"expireAt":"2026-01-08T04:33:43.754Z"}
    @PostMapping("/renew")
    public ApiResponse<Void> renewStudent(
            @RequestBody CoachStudentDTO coachStudentRequest) {

        Long coachId = coachStudentRequest.getCoachId();
        Long studentId = coachStudentRequest.getStudentId();
        CoachStudent existingRelation = coachStudentRepository.findByCoachIdAndStudentId(coachId, studentId);

        if (existingRelation == null) {
            // 不会会员就增加会员
            // 创建教练-学生关系
            CoachStudent coachStudent = new CoachStudent();
            // 检查学生是否存在
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new BusinessException(404, "Student not found"));


            User coach = userRepository.findById(coachId)
                    .orElseThrow(() -> new BusinessException(404, "Student not found"));

            coachStudent.setStudent(student);
            coachStudent.setCoach(coach);
            coachStudentRepository.save(coachStudent);

            existingRelation = coachStudentRepository.findByCoachIdAndStudentId(coachId, studentId);

        }

        // 直接 set 即可，Spring 已经帮你转好了
        existingRelation.setExpireAt(coachStudentRequest.getExpireAt());
        existingRelation.setStatus(1);
        coachStudentRepository.save(existingRelation);

        return ApiResponse.success(null);
    }
}
