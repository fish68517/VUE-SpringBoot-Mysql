package com.sharkfitness.repository;


import com.sharkfitness.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByJobPost_IdAndJobseeker_Id(Long jobPostId, Long jobseekerId);
}
