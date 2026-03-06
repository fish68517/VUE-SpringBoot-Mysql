package com.submission.service;

import com.submission.dto.CategoryDTO;
import com.submission.entity.Category;
import com.submission.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Category Service - Business logic for category operations
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    /**
     * Get all categories
     */
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryMapper.findAll();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get category by id
     */
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }
        return convertToDTO(category);
    }

    /**
     * Get category requirements by id
     */
    public CategoryDTO getCategoryRequirements(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }
        return convertToDTO(category);
    }

    /**
     * Create a new category
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Validate category name is not empty
        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty");
        }

        // Check if category with same name already exists
        Category existingCategory = categoryMapper.findByName(categoryDTO.getName());
        if (existingCategory != null) {
            throw new RuntimeException("Category with this name already exists");
        }

        Category category = convertToEntity(categoryDTO);
        int result = categoryMapper.insert(category);
        if (result <= 0) {
            throw new RuntimeException("Failed to create category");
        }

        return convertToDTO(category);
    }

    /**
     * Update category
     */
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryMapper.findById(id);
        if (existingCategory == null) {
            throw new RuntimeException("Category not found");
        }

        // Validate category name is not empty
        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty");
        }

        // Check if another category with same name exists
        if (!existingCategory.getName().equals(categoryDTO.getName())) {
            Category categoryWithSameName = categoryMapper.findByName(categoryDTO.getName());
            if (categoryWithSameName != null) {
                throw new RuntimeException("Category with this name already exists");
            }
        }

        Category category = convertToEntity(categoryDTO);
        category.setId(id);
        int result = categoryMapper.update(category);
        if (result <= 0) {
            throw new RuntimeException("Failed to update category");
        }

        return convertToDTO(category);
    }

    /**
     * Delete category
     */
    public void deleteCategory(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        int result = categoryMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("Failed to delete category");
        }
    }

    /**
     * Convert Category entity to CategoryDTO
     */
    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .requirements(category.getRequirements())
                .fileFormat(category.getFileFormat())
                .maxFileSize(category.getMaxFileSize())
                .wordCountMin(category.getWordCountMin())
                .wordCountMax(category.getWordCountMax())
                .build();
    }

    /**
     * Convert CategoryDTO to Category entity
     */
    private Category convertToEntity(CategoryDTO categoryDTO) {
        return Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .requirements(categoryDTO.getRequirements())
                .fileFormat(categoryDTO.getFileFormat())
                .maxFileSize(categoryDTO.getMaxFileSize())
                .wordCountMin(categoryDTO.getWordCountMin())
                .wordCountMax(categoryDTO.getWordCountMax())
                .build();
    }
}
