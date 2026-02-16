package com.campus.service;

import com.campus.dto.ActivityStatisticsDTO;
import com.campus.dto.AdminDashboardDTO;
import com.campus.dto.CrowdfundingStatisticsDTO;
import com.campus.dto.FeedbackStatisticsDTO;
import com.campus.entity.Activity;
import com.campus.entity.CrowdfundingSupport;
import com.campus.entity.FundProof;
import com.campus.entity.Registration;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.CrowdfundingSupportRepository;
import com.campus.repository.FeedbackRepository;
import com.campus.repository.FundProofRepository;
import com.campus.repository.RegistrationRepository;
import com.campus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Statistics Service
 * Handles statistics and data aggregation for organizers and admins
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class StatisticsService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CrowdfundingSupportRepository crowdfundingSupportRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FundProofRepository fundProofRepository;

    /**
     * Get activity statistics for an organizer
     * Calculates registration count, crowdfunding completion rate, and average rating
     * 
     * @param username Username of the organizer
     * @return ActivityStatisticsDTO containing aggregated statistics
     * @throws BusinessException if organizer not found
     */
    public ActivityStatisticsDTO getActivityStatistics(String username) {
        log.info("Fetching activity statistics for organizer: {}", username);

        // Find organizer
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Organizer not found: {}", username);
            throw new BusinessException(404, "Organizer not found");
        }

        User organizer = userOptional.get();

        // Validate organizer role
        if (organizer.getRole() != User.UserRole.ORGANIZER && organizer.getRole() != User.UserRole.ADMIN) {
            log.warn("User {} is not an organizer", username);
            throw new BusinessException(403, "Only organizers can access activity statistics");
        }

        // Get all activities for this organizer
        List<Activity> activities = activityRepository.findByStatus(Activity.ActivityStatus.APPROVED);
        activities = activities.stream()
                .filter(a -> a.getOrganizerId().equals(organizer.getId()))
                .toList();

        // Calculate statistics
        long totalRegistrations = 0;
        long totalCrowdfundingAmount = 0;
        double totalRating = 0;
        int activitiesWithFeedback = 0;

        for (Activity activity : activities) {
            // Count registrations
            long registrationCount = registrationRepository.countByActivityIdAndStatus(
                    activity.getId(),
                    Registration.RegistrationStatus.REGISTERED
            );
            totalRegistrations += registrationCount;

            // Sum crowdfunding amount if enabled
            if (activity.getEnableCrowdfunding() != null && activity.getEnableCrowdfunding()) {
                BigDecimal crowdfundingAmount = crowdfundingSupportRepository.getTotalAmountByActivityId(activity.getId());
                if (crowdfundingAmount != null) {
                    totalCrowdfundingAmount += crowdfundingAmount.longValue();
                }
            }

            // Calculate average rating
            Double averageRating = feedbackRepository.getAverageRatingByActivityId(activity.getId());
            if (averageRating != null && averageRating > 0) {
                totalRating += averageRating;
                activitiesWithFeedback++;
            }
        }

        // Calculate average rating across all activities
        double averageRating = activitiesWithFeedback > 0 ? totalRating / activitiesWithFeedback : 0.0;

        // Build response
        ActivityStatisticsDTO statistics = ActivityStatisticsDTO.builder()
                .totalActivities((long) activities.size())
                .totalRegistrations(totalRegistrations)
                .averageRegistrationsPerActivity(activities.size() > 0 ? totalRegistrations / (double) activities.size() : 0.0)
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .build();

        log.info("Activity statistics calculated for organizer: {} - total activities: {}, total registrations: {}", 
                username, statistics.getTotalActivities(), statistics.getTotalRegistrations());

        return statistics;
    }

    /**
     * Get crowdfunding statistics for an organizer
     * Calculates total crowdfunding amount, completion rate, and supporter count
     * 
     * @param username Username of the organizer
     * @return CrowdfundingStatisticsDTO containing aggregated statistics
     * @throws BusinessException if organizer not found
     */
    public CrowdfundingStatisticsDTO getCrowdfundingStatistics(String username) {
        log.info("Fetching crowdfunding statistics for organizer: {}", username);

        // Find organizer
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Organizer not found: {}", username);
            throw new BusinessException(404, "Organizer not found");
        }

        User organizer = userOptional.get();

        // Validate organizer role
        if (organizer.getRole() != User.UserRole.ORGANIZER && organizer.getRole() != User.UserRole.ADMIN) {
            log.warn("User {} is not an organizer", username);
            throw new BusinessException(403, "Only organizers can access crowdfunding statistics");
        }

        // Get all activities for this organizer with crowdfunding enabled
        List<Activity> activities = activityRepository.findByStatus(Activity.ActivityStatus.APPROVED);
        activities = activities.stream()
                .filter(a -> a.getOrganizerId().equals(organizer.getId()) && 
                           a.getEnableCrowdfunding() != null && a.getEnableCrowdfunding())
                .toList();

        // Calculate statistics
        long totalCrowdfundingAmount = 0;
        long totalSupporters = 0;
        long totalTargetAmount = 0;
        int completedActivities = 0;

        for (Activity activity : activities) {
            // Get crowdfunding amount
            BigDecimal crowdfundingAmount = crowdfundingSupportRepository.getTotalAmountByActivityId(activity.getId());
            if (crowdfundingAmount != null) {
                totalCrowdfundingAmount += crowdfundingAmount.longValue();
            }

            // Count supporters
            long supporterCount = crowdfundingSupportRepository.countByActivityIdAndPaymentStatus(
                    activity.getId(),
                    CrowdfundingSupport.PaymentStatus.COMPLETED
            );
            totalSupporters += supporterCount;

            // Sum target amounts
            if (activity.getCrowdfundingTarget() != null) {
                totalTargetAmount += activity.getCrowdfundingTarget().longValue();
                
                // Check if target is reached
                if (crowdfundingAmount != null && crowdfundingAmount.longValue() >= activity.getCrowdfundingTarget().longValue()) {
                    completedActivities++;
                }
            }
        }

        // Calculate completion rate
        double completionRate = activities.size() > 0 ? (completedActivities / (double) activities.size()) * 100 : 0.0;

        // Build response
        CrowdfundingStatisticsDTO statistics = CrowdfundingStatisticsDTO.builder()
                .totalCrowdfundingAmount(totalCrowdfundingAmount)
                .totalTargetAmount(totalTargetAmount)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .totalSupporters(totalSupporters)
                .averageSupportPerActivity(activities.size() > 0 ? totalSupporters / (double) activities.size() : 0.0)
                .build();

        log.info("Crowdfunding statistics calculated for organizer: {} - total amount: {}, completion rate: {}", 
                username, statistics.getTotalCrowdfundingAmount(), statistics.getCompletionRate());

        return statistics;
    }

    /**
     * Get feedback statistics for an organizer
     * Calculates total feedback count, average rating, and rating distribution
     * 
     * @param username Username of the organizer
     * @return FeedbackStatisticsDTO containing aggregated statistics
     * @throws BusinessException if organizer not found
     */
    public FeedbackStatisticsDTO getFeedbackStatistics(String username) {
        log.info("Fetching feedback statistics for organizer: {}", username);

        // Find organizer
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Organizer not found: {}", username);
            throw new BusinessException(404, "Organizer not found");
        }

        User organizer = userOptional.get();

        // Validate organizer role
        if (organizer.getRole() != User.UserRole.ORGANIZER && organizer.getRole() != User.UserRole.ADMIN) {
            log.warn("User {} is not an organizer", username);
            throw new BusinessException(403, "Only organizers can access feedback statistics");
        }

        // Get all activities for this organizer
        List<Activity> activities = activityRepository.findByStatus(Activity.ActivityStatus.APPROVED);
        activities = activities.stream()
                .filter(a -> a.getOrganizerId().equals(organizer.getId()))
                .toList();

        // Calculate statistics
        long totalFeedback = 0;
        double totalRating = 0;
        int activitiesWithFeedback = 0;

        for (Activity activity : activities) {
            // Count feedback
            long feedbackCount = feedbackRepository.countByActivityId(activity.getId());
            totalFeedback += feedbackCount;

            // Calculate average rating
            Double averageRating = feedbackRepository.getAverageRatingByActivityId(activity.getId());
            if (averageRating != null && averageRating > 0) {
                totalRating += averageRating;
                activitiesWithFeedback++;
            }
        }

        // Calculate overall average rating
        double averageRating = activitiesWithFeedback > 0 ? totalRating / activitiesWithFeedback : 0.0;

        // Build response
        FeedbackStatisticsDTO statistics = FeedbackStatisticsDTO.builder()
                .totalFeedback(totalFeedback)
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .activitiesWithFeedback((long) activitiesWithFeedback)
                .build();

        log.info("Feedback statistics calculated for organizer: {} - total feedback: {}, average rating: {}", 
                username, statistics.getTotalFeedback(), statistics.getAverageRating());

        return statistics;
    }

    /**
     * Get admin dashboard data
     * Calculates key metrics for admin dashboard including total users, activities, crowdfunding amount, etc.
     * 
     * @return AdminDashboardDTO containing dashboard metrics
     */
    public AdminDashboardDTO getAdminDashboard() {
        log.info("Fetching admin dashboard data");

        // Get total users
        long totalUsers = userRepository.count();

        // Get total activities
        long totalActivities = activityRepository.count();

        // Get total crowdfunding amount
        BigDecimal totalCrowdfundingAmount = crowdfundingSupportRepository.getTotalCrowdfundingAmount();
        long totalCrowdfundingAmountLong = totalCrowdfundingAmount != null ? totalCrowdfundingAmount.longValue() : 0L;

        // Get average activity rating
        Double averageRating = feedbackRepository.getAverageRating();
        double avgRating = averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0.0;

        // Get total registrations
        long totalRegistrations = registrationRepository.countByStatus(Registration.RegistrationStatus.REGISTERED);

        // Get total feedback
        long totalFeedback = feedbackRepository.count();

        // Get pending audit activities
        long pendingAuditActivities = activityRepository.countByStatus(Activity.ActivityStatus.PENDING_AUDIT);

        // Get pending audit crowdfunding (from fund_proofs table)
        long pendingAuditCrowdfunding = fundProofRepository.countByStatus(FundProof.ProofStatus.PENDING_AUDIT);

        // Build response
        AdminDashboardDTO dashboard = AdminDashboardDTO.builder()
                .totalUsers(totalUsers)
                .totalActivities(totalActivities)
                .totalCrowdfundingAmount(totalCrowdfundingAmountLong)
                .averageActivityRating(avgRating)
                .totalRegistrations(totalRegistrations)
                .totalFeedback(totalFeedback)
                .pendingAuditActivities(pendingAuditActivities)
                .pendingAuditCrowdfunding(pendingAuditCrowdfunding)
                .build();

        log.info("Admin dashboard data calculated - total users: {}, total activities: {}, total crowdfunding: {}", 
                totalUsers, totalActivities, totalCrowdfundingAmountLong);

        return dashboard;
    }

    /**
     * Export statistics data to Excel
     * Generates an Excel file containing user data, activity data, and crowdfunding data
     * 
     * @return Byte array containing Excel file data
     * @throws BusinessException if export fails
     */
    public byte[] exportStatisticsToExcel() {
        log.info("Exporting statistics to Excel");

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create sheets
            createUserDataSheet(workbook);
            createActivityDataSheet(workbook);
            createCrowdfundingDataSheet(workbook);

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelData = outputStream.toByteArray();

            log.info("Statistics exported to Excel successfully, file size: {} bytes", excelData.length);
            return excelData;

        } catch (IOException e) {
            log.error("Failed to export statistics to Excel", e);
            throw new BusinessException(500, "Failed to export statistics to Excel");
        }
    }

    /**
     * Create user data sheet in Excel workbook
     */
    private void createUserDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Users");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("User ID");
        headerRow.createCell(1).setCellValue("Username");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Role");
        headerRow.createCell(4).setCellValue("Status");

        // Get all users
        List<User> users = userRepository.findAll();

        // Add data rows
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getEmail());
            row.createCell(3).setCellValue(user.getRole() != null ? user.getRole().toString() : "");
            row.createCell(4).setCellValue(user.getStatus() != null ? user.getStatus().toString() : "");
        }

        // Auto-size columns
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create activity data sheet in Excel workbook
     */
    private void createActivityDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Activities");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Activity ID");
        headerRow.createCell(1).setCellValue("Title");
        headerRow.createCell(2).setCellValue("Type");
        headerRow.createCell(3).setCellValue("Status");
        headerRow.createCell(4).setCellValue("Registrations");
        headerRow.createCell(5).setCellValue("Average Rating");

        // Get all activities
        List<Activity> activities = activityRepository.findAll();

        // Add data rows
        int rowNum = 1;
        for (Activity activity : activities) {
            long registrationCount = registrationRepository.countByActivityIdAndStatus(
                    activity.getId(),
                    Registration.RegistrationStatus.REGISTERED
            );
            Double avgRating = feedbackRepository.getAverageRatingByActivityId(activity.getId());

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(activity.getId());
            row.createCell(1).setCellValue(activity.getTitle());
            row.createCell(2).setCellValue(activity.getType());
            row.createCell(3).setCellValue(activity.getStatus().toString());
            row.createCell(4).setCellValue(registrationCount);
            row.createCell(5).setCellValue(avgRating != null ? avgRating : 0.0);
        }

        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create crowdfunding data sheet in Excel workbook
     */
    private void createCrowdfundingDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Crowdfunding");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Activity ID");
        headerRow.createCell(1).setCellValue("Activity Title");
        headerRow.createCell(2).setCellValue("Target Amount");
        headerRow.createCell(3).setCellValue("Raised Amount");
        headerRow.createCell(4).setCellValue("Supporters");
        headerRow.createCell(5).setCellValue("Completion Rate");

        // Get all activities with crowdfunding enabled
        List<Activity> activities = activityRepository.findAll();
        activities = activities.stream()
                .filter(a -> a.getEnableCrowdfunding() != null && a.getEnableCrowdfunding())
                .toList();

        // Add data rows
        int rowNum = 1;
        for (Activity activity : activities) {
            BigDecimal raisedAmount = crowdfundingSupportRepository.getTotalAmountByActivityId(activity.getId());
            long supporterCount = crowdfundingSupportRepository.countByActivityIdAndPaymentStatus(
                    activity.getId(),
                    CrowdfundingSupport.PaymentStatus.COMPLETED
            );

            long targetAmount = activity.getCrowdfundingTarget() != null ? 
                    activity.getCrowdfundingTarget().longValue() : 0L;
            long raised = raisedAmount != null ? raisedAmount.longValue() : 0L;
            double completionRate = targetAmount > 0 ? (raised / (double) targetAmount) * 100 : 0.0;

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(activity.getId());
            row.createCell(1).setCellValue(activity.getTitle());
            row.createCell(2).setCellValue(targetAmount);
            row.createCell(3).setCellValue(raised);
            row.createCell(4).setCellValue(supporterCount);
            row.createCell(5).setCellValue(Math.round(completionRate * 100.0) / 100.0);
        }

        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

}
