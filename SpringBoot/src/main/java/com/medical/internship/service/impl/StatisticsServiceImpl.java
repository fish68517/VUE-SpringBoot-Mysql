package com.medical.internship.service.impl;

import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.HospitalStatisticsResponse;
import com.medical.internship.dto.SchoolStatisticsResponse;
import com.medical.internship.entity.Application;
import com.medical.internship.entity.Internship;
import com.medical.internship.entity.Post;
import com.medical.internship.repository.ApplicationRepository;
import com.medical.internship.repository.InternshipRepository;
import com.medical.internship.repository.PostRepository;
import com.medical.internship.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final PostRepository postRepository;
    
    @Override
    public SchoolStatisticsResponse getSchoolStatistics() {
        // 获取当前用户所属学校ID
        Long schoolId = SessionContext.getCurrentOrganizationId();
        
        // 获取该学校所有学生的申请
        List<Application> allApplications = applicationRepository.findAll().stream()
                .filter(app -> app.getStudent().getOrganization().getId().equals(schoolId))
                .collect(Collectors.toList());
        
        // 统计各类申请
        long totalApplications = allApplications.size();
        long approvedApplications = allApplications.stream()
                .filter(app -> "APPROVED".equals(app.getHospitalStatus()))
                .count();
        long rejectedApplications = allApplications.stream()
                .filter(app -> "REJECTED".equals(app.getHospitalStatus()))
                .count();
        
        // 获取该学校所有实习记录
        List<Internship> allInternships = internshipRepository.findAll().stream()
                .filter(internship -> internship.getStudent().getOrganization().getId().equals(schoolId))
                .collect(Collectors.toList());
        
        long ongoingInternships = allInternships.stream()
                .filter(internship -> "ONGOING".equals(internship.getStatus()))
                .count();
        long completedInternships = allInternships.stream()
                .filter(internship -> "COMPLETED".equals(internship.getStatus()))
                .count();
        
        // 计算完成率
        double completionRate = 0.0;
        if (approvedApplications > 0) {
            completionRate = (double) completedInternships / approvedApplications * 100;
        }
        
        return SchoolStatisticsResponse.builder()
                .totalApplications(totalApplications)
                .approvedApplications(approvedApplications)
                .ongoingInternships(ongoingInternships)
                .completedInternships(completedInternships)
                .completionRate(completionRate)
                .rejectedApplications(rejectedApplications)
                .build();
    }
    
    @Override
    public HospitalStatisticsResponse getHospitalStatistics() {
        // 获取当前用户所属医院ID
        Long hospitalId = SessionContext.getCurrentOrganizationId();
        
        // 获取该医院所有岗位
        List<Post> allPosts = postRepository.findByHospitalId(hospitalId);
        long totalPosts = allPosts.size();
        
        // 获取该医院所有岗位的申请
        List<Application> allApplications = allPosts.stream()
                .flatMap(post -> applicationRepository.findByPostId(post.getId()).stream())
                .collect(Collectors.toList());
        
        long totalApplications = allApplications.size();
        long approvedApplications = allApplications.stream()
                .filter(app -> "APPROVED".equals(app.getHospitalStatus()))
                .count();
        long rejectedApplications = allApplications.stream()
                .filter(app -> "REJECTED".equals(app.getHospitalStatus()))
                .count();
        
        // 获取该医院所有实习记录
        List<Internship> allInternships = allPosts.stream()
                .flatMap(post -> internshipRepository.findByPostId(post.getId()).stream())
                .collect(Collectors.toList());
        
        long ongoingInternships = allInternships.stream()
                .filter(internship -> "ONGOING".equals(internship.getStatus()))
                .count();
        long completedInternships = allInternships.stream()
                .filter(internship -> "COMPLETED".equals(internship.getStatus()))
                .count();
        
        return HospitalStatisticsResponse.builder()
                .totalPosts(totalPosts)
                .totalApplications(totalApplications)
                .approvedApplications(approvedApplications)
                .ongoingInternships(ongoingInternships)
                .completedInternships(completedInternships)
                .rejectedApplications(rejectedApplications)
                .build();
    }
}
