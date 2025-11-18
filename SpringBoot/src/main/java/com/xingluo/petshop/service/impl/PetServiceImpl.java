package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.Pet;
import com.xingluo.petshop.repository.PetRepository;
import com.xingluo.petshop.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 宠物档案服务实现类
 */
@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {
    
    private final PetRepository petRepository;
    
    /**
     * 创建宠物档案
     */
    @Override
    @Transactional
    public Pet createPet(Pet pet) {
        return petRepository.save(pet);
    }
    
    /**
     * 查询用户宠物列表
     */
    @Override
    public List<Pet> getPetsByUserId(Long userId) {
        return petRepository.findByUserId(userId);
    }
    
    /**
     * 更新宠物信息
     */
    @Override
    @Transactional
    public Pet updatePet(Long petId, Pet pet) {
        // 查询宠物是否存在
        Pet existingPet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("宠物档案不存在"));
        
        // 更新宠物信息
        if (pet.getName() != null) {
            existingPet.setName(pet.getName());
        }
        if (pet.getSpecies() != null) {
            existingPet.setSpecies(pet.getSpecies());
        }
        if (pet.getAge() != null) {
            existingPet.setAge(pet.getAge());
        }
        if (pet.getGender() != null) {
            existingPet.setGender(pet.getGender());
        }
        if (pet.getAvatar() != null) {
            existingPet.setAvatar(pet.getAvatar());
        }
        
        return petRepository.save(existingPet);
    }
    
    /**
     * 删除宠物档案
     */
    @Override
    @Transactional
    public void deletePet(Long petId, Long userId) {
        // 查询宠物是否存在
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("宠物档案不存在"));
        
        // 验证宠物是否属于该用户
        if (!pet.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该宠物档案");
        }
        
        petRepository.deleteById(petId);
    }
}
