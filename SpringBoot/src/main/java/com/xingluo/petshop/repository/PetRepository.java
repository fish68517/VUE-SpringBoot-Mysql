package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 宠物档案Repository接口
 */
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    /**
     * 根据用户ID查询宠物列表
     * @param userId 用户ID
     * @return 宠物列表
     */
    List<Pet> findByUserId(Long userId);
}
