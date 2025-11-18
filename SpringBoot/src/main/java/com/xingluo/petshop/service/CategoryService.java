package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.Category;
import java.util.List;

/**
 * 商品分类服务接口
 */
public interface CategoryService {
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();
}
