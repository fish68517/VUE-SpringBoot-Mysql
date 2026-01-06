package com.sharkfitness.repository;


import com.sharkfitness.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPost, Long> { }
