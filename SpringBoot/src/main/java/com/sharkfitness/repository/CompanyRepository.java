package com.sharkfitness.repository;


import com.sharkfitness.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> { }
