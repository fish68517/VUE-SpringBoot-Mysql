package com.sharkfitness.repository;


import com.sharkfitness.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> { }
