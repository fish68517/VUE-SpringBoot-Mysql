package com.sharkfitness.repository;


import com.sharkfitness.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> { }
