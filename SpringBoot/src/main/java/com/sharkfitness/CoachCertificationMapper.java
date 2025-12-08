package com.sharkfitness;


import com.sharkfitness.entity.CoachCertification;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CoachCertificationMapper {

    @Select("SELECT * FROM coach_certification WHERE coach_id = #{coachId} ORDER BY created_at DESC")
    List<CoachCertification> findByCoachId(Long coachId);

    @Select("SELECT * FROM coach_certification WHERE id = #{id}")
    CoachCertification findById(Long id);

    @Insert("INSERT INTO coach_certification (coach_id, name, issuing_authority, image_url, issue_date, expiry_date, status) " +
            "VALUES (#{coachId}, #{name}, #{issuingAuthority}, #{imageUrl}, #{issueDate}, #{expiryDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CoachCertification cert);

    @Update("UPDATE coach_certification SET name=#{name}, issuing_authority=#{issuingAuthority}, " +
            "image_url=#{imageUrl}, issue_date=#{issueDate}, expiry_date=#{expiryDate}, status=0 " + // 修改后重置为待审核
            "WHERE id=#{id} AND coach_id=#{coachId}")
    int update(CoachCertification cert);

    @Delete("DELETE FROM coach_certification WHERE id = #{id} AND coach_id = #{coachId}")
    int delete(@Param("id") Long id, @Param("coachId") Long coachId);
}