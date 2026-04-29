package com.medical.internship.repository;

import com.medical.internship.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 组织数据访问接口
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    /**
     * 根据组织代码查询组织
     */
    Optional<Organization> findByCode(String code);
    
    /**
     * 根据组织类型查询组织列表
     */
    java.util.List<Organization> findByType(String type);
}
