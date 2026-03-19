package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.CreatePetDTO;
import com.xingluo.petshop.dto.PetVO;
import com.xingluo.petshop.dto.UpdatePetDTO;
import com.xingluo.petshop.entity.Pet;
import com.xingluo.petshop.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 宠物档案控制器
 * 提供宠物档案的创建、查询、更新和删除等RESTful API
 */
@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {
    
    private final PetService petService;

    private static final String PET_UPLOAD_DIR =
            System.getProperty("user.dir") + "/uploads/pets/";
    
    /**
     * 创建宠物档案
     * POST /api/pet
     */
    @PostMapping
    public ApiResponse<PetVO> createPet(@RequestBody CreatePetDTO createPetDTO) {
        // 将DTO转换为实体
        Pet pet = new Pet();
        pet.setUserId(createPetDTO.getUserId());
        pet.setName(createPetDTO.getName());
        pet.setSpecies(createPetDTO.getSpecies());
        pet.setAge(createPetDTO.getAge());
        pet.setGender(createPetDTO.getGender());
        pet.setAvatar(createPetDTO.getAvatar());
        
        // 调用服务层创建
        Pet createdPet = petService.createPet(pet);
        
        // 转换为VO返回
        PetVO petVO = convertToVO(createdPet);
        return ApiResponse.ok(petVO);
    }
    
    /**
     * 获取宠物列表
     * GET /api/pet/list
     * 注意：实际项目中应该从token中获取userId，这里简化处理，通过请求参数传递
     */
    @GetMapping("/list")
    public ApiResponse<List<PetVO>> getPetList(@RequestParam Long userId) {
        List<Pet> pets = petService.getPetsByUserId(userId);
        
        // 转换为VO列表返回
        List<PetVO> petVOList = pets.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return ApiResponse.ok(petVOList);
    }
    
    /**
     * 更新宠物信息
     * PUT /api/pet/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<PetVO> updatePet(@PathVariable Long id,
                                        @RequestBody UpdatePetDTO updatePetDTO) {
        // 将DTO转换为实体
        Pet pet = new Pet();
        pet.setName(updatePetDTO.getName());
        pet.setSpecies(updatePetDTO.getSpecies());
        pet.setAge(updatePetDTO.getAge());
        pet.setGender(updatePetDTO.getGender());
        pet.setAvatar(updatePetDTO.getAvatar());
        
        // 调用服务层更新
        Pet updatedPet = petService.updatePet(id, pet);
        
        // 转换为VO返回
        PetVO petVO = convertToVO(updatedPet);
        return ApiResponse.ok(petVO);
    }
    
    /**
     * 删除宠物档案
     * DELETE /api/pet/{id}
     * 注意：实际项目中应该从token中获取userId，这里简化处理，通过请求参数传递
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePet(@PathVariable Long id,
                                       @RequestParam Long userId) {
        petService.deletePet(id, userId);
        return ApiResponse.ok(null);
    }

    /**
     * 上传宠物头像（保存到 uploads/pets）
     * POST /api/pet/avatar
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadPetAvatar(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return (ApiResponse<String>) ApiResponse.error("上传文件不能为空");
        }

        File uploadDir = new File(PET_UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            return (ApiResponse<String>) ApiResponse.error("创建宠物图片目录失败");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = ".jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;
        File target = new File(uploadDir, newFilename);

        try {
            file.transferTo(target);
        } catch (IOException e) {
            return (ApiResponse<String>) ApiResponse.error("宠物图片上传失败: " + e.getMessage());
        }

        // 仅保存相对路径到数据库
        return ApiResponse.ok("pets/" + newFilename);
    }
    
    /**
     * 将Pet实体转换为PetVO
     */
    private PetVO convertToVO(Pet pet) {
        PetVO petVO = new PetVO();
        BeanUtils.copyProperties(pet, petVO);
        return petVO;
    }
}
