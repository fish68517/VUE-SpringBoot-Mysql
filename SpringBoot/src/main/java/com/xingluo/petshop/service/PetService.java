package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.Pet;

import java.util.List;

/**
 * 宠物档案服务接口
 */
public interface PetService {
    
    /**
     * 创建宠物档案
     * @param pet 宠物信息
     * @return 创建后的宠物档案
     */
    Pet createPet(Pet pet);
    
    /**
     * 查询用户宠物列表
     * @param userId 用户ID
     * @return 宠物列表
     */
    List<Pet> getPetsByUserId(Long userId);
    
    /**
     * 更新宠物信息
     * @param petId 宠物ID
     * @param pet 更新的宠物信息
     * @return 更新后的宠物信息
     */
    Pet updatePet(Long petId, Pet pet);
    
    /**
     * 删除宠物档案
     * @param petId 宠物ID
     * @param userId 用户ID（用于验证权限）
     */
    void deletePet(Long petId, Long userId);
}
