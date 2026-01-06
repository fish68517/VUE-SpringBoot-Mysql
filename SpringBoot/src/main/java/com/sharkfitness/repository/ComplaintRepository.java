package com.sharkfitness.repository;


import com.sharkfitness.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> { }
